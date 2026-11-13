package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;
import java.util.Set;

/* JADX INFO: loaded from: classes10.dex */
public class GetSportDimenStatisticsReq implements IRequest {
    private Integer dataSource = 2;
    private Integer endTime;
    private Set<Integer> sportTypes;
    private Integer startTime;

    public Integer getDataSource() {
        return this.dataSource;
    }

    public Integer getEndTime() {
        return this.endTime;
    }

    public Set<Integer> getSportTypes() {
        return this.sportTypes;
    }

    public Integer getStartTime() {
        return this.startTime;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/sport/getSportsDimenStat";
    }

    public void setDataSource(Integer num) {
        this.dataSource = num;
    }

    public void setEndTime(Integer num) {
        this.endTime = num;
    }

    public void setSportTypes(Set<Integer> set) {
        this.sportTypes = set;
    }

    public void setStartTime(Integer num) {
        this.startTime = num;
    }

    public String toString() {
        return "GetSportDimenStatisticsReq{startTime=" + this.startTime + ", endTime=" + this.endTime + ", dataSource=" + this.dataSource + ", sportTypes=" + this.sportTypes + "}";
    }
}
