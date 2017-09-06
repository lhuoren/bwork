package com.singun.openvpn.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.singun.openvpn.Launcher;
import com.singun.openvpn.client.R;
import com.singun.openvpn.receiver.BootBroadcastReceiver;
import com.singun.openvpn.util.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by access on 2017/8/9.
 */

public class IncomingCallActivity extends Activity{
    private final static String TAG = GpsService.class.getSimpleName();
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    private TelephonyManager telMgr;
    @RequiresApi(api = Build.VERSION_CODES.M)

    private static IncomingCallActivity incomingCallActivity;
    public static IncomingCallActivity getInstance() {
        return incomingCallActivity;
    }
//    private MyReceiver receiver;
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        incomingCallActivity = this;
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        setContentView(R.layout.activity_incoming);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题  必须在setContentView()方法之前调用
//        setContentView(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏


//        telMgr = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        LogUtil.i("inBootBroadcastReceiverFlag", BootBroadcastReceiver.inBootBroadcastReceiverFlag);
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkCallPhonePermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(IncomingCallActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PHONE);
//                return;
//            } else {
////                    if (BootBroadcastReceiver.inBootBroadcastReceiverFlag) {
//                endCall();
////                    }
//            }
//        } else {
////                if (BootBroadcastReceiver.inBootBroadcastReceiverFlag) {
//            endCall();
////                }
//        }
//        receiver = new MyReceiver();
//        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//
//        registerReceiver(receiver, homeFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
    }

    /**
     * 挂断电话
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void endCall() {
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            Log.e(TAG, "End call.");
            iTelephony.endCall();
            this.finish();
        } catch (Exception e) {
//            handler.post(new ToastRunnable("Fail to answer ring call." +e));
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (reason == null)
                    return;

                // Home键
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Toast.makeText(getApplicationContext(), "按了Home键", Toast.LENGTH_SHORT).show();
                }

                // 最近任务列表键
                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    Toast.makeText(getApplicationContext(), "按了最近任务列表", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
