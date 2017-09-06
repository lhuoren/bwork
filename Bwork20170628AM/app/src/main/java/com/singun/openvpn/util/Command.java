package com.singun.openvpn.util;

import android.util.Log;

import java.io.IOException;

/**
 * Created by access on 2017/7/13.
 */

public class Command {
    final private static String TAG = "Command";
    public static void command(String com) {
        try {
            Log.i(TAG, "Command : " + com);
            Runtime.getRuntime().exec(com);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
