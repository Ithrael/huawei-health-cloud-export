# 华为运动健康云端导出 Skill

简体中文 | [English](README.en.md)

独立的 Codex Skill 与 Python 只读导出项目。支持本人用户名、密码、本人短信验证码登录，导出活动、睡眠、HRV、心率、压力、情绪等健康数据，并覆盖已经确认参数的运动能力读取接口。

接口来源：**华为运动健康 Android `com.huawei.health` 17.0.10.300 / versionCode 1700010300**，2026-11-13 分析和实测。APK SHA-256：`27e6c6dbaa96e09e1314a1b40d00db4e87e73e8c8c8cbb54bcd495cf386c3119`。下载地址、证书指纹、核验方式见 [provenance](references/apk-provenance.json)，代码证据见 [source-evidence](references/source-evidence)。本项目不附 APK、个人健康数据或登录凭据。

当前整理了 **26 条原生相关接口、127 个新版字典类型**，另附 ArkUI-X 的 11 条候选。实测 21 条原生接口，20 条至少一组参数成功：9 条有业务数据、10 条成功但为空、1 条同步辅助接口。`getSampleSequenceLatest` 的已测参数失败。其余接口保持未验证状态，不自动猜测鉴权或发起计算。

## 使用 Skill（★推荐）

需要 Python 3.10+ 和能保持 Playwright `page` 的浏览器工具；Python 运行无第三方依赖。根目录的 [SKILL.md](SKILL.md) 是完整技能入口。

将本项目目录链接或复制到 `~/.codex/skills/huawei-health-cloud-export` 后调用；已有同名技能时先保留旧版，勿直接覆盖不明来源的内容。读取当前项目也可以直接让助手使用本项目的 `SKILL.md`。

```text
$huawei-health-cloud-export --username "你的华为账号" --password "你的密码"
$huawei-health-cloud-export "你的华为账号" "你的密码" --days 30
```

这些是 **助手的 Skill 参数**，不是终端命令。密码只在浏览器交互内存中使用；也可让本人在官方页填写密码。助手发送一次验证码后等待本人提交，不会自动获取手机短信。

导出默认最近 30 个自然日、UTC+8。新凭据使用新的隔离登录；已确认同一账号且用户要求沿用时可复用缓存。访问令牌会在将过期时正常刷新；刷新令牌的长期有效期未确认。

## Python 导出

网页登录和短信流程由 Skill 驱动浏览器；Python CLI 不接收密码参数。按 [登录说明](references/browser-login.md) 将令牌通过隐藏 stdin 导入新的私密缓存后：

```sh
python3 scripts/health.py export \
  --session .secrets/huawei-health/session.json \
  --output exports/latest --days 30

# 仅限制按版本读取的类型；统计等普通接口仍会查询
python3 scripts/health.py export \
  --session .secrets/huawei-health/session.json \
  --output exports/selected --types 500044,7,9,700013

python3 scripts/health.py status --session .secrets/huawei-health/session.json
python3 -m unittest discover -s tests
```

一次导出可能需要数分钟，取决于类型、数据量和分页。输出目录须为本次新目录，不能混合不同账号。网络导出会按正常接口读取本人数据；生成 Skill 和离线测试不登录、不发短信。

## 会话续期观察

用户明确要求持续维持本次会话时，可运行到期前续期观察器：

```sh
python3 scripts/keepalive.py --session /本次工作区/.secrets/huawei-health/session.json \
  --state /本次工作区/.local/keepalive/status.json --hours 24
```

默认在访问令牌到期前120秒调用正常 refresh，再校验账号上下文；不读取健康数据、不登录、不发短信。同一会话只允许一个观察器。`--hours 0` 表示持续运行到手动停止，SIGTERM/Ctrl+C 可停止。状态文件和标准输出仅包含时间、计数和轮换布尔值，不包含令牌或账号。任何刷新/校验失败即停止，不自动重登。

此过程续期的是健康接口访问令牌，不能续期官方网页登录 Cookie。刷新令牌的长期有效期、撤销策略及能否永久续期尚未确认；短期成功不能证明永久有效。观察器运行依赖本机进程、联网与唤醒状态，默认不安装开机任务。

