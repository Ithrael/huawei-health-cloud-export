 class Lezu$2; implements com.huawei.hwcloudmodel.callback.GrsQueryCallback {
    final synthetic ezu c;
    final synthetic com.huawei.hwbasemgr.IBaseResponseCallback d;

    @Signature(value={"()V"})
    ezu$2(ezu p1, com.huawei.hwbasemgr.IBaseResponseCallback p2)
    {
        this.c = p1;
        this.d = p2;
        return;
    }

    public void onCallBackFail(int p3)
    {
        com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"requestPerformanceBestOfMonth getUrl failed resultCode = ", Integer.valueOf(p3)}));
        this.d.onResponse(-1, 0);
        return;
    }

    public void onCallBackSuccess(String p7)
    {
        android.content.Context v0_0 = com.huawei.health.ecologydevice.networkclient.HealthEngineRequestManager.addPublicParams();
        v0_0.put("bestItems", ezu.e(this.c));
        v0_0.put("queryType", Integer.valueOf(1));
        v0_0.put("startTime", Long.valueOf(ezu.d(this.c, 0)));
        v0_0.put("endTime", Long.valueOf((ezu.d(this.c, 1) - 1000)));
        v0_0.put("timeZone", health.compact.a.HiDateUtil.d(0));
        String v2_6 = com.huawei.health.ecologydevice.networkclient.HealthEngineRequestManager.addPublicHeaders();
        String v3_5 = pnq.c();
        String v4_1 = new StringBuilder();
        v4_1.append(p7);
        v4_1.append("/dataQuery/report/getCycleReport");
        String v7_4 = ((com.huawei.health.ecologydevice.open.data.model.RopeBestRecord) v3_5.a(v4_1.toString(), v2_6, pob.a(v0_0), com.huawei.health.ecologydevice.open.data.model.RopeBestRecord));
        if (v7_4 != null) {
            if (v7_4.getResultCode() == 0) {
                String v7_5 = v7_4.getUserCycleReport();
                if (v7_5 != null) {
                    String v7_6 = v7_5.getBestReports();
                    ezu.a(this.c, v7_6, this.d);
                    health.compact.a.SharedPreferenceManager.c(ezu.d(this.c), "rope_performance_cache", "rope_cache_key_best_of_month", pob.a(v7_6), 0);
                    return;
                } else {
                    com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"requestPerformanceBestOfMonth userCycleReport is null"}));
                    this.d.onResponse(-1, 0);
                    return;
                }
            } else {
                com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"requestPerformanceBestOfMonth fail resultCode is ", Integer.valueOf(v7_4.getResultCode()), " resultDesc is ", v7_4.getResultDesc()}));
                this.d.onResponse(-1, 0);
                return;
            }
        } else {
            com.huawei.hwlogsmodel.LogUtil.g("RopePerformanceHelper", new Object[] {"requestPerformanceBestOfMonth() ropeBestRecordData is null"}));
            this.d.onResponse(-1, 0);
            return;
        }
    }
}
