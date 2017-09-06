package com.singun.openvpn.eventbus;

/**
 * Created by Administrator on 2016/10/31.
 */
public class ClearBackTaskEvent {
    private String mMsg;
    public ClearBackTaskEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }
}
