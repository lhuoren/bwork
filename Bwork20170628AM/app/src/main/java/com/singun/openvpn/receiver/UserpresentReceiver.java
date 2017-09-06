package com.singun.openvpn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.singun.openvpn.eventbus.ClearBackTaskEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by access on 2017/7/26.
 */

public class UserpresentReceiver extends BroadcastReceiver {
    private String TAG="UserpresentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        Log.e(TAG, "action="+action);
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
//            Log.e(TAG, "竟然可以解锁");
//            showRecentlyAppUtil.showRecentlyApp();
//            /**粘性eventbus策略关闭进度条*/
            EventBus.getDefault().postSticky(new ClearBackTaskEvent("UserpresentReceiver"));
        }
    }
}
