package com.cy.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.cy.io.Log;
import com.cy.phoneintercept.library.R;

public class MyService extends Service {
    public static final String BUNDLE_KEY_EVENT="event";
    public static final String BUNDLE_KEY_NUMBER="number";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public static void startFloatWindowService(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MyService.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            return;
        }
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopService(Context context){
        Intent intent = new Intent(context, MyService.class);
        context.stopService(intent);
        Log.w("主动停止service");
    }

    public static void bindService(Context context){
        context=context.getApplicationContext();
        Intent intent = new Intent(context, MyService.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        },Context.BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotificationIfNeed();
        Log.w("在onStartCommand中显示通知");
        if (intent != null) {
            Bundle bundle=intent.getExtras();
            if (bundle!=null) {
                String event = bundle.getString(BUNDLE_KEY_EVENT);
                String number = bundle.getString(BUNDLE_KEY_NUMBER);
                Log.w(event);
                if (!TextUtils.isEmpty(event)){
                    if (event.equals(PhoneReceiver.PHONE_EVENT_RING)){
                        onRing(number);
                    }else if (event.equals(PhoneReceiver.PHONE_EVENT_CALL_IN_ACCEPT)){
                        onCallInAccept(number);
                    }else if (event.equals(PhoneReceiver.PHONE_EVENT_CALLIN_HANGUP)){
                        onCallInHangUp(number);
                    }else if (event.equals(PhoneReceiver.PHONE_EVENT_OUTGOING_CALL)){
                        onOutgoingCall(number);
                    }else if (event.equals(PhoneReceiver.PHONE_EVENT_OUTCALL_HANGUP)){
                        onOutCallHangUp(number);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("MyService onCreate");
    }

    public void onRing(String number) {

    }

    public void onCallInAccept(String number) {

    }

    public void onCallInHangUp(String number) {

    }

    public String onOutgoingCall(String number) {
        return "";
    }

    public void onOutCallHangUp(String number) {

    }

    private void startNotificationIfNeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("im_channel_id",
                    "System", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);

            Notification notification = new Notification
                    .Builder(this,"im_channel_id")
                    .setSmallIcon(R.drawable.notification)  // the status icon
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentText("服务正在运行")  // the contents of the entry
                    .build();
            startForeground(1, notification);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
