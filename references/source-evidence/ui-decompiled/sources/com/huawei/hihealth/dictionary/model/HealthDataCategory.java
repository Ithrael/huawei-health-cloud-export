package com.huawei.hihealth.dictionary.model;

import com.huawei.hihealth.dictionary.utils.DictUtils;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: /work/unpacked/classes2.dex */
public class HealthDataCategory {
    private static final Set<Integer> ALL_TYPES;
    public static final int REALTIME = 3;
    public static final int SAMPLE_PERIODIC_REPORT = 6;
    public static final int SAMPLE_POINT = 0;
    public static final int SEQUENCE = 1;
    public static final int STATISTICS = 2;
    public static final int UNKNOWN = -1;

    static {
        HashSet hashSet = new HashSet();
        ALL_TYPES = hashSet;
        hashSet.addAll(DictUtils.b((Class<?>) HealthDataCategory.class));
    }

    public static boolean validate(Integer num) {
        return ALL_TYPES.contains(num);
    }
}
