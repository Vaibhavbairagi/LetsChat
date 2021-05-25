package com.vaibhav.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CallReceiverActivity extends AppCompatActivity {

    private static final String TAG = "CallReceiverActivity";
    String roomName, callerName;
    int callType;
    TextView callTv;
    FloatingActionButton connectCall, disconnectCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_receiver);

        roomName = getIntent().getStringExtra("roomName");
        callerName = getIntent().getStringExtra("callerName");
        callType = getIntent().getIntExtra("callType",OneToOneCallActivity.CALL_TYPE_VIDEO);
        Log.d(TAG, "onCreate: ");

        callTv = findViewById(R.id.call_tv);
        connectCall = findViewById(R.id.connect_action_fab);
        disconnectCall = findViewById(R.id.disconnect_action_fab);
        callTv.setText(callerName+" Calling...");
        initUI();
    }

    private void initUI() {
        connectCall.show();
        connectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCall
                Intent startCall = new Intent(CallReceiverActivity.this,OneToOneCallActivity.class);
                startCall.putExtra("roomName",roomName);
                startCall.putExtra("callerName",callerName);
                startCall.putExtra("callType", callType);
                startActivity(startCall);
                finish();
            }
        });
        disconnectCall.show();
        disconnectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //endCall
                finish();
            }
        });
    }
}