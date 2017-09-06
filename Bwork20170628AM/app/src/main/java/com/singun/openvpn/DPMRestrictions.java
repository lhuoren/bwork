package com.singun.openvpn;


        import android.app.admin.DevicePolicyManager;
        import android.content.ComponentName;
        import android.content.Context;
        import android.os.Bundle;
        import android.os.UserManager;
        import android.net.ProxyInfo;

        import com.singun.openvpn.receiver.MyPolicyReceiver;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;


public class DPMRestrictions {

    public static boolean isRestricted(Context context, String key) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        return userManager.hasUserRestriction(key);
    }

    public static void setUserRestriction(Context context, String key, boolean restriction) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyPolicyReceiver.class);
        if (restriction) {
            manager.addUserRestriction(componentName, key);
        } else {
            manager.clearUserRestriction(componentName, key);
        }
    }
}
