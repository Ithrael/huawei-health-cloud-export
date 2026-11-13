package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.google.gson.annotations.SerializedName;
import com.huawei.hwdevice.phoneprocess.mgr.exercise.HwExerciseConstants;
import com.huawei.networkclient.IRequest;
import com.huawei.profile.profile.ProfileUtilsSdk;
import health.compact.a.Utils;

/* JADX INFO: loaded from: /work/unpacked/classes39.dex */
public class GetSampleSequenceByTimeReq implements IRequest {

    @SerializedName("dataType")
    private final int mDataType;

    @SerializedName("deviceCode")
    private final long mDeviceCode;

    @SerializedName(HwExerciseConstants.JSON_NAME_WORKOUT_RECORD_END_TIME)
    private final long mEndTime;

    @SerializedName("queryType")
    private final int mQueryType;

    @SerializedName(HwExerciseConstants.JSON_NAME_WORKOUT_RECORD_START_TIME)
    private final long mStartTime;

    @SerializedName(ProfileUtilsSdk.TYPE)
    private final int mType;

    public static class Builder {
        private int mDataType;
        private long mDeviceCode;
        private long mEndTime;
        private int mQueryType;
        private long mStartTime;
        private int mType;

        public GetSampleSequenceByTimeReq build() {
            return new GetSampleSequenceByTimeReq(this);
        }

        public Builder dataType(int i) {
            this.mDataType = i;
            return this;
        }

        public Builder deviceCode(long j) {
            this.mDeviceCode = j;
            return this;
        }

        public Builder endTime(long j) {
            this.mEndTime = j;
            return this;
        }

        public Builder queryType(int i) {
            this.mQueryType = i;
            return this;
        }

        public Builder startTime(long j) {
            this.mStartTime = j;
            return this;
        }

        public Builder type(int i) {
            this.mType = i;
            return this;
        }
    }

    public GetSampleSequenceByTimeReq(Builder builder) {
        this.mType = builder.mType;
        this.mQueryType = builder.mQueryType;
        this.mStartTime = builder.mStartTime;
        this.mEndTime = builder.mEndTime;
        this.mDataType = builder.mDataType;
        this.mDeviceCode = builder.mDeviceCode;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/sequence/getSampleSequenceByTime";
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("GetSampleSequenceByTimeReq {type = ");
        stringBuffer.append(this.mType);
        stringBuffer.append(", queryType = ").append(this.mQueryType);
        stringBuffer.append(", startTime = ").append(this.mStartTime);
        stringBuffer.append(", endTime = ").append(this.mEndTime);
        stringBuffer.append(", dataType = ").append(this.mDataType);
        stringBuffer.append(", deviceCode = ").append(this.mDeviceCode);
        stringBuffer.append('}');
        return stringBuffer.toString();
    }
}
