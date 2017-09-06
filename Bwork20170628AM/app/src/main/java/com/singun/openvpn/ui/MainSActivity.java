package com.singun.openvpn.ui;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.UserManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.content.DialogInterface;
import android.widget.BaseAdapter;

import android.net.wifi.WifiManager;
import android.net.VpnService;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;
        import java.util.Vector;
        import android.graphics.Bitmap;
        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.ResolveInfo;
        import android.graphics.BitmapFactory;
        import android.graphics.Point;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.view.GestureDetector;
        import android.view.GestureDetector.OnGestureListener;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnTouchListener;
        import android.view.ViewConfiguration;
        import android.view.ViewGroup;
        import android.view.ViewTreeObserver.OnGlobalLayoutListener;
        import android.view.ViewTreeObserver.OnScrollChangedListener;
        import android.view.animation.Animation;
        import android.view.animation.Animation.AnimationListener;
        import android.view.animation.AnimationUtils;
        import android.view.animation.Transformation;
        import android.widget.FrameLayout;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.singun.openvpn.client.MainApplication;
        import com.singun.openvpn.client.R;
import com.singun.openvpn.data.model.VpnConfigFile;
import com.singun.openvpn.logic.VpnUiLogic;
import com.singun.openvpn.receiver.MyPolicyReceiver;
import com.singun.openvpn.widget.ApplicationInfo;
        import com.singun.openvpn.widget.CancellableQueueTimer;
        import com.singun.openvpn.widget.Controller;
        import com.singun.openvpn.widget.DotView;
        import com.singun.openvpn.widget.FolderContentView;
        import com.singun.openvpn.widget.FolderInfo;
        import com.singun.openvpn.widget.FolderScrollView;
        import com.singun.openvpn.widget.FolderView;
        import com.singun.openvpn.widget.HitTestResult3;
        import com.singun.openvpn.widget.IPageView;
        import com.singun.openvpn.widget.IconMover;
        import com.singun.openvpn.widget.IconMover.OnMovingStopped;
        import com.singun.openvpn.widget.ItemInfo;
        import com.singun.openvpn.widget.JiggleModeActivator;
        import com.singun.openvpn.widget.LayoutCalculator;
        import com.singun.openvpn.widget.ObjectPool;
        import com.singun.openvpn.widget.PageScrollView;
        import com.singun.openvpn.widget.SpringBoardPage;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.PowerManager;
        import com.singun.openvpn.widget.ScreenListener;
        import android.os.PowerManager.WakeLock;
import java.lang.reflect.Constructor;
import android.net.wifi.WifiManager;
import java.io.IOException;
import java.io.OutputStream;
import com.singun.openvpn.receiver.NetworkConnectChangedReceiver;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.net.ConnectivityManager;
import android.util.Log;
import android.app.AlertDialog;
import android.app.ActivityManager.MemoryInfo;
import de.blinkt.openvpn.core.VpnStatus;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.content.res.Resources;
import android.os.Build;
import static android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE;
import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME;


