package com.vaibhav.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.vaibhav.letschat.api.ChatAPI;
import com.vaibhav.letschat.utils.AppPreferences;
import com.vaibhav.letschat.utils.ConversationsPreferences;
import com.vaibhav.letschat.utils.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "LetsChat";
    public String userId = "Hiv2", userName = "Vaibhav Bairagi";

    RelativeLayout fullscreenProgressLayout;

    ConversationsPreferences conversationsPreferences;
    AppPreferences appPreferences;
    String accessToken;

    ConversationsClient conversationsClient;
    ArrayList<Conversation> conversations = new ArrayList<>();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conversationsPreferences = new ConversationsPreferences(this);
        appPreferences = new AppPreferences(this);
        accessToken = conversationsPreferences.getAccessToken();

        context = this;

        fullscreenProgressLayout = findViewById(R.id.fullscreen_progress_layout);

        if (accessToken == null) {
            requestNewAccessTokenFromServer();
            Log.d(TAG, "New Access token created");
        } else {
            initializeConvClientWithAccessToken();
        }
    }

    private void requestNewAccessTokenFromServer() {
        Retrofit retrofit = RetrofitClient.getInstance();

        ChatAPI chatAPI = retrofit.create(ChatAPI.class);

        Call<String> call = chatAPI.generateNewAccessToken(userId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    accessToken = response.body();
                    conversationsPreferences.saveAccessToken(accessToken);
                    initializeConvClientWithAccessToken();
                    Log.d(TAG, "Access token fetched");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Access token couldn't be generated", Toast.LENGTH_SHORT).show();
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
                Log.d(MainActivity.TAG, "Twilio Conversation Client created successfully");
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error creating Twilio Conversations Client: " + errorInfo.getMessage());
            }
        });
    }

    public final ConversationsClientListener conversationsClientListener = new ConversationsClientListener() {
        @Override
        public void onConversationAdded(Conversation conversation) {

        }

        @Override
        public void onConversationUpdated(Conversation conversation, Conversation.UpdateReason reason) {

        }

        @Override
        public void onConversationDeleted(Conversation conversation) {

        }

        @Override
        public void onConversationSynchronizationChange(Conversation conversation) {

        }

        @Override
        public void onError(ErrorInfo errorInfo) {

        }

        @Override
        public void onUserUpdated(User user, User.UpdateReason reason) {

        }

        @Override
        public void onUserSubscribed(User user) {

        }

        @Override
        public void onUserUnsubscribed(User user) {

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

                        }
                    });
                } else if (ft != null && !ft.equals(rft)) {
                    conversationsClient.registerFCMToken(new ConversationsClient.FCMToken(appPreferences.getFCMToken()), new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(MainActivity.TAG, "YAY");
                        }
                    });
                }
                fetchConversationsList();
                fullscreenProgressLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNewMessageNotification(String conversationSid, String messageSid, long messageIndex) {

        }

        @Override
        public void onAddedToConversationNotification(String conversationSid) {

        }

        @Override
        public void onRemovedFromConversationNotification(String conversationSid) {

        }

        @Override
        public void onNotificationSubscribed() {

        }

        @Override
        public void onNotificationFailed(ErrorInfo errorInfo) {

        }

        @Override
        public void onConnectionStateChange(ConversationsClient.ConnectionState state) {

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
            for (Conversation c : conversations) {
                Log.d(TAG, c.getUniqueName());
            }
        }
    }


}