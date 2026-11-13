package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;

/* JADX INFO: loaded from: classes10.dex */
public class GetHealthDataByTimeReq implements IRequest {
    private Integer dataType;
    private Long deviceCode;
    private Long endTime;
    private Integer queryType;
    private Long startTime;
    private Integer type;

    public Integer getDataType() {
        return this.dataType;
    }

    public Long getDeviceCode() {
        return this.deviceCode;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public Integer getQueryType() {
        return this.queryType;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Integer getType() {
        return this.type;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/health/getHealthData";
    }

    public void setDataType(Integer num) {
        this.dataType = num;
    }

    public void setDeviceCode(Long l) {
        this.deviceCode = l;
    }

    public void setEndTime(Long l) {
        this.endTime = l;
    }

    public void setQueryType(Integer num) {
        this.queryType = num;
    }

    public void setStartTime(Long l) {
        this.startTime = l;
    }

    public void setType(Integer num) {
        this.type = num;
    }

    public String toString() {
        return "GetHealthDataByTimeReq{queryType=" + this.queryType + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", type=" + this.type + ", dataType=" + this.dataType + ", deviceCode=" + this.deviceCode + '}';
    }
}
