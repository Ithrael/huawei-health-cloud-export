# 接口来源、状态与参数

源 APK：17.0.10.300，versionCode 1700010300，包名 com.huawei.health。来源与 SHA-256 见 [apk-provenance.json](apk-provenance.json)。2026-11-13 从 48 个 DEX、dict_config.txt、原生调用链以及 ArkUI-X 资源整理；[api-catalog.json](api-catalog.json) 指向本项目的反编译证据文件和行号，39 个主要 Java 证据文件及补充跳绳报告调用类一同保存。

网页登录、授权码交换和缓存刷新基于此前 15.1.6.310 建立并在 2026-11-13 成功使用的正常登录流程；数据接口清单、请求模型和类型映射已更新至 17.0.10.300。新建技能的离线测试不等于又完成一次真实登录。

数据域名 `https://healthdata.dbankcloud.cn`；正常会话域名 `https://healthsession.things.dbankcloud.cn`。当前读取计划使用本人 AT、tokenType=2、公共参数 appId=com.huawei.health、siteId=1 和 x-huid/x-version/x-ts；账号地区需正常 query 确认为 CN。别把此会话当作其他 Factory 的 Bearer 或 serviceToken。

## 20 条至少一组参数成功的接口

以下“空”只代表测试账号在本次参数下没有返回记录。其他账号和设备可能不同；同一路由的所有 type 也不保证通用。

| 路由 | 验证及读取方式 |
| --- | --- |
| `/dataQuery/common/getSyncVersions` | 同步目标；syncKeys 每批不超过25，type/dataType=2 |
| `/dataQuery/health/getHealthDataByVersion` | 点数据，type/dataType=2/deviceCode=0/version；睡眠、心率、压力、HRV等有记录 |
| `/dataQuery/sequence/getSampleSequenceByVersion` | 序列，另加subTypes=[]；睡眠详情、RRI等有记录 |
| `/dataQuery/health/getHealthStatisticsByVersion` | 分类2统计，type/dataSource=2/dataType=2/version；VO2max 502004等为空 |
| `/dataQuery/sport/getSportsDataByVersion` | 活动片段，按version翻页；同步主类型1 |
| `/dataQuery/path/getMotionPathByVersion` | 训练轨迹，当前为空，目标版本缺失，覆盖未知 |
| `/dataQuery/sport/v2/getSportsStat` | 日活动，startTime/endTime=YYYYMMdd，dataSource=2/deviceCode=0 |
| `/dataQuery/sport/getSportsDimenStat` | 运动维度，startTime/endTime=YYYYMMdd，dataSource=2 |
| `/dataQuery/health/getHealthStat` | 心率/睡眠/压力汇总，YYYYMMdd，types=[3,7,9,11,16] |
| `/dataQuery/sport/getSleepStat` | 睡眠汇总，同日统计参数；当前为空 |
| `/dataQuery/health/getHealthData` | queryType=2，毫秒startTime/endTime，每段≤7天；type=7/500044成功 |
| `/dataQuery/sport/getSportsDataByTime` | queryType=2，毫秒startTime/endTime，每段≤7天，sportTypes=[6,7,8]；空 |
| `/dataQuery/sport/getSleepSportsDataLatest` | timestamp/days/dataType=2；已测为空，仅最新范围不是全历史 |
| `/dataQuery/health/getHealthDataLatest` | 睡眠type=9/dataType=2/counts=1成功；HRV 500044失败 |
| `/dataQuery/health/getHealthDataLatest` | 同一路由的乳酸阈值type=300012/dataType=0/counts=1；当前为空 |
| `/dataQuery/health/getHealthDataByRecordDay` | recordDay=YYYYMMdd/type=800001/dataType=2；最新页为空，不把此接口当生理周期 |
| `/dataAnalyse/app/getRunLevel` | startDay/endDay=YYYYMMdd/queryType=0/types=[1..6]/timeZone=+0800；当前为空 |
| `/dataQuery/health/getHealthStatistics` | 乳酸统计，startTime/endTime=YYYYMMdd、types=[300012]；为空 |
| `/dataQuery/report/getSampleReportByDay` | 训练负荷，startDay/endDay=YYYYMMdd/type=300010/dataType=2/deviceCode=0；空 |
| `/dataQuery/report/getPersonalReport` | accumulatedItems=[accumPerfectGoalAchievedDays]；有累计统计对象 |
| `/dataQuery/report/getCycleReport` | 跳绳当月最佳，bestItems/queryType=1/毫秒startTime/endTime/timeZone；空，不是女性生理周期 |

表中 getHealthDataLatest 的两种请求共用一条物理路由。按时间查询 30 天报1001，7天成功，不在未知最大区间上继续试探。新版 helper 固定按≤7天分段；不据此声称已经验证精确最大跨度。

## 类型与未接入项

- 新版字典127类型，旧版87类型；40个新增。分类0→点数据，1→序列，2→统计，6→Bearer周期报告，3→实时，不能全部套用一个接口。
- VO2max：400028点、502004统计；ArkUI-X HealthKit本地聚合ID30001不是云端VO2max类型。
- 运动302xx/342xx是本地复合ID，映射主类型30001/34001及subTypes。合并记录33259/33260对应33001线索，读取链未验证。
- 500029、300003、300004、700018已测当前请求返回1001，默认标明未调用原因，不重复同一请求，也不写成空数据。
- `getSampleSequenceLatest`：睡眠详情已测参数返回1001；默认用按版本接口。
- `getMotionPathData`、`getSampleSequenceByTime`：发现模型，未确认实际业务调用与可用参数，默认未调用。
- GET `/dataService/v1/healthDatas/sampleReports/history` 使用App Bearer鉴权，本项目当前会话不是已经确认可用的对应令牌。
- `/achievement/v1/ftp:refresh`、`/dataStat/v1/basicCalculations/lifetimeStatistics:calculate` 涉及刷新/计算，另需App/serviceToken鉴权，只读导出不调用。
- ArkUI-X另11条候选在 [ark-data-service-candidates.json](ark-data-service-candidates.json)。路径常量不等于可用读取接口；部分是写入/删除操作，不纳入自动导出。

## 字段与覆盖

[mapping.json](mapping.json) 包含常见字段和新版类型字段；[type-mapping.json](type-mapping.json) 提供每种类型的字段、格式、单位和验证规则。部分单位/枚举无法从代码确认时保留原值和未知说明。活动 calorie 为cal，换算kcal需除1000；altitude 的累计爬升米数与 floor 层数分开。

同步version反映修改顺序；请求从起点前一天开始以涵盖跨夜记录，再按事件区间过滤。分页只有达到 getSyncVersions 快照目标才算完成，空响应/HTTP200本身不足以判定完整。原始接口JSON保留deleteInfos，清洗数据不将删除标记当作测量。不同设备与不同接口可能重复，不自动相加，不推断手机App里是否还有未同步的数据。
