package com.singun.openvpn.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageInfo;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import android.app.Activity;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.miui.enterprise.sdk.ApplicationManager;
import com.miui.enterprise.sdk.DeviceManager;
import com.miui.enterprise.sdk.PhoneManager;
import com.miui.enterprise.sdk.RestrictionsManager;
import com.singun.openvpn.Launcher;
import com.singun.openvpn.api.TAHGPSRail.ApiTAHGPSRailGet;
import com.singun.openvpn.api.TAHUserPath.ApiTAHUserPathSave;
import com.singun.openvpn.client.R;
import com.singun.openvpn.db.dao.SavePackageNameDao;
import com.singun.openvpn.eventbus.CheckGPSEvent;
import com.singun.openvpn.eventbus.ClearBackTaskEvent;
import com.singun.openvpn.eventbus.StrategyChangeEvent;
import com.singun.openvpn.mvp.gpsservice.GpsServiceContract;
import com.singun.openvpn.mvp.gpsservice.GpsServicePresenter;
import com.singun.openvpn.receiver.BootBroadcastReceiver;
import com.singun.openvpn.receiver.MyPolicyReceiver;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.singun.openvpn.receiver.OutGoingReceiver;
import com.singun.openvpn.receiver.PhoneStatReceiver;
import com.singun.openvpn.util.ACache;
import com.singun.openvpn.util.BluetoothManager;
import com.singun.openvpn.util.LogUtil;
import com.singun.openvpn.util.ToolsUtil;
import com.singun.openvpn.util.UserUtils;

import android.util.SparseArray;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.greenrobot.eventbus.EventBus;

import static android.provider.Settings.Secure.SKIP_FIRST_USE_HINTS;
import static com.miui.enterprise.sdk.ApplicationManager.FLAG_LOCK;

public class GpsService extends Service implements GpsServiceContract.View {
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;

    private boolean threadDisable = false;
    private final static String TAG = GpsService.class.getSimpleName();
    public MyLocationListener mMyLocationListener;
    public LocationClient mLocationClient;
    //    private ComponentName mComponentName;
    private int locklauncher = 0; //0初始,1开,2关
    private static final String PREFS_DEVICE_OWNER = "launcher";
    private static final String PREF_LAUNCHER = "launcher";
    private ArrayList<PointD> ptLists;//围栏集合
    PowerManager.WakeLock wakeLock = null;
    private ACache mCache;//缓存数据
    private boolean isAppRunning = false;
    private boolean isFirstInstallOrRunning = true;
    //    public static String isFence = null;
    private boolean isOpenAccessibility = true;
    private boolean isOpenGPS = false;
    private ActivityManager mActivityManager = null;
    private boolean firstGetEGps = true;
    private GpsServicePresenter gpsServicePresenter;
    private LocationManager locationManager;
    private boolean firstSavePackageName = true;
    private List<String> packageNameInList = new ArrayList<String>();
    private List<String> packageNameInFiltrationList = new ArrayList<String>();
    private List<String> packageNameOutList = new ArrayList<String>();
    public static boolean isFirstOpenThePhone = true;
    //小米api变量
    private RestrictionsManager mRestrictionsManager;//小米控制蓝牙状态
    private ApplicationManager mApplicationManager;
    private PhoneManager mPhoneManager;

