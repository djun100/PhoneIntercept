package com.cy.core;

/**
 * Created by wangxuechao on 2017/5/18.
 */

public abstract class OnPhoneListener {
    public static final String V_GO_ON="";
    public static final String V_CANCEL_DIAL=null;
    /**
     * @param incomingNumber
     * @return  1、""不处理     2、null取消拨打电话    3、其他值更改拨打的号码
     */
    public String onOutgoingCall(String incomingNumber){
        return V_GO_ON;
    }
    public abstract void onRing(String incomingNumber);
    public abstract void onCallInAccept(String incomingNumber);
    public abstract void onCallInHangUp();
}
