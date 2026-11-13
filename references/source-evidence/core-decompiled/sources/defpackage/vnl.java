package defpackage;

import com.google.gson.annotations.SerializedName;
import com.huawei.health.ecologydevice.networkclient.HealthEngineRequestManager;
import com.huawei.hwbasemgr.IBaseResponseCallback;
import com.huawei.hwcloudmodel.callback.GrsQueryCallback;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwlogsmodel.LogUtil;
import defpackage.vni;
import health.compact.a.GRSManager;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import java.util.HashSet;
import java.util.Map;

/* JADX INFO: loaded from: classes19.dex */
public class vnl {

    static class d {

        @SerializedName("resultCode")
        private int b;

        @SerializedName("accumulatedReports")
        private vni c;

        @SerializedName("resultDesc")
        private String d;

        private d() {
        }

        public vni c() {
            return this.c;
        }

        public String d() {
            return this.d;
        }

        public int e() {
            return this.b;
        }

        public String toString() {
            return "PerfectDaysResponses{resultCode=" + this.b + ", resultDesc=" + this.d + ", accumulatedReports=" + this.c + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(String str, IBaseResponseCallback iBaseResponseCallback) {
        Map mapAddPublicParams = HealthEngineRequestManager.addPublicParams();
        HashSet hashSet = new HashSet();
        hashSet.add("accumPerfectGoalAchievedDays");
        mapAddPublicParams.put("accumulatedItems", hashSet);
        Map<String, String> mapAddPublicHeaders = HealthEngineRequestManager.addPublicHeaders();
        d dVar = (d) pnq.c().a(str + "/dataQuery/report/getPersonalReport", mapAddPublicHeaders, pob.a(mapAddPublicParams), d.class);
        if (dVar == null || dVar.c() == null) {
            ReleaseLogUtil.d("SCUI_ThreeCirclePerfectDaysHelper", "requestPerformanceBestOfHistory() PerfectDaysResponses is null");
            iBaseResponseCallback.a(-1, null);
        } else if (dVar.e() == 0) {
            vni.a aVarA = dVar.c().a();
            LogUtil.d("SCUI_ThreeCirclePerfectDaysHelper", "pData is ", aVarA);
            iBaseResponseCallback.a(0, aVarA);
        } else {
            ReleaseLogUtil.a("SCUI_ThreeCirclePerfectDaysHelper", "requestPerformanceBestOfHistory fail resultCode is ", Integer.valueOf(dVar.e()), " resultDesc is ", dVar.d());
            iBaseResponseCallback.a(-1, null);
        }
    }

    public void c(final IBaseResponseCallback iBaseResponseCallback) {
        GRSManager.b(BaseApplication.getContext()).d("healthCloudUrl", new GrsQueryCallback() { // from class: vnl.3
            @Override // com.huawei.hwcloudmodel.callback.GrsQueryCallback
            public void onCallBackFail(int i) {
                ReleaseLogUtil.a("SCUI_ThreeCirclePerfectDaysHelper", "requestPerformanceBestOfHistory getUrl failed resultCode = ", Integer.valueOf(i));
                iBaseResponseCallback.a(-1, null);
            }

            @Override // com.huawei.hwcloudmodel.callback.GrsQueryCallback
            public void onCallBackSuccess(String str) {
                LogUtil.d("SCUI_ThreeCirclePerfectDaysHelper", "hostUrl is ", str);
                vnl.this.c(str, iBaseResponseCallback);
            }
        });
    }
}
