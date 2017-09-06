package com.singun.openvpn.api.SavePushClientID;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by yy on 2017/8/31.
 */

public class ApiSavePushClinetID {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     * 提交定点坐标
     */
    public static void SavePushClinetID(String userid, String clientID, Callback callback){
        okHttpUtils
                .post()
                .url(constant.ApiSavePushClinetID)
                .addParams("UserID",userid)
                .addParams("ClientID",clientID)
                .tag("ApiSavePushClinetID")
                .build()
                .execute(callback);
    }

    public static void cancelSavePushClinetID(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
