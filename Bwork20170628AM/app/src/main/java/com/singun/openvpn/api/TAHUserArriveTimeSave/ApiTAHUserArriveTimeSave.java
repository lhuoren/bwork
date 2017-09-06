package com.singun.openvpn.api.TAHUserArriveTimeSave;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by lhuoren
 */

public class ApiTAHUserArriveTimeSave {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     * 提交定点坐标
     */
    public static void TAHUserArriveTimeSave(String type, String userid, Callback callback){
        okHttpUtils
                .post()
                .url(constant.TAHUserArriveTimeSaveUrl)
                .addParams("Type",type)
                .addParams("UserID",userid)
                .tag("TAHUserArriveTimeSave")
                .build()
                .execute(callback);
    }

    public static void cancelApiTAHUserArriveTimeSaveTag(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
