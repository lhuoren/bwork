package com.singun.openvpn.client;
import net.tsz.afinal.FinalBitmap;
import android.app.Application;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;


/**
 * Created by singun on 2017/3/4 0004.
 */

public class MainApplication extends ICSOpenVPNApplication {
    private static MainApplication instacne = null;
    public FinalBitmap fb;
    @Override
    public void onCreate() {
        super.onCreate();
        fb = FinalBitmap.create(getApplicationContext());
        instacne = this;

    }

    public static MainApplication getInstance() {
        return instacne;
    }
}
