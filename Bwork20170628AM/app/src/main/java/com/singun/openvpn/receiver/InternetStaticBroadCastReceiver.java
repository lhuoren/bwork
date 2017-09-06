package com.singun.openvpn.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class InternetStaticBroadCastReceiver extends BroadcastReceiver{
    private Context mContext;
    Handler handler = new Handler();
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
//        handler.post(new ToastRunnable("123456789" ));
    }
    private class ToastRunnable implements Runnable {
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