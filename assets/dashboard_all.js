// Full-data views: semantic field charts plus a lazy, paginated archive browser.
const catalog = data.catalog || [];
const metricGroups = data.metricGroups || [];
const groupPrefs = new Map();
const categoryColors = {'活动与运动':'#ff783e','心率与状态':'#ff6875','睡眠与作息':'#bb9df7','目标与成就':'#8edb66','原始序列':'#359fff'};
const archiveState = {file:null,artifact:null,collection:'records',page:0,category:'全部',loadTicket:0};
const fileCache = new Map();
const statusLabels = {data:'有数据',empty:'成功但为空',failed:'失败',not_called:'未调用',partial:'部分覆盖',metadata:'说明文件'};
function summaryMarkup(metrics=false){
  const datasets=catalog.filter(x=>x.kind==='datasets');
  if(metrics)return [[metricGroups.length,'个健康主题'],[metricGroups.reduce((n,g)=>n+g.fields.length,0),'个可切换字段'],[datasets.filter(x=>x.recordCount||x.undatedCount).length,'个有记录的类型']].map(([v,label])=>`<div><strong>${fmt(v)}</strong><span>${label}</span></div>`).join('')+`<p>${esc(data.rangeStart.slice(0,10))} — ${esc(data.rangeEnd.slice(0,10))}<br>完整快照 · 不同来源分别查看</p>`;
  return [[catalog.length,'份完整 JSON 档案'],[datasets.length,'个数据集'],[datasets.filter(x=>x.recordCount||x.undatedCount).length,'个有记录的数据集'],[catalog.filter(x=>x.kind==='interfaces').length,'份接口结果']].map(([v,label])=>`<div><strong>${fmt(v)}</strong><span>${label}</span></div>`).join('')+`<p>所有文件均已内嵌，可离线查看。数据集与接口结果可能重复，不合并计算。</p>`;
}
function setView(view,updateHash=true){
  state.view=['overview','metrics','archive'].includes(view)?view:'overview';
  document.body.dataset.view=state.view;
  for(const v of ['overview','metrics','archive'])$('view-'+v).hidden=v!==state.view;

function jumpToMetric(name){
  const demoNames={HEART_RATE:'heartRate',HEART_RATE_VARIABILITY:'hrv',SLEEP_DETAILS:'sleepScore',STRESS:'stress'};
  const group=metricGroups.find(g=>g.id.endsWith(':'+name))||metricGroups.find(g=>g.id==='demo:'+demoNames[name]);
  if(!group){toast('本次导出未返回此主题的详情，可在数据档案检查覆盖范围');return;}
  archiveState.category='全部';
  document.querySelectorAll('[data-category]').forEach(b=>{b.classList.toggle('selected',b.dataset.category==='全部');b.setAttribute('aria-pressed',String(b.dataset.category==='全部'));});
  setView('metrics');
  requestAnimationFrame(()=>{const card=[...document.querySelectorAll('[data-group]')].find(c=>c.dataset.group===group.id);if(card){card.classList.add('metric-spotlight');card.scrollIntoView({behavior:state.paused?'instant':'smooth',block:'center'});const control=card.querySelector('select');control?.focus({preventScroll:true});setTimeout(()=>card.classList.remove('metric-spotlight'),1800);}});
}
document.querySelectorAll('[data-jump-name]').forEach(button=>button.addEventListener('click',()=>jumpToMetric(button.dataset.jumpName)));

document.querySelectorAll('[data-view]').forEach(b=>{b.classList.toggle('selected',b.dataset.view===state.view);b.setAttribute('aria-pressed',String(b.dataset.view===state.view));});
  set('page-title',{overview:'我的运动健康',metrics:'全部健康指标',archive:'健康数据档案'}[state.view]);
  if(updateHash)history.replaceState(null,'','#'+({overview:'overview',metrics:'all-metrics',archive:'data-archive'}[state.view]));
  if(state.view==='metrics')renderAllMetrics();
  if(state.view==='archive'){renderFileList();if(!archiveState.file&&catalog.length)openArtifact(catalog.find(x=>x.recordCount)?.file||catalog[0].file);}
  if(state.view==='overview')renderTrend();
  if(updateHash)window.scrollTo(0,0);
}
function windowContains(date){return date && date>=dateShift(state.date,1-state.period)&&date<=state.date;}
function clockValue(value){const v=Number(value),ms=v<4102444800?v*1000:v;return ms>=946684800000&&ms<4102444800000?new Date(ms):null;}
function formatField(value,field){
  if(value==null)return '—';
  if(field.categorical)return ({1:'不愉悦',2:'平静',3:'愉悦'})[value]||`未知类别 ${value}`;
  if(field.clock&&clockValue(value))return clockValue(value).toLocaleTimeString('zh-CN',{timeZone:'Asia/Shanghai',hour:'2-digit',minute:'2-digit',hour12:false});
  return fmt(value,Number.isInteger(value)?0:2);
}
function fieldPlot(points,field,color){
  if(!points.length)return empty('当前范围与来源没有此字段记录');
  const maxPoints=160,display=points.length>maxPoints?Array.from({length:maxPoints},(_,i)=>points[Math.round(i*(points.length-1)/(maxPoints-1))]):points;
  const values=display.map(p=>p.value),min=Math.min(...values),max=Math.max(...values),span=Math.max(max-min,1),w=440,h=92;
  const coords=display.map((p,i)=>[8+i/Math.max(1,display.length-1)*(w-16),h-20-(p.value-min)/span*(h-35)]);
  const path=coords.map((p,i)=>`${i?'L':'M'}${p[0].toFixed(2)},${p[1].toFixed(2)}`).join(' ');
  const dots=coords.map((p,i)=>`<circle cx="${p[0]}" cy="${p[1]}" r="${field.categorical?3:1.8}" fill="${field.categorical?({1:'#ff6875',2:'#359fff',3:'#8edb66'})[display[i].value]||color:color}"><title>${esc(display[i].date||'累计 / 无日期')} ${esc(time(display[i].time))} · ${esc(formatField(display[i].value,field))}</title></circle>`).join('');
  return `<svg viewBox="0 0 ${w} ${h}" preserveAspectRatio="none" role="img" aria-label="${esc(field.label)}，共 ${points.length} 个字段值"><line x1="0" y1="${h-17}" x2="${w}" y2="${h-17}" class="chart-grid"/><path d="${path}" stroke="${color}" stroke-width="1.5" fill="none" vector-effect="non-scaling-stroke"/>${dots}</svg><span class="sample-note">${points.length>maxPoints?`等距抽样 ${maxPoints} / ${fmt(points.length)} 点 · 全量见明细`:`${fmt(points.length)} 个字段值`}</span>`;
}
function renderAllMetrics(){
  const selected=archiveState.category;
  const visible=metricGroups.filter(g=>selected==='全部'||g.category===selected);
  $('all-metric-grid').innerHTML=visible.map(group=>{
    const pref=groupPrefs.get(group.id)||{};
    const field=group.fields.find(f=>f.key===pref.field)||group.fields[0];
    const counts={};for(const p of group.points)counts[p.source]=(counts[p.source]||0)+1;
    const sources=Object.keys(counts).sort((a,b)=>counts[b]-counts[a]||a.localeCompare(b));
    const source=sources.includes(pref.source)?pref.source:sources[0];
    groupPrefs.set(group.id,{field:field?.key,source});
    let points=group.points.filter(p=>p.source===source&&(!$('metrics-scope').value.includes('window')||!p.date||windowContains(p.date))).filter(p=>field&&Number.isFinite(p.values[field.key])).map(p=>({...p,value:p.values[field.key]}));
    points.sort((a,b)=>(a.time||a.date||'').localeCompare(b.time||b.date||''));
    const placeholders=field?.positive?points.filter(p=>p.value<=0).length:0;
    if(field?.positive)points=points.filter(p=>p.value>0);
    const last=points.at(-1),color=/HEART_RATE_VARIABILITY|demo:hrv/.test(group.id)?colors.hrv:/STRESS|stress/.test(group.id)?colors.stress:categoryColors[group.category]||'#ff783e';
    const invalidClock=field?.clock&&last&&!clockValue(last.value);
    const unit=field?.categorical?'类别':field?.clock&&!invalidClock?'时间':field?.unit||'原始值';
    return `<article class="full-metric-card" data-group="${esc(group.id)}" style="--group-color:${color}"><div class="full-card-heading"><h2 title="${esc(group.title)}"><span class="group-icon"><svg><use href="#${({'活动与运动':'i-step','心率与状态':'i-heart','睡眠与作息':'i-moon','目标与成就':'i-grid','原始序列':'i-source'})[group.category]||'i-pulse'}"/></svg></span>${esc(group.title)}</h2><span>${fmt(group.recordCount)} 条记录</span></div>${group.fields.length?`<div class="field-controls"><select data-group-field aria-label="${esc(group.title)}字段">${group.fields.map(f=>`<option value="${esc(f.key)}" ${f.key===field.key?'selected':''}>${esc(f.label)}</option>`).join('')}</select><select data-group-source aria-label="${esc(group.title)}来源">${sources.map(s=>`<option value="${esc(s)}" ${s===source?'selected':''}>${esc(data.sources[s]?.label||s)}</option>`).join('')}</select></div><div class="full-reading"><strong>${esc(last?formatField(last.value,field):'—')}</strong><span>${esc(unit)}</span><small>${last?esc(last.date||'累计 / 无日期'):'当前范围无值'}</small></div><div class="full-field-chart">${fieldPlot(points,field,color)}</div><details class="field-explanation"><summary>${group.fields.length} 个字段${placeholders?` · ${placeholders} 个占位值仅在明细保留`:' · 查看单位说明'}</summary><p>${esc(field.note)}${invalidClock?' 当前值不是已确认的 Unix 时间戳，按原值保留。':''}</p></details>`:`<div class="sequence-preview"><svg><use href="#i-pulse"/></svg><strong>${fmt(group.binaryCount||group.recordCount)}</strong><span>${group.binaryCount?'个未解码数据包':'条原始记录'}</span><p>时间、摘要、原始内容均可查看<br>尚未解码的内容保留原值</p></div>`}<button class="full-detail-button" data-open-file="${esc(group.file)}">查看全部记录与字段 <svg><use href="#i-arrow"/></svg></button></article>`;
  }).join('')||empty('这个分类没有返回记录。全部类型及读取状态可在数据档案中查看。');
  $('all-metric-grid').querySelectorAll('[data-group]').forEach(card=>{
    const id=card.dataset.group;
    for(const [selector,key] of [['[data-group-field]','field'],['[data-group-source]','source']])card.querySelector(selector)?.addEventListener('change',e=>{groupPrefs.get(id)[key]=e.target.value;renderAllMetrics();[...document.querySelectorAll('[data-group]')].find(c=>c.dataset.group===id)?.querySelector(selector)?.focus({preventScroll:true});});
  });
  $('all-metric-grid').querySelectorAll('[data-open-file]').forEach(button=>button.addEventListener('click',()=>{setView('archive');openArtifact(button.dataset.openFile);}));
  $('all-metric-grid').querySelectorAll('.full-reading strong').forEach(el=>{el.title=el.textContent;el.classList.toggle('long-value',el.textContent.length>9);});
}
function renderFileList(){
  const term=$('file-search').value.trim().toLowerCase(),kind=$('file-kind').value,status=$('file-status').value;
  const entries=catalog.filter(e=>(kind==='all'||e.kind===kind)&&(status==='all'||(status==='partial'?e.complete===false:e.status===status))&&(!term||`${e.title} ${e.name} ${e.file} ${e.endpoint||''}`.toLowerCase().includes(term)));
  set('file-match-count',`${entries.length} / ${catalog.length} 份文件`);
  $('archive-file-list').innerHTML=entries.map(e=>`<button class="file-entry ${e.file===archiveState.file?'selected':''}" data-file="${esc(e.file)}" title="${esc(e.file)}"><span class="file-status-dot ${e.status==='data'?'has-data':''}"></span><span><strong>${esc(e.title)}</strong><small>${esc(e.kind==='interfaces'?e.endpoint:e.file)}</small></span><em>${e.kind==='interfaces'?`${e.callCount||0} 次请求`:e.recordCount||e.undatedCount?`${fmt(e.recordCount+e.undatedCount)} 条`:esc(statusLabels[e.status]||e.status)}</em></button>`).join('')||empty('没有匹配的文件');
  $('archive-file-list').querySelectorAll('[data-file]').forEach(button=>button.addEventListener('click',()=>openArtifact(button.dataset.file)));
}
async function decodeArtifact(file){
  if(fileCache.has(file))return fileCache.get(file);
  const encoded=data.archive?.files?.[file];if(!encoded)throw Error('此文件未包含在当前页面中');
  if(typeof DecompressionStream!=='function')throw Error('当前浏览器不支持离线解压，请使用支持 DecompressionStream 的浏览器');
  const bytes=Uint8Array.from(atob(encoded),c=>c.charCodeAt(0));
  const stream=new Blob([bytes]).stream().pipeThrough(new DecompressionStream('gzip'));
  const artifact=JSON.parse(await new Response(stream).text());
  fileCache.set(file,artifact);if(fileCache.size>2)fileCache.delete(fileCache.keys().next().value);
  return artifact;
}
async function openArtifact(file){
  const entry=catalog.find(e=>e.file===file);if(!entry)return;
  const ticket=++archiveState.loadTicket;archiveState.file=file;archiveState.artifact=null;archiveState.page=0;
  set('archive-title',entry.title);set('archive-file-path',file);set('record-message','正在本地解压文件…');$('record-content').replaceChildren();$('download-artifact').disabled=true;renderFileList();
  $('archive-file-meta').innerHTML=`<span class="coverage-chip">${esc(statusLabels[entry.status]||entry.status)}</span><span class="coverage-chip">${fmt(entry.byteSize/1024,1)} KB 原文件</span>${entry.complete!=null?`<span class="coverage-chip">${entry.complete?'分页目标已到达':'覆盖未完成'}</span>`:''}`;
  try{
    const artifact=await decodeArtifact(file);if(ticket!==archiveState.loadTicket)return;
    archiveState.artifact=artifact;$('download-artifact').disabled=false;
    const collections=[['records','记录'],['undatedRecords','无日期记录'],['deleteInfos','删除标记'],['calls','请求与响应']].filter(([k])=>Array.isArray(artifact[k]));
    $('record-collection').innerHTML=collections.map(([k,label])=>`<option value="${k}">${label} (${artifact[k].length})</option>`).join('')+'<option value="full">完整文件与所有字段</option>';
    archiveState.collection=collections.find(([k])=>artifact[k].length)?.[0]||'full';$('record-collection').value=archiveState.collection;
    $('record-source').value='all';$('record-search').value='';$('record-date-scope').value='all';updateRecordSources();renderRecords();
  }catch(error){if(ticket===archiveState.loadTicket)set('record-message',`无法打开：${error.message}`);}
}
function recordSource(row){
  if(!row||typeof row!=='object')return '未提供来源';
  const device=row.deviceCode??'未知',origin=row.dataSource??row.healthDataSource??row.sportDataSource??'未知';
  return device==='未知'&&origin==='未知'?'未提供来源':`设备 ${device} / 来源 ${origin}`;
}
function recordDate(row){
  if(!row||typeof row!=='object')return null;
  const day=String(row.recordDay||'');if(/^\d{8}$/.test(day))return `${day.slice(0,4)}-${day.slice(4,6)}-${day.slice(6)}`;
  const date=clockValue(row.endTime||row.startTime);return date?new Intl.DateTimeFormat('sv-SE',{timeZone:'Asia/Shanghai',year:'numeric',month:'2-digit',day:'2-digit'}).format(date):null;
}
function updateRecordSources(){
  const rows=archiveState.artifact?.[archiveState.collection]||[];
  const sources=Array.isArray(rows)?[...new Set(rows.map(recordSource))]:[];
  $('record-source').innerHTML='<option value="all">全部来源</option>'+sources.map(s=>`<option value="${esc(s)}">${esc(s)}</option>`).join('');
}
function jsonTree(value,label='记录',open=false){
  const composite=value!==null&&typeof value==='object';
  if(!composite){
    const wrap=document.createElement(typeof value==='string'&&value.length>500?'details':'div');wrap.className='tree-leaf';
    if(wrap.tagName==='DETAILS'){const summary=document.createElement('summary');summary.textContent=`${label} · ${value.length.toLocaleString()} 字符${label==='detailData'?' · 未解码原始内容':''}`;wrap.append(summary);wrap.addEventListener('toggle',()=>{if(wrap.open&&wrap.childElementCount===1){const pre=document.createElement('pre');pre.textContent=value;wrap.append(pre);}});}
    else{const key=document.createElement('b');key.textContent=label+': ';wrap.append(key);const span=document.createElement('span');span.textContent=value===null?'null':String(value);wrap.append(span);}
    return wrap;
  }
  const details=document.createElement('details');details.className='json-tree';const summary=document.createElement('summary');const entries=Object.entries(value);summary.textContent=`${label} · ${entries.length} ${Array.isArray(value)?'项':'个字段'}`;details.append(summary);
  let built=false;
  details.addEventListener('toggle',()=>{if(!details.open||built)return;built=true;const body=document.createElement('div');body.className='tree-children';details.append(body);let count=0;const more=document.createElement('button');more.className='quiet-button tree-more';function append(){more.remove();const stop=Math.min(count+100,entries.length);for(;count<stop;count++)body.append(jsonTree(entries[count][1],entries[count][0]));if(count<entries.length){more.textContent=`再展示 100 项（余 ${entries.length-count} 项）`;body.append(more);}}more.addEventListener('click',append);append();});
  if(open)details.open=true;
  return details;
}
function rowSummary(row){
  if(!row||typeof row!=='object')return String(row);
  if(row.detailData)return `含原始序列 ${String(row.detailData).length.toLocaleString()} 字符；摘要与未知字段可展开`;
  const points=row.samplePoints?.[0];let values=points?.value||row.summaryData||row.sportBasicInfo||row.heartRateBasic||row.stressBasic||row.professionalSleep||row;
  if(typeof values==='string'){try{values=JSON.parse(values);}catch{return values.slice(0,100);}}
  if(!values||typeof values!=='object')return String(values);
  return Object.entries(values).filter(([k])=>!['dataId','recordId','readableTimes','deviceCode','version','startTime','endTime','modifiedTime'].includes(k)).slice(0,3).map(([k,v])=>`${k}: ${v!==null&&typeof v==='object'?(Array.isArray(v)?`${v.length} 项`:`${Object.keys(v).length} 个字段`):String(v).slice(0,55)}`).join(' / ');
}
function renderRecords(){
  if(!archiveState.artifact)return;
  const artifact=archiveState.artifact,collection=archiveState.collection,entry=catalog.find(e=>e.file===archiveState.file);
  const full=collection==='full';for(const id of ['record-source','record-date-scope','record-search'])$(id).disabled=full;
  const reason=entry?.reason?` ${entry.reason}`:'';
  set('record-message',`${reason} 全部字段均可展开；大整数以字符串保留，认证字段已隐藏。未知编码与单位按原值展示。`.trim());
  $('record-content').replaceChildren();
  if(full){$('record-content').append(jsonTree(artifact,'完整文件',true));set('record-count-label','包含记录、来源、状态及全部未知字段');set('record-page','完整文件');$('record-prev').disabled=true;$('record-next').disabled=true;return;}
  const all=artifact[collection]||[],source=$('record-source').value,scope=$('record-date-scope').value,query=$('record-search').value.trim().toLowerCase();
  const filtered=all.filter(row=>{
    if(source!=='all'&&recordSource(row)!==source)return false;
    const date=recordDate(row);if(scope==='window'&&!windowContains(date))return false;if(scope==='day'&&date!==state.date)return false;if(scope==='undated'&&date)return false;
    return !query||JSON.stringify(row).toLowerCase().includes(query);
  });
  const pages=Math.max(1,Math.ceil(filtered.length/50));archiveState.page=Math.min(archiveState.page,pages-1);
  const begin=archiveState.page*50,visible=filtered.slice(begin,begin+50);
  if(!visible.length)$('record-content').innerHTML=empty('没有匹配的记录。可选择全部来源、全部日期或清空搜索。');
  else{
    const table=document.createElement('table');table.className='records-table';table.innerHTML='<thead><tr><th>时间 / 日期</th><th>来源</th><th>记录摘要与完整字段</th></tr></thead>';const body=document.createElement('tbody');table.append(body);
    visible.forEach((row,index)=>{const tr=document.createElement('tr'),date=document.createElement('td'),src=document.createElement('td'),content=document.createElement('td');date.textContent=recordDate(row)||'无日期';src.textContent=recordSource(row);const preview=document.createElement('p');preview.className='row-preview';preview.textContent=rowSummary(row);content.append(preview,jsonTree(row,`查看第 ${begin+index+1} 条的全部字段`));tr.append(date,src,content);body.append(tr);});$('record-content').append(table);
  }
  set('record-count-label',`${fmt(filtered.length)} / ${fmt(all.length)} 条 · 每页 50 条`);set('record-page',`${archiveState.page+1} / ${pages}`);$('record-prev').disabled=archiveState.page===0;$('record-next').disabled=archiveState.page>=pages-1;
}
document.querySelectorAll('[data-view]').forEach(button=>button.addEventListener('click',()=>setView(button.dataset.view)));
document.querySelectorAll('.rail-nav a,.mark').forEach(link=>link.addEventListener('click',()=>setView('overview',false)));
const categories=['全部',...Object.keys(categoryColors)];
$('category-filters').innerHTML=categories.map(category=>`<button data-category="${category}" class="${category==='全部'?'selected':''}" aria-pressed="${category==='全部'}">${category}<span>${category==='全部'?metricGroups.length:metricGroups.filter(g=>g.category===category).length}</span></button>`).join('');
$('category-filters').querySelectorAll('button').forEach(button=>button.addEventListener('click',()=>{archiveState.category=button.dataset.category;document.querySelectorAll('[data-category]').forEach(b=>{b.classList.toggle('selected',b===button);b.setAttribute('aria-pressed',String(b===button));});renderAllMetrics();}));
$('metrics-scope').addEventListener('change',renderAllMetrics);
for(const id of ['file-kind','file-status'])$(id).addEventListener('change',renderFileList);
$('file-search').addEventListener('input',renderFileList);
$('record-collection').addEventListener('change',event=>{archiveState.collection=event.target.value;archiveState.page=0;updateRecordSources();renderRecords();});
for(const id of ['record-source','record-date-scope'])$(id).addEventListener('change',()=>{archiveState.page=0;renderRecords();});
let recordSearchTimer;$('record-search').addEventListener('input',()=>{clearTimeout(recordSearchTimer);recordSearchTimer=setTimeout(()=>{archiveState.page=0;renderRecords();},250);});
$('record-prev').addEventListener('click',()=>{archiveState.page=Math.max(0,archiveState.page-1);renderRecords();});
$('record-next').addEventListener('click',()=>{archiveState.page++;renderRecords();});
$('download-artifact').addEventListener('click',()=>{if(!archiveState.artifact)return;const blob=new Blob([JSON.stringify(archiveState.artifact,null,2)],{type:'application/json'}),url=URL.createObjectURL(blob),link=document.createElement('a');link.href=url;link.download=archiveState.file.split('/').at(-1);document.body.append(link);link.click();link.remove();setTimeout(()=>URL.revokeObjectURL(url),5000);});
window.addEventListener('hashchange',()=>{const hash=location.hash;setView(hash==='#all-metrics'?'metrics':hash==='#data-archive'?'archive':'overview',false);});
set('archive-count',String(catalog.length));$('all-summary').innerHTML=summaryMarkup(true);$('archive-summary').innerHTML=summaryMarkup();
setView(location.hash==='#all-metrics'?'metrics':location.hash==='#data-archive'?'archive':'overview',false);
