package com.singun.openvpn.mvp.pushservice;

import android.content.Context;
import android.text.TextUtils;

import com.singun.openvpn.api.SavePushClientID.ApiSavePushClinetID;
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

public class PushServicePresenter implements PushServiceContract.Present{
    private PushServiceContract.View mView;

    public PushServicePresenter(PushServiceContract.View view)
    {
        this.mView = view;
    }

    @Override
    public void postSavePushClinetID(String userId, String clientID, final Context context) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        ApiSavePushClinetID.SavePushClinetID(
                userId,
                clientID,
                new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                mView.postSavePushClinetIDError(context.getResources().getString(R.string.contact_net_error));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                LogUtil.i("postSavePushClinetID_response", response);
                                mView.postSavePushClinetIDSuccess();
                            }
                        });
    }

    @Override
    public void start() {
    }
}
