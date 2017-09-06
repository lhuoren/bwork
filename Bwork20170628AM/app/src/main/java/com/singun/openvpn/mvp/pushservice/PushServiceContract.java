package com.singun.openvpn.mvp.pushservice;

import android.content.Context;

import com.singun.openvpn.mvp.base.BasePresenter;
import com.singun.openvpn.mvp.base.BaseView;

/**
 * Created by access on 2017/8/14.
 */

public class PushServiceContract {
    public interface View extends BaseView<Present> {
        void postSavePushClinetIDError(String msg);
        void postSavePushClinetIDSuccess();

    }
    public interface Present extends BasePresenter {
        void postSavePushClinetID(String userId, String client, Context context);

    }
}
