package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;

/* JADX INFO: loaded from: classes10.dex */
public class GetSportStatReq implements IRequest {
    private static final int STRING_BUILDER_INIT_CAPACITY = 150;
    private Integer dataSource = 2;
    private Long deviceCode;
    private Integer endTime;
    private Integer startTime;

    public Integer getDataSource() {
        return this.dataSource;
    }

    public Long getDeviceCode() {
        return this.deviceCode;
    }

    public Integer getEndTime() {
        return this.endTime;
    }

    public Integer getStartTime() {
        return this.startTime;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/sport/v2/getSportsStat";
    }

    public void setDataSource(Integer num) {
        this.dataSource = num;
    }

    public void setDeviceCode(Long l) {
        this.deviceCode = l;
    }

    public void setEndTime(Integer num) {
        this.endTime = num;
    }

    public void setStartTime(Integer num) {
        this.startTime = num;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(150);
        sb.append("GetSportStatReq{startTime=");
        sb.append(this.startTime);
        sb.append(", endTime=");
        sb.append(this.endTime);
        sb.append(", dataSource=");
        sb.append(this.dataSource);
        sb.append(", deviceCode=");
        sb.append(this.deviceCode);
        return sb.toString();
    }
}
