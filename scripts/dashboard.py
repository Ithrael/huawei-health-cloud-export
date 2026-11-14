#!/usr/bin/env python3
"""Build a self-contained, offline health dashboard from an export or demo data."""
import argparse
import datetime as dt
import json
import math
from pathlib import Path
import random
import sys
from dashboard_catalog import attach_catalog

ROOT = Path(__file__).resolve().parents[1]
ZONE = dt.timezone(dt.timedelta(hours=8))
METRICS = ('steps', 'distance', 'calorie', 'floor', 'heartRate', 'restHeartRate', 'hrv', 'stress', 'sleepScore')
STAGES = {'PROFESSIONAL_SLEEP_SHALLOW': 'light', '22101': 'light',
          'PROFESSIONAL_SLEEP_DREAM': 'rem', '22102': 'rem',
          'PROFESSIONAL_SLEEP_DEEP': 'deep', '22103': 'deep',
          'PROFESSIONAL_SLEEP_WAKE': 'awake', '22104': 'awake'}


def number(value):
    if isinstance(value, bool):
        return None
    try:
        result = float(value)
        return result if math.isfinite(result) and result >= 0 else None
    except (TypeError, ValueError):
        return None


def timestamp(value):
    value = number(value)
    if value is None:
        return None
    if 946684800 <= value < 4102444800:
        value *= 1000
    if 946684800000 <= value < 4102444800000:
        return dt.datetime.fromtimestamp(value / 1000, ZONE).isoformat()
    return None


def record_date(row):
    day = str(row.get('recordDay', ''))
    if len(day) == 8:
        try:
            return dt.datetime.strptime(day, '%Y%m%d').date().isoformat()
        except ValueError:
            pass
    stamp = timestamp(row.get('endTime')) or timestamp(row.get('startTime'))
    return stamp[:10] if stamp else None


def base_payload(mode, start, end):
    return dict(mode=mode, rangeStart=start, rangeEnd=end, timezone='Asia/Shanghai',
                metrics={key: [] for key in METRICS}, sleep=[], sources={}, coverage={}, notes=[])


