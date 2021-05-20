package com.vaibhav.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ErrorInfo;
import com.twilio.conversations.Message;

import java.util.ArrayList;
import java.util.List;

public class OneToOneConversationActivity extends AppCompatActivity {

    private static final String TAG = "OneToOneConversation";
    ArrayList<Message> messages = new ArrayList<>();
    String sid;
    TextView convoNameTv;
    ImageView backButtonIv, voiceCallIv, videoCallIv, convoImgIv;
    RecyclerView msgRecyclerView;
    Conversation c;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_conversation);
        sid = getIntent().getExtras().getString("conversationSid");
        context = this;
        backButtonIv = findViewById(R.id.iv_otoc_back);
        voiceCallIv = findViewById(R.id.iv_otoc_voiceCall);
        videoCallIv = findViewById(R.id.iv_otoc_videoCall);
        convoNameTv = findViewById(R.id.iv_otoc_convoName);
        convoImgIv = findViewById(R.id.iv_otoc_profile_image);
        msgRecyclerView = findViewById(R.id.messageRecyclerView);

        AppController.getInstance().getConversationsClient().getConversation(sid, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation result) {
                c = result;
                initPageSetup();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.d(TAG, "onError: "+errorInfo);
                Toast.makeText(context, "Failed to start conversation, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPageSetup() {
        backButtonIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //todo: change this to display the other user name
        convoNameTv.setText(c.getFriendlyName());
        //gets last 30 messages
        c.getLastMessages(30, new CallbackListener<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                messages.addAll(result);
                //setup recycler view now
            }
        });
    }
}