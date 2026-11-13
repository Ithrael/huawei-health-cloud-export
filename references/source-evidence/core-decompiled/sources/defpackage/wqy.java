package defpackage;

import android.content.Context;
import android.text.format.DateFormat;
import com.google.gson.JsonSyntaxException;
import com.huawei.hihealth.HiAggregateOption;
import com.huawei.hihealth.HiDataReadOption;
import com.huawei.hihealth.HiHealthData;
import com.huawei.hihealth.HiTimeInterval;
import com.huawei.hihealth.api.HiHealthManager;
import com.huawei.hihealth.data.listener.HiAggregateListener;
import com.huawei.hwbasemgr.IBaseResponseCallback;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.ui.commonui.base.CommonUiBaseResponse;
import health.compact.a.HiCommonUtil;
import health.compact.a.Utils;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes20.dex */
public class wqy {
    private ArrayList<wqz> b;

    static class c implements IBaseResponseCallback {
        private CommonUiBaseResponse a;
        private WeakReference<wqy> d;
        private long e;

        protected c(wqy wqyVar, long j, CommonUiBaseResponse commonUiBaseResponse) {
            this.e = j;
            this.a = commonUiBaseResponse;
            this.d = new WeakReference<>(wqyVar);
        }

        @Override // com.huawei.hwbasemgr.IBaseResponseCallback
        public void onResponse(int i, Object obj) {
            LogUtil.d("UIHLH_HealthDataInteractor", "readHeartRateData time = ", Long.valueOf(System.currentTimeMillis() - this.e));
            wqy wqyVar = this.d.get();
            if (wqyVar != null) {
                wqyVar.d(obj, this.a);
            }
        }
    }

    static class e {
        private static final wqy b = new wqy();
    }

    private wqy() {
        this.b = new ArrayList<>(31);
    }

    public static wqy a() {
        return e.b;
    }

    static /* synthetic */ void a(IBaseResponseCallback iBaseResponseCallback, long j, int i, Object obj) {
        if (!(obj instanceof List)) {
            iBaseResponseCallback.onResponse(100001, null);
            return;
        }
        List<HiHealthData> list = (List) obj;
        health.compact.a.util.LogUtil.d("UIHLH_HealthDataInteractor", "getSportRecords hasData, total count = ", Integer.valueOf(list.size()));
        ArrayList arrayList = new ArrayList(16);
        for (HiHealthData hiHealthData : list) {
            wgj wgjVar = new wgj();
            try {
                wgjVar = (wgj) omi.e(hiHealthData.getMetaData(), wgj.class);
            } catch (JsonSyntaxException unused) {
                health.compact.a.util.LogUtil.c("UIHLH_HealthDataInteractor", "getSportRecord JsonUtil fromJson fail");
            }
            wgjVar.setStartTime(hiHealthData.getStartTime());
            wgjVar.setEndTime(hiHealthData.getEndTime());
            wgjVar.c(j);
            arrayList.add(wgjVar);
        }
        iBaseResponseCallback.onResponse(0, arrayList);
    }

    private void a(List<HiHealthData> list, CommonUiBaseResponse commonUiBaseResponse) {
        LogUtil.d("UIHLH_HealthDataInteractor", "handleTemperatureData enter");
        long jCurrentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList(31);
        if (list == null || list.size() <= 0) {
            LogUtil.d("UIHLH_HealthDataInteractor", "handleTemperatureData data is null or empty");
        } else {
            LogUtil.d("UIHLH_HealthDataInteractor", "handleTemperatureData data.size = ", Integer.valueOf(list.size()));
            for (HiHealthData hiHealthData : list) {
                if (hiHealthData.getEndTime() <= jCurrentTimeMillis) {
                    double value = hiHealthData.getValue();
                    long startTime = hiHealthData.getStartTime();
                    int i = hiHealthData.getInt("trackdata_deviceType");
                    int type = hiHealthData.getType();
                    wqz wqzVar = new wqz();
                    wqzVar.d(value);
                    wqzVar.a(startTime);
                    wqzVar.b(i);
                    wqzVar.h(type);
                    if (Math.abs(wqzVar.k()) >= 0.0d) {
                        arrayList.add(wqzVar);
                    } else {
                        LogUtil.g("UIHLH_HealthDataInteractor", "invalid temperature value");
                    }
                }
            }
        }
        if (commonUiBaseResponse != null) {
            commonUiBaseResponse.onResponse(0, arrayList);
        }
        LogUtil.d("UIHLH_HealthDataInteractor", "handleTemperatureData cost of time = ", Long.valueOf(System.currentTimeMillis() - jCurrentTimeMillis));
    }

