package com.huawei.hwcloudmodel.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import androidx.health.connect.client.records.metadata.Metadata;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.hwcloudmodel.model.CloudCommonReponse;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.login.ui.login.AccountConstants;
import com.huawei.login.ui.login.LoginInit;
import com.huawei.thirdloginbasemodule.ThirdLoginDataStorageUtil;
import com.huawei.up.utils.NSPException;
import defpackage.mli;
import defpackage.ojs;
import health.compact.a.BroadcastManagerUtil;
import health.compact.a.CommonUtil;
import health.compact.a.GRSManager;
import health.compact.a.KeyValDbManager;
import health.compact.a.LocalBroadcast;
import health.compact.a.SharedPreferenceManager;
import health.compact.a.Utils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes10.dex */
public class NspClient extends AbsNspClient {
    private static boolean b = false;
    private static d e;
    private LoginInit a;
    private Context c;

    static class d extends BroadcastReceiver {
        d() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action;
            if (intent == null || context == null) {
                return;
            }
            try {
                action = intent.getAction();
            } catch (Exception unused) {
                LogUtil.a("UIME_NspClient", "onReceive: getAction() exception");
                action = null;
            }
            LogUtil.e("UIME_NspClient", "onReceive:", action);
            if ("com.huawei.plugin.account.logout".equals(action)) {
                boolean unused2 = NspClient.b = true;
            } else if ("com.huawei.plugin.account.login".equals(action)) {
                boolean unused3 = NspClient.b = false;
            } else {
                LogUtil.g("UIME_NspClient", "onReceive is other action");
            }
        }
    }

    public NspClient(Context context) {
        this.c = null;
        this.a = null;
        if (context == null) {
            LogUtil.g("UIME_NspClient", "Nsplient context is null");
            return;
        }
        Context applicationContext = context.getApplicationContext();
        this.c = applicationContext;
        if (this.a == null) {
            this.a = LoginInit.getInstance(applicationContext);
        }
        if (e == null) {
            e = new d();
            d();
            a();
        }
    }

    private void a() {
        LogUtil.d("UIME_NspClient", "registerLoginBroadcastReceiver register accout login broadcast ");
        BroadcastManagerUtil.e(this.c, e, new IntentFilter("com.huawei.plugin.account.login"), LocalBroadcast.a, null);
    }

    private String b() {
        String strD;
        return (CommonUtil.aq() && (strD = SharedPreferenceManager.d(this.c, String.valueOf(10008), "key_download_config")) != null && strD.contains("lfhealthtest2")) ? strD : GRSManager.b(this.c).getUrl("healthDeviceUrl");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: com.huawei.up.utils.NSPException */
    private void c(String str, Map<String, Object> map, Map<String, Object> map2) throws NSPException {
        e(map2);
        boolean zK = Utils.k();
        LogUtil.d("UIME_NspClient", "callService isOverSea =", Boolean.valueOf(zK));
        if (zK) {
            String accountInfo = LoginInit.getInstance(BaseApplication.getContext()).getAccountInfo(AccountConstants.SITE_ID);
            map2.put("siteId", accountInfo);
            LogUtil.d("UIME_NspClient", "callService getSiteId=", accountInfo);
        }
        map2.put("deviceType", String.valueOf(CommonUtil.i(this.c)));
        String deviceType = LoginInit.getInstance(this.c).getDeviceType();
        LogUtil.e("UIME_NspClient", "callService upDeviceType = ", deviceType);
        map2.put("upDeviceType", deviceType);
        String deviceId = LoginInit.getInstance(this.c).getDeviceId();
        if (Metadata.EMPTY_ID.equals(deviceId)) {
            deviceId = "clientnull";
        }
        LogUtil.e("UIME_NspClient", "callService deviceId=", deviceId);
        map2.put("deviceId", deviceId);
        map2.put("sysVersion", Build.VERSION.RELEASE);
        if (str.equals("/dataQuery/path/getMotionPathByVersion") || str.equals("/dataQuery/path/getMotionPathData")) {
            map2.put("iVersion", Integer.valueOf(Utils.c()));
        } else {
            map2.put("iVersion", Integer.valueOf(Utils.d()));
        }
        map2.put("isManually", Integer.valueOf(Utils.e()));
        map2.put("language", "zh");
        map2.put("oaId", Metadata.EMPTY_ID);
        String strD = KeyValDbManager.b(this.c).d("cloud_user_privacy10");
        if (strD == null || !"true".equals(strD)) {
            map2.put("isTrackingEnabled", 0);
        } else {
            map2.put("isTrackingEnabled", 1);
        }
        map2.put("improveState", Boolean.valueOf(ojs.c()));
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry != null) {
                map2.put(entry.getKey(), entry.getValue());
            }
        }
        map2.put("currentManufacturer", Build.MANUFACTURER);
    }

    private void d() {
        LogUtil.d("UIME_NspClient", "registerLogoutBroadcastReceiver register accout logout broadcast ");
        BroadcastManagerUtil.e(this.c, e, new IntentFilter("com.huawei.plugin.account.logout"), LocalBroadcast.a, null);
    }

    private void d(mli mliVar, Gson gson) {
        String strA;
        CloudCommonReponse cloudCommonReponse;
        if (mliVar.a() == null || (strA = Utils.a(mliVar.a())) == null) {
            return;
        }
        if (strA.length() < 1024) {
            LogUtil.e("UIME_NspClient", "callService response content=", strA);
        } else {
            LogUtil.e("UIME_NspClient", "callService response content=", strA.substring(0, 1023));
        }
        try {
            cloudCommonReponse = (CloudCommonReponse) gson.fromJson(strA, CloudCommonReponse.class);
        } catch (JsonSyntaxException e2) {
            if (strA.length() < 30) {
                LogUtil.d("UIME_NspClient", "processResponseContent fromJson exception response content=", strA);
            } else {
                LogUtil.d("UIME_NspClient", "processResponseContent fromJson exception response content=", strA.substring(0, 30));
            }
            LogUtil.d("UIME_NspClient", "processResponseContent, fromJson exception :", e2.getMessage());
            cloudCommonReponse = null;
        }
        if (cloudCommonReponse != null) {
            if (cloudCommonReponse.getResultCode().intValue() == 1002 || cloudCommonReponse.getResultCode().intValue() == 1004) {
                LogUtil.d("UIME_NspClient", "processResponseContent auth failed, so need to logout!", cloudCommonReponse.getResultCode());
                LoginInit.tryLoginWhenTokenInvalid();
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: com.huawei.up.utils.NSPException */
    private void e(Map<String, Object> map) throws NSPException {
        Object objD;
        map.put("ts", Long.valueOf(System.currentTimeMillis()));
        map.put("tokenType", Integer.valueOf(ThirdLoginDataStorageUtil.getTokenTypeValue()));
        LoginInit loginInit = this.a;
        if (loginInit == null) {
            throw new NSPException(1, "mLogin is null.");
        }
        String accountInfo = loginInit.getAccountInfo(AccountConstants.TOKEN);
        LogUtil.e("UIME_NspClient", "callService severToken from db = ", accountInfo);
        if (TextUtils.isEmpty(accountInfo)) {
            LogUtil.g("UIME_NspClient", "callService severToken is null!");
            LoginInit.getInstance(this.c).moveInfoFromSPTODB();
            if (this.a.getIsLogined()) {
                LoginInit.tryLoginWhenTokenInvalid();
            }
            throw new NSPException(1, "severToken is null.");
        }
        map.put("token", accountInfo);
        if (LoginInit.getInstance(this.c).isLoginedByWear()) {
            LogUtil.d("UIME_NspClient", "callService appid wear logined");
            map.put("source", 2);
            objD = BaseApplication.APP_PACKAGE_WEAR;
        } else {
            LogUtil.d("UIME_NspClient", "callService appid health logined");
            objD = com.huawei.haf.application.BaseApplication.d();
            map.put("source", 1);
        }
        map.put("appId", objD);
    }

    @Override // com.huawei.hwcloudmodel.utils.AbsNspClient
    protected mli callService(String str, Map<String, Object> map, int i, int i2, int i3) throws NSPException {
        return callService(str, map, i, i2, i3, 1);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: com.huawei.up.utils.NSPException */
    @Override // com.huawei.hwcloudmodel.utils.AbsNspClient
    protected mli callService(String str, Map<String, Object> map, int i, int i2, int i3, int i4) throws NSPException {
        String json;
        String url;
        LogUtil.d("UIME_NspClient", "callService enter urlType=", Integer.valueOf(i4));
        HashMap map2 = new HashMap();
        if (TextUtils.isEmpty(str)) {
            LogUtil.g("UIME_NspClient", "callService Service isEmpty");
            throw new NSPException(1, "Service name is empty.");
        }
        c(str, map, map2);
        Gson gson = new Gson();
        try {
            json = gson.toJson(map2);
            try {
                if (json.length() < 1024) {
                    LogUtil.e("UIME_NspClient", "callService data = ", json, "length of json more than 1024");
                } else {
                    LogUtil.e("UIME_NspClient", "callService data = ", json.substring(0, 1023), "length of json less than 1024");
                }
            } catch (Exception unused) {
                LogUtil.g("UIME_NspClient", "callService exception");
            }
        } catch (Exception unused2) {
            json = null;
        }
        String str2 = json;
        StringBuffer stringBuffer = new StringBuffer();
        if (i4 == 2) {
            url = b();
        } else if (str.equals("/dataRecommend/common/updateCommentRemainderTimes") || str.equals("/dataRecommend/common/getCommentRemainderTimes")) {
            url = GRSManager.b(this.c).getUrl("healthRecommendUrl");
        } else if (str.equals("hiHealth/getThirdIdentify")) {
            url = GRSManager.b(this.c).getUrl("domainHealthcommonHicloud") + "/commonAbility/";
        } else {
            if (!Utils.i()) {
                LogUtil.g("UIME_NspClient", "callService fetch HEALTH_CLOUD from no_cloud user; service:", str, " type:", Integer.valueOf(i3));
                throw new NSPException(2, "NspClient request HEALTH_CLOUD in no-cloud country");
            }
            url = GRSManager.b(this.c).getUrl("healthCloudUrl");
        }
        stringBuffer.append(url);
        stringBuffer.append(str);
        String string = stringBuffer.toString();
        LogUtil.e("UIME_NspClient", "callService apiURL=", string);
        mli mliVarRequest = request(this.c, string, str2, i, i2, i3);
        if (mliVarRequest == null) {
            LogUtil.g("UIME_NspClient", "callService response=null");
            throw new NSPException(1, "Server No Response");
        }
        LogUtil.d("UIME_NspClient", "callService response=[status:", Integer.valueOf(mliVarRequest.e()), ", code:", Integer.valueOf(mliVarRequest.d()), "]");
        d(mliVarRequest, gson);
        return mliVarRequest;
    }
}
