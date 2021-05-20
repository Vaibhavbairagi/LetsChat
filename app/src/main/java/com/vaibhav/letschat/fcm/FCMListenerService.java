package com.vaibhav.letschat.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vaibhav.letschat.ConversationsActivity;
import com.vaibhav.letschat.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class FCMListenerService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    NotificationCompat.Builder builder;

    private static final String CHANNEL_ID = "com.vaibhav.LetsChat";
    private static final String MSG_NOTIFICATION_DESCRIPTION = "New Message Notification";
    private int notificationID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM", "notif: " + remoteMessage.toString());
        Log.d("FCM", "onMessageReceived for FCM");

        Log.d("FCM", "From: " + remoteMessage.getFrom());

        Log.d("FCM", "Data Message Body: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.e("FCM", "We do not parse notification body - leave it to system");
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (remoteMessage.getData().size()>0){
            JSONObject obj = new JSONObject(remoteMessage.getData());
            Log.d("FCM", "onMessageReceived: "+obj);
            JsonElement mJson =  JsonParser.parseString(obj.toString());
            Gson gson = new Gson();
            NewMessageNotificationModel notificationModel = gson.fromJson(mJson, NewMessageNotificationModel.class);

            String body = notificationModel.getTwiBody();
            String msg = body.substring(getStartIndexOfMessage(body)+1);

            Intent intent = new Intent(this, ConversationsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                notificationChannel = new NotificationChannel(CHANNEL_ID, MSG_NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationChannel.setSound(uri, attributes);

                notificationManager.createNotificationChannel(notificationChannel);

                builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_message_24)
                        .setContentTitle(notificationModel.getAuthor())
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);
            } else {
                builder = new  NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_message_24)
                        .setContentTitle(notificationModel.getAuthor())
                        .setContentText(msg)
                        //.setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSound(uri)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent);
            }
            notificationManager.notify(notificationID, builder.build());

        }

    }

    private int getStartIndexOfMessage(String msg){
        int colonCount = 0, index = 0;
        while (colonCount<2){
            if (msg.charAt(index) == ':'){
                colonCount++;
            }
            index++;
        }
        return index;
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("fcmToken", s);
        startService(intent);
    }
}