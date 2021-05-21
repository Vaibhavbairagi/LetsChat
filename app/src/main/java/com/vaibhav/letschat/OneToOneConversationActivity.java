package com.vaibhav.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationListener;
import com.twilio.conversations.ErrorInfo;
import com.twilio.conversations.Message;
import com.twilio.conversations.Participant;
import com.vaibhav.letschat.adapters.ConversationsMessageRVAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OneToOneConversationActivity extends AppCompatActivity implements ConversationListener {

    private static final String TAG = "OneToOneConversation";
    ArrayList<Message> messages = new ArrayList<>();
    String sid;
    TextView convoNameTv;
    ImageView backButtonIv, voiceCallIv, videoCallIv, convoImgIv;
    RecyclerView msgRecyclerView;
    Conversation conversation;
    Context context;
    ConversationsMessageRVAdapter conversationsMessageRVAdapter;
    EditText sendMsgTextET;
    ImageButton sendIBtn, attachIBtn;
    ProgressBar topProgress;

    boolean isOldMessagesLoading = false;

    private final int LOAD_MESSAGE_COUNT = 30;

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
        sendIBtn = findViewById(R.id.ib_otoc_send_msg);
        attachIBtn = findViewById(R.id.ib_otoc_attach);
        sendMsgTextET = findViewById(R.id.et_otoc_send_msg_text);
        topProgress = findViewById(R.id.progress_top_otoc);

        conversationsMessageRVAdapter = new ConversationsMessageRVAdapter(messages, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(conversationsMessageRVAdapter);

        AppController.getInstance().getConversationsClient().getConversation(sid, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation result) {
                conversation = result;
                initPageSetup();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.d(TAG, "onError: " + errorInfo);
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
        convoNameTv.setText(conversation.getFriendlyName());
        conversation.addListener(this);
        loadLastMessages();
        sendIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { ;
                String msg = sendMsgTextET.getText().toString();
                if (!msg.isEmpty()) {
                    sendMsgTextET.setText("");
                    sendMessage(msg);
                } else {
                    Toast.makeText(context, "Enter a msg to send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String messageBody) {
        if (conversation != null) {
            Message.Options options = Message.options().withBody(messageBody);
            Log.d(TAG, "Message created");
            conversation.sendMessage(options, new CallbackListener<Message>() {
                @Override
                public void onSuccess(Message message) {
                    Log.d(TAG, "Message sent");
                    //todo: implement ticks
                }
            });
        }
    }

    private void loadLastMessages() {
        topProgress.setVisibility(View.VISIBLE);
        //gets last 30 messages
        conversation.getLastMessages(LOAD_MESSAGE_COUNT, new CallbackListener<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                //todo; add all message to start
                if (result.size() > 0) {
                    topProgress.setVisibility(View.GONE);
                    messages.addAll(result);
                    conversationsMessageRVAdapter.notifyDataSetChanged();
                    initScrollUpToLoadMoreFeature();
                }
            }
        });
    }

    private void initScrollUpToLoadMoreFeature() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            msgRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 < i3) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) msgRecyclerView.getLayoutManager();

                        if (!isOldMessagesLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                loadPreviousMessages();
                                isOldMessagesLoading = true;
                            }
                        }
                    }
                }
            });
        } else {
            msgRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy < 0) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) msgRecyclerView.getLayoutManager();

                        if (!isOldMessagesLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                loadPreviousMessages();
                                isOldMessagesLoading = true;
                            }
                        }
                    }
                }
            });
        }
    }

    private void loadPreviousMessages() {
        if (messages.get(0).getMessageIndex() > 0){
            conversation.getMessagesBefore(messages.get(0).getMessageIndex()-1, LOAD_MESSAGE_COUNT, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> result) {
                    isOldMessagesLoading = false;
                    topProgress.setVisibility(View.GONE);
                    result.addAll(messages);
                    messages.clear();
                    messages.addAll(result);
                    conversationsMessageRVAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onMessageAdded(final Message message) {
        Log.d(TAG, "Message added");
        messages.add(message);
        conversationsMessageRVAdapter.notifyDataSetChanged();
        msgRecyclerView.smoothScrollToPosition(messages.size()-1);
    }

    @Override
    public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
        Log.d(TAG, "Message updated: " + message.getMessageBody());
    }

    @Override
    public void onMessageDeleted(Message message) {
        Log.d(TAG, "Message deleted");
    }

    @Override
    public void onParticipantAdded(Participant participant) {
        Log.d(TAG, "Participant added: " + participant.getIdentity());
    }

    @Override
    public void onParticipantUpdated(Participant participant, Participant.UpdateReason updateReason) {
        Log.d(TAG, "Participant updated: " + participant.getIdentity() + " " + updateReason.toString());
    }

    @Override
    public void onParticipantDeleted(Participant participant) {
        Log.d(TAG, "Participant deleted: " + participant.getIdentity());
    }

    @Override
    public void onTypingStarted(Conversation conversation, Participant participant) {
        Log.d(TAG, "Started Typing: " + participant.getIdentity());
    }

    @Override
    public void onTypingEnded(Conversation conversation, Participant participant) {
        Log.d(TAG, "Ended Typing: " + participant.getIdentity());
    }

    @Override
    public void onSynchronizationChanged(Conversation conversation) {

    }
}