package com.huawei.hwcloudmodel.model.unite;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hihealthservice.db.table.DBPointCommon;
import com.huawei.networkclient.IRequest;
import health.compact.a.GRSManager;
import java.util.Set;

/* JADX INFO: loaded from: classes10.dex */
public class GetRunLevelReq implements IRequest {

    @SerializedName("endDay")
    private int endDay;

    @SerializedName("queryType")
    private int queryType;

    @SerializedName("startDay")
    private int startDay;

    @SerializedName(DBPointCommon.COLUMN_TIME_ZONE)
    private String timeZone;

    @SerializedName("types")
    private Set<Integer> types;

    public int getEndDay() {
        return this.endDay;
    }

    public int getQueryType() {
        return this.queryType;
    }

    public int getStartDay() {
        return this.startDay;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public Set<Integer> getTypes() {
        return this.types;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + "/dataAnalyse/app/getRunLevel";
    }

    public void setEndDay(int i) {
        this.endDay = i;
    }

    public void setQueryType(int i) {
        this.queryType = i;
    }

    public void setStartDay(int i) {
        this.startDay = i;
    }

    public void setTimeZone(String str) {
        this.timeZone = str;
    }

    public void setTypes(Set<Integer> set) {
        this.types = set;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(16);
        sb.append("GetRunLevelReq{startDay=");
        sb.append(this.startDay);
        sb.append(", endDay=");
        sb.append(this.endDay);
        sb.append(", queryType=");
        sb.append(this.queryType);
        sb.append(", types=");
        sb.append(this.types);
        sb.append(", timeZone=");
        sb.append(this.timeZone);
        sb.append('}');
        return sb.toString();
    }
}
