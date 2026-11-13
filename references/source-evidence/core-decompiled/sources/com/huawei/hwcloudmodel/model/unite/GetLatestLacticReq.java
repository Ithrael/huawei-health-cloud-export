package com.huawei.hwcloudmodel.model.unite;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import health.compact.a.GRSManager;

/* JADX INFO: loaded from: classes10.dex */
public class GetLatestLacticReq implements IRequest {
    private static final String LATEST_LACTIC_URL = "/dataQuery/health/getHealthDataLatest";

    @SerializedName("counts")
    private int counts;

    @SerializedName("dataType")
    private int dataType;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName(DnsResult.KEY_TYPE)
    private int type;

    public int getCounts() {
        return this.counts;
    }

    public int getDataType() {
        return this.dataType;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int getType() {
        return this.type;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + LATEST_LACTIC_URL;
    }

    public void setCounts(int i) {
        this.counts = i;
    }

    public void setDataType(int i) {
        this.dataType = i;
    }

    public void setTimestamp(long j) {
        this.timestamp = j;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String toString() {
        return "GetLatestLacticReq{, timestamp=" + this.timestamp + ", type=" + this.type + ", dataType=" + this.dataType + ", counts=" + this.counts + '}';
    }
}
