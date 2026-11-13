package com.huawei.networkclient;

import com.huawei.hms.framework.network.restclient.Submit;
import com.huawei.hms.framework.network.restclient.annotate.Body;
import com.huawei.hms.framework.network.restclient.annotate.DELETE;
import com.huawei.hms.framework.network.restclient.annotate.GET;
import com.huawei.hms.framework.network.restclient.annotate.HeaderMap;
import com.huawei.hms.framework.network.restclient.annotate.POST;
import com.huawei.hms.framework.network.restclient.annotate.PUT;
import com.huawei.hms.framework.network.restclient.annotate.QueryMap;
import com.huawei.hms.framework.network.restclient.annotate.Url;
import com.huawei.hms.framework.network.restclient.hwhttp.ResponseBody;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface CommonApi {
    @GET
    Submit<ResponseBody> commonDownload(@Url String str);

    @GET
    Submit<ResponseBody> commonDownload(@Url String str, @HeaderMap Map<String, String> map);

    @POST
    Submit<ResponseBody> commonDownload(@Url String str, @HeaderMap Map<String, String> map, @Body String str2);

    @GET
    Submit<String> commonGet(@Url String str, @HeaderMap Map<String, String> map);

    @GET
    Submit<String> commonGet(@Url String str, @HeaderMap Map<String, String> map, @QueryMap Map<String, String> map2);

    @GET
    Submit<ResponseBody> commonGetRaw(@Url String str, @HeaderMap Map<String, String> map, @QueryMap Map<String, String> map2);

    @POST
    Submit<String> commonPost(@Url String str, @HeaderMap Map<String, String> map, @Body String str2);

    @POST
    Submit<ResponseBody> commonPostRaw(@Url String str, @HeaderMap Map<String, String> map, @Body String str2);

    @DELETE
    Submit<String> deleteReq(@Url String str, @HeaderMap Map<String, String> map);

    @DELETE
    Submit<ResponseBody> deleteReqRaw(@Url String str, @HeaderMap Map<String, String> map);

    @PUT
    Submit<String> putReq(@Url String str, @HeaderMap Map<String, String> map, @Body String str2);

    @PUT
    Submit<ResponseBody> putReqRaw(@Url String str, @HeaderMap Map<String, String> map, @Body String str2);
}
