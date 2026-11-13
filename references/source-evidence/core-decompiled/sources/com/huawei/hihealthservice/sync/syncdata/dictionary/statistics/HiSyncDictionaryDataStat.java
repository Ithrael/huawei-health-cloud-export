package com.huawei.hihealthservice.sync.syncdata.dictionary.statistics;

import android.content.Context;
import com.huawei.hihealth.HiHealthData;
import com.huawei.hihealth.HiSyncOption;
import com.huawei.hihealth.data.type.HealthSyncCategory;
import com.huawei.hihealth.dictionary.HiHealthDictManager;
import com.huawei.hihealth.dictionary.model.HiHealthDictStat;
import com.huawei.hihealth.dictionary.model.HiHealthDictType;
import com.huawei.hihealth.dictionary.utils.DicDataTypeUtil$DataType;
import com.huawei.hihealth.util.HiJsonUtil;
import com.huawei.hihealthservice.sync.dataswitch.HealthStatisticsSwitch;
import com.huawei.hihealthservice.sync.syncdata.HiSyncBase;
import com.huawei.hihealthservice.sync.syncdata.dictionary.statistics.HealthStatisticsDownloadByVersionReq;
import com.huawei.hwcloudmodel.healthdatacloud.HealthDataCloudFactory;
import com.huawei.hwcloudmodel.model.unite.SyncKey;
import com.huawei.hwlogsmodel.LogUtil;
import defpackage.lob;
import defpackage.lqb;
import defpackage.lre;
import defpackage.lzt;
import defpackage.mbx;
import defpackage.mck;
import defpackage.mcm;
import defpackage.mcv;
import defpackage.mcx;
import defpackage.mlc;
import defpackage.okt;
import defpackage.pnq;
import health.compact.a.CommonUtil;
import health.compact.a.HiCommonUtil;
import health.compact.a.HiDateUtil;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes10.dex */
public class HiSyncDictionaryDataStat implements HiSyncBase {
    private static final String TAG = "HiH_HiSyncDicDaStat";
    private Context mContext;
    private HealthStatisticsSwitch mConverter;
    private int mMainUserId;
    private HealthDataCloudFactory mParamsFactory;
    private HiSyncOption mSyncOption;
    private int mUpLoadFailCount = 0;
    private int mUploadCount = 0;
    private lre mSyncTypeManager = lre.d();
    private Set<Integer> mDownloadedTypes = new HashSet();
    private lqb mDataClientManager = lqb.a();
    private boolean mIsOverSea = mcx.h();

    public HiSyncDictionaryDataStat(Context context, HiSyncOption hiSyncOption, int i) {
        this.mContext = context;
        this.mSyncOption = hiSyncOption;
        this.mMainUserId = i;
        this.mParamsFactory = mlc.b(context).a();
        this.mConverter = new HealthStatisticsSwitch(this.mContext);
        List<Integer> arrayList = new ArrayList<>(1);
        if (HiHealthDictManager.d(this.mContext).c(this.mSyncOption.getSyncDataType()) != null) {
            arrayList.add(Integer.valueOf(this.mSyncOption.getSyncDataType()));
        } else {
            arrayList = HiHealthDictManager.d(this.mContext).c();
        }
        if (okt.a(arrayList)) {
            ReleaseLogUtil.a(TAG, "allTypes is empty!");
            return;
        }
        for (Integer num : arrayList) {
            int iIntValue = num.intValue();
            if (iIntValue != DicDataTypeUtil$DataType.ACTIVE_HOUR.value() && iIntValue != DicDataTypeUtil$DataType.SPORT_GOAL_ACHIEVEMENT_DATA.value()) {
                HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(iIntValue);
                if (hiHealthDictTypeC == null) {
                    ReleaseLogUtil.a(TAG, "dictTp=null,tPId=", num);
                } else if (hiHealthDictTypeC.b() == 0 || hiHealthDictTypeC.b() == 2) {
                    this.mDownloadedTypes.add(num);
                }
            }
        }
    }

