package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.google.gson.annotations.SerializedName;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;

/* JADX INFO: loaded from: classes.dex */
public class GetHealthDataByReversedOrderReq implements IRequest {

    @SerializedName("dataType")
    private int dataType;

    @SerializedName("deviceCode")
    private long deviceCode;

    @SerializedName("recordDay")
    private int recordDay;

    @SerializedName(DnsResult.KEY_TYPE)
    private int type;

    public int getDataType() {
        return this.dataType;
    }

    public long getDeviceCode() {
        return this.deviceCode;
    }

    public int getRecordDay() {
        return this.recordDay;
    }

    public int getType() {
        return this.type;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/health/getHealthDataByRecordDay";
    }

    public void setDataType(Integer num) {
        this.dataType = num.intValue();
    }

    public void setDeviceCode(Long l) {
        this.deviceCode = l.longValue();
    }

    public void setRecordDay(int i) {
        this.recordDay = i;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String toString() {
        return "GetHealthDataByReversedOrderReq{recordDay=" + this.recordDay + ", type=" + this.type + ", dataType=" + this.dataType + ", deviceCode=" + this.deviceCode + '}';
    }
}
