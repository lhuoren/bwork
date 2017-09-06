package com.singun.openvpn.api.AppMemLoginIMEI;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by lhuoren
 */

public class ApiAppMemLoginIMEI {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     * 获取用户userid
     */
    public static void AppMemLoginIMEI(String imei, Callback callback){
        okHttpUtils
                .post()
                .url(constant.AppMemLoginIMEIUrl)
                .addParams("imei",imei)
                .tag("AppMemLoginIMEI")
                .build()
                .execute(callback);
    }

    public static void cancelApiAppMemLoginIMEIGetTag(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
