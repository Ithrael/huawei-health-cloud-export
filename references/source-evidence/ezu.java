public class Lezu; {
    private static final Object d;
    private static volatile ezu e;
    private final java.util.Map c;
    private final android.content.Context b;
    private com.huawei.health.ecologydevice.open.data.model.RopePerformance i;
    private String a;
    private float f;

    private ezu()
    {
        java.util.HashMap v0_1 = new java.util.HashMap(10);
        this.c = v0_1;
        this.b = com.huawei.hwcommonmodel.application.BaseApplication.getContext();
        v0_1.put("maxSkipSpeedRank", "fastedRopeSpeed");
        v0_1.put("enduranceAbilityRank", "ropeSpeedEndurance");
        v0_1.put("enduranceTimeAbilityRank", "ropeEnduranceTime");
        v0_1.put("skipNumAbilityRank", "ropeSkipNum");
        v0_1.put("maxSkippingTimesAbilityRank", "maxRopeSkippingTimes");
        return;
    }

    private void b(com.huawei.health.ecologydevice.open.data.model.RopeBestRecord$BestRecordData p17, com.huawei.hwbasemgr.IBaseResponseCallback p18)
    {
        if (p17 != null) {
            int v5_0 = ((float) this.a(p17.getBestRopeSkippingMaxSpeed1MIN()));
            float v6_1 = this.e("maxSkipSpeedRank", v5_0, com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE);
            int v7_4 = (((float) this.a(p17.getBestRopeSkippingEnduranceTimeAbility())) / 1114636288);
            float v8_4 = this.e("enduranceTimeAbilityRank", ((float) this.a(p17.getBestRopeSkippingEnduranceTimeAbility())), com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE);
            int v9_3 = ((float) this.a(p17.getBestRopeSkippingContinuousCount()));
            float v10_2 = this.e("maxSkippingTimesAbilityRank", v9_3, com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE);
            float[] v11_3 = ((float) this.a(p17.getBestRopeSkippingSingleCount()));
            float v12_1 = this.e("skipNumAbilityRank", v11_3, com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE);
            float v13_3 = ((float) this.a(p17.getBestRopeSkippingEnduranceAbility()));
            float[] v15 = new float[5];
            v15[0] = v5_0;
            v15[1] = v7_4;
            v15[2] = v9_3;
            v15[3] = v11_3;
            v15[4] = v13_3;
            androidx.core.util.Pair v3_2 = this.e("enduranceAbilityRank", v13_3, com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE);
            float[] v11_6 = new float[5];
            v11_6[0] = v6_1;
            v11_6[1] = v8_4;
            v11_6[2] = v10_2;
            v11_6[3] = v12_1;
            v11_6[4] = v3_2;
            if ((!this.d(v15)) && (!this.d(v11_6))) {
                p18.onResponse(0, new androidx.core.util.Pair(v15, v11_6));
                return;
            } else {
                com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"value or rank is invalid value"}));
                p18.onResponse(-1, 0);
                return;
            }
        } else {
            com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"convertBestData() bestRecordData is null"}));
            p18.onResponse(-1, 0);
            return;
        }
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopeBestRecordBean;", ">;)I"})
    private int a(java.util.List p3)
    {
        if (!okt.a(p3)) {
            java.util.Collections.sort(p3, new ezs());
            return ((com.huawei.health.ecologydevice.open.data.model.RopeBestRecordBean) p3.get(0)).getValue();
        } else {
            return 0;
        }
    }

    private boolean d(float[] p6)
    {
        int v2 = 0;
        while (v2 < p6.length) {
            if (fgx.e(p6[v2], 0) <= 0) {
                v2++;
            } else {
                return 0;
            }
        }
        return 1;
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Rank;", ">;)F"})
    private float d(java.util.List p21)
    {
        com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"enter calculateRank"}));
        java.util.Collections.sort(p21, new fab());
        java.math.BigDecimal v1_3 = new java.util.ArrayList();
        String v4_1 = new java.util.ArrayList();
        Float v3_0 = p21.iterator();
        while (v3_0.hasNext()) {
            int v5_9 = ((com.huawei.health.ecologydevice.open.data.model.RopePerformance$Rank) v3_0.next());
            v1_3.add(Float.valueOf(v5_9.getThreshold()));
            v4_1.add(Float.valueOf(v5_9.getValue()));
        }
        Float v3_2 = v1_3.size();
        int v5_5 = 0;
        int v7 = 0;
        float v8_5 = 0;
        while (v7 < v3_2) {
            java.math.BigDecimal v1_9;
            java.math.BigDecimal v18_0;
            if (fgx.e(((Float) v1_3.get(v5_5)).floatValue(), this.f) != 1) {
                if (fgx.e(((Float) v1_3.get((v3_2 - 1))).floatValue(), this.f) != -1) {
                    if (fgx.e(((Float) v1_3.get(v7)).floatValue(), this.f) != 0) {
                        if (fgx.e(((Float) v1_3.get(v7)).floatValue(), this.f) != 1) {
                            v18_0 = v1_3;
                            com.huawei.hwlogsmodel.LogUtil.e("RopePerformanceHelper", new Object[] {"Unknown!"}));
                            v1_9 = 0;
                        } else {
                            float v8_9 = ((Float) v1_3.get(v7)).floatValue();
                            java.math.BigDecimal v9_20 = (v7 - 1);
                            Float v16_1 = ((Float) v4_1.get(v9_20));
                            int v5_12 = v16_1.floatValue();
                            java.math.BigDecimal v6_0 = ((Float) v4_1.get(v7)).floatValue();
                            String v13 = ", maxValue = ";
                            v18_0 = v1_3;
                            java.math.BigDecimal v1_2 = ((Float) v1_3.get(v9_20)).floatValue();
                            com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"maxThreshold = ", v16_1}));
                            java.math.BigDecimal v9_2 = new java.math.BigDecimal(((double) v8_9));
                            float v8_1 = new java.math.BigDecimal(((double) v1_2));
                            java.math.BigDecimal v1_5 = new java.math.BigDecimal(((double) v6_0));
                            java.math.BigDecimal v6_2 = new java.math.BigDecimal(((double) v5_12));
                            int v5_3 = this.e(new java.math.BigDecimal(((double) this.f)).subtract(v8_1), v9_2.subtract(v8_1));
                            if (!java.math.BigDecimal.ZERO.equals(v5_3)) {
                                v8_5 = v5_3.multiply(v1_5.subtract(v6_2)).add(v6_2).floatValue();
                            } else {
                                v1_9 = 0;
                                v8_5 = 0;
                            }
                        }
                    } else {
                        v8_5 = ((Float) v4_1.get(v7)).floatValue();
                        v18_0 = v1_3;
                    }
                } else {
                    v8_5 = 1120403456;
                }
            } else {
                v8_5 = 1065353216;
            }
            if (v8_5 == v1_9) {
                v7++;
                v1_3 = v18_0;
                v5_5 = 0;
            } else {
                com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"calculateRank value = ", Float.valueOf(v8_5), ", score = ", Float.valueOf(this.f)}));
                return v8_5;
            }
        }
        return v8_5;
    }

    private void b(com.huawei.hwbasemgr.IBaseResponseCallback p5)
    {
        com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"enter handleRankData"}));
        java.util.List v0_9 = this.i;
        if (v0_9 != null) {
            if (v0_9.getResultCode() == 0) {
                java.util.List v0_3 = this.i.getPopulationStats();
                if (!this.c(this.e(), v0_3)) {
                    com.huawei.hwlogsmodel.LogUtil.e("RopePerformanceHelper", new Object[] {"handleRankData same cache current data."}));
                } else {
                    this.b(v0_3);
                }
                if (okt.a(this.c())) {
                    this.e(v0_3);
                }
                p5.onResponse(0, v0_3);
                return;
            } else {
                com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"Failed to handleRankData."}));
                p5.onResponse(-1, 0);
                return;
            }
        } else {
            com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"mRopePerformance is null."}));
            p5.onResponse(-1, 0);
            return;
        }
    }

    private java.math.BigDecimal e(java.math.BigDecimal p3, java.math.BigDecimal p4)
    {
        try {
            return p3.divide(p4, 1, 4);
        } catch (ArithmeticException) {
            return java.math.BigDecimal.ZERO;
        }
    }

    private long a(int p3)
    {
        long v0_0 = java.util.Calendar.getInstance();
        v0_0.add(2, p3);
        v0_0.set(5, 1);
        v0_0.set(11, 0);
        v0_0.set(12, 0);
        v0_0.set(13, 0);
        v0_0.set(14, 0);
        return v0_0.getTimeInMillis();
    }

    @Signature(value={"(", "Ljava/util/Map<", "Ljava/lang/String;", "Ljava/lang/Object;", ">;)", "Ljava/util/Map<", "Ljava/lang/String;", "Ljava/lang/Object;", ">;"})
    private java.util.Map d(java.util.Map p5)
    {
        String v0_1 = new java.util.ArrayList();
        v0_1.add(new com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance("fastedRopeSpeed", "-1"));
        v0_1.add(new com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance("ropeSpeedEndurance", "-1"));
        v0_1.add(new com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance("ropeEnduranceTime", "-1"));
        v0_1.add(new com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance("ropeSkipNum", "-1"));
        v0_1.add(new com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance("maxRopeSkippingTimes", "-1"));
        p5.put("categories", v0_1);
        p5.put("timeZone", health.compact.a.HiDateUtil.d(0));
        return p5;
    }

    @Signature(value={"()", "Ljava/util/Set<", "Ljava/lang/String;", ">;"})
    private java.util.Set b()
    {
        java.util.HashSet v0_1 = new java.util.HashSet();
        v0_1.add("bestRopeSkippingSingleCount");
        v0_1.add("bestRopeSkippingContinuousCount");
        v0_1.add("bestRopeSkippingMaxSpeed1MIN");
        v0_1.add("bestRopeSkippingEnduranceAbility");
        v0_1.add("bestRopeSkippingEnduranceTimeAbility");
        return v0_1;
    }

    private String i()
    {
        return android.text.format.DateUtils.formatDateTime(this.b, System.currentTimeMillis(), 52);
    }

    private void b(com.huawei.hwbasemgr.IBaseResponseCallback p5, com.huawei.ui.commonui.linechart.common.DateType p6)
    {
        com.huawei.health.ecologydevice.open.data.model.RopeBestRecord$BestRecordData v6_3;
        com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"quaryBestDataFromCache() dateType = ", p6}));
        if (p6 != com.huawei.ui.commonui.linechart.common.DateType.DATE_ALL) {
            if (p6 != com.huawei.ui.commonui.linechart.common.DateType.DATE_MONTH) {
                com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"quaryBestDataFromCache() error dateType"}));
                v6_3 = 0;
            } else {
                v6_3 = health.compact.a.SharedPreferenceManager.d(this.b, "rope_performance_cache", "rope_cache_key_best_of_month");
            }
        } else {
            v6_3 = health.compact.a.SharedPreferenceManager.d(this.b, "rope_performance_cache", "rope_cache_key_best_of_history");
        }
        if (v6_3 != null) {
            this.b(((com.huawei.health.ecologydevice.open.data.model.RopeBestRecord$BestRecordData) new com.google.gson.Gson().fromJson(v6_3, com.huawei.health.ecologydevice.open.data.model.RopeBestRecord$BestRecordData)), p5);
            return;
        } else {
            com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"quaryBestDataFromCache() bestDataJson is null"}));
            p5.onResponse(-1, 0);
            return;
        }
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;)F"})
    private float c(java.util.List p6)
    {
        if (!okt.a(p6)) {
            float v6_5 = p6.iterator();
            while (v6_5.hasNext()) {
                com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance v0_2 = ((com.huawei.health.ecologydevice.open.data.model.RopePerformance$Performance) v6_5.next());
                if ((v0_2 != null) && (java.util.Objects.equals(this.c.get(this.a), v0_2.getPortraitCategory()))) {
                    return this.d(v0_2.getPortraitValue());
                }
            }
            return 0;
        } else {
            com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"parseRankData performances is null"}));
            return 0;
        }
    }

    static ezu()
    {
        ezu.d = new Object();
        return;
    }

    public static ezu a()
    {
        if (ezu.e != null) {
            return ezu.e;
        } else {
            try {
                if (ezu.e == null) {
                    ezu.e = new ezu();
                }
            } catch (ezu v1_1) {
                throw v1_1;
            }
            return ezu.e;
        }
    }

    static synthetic void a(int p0, Object p1)
    {
        com.huawei.hwlogsmodel.LogUtil.e("RopePerformanceHelper", new Object[] {"initData requestPerformanceBestOfMonth errorCode = ", Integer.valueOf(p0)}));
        return;
    }

    static synthetic void a(ezu p0, com.huawei.health.ecologydevice.open.data.model.RopeBestRecord$BestRecordData p1, com.huawei.hwbasemgr.IBaseResponseCallback p2)
    {
        p0.b(p1, p2);
        return;
    }

    static synthetic int c(com.huawei.health.ecologydevice.open.data.model.RopeBestRecordBean p0, com.huawei.health.ecologydevice.open.data.model.RopeBestRecordBean p1)
    {
        return Integer.compare(p1.getValue(), p0.getValue());
    }

    static synthetic void c(int p0, Object p1)
    {
        com.huawei.hwlogsmodel.LogUtil.e("RopePerformanceHelper", new Object[] {"initData requestPerformanceBestOfHistory errorCode = ", Integer.valueOf(p0)}));
        return;
    }

    static synthetic void c(ezu p0, com.huawei.hwbasemgr.IBaseResponseCallback p1)
    {
        p0.b(p1);
        return;
    }

    static synthetic int d(com.huawei.health.ecologydevice.open.data.model.RopePerformance$Rank p0, com.huawei.health.ecologydevice.open.data.model.RopePerformance$Rank p1)
    {
        return fgx.e(p0.getValue(), p1.getValue());
    }

    static synthetic long d(ezu p0, int p1)
    {
        return p0.a(p1);
    }

    static synthetic android.content.Context d(ezu p0)
    {
        return p0.b;
    }

    static synthetic java.util.Map d(ezu p0, java.util.Map p1)
    {
        return p0.d(p1);
    }

    static synthetic com.huawei.health.ecologydevice.open.data.model.RopePerformance e(ezu p0, com.huawei.health.ecologydevice.open.data.model.RopePerformance p1)
    {
        p0.i = p1;
        return p1;
    }

    static synthetic java.util.Set e(ezu p0)
    {
        return p0.b();
    }

    static synthetic void e(int p0, Object p1)
    {
        return;
    }

    public float e(String p4, float p5, com.huawei.ui.commonui.linechart.common.DateType p6)
    {
        if ((!android.text.TextUtils.isEmpty(p4)) && (fgx.e(p5, 0) > 0)) {
            this.a = p4;
            this.f = p5;
            if (p6 != com.huawei.ui.commonui.linechart.common.DateType.DATE_NONE) {
                if (p6 != com.huawei.ui.commonui.linechart.common.DateType.DATE_MONTH) {
                    com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"Unsupported time type!"}));
                    return 0;
                } else {
                    return this.c(this.c());
                }
            } else {
                return this.c(this.e());
            }
        } else {
            com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"category is null or score <= 0"}));
            return 0;
        }
    }

    @Signature(value={"()", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;"})
    public java.util.List e()
    {
        String v0_1 = health.compact.a.SharedPreferenceManager.d(this.b, "rope_performance_cache", "rope_cache_key_current_data");
        java.util.ArrayList v1_2 = new java.util.ArrayList();
        mnd.b(v0_1, this.getClass().getClassLoader(), 0, v1_2);
        return v1_2;
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;)Z"})
    public boolean c(java.util.List p3, java.util.List p4)
    {
        if (!okt.a(p3)) {
            return (mnd.c(p3, 0).equals(mnd.c(p4, 0)) ^ 1);
        } else {
            com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"isDiff cacheData is null."}));
            return 1;
        }
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;)V"})
    public void b(java.util.List p5)
    {
        health.compact.a.SharedPreferenceManager.c(this.b, "rope_performance_cache", "rope_cache_key_current_data", mnd.c(p5, 0), 0);
        return;
    }

    @Signature(value={"()", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;"})
    public java.util.List c()
    {
        java.util.List v0_0 = this.i();
        java.util.ArrayList v1_4 = health.compact.a.SharedPreferenceManager.d(this.b, "rope_performance_cache", "rope_cache_key_time");
        com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"getCacheData time: ", v0_0, ", cacheTime: ", v1_4}));
        if (!v0_0.equals(v1_4)) {
            return java.util.Collections.EMPTY_LIST;
        } else {
            java.util.List v0_4 = health.compact.a.SharedPreferenceManager.d(this.b, "rope_performance_cache", "rope_cache_key_month_data");
            java.util.ArrayList v1_3 = new java.util.ArrayList();
            mnd.b(v0_4, this.getClass().getClassLoader(), 0, v1_3);
            return v1_3;
        }
    }

    @Signature(value={"(", "Ljava/util/List<", "Lcom/huawei/health/ecologydevice/open/data/model/RopePerformance$Performance;", ">;)V"})
    public void e(java.util.List p6)
    {
        android.content.Context v0_0 = this.i();
        com.huawei.hwlogsmodel.LogUtil.d("RopePerformanceHelper", new Object[] {"setCacheData time: ", v0_0}));
        String v6_1 = mnd.c(p6, 0);
        health.compact.a.SharedPreferenceManager.c(this.b, "rope_performance_cache", "rope_cache_key_time", v0_0, 0);
        health.compact.a.SharedPreferenceManager.c(this.b, "rope_performance_cache", "rope_cache_key_month_data", v6_1, 0);
        return;
    }

    public void c(com.huawei.hwbasemgr.IBaseResponseCallback p3)
    {
        health.compact.a.GRSManager.b(com.huawei.hwcommonmodel.application.BaseApplication.getContext()).e("healthCloudUrl", new ezu$4(this, p3));
        return;
    }

    public void e(com.huawei.hwbasemgr.IBaseResponseCallback p3)
    {
        health.compact.a.GRSManager.b(com.huawei.hwcommonmodel.application.BaseApplication.getContext()).e("healthCloudUrl", new ezu$2(this, p3));
        return;
    }

    public void d(com.huawei.hwbasemgr.IBaseResponseCallback p3)
    {
        health.compact.a.GRSManager.b(com.huawei.hwcommonmodel.application.BaseApplication.getContext()).e("healthCloudUrl", new ezu$5(this, p3));
        return;
    }

    public void c(com.huawei.hwbasemgr.IBaseResponseCallback p2, com.huawei.ui.commonui.linechart.common.DateType p3)
    {
        if (health.compact.a.CommonUtil.aa(com.huawei.hwcommonmodel.application.BaseApplication.getContext())) {
            if (p3 != com.huawei.ui.commonui.linechart.common.DateType.DATE_ALL) {
                if (p3 != com.huawei.ui.commonui.linechart.common.DateType.DATE_MONTH) {
                    com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"quaryPerformanceOfBest() error dateType"}));
                    p2.onResponse(-1, 0);
                    return;
                } else {
                    this.e(p2);
                    return;
                }
            } else {
                this.c(p2);
                return;
            }
        } else {
            this.b(p2, p3);
            return;
        }
    }

    public void d()
    {
        this.d(new ezx());
        this.c(new ezz());
        this.e(new faa());
        return;
    }
}
