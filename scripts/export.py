"""Source-backed comprehensive read plan for Huawei Health 17.0.10.300."""
from collections import Counter
import datetime as dt
import json
from pathlib import Path
import time
import zipfile
import health as h

HEALTH='/dataQuery/health/getHealthDataByVersion'
SEQUENCE='/dataQuery/sequence/getSampleSequenceByVersion'
STAT='/dataQuery/health/getHealthStatisticsByVersion'
VERSIONS='/dataQuery/common/getSyncVersions'
SPORT='/dataQuery/sport/getSportsDataByVersion'
TRACK='/dataQuery/path/getMotionPathByVersion'
CORE={3:'BASIC_SLEEP',4:'BLOOD_GLUCOSE',7:'HEART_RATE',9:'PROFESSIONAL_SLEEP',11:'STRESS',
      15:'LEGACY_ECG',16:'BLOOD_OXYGEN_SATURATION',18:'BODY_THERMOMETER',19:'BLOOD_OXYGEN_REMIND',21:'URIC_ACID'}
INVALID_TYPES={500029,300003,300004,700018}
SKIP={
 '/dataQuery/sequence/getSampleSequenceLatest':'已测睡眠详情参数返回1001；改由按版本接口获取，不把失败写成空数据。',
 '/dataQuery/path/getMotionPathData':'只发现模型/路由，业务调用和参数语义待确认。',
 '/dataQuery/sequence/getSampleSequenceByTime':'只发现模型，业务调用待确认。',
 '/dataService/v1/healthDatas/sampleReports/history':'App Bearer鉴权未接入，不能将tokenType=2直接套用。',
 '/achievement/v1/ftp:refresh':'刷新/计算操作且需要其他鉴权；只读导出不调用。',
 '/dataStat/v1/basicCalculations/lifetimeStatistics:calculate':'计算操作且需要serviceToken等其他鉴权；只读导出不调用。'}


def records(value):
    """Only measurement rows; deleted IDs are never measurements."""
    out=[]
    for key in ('detailInfos','data','sportStat','professionalSleepTotal','heartRateTotal','stressTotal','statisticTotal',
                'sleepTotal','sleepTotals','bloodOxygenSaturationTotal','bradycardiaAlarmTotal',
                'exerciseIntensityTotal','heartRateRiseAlarmTotal','menstrualTotal'):
        group=value.get(key)
        if isinstance(group,list):out.extend(r for r in group if isinstance(r,dict))
        elif isinstance(group,dict):
            for rows in group.values():
                if isinstance(rows,list):out.extend(r for r in rows if isinstance(r,dict))
                elif key=='data' and isinstance(rows,dict):out.append(rows)
    return out


def in_range(row, start, end):
    if row.get('recordDay'):
        return int(start.strftime('%Y%m%d'))<=int(row['recordDay'])<=int(end.strftime('%Y%m%d'))
    if row.get('startTime') or row.get('endTime'):
        return int(row.get('endTime') or row['startTime'])>=int(start.timestamp()*1000) and int(row.get('startTime') or 0)<=int(end.timestamp()*1000)
    return None  # Undated summaries are retained separately, not claimed as date-filtered.


def clean(value, names):
    if isinstance(value,str) and value.lstrip().startswith(('{','[')):
        try:return clean(json.loads(value),names)
        except ValueError:return value
    if isinstance(value,list):return [clean(v,names) for v in value]
    if not isinstance(value,dict):return value
    out={k:(v if k=='detailData' else clean(v,names)) for k,v in value.items()}
    for key in ('dataId','recordId','deviceCode'):
        if out.get(key) is not None:out[key]=str(out[key])
    if 'key' in out and 'value' in out:
        out['keyName']=names.get(str(out['key']),str(out['key']))
    times={}
    for key in ('startTime','endTime','modifiedTime'):
        v=out.get(key)
        if isinstance(v,(int,float)) and not isinstance(v,bool) and 946684800000<=v<=4102444800000:
            times[key]=dt.datetime.fromtimestamp(v/1000,h.ZONE).isoformat()
    if times:out['readableTimes']=times
    return out


def chunks(start_ms,end_ms):
    """Inclusive intervals, at most seven days, with no gaps/overlap."""
    while start_ms<=end_ms:
        stop=min(end_ms,start_ms+7*86400000-1)
        yield start_ms,stop
        start_ms=stop+1


