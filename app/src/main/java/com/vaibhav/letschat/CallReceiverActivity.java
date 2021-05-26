package com.vaibhav.letschat;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vaibhav.letschat.service.CallReceiverService;

public class CallReceiverActivity extends AppCompatActivity {

    private static final String TAG = "CallReceiverActivity";
    String roomName, callerName;
    int callType;
    TextView callTv;
    FloatingActionButton connectCall, disconnectCall;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_receiver);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
            setShowWhenLocked(true);
            setTurnScreenOn(true);

            // If you want to display the keyguard to prompt the user to unlock the phone:
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        } else {
            // For older versions, do it as you did before.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        roomName = getIntent().getStringExtra("roomName");
        callerName = getIntent().getStringExtra("callerName");
        callType = getIntent().getIntExtra("callType", OneToOneCallActivity.CALL_TYPE_VIDEO);
        Log.d(TAG, "onCreate: ");

        callTv = findViewById(R.id.call_tv);
        connectCall = findViewById(R.id.connect_action_fab);
        disconnectCall = findViewById(R.id.disconnect_action_fab);
        callTv.setText(callerName + " Calling...");
        initUI();
    }

    private void initUI() {
        connectCall.show();
        connectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCall
                Intent startCall = new Intent(CallReceiverActivity.this, OneToOneCallActivity.class);
                startCall.putExtra("roomName", roomName);
                startCall.putExtra("callerName", callerName);
                startCall.putExtra("callType", callType);
                startActivity(startCall);
                clearForegroundNotification();
                finish();
            }
        });
        disconnectCall.show();
        disconnectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //endCall
                clearForegroundNotification();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        clearForegroundNotification();
        super.onDestroy();
    }

    private void clearForegroundNotification() {
        Intent myService = new Intent(CallReceiverActivity.this, CallReceiverService.class);
        stopService(myService);
    }
}