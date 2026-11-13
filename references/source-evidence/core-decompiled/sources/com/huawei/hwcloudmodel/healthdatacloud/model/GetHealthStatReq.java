package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;
import java.util.Set;

/* JADX INFO: loaded from: classes10.dex */
public class GetHealthStatReq implements IRequest {
    private int dataSource;
    private long deviceCode;
    private int endTime;
    private int startTime;
    private Set<Integer> types;

    public int getDataSource() {
        return this.dataSource;
    }

    public long getDeviceCode() {
        return this.deviceCode;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public Set<Integer> getTypes() {
        return this.types;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/health/getHealthStat";
    }

    public void setDataSource(int i) {
        this.dataSource = i;
    }

    public void setDeviceCode(long j) {
        this.deviceCode = j;
    }

    public void setEndTime(int i) {
        this.endTime = i;
    }

    public void setStartTime(int i) {
        this.startTime = i;
    }

    public void setTypes(Set<Integer> set) {
        this.types = set;
    }

    public String toString() {
        return "GetHealthStatReq{startTime=" + this.startTime + ", endTime=" + this.endTime + ", dataSource=" + this.dataSource + ", deviceCode=" + this.deviceCode + ", types=" + this.types + '}';
    }
}
