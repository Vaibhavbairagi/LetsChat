package com.vaibhav.letschat.fcm;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class FCMListenerService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM","onMessageReceived for FCM");

        Log.d("FCM","From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM","Data Message Body: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM","Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.e("FCM","We do not parse notification body - leave it to system");
        }
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("fcmToken",s);
        startService(intent);
    }
}