def paged(request,path,params,target,max_pages,start,end):
    rows=[];undated=[];deleted=[];cursor=params['version'];pages=0;reason=None;complete=False
    for _ in range(max_pages):
        value=request(path,dict(params,version=cursor))
        if value is None:
            reason='request_failed_or_stopped';break
        pages+=1
        for row in records(value):
            inside=in_range(row,start,end)
            if inside is True:rows.append(row)
            elif inside is None:undated.append(row)
        deleted.extend(value.get('deleteInfos') or [])
        next_cursor=int(value.get('currentVersion') or 0)
        if target is not None and next_cursor>=target:
            complete=True;break
        if next_cursor<=cursor:
            reason='missing_target_or_cursor_stalled';break
        cursor=next_cursor
    else:reason='max_pages_reached'
    return {'records':rows,'undatedRecords':undated,'deleteInfos':deleted,'complete':complete,
            'pages':pages,'cloudTargetVersion':target,'reason':reason,
            'status':'data' if rows or undated else 'empty' if complete else 'partial'}


def export(client,output,days=30,selected=None,max_pages=100,now=None):
    if days<1 or max_pages<1:raise h.HealthError('Days and pages must be positive')
    output=Path(output)
    if output.exists() and any(output.iterdir()):
        raise h.HealthError('Output must be a new or empty directory; do not mix runs/accounts')
    output.mkdir(parents=True,exist_ok=True,mode=0o700);output.chmod(0o700)
    end=(now or dt.datetime.now(h.ZONE)).astimezone(h.ZONE)
    start=(end-dt.timedelta(days=days-1)).replace(hour=0,minute=0,second=0,microsecond=0)
    sm,em=int(start.timestamp()*1000),int(end.timestamp()*1000)
    sd,ed=int(start.strftime('%Y%m%d')),int(end.strftime('%Y%m%d'))
    tomorrow=int((end+dt.timedelta(days=1)).replace(hour=0,minute=0,second=0,microsecond=0).timestamp()*1000)
    definitions=json.loads((h.ROOT/'references/type-mapping.json').read_text())['types']
    definitions += [dict(typeId=k,name=v,category=0,cloudType=k,subTypes=[],endpoint=HEALTH) for k,v in CORE.items()]
    known={x['typeId'] for x in definitions}
    if selected and selected-known:
        raise h.HealthError('Unknown type IDs; inspect references/type-mapping.json')
    chosen=[x for x in definitions if selected is None or x['typeId'] in selected]
    catalog=json.loads((h.ROOT/'references/api-catalog.json').read_text())['interfaces']
    interfaces={x['path']:{'endpoint':x['path'],'method':x['method'],'calls':[],
                'status':'not_called','reason':SKIP.get(x['path'])} for x in catalog}
    state={'halted':False};issues=[]
    client.ready()

    def request(path,params):
        if state['halted']:return None
        item={'parameters':params.copy()}
        try:
            value=client.query(path,params)
            item.update(status='success',resultCode=0,response=value)
            return value
        except h.HealthError as error:
            item.update(status='failed',error=str(error));issues.append({'endpoint':path,'error':str(error)})
            if any(code in str(error) for code in ('HTTP 401','HTTP 403','HTTP 429')):
                state['halted']=True
            return None
        finally:
            interfaces[path]['calls'].append(item)
            # Checkpoint partial results before moving to another request.
            h.private_json(output/'interfaces'/(path.strip('/').replace('/','__')+'.json'),interfaces[path])
            time.sleep(.15)

    cloud_types={x['cloudType'] for x in chosen if x['category'] in (0,1,2) and x['typeId'] not in (33259,33260)}
    cloud_types.add(1)  # Activity segments sync key; motion-path coverage is separate/unknown.
    targets={}
    for offset in range(0,len(cloud_types),25):
        ids=sorted(cloud_types)[offset:offset+25]
        value=request(VERSIONS,{'syncKeys':[{'type':k,'dataType':2} for k in ids]})
        if value:targets.update({int(v['type']):int(v['version']) for v in value.get('versions',[])})
    by_cloud={};datasets=[]
    for item in chosen:
        kind=item['typeId'];category=item['category'];main=item['cloudType']
        result={'type':kind,'cloudType':main,'name':item['name'],'endpoint':item.get('endpoint'),
                'rangeStart':start.isoformat(),'rangeEnd':end.isoformat()}
        reason=None
        if category not in (0,1,2):reason='实时数据或其他Bearer鉴权周期报告；未调用历史tokenType=2接口。'
        elif kind in (33259,33260):reason='合并轨迹主类型33001读取映射待确认。'
        elif kind in INVALID_TYPES:reason='此类型的当前请求已实测返回1001；未重复发送，不作为空数据。'
        if reason:
            result.update(status='not_called',complete=False,reason=reason,records=[])
        else:
            path={0:HEALTH,1:SEQUENCE,2:STAT}[category]
            if main not in by_cloud:
                params={'type':main,'dataType':2,'version':sm-86400000}
                params.update({'dataSource':2} if category==2 else {'deviceCode':0})
                if category==1:params['subTypes']=[]
                by_cloud[main]=paged(request,path,params,targets.get(main),max_pages,start,end)
            result.update(by_cloud[main])
            if item['subTypes']:
                allowed=set(item['subTypes'])
                result['records']=[r for r in result['records'] if int(r.get('subType',-1)) in allowed]
                result['undatedRecords']=[r for r in result['undatedRecords'] if int(r.get('subType',-1)) in allowed]
                result['derivedFromCloudType']=main
                result['status']='data' if result['records'] or result['undatedRecords'] else 'empty' if result['complete'] else 'partial'
        datasets.append(result)
    for name,path,target in [('ACTIVITY_SEGMENTS',SPORT,targets.get(1)),('WORKOUT_PATH',TRACK,None)]:
        result=paged(request,path,{'dataType':2,'deviceCode':0,'version':sm-86400000},target,max_pages,start,end)
        datasets.append(dict(result,name=name,endpoint=path))
    dates={'startTime':sd,'endTime':ed};stats=dict(dates,dataSource=2,deviceCode=0)
    reads=[
      ('/dataQuery/sport/v2/getSportsStat',stats),
      ('/dataQuery/sport/getSportsDimenStat',dict(dates,dataSource=2)),
      ('/dataQuery/health/getHealthStat',dict(stats,types=[3,7,9,11,16])),
      ('/dataQuery/sport/getSleepStat',stats),
      ('/dataQuery/sport/getSleepSportsDataLatest',{'timestamp':tomorrow,'days':min(days,7),'dataType':2}),
      ('/dataQuery/health/getHealthDataByRecordDay',{'recordDay':ed,'type':800001,'dataType':2,'deviceCode':0}),
      ('/dataAnalyse/app/getRunLevel',{'startDay':sd,'endDay':ed,'queryType':0,'types':[1,2,3,4,5,6],'timeZone':'+0800'}),
      ('/dataQuery/health/getHealthDataLatest',{'timestamp':tomorrow+20*3600000,'counts':1,'type':9,'dataType':2}),
      ('/dataQuery/health/getHealthDataLatest',{'timestamp':em,'counts':1,'type':300012,'dataType':0}),
      ('/dataQuery/health/getHealthStatistics',dict(dates,types=[300012])),
      ('/dataQuery/report/getSampleReportByDay',{'startDay':sd,'endDay':ed,'type':300010,'dataType':2,'deviceCode':0}),
      ('/dataQuery/report/getPersonalReport',{'accumulatedItems':['accumPerfectGoalAchievedDays']}),
      ('/dataQuery/report/getCycleReport',{'bestItems':['bestRopeSkippingSingleCount','bestRopeSkippingContinuousCount','bestRopeSkippingMaxSpeed1MIN','bestRopeSkippingEnduranceAbility','bestRopeSkippingEnduranceTimeAbility'],'queryType':1,'startTime':int(end.replace(day=1,hour=0,minute=0,second=0,microsecond=0).timestamp()*1000),'endTime':em,'timeZone':'+0800'})]
    daily=None
    for path,params in reads:
        value=request(path,params)
        if path=='/dataQuery/sport/v2/getSportsStat':daily=value
    for left,right in chunks(sm,em):
        for kind in (7,500044):
            if selected is None or kind in selected:
                request('/dataQuery/health/getHealthData',{'type':kind,'queryType':2,'dataType':2,'deviceCode':0,'startTime':left,'endTime':right})
        request('/dataQuery/sport/getSportsDataByTime',{'queryType':2,'dataType':2,'deviceCode':0,'startTime':left,'endTime':right,'sportTypes':[6,7,8]})
    names={'DATA_POINT_DYNAMIC_HEARTRATE':'动态心率','DATA_POINT_REST_HEARTRATE':'静息心率',
           'DATA_POINT_NEW_REST_HEARTRATE':'新版静息心率','PROFESSIONAL_SLEEP_SHALLOW':'浅睡',
           'PROFESSIONAL_SLEEP_DEEP':'深睡','PROFESSIONAL_SLEEP_DREAM':'快速眼动睡眠',
           'PROFESSIONAL_SLEEP_WAKE':'清醒','STRESS_DATA':'压力测量','HEART_RATE_VARIABILITY':'HRV RMSSD'}
    for item in definitions:
        for field in item.get('fields',[]):
            code=field.get('healthType',field.get('statType'))
            if code is not None:names[str(code)]=field['name']
    for result in datasets:
        name=str(result.get('type',result['name']))+'_'+result['name']+'.json'
        h.private_json(output/'datasets'/name,clean(result,names))
    # Keep each source row; do not add totals from different devices.
    for field,unit in [('steps','步'),('distance','m'),('floor','层'),('calorie','cal')]:
        projected=[]
        for row in (daily or {}).get('sportStat',[]):
            value=(row.get('sportBasicInfo') or {}).get(field)
            item={k:row.get(k) for k in ('recordDay','timeZone','dataSource','deviceCode','dataId')}
            item.update(value=value,unit=unit)
            if field=='calorie':item['kcal']=value/1000 if isinstance(value,(int,float)) else None
            projected.append(clean(item,names))
        h.private_json(output/'activity'/(field+'.json'),{'field':field,'records':projected,
            'sourceEndpoint':'/dataQuery/sport/v2/getSportsStat',
            'status':'data' if projected else 'empty' if daily is not None else 'failed',
            'missingValue':'null表示该来源未返回字段，不补成0'})
    index=[]
    for path,entry in interfaces.items():
        successful=[c for c in entry['calls'] if c['status']=='success']
        count=sum(len(records(c['response'])) for c in successful)
        objects=any(c['response'].get(k) for c in successful for k in ('accumulatedReports','userRunLevelData','userCycleReport'))
        entry['status']=('metadata' if path==VERSIONS else 'data' if count or objects else 'empty') if successful else 'failed' if entry['calls'] else 'not_called'
        entry['recordCount']=count
        file='interfaces/'+path.strip('/').replace('/','__').replace(':','_')+'.json'
        h.private_json(output/file,entry)
        index.append({k:v for k,v in dict(entry,file=file).items() if k!='calls'})
    mapping=json.loads((h.ROOT/'references/mapping.json').read_text())
    mapping.update(sampleKeyMapping=names,enumMapping={'emotionStatus':{'1':'不愉悦','2':'平静','3':'愉悦'}},
                   outputFields={'records':'按记录时间过滤的数据','undatedRecords':'无可确认事件时间的记录；未声称属于日期窗口',
                    'complete':'按版本分页到达快照目标；与是否有数据无关','cloudTargetVersion':'开始导出时的云端同步目标',
                    'readableTimes':'外层毫秒时间的UTC+8可读副本','keyName':'指标名称，见sampleKeyMapping'})
    h.private_json(output/'mapping.json',mapping)
    h.private_json(output/'type-mapping.json',{'types':definitions})
    h.private_json(output/'provenance.json',json.loads((h.ROOT/'references/apk-provenance.json').read_text()))
    manifest={'status':'partial','scope':'source-discovered native API catalog; full App coverage not verified',
      'appVersion':'17.0.10.300','rangeStart':start.isoformat(),'rangeEnd':end.isoformat(),
      'timezone':'Asia/Shanghai','credentialsIncluded':False,'stoppedOnAuthOrRateLimit':state['halted'],
      'endpointCounts':dict(Counter(e['status'] for e in index)), 'interfaces':index,
      'datasets':[{k:v for k,v in d.items() if k not in ('records','undatedRecords','deleteInfos')} |
                   {'recordCount':len(d['records'])} for d in datasets], 'issues':issues,
      'limitations':['不同App鉴权、合并记录与实时类型未接入；manifest保持partial，不代表可读数据导出失败。',
       '轨迹目标版本未知；空响应不能证明手机App没有训练记录。',
       '最新记录不是全历史；getHealthDataByRecordDay仅查询最新一页；getCycleReport仅当前月；无日期的累计统计不强行归入30天。',
       '日汇总按来源保留，不把多设备/多接口结果相加；活动能量cal除以1000才是kcal。',
       '原始响应保存在各接口JSON calls[].response；清洗结果保存在datasets/；detailData二进制内容未解码。']}
    h.private_json(output/'manifest.json',manifest)
    archive=output/'huawei-health-data.zip'
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
        for file in sorted(output.rglob('*.json')):z.write(file,str(file.relative_to(output)))
    archive.chmod(0o600)
    for folder in [p for p in output.rglob('*') if p.is_dir()]:folder.chmod(0o700)
    return manifest
