import datetime as dt
import base64
import gzip
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'scripts'))
import dashboard as d


class DashboardTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.start = dt.datetime(2026, 11, 13, tzinfo=d.ZONE)
        self.stamp = int(self.start.timestamp() * 1000)
        self.save('manifest.json', dict(status='partial', rangeStart=self.start.isoformat(),
            rangeEnd=(self.start + dt.timedelta(hours=23)).isoformat(),
            endpointCounts={'data': 3, 'failed': 1}, datasets=[{'complete': False}],
            limitations=['未知字段不解释']))

    def tearDown(self):
        self.tmp.cleanup()

    def save(self, name, value):
        path = self.root / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(json.dumps(value))

    def load(self):
        with patch('socket.socket', side_effect=AssertionError('Offline only')):
            return d.load_export(self.root)

    def test_daily_sources_units_and_missing_values(self):
        self.save('activity/steps.json', dict(sourceEndpoint='/daily', records=[
            dict(recordDay=20261113, value=100, deviceCode=1),
            dict(recordDay=20261113, value=300, deviceCode=2),
            dict(recordDay=20261113, value=None, deviceCode=1),
            dict(recordDay=20261112, value=900, deviceCode=1)]))
        self.save('activity/calorie.json', dict(records=[dict(recordDay=20261113, value=123400)]))
        payload = self.load()
        self.assertEqual([r['value'] for r in payload['metrics']['steps']], [100, 300])
        self.assertEqual(len({r['source'] for r in payload['metrics']['steps']}), 2)
        self.assertEqual(payload['metrics']['calorie'][0]['value'], 123.4)
        self.assertEqual(payload['metrics']['floor'], [])
        self.assertEqual(payload['coverage']['status'], 'partial')
        self.assertEqual(payload['coverage']['incompleteDatasets'], 1)

    def test_nested_points_deduplicate_and_do_not_confuse_rr_with_rmssd(self):
        row = dict(startTime=self.stamp, endTime=self.stamp + 1000, samplePoints=[
            {'key': 'HEART_RATE_VARIABILITY', 'value': '{"heartRateVariabilityRMSSD":42}'},
            {'key': 500044831, 'value': 42},
            {'key': 'RR', 'value': {'heartRateVariability': 900}}])
        self.save('datasets/hrv.json', dict(type=500044, records=[row, row], endpoint='/hrv'))
        payload = self.load()
        self.assertEqual(len(payload['metrics']['hrv']), 1)
        self.assertEqual(payload['metrics']['hrv'][0]['value'], 42)
        self.assertEqual(payload['metrics']['hrv'][0]['endpoint'], '/hrv')

    def test_resting_and_dynamic_heart_rate_remain_distinct(self):
        self.save('datasets/heart.json', dict(type=7, records=[dict(startTime=self.stamp,
            samplePoints=[{'key': 'DATA_POINT_DYNAMIC_HEARTRATE', 'value': '72'},
                          {'key': 'DATA_POINT_REST_HEARTRATE', 'value': '{"heartRate":56}'}])]))
        payload = self.load()
        self.assertEqual([r['value'] for r in payload['metrics']['heartRate']], [72])
        self.assertEqual([r['value'] for r in payload['metrics']['restHeartRate']], [56])

    def test_field_modification_metadata_is_not_a_measurement(self):
        self.save('datasets/hrv.json', dict(type=500044, records=[dict(startTime=self.stamp,
            samplePoints=[{'key': 'HEART_RATE_VARIABILITY',
                           'value': {'heartRateVariabilityRMSSD': 42},
                           'fieldsModifyTime': {'heartRateVariabilityRMSSD': self.stamp},
                           'fieldsMetadata': {'heartRateVariabilityRMSSD': 99}}])]))
        payload = self.load()
        self.assertEqual([r['value'] for r in payload['metrics']['hrv']], [42])

    def test_sleep_intervals_use_timestamps_not_unknown_duration_units(self):
        row = dict(startTime=self.stamp, endTime=self.stamp + 3600000,
                   samplePoints=[{'key': 'PROFESSIONAL_SLEEP_DEEP', 'value': 123}])
        self.save('datasets/sleep.json', dict(type=9, records=[row, row,
            dict(recordDay=20261113, professionalSleep={'allSleepTime': 999, 'deepSleepTime': 42},
                 detailData='UNDECODED')]))
        payload = self.load()
        self.assertEqual(len(payload['sleep']), 1)
        self.assertEqual(payload['sleep'][0]['stage'], 'deep')
        self.assertEqual(payload['sleep'][0]['start'], self.start.isoformat())
        # The full archive retains unknown-unit summaries; overview metrics do not infer minutes.
        self.assertNotIn('999', json.dumps(payload['metrics']))

    def test_unknown_and_undated_records_do_not_become_measurements(self):
        self.save('datasets/hrv.json', dict(type=500044, records=[
            dict(heartRateVariabilityRMSSD=99),
            dict(startTime=self.stamp, heartRateVariabilityRMSSD=True),
            dict(startTime=self.stamp, heartRateVariabilityRMSSD='NaN')],
            undatedRecords=[dict(heartRateVariabilityRMSSD=50)]))
        self.save('.secrets/session.json', dict(accessToken='PRIVATE_SENTINEL'))
        self.assertEqual(self.load()['metrics']['hrv'], [])
        self.assertNotIn('PRIVATE_SENTINEL', json.dumps(self.load()))

    def test_inert_json_and_private_standalone_output(self):
        payload = d.demo_payload()
        payload['notes'].append('</script><img src=x onerror=alert(1)>')
        path = d.write_dashboard(payload, self.root / 'result/dashboard.html')
        html = path.read_text()
        raw = html.split('<script type="application/json" id="dashboard-data">')[1].split('</script>')[0]
        self.assertEqual(json.loads(raw), payload)
        self.assertNotIn('</script><img', html)
        self.assertNotIn('/* DASHBOARD_', html)
        self.assertIn("connect-src 'none'", html)
        self.assertEqual(path.stat().st_mode & 0o777, 0o600)
        self.assertEqual(path.parent.stat().st_mode & 0o777, 0o700)

    def test_demo_is_deterministic_and_fully_labelled(self):
        with patch('socket.socket', side_effect=AssertionError('Offline only')):
            one, two = d.demo_payload(), d.demo_payload()
        self.assertEqual(one, two)
        self.assertEqual(one['mode'], 'demo')
        self.assertEqual(one['coverage']['status'], 'simulated')
        self.assertEqual(len(one['metrics']['steps']), 30)
        self.assertEqual(one['metrics']['steps'][-1]['value'], 8642)
        self.assertTrue(all(r['endpoint'] == '模拟数据' for r in one['sleep']))

    def unpack(self, payload, file):
        return json.loads(gzip.decompress(base64.b64decode(payload['archive']['files'][file])))

    def test_complete_catalog_preserves_unknown_binary_undated_and_deleted_records(self):
        original = dict(type=700021, name='RRI', complete=False, status='data',
            records=[{'recordDay': 20261113, 'detailData': 'UNDECODED_' * 100,
                      'unknownFields': {'nested': [None, False, -1, 255]}}],
            undatedRecords=[{'unknownSummary': 9}], deleteInfos=[{'dataId': 'deleted'}])
        self.save('datasets/rri.json', original)
        self.save('interfaces/empty.json', dict(endpoint='/read', status='empty', calls=[
            dict(status='success', parameters={'type': 1}, response={'resultCode': 0, 'unknown': []})]))
        self.save('mapping.json', {'unknown': {'名称': '未确认', '单位': None}})
        payload = self.load()
        self.assertEqual(set(payload['archive']['files']), {'manifest.json', 'mapping.json', 'datasets/rri.json', 'interfaces/empty.json'})
        self.assertEqual(self.unpack(payload, 'datasets/rri.json'), original)
        entry = next(x for x in payload['catalog'] if x['file'] == 'datasets/rri.json')
        self.assertEqual((entry['recordCount'], entry['undatedCount'], entry['deletedCount']), (1, 1, 1))
        self.assertFalse(entry['complete'])
        self.assertEqual(payload['metricGroups'][0]['binaryCount'], 1)

    def test_archive_hides_credentials_and_preserves_large_identifier_digits(self):
        self.save('interfaces/returned.json', dict(endpoint='/read', status='data', calls=[
            dict(response={'resultCode': 0, 'dataId': 9223372036854775806,
                'accessToken': 'NEVER_EMBED_ACCESS', 'uid': 'NEVER_EMBED_UID',
                'nestedJson': '{"refreshToken":"NEVER_EMBED_REFRESH","unknownField":42}'})]))
        raw = self.unpack(self.load(), 'interfaces/returned.json')
        self.assertNotIn('NEVER_EMBED', json.dumps(raw))
        response = raw['calls'][0]['response']
        self.assertEqual(response['dataId'], '9223372036854775806')
        self.assertEqual(json.loads(response['nestedJson'])['unknownField'], 42)

    def test_full_metric_fields_exclude_modification_metadata_and_keep_raw_units(self):
        self.save('datasets/basal.json', dict(type=500018, name='BASAL_METABOLISM', status='data', records=[
            dict(startTime=self.stamp, samplePoints=[dict(value={'basalMetabolism': 1234},
                fieldsModifyTime={'basalMetabolism': self.stamp}, fieldsMetadata={'unknown': 255})])]))
        payload = self.load()
        group = next(g for g in payload['metricGroups'] if g['title'] == '基础代谢')
        self.assertEqual(group['fields'][0]['unit'], '原始值')
        self.assertEqual(group['points'][0]['values'], {'basalMetabolism': 1234})

    def test_interface_summaries_and_cumulative_report_have_full_data_views(self):
        self.save('interfaces/health.json', dict(endpoint='/dataQuery/health/getHealthStat', status='data', calls=[
            dict(status='success', response={'heartRateTotal': [dict(recordDay=20261113, heartRateBasic={'avgRestingHeartRate': 56})],
                'stressTotal': [dict(recordDay=20261113, stressBasic={'meanScore': 30})]})]))
        self.save('interfaces/report.json', dict(endpoint='/dataQuery/report/getPersonalReport', status='data', calls=[
            dict(status='success', response={'accumulatedReports': {'accumPerfectGoalAchievedDays': {'value': 99}}})]))
        groups = self.load()['metricGroups']
        self.assertEqual({g['title'] for g in groups}, {'心率日汇总', '压力日汇总', '累计达标报告'})
        report = next(g for g in groups if g['title'] == '累计达标报告')
        self.assertIsNone(report['points'][0]['date'])

    def test_catalog_does_not_follow_links_to_other_files(self):
        self.save('not-an-export.json', {'accessToken': 'OUTSIDE_FILE'})
        (self.root / 'datasets').mkdir()
        (self.root / 'datasets/linked.json').symlink_to(self.root / 'not-an-export.json')
        payload = self.load()
        self.assertEqual(set(payload['archive']['files']), {'manifest.json'})


if __name__ == '__main__':
    unittest.main()
