package com.cy.phoneintercept;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.cy.app.UtilContext;
import com.cy.io.Log;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends Activity {
    final PhoneStateListener mPhoneStateListener=new MyPhoneListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.mbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermission();
            }
        });

        findViewById(R.id.mbtnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UPermission.startSettings(MainActivity.this,0);
            }
        });

        findViewById(R.id.mbtnLaunchApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MyPhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.w("state:"+state+" incomingNumber:"+incomingNumber);
        }
    }

    private void doPermission() {
        AndPermission.with(MainActivity.this)
                .requestCode(100)
                .permission(Manifest.permission.PROCESS_OUTGOING_CALLS)
//                        .rationale(...)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        Toast.makeText(MainActivity.this, "phone组有权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "phone组无权限", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    /**
     * 启动App
     */
    public static void launchApp(String packageName) {
        Context context = UtilContext.getContext();
        // 判断是否安装过App，否则去市场下载
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.w("打开其他APP");
        }

    }
}
