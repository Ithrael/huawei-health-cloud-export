package com.huawei.hihealthservice.sync.syncdata.dictionary.detail;

import com.google.gson.annotations.SerializedName;
import com.huawei.haf.application.BaseApplication;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import com.huawei.ui.openservice.db.model.ServiceVersionTable;
import health.compact.a.GRSManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes10.dex */
public class GetSampleSequenceByVersionReq implements IRequest {

    @SerializedName("dataType")
    private final int mDataType;

    @SerializedName("deviceCode")
    private final long mDeviceCode;

    @SerializedName("subTypes")
    private final int[] mSubTypes;

    @SerializedName(DnsResult.KEY_TYPE)
    private final int mType;

    @SerializedName(ServiceVersionTable.COLUMN_VERSION)
    private final long mVersion;

    public static class Builder {
        private int mDataType;
        private long mDeviceCode;
        private int[] mSubTypes;
        private int mType;
        private long mVersion;

        public GetSampleSequenceByVersionReq build() {
            return new GetSampleSequenceByVersionReq(this);
        }

        public Builder dataType(int i) {
            this.mDataType = i;
            return this;
        }

        public Builder deviceCode(long j) {
            this.mDeviceCode = j;
            return this;
        }

        public Builder subTypes(int[] iArr) {
            this.mSubTypes = iArr != null ? (int[]) iArr.clone() : new int[0];
            return this;
        }

        public Builder type(int i) {
            this.mType = i;
            return this;
        }

        public Builder version(long j) {
            this.mVersion = j;
            return this;
        }
    }

    public GetSampleSequenceByVersionReq(Builder builder) {
        this.mType = builder.mType;
        this.mVersion = builder.mVersion;
        this.mDataType = builder.mDataType;
        this.mDeviceCode = builder.mDeviceCode;
        this.mSubTypes = builder.mSubTypes;
    }

    public int getDataType() {
        return this.mDataType;
    }

    public long getDeviceCode() {
        return this.mDeviceCode;
    }

    public int[] getSubTypes() {
        int[] iArr = this.mSubTypes;
        return iArr == null ? new int[0] : (int[]) iArr.clone();
    }

    public int getType() {
        return this.mType;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + "/dataQuery/sequence/getSampleSequenceByVersion";
    }

    public long getVersion() {
        return this.mVersion;
    }

    public Builder toBuilder() {
        return new Builder().type(this.mType).version(this.mVersion).dataType(this.mDataType).deviceCode(this.mDeviceCode).subTypes(this.mSubTypes);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("GetSampleSequenceByVersionReq {type = ");
        stringBuffer.append(this.mType);
        stringBuffer.append(", version = ").append(this.mVersion);
        stringBuffer.append(", dataType = ").append(this.mDataType);
        stringBuffer.append(", deviceCode = ").append(this.mDeviceCode);
        stringBuffer.append(", subTypes = ").append(Arrays.toString(this.mSubTypes));
        stringBuffer.append('}');
        return stringBuffer.toString();
    }
}
