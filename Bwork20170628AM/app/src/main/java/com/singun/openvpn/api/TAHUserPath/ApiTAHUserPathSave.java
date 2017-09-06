package com.singun.openvpn.api.TAHUserPath;

import com.singun.openvpn.util.constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.OkHttpClient;

/**
 * Created by lhuoren
 */

public class ApiTAHUserPathSave {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static OkHttpUtils okHttpUtils = new OkHttpUtils(okHttpClient);
    /**
     * 提交定点坐标
     */
    public static void TAHUserPathSaveAdd(String userid, String egps, Callback callback){
        okHttpUtils
                .post()
                .url(constant.TAHUserPathSaveUrl)
                .addParams("hAct","add")
                .addParams("userid",userid)
                .addParams("egps",egps)
                .tag("TAHUserPathSaveAdd")
                .build()
                .execute(callback);
    }

    public static void cancelApiTAHUserPathSaveAddTag(String cancelTagStr){
        //可以取消同一个tag的
        okHttpUtils.cancelTag(cancelTagStr);//取消以cancelTagStr作为tag的请求
    }
}
