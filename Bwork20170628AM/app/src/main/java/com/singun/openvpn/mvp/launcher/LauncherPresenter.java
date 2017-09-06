package com.singun.openvpn.mvp.launcher;

import android.text.TextUtils;
import android.widget.Toast;

import com.singun.openvpn.Launcher;
import com.singun.openvpn.api.AppMemLoginIMEI.ApiAppMemLoginIMEI;
import com.singun.openvpn.api.TAHGPSRail.ApiTAHGPSRailGet;
import com.singun.openvpn.api.TAHUserArriveTimeSave.ApiTAHUserArriveTimeSave;
import com.singun.openvpn.api.TAHUserPath.ApiTAHUserPathSave;
import com.singun.openvpn.mvp.gpsservice.GpsServiceContract;
import com.singun.openvpn.util.LogUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by access on 2017/8/14.
 */

public class LauncherPresenter implements LauncherContract.Present {
    private LauncherContract.View mView;

    public LauncherPresenter(LauncherContract.View view) {
        this.mView = view;
    }


    @Override
    public void getAppMemLoginIMEI(String imei) {
        LogUtil.i("imeiimei",imei);
        if (TextUtils.isEmpty(imei)) {
            return;
        }

        ApiAppMemLoginIMEI.AppMemLoginIMEI(imei, new
                StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("AppMemLoginIMEIonError",e);
                        mView.getAppMemLoginIMEIError("网络异常或无法连接服务器");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("AppMemLoginIMEIresponse", response);
//                        Toast.makeText(Launcher.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        mCache.put("userId",response);
                        mView.getAppMemLoginIMEISuccess(response);
                    }
                });
    }

    @Override
    public void start() {

    }

}

