package com.huawei.hihealthservice.sync.syncdata.dictionary.detail;

import android.content.Context;
import android.util.SparseArray;
import com.huawei.haf.common.exception.ExceptionUtils;
import com.huawei.hihealth.HiHealthData;
import com.huawei.hihealth.HiSyncOption;
import com.huawei.hihealth.data.type.HealthSyncCategory;
import com.huawei.hihealth.dictionary.HiHealthDictManager;
import com.huawei.hihealth.dictionary.model.HiHealthDictField;
import com.huawei.hihealth.dictionary.model.HiHealthDictType;
import com.huawei.hihealth.dictionary.utils.DicDataTypeUtil$DataType;
import com.huawei.hihealth.util.HiJsonUtil;
import com.huawei.hihealthservice.store.merge.HiDicSequenceMerge;
import com.huawei.hihealthservice.sync.dataswitch.HealthDataSwitch;
import com.huawei.hihealthservice.sync.dataswitch.MotionPathDataSwitch;
import com.huawei.hihealthservice.sync.dataswitch.PeriodicReportSwitch;
import com.huawei.hihealthservice.sync.syncdata.HiSyncBase;
import com.huawei.hihealthservice.sync.syncdata.dictionary.detail.GetSampleSequenceByVersionReq;
import com.huawei.hms.framework.network.restclient.hwhttp.ClientConfiguration;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.hwcloudmodel.healthdatacloud.HealthDataCloudFactory;
import com.huawei.hwcloudmodel.healthdatacloud.model.AddHealthDataReq;
import com.huawei.hwcloudmodel.healthdatacloud.model.AddSamplePeriodicReportReq;
import com.huawei.hwcloudmodel.healthdatacloud.model.AddSamplePeriodicReportRsp;
import com.huawei.hwcloudmodel.healthdatacloud.model.BaseMetaData;
import com.huawei.hwcloudmodel.healthdatacloud.model.GetHealthDataByTimeReq;
import com.huawei.hwcloudmodel.healthdatacloud.model.GetHealthDataByVersionReq;
import com.huawei.hwcloudmodel.healthdatacloud.model.GetHealthDataLatestReq;
import com.huawei.hwcloudmodel.healthdatacloud.model.GetHealthDataLatestRsp;
import com.huawei.hwcloudmodel.healthdatacloud.model.SampleSequence;
import com.huawei.hwcloudmodel.model.unite.AddHealthDataRsp;
import com.huawei.hwcloudmodel.model.unite.GetHealthDataByTimeRsp;
import com.huawei.hwcloudmodel.model.unite.GetHealthDataByVersionRsp;
import com.huawei.hwcloudmodel.model.unite.HealthDetail;
import com.huawei.hwcloudmodel.model.unite.ParamValidDetail;
import com.huawei.hwcloudmodel.model.unite.SamplePeriodicReport;
import com.huawei.hwcloudmodel.model.unite.SyncKey;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.login.ui.login.AccountConstants;
import com.huawei.login.ui.login.LoginInit;
import com.huawei.networkclient.ResultCallback;
import com.huawei.operation.OpAnalyticsUtil;
import com.huawei.operation.OperationKey;
import defpackage.lob;
import defpackage.lqb;
import defpackage.lqg;
import defpackage.lre;
import defpackage.lsn;
import defpackage.lzt;
import defpackage.maq;
import defpackage.mbp;
import defpackage.mbx;
import defpackage.mck;
import defpackage.mcm;
import defpackage.mcr;
import defpackage.mcv;
import defpackage.mcx;
import defpackage.mdc;
import defpackage.mdu;
import defpackage.mdx;
import defpackage.mel;
import defpackage.mlc;
import defpackage.okt;
import defpackage.pnq;
import health.compact.a.CommonUtil;
import health.compact.a.HiCommonUtil;
import health.compact.a.HiDateUtil;
import health.compact.a.LogAnonymous;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes10.dex */
public class HiSyncDictionaryDataDetail implements HiSyncBase {
    private static final String TAG = "HiH_HiSyncDictData";
    private static final List<Integer> UN_CLOUD_TYPE = Arrays.asList(Integer.valueOf(DicDataTypeUtil$DataType.MERGED_RUN.value()), Integer.valueOf(DicDataTypeUtil$DataType.MERGED_BIKE.value()));
    List<lob> localVersions;
    private Context mContext;
    private long mCurrentVersion;
    private lqg mDataSequenceManager;
    private HealthDataSwitch mHealthDataSwitch;
    private lzt mHiHealthDataInsertStore;
    private mlc mHwCloudMgr;
    private int mMainUserId;
    private MotionPathDataSwitch mMotionPathDataSwitch;
    private maq mMotionPathDataSwitchHelper;
    private HealthDataCloudFactory mParamsFactory;
    private PeriodicReportSwitch mPeriodicReportSwitch;
    private HiSyncOption mSyncOption;
    List<Integer> mSyncTypes;
    private mbx mSyncVersionMgr;
    private int mUploadFailCount = 0;
    private int mUploadTotalCount = 0;
    private lre mSyncAnchorMgr = lre.d();

    public HiSyncDictionaryDataDetail(Context context, HiSyncOption hiSyncOption, int i) {
        this.mContext = context;
        this.mSyncOption = hiSyncOption;
        this.mMainUserId = i;
        mlc mlcVarB = mlc.b(context);
        this.mHwCloudMgr = mlcVarB;
        this.mParamsFactory = mlcVarB.a();
        this.mSyncVersionMgr = mbx.d(this.mContext);
        this.mHiHealthDataInsertStore = lzt.b(this.mContext);
        this.mHealthDataSwitch = new HealthDataSwitch(this.mContext);
        this.mMotionPathDataSwitch = new MotionPathDataSwitch(this.mContext);
        this.mPeriodicReportSwitch = new PeriodicReportSwitch(this.mContext);
        this.mMotionPathDataSwitchHelper = maq.a(context);
        this.mDataSequenceManager = lqg.b(this.mContext);
    }

    static /* synthetic */ int access$008(HiSyncDictionaryDataDetail hiSyncDictionaryDataDetail) {
        int i = hiSyncDictionaryDataDetail.mUploadFailCount;
        hiSyncDictionaryDataDetail.mUploadFailCount = i + 1;
        return i;
    }

    private long addHealthData(List<HiHealthData> list, int i, int i2, boolean z, long j) throws Throwable {
        if (z || !mcx.h()) {
            int i3 = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i3;
            mcx.e(i3, this.mSyncOption.getSyncManual());
        } else {
            int i4 = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i4;
            if (i4 > 5) {
                ReleaseLogUtil.c(TAG, "isOverSea mUploadTotalCount too much");
                this.mUploadFailCount += 2;
                return -1L;
            }
        }
        HashMap map = new HashMap();
        List<HealthDetail> listE = this.mHealthDataSwitch.e(list, i, i2, map);
        AddHealthDataReq addHealthDataReq = new AddHealthDataReq();
        addHealthDataReq.setTimeZone(list.get(0).getTimeZone());
        addHealthDataReq.setDetailInfo(listE);
        addHealthDataReq.setLocalMaxVersion(Long.valueOf(j));
        while (true) {
            int i5 = this.mUploadFailCount;
            if (i5 >= 2) {
                ReleaseLogUtil.c(TAG, "addDataToCloud failed! uploadCount", Integer.valueOf(i5), " tp=", Integer.valueOf(i2), ", date sz=", Integer.valueOf(list.size()), " clientId ", Integer.valueOf(i));
                return -1L;
            }
            AddHealthDataRsp addHealthDataRspC = this.mHwCloudMgr.c(addHealthDataReq);
            if (addHealthDataRspC != null && addHealthDataRspC.getResultCode().intValue() == 1001) {
                ParamValidDetail paramValidDetail = addHealthDataRspC.getParamValidDetail();
                mcx.a(addHealthDataRspC, paramValidDetail);
                if (paramValidDetail != null) {
                    mcx.a(list, paramValidDetail, map);
                    updateDataSyncStatus(list, i2);
                    pointDownloadMore(addHealthDataReq, addHealthDataRspC, i2);
                    ReleaseLogUtil.a(TAG, "addDataToCloud failed ! resultCode is ", addHealthDataRspC.getResultCode(), ", resultDesc is ", addHealthDataRspC.getResultDesc());
                    return addHealthDataRspC.getTimestamp().longValue();
                }
                this.mUploadFailCount++;
            } else {
                if (mck.a(addHealthDataRspC, false)) {
                    ReleaseLogUtil.c(TAG, "addDataToCloud OK! uploadCount=", Integer.valueOf(this.mUploadFailCount), " tp=", Integer.valueOf(i2), ",dtsz=", Integer.valueOf(list.size()), " cId=", Integer.valueOf(i));
                    updateDataSyncStatus(list, i2);
                    pointDownloadMore(addHealthDataReq, addHealthDataRspC, i2);
                    return addHealthDataRspC.getTimestamp().longValue();
                }
                this.mUploadFailCount++;
            }
        }
    }

