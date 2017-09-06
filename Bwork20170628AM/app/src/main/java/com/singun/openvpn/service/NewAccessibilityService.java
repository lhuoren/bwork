package com.singun.openvpn.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import com.singun.openvpn.util.LogUtil;

/**
 * Created by Administrator on 2017-07-15.
 */

public class NewAccessibilityService extends AccessibilityService {
    private static NewAccessibilityService myAccessibilityServiceInstance;
    public AccessibilityEvent mEvent;
    private AccessibilityServiceInfo accessibilityServiceInfo;
    public static NewAccessibilityService getInstance(){
        return myAccessibilityServiceInstance;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mEvent = event;
        LogUtil.i("onAccessibilityEvent",event);
    }

    @Override
    public void onInterrupt() {
        LogUtil.i("onInterrupt","onInterrupt");
    }
    @Override
    public boolean onUnbind(Intent intent) {
        myAccessibilityServiceInstance = null;
        return super.onUnbind(intent);
    }
    @Override
    public void onServiceConnected() {
        myAccessibilityServiceInstance = NewAccessibilityService.this;
        LogUtil.i("onServiceConnected",myAccessibilityServiceInstance);
        accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = 100;
        accessibilityServiceInfo.packageNames = new String[]{"com.singun.openvpn.client"};
        setServiceInfo(accessibilityServiceInfo);
        super.onServiceConnected();
    }

    public boolean clickRECENTS() {
        boolean b =performGlobalAction(GLOBAL_ACTION_RECENTS);
        getServiceInfo().flags = accessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        return  b;
    }

    public boolean clickBACK() {
        boolean back =performGlobalAction(GLOBAL_ACTION_BACK);
        getServiceInfo().flags = accessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        return  back;
    }

    public boolean clickHome() {
        boolean home =performGlobalAction(GLOBAL_ACTION_HOME);
        getServiceInfo().flags = accessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        return  home;
    }

}