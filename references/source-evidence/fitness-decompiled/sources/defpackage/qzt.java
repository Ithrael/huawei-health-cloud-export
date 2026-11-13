package defpackage;

import android.content.Context;
import com.huawei.basefitnessadvice.callback.UiCallback;
import com.huawei.haf.application.BaseApplication;
import com.huawei.haf.common.exception.ExceptionUtils;
import com.huawei.haf.handler.HandlerExecutor;
import com.huawei.haf.threadpool.ThreadPoolManager;
import com.huawei.health.devicemgr.api.constant.GetDeviceInfoMode;
import com.huawei.health.devicemgr.business.entity.HwGetDevicesParameter;
import com.huawei.health.h5pro.core.H5ProLaunchOption;
import com.huawei.hihealth.HiSampleConfig;
import com.huawei.hihealth.data.listener.HiDataOperateListener;
import com.huawei.hihealth.data.listener.HiDataReadResultListener;
import com.huawei.hwcommonmodel.constants.AnalyticsValue;
import com.huawei.hwcommonmodel.utils.DateFormatUtil;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.networkclient.ResultCallback;
import com.huawei.pluginfitnessadvice.h5pro.SeriesCourseH5Repository;
import com.huawei.pluginsport.ftp.FtpCloudFactory;
import com.huawei.pluginsport.ftp.FtpLacHRCloudFactory;
import defpackage.rab;
import health.compact.a.GRSManager;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: /work/unpacked/classes15.dex */
public class qzt {
    private static qzt b = null;
    private static final Object c = new Object();
    public static int e = 4;
    private final FtpCloudFactory d = new FtpCloudFactory(BaseApplication.b());
    private final FtpLacHRCloudFactory a = new FtpLacHRCloudFactory(BaseApplication.b());

    public static class d {
        public static int a = 120;
        public static int b = 150;
        public static int c = 0;
        public static int d = 105;
        public static int e = 90;
        public static int g = 500;
        public static int h = 75;
        public static int j = 55;
    }

    private qzt() {
    }

    public static rab a(int i, rab rabVar) {
        if (rabVar == null) {
            LogUtil.d("FtpDataManager", new Object[]{"updatePowerZoneConfig config == null"});
            return d(i);
        }
        double d2 = i;
        return new rab.b().d(rabVar.a(), c(d2, rabVar.a())).c(rabVar.d(), c(d2, rabVar.d())).a(rabVar.e(), c(d2, rabVar.e())).b(rabVar.b(), c(d2, rabVar.b())).e(rabVar.c(), c(d2, rabVar.c())).g(rabVar.j(), c(d2, rabVar.j())).f(rabVar.f(), c(d2, rabVar.f())).j(rabVar.i(), c(d2, rabVar.i())).c();
    }

    public static void a() {
        HashMap map = new HashMap(16);
        map.put("event", 0);
        map.put("isHaveData", true);
        map.put("dataType", Integer.valueOf(e));
        mgw.a().d(BaseApplication.b(), AnalyticsValue.SPORT_RECORD_ATHLETIC_ABILITY_CARD_2040191.value(), map, 0);
    }

    public static void a(Context context, int i) {
        dqg.b().initH5Pro();
        dqg dqgVarB = dqg.b();
        H5ProLaunchOption.Builder forceDarkMode = new H5ProLaunchOption.Builder().addPath("#/ftp?entrance=" + i).setImmerse().showStatusBar().setStatusBarTextBlack(true).setForceDarkMode(1);
        SeriesCourseH5Repository.registerService();
        dqgVarB.loadH5ProApp(context, "com.huawei.health.h5.cycle", forceDarkMode);
    }

    private void a(final UiCallback<rac> uiCallback, final boolean z) {
        d().e(new ResultCallback<String>() { // from class: qzt.5
            /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
            public void onSuccess(String str) {
                int iC = qzt.c(str);
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpLacHRRecord:", Integer.valueOf(iC)});
                qzt.this.c((UiCallback<rac>) uiCallback, z, iC);
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.a("R_FtpDataManager", new Object[]{"requestFtpLacData onFailure", ExceptionUtils.a(th)});
                qzt.this.c((UiCallback<rac>) uiCallback, z, 0);
            }
        });
    }

    private static int c(double d2, int i) {
        return (int) Math.round((((long) d2) * ((long) i)) / 100.0d);
    }

