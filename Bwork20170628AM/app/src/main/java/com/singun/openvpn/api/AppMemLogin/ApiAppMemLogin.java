package com.singun.openvpn.api.AppMemLogin;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by lhuoren
 */

public class ApiAppMemLogin {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     *
     */
    public static void AppMemLogin(String imei, Callback callback){
        okHttpUtils
                .post()
                .url(constant.AppMemLoginUrl)
                .addParams("imei",imei)
                .tag("TAHGPSRailGet")
                .build()
                .execute(callback);
    }

    public static void cancelApiAppMemLoginGetTag(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
