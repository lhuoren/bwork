package com.singun.openvpn.ui;

/**
 * Created by Administrator on 2017-05-12.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.singun.openvpn.client.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.net.Uri;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.Context;
import com.singun.openvpn.receiver.MyPolicyReceiver;
import android.os.Build;
import java.lang.reflect.Method;
import android.app.Fragment;
//  修改数据连接
import  java.lang.reflect.Field;
import android.telephony.SubscriptionManager;
import java.io.IOException;
import android.telephony.TelephonyManager;

//
public class EgSpeechActivity extends Activity implements OnClickListener {
    public final static String EXTRA_MESSAGE = "com.example.androideg.speech.MESSAGE";

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private static final String SPEECH_PROMPT = "请讲话";
    private DevicePolicyManager dpm;
    private ComponentName mComponentName;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = MyPolicyReceiver.getComponentName(this);
//         检查是否存在recognition activity
//        PackageManager pm = getPackageManager();
//        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
//                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        Button btn = (Button) findViewById(R.id.btn_speek);
//        if (activities.size() != 0) {
//            //如果存在recognitio    n activity则为按钮绑定点击事件
            btn.setOnClickListener(this);
        Button btnxz = (Button) findViewById(R.id.btn_xz);
        btnxz.setOnClickListener(this);
        Button btnjzwl = (Button) findViewById(R.id.btn_jzwl );
        btnjzwl.setOnClickListener(this);
//        } else {
//            // 如果不存在则禁用按钮
//            btn.setEnabled(false);
//            btn.setText("语音识别不可用");
//        }

    }
    private void   getBooleanGlobalSetting(ContentResolver resolver, String setting) {
       Settings.Global.putInt(resolver, setting, 0);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_speek){
            startVoiceRecognitionActivity();

        }else if(v.getId() == R.id.btn_xz)
        {
            dpm.setUninstallBlocked(mComponentName, "com.singun.openvpn.client", false);

        }else if(v.getId() == R.id.btn_jzwl){
            changeConnection(false);
        }
    }
    public void changeConnection(boolean enable) {

        getBooleanGlobalSetting(getContentResolver(), "mobile_data");
    }

    public boolean setMobileDataEnable(boolean enable) {
        //5.0以上，禁用移动网络使用TelephonyManager#setDataEnabled
        //5.0以下，则是ConnectivityManager#setMobileDataEnabled
        Object object = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? getSystemService(TELEPHONY_SERVICE) :
                getSystemService(Context.CONNECTIVITY_SERVICE);
        String methodName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? "setDataEnabled" : "setMobileDataEnabled";
        Method setMobileDataEnable;
        try {
            setMobileDataEnable = object.getClass().getDeclaredMethod(methodName, boolean.class);
            setMobileDataEnable.setAccessible(true);
            setMobileDataEnable.invoke(object, enable);
            return true;
        } catch (Exception e) {
           // NLog.d(TAG, "[setMobileDataEnable] error,exception:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 启动语音识别activity，接收用户语音输入
     */
    private void startVoiceRecognitionActivity(){
        //通过Intent传递语音识别的模式
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //语言模式：自由形式的语音识别
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //提示语音开始
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, SPEECH_PROMPT);
        //开始执行我们的Intent、语音识别  并等待返回结果
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 确定是语音识别activity返回的结果
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE){
            // 确定返回结果的状态是成功
            if (resultCode == RESULT_OK){
                // 获取语音识别结果
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                startDisplayMessageActivity(matches);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 启动展示activity，显示识别结果
     * @param message
     */
    private void startDisplayMessageActivity(ArrayList<String> strList){
        for(int i=0;i<strList.size();i++){
            if (strList.get(i).indexOf("救命")!=-1 ){
                call();
            }
        }
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        intent.putExtra(EXTRA_MESSAGE, strList);
//        startActivity(intent);
    }
    public void call() {

        //激活可以打电话的组件
        Intent intent = new Intent();
        intent.setAction("Android.intent.action.CALL");
        //intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("tel:123"));
        startActivity(intent);//方法内部自动添加android.intent.category.DEFAULT
    }
}