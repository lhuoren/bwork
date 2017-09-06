package com.singun.openvpn;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.singun.openvpn.service.GpsService;
import com.singun.openvpn.service.NewAccessibilityService;
import com.singun.openvpn.util.LogUtil;
import com.singun.openvpn.util.ToolsUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.singun.openvpn.Launcher.DEBUG_STRICT_MODE;

/**
 * Created by access on 2017/8/22.
 */

public class BaseActivity extends Activity {
    private String TAG = "BaseActivity";
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    private HomeAndTaskReceiver homeAndTaskReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        LogUtil.i("permissionspermissions","permissionspermissions");
        if (DEBUG_STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        getPersimmions();//百度地图定位权限
        hasPermission(); //检测用户是否对本app开启了“Apps with usage access”权限
        homeAndTaskReceiver = new HomeAndTaskReceiver();
        IntentFilter homeAndTaskFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeAndTaskReceiver, homeAndTaskFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(BaseActivity.this)) {
            //注册EventBus
            EventBus.getDefault().register(BaseActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeAndTaskReceiver);
        EventBus.getDefault().unregister(BaseActivity.this);//反注册EventBus
    }


    /**
     * 动态权限获取
     */
    @TargetApi(23)
    protected void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();


            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            if (checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
            }
            /**无障碍权限*/
            if (checkSelfPermission(Manifest.permission.BIND_ACCESSIBILITY_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BIND_ACCESSIBILITY_SERVICE);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_SECURE_SETTINGS);
            }
            /**来电去电广播权限*/
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CALL_PHONE);
            }
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
                LogUtil.i("permissionspermissions",permissions.size());
            }
        }
    }

    @TargetApi(23)
    protected boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    protected boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    protected class HomeAndTaskReceiver extends BroadcastReceiver {

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
//                    Toast.makeText(getApplicationContext(), "按了Home键", Toast.LENGTH_SHORT).show();
//                    try {
//                            PackageManager packageManager2 = getPackageManager();
//                            LogUtil.i("packageManager2", packageManager2);
//                            Intent intent2 = new Intent();
//                            intent2 = packageManager2.getLaunchIntentForPackage("com.singun.openvpn.client");
//                            startActivity(intent2);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(), "未发现天翼对讲APP，请安装天翼对讲APP！", Toast.LENGTH_SHORT);
//                        }
                }

                // 最近任务列表键
                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    Toast.makeText(getApplicationContext(), "按了最近任务列表", Toast.LENGTH_SHORT).show();
//                    if (null != GpsService.isFence && "INFence".equals(GpsService.isFence)) {
                    if (Launcher.getInstance().mIn == true) {
                        final NewAccessibilityService abs = NewAccessibilityService.getInstance();
                        LogUtil.i("NewAccessibilityServiceabs",abs);
                        if (ToolsUtil.isAccessibilitySettingsOn(BaseActivity.this) && null != abs) {
                            abs.clickHome();
                        }
                    }
                }
            }
        }
    }
}
