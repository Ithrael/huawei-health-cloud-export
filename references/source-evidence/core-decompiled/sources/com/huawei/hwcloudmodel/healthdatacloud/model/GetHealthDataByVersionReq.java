package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;

/* JADX INFO: loaded from: classes10.dex */
public class GetHealthDataByVersionReq implements IRequest {
    private Integer dataType;
    private Long deviceCode;
    private Integer type;
    private long version;

    public Integer getDataType() {
        return this.dataType;
    }

    public Long getDeviceCode() {
        return this.deviceCode;
    }

    public Integer getType() {
        return this.type;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/health/getHealthDataByVersion";
    }

    public long getVersion() {
        return this.version;
    }

    public void setDataType(Integer num) {
        this.dataType = num;
    }

    public void setDeviceCode(Long l) {
        this.deviceCode = l;
    }

    public void setType(Integer num) {
        this.type = num;
    }

    public void setVersion(long j) {
        this.version = j;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("GetHealthDataByVersionReq{version=");
        stringBuffer.append(this.version);
        stringBuffer.append(", type=").append(this.type);
        stringBuffer.append(", dataType=").append(this.dataType);
        stringBuffer.append('}');
        return stringBuffer.toString();
    }
}
