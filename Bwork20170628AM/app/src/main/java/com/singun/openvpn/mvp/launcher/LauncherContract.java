package com.singun.openvpn.mvp.launcher;

import com.singun.openvpn.mvp.base.BasePresenter;
import com.singun.openvpn.mvp.base.BaseView;

/**
 * Created by access on 2017/8/14.
 */

public class LauncherContract {
    public interface View extends BaseView<Present> {
        void getAppMemLoginIMEIError(String msg);
        void getAppMemLoginIMEISuccess(String userId);
    }
    public interface Present extends BasePresenter {
        void getAppMemLoginIMEI(String imei);
    }
}
