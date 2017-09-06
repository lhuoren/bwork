package com.singun.openvpn.util;

import android.content.ContentResolver;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

public class UserUtils {
    public static int myUserId() {
        int userId = 0;
        try {
            userId = ReflectUtils.callStaticObjectMethod(UserHandle.class,
                    int.class, "myUserId", new Class[]{}, new Object[]{});
        } catch (Exception e) {
            Log.d("UserUtils", "Failed to get myUserId", e);
        }
        return userId;
    }

    public static int getSecuritySpaceId(Context context) {
        int userId = 0;
        try {
            userId = ReflectUtils.callStaticObjectMethod(Settings.Secure.class,
                    int.class, "getIntForUser",
                    new Class[]{ContentResolver.class, String.class, int.class, int.class},
                    new Object[]{context.getContentResolver(), "second_user_id", 0, 0});
        } catch (Exception e) {
            Log.d("UserUtils", "Failed to getSecuritySpaceId", e);
        }
        return userId;
    }
}
