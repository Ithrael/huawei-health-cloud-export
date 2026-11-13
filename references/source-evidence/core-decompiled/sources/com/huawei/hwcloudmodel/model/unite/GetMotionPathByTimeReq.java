package com.huawei.hwcloudmodel.model.unite;

/* JADX INFO: loaded from: classes10.dex */
public class GetMotionPathByTimeReq {
    private Integer dataType;
    private Long deviceCode;
    private Long endTime;
    private Integer queryType;
    private Long startTime;

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

    public String toString() {
        return "GetMotionPathByTimeReq{queryType=" + this.queryType + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", dataType=" + this.dataType + ", deviceCode=" + this.deviceCode + '}';
    }
}
