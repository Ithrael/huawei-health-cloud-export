import datetime as dt
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch
import zipfile

sys.path.insert(0,str(Path(__file__).resolve().parents[1]/'scripts'))
import health as h
import export as e


class ExportTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory()
        self.root=Path(self.tmp.name)
        self.start=dt.datetime(2026,11,4,tzinfo=h.ZONE)
        self.end=dt.datetime(2026,11,13,22,tzinfo=h.ZONE)
        self.sm=int(self.start.timestamp()*1000)
        self.em=int(self.end.timestamp()*1000)

    def tearDown(self):self.tmp.cleanup()

    def test_seven_day_windows_are_contiguous(self):
        windows=list(e.chunks(self.sm,self.em))
        self.assertEqual(len(windows),2)
        self.assertEqual(windows[0][0],self.sm)
        self.assertEqual(windows[-1][1],self.em)
        self.assertTrue(all(b-a+1<=7*86400000 for a,b in windows))
        self.assertTrue(all(a[1]+1==b[0] for a,b in zip(windows,windows[1:])))

    def test_paging_filters_event_dates_and_requires_target(self):
        calls=[]
        def query(path,params):
            calls.append(params['version'])
            if len(calls)==1:
                return {'currentVersion':100,'detailInfos':[
                    {'startTime':self.sm-2000,'endTime':self.sm-1000,'modifiedTime':self.em},
                    {'startTime':self.sm-1000,'endTime':self.sm+1000}]}
            return {'currentVersion':200,'detailInfos':[{'startTime':self.em,'endTime':self.em}],
                    'deleteInfos':[{'dataId':'deleted-test-id'}]}
        result=e.paged(query,e.HEALTH,{'version':0},200,4,self.start,self.end)
        self.assertEqual(calls,[0,100])
        self.assertTrue(result['complete'])
        self.assertEqual(len(result['records']),2)
        self.assertEqual(len(result['deleteInfos']),1)
        stopped=e.paged(lambda *_:{'currentVersion':0,'detailInfos':[]},e.HEALTH,{'version':0},200,3,self.start,self.end)
        self.assertFalse(stopped['complete'])
        self.assertEqual(stopped['reason'],'missing_target_or_cursor_stalled')

    def test_page_limit_does_not_claim_complete(self):
        r=e.paged(lambda *_:{'currentVersion':1,'detailInfos':[]},e.HEALTH,{'version':0},100,1,self.start,self.end)
        self.assertFalse(r['complete'])
        self.assertEqual(r['reason'],'max_pages_reached')

    def test_private_cache_does_not_store_password_and_prevents_account_overwrite(self):
        file=self.root/'cache/session.json'
        payload={'uid':'test-owner-a','accessToken':'fake-at','refreshToken':'fake-rt',
                 'accessTokenExpireTime':9999999999999,'password':'DO_NOT_STORE','otp':'000000'}
        h.import_session(file,payload)
        self.assertNotIn('password',json.loads(file.read_text()))
        self.assertNotIn('otp',json.loads(file.read_text()))
        self.assertEqual(file.stat().st_mode&0o777,0o600)
        self.assertEqual(file.parent.stat().st_mode&0o777,0o700)
        before=file.read_bytes()
        with self.assertRaises(h.HealthError):h.import_session(file,dict(payload,uid='test-owner-b'))
        self.assertEqual(file.read_bytes(),before)

    def test_transport_rejects_other_hosts_and_mutations(self):
        client=object.__new__(h.Client)
        for host,path in [('https://example.invalid',e.HEALTH),
                          (h.DATA,'/dataStat/v1/basicCalculations/lifetimeStatistics:calculate'),
                          (h.AUTH,'/commonAbility/userAccessToken/obtain')]:
            with self.assertRaises(h.HealthError):client.post(host,path,{})

    def test_clean_keeps_ids_and_binary_and_decodes_values(self):
        original={'dataId':9223372036854775806,'detailData':'deadbeef',
                  'samplePoints':[{'key':'HEART_RATE_VARIABILITY','value':'{"heartRateVariabilityRMSSD":42}'}]}
        result=e.clean(original,{'HEART_RATE_VARIABILITY':'HRV RMSSD'})
        self.assertIsInstance(result['dataId'],str)
        self.assertEqual(result['detailData'],'deadbeef')
        self.assertEqual(result['samplePoints'][0]['value']['heartRateVariabilityRMSSD'],42)
        self.assertIsInstance(original['dataId'],int)

    def test_sleep_and_oxygen_summary_containers_are_not_empty(self):
        self.assertEqual(len(e.records({'sleepTotals':[{'recordDay':20261113}]})),1)
        self.assertEqual(len(e.records({'sleepTotal':[{'recordDay':20261113}],
            'bloodOxygenSaturationTotal':[{'recordDay':20261113}],'deleteInfos':[{'dataId':1}]})),2)

    @patch('export.time.sleep',lambda _:None)
    def test_default_full_plan_uses_cloud_main_types(self):
        class EmptyCloud:
            def __init__(client):client.calls=[]
            def ready(client):pass
            def query(client,path,params):
                client.calls.append((path,params.copy()))
                if path==e.VERSIONS:
                    return {'resultCode':0,'versions':[dict(k,version=self.em) for k in params['syncKeys']]}
                return {'resultCode':0,'currentVersion':self.em,'data':{}}
        client=EmptyCloud();out=self.root/'full'
        result=e.export(client,out,1,None,2,self.end)
        self.assertEqual(len(result['datasets']),139)
        sequence_types={p['type'] for path,p in client.calls if path==e.SEQUENCE}
        self.assertIn(30001,sequence_types);self.assertIn(34001,sequence_types)
        self.assertFalse(any(30200<=k<34300 and k not in (30001,34001) for k in sequence_types))
        point_types={p['type'] for path,p in client.calls if path==e.HEALTH}
        self.assertFalse(point_types&e.INVALID_TYPES)
        self.assertEqual(len({p for p,_ in client.calls}),20)

    @patch('export.time.sleep',lambda _:None)
    def test_offline_export_packages_interfaces_and_partial_coverage(self):
        class FakeClient:
            session={'accessToken':'FAKE_TOKEN_NOT_FOR_EXPORT','refreshToken':'FAKE_REFRESH_NOT_FOR_EXPORT'}
            def __init__(client):client.calls=[]
            def ready(client):pass
            def query(client,path,params):
                client.calls.append((path,params.copy()))
                if path==e.VERSIONS:
                    return {'resultCode':0,'versions':[dict(k,version=self.em) for k in params['syncKeys']]}
                if path==e.HEALTH:
                    return {'resultCode':0,'currentVersion':self.em,'detailInfos':[
                        {'startTime':self.sm,'endTime':self.em,'dataId':123,
                         'samplePoints':[{'key':'HEART_RATE_VARIABILITY','value':'{"heartRateVariabilityRMSSD":42}'}]}]}
                if path=='/dataQuery/sport/v2/getSportsStat':
                    return {'resultCode':0,'sportStat':[{'recordDay':20261113,'deviceCode':1,
                        'sportBasicInfo':{'steps':100,'distance':70,'calorie':123400}}]}
                return {'resultCode':0,'currentVersion':self.em,'data':{}}
        client=FakeClient();out=self.root/'export'
        manifest=e.export(client,out,10,{500044},3,self.end)
        self.assertEqual(manifest['status'],'partial')
        self.assertEqual(len(list((out/'interfaces').glob('*.json'))),26)
        self.assertTrue(all(p in h.READ_PATHS for p,_ in client.calls))
        timed=[v for p,v in client.calls if p=='/dataQuery/health/getHealthData']
        self.assertEqual(len(timed),2)
        self.assertTrue(all(v['endTime']-v['startTime']+1<=7*86400000 for v in timed))
        lactic=[v for p,v in client.calls if p.endswith('/getHealthDataLatest') and v['type']==300012]
        self.assertEqual(lactic[0]['dataType'],0)
        self.assertIsNone(json.loads((out/'activity/floor.json').read_text())['records'][0]['value'])
        self.assertEqual(json.loads((out/'activity/calorie.json').read_text())['records'][0]['kcal'],123.4)
        with zipfile.ZipFile(out/'huawei-health-data.zip') as z:
            self.assertIsNone(z.testzip())
            for name in z.namelist():
                raw=z.read(name);json.loads(raw)
                self.assertNotIn(b'FAKE_TOKEN_NOT_FOR_EXPORT',raw)
                self.assertNotIn(b'FAKE_REFRESH_NOT_FOR_EXPORT',raw)
        with self.assertRaises(h.HealthError):e.export(client,out,10,{500044},3,self.end)

    @patch('export.time.sleep',lambda _:None)
    def test_http_429_stops_following_requests_and_keeps_partial_files(self):
        class Limited:
            def __init__(client):client.count=0
            def ready(client):pass
            def query(client,*_):
                client.count+=1
                raise h.HealthError('HTTP 429 at /dataQuery/common/getSyncVersions')
        client=Limited();out=self.root/'limited'
        result=e.export(client,out,10,{500044},2,self.end)
        self.assertEqual(client.count,1)
        self.assertTrue(result['stoppedOnAuthOrRateLimit'])
        self.assertTrue((out/'manifest.json').exists())
        self.assertTrue((out/'huawei-health-data.zip').exists())


if __name__=='__main__':unittest.main()
