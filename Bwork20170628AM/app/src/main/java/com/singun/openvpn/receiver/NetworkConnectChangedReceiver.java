package com.singun.openvpn.receiver;

/**
 * Created by Administrator on 2017-05-09.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import java.lang.reflect.Method;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;

import com.singun.openvpn.client.R;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    Context mcontext;
    public void setMobileDataState(Context context, boolean enabled) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method setDataEnabled = telephonyService.getClass().getDeclaredMethod("setDataEnabled",boolean.class);
            if (null != setDataEnabled) {
                setDataEnabled.invoke(telephonyService, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getMobileDataState(Context context) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getDataEnabled = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getDataEnabled) {
                return (Boolean) getDataEnabled.invoke(telephonyService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private String getConnectionType(int type) {

        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = mcontext.getResources().getString(R.string.NetworkConnectChangedReceiver_3G_netWork);

        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = mcontext.getResources().getString(R.string.NetworkConnectChangedReceiver_wifi_netWork);
        }
        return connType;
    }
    WifiManager mWifiManager;
    WifiInfo mWifiInfo;
    @Override
    public void onReceive(Context context, Intent intent) {
        mcontext = context;
        // 取得WifiManager对象
         mWifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
         mWifiInfo = mWifiManager.getConnectionInfo();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.e("TAG", "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        // 监听wifi的连接状态即是否连上了一个有效无线路由
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                Log.e("TAG", "isConnected:" + isConnected);
                if (isConnected) {
                } else {

                }
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.i("TAG", getConnectionType(info.getType()) + "Connected");
                        setMobileDataState(mcontext,false);
                        if (mWifiManager.isWifiEnabled()) {
                            mWifiManager.setWifiEnabled(false);
                        }
                    }
                } else {
                    Log.i("TAG", getConnectionType(info.getType()) + "disconnect");
                }
            }
        }
    }
}