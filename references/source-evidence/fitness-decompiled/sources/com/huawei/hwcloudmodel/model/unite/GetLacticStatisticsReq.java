package com.huawei.hwcloudmodel.model.unite;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hwdevice.phoneprocess.mgr.exercise.HwExerciseConstants;
import com.huawei.networkclient.IRequest;
import health.compact.a.GRSManager;

/* JADX INFO: loaded from: /work/unpacked/classes39.dex */
public class GetLacticStatisticsReq implements IRequest {
    private static final String LACTIC_STATISTICS_URL = "/dataQuery/health/getHealthStatistics";

    @SerializedName(HwExerciseConstants.JSON_NAME_WORKOUT_RECORD_END_TIME)
    private long endTime;

    @SerializedName(HwExerciseConstants.JSON_NAME_WORKOUT_RECORD_START_TIME)
    private long startTime;

    @SerializedName("types")
    private int[] types;

    public long getEndTime() {
        return this.endTime;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int[] getType() {
        return this.types;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + LACTIC_STATISTICS_URL;
    }

    public void setEndTime(long j) {
        this.endTime = j;
    }

    public void setStartTime(long j) {
        this.startTime = j;
    }

    public void setType(int[] iArr) {
        this.types = iArr;
    }

    public String toString() {
        return "GetLacticStatisticsReq{startTime=" + this.startTime + ", endTime=" + this.endTime + ", types=" + this.types + '}';
    }
}
