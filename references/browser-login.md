# 浏览器登录与私密传输

本流程依赖可保持 Playwright `page` 对象的浏览器工具。先发现实际工具名；下面用本环境的 `mcp__playwright__browser_run_code_unsafe` 举例。不要假设 `globalThis` 能跨调用保存状态，helper 将状态保存在当前 `page` 上。

## 加载与调用 helper

用正常文件读取工具读取技能目录下的 `scripts/browser_step.js`，将源码存到 `functions` 的会话内存，例如 `store("hw.helper", result.output)`；不需要把源码复制到工作区。创建一个新的 `isolatedContext` 和 `about:blank` 标签页，并保持后续操作都在该页。

每一步在内存设置 `store("hw.input", {...})`。`start` 不含账号密码；`credentials` 的 `account/password` 来自用户本次输入；`verify` 的 `code` 只能来自用户刚提交的短信验证码。不要把这些值写进文件或 shell 命令。

以下包装器把输入放入 `page` 后执行 helper。浏览器工具可能附带“执行过的代码”和完整页面 URL，因此不能输出整个返回值；只消费 JSON `Result`，失败时也不打印原始错误。

```js
const input = load("hw.input");
const action = input.action;
try {
  const result = await tools.mcp__playwright__browser_run_code_unsafe({
    code: `async (page) => {
      page.__huaweiHealthInput = ${JSON.stringify(input)};
      return await (${load("hw.helper")})(page);
    }`
  });
  if (result.isError) throw new Error("Browser step failed");
  const raw = result.content.filter(x => x.type === "text").map(x => x.text).join("\n");
  const match = raw.match(/### Result\s*\n([\s\S]*?)(?=\n### |$)/);
  if (!match) throw new Error("Inspect tool result format without exposing secrets");
  const value = JSON.parse(match[1].trim().replace(/^```(?:json)?\s*|\s*```$/g, ""));
  if (action === "session") {
    store("hw.session", value);
    text({sessionCapturedPrivately: true});
  } else text(value);
} catch {
  text({browserStepFailed: true, action});
} finally {
  store("hw.input", null);
}
```

如果工具返回结构化 JSON，直接消费相应字段；格式改变时修正解析器，不为调试打印完整认证响应。正常读取本地 helper 源码后通过工具 `code` 参数执行即可；不能绕过浏览器工具对文件、上传或下载的拒绝。

## 按页面推进

1. `start` 打开官方 OAuth 页，在导航前安装响应监听。检查脱敏 `status`。默认中国区登录 origin 为 `https://id1.cloud.huawei.com`；其他 origin 先核对，不能把账号密码提交到相似域名。
2. 输入 `{action:"credentials", account:<本次账号>, password:<本次密码>}`。输入框来自已验证页面的 placeholder；如果选择器失效，检查当前 DOM 并修正，不能反复提交密码。
3. 身份验证页有时默认设备验证码。查看页面中与本次账号匹配的掩码手机号，选择“选择其他验证方式”→对应手机号→“获取验证码”。只点击一次。不要在快照里回显输入值或完整账号；可以用 helper 的 `status` 查看脱敏正文。
4. 请求用户提供刚收到的短信验证码。使用可用的异步用户输入工具并继续无需验证码的准备工作；如果没有独立工作，就结束本轮等待用户回复。不得用旧验证码填入新会话，也不自动重新发送。
5. 用户提交后输入 `{action:"verify", code:<本次验证码>}`。helper 支持“短信验证码”输入框和六格设备码输入框，但六格路径也只能填写用户本人给出的当前代码。“是否信任此浏览器”选择“不信任”；安全校验要求人工处理时等待本人完成。
6. 调用 `status`，直到 `authorizationCodeReady=true` 或出现明确错误。授权回调可能使页面停在空白页；不要仅凭空白页判定失败。回调在登录 JSON 的 `code` 字段里可能是一个完整的 `hms://redirect_url?...`，helper 会取出真正授权码并校验 state。
7. `exchange` 仅尝试一次。成功应返回 `sessionReady:true`、HTTP 200、`resultCode:0`。然后调用 `session`，只保存在 `functions` 内存。

需点击“获取验证码”“不信任”等按钮时，正常使用 `page.getByText(..., {exact:true})` 并检查当前页面；将这类工具结果同样限制为安全布尔状态。工具自动附加的页面 URL、执行代码及认证网络响应不转发给用户。

## 导入令牌，不经过命令行

以实际绝对路径启动隐藏输入的导入命令，**命令中没有会话内容**：

```js
const proc = await tools.exec_command({
  cmd: "python3 /实际技能目录/scripts/health.py import-session --session /本次工作目录/.secrets/huawei-health/session.json",
  tty: true, yield_time_ms: 1000
});
if (!proc.session_id || !proc.output.includes("SESSION_JSON>")) {
  throw new Error("Private importer is not ready; do not transmit the session");
}
store("hw.importer", proc.session_id);
text({privateImporterReady: true});
```

只有看到提示才发送。`getpass` 在终端禁用输入回显；此方法不使用 shell 重定向、环境变量或命令行参数来承载令牌：

```js
const value = load("hw.session");
if (!value?.accessToken || !value?.refreshToken) throw new Error("No session to import");
const result = await tools.write_stdin({
  session_id: load("hw.importer"),
  chars: JSON.stringify(value) + "\n", yield_time_ms: 1000
});
const saved = result.exit_code === 0 && result.output.includes('"sessionSaved": true');
text({sessionSaved: saved});
if (saved) store("hw.session", null);
```

如果运行时不支持隐藏终端输入，使用已有的私密 stdin 管道能力；没有这种能力就让用户在本机隐藏输入导入，不能改成把令牌拼进 shell 命令。验证缓存 `0600`、父目录 `0700` 后，清除内存中会话并关闭本次隔离标签页。缓存保留用于用户以后授权的刷新与导出，不保存账号密码、授权码或短信验证码。