    private void deleteAndDoMerge(List<HiHealthData> list, List<Integer> list2) {
        HiDicSequenceMerge hiDicSequenceMerge = new HiDicSequenceMerge(this.mContext);
        if (HiCommonUtil.a((List) list)) {
            return;
        }
        for (HiHealthData hiHealthData : list) {
            hiDicSequenceMerge.merge(hiHealthData, hiHealthData.getClientId(), list2);
        }
    }

    private void deleteSequences(List<BaseMetaData> list) throws Throwable {
        boolean zB;
        Context context;
        int i;
        ArrayList arrayList = new ArrayList(10);
        for (BaseMetaData baseMetaData : list) {
            ReleaseLogUtil.a(TAG, "deleteSequence ", baseMetaData.toString());
            List<Integer> listD = lqb.a().d(this.mContext, Long.valueOf(baseMetaData.getDeviceCode()), this.mMainUserId);
            if (HiCommonUtil.a((List) listD)) {
                ReleaseLogUtil.a(TAG, "deleteSequence clientId error");
            } else {
                int[] iArr = {30001};
                List<HiHealthData> listA = this.mDataSequenceManager.a(baseMetaData.getStartTime(), baseMetaData.getEndTime(), iArr[0], listD, mel.e(baseMetaData.getSubType()));
                if (listA == null || listA.isEmpty()) {
                    ReleaseLogUtil.a(TAG, "deleteSequence not found");
                } else {
                    HiHealthData hiHealthData = listA.get(0);
                    try {
                        zB = !mdu.h(this.mContext, iArr[0]) ? mdu.b(this.mContext, iArr[0]) : false;
                        try {
                            try {
                                int iE = this.mDataSequenceManager.e(hiHealthData);
                                if (iE <= 0) {
                                    ReleaseLogUtil.a(TAG, "deleteSequences() fail");
                                } else {
                                    hiHealthData.setUserId(this.mMainUserId);
                                    hiHealthData.setSyncStatus(2);
                                    hiHealthData.setType(hiHealthData.getType() + hiHealthData.getSubType());
                                    arrayList.add(hiHealthData);
                                }
                                if (zB && iE > 0) {
                                    mdu.g(this.mContext, iArr[0]);
                                }
                            } catch (Exception e) {
                                e = e;
                                ReleaseLogUtil.d(TAG, "deleteSequences Exception ", LogAnonymous.c(e));
                                if (zB) {
                                    context = this.mContext;
                                    i = iArr[0];
                                    mdu.d(context, i);
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (zB) {
                                mdu.d(this.mContext, iArr[0]);
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        zB = false;
                    } catch (Throwable th2) {
                        th = th2;
                        zB = false;
                    }
                    if (zB) {
                        context = this.mContext;
                        i = iArr[0];
                        mdu.d(context, i);
                    }
                }
            }
        }
        deleteAndDoMerge(arrayList, lqb.a().e(this.mMainUserId));
        prepareAndDoHealthDataStat(arrayList);
    }

    private void deleteTemporaryData(HiHealthData hiHealthData, int i) {
        if (i != 0) {
            ReleaseLogUtil.d(TAG, "Not supported delaySecondToDelete is ", Integer.valueOf(i));
        } else {
            ReleaseLogUtil.c(TAG, "delete type:", Integer.valueOf(hiHealthData.getType()), ", subTypeId:", Integer.valueOf(hiHealthData.getSubType()), " ,st: ", Long.valueOf(hiHealthData.getStartTime()), ", et: ", Long.valueOf(hiHealthData.getEndTime()));
            ReleaseLogUtil.c(TAG, "End to delete, result: ", Boolean.valueOf(this.mDataSequenceManager.e(hiHealthData) > 0));
        }
    }

    private void downloadAllStatByTime(int i, SparseArray<Integer> sparseArray) throws mcm {
        if (sparseArray == null || sparseArray.size() <= 0) {
            ReleaseLogUtil.a(TAG, "downloadAllStatByTime() downloadDaysMap is null, stop pullDataByVersion!");
            return;
        }
        for (int size = sparseArray.size() - 1; size >= 0; size--) {
            int iKeyAt = sparseArray.keyAt(size);
            downloadByTime(i, HiDateUtil.e(iKeyAt), HiDateUtil.e(sparseArray.get(iKeyAt).intValue()) + DnsResult.TIME_TO_UPDATE);
        }
    }

    private long downloadPointDataByRequest(GetHealthDataByVersionReq getHealthDataByVersionReq) throws mcm {
        GetHealthDataByVersionRsp getHealthDataByVersionRspA = this.mHwCloudMgr.a(getHealthDataByVersionReq);
        if (!mck.a(getHealthDataByVersionRspA, false)) {
            ReleaseLogUtil.a(TAG, "downOneTypeDataOnce() SyncError checkCloudRsp");
            return -1L;
        }
        long currentVersion = getHealthDataByVersionRspA.getCurrentVersion();
        if (currentVersion <= getHealthDataByVersionReq.getVersion()) {
            ReleaseLogUtil.a(TAG, "downloadPointDataByRequest downloadVersion is ", Long.valueOf(currentVersion), " smaller than currentVersion ", Long.valueOf(getHealthDataByVersionReq.getVersion()));
            return -1L;
        }
        List<HealthDetail> detailInfos = getHealthDataByVersionRspA.getDetailInfos();
        if (HiCommonUtil.a((List) detailInfos)) {
            ReleaseLogUtil.a(TAG, " downOneTypeDataOnce() detailInfos is null or empty");
            return currentVersion;
        }
        if (!savePointData(detailInfos, getHealthDataByVersionReq.getType().intValue(), getHealthDataByVersionReq.getVersion())) {
            return -1L;
        }
        mdc.a().a(getHealthDataByVersionReq.getType().intValue(), new lsn(this.mContext.getPackageName()));
        return currentVersion;
    }

    private void downloadPointDataByType(SyncKey syncKey) throws mcm {
        if (syncKey == null) {
            ReleaseLogUtil.a(TAG, "downloadPointDataByType syncKey is null");
            return;
        }
        Integer type = syncKey.getType();
        int iIntValue = type.intValue();
        Long version = syncKey.getVersion();
        long jLongValue = version.longValue();
        LogUtil.e(TAG, "downloadOneTypeDataByVersion type is ", type, ", maxVersion is ", version);
        if (jLongValue <= 0) {
            ReleaseLogUtil.a(TAG, "downloadOneTypeDataByVersion cloud has no such data, type is ", type);
            return;
        }
        GetHealthDataByVersionReq getHealthDataByVersionReq = new GetHealthDataByVersionReq();
        getHealthDataByVersionReq.setDataType(Integer.valueOf(this.mSyncOption.getSyncMethod()));
        getHealthDataByVersionReq.setType(type);
        lob oneSyncLocalVersion = getOneSyncLocalVersion(iIntValue);
        if (oneSyncLocalVersion == null) {
            LogUtil.e(TAG, " syncAnchorTable is null");
            getHealthDataByVersionReq.setVersion(0L);
            downloadPointDataByVersion(getHealthDataByVersionReq, jLongValue);
        } else if (oneSyncLocalVersion.h() >= jLongValue) {
            LogUtil.e(TAG, " do not need downloadOneTypeDataByVersion data, type is ", type, ", DBversion is ", Long.valueOf(oneSyncLocalVersion.h()), ", maxVersion is ", version);
        } else {
            getHealthDataByVersionReq.setVersion(oneSyncLocalVersion.h());
            downloadPointDataByVersion(getHealthDataByVersionReq, jLongValue);
        }
    }

    private void downloadPointDataByVersion(GetHealthDataByVersionReq getHealthDataByVersionReq, long j) throws mcm {
        long jDownloadPointDataByRequest;
        ReleaseLogUtil.c(TAG, " downloadPointDataByVersion rep = ", getHealthDataByVersionReq, ", maxVersion = ", Long.valueOf(j));
        int i = 0;
        do {
            jDownloadPointDataByRequest = downloadPointDataByRequest(getHealthDataByVersionReq);
            LogUtil.e(TAG, " downloadPointDataByVersion downCurrentVersion is ", Long.valueOf(jDownloadPointDataByRequest), " maxVersion is ", Long.valueOf(j));
            i++;
            if (jDownloadPointDataByRequest <= -1) {
                return;
            }
            if (!this.mSyncAnchorMgr.b(this.mMainUserId, getHealthDataByVersionReq.getType().intValue(), jDownloadPointDataByRequest, 0L)) {
                ReleaseLogUtil.a(TAG, "downloadPointDataByVersion saveVersionToDB failed!");
                return;
            }
            getHealthDataByVersionReq.setVersion(jDownloadPointDataByRequest);
            if (mao.h() && !mcx.j()) {
                ReleaseLogUtil.a(TAG, " downloadPointDataByVersion() background is running");
                return;
            } else if (!mcx.j() && i >= 20 && getHealthDataByVersionReq.getType().intValue() != DicDataTypeUtil$DataType.ALTITUDE_TYPE_SET.value()) {
                ReleaseLogUtil.a(TAG, " downloadPointDataByVersion() pullDataByVersion too many times");
                return;
            }
        } while (jDownloadPointDataByRequest < j);
    }

    private long downloadSamplePeriodicReportDataByRequest(GetSamplePeriodicReportByVersionReq getSamplePeriodicReportByVersionReq) throws mcm {
        GetSamplePeriodicReportByVersionRsp getSamplePeriodicReportByVersionRsp = (GetSamplePeriodicReportByVersionRsp) pnq.c().b(getSamplePeriodicReportByVersionReq.getUrl(), getHeader(), getSamplePeriodicReportByVersionReq.getQueryParams(), GetSamplePeriodicReportByVersionRsp.class);
        if (!mck.a(getSamplePeriodicReportByVersionRsp, false)) {
            ReleaseLogUtil.d(TAG, "downloadPeriodicReportByRequest SyncError checkCloudRsp");
            return -1L;
        }
        long currentVersion = getSamplePeriodicReportByVersionRsp.getCurrentVersion();
        if (currentVersion <= getSamplePeriodicReportByVersionReq.getVersion()) {
            ReleaseLogUtil.a(TAG, "downloadPeriodicReportByRequest downloadVersion is ", Long.valueOf(currentVersion), " smaller than currentVersion ", Long.valueOf(getSamplePeriodicReportByVersionReq.getVersion()));
            return -1L;
        }
        List<SamplePeriodicReport> sampleReports = getSamplePeriodicReportByVersionRsp.getSampleReports();
        if (HiCommonUtil.a((List) sampleReports)) {
            ReleaseLogUtil.a(TAG, "downloadPeriodicReportByRequest cloudReports is null or empty");
            return currentVersion;
        }
        if (!saveSamplePeriodicReportData(sampleReports)) {
            return -1L;
        }
        mdc.a().a(getSamplePeriodicReportByVersionReq.getType(), new lsn(this.mContext.getPackageName()));
        return currentVersion;
    }

    private void downloadSamplePeriodicReportDataByType(SyncKey syncKey) throws mcm {
        if (syncKey == null) {
            ReleaseLogUtil.a(TAG, "downloadPeriodicReportDByType syncKey is null");
            return;
        }
        Integer type = syncKey.getType();
        int iIntValue = type.intValue();
        Long version = syncKey.getVersion();
        long jLongValue = version.longValue();
        LogUtil.e(TAG, "downloadPeriodicReportDByType type is ", type, ", maxVersion is ", version);
        if (jLongValue <= 0) {
            ReleaseLogUtil.a(TAG, "downloadPeriodicReportDByType cloud has no such data, type is ", type);
            return;
        }
        GetSamplePeriodicReportByVersionReq getSamplePeriodicReportByVersionReq = new GetSamplePeriodicReportByVersionReq();
        getSamplePeriodicReportByVersionReq.setType(iIntValue);
        lob oneSyncLocalVersion = getOneSyncLocalVersion(iIntValue);
        if (oneSyncLocalVersion == null) {
            LogUtil.e(TAG, "downloadPeriodicReportDByType syncAnchorTable is null");
            getSamplePeriodicReportByVersionReq.setVersion(0L);
            downloadSamplePeriodicReportDataByVersion(getSamplePeriodicReportByVersionReq, jLongValue);
        } else if (oneSyncLocalVersion.h() >= jLongValue) {
            ReleaseLogUtil.c(TAG, "do not need downloadPeriodicReportDByType data, type is ", type, ", DBversion is ", Long.valueOf(oneSyncLocalVersion.h()), ", maxVersion is ", version);
        } else {
            getSamplePeriodicReportByVersionReq.setVersion(oneSyncLocalVersion.h());
            downloadSamplePeriodicReportDataByVersion(getSamplePeriodicReportByVersionReq, jLongValue);
        }
    }

    private void downloadSamplePeriodicReportDataByVersion(GetSamplePeriodicReportByVersionReq getSamplePeriodicReportByVersionReq, long j) throws mcm {
        long jDownloadSamplePeriodicReportDataByRequest;
        ReleaseLogUtil.c(TAG, "downloadPeriodicReportByVersion req = ", HiJsonUtil.a(getSamplePeriodicReportByVersionReq), ", maxVersion = ", Long.valueOf(j));
        int i = 0;
        do {
            jDownloadSamplePeriodicReportDataByRequest = downloadSamplePeriodicReportDataByRequest(getSamplePeriodicReportByVersionReq);
            LogUtil.e(TAG, "downloadPeriodicReportByVersion downCurrentVersion is ", Long.valueOf(jDownloadSamplePeriodicReportDataByRequest), " maxVersion is ", Long.valueOf(j));
            i++;
            if (jDownloadSamplePeriodicReportDataByRequest <= -1) {
                return;
            }
            if (!this.mSyncAnchorMgr.b(this.mMainUserId, getSamplePeriodicReportByVersionReq.getType(), jDownloadSamplePeriodicReportDataByRequest, 0L)) {
                ReleaseLogUtil.a(TAG, "downloadPeriodicReportByVersion saveVersionToDB failed!");
                return;
            }
            getSamplePeriodicReportByVersionReq.setVersion(jDownloadSamplePeriodicReportDataByRequest);
            if (mao.h() && !mcx.j()) {
                ReleaseLogUtil.a(TAG, "downloadPeriodicReportByVersion() background is running");
                return;
            } else if (!mcx.j() && i >= 20) {
                ReleaseLogUtil.a(TAG, "downloadPeriodicReportByVersion() pullDataByVersion too many times");
                return;
            }
        } while (jDownloadSamplePeriodicReportDataByRequest < j);
    }

    private long downloadSequenceDataByRequest(GetSampleSequenceByVersionReq getSampleSequenceByVersionReq) throws Throwable {
        GetSampleSequenceByVersionRsp getSampleSequenceByVersionRsp = (GetSampleSequenceByVersionRsp) pnq.c().a(getSampleSequenceByVersionReq.getUrl(), this.mParamsFactory.getHeaders(), pob.a(this.mParamsFactory.getBody(getSampleSequenceByVersionReq)), GetSampleSequenceByVersionRsp.class);
        if (!mck.a(getSampleSequenceByVersionRsp, false)) {
            ReleaseLogUtil.a(TAG, "downloadSequenceDataByRequest SyncError checkCloudRsp");
            return -1L;
        }
        long currentVersion = getSampleSequenceByVersionRsp.getCurrentVersion();
        long j = this.mCurrentVersion;
        if (currentVersion <= j) {
            ReleaseLogUtil.a(TAG, "downloadSequenceDataByRequest downloadVersion is ", Long.valueOf(currentVersion), " smaller than currentVersion ", Long.valueOf(j));
            return -1L;
        }
        List<BaseMetaData> deleteInfos = getSampleSequenceByVersionRsp.getDeleteInfos();
        if (!HiCommonUtil.a((List) deleteInfos) && !mcx.j()) {
            deleteSequences(deleteInfos);
        }
        List<SampleSequence> detailInfos = getSampleSequenceByVersionRsp.getDetailInfos();
        if (HiCommonUtil.a((List) detailInfos)) {
            ReleaseLogUtil.a(TAG, "downloadSequenceDataByRequest cloudTracks is null or empty");
            this.mCurrentVersion = currentVersion;
            return currentVersion;
        }
        this.mCurrentVersion = currentVersion;
        saveSequenceData(detailInfos, false);
        return currentVersion;
    }

    private void downloadSequenceDataByType(SyncKey syncKey) throws Throwable {
        LogUtil.e(TAG, "downloadSequenceDataByType key = ", syncKey);
        if (syncKey == null) {
            ReleaseLogUtil.a(TAG, "downloadSequenceDataByType syncKey is null");
            return;
        }
        int iIntValue = syncKey.getType().intValue();
        Long version = syncKey.getVersion();
        long jLongValue = version.longValue();
        if (jLongValue <= 0) {
            LogUtil.g(TAG, "downloadSequenceDataByType the maxVersion is not right , maxVersion is ", version);
            return;
        }
        lob oneSyncLocalVersion = getOneSyncLocalVersion(iIntValue);
        if (oneSyncLocalVersion == null) {
            downloadSequenceDataByVersion(new GetSampleSequenceByVersionReq.Builder().type(iIntValue).version(0L).dataType(this.mSyncOption.getSyncMethod()).build(), jLongValue);
        } else if (oneSyncLocalVersion.h() < jLongValue) {
            downloadSequenceDataByVersion(new GetSampleSequenceByVersionReq.Builder().type(iIntValue).version(oneSyncLocalVersion.h()).dataType(this.mSyncOption.getSyncMethod()).build(), jLongValue);
        } else {
            LogUtil.e(TAG, "do not need download data, DBversion is ", Long.valueOf(oneSyncLocalVersion.h()), ", maxVersion is ", version);
        }
    }

    private void downloadSequenceDataByVersion(GetSampleSequenceByVersionReq getSampleSequenceByVersionReq, long j) throws Throwable {
        long jDownloadSequenceDataByRequest;
        ReleaseLogUtil.c(TAG, "performDownloadByVersion req = ", getSampleSequenceByVersionReq, " maxVersion = ", Long.valueOf(j));
        this.mCurrentVersion = getSampleSequenceByVersionReq.getVersion();
        do {
            jDownloadSequenceDataByRequest = downloadSequenceDataByRequest(getSampleSequenceByVersionReq);
            ReleaseLogUtil.c(TAG, "performDownloadByVersion downCurrentVersion = ", Long.valueOf(jDownloadSequenceDataByRequest), " maxVersion = ", Long.valueOf(j));
            if (jDownloadSequenceDataByRequest <= -1) {
                return;
            }
            if (!this.mSyncAnchorMgr.b(this.mMainUserId, getSampleSequenceByVersionReq.getType(), jDownloadSequenceDataByRequest, 0L)) {
                ReleaseLogUtil.a(TAG, "performDownloadByVersion saveVersionToDB failed ");
            }
            getSampleSequenceByVersionReq = getSampleSequenceByVersionReq.toBuilder().version(this.mCurrentVersion).dataType(this.mSyncOption.getSyncMethod()).build();
        } while (jDownloadSequenceDataByRequest < j);
    }

    private static Map<String, String> getHeader() {
        String accountInfo = LoginInit.getInstance(BaseApplication.getContext()).getAccountInfo(AccountConstants.NEW_ACCESS_TOKEN);
        HashMap map = new HashMap(16);
        map.put("Content-type", "application/json");
        map.put("Authorization", "Bearer " + accountInfo);
        map.put("x-ts", String.valueOf(System.currentTimeMillis()));
        map.put("x-version", CommonUtil.c(BaseApplication.getContext()));
        return map;
    }

    private List<HiHealthData> getUploadDataList(int i, List<Integer> list, int i2, int i3) {
        List<HiHealthData> listB = mdu.c(this.mContext, i).b(i2, list, i3);
        if (HiCommonUtil.a((List) listB)) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList(10);
        ArrayList arrayList2 = new ArrayList(listB.size());
        for (HiHealthData hiHealthData : listB) {
            String str = hiHealthData.getStartTime() + "_" + hiHealthData.getEndTime();
            if (!arrayList2.contains(str)) {
                List<HiHealthData> listB2 = mdu.c(this.mContext, i).b(i2, list, hiHealthData.getStartTime(), hiHealthData.getEndTime());
                if (!HiCommonUtil.a((List) listB2)) {
                    arrayList.addAll(listB2);
                }
                arrayList2.add(str);
            }
        }
        return arrayList;
    }

    private GetHealthDataByTimeReq initByTimeRep(int i, long j, long j2) {
        GetHealthDataByTimeReq getHealthDataByTimeReq = new GetHealthDataByTimeReq();
        getHealthDataByTimeReq.setQueryType(2);
        getHealthDataByTimeReq.setDataType(2);
        getHealthDataByTimeReq.setStartTime(Long.valueOf(j));
        getHealthDataByTimeReq.setEndTime(Long.valueOf(j2));
        getHealthDataByTimeReq.setType(Integer.valueOf(i));
        return getHealthDataByTimeReq;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void periodicReportDownloadMore(AddSamplePeriodicReportReq addSamplePeriodicReportReq, AddSamplePeriodicReportRsp addSamplePeriodicReportRsp, int i) throws mcm {
        ReleaseLogUtil.c(TAG, "periodicReport LocalMaxVersion=", addSamplePeriodicReportReq.getLocalMaxVersion(), ",rsp=", Long.valueOf(addSamplePeriodicReportRsp.getVersion()), ",isHasMore=", Boolean.valueOf(addSamplePeriodicReportRsp.isHasMore()), ",cloudTp=", Integer.valueOf(i));
        if (!addSamplePeriodicReportRsp.isHasMore()) {
            if (this.mSyncAnchorMgr.b(this.mMainUserId, i, addSamplePeriodicReportRsp.getVersion(), 0L)) {
                return;
            }
            ReleaseLogUtil.a(TAG, "sequence downloadMore saveVersionToDB failed!");
        } else {
            SyncKey syncKey = new SyncKey();
            syncKey.setType(Integer.valueOf(i));
            syncKey.setVersion(Long.valueOf(addSamplePeriodicReportRsp.getVersion()));
            syncKey.setCategory("SampleReport");
            syncKey.setDataType(2);
            downloadSamplePeriodicReportDataByType(syncKey);
        }
    }

    private void pointDownloadMore(AddHealthDataReq addHealthDataReq, AddHealthDataRsp addHealthDataRsp, int i) throws mcm {
        ReleaseLogUtil.c(TAG, "point LocalMaxVersion=", addHealthDataReq.getLocalMaxVersion(), ",rsp=", addHealthDataRsp.getTimestamp(), ",isHasMore=", Boolean.valueOf(addHealthDataRsp.isHasMore()), ",cloudTp=", Integer.valueOf(i));
        if (!addHealthDataRsp.isHasMore()) {
            if (this.mSyncAnchorMgr.b(this.mMainUserId, i, addHealthDataRsp.getTimestamp().longValue(), 0L)) {
                return;
            }
            ReleaseLogUtil.a(TAG, "point downloadMore saveVersionToDB failed!");
        } else {
            SyncKey syncKey = new SyncKey();
            syncKey.setType(Integer.valueOf(i));
            syncKey.setVersion(addHealthDataRsp.getTimestamp());
            downloadPointDataByType(syncKey);
        }
    }

    private void prepareAndDoHealthDataStat(List<HiHealthData> list) {
        if (HiCommonUtil.a((List) list)) {
            return;
        }
        lzt lztVarB = lzt.b(this.mContext);
        lztVarB.prepareRealTimeHealthDataStat(list);
        lztVarB.doRealTimeHealthDataStat();
    }

    private void saveHealthDetailData(List<HiHealthData> list, int i) throws Throwable {
        this.mHiHealthDataInsertStore.saveSyncHealthDetailData(list, this.mMainUserId);
        if (HiHealthDictManager.d(this.mContext).t(i)) {
            this.mHiHealthDataInsertStore.prepareRealTimeHealthDataStat(list);
            this.mHiHealthDataInsertStore.doRealTimeHealthDataStat();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0082  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean savePointData(java.util.List<com.huawei.hwcloudmodel.model.unite.HealthDetail> r7, int r8, long r9) throws java.lang.Throwable {
        /*
            r6 = this;
            java.lang.String r0 = " saveData()"
            java.lang.Object[] r0 = new java.lang.Object[]{r0}
            java.lang.String r1 = "HiH_HiSyncDictData"
            com.huawei.hwlogsmodel.LogUtil.e(r1, r0)
            mcq r0 = defpackage.mcq.e()
            java.util.Collections.sort(r7, r0)
            com.huawei.hihealthservice.sync.dataswitch.HealthDataSwitch r0 = r6.mHealthDataSwitch
            int r2 = r6.mMainUserId
            java.util.List r7 = r0.b(r7, r2, r9)
            boolean r9 = health.compact.a.HiCommonUtil.a(r7)
            r10 = 1
            if (r9 == 0) goto L2b
            java.lang.String r7 = "local data empty"
            java.lang.Object[] r7 = new java.lang.Object[]{r7}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.a(r1, r7)
            return r10
        L2b:
            r9 = 0
            java.lang.Object r0 = r7.get(r9)
            com.huawei.hihealth.HiHealthData r0 = (com.huawei.hihealth.HiHealthData) r0
            int r0 = r0.getType()
            int r2 = r7.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            java.lang.String r4 = ", size is "
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.String r5 = "local point data type is "
            java.lang.Object[] r2 = new java.lang.Object[]{r5, r3, r4, r2}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.c(r1, r2)
            android.content.Context r2 = r6.mContext     // Catch: java.lang.Throwable -> L6a java.lang.Exception -> L6c
            boolean r2 = defpackage.mdu.h(r2, r0)     // Catch: java.lang.Throwable -> L6a java.lang.Exception -> L6c
            if (r2 != 0) goto L5c
            android.content.Context r2 = r6.mContext     // Catch: java.lang.Throwable -> L6a java.lang.Exception -> L6c
            boolean r2 = defpackage.mdu.b(r2, r0)     // Catch: java.lang.Throwable -> L6a java.lang.Exception -> L6c
            goto L5d
        L5c:
            r2 = r9
        L5d:
            r6.saveHealthDetailData(r7, r8)     // Catch: java.lang.Exception -> L6d java.lang.Throwable -> L7e
            if (r2 == 0) goto L67
            android.content.Context r7 = r6.mContext     // Catch: java.lang.Exception -> L6d java.lang.Throwable -> L7e
            defpackage.mdu.g(r7, r0)     // Catch: java.lang.Exception -> L6d java.lang.Throwable -> L7e
        L67:
            if (r2 == 0) goto L7d
            goto L78
        L6a:
            r7 = move-exception
            goto L80
        L6c:
            r2 = r9
        L6d:
            java.lang.Object[] r7 = new java.lang.Object[r10]     // Catch: java.lang.Throwable -> L7e
            java.lang.String r8 = "savePointData Exception"
            r7[r9] = r8     // Catch: java.lang.Throwable -> L7e
            health.compact.a.hwlogsmodel.ReleaseLogUtil.d(r1, r7)     // Catch: java.lang.Throwable -> L7e
            if (r2 == 0) goto L7d
        L78:
            android.content.Context r7 = r6.mContext
            defpackage.mdu.d(r7, r0)
        L7d:
            return r10
        L7e:
            r7 = move-exception
            r9 = r2
        L80:
            if (r9 == 0) goto L87
            android.content.Context r8 = r6.mContext
            defpackage.mdu.d(r8, r0)
        L87:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.huawei.hihealthservice.sync.syncdata.dictionary.detail.HiSyncDictionaryDataDetail.savePointData(java.util.List, int, long):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean saveSamplePeriodicReportData(java.util.List<com.huawei.hwcloudmodel.model.unite.SamplePeriodicReport> r11) throws java.lang.Throwable {
        /*
            r10 = this;
            long r0 = java.lang.System.currentTimeMillis()
            boolean r2 = health.compact.a.HiCommonUtil.a(r11)
            r3 = 1
            java.lang.String r4 = "HiH_HiSyncDictData"
            if (r2 == 0) goto L17
            java.lang.String r11 = "saveSamplePeriodicReportData cloudReports is empty"
            java.lang.Object[] r11 = new java.lang.Object[]{r11}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.c(r4, r11)
            return r3
        L17:
            com.huawei.hihealthservice.sync.dataswitch.PeriodicReportSwitch r2 = r10.mPeriodicReportSwitch
            int r5 = r10.mMainUserId
            java.util.List r11 = r2.d(r11, r5)
            boolean r2 = health.compact.a.HiCommonUtil.a(r11)
            if (r2 == 0) goto L2f
            java.lang.String r11 = "saveSamplePeriodicReportData hiHealthDatas is empty"
            java.lang.Object[] r11 = new java.lang.Object[]{r11}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.c(r4, r11)
            return r3
        L2f:
            r2 = 0
            java.lang.Object r5 = r11.get(r2)
            com.huawei.hihealth.HiHealthData r5 = (com.huawei.hihealth.HiHealthData) r5
            int r5 = r5.getType()
            int r6 = r11.size()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r5)
            java.lang.String r8 = ", size is "
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r9 = "local periodic report data type is "
            java.lang.Object[] r6 = new java.lang.Object[]{r9, r7, r8, r6}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.c(r4, r6)
            android.content.Context r6 = r10.mContext     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L89
            boolean r6 = defpackage.mdu.h(r6, r5)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L89
            if (r6 != 0) goto L60
            android.content.Context r6 = r10.mContext     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L89
            boolean r6 = defpackage.mdu.b(r6, r5)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L89
            goto L61
        L60:
            r6 = r2
        L61:
            r10.saveHealthDetailData(r11, r5)     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> La3
            if (r6 == 0) goto L6b
            android.content.Context r11 = r10.mContext     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> La3
            defpackage.mdu.g(r11, r5)     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> La3
        L6b:
            if (r6 == 0) goto L72
            android.content.Context r11 = r10.mContext
            defpackage.mdu.d(r11, r5)
        L72:
            long r5 = java.lang.System.currentTimeMillis()
            long r5 = r5 - r0
            java.lang.Long r11 = java.lang.Long.valueOf(r5)
            java.lang.String r0 = "saveSamplePeriodicReportData end saveDetailTime = "
            java.lang.Object[] r11 = new java.lang.Object[]{r0, r11}
            health.compact.a.hwlogsmodel.ReleaseLogUtil.c(r4, r11)
            return r3
        L85:
            r11 = move-exception
            goto L8b
        L87:
            r11 = move-exception
            goto La5
        L89:
            r11 = move-exception
            r6 = r2
        L8b:
            java.lang.String r11 = com.huawei.haf.common.exception.ExceptionUtils.a(r11)     // Catch: java.lang.Throwable -> La3
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> La3
            java.lang.String r1 = "saveSamplePeriodicReportData Exception:"
            r0[r2] = r1     // Catch: java.lang.Throwable -> La3
            r0[r3] = r11     // Catch: java.lang.Throwable -> La3
            health.compact.a.hwlogsmodel.ReleaseLogUtil.d(r4, r0)     // Catch: java.lang.Throwable -> La3
            if (r6 == 0) goto La2
            android.content.Context r11 = r10.mContext
            defpackage.mdu.d(r11, r5)
        La2:
            return r2
        La3:
            r11 = move-exception
            r2 = r6
        La5:
            if (r2 == 0) goto Lac
            android.content.Context r0 = r10.mContext
            defpackage.mdu.d(r0, r5)
        Lac:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.huawei.hihealthservice.sync.syncdata.dictionary.detail.HiSyncDictionaryDataDetail.saveSamplePeriodicReportData(java.util.List):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00bb A[PHI: r7
      0x00bb: PHI (r7v3 int) = (r7v2 int), (r7v9 int) binds: [B:36:0x00b9, B:27:0x00a5] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void saveSequenceData(java.util.List<com.huawei.hwcloudmodel.healthdatacloud.model.SampleSequence> r13, boolean r14) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 263
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.huawei.hihealthservice.sync.syncdata.dictionary.detail.HiSyncDictionaryDataDetail.saveSequenceData(java.util.List, boolean):void");
    }

    private void sequenceDownloadMore(AddSampleSequenceReq addSampleSequenceReq, AddSampleSequenceRsp addSampleSequenceRsp, int i) throws Throwable {
        ReleaseLogUtil.c(TAG, "sequence LocalMaxVersion=", addSampleSequenceReq.getLocalMaxVersion(), ",rsp=", Long.valueOf(addSampleSequenceRsp.getTimestamp()), ",isHasMore=", Boolean.valueOf(addSampleSequenceRsp.isHasMore()), ",cloudTp=", Integer.valueOf(i));
        if (DicDataTypeUtil$DataType.ONE_SECOND_SPORT_DATA_TYPE.value() == i || !addSampleSequenceRsp.isHasMore()) {
            if (this.mSyncAnchorMgr.b(this.mMainUserId, i, addSampleSequenceRsp.getTimestamp(), 0L)) {
                return;
            }
            ReleaseLogUtil.a(TAG, "sequence downloadMore saveVersionToDB failed!");
        } else {
            SyncKey syncKey = new SyncKey();
            syncKey.setType(Integer.valueOf(i));
            syncKey.setVersion(Long.valueOf(addSampleSequenceRsp.getTimestamp()));
            syncKey.setDeviceCode(0L);
            downloadSequenceDataByType(syncKey);
        }
    }

    private void updateDataSyncStatus(List<HiHealthData> list, int i) throws Throwable {
        for (HiHealthData hiHealthData : list) {
            mdu.c(this.mContext, i).b(hiHealthData.getDataId(), hiHealthData.getModifiedTime(), 1);
            hiHealthData.setUserId(this.mMainUserId);
            hiHealthData.setSyncStatus(1);
        }
        this.mHiHealthDataInsertStore.prepareRealTimeHealthDataStat(list, false);
        this.mHiHealthDataInsertStore.doRealTimeHealthDataStat(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePeriodicReportSyncStatus(List<HiHealthData> list) {
        for (HiHealthData hiHealthData : list) {
            mdu.e(this.mContext, hiHealthData.getType()).a(hiHealthData.getDataId(), 1, hiHealthData.getMergedStatus());
            LogUtil.e(TAG, "updatePeriodic dataId=", Long.valueOf(hiHealthData.getDataId()), ", syncStatus", 1);
        }
    }

    private void uploadPeriodicReportData(int i, int i2, List<Integer> list) throws mcm {
        LogUtil.d(TAG, "uploadPeriodicReportData clientId=", Integer.valueOf(i), ", typeId=", Integer.valueOf(i2));
        while (this.mUploadFailCount < 2) {
            List<HiHealthData> listB = mdu.e(this.mContext, i2).b(i, list, 0, 100);
            if (HiCommonUtil.a((List) listB)) {
                return;
            }
            lob lobVarA = this.mSyncAnchorMgr.a(this.mMainUserId, 0L, i2);
            this.localVersions = Collections.singletonList(lobVarA);
            boolean zUploadPeriodicReportDataOnce = uploadPeriodicReportDataOnce(listB, lobVarA != null ? lobVarA.h() : 0L);
            mcx.e(ClientConfiguration.INTERVAL);
            if (zUploadPeriodicReportDataOnce && listB.size() < 100) {
                return;
            }
        }
    }

    private boolean uploadPeriodicReportDataOnce(final List<HiHealthData> list, long j) throws mcm {
        if (mcx.h()) {
            int i = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i;
            if (i > 10) {
                this.mUploadFailCount += 2;
                ReleaseLogUtil.a(TAG, "uploadPeriodicReportDataOnce uploadTotalCount too much");
                return false;
            }
        } else {
            int i2 = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i2;
            mcx.e(i2, this.mSyncOption.getSyncManual());
        }
        final List<SamplePeriodicReport> listA = this.mPeriodicReportSwitch.a(list);
        if (HiCommonUtil.a((List) listA)) {
            ReleaseLogUtil.a(TAG, "uploadPeriodicReportDataOnce cloudReports is null or empty");
            return false;
        }
        LogUtil.e(TAG, "uploadPeriodicReportDataOnce cloudReports=", HiJsonUtil.a(listA));
        final AddSamplePeriodicReportReq addSamplePeriodicReportReq = new AddSamplePeriodicReportReq(listA);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        addSamplePeriodicReportReq.setLocalMaxVersion(Long.valueOf(j));
        final boolean[] zArr = {false};
        mlc.b(com.huawei.haf.application.BaseApplication.b()).b(addSamplePeriodicReportReq, new ResultCallback<AddSamplePeriodicReportRsp>() { // from class: com.huawei.hihealthservice.sync.syncdata.dictionary.detail.HiSyncDictionaryDataDetail.1
            public void onFailure(Throwable th) {
                HiSyncDictionaryDataDetail.access$008(HiSyncDictionaryDataDetail.this);
                ReleaseLogUtil.d(HiSyncDictionaryDataDetail.TAG, "uploadPeriodicReportDataOnce onFailure:", ExceptionUtils.a(th));
            }

            /* JADX WARN: Removed duplicated region for block: B:7:0x0035 A[Catch: all -> 0x00af, mcm -> 0x00b1, TryCatch #0 {mcm -> 0x00b1, blocks: (B:4:0x0009, B:6:0x0015, B:7:0x0035, B:9:0x003b, B:11:0x0043, B:13:0x004d), top: B:24:0x0009, outer: #1 }] */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onSuccess(com.huawei.hwcloudmodel.healthdatacloud.model.AddSamplePeriodicReportRsp r11) {
                /*
                    Method dump skipped, instruction units count: 205
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.huawei.hihealthservice.sync.syncdata.dictionary.detail.HiSyncDictionaryDataDetail.AnonymousClass1.onSuccess(com.huawei.hwcloudmodel.healthdatacloud.model.AddSamplePeriodicReportRsp):void");
            }
        });
        try {
            countDownLatch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ReleaseLogUtil.d(TAG, "uploadPeriodicReportDataOnce latch.await : ", ExceptionUtils.a(e));
        }
        return zArr[0];
    }

    private void uploadPointData(int i, List<Integer> list, int i2, List<Integer> list2) throws Throwable {
        lob lobVarA = this.mSyncAnchorMgr.a(this.mMainUserId, 0L, i2);
        this.localVersions = Collections.singletonList(lobVarA);
        long jH = lobVarA != null ? lobVarA.h() : 0L;
        do {
            if (this.mUploadFailCount < 2) {
                List<HiHealthData> uploadDataList = getUploadDataList(i2, list, i, (200 / list.size()) * list.size());
                if (HiCommonUtil.a((List) uploadDataList)) {
                    LogUtil.d(TAG, "no china data to upload ", Integer.valueOf(i2), " clientId ", Integer.valueOf(i));
                    do {
                        if (this.mUploadFailCount < 2) {
                            long jCurrentTimeMillis = System.currentTimeMillis();
                            List<HiHealthData> listE = mdu.c(this.mContext, i2).e(i, list2, HiDateUtil.s(jCurrentTimeMillis), HiDateUtil.h(jCurrentTimeMillis));
                            if (HiCommonUtil.a((List) listE)) {
                                LogUtil.e(TAG, "no oversea data to upload", Integer.valueOf(i2));
                            } else {
                                jH = addHealthData(listE, i, i2, true, jH);
                            }
                        }
                        this.mUploadFailCount = 0;
                        return;
                    } while (jH != -1);
                    return;
                }
                jH = addHealthData(uploadDataList, i, i2, false, jH);
                mcx.e(ClientConfiguration.INTERVAL);
            }
        } while (jH != -1);
    }

    private void uploadSamplePointData(List<Integer> list, List<Integer> list2) throws Throwable {
        LogUtil.d(TAG, "uploadSamplePointData clientIds=", HiJsonUtil.a(list));
        if (list2.size() > 1) {
            list2 = mcx.a("type_id", 0);
            if (HiCommonUtil.a((List) list2)) {
                return;
            }
        }
        List<Integer> listA = mcx.a("client_id", 0);
        if (HiCommonUtil.a((List) listA)) {
            return;
        }
        LogUtil.d(TAG, "uploadSamplePointData syncClients=", HiJsonUtil.a(listA));
        listA.retainAll(list);
        if (HiCommonUtil.a((List) listA)) {
            return;
        }
        ReleaseLogUtil.c(TAG, "uploadSamplePointData retainAll ", HiJsonUtil.a(listA));
        Iterator<Integer> it = list2.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            List<HiHealthDictField> listG = HiHealthDictManager.d(this.mContext).g(iIntValue);
            ArrayList arrayList = new ArrayList(listG.size());
            ArrayList arrayList2 = new ArrayList(listG.size());
            for (HiHealthDictField hiHealthDictField : listG) {
                arrayList.add(Integer.valueOf(hiHealthDictField.d()));
                if (hiHealthDictField.j()) {
                    arrayList2.add(Integer.valueOf(hiHealthDictField.d()));
                }
            }
            Iterator<Integer> it2 = listA.iterator();
            while (it2.hasNext()) {
                uploadPointData(it2.next().intValue(), arrayList, iIntValue, arrayList2);
            }
        }
    }

    private void uploadSampleSequenceData(List<Integer> list, List<Integer> list2) throws mcm {
        List<Integer> listA = lqg.b(this.mContext).a();
        if (HiCommonUtil.a((List) listA)) {
            return;
        }
        listA.retainAll(list);
        ReleaseLogUtil.c(TAG, "uploadSampleSequenceData clientIds=", HiJsonUtil.a(listA));
        if (HiCommonUtil.a((List) listA)) {
            return;
        }
        ArrayList arrayList = new ArrayList(list2.size());
        ArrayList arrayList2 = new ArrayList(list2.size());
        for (Integer num : list2) {
            HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(num.intValue());
            if (hiHealthDictTypeC != null) {
                if (hiHealthDictTypeC.f() == 0) {
                    arrayList.add(num);
                } else {
                    arrayList2.add(Integer.valueOf(hiHealthDictTypeC.f()));
                }
            }
        }
        List<Integer> listE = lqb.a().e(this.mMainUserId);
        boolean z = false;
        if (!HiCommonUtil.a((List) arrayList) && !HiCommonUtil.a((List) lqg.b(this.mContext).a(listE, CommonUtil.b(arrayList), new int[]{0}, 2, 1))) {
            z = true;
        }
        boolean z2 = (HiCommonUtil.a((List) arrayList2) || z || HiCommonUtil.a((List) lqg.b(this.mContext).a(listE, new int[]{30001}, CommonUtil.b(arrayList2), 2, 1))) ? z : true;
        for (Integer num2 : list2) {
            int iIntValue = num2.intValue();
            if (UN_CLOUD_TYPE.contains(num2)) {
                LogUtil.d(TAG, "needn't syncCloud, typeId: ", num2);
            } else {
                if (z2) {
                    mcr.e(this.mContext, this.mMainUserId, iIntValue);
                }
                Iterator<Integer> it = listA.iterator();
                while (it.hasNext()) {
                    uploadSequenceData(it.next().intValue(), iIntValue);
                }
            }
        }
    }

    private void uploadSequenceData(int i, int i2) throws mcm, IllegalArgumentException {
        int iValue;
        List<HiHealthData> listB;
        int iB = mdx.b();
        while (this.mUploadFailCount < 2) {
            HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(i2);
            if (hiHealthDictTypeC == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(Integer.valueOf(i));
            if (hiHealthDictTypeC.f() == 0) {
                listB = this.mDataSequenceManager.a(arrayList, new int[]{i2}, new int[]{0}, 0, mdx.e(iB, 10));
                iValue = i2;
            } else {
                iValue = i2 >= DicDataTypeUtil$DataType.ONE_SECOND_SPORT_DATA_TYPE.value() ? DicDataTypeUtil$DataType.ONE_SECOND_SPORT_DATA_TYPE.value() : 30001;
                listB = mbp.e(this.mContext).b(this.mDataSequenceManager.a(arrayList, new int[]{iValue}, new int[]{hiHealthDictTypeC.f()}, 0, mdx.e(iB, 5)), arrayList);
            }
            if (HiCommonUtil.a((List) listB)) {
                break;
            }
            lob lobVarA = this.mSyncAnchorMgr.a(this.mMainUserId, 0L, iValue);
            this.localVersions = Collections.singletonList(lobVarA);
            try {
                if (!uploadSequenceDataOnce(listB, lobVarA != null ? lobVarA.h() : 0L)) {
                    break;
                }
            } catch (OutOfMemoryError e) {
                ReleaseLogUtil.d(TAG, "uploadSequenceDataOnce OutOfMemoryError, mup=", Integer.valueOf(iB), ", typeId=", Integer.valueOf(i2), ", size", Integer.valueOf(listB.size()));
                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("mup", String.valueOf(iB));
                linkedHashMap.put("typeId", String.valueOf(i2));
                linkedHashMap.put("size", String.valueOf(listB.size()));
                OpAnalyticsUtil.getInstance().setEvent2nd(OperationKey.HEALTH_APP_SYNC_CRASH_2129061.value(), linkedHashMap);
                throw e;
            }
        }
        this.mUploadFailCount = 0;
    }

    private boolean uploadSequenceDataOnce(List<HiHealthData> list, long j) throws Throwable {
        if (mcx.h()) {
            int i = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i;
            if (i > 10) {
                this.mUploadFailCount += 2;
                ReleaseLogUtil.a(TAG, "uploadSequenceDataOnce uploadTotalCount");
                return false;
            }
        } else {
            int i2 = this.mUploadTotalCount + 1;
            this.mUploadTotalCount = i2;
            mcx.e(i2, this.mSyncOption.getSyncManual());
        }
        List<SampleSequence> listB = this.mMotionPathDataSwitchHelper.b(list);
        if (HiCommonUtil.a((List) listB)) {
            ReleaseLogUtil.a(TAG, "uploadTrackDataOnce cloudTrack is null or empty ");
            return false;
        }
        AddSampleSequenceReq addSampleSequenceReq = new AddSampleSequenceReq(listB);
        addSampleSequenceReq.setLocalMaxVersion(Long.valueOf(j));
        while (this.mUploadFailCount < 2) {
            AddSampleSequenceRsp addSampleSequenceRsp = (AddSampleSequenceRsp) pnq.c().a(addSampleSequenceReq.getUrl(), this.mParamsFactory.getHeaders(), pob.a(this.mParamsFactory.getBody(addSampleSequenceReq)), AddSampleSequenceRsp.class);
            if (addSampleSequenceRsp != null && addSampleSequenceRsp.getResultCode().intValue() == 1001) {
                ParamValidDetail paramValidDetail = addSampleSequenceRsp.getParamValidDetail();
                mcx.a(addSampleSequenceRsp, paramValidDetail);
                if (paramValidDetail != null) {
                    mcx.a(list, paramValidDetail, (Map<Long, List<Long>>) null);
                    uploadSequenceDone(list);
                    sequenceDownloadMore(addSampleSequenceReq, addSampleSequenceRsp, list.get(0).getType());
                    ReleaseLogUtil.c(TAG, "uploadSequenceDataOnce failed ! resultCode is ", addSampleSequenceRsp.getResultCode(), ", resultDesc is ", addSampleSequenceRsp.getResultDesc());
                    return true;
                }
                this.mUploadFailCount++;
            } else {
                if (mck.a(addSampleSequenceRsp, false)) {
                    uploadSequenceDone(list);
                    sequenceDownloadMore(addSampleSequenceReq, addSampleSequenceRsp, list.get(0).getType());
                    ReleaseLogUtil.c(TAG, "uploadSequenceDataOnce OK ! uploadCount is ", Integer.valueOf(this.mUploadTotalCount), ", date size = ", Integer.valueOf(list.size()), ",type,", Integer.valueOf(list.get(0).getType()));
                    return true;
                }
                this.mUploadFailCount++;
            }
        }
        ReleaseLogUtil.c(TAG, "uploadSequenceDataOnce failed ! uploadCount is ", Integer.valueOf(this.mUploadTotalCount), ", date size = ", Integer.valueOf(list.size()), ",type,", Integer.valueOf(list.get(0).getType()));
        return false;
    }

    private void uploadSequenceDone(List<HiHealthData> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int iC = 0;
        for (HiHealthData hiHealthData : list) {
            HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(com.huawei.haf.application.BaseApplication.b()).c(hiHealthData.getType() + hiHealthData.getSubType());
            if (hiHealthDictTypeC == null || hiHealthDictTypeC.e() == -1) {
                long dataId = hiHealthData.getDataId();
                long j = hiHealthData.getLong("modified_time");
                if (this.mDataSequenceManager.d(dataId, j)) {
                    iC = this.mDataSequenceManager.c(dataId, j);
                }
                long startTime = hiHealthData.getStartTime();
                ReleaseLogUtil.c(TAG, "uploadSequenceDone sID=", Long.valueOf(dataId), " trackTm=", Long.valueOf(startTime), " mdfdTm=", HiDateUtil.m(j), " update res=", Integer.valueOf(iC));
            } else {
                deleteTemporaryData(hiHealthData, hiHealthDictTypeC.e());
            }
        }
    }

    public void downloadByTime(int i, long j, long j2) throws mcm {
        if (j > j2 || j <= 0) {
            ReleaseLogUtil.a(TAG, "downloadByTime the time is not right");
            return;
        }
        ReleaseLogUtil.c(TAG, "downloadByTime is ", Integer.valueOf(i), ",", Long.valueOf(j), " ,", Long.valueOf(j2));
        GetHealthDataByTimeReq getHealthDataByTimeReqInitByTimeRep = initByTimeRep(i, j, j2);
        GetHealthDataByTimeRsp getHealthDataByTimeRspE = this.mHwCloudMgr.e(getHealthDataByTimeReqInitByTimeRep);
        if (!mck.a(getHealthDataByTimeRspE, false)) {
            ReleaseLogUtil.a(TAG, "pullDataByTime error");
            return;
        }
        List<HealthDetail> detailInfos = getHealthDataByTimeRspE.getDetailInfos();
        if (detailInfos == null || detailInfos.isEmpty()) {
            ReleaseLogUtil.a(TAG, "pullDataByTime data error");
        } else {
            if (savePointData(detailInfos, getHealthDataByTimeReqInitByTimeRep.getType().intValue(), 0L)) {
                return;
            }
            ReleaseLogUtil.a(TAG, "pullDataByTime save data error");
        }
    }

    public void downloadLatestPointData(int i, int i2, int i3) throws Throwable {
        ReleaseLogUtil.c(TAG, "downloadLatestData enter");
        GetHealthDataLatestReq getHealthDataLatestReq = new GetHealthDataLatestReq();
        getHealthDataLatestReq.setType(i3);
        getHealthDataLatestReq.setDataType(2);
        getHealthDataLatestReq.setTimestamp(HiDateUtil.j(System.currentTimeMillis()));
        if (i > 0) {
            getHealthDataLatestReq.setDays(i);
        }
        if (i2 > 0) {
            getHealthDataLatestReq.setCounts(i2);
        }
        GetHealthDataLatestRsp getHealthDataLatestRspB = this.mHwCloudMgr.b(getHealthDataLatestReq);
        if (!mck.a(getHealthDataLatestRspB, false)) {
            ReleaseLogUtil.a(TAG, "downloadLatestData warning");
            return;
        }
        List<HealthDetail> detailInfos = getHealthDataLatestRspB.getDetailInfos();
        if (okt.a(detailInfos)) {
            ReleaseLogUtil.a(TAG, "downloadLatestData dataList is Empty");
        } else {
            savePointData(detailInfos, i3, 0L);
            mdc.a().e(mdc.a(i3), "latestDataDownload", new lsn(this.mContext.getPackageName()));
        }
    }

    public void downloadLatestSequenceData(int i, int i2, int i3) throws Throwable {
        ReleaseLogUtil.c(TAG, "downloadLatestData enter");
        GetSampleSequenceLatestReq getSampleSequenceLatestReq = new GetSampleSequenceLatestReq();
        getSampleSequenceLatestReq.setType(i3);
        getSampleSequenceLatestReq.setDataType(2);
        if (i3 == DicDataTypeUtil$DataType.SLEEP_DETAILS.value()) {
            getSampleSequenceLatestReq.setTimestamp(HiDateUtil.n(System.currentTimeMillis()) + 1);
        } else {
            getSampleSequenceLatestReq.setTimestamp(HiDateUtil.j(System.currentTimeMillis()));
        }
        if (i > 0) {
            getSampleSequenceLatestReq.setDays(i);
        }
        if (i2 > 0) {
            getSampleSequenceLatestReq.setCounts(i2);
        }
        GetSampleSequenceLatestRsp getSampleSequenceLatestRsp = (GetSampleSequenceLatestRsp) pnq.c().a(getSampleSequenceLatestReq.getUrl(), this.mParamsFactory.getHeaders(), pob.a(this.mParamsFactory.getBody(getSampleSequenceLatestReq)), GetSampleSequenceLatestRsp.class);
        if (!mck.a(getSampleSequenceLatestRsp, false)) {
            ReleaseLogUtil.a(TAG, "downloadLatestData warning");
            return;
        }
        List<SampleSequence> detailInfos = getSampleSequenceLatestRsp.getDetailInfos();
        if (okt.a(detailInfos)) {
            ReleaseLogUtil.a(TAG, "downloadLatestData dataList is Empty");
            return;
        }
        saveSequenceData(detailInfos, true);
        if (i3 == 30001) {
            mdc.a().e(4, "latestDataDownload", new lsn(this.mContext.getPackageName()));
        }
    }

    public lob getOneSyncLocalVersion(int i) {
        if (HiCommonUtil.a((List) this.localVersions)) {
            return null;
        }
        for (lob lobVar : this.localVersions) {
            if (i == lobVar.d()) {
                return lobVar;
            }
        }
        return null;
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pullDataByTime(long j, long j2) throws mcm {
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pullDataByVersion() throws Throwable {
        ReleaseLogUtil.c(TAG, "pullDataByVersion begin");
        mcv.a(20.0d, "SYNC_DICTIONARY_DATA_DOWNLOAD_PERCENT_MAX");
        this.mSyncTypes = new ArrayList(1);
        if (HiHealthDictManager.d(this.mContext).c(this.mSyncOption.getSyncDataType()) != null) {
            this.mSyncTypes.add(Integer.valueOf(this.mSyncOption.getSyncDataType()));
        } else if (this.mSyncOption.getSyncDataType() == DicDataTypeUtil$DataType.ONE_SECOND_SPORT_DATA_TYPE.value()) {
            this.mSyncTypes = Collections.singletonList(Integer.valueOf(this.mSyncOption.getSyncDataType()));
        } else {
            this.mSyncTypes = HiHealthDictManager.d(this.mContext).a();
        }
        LogUtil.e(TAG, "dictionary health types = ", this.mSyncTypes);
        List<SyncKey> listB = this.mSyncVersionMgr.b(this.mSyncTypes);
        this.localVersions = this.mSyncAnchorMgr.c(this.mMainUserId, 0L, this.mSyncTypes, new int[]{HealthSyncCategory.SyncCategory.POINT.getValue(), HealthSyncCategory.SyncCategory.SEQUENCE.getValue(), HealthSyncCategory.SyncCategory.PERIODIC_REPORT.getValue()});
        if (HiCommonUtil.a((List) listB)) {
            ReleaseLogUtil.a(TAG, "dictionary data not exist in cloud");
            return;
        }
        ReleaseLogUtil.c(TAG, "pullDataByVersion syncKeys: ", HiJsonUtil.a(listB));
        double size = 1.0d / ((double) listB.size());
        for (SyncKey syncKey : listB) {
            mcv.e(this.mContext, 1.0d, size, 20.0d);
            if (syncKey.getType().intValue() == 30001 || syncKey.getType().intValue() == DicDataTypeUtil$DataType.ONE_SECOND_SPORT_DATA_TYPE.value()) {
                downloadSequenceDataByType(syncKey);
            } else if (!mcx.g(this.mContext, this.mMainUserId) && syncKey.getType().intValue() == DicDataTypeUtil$DataType.ALTITUDE_TYPE_SET.value()) {
                ReleaseLogUtil.a(TAG, "ALTITUDE_TYPE_SET isFirstSync");
            } else if (mcx.g(this.mContext, this.mMainUserId) || !mcx.c.containsKey(syncKey.getType())) {
                HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(syncKey.getType().intValue());
                if (hiHealthDictTypeC == null) {
                    ReleaseLogUtil.a(TAG, "dictType is null, syncKey is ", syncKey);
                } else if (hiHealthDictTypeC.b() == 0) {
                    downloadPointDataByType(syncKey);
                } else if (hiHealthDictTypeC.b() == 1) {
                    downloadSequenceDataByType(syncKey);
                } else if (hiHealthDictTypeC.b() == 6) {
                    downloadSamplePeriodicReportDataByType(syncKey);
                } else {
                    ReleaseLogUtil.a(TAG, "category error, syncKey is ", syncKey);
                }
            } else {
                ReleaseLogUtil.a(TAG, "FirstSync return ", syncKey.getType());
                if (syncKey.getVersion().longValue() != 0) {
                    long jH = HiDateUtil.h(System.currentTimeMillis());
                    downloadAllStatByTime(syncKey.getType().intValue(), mcx.e(mcx.d(jH, mcx.c.get(syncKey.getType()).c()), jH, 8));
                    if (!this.mSyncAnchorMgr.b(this.mMainUserId, syncKey.getType().intValue(), syncKey.getVersion().longValue(), 0L)) {
                        ReleaseLogUtil.a(TAG, "saveVersionToDB failed!", syncKey.getType());
                    }
                }
            }
        }
        ReleaseLogUtil.c(TAG, "pullDataByVersion end");
    }

    @Override // com.huawei.hihealthservice.sync.syncdata.HiSyncBase
    public void pushData() throws Throwable {
        ReleaseLogUtil.c(TAG, "pushData begin");
        if (!mao.o()) {
            ReleaseLogUtil.a(TAG, "dataPrivacy switch is closed, push end");
            return;
        }
        List<Integer> listE = lqb.a().e(this.mMainUserId);
        this.mSyncTypes = new ArrayList(1);
        if (HiHealthDictManager.d(this.mContext).c(this.mSyncOption.getSyncDataType()) != null) {
            this.mSyncTypes.add(Integer.valueOf(this.mSyncOption.getSyncDataType()));
        } else {
            this.mSyncTypes = HiHealthDictManager.d(this.mContext).c();
        }
        if (HiCommonUtil.a((List) listE)) {
            ReleaseLogUtil.c(TAG, "no need upload data");
            if (this.mSyncTypes.size() == 1) {
                mcr.e(this.mContext, this.mMainUserId, this.mSyncTypes.get(0).intValue());
                return;
            }
            return;
        }
        ArrayList arrayList = new ArrayList(this.mSyncTypes.size());
        ArrayList arrayList2 = new ArrayList(this.mSyncTypes.size());
        ArrayList arrayList3 = new ArrayList(this.mSyncTypes.size());
        for (Integer num : this.mSyncTypes) {
            HiHealthDictType hiHealthDictTypeC = HiHealthDictManager.d(this.mContext).c(num.intValue());
            if (hiHealthDictTypeC == null) {
                ReleaseLogUtil.a(TAG, "dictType=null,typeId=", num);
            } else if (hiHealthDictTypeC.b() == 1) {
                arrayList2.add(num);
            } else if (hiHealthDictTypeC.b() == 0) {
                arrayList.add(num);
            } else if (hiHealthDictTypeC.b() == 6) {
                arrayList3.add(num);
            } else {
                ReleaseLogUtil.a(TAG, "typeId needn't syncCloud,typeId=", num);
            }
        }
        uploadSampleSequenceData(listE, arrayList2);
        uploadSamplePointData(listE, arrayList);
        uploadSamplePeriodicReportData(listE, arrayList3);
        ReleaseLogUtil.c(TAG, "pushData end");
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    public void uploadSamplePeriodicReportData(List<Integer> list, List<Integer> list2) throws mcm {
        if (HiCommonUtil.a((List) list2)) {
            return;
        }
        List<Integer> listC = mcx.c("report_type", 0, list);
        if (HiCommonUtil.a((List) listC)) {
            return;
        }
        List<Integer> listC2 = mcx.c("client_id", 0, list);
        if (HiCommonUtil.a((List) listC2)) {
            return;
        }
        ReleaseLogUtil.c(TAG, "uploadSamplePeriodicReportData syncClients: ", HiJsonUtil.a(listC2));
        Iterator<Integer> it = listC.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            List<Integer> listJ = HiHealthDictManager.d(this.mContext).j(iIntValue);
            Iterator<Integer> it2 = listC2.iterator();
            while (it2.hasNext()) {
                uploadPeriodicReportData(it2.next().intValue(), iIntValue, listJ);
            }
        }
    }
}
