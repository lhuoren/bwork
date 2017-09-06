package com.singun.openvpn.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by access on 2017/7/14.
 */

public class BootService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    public static void startForeground(Service service){
        Notification.Builder builder = new Notification.Builder(service);
        builder.setContentTitle("The service is running").setAutoCancel(true);
        Notification notification = builder.getNotification();
        service.startForeground(818,notification);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
