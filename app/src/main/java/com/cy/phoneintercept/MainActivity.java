package com.cy.phoneintercept;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.cy.core.OnPhoneListener;
import com.cy.core.UPhone;
import com.cy.utils.UPermission;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends Activity {

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
                UPermission.startSettings(MainActivity.this,0);
            }
        });

        setPhoneListener();
    }

    private void setPhoneListener() {
        UPhone.setOnPhoneListener(new OnPhoneListener() {
            @Override
            public String onOutgoingCall(String incomingNumber) {
                return OnPhoneListener.V_CANCEL_DIAL;
            }

            @Override
            public void onRing(String incomingNumber) {

            }

            @Override
            public void onCallInAccept(String incomingNumber) {

            }

            @Override
            public void onCallInHangUp() {

            }
        });
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

}