    private Handler gpsServiceHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x789:
                    try {

                        /**粘性eventbus策略修改*/
                        EventBus.getDefault().postSticky(new StrategyChangeEvent("EnterTheFence"));
//                    SavePackageNameDao savePackageName = new SavePackageNameDao(getApplicationContext());
//                    savePackageName.delect();
//                    savePackageName.mDb.close();
                        packageNameInList.clear();
                        packageNameInFiltrationList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x456:
                    try {

                        /**粘性eventbus策略关闭进度条*/
                        EventBus.getDefault().postSticky(new StrategyChangeEvent("LeaveTheFence"));
                        packageNameOutList.clear();
                        saveAndLoadApps();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x123:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();

        try {


            acquireWakeLock();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                saveAndLoadApps();
//                try {
//                    gpsServiceHandler.sendEmptyMessage(0x123);//发送消息
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//        }).start();


//        if(firstSavePackageName){
//            saveAndLoadApps();
//            firstSavePackageName = false;
//        }

            gpsServicePresenter = new GpsServicePresenter(this);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (isFirstOpenThePhone) {
////            if (BootBroadcastReceiver.inBootBroadcastReceiverFlag) {
//            try {
//                PackageManager packageManager2 = getApplicationContext().getPackageManager();
//                LogUtil.i("packageManager2", packageManager2);
//                Intent intent2 = new Intent();
//                intent2 = packageManager2.getLaunchIntentForPackage("com.singun.openvpn.client");
//                getApplicationContext().startActivity(intent2);
//                BootBroadcastReceiver.inBootBroadcastReceiverFlag = false;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            }
////            isFirstOpenThePhone = false;
//        }

//            getTAHGPSRailGetData();//获取电子围栏
//        gpsServicePresenter.getTAHGPSRailGetData(getApplicationContext());
            mRestrictionsManager = RestrictionsManager.getInstance();
            mApplicationManager = ApplicationManager.getInstance();
            mPhoneManager = PhoneManager.getInstance();
            PhoneManager mPhoneManager = PhoneManager.getInstance();

            changeStatus(RestrictionsManager.BLUETOOTH_STATE, RestrictionsManager.ENABLE);

            PackageManager pm = getPackageManager();
            Intent i = new Intent("Android.intent.action.MAIN");
            i.addCategory("android.intent.category.HOME");

            mCache = ACache.get(getApplicationContext());

            List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
            if (lst != null) {
                for (ResolveInfo resolveInfo : lst) {
                    if (isSystemApp(resolveInfo.serviceInfo.applicationInfo)) {

                    }
                    Log.d("Test", "New Launcher Found: " + resolveInfo.activityInfo.packageName);
                }
            }
            mLocationClient = new LocationClient(getApplicationContext());
            mMyLocationListener = new MyLocationListener();
            mLocationClient.registerLocationListener(mMyLocationListener);

            //cellIds=UtilTool.init(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            //设置定位模式
            //Hight_Accuracy:高精度定位模式下，会同时locationListener用网络定位即Wifi和基站定位，返回的是当前条件下精度最好的网络定位结果,
            //Device_Sensors:仅用设备定位模式下，只使用用户的GPS进行定位。这个模式下，由于GPS芯片锁定需要时间，首次定位速度会需要一定的时间,
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

            //返回的定位结果是百度经纬度，gcj02(国测局加密经纬度坐标),bd09ll(百度加密经纬度坐标),bd09(百度加密墨卡托坐标)
            option.setCoorType("bd09ll");
            //设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
            option.setScanSpan(5000);
            option.setOpenGps(true);
            //option.setIsNeedAddress(true);//反地理编码
            mLocationClient.setLocOption(option);
            mLocationClient.requestLocation();
            if (mLocationClient != null && !mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postTAHUserPathSaveAddDataError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postTAHUserPathSaveAddDataSuccess(String msg) {
        String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        Toast.makeText(getApplicationContext(), msg + ",imei:" + imei, Toast.LENGTH_LONG).show();
    }

    //获取围栏点
    @Override
    public void getTAHGPSRailGetDataError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getTAHGPSRailGetDataSuccess(String gpsStr) {
        addPointD(gpsStr);
    }

    @Override
    public void postTAHUserArriveTimeSaveError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postTAHUserArriveTimeSaveSuccess() {

    }

    @Override
    public void setPresenter(GpsServiceContract.Present presenter) {

    }


    protected boolean isSystemApp(ApplicationInfo applicationInfo) {
        return applicationInfo != null
                && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
//            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 实现定位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceiveLocation(final BDLocation location) {

            TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            LogUtil.i(" telMgr.getLine1Number()", telMgr.getLine1Number() + "01");

            try {
//            Toast.makeText(getApplicationContext(),"首次运行使用网络定位，重新运行使用GPS定位室内则延续使用该GPS点8",Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), "已经安装新的包1", Toast.LENGTH_SHORT).show();
                LogUtil.i("isFirstInstallOrRunning", isFirstInstallOrRunning);

                isFirstOpenThePhone();
                gpsServicePresenter.getTAHGPSRailGetData(getApplicationContext());

//            if (!ToolsUtil.isAccessibilitySettingsOn(getApplicationContext())) {
//                EventBus.getDefault().postSticky(new ClearBackTaskEvent("OpenAccessibilityServiceDialog"));
//                LogUtil.i("isOpenAccessibility", isOpenAccessibility);
//                if (isOpenAccessibility) {
//                    Launcher.isShowOpenAccessibilityDialog = true;
//                    isOpenAccessibility = false;
//                }
//            } else {
//                EventBus.getDefault().postSticky(new ClearBackTaskEvent("CloseAccessibilityServiceDialog"));
//                isOpenAccessibility = true;
////                if (Build.VERSION.SDK_INT >= 23) {
////                    LogUtil.i("Build.VERSION.SDK_INTclearBackTask", Build.VERSION.SDK_INT);
////                    if (checkSelfPermission(Manifest.permission.BIND_ACCESSIBILITY_SERVICE) != PackageManager.PERMISSION_GRANTED) {
////                        ActivityCompat.requestPermissions(Launcher.getInstance(), new String[]{Manifest.permission.BIND_ACCESSIBILITY_SERVICE}, REQUEST_CODE_ASK_CALL_PHONE);
////                        LogUtil.i("Build.VERSION.SDK_INT222", Build.VERSION.SDK_INT);
////                        return;
////                    } else {
////                        LogUtil.i("clearBackTask", "clearBackTask1");
////                        Launcher.getInstance().clearBackTask();
////                    }
////                } else {
////                    LogUtil.i("clearBackTask", "clearBackTask2");
////                    Launcher.getInstance().clearBackTask();
////                }
//            }

                if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                    EventBus.getDefault().postSticky(new CheckGPSEvent("OpenGPSDialog"));
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.gpsService_close_gps), Toast.LENGTH_SHORT).show();
                    if (isOpenGPS) {
                        Launcher.isShowOpenGpsDialog = true;
                        isOpenGPS = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.gpsService_open_gps), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new CheckGPSEvent("CloseGPSDialog"));
                    isOpenGPS = true;
                }

                Handler handler = new Handler();
//            handler.post(new ToastRunnable(location.getLongitude()+ ","+location.getLatitude()+location.getLocType()+",61GPS,161网络"));
                Log.i("getLongitude", location.getLongitude() + "");
                Log.i("getLatitude", location.getLatitude() + "");
                LogUtil.i("ptListsptLists", ptLists.size());
//            Toast.makeText(getApplicationContext(),location.getLongitude() + ","+ location.getLatitude()+","+location.getLocType(),Toast.LENGTH_SHORT).show();
                PointD p = new PointD(location.getLongitude(), location.getLatitude());
                LogUtil.i(" IsWithin(p,ptLists,true)", IsWithin(p, ptLists, true));

                Toast.makeText(getApplicationContext(), location.getLongitude() + ","
                                + location.getLatitude() + ","
                                + location.getLocType() + ",61GPS,161网络" + "," +
                                IsWithin(p, ptLists, true) + "," + locklauncher
                        , Toast.LENGTH_SHORT).show();

                /**GPS定位*/
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    LogUtil.i("TypeGpsLocation", BDLocation.TypeGpsLocation);
                    if (IsWithin(p, ptLists, true) && locklauncher != 1) {
                        try {
                            setPreferredLauncher(true);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() + "TypeGpsLocation  setPreferredLauncher(true)", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!IsWithin(p, ptLists, true) && locklauncher != 2) {
                        try {
                            setPreferredLauncher(false);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() + "TypeGpsLocation  setPreferredLauncher(false)", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                /**GPS定位*/
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                if (IsWithin(p, ptLists, true) && locklauncher != 1) {//当前不在围栏里，定位结果在围栏内
//                    try {
//                        setPreferredLauncher(true);
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), e.getMessage() + "TypeGpsLocation  setPreferredLauncher(true)", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (!IsWithin(p, ptLists, true) && locklauncher != 2) {//当前不在围栏外，定位结果在围栏外
//                    try {
//                        boolean bsort = false;
//                        for (int i = 0; i < ptLists.size(); i++) {//
//
//                            double x1, x2, y1, y2;
//                            x1 = ptLists.get(i).x;
//                            y1 = ptLists.get(i).y;
//                            if (i == ptLists.size() - 1) {
//                                x2 = ptLists.get(0).x;
//                                y2 = ptLists.get(0).y;
//                            } else {
//                                x2 = ptLists.get(i + 1).x;
//                                y2 = ptLists.get(i + 1).y;
//                            }
//                            //网络定位误差值
//                            LogUtil.i("pointToLine", pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()));
////                            Toast.makeText(getApplicationContext(), pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) + "", Toast.LENGTH_SHORT).show();
//                            if (pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) > 30) {
//                                bsort = true;
//                                break;
//                            }
//                        }
//                        LogUtil.i("bsortbsort", bsort);
//                        if (bsort == true) {
//
//                            setPreferredLauncher(false);
//
//                        }
//
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), e.getMessage() + "TypeGpsLocation  setPreferredLauncher(false)", Toast.LENGTH_SHORT).show();
//                    }
//                }else if((!IsWithin(p, ptLists, true) && locklauncher != 1))//当前不在围栏内，定位结果也不在围栏内
//                {
//                    try {
//                        boolean bsort = false;
//                        for (int i = 0; i < ptLists.size(); i++) {//
//
//                            double x1, x2, y1, y2;
//                            x1 = ptLists.get(i).x;
//                            y1 = ptLists.get(i).y;
//                            if (i == ptLists.size() - 1) {
//                                x2 = ptLists.get(0).x;
//                                y2 = ptLists.get(0).y;
//                            } else {
//                                x2 = ptLists.get(i + 1).x;
//                                y2 = ptLists.get(i + 1).y;
//                            }
//                            //网络定位误差值
//                            LogUtil.i("pointToLine", pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()));
////                            Toast.makeText(getApplicationContext(), pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) + "", Toast.LENGTH_SHORT).show();
//                            if (pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) < 30) {
//                                bsort = true;
//                                break;
//                            }
//                        }
//                        LogUtil.i("bsortbsort", bsort);
//                        if (bsort == true) {
//
//                            setPreferredLauncher(true);
//
//                        }
//
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), e.getMessage() + "TypeGpsLocation  setPreferredLauncher(false)", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }

                if (isFirstInstallOrRunning) {
                    /**NETWORK定位*/
                    if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        LogUtil.i("IsWithin(p,ptLists,true)", IsWithin(p, ptLists, true));
                        LogUtil.i("locklauncherisFirstInstallOrRunning", locklauncher);
//                    handler.post(new ToastRunnable(location.getLongitude() + "," + location.getLatitude() + "  NETWORK" + "," + locklauncher + IsWithin(p, ptLists, true)));
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.gpsService_miscalculation_fence), Toast.LENGTH_SHORT).show();
                        if (IsWithin(p, ptLists, true) && locklauncher != 1) {
                            try {
                                setPreferredLauncher(true);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage() + "TypeNetWorkLocation  setPreferredLauncher(true) 1", Toast.LENGTH_SHORT).show();
                            }
                        } else if (!IsWithin(p, ptLists, true) && locklauncher != 2 && locklauncher != 1) {
                            boolean bsort = false;
                            for (int i = 0; i < ptLists.size(); i++) {//

                                double x1, x2, y1, y2;
                                x1 = ptLists.get(i).x;
                                y1 = ptLists.get(i).y;
                                if (i == ptLists.size() - 1) {
                                    x2 = ptLists.get(0).x;
                                    y2 = ptLists.get(0).y;
                                } else {
                                    x2 = ptLists.get(i + 1).x;
                                    y2 = ptLists.get(i + 1).y;
                                }
                                //网络定位误差值
                                LogUtil.i("pointToLine", pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()));
//                            Toast.makeText(getApplicationContext(), pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) + "", Toast.LENGTH_SHORT).show();
                                if (pointToLine(y1, x1, y2, x2, location.getLatitude(), location.getLongitude()) > 1100) {
                                    bsort = true;
                                    break;
                                }
                            }
                            LogUtil.i("bsortbsort", bsort);
                            if (bsort == true) {
                                try {
                                    setPreferredLauncher(false);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage() + "TypeNetWorkLocation  setPreferredLauncher(false)", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    setPreferredLauncher(true);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage() + "TypeNetWorkLocation  setPreferredLauncher(true)", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    isFirstInstallOrRunning = false;
                }

//            if (ToolsUtil.isAccessibilitySettingsOn(getApplicationContext())) {
//                //在围栏内外判断天翼对讲是否在运行，不运行则重新打开
//                AppRunning();
//                LogUtil.i("AppRunning()", "AppRunning()");
//            }
//            if (isFence.equals("INFence")) {
//                if (BluetoothManager.isBluetoothEnabled()) {
//                    BluetoothManager.turnOffBluetooth();
//                }
//            }

                AppRunning();
                changeStatus(RestrictionsManager.GPS_STATE, RestrictionsManager.FORCE_OPEN);
                if (Launcher.getInstance().mIn == true) {
                    if (BluetoothManager.isBluetoothEnabled()) {
                        BluetoothManager.turnOffBluetooth();
                    }
                    changeStatus(RestrictionsManager.BLUETOOTH_STATE, RestrictionsManager.CLOSE);
//                changeStatus(RestrictionsManager.WIFI_STATE, RestrictionsManager.CLOSE);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String eGps = location.getLongitude() + "," + location.getLatitude();
                        LogUtil.i("getAsString(eGps)1", eGps);
                        gpsServicePresenter.postTAHUserPathSaveAddData(mCache.getAsString("userId"), eGps, getApplicationContext());
                    }

                }, 10000);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SystemClock.sleep(10000);
//                    String eGps = location.getLongitude()+","+location.getLatitude();
//                    postTAHUserPathSaveModData(eGps);
//                }
//            }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void onConnectHotSpotMessage(String s, int i) {
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPreferredLauncher(boolean b) {
        try {


            if (b == true) {
//            isFence = "INFence";
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.gpsService_in_fence), Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("刷新成功", "刷新成功");
                        // TODO Auto-generated method stub
                        // 在此执行耗时工作，执行完毕后调用handler发送消息
                        try {
                            gpsServicePresenter.postTAHUserArriveTimeSave("1", mCache.getAsString("userId"), getApplicationContext());
                            setStrategy();
                        } catch (Exception e) {
                            LogUtil.i("eeeeee", e.getMessage());
                        }
                        try {
                            gpsServiceHandler.sendEmptyMessage(0x789);//发送消息
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();

            } else {
//            isFence = "OUTFence";
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.gpsService_out_fence), Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("刷新成功", "刷新成功");
                        // TODO Auto-generated method stub
                        // 在此执行耗时工作，执行完毕后调用handler发送消息
                        try {
                            gpsServicePresenter.postTAHUserArriveTimeSave("0", mCache.getAsString("userId"), getApplicationContext());
                            removeStrategy();
                        } catch (Exception e) {
                            LogUtil.i("eeeeee", e.getMessage());
                        }
                        try {
                            gpsServiceHandler.sendEmptyMessage(0x456);//发送消息
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loadPersistentPreferredLauncher(Service service) {
        return service.getSharedPreferences(PREFS_DEVICE_OWNER, Context.MODE_PRIVATE)
                .getString(PREF_LAUNCHER, null);
    }

    private void savePersistentPreferredLauncher(Service service, String packageName) {
        SharedPreferences.Editor editor = service.getSharedPreferences(PREFS_DEVICE_OWNER,
                Context.MODE_PRIVATE).edit();
        if (packageName == null) {
            editor.remove(PREF_LAUNCHER);
        } else {
            editor.putString(PREF_LAUNCHER, packageName);
        }
        editor.apply();
    }

    public class PointD implements Parcelable {
        public double x;
        public double y;

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(x);
            out.writeDouble(y);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public PointD() {
        }

        public PointD(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public PointD(Point p) {
            this.x = p.x;
            this.y = p.y;
        }
    }

    public static boolean IsWithin(PointD pt, List<PointD> polygon, boolean noneZeroMode) {
        int ptNum = polygon.size();
        if (ptNum < 3) {
            return false;
        }
        int j = ptNum - 1;
        boolean oddNodes = false;
        int zeroState = 0;
        for (int k = 0; k < ptNum; k++) {
            PointD ptK = polygon.get(k);
            PointD ptJ = polygon.get(j);
            if (((ptK.y > pt.y) != (ptJ.y > pt.y)) && (pt.x < (ptJ.x - ptK.x) * (pt.y - ptK.y) / (ptJ.y - ptK.y) + ptK.x)) {
                oddNodes = !oddNodes;
                if (ptK.y > ptJ.y) {
                    zeroState++;
                } else {
                    zeroState--;
                }
            }
            j = k;
        }

        return noneZeroMode ? zeroState != 0 : oddNodes;
    }


    public boolean PtInPolygon(PointD point, List<PointD> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            PointD p1 = APoints.get(i);
            PointD p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.y == p2.y) // p1p2 与 y=p0.y平行
                continue;
            if (point.y < Math.min(p1.y, p2.y)) // 交点在p1p2延长线上
                continue;
            if (point.y >= Math.max(p1.y, p2.y)) // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标
            // --------------------------------------------------------------
            double x = (double) (point.y - p1.y)
                    * (double) (p2.x - p1.x)
                    / (double) (p2.y - p1.y) + p1.x;
            if (x > point.x)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }

    @Override
    public void onDestroy() {
        threadDisable = true;
        super.onDestroy();
        ptLists.clear();
        startService(new Intent(this, GpsService.class));
        ApiTAHGPSRailGet.cancelApiTAHGPSRailGetTag("TAHGPSRailGet");
        ApiTAHUserPathSave.cancelApiTAHUserPathSaveAddTag("TAHUserPathSaveAdd");
    }

    private double pointToLine(double x1, double y1, double x2, double y2, double x0,
                               double y0) {
        double space = 0;
        double a, b, c;
        a = getDistance(x1, y1, x2, y2);// 线段的长度
        b = getDistance(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = getDistance(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0 || b <= 0) {
            space = 0;
            return space;
        }
        if (a <= 0) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    // 计算两点之间的距离
//    private double lineSpace(int x1, int y1, int x2, int y2) {
//        double lineLength = 0;
//        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
//                * (y1 - y2));
//        return lineLength;
//    }
    private static double EARTH_RADIUS = 6378.137;

    //    private static double EARTH_RADIUS = 200;
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * 把获取坐标字符串转换成集合
     */
    private void addPointD(String gpsStr) {
        LogUtil.i("aaa", "aaa");
        ptLists = new ArrayList<PointD>();
        String[] stringArr = gpsStr.split("\\|");  //注意分隔符是需要转译...
//        Toast.makeText(getApplicationContext(),gpsStr,Toast.LENGTH_LONG).show();
        for (int i = 0; i < stringArr.length; i++) {
            String[] stringArrLatLng = stringArr[i].split(",");
            LogUtil.i("stringArrLatLng[1]", stringArrLatLng[1]);//经度
            LogUtil.i("stringArrLatLng[0]", stringArrLatLng[0]);//纬度
            PointD llDuobianxing = new PointD(Double.parseDouble(stringArrLatLng[1]), Double.parseDouble(stringArrLatLng[0]));
            ptLists.add(llDuobianxing);
        }
        LogUtil.i("ptLists", ptLists.size());
    }

    private void setwifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        boolean isEnabled = wifiManager.setWifiEnabled(false);
        // wifiManager.
    }

    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void AppRunning() {
        String packName = getRunningServiceInfo();
        LogUtil.i("packName", getRunningServiceInfo());
        if (TextUtils.isEmpty(packName) || "null".equals(packName)) {
            isAppRunning = false;
            LogUtil.i("isAppRunningfalse", isAppRunning);
        } else {
            LogUtil.i("packNameelse", getRunningServiceInfo());
            if ("cn.com.ctsi.android.qchat".equals(packName)) {
                isAppRunning = true;
            } else {
                isAppRunning = false;
            }
        }
        LogUtil.i("isAppRunning", isAppRunning);
        if (!isAppRunning) {
            try {
                PackageManager packageManagerTY = getPackageManager();
                Intent intentTY = new Intent();
                intentTY = packageManagerTY.getLaunchIntentForPackage("cn.com.ctsi.android.qchat");
                startActivity(intentTY);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i("e.getMessage", e.getMessage());
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        BootService.startForeground(this);
        startService(new Intent(this, BootService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 设置策略
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setStrategy() {

//        TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
//        LogUtil.i(" telMgr.getLine1Number()", telMgr.getLine1Number() + "01");
//
////        PhoneManager.getInstance().controlPhoneCall(FLAG_DISALLOW_OUT, 0,0);
////        PhoneManager.getInstance().controlPhoneCall(FLAG_DISALLOW_OUT, 1,0);
//
//        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        ComponentName mComponentName = MyPolicyReceiver.getComponentName(getApplicationContext());
//        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        dpm.setSecureSetting(mComponentName, SKIP_FIRST_USE_HINTS, "1");
//        if (!dpm.getCameraDisabled(mComponentName)) {
//            dpm.setCameraDisabled(mComponentName, true);//禁用摄像头
//        }
//
//        dpm.setSecureSetting(mComponentName, "skip_first_use_hints", "1");
////        setnetwork();
////        setwifi();
//        dpm.setGlobalSetting(mComponentName, Settings.Global.WIFI_ON, "1");
//        //   dpm.setGlobalSetting(mComponentName, Settings.Global.WIFI_DEVICE_OWNER_CONFIGS_LOCKDOWN,"1");
//        dpm.addUserRestriction(mComponentName, "no_sms");
//        dpm.clearUserRestriction(mComponentName, "no_outgoing_calls");
//        dpm.addUserRestriction(mComponentName, "no_config_wifi");
//        dpm.addUserRestriction(mComponentName, "no_config_mobile_networks");
//        dpm.addUserRestriction(mComponentName, "no_usb_file_transfer");
//        dpm.setGlobalSetting(mComponentName, Settings.Global.BLUETOOTH_ON, "0");
//        dpm.addUserRestriction(mComponentName, "no_physical_media");
//        if (Build.VERSION.SDK_INT >= 24 && Build.VERSION.SDK_INT < 26) {
//            dpm.addUserRestriction(mComponentName, "no_config_bluetooth");
//            dpm.addUserRestriction(mComponentName, "no_bluetooth");
//            dpm.addUserRestriction(mComponentName, "no_bluetooth_sharing");
//        } else if (Build.VERSION.SDK_INT == 26) {
//            dpm.addUserRestriction(mComponentName, "no_bluetooth");
//            dpm.addUserRestriction(mComponentName, "no_bluetooth_sharing");
//        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            dpm.setStatusBarDisabled(mComponentName, true);
//        }
//        dpm.addPersistentPreferredActivity(
//                mComponentName, filter, new ComponentName(getApplicationContext().getApplicationContext(), Launcher.class));
//        savePersistentPreferredLauncher(GpsService.this, getApplicationContext().getPackageName());
////        changeStatus(RestrictionsManager.BLUETOOTH_STATE, RestrictionsManager.DISABLE);
//        locklauncher = 1;
//
//        SavePackageNameDao savePackageName = new SavePackageNameDao(getApplicationContext());
//        packageNameInList = new ArrayList<>();
//        packageNameInList.clear();
//        packageNameInList = savePackageName.query();
//        savePackageName.mDb.close();
//        packageNameInFiltrationList = new ArrayList<>();
//        packageNameInFiltrationList.clear();
//        for (String packageName : packageNameInList) {
//            LogUtil.i("setStrategysaveAndLoadApps", packageName + "0");
//            if (!"com.android.settings".equals(packageName) &&
//                    !"cn.com.ctsi.android.qchat".equals(packageName) &&
//                    !"com.android.contacts".equals(packageName) &&
//                    !"com.android.providers.contacts".equals(packageName) &&
//                    !"com.android.phone".equals(packageName) &&
//                    !"com.singun.openvpn.client".equals(packageName)&&
//                    !"com.android.incallui".equals(packageName)&&
//                    !"com.lbe.security.miui".equals(packageName)
////                    !packageName.contains("com.miui")
////                    !packageName.contains("com.xiaomi") &&
////                    !packageName.contains("com.miui") &&
////                    !packageName.contains("com.mi.misupport")&&
////                    !packageName.contains("com.google")&&
////                    !packageName.contains("com.milink") &&
////                    !packageName.contains("com.qualcomm")
//                    ) {
////                if(!packageName.contains("com.android")){
//                    LogUtil.i("setStrategysaveAndLoadApps2", packageName);
//                    packageNameInFiltrationList.add(packageName);
////                }
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 24) {
//            for (String packageNameL : packageNameInFiltrationList) {
//                LogUtil.i("packageNameInFList", packageNameL + "0");
//                dpm.setApplicationHidden(mComponentName, packageNameL, true);
//            }
//        } else if (Build.VERSION.SDK_INT >= 24) {
////                String[] aArray = {"com.tencent.mm", "com.tencent.mobileqq", "com.tencent.mqq"};
//            String[] aArray = packageNameInFiltrationList.toArray(new String[packageNameInFiltrationList.size()]);// list转成字符数组
//            dpm.setPackagesSuspended(mComponentName, aArray, true);
//        }
//
//        for (String packageNameL : packageNameInFiltrationList) {
//            mApplicationManager.setApplicationSettings(packageNameL, FLAG_LOCK, UserUtils.myUserId());
//        }

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mComponentName = MyPolicyReceiver.getComponentName(getApplicationContext());
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        dpm.setSecureSetting(mComponentName, SKIP_FIRST_USE_HINTS, "1");
        if (!dpm.getCameraDisabled(mComponentName)) {
            dpm.setCameraDisabled(mComponentName, true);//禁用摄像头
        }
        if (Build.VERSION.SDK_INT >= 23) {
            dpm.setStatusBarDisabled(mComponentName, true);
        }
        dpm.addUserRestriction(mComponentName, "no_usb_file_transfer");
        dpm.addPersistentPreferredActivity(
                mComponentName, filter, new ComponentName(getApplicationContext().getApplicationContext(), Launcher.class));
        locklauncher = 1;

        /**小米sdk的api*/
        /**暂时开启**/
        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DEFAULT, 0, UserUtils.myUserId());
        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DEFAULT, 1, UserUtils.myUserId());

//        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DISALLOW_IN | PhoneManager.FLAG_DISALLOW_OUT, 0, UserUtils.myUserId());
//        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DISALLOW_IN | PhoneManager.FLAG_DISALLOW_OUT, 1, UserUtils.myUserId());
//        mPhoneManager.controlSMS(PhoneManager.FLAG_DISALLOW_IN | PhoneManager.FLAG_DISALLOW_OUT, 0, UserUtils.myUserId());
//        mPhoneManager.controlSMS(PhoneManager.FLAG_DISALLOW_IN | PhoneManager.FLAG_DISALLOW_OUT, 1, UserUtils.myUserId());
//        DeviceManager deviceManager=DeviceManager.getInstance();
//        List<String> numberList = new ArrayList<>();
//        numberList.add("18978501605");
//        numberList.add("18178521010");
//        numberList.add("15277093429");
//        deviceManager.setUrlWhiteList(numberList,UserUtils.myUserId());

        /**小米SDK设置在策略内设置USB失效**/
//        toggleRestriction(RestrictionsManager.DISALLOW_MTP, false);
//        toggleRestriction(RestrictionsManager.DISALLOW_OTG, false);
//        toggleRestriction(RestrictionsManager.DISALLOW_USBDEBUG, false);
//        toggleRestriction(RestrictionsManager.DISALLOW_CAMERA, false);

    }

    /**
     * 解除策略
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void removeStrategy() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mComponentName = MyPolicyReceiver.getComponentName(getApplicationContext());
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        dpm.setSecureSetting(mComponentName, SKIP_FIRST_USE_HINTS, "1");
        dpm.clearUserRestriction(mComponentName, "no_sms");
        dpm.clearUserRestriction(mComponentName, "no_outgoing_calls");
        dpm.clearUserRestriction(mComponentName, "no_config_wifi");
        dpm.clearUserRestriction(mComponentName, "no_config_mobile_networks");
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            dpm.clearUserRestriction(mComponentName, "no_config_bluetooth");
            dpm.clearUserRestriction(mComponentName, "no_bluetooth");
        } else if (Build.VERSION.SDK_INT == 26) {
            dpm.clearUserRestriction(mComponentName, "no_bluetooth");
            dpm.clearUserRestriction(mComponentName, "no_bluetooth_sharing");
        }
        dpm.clearUserRestriction(mComponentName, "no_physical_media");
        dpm.clearUserRestriction(mComponentName, "no_usb_file_transfer");
        dpm.setCameraDisabled(mComponentName, false);//解除摄像头
        if (Build.VERSION.SDK_INT >= 23) {
            dpm.setStatusBarDisabled(mComponentName, false);
        }

        dpm.addPersistentPreferredActivity(
                mComponentName, filter, new ComponentName(getApplicationContext().getApplicationContext(), Launcher.class));
        savePersistentPreferredLauncher(GpsService.this, null);
        locklauncher = 2;

        /**小米sdk的api*/
        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DEFAULT, 0, UserUtils.myUserId());
        mPhoneManager.controlPhoneCall(PhoneManager.FLAG_DEFAULT, 1, UserUtils.myUserId());

        /**小米SDK设置在策略内设置USB开启**/
//        toggleRestriction(RestrictionsManager.DISALLOW_MTP, true);
//        toggleRestriction(RestrictionsManager.DISALLOW_OTG, true);
//        toggleRestriction(RestrictionsManager.DISALLOW_USBDEBUG, true);
//        toggleRestriction(RestrictionsManager.DISALLOW_CAMERA, true);

    }

    // 获得系统正在运行的进程信息
    private String getRunningServiceInfo() {
        // 获得ActivityManager服务的对象
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // 设置一个默认Service的数量大小
        int defaultNum = 100;
        // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager
                .getRunningServices(defaultNum);
        // ServiceInfo Model类 用来保存所有进程信息
        String pkgName = null;
        for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {
            // 获得该Service的组件信息 可能是pkgname/servicename
            ComponentName serviceCMP = runServiceInfo.service;
            if ("cn.com.ctsi.android.qchat".equals(serviceCMP.getPackageName())) {
                pkgName = serviceCMP.getPackageName(); // 包名
            }
        }
        return pkgName;
    }


    /**
     * 获取并存储手机中所有的App
     */
    public void saveAndLoadApps() {
        PackageManager pckMan = getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(0);//所有包名集合

        //隐藏桌面应用图标
        String packageName = null;
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo info = (PackageInfo) packs.get(i);
            packageName = info.applicationInfo.packageName;
            if (info.applicationInfo.uid > 10000) {
                if (!"com.android.settings".equals(packageName) &&
                        !"cn.com.ctsi.android.qchat".equals(packageName) &&
                        !"com.android.contacts".equals(packageName) &&
                        !"com.android.providers.contacts".equals(packageName) &&
                        !"com.android.phone".equals(packageName) &&
                        !"com.singun.openvpn.client".equals(packageName) &&
                        !"com.android.incallui".equals(packageName) &&
                        !"com.lbe.security.miui".equals(packageName)
//                        !packageName.contains("com.miui")

//                        !packageName.contains("com.xiaomi") &&
//                        !packageName.contains("com.mi.misupport") &&
//                        !packageName.contains("com.google") &&
//                        !packageName.contains("com.milink") &&
//                        !packageName.contains("com.qualcomm")
                        ) {
//                    if(!packageName.contains("com.android")){
                    LogUtil.i("saveAndLoadApps", packageName);
                    SavePackageNameDao visitCityDao = new SavePackageNameDao(Launcher.getInstance());
                    visitCityDao.add(packageName);
                    visitCityDao.mDb.close();
//                    }
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void isFirstOpenThePhone() {

        if (isFirstOpenThePhone) {

            LogUtil.i("inBootBroadcastReceiverFlag", BootBroadcastReceiver.inBootBroadcastReceiverFlag);
            if (Build.VERSION.SDK_INT >= 23) {
                LogUtil.i("inBootBroadcastReceiverFlag", "Build.VERSION.SDK_INT");
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PHONE);
                    LogUtil.i("inBootBroadcastReceiverFlag", "Build.VERSION.SDK_INT2");
                    return;
                } else {
                    LogUtil.i("inBootBroadcastReceiverFlag", "endCall1");
                    endCall();
                }
            } else {
                LogUtil.i("inBootBroadcastReceiverFlag", "endCall2");
                endCall();
            }
        }
    }

    /**
     * 挂断电话
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void endCall() {
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            Log.e(TAG, "End call.");
            iTelephony.endCall();
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }


    /**
     * 改变GPS.WIFI.蓝牙.NCF等的状态
     *
     * @param key
     */
    private void changeStatus(String key, int state) {
        int myUserId = UserUtils.myUserId();
        mRestrictionsManager.setControlStatus(key, state, myUserId);
    }

    private void toggleRestriction(String key, boolean flag) {
        int myUserId = UserUtils.getSecuritySpaceId(this);
        mRestrictionsManager.setRestriction(key, flag, myUserId);
//        boolean hasRestriction = mRestrictionsManager.hasRestriction(key, myUserId);
//        mRestrictionsManager.setRestriction(key, !hasRestriction, myUserId);
//        Toast.makeText(this, msg + ": " + !hasRestriction, Toast.LENGTH_SHORT).show();
    }
}
