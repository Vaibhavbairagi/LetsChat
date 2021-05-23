package com.vaibhav.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.ConversationsClientListener;
import com.twilio.conversations.ErrorInfo;
import com.twilio.conversations.StatusListener;
import com.twilio.conversations.User;
import com.vaibhav.letschat.adapters.ConversationsListRVAdapter;
import com.vaibhav.letschat.api.AccessTokenResponse;
import com.vaibhav.letschat.api.ChatAPI;
import com.vaibhav.letschat.listeners.OnConversationClickedListener;
import com.vaibhav.letschat.utils.AppPreferences;
import com.vaibhav.letschat.utils.ConversationsPreferences;
import com.vaibhav.letschat.utils.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConversationsActivity extends AppCompatActivity implements OnConversationClickedListener {

    public static final String TAG = "LetsChat";
    //Todo:change user details
    public String userId = "Hiv2", userName = "Vaibhav Bairagi";


    RelativeLayout fullscreenProgressLayout;

    ConversationsPreferences conversationsPreferences;
    AppPreferences appPreferences;
    String accessToken;

    ConversationsClient conversationsClient;
    public ArrayList<Conversation> conversations = new ArrayList<>();
    Context context;
    ConversationsListRVAdapter conversationsListRVAdapter;
    RecyclerView chatsListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationsPreferences = new ConversationsPreferences(this);
        appPreferences = new AppPreferences(this);
        accessToken = conversationsPreferences.getAccessToken();

        context = this;

        fullscreenProgressLayout = findViewById(R.id.fullscreen_progress_layout);
        chatsListRV = findViewById(R.id.rv_chats_list);

        conversationsListRVAdapter = new ConversationsListRVAdapter(conversations, context, this);
        chatsListRV.setLayoutManager(new LinearLayoutManager(this));
        chatsListRV.setAdapter(conversationsListRVAdapter);
        conversationsListRVAdapter.notifyDataSetChanged();

        //Check if the accessToken already present in the device is valid, 24hr ttl
        if (conversationsPreferences.getTokenCreationTime() == null) {
            //Token doesn't exist on device
            requestNewAccessTokenFromServer();
        } else {
            //Token exists check validity
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = Calendar.getInstance().getTime(), tokenCreationTime = new Date();
            Log.d(TAG, "onCreate: " + dateFormat.format(date));
            try {
                tokenCreationTime = dateFormat.parse(conversationsPreferences.getTokenCreationTime());
                Log.d(TAG, "onCreate: "+tokenCreationTime);
            } catch (ParseException e) {
                //Won't end up here based on design.
                e.printStackTrace();
            }
            //Token expired, request new
            Log.d(TAG, "onCreate: " + (date.getTime() - tokenCreationTime.getTime()));
            if ((date.getTime() - tokenCreationTime.getTime()) / 1000 > 86400) {
                requestNewAccessTokenFromServer();
            } else {
                //accessToken already exists use that
                accessToken = conversationsPreferences.getAccessToken();
                initializeConvClientWithAccessToken();
            }
        }
    }

    private void requestNewAccessTokenFromServer() {
        Log.d(TAG, "Requesting New Access Token From Server.");
        Retrofit retrofit = RetrofitClient.getInstance();
        ChatAPI chatAPI = retrofit.create(ChatAPI.class);

        Call<AccessTokenResponse> call = chatAPI.generateNewAccessToken(userId);
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.body() != null) {
                    accessToken = response.body().getToken();
                    conversationsPreferences.saveAccessToken(accessToken);
                    conversationsPreferences.saveTokenCreationTime(response.body().getTokenCreationTime());
                    if (conversationsPreferences.getCurrentUserId() == null) {
                        conversationsPreferences.saveCurrentUserId(userId);
                    }
                    Log.d(TAG, "Access token fetched: " + accessToken);
                    initializeConvClientWithAccessToken();
                } else {
                    Log.d(TAG, "Access token null");
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.d(TAG, "Access token fail:" + t.getMessage());
                Toast.makeText(context, "Access token couldn't be generated: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeConvClientWithAccessToken() {
        ConversationsClient.Properties props = ConversationsClient.Properties.newBuilder().createProperties();
        ConversationsClient.create(context, accessToken, props, new CallbackListener<ConversationsClient>() {
            @Override
            public void onSuccess(ConversationsClient result) {
                AppController.getInstance().setConversationsClient(result);
                conversationsClient = AppController.getInstance().getConversationsClient();
                conversationsClient.addListener(conversationsClientListener);
                if (conversationsPreferences.getCurrentUserId() == null) {
                    conversationsPreferences.saveCurrentUserId(userId);
                }
                Log.d(ConversationsActivity.TAG, "Twilio Conversation Client created successfully");
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error creating Twilio Conversations Client: " + errorInfo.getMessage());
                requestNewAccessTokenFromServer();
            }
        });
    }

    public final ConversationsClientListener conversationsClientListener = new ConversationsClientListener() {
        @Override
        public void onConversationAdded(Conversation conversation) {
            Log.d(TAG, "Conversation added: " + conversation.getSid());
        }

        @Override
        public void onConversationUpdated(Conversation conversation, Conversation.UpdateReason reason) {
            Log.d(TAG, "Conversation updated: " + conversation.getSid());
        }

        @Override
        public void onConversationDeleted(Conversation conversation) {
            Log.d(TAG, "Conversation deleted: " + conversation.getSid());
        }

        @Override
        public void onConversationSynchronizationChange(Conversation conversation) {
            Log.d(TAG, "Conversation sync change: " + conversation.getSid());
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            Log.d(TAG, "onE" +
                    "" +
                    "rror: " + errorInfo.getMessage());
        }

        @Override
        public void onUserUpdated(User user, User.UpdateReason reason) {
            Log.d(TAG, "User updated: " + user.getIdentity());
        }

        @Override
        public void onUserSubscribed(User user) {
            Log.d(TAG, "User subscribed: " + user.getIdentity());
        }

        @Override
        public void onUserUnsubscribed(User user) {
            Log.d(TAG, "User unsubscribed: " + user.getIdentity());
        }

        @Override
        public void onClientSynchronization(ConversationsClient.SynchronizationStatus status) {
            if (status == ConversationsClient.SynchronizationStatus.COMPLETED) {
                String ft = appPreferences.getFCMToken();
                String rft = conversationsPreferences.getRegisteredFCMToken();
                if (ft == null && rft == null) {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<String> task) {
                            String token = task.getResult();
                            appPreferences.saveFCMToken(token);
                            conversationsClient.registerFCMToken(new ConversationsClient.FCMToken(token), new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(ConversationsActivity.TAG, "AppPref FCM and ConvPref FCM created and updated");
                                    conversationsPreferences.saveRegisteredFCMToken(token);
                                }
                            });
                        }
                    });
                } else if (ft != null && !ft.equals(rft)) {
                    conversationsClient.registerFCMToken(new ConversationsClient.FCMToken(appPreferences.getFCMToken()), new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(ConversationsActivity.TAG, "Updated ConvPref FCM to match existing");
                            conversationsPreferences.saveRegisteredFCMToken(ft);
                        }
                    });
                }
                fetchConversationsList();
                fullscreenProgressLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNewMessageNotification(String conversationSid, String messageSid, long messageIndex) {
            Log.d(TAG, "New message notification: " + conversationSid);
        }

        @Override
        public void onAddedToConversationNotification(String conversationSid) {
            Log.d(TAG, "addedToConversation: " + conversationSid);
        }

        @Override
        public void onRemovedFromConversationNotification(String conversationSid) {
            Log.d(TAG, "Removed from conversation: " + conversationSid);
        }

        @Override
        public void onNotificationSubscribed() {
            Log.d(TAG, "Notification subscribed");
        }

        @Override
        public void onNotificationFailed(ErrorInfo errorInfo) {
            Log.d(TAG, "Notification failed: " + errorInfo.getMessage());
        }

        @Override
        public void onConnectionStateChange(ConversationsClient.ConnectionState state) {
            Log.d(TAG, "ConnectionStateChange: " + state.name());
        }

        @Override
        public void onTokenExpired() {
            requestNewAccessTokenFromServer();
            Log.d(TAG, "Access token refreshed");
        }

        @Override
        public void onTokenAboutToExpire() {
            requestNewAccessTokenFromServer();
            Log.d(TAG, "Access token refreshed");
        }
    };

    private void fetchConversationsList() {
        if (conversationsClient.getMyConversations() != null) {
            conversations.addAll(conversationsClient.getMyConversations());
            conversationsListRVAdapter.notifyDataSetChanged();
            for (Conversation c : conversations) {
                Log.d(TAG, "fetchConversationsList: " + c.getSid());
            }
        } else {
            Log.d(TAG, "fetchConversationsList: Null");
        }
    }


    @Override
    public void onConversationClicked(int position) {
        //TODO: decide what all to do and open one to one chat
        Intent intent = new Intent(ConversationsActivity.this, OneToOneConversationActivity.class);
        intent.putExtra("conversationSid", conversations.get(position).getSid());
        startActivity(intent);
    }
}