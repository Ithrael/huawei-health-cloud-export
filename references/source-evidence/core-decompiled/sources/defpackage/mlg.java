package defpackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import androidx.health.connect.client.records.metadata.Metadata;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.haf.common.exception.ExceptionUtils;
import com.huawei.haf.threadpool.ThreadPoolManager;
import com.huawei.hwcloudmodel.callback.ICloudOperationResult;
import com.huawei.hwcloudmodel.callback.IHttpOperationResult;
import com.huawei.hwcloudmodel.callback.WifiRequestCallBack;
import com.huawei.hwcloudmodel.model.CloudCommonReponse;
import com.huawei.hwcloudmodel.model.WifiDataCloudFactory;
import com.huawei.hwcloudmodel.model.intelligent.DeviceLinkResultNotifyRequest;
import com.huawei.hwcloudmodel.model.intelligent.DeviceLinkResultNotifyResponse;
import com.huawei.hwcloudmodel.model.intelligent.ReleaseDeviceLinkageRequest;
import com.huawei.hwcloudmodel.model.intelligent.StartDeviceLinkageRequest;
import com.huawei.hwcloudmodel.model.intelligent.StopDeviceLinkageRequest;
import com.huawei.hwcloudmodel.model.intelligent.TransferDeviceDataRequest;
import com.huawei.hwcloudmodel.model.unite.AddEvaluationResultReq;
import com.huawei.hwcloudmodel.model.unite.AddSleepStatReq;
import com.huawei.hwcloudmodel.model.unite.DelEvaluationResultReq;
import com.huawei.hwcloudmodel.model.unite.DeleteAllHealthDataReq;
import com.huawei.hwcloudmodel.model.unite.DeleteAllMotionPathReq;
import com.huawei.hwcloudmodel.model.unite.DeleteAllSportDataReq;
import com.huawei.hwcloudmodel.model.unite.DeleteSportDataReq;
import com.huawei.hwcloudmodel.model.unite.GetEvaluationResultReq;
import com.huawei.hwcloudmodel.model.unite.GetMotionPathByTimeReq;
import com.huawei.hwcloudmodel.model.unite.GetSleepStatReq;
import com.huawei.hwcloudmodel.model.unite.GetThirdSignReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceAddAuthMsgBySubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceAddAuthorizeForSubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceAuthorizeByMainUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceControlDataModelReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceDeleteAuthorizeSubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceExitAuthorizeSubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetAllDeviceRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetAuthorizeSubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetAuthorizeSubUserRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetDeviceRegistration;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetDeviceStatusReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetDeviceStatusRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetMainUserAuth;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetSubUserAuthMsgReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetVerifyCodeForMainUserRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetWifiDeviceInfoReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceGetWifiDeviceInfoRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceServiceInfoReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceServiceInfoRsp;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceShareByMainUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceShareMemberInfoBySubUserReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceSubuserAuthorize;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceUnbindReq;
import com.huawei.hwcloudmodel.model.unite.WifiDeviceUpdateAuthorizeSubUserReq;
import com.huawei.hwcloudmodel.model.userprofile.DeleteAuthorizeReq;
import com.huawei.hwcloudmodel.model.userprofile.GetAuthorizeReq;
import com.huawei.hwcloudmodel.model.userprofile.GetCommentReq;
import com.huawei.hwcloudmodel.model.userprofile.GetCommentRsp;
import com.huawei.hwcloudmodel.model.userprofile.GetThirdIdentifyReq;
import com.huawei.hwcloudmodel.model.userprofile.GetUserMergeInfoReq;
import com.huawei.hwcloudmodel.model.userprofile.GetUserMergeInfoRsp;
import com.huawei.hwcloudmodel.model.userprofile.MergeUserAllDataReq;
import com.huawei.hwcloudmodel.model.userprofile.MergeUserAllDataRsp;
import com.huawei.hwcloudmodel.model.userprofile.SetAuthorizeReq;
import com.huawei.hwcloudmodel.model.userprofile.UpdateCommentReq;
import com.huawei.hwcloudmodel.utils.GetEvaluationResultRsp;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwlogsmodel.LogUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes10.dex */
public class mlg {
    private mlo b;
    private mlj g;
    private WifiDataCloudFactory j;
    public static final HostnameVerifier e = new HostnameVerifier() { // from class: mlg.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String str, SSLSession sSLSession) {
            return true;
        }
    };
    private static final Object d = new Object();
    private static Context c = null;
    private static volatile mlg a = null;

    private mlg() {
        Context context = BaseApplication.getContext();
        c = context;
        this.g = new mlj(context);
        mlo mloVar = new mlo();
        this.b = mloVar;
        mloVar.d();
        this.j = new WifiDataCloudFactory(c);
    }

    private String a(Object obj) {
        if (obj instanceof StartDeviceLinkageRequest) {
            return "/deviceAgent/startDeviceLinkage";
        }
        if (obj instanceof TransferDeviceDataRequest) {
            return "/deviceAgent/transDeviceData";
        }
        if (obj instanceof StopDeviceLinkageRequest) {
            return "/deviceAgent/stopDeviceLinkage";
        }
        if (obj instanceof ReleaseDeviceLinkageRequest) {
            return "/deviceAgent/releaseDeviceLinkage";
        }
        if (obj instanceof GetThirdIdentifyReq) {
            return "hiHealth/getThirdIdentify";
        }
        return null;
    }

    private void b(String str, String str2, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter wifiDeviceRequest");
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(str2), str, headers, new WifiRequestCallBack() { // from class: mlg.18
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str3) {
                LogUtil.d("HWCloudUtils", "wifiDeviceRequest in operationResult result is ", str3);
                try {
                    if (TextUtils.isEmpty(str3)) {
                        LogUtil.g("HWCloudUtils", "wifiDeviceRequest, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str3, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "wifiDeviceRequest ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, str3, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, str3, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "wifiDeviceRequest JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public static boolean c(Context context) {
        NetworkInfo activeNetworkInfo;
        if (context == null || (activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo()) == null) {
            return false;
        }
        return activeNetworkInfo.isAvailable();
    }

    private String d(Object obj) {
        if (obj instanceof SetAuthorizeReq) {
            return "/profile/third/authorize";
        }
        if (obj instanceof GetAuthorizeReq) {
            return "/profile/third/getAuthorizeToken";
        }
        if (obj instanceof DeleteAuthorizeReq) {
            return "/profile/third/cancelAuthorize";
        }
        if (obj instanceof GetThirdSignReq) {
            return "/dataOpen/third/getThirdSign";
        }
        if (obj instanceof AddEvaluationResultReq) {
            return "/profile/evaluation/addEvaluationResult";
        }
        if (obj instanceof GetEvaluationResultReq) {
            return "/profile/evaluation/getEvaluationResult";
        }
        if (obj instanceof DelEvaluationResultReq) {
            return "/profile/evaluation/deleteEvaluationResult";
        }
        return null;
    }

    private String e(Object obj) {
        if (obj instanceof MergeUserAllDataReq) {
            return "/profile/merge/mergeUserAllData";
        }
        if (obj instanceof GetUserMergeInfoReq) {
            return "/profile/merge/getUserMergeInfo";
        }
        if (obj instanceof UpdateCommentReq) {
            return "/dataRecommend/common/updateCommentRemainderTimes";
        }
        if (obj instanceof GetCommentReq) {
            return "/dataRecommend/common/getCommentRemainderTimes";
        }
        if (obj instanceof DeleteSportDataReq) {
            return "/dataSync/sport/deleteSportsData";
        }
        if (obj instanceof DeleteAllSportDataReq) {
            return "/dataSync/sport/deleteAllSportsData";
        }
        if (obj instanceof AddSleepStatReq) {
            return "/dataSync/sport/addSleepStat";
        }
        if (obj instanceof GetSleepStatReq) {
            return "/dataQuery/sport/getSleepStat";
        }
        if (obj instanceof DeleteAllHealthDataReq) {
            return "/dataSync/health/deleteAllHealthData";
        }
        if (obj instanceof GetMotionPathByTimeReq) {
            return "/dataQuery/path/getMotionPathData";
        }
        if (obj instanceof DeleteAllMotionPathReq) {
            return "/dataSync/path/deleteAllMotionPathData";
        }
        String strD = d(obj);
        if (strD != null) {
            return strD;
        }
        String strA = a(obj);
        if (strA != null) {
            return strA;
        }
        return null;
    }

    public static mlg e() {
        if (a == null) {
            synchronized (d) {
                if (a == null) {
                    a = new mlg();
                }
            }
        }
        return a;
    }

    public void a(final double d2, final double d3, final ICloudOperationResult<mla> iCloudOperationResult) {
        ThreadPoolManager.a().d("HWCloudUtils", new Runnable() { // from class: mlg.25
            @Override // java.lang.Runnable
            public void run() {
                mla mlaVarC = mkx.a().c(d2, d3);
                if (mlaVarC.d() == -1.0d || mlaVarC.a() == 0) {
                    iCloudOperationResult.operationResult(mlaVarC, null, false);
                } else {
                    iCloudOperationResult.operationResult(mlaVarC, null, true);
                }
            }
        });
    }

    public void a(final ICloudOperationResult<WifiDeviceGetAllDeviceRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getUserAllWifiDevice");
        String strA = pob.a(this.j.getBody(null));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl("/deviceAgent/getUserDevice"), strA, headers, new WifiRequestCallBack() { // from class: mlg.7
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getUserAllWifiDevice in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getUserAllWifiDevice, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    WifiDeviceGetAllDeviceRsp wifiDeviceGetAllDeviceRsp = (WifiDeviceGetAllDeviceRsp) new Gson().fromJson(str, WifiDeviceGetAllDeviceRsp.class);
                    if (wifiDeviceGetAllDeviceRsp != null) {
                        LogUtil.d("HWCloudUtils", "getUserAllWifiDevice ResultCode = ", wifiDeviceGetAllDeviceRsp.getResultCode());
                        if (Objects.equals(wifiDeviceGetAllDeviceRsp.getResultCode(), 0)) {
                            iCloudOperationResult.operationResult(wifiDeviceGetAllDeviceRsp, null, true);
                        } else {
                            iCloudOperationResult.operationResult(wifiDeviceGetAllDeviceRsp, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " getUserAllWifiDevice JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void a(DeviceLinkResultNotifyRequest deviceLinkResultNotifyRequest, final ICloudOperationResult<DeviceLinkResultNotifyResponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "deviceLinkResultNotify");
        if (deviceLinkResultNotifyRequest == null || iCloudOperationResult == null) {
            LogUtil.g("HWCloudUtils", "deviceLinkResultNotify params is null.please check.");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        b(deviceLinkResultNotifyRequest, map);
        this.g.a("/deviceAgent/deviceLinkResultNotify", map, new IHttpOperationResult() { // from class: mlg.17
            @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
            public void exception(int i, Exception exc) {
                LogUtil.d("HWCloudUtils", "deviceLinkResultNotify Exception code: ", Integer.valueOf(i));
                iCloudOperationResult.operationResult(null, ExceptionUtils.a(exc), false);
            }

            @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
            public void operationResult(String str) {
                try {
                    DeviceLinkResultNotifyResponse deviceLinkResultNotifyResponse = (DeviceLinkResultNotifyResponse) new Gson().fromJson(str, DeviceLinkResultNotifyResponse.class);
                    if (deviceLinkResultNotifyResponse == null) {
                        LogUtil.g("HWCloudUtils", "responseEntity is null.");
                        return;
                    }
                    LogUtil.d("HWCloudUtils", "responseEntity ResultCode : ", deviceLinkResultNotifyResponse.getResultCode());
                    if (deviceLinkResultNotifyResponse.getResultCode().intValue() == 0) {
                        iCloudOperationResult.operationResult(deviceLinkResultNotifyResponse, null, true);
                    } else {
                        iCloudOperationResult.operationResult(deviceLinkResultNotifyResponse, null, false);
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "deviceLinkResultNotify exception : ", ExceptionUtils.a(e2));
                    iCloudOperationResult.operationResult(null, ExceptionUtils.a(e2), false);
                }
            }
        });
    }

    public void a(AddEvaluationResultReq addEvaluationResultReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter addEvaluationResult ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(addEvaluationResultReq, map);
        } catch (Exception unused) {
            LogUtil.d("HWCloudUtils", "addEvaluationResult Exception");
        }
        String strE = e(addEvaluationResultReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.3
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "addEvaluationResult Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "addEvaluationResult in operationResult");
                    try {
                        CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                        if (cloudCommonReponse != null) {
                            if (cloudCommonReponse.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "addEvaluationResult in operationResult successful");
                                iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "addEvaluationResult in operationResult fail: ", cloudCommonReponse.getResultCode());
                            iCloudOperationResult.operationResult(null, "code:" + cloudCommonReponse.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.d("HWCloudUtils", "addEvaluationResult json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "addEvaluationResult unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void a(WifiDeviceDeleteAuthorizeSubUserReq wifiDeviceDeleteAuthorizeSubUserReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter deleteWifiDeviceAuthorizeSubUser");
        String strA = pob.a(this.j.getBody(wifiDeviceDeleteAuthorizeSubUserReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceDeleteAuthorizeSubUserReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.14
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "deleteWifiDeviceAuthorizeSubUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "deleteWifiDeviceAuthorizeSubUser, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "deleteWifiDeviceAuthorizeSubUser ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "deleteWifiDeviceAuthorizeSubUser JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void a(WifiDeviceGetDeviceStatusReq wifiDeviceGetDeviceStatusReq, final ICloudOperationResult<WifiDeviceGetDeviceStatusRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getWifiDeviceStatus");
        String strA = pob.a(this.j.getBody(wifiDeviceGetDeviceStatusReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceGetDeviceStatusReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.16
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getWifiDeviceStatus in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getWifiDeviceStatus, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    WifiDeviceGetDeviceStatusRsp wifiDeviceGetDeviceStatusRsp = (WifiDeviceGetDeviceStatusRsp) new Gson().fromJson(str, WifiDeviceGetDeviceStatusRsp.class);
                    if (wifiDeviceGetDeviceStatusRsp != null) {
                        LogUtil.d("HWCloudUtils", "getWifiDeviceStatus ResultCode = ", wifiDeviceGetDeviceStatusRsp.getResultCode());
                        if (wifiDeviceGetDeviceStatusRsp.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(wifiDeviceGetDeviceStatusRsp, null, true);
                        } else {
                            iCloudOperationResult.operationResult(wifiDeviceGetDeviceStatusRsp, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " getWifiDeviceStatus JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void a(GetUserMergeInfoReq getUserMergeInfoReq, final ICloudOperationResult<GetUserMergeInfoRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getUserMergeInfo");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(getUserMergeInfoReq, map);
        } catch (Exception unused) {
            LogUtil.a("HWCloudUtils", "getUserMergeInfo Exception");
        }
        String strE = e(getUserMergeInfoReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.24
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "getUserMergeInfo Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "getUserMergeInfo in operationResult");
                    try {
                        GetUserMergeInfoRsp getUserMergeInfoRsp = (GetUserMergeInfoRsp) new Gson().fromJson(str, GetUserMergeInfoRsp.class);
                        if (getUserMergeInfoRsp != null) {
                            if (getUserMergeInfoRsp.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "getUserMergeInfo in operationResult successful");
                                iCloudOperationResult.operationResult(getUserMergeInfoRsp, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "getUserMergeInfo in operationResult fail: ", getUserMergeInfoRsp.getResultCode());
                            iCloudOperationResult.operationResult(null, Metadata.EMPTY_ID + getUserMergeInfoRsp.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.a("HWCloudUtils", "getUserMergeInfo json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.d("HWCloudUtils", "getUserMergeInfo unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void b(WifiDeviceAddAuthMsgBySubUserReq wifiDeviceAddAuthMsgBySubUserReq, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter wifiDeviceAddAuthMsgBySubUser");
        b(pob.a(this.j.getBody(wifiDeviceAddAuthMsgBySubUserReq)), wifiDeviceAddAuthMsgBySubUserReq.getUrl(), iCloudOperationResult);
    }

    public void b(WifiDeviceExitAuthorizeSubUserReq wifiDeviceExitAuthorizeSubUserReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter exitWifiDeviceAuthorizeSubUser");
        String strA = pob.a(this.j.getBody(wifiDeviceExitAuthorizeSubUserReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceExitAuthorizeSubUserReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.15
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "exitWifiDeviceAuthorizeSubUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "deleteWifiDeviceAuthorizeSubUser, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "exitWifiDeviceAuthorizeSubUser ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "exitWifiDeviceAuthorizeSubUser JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void b(WifiDeviceGetMainUserAuth wifiDeviceGetMainUserAuth, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter authorizeBySubUser");
        b(pob.a(this.j.getBody(wifiDeviceGetMainUserAuth)), wifiDeviceGetMainUserAuth.getUrl(), iCloudOperationResult);
    }

    public void b(WifiDeviceGetWifiDeviceInfoReq wifiDeviceGetWifiDeviceInfoReq, final ICloudOperationResult<WifiDeviceGetWifiDeviceInfoRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getWifiDeviceInfo");
        String strA = pob.a(this.j.getBody(wifiDeviceGetWifiDeviceInfoReq));
        Map<String, String> headers = this.j.getHeaders();
        String requestUrl = this.j.getRequestUrl(wifiDeviceGetWifiDeviceInfoReq.getUrl());
        LogUtil.e("HWCloudUtils", "getWifiDeviceInfo, httpUrl: ", requestUrl, " params: ", strA);
        this.b.a(requestUrl, strA, headers, new WifiRequestCallBack() { // from class: mlg.8
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getWifiDeviceInfo in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getWifiDeviceInfo, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    LogUtil.e("HWCloudUtils", "getWifiDeviceInfo json: ", str);
                    try {
                        JSONObject jSONObject = new JSONObject(str);
                        LogUtil.d("HWCloudUtils", "getWifiDeviceInfo primal json resultCode: ", Integer.valueOf(jSONObject.has("resultCode") ? jSONObject.getInt("resultCode") : -1));
                    } catch (JSONException unused) {
                        LogUtil.a("HWCloudUtils", "getWifiDeviceInfo JSONException");
                    }
                    WifiDeviceGetWifiDeviceInfoRsp wifiDeviceGetWifiDeviceInfoRsp = (WifiDeviceGetWifiDeviceInfoRsp) new Gson().fromJson(str, WifiDeviceGetWifiDeviceInfoRsp.class);
                    if (wifiDeviceGetWifiDeviceInfoRsp != null) {
                        LogUtil.d("HWCloudUtils", "getWifiDeviceInfo ResultCode = ", wifiDeviceGetWifiDeviceInfoRsp.getResultCode());
                        if (wifiDeviceGetWifiDeviceInfoRsp.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(wifiDeviceGetWifiDeviceInfoRsp, null, true);
                        } else {
                            iCloudOperationResult.operationResult(wifiDeviceGetWifiDeviceInfoRsp, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " getWifiDeviceInfo JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void b(final WifiDeviceServiceInfoReq wifiDeviceServiceInfoReq, final ICloudOperationResult<WifiDeviceServiceInfoRsp> iCloudOperationResult) {
        ThreadPoolManager.a().b("getWifiDeviceServiceInfo", new Runnable() { // from class: mlf
            @Override // java.lang.Runnable
            public final void run() {
                this.c.e(wifiDeviceServiceInfoReq, iCloudOperationResult);
            }
        });
    }

    public void b(WifiDeviceShareMemberInfoBySubUserReq wifiDeviceShareMemberInfoBySubUserReq, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter getMemberInfoBySubUser");
        b(pob.a(this.j.getBody(wifiDeviceShareMemberInfoBySubUserReq)), wifiDeviceShareMemberInfoBySubUserReq.getUrl(), iCloudOperationResult);
    }

    public void b(WifiDeviceUnbindReq wifiDeviceUnbindReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter unBindWifiDevice");
        String strA = pob.a(this.j.getBody(wifiDeviceUnbindReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceUnbindReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.10
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "unBindWifiDevice in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "unBindWifiDevice, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "unBindWifiDevice ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " unBindWifiDevice JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void b(Object obj, HashMap<String, Object> map) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                String name = field.getName();
                boolean zIsAccessible = field.isAccessible();
                if (!zIsAccessible) {
                    field.setAccessible(true);
                }
                Object obj2 = field.get(obj);
                if (!zIsAccessible) {
                    field.setAccessible(false);
                }
                if (obj2 != null) {
                    map.put(name, obj2);
                }
            }
        } catch (IllegalAccessException e2) {
            LogUtil.a("HWCloudUtils", "HWCloudUtils IllegalAccessException E: ", e2.getMessage());
        } catch (IllegalArgumentException e3) {
            LogUtil.a("HWCloudUtils", "HWCloudUtils IllegalArgumentException E: ", e3.getMessage());
        }
    }

    public void c(GetEvaluationResultReq getEvaluationResultReq, final ICloudOperationResult<GetEvaluationResultRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getEvaluationResult  ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(getEvaluationResultReq, map);
        } catch (Exception unused) {
            LogUtil.d("HWCloudUtils", "getEvaluationResult eception");
        }
        String strE = e(getEvaluationResultReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.5
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "getEvaluationResult Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "getEvaluationResult in operationResult");
                    try {
                        GetEvaluationResultRsp getEvaluationResultRsp = (GetEvaluationResultRsp) new Gson().fromJson(str, GetEvaluationResultRsp.class);
                        if (getEvaluationResultRsp != null) {
                            if (getEvaluationResultRsp.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "getEvaluationResult in operationResult successful");
                                iCloudOperationResult.operationResult(getEvaluationResultRsp, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "getEvaluationResult in operationResult fail: ", getEvaluationResultRsp.getResultCode());
                            iCloudOperationResult.operationResult(null, "code:" + getEvaluationResultRsp.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.d("HWCloudUtils", "getEvaluationResult json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "getEvaluationResult unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void c(WifiDeviceAuthorizeByMainUserReq wifiDeviceAuthorizeByMainUserReq, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter wifiDeviceAuthorizeByMainUser");
        b(pob.a(this.j.getBody(wifiDeviceAuthorizeByMainUserReq)), wifiDeviceAuthorizeByMainUserReq.getUrl(), iCloudOperationResult);
    }

    public void c(WifiDeviceGetAuthorizeSubUserReq wifiDeviceGetAuthorizeSubUserReq, final ICloudOperationResult<WifiDeviceGetAuthorizeSubUserRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getWifiDeviceAuthorizeSubUser");
        String strA = pob.a(this.j.getBody(wifiDeviceGetAuthorizeSubUserReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceGetAuthorizeSubUserReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.11
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getWifiDeviceAuthorizeSubUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getWifiDeviceAuthorizeSubUser, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    WifiDeviceGetAuthorizeSubUserRsp wifiDeviceGetAuthorizeSubUserRsp = (WifiDeviceGetAuthorizeSubUserRsp) new Gson().fromJson(str, WifiDeviceGetAuthorizeSubUserRsp.class);
                    if (wifiDeviceGetAuthorizeSubUserRsp != null) {
                        LogUtil.d("HWCloudUtils", "getWifiDeviceAuthorizeSubUser ResultCode = ", wifiDeviceGetAuthorizeSubUserRsp.getResultCode());
                        if (wifiDeviceGetAuthorizeSubUserRsp.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(wifiDeviceGetAuthorizeSubUserRsp, null, true);
                        } else {
                            iCloudOperationResult.operationResult(wifiDeviceGetAuthorizeSubUserRsp, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "getWifiDeviceAuthorizeSubUser, JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void c(WifiDeviceShareByMainUserReq wifiDeviceShareByMainUserReq, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter addAuthMsgByMainUser");
        b(pob.a(this.j.getBody(wifiDeviceShareByMainUserReq)), wifiDeviceShareByMainUserReq.getUrl(), iCloudOperationResult);
    }

    public void c(WifiDeviceSubuserAuthorize wifiDeviceSubuserAuthorize, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter authorizeBySubUser");
        b(pob.a(this.j.getBody(wifiDeviceSubuserAuthorize)), wifiDeviceSubuserAuthorize.getUrl(), iCloudOperationResult);
    }

    public void c(WifiDeviceUpdateAuthorizeSubUserReq wifiDeviceUpdateAuthorizeSubUserReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter upateWifiDeviceAuthorizeSubUser");
        String strA = pob.a(this.j.getBody(wifiDeviceUpdateAuthorizeSubUserReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceUpdateAuthorizeSubUserReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.13
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "upateWifiDeviceAuthorizeSubUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "updateWifiDeviceAuthorizeSubUser, json is null");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "upateWifiDeviceAuthorizeSubUser ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "upateWifiDeviceAuthorizeSubUser, JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void c(UpdateCommentReq updateCommentReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", " Enter updateCommentRemainderTimes ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(updateCommentReq, map);
        } catch (Exception unused) {
            LogUtil.a("HWCloudUtils", "updateCommentRemainderTimes Exception");
        }
        String strE = e(updateCommentReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.19
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "addPrivacyRecord Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "updateCommentRemainderTimes in operationResult");
                    try {
                        CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                        if (cloudCommonReponse != null) {
                            if (cloudCommonReponse.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "updateCommentRemainderTimes in operationResult successful");
                                iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "updateCommentRemainderTimes in operationResult fail: ", cloudCommonReponse.getResultCode());
                            iCloudOperationResult.operationResult(null, "code:" + cloudCommonReponse.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.a("HWCloudUtils", "updateCommentRemainderTimes json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "updateCommentRemainderTimes unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void d(DelEvaluationResultReq delEvaluationResultReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter delEvaluationResult ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(delEvaluationResultReq, map);
        } catch (Exception unused) {
            LogUtil.d("HWCloudUtils", "delEvaluationResult Exception");
        }
        String strE = e(delEvaluationResultReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.2
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "delEvaluationResult Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "delEvaluationResult in operationResult");
                    try {
                        CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                        if (cloudCommonReponse != null) {
                            if (cloudCommonReponse.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "delEvaluationResult in operationResult successful");
                                iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "delEvaluationResult  in operationResult fail: ", cloudCommonReponse.getResultCode());
                            iCloudOperationResult.operationResult(null, "code:" + cloudCommonReponse.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.d("HWCloudUtils", "delEvaluationResult json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "delEvaluationResult unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void d(WifiDeviceControlDataModelReq wifiDeviceControlDataModelReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter syncWifiDeviceControl");
        String strA = pob.a(this.j.getBody(wifiDeviceControlDataModelReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceControlDataModelReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.4
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "syncWifiDeviceControl in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "syncWifiDeviceControl, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "syncWifiDeviceControl ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                        } else {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " syncWifiDeviceControl JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void d(WifiDeviceGetDeviceRegistration wifiDeviceGetDeviceRegistration, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter getDeviceRegistration");
        b(pob.a(this.j.getBody(wifiDeviceGetDeviceRegistration)), wifiDeviceGetDeviceRegistration.getUrl(), iCloudOperationResult);
    }

    public void d(WifiDeviceGetSubUserAuthMsgReq wifiDeviceGetSubUserAuthMsgReq, ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "enter wifiDeviceGetSubUserAuthMsg");
        b(pob.a(this.j.getBody(wifiDeviceGetSubUserAuthMsgReq)), wifiDeviceGetSubUserAuthMsgReq.getUrl(), iCloudOperationResult);
    }

    public void d(MergeUserAllDataReq mergeUserAllDataReq, final ICloudOperationResult<MergeUserAllDataRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter mergeUserAllData ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(mergeUserAllDataReq, map);
        } catch (Exception unused) {
            LogUtil.a("HWCloudUtils", "mergeUserAllData Exception");
        }
        String strE = e(mergeUserAllDataReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.22
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "mergeUserAllData Exception code: ", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "mergeUserAllData in operationResult");
                    try {
                        MergeUserAllDataRsp mergeUserAllDataRsp = (MergeUserAllDataRsp) new Gson().fromJson(str, MergeUserAllDataRsp.class);
                        if (mergeUserAllDataRsp != null) {
                            if (mergeUserAllDataRsp.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "mergeUserAllData in operationResult successful");
                                iCloudOperationResult.operationResult(mergeUserAllDataRsp, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "mergeUserAllData in operationResult fail: ", mergeUserAllDataRsp.getResultCode());
                            iCloudOperationResult.operationResult(null, Metadata.EMPTY_ID + mergeUserAllDataRsp.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.a("HWCloudUtils", "mergeUserAllData json exception : ", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "mergeUserAllData unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }

    public void e(final ICloudOperationResult<WifiDeviceGetVerifyCodeForMainUserRsp> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getWifiDeviceVerifyCodeForMainUser");
        String strA = pob.a(this.j.getBody(null));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl("/deviceAgent/getVerifyCode"), strA, headers, new WifiRequestCallBack() { // from class: mlg.20
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getWifiDeviceVerifyCodeForMainUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getWifiDeviceVerifyCodeForMainUser, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    WifiDeviceGetVerifyCodeForMainUserRsp wifiDeviceGetVerifyCodeForMainUserRsp = (WifiDeviceGetVerifyCodeForMainUserRsp) new Gson().fromJson(str, WifiDeviceGetVerifyCodeForMainUserRsp.class);
                    if (wifiDeviceGetVerifyCodeForMainUserRsp != null) {
                        LogUtil.d("HWCloudUtils", "getWifiDeviceVerifyCodeForMainUser ResultCode = ", wifiDeviceGetVerifyCodeForMainUserRsp.getResultCode());
                        if (wifiDeviceGetVerifyCodeForMainUserRsp.getResultCode().intValue() == 0) {
                            iCloudOperationResult.operationResult(wifiDeviceGetVerifyCodeForMainUserRsp, null, true);
                        } else {
                            iCloudOperationResult.operationResult(wifiDeviceGetVerifyCodeForMainUserRsp, null, false);
                        }
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " getWifiDeviceVerifyCodeForMainUser JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void e(WifiDeviceAddAuthorizeForSubUserReq wifiDeviceAddAuthorizeForSubUserReq, final ICloudOperationResult<CloudCommonReponse> iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter addWifiDeviceAuthorizeForSubUser");
        String strA = pob.a(this.j.getBody(wifiDeviceAddAuthorizeForSubUserReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceAddAuthorizeForSubUserReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.12
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "addWifiDeviceAuthorizeForSubUser in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "addWifiDeviceAuthorizeForSubUser, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    CloudCommonReponse cloudCommonReponse = (CloudCommonReponse) new Gson().fromJson(str, CloudCommonReponse.class);
                    if (cloudCommonReponse != null) {
                        LogUtil.d("HWCloudUtils", "addWifiDeviceAuthorizeForSubUser ResultCode = ", cloudCommonReponse.getResultCode());
                        if (cloudCommonReponse.getResultCode().intValue() != 0 && cloudCommonReponse.getResultCode().intValue() != 112000020) {
                            iCloudOperationResult.operationResult(cloudCommonReponse, null, false);
                            return;
                        }
                        iCloudOperationResult.operationResult(cloudCommonReponse, null, true);
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", "addWifiDeviceAuthorizeForSubUser JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    /* synthetic */ void e(WifiDeviceServiceInfoReq wifiDeviceServiceInfoReq, final ICloudOperationResult iCloudOperationResult) {
        LogUtil.d("HWCloudUtils", "Enter getWifiDeviceServiceInfo");
        String strA = pob.a(this.j.getBody(wifiDeviceServiceInfoReq));
        Map<String, String> headers = this.j.getHeaders();
        this.b.a(this.j.getRequestUrl(wifiDeviceServiceInfoReq.getUrl()), strA, headers, new WifiRequestCallBack() { // from class: mlg.6
            @Override // com.huawei.hwcloudmodel.callback.WifiRequestCallBack
            public void operationResult(String str) {
                LogUtil.d("HWCloudUtils", "getWifiDeviceServiceInfo in operationResult text=***");
                try {
                    if (TextUtils.isEmpty(str)) {
                        LogUtil.g("HWCloudUtils", "getWifiDeviceServiceInfo, json is empty");
                        iCloudOperationResult.operationResult(null, null, false);
                        return;
                    }
                    WifiDeviceServiceInfoRsp wifiDeviceServiceInfoRsp = new WifiDeviceServiceInfoRsp();
                    wifiDeviceServiceInfoRsp.toObject(str);
                    LogUtil.d("HWCloudUtils", "getWifiDeviceServiceInfo ResultCode = ", wifiDeviceServiceInfoRsp.getResultCode());
                    if (wifiDeviceServiceInfoRsp.getResultCode().intValue() == 0) {
                        iCloudOperationResult.operationResult(wifiDeviceServiceInfoRsp, null, true);
                    } else {
                        iCloudOperationResult.operationResult(wifiDeviceServiceInfoRsp, null, false);
                    }
                } catch (JsonSyntaxException e2) {
                    LogUtil.a("HWCloudUtils", " getWifiDeviceServiceInfo JsonSyntaxException");
                    iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                }
            }
        });
    }

    public void e(GetCommentReq getCommentReq, final ICloudOperationResult<GetCommentRsp> iCloudOperationResult) {
        if (getCommentReq == null || iCloudOperationResult == null) {
            LogUtil.g("HWCloudUtils", "getCommentRemainderTimes req or cb is null");
            return;
        }
        LogUtil.d("HWCloudUtils", " Enter getCommentRemainderTimes ");
        HashMap<String, Object> map = new HashMap<>();
        try {
            b(getCommentReq, map);
        } catch (Exception unused) {
            LogUtil.a("HWCloudUtils", "getCommentRemainderTimes Exception");
        }
        String strE = e(getCommentReq);
        if (strE != null) {
            this.g.a(strE, map, new IHttpOperationResult() { // from class: mlg.9
                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void exception(int i, Exception exc) {
                    LogUtil.d("HWCloudUtils", "addPrivacyRecord Exception code:", Integer.valueOf(i));
                    iCloudOperationResult.operationResult(null, exc.getMessage(), false);
                }

                @Override // com.huawei.hwcloudmodel.callback.IHttpOperationResult
                public void operationResult(String str) {
                    LogUtil.d("HWCloudUtils", "getCommentRemainderTimes in operationResult");
                    try {
                        GetCommentRsp getCommentRsp = (GetCommentRsp) new Gson().fromJson(str, GetCommentRsp.class);
                        if (getCommentRsp != null) {
                            if (getCommentRsp.getResultCode().intValue() == 0) {
                                LogUtil.d("HWCloudUtils", "getCommentRemainderTimes in operationResult successful");
                                iCloudOperationResult.operationResult(getCommentRsp, null, true);
                                return;
                            }
                            LogUtil.d("HWCloudUtils", "getCommentRemainderTimes in operationResult fail:", getCommentRsp.getResultCode());
                            iCloudOperationResult.operationResult(null, "code:" + getCommentRsp.getResultCode(), false);
                        }
                    } catch (JsonSyntaxException e2) {
                        LogUtil.a("HWCloudUtils", "getCommentRemainderTimes json exception :", e2.getMessage());
                        iCloudOperationResult.operationResult(null, e2.getMessage(), false);
                    }
                }
            });
        } else {
            LogUtil.g("HWCloudUtils", "getCommentRemainderTimes unknown url");
            iCloudOperationResult.operationResult(null, "unknown url", false);
        }
    }
}
