package com.huawei.pluginsport.ftp;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hwcloudmodel.healthdatacloud.HealthDataCloudFactory;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.login.ui.login.LoginInit;
import com.huawei.profile.coordinator.ProfileRequestConstants;
import com.huawei.profile.profile.ProfileUtilsSdk;
import com.huawei.profile.utils.BaseUtil;
import health.compact.a.CommonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: /work/unpacked/classes15.dex */
public class FtpLacHRCloudFactory extends HealthDataCloudFactory {
    public FtpLacHRCloudFactory(Context context) {
        super(context);
    }

    private List<Map<String, Object>> c() {
        ArrayList arrayList = new ArrayList();
        HashMap map = new HashMap();
        map.put(ProfileUtilsSdk.TYPE, 300016);
        map.put("fieldNames", Collections.singletonList("lthr"));
        map.put("strategy", Collections.singletonList("LAST"));
        arrayList.add(map);
        return arrayList;
    }

    public Map<String, Object> getBody(Object obj) {
        HashMap map = new HashMap();
        LoginInit loginInit = LoginInit.getInstance(BaseApplication.b());
        String accountInfo = loginInit.getAccountInfo(1008);
        map.put("siteID", loginInit.getAccountInfo(1009));
        map.put("source", 1);
        map.put("ts", Long.valueOf(System.currentTimeMillis()));
        map.put("token", accountInfo);
        map.put("tokenType", 1);
        map.put("appId", BaseUtil.HEALTH_PACKAGE_NAME);
        map.put("deviceType", zfv.d());
        map.put(ProfileUtilsSdk.DEVICE_ID, loginInit.getDeviceId());
        map.put("iVersion", 1);
        map.put("language", csp.b());
        map.put("timeZone", iwb.d());
        map.put("lifetimeStatisticsReqs", c());
        map.put("signType", "BUSCloud_sign_dataCloud");
        return map;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = super.getHeaders();
        String accountInfo = LoginInit.getInstance(BaseApplication.b()).getAccountInfo(1008);
        if (TextUtils.isEmpty(accountInfo)) {
            LogUtil.a("FtpCloudFactory", new Object[]{"callService severToken is null!"});
            LoginInit.getInstance(BaseApplication.b()).moveInfoFromSPTODB();
            if (LoginInit.getInstance(BaseApplication.b()).getIsLogined()) {
                LoginInit.tryLoginWhenTokenInvalid();
            }
        }
        String accountInfo2 = LoginInit.getInstance(BaseApplication.b()).getAccountInfo(1011);
        headers.put(ProfileRequestConstants.AUTHORIZATION, "Bearer " + LoginInit.getInstance(BaseApplication.b()).getAccountInfo(1015));
        headers.put(ProfileRequestConstants.CONTENT_TYPE, "application/json;charset=UTF-8");
        headers.put("x-client-timezone", mna.s(System.currentTimeMillis()));
        headers.put("x-ts", String.valueOf(System.currentTimeMillis()));
        headers.put("x-app-id", BaseApplication.d());
        headers.put("x-token", accountInfo);
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("x-caller-trace-id", String.valueOf(System.currentTimeMillis()) + Math.random());
        headers.put("x-os-version", CommonUtil.b(BaseApplication.b()));
        headers.put(ProfileRequestConstants.X_HUID_KEY, accountInfo2);
        headers.put(ProfileRequestConstants.X_VERSION_KEY, CommonUtil.b(BaseApplication.b()));
        headers.put("x-client-type", "Android");
        return headers;
    }
}
