package com.vaibhav.letschat.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.StatusListener;
import com.vaibhav.letschat.AppController;
import com.vaibhav.letschat.ConversationsActivity;
import com.vaibhav.letschat.utils.AppPreferences;
import com.vaibhav.letschat.utils.ConversationsPreferences;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String[] TOPICS = { "global" };

    public RegistrationIntentService()
    {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String fcmToken = intent.getExtras().getString("fcmToken");
        AppPreferences.getInstance().saveFCMToken(fcmToken);
        try {
            AppController.getInstance().getConversationsClient().registerFCMToken(new ConversationsClient.FCMToken(fcmToken), new StatusListener() {
                @Override
                public void onSuccess() {
                    ConversationsPreferences.getInstance().saveRegisteredFCMToken(fcmToken);
                    Log.d(ConversationsActivity.TAG, "YAY "+fcmToken);
                }
            });
            subscribeTopics(fcmToken);

        } catch (Exception e) {

        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("FCMPreferences.REGISTRATION_COMPLETE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void subscribeTopics(String token) throws IOException {
        // for (String topic : TOPICS) {
        //     FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+topic);
        // }
    }
}