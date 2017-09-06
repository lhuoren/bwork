package com.singun.openvpn.util;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.singun.openvpn.receiver.PhoneStatReceiver;

import java.lang.reflect.Method;

/**
 * Created by access on 2017/8/9.
 */

public class LimitCallPhoneUtil {
    private static String TAG = "tag";
    private static Context mContext;
    private static Handler handler = new Handler();
    /**
     * 挂断电话
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void endCall(Context context)
    {
        mContext = context;
        TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try
        {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            Log.e(TAG, "End call.");
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            iTelephony.endCall();
            handler.post(new ToastRunnable("自动挂断了电话" ));
        }
        catch (Exception e)
        {
//            handler.post(new ToastRunnable("Fail to answer ring call." +e));
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }
    private static class ToastRunnable implements Runnable {
        String mText;
        public ToastRunnable(String text) {
            mText = text;
        }
        @Override
        public void run(){
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }


}