    public static int c(String str) {
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("lifetimeStatisticsResults");
            if (jSONArrayOptJSONArray != null && jSONArrayOptJSONArray.length() != 0) {
                int i = jSONArrayOptJSONArray.getJSONObject(0).getJSONObject("statistics").getInt("last");
                if (i > 0 && i < 255) {
                    return i;
                }
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"ftpLacHR ", Integer.valueOf(i), " is invalid"});
                return 0;
            }
            ReleaseLogUtil.d("R_FtpDataManager", new Object[]{"resultsArray is null"});
            return 0;
        } catch (JSONException e2) {
            ReleaseLogUtil.d("R_FtpDataManager", new Object[]{"getFtpLacHR exception ", ExceptionUtils.a(e2)});
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(long j, String str, final String str2) {
        rrt.d("9002", str2, str, new HiDataOperateListener() { // from class: qzw
            public final void onResult(int i, Object obj) {
                ReleaseLogUtil.c("FtpDataManager", new Object[]{"sendPowerZoneForCloud errorCode:", Integer.valueOf(i), "configId:", str2});
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(final UiCallback<rac> uiCallback, final boolean z, final int i) {
        c(new ResultCallback<rah>() { // from class: qzt.10
            /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
            public void onSuccess(rah rahVar) {
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpRecord:", rahVar});
                raf rafVarA = rahVar.a();
                if (rafVarA == null) {
                    ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"ftpRecord == null"});
                    uiCallback.onSuccess((Object) null);
                    return;
                }
                rac racVar = new rac();
                racVar.b((int) Math.round(rafVarA.b()));
                racVar.e(DateFormatUtil.b(rafVarA.a()));
                racVar.a(1);
                racVar.c(0);
                racVar.e(i);
                if (z) {
                    qzt.this.c(racVar.d(), sbk.a(racVar), "900200037");
                }
                uiCallback.onSuccess(racVar);
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.a("R_FtpDataManager", new Object[]{"getFtpRecord onFailure", ExceptionUtils.a(th)});
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(final rac racVar, List<String> list) {
        if (racVar == null) {
            ReleaseLogUtil.a("R_FtpDataManager", new Object[]{"ftpConfig == null"});
        } else {
            rrt.d("9002", list, new HiDataReadResultListener() { // from class: qzt.8
                public void onResult(Object obj, int i, int i2) {
                    ReleaseLogUtil.c("R_FtpDataManager", oqe.e(new Object[]{"dealPowerZone errorCode: ", Integer.valueOf(i), ", data: ", obj}));
                    qzt.this.d(obj, racVar);
                }

                public void onResultIntent(int i, Object obj, int i2, int i3) {
                }
            });
        }
    }

    public static qzt d() {
        if (b == null) {
            synchronized (c) {
                qzt qztVar = new qzt();
                b = qztVar;
                LogUtil.d("FtpDataManager", new Object[]{"FtpDataManager instance hashcode: ", Integer.valueOf(qztVar.hashCode())});
            }
        }
        return b;
    }

    public static rab d(double d2) {
        return new rab.b().d(d.c, 0).c(d.j, c(d2, d.j)).a(d.h, c(d2, d.h)).b(d.e, c(d2, d.e)).e(d.d, c(d2, d.d)).g(d.a, c(d2, d.a)).f(d.b, c(d2, d.b)).j(d.g, c(d2, d.g)).c();
    }

    public static void d(Context context, int i) {
        dqg.b().initH5Pro();
        dqg dqgVarB = dqg.b();
        H5ProLaunchOption.Builder forceDarkMode = new H5ProLaunchOption.Builder().addPath("#/ftp?entrance=" + i + "&showLactate=1&lactateTab=1").setImmerse().showStatusBar().setStatusBarTextBlack(true).setForceDarkMode(1);
        SeriesCourseH5Repository.registerService();
        dqgVarB.loadH5ProApp(context, "com.huawei.health.h5.cycle", forceDarkMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(Object obj, UiCallback<rac> uiCallback, boolean z) {
        if (!d(obj)) {
            a(uiCallback, z);
            return;
        }
        rac racVar = (rac) sbk.d(((HiSampleConfig) ((List) obj).get(0)).getConfigData(), rac.class);
        if (racVar == null) {
            a(uiCallback, z);
        } else if (racVar.a() == 0) {
            uiCallback.onSuccess(racVar);
        } else {
            a(uiCallback, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(Object obj, rac racVar) {
        if (!d(obj)) {
            c(racVar.d(), sbk.a(d(racVar.e())), "900200036");
            return;
        }
        String configData = ((HiSampleConfig) ((List) obj).get(0)).getConfigData();
        ReleaseLogUtil.c("R_FtpDataManager", oqe.e(new Object[]{"onResult json: ", configData}));
        rab rabVar = (rab) sbk.d(configData, rab.class);
        if (rabVar == null) {
            c(racVar.d(), sbk.a(d(racVar.e())), "900200036");
        } else {
            c(racVar.d(), sbk.a(a(racVar.e(), rabVar)), "900200036");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean d(Object obj) {
        if (!okt.e(obj, HiSampleConfig.class)) {
            ReleaseLogUtil.a("R_FtpDataManager", new Object[]{"list getSampleConfigData isListTypeMatch false "});
            return false;
        }
        if (!okt.a((List) obj)) {
            return true;
        }
        ReleaseLogUtil.a("R_FtpDataManager", new Object[]{"list getSampleConfigData list is empty "});
        return false;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public void c(final ResultCallback<rah> resultCallback) {
        if (HandlerExecutor.a()) {
            ThreadPoolManager.a().execute(new Runnable() { // from class: raa
                @Override // java.lang.Runnable
                public final void run() {
                    this.d.c(resultCallback);
                }
            });
            return;
        }
        String str = GRSManager.b(BaseApplication.b()).getUrl("achievementUrl") + "/achievement/v1/ftp:refresh";
        ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpRecord url:", str});
        pnq.c().c(str, this.d.getHeaders(), pob.a(this.d.getBody(null)), rah.class, new ResultCallback<rah>() { // from class: qzt.3
            /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
            public void onSuccess(rah rahVar) {
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpRecord data:", rahVar});
                resultCallback.onSuccess(rahVar);
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("R_FtpDataManager", new Object[]{"getFtpRecord onFailure:", ExceptionUtils.a(th)});
                resultCallback.onFailure(th);
            }
        });
    }

    public void b() {
        ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"sendFtpAndPowerZone"});
        if (esl.a(eqj.b().getDeviceInfo(GetDeviceInfoMode.CONNECTED_MAIN_DEVICES_WITHOUT_AW70, (HwGetDevicesParameter) null, "FtpDataManager"), 266)) {
            c(new UiCallback<rac>() { // from class: qzt.7
                /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
                public void onSuccess(rac racVar) {
                    ArrayList arrayList = new ArrayList(1);
                    arrayList.add("900200036");
                    qzt.this.c(racVar, arrayList);
                }

                public void onFailure(int i, String str) {
                }
            }, true);
        } else {
            ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"sendFtpAndPowerZone no checkSupportCapability"});
        }
    }

    /* synthetic */ void b(UiCallback uiCallback, boolean z) {
        c((UiCallback<rac>) uiCallback, z);
    }

    /* synthetic */ void b(ResultCallback resultCallback) {
        e((ResultCallback<String>) resultCallback);
    }

    public void c(final UiCallback<rac> uiCallback, final boolean z) {
        if (HandlerExecutor.a()) {
            ThreadPoolManager.a().execute(new Runnable() { // from class: qzz
                @Override // java.lang.Runnable
                public final void run() {
                    this.c.b(uiCallback, z);
                }
            });
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("900200037");
        rrt.d("9002", arrayList, new HiDataReadResultListener() { // from class: qzt.1
            public void onResult(Object obj, int i, int i2) {
                ReleaseLogUtil.c("R_FtpDataManager", oqe.e(new Object[]{"onResult errorCode: ", Integer.valueOf(i), ", data: ", obj}));
                qzt.this.d(obj, (UiCallback<rac>) uiCallback, z);
            }

            public void onResultIntent(int i, Object obj, int i2, int i3) {
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"onResultIntent getFtpConfig"});
            }
        });
    }

    public void e(final UiCallback<rab> uiCallback) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("900200036");
        rrt.d("9002", arrayList, new HiDataReadResultListener() { // from class: qzt.4
            public void onResult(Object obj, int i, int i2) {
                ReleaseLogUtil.c("R_FtpDataManager", oqe.e(new Object[]{"onResult errorCode: ", Integer.valueOf(i), ", data: ", obj}));
                if (!qzt.this.d(obj)) {
                    uiCallback.onSuccess((Object) null);
                    return;
                }
                String configData = ((HiSampleConfig) ((List) obj).get(0)).getConfigData();
                ReleaseLogUtil.c("R_FtpDataManager", oqe.e(new Object[]{"onResult json: ", configData}));
                uiCallback.onSuccess((rab) sbk.d(configData, rab.class));
            }

            public void onResultIntent(int i, Object obj, int i2, int i3) {
            }
        });
    }

    public void e(final ResultCallback<String> resultCallback) {
        if (HandlerExecutor.a()) {
            ThreadPoolManager.a().execute(new Runnable() { // from class: qzx
                @Override // java.lang.Runnable
                public final void run() {
                    this.e.b(resultCallback);
                }
            });
            return;
        }
        String str = GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + "/dataStat/v1/basicCalculations/lifetimeStatistics:calculate";
        ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpLacRecord url:", str});
        pnq.c().c(str, this.a.getHeaders(), pob.a(this.a.getBody(null)), String.class, new ResultCallback<String>() { // from class: qzt.2
            /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
            public void onSuccess(String str2) {
                ReleaseLogUtil.c("R_FtpDataManager", new Object[]{"getFtpLacRecord data:", str2});
                resultCallback.onSuccess(str2);
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("R_FtpDataManager", new Object[]{"getFtpLacRecord onFailure:", ExceptionUtils.a(th)});
                resultCallback.onFailure(th);
            }
        });
    }
}
