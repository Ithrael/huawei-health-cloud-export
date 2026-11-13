---
name: huawei-health-cloud-export
description: 使用本人的用户名、密码和本人提供的短信验证码登录华为账号，读取华为运动健康中国区云端数据，按接口导出 JSON、字段 mapping 和 ZIP。支持睡眠、活动统计、HRV、心率、压力及已确认的运动能力接口；依据 Android 17.0.10.300 的逆向与实测，不替代用户指定的手机 App 页面采集。
---

# 华为运动健康云端导出

此技能自包含于项目目录。接口逆向来源是 **com.huawei.health 17.0.10.300，versionCode 1700010300**；2026-11-13 完成静态分析及本人账号只读验证。详见 [APK 来源及哈希](references/apk-provenance.json)。不是华为官方 API SDK，不把 127 个字典类型说成 127 个接口。

## 输入参数

两种调用均支持，参数由助手在内存解析，**不是 shell 命令**：

```text
$huawei-health-cloud-export <用户名> <密码>
$huawei-health-cloud-export --username "<用户名>" --password "<密码>" --days 30
```

- `username`：本次华为账号的手机号、邮箱或账号名；传给浏览器 helper 时映射为 `account`。
- `password`：本次密码，保留大小写和特殊字符，不经过 shell；也可以让本人直接在浏览器填写。
- `days`：可选，默认最近 30 个自然日，Asia/Shanghai；说明默认范围。
- `types`：可选，逗号分隔数据类型，如 `500044,7,9,700013`，限制按版本导出的类型；常规统计接口仍会读取。
- 用户只给用户名时询问缺少的密码或让其在官方页输入；只给验证码时续接当前等待中的会话，不重新登录。
- 用户提交新账号或新密码时必须隔离新登录；未提供凭据、且已确认是同一账号并明确沿用时，才复用旧缓存。

## 登录及验证码

1. 先确定本次工作区及绝对路径，选用新的 `.secrets/huawei-health/session-<时间>.json` 和 `exports/<时间>/`。不要读取技能项目里的作者缓存或把缓存写进技能目录。
2. 发现实际可用的浏览器工具；读取 [网页登录流程](references/browser-login.md) 和 `scripts/browser_step.js`，在新的隔离标签页执行 `start`。账号密码只可交给官方页面，默认验证过的登录 origin 为 `https://id1.cloud.huawei.com`。
3. 将用户本次传入的 `username/password` 放入浏览器调用内存，执行 `credentials`；最多提交一次密码，不输出输入值、认证响应、请求头或含授权码的 URL。
4. 页面要求身份验证时，选择与账号相符的掩码手机号并请求一次短信验证码。有多个号码无法确定时先让用户选。
5. 明确告知已发送到哪个掩码号码，并等待本人提供本次验证码。用异步输入工具或结束本轮等待；验证码到来前只做不依赖它的准备。本人提交后执行 `verify`；不猜码、不代收短信、不自动重发。遇到图形验证码/扫码/风控，等待本人处理。
6. “是否信任此浏览器”默认选“不信任”。验证授权 state 后执行一次 `exchange`，再用 `session` 私密取得令牌。密码错误或验证码过期时等待用户更正，不循环重试。
7. 按 reference 通过隐藏 stdin 调用 `scripts/health.py import-session`，令牌不进 shell 参数；成功后清空 functions 中的密码、验证码和会话变量，关闭本次隔离标签页。

用户名和密码已提供即授权本次正常登录；不要重复索取登录批准。**生成或测试此 Skill 本身不表示要登录或发短信。** 没有浏览器、认证被阻止或导入失败时说明具体阶段，不能声称已经登录。

## 导出

`SKILL_DIR` 是本文件所在目录，`WORK_DIR` 是用户本次数据工作区。实际执行时展开为绝对路径并正确引用路径；不得把用户名、密码、验证码或令牌拼入命令：

```sh
python3 "$SKILL_DIR/scripts/health.py" export \
  --session "$WORK_DIR/.secrets/huawei-health/session-本次时间.json" \
  --output "$WORK_DIR/exports/本次时间" --days 30
```

若指定类型，另加 `--types 500044,7,9,700013`。默认尝试所有已确认可用的健康明细/序列/统计类型，以及活动、睡眠、运动能力、报告等读取接口。无需重新下载 APK。具体参数、限制和状态见 [接口说明](references/interfaces.md)，字段查 [mapping.json](references/mapping.json) 或 [类型字典](references/type-mapping.json)。

必须保留的协议细节：

- `getHealthData` / `getSportsDataByTime` 按不超过 7 天分段。30 天直接请求实测返回 1001；7 天心率、HRV 成功，不能把前者当成账号无数据。
- 按版本读取到快照目标 `currentVersion` 才算该类型完成；缺目标、游标停滞或达到页数上限必须标明未完成。同步版本是修改游标，最终还需按记录发生时间过滤。
- HRV 主要读 `getHealthDataByVersion(type=500044)`，RMSSD 单位 ms；另有 7 天按时间读取。HRV 套用 `getHealthDataLatest` 实测失败，不重试同一无效参数。
- VO2max 点类型 `400028`、统计类型 `502004`；乳酸阈值 latest 使用 `type=300012,dataType=0`，不能强制改成 2。空结果只说明该次读取为空。
- 运动复合类型走 `cloudType=30001/34001`，再按 `subType` 筛选；本地 302xx/342xx 不能直接当云端 type。33001 合并轨迹映射、Bearer 周期报告及 ArkUI-X 候选仍未接入。
- 只调用固定读取白名单及正常令牌续期。FTP refresh、lifetimeStatistics calculate 会触发刷新/计算且需要其他鉴权，不属于本次只读计划。
- 遇到 HTTP 401/403/429 停止后续数据请求，保存已取得部分；不要通过换 tokenType、伪造签名或重复登录绕过。仅在确需本人正常重新认证时告知用户。

## 交付与保密

读取 `manifest.json`，分别报告有数据、成功但为空、失败、未调用及分页未完成。脚本退出 **2** 表示已交付部分范围，通常是已知覆盖限制；不要因此重复登录或把整个导出说成失败。

- `interfaces/*.json`：每条接口一个文件，`calls[]` 含不带鉴权的业务参数、状态和原始 JSON 响应。
- `datasets/*.json`：按类型清洗的数据、来源、时间范围和分页覆盖；JSON 字符串递归解析，未知编码与枚举保留。
- `activity/*.json`：步数、距离、爬楼、活动热量分别展示，不把不同来源相加。
- `mapping.json`、`type-mapping.json`、`provenance.json`：字段意义、类型、单位及 APK 证据。
- `huawei-health-data.zip`：上述数据打包；会话缓存不在包内。给出可点击的文件路径。

会话/输出目录 `0700`，文件 `0600`。密码与验证码仅在浏览器交互内存中使用，不写日志、截图、源码、命令行或文件。令牌/UID 仅作必要私密缓存。Git 中排除 `.secrets/`、`.local/`、`exports/`；发布前检查实际待提交文件。不要把未经验证的字段解释成医学结论或承诺整个 App 的数据均可获取。
