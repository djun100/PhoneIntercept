package com.cy.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.cy.util.Log;


public class PhoneReceiver extends BroadcastReceiver {
    private static boolean incomingFlag = false;

    private static OnPhoneListener mOnPhoneListener;
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.e(UPhone.LOG_PHONE,action);

        Log.printBundle(intent.getExtras());

        //拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;

            String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String phoneNum2=getResultData();//得到外拔电话

            String reSetedPhoneNumber= mOnPhoneListener.onOutgoingCall(phoneNum);

            if (reSetedPhoneNumber==null){
                setResultData(null); //清除电话，广播被传给系统的接收者后，因为电话为null，取消电话拔打
            }else if (!TextUtils.isEmpty(reSetedPhoneNumber)){
                 setResultData(reSetedPhoneNumber);
            }
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            int callState=tm.getCallState();
            Log.w(UPhone.LOG_PHONE,"callState:"+callState);

            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    final PhoneStateListener listener=new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;
                    Log.i(UPhone.LOG_PHONE, "CALL IN RINGING :" + incomingNumber);
//                    UPhone.rejectCall();
                    mOnPhoneListener.onRing(incomingNumber);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                        Log.i(UPhone.LOG_PHONE, "CALL IN ACCEPT :" + incomingNumber);
                        mOnPhoneListener.onCallInAccept(incomingNumber);
                    }
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                        Log.i(UPhone.LOG_PHONE, "CALL IDLE");
                        mOnPhoneListener.onCallInHangUp();
                    }
                    break;
            }
        }
    };

    static void setOnPhoneListener(OnPhoneListener onPhoneListener) {
        mOnPhoneListener = onPhoneListener;
    }
}