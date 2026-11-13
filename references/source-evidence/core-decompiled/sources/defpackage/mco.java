package defpackage;

import android.content.Context;
import android.text.TextUtils;
import androidx.health.connect.client.records.metadata.Metadata;
import com.google.gson.JsonSyntaxException;
import com.huawei.hihealth.HiHealthData;
import com.huawei.hihealth.data.model.EcgMetaData;
import com.huawei.hihealth.dictionary.HiHealthDictManager;
import com.huawei.hihealth.util.HiJsonUtil;
import com.huawei.hihealthservice.sync.syncdata.Electrocardiogram;
import com.huawei.hihealthservice.sync.syncdata.HiSyncBloodOxygenRemind;
import com.huawei.hihealthservice.sync.syncdata.HiSyncBloodOxygenSaturation;
import com.huawei.hihealthservice.sync.syncdata.HiSyncBodyThermometer;
import com.huawei.hihealthservice.sync.syncdata.HiSyncUricAcid;
import com.huawei.hihealthservice.sync.util.CloudIntensity;
import com.huawei.hwcloudmodel.model.samplepoint.SamplePoint;
import com.huawei.hwcloudmodel.model.unite.HealthDetail;
import com.huawei.hwcloudmodel.model.unite.SportBasicInfo;
import com.huawei.hwlogsmodel.LogUtil;
import health.compact.a.LogAnonymous;
import health.compact.a.hwlogsmodel.ReleaseLogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public class mco {
    public static int a(int i) {
        int iE = mcj.e(i);
        return (iE != -1 || HiHealthDictManager.d((Context) null).c(i) == null) ? iE : i;
    }

    public static HiSyncBloodOxygenSaturation a(String str) {
        try {
            return (HiSyncBloodOxygenSaturation) HiJsonUtil.d(str, HiSyncBloodOxygenSaturation.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBloodOxygenValue JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBloodOxygenValue Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    public static SamplePoint a(String str, String str2, long j, long j2, String str3) {
        SamplePoint samplePoint = new SamplePoint();
        samplePoint.setKey(str);
        samplePoint.setValue(str2);
        samplePoint.setStartTime(Long.valueOf(j));
        samplePoint.setEndTime(Long.valueOf(j2));
        samplePoint.setUnit(str3);
        return samplePoint;
    }

    public static SamplePoint a(String str, String str2, long j, long j2, String str3, String str4) {
        SamplePoint samplePoint = new SamplePoint();
        samplePoint.setKey(str);
        samplePoint.setValue(str2);
        samplePoint.setStartTime(Long.valueOf(j));
        samplePoint.setEndTime(Long.valueOf(j2));
        samplePoint.setUnit(str3);
        samplePoint.setFieldsModifyTime(str4);
        return samplePoint;
    }

    public static HealthDetail a(HiHealthData hiHealthData, int i) {
        SamplePoint samplePointJ;
        if (hiHealthData == null || i <= -1) {
            return null;
        }
        HealthDetail healthDetail = new HealthDetail();
        ArrayList arrayList = new ArrayList(10);
        if (i == 4) {
            samplePointJ = j(hiHealthData);
            healthDetail.setMetadata(hiHealthData.getMetaData());
        } else if (i != 19) {
            samplePointJ = c(hiHealthData, i);
        } else {
            samplePointJ = f(hiHealthData);
            healthDetail.setMetadata(hiHealthData.getMetaData());
        }
        if (samplePointJ == null) {
            return null;
        }
        arrayList.add(samplePointJ);
        healthDetail.setStartTime(Long.valueOf(hiHealthData.getStartTime()));
        healthDetail.setEndTime(Long.valueOf(hiHealthData.getEndTime()));
        healthDetail.setTimeZone(hiHealthData.getTimeZone());
        healthDetail.setType(Integer.valueOf(i));
        healthDetail.setSamplePoints(arrayList);
        healthDetail.setDataId(hiHealthData.getDataId());
        return healthDetail;
    }

    public static String a(HiHealthData hiHealthData) {
        if (hiHealthData == null) {
            return Metadata.EMPTY_ID;
        }
        Electrocardiogram electrocardiogram = new Electrocardiogram();
        EcgMetaData ecgMetaData = (EcgMetaData) HiJsonUtil.d(hiHealthData.getMetaData(), EcgMetaData.class);
        if (ecgMetaData == null) {
            LogUtil.g("HiDataSwitchUtil", "getCloudEcgStr metadata null");
            return Metadata.EMPTY_ID;
        }
        electrocardiogram.setAverageHeartRate(ecgMetaData.getAverageHeartRate());
        electrocardiogram.setEcgArrhyType(ecgMetaData.getEcgArrhyType());
        electrocardiogram.setEcgDataLength(ecgMetaData.getEcgDataLength());
        electrocardiogram.setUserSymptom(ecgMetaData.getUserSymptom());
        electrocardiogram.setDetails(hiHealthData.getSequenceData());
        List<Float> listB = b(hiHealthData);
        if (okt.c(listB)) {
            electrocardiogram.setEcgDataList(listB);
        }
        electrocardiogram.setMetadata(HiJsonUtil.a(ecgMetaData));
        return HiJsonUtil.a(electrocardiogram);
    }

    public static List<HealthDetail> a(List<HiHealthData> list, int i, long j) {
        SamplePoint samplePointC;
        if (okt.a(list)) {
            return null;
        }
        ArrayList arrayList = new ArrayList(list.size());
        for (HiHealthData hiHealthData : list) {
            if (hiHealthData != null && (samplePointC = c(hiHealthData, i)) != null) {
                ArrayList arrayList2 = new ArrayList(1);
                arrayList2.add(samplePointC);
                HealthDetail healthDetail = new HealthDetail();
                healthDetail.setType(Integer.valueOf(i));
                healthDetail.setTimeZone(hiHealthData.getTimeZone());
                healthDetail.setDeviceCode(Long.valueOf(j));
                healthDetail.setDataId(hiHealthData.getDataId());
                if (i == 9 || i == 12 || i == 15) {
                    healthDetail.setMergedFlag(Integer.valueOf(hiHealthData.getMergedStatus()));
                }
                e(arrayList, hiHealthData.getEndTime(), hiHealthData.getStartTime(), healthDetail, arrayList2);
            }
        }
        return arrayList;
    }

    public static lnv a(int i, double d, int i2) {
        lnv lnvVar = new lnv();
        lnvVar.a(d);
        lnvVar.d(i);
        lnvVar.j(i2);
        return lnvVar;
    }

    public static int b(int i) {
        int iA = mcj.a(i);
        return (iA != -1 || HiHealthDictManager.d((Context) null).c(i) == null) ? iA : i;
    }

    public static HiSyncBloodOxygenRemind b(String str) {
        try {
            return (HiSyncBloodOxygenRemind) HiJsonUtil.d(str, HiSyncBloodOxygenRemind.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBloodOxygenRemind JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBloodOxygenRemind Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    public static SportBasicInfo b(Integer num, Integer num2, Integer num3, Float f, Integer num4, Integer num5, Integer num6) {
        SportBasicInfo sportBasicInfo = new SportBasicInfo();
        sportBasicInfo.configAltitude(f.floatValue());
        sportBasicInfo.configCalorie(num3.intValue());
        sportBasicInfo.configDistance(num2.intValue());
        sportBasicInfo.configDuration(num5.intValue());
        sportBasicInfo.configFloor(num4.intValue());
        sportBasicInfo.configSteps(num.intValue());
        sportBasicInfo.configCount(num6.intValue());
        return sportBasicInfo;
    }

    private static List b(HiHealthData hiHealthData) {
        String simpleData = hiHealthData.getSimpleData();
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(simpleData)) {
            try {
                Float[] fArr = (Float[]) HiJsonUtil.d(simpleData, Float[].class);
                if (fArr != null) {
                    return Arrays.asList(fArr);
                }
            } catch (JsonSyntaxException e) {
                ReleaseLogUtil.a("HiH_HiDataSwitchUtil", "simpleData parse fail", LogAnonymous.c(e));
            }
        }
        return arrayList;
    }

    public static int c(int i) {
        return mcj.b(i);
    }

    public static CloudIntensity c(String str) {
        try {
            return (CloudIntensity) HiJsonUtil.d(str, CloudIntensity.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getCloudIntensityValue JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getCloudIntensityValue Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0065  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.huawei.hwcloudmodel.model.samplepoint.SamplePoint c(com.huawei.hihealth.HiHealthData r9, int r10) {
        /*
            r0 = 0
            if (r9 != 0) goto L4
            return r0
        L4:
            long r3 = r9.getStartTime()
            long r5 = r9.getEndTime()
            int r1 = r9.getType()
            java.lang.String r1 = f(r1)
            if (r1 != 0) goto L17
            return r0
        L17:
            r0 = 3
            if (r10 == r0) goto L65
            r0 = 9
            if (r10 == r0) goto L59
            r0 = 18
            if (r10 == r0) goto L54
            r0 = 21
            if (r10 == r0) goto L4f
            switch(r10) {
                case 11: goto L4a;
                case 12: goto L40;
                case 13: goto L4a;
                case 14: goto L4a;
                case 15: goto L3b;
                case 16: goto L32;
                default: goto L29;
            }
        L29:
            double r7 = r9.getValue()
            java.lang.String r10 = java.lang.Double.toString(r7)
            goto L67
        L32:
            double r7 = r9.getValue()
            java.lang.String r10 = e(r7)
            goto L67
        L3b:
            java.lang.String r10 = a(r9)
            goto L67
        L40:
            double r7 = r9.getValue()
            int r10 = (int) r7
            java.lang.String r10 = d(r10)
            goto L67
        L4a:
            java.lang.String r10 = r9.getMetaData()
            goto L67
        L4f:
            java.lang.String r10 = d(r9)
            goto L67
        L54:
            java.lang.String r10 = c(r9)
            goto L67
        L59:
            java.lang.String r10 = r9.getMetaData()
            if (r10 != 0) goto L60
            goto L65
        L60:
            java.lang.String r10 = r9.getMetaData()
            goto L67
        L65:
            java.lang.String r10 = ""
        L67:
            r2 = r10
            int r9 = r9.getPointUnit()
            java.lang.String r7 = java.lang.Integer.toString(r9)
            com.huawei.hwcloudmodel.model.samplepoint.SamplePoint r9 = a(r1, r2, r3, r5, r7)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: defpackage.mco.c(com.huawei.hihealth.HiHealthData, int):com.huawei.hwcloudmodel.model.samplepoint.SamplePoint");
    }

    public static String c(HiHealthData hiHealthData) {
        return hiHealthData == null ? Metadata.EMPTY_ID : HiJsonUtil.a(new HiSyncBodyThermometer(hiHealthData.getValue()));
    }

    private static int d(String str, String str2) {
        return mcj.b(str, str2);
    }

    public static HiHealthData d(SamplePoint samplePoint) {
        if (samplePoint == null) {
            return null;
        }
        int iD = d(samplePoint.getKey(), samplePoint.getValue());
        if (iD <= 0) {
            LogUtil.g("HiDataSwitchUtil", "switchSportSamplePoint so such type");
            return null;
        }
        HiHealthData hiHealthData = new HiHealthData();
        try {
            hiHealthData.setValue(Double.parseDouble(samplePoint.getValue()));
            hiHealthData.setType(iD);
            hiHealthData.setTimeInterval(samplePoint.getStartTime().longValue(), samplePoint.getEndTime().longValue());
            hiHealthData.setPointUnit(0);
            LogUtil.e("HiDataSwitchUtil", "switchSportSamplePoint hiHealthData is ", hiHealthData);
            return hiHealthData;
        } catch (NumberFormatException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "switchSportSamplePoint NumberFormatException", LogAnonymous.c(e));
            return null;
        }
    }

    public static HiSyncBodyThermometer d(String str) {
        try {
            return (HiSyncBodyThermometer) HiJsonUtil.d(str, HiSyncBodyThermometer.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBodyThermometer JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncBodyThermometer Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    public static String d(int i) {
        return HiJsonUtil.a(new CloudIntensity(i));
    }

    public static String d(HiHealthData hiHealthData) {
        return hiHealthData == null ? Metadata.EMPTY_ID : HiJsonUtil.a(new HiSyncUricAcid(hiHealthData.getIntValue()));
    }

    public static Electrocardiogram e(String str) {
        try {
            return (Electrocardiogram) HiJsonUtil.d(str, Electrocardiogram.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncEcgValue JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncEcgValue Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    public static String e(double d) {
        return HiJsonUtil.a(new HiSyncBloodOxygenSaturation(d));
    }

    public static String e(int i) {
        return mcj.c(i);
    }

    public static String e(HiHealthData hiHealthData) {
        return hiHealthData == null ? Metadata.EMPTY_ID : HiJsonUtil.a(new HiSyncBloodOxygenRemind(hiHealthData.getIntValue()));
    }

    public static List<HiHealthData> e(SamplePoint samplePoint, HealthDetail healthDetail) {
        if (samplePoint == null || healthDetail == null) {
            return null;
        }
        return mcp.b(samplePoint, healthDetail);
    }

    private static void e(List<HealthDetail> list, long j, long j2, HealthDetail healthDetail, List<SamplePoint> list2) {
        if (list == null) {
            return;
        }
        healthDetail.setStartTime(Long.valueOf(j2));
        healthDetail.setEndTime(Long.valueOf(j));
        healthDetail.setSamplePoints(list2);
        list.add(healthDetail);
    }

    private static SamplePoint f(HiHealthData hiHealthData) {
        long startTime = hiHealthData.getStartTime();
        long endTime = hiHealthData.getEndTime();
        String strF = f(hiHealthData.getType());
        if (strF == null) {
            return null;
        }
        return a(strF, e(hiHealthData), startTime, endTime, Integer.toString(hiHealthData.getPointUnit()));
    }

    private static String f(int i) {
        return mcj.d(i);
    }

    public static HiSyncUricAcid j(String str) {
        try {
            return (HiSyncUricAcid) HiJsonUtil.d(str, HiSyncUricAcid.class);
        } catch (JsonSyntaxException e) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncUricAcid JsonSyntaxException", LogAnonymous.c(e));
            return null;
        } catch (Exception e2) {
            ReleaseLogUtil.d("HiH_HiDataSwitchUtil", "getSyncUricAcid Exception", LogAnonymous.c(e2));
            return null;
        }
    }

    private static SamplePoint j(HiHealthData hiHealthData) {
        String strF = f(hiHealthData.getType());
        if (strF == null) {
            return null;
        }
        String strE = e(hiHealthData.getType());
        mcn mcnVar = new mcn();
        mcnVar.a(strE, hiHealthData.getValue());
        String strA = mcnVar.a();
        if (strA == null) {
            return null;
        }
        mcn mcnVar2 = new mcn();
        mcnVar2.a(strE, hiHealthData.getModifiedTime());
        mcnVar2.a("mIsConfirmed", hiHealthData.getModifiedTime());
        return a(strF, strA, hiHealthData.getStartTime(), hiHealthData.getEndTime(), Integer.toString(hiHealthData.getPointUnit()), mcnVar2.a());
    }

    public static boolean j(int i) {
        return mcj.j(i);
    }
}
