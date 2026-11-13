package com.huawei.hihealthservice.sync.syncdata.dictionary.detail;

import com.huawei.haf.application.BaseApplication;
import com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult;
import com.huawei.networkclient.IRequest;
import com.huawei.ui.openservice.db.model.ServiceVersionTable;
import health.compact.a.GRSManager;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes10.dex */
public class GetSamplePeriodicReportByVersionReq implements IRequest {
    private int type;
    private long version;

    public Map<String, String> getQueryParams() {
        HashMap map = new HashMap();
        map.put(DnsResult.KEY_TYPE, String.valueOf(this.type));
        map.put(ServiceVersionTable.COLUMN_VERSION, String.valueOf(this.version));
        return map;
    }

    public int getType() {
        return this.type;
    }

    public String getUrl() {
        return GRSManager.b(BaseApplication.b()).getUrl("healthCloudUrl") + "/dataService/v1/healthDatas/sampleReports/history";
    }

    public long getVersion() {
        return this.version;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setVersion(long j) {
        this.version = j;
    }
}