public class MainSActivity extends Activity  implements SensorEventListener, VpnUiLogic.UiLogicListener{
    private final static String RECEIVER_ADD_APP = "RECEIVER_ADD_APP";
    private boolean baojing;
    private LinearLayout scrollView;
    private PageScrollView scrollContainer;
    private int screenWidth;
    private int screenHeight;
    private FrameLayout mFrame;
    private GestureDetector gd;
    private List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
    private LayoutCalculator lc;
    private Vector<ApplicationInfo[]> pages;
    private ObjectPool pp;
    private Handler handler;
    private float touchSlop;
    private  WifiManager mWifiManager ;
    private int selectedPageIndex = 1;
    private HitTestResult3 hitTest2 = new HitTestResult3();
    private HitTestResult3 hitTest3 = new HitTestResult3();
    private IconMover mover;
    private DotView dotView;
    private RelativeLayout mContainer;
    private FolderView mFolderView;
    private int openFolderIndex = -1;
    private RelativeLayout mTouchController;
    private boolean needUpload = false;
    private List<ResolveInfo> apps;
    private ResolveInfo resolveInfo;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private DevicePolicyManager dpm;
    private ComponentName mComponentName;
    private ScreenListener screenListener ;
    private Context mContext;
    private static Process process;
    private List<RunningAppProcessInfo> runningProcesses;
    private String packName;
    private PackageManager pManager;
    private ActivityManager manager;
    GridView appsGrid;
    private List<ResolveInfo> appss = new ArrayList<ResolveInfo>();
    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(this, "Provisioning done.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Provisioning failed.", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


//    private void provisionManagedProfile() {
//        Activity activity =  this;
//        if (null == activity) {
//            return;
//        }
//        Intent intent = new Intent(ACTION_PROVISION_MANAGED_PROFILE);
//        intent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
//                activity.getApplicationContext().getPackageName());
//        if (intent.resolveActivity(activity.getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
//            //activity.finish();
//        } else {
//            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
    protected void onCreate(Bundle savedInstanceState) {

//        if (savedInstanceState == null) {
//            DevicePolicyManager manager = (DevicePolicyManager)
//                    getSystemService(Context.DEVICE_POLICY_SERVICE);
//            if (manager.isProfileOwnerApp(getApplicationContext().getPackageName()) || manager.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
//                // If the managed profile is already set up, we show the main screen.
//                //showMainFragment();
//            } else {
//                if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
//                    provisionManagedProfile();
//                }else if(Build.VERSION.SDK_INT >= 23){
//
//                }
//                // If not, we show the set up screen.
//
//            }
//        }
        super.onCreate(savedInstanceState);
        startLockTask();
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = MyPolicyReceiver.getComponentName(this);
//        Intent intent = new Intent(MainSActivity.this, EgSpeechActivity.class);
        //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        dpm.setProfileEnabled(mComponentName);
        if (dpm.isDeviceOwnerApp(getApplicationContext().getPackageName())){
            removeFactoryReset();
            //setFactoryReset();
            setNetwork();
//        removeWifi();
//        removeMobileNetwork();
            UninstallBlocked();
            getBooleanGlobalSetting();
            loadApps();
        }

        View view = View.inflate(MainSActivity.this, R.layout.main_activity, null);
        setContentView(view);
        //setContentView(R.layout.main_activity);
        appsGrid = (GridView) findViewById(R.id.apps_list);



        mFrame = (FrameLayout) findViewById(R.id.frame);
        scrollView = (LinearLayout) findViewById(R.id.container);
        scrollContainer = (PageScrollView) findViewById(R.id.pageView);
        dotView = (DotView) findViewById(R.id.dotView);
        mContainer = (RelativeLayout) findViewById(R.id.springboard_container);
        mTouchController = (RelativeLayout) findViewById(R.id.touchController);

        gd = new GestureDetector(MainSActivity.this, this.gestureListener);
        scrollContainer.setOnScrollChangedListener(scrollContainer_OnScrollChanged);
        mTouchController.setOnTouchListener(scrollContainer_OnTouch);
        lc = new LayoutCalculator(MainSActivity.this);
        pp = new ObjectPool(MainSActivity.this, lc);
        handler = new Handler();
        touchSlop = ViewConfiguration.get(MainSActivity.this).getScaledTouchSlop();
        mover = new IconMover(view, lc, pp, handler);
        dotView.init(lc, pp);
        OnLayoutReady onLayoutReady = new OnLayoutReady();
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutReady);
        IntentFilter filter = new IntentFilter(RECEIVER_ADD_APP);
        filter.addAction(ApplicationInfo.LOAD_ICON);
//        registerReceiver(receiver, filter);
        mManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //获取系统服务POWER_SERVICE，返回一个PowerManager对象
        localPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower");//第一个参数为电源锁级别，第二个是日志tag
//        initView();

        try{
            initVpn();
            mVpnUiLogic.connectOrDisconnect();
        }   catch (Exception e) {
            setNetwork();
        }
        //注册BORDCASTRECEIVER 监听网络改变
//        IntentFilter filter1 = new IntentFilter();
//        filter1.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter1.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter1.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(new NetworkConnectChangedReceiver(), filter1);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyPolicyReceiver.class);
        screenListener = new ScreenListener( MainSActivity.this ) ;

        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Toast.makeText( MainSActivity.this , "屏幕打开了" , Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onScreenOff() {

                pManager = MainSActivity.this .getPackageManager();
                manager = (ActivityManager) MainSActivity.this.getSystemService(Context.ACTIVITY_SERVICE);

                killOthers(MainSActivity.this);
            }

            @Override
            public void onUserPresent() {
                Toast.makeText( MainSActivity.this , "解锁了" , Toast.LENGTH_SHORT ).show();
            }
        });
        this.registerReceiver(this.myBatteryReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));



    };




    private OnItemClickListener clickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i!=3)
            {
                ResolveInfo info = appss.get(i);
                //该应用的包名
                String pkg = info.activityInfo.packageName;
                //应用的主activity类
                String cls = info.activityInfo.name;
                ComponentName componet = new ComponentName(pkg, cls);

                Intent intent = new Intent();
                intent.setComponent(componet);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }else{

                Intent intent = new Intent(MainSActivity.this, EgSpeechActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    };
    private BroadcastReceiver myBatteryReceiver =
            new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    int bLevel = arg1.getIntExtra("level", 0);// the battery level in integer
                    if(bLevel <= 20 & baojing == true )
                    {
                        baojing = false;
                        new AlertDialog.Builder(MainSActivity.this).setTitle("系统提示")//设置对话框标题

                                .setMessage("电量不足！")//设置显示的内容

                                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮



                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                        // TODO Auto-generated method stub

                                        //finish();

                                    }

                                }).show();//在按键响应事件中显示此对话框
                    }else
                    {
                        baojing = true;
                    }
                    Log.i("Level", ""+bLevel);
                }
            };

    private void killOthers(Context context) {

        runningProcesses = manager.getRunningAppProcesses();

        for (RunningAppProcessInfo runningProcess : runningProcesses) {
            try {
                packName = runningProcess.processName;
                android.content.pm.ApplicationInfo applicationInfo = pManager.getPackageInfo(packName, 0).applicationInfo;

                if (!"com.asms".equals(packName)&&filterApp(applicationInfo)) {
                    forceStopPackage(packName,context);
                    System.out.println(packName+"JJJJJJ");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *强制停止应用程序
     * @param pkgName
     */
    private void forceStopPackage(String pkgName,Context context) throws Exception{
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(pkgName);


    }

    /**
     * 判断某个应用程序是 不是三方的应用程序
     * @param info
     * @return
     */
    public boolean filterApp(android.content.pm.ApplicationInfo  info) {
        if ((info.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

    private VpnUiLogic mVpnUiLogic;
    private void initVpn() {
        mVpnUiLogic = new VpnUiLogic();
        mVpnUiLogic.init(this, this);
    }
    /**
     * 初始化进程
     */
    private static void initProcess() {
        if (process == null)
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    /**
     * 结束进程
     */
    private static void killProcess(String packageName) {
        OutputStream out = process.getOutputStream();
        String cmd = "am force-stop " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭输出流
     */
    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void killAll(){

        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统中所有正在运行的进程
        List<RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        int count=0;//被杀进程计数
        String nameList="";//记录被杀死进程的包名
        long beforeMem = getAvailMemory(MainSActivity.this);//清理前的可用内存
        Log.i(TAG, "清理前可用内存为 : " + beforeMem);

        for (RunningAppProcessInfo appProcessInfo:appProcessInfos) {
            nameList="";
            if( appProcessInfo.processName.contains("com.android.system")
                    ||appProcessInfo.pid==android.os.Process.myPid())//跳过系统 及当前进程
                continue;
            String[] pkNameList=appProcessInfo.pkgList;//进程下的所有包名
            for(int i=0;i<pkNameList.length;i++){
                String pkName=pkNameList[i];
                activityManager.killBackgroundProcesses(pkName);//杀死该进程
//                Method forceStopPackage = null;
//                try{
//                    forceStopPackage= activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
//                } catch (java.lang.NoSuchMethodException e) {
//                    VpnStatus.logException("Error writing config file", e);
//                }
//
//
//                try{
//                    if(forceStopPackage != null){
//                        forceStopPackage.setAccessible(true);
//                    }
//
//                    forceStopPackage.invoke(activityManager,pkName);
//                } catch (java.lang.IllegalAccessException e) {
//                    VpnStatus.logException("Error writing config file", e);
//                } catch (java.lang.reflect.InvocationTargetException e) {
//                    VpnStatus.logException("Error writing config file", e);
//                }catch (Exception e){
//
//                }
                initProcess();
                killProcess(pkName);
                close();


//                try {
//                    Class c;
//                    c = Class.forName("android.app.ActivityManager");
//                    Method m = c.getMethod("forceStopPackage", new Class[]{String.class});
//                    Object o = m.invoke(am, new Object[]{pkName});
//                }catch (Exception e){
//                    Log.e(TAG, "Failed to kill service", e);
//                }
                count++;//杀死进程的计数+1
                nameList+="  "+pkName;
            }
            Log.i(TAG, nameList+"---------------------");
        }

        long afterMem = getAvailMemory(MainSActivity.this);//清理后的内存占用
//        Toast.makeText(MainSActivity.this, "杀死 " + (count+1) + " 个进程, 释放"
//                + formatFileSize(afterMem - beforeMem) + "内存", Toast.LENGTH_LONG).show();
//        Log.i(TAG, "清理后可用内存为 : " + afterMem);
//        Log.i(TAG, "清理进程数量为 : " + count+1);

    }


    /*
     * *获取可用内存大小
     */
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /*
     * *字符串转换 long-string KB/MB
     */
//    private String formatFileSize(long number){
//        return Formatter.formatFileSize(MainSActivity.this, number);
//    }
//    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        mStatusView = (TextView) findViewById(R.id.status);
//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
//        mActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent=new Intent(MainActivity.this,MainSActivity.class);
//                startActivity(intent);
//                // mVpnUiLogic.connectOrDisconnect();
//            }
//        });
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateVpnUIStatus(VpnStatus.ConnectionStatus level, String stateMessage) {
//        if (TextUtils.isEmpty(stateMessage)) {
//            stateMessage = getString(R.string.state_welcome);
//        }
//        mStatusView.setText(stateMessage);
//        if (level == VpnStatus.ConnectionStatus.LEVEL_CONNECTED) {
//            mActionButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//            mActionButton.setEnabled(true);
//        } else if (level == VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED) {
//            mActionButton.setImageResource(android.R.drawable.ic_menu_send);
//            mActionButton.setEnabled(true);
//        } else {
//            mActionButton.setEnabled(false);
//        }
    }

    @Override
    public void onVpnStateChange(VpnStatus.ConnectionStatus level, String message) {
        updateVpnUIStatus(level,message);
 //       if(level = VpnStatus.ConnectionStatus.)
        if (VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED ==level){
            setNetwork();
//            finish();
        }
    }

//    @Override
    public void onDataUpdate(List<VpnConfigFile> listData) {
//        mRecyclerView.setAdapter(new VpnConfigListAdapter(listData));
    }

    @Override
    public void onDataLoadFailed(String errMessage) {

    }
    public static final String TAG = "SensorTest";
    //调用距离传感器，控制屏幕
    private SensorManager mManager;//传感器管理对象
    //屏幕开关
    private PowerManager localPowerManager = null;//电源管理对象
    private PowerManager.WakeLock localWakeLock = null;//电源锁
    //TextView
  //  private TextView tv;//基本上没啥用

//    @Override
//    public void onResume(){
//        super.onResume();
//
//    }

//    public void onStop(){
//        super.onStop();
//  //      Log.d(TAG,"on stop");
//    }

//    public void onDestroy(){
//        super.onDestroy();
//    //    Log.d(TAG,"on destroy");
//
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] its = event.values;
        //Log.d(TAG,"its array:"+its+"sensor type :"+event.sensor.getType()+" proximity type:"+Sensor.TYPE_PROXIMITY);
        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

//            System.out.println("its[0]:" + its[0]);
      //      tv.setText(its[0]+"");
            //经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
            if (its[0] == 0.0) {// 贴近手机

                System.out.println("hands up");
     //           Log.d(TAG,"hands up in calling activity");
                if (localWakeLock.isHeld()) {
                    return;
                } else{

                    localWakeLock.acquire();// 申请设备电源锁
                }
            } else {// 远离手机

                System.out.println("hands moved");
     //           Log.d(TAG,"hands moved in calling activity");
                if (localWakeLock.isHeld()) {
                    return;
                } else{
                    localWakeLock.setReferenceCounted(false);
                    localWakeLock.release(); // 释放设备电源锁
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO 自动生成的方法存根

    }
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        new ImageView(MainSActivity.this);

        apps = getPackageManager().queryIntentActivities(mainIntent, 0);


       for (int i = 0; i <apps.size(); i++) {
            ApplicationInfo info = new ApplicationInfo();
            info.setId(i + "");
            info.setTitle((String) apps.get(i).loadLabel(getPackageManager()));
            info.setPackageName(apps.get(i).activityInfo.packageName);
            info.setcls(apps.get(i).activityInfo.name);
////                        info.setTitle(i+"应用");
//            info.setOrder(i);
////            System.out.println("1" + (String) apps.get(i).loadLabel(getPackageManager()));
////            System.out.println("2" + apps.get(i).activityInfo.packageName);
////            System.out.println("3" + apps.get(i).activityInfo.name);
//            if (appss.get(position).activityInfo.name !="com.miui.gallery.activity.HomePageActivity" ){
//                ApplicationInfo info = new ApplicationInfo();
//                info.setId(position + "");
//                info.setTitle("设置");
//                info.setPackageName(appss.get(position).activityInfo.packageName) ;
//                info.setcls(appss.get(position).activityInfo.name);
////                        info.setTitle(i+"应用");
//                info.setOrder(position);
//                System.out.println("1" + (String)apps.get(i).loadLabel(getPackageManager()));
//                System.out.println("2" + apps.get(i).activityInfo.packageName);
//                System.out.println("3" + apps.get(i).activityInfo.name);
//                Resources r = this.getContext().getResources();
//                Inputstream ls = r.openRawResource(R.drawable.my_background_image);
//                BitmapDrawable  bmpDraw = new BitmapDrawable(is);
//                BitmapDrawable bd = (BitmapDrawable) apps.get(position).activityInfo.loadIcon(getPackageManager());
//                Bitmap bitmap = bd.getBitmap();
//                info.setBitmap(bitmap);
////                info.setImgUrl("http://pica.nipic.com/2008-03-20/2008320152335853_2.jpg");
            if (info.getTitle().equals("短信")) {
                appss.add(apps.get(i));
            } else if (info.getTitle().equals("电话")) {
                appss.add(apps.get(i));
            } else if (info.getTitle().equals("相机")) {
                appss.add(apps.get(i));
            } else if (info.getTitle().equals("设置")) {
                appss.add(apps.get(i));
                ResolveInfo rl = new ResolveInfo();
            }
//            } else {
//                list.add(info);
//                //System.out.println("4" + info.getPackageName());
//            }
       }
//        for (int i = 0; i <apps.size(); i++) {
//            if (apps.get(i).activityInfo.packageName.equals("com.android.settings")){
//               boolean b =  appss.add(apps.get(i));
//            }
//        }
       // appss = apps;
//        if (apps != null) {
//            for ( resolveInfo : apps) {
//
////                Log.v(TAG, resolveInfo.toString());
//            }
//        }
    }
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (RECEIVER_ADD_APP.equals(intent.getAction())) {
//                SpringBoardPage page = (SpringBoardPage) getPage(getPageCount(), false);
//                if (page.getIconsCount() >= LayoutCalculator.iconsPerPage) {
//                    page = addNewPage();
//                }
//                ApplicationInfo info = new ApplicationInfo();
//                info.setTitle("应用" + 88);
//                info.setOrder(88);
//                info.setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//                page.clearUp(info);
//                page.invalidate();
//            } else if (ApplicationInfo.LOAD_ICON.equals(intent.getAction())) {
//                if (mFolderView == null) {
//                    for (int i = 1; i < getPageCount() + 1; i++) {
//                        SpringBoardPage page = (SpringBoardPage) getPage(i, false);
//                        page.invalidate();
//                    }
//                } else {
//                    FolderContentView page = (FolderContentView) getPage(0, true);
//                    page.invalidate();
//                }
//
//            }
//        }
//    };

    private OnGestureListener gestureListener = new OnGestureListener() {
        public boolean onDown(MotionEvent ev) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) <= 100.0F)
                return false;
            if (velocityX > 0) {
                scrollToLeft();
            } else {
                scrollToRight();
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    };

    private class OnLayoutReady implements OnGlobalLayoutListener {

        private OnLayoutReady() {
        }

        public void onGlobalLayout() {
            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            layoutReady();
            setupScrollView();
        }
    }

    private void layoutReady() {
        pages = new Vector<ApplicationInfo[]>();
        screenWidth = mFrame.getWidth();
        screenHeight = mFrame.getHeight();
        lc.layoutReady(mFrame);
    }

    private void setupScrollView() {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                initDataFromDB(list);
                return "";
            }

            @Override
            protected void onPostExecute(String result) {
                loaded();
                if (!needUpload) {
                    list.clear();
                    controller.initData(list);
                    appsGrid.setAdapter(new AppsAdapter());
                    appsGrid.setOnItemClickListener(clickListener);
                } else {
                    controller.onSynchronize();
                }
            }
        };
        task.execute("");
    }

    private void ensurePages(int count) {
        if (pages.size() < count)
            addPage();
    }

    private ApplicationInfo[] addPage() {
        ApplicationInfo[] infos = new ApplicationInfo[LayoutCalculator.rows * LayoutCalculator.columns];
        pages.add(infos);
        return infos;
    }

    public void loaded() {
        pages.clear();
        scrollView.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, -1);
        LinearLayout emptyView = new LinearLayout(MainSActivity.this);
        emptyView.setBackgroundDrawable(null);
        scrollView.addView(emptyView, params);
        LinearLayout emptyView2 = new LinearLayout(MainSActivity.this);
        emptyView2.setBackgroundDrawable(null);
        scrollView.addView(emptyView2, params);
        ensurePages(1);
        for (int i = 0; i < list.size(); i++) {
            int page = i / LayoutCalculator.iconsPerPage + 1;
            int index = i % LayoutCalculator.iconsPerPage;
            ensurePages(page);
            ApplicationInfo[] infos = pages.get(page - 1);
            if ((index < infos.length) && (infos[index] == null))
                infos[index] = list.get(i);
        }
        for (int i = 0; i < pages.size(); i++) {
            SpringBoardPage page = new SpringBoardPage(MainSActivity.this);
            page.init(lc, pp);
            page.setIcons(pages.get(i));
            scrollView.addView(page, scrollView.getChildCount() - 1, params);
        }
        scrollContainer.post(new Runnable() {

            @Override
            public void run() {
                scrollContainer.scrollTo(screenWidth, 0);
                scrollContainer.setVisibility(View.VISIBLE);
            }
        });
        dotView.setPages(getPageCount());
        dotView.setCurrentPage(0);
    }

    private void scrollToCurrent(int index) {
        scrollContainer.smoothScrollTo(index * screenWidth, 0);
        dotView.setCurrentPage(index - 1);
    }

    public void scrollToLeft() {
        IPageView page =getPage(selectedPageIndex, false);
        if (page != null) {
            page.deselect();
        }
        if (--selectedPageIndex <= 1) {
            selectedPageIndex = 1;
        }
        scrollContainer.smoothScrollTo(selectedPageIndex * screenWidth, 0);
        dotView.setCurrentPage(selectedPageIndex - 1);
    }

    public void scrollToRight() {
        getPage(selectedPageIndex, false).deselect();
        if (++selectedPageIndex > scrollView.getChildCount() - 2) {
            selectedPageIndex = scrollView.getChildCount() - 2;
        }
        scrollContainer.smoothScrollTo(selectedPageIndex * screenWidth, 0);
        dotView.setCurrentPage(selectedPageIndex - 1);
    }

    private static double getDistance(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    private OnTouchListener scrollContainer_OnTouch = new OnTouchListener() {
        private float x;
        private float y;
        boolean gdIntercept;
        private int scrollPointX;
        private int scrollPointY;
        IPageView currentPage;
        private CancellableQueueTimer jiggleModeWaiter;
        private CancellableQueueTimer moveToScrollWaiter;
        private CancellableQueueTimer moveIntoFolderWaiter;
        private CancellableQueueTimer startDragWaiter;
        private CancellableQueueTimer moveIconWaiter;
        private CancellableQueueTimer moveToDesktopWaiter;
        private int startIndex = -1;
        private boolean isDrag = false; //是否可以拖动
        private HitTestResult3 oldHitTest2 = new HitTestResult3();
        private FolderScrollView folderScrollView;
        private boolean isFolderActionDown = false;
        private boolean isDesktopActionDown = false;

        private Runnable jiggleDetacher = new Runnable() {

            @Override
            public void run() {
                if (mFolderView != null) {
                    mFolderView.jiggle();
                } else {
                    jma.jiggle();
                }
            }
        };
        private Runnable scrollToLeftDetacher = new Runnable() {

            @Override
            public void run() {
                if (mover.isAboveFolder()) {
                    mover.bisideFolder();
                    currentPage.removeFolderBound();
                    if (moveIntoFolderWaiter != null) {
                        moveIntoFolderWaiter.cancel();
                        moveIntoFolderWaiter = null;
                    }
                }
                scrollToLeft();
            }
        };
        private Runnable scrollToRightDetacher = new Runnable() {

            @Override
            public void run() {
                if (mover.isAboveFolder()) {
                    mover.bisideFolder();
                    currentPage.removeFolderBound();
                    if (moveIntoFolderWaiter != null) {
                        moveIntoFolderWaiter.cancel();
                        moveIntoFolderWaiter = null;
                    }
                }
                scrollToRight();
            }
        };
        private Runnable startDragDetacher = new Runnable() {

            @Override
            public void run() {
                isDrag = true;
                if (currentPage != null) {
                    detachIcon(currentPage, startIndex, currentPage.getSelectedIndex(), mFolderView != null);
                }
            }
        };
        private Runnable moveIconDetacher = new Runnable() {
            HitTestResult3 oldHitTest = new HitTestResult3();

            @Override
            public void run() {
                if (hitTest2.index >= 0) {
                    if (oldHitTest.index != hitTest2.index || oldHitTest.inIcon != hitTest2.inIcon) {
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                        oldHitTest.index = hitTest2.index;
                        oldHitTest.inIcon = hitTest2.inIcon;
                    }
                    if (!hitTest2.inIcon) {
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                        if (currentPage.setMoveTo(hitTest2.index)) {
                            if (mFolderView != null) {
                                mFolderView.initLayout();
                            }
                            mover.setIndex(hitTest2.index);
                            mover.setPageIndex(selectedPageIndex);
                            mover.setsIndex(hitTest2.index);
                            mover.setsPageIndex(selectedPageIndex);
                        } else {
                            mover.setIndex(mover.getsIndex());
                            mover.setPageIndex(mover.getsPageIndex());
                        }
                    } else {
                        if (!mover.isAboveFolder()) {
                            mover.aboveFolder();
                            mover.setIndex(hitTest2.index);
                            mover.setPageIndex(selectedPageIndex);
                            currentPage.createFolderBound(hitTest2.index);
                            if (moveIntoFolderWaiter == null) {
                                moveIntoFolderWaiter = new CancellableQueueTimer(handler,
                                        ViewConfiguration.getLongPressTimeout(), moveIntoFolderDetacher);
                            }
                        }
                    }
                } else {
                    mover.setIndex(mover.getsIndex());
                    mover.setPageIndex(mover.getsPageIndex());
                }
                moveIconWaiter = null;
            }
        };
        private Runnable moveIntoFolderDetacher = new Runnable() {

            @Override
            public void run() {
                if (hitTest2.index > 0) {
                    IPageView page = getPage(selectedPageIndex, false);
                    ApplicationInfo info = page.getIcon(hitTest2.index);
                    if (info != null && info.getType() == ItemInfo.TYPE_FOLDER) {
                        FolderInfo folder = (FolderInfo) info;
                        openFolderIndex = hitTest2.index;
                        openFolder(folder);
                    }
                }
                moveIntoFolderWaiter = null;
            }
        };

        private Runnable moveToDesktopDetacher = new Runnable() {

            @Override
            public void run() {
                currentPage.clearUp(null);
                closeFolder();
                getPage(selectedPageIndex, false).clearUp(null);
            }
        };

        public boolean onTouchFolder(View v, final MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mover.isMoving()) {
                        return false;
                    }
                    x = ev.getX();
                    y = ev.getY();
                    isDrag = false;
                    oldHitTest2.index = -1;
                    oldHitTest2.inIcon = false;
                    folderScrollView = mFolderView.getScrollView();
                    mover.setAboveFolder(false);
                    scrollPointY = folderScrollView.getScrollY();
                    currentPage = getPage(0, true);
                    if (x < mFolderView.getTranslateLeft() || y < mFolderView.getTranslateTop()
                            || x > folderScrollView.getWidth() + mFolderView.getTranslateLeft()
                            || y > folderScrollView.getHeight() + mFolderView.getTranslateTop()) {
                        closeFolder();
                    }
                    isDesktopActionDown = false;
                    isFolderActionDown = true;
                    if (currentPage != null) {
                        currentPage.hitTest3((int) x - mFolderView.getTranslateLeft(),
                                (int) y - mFolderView.getTranslateTop() + scrollPointY, hitTest3);
                        if (hitTest3.index >= 0) {
                            currentPage.select(hitTest3.index);
                            ApplicationInfo info = currentPage.getIcon(hitTest3.index);
                            if (info != null) {
                                if (mFolderView.isJiggling()) {
                                    if (!hitTest3.buttonRemove) {
                                        if (startDragWaiter == null) {
                                            startDragWaiter = new CancellableQueueTimer(handler, 200, startDragDetacher);
                                        }
                                    }
                                } else {
                                    hitTest3.buttonRemove = false;
                                }
                            }
                        }

                    }
                    if (!mFolderView.isJiggling() && jiggleModeWaiter == null) {
                        jiggleModeWaiter = new CancellableQueueTimer(handler, ViewConfiguration.getLongPressTimeout(),
                                jiggleDetacher);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (!isFolderActionDown) {
                        folderScrollView = mFolderView.getScrollView();
                        x = ev.getX();
                        y = ev.getY();
                        mover.setAboveFolder(false);
                        scrollPointY = folderScrollView.getScrollY();
                        currentPage = getPage(0, true);
                        isFolderActionDown = true;
                        isDesktopActionDown = false;
                    }
                    if (mFolderView.isJiggling()) {
                        if (getDistance(ev.getX(), ev.getY(), x, y) <= touchSlop) {
                            if (currentPage != null) {
                                if (isDrag) {
                                    detachIcon(currentPage, startIndex, currentPage.getSelectedIndex(), true);
                                }
                            }
                        } else {
                            if (currentPage != null) {
                                currentPage.deselect();
                            }
                            if (startDragWaiter != null) {
                                startDragWaiter.cancel();
                                startDragWaiter = null;
                            }
                            hitTest3.buttonRemove = false;
                        }
                        if (mover.isMoving()) {
                            Point point = new Point((int) ev.getX(), (int) ev.getY());
                            mover.moveTo(point.x, point.y);
                            mFolderView.invalidate(mover.getBounds());
                            if (currentPage != null) {
                                if (point.x < mFolderView.getTranslateLeft() || point.y < mFolderView.getTranslateTop()
                                        || point.x > folderScrollView.getWidth() + mFolderView.getTranslateLeft()
                                        || point.y > folderScrollView.getHeight() + mFolderView.getTranslateTop()) {
                                    if (moveToDesktopWaiter == null) {
                                        moveToDesktopWaiter = new CancellableQueueTimer(handler,
                                                ViewConfiguration.getLongPressTimeout(), moveToDesktopDetacher);
                                    }
                                } else {
                                    if (moveToDesktopWaiter != null) {
                                        moveToDesktopWaiter.cancel();
                                        moveToDesktopWaiter = null;
                                    }
                                }
                                if (point.y - mFolderView.getTranslateTop() < folderScrollView.getHeight() / 3) {
                                    folderScrollView.scrollBy(0, -10);
                                    scrollPointY = folderScrollView.getScrollY();
                                }
                                if (point.y - mFolderView.getTranslateTop() > folderScrollView.getHeight() * 2 / 3) {
                                    mFolderView.getScrollView().scrollBy(0, 10);
                                    scrollPointY = folderScrollView.getScrollY();
                                }
                                int position = currentPage.hitTest2(point.x - mFolderView.getTranslateLeft(), point.y
                                        - mFolderView.getTranslateTop() + scrollPointY, hitTest2, false);
                                if (position == 0) {
                                    if (hitTest2.index >= 0) {
                                        if (oldHitTest2.index != hitTest2.index || oldHitTest2.inIcon != hitTest2.inIcon) {
                                            oldHitTest2.index = hitTest2.index;
                                            oldHitTest2.inIcon = hitTest2.inIcon;
                                            if (moveIconWaiter != null) {
                                                moveIconWaiter.cancel();
                                                moveIconWaiter = null;
                                            }
                                            moveIconWaiter = new CancellableQueueTimer(handler, 100, moveIconDetacher);
                                        }
                                    }
                                } else {
                                    if (moveIconWaiter != null) {
                                        moveIconWaiter.cancel();
                                        moveIconWaiter = null;
                                    }
                                    if (mover.isAboveFolder()) {
                                        mover.bisideFolder();
                                        mover.setIndex(mover.getsIndex());
                                        mover.setPageIndex(mover.getsPageIndex());
                                        currentPage.removeFolderBound();
                                        if (moveIntoFolderWaiter != null) {
                                            moveIntoFolderWaiter.cancel();
                                            moveIntoFolderWaiter = null;
                                        }
                                    }
                                }
                                if (moveToScrollWaiter != null) {
                                    moveToScrollWaiter.cancel();
                                    moveToScrollWaiter = null;
                                }
                            }
                            return true;
                        } else {
                            folderScrollView.scrollTo(0, (int) (scrollPointY - (ev.getY() - y)));
                        }
                    } else {
                        folderScrollView.scrollTo(0, (int) (scrollPointY - (ev.getY() - y)));
                        if (getDistance(ev.getX(), ev.getY(), x, y) > touchSlop) {
                            if (jiggleModeWaiter != null) {
                                jiggleModeWaiter.cancel();
                                jiggleModeWaiter = null;
                            }
                            if (currentPage != null) {
                                if (currentPage.getSelectedIndex() >= 0) {
                                    currentPage.deselect();
                                }
                            }
                        }

                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    currentPage.deselect();
                case MotionEvent.ACTION_UP:
                    isDesktopActionDown = false;
                    isFolderActionDown = false;
                    if (jiggleModeWaiter != null) {
                        jiggleModeWaiter.cancel();
                        jiggleModeWaiter = null;
                    }
                    if (moveToScrollWaiter != null) {
                        moveToScrollWaiter.cancel();
                        moveToScrollWaiter = null;
                    }
                    if (startDragWaiter != null) {
                        startDragWaiter.cancel();
                        startDragWaiter = null;
                    }
                    if (moveToDesktopWaiter != null) {
                        moveToDesktopWaiter.cancel();
                        moveToDesktopWaiter = null;
                    }
                    final IPageView currentPage = getPage(selectedPageIndex, true);
                    if (currentPage != null) {
                        final int select = currentPage.getSelectedIndex();
                        if (select >= 0) {
                            ApplicationInfo info = currentPage.getSelectedApp();
                            if (info != null) {
                                if (!mFolderView.isJiggling()) {
                                    controller.onAppClick(info);
                                } else {
                                    if (hitTest3.buttonRemove) {
                                        currentPage.removeApp(hitTest3.index);
                                        controller.onAppRemove(info);
                                    }
                                }
                            }
                        }
                        currentPage.deselect();
                    }
                    if (mover.isMoving()) {
                        final IPageView p = getPage(mover.getPageIndex(), true);
                        Point point = p.getIconLocation(mover.getIndex());
                        final ApplicationInfo app = mover.hook();
                        mover.stopMoving(point.x + mFolderView.getTranslateLeft(), point.y + mFolderView.getTranslateTop()
                                - scrollPointY, new OnMovingStopped() {
                            @Override
                            public void movingStopped(ApplicationInfo appInfo) {
                                p.clearUp(app);
                            }
                        });
                    }
                    startIndex = -1;
                    return true;
            }
            return false;

        }

        @Override
        public boolean onTouch(View v, final MotionEvent ev) {
            hitTest2.index = -1;
            hitTest2.inIcon = false;
            hitTest2.buttonRemove = false;
            if (mFolderView != null) {
                return onTouchFolder(v, ev);
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mover.isMoving()) {
                        return false;
                    }
                    isDrag = false;
                    oldHitTest2.index = -1;
                    oldHitTest2.inIcon = false;
                    mover.setAboveFolder(false);
                    x = ev.getX();
                    y = ev.getY();
                    scrollPointX = scrollContainer.getScrollX();
                    int index = scrollPointX / screenWidth;
                    startIndex = index;
                    currentPage = getPage(index, false);
                    isDesktopActionDown = true;
                    isFolderActionDown = false;
                    if (currentPage != null) {
                        currentPage.hitTest3((int) x, (int) y, hitTest3);
                        if (hitTest3.index >= 0) {
                            currentPage.select(hitTest3.index);
                            ApplicationInfo info = currentPage.getIcon(hitTest3.index);
                            if (info != null) {
                                if (jma.isJiggling()) {
                                    if (!hitTest3.buttonRemove) {
                                        if (startDragWaiter == null) {
                                            startDragWaiter = new CancellableQueueTimer(handler, 200, startDragDetacher);
                                        }
                                    }
                                } else {
                                    hitTest3.buttonRemove = false;
                                }
                            }
                        }

                    }
                    gdIntercept = gd.onTouchEvent(ev);
                    if (!jma.isJiggling() && jiggleModeWaiter == null) {
                        jiggleModeWaiter = new CancellableQueueTimer(handler, ViewConfiguration.getLongPressTimeout(),
                                jiggleDetacher);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int currentPointX = scrollContainer.getScrollX();
                    index = currentPointX / screenWidth;
                    currentPage = getPage(index, false);
                    if (!isDesktopActionDown) {
                        x = ev.getX();
                        y = ev.getY();
                        scrollPointX = scrollContainer.getScrollX();
                        startIndex = index;
                        isFolderActionDown = false;
                        isDesktopActionDown = true;
                    }
                    if (!gdIntercept) {
                        if (jma.isJiggling()) {
                            if (getDistance(ev.getX(), ev.getY(), x, y) <= touchSlop) {
                                if (currentPage != null) {
                                    if (isDrag) {
                                        detachIcon(currentPage, startIndex, currentPage.getSelectedIndex(), false);
                                    }
                                }
                            } else {
                                if (currentPage != null) {
                                    currentPage.deselect();
                                }
                                if (startDragWaiter != null) {
                                    startDragWaiter.cancel();
                                    startDragWaiter = null;
                                }
                                hitTest3.buttonRemove = false;
                            }
                            if (mover.isMoving()) {
                                Point point = new Point((int) ev.getX(), (int) ev.getY());
                                mover.moveTo(point.x, point.y);
                                mFrame.invalidate(mover.getBounds());
                                if (currentPage != null) {
                                    int position = currentPage.hitTest2(point.x, point.y, hitTest2,
                                            mover.hook().getType() == ItemInfo.TYPE_FOLDER);
                                    if (position == -1) {
                                        if (moveToScrollWaiter == null) {
                                            moveToScrollWaiter = new CancellableQueueTimer(handler,
                                                    ViewConfiguration.getLongPressTimeout(), scrollToLeftDetacher);
                                        }
                                        return true;
                                    } else if (position == 1) {
                                        if (moveToScrollWaiter == null) {
                                            moveToScrollWaiter = new CancellableQueueTimer(handler,
                                                    ViewConfiguration.getLongPressTimeout(), scrollToRightDetacher);
                                        }
                                        return true;
                                    } else if (position == 0) {
                                        if (hitTest2.index >= 0) {
                                            if (oldHitTest2.index != hitTest2.index
                                                    || oldHitTest2.inIcon != hitTest2.inIcon) {
                                                oldHitTest2.index = hitTest2.index;
                                                oldHitTest2.inIcon = hitTest2.inIcon;
                                                if (moveIconWaiter != null) {
                                                    moveIconWaiter.cancel();
                                                    moveIconWaiter = null;
                                                }
                                                moveIconWaiter = new CancellableQueueTimer(handler, 100, moveIconDetacher);
                                            }
                                        }
                                    } else {
                                        if (moveIconWaiter != null) {
                                            moveIconWaiter.cancel();
                                            moveIconWaiter = null;
                                        }
                                        if (mover.isAboveFolder()) {
                                            mover.bisideFolder();
                                            mover.setIndex(mover.getsIndex());
                                            mover.setPageIndex(mover.getsPageIndex());
                                            currentPage.removeFolderBound();
                                            if (moveIntoFolderWaiter != null) {
                                                moveIntoFolderWaiter.cancel();
                                                moveIntoFolderWaiter = null;
                                            }
                                        }
                                    }
                                    if (moveToScrollWaiter != null) {
                                        moveToScrollWaiter.cancel();
                                        moveToScrollWaiter = null;
                                    }
                                }
                                return true;
                            } else {
                                scrollContainer.scrollTo((int) (scrollPointX - (ev.getX() - x)), 0);
                            }
                        } else {
                            scrollContainer.scrollTo((int) (scrollPointX - (ev.getX() - x)), 0);
                            if (getDistance(ev.getX(), ev.getY(), x, y) > touchSlop) {
                                if (jiggleModeWaiter != null) {
                                    jiggleModeWaiter.cancel();
                                    jiggleModeWaiter = null;
                                }
                                if (currentPage != null) {
                                    if (currentPage.getSelectedIndex() >= 0) {
                                        currentPage.deselect();
                                    }
                                }
                            }

                        }
                    }
                    if (isOverScrolling()) {
                        float f = ev.getX() - x;
                        scrollContainer.scrollTo((int) (scrollPointX - f / 2.0F), 0);
                        return true;
                    }
                    gdIntercept = gd.onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (currentPage != null) {
                        currentPage.deselect();
                    }
                case MotionEvent.ACTION_UP:
                    if (jiggleModeWaiter != null) {
                        jiggleModeWaiter.cancel();
                        jiggleModeWaiter = null;
                    }
                    if (moveToScrollWaiter != null) {
                        moveToScrollWaiter.cancel();
                        moveToScrollWaiter = null;
                    }
                    if (startDragWaiter != null) {
                        startDragWaiter.cancel();
                        startDragWaiter = null;
                    }
                    gdIntercept = gd.onTouchEvent(ev);
                    if (isOverScrolling()) {
                        scrollToCurrent(selectedPageIndex);
                    } else {
                        if (!gdIntercept && !mover.isMoving()) {
                            if (Math.abs((ev.getX() - x)) > screenWidth / 2) {
                                if (ev.getX() - x > 0) {
                                    scrollToLeft();
                                } else {
                                    scrollToRight();
                                }
                            } else {
                                scrollToCurrent(selectedPageIndex);
                            }
                        }
                    }
                    isDesktopActionDown = false;
                    isFolderActionDown = false;
                    final IPageView currentPage = getPage(selectedPageIndex, false);
                    if (currentPage != null) {
                        final int select = currentPage.getSelectedIndex();
                        if (select >= 0) {
                            ApplicationInfo info = currentPage.getSelectedApp();
                            if (info != null) {
                                if (!jma.isJiggling()) {
                                    if (info.getType() == ItemInfo.TYPE_FOLDER) {
                                        openFolderIndex = select;
                                        openFolder((FolderInfo) info);
                                    } else {
                                        controller.onAppClick(info);
                                    }
                                } else {
                                    if (hitTest3.buttonRemove) {
                                        currentPage.removeApp(hitTest3.index);
                                        controller.onAppRemove(info);
                                    }
                                    if (info.getType() == ItemInfo.TYPE_FOLDER && !isDrag) {
                                        openFolderIndex = select;
                                        openFolder((FolderInfo) info);
                                    }
                                }

                            }
                        }
                        currentPage.deselect();
                    }
                    if (mover.isMoving()) {
                        for (int i = 1; i < getPageCount() + 1; i++) {
                            if (i != mover.getPageIndex()) {
                                getPage(i, false).clearUp(null);
                            }
                        }
                        final IPageView p = getPage(mover.getPageIndex(), false);
                        if (p != null) {
                            Point point = p.getIconLocation(mover.getIndex());
                            final ApplicationInfo app = mover.hook();
                            final int i = mover.getIndex();
                            if (!mover.isAboveFolder()) {
                                mover.stopMoving((mover.getPageIndex() - selectedPageIndex) * screenWidth + point.x,
                                        point.y, new OnMovingStopped() {
                                            @Override
                                            public void movingStopped(ApplicationInfo appInfo) {
                                                p.clearUp(app);
                                            }
                                        });

                            } else {
                                mover.setAboveFolder(false);
                                p.removeFolderBound();
                                mover.moveIntoFolder((mover.getPageIndex() - selectedPageIndex) * screenWidth + point.x,
                                        point.y, new OnMovingStopped() {
                                            @Override
                                            public void movingStopped(ApplicationInfo appInfo) {
                                                p.addToFolder(i, app);
                                                p.clearUp(null);
                                            }
                                        });
                            }
                        }
                    }
                    startIndex = -1;
                    return true;
            }
            return false;
        }

        private void detachIcon(IPageView page, int pageIndex, int index, boolean isFolder) {

            ApplicationInfo info = page.getIcon(index);
            if (info == null)
                return;
            page.deselect();
            Point point = page.getIconLocation(index);
            if (!mover.isMoving()) {
                if (isFolder) {
                    mover.startMoving(info, point.x + mFolderView.getTranslateLeft(), mFolderView.getTranslateTop()
                            + point.y - folderScrollView.getScrollY(), (int) x, (int) y);
                } else {
                    mover.startMoving(info, point.x, point.y, (int) x, (int) y);
                    mover.setPageIndex(pageIndex);
                    mover.setsPageIndex(pageIndex);
                }
                mover.setIndex(index);
                mover.setsIndex(index);
            }
            page.setIconIntoPage(index, null);
        }
    };

    @Override
    public void onBackPressed() {
        if (jma.isJiggling()) {
            jma.unjiggle();
            IPageView page = getPage(selectedPageIndex, false);
            if (page.isMessed()) {
                onSynchronize();
            }
        } else {
            if (mFolderView != null) {
                if (mFolderView.isJiggling()) {
                    mFolderView.unJiggle();
                    onSynchronize();
                } else {
                    closeFolder();
                }
            }
        }
    };

    private void onSynchronize() {
        boolean isMessed = false;
        if (mFolderView != null) {
            IPageView page = getPage(0, true);
            if (page.isMessed()) {
                isMessed = true;
                page.setMessed(false);
            }
        }
        for (int i = 1; i < getPageCount() + 1; i++) {
            IPageView page = getPage(i, false);
            if (page.isMessed()) {
                isMessed = true;
                page.setMessed(false);
            }
        }
        if (isMessed) {
            onSynchronizeDB();
            controller.onSynchronize();
        }
    }

    private void onSynchronizeDB() {

    }

    private boolean isOverScrolling() {
        int x = scrollContainer.getScrollX();
        if (x < screenWidth)
            return true;
        else {
            if (x > (scrollView.getChildCount() - 2) * this.screenWidth) {
                return true;
            }
        }
        return false;
    }

    public static abstract interface OnPageScrollListener {
        public abstract void onPageScroll(int paramInt);
    }

    public IPageView getPage(int index, boolean isFolder) {
        IPageView page = null;
        if (isFolder && mFolderView != null) {
            return mFolderView.getContentView();
        }
        if ((index <= 0) || (index > getPageCount()))
            return null;
        View view = scrollView.getChildAt(index);
        if (view instanceof SpringBoardPage)
            page = (SpringBoardPage) view;
        return page;
    }

    public int getPageCount() {
        return scrollView.getChildCount() - 2;
    }

    private OnScrollChangedListener scrollContainer_OnScrollChanged = new OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
        }
    };

    private JiggleModeActivator jma = new JiggleModeActivator() {
        public boolean isJigglable() {
            int i = scrollContainer.getScrollX();
            if ((i >= screenWidth) && (i % screenWidth == 0))
                return true;
            return false;
        }

        public void jiggle() {
            setState(JiggleModeActivator.STATE_JIGGLE);
            if (mFolderView != null) {
                mFolderView.jiggle();
            } else {
//                addNewPage();
                for (int i = 1; i < scrollView.getChildCount() - 1; i++) {
                    getPage(i, false).jiggle();
                }
            }
        }

        public void unjiggle() {
            setState(JiggleModeActivator.STATE_UNJIGGLE);
            for (int i = scrollView.getChildCount() - 2; i >= 1; i--) {
                IPageView page = getPage(i, false);
                page.unJiggle();
                if (page.getIconsCount() == 0) {
                    scrollView.removeViewAt(i);
                    if (getCurrentPageIndex() == i && getCurrentPageIndex() == getPageCount() + 1) {
                        scrollToLeft();
                    }
                }
            }
            dotView.setPages(getPageCount());
        }
    };

    public int getCurrentPageIndex() {
        return scrollContainer.getScrollX() / this.screenWidth;
    }

    public SpringBoardPage addNewPage() {
        if (getPage(scrollView.getChildCount() - 2, false).getIconsCount() == 0) {
            return null;
        }
        SpringBoardPage page = new SpringBoardPage(MainSActivity.this);
        page.init(this.lc, this.pp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.screenWidth, -1);
        scrollView.addView(page, -1 + this.scrollView.getChildCount(), layoutParams);
        dotView.setPages(getPageCount());
        return page;
    }

    @Override
    public void onResume() {
        super.onResume();
        mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
                SensorManager.SENSOR_DELAY_NORMAL);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型
    }

    @Override
    public void onStop() {
        closeFolder();
        if (jma.isJiggling()) {
            jma.unjiggle();
        }
        selectedPageIndex = 1;
        scrollToCurrent(selectedPageIndex);
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        MainSActivity.this.unregisterReceiver(receiver);
        super.onDestroy();

//        mVpnUiLogic.release();
        if(mManager != null){
            localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试
            mManager.unregisterListener(this);//注销传感器监听
        }
    }

    private void closeFolder() {
        SpringBoardPage page = (SpringBoardPage) getPage(selectedPageIndex, false);
        Transformation transformation = new Transformation();
        if (mFolderView != null
                && !mFolderView.getAnimation().getTransformation(AnimationUtils.currentAnimationTimeMillis(),
                transformation)) {
            Point point = page.getIconLocation(openFolderIndex);
            Animation animation = pp.createAnimationOpenFolder(point.x, point.y, screenWidth, screenHeight, false);
            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    boolean isJiggling = mFolderView.isJiggling();
                    mContainer.removeView(mFolderView);
                    mFolderView = null;
                    if (isJiggling) {
                        jma.jiggle();
                    }
                    getPage(selectedPageIndex, false).clearUp(null);
                }
            });
            if (mFolderView != null) {
                mFolderView.startAnimation(animation);
            }
            Animation pageAnimation = pp.createAnimationPageShow(true);
            page.startAnimation(pageAnimation);
            dotView.setVisibility(View.VISIBLE);
        }
    }

    private void openFolder(FolderInfo folderInfo) {
        if (mFolderView == null) {
            SpringBoardPage page = (SpringBoardPage) getPage(selectedPageIndex, false);
            int index;
            if (hitTest2.index != -1) {
                index = hitTest2.index;
            } else {
                index = page.getSelectedIndex();
            }
            Point point = page.getIconLocation(index);
            dotView.setVisibility(View.INVISIBLE);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mFolderView = folderInfo.getFolderView(MainSActivity.this);
            if (mFolderView.getParent() == null) {
                mContainer.addView(mFolderView, params);
            }
            mFolderView.onReday(lc, pp, screenHeight);
            Animation folderAnimation = pp.createAnimationOpenFolder(point.x, point.y, screenWidth, screenHeight, true);
            mFolderView.startAnimation(folderAnimation);
            Animation pageAnimation = pp.createAnimationPageShow(false);
            page.startAnimation(pageAnimation);
            page.removeFolderBound();
            page.clearUp(null);
            if (jma.isJiggling()) {
                mFolderView.jiggle();
            } else {
                mFolderView.unJiggle();
            }
            jma.unjiggle();
        }
    }


    private void initDataFromDB(List<ApplicationInfo> list) {
        //        List<ApplicationInfo> child = new ArrayList<ApplicationInfo>();
        //        for (int i = 0; i < 25; i++) {
        //            ApplicationInfo info = new ApplicationInfo();
        //            info.setTitle("应用" + i);
        //            info.setOrder(i);
        //            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        //            list.add(info);
        //        }
        //        for (int i = 0; i < 13; i++) {
        //            ApplicationInfo info = new ApplicationInfo();
        //            info.setTitle("应用" + i);
        //            info.setId(i + "");
        //            info.setOrder(i);
        //            if (i == 0) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a1));
        //            } else if (i == 1) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a2));
        //            } else if (i == 2) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a3));
        //            } else if (i == 3) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a4));
        //            } else if (i == 4) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a5));
        //            } else if (i == 5) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a6));
        //            } else if (i == 6) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a7));
        //            } else if (i == 7) {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a8));
        //            } else {
        //                info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        //            }
        //            child.add(info);
        //        }
        //        FolderInfo folder = new FolderInfo();
        //        folder.setIcons(child);
        //        folder.setOrder(25);
        //        folder.setTitle("文件夹");
        //        list.add(folder);
        //        Collections.sort(list, new Comparator<ApplicationInfo>() {
        //            public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
        //                return arg0.getOrder() - arg1.getOrder();
        //            }
        //        });
//        List<Launcher> launchers = db.queryLaunchers();
//        for (int i = launchers.size() - 1; i >= 0; i--) {
//            Launcher launcher = launchers.get(i);
//            if (Launcher.RES_TYPE_APP.equals(launcher.getResType())) {
//                ApplicationInfo info = new ApplicationInfo();
//                info.setId(launcher.getId());
//                info.setTitle(launcher.getResName());
//                info.setImgUrl(launcher.getPicPath());
//                list.add(info);
//                launchers.remove(i);
//            }
//            if (Launcher.RES_TYPE_FOLDER.equals(launcher.getResType())) {
//                FolderInfo info = new FolderInfo();
//                info.setId(launcher.getId());
//                info.setTitle(launcher.getResName());
//                info.setImgUrl(launcher.getPicPath());
//                list.add(info);
//                launchers.remove(i);
//            }
//            if (launcher.getUpload().equals(Launcher.UPLOAD_FAIL)) {
//                needUpload = true;
//            }
//        }
//        for (int i = 0; i < launchers.size(); i++) {
//            Launcher launcher = launchers.get(i);
//            if (Launcher.RES_TYPE_FOLDER_APP.equals(launcher.getResType())) {
//                ApplicationInfo info = new ApplicationInfo();
//                info.setId(launcher.getId());
//                info.setTitle(launcher.getResName());
//                info.setImgUrl(launcher.getPicPath());
//                for (int j = 0; j < list.size(); j++) {
//                    ApplicationInfo app = list.get(j);
//                    if (ItemInfo.TYPE_FOLDER == app.getType() && app.getId().equals(launcher.getUpId())) {
//                        FolderInfo folder = (FolderInfo) app;
//                        folder.addIcon(info);
//                    }
//                }
//            }
//        }

    }

    private Controller controller = new Controller() {
        @Override
        public void initData(final List<ApplicationInfo> list) {
            AsyncTask<String, String, String> tast = new AsyncTask<String, String, String>() {

                @Override
                protected String doInBackground(String... params) {
                    List<ApplicationInfo> child = new ArrayList<ApplicationInfo>();

                    for (int i = 0; i <apps.size(); i++) {
                        ApplicationInfo info = new ApplicationInfo();
                        info.setId(i + "");
                        info.setTitle((String)apps.get(i).loadLabel(getPackageManager()));
                        info.setPackageName(apps.get(i).activityInfo.packageName) ;
                        info.setcls(apps.get(i).activityInfo.name);

//                        info.setTitle(i+"应用");
                        info.setOrder(i);
                        System.out.println("1" + (String)apps.get(i).loadLabel(getPackageManager()));
                        System.out.println("2" + apps.get(i).activityInfo.packageName);
                        System.out.println("3" + apps.get(i).activityInfo.name);

                        BitmapDrawable bd = (BitmapDrawable) apps.get(i).activityInfo.loadIcon(getPackageManager());
                        Bitmap bitmap = bd.getBitmap();
                        info.setBitmap(bitmap);
                        info.setImgUrl("http://pica.nipic.com/2008-03-20/2008320152335853_2.jpg");
//                        if (i == 0) {
//                            info.setImgUrl("http://img3.imgtn.bdimg.com/it/u=568867752,3099839373&fm=21&gp=0.jpg");
//                        } else if (i == 1) {
//                            info.setImgUrl("http://a2.att.hudong.com/04/58/300001054794129041580438110_950.jpg");
//                        } else if (i == 2) {
//                            info.setImgUrl("http://img.sc115.com/uploads/sc/jpgs/11/pic1916_sc115.com.jpg");
//                        } else if (i == 3) {
//                            info.setImgUrl("http://pic12.nipic.com/20110222/6660820_111945190101_2.jpg");
//                        } else if (i == 4) {
//                            info.setImgUrl("http://pica.nipic.com/2007-12-26/2007122602930235_2.jpg");
//                        } else if (i == 5) {
//                            info.setImgUrl("http://pic9.nipic.com/20100902/5615113_084913055054_2.jpg");
//                        } else if (i == 6) {
//                            info.setImgUrl("http://pica.nipic.com/2008-01-03/200813165855102_2.jpg");
//                        } else if (i == 7) {
//                            info.setImgUrl("http://pica.nipic.com/2008-03-20/2008320152335853_2.jpg");
//                        } else {
//                            //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
//                            info.setImgUrl("http://pic2.ooopic.com/01/26/61/83bOOOPIC72.jpg");
//                        }
                        //                        info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                        //System.out.println("4" + info.getPackageName());
                        final Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        final ResolveInfo res = getPackageManager().resolveActivity(intent, 0);
                        if(info.getTitle().equals("短信" ))
                        {
                          // appss.add(apps.get(i));
                        }else if(info.getTitle().equals("电话" )){
                           // appss.add(apps.get(i));
                        }else if (info.getTitle().equals("相机" )){
                           // appss.add(apps.get(i));
                        }else
                        {
                            list.add(info);
                            //System.out.println("4" + info.getPackageName());
                        }

                    }
                    //                    for (int i = 0; i < 13; i++) {
                    //                        ApplicationInfo info = new ApplicationInfo();
                    //                        info.setId(i + "aa");
                    //                        info.setTitle("应用" + i);
                    //                        info.setId(i + "");
                    //                        info.setOrder(i);
                    //                        if (i == 0) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a1));
                    //                        } else if (i == 1) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a2));
                    //                        } else if (i == 2) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a3));
                    //                        } else if (i == 3) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a4));
                    //                        } else if (i == 4) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a5));
                    //                        } else if (i == 5) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a6));
                    //                        } else if (i == 6) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a7));
                    //                        } else if (i == 7) {
                    //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.a8));
                    //                        } else {
                    //                            //                            info.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                    //                            info.setImgUrl("http://img3.imgtn.bdimg.com/it/u=568867752,3099839373&fm=21&gp=0.jpg");
                    //                        }
                    //                        child.add(info);
                    //                    }
                    //                    FolderInfo folder = new FolderInfo();
                    //                    folder.setId(25 + "");
                    //                    folder.setIcons(child);
                    //                    folder.setOrder(25);
                    //                    folder.setTitle("文件夹");
                    //                    list.add(folder);
                    Collections.sort(list, new Comparator<ApplicationInfo>() {
                        public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
                            return arg0.getOrder() - arg1.getOrder();
                        }
                    });
                    return null;
                }

                protected void onPostExecute(String result) {
                    loaded();
                };
            };
            tast.execute("");
        }

        @Override
        public void onSynchronize() {
            Toast.makeText(MainSActivity.this, "正在同步数据", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAppClick(ApplicationInfo app) {
            Toast.makeText(MainSActivity.this, "点击了" + app.getTitle(), Toast.LENGTH_SHORT).show();
            String pkg = app.getPackageName();
            //应用的主activity类
            String cls = app.getcls();
            ComponentName componet = new ComponentName(pkg, cls);
            Intent intent = new Intent();
            intent.setComponent(componet);
            intent.setAction("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void onAppRemove(ApplicationInfo app) {
            String packagename = app.getPackageName();
            Intent deleteIntent = new Intent();
            deleteIntent.setAction(Intent.ACTION_DELETE);
            deleteIntent.setData(Uri.parse("package:" + packagename));
            startActivityForResult(deleteIntent, 0);
            Toast.makeText(MainSActivity.this, "删除" + app.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RECEIVER_ADD_APP);
            sendBroadcast(intent);
        }
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            Toast.makeText(MainSActivity.this, "卸载成功！", Toast.LENGTH_LONG);
//        }
    };
    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;

            if (convertView == null) {
                i = new ImageView(MainSActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new GridView.LayoutParams(200, 200));
                i.setAdjustViewBounds(false);
                i.setPadding(8, 8, 8, 8);
            } else {
                i = (ImageView) convertView;
            }
//            ResolveInfo info = appss.get(position);
//            i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
            //position =4 是APP的具体设置，不在APPSS里
            if (appss.get(position).activityInfo.name !="com.miui.gallery.activity.HomePageActivity" ){
                ResolveInfo info = appss.get(position);
                i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
            }else
            {
                i.setImageResource(R.drawable.ic_launcher);
            }


            return i;
        }

        public final int getCount() {
            return appss.size();
        }

        public final Object getItem(int position) {

            if (appss.get(position).loadLabel(getPackageManager()) !="设置" ){
                ApplicationInfo info = new ApplicationInfo();
                info.setId(position + "");
                info.setTitle("设置");
                info.setPackageName(appss.get(position).activityInfo.packageName) ;
                info.setcls(appss.get(position).activityInfo.name);
//                        info.setTitle(i+"应用");
                info.setOrder(position);
//                System.out.println("1" + (String)apps.get(i).loadLabel(getPackageManager()));
//                System.out.println("2" + apps.get(i).activityInfo.packageName);
//                System.out.println("3" + apps.get(i).activityInfo.name);
//                Resources r = this.getContext().getResources();
//                Inputstream ls = r.openRawResource(R.drawable.my_background_image);
//                BitmapDrawable  bmpDraw = new BitmapDrawable(is);
//                BitmapDrawable bd = (BitmapDrawable) apps.get(position).activityInfo.loadIcon(getPackageManager());
//                Bitmap bitmap = bd.getBitmap();
//                info.setBitmap(bitmap);
//                info.setImgUrl("http://pica.nipic.com/2008-03-20/2008320152335853_2.jpg");
//
//
//                i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
                return info;
            }else
            {
                return appss.get(position);
            }

        }


        public final long getItemId(int position) {
            return position;
        }
    }
    public void setFactoryReset() {
        dpm.addUserRestriction(mComponentName, "no_factory_reset");
    }
    public void removeFactoryReset() {
        dpm.clearUserRestriction(mComponentName, "no_factory_reset");
    }
    public void setNetwork() {

        dpm.addUserRestriction(mComponentName, "no_network_reset");
//        dpm.addUserRestriction(mComponentName, UserManager.DISALLOW_CONFIG_WIFI);
//
//        dpm.addUserRestriction(mComponentName, UserManager.DISALLOW_CO"NFIG_MOBILE_NETWORKS);
//        Bundle bundle = um.getUserRestrictions();
//        checkUserRestriction((Boolean) bundle.get(key));
    }
    public void UninstallBlocked() {
        dpm.setUninstallBlocked(mComponentName, "com.singun.openvpn.client", true);
    }

    public void removeWifi() {
        dpm.clearUserRestriction(mComponentName, UserManager.DISALLOW_CONFIG_WIFI);

//        Bundle bundle = um.getUserRestrictions();
//        checkUserRestriction((Boolean) bundle.get(key));
    }
    public void removeMobileNetwork() {
        dpm.clearUserRestriction(mComponentName,  UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);

//        Bundle bundle = um.getUserRestrictions();
//        checkUserRestriction((Boolean) bundle.get(key));
    }
    private void   getBooleanGlobalSetting() {
//        mWifiManager = (WifiManager) MyPolicyReceiver.class
//                .getSystemService(Context.WIFI_SERVICE);
//        mWifiManager.setWifiEnabled(false);
       dpm.setGlobalSetting(MyPolicyReceiver.getComponentName(this),Settings.Global.BLUETOOTH_ON,"0");
    }
}
