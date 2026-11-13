package com.huawei.pluginsport.ftp;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hwcloudmodel.healthdatacloud.HealthDataCloudFactory;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.login.ui.login.LoginInit;
import com.huawei.profile.coordinator.ProfileRequestConstants;
import health.compact.a.CommonUtil;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: /work/unpacked/classes15.dex */
public class FtpCloudFactory extends HealthDataCloudFactory {
    public FtpCloudFactory(Context context) {
        super(context);
    }

    public Map<String, Object> getBody(Object obj) {
        HashMap map = new HashMap();
        map.put("language", csp.b());
        return map;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = super.getHeaders();
        headers.put("x-client-timezone", mna.s(System.currentTimeMillis()));
        headers.put("ts", String.valueOf(System.currentTimeMillis()));
        headers.put(ProfileRequestConstants.AUTHORIZATION, "Bearer " + LoginInit.getInstance(BaseApplication.b()).getAccountInfo(1015));
        String accountInfo = LoginInit.getInstance(BaseApplication.b()).getAccountInfo(1008);
        if (TextUtils.isEmpty(accountInfo)) {
            LogUtil.a("FtpCloudFactory", new Object[]{"callService severToken is null!"});
            LoginInit.getInstance(BaseApplication.b()).moveInfoFromSPTODB();
            if (LoginInit.getInstance(BaseApplication.b()).getIsLogined()) {
                LoginInit.tryLoginWhenTokenInvalid();
            }
        }
        headers.put("token", accountInfo);
        headers.put("x-app-id", BaseApplication.d());
        headers.put("x-client-version", CommonUtil.c(BaseApplication.b()));
        return headers;
    }
}
