package defpackage;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hihealth.HiUserPreference;
import com.huawei.hihealth.api.HiHealthManager;
import com.huawei.hihealth.util.HiJsonUtil;
import com.huawei.hwbasemgr.IBaseResponseCallback;
import com.huawei.hwcloudmodel.model.unite.GetLacticStatisticsReq;
import com.huawei.hwcloudmodel.model.unite.GetLacticStatisticsRsp;
import com.huawei.hwcloudmodel.model.unite.GetLatestLacticReq;
import com.huawei.hwcloudmodel.model.unite.GetLatestLacticRsp;
import com.huawei.hwcloudmodel.model.unite.GetLoadPeakReq;
import com.huawei.hwcloudmodel.model.unite.GetLoadPeakRsp;
import com.huawei.hwcloudmodel.model.unite.GetRunLevelReq;
import com.huawei.hwcloudmodel.model.unite.GetRunLevelRsp;
import com.huawei.hwcloudmodel.model.unite.LacticBasic;
import com.huawei.hwcloudmodel.model.unite.LacticData;
import com.huawei.hwcloudmodel.model.unite.LacticStatistics;
import com.huawei.hwcloudmodel.model.unite.RunLevelDetail;
import com.huawei.hwcloudmodel.model.unite.UserRunLevelData;
import com.huawei.hwcommonmodel.utils.DateFormatUtil;
import com.huawei.networkclient.ResultCallback;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import health.compact.a.util.LogUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes10.dex */
public class kro {
    private boolean e;
    private hqy c = new hqy();
    private HiUserPreference d = new HiUserPreference();
    private Context a = BaseApplication.b();

