package com.vaibhav.letschat.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vaibhav.letschat.CallReceiverActivity;
import com.vaibhav.letschat.OneToOneCallActivity;
import com.vaibhav.letschat.R;

public class CallReceiverService extends Service implements MediaPlayer.OnPreparedListener {
//    MediaPlayer mediaPlayer;
//    Vibrator mvibrator;

    //todo: add check if user is already on call
    private static final String TAG = "CallReceiverService";
    String roomName, callerName;
    int callType;

    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    NotificationCompat.Builder builder;

    private final String CALL_CHANNEL_ID = "com.vaibhav.letschat" + "Call";
    private final String CALL_NOTIFICATION_DESCRIPTION = "New Incoming Call";
    private int callNotificationID = 100;
    Context mContext;
    Ringtone r;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mContext = getApplicationContext();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        roomName = intent.getStringExtra("roomName");
        callerName = intent.getStringExtra("callerName");
        callType = intent.getIntExtra("callType", OneToOneCallActivity.CALL_TYPE_VIDEO);
        Log.d(TAG, "Started callReceiverService");

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

//        try {
//            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//            if (audioManager != null) {
//                switch (audioManager.getRingerMode()) {
//                    case AudioManager.RINGER_MODE_NORMAL:
//                        status = true;
//                        break;
//                    case AudioManager.RINGER_MODE_SILENT:
//                        status = false;
//                        break;
//                    case AudioManager.RINGER_MODE_VIBRATE:
//                        status = false;
//                        vstatus = true;
//                        Log.e("Service!!", "vibrate mode");
//                        break;
//                }
//            }
//
//            if (status) {
//                Runnable delayedStopRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        releaseMediaPlayer();
//                    }
//                };
//
//                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
//                    public void onAudioFocusChange(int focusChange) {
//                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//                            // Permanent loss of audio focus
//                            // Pause playback immediately
//                            //mediaController.getTransportControls().pause();
//                            if (mediaPlayer != null) {
//                                if (mediaPlayer.isPlaying()) {
//                                    mediaPlayer.pause();
//                                }
//                            }
//                            // Wait 30 seconds before stopping playback
//                            handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30));
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
//                            // Pause playback
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//                            // Lower the volume, keep playing
//                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                            // Your app has been granted audio focus again
//                            // Raise volume to normal, restart playback if necessary
//                        }
//                    }
//                };
//                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//
//
//                mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
//                mediaPlayer.setLooping(true);
//                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    handler = new Handler();
//
//
//                    playbackAttributes = new AudioAttributes.Builder()
//                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                            .build();
//
//                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
//                            .setAudioAttributes(playbackAttributes)
//                            .setAcceptsDelayedFocusGain(true)
//                            .setOnAudioFocusChangeListener(afChangeListener, handler)
//                            .build();
//                    int res = audioManager.requestAudioFocus(focusRequest);
//                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        if (!keyguardManager.isDeviceLocked()) {
//
//                            mediaPlayer.start();
//                        }
//
//                    }
//                } else {
//
//                    // Request audio focus for playback
//                    int result = audioManager.requestAudioFocus(afChangeListener,
//                            // Use the music stream.
//                            AudioManager.STREAM_MUSIC,
//                            // Request permanent focus.
//                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//
//                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        if (!keyguardManager.isDeviceLocked()) {
//                            // Start playback
//                            mediaPlayer.start();
//                        }
//                    }
//
//                }
//
//            } else if (vstatus) {
//                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Start without a delay
//                // Each element then alternates between vibrate, sleep, vibrate, sleep...
//                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
//                        150, 75, 150};
//
//                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
//                mvibrator.vibrate(pattern, 0);
//                Log.e("Service!!", "vibrate mode start");
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        if (intent != null && intent.getExtras() != null) {
//
//            data = intent.getExtras();
//            name = data.getString("inititator");
//            if (AppController.getInstance().getCall_type().equalsIgnoreCase(ApplicationRef.Constants.AUDIO_CALL)) {
//                callType = "Audio";
//            } else {
//                callType = "Video";
//            }
//
//        }
//        try {
//            Intent receiveCallAction = new Intent(getApplicationContext(), CallReceiverActivity.class);
//
//            receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
//            receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
//            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
//            receiveCallAction.setAction("RECEIVE_CALL");
//
//            Intent cancelCallAction = new Intent(AppController.getInstance().getContext(), CallNotificationActionReceiver.class);
//            cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
//            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
//            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
//            cancelCallAction.setAction("CANCEL_CALL");
//
//            Intent callDialogAction = new Intent(AppController.getInstance().getContext(), CallNotificationActionReceiver.class);
//            callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
//            callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
//            callDialogAction.setAction("DIALOG_CALL");
//
//            PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(AppController.getInstance().getContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(AppController.getInstance().getContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(AppController.getInstance().getContext(), 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            createChannel();
//            NotificationCompat.Builder notificationBuilder = null;
//            if (data != null) {
//                // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
//                notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentTitle(name)
//                        .setContentText("Incoming " + callType + " Call")
//                        .setSmallIcon(R.drawable.ic_call_icon)
//                        .setPriority(NotificationCompat.PRIORITY_MAX)
//                        .setCategory(NotificationCompat.CATEGORY_CALL)
//                        .addAction(R.drawable.ic_call_decline, getString(R.string.reject_call), cancelCallPendingIntent)
//                        .addAction(R.drawable.ic_call_accept, getString(R.string.answer_call), receiveCallPendingIntent)
//                        .setAutoCancel(true)
//                        //.setSound(ringUri)
//                        .setFullScreenIntent(callDialogPendingIntent, true);
//
//            }
//
//            Notification incomingCallNotification = null;
//            if (notificationBuilder != null) {
//                incomingCallNotification = notificationBuilder.build();
//            }
//            startForeground(NOTIFICATION_ID, incomingCallNotification);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Intent handleCall = new Intent(CallReceiverService.this, CallReceiverActivity.class);
        handleCall.putExtra("roomName", roomName);
        handleCall.putExtra("callerName", callerName);
        handleCall.putExtra("callType", callType);
        //if activity already exists don't create a new one
        handleCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, handleCall, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel = new NotificationChannel(CALL_CHANNEL_ID, CALL_NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new NotificationCompat.Builder(this, CALL_CHANNEL_ID)
                    .setChannelId(CALL_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_call_24)
                    .setContentTitle("Incoming Call!")
                    .setContentText(callerName)
                    .setAutoCancel(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true);
        } else {
            builder = new NotificationCompat.Builder(this, CALL_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_call_24)
                    .setContentTitle("Incoming Call!")
                    .setContentText(callerName)
                    //.setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true);
        }
        Log.d(TAG, "onMessageReceived: Call received");
        startForeground(callNotificationID, builder.build());
        startClosingTimer();

        return START_STICKY;
    }

    private void startClosingTimer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onDestroy();
            }
        };
        Handler handler = new Handler();
        //End call after the 25 secs if user hasn't already picked or cancelled
        handler.postDelayed(runnable, 25000);
        Log.d(TAG, "startClosingTimer: call ended based on timer");
    }

    @Override
    public void onDestroy() {
        r.stop();
        stopForeground(true);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(CallReceiverService.this);
        localBroadcastManager.sendBroadcast(new Intent(
                CallReceiverActivity.callEndAction));
        super.onDestroy();
    }

//    public void releaseVibration() {
//        try {
//            if (mvibrator != null) {
//                if (mvibrator.hasVibrator()) {
//                    mvibrator.cancel();
//                }
//                mvibrator = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void releaseMediaPlayer() {
//        try {
//            if (mediaPlayer != null) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                    mediaPlayer.release();
//                }
//                mediaPlayer = null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}