package com.cy.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.cy.io.UtilLog;

/**
 * 1、onReceive中，String action = intent.getAction();取到的值因手机而异，
 * infinix全为android.intent.action.PHONE_STATE，
 * 360手机外拨的时候可以取到android.intent.action.NEW_OUTGOING_CALL，
 * 其他都为android.intent.action.PHONE_STATE
 * 2、intent.getExtras()取到的值因手机而异，infix与360取到的值分别为：
 //outcall:          incoming_number:10010;state:OFFHOOK;
 //outcall hungup:   incoming_number:10010;state:IDLE;
 //callin:           incoming_number:17640396946;state:RINGING;
 //callin idle:      incoming_number:17640396946;state:IDLE;
 //360 outcall:      android.intent.extra.PHONE_NUMBER:10010;
 3、infinix不可以获取外拨电话，360手机可以通过
 String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
 String phoneNum2=getResultData();//得到外拔电话
 360手机可以通过setResultData(reSetedPhoneNumber);重设外拨号码，传null则挂断外拨电话
 4、可以通过TelephonyManager实例而不通过listener直接获取呼入呼出状态
 TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
 int callState = tm.getCallState();
 //outcall:          callState:CALL_STATE_OFFHOOK 2
 //outcall hungup:   callState:CALL_STATE_IDLE 0
 //callin:           callState:CALL_STATE_RINGING 1
 //callin idle:      callState:CALL_STATE_IDLE 0
 */
public class PhoneReceiver extends BroadcastReceiver {
    private static boolean incomingFlag = false;

    private static OnPhoneListener mOnPhoneListener;
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }
    final PhoneStateListener listener=new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String number) {
            super.onCallStateChanged(state, number);
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;
                    //CALL IN RINGING :17640396946
                    UtilLog.e( "CALL IN RINGING :" + number);
//                    UPhone.rejectCall();
                    if (mOnPhoneListener!=null) {
                        mOnPhoneListener.onRing(number);
                    }
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                        UtilLog.e( "CALL IN ACCEPT :" + number);
                        if (mOnPhoneListener!=null) {
                            mOnPhoneListener.onCallInAccept(number);
                        }
                    }else {
                        //360手机第一次回调可以获取到，infinix获取不到
                        UtilLog.e("OUTGOING CALL:" + number);
                        if (mOnPhoneListener != null) {
                            mOnPhoneListener.onOutgoingCall(number);
                        }
                    }
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                        UtilLog.e( "CALL IN IDLE");
                        if (mOnPhoneListener!=null) {
                            mOnPhoneListener.onCallInHangUp();
                        }
                    }else {
                        UtilLog.e( "OUTCALL HANG UP");
                        if (mOnPhoneListener!=null) {
                            mOnPhoneListener.onOutCallHangUp(number);
                        }else {
                            UtilLog.e("mOnPhoneListener null");
                        }
                    }
                    break;
            }
        }
    };

    static void setOnPhoneListener(OnPhoneListener onPhoneListener) {
        mOnPhoneListener = onPhoneListener;
    }
}