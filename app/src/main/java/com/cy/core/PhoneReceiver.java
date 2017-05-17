package com.cy.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.cy.app.Log;

public class PhoneReceiver extends BroadcastReceiver {
    private static boolean incomingFlag = false;
//    private String incomingNumber;
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.e("PhoneListener",action);

        com.cy.app.Log.printBundle(intent.getExtras());

        //拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
            final String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.w( "out going call,dialing phoneNum: " + phoneNum);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            int callState=tm.getCallState();
            com.cy.app.Log.w("callState:"+callState);

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
                    Log.i( "CALL IN RINGING :" + incomingNumber);
//                    new IncomingPresenter().rejectCall();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                        Log.i( "CALL IN ACCEPT :" + incomingNumber);
                    }
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                        Log.i( "CALL IDLE");
                    }
                    break;
            }
        }
    };
}