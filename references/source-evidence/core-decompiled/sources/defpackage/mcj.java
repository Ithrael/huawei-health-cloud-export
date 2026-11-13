package defpackage;

import android.text.TextUtils;
import androidx.collection.ArrayMap;
import com.huawei.hihealth.data.type.HiHealthDataType;
import com.huawei.hihealth.dictionary.utils.DicDataTypeUtil$DataType;
import java.util.Map;

/* JADX INFO: loaded from: classes10.dex */
public class mcj {
    private static volatile Map<Integer, String> a;
    private static volatile Map<Integer, Integer> b;
    private static volatile Map<Integer, String> c;
    private static volatile Map<String, Integer> d;
    private static volatile Map<Integer, Integer> e;
    private static volatile Map<Integer, Integer> g;

    static int a(int i) {
        if (b == null) {
            b = new ArrayMap<Integer, Integer>() { // from class: mcj.1
                {
                    put(15, 31001);
                    put(5000, 61001);
                    put(5001, 61002);
                }
            };
        }
        Integer num = b.get(Integer.valueOf(i));
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    private static void a() {
        if (d != null) {
            return;
        }
        d = new ArrayMap<String, Integer>() { // from class: mcj.3
            {
                put("BLOODGLUCOSE_BLOODSUGAR", 0);
                put("DATA_POINT_DYNAMIC_HEARTRATE", 2002);
                put("DATA_POINT_REST_HEARTRATE", 2018);
                put("DATA_POINT_NEW_REST_HEARTRATE", 2105);
                put("SLEEP_DEEP", 22001);
                put("SLEEP_LIGHT", 22002);
                put("SLEEP_AWAKE", 22003);
                put("PROFESSIONAL_SLEEP_SHALLOW", 22101);
                put("PROFESSIONAL_SLEEP_DREAM", 22102);
                put("PROFESSIONAL_SLEEP_DEEP", 22103);
                put("PROFESSIONAL_SLEEP_WAKE", 22104);
                put("PROFESSIONAL_SLEEP_NOON", 22105);
                put("PROFESSIONAL_SLEEP_BED", 22106);
                put("PROFESSIONAL_SLEEP_ON", 22107);
                put("STRESS_PASSIVE_MEASUREMENT", 2019);
                put("STRESS_POSITIVE_MEASUREMENT", 2020);
                put("BREATHING_RELAXATION", 2021);
                put("STRESS_DATA", 2034);
                put("BIOFEEDBACK_GAMES", 2036);
                put("RELAX_ASSESSMENT", 2035);
                put("BREATHE_TRAINING", 2037);
                put("EXERCISE_INTENSITY", 7);
                put("HEART_RATE_RISE_ALARM", 2101);
                put("BRADYCARDIA_ALARM", 2102);
                put("BLOOD_OXYGEN_SATURATION", 2103);
                put("ECG", 31001);
                put("BODY_THERMOMETER", 2104);
                put("BLOOD_OXYGEN_REMIND", 2107);
                put("CONTINUE_BLOOD_SUGAR", 2108);
                put("URIC_ACID", 2109);
                put("BLOOD_SUGAR_BEFORE_DAWN", 2015);
                put("BLOOD_SUGAR_BF_BEFORE", 2008);
                put("BLOOD_SUGAR_BF_AFTER", 2009);
                put("BLOOD_SUGAR_LC_BEFORE", 2010);
                put("BLOOD_SUGAR_LC_AFTER", 2011);
                put("BLOOD_SUGAR_DN_BEFORE", 2012);
                put("BLOOD_SUGAR_DN_AFTER", 2013);
                put("BLOOD_SUGAR_SL_BEFORE", 2014);
                put("BLOOD_SUGAR_RANDOM", 2106);
            }
        };
    }

    static int b(int i) {
        if (e == null) {
            e = new ArrayMap<Integer, Integer>() { // from class: mcj.8
                {
                    put(Integer.valueOf(DicDataTypeUtil$DataType.ELECTROCARDIOGRAM.value()), 61002);
                }
            };
        }
        Integer num = e.get(Integer.valueOf(i));
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    static int b(String str, String str2) {
        if (str == null || str2 == null) {
            return 0;
        }
        a();
        Integer num = d.get(str);
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    private static void b() {
        if (c != null) {
            return;
        }
        c = new ArrayMap<Integer, String>() { // from class: mcj.5
            {
                put(2015, "BLOOD_SUGAR_BEFORE_DAWN");
                put(2008, "BLOOD_SUGAR_BF_BEFORE");
                put(2009, "BLOOD_SUGAR_BF_AFTER");
                put(2010, "BLOOD_SUGAR_LC_BEFORE");
                put(2011, "BLOOD_SUGAR_LC_AFTER");
                put(2012, "BLOOD_SUGAR_DN_BEFORE");
                put(2013, "BLOOD_SUGAR_DN_AFTER");
                put(2014, "BLOOD_SUGAR_SL_BEFORE");
                put(2106, "BLOOD_SUGAR_RANDOM");
            }
        };
    }

    static int c(String str) {
        if (TextUtils.isEmpty(str)) {
            return 2015;
        }
        a();
        Integer num = d.get(str);
        if (num != null) {
            return num.intValue();
        }
        return 2015;
    }

    static String c(int i) {
        b();
        String str = c.get(Integer.valueOf(i));
        return str != null ? str : "BLOOD_SUGAR_BEFORE_DAWN";
    }

    static String d(int i) {
        d();
        return a.get(Integer.valueOf(i));
    }

    private static void d() {
        if (a != null) {
            return;
        }
        a = new ArrayMap<Integer, String>() { // from class: mcj.2
            {
                put(2002, "DATA_POINT_DYNAMIC_HEARTRATE");
                put(2018, "DATA_POINT_REST_HEARTRATE");
                put(2105, "DATA_POINT_NEW_REST_HEARTRATE");
                put(22001, "SLEEP_DEEP");
                put(22002, "SLEEP_LIGHT");
                put(22003, "SLEEP_AWAKE");
                put(22101, "PROFESSIONAL_SLEEP_SHALLOW");
                put(22102, "PROFESSIONAL_SLEEP_DREAM");
                put(22103, "PROFESSIONAL_SLEEP_DEEP");
                put(22104, "PROFESSIONAL_SLEEP_WAKE");
                put(22105, "PROFESSIONAL_SLEEP_NOON");
                put(22106, "PROFESSIONAL_SLEEP_BED");
                put(22107, "PROFESSIONAL_SLEEP_ON");
                put(2015, "BLOODGLUCOSE_BLOODSUGAR");
                put(2008, "BLOODGLUCOSE_BLOODSUGAR");
                put(2009, "BLOODGLUCOSE_BLOODSUGAR");
                put(2010, "BLOODGLUCOSE_BLOODSUGAR");
                put(2011, "BLOODGLUCOSE_BLOODSUGAR");
                put(2012, "BLOODGLUCOSE_BLOODSUGAR");
                put(2013, "BLOODGLUCOSE_BLOODSUGAR");
                put(2014, "BLOODGLUCOSE_BLOODSUGAR");
                put(2106, "BLOODGLUCOSE_BLOODSUGAR");
                put(2019, "STRESS_PASSIVE_MEASUREMENT");
                put(2020, "STRESS_POSITIVE_MEASUREMENT");
                put(2021, "BREATHING_RELAXATION");
                put(2034, "STRESS_DATA");
                put(2037, "BREATHE_TRAINING");
                put(2036, "BIOFEEDBACK_GAMES");
                put(2035, "RELAX_ASSESSMENT");
                put(7, "EXERCISE_INTENSITY");
                put(2101, "HEART_RATE_RISE_ALARM");
                put(2102, "BRADYCARDIA_ALARM");
                put(2103, "BLOOD_OXYGEN_SATURATION");
                put(31001, "ECG");
                put(2104, "BODY_THERMOMETER");
                put(2107, "BLOOD_OXYGEN_REMIND");
                put(2108, "CONTINUE_BLOOD_SUGAR");
                put(2109, "URIC_ACID");
            }
        };
    }

    static int e(int i) {
        e();
        Integer num = g.get(Integer.valueOf(i));
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    private static void e() {
        if (g != null) {
            return;
        }
        g = new ArrayMap<Integer, Integer>() { // from class: mcj.4
            {
                put(2002, 7);
                put(2018, 7);
                put(2105, 7);
                put(22001, 3);
                put(22002, 3);
                put(22003, 3);
                put(22101, 9);
                put(22102, 9);
                put(22103, 9);
                put(22104, 9);
                put(22105, 9);
                put(22106, 9);
                put(22107, 9);
                put(2015, 4);
                put(2008, 4);
                put(2009, 4);
                put(2010, 4);
                put(2011, 4);
                put(2012, 4);
                put(2013, 4);
                put(2014, 4);
                put(2106, 4);
                put(2104, 18);
                put(2107, 19);
                put(31001, 15);
                put(2109, 21);
                put(2034, 11);
                put(2019, 11);
                put(2020, 11);
                put(2103, 16);
            }
        };
    }

    static boolean j(int i) {
        int[] iArr = {2015, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2106, 2104, 2107, 31001, 2103, 21, 2034, 2002, 2019, 2020, 2018, 2105};
        for (int i2 = 0; i2 < 20; i2++) {
            if (iArr[i2] == i) {
                return true;
            }
        }
        return HiHealthDataType.p(i) || HiHealthDataType.f(i);
    }
}
