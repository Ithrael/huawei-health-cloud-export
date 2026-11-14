'use strict';
const data = JSON.parse(document.getElementById('dashboard-data').textContent);
const $ = id => document.getElementById(id);
const colors = {steps:'#ff783e',calorie:'#ffd23d',distance:'#359fff',heartRate:'#ff6875',hrv:'#ff6c9c',stress:'#8edb66',deep:'#8f6ee5',light:'#c1a8fc',rem:'#6097ed',awake:'#ffc45c'};
const names = {steps:'步数',calorie:'活动热量',distance:'行走距离',floor:'爬楼',heartRate:'心率',restHeartRate:'静息心率',hrv:'HRV RMSSD',stress:'压力',sleepScore:'睡眠评分',sleep:'睡眠区间'};
const state = {date:data.rangeEnd.slice(0,10),period:7,metric:'steps',sources:{},goals:{steps:10000,calorie:500,distance:8},paused:matchMedia('(prefers-reduced-motion: reduce)').matches};
const esc = value => String(value ?? '').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const fmt = (v,d=0) => v == null ? '—' : Number(v).toLocaleString('zh-CN',{maximumFractionDigits:d,minimumFractionDigits:d});
const set = (id,value) => {$(id).textContent=value;};
const time = iso => iso ? iso.slice(11,16) : '未提供测量时间';
const dayLabel = date => `${Number(date.slice(5,7))} 月 ${Number(date.slice(8,10))} 日`;
const dateShift = (date,n) => {const d=new Date(`${date}T12:00:00+08:00`);d.setUTCDate(d.getUTCDate()+n);return d.toISOString().slice(0,10);};
const allRows = metric => metric==='sleep' ? data.sleep : (data.metrics[metric] || []);
for(const metric of [...Object.keys(data.metrics),'sleep']){
  const counts={};for(const row of allRows(metric)) counts[row.source]=(counts[row.source]||0)+1;
  state.sources[metric]=Object.keys(counts).sort((a,b)=>counts[b]-counts[a] || a.localeCompare(b))[0];
}
const rows = (metric,date=state.date) => allRows(metric).filter(r=>r.source===state.sources[metric] && r.date===date);
const daily = (metric,date=state.date) => {const r=rows(metric,date);return r.length ? r[r.length-1].value : null;};
const latest = metric => rows(metric).slice(-1)[0];
const scaled = (metric,v) => v == null ? null : metric==='distance' ? v/1000 : v;
const empty = message => `<div class="empty-chart">${esc(message)}</div>`;
let toastTimer;
function toast(message){set('toast',message);$('toast').classList.add('visible');clearTimeout(toastTimer);toastTimer=setTimeout(()=>$('toast').classList.remove('visible'),3000);}
function animateNumber(id,target,decimals=0){
  const el=$(id);if(el._frame)cancelAnimationFrame(el._frame);
  if(target==null){el.textContent='—';el.dataset.value='';return;}
  const previous=el.dataset.value==='' ? target : Number(el.dataset.value ?? target*.72);
  el.dataset.value=target;
  if(state.paused){el.textContent=fmt(target,decimals);return;}
  let begin;
  function tick(stamp){begin ??=stamp;const p=Math.min(1,(stamp-begin)/850);el.textContent=fmt(previous+(target-previous)*(1-Math.pow(1-p,3)),decimals);if(p<1)el._frame=requestAnimationFrame(tick);}
  el._frame=requestAnimationFrame(tick);
}
function chartLine(id,points,color){
  if(!points.length){$(id).innerHTML=empty('当天暂无可展示的测量');return;}
  const w=500,h=49,pad=4,values=points.map(x=>x.value),min=Math.min(...values),max=Math.max(...values),range=Math.max(8,max-min);
  const stamps=points.map(x=>x.time ? new Date(x.time).getTime() : null),hasTime=stamps.every(x=>x!==null),left=hasTime?Math.min(...stamps):0,right=hasTime?Math.max(...stamps):points.length-1;
  const coords=points.map((p,i)=>[pad+(w-pad*2)*(hasTime?(stamps[i]-left)/Math.max(1,right-left):i/Math.max(1,points.length-1)),h-pad-(p.value-min)/range*(h-pad*2)]);
  const path=coords.map((p,i)=>`${i?'L':'M'}${p[0].toFixed(1)},${p[1].toFixed(1)}`).join(' '),last=coords.at(-1);
  const dots=points.length<15?coords.map((p,i)=>`<circle cx="${p[0]}" cy="${p[1]}" r="2.3" fill="${color}" stroke="${getComputedStyle(document.body).getPropertyValue('--panel')}"><title>${esc(time(points[i].time))} · ${fmt(points[i].value,1)}</title></circle>`).join(''):'';
  $(id).innerHTML=`<svg viewBox="0 0 ${w} ${h}" preserveAspectRatio="none" role="img" aria-label="${points.length} 个测量点，${fmt(min,1)} 至 ${fmt(max,1)}"><defs><linearGradient id="${id}-fill" x1="0" y1="0" x2="0" y2="1"><stop stop-color="${color}" stop-opacity=".15"/><stop offset="1" stop-color="${color}" stop-opacity="0"/></linearGradient></defs><path d="${path} L${last[0]},${h} L${coords[0][0]},${h}Z" fill="url(#${id}-fill)" stroke="none"/><path class="chart-line" d="${path}" stroke="${color}" fill="none" vector-effect="non-scaling-stroke"/>${dots}<circle cx="${last[0]}" cy="${last[1]}" r="2.6" stroke="none" fill="${color}"/></svg>`;
}
function renderActivity(){
  const steps=daily('steps'),calorie=daily('calorie'),distance=scaled('distance',daily('distance'));
  animateNumber('steps-value',steps);animateNumber('calorie-value',calorie);animateNumber('distance-value',distance,2);
  set('steps-goal',fmt(state.goals.steps));set('ring-percent',steps==null?'—':`${fmt(100*steps/state.goals.steps)}%`);
  set('goal-state',steps==null?'暂无记录':steps>=state.goals.steps?'目标已达成':`还差 ${fmt(state.goals.steps-steps)} 步`);
  const before=daily('steps',dateShift(state.date,-1));
  set('activity-note',steps==null?'选择其他日期或检查导出覆盖范围。':before==null?'用每一步，记录今天的活动。':`比前一天${steps>=before?'多':'少'}走 ${fmt(Math.abs(steps-before))} 步`);
  set('activity-source',`来源：${data.sources[state.sources.steps]?.label || '无活动记录'}`);
  const metrics=[['steps',steps,126],['calorie',calorie,104],['distance',distance,82]];
  $('activity-rings').innerHTML=metrics.map(([key,value,r])=>{
    const c=2*Math.PI*r,ratio=value==null?0:Math.min(value/state.goals[key],1);
    return `<circle class="ring-track" cx="150" cy="150" r="${r}" stroke="${colors[key]}"/><circle class="ring-progress" data-length="${ratio*c}" data-circumference="${c}" cx="150" cy="150" r="${r}" stroke="${colors[key]}" stroke-dasharray="${state.paused?ratio*c:0} ${c}"/>`;
  }).join('');
  $('activity-rings').setAttribute('aria-label',metrics.map(([k,v])=>`${names[k]} ${v==null?'暂无记录':fmt(v,k==='distance'?2:0)}，目标 ${fmt(state.goals[k])}`).join('；'));
  requestAnimationFrame(()=>requestAnimationFrame(()=>document.querySelectorAll('.ring-progress').forEach(el=>el.setAttribute('stroke-dasharray',`${el.dataset.length} ${el.dataset.circumference}`))));
}
function renderVitals(){
  for(const [metric,prefix] of [['heartRate','heart'],['hrv','hrv']]){
    const point=latest(metric);animateNumber(`${prefix}-value`,point?.value);set(`${prefix}-time`,point?time(point.time):'当天暂无记录');
    chartLine(`${prefix}-chart`,rows(metric),colors[metric]);
  }
  set('hrv-count',`${rows('hrv').length} 次测量 · RMSSD`);
  const heart=rows('heartRate');set('heart-range',heart.length?`${fmt(Math.min(...heart.map(r=>r.value)))}–${fmt(Math.max(...heart.map(r=>r.value)))} 次/分钟`:'');
  const stress=rows('stress'),last=stress.at(-1);animateNumber('stress-value',last?.value);set('stress-time',last?time(last.time):'当天暂无记录');set('stress-count',`${stress.length} 次记录`);
  if(!stress.length){$('stress-chart').innerHTML=empty('暂无压力记录');return;}
  const max=Math.max(...stress.map(r=>r.value),1),bar=300/stress.length;
  $('stress-chart').innerHTML=`<svg viewBox="0 0 300 43" preserveAspectRatio="none" role="img" aria-label="压力测量原始分数，不解释等级">${stress.map((r,i)=>`<rect class="bar-shape" x="${i*bar}" y="${40-r.value/max*34}" width="${Math.max(1,bar*.6)}" height="${r.value/max*34}" rx="2" fill="${colors.stress}" opacity="${i===stress.length-1?1:.4}" style="animation-delay:${i*12}ms"><title>${esc(time(r.time))} · ${fmt(r.value)}</title></rect>`).join('')}</svg>`;
}
function renderTrend(){
  const dates=Array.from({length:state.period},(_,i)=>dateShift(state.date,i-state.period+1)).filter(d=>d>=data.rangeStart.slice(0,10));
  const values=dates.map(d=>scaled(state.metric,daily(state.metric,d))),valid=values.filter(v=>v!=null),total=valid.reduce((a,b)=>a+b,0),average=valid.length?total/valid.length:null,decimals=state.metric==='distance'?2:0;
  const unit={steps:'步',calorie:'kcal',distance:'km'}[state.metric];
  animateNumber('trend-average',average,decimals);set('trend-unit',`${unit} / 有记录日均`);set('trend-total',`${valid.length} 天记录 · 合计 ${valid.length?fmt(total,decimals):'—'} ${unit}`);set('trend-subtitle',`${dayLabel(dates[0]||state.date)} — ${dayLabel(state.date)}`);
  if(!valid.length){$('trend-chart').innerHTML=empty('这个时间范围内暂无此指标，缺失值不会补为零');return;}
  const w=Math.max(280,$('trend-chart').clientWidth),h=Math.max(70,$('trend-chart').clientHeight),left=42,bottom=25,top=14,usable=h-bottom-top,max=Math.max(...valid,1)*1.2,step=(w-left)/dates.length,barWidth=Math.min(38,step*.48),tick=max/2;
  let svg=[0,1,2].map(i=>`<line x1="${left}" y1="${top+i*usable/2}" x2="${w}" y2="${top+i*usable/2}" class="chart-grid"/><text x="${left-8}" y="${top+i*usable/2+3}" text-anchor="end" class="chart-label">${fmt((2-i)*tick,state.metric==='distance'?1:0)}</text>`).join('');
  svg+=dates.map((date,i)=>{const value=values[i],height=value==null?0:value/max*usable,x=left+step*(i+.5),label=state.period===7 || i%5===0 || i===dates.length-1;
    return `<g class="bar-group" role="button" tabindex="0" data-date="${date}" aria-label="查看 ${date}，${names[state.metric]} ${value==null?'无记录':`${fmt(value,decimals)} ${unit}`}" aria-pressed="${date===state.date}"><rect class="hit-area" x="${x-step*.45}" y="0" width="${step*.9}" height="${h}" fill="transparent" rx="4"/>${value==null?`<text x="${x}" y="${h-bottom-5}" text-anchor="middle" class="chart-label">—</text>`:`<rect class="bar-shape" x="${x-barWidth/2}" y="${h-bottom-height}" width="${barWidth}" height="${Math.max(1,height)}" rx="4" fill="${date===state.date?colors[state.metric]:'#48484f'}" style="animation-delay:${i*14}ms"/><text class="bar-value" x="${x}" y="${h-bottom-height-6}" text-anchor="middle">${fmt(value,decimals)}</text>`}${label?`<text x="${x}" y="${h-5}" text-anchor="middle" class="chart-label" style="fill:${date===state.date?'#f5f5f7':'#83838c'}">${Number(date.slice(5,7))}/${Number(date.slice(8,10))}</text>`:''}<title>${date} · ${value==null?'暂无记录':`${fmt(value,decimals)} ${unit}`}</title></g>`;
  }).join('');
  $('trend-chart').innerHTML=`<svg viewBox="0 0 ${w} ${h}" preserveAspectRatio="none" aria-label="${names[state.metric]}日趋势">${svg}</svg>`;
  $('trend-chart').querySelectorAll('[data-date]').forEach(el=>{const choose=()=>selectDate(el.dataset.date);el.addEventListener('click',choose);el.addEventListener('keydown',event=>{if(['Enter',' '].includes(event.key)){event.preventDefault();choose();$('selected-date').focus();}});});
}
function renderSleep(){
  const segments=rows('sleep'),totals={deep:0,light:0,rem:0,awake:0},stageNames={deep:'深睡',light:'浅睡',rem:'快速眼动',awake:'清醒'};
  for(const s of segments)totals[s.stage]+=(new Date(s.end)-new Date(s.start))/60000;
  const duration=totals.deep+totals.light+totals.rem;
  $('sleep-duration').innerHTML=segments.length?`${Math.floor(duration/60)}<small>小时</small>${fmt(duration%60)}<small>分钟</small>`:'—<small>暂无可解码时长</small>';
  animateNumber('sleep-score',daily('sleepScore'));
  set('sleep-night',dayLabel(state.date));
  $('sleep-legend').innerHTML=Object.keys(stageNames).map(stage=>`<div><i class="mini-dot" style="background:${colors[stage]}"></i>${stageNames[stage]}<b>${segments.length?`${fmt(totals[stage])} 分钟`:'—'}</b></div>`).join('');
  if(!segments.length){$('sleep-chart').innerHTML=empty('睡眠区间未返回或详情尚未解码');set('sleep-window','');return;}
  const left=Math.min(...segments.map(s=>new Date(s.start).getTime())),right=Math.max(...segments.map(s=>new Date(s.end).getTime())),width=480,heights={awake:10,rem:25,light:39,deep:56};
  const bars=segments.map(s=>{const x=(new Date(s.start)-left)/(right-left)*width,w=(new Date(s.end)-new Date(s.start))/(right-left)*width;return `<rect class="sleep-stage" x="${x}" y="${58-heights[s.stage]}" width="${Math.max(.5,w-1)}" height="${heights[s.stage]}" rx="2" fill="${colors[s.stage]}"><title>${stageNames[s.stage]} · ${time(s.start)}–${time(s.end)}</title></rect>`;}).join('');
  $('sleep-chart').innerHTML=`<svg viewBox="0 0 ${width} 65" preserveAspectRatio="none" role="img" aria-label="睡眠分期，实际睡眠 ${fmt(duration)} 分钟">${bars}</svg>`;
  const ordered=segments.slice().sort((a,b)=>a.start.localeCompare(b.start));set('sleep-window',`${time(ordered[0].start)} 入睡区间起点 — ${time(ordered.at(-1).end)} 结束`);
}
function renderObservation(){
  const dates=Array.from({length:state.period},(_,i)=>dateShift(state.date,i-state.period+1)).filter(d=>d>=data.rangeStart.slice(0,10)),valid=dates.map(d=>daily('steps',d)).filter(v=>v!=null);
  set('record-days',valid.length);
  if(!valid.length){set('observation','当前范围暂无步数记录。可切换日期、来源，或重新导出需要的日期范围。');return;}
  const reached=valid.filter(v=>v>=state.goals.steps).length;
  set('observation',`这 ${dates.length} 天，累计记录 ${fmt(valid.reduce((a,b)=>a+b,0))} 步，其中 ${reached} 天达到你设置的步数目标。`);
}
function render(){
  $('selected-date').value=state.date;
  $('previous-date').disabled=state.date<=data.rangeStart.slice(0,10);
  $('next-date').disabled=state.date>=data.rangeEnd.slice(0,10);
  set('range-description',`${state.date.replaceAll('-',' / ')}   ·   ${['周日','周一','周二','周三','周四','周五','周六'][new Date(`${state.date}T12:00:00+08:00`).getUTCDay()]}   ·   Asia/Shanghai`);
  renderActivity();renderVitals();renderTrend();renderSleep();renderObservation();
  if(state.view==='metrics')renderAllMetrics();
  if(state.view==='archive')renderRecords();
}
function selectDate(date){if(date<data.rangeStart.slice(0,10)||date>data.rangeEnd.slice(0,10)){toast('请选择导出范围内的日期');$('selected-date').value=state.date;return;}state.date=date;render();}
function sourceDialog(){
  const coverage=data.coverage,counts=coverage.endpointCounts || {},labels={data:'有数据',empty:'成功但为空',failed:'失败',not_called:'未调用',metadata:'元数据'},status=data.mode==='demo'?'模拟演示':coverage.status==='complete'?'导出范围完成':'部分覆盖';
  let html=`<div class="coverage-row"><span class="coverage-chip">${status}</span>${Object.entries(counts).map(([k,v])=>`<span class="coverage-chip">${esc(labels[k]||k)} ${esc(v)}</span>`).join('')}<span class="coverage-chip">未完成数据集 ${esc(coverage.incompleteDatasets??0)}</span></div>`;
  for(const metric of [...Object.keys(data.metrics),'sleep']){
    const sources=[...new Set(allRows(metric).map(r=>r.source))];if(!sources.length)continue;
    const selected=state.sources[metric];
    html+=`<div class="source-row"><label for="source-${metric}">${names[metric]}</label><select id="source-${metric}" data-source-metric="${metric}">${sources.map(key=>`<option value="${esc(key)}" ${key===selected?'selected':''}>${esc(data.sources[key]?.label||key)} · ${allRows(metric).filter(r=>r.source===key).length} 条</option>`).join('')}</select><span class="source-detail">${esc(data.sources[selected]?.detail||'')}<br>${esc([...new Set(allRows(metric).filter(r=>r.source===selected).map(r=>r.endpoint).filter(Boolean))].join('，'))}</span></div>`;
  }
  html+=`<ul class="notes-list">${[...(data.mode==='demo'?['当前所有数值为确定性生成的模拟数据，没有连接华为账号。']:[]),`导出日期：${data.rangeStart.slice(0,10)} 至 ${data.rangeEnd.slice(0,10)}；Asia/Shanghai。`,'同一指标仅展示所选来源；日汇总重复时取最后一条，不相加。','默认来源为该指标记录最多的来源，可在此切换。',...(coverage.limitations||[]),...data.notes].map(n=>`<li>${esc(n)}</li>`).join('')}</ul>`;
  $('source-content').innerHTML=html;
  $('source-content').querySelectorAll('[data-source-metric]').forEach(select=>select.addEventListener('change',()=>{state.sources[select.dataset.sourceMetric]=select.value;render();sourceDialog();$('source-'+select.dataset.sourceMetric).focus();}));
  if(!$('source-dialog').open)$('source-dialog').showModal();
}
function motion(){
  document.body.classList.toggle('paused',state.paused);$('motion-button').setAttribute('aria-pressed',String(state.paused));$('motion-button').innerHTML=`<svg><use href="#i-${state.paused?'play':'pause'}"/></svg><span>${state.paused?'开启动效':'暂停动效'}</span>`;
  if(state.paused)for(const el of document.querySelectorAll('[data-value]')){cancelAnimationFrame(el._frame);if(el.dataset.value!=='')el.textContent=fmt(Number(el.dataset.value),el.id==='distance-value'||(el.id==='trend-average'&&state.metric==='distance')?2:0);}
}
$('selected-date').min=data.rangeStart.slice(0,10);$('selected-date').max=data.rangeEnd.slice(0,10);
$('selected-date').addEventListener('change',event=>{if(event.target.value)selectDate(event.target.value);});
$('previous-date').addEventListener('click',()=>selectDate(dateShift(state.date,-1)));
$('next-date').addEventListener('click',()=>selectDate(dateShift(state.date,1)));
document.querySelectorAll('[data-period]').forEach(button=>button.addEventListener('click',()=>{state.period=Number(button.dataset.period);document.querySelectorAll('[data-period]').forEach(b=>{b.classList.toggle('selected',b===button);b.setAttribute('aria-pressed',String(b===button));});renderTrend();renderObservation();if(state.view==='metrics'){$('metrics-scope').value='window';renderAllMetrics();}if(state.view==='archive'&&archiveState.collection!=='full'){$('record-date-scope').value='window';archiveState.page=0;renderRecords();}}));
document.querySelectorAll('[data-metric]').forEach(button=>button.addEventListener('click',()=>{state.metric=button.dataset.metric;document.querySelectorAll('[data-metric]').forEach(b=>{b.classList.toggle('selected',b===button);b.setAttribute('aria-pressed',String(b===button));});renderTrend();}));
for(const id of ['source-button','rail-source'])$(id).addEventListener('click',sourceDialog);
document.querySelectorAll('[data-close]').forEach(button=>button.addEventListener('click',()=>button.closest('dialog').close()));
document.querySelectorAll('dialog').forEach(dialog=>dialog.addEventListener('click',event=>{const r=dialog.getBoundingClientRect();if(event.target===dialog&&(event.clientX<r.left||event.clientX>r.right||event.clientY<r.top||event.clientY>r.bottom))dialog.close();}));
$('goal-button').addEventListener('click',()=>$('goals-dialog').showModal());
$('goals-form').addEventListener('submit',event=>{event.preventDefault();const values=new FormData(event.target);for(const key of ['steps','calorie','distance'])state.goals[key]=Number(values.get(key));$('goals-dialog').close();renderActivity();renderObservation();toast('每日目标已更新');});
$('motion-button').addEventListener('click',()=>{state.paused=!state.paused;motion();});
matchMedia('(prefers-reduced-motion: reduce)').addEventListener('change',event=>{state.paused=event.matches;motion();});
$('fullscreen').addEventListener('click',async()=>{try{if(document.fullscreenElement)await document.exitFullscreen();else await document.documentElement.requestFullscreen();}catch{toast('浏览器未允许全屏，可使用窗口的全屏按钮');}});
document.addEventListener('fullscreenchange',()=>{$('fullscreen').setAttribute('aria-label',document.fullscreenElement?'退出全屏':'进入全屏');});
document.querySelectorAll('.rail-nav a').forEach(link=>link.addEventListener('click',()=>{document.querySelectorAll('.rail-nav a').forEach(a=>a.classList.toggle('active',a===link));}));
let resizeTimer;
window.addEventListener('resize',()=>{clearTimeout(resizeTimer);resizeTimer=setTimeout(renderTrend,150);});
set('data-badge',data.mode==='demo'?'模拟数据演示':'本地导出数据');set('footer-mode',data.mode==='demo'?'全部为模拟数据，无账号连接':'本人数据导出 · 无外部请求');set('coverage-summary',data.mode==='demo'?'30 天模拟快照':data.coverage.status==='complete'?'导出范围完成':'部分覆盖 · 详情见数据与来源');
motion();render();
