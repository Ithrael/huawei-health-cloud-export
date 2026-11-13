package com.huawei.hwcloudmodel.healthdatacloud.model;

import com.huawei.hwcloudmodel.model.unite.SyncKey;
import com.huawei.networkclient.IRequest;
import health.compact.a.Utils;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public class GetSyncVersionsReq implements IRequest {
    private List<SyncKey> syncKeys;

    public String getUrl() {
        return Utils.b() + "/dataQuery/common/getSyncVersions";
    }

    public void setSyncKeys(List<SyncKey> list) {
        this.syncKeys = list;
    }

    public String toString() {
        return "GetSyncVersionsReq{syncKeys=" + this.syncKeys + '}';
    }
}
