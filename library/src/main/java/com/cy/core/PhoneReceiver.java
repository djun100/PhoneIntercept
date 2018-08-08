package com.cy.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.cy.io.Log;
import com.cy.io.UtilLog;

/**
 * 1、onReceive中，String action = intent.getAction();取到的值因手机而异，
 * infinix全为android.intent.action.PHONE_STATE，
 * 360手机外拨的时候可以取到android.intent.action.NEW_OUTGOING_CALL，
 * 其他都为android.intent.action.PHONE_STATE
 2、360手机呼出为有序广播，可以通过
 String phoneNum2=getResultData();//得到外拔电话
 通过setResultData(reSetedPhoneNumber);重设外拨号码，传null则挂断外拨电话
 4、可以通过TelephonyManager实例而不通过listener直接获取呼入呼出状态
 TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
 int callState = tm.getCallState();
 //outcall:          callState:CALL_STATE_OFFHOOK 2
 //outcall hungup:   callState:CALL_STATE_IDLE 0

 //callin:           callState:CALL_STATE_RINGING 1
 //callin accept:    callState:CALL_STATE_OFFHOOK 2
 //callin hungup:    callState:CALL_STATE_IDLE 0

 如下方式不可与广播一起用，功能重复
 TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
 tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
 */
public class PhoneReceiver extends BroadcastReceiver {
    private static boolean incomingFlag = false;
    private Context mContext;
    public static final String PHONE_EVENT_RING = "ring";
    public static final String PHONE_EVENT_CALL_IN_ACCEPT = "CALL IN ACCEPT";
    public static final String PHONE_EVENT_OUTGOING_CALL = "OUTGOING CALL";
    public static final String PHONE_EVENT_CALLIN_HANGUP = "CallInHangUp";
    public static final String PHONE_EVENT_OUTCALL_HANGUP = "OutCallHangUp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("intent:" + UtilLog.intentToString(intent));
        mContext = context;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incoming_number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (state != null && incoming_number != null) {
            handleState(state, incoming_number);
        }
    }

    private void handleState(String state, String number) {

        switch (state) {
            //电话等待接听
            case "RINGING":
                incomingFlag=true;
                UtilLog.e("CALL IN RINGING :" + number);

                Bundle bundle = new Bundle();
                bundle.putString("event", PHONE_EVENT_RING);
                bundle.putString("number", number);
                MyService.startFloatWindowService(mContext, bundle);

                break;

            case "OFFHOOK":
                if (incomingFlag) {
                    //电话接听
                    UtilLog.e("CALL IN ACCEPT :" + number);

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("event", PHONE_EVENT_CALL_IN_ACCEPT);
                    bundle1.putString("number", number);
                    MyService.startFloatWindowService(mContext, bundle1);

                } else {
                    //外拨电话 有的手机有时候会调用两次
                    UtilLog.e("OUTGOING CALL:" + number);

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("event", PHONE_EVENT_OUTGOING_CALL);
                    bundle2.putString("number", number);
                    MyService.startFloatWindowService(mContext, bundle2);
                }
                break;
            //电话挂机
            case "IDLE":
                if (incomingFlag) {
                    incomingFlag = false;
                    UtilLog.e("CALL IN IDLE number:" + number);

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("event", PHONE_EVENT_CALLIN_HANGUP);
                    bundle2.putString("number", number);
                    MyService.startFloatWindowService(mContext, bundle2);

                } else {
                    UtilLog.e("OUTCALL HANG UP number:" + number);

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("event", PHONE_EVENT_OUTCALL_HANGUP);
                    bundle2.putString("number", number);
                    MyService.startFloatWindowService(mContext, bundle2);

                }
                break;
        }
    }
}