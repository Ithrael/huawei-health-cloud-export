"""Complete offline archive and field views for the health dashboard."""
import base64
import gzip
import json
import math

TITLES = {
    'EMOTION': '情绪', 'ALTITUDE': '海拔采样', 'ACTIVE_HOUR': '活动小时标记',
    'BASAL_METABOLISM': '基础代谢', 'RRI': 'RR 间期序列', 'SLEEP_DETAILS': '睡眠扩展详情',
    'SLEEP_RECORD': '睡眠评估记录', 'SLEEP_ON_OFF_BED_RECORD': '上床与离床',
    'SPO2_RESP_INFECTION': '专项血氧序列', 'RRI_RESP_INFECTION': '专项 RRI 序列',
    'TEMPERATURE_RESP_INFECTION': '专项温度序列', 'SLEEP_FRAGMENT_RESP_INFECTION': '专项睡眠片段',
    'SPORT_GOAL_ACHIEVEMENT_DATA': '运动圆环状态', 'FOUR_LEAF_ACHIEVEMENT_DATA': '四叶草进度',
    'HEART_RATE_VARIABILITY': 'HRV 心率变异性', 'HEART_RATE': '心率与静息心率',
    'PROFESSIONAL_SLEEP': '睡眠分期', 'BASIC_SLEEP': '基础睡眠', 'STRESS': '压力',
    'ACTIVITY_SEGMENTS': '运动活动片段', 'WORKOUT_PATH': '运动轨迹',
    'getHealthStat': '云端健康日统计', 'getSportsDimenStat': '按运动类型汇总',
    'getSportsStat': '每日运动汇总', 'getPersonalReport': '累计达标报告',
    'manifest': '导出覆盖清单', 'mapping': '字段说明', 'type-mapping': '类型字典',
    'provenance': '接口来源与版本', 'steps': '每日步数', 'distance': '每日距离',
    'calorie': '每日活动热量', 'floor': '每日爬楼',
}
PRIORITY = ['heartRateVariabilityRMSSD', 'heartRate', 'DATA_POINT_DYNAMIC_HEARTRATE',
            'DATA_POINT_NEW_REST_HEARTRATE', 'score', 'emotionStatus', 'altitude', 'isActive',
            'basalMetabolism', 'bedTime', 'sleepScore', 'steps', 'sportLeafProgress', 'activeIsRing']
SECRET_KEYS = {'password', 'passwd', 'otp', 'smscode', 'accesstoken', 'refreshtoken',
               'token', 'authorizationcode', 'authorization', 'cookie', 'uid', 'x-huid'}
SKIP_FIELDS = {'fieldsModifyTime', 'fieldsMetadata', 'readableTimes', 'modifiedTime',
               'startTime', 'endTime', 'detailData', 'extendData', 'dataId', 'recordId',
               'recordDay', 'deviceCode', 'version', 'type', 'subType', 'dataSource',
               'healthDataSource', 'sportDataSource', 'timeZone', 'appType', 'mergedFlag'}
CLOCK_FIELDS = {'bedTime', 'risingTime', 'fallAsleepTime', 'wakeupTime', 'goBedTime', 'deviceBedTime'}


def private_copy(value):
    """Keep health fields, hide credentials, and preserve large integer digits in JS."""
    if isinstance(value, dict):
        return {k: '[已隐藏凭据]' if k.lower() in SECRET_KEYS else private_copy(v)
                for k, v in value.items()}
    if isinstance(value, list):
        return [private_copy(v) for v in value]
    if isinstance(value, str) and value.lstrip().startswith(('{', '[')):
        try:
            return json.dumps(private_copy(json.loads(value)), ensure_ascii=False, separators=(',', ':'))
        except ValueError:
            pass
    if isinstance(value, int) and not isinstance(value, bool) and abs(value) > 9007199254740991:
        return str(value)
    if isinstance(value, float) and not math.isfinite(value):
        return str(value)
    return value


def encode_file(value):
    raw = json.dumps(private_copy(value), ensure_ascii=False, separators=(',', ':')).encode()
    return base64.b64encode(gzip.compress(raw, mtime=0)).decode()


