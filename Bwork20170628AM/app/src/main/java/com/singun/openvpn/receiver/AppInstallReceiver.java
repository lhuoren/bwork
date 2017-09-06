package com.singun.openvpn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.util.ACache;
import com.singun.openvpn.util.LogUtil;

/**
 * Created by access on 2017/7/19.
 */

public class AppInstallReceiver extends BroadcastReceiver {
    private ACache mCache;//缓存数据

    @Override
    public void onReceive(Context context, Intent intent) {
//        mCache = ACache.get(context);
//        PackageManager manager = context.getPackageManager();
//        StringBuilder sbAppUninstallPack = new StringBuilder(256);
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
////            int i = 0;
////            i++;
////            for(int j=0;j<i+1;j++){
////                sbAppUninstallPack.append(packageName);
////                sbAppUninstallPack.append(",");
////            }
////
////            LogUtil.i("sbAppUninstallPack",sbAppUninstallPack);
////            mCache.put("AppUninstallPackageName", sbAppUninstallPack.length()-1);
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
//        }


    }

}