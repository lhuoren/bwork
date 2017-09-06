package com.singun.openvpn.eventbus;

/**
 * Created by access on 2017/8/17.
 */

public class CheckGPSEvent {
    private String mMsg;
    public CheckGPSEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }
}