def load_export(directory):
    """Use only curated export files. Never read session caches or arbitrary interface responses."""
    directory = Path(directory)
    manifest = json.loads((directory / 'manifest.json').read_text())
    start, end = manifest['rangeStart'], manifest['rangeEnd']
    dt.datetime.fromisoformat(start)
    dt.datetime.fromisoformat(end)
    payload = base_payload('export', start, end)
    payload['coverage'] = {key: manifest.get(key) for key in
                           ('status', 'endpointCounts', 'appVersion', 'limitations')}
    payload['coverage']['incompleteDatasets'] = sum(not x.get('complete', False)
                                                   for x in manifest.get('datasets', []))

    def source(row):
        # Unknown devices stay unknown; do not invent a watch model from deviceCode.
        origin = row.get('dataSource', row.get('healthDataSource', row.get('sportDataSource', '?')))
        key = f"{origin}/{row.get('deviceCode', '?')}"
        if row.get('_dimension'):
            key += '|' + row['_dimension']
        if key not in payload['sources']:
            suffix = f" / {row['_dimension']}" if row.get('_dimension') else ''
            payload['sources'][key] = dict(label=f"来源 {len(payload['sources']) + 1}" + suffix,
                                           detail=f"dataSource={origin} / deviceCode={row.get('deviceCode', '未知')}" + suffix)
        return key

    def add(metric, value, context, endpoint):
        value = number(value)
        date = record_date(context)
        if value is None or not date or not start[:10] <= date <= end[:10]:
            return
        time = timestamp(context.get('startTime'))
        if time and (time < start or time > end):
            return
        payload['metrics'][metric].append(dict(date=date, time=time, value=value,
                                               source=source(context), endpoint=endpoint))

    for field in ('steps', 'distance', 'calorie', 'floor'):
        file = directory / 'activity' / f'{field}.json'
        if not file.exists():
            continue
        data = json.loads(file.read_text())
        for row in data.get('records', []):
            value = number(row.get('value'))
            if field == 'calorie' and value is not None:
                value /= 1000  # Source-backed activity cal → kcal only.
            add(field, value, row, data.get('sourceEndpoint'))

    def visit(node, context, kind, endpoint):
        if isinstance(node, str) and node.lstrip().startswith(('{', '[')):
            try:
                node = json.loads(node)
            except ValueError:
                return
        if isinstance(node, list):
            for child in node:
                visit(child, context, kind, endpoint)
        if not isinstance(node, dict):
            return
        context = {**context, **{k: node[k] for k in
                   ('recordDay', 'startTime', 'endTime', 'deviceCode', 'dataSource', 'healthDataSource', 'sportDataSource') if k in node}}
        key = str(node.get('key', ''))
        if kind == 500044:
            value = node.get('heartRateVariabilityRMSSD')
            if key == '500044831':
                value = node.get('value')
            add('hrv', value, context, endpoint)
        if kind == 7:
            metric = 'restHeartRate' if context.get('_rest') or key in ('2018', '2105', 'DATA_POINT_REST_HEARTRATE',
                                                'DATA_POINT_NEW_REST_HEARTRATE') else 'heartRate'
            value = node.get('heartRate')
            if key in ('2002', '2018', '2105', 'DATA_POINT_DYNAMIC_HEARTRATE',
                       'DATA_POINT_REST_HEARTRATE', 'DATA_POINT_NEW_REST_HEARTRATE'):
                value = node.get('value') if not isinstance(node.get('value'), (dict, list)) else value
            add(metric, value, context, endpoint)
        if kind == 11:
            add('stress', node.get('score'), context, endpoint)
        if kind in (9, 500005, 700013):
            score = number(node.get('sleepScore'))
            if score and score <= 100:
                add('sleepScore', score, context, endpoint)
        if kind == 9 and key in STAGES:
            left, right = timestamp(context.get('startTime')), timestamp(context.get('endTime'))
            date = record_date(context)
            if left and right and left < right and date and start[:10] <= date <= end[:10]:
                payload['sleep'].append(dict(date=date, start=left, end=right,
                    stage=STAGES[key], source=source(context), endpoint=endpoint))
        for name, child in node.items():
            if name not in ('detailData', 'readableTimes', 'undatedRecords', 'fieldsModifyTime', 'fieldsMetadata') and isinstance(child, (dict, list, str)):
                # Scalar string values are handled by their owning point, not guessed.
                child_context = dict(context)
                if key in ('2018', '2105', 'DATA_POINT_REST_HEARTRATE', 'DATA_POINT_NEW_REST_HEARTRATE'):
                    child_context['_rest'] = True
                visit(child, child_context, kind, endpoint)

    for file in sorted((directory / 'datasets').glob('*.json')):
        dataset = json.loads(file.read_text())
        if dataset.get('type') not in (7, 9, 11, 500005, 500044, 700013):
            continue
        for row in dataset.get('records', []):
            visit(row, {}, dataset['type'], dataset.get('endpoint'))

    for key, rows in payload['metrics'].items():
        unique = {(r['date'], r['time'], r['source'], r['value']): r for r in rows}
        payload['metrics'][key] = sorted(unique.values(), key=lambda r: (r['date'], r['time'] or ''))
    # Overlapping sleep intervals may be summary records. Do not double-count them.
    accepted, previous = [], {}
    for row in sorted(payload['sleep'], key=lambda r: (r['source'], r['start'], r['end'])):
        if row['start'] >= previous.get(row['source'], ''):
            accepted.append(row)
            previous[row['source']] = row['end']
    if len(accepted) != len(payload['sleep']):
        payload['notes'].append('睡眠区间存在重复或重叠，仅展示互不重叠的已识别区间；原始记录保留在导出目录。')
    payload['sleep'] = accepted
    payload['notes'].append('仅展示已确认字段；二进制睡眠详情、未知单位与无日期记录不推测。')
    attach_catalog(payload, directory, source, record_date, timestamp)
    return payload


