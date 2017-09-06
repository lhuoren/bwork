package com.singun.openvpn.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.singun.openvpn.Launcher;
import com.singun.openvpn.client.R;
import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class OutGoingReceiver extends BroadcastReceiver {
    Context mcontext;
    Handler handler = new Handler();
    private List<String> numberList;
    private boolean callOutFlag =false;
    private String number;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0123:
                    handler.post(new ToastRunnable(callOutFlag +":"+mcontext.getResources().getString(R.string.OutGoingReceiver_hang_up_phone)));
                    LogUtil.i("callOutFlag：", callOutFlag+":"+mcontext.getResources().getString(R.string.OutGoingReceiver_hang_up_phone));
                    if(callOutFlag){
                        setResultData("");
                        LogUtil.i("callOutFlag：", callOutFlag+"setResultData");
                    }else {
                        setResultData(number);
//                        setResultData("");
                    }
                    numberList.clear();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        mcontext=context;
        numberList = new ArrayList<>();
        TelephonyManager telMgr = (TelephonyManager) mcontext.getSystemService(Service.TELEPHONY_SERVICE);
        if(!numberList.isEmpty()){
            numberList.clear();
        }
        numberList.add("18978501605");
        numberList.add("18178521010");
        number = getResultData();
        LogUtil.i("outBootBroadcastReceiverFlag", BootBroadcastReceiver.outBootBroadcastReceiverFlag);
        handler.post(new ToastRunnable(mcontext.getResources().getString(R.string.OutGoingReceiver_starting_up_phone)+BootBroadcastReceiver.outBootBroadcastReceiverFlag ));
        if (Launcher.getInstance().mIn == true) {
            for(int i=0;i<numberList.size();i++){
                if (number.trim().equals(numberList.get(i).trim())) {
                    callOutFlag = false;
                    break;
                }else {
                    callOutFlag = true;
                }
            }
            LogUtil.i("callOutFlag：", callOutFlag+mcontext.getResources().getString(R.string.OutGoingReceiver_hang_up_phone));
            if(callOutFlag){
                setResultData("");
                LogUtil.i("callOutFlag：", callOutFlag+"setResultData");
            }else {
                setResultData(number);
            }
        }

//        if (!OutGoingReceiverInFlag) {
//            LogUtil.i("18978501605", number);
//            setResultData(number);
//        }
    }

    private class ToastRunnable implements Runnable {
        String mText;
        public ToastRunnable(String text) {
            mText = text;
        }
        @Override
        public void run(){
            Toast.makeText(mcontext, mText, Toast.LENGTH_SHORT).show();
        }
    }
}