    private void a(List<wqz> list, wqz wqzVar) {
        int size = list.size();
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                break;
            }
            if (list.get(i).k() > wqzVar.k()) {
                size = i;
                break;
            }
            i++;
        }
        list.add(size, wqzVar);
    }

    private static HiAggregateOption b() {
        HiAggregateOption hiAggregateOption = new HiAggregateOption();
        hiAggregateOption.setType(new int[]{2006, 2007});
        hiAggregateOption.setConstantsKey(new String[]{"BLOOD_PRESSURE_SYSTOLIC", "BLOOD_PRESSURE_DIASTOLIC"});
        hiAggregateOption.setReadType(0);
        hiAggregateOption.setAggregateType(1);
        hiAggregateOption.setGroupUnitType(0);
        hiAggregateOption.setSortOrder(1);
        return hiAggregateOption;
    }

    private void b(Object obj, CommonUiBaseResponse commonUiBaseResponse) {
        synchronized (this) {
            LogUtil.d("UIHLH_HealthDataInteractor", "handleWeightData enter");
            List<HiHealthData> list = (List) obj;
            ArrayList arrayList = new ArrayList(31);
            if (HiCommonUtil.a(list)) {
                LogUtil.d("UIHLH_HealthDataInteractor", "handleWeightData enter datas is null");
                if (commonUiBaseResponse != null) {
                    commonUiBaseResponse.onResponse(0, arrayList);
                }
                return;
            }
            LogUtil.d("UIHLH_HealthDataInteractor", "handleWeightData datas.size = ", Integer.valueOf(list.size()));
            ArrayList<wqz> arrayList2 = new ArrayList<>(31);
            for (HiHealthData hiHealthData : list) {
                if (hiHealthData.getDouble("bodyWeight") > 0.0d) {
                    arrayList2.add(wrc.c(hiHealthData));
                } else {
                    LogUtil.g("UIHLH_HealthDataInteractor", "illegal weight, timeStamp ", Long.valueOf(hiHealthData.getStartTime()));
                }
            }
            d(arrayList2, arrayList);
            if (commonUiBaseResponse != null) {
                commonUiBaseResponse.onResponse(0, arrayList);
            }
            LogUtil.d("UIHLH_HealthDataInteractor", "handleWeightData end");
        }
    }

    private void c(ArrayList<wqz> arrayList, List<ArrayList<wqz>> list) {
        ArrayList arrayList2 = new ArrayList(31);
        for (wqz wqzVar : arrayList) {
            if (arrayList2.contains(b(wqzVar.j()))) {
                list.get(arrayList2.indexOf(b(wqzVar.j()))).add(wqzVar);
            } else {
                ArrayList<wqz> arrayList3 = new ArrayList<>(31);
                arrayList3.add(wqzVar);
                arrayList2.add(b(wqzVar.j()));
                list.add(arrayList3);
            }
        }
    }

    private List<ArrayList<wqz>> d(ArrayList<wqz> arrayList) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        TreeMap treeMap = new TreeMap(new Comparator() { // from class: wrf
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((Long) obj2).compareTo((Long) obj);
            }
        });
        for (wqz wqzVar : arrayList) {
            try {
                long time = simpleDateFormat.parse(simpleDateFormat.format(Long.valueOf(wqzVar.j()))).getTime();
                ArrayList arrayList2 = (ArrayList) treeMap.get(Long.valueOf(time));
                if (arrayList2 == null) {
                    ArrayList arrayList3 = new ArrayList();
                    treeMap.put(Long.valueOf(time), arrayList3);
                    arrayList3.add(wqzVar);
                } else {
                    a(arrayList2, wqzVar);
                }
            } catch (ParseException e2) {
                LogUtil.a("UIHLH_HealthDataInteractor", e2);
            }
        }
        return new ArrayList(treeMap.values());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(Object obj, CommonUiBaseResponse commonUiBaseResponse) {
        LogUtil.d("UIHLH_HealthDataInteractor", "handlerLastTimeHeartRateData enter");
        if (commonUiBaseResponse != null) {
            commonUiBaseResponse.onResponse(0, obj);
        }
    }

    private void e(ArrayList<wqz> arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size - 1) {
            int i2 = i + 1;
            int size2 = arrayList.size();
            for (int i3 = i2; i3 < size2; i3++) {
                if (arrayList.get(i).j() < arrayList.get(i3).j()) {
                    wqz wqzVar = arrayList.get(i);
                    arrayList.set(i, arrayList.get(i3));
                    arrayList.set(i3, wqzVar);
                }
            }
            i = i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e(List<HiHealthData> list, CommonUiBaseResponse commonUiBaseResponse) {
        LogUtil.d("UIHLH_HealthDataInteractor", "handleBloodsugarData enter");
        long jCurrentTimeMillis = System.currentTimeMillis();
        List<ArrayList<wqz>> arrayList = new ArrayList<>(31);
        ArrayList<wqz> arrayList2 = new ArrayList<>(31);
        if (list == null || list.size() <= 0) {
            LogUtil.d("UIHLH_HealthDataInteractor", "datas null or empty");
        } else {
            LogUtil.d("UIHLH_HealthDataInteractor", "handleBloodSugarData data.size = ", Integer.valueOf(list.size()));
            for (HiHealthData hiHealthData : list) {
                if (hiHealthData.getEndTime() <= jCurrentTimeMillis) {
                    arrayList2.add(wrc.b(hiHealthData));
                }
            }
            arrayList = d(arrayList2);
            LogUtil.d("UIHLH_HealthDataInteractor", "handleBloodSugarData data.size1 = ", Integer.valueOf(arrayList.size()));
        }
        if (commonUiBaseResponse != null) {
            commonUiBaseResponse.onResponse(0, arrayList);
        }
        LogUtil.d("UIHLH_HealthDataInteractor", "handleBloodsugarData time = ", Long.valueOf(System.currentTimeMillis() - jCurrentTimeMillis));
        LogUtil.d("UIHLH_HealthDataInteractor", "handleWeightData end");
    }

    public void a(Context context, long j, long j2, IBaseResponseCallback iBaseResponseCallback) {
        LogUtil.e("UIHLH_HealthDataInteractor", "deleteWeightData ");
        okq.b().b(context, j, j2, iBaseResponseCallback);
    }

    public void a(Context context, List<HiTimeInterval> list, IBaseResponseCallback iBaseResponseCallback) {
        LogUtil.e("UIHLH_HealthDataInteractor", "deleteWeightData ");
        okq.b().e(context, list, iBaseResponseCallback);
    }

    public void a(Context context, long[] jArr, int i, double d, IBaseResponseCallback iBaseResponseCallback) {
        okq.b().e(context, jArr, new double[]{i, d, 0.0d}, iBaseResponseCallback);
    }

    public String b(long j) {
        Date date = new Date();
        date.setTime(j);
        return new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMd")).format(date);
    }

    public void b(IBaseResponseCallback iBaseResponseCallback) {
        okq.b().b(iBaseResponseCallback);
    }

    public void b(final CommonUiBaseResponse commonUiBaseResponse) {
        wwb wwbVarD = wwb.d();
        int[] iArr = {wwbVarD.b(), 2104, wwbVarD.r()};
        if (Utils.k()) {
            iArr = new int[]{wwbVarD.o()};
        }
        HiDataReadOption hiDataReadOption = new HiDataReadOption();
        hiDataReadOption.setSortOrder(1);
        hiDataReadOption.setType(iArr);
        hiDataReadOption.setTimeInterval(0L, System.currentTimeMillis());
        hiDataReadOption.setCount(1);
        wwbVarD.d(iArr, hiDataReadOption, new IBaseResponseCallback() { // from class: wrb
            @Override // com.huawei.hwbasemgr.IBaseResponseCallback
            public final void onResponse(int i, Object obj) {
                this.b.d(commonUiBaseResponse, i, obj);
            }
        });
    }

    public void c(final long j, long j2, final IBaseResponseCallback iBaseResponseCallback) {
        okq.b().a(j, j2, new IBaseResponseCallback() { // from class: wrd
            @Override // com.huawei.hwbasemgr.IBaseResponseCallback
            public final void onResponse(int i, Object obj) {
                wqy.a(iBaseResponseCallback, j, i, obj);
            }
        });
    }

    public void c(long j, IBaseResponseCallback iBaseResponseCallback) {
        LogUtil.e("UIHLH_HealthDataInteractor", "deleteBloodPressureData ");
        ArrayList arrayList = new ArrayList();
        HiTimeInterval hiTimeInterval = new HiTimeInterval();
        hiTimeInterval.setStartTime(j);
        hiTimeInterval.setEndTime(j);
        arrayList.add(hiTimeInterval);
        d(arrayList, iBaseResponseCallback);
    }

    public void c(Context context, int i, long j, long j2, IBaseResponseCallback iBaseResponseCallback) {
        LogUtil.e("UIHLH_HealthDataInteractor", "deleteBloodSuagrData ");
        okq.b().b(context, i, j, j2, iBaseResponseCallback);
    }

    public void c(Context context, long j, long j2, CommonUiBaseResponse commonUiBaseResponse) {
        okq.b().a(context, j, j2, new c(this, System.currentTimeMillis(), commonUiBaseResponse));
    }

    public void c(Context context, long[] jArr, int i, final CommonUiBaseResponse commonUiBaseResponse) {
        if (jArr == null || jArr.length <= 1) {
            return;
        }
        oky.e().e(context, jArr[0], jArr[1], i, new IBaseResponseCallback() { // from class: wra
            @Override // com.huawei.hwbasemgr.IBaseResponseCallback
            public final void onResponse(int i2, Object obj) {
                this.a.e(commonUiBaseResponse, i2, obj);
            }
        });
    }

    public void d(long j, double[] dArr, List<String> list, String str, IBaseResponseCallback iBaseResponseCallback) {
        long[] jArr = {j, j};
        if (list == null) {
            list = new ArrayList<>(0);
        }
        List<String> list2 = list;
        LogUtil.d("UIHLH_HealthDataInteractor", "customActions: ", list2.toString());
        okq.b().e(jArr, dArr, list2, str, iBaseResponseCallback);
    }

    public void d(Context context, long j, long j2, int i, final CommonUiBaseResponse commonUiBaseResponse) {
        final long jCurrentTimeMillis = System.currentTimeMillis();
        okq.b().a(context, j, j2, i, new IBaseResponseCallback() { // from class: wqy.1
            @Override // com.huawei.hwbasemgr.IBaseResponseCallback
            public void onResponse(int i2, Object obj) {
                LogUtil.d("UIHLH_HealthDataInteractor", "readBloodSugarData time = ", Long.valueOf(System.currentTimeMillis() - jCurrentTimeMillis));
                wqy.this.e((List) obj, commonUiBaseResponse);
            }
        });
    }

    public void d(Context context, long j, long j2, CommonUiBaseResponse commonUiBaseResponse) {
        okq.b().d(context, j, j2, new c(this, System.currentTimeMillis(), commonUiBaseResponse));
    }

    public void d(final CommonUiBaseResponse commonUiBaseResponse) {
        if (commonUiBaseResponse == null) {
            LogUtil.g("UIHLH_HealthDataInteractor", "readData callback is null");
            return;
        }
        HiAggregateOption hiAggregateOptionB = b();
        hiAggregateOptionB.setCount(1);
        hiAggregateOptionB.setTimeRange(0L, System.currentTimeMillis());
        HiHealthManager.e(BaseApplication.getContext()).aggregateHiHealthData(hiAggregateOptionB, new HiAggregateListener() { // from class: wqy.2
            @Override // com.huawei.hihealth.data.listener.HiAggregateListener
            public void onResult(List<HiHealthData> list, int i, int i2) {
                if (okt.a(list)) {
                    LogUtil.g("UIHLH_HealthDataInteractor", "datas is empty");
                    commonUiBaseResponse.onResponse(100001, new ArrayList());
                } else {
                    LogUtil.d("UIHLH_HealthDataInteractor", "readData data size = ", Integer.valueOf(list.size()));
                    commonUiBaseResponse.onResponse(0, list);
                }
            }

            @Override // com.huawei.hihealth.data.listener.HiAggregateListener
            public void onResultIntent(int i, List<HiHealthData> list, int i2, int i3) {
            }
        });
    }

    /* synthetic */ void d(CommonUiBaseResponse commonUiBaseResponse, int i, Object obj) {
        a(obj instanceof List ? (List) obj : null, commonUiBaseResponse);
    }

    public void d(ArrayList<wqz> arrayList, List<ArrayList<wqz>> list) {
        list.clear();
        c(arrayList, list);
        Iterator<ArrayList<wqz>> it = list.iterator();
        while (it.hasNext()) {
            e(it.next());
        }
        int i = 0;
        while (i < list.size() - 1) {
            int i2 = i + 1;
            for (int i3 = i2; i3 < list.size(); i3++) {
                if (list.get(i).get(0).j() < list.get(i3).get(0).j()) {
                    ArrayList<wqz> arrayList2 = list.get(i);
                    list.set(i, list.get(i3));
                    list.set(i3, arrayList2);
                }
            }
            i = i2;
        }
    }

    public void d(List<HiTimeInterval> list, IBaseResponseCallback iBaseResponseCallback) {
        LogUtil.e("UIHLH_HealthDataInteractor", "deleteBloodPressureData ");
        okq.b().e(list, iBaseResponseCallback);
    }

    public void e() {
        ArrayList<wqz> arrayList = this.b;
        if (arrayList != null) {
            arrayList.clear();
        } else {
            this.b = new ArrayList<>(31);
        }
    }

    public void e(Context context, long j, long j2, final CommonUiBaseResponse commonUiBaseResponse) {
        okq.b().c(context, j, j2, new IBaseResponseCallback() { // from class: wqy.4
            @Override // com.huawei.hwbasemgr.IBaseResponseCallback
            public void onResponse(int i, Object obj) {
                commonUiBaseResponse.onResponse(0, obj);
            }
        });
    }

    /* synthetic */ void e(CommonUiBaseResponse commonUiBaseResponse, int i, Object obj) {
        b(obj, commonUiBaseResponse);
    }
}
