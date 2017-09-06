package com.singun.openvpn.eventbus;

/**
 * Created by Administrator on 2016/10/31.
 */
public class StrategyChangeEvent {
    private String mMsg;
    public StrategyChangeEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }
}