    private long downOneTypeDataOnce(HealthStatisticsDownloadByVersionReq healthStatisticsDownloadByVersionReq) throws mcm {
        HealthStatisticsDownloadByVersionRsp healthStatisticsDownloadByVersionRsp = (HealthStatisticsDownloadByVersionRsp) pnq.c().a(healthStatisticsDownloadByVersionReq.getUrl(), this.mParamsFactory.getHeaders(), pob.a(this.mParamsFactory.getBody(healthStatisticsDownloadByVersionReq)), HealthStatisticsDownloadByVersionRsp.class);
        if (!mck.a(healthStatisticsDownloadByVersionRsp, false) || healthStatisticsDownloadByVersionRsp.getCurrentVersion() <= healthStatisticsDownloadByVersionReq.getVersion()) {
            return -1L;
        }
        saveHealthStatistics(healthStatisticsDownloadByVersionRsp.getData());
        return healthStatisticsDownloadByVersionRsp.getCurrentVersion();
    }

    private void downloadStatByVersion(List<SyncKey> list) throws mcm {
        long j;
        ReleaseLogUtil.c(TAG, "downloadStatByVersion syncKeys: ", HiJsonUtil.a(list));
        long jCurrentTimeMillis = System.currentTimeMillis();
        boolean zJ = mcx.j();
        for (SyncKey syncKey : list) {
            Long version = syncKey.getVersion();
            long jLongValue = version.longValue();
            int iIntValue = syncKey.getType().intValue();
            long currentVersion = getCurrentVersion(iIntValue, zJ, jCurrentTimeMillis);
            if (currentVersion < jLongValue) {
                HealthStatisticsDownloadByVersionReq healthStatisticsDownloadByVersionReqBuild = new HealthStatisticsDownloadByVersionReq.Builder().dataSource(2).types(iIntValue).version(currentVersion).dataType(2).build();
                int i = 0;
                while (true) {
                    LogUtil.d(TAG, "downloadStat cur = ", Long.valueOf(currentVersion), " max = ", version);
                    long jDownOneTypeDataOnce = downOneTypeDataOnce(healthStatisticsDownloadByVersionReqBuild);
                    int i2 = i + 1;
                    if (jDownOneTypeDataOnce <= -1) {
                        j = jCurrentTimeMillis;
                        break;
                    }
                    j = jCurrentTimeMillis;
                    HealthStatisticsDownloadByVersionReq healthStatisticsDownloadByVersionReq = healthStatisticsDownloadByVersionReqBuild;
                    if (!this.mSyncTypeManager.c(this.mMainUserId, iIntValue, jDownOneTypeDataOnce, 0L, HealthSyncCategory.SyncCategory.STAT.getValue())) {
                        LogUtil.g(TAG, "downloadStat saveVersionToDB failed ");
                        break;
                    }
                    currentVersion = jDownOneTypeDataOnce;
                    healthStatisticsDownloadByVersionReq.setVersion(currentVersion);
                    if (!zJ) {
                        i = i2;
                        if (i >= 20) {
                            ReleaseLogUtil.a(TAG, "downloadStat too many times");
                            break;
                        }
                    } else {
                        i = i2;
                    }
                    if (currentVersion >= jLongValue) {
                        break;
                    }
                    healthStatisticsDownloadByVersionReqBuild = healthStatisticsDownloadByVersionReq;
                    jCurrentTimeMillis = j;
                }
                jCurrentTimeMillis = j;
            }
        }
        mcv.c(this.mContext);
        ReleaseLogUtil.c(TAG, "downloadStatByVersion() end!");
    }

    private long getCurrentVersion(int i, boolean z, long j) {
        lob lobVarB = this.mSyncTypeManager.b(this.mMainUserId, 0L, i, HealthSyncCategory.SyncCategory.STAT.getValue());
        if (lobVarB != null) {
            return lobVarB.h();
        }
        if (z) {
            return 0L;
        }
        return HiDateUtil.d(j, TimeZone.getDefault(), -10);
    }

