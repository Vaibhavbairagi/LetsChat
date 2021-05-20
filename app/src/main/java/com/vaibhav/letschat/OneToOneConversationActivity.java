package com.vaibhav.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OneToOneConversationActivity extends AppCompatActivity {

    String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_conversation);
        sid = getIntent().getExtras().getString("conversationSid");


    }
}