def category(name):
    if any(x in name for x in ('RESP_INFECTION', 'RRI')):
        return '原始序列'
    if any(x in name for x in ('SLEEP', 'Sleep', 'professionalSleep')):
        return '睡眠与作息'
    if any(x in name for x in ('HEART', 'STRESS', 'EMOTION', 'heartRate', 'stress')):
        return '心率与状态'
    if any(x in name for x in ('GOAL', 'LEAF', 'PersonalReport')):
        return '目标与成就'
    return '活动与运动'


def attach_catalog(payload, directory, source, date_of, stamp_of):
    """Only exact export artifact locations are eligible, never cache directories."""
    catalog, files, groups = [], {}, []
    mapping_path = directory / 'mapping.json'
    mapping = json.loads(mapping_path.read_text()) if mapping_path.exists() else {}
    meanings = {}

    def find_meanings(node):
        if not isinstance(node, dict):
            return
        for key, value in node.items():
            if isinstance(value, dict) and '名称' in value:
                meanings[key] = value
            elif isinstance(value, dict):
                find_meanings(value)
    find_meanings(mapping)

    def field_info(key):
        leaf = key.split('.')[-1]
        info = meanings.get(leaf, {})
        title = info.get('名称', leaf).split('；')[0].split('，')[0]
        overrides = {'DATA_POINT_DYNAMIC_HEARTRATE': ('动态心率', '次/分钟'),
                     'DATA_POINT_NEW_REST_HEARTRATE': ('静息心率', '次/分钟'),
                     'DATA_POINT_REST_HEARTRATE': ('静息心率', '次/分钟')}
        title, unit = overrides.get(leaf, (title, info.get('单位') or '原始值'))
        note = info.get('名称', '字段含义或单位未确认，保留原值。')
        if leaf in overrides:
            note = '已确认的心率点类型，单位为次/分钟；不同来源分别展示。'
        if key == 'accumPerfectGoalAchievedDays.value':
            title, unit = '累计完美达标天数', '天'
            note = '累计天数的起始范围未确认，不强行归入当前日期窗口。'
        return dict(key=key, label=title, unit=unit,
                    note=note,
                    clock=leaf in CLOCK_FIELDS, categorical=leaf == 'emotionStatus',
                    positive=leaf in {'avgHeartrate', 'avgBreathrate', 'avgHrv', 'avgOxygenSaturation',
                        'heartRateVariabilityRMSSD', 'sleepScore', 'lastAvgHeartrate', 'lastAvgSpO2', 'lastAvgBreathrate', 'lastAvgHrv'})

    def extract_fields(node, prefix='', point_key=''):
        """Explicit value/summary containers only; modification metadata stays raw."""
        if isinstance(node, str):
            if node.lstrip().startswith(('{', '[')):
                try:
                    node = json.loads(node)
                except ValueError:
                    return {}
            elif point_key:
                try:
                    node = float(node)
                except ValueError:
                    return {}
        if isinstance(node, (int, float)) and not isinstance(node, bool) and math.isfinite(node):
            return {prefix or point_key or 'value': node}
        if not isinstance(node, dict):
            return {}
        values = {}
        for key, value in node.items():
            if key not in SKIP_FIELDS:
                values.update(extract_fields(value, f'{prefix}.{key}' if prefix else key))
        return values

    def metric_group(rows, name, title, file, endpoint=None):
        points, field_keys, dated = [], set(), 0
        for row in rows:
            values = {}
            if 'samplePoints' in row:
                for point in row['samplePoints']:
                    if isinstance(point, dict):
                        values.update(extract_fields(point.get('value'), point_key=str(point.get('key', 'value'))))
            elif 'summaryData' in row:
                values = extract_fields(row.get('summaryData'))
            elif 'sportBasicInfos' in row:
                for index, item in enumerate(row['sportBasicInfos']):
                    values.update(extract_fields(item, f'活动{index + 1}'))
            elif 'sportBasicInfo' in row:
                values = extract_fields(row['sportBasicInfo'])
            else:
                for container in ('professionalSleep', 'heartRateBasic', 'stressBasic'):
                    if isinstance(row.get(container), dict):
                        values.update(extract_fields(row[container]))
            date = date_of(row)
            if date:
                dated += 1
            if values:
                field_keys.update(values)
                points.append(dict(date=date, time=stamp_of(row.get('startTime')), source=source(row), values=values))
        fields = [field_info(key) for key in sorted(field_keys,
                  key=lambda key: (PRIORITY.index(key.split('.')[-1]) if key.split('.')[-1] in PRIORITY else 99, key))]
        groups.append(dict(id=file + ':' + name, title=title, category=category(name), file=file,
                           endpoint=endpoint, recordCount=len(rows), datedCount=dated,
                           fields=fields, points=points,
                           binaryCount=sum(bool(row.get('detailData')) for row in rows)))

    candidates = [directory / name for name in ('manifest.json', 'mapping.json', 'type-mapping.json', 'provenance.json')]
    for folder in ('datasets', 'interfaces', 'activity'):
        candidates.extend(sorted((directory / folder).glob('*.json')))
    for path in candidates:
        if not path.is_file() or path.is_symlink():
            continue
        content = json.loads(path.read_text())
        file = path.relative_to(directory).as_posix()
        files[file] = encode_file(content)
        kind = path.parent.name if path.parent != directory else 'metadata'
        name = content.get('name', path.stem)
        endpoint = content.get('endpoint', content.get('sourceEndpoint'))
        if kind == 'interfaces':
            name = (endpoint or path.stem).rsplit('/', 1)[-1]
        title = TITLES.get(name, name)
        rows = content.get('records', [])
        entry = dict(file=file, title=title, name=name, kind=kind, endpoint=endpoint,
                     status=content.get('status', 'metadata'), complete=content.get('complete'),
                     recordCount=len(rows), undatedCount=len(content.get('undatedRecords', [])),
                     deletedCount=len(content.get('deleteInfos', [])),
                     reason=content.get('reason'), byteSize=path.stat().st_size)
        if kind == 'interfaces':
            entry['recordCount'] = content.get('recordCount', 0)
            entry['callCount'] = len(content.get('calls', []))
        catalog.append(entry)
        if kind == 'datasets' and rows:
            metric_group(rows, name, title, file, endpoint)
        if kind == 'interfaces':
            for call in content.get('calls', []):
                if call.get('status') != 'success':
                    continue
                response = call.get('response', {})
                if name == 'getHealthStat':
                    for container, title in [('professionalSleepTotal', '睡眠日汇总'),
                            ('heartRateTotal', '心率日汇总'), ('stressTotal', '压力日汇总')]:
                        if response.get(container):
                            metric_group(response[container], container, title, file, endpoint)
                if name == 'getSportsDimenStat' and response.get('sportStat'):
                    # Keep sport-type dimensions distinct, without guessing type names.
                    stat_rows = [dict(row, _dimension=f"运动类型{row.get('sportType', '?')}")
                                 for row in response['sportStat']]
                    metric_group(stat_rows, name, TITLES[name], file, endpoint)
                if name == 'getPersonalReport' and response.get('accumulatedReports'):
                    report = response['accumulatedReports']
                    values = {f'{key}.{field}': value for key, obj in report.items()
                              if isinstance(obj, dict) for field, value in obj.items()
                              if isinstance(value, (int, float)) and not isinstance(value, bool)}
                    groups.append(dict(id=file + ':report', title=TITLES[name], category='目标与成就',
                        file=file, endpoint=endpoint, recordCount=len(report), datedCount=0,
                        fields=[field_info(key) for key in values],
                        points=[dict(date=None, time=None, source=source({}), values=values)], binaryCount=0))
    # The four daily projections already have carefully normalized units and sources.
    for metric, title, unit in [('steps', '每日步数', '步'), ('distance', '每日距离', 'm'),
                              ('calorie', '每日活动热量', 'kcal'), ('floor', '每日爬楼', '层')]:
        rows = payload['metrics'][metric]
        if rows:
            groups.insert(0, dict(id='activity/' + metric, title=title, category='活动与运动',
                file='activity/' + metric + '.json', recordCount=len(rows), datedCount=len(rows),
                fields=[dict(key='value', label=title, unit=unit, note='同一来源的日统计，不叠加其他设备。')],
                points=[dict(date=r['date'], time=r['time'], source=r['source'], values={'value': r['value']}) for r in rows],
                binaryCount=0))
    payload.update(catalog=catalog, archive=dict(codec='gzip-base64', files=files), metricGroups=groups)
