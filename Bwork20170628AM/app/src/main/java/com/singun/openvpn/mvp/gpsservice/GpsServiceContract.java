package com.singun.openvpn.mvp.gpsservice;

import android.content.Context;

import com.singun.openvpn.mvp.base.BasePresenter;
import com.singun.openvpn.mvp.base.BaseView;

/**
 * Created by access on 2017/8/14.
 */

public class GpsServiceContract {
    public interface View extends BaseView<Present> {
        void postTAHUserPathSaveAddDataError(String msg);
        void postTAHUserPathSaveAddDataSuccess(String msg);

        void getTAHGPSRailGetDataError(String msg);
        void getTAHGPSRailGetDataSuccess(String gpsStr);

        void postTAHUserArriveTimeSaveError(String msg);
        void postTAHUserArriveTimeSaveSuccess();
    }
    public interface Present extends BasePresenter {
        void postTAHUserPathSaveAddData(String userId, String egps, Context context);
        void getTAHGPSRailGetData(Context context);
        void postTAHUserArriveTimeSave(String type, String userId, Context context);
    }
}
