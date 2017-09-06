package com.singun.openvpn.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;

import com.singun.openvpn.client.R;
import com.singun.openvpn.mvp.pushservice.PushServiceContract;
import com.singun.openvpn.mvp.pushservice.PushServicePresenter;
import com.singun.openvpn.util.ACache;
import com.singun.openvpn.util.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;


/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class PushIntentService extends GTIntentService implements PushServiceContract.View {

    private PushServicePresenter pushServicePresenter;
    private NotificationManager manager;
    private final String APK_NAME = "abhuitong.apk";
    private boolean autoDownLoad = false;

    public  final static String pushResetAction="com.singun.openvpn.service.pushService_reset";
    public  final static String pushReloadAction="com.singun.openvpn.service.pushService_reload";

    /**
     * 为了观察透传数据变化.
     */
//    private static int cnt;
    public PushIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        LogUtil.i(TAG, "onReceiveMessageData msg-> " + msg);
        try {
            //接受后台透传信息
            String data = new String(msg.getPayload());
            final JSONObject obj = new JSONObject(data);
            String actionName = obj.optString("ActionName");
            LogUtil.i(TAG, "onReceiveMessageData -> " + actionName);

            //接收后进行事件分发
            switch (actionName) {

                //更新APP操作
                case "UpdateOwnerApp":
                    manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    //hongyang大神封装好的okHttp网络请求
                    //启动分线程下载
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                okHttpDownLoadApk(obj.optJSONObject("Content").optString("DownLoadUrl"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;

                //更新桌面操作
                case "CanShowApp":
                    Intent intent = new Intent();
                    intent.setAction(pushReloadAction);
                    String APPName = "";
                    JSONArray array = obj.optJSONObject("Content").optJSONArray("PkgName");
                    for (int i = 0; i < array.length(); i++) {
                        APPName = APPName + array.optString(i) + ",";
                    }
                    intent.putExtra("data", APPName.substring(0, APPName.length() - 1));
                    sendBroadcast(intent);
                    break;

                //强制恢复出厂设置
                case "FactoryReset":
                    sendBroadcast(new Intent(pushResetAction));
                    break;
            }

            String cid = msg.getClientId();
            Log.e("pushData", "pushData:" + data + ",clientId:" + cid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveClientId(Context context,final String clientID) {
        Log.e("clientId", "clientId:" + clientID);
        pushServicePresenter = new PushServicePresenter(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ACache mCache = ACache.get(getApplicationContext());
                    pushServicePresenter.postSavePushClinetID(mCache.getAsString("userId"), clientID, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if (action == PushConsts.BIND_ALIAS_RESULT) {
            bindAliasResult((BindAliasCmdMessage) cmdMessage);
        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
            unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }


    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();


        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:

                break;

            case PushConsts.SETTAG_ERROR_COUNT:

                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:

                break;

            case PushConsts.SETTAG_ERROR_REPEAT:

                break;

            case PushConsts.SETTAG_ERROR_UNBIND:

                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:

                break;

            case PushConsts.SETTAG_ERROR_NULL:

                break;

            case PushConsts.SETTAG_NOTONLINE:

                break;

            case PushConsts.SETTAG_IN_BLACKLIST:

                break;

            case PushConsts.SETTAG_NUM_EXCEED:

                break;

            default:
                break;
        }


    }

    private void bindAliasResult(BindAliasCmdMessage bindAliasCmdMessage) {
        String sn = bindAliasCmdMessage.getSn();
        String code = bindAliasCmdMessage.getCode();


        switch (Integer.valueOf(code)) {
            case PushConsts.BIND_ALIAS_SUCCESS:

                break;
            case PushConsts.ALIAS_ERROR_FREQUENCY:

                break;
            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:

                break;
            case PushConsts.ALIAS_REQUEST_FILTER:

                break;
            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:

                break;
            case PushConsts.ALIAS_CID_LOST:

                break;
            case PushConsts.ALIAS_CONNECT_LOST:

                break;
            case PushConsts.ALIAS_INVALID:

                break;
            case PushConsts.ALIAS_SN_INVALID:

                break;
            default:
                break;

        }


    }

    private void unbindAliasResult(UnBindAliasCmdMessage unBindAliasCmdMessage) {
        String sn = unBindAliasCmdMessage.getSn();
        String code = unBindAliasCmdMessage.getCode();

        switch (Integer.valueOf(code)) {
            case PushConsts.UNBIND_ALIAS_SUCCESS:
                ;
                break;
            case PushConsts.ALIAS_ERROR_FREQUENCY:

                break;
            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:

                break;
            case PushConsts.ALIAS_REQUEST_FILTER:

                break;
            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:

                break;
            case PushConsts.ALIAS_CID_LOST:

                break;
            case PushConsts.ALIAS_CONNECT_LOST:

                break;
            case PushConsts.ALIAS_INVALID:

                break;
            case PushConsts.ALIAS_SN_INVALID:

                break;
            default:
                break;

        }


    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private void sendMessage(String data, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = data;

    }

    @Override
    public void postSavePushClinetIDError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postSavePushClinetIDSuccess() {

    }

    @Override
    public void setPresenter(PushServiceContract.Present presenter) {

    }

    int currentProgress = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    //显示 Notification
                    showNotificationProgress(msg.arg1);
                    break;
                case 2:
                    //安装apk
                    installApk();
                    break;
            }
        }
    };


    /**
     * 联网下载最新版本apk
     */
    private void okHttpDownLoadApk(final String url) {
        OkHttpUtils
                .get()
                .url(url)
                .build()// Environment.getExternalStorageDirectory().getAbsolutePath() 存储路径
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), APK_NAME) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        //Log.e(TAG, "onResponse() 当前线程 == " + Thread.currentThread().getName());
                        //Log.e(TAG, "onResponse :" + response.getAbsolutePath());
                    }

                    @Override
                    public void inProgress(final float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        //Log.e(TAG, "inProgress() 当前线程 == " + Thread.currentThread().getName());
                        if (autoDownLoad) {

                            if ((100 * progress) == 100.0) {
                                //Log.e(TAG, "网络请求 自动安装 当前线程 == " + Thread.currentThread().getName());
                                handler.sendEmptyMessage(2);
                            }

                        } else {//否则 进度条下载

                            int pro = (int) (100 * progress);

                            //解决pro进度重复传递 progress的问题 这里解决UI界面卡顿问题
                            if (currentProgress < pro && pro <= 100) {
                                //Log.e(TAG, "进入");

                                currentProgress = pro;

                                Message msg = new Message();
                                msg.what = 1;
                                msg.arg1 = currentProgress;
                                handler.sendMessage(msg);
                            }
                        }

                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotificationProgress(int progress) {
        //Log.e(TAG, "进度 == " + progress);
        String message = "当前下载进度: " + progress + "%";
        int AllProgress = 100;


        Intent intent = null;
        if (progress == 100) {
            message = "下载完毕，点击安装";
            //Log.e(TAG, "下载完成 " + progress);

            //安装apk
            installApk();
            if (manager != null) {
                manager.cancel(0);//下载完毕 移除通知栏
            }

            //当进度为100%时 传入安装apk的intent
            File fileLocation = new File(Environment.getExternalStorageDirectory(), APK_NAME);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(fileLocation), "application/vnd.android.package-archive");
        }
        if (intent == null) {
            intent = new Intent(); //暂时没有更好的办法 保证不能为空即可
        }
        //表示返回的PendingIntent仅能执行一次，执行完后自动取消
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)//App小的图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))//App大图标
                .setContentTitle("下载中")//设置通知的信息
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setContentText(message)
                .setAutoCancel(false)//用户点击后自动删除
                //.setDefaults(Notification.DEFAULT_LIGHTS)//灯光
                .setPriority(Notification.PRIORITY_DEFAULT)//设置优先级
                .setOngoing(true)
                .setProgress(AllProgress, progress, false) //AllProgress最大进度 //progress 当前进度
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);
    }


    /**
     * 安装apk
     */
    private void installApk() {
        //Environment.getExternalStorageDirectory() 保存的路径
        Log.e(TAG, "installApk运行了");
        File fileLocation = new File(Environment.getExternalStorageDirectory(), APK_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(fileLocation), "application/vnd.android.package-archive");
        startActivity(intent);


    }


}
