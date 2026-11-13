package com.huawei.hihealthservice.sync.syncdata.dictionary.statistics;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import com.huawei.ui.openservice.db.model.ServiceVersionTable;
import health.compact.a.GRSManager;

/* JADX INFO: loaded from: classes10.dex */
public class HealthStatisticsDownloadByVersionReq implements IRequest {

    @SerializedName("dataSource")
    private final int dataSource;

    @SerializedName("dataType")
    private int dataType;

    @SerializedName(DnsResult.KEY_TYPE)
    private final int type;

    @SerializedName(ServiceVersionTable.COLUMN_VERSION)
    private long version;

    public static class Builder {
        private int dataSource;
        private int dataType;
        private int type;
        private long version;

        public HealthStatisticsDownloadByVersionReq build() {
            return new HealthStatisticsDownloadByVersionReq(this);
        }

        public Builder dataSource(int i) {
            this.dataSource = i;
            return this;
        }

        public Builder dataType(int i) {
            this.dataType = i;
            return this;
        }

        public Builder deviceCode(int i) {
            return this;
        }

        public Builder types(int i) {
            this.type = i;
            return this;
        }

        public Builder version(long j) {
            this.version = j;
            return this;
        }
    }

    public HealthStatisticsDownloadByVersionReq(Builder builder) {
        this.dataSource = builder.dataSource;
        this.type = builder.type;
        this.version = builder.version;
        this.dataType = builder.dataType;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + "/dataQuery/health/getHealthStatisticsByVersion";
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long j) {
        this.version = j;
    }

    public String toString() {
        return "HealthStatisticsDownloadByVersionReq{src=" + this.dataSource + ",tp=" + this.type + ",ver=" + this.version + '}';
    }
}