## 输出

| 路径 | 内容 |
| --- | --- |
| `interfaces/` | 每条接口一个 JSON，含业务参数、状态、原始响应 |
| `datasets/` | 按数据类型清洗，标明时间、来源、分页覆盖 |
| `activity/` | 步数、距离、爬楼、活动热量分别输出 |
| `mapping.json` | 字段名称、含义、单位、已验证枚举与未知项 |
| `type-mapping.json` | 新字典 127 类型与旧核心类型、云端映射 |
| `provenance.json` | APK 版本、哈希、来源和核验方式 |
| `manifest.json` | 各接口结果、未调用原因和覆盖限制 |
| `huawei-health-data.zip` | 以上文件打包，不含会话凭据 |

`recordCount` 是响应/数据集的记录数，不等于测量天数；日统计不同来源不相加。原始同步记录可能修改自更早日期，清洗后按事件时间过滤；无法确认事件时间的放入 `undatedRecords`。`detailData` 二进制保留，HRV RMSSD 与原始 RR 间期分别处理。

完整 App 覆盖仍未验证，所以默认 `manifest.status=partial`，CLI 退出码 **2**，已成功结果照常交付。退出码 1 才表示会话、输入或执行错误。不要因为 partial 再次登录、重发验证码或将空数据补成零。

## 离线健康大屏

★推荐：先用模拟数据体验，不需要账号和第三方依赖：

```sh
python3 scripts/dashboard.py --demo --output exports/demo/dashboard.html
open exports/demo/dashboard.html

# 将已有导出转成大屏，不发起网络请求
python3 scripts/dashboard.py --input exports/latest --output exports/latest/dashboard.html
```

采用华为手表界面风格的 AMOLED 黑底、圆角卡片、彩色数据图和大字号数字，包含自定义活动三环、心率、HRV、活动趋势、睡眠分期和压力记录。支持日期、近7/30天、趋势指标、按指标选择数据来源、每日目标、前后日期切换、卡片详情定位、全屏和暂停动效；适配手机并遵循系统减弱动态偏好。单个 HTML 内嵌数据、样式和脚本，可离线打开。也可以直接对 Skill 说「把这个导出目录生成健康大屏」或「用模拟数据展示大屏」。

桌面顶部、手机底部胶囊导航可切换「健康总览」「全部指标」「数据档案」。全部指标补充静息心率、情绪、海拔、爬楼、活动小时、基础代谢、运动片段、睡眠扩展字段和成就等主题，每个主题可切换字段与来源。数据档案包含本次导出的全部数据集、接口结果、活动统计和字段/覆盖说明，支持搜索、状态及日期筛选、分页、全部未知字段展开和完整 JSON 展示副本下载；无日期记录、删除标记与未解码序列也有入口。

档案逐文件 gzip 内嵌并在浏览器按需解压，需要支持 `DecompressionStream` 的浏览器。图表的密集采样会明确标出抽样点数，完整记录不截断。展示副本隐藏认证字段、大整数转成字符串保留精度，磁盘上的原始导出不修改。未知单位、枚举和二进制不推测成健康测量。

模拟页面始终标明虚构数据；真实页面保留覆盖限制，不把缺失补为0，不合并不同设备。仅渲染已识别字段：尚未解码的睡眠详情和单位未确认的汇总不推测时长。目标只用于当前页面，刷新后恢复默认值。生成的 HTML 含健康数据，与 JSON 一样保存在忽略提交的 `exports/`；不会自动上传，也不自动加入原有数据 ZIP。

接口参数差异、失败类型、不同鉴权和代码位置见 [接口说明](references/interfaces.md)、[接口清单](references/api-catalog.json)、[类型映射](references/type-mapping.json)。中国区以外尚未验证。更新版本时应同步来源和验证记录，而非只修改请求头。

页面三环为步数、活动热量、距离的自定义目标；华为官方活力三环为活动热量、锻炼时长、活动小时数，当前未将未确认的字段强行映射为官方三环。
