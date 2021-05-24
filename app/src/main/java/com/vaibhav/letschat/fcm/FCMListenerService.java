package com.vaibhav.letschat.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vaibhav.letschat.ConversationsActivity;
import com.vaibhav.letschat.R;
import com.vaibhav.letschat.api.ChatAPI;
import com.vaibhav.letschat.api.StatusResponse;
import com.vaibhav.letschat.utils.RetrofitClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FCMListenerService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    NotificationCompat.Builder builder;

    private static final String CHANNEL_ID = "com.vaibhav.LetsChat";
    private static final String MSG_NOTIFICATION_DESCRIPTION = "New Message Notification";
    private int notificationID = 1;

    private static final String TAG = "FCMListenerService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "notif: " + remoteMessage.toString());
        Log.d(TAG, "onMessageReceived for FCM");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data Message Body: " + remoteMessage.getData().toString());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        }

        if (isAppInBackground(getApplicationContext())) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (remoteMessage.getData().size() > 0) {

                JSONObject obj = new JSONObject(remoteMessage.getData());
                Log.d(TAG, "onMessageReceived: " + obj);
                JsonElement mJson = JsonParser.parseString(obj.toString());
                Gson gson = new Gson();
                NewMessageNotificationModel notificationModel = gson.fromJson(mJson, NewMessageNotificationModel.class);
                //todo: handle the case where type is call
                if(notificationModel.getTwiMessageType()!=null){
                    String body = notificationModel.getTwiBody();
                    String msg = body.substring(getStartIndexOfMessage(body) + 1);

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
                                .setChannelId(CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_baseline_message_24)
                                .setContentTitle(notificationModel.getAuthor())
                                .setContentText(msg)
                                .setAutoCancel(true)
                                .setSound(uri)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setContentIntent(pendingIntent);
                    } else {
                        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
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
        }
    }

    private int getStartIndexOfMessage(String msg) {
        int colonCount = 0, index = 0;
        while (colonCount < 2) {
            if (msg.charAt(index) == ':') {
                colonCount++;
            }
            index++;
        }
        return index;
    }

    private boolean isAppInBackground(Context context) {
        boolean isInBackground = true;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: "+s);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("fcmToken", s);
        startService(intent);

        //Todo: make this api endpoint
        Retrofit retrofit = RetrofitClient.getInstance();
        ChatAPI chatAPI = retrofit.create(ChatAPI.class);
        Call<StatusResponse> call = chatAPI.updateFCMToken(s);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.body() != null) {
                    StatusResponse res = response.body();
                    Log.d(TAG, "Token updation onto BigOHealth server status: "+res.getStatus());

                } else {
                    Log.d(TAG, "Null returned on Token updation onto BigOHealth server");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.d(TAG, "Token updation onto BigOHealth server failed:" + t.getMessage());
            }
        });

    }
}