    private String[] getStatFieldNameByStatType(List<Integer> list) {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            HiHealthDictStat hiHealthDictStatA = HiHealthDictManager.d(this.mContext).a(it.next().intValue());
            if (hiHealthDictStatA != null) {
                arrayList.add(hiHealthDictStatA.b());
            }
        }
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = (String) arrayList.get(i);
        }
        return strArr;
    }

    private void saveHealthStatistics(Map<String, List<HealthStatistics>> map) {
        for (Map.Entry<String, List<HealthStatistics>> entry : map.entrySet()) {
            String key = entry.getKey();
            if (HiHealthDictManager.d(this.mContext).c(CommonUtil.h(key)) == null) {
                ReleaseLogUtil.a(TAG, key, " is not exist in dictionary configure file");
            } else {
                List<HealthStatistics> value = entry.getValue();
                if (okt.a(value)) {
                    ReleaseLogUtil.a(TAG, key, " no value");
                } else {
                    ReleaseLogUtil.c(TAG, "result code = ", Integer.valueOf(lzt.b(this.mContext).transferHealthStatData(this.mConverter.e(CommonUtil.h(key), value, this.mMainUserId))));
                }
            }
        }
    }

    private boolean uploadHealthStatistics(List<HiHealthData> list, int[] iArr, int i) throws mcm {
        if (this.mIsOverSea) {
            int i2 = this.mUploadCount + 1;
            this.mUploadCount = i2;
            if (i2 > 3) {
                this.mUpLoadFailCount += 2;
                return false;
            }
        } else {
            int i3 = this.mUploadCount + 1;
            this.mUploadCount = i3;
            mcx.e(i3, this.mSyncOption.getSyncManual());
        }
        Map<String, List<HealthStatistics>> mapA = this.mConverter.a(list, iArr);
        if (mapA.isEmpty()) {
            this.mUpLoadFailCount++;
            return false;
        }
        HealthStatisticsUploadReq healthStatisticsUploadReq = new HealthStatisticsUploadReq(mapA, list.get(0).getTimeZone());
        while (true) {
            if (this.mUpLoadFailCount >= 2) {
                ReleaseLogUtil.c(TAG, "upload failed! uploadCount: ", Integer.valueOf(this.mUploadCount), " types: ", Arrays.toString(iArr));
                LogUtil.e(TAG, "stat is ", CommonUtil.ao() ? HiJsonUtil.a(mapA) : null);
                return false;
            }
            HealthStatisticsUploadRsp healthStatisticsUploadRsp = (HealthStatisticsUploadRsp) pnq.c().a(healthStatisticsUploadReq.getUrl(), this.mParamsFactory.getHeaders(), pob.a(this.mParamsFactory.getBody(healthStatisticsUploadReq)), HealthStatisticsUploadRsp.class);
            if (mck.a(healthStatisticsUploadRsp, false)) {
                if (!this.mSyncTypeManager.c(this.mMainUserId, i, healthStatisticsUploadRsp.getVersion(), 0L, HealthSyncCategory.SyncCategory.STAT.getValue())) {
                    ReleaseLogUtil.a(TAG, "uploadStat saveVersionToDB failed!");
                }
                ReleaseLogUtil.c(TAG, "upload success! uploadCount: ", Integer.valueOf(this.mUploadCount), " types: ", Arrays.toString(iArr));
                LogUtil.e(TAG, "stat is ", CommonUtil.ao() ? HiJsonUtil.a(mapA) : null);
                return true;
            }
            this.mUpLoadFailCount++;
        }
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pullDataByTime(long j, long j2) throws mcm {
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pullDataByVersion() throws mcm {
        ReleaseLogUtil.c(TAG, "pullDataByVersion() begin!");
        mcv.a(1.0d, "SYNC_DICT_STAT_DOWNLOAD_PERCENT_MAX");
        if (this.mDownloadedTypes.isEmpty()) {
            ReleaseLogUtil.c(TAG, "mDownloadedTypes is empty pullDataByVersion() end");
            mcv.c(this.mContext);
            return;
        }
        List<SyncKey> listC = mbx.d(this.mContext).c(new ArrayList(this.mDownloadedTypes));
        if (HiCommonUtil.a((List) listC)) {
            ReleaseLogUtil.a(TAG, "dictionary stat not exist in cloud");
            mcv.c(this.mContext);
        } else {
            downloadStatByVersion(listC);
            mcv.c(this.mContext);
            ReleaseLogUtil.c(TAG, "pullDataByVersion() end!");
        }
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pushData() throws mcm {
        ReleaseLogUtil.c(TAG, "pushData begin");
        if (!mao.o()) {
            ReleaseLogUtil.a(TAG, "dataPrivacy switch is closed ,can not pushData right now ,push end!");
            return;
        }
        int iL = this.mDataClientManager.l(this.mMainUserId);
        if (iL <= 0) {
            ReleaseLogUtil.a(TAG, "no statClient get, maybe no data need to push ,push end !");
            return;
        }
        List<Integer> arrayList = new ArrayList<>(1);
        if (HiHealthDictManager.d(this.mContext).c(this.mSyncOption.getSyncDataType()) != null) {
            arrayList.add(Integer.valueOf(this.mSyncOption.getSyncDataType()));
        } else {
            arrayList = HiHealthDictManager.d(this.mContext).c();
        }
        if (okt.a(arrayList)) {
            ReleaseLogUtil.a(TAG, "no dictionary stat data need to push ,push end !");
            return;
        }
        for (Integer num : arrayList) {
            int iIntValue = num.intValue();
            if (iIntValue != DicDataTypeUtil$DataType.ACTIVE_HOUR.value() && iIntValue != DicDataTypeUtil$DataType.SPORT_GOAL_ACHIEVEMENT_DATA.value()) {
                HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(iIntValue);
                if (hiHealthDictTypeC == null) {
                    ReleaseLogUtil.a(TAG, "dictTp=null,tpId=", num);
                } else if (hiHealthDictTypeC.b() != 1 && hiHealthDictTypeC.b() != 3) {
                    List<String> listK = HiHealthDictManager.d(this.mContext).k(iIntValue);
                    if (HiCommonUtil.a((List) listK)) {
                        ReleaseLogUtil.a(TAG, "tpId=", num, " no statDt");
                    } else {
                        ArrayList arrayList2 = new ArrayList(listK.size());
                        ArrayList arrayList3 = new ArrayList(listK.size());
                        Iterator<String> it = listK.iterator();
                        while (it.hasNext()) {
                            int iC = HiHealthDictManager.d(this.mContext).c(iIntValue, it.next(), 0);
                            if (iC != 0) {
                                arrayList2.add(Integer.valueOf(iC));
                                if (HiHealthDictManager.d(this.mContext).r(iC)) {
                                    arrayList3.add(Integer.valueOf(iC));
                                }
                            }
                        }
                        int i = this.mIsOverSea;
                        int[] iArr = {50, i};
                        int[] iArrB = i != 0 ? CommonUtil.b(arrayList3) : CommonUtil.b(arrayList2);
                        String[] statFieldNameByStatType = this.mIsOverSea ? getStatFieldNameByStatType(arrayList3) : getStatFieldNameByStatType(arrayList2);
                        while (true) {
                            if (this.mUpLoadFailCount >= 2) {
                                break;
                            }
                            List<HiHealthData> listE = mcx.e(this.mContext, iL, iArrB, statFieldNameByStatType, iArr);
                            if (okt.a(listE)) {
                                ReleaseLogUtil.a(TAG, "noNeed statData push");
                                break;
                            } else if (uploadHealthStatistics(listE, iArrB, iIntValue)) {
                                mcx.b(this.mContext, listE, iArrB, iL);
                            }
                        }
                        this.mUpLoadFailCount = 0;
                    }
                }
            }
        }
        ReleaseLogUtil.c(TAG, "pushData end");
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