def demo_payload():
    rng = random.Random(17)
    end = dt.datetime(2026, 11, 13, 21, 30, tzinfo=ZONE)
    start = (end - dt.timedelta(days=29)).replace(hour=0, minute=0)
    payload = base_payload('demo', start.isoformat(), end.isoformat())
    payload['sources'] = {'demo-watch': dict(label='演示手表', detail='完全虚构的模拟数据，不代表真实账号或测量')}
    payload['coverage'] = dict(status='simulated', endpointCounts={}, incompleteDatasets=0,
                                appVersion='17.0.10.300', limitations=[])
    for offset in range(30):
        day = start + dt.timedelta(days=offset)
        date = day.date().isoformat()

        def point(metric, value, hour=None):
            moment = day + dt.timedelta(hours=hour) if hour is not None else None
            payload['metrics'][metric].append(dict(date=date, time=moment.isoformat() if moment else None,
                value=round(value, 1), source='demo-watch', endpoint='模拟数据'))

        steps = 8642 if offset == 29 else rng.randint(4200, 13800)
        point('steps', steps)
        point('distance', 6280 if offset == 29 else steps * .726)
        point('calorie', 386 if offset == 29 else steps * .0447)
        point('floor', rng.randint(6, 22))
        point('restHeartRate', 58 + rng.randint(-4, 4), 6)
        point('sleepScore', 88 if offset == 29 else rng.randint(73, 94), 7)
        for i in range(86):
            hour = i / 4
            heart = 62 + 9 * math.sin(i * .17) + rng.randint(-5, 5)
            heart += 53 * math.exp(-((hour - 17.7) / .7) ** 2)
            point('heartRate', 64 if i == 85 and offset == 29 else heart, hour)
        for i in range(12):
            point('hrv', 52 if i == 11 and offset == 29 else 46 + rng.randint(-12, 15), i * 1.8)
            point('stress', 32 if i == 11 and offset == 29 else rng.randint(18, 53), i * 1.8)
        left = day - dt.timedelta(minutes=32)
        schedule = [('light', 22), ('deep', 38), ('light', 36), ('rem', 22), ('light', 34),
                    ('deep', 48), ('light', 28), ('awake', 8), ('light', 31), ('rem', 35),
                    ('light', 32), ('deep', 24), ('light', 28), ('rem', 42), ('light', 55)]
        for stage, minutes in schedule:
            right = left + dt.timedelta(minutes=minutes)
            payload['sleep'].append(dict(date=date, start=left.isoformat(), end=right.isoformat(),
                stage=stage, source='demo-watch', endpoint='模拟数据'))
            left = right
    attach_demo_catalog(payload)
    return payload


