package com.singun.openvpn.util;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.singun.openvpn.Launcher;
import com.singun.openvpn.db.dao.SavePackageNameDao;
import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.service.NewAccessibilityService;

import java.util.List;

/**
 * Created by access on 2017/7/31.
 */

public class ToolsUtil {
    public static final String TAG = "Launcher";
    //检查无障碍模式是否打开
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = mContext.getPackageName() + "/" + NewAccessibilityService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
//            Settings.Secure.putInt(mContext.getApplicationContext().getContentResolver(),
//                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED,1);
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }


    /**
     * 强制帮用户打开GPS //android5.0、7.0无效
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**获取并存储手机中所有的App*/
    public static void saveAndLoadApps(Context context) {
        PackageManager pckMan = context.getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages( 0 );//所有包名集合

        //隐藏桌面应用图标
        String packageName = null;
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 24) {
            //for循环遍历ResolveInfo对象获取包名和类名
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo info = (PackageInfo) packs.get(i);
                packageName = info.applicationInfo.packageName;
                LogUtil.i("saveAndLoadApps",packageName);
                if (info.applicationInfo.uid > 10000 ){
                    SavePackageNameDao visitCityDao = new SavePackageNameDao(context);
                    visitCityDao.add(packageName);
                    visitCityDao.mDb.close();
                }
            }
        }
    }

}
