package com.singun.openvpn.receiver;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.singun.openvpn.Launcher;
import com.singun.openvpn.client.R;
import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.util.IsNetWord;
import com.singun.openvpn.util.LimitCallPhoneUtil;
import com.singun.openvpn.util.LogUtil;
import com.singun.openvpn.util.ToolsUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStatReceiver extends BroadcastReceiver{
    private final int SDK_PERMISSION_REQUEST = 127;
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    String TAG = "tag";
    TelephonyManager telMgr;
    private Context mContext;
    private Intent mIntent;
    Handler handler = new Handler();
    private static boolean incomingFlag = false;
    private static String incoming_number = null;
    private List<String> numberList;
    private boolean callInFlag =false;
    private Handler myHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    handler.post(new ToastRunnable(callInFlag +":"+mContext.getResources().getString(R.string.OutGoingReceiver_hang_up_phone)));
                    if(callInFlag){
                        endCall();
                    }
                    numberList.clear();
                    break;

                case 0x456:

                    numberList.clear();
                    break;

            }
            super.handleMessage(msg);
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        GpsService.isFirstOpenThePhone = false;
        telMgr = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        Log.i(TAG, "getAction"+intent.getAction());
        numberList = new ArrayList<>();
        numberList.add("18978501605");
        numberList.add("18178521010");
        numberList.add("15277093429");
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = mContext.checkSelfPermission(Manifest.permission.CALL_PHONE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_CALL_PHONE);
                return;
            }else{
                callingPhone();
            }
        } else {
            callingPhone();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callingPhone(){

        switch (telMgr.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING://响铃
                handler.post(new ToastRunnable(mContext.getResources().getString(R.string.OutGoingReceiver_starting_in_phone)+BootBroadcastReceiver.inBootBroadcastReceiverFlag ));
                LogUtil.i("inBootBroadcastReceiverFlag", BootBroadcastReceiver.inBootBroadcastReceiverFlag);

                incomingFlag = true;//标识当前是来电
                final String number = mIntent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.v(TAG, "number:" + number);
                SharedPreferences phonenumSP = mContext.getSharedPreferences("in_phone_num", Context.MODE_PRIVATE);
                Editor editor = phonenumSP.edit();
                editor.putString(number, number);
                editor.commit();
                handler.post(new ToastRunnable("电话打进来了" ));
                handler.post(new ToastRunnable("电话号码是："+number.trim()));

                if (Launcher.getInstance().mIn == true) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            // 在此执行耗时工作，执行完毕后调用handler发送消息
                            for(int i=0;i<numberList.size();i++){
                                if (number.trim().equals(numberList.get(i).trim())) {
                                    callInFlag = false;
                                    break;
                                }else {
                                    callInFlag = true;
                                }
                            }
                            try {
                                myHandler.sendEmptyMessage(0x123);//发送消息
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK://接听
                break;
            case TelephonyManager.CALL_STATE_IDLE://闲置
                break;
        }

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

    /**
     * 挂断电话
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void endCall()
    {
        Class<TelephonyManager> c = TelephonyManager.class;
        try
        {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            Log.e(TAG, "End call.");
            iTelephony.endCall();
        }
        catch (Exception e)
        {
//            handler.post(new ToastRunnable("Fail to answer ring call." +e));
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }
}