    /* JADX INFO: Access modifiers changed from: private */
    public void a(GetRunLevelRsp getRunLevelRsp) {
        if (getRunLevelRsp == null) {
            ReleaseLogUtil.d("Track_RQDataManager", "getRqData runLevelRsp is null");
            return;
        }
        Map<Integer, RunLevelDetail> data = getRunLevelRsp.getData();
        UserRunLevelData userRunLevelData = getRunLevelRsp.getUserRunLevelData();
        if (data == null || userRunLevelData == null) {
            ReleaseLogUtil.d("Track_RQDataManager", "runLevelDetails or userRunLevelData is null");
            return;
        }
        this.c.e(data);
        this.c.b(userRunLevelData);
        this.c.a((int) (System.currentTimeMillis() / 1000));
        ReleaseLogUtil.c("Track_RQDataManager", "runLevelRsp convert to rqdata success!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LacticBasic c(GetLatestLacticRsp getLatestLacticRsp) {
        List<LacticData> detailInfos = getLatestLacticRsp.getDetailInfos();
        if (okt.a(detailInfos)) {
            return null;
        }
        LacticData lacticData = detailInfos.get(0);
        if (okt.a(lacticData.getSamplePoints())) {
            return null;
        }
        return (LacticBasic) HiJsonUtil.d(lacticData.getSamplePoints().get(0).getValue(), LacticBasic.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<LacticStatistics> d(GetLacticStatisticsRsp getLacticStatisticsRsp, int i) {
        Map statisticTotal = getLacticStatisticsRsp.getStatisticTotal();
        List<LacticStatistics> arrayList = new ArrayList<>();
        for (Map.Entry entry : statisticTotal.entrySet()) {
            if (((Integer) entry.getKey()).intValue() == i) {
                arrayList = (List) entry.getValue();
            }
        }
        return arrayList;
    }

    public void a(final Context context, final int i) {
        if (context == null) {
            ReleaseLogUtil.d("Track_RQDataManager", "pushRQDataToDevice: context is null");
        } else {
            mnj.e(new Runnable() { // from class: kro.4
                @Override // java.lang.Runnable
                public void run() {
                    if (kro.this.e) {
                        ReleaseLogUtil.c("Track_RQDataManager", "RqData has been pushed!");
                        return;
                    }
                    GetRunLevelReq getRunLevelReq = new GetRunLevelReq();
                    LogUtil.d("Track_RQDataManager", "testInitData: ", Integer.valueOf(i));
                    getRunLevelReq.setStartDay(i);
                    getRunLevelReq.setEndDay(i);
                    getRunLevelReq.setQueryType(0);
                    HashSet hashSet = new HashSet(16);
                    hashSet.add(1);
                    hashSet.add(2);
                    hashSet.add(3);
                    hashSet.add(4);
                    hashSet.add(5);
                    hashSet.add(6);
                    getRunLevelReq.setTypes(hashSet);
                    getRunLevelReq.setTimeZone(iwb.d());
                    LogUtil.d("Track_RQDataManager", getRunLevelReq.toString());
                    mlc.b(context).b(getRunLevelReq, new ResultCallback<GetRunLevelRsp>() { // from class: kro.4.3
                        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
                        public void onSuccess(GetRunLevelRsp getRunLevelRsp) {
                            ReleaseLogUtil.c("Track_RQDataManager", "pushRqDataToDevice: GetRunLevelRsp success");
                            kro.this.a(getRunLevelRsp);
                            if (kro.this.c != null) {
                                kro.this.e(context, i, kro.this.c);
                            } else {
                                ReleaseLogUtil.d("Track_RQDataManager", "Failed to parse rqData");
                            }
                        }

                        public void onFailure(Throwable th) {
                            ReleaseLogUtil.d("Track_RQDataManager", "getRunLevelDetail: request fail");
                            if (TextUtils.isEmpty(th.getMessage())) {
                                ReleaseLogUtil.d("Track_RQDataManager", "getRunLevelDetail: request fail and throwable.getMessage is null");
                            }
                        }
                    });
                }
            });
        }
    }

    public void a(Context context, int i, int i2, int i3, final IBaseResponseCallback iBaseResponseCallback) {
        if (context == null || iBaseResponseCallback == null) {
            return;
        }
        GetRunLevelReq getRunLevelReq = new GetRunLevelReq();
        getRunLevelReq.setStartDay(i);
        getRunLevelReq.setEndDay(i2);
        getRunLevelReq.setQueryType(i3);
        HashSet hashSet = new HashSet();
        hashSet.add(1);
        hashSet.add(2);
        hashSet.add(3);
        hashSet.add(4);
        hashSet.add(5);
        hashSet.add(6);
        getRunLevelReq.setTypes(hashSet);
        getRunLevelReq.setTimeZone(iwb.d());
        LogUtil.d("Track_RQDataManager", getRunLevelReq.toString());
        mlc.b(context.getApplicationContext()).b(getRunLevelReq, new ResultCallback<GetRunLevelRsp>(this) { // from class: kro.1
            /* JADX INFO: renamed from: d, reason: merged with bridge method [inline-methods] */
            public void onSuccess(GetRunLevelRsp getRunLevelRsp) {
                if (getRunLevelRsp == null) {
                    ReleaseLogUtil.a("getRunLevel, resp is null", new Object[0]);
                    return;
                }
                LogUtil.d("Track_RQDataManager", getRunLevelRsp.toString());
                if (getRunLevelRsp.getUserRunLevelData() != null) {
                    ReleaseLogUtil.c("Track_RQDataManager", getRunLevelRsp.printString());
                } else {
                    ReleaseLogUtil.a("Track_RQDataManager", "userRunLevel is null");
                }
                if (getRunLevelRsp.getResultCode().intValue() == 0) {
                    iBaseResponseCallback.a(200, getRunLevelRsp);
                } else {
                    iBaseResponseCallback.a(2, getRunLevelRsp);
                }
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("Track_RQDataManager", "getRunlevel error:", th.getMessage());
                iBaseResponseCallback.a(2, null);
            }
        });
    }

    public void a(Context context, final IBaseResponseCallback iBaseResponseCallback) {
        if (context == null || iBaseResponseCallback == null) {
            LogUtil.b("Track_RQDataManager", "getLatestLacticData context: ", context, ", callback: ", iBaseResponseCallback);
            return;
        }
        GetLatestLacticReq getLatestLacticReq = new GetLatestLacticReq();
        getLatestLacticReq.setTimestamp(System.currentTimeMillis());
        getLatestLacticReq.setCounts(1);
        getLatestLacticReq.setType(300012);
        getLatestLacticReq.setDataType(0);
        LogUtil.d("Track_RQDataManager", getLatestLacticReq.toString());
        mlc.b(context.getApplicationContext()).b(getLatestLacticReq, new ResultCallback<GetLatestLacticRsp>() { // from class: kro.3
            /* JADX INFO: renamed from: d, reason: merged with bridge method [inline-methods] */
            public void onSuccess(GetLatestLacticRsp getLatestLacticRsp) {
                if (getLatestLacticRsp == null) {
                    onFailure(new Throwable("response is null"));
                    return;
                }
                LogUtil.d("Track_RQDataManager", "getLatestLacticData response: ", getLatestLacticRsp.toString());
                iBaseResponseCallback.a(getLatestLacticRsp.getResultCode().intValue(), kro.this.c(getLatestLacticRsp));
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("Track_RQDataManager", "getLatestLacticData error:", th.getMessage());
                iBaseResponseCallback.a(2, null);
            }
        });
    }

    public void b(Context context, long j, long j2, final int i, final IBaseResponseCallback iBaseResponseCallback) {
        if (context == null || iBaseResponseCallback == null) {
            LogUtil.b("Track_RQDataManager", "getLacticStatisticsData context: ", context, ", callback: ", iBaseResponseCallback);
            return;
        }
        GetLacticStatisticsReq getLacticStatisticsReq = new GetLacticStatisticsReq();
        getLacticStatisticsReq.setStartTime(DateFormatUtil.c(j));
        getLacticStatisticsReq.setEndTime(DateFormatUtil.c(j2));
        getLacticStatisticsReq.setType(new int[]{i});
        LogUtil.d("Track_RQDataManager", getLacticStatisticsReq.toString());
        mlc.b(context.getApplicationContext()).e(getLacticStatisticsReq, new ResultCallback<GetLacticStatisticsRsp>(this) { // from class: kro.7
            /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
            public void onSuccess(GetLacticStatisticsRsp getLacticStatisticsRsp) {
                if (getLacticStatisticsRsp == null) {
                    onFailure(new Throwable("response is null"));
                    return;
                }
                LogUtil.d("Track_RQDataManager", "getLacticStatisticsData response: ", getLacticStatisticsRsp.toString());
                if (getLacticStatisticsRsp.getStatisticTotal() == null) {
                    onFailure(new Throwable("data is null"));
                } else {
                    iBaseResponseCallback.a(getLacticStatisticsRsp.getResultCode().intValue(), kro.d(getLacticStatisticsRsp, i));
                }
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("Track_RQDataManager", "getLacticStatisticsData error:", th.getMessage());
                iBaseResponseCallback.a(2, null);
            }
        });
    }

    public void c(Context context, long j, long j2, int i, final IBaseResponseCallback iBaseResponseCallback) {
        if (context == null || iBaseResponseCallback == null) {
            LogUtil.b("Track_RQDataManager", "getLoadPeakData context: ", context, ", callback: ", iBaseResponseCallback);
            return;
        }
        GetLoadPeakReq getLoadPeakReq = new GetLoadPeakReq();
        getLoadPeakReq.setStartDay(j);
        getLoadPeakReq.setEndDay(j2);
        getLoadPeakReq.setType(i);
        getLoadPeakReq.setDataType(2);
        getLoadPeakReq.setDeviceCode(0);
        LogUtil.d("Track_RQDataManager", getLoadPeakReq.toString());
        mlc.b(context.getApplicationContext()).a(getLoadPeakReq, new ResultCallback<GetLoadPeakRsp>(this) { // from class: kro.2
            /* JADX INFO: renamed from: e, reason: merged with bridge method [inline-methods] */
            public void onSuccess(GetLoadPeakRsp getLoadPeakRsp) {
                if (getLoadPeakRsp == null) {
                    onFailure(new Throwable("response is null"));
                    return;
                }
                LogUtil.d("Track_RQDataManager", "GetLoadPeakRsp response: ", getLoadPeakRsp.toString());
                if (getLoadPeakRsp.getData() == null) {
                    onFailure(new Throwable("data is null"));
                } else {
                    iBaseResponseCallback.a(getLoadPeakRsp.getResultCode().intValue(), getLoadPeakRsp);
                }
            }

            public void onFailure(Throwable th) {
                ReleaseLogUtil.d("Track_RQDataManager", "getLoadPeakData error:", th.getMessage());
                iBaseResponseCallback.a(2, null);
            }
        });
    }

    public void e(Context context, int i, hqy hqyVar) {
        if (hqyVar == null) {
            ReleaseLogUtil.a("Track_RQDataManager", "pushToDevice rqData is null");
            return;
        }
        RunLevelDetail runLevelDetail = (RunLevelDetail) hqyVar.e().get(Integer.valueOf(i));
        UserRunLevelData userRunLevelDataC = hqyVar.c();
        if (runLevelDetail == null || userRunLevelDataC == null) {
            ReleaseLogUtil.d("Track_RQDataManager", "runLevelDetail or userRunLevelData is null");
        } else {
            krm.e(context).e(hqyVar, i, false, new IBaseResponseCallback() { // from class: kro.5
                @Override // com.huawei.hwbasemgr.IBaseResponseCallback
                /* JADX INFO: renamed from: onResponse */
                public void a(int i2, Object obj) {
                    LogUtil.d("Track_RQDataManager", "mDeviceResponseCallback errorCode = ", Integer.valueOf(i2));
                    if (i2 != 0) {
                        ReleaseLogUtil.c("Track_RQDataManager", "mDeviceResponseCallback something error");
                        return;
                    }
                    ReleaseLogUtil.c("Track_RQDataManager", "mDeviceResponseCallback success");
                    kro.this.d.setKey("track.rq_config");
                    HashMap map = new HashMap();
                    map.put("isSyncOrNot", Boolean.toString(true));
                    kro.this.d.setValue(qod.c(map));
                    HiHealthManager.e(kro.this.a).setUserPreference(kro.this.d);
                }
            });
        }
    }
}
