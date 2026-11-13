优先中文。先读 README.md 和 SKILL.md。

- APK 来源为 references/apk-provenance.json，当前版本 17.0.10.300；新增接口须注明源码和实测状态。
- 正常账号登录、本人验证码、只读本人健康数据；创建/测试 Skill 不自动登录或发短信。
- 用户名/密码是 Skill 参数，由浏览器 helper 内存消费。禁止把真实密码或验证码放入 shell 参数、代码、测试、日志或 Git。
- .secrets/ 会话、.local/ 和 exports/ 数据不得提交；不复制开发者的账号或令牌。
- 运行 python3 -m unittest discover -s tests；离线测试不得发网络请求。新增网络测试须使用本次授权的本人账号。
- 7 天按时间分段；按版本读取必须检查分页目标，未完成标记 partial。保持未知字段和来源，不猜测健康结论。
