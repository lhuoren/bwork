package com.singun.openvpn.api.TAHGPSRail;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by lhuoren
 */

public class ApiTAHGPSRailGet {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     * 获取服务器电子围栏坐标
     */
    public static void TAHGPSRailGet(String hDid, Callback callback){
        okHttpUtils
                .post()
                .url(constant.TAHGPSRailGetUrl)
                .addParams("hDid",hDid)
                .tag("TAHGPSRailGet")
                .build()
                .execute(callback);
    }

    public static void cancelApiTAHGPSRailGetTag(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
