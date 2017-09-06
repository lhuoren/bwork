package com.singun.openvpn.mvp.gpsservice;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.singun.openvpn.Launcher;
import com.singun.openvpn.api.TAHGPSRail.ApiTAHGPSRailGet;
import com.singun.openvpn.api.TAHUserArriveTimeSave.ApiTAHUserArriveTimeSave;
import com.singun.openvpn.api.TAHUserPath.ApiTAHUserPathSave;
import com.singun.openvpn.client.R;
import com.singun.openvpn.util.LogUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by access on 2017/8/14.
 */

public class GpsServicePresenter implements GpsServiceContract.Present{
    private GpsServiceContract.View mView;

    public GpsServicePresenter(GpsServiceContract.View view)
    {
        this.mView = view;
    }

    @Override
    public void postTAHUserPathSaveAddData(final String userId,final String egps, final Context context) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        ApiTAHUserPathSave.TAHUserPathSaveAdd(
                userId,
                egps,
                new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                mView.postTAHUserPathSaveAddDataError(context.getResources().getString(R.string.contact_net_error));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                LogUtil.i("postTAHUserPathSaveModresponse", response);
                                mView.postTAHUserPathSaveAddDataSuccess("uid:"+userId+",gps:"+egps+",response:"+response);
                            }
                        });
    }

    @Override
    public void getTAHGPSRailGetData(final Context context) {
        //2贵港8南宁
        ApiTAHGPSRailGet.TAHGPSRailGet("8",
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mView.getTAHGPSRailGetDataError(context.getResources().getString(R.string.contact_net_error));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("getDataresponse", response);
//                        Toast.makeText(getApplicationContext(),"成功获取围栏",Toast.LENGTH_SHORT).show();
                        //创建容器，由我们手动创建javaBean知道，要储存Tngou
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray rows = rootObject.getJSONArray("rows");//得到数组，再一个个遍历
                            LogUtil.i("rowsrows", rows);
                            for (int i = 0; i < rows.length(); i++) {
                                JSONObject object = rows.getJSONObject(i);
                                LogUtil.i("objectobject", object);
                                String gpsStr = object.getString("GPS");
                                mView.getTAHGPSRailGetDataSuccess(gpsStr);
                                LogUtil.i("gpsStr", gpsStr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }


    @Override
    public void postTAHUserArriveTimeSave(String type, String userId, final Context context) {
        if (TextUtils.isEmpty(type)&&TextUtils.isEmpty(userId)) {
            return;
        }
        ApiTAHUserArriveTimeSave.TAHUserArriveTimeSave(type, userId,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mView.postTAHUserArriveTimeSaveError(context.getResources().getString(R.string.contact_net_error));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("postTAHUserArriveTimeSaveonResponse", response);
                    }
                });
    }

    @Override
    public void start() {
    }
}
