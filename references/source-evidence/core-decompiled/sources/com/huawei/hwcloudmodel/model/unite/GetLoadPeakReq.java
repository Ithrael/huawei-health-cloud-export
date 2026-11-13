package com.huawei.hwcloudmodel.model.unite;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import health.compact.a.GRSManager;

/* JADX INFO: loaded from: classes10.dex */
public class GetLoadPeakReq implements IRequest {
    private static final String LOAD_PEAK_URL = "/dataQuery/report/getSampleReportByDay";

    @SerializedName("dataType")
    private int dataType;

    @SerializedName("deviceCode")
    private int deviceCode;

    @SerializedName("endDay")
    private long endDay;

    @SerializedName("recordDay")
    private int recordDay;

    @SerializedName("startDay")
    private long startDay;

    @SerializedName(DnsResult.KEY_TYPE)
    private int type;

    public int getDataType() {
        return this.dataType;
    }

    public int getDeviceCode() {
        return this.deviceCode;
    }

    public long getEndDay() {
        return this.endDay;
    }

    public int getRecordDay() {
        return this.recordDay;
    }

    public long getStartDay() {
        return this.startDay;
    }

    public int getType() {
        return this.type;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + LOAD_PEAK_URL;
    }

    public void setDataType(int i) {
        this.dataType = i;
    }

    public void setDeviceCode(int i) {
        this.deviceCode = i;
    }

    public void setEndDay(long j) {
        this.endDay = j;
    }

    public void setRecordDay(int i) {
        this.recordDay = i;
    }

    public void setStartDay(long j) {
        this.startDay = j;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String toString() {
        return "GetLoadPeakReq{startDay=" + this.startDay + ", endDay=" + this.endDay + ", type=" + this.type + ", dataType=" + this.dataType + ", deviceCode=" + this.deviceCode + ", recordDay=" + this.recordDay + '}';
    }
}
