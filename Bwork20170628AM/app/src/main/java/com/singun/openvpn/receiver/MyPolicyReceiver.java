package com.singun.openvpn.receiver;


        import android.app.admin.DeviceAdminReceiver;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;
        import android.content.BroadcastReceiver;
public class MyPolicyReceiver extends DeviceAdminReceiver {

    private static final String PREFIX = MyPolicyReceiver.class.getSimpleName() + ": ";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Admin.logger(PREFIX, "action: " + intent.getAction(), Log.DEBUG);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Admin.logger(PREFIX, "onEnabled()", Log.INFO);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Admin.logger(PREFIX, "onDisabled()", Log.INFO);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Admin.logger(PREFIX, "onDisableRequested()", Log.INFO);
//        return super.onDisableRequested(context, intent);
        return null;
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Admin.logger(PREFIX, "onPasswordSucceeded()", Log.INFO);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Admin.logger(PREFIX, "onPasswordFailed()", Log.INFO);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Admin.logger(PREFIX, "onPasswordChanged()", Log.INFO);
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);
        Admin.logger(PREFIX, "onPasswordExpiring()", Log.INFO);
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        super.onLockTaskModeEntering(context, intent, pkg);
        Admin.logger(PREFIX, "onLockTaskModeEntering()", Log.INFO);
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
        Admin.logger(PREFIX, "onLockTaskModeExiting()", Log.INFO);
    }



    /**
     * 获取ComponentName，DevicePolicyManager的大多数方法都会用到
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), MyPolicyReceiver.class);
    }
}