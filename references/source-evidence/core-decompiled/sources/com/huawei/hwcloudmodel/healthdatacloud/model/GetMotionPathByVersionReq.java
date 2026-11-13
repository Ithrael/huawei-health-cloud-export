package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;

/* JADX INFO: loaded from: classes10.dex */
public class GetMotionPathByVersionReq implements IRequest {
    private String condition;
    private Integer dataType;
    private Long deviceCode;
    private Long version;

    public String getCondition() {
        return this.condition;
    }

    public Integer getDataType() {
        return this.dataType;
    }

    public Long getDeviceCode() {
        return this.deviceCode;
    }

    public String getUrl() {
        return Utils.b() + "/dataQuery/path/getMotionPathByVersion";
    }

    public Long getVersion() {
        return this.version;
    }

    public void setCondition(String str) {
        this.condition = str;
    }

    public void setDataType(Integer num) {
        this.dataType = num;
    }

    public void setDeviceCode(Long l) {
        this.deviceCode = l;
    }

    public void setVersion(int i) {
        this.version = Long.valueOf(i);
    }

    public void setVersion(Long l) {
        this.version = l;
    }

    public String toString() {
        return "GetMotionPathByVersionReq { version = " + this.version + ", dataType = " + this.dataType + ", deviceCode = " + this.deviceCode + ", condition = " + this.condition + '}';
    }
}
