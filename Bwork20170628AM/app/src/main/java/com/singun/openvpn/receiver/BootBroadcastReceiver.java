package com.singun.openvpn.receiver;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.Launcher;
import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.service.ScreenService;
import com.singun.openvpn.util.LogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public static boolean inBootBroadcastReceiverFlag = false;
    public static boolean outBootBroadcastReceiverFlag = false;
    private Context mContext;
    private Handler myHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:

                    break;
            }
            super.handleMessage(msg);
        }
    };

    //重写onReceive方法
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Handler handler = new Handler();
//        handler.post(new ToastRunnable("开机了" ));
        openSaveDesktop();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openSaveDesktop() {
        LogUtil.i("openSaveDesktop", "openSaveDesktop3");
        inBootBroadcastReceiverFlag = true;
        outBootBroadcastReceiverFlag = true;
//        mContext.startService(new Intent(mContext, GpsService.class));
//        mContext.startService(new Intent(mContext,ScreenService.class));
        try {
            PackageManager packageManager2 = mContext.getPackageManager();
            LogUtil.i("packageManager2", packageManager2);
            Intent intent2 = new Intent();
            intent2 = packageManager2.getLaunchIntentForPackage("com.singun.openvpn.client");
            mContext.startActivity(intent2);
            BootBroadcastReceiver.inBootBroadcastReceiverFlag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }

}