def attach_demo_catalog(payload):
    """Small complete synthetic artifacts exercise every full-data view offline."""
    from dashboard_catalog import encode_file
    payload.update(catalog=[], archive=dict(codec='gzip-base64', files={}), metricGroups=[])
    examples = [('静息心率', '心率与状态', 'heartRate', 58, '次/分钟'),
        ('情绪', '心率与状态', 'emotionStatus', 2, '分类'),
        ('海拔采样', '活动与运动', 'altitude', 46, 'm'),
        ('活动小时标记', '活动与运动', 'isActive', 1, '原始值'),
        ('基础代谢', '活动与运动', 'basalMetabolism', 1480000, '原始值'),
        ('上床与离床', '睡眠与作息', 'bedTime', 1794497400, '时间'),
        ('睡眠扩展详情', '睡眠与作息', 'avgHeartrate', 59, '次/分钟'),
        ('运动活动片段', '活动与运动', 'duration', 36, 'min'),
        ('四叶草进度', '目标与成就', 'sportLeafProgress', 76, '%'),
        ('累计达标报告', '目标与成就', 'value', 82, '天')]
    for index, (title, category, field, value, unit) in enumerate(examples):
        file = f'datasets/demo-{index}.json'
        points, records = [], []
        for n in range(7):
            day = dt.datetime(2026, 11, 7 + n, 12, tzinfo=ZONE)
            current = value if field in ('emotionStatus', 'isActive', 'bedTime') else value * (.9 + n / 30)
            date = None if title == '累计达标报告' else day.date().isoformat()
            points.append(dict(date=date, time=day.isoformat(), source='demo-watch', values={field: current}))
            records.append(dict(recordDay=int(day.strftime('%Y%m%d')), startTime=int(day.timestamp()*1000),
                                deviceCode='demo-watch', samplePoints=[dict(value={field: current})], demo=True))
        artifact = dict(name=title, status='data', complete=True, records=records,
                        undatedRecords=[dict(note='虚构的无日期记录', unknownField={'保留': True})],
                        deleteInfos=[dict(dataId='demo-deleted')])
        payload['archive']['files'][file] = encode_file(artifact)
        payload['catalog'].append(dict(file=file, title=title, name=title, kind='datasets', status='data',
            recordCount=len(records), undatedCount=1, deletedCount=1, complete=True, byteSize=1000))
        payload['metricGroups'].append(dict(id=file, title=title, category=category, file=file, recordCount=len(records),
            datedCount=len(records), binaryCount=0, fields=[dict(key=field, label=title, unit=unit, clock=field=='bedTime',
                categorical=field=='emotionStatus', note='模拟数据，仅用于展示界面。')], points=points))
    for name in ('RR 间期序列', '专项血氧序列', '专项温度序列', '专项睡眠片段'):
        file = f'datasets/demo-{name}.json'
        artifact = dict(name=name, records=[dict(recordDay=20261113, detailData='dGVzdC1kZW1vLW9ubHk=',
            summaryData={'unknownFlag': 255}, demo=True)], complete=True, status='data')
        payload['archive']['files'][file] = encode_file(artifact)
        payload['catalog'].append(dict(file=file, title=name, name=name, kind='datasets', status='data',
            recordCount=1, undatedCount=0, deletedCount=0, complete=True, byteSize=200))
        payload['metricGroups'].append(dict(id=file, title=name, category='原始序列', file=file, recordCount=1,
            binaryCount=1, datedCount=1, fields=[], points=[]))
    for metric, title, unit, category in [('heartRate', '心率', '次/分钟', '心率与状态'),
            ('hrv', 'HRV 心率变异性', 'ms', '心率与状态'), ('stress', '压力', '原始分数', '心率与状态'),
            ('sleepScore', '睡眠评分', '分', '睡眠与作息'), ('floor', '每日爬楼', '层', '活动与运动')]:
        file = f'datasets/demo-{metric}.json'
        rows = payload['metrics'][metric]
        records = [dict(recordDay=int(row['date'].replace('-', '')), value=row['value'],
                        measurementTime=row['time'], demo=True) for row in rows]
        payload['archive']['files'][file] = encode_file(dict(name=title, status='data', complete=True, records=records))
        payload['catalog'].append(dict(file=file, title=title, name=title, kind='datasets', status='data',
            recordCount=len(rows), undatedCount=0, deletedCount=0, complete=True, byteSize=len(json.dumps(records))))
        payload['metricGroups'].append(dict(id='demo:' + metric, title=title, category=category, file=file,
            recordCount=len(rows), datedCount=len(rows), binaryCount=0,
            fields=[dict(key=metric, label=title, unit=unit, note='模拟数据，仅用于界面演示。')],
            points=[dict(date=row['date'], time=row['time'], source=row['source'], values={metric: row['value']}) for row in rows]))


def write_dashboard(payload, output):
    output = Path(output)
    output.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    # Embedded JSON is inert and cannot terminate its script element.
    data = json.dumps(payload, ensure_ascii=False, separators=(',', ':'), allow_nan=False)
    data = data.replace('<', '\\u003c').replace('>', '\\u003e').replace('&', '\\u0026')
    template = (ROOT / 'assets/dashboard.html').read_text()
    css = '\n'.join((ROOT / 'assets' / name).read_text() for name in ('dashboard.css', 'dashboard_all.css'))
    js = '\n'.join((ROOT / 'assets' / name).read_text() for name in ('dashboard.js', 'dashboard_all.js'))
    content = template.replace('/* DASHBOARD_CSS */', css)
    content = content.replace('/* DASHBOARD_JS */', js)
    content = content.replace('<!-- DASHBOARD_DATA -->', data)
    with output.open('w') as stream:
        output.chmod(0o600)
        stream.write(content)
    return output


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--demo', action='store_true', help='Generate 30 days of deterministic simulated data; no login/network')
    group.add_argument('--input', type=Path, help='Existing export directory containing manifest.json')
    parser.add_argument('--output', type=Path, required=True, help='Standalone HTML output file')
    args = parser.parse_args()
    payload = demo_payload() if args.demo else load_export(args.input)
    output = write_dashboard(payload, args.output)
    print(json.dumps(dict(dashboard=str(output.resolve()), mode=payload['mode']), ensure_ascii=False))


if __name__ == '__main__':
    try:
        main()
    except (OSError, ValueError, KeyError, TypeError):
        print('无法生成大屏：请检查导出目录中的 manifest、activity 与 datasets 文件。', file=sys.stderr)
        raise SystemExit(1)
