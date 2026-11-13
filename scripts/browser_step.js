async (page) => {
  const input = page.__huaweiHealthInput || { action: "status" };
  delete page.__huaweiHealthInput;
  let flow = page.__huaweiHealthFlow;
  const capture = (location) => {
    try {
      const url = new URL(location);
      if (url.protocol !== "hms:" || url.hostname !== "redirect_url") return;
      if (url.searchParams.get("state") !== flow.state) {
        flow.error = "OAuth state mismatch";
        return;
      }
      flow.code = url.searchParams.get("code");
      flow.error = url.searchParams.get("error");
    } catch { flow.error = "Unrecognized OAuth callback"; }
  };
  if (input.action === "start") {
    if (flow?.listener) page.off("response", flow.listener);
    const random = await page.evaluate(() => Array.from(crypto.getRandomValues(new Uint8Array(64)), b => b.toString(16).padStart(2, "0")).join(""));
    flow = page.__huaweiHealthFlow = { state: random.slice(0, 48), nonce: random.slice(48, 96), uuid: random.slice(96), code: null };
    flow.listener = async (response) => {
      try {
        const url = new URL(response.url());
        if (!url.hostname.endsWith(".cloud.huawei.com")) return;
        if (url.pathname === "/oauth2/ajax/login") {
          const result = await response.json();
          if (String(result.isSuccess) === "true" && typeof result.code === "string") capture(result.code);
        }
        if (/\/oauth2\/v3\/(?:authorize|loginCallback)$/.test(url.pathname)) {
          const headers = await response.allHeaders();
          if (headers.location?.startsWith("hms://")) capture(headers.location);
        }
      } catch { /* Ignore unrelated or incomplete responses. */ }
    };
    page.on("response", flow.listener);
    const params = new URLSearchParams({ access_type: "offline", response_type: "code", client_id: "10414141",
      ui_locales: "zh-cn", redirect_uri: "hms://redirect_url", state: flow.state, nonce: flow.nonce,
      uuid: flow.uuid, display: "touch", include_granted_scopes: "true", countryCode: "CN", cVersion: "HwID_6.12.0.303",
      scope: "openid https://www.huawei.com/auth/account/base.profile https://www.huawei.com/healthkit/sleep.both https://www.huawei.com/healthkit/activityrecord.both https://www.huawei.com/healthkit/activity.both" });
    await page.goto("https://oauth-login.cloud.huawei.com/oauth2/v3/authorize?" + params, { waitUntil: "domcontentloaded" });
    return { started: true };
  }
  if (!flow) throw new Error("Run start in the same isolated tab first");
  if (input.action === "credentials") {
    if (new URL(page.url()).origin !== "https://id1.cloud.huawei.com") throw new Error("Not the verified China Huawei login origin");
    if (flow.passwordAttempted) throw new Error("Password already submitted; wait for the user or start a new authorized attempt");
    if (!input.account || !input.password) throw new Error("Account and password are required in memory");
    flow.passwordAttempted = true;
    flow.accountForRedaction = String(input.account);
    try {
      await page.getByPlaceholder("手机号/邮件地址/账号名", { exact: true }).fill(input.account);
      await page.getByPlaceholder("密码", { exact: true }).fill(input.password);
      await page.getByText("登录", { exact: true }).click();
    } finally { input.password = null; input.account = null; }
    return { passwordSubmitted: true };
  }
  if (input.action === "verify") {
    if (new URL(page.url()).origin !== "https://id1.cloud.huawei.com") throw new Error("Not the verified China Huawei verification origin");
    if (!/^\d{6}$/.test(input.code || "")) throw new Error("A user-provided six-digit code is required");
    try {
      const sms = page.getByPlaceholder("短信验证码", { exact: true });
      if (await sms.isVisible()) await sms.fill(input.code);
      else {
        const boxes = page.locator('input[type="tel"]:visible');
        if (await boxes.count() !== 6) throw new Error("Inspect the current verification form first");
        for (let i = 0; i < 6; i++) await boxes.nth(i).fill(input.code[i]);
      }
      await page.getByText("确定", { exact: true }).click();
    } finally { input.code = null; }
    return { verificationSubmitted: true };
  }
  if (input.action === "exchange") {
    if (flow.exchangeAttempted || !flow.code || flow.error) throw new Error("No unused, validated authorization code");
    flow.exchangeAttempted = true;
    let response;
    try {
      response = await page.request.post("https://healthsession.things.dbankcloud.cn/commonAbility/userAccessToken/obtain", {
        headers: { "Content-Type": "application/json", "x-ts": String(Date.now()), "x-version": "and_health_17.0.10.300" },
        data: { authorizationCode: flow.code, appId: "10414141" }, timeout: 25000, maxRedirects: 0 });
    } finally { flow.code = null; }
    const result = await response.json();
    const ok = response.ok() && result.resultCode === 0 && !!result.accessToken && !!result.refreshToken && !!result.uid && !!result.accessTokenExpireTime;
    if (ok) flow.session = { accessToken: result.accessToken, refreshToken: result.refreshToken,
      uid: result.uid, accessTokenExpireTime: result.accessTokenExpireTime, timeStamp: result.timeStamp,
      appId: "10414141", appVersion: "and_health_17.0.10.300" };
    return { httpStatus: response.status(), resultCode: result.resultCode, sessionReady: !!ok };
  }
  if (input.action === "session") {
    if (!flow.session) throw new Error("No successful session to transfer privately");
    return flow.session; // Secret output: consume inside functions, never display the tool response.
  }
  const location = new URL(page.url());
  let text = await page.locator("body").innerText();
  if (flow.accountForRedaction) text = text.split(flow.accountForRedaction).join("[账号]");
  return { page: location.origin + location.pathname, authorizationCodeReady: !!flow.code,
    sessionReady: !!flow.session, error: flow.error || null,
    text: text.replace(/\b1\d{10}\b/g, "[手机号]").replace(/\b\d{6}\b/g, "[验证码]")
      .replace(/[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}/gi, "[邮箱]").slice(-2200) };
}
