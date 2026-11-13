# 华为运动健康云端导出 Skill

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

接口参数差异、失败类型、不同鉴权和代码位置见 [接口说明](references/interfaces.md)、[接口清单](references/api-catalog.json)、[类型映射](references/type-mapping.json)。中国区以外尚未验证。更新版本时应同步来源和验证记录，而非只修改请求头。
