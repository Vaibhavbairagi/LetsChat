package com.vaibhav.letschat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationListener;
import com.twilio.conversations.ErrorInfo;
import com.twilio.conversations.Message;
import com.twilio.conversations.Participant;
import com.vaibhav.letschat.adapters.ConversationsMessageRVAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OneToOneConversationActivity extends AppCompatActivity implements ConversationListener {

    private static final String TAG = "OneToOneConversation";
    String token = "fim66bx1Tyu4uD4zCc_nFe:APA91bFIjtNeVAXVJ8ehn517g2dQO_TRl0IABcOhVfAwc4tU4fZjEvb50li0_OPURz_ow6MSACf1MwFIosfo9QUc7tzOY_j_gKrmlsmx_SPT5fB2nz4qd4RbxIWSKbJWjK2Id9K-3OaL";
    public static final int CALL_TYPE_VIDEO = 0;
    public static final int CALL_TYPE_AUDIO = 1;
    public static final String CALL_TYPE = "callType";

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
    TextView loadingIndicator;
    boolean isMessageSent = false;
    RelativeLayout rootViewRL, sendMsgParent;
    PopupWindow mediaChooserPopup;
    View mediaChooserPopupLayout;

    boolean isOldMessagesLoading = false;

    private final int LOAD_MESSAGE_COUNT = 30;

    LinearLayoutManager layoutManager;
    public final int CAMERA_PERMISSION_REQCODE = 101;
    int mediaChooserAction;
    public static final int MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE = 0;
    public static final int MEDIA_CHOOSER_ACTION_CAMERA_VIDEO_CAPTURE = 1;
    public static final int MEDIA_CHOOSER_ACTION_GALLERY = 2;
    public static final int MEDIA_CHOOSER_ACTION_ATTACH = 3;

    public static final String IMAGE_DIRECTORY = "/LetsChat/SharedImages";

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
        loadingIndicator = findViewById(R.id.tv_otoc_loading_indicator);
        rootViewRL = findViewById(R.id.rl_otoc_root_parent);
        sendMsgParent = findViewById(R.id.rl_otoc_send_msg_layout);

        conversationsMessageRVAdapter = new ConversationsMessageRVAdapter(messages, context);
        layoutManager = new LinearLayoutManager(this);
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
            public void onClick(View view) {
                String msg = sendMsgTextET.getText().toString();
                if (!msg.isEmpty()) {
                    sendMsgTextET.setText("");
                    sendMessage(msg);
                } else {
                    Toast.makeText(context, "Enter a msg to send", Toast.LENGTH_SHORT).show();
                }
            }
        });

        videoCallIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outToVideoCall = new Intent(OneToOneConversationActivity.this, OneToOneCallActivity.class);
                outToVideoCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                //todo: get roomName properly
                outToVideoCall.putExtra("receiverFCM", token);
                outToVideoCall.putExtra("receiverName", "Ayush");
                outToVideoCall.putExtra("roomName", conversation.getSid());
                outToVideoCall.putExtra(CALL_TYPE, CALL_TYPE_VIDEO);
                startActivity(outToVideoCall);
            }
        });
        voiceCallIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outToVoiceCall = new Intent(OneToOneConversationActivity.this, OneToOneCallActivity.class);
                outToVoiceCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                //todo: get data properly
                outToVoiceCall.putExtra("receiverFCM",token);
                outToVoiceCall.putExtra("receiverName","Ayush");
                outToVoiceCall.putExtra("roomName", conversation.getSid());
                outToVoiceCall.putExtra(CALL_TYPE,CALL_TYPE_AUDIO);
                startActivity(outToVoiceCall);
            }
        });


        LayoutInflater inflater = getLayoutInflater();
        mediaChooserPopupLayout = inflater.inflate(R.layout.media_chooser_options_popup_layout, rootViewRL, false);
        mediaChooserPopup = new PopupWindow(mediaChooserPopupLayout, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mediaChooserPopup.setFocusable(true);
        mediaChooserPopup.setAnimationStyle(R.style.PopupAnimationStyle);
        //todo:setup and open choosers
        handleMediaChooserOptions();
        attachIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMediaChooserOptions();
            }
        });
    }

    private void sendMessage(String messageBody) {
        isMessageSent = false;
        if (conversation != null) {
            Message.Options options = Message.options().withBody(messageBody);
            Log.d(TAG, "Message created");
            loadingIndicator.setVisibility(View.VISIBLE);
            Thread loadingTimer = new Thread() {
                @Override
                public void run() {
                    try {
                        int loadTime = 0;
                        while (!isMessageSent) {

                            sleep(400);

                            if (loadTime < 400) {
                                setLoadingText("Sending.");
                            } else if (loadTime < 800) {
                                setLoadingText("Sending..");
                            } else {
                                setLoadingText("Sending...");
                            }
                            loadTime = loadTime + 400;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                }
            };
            loadingTimer.start();
            conversation.sendMessage(options, new CallbackListener<Message>() {
                @Override
                public void onSuccess(Message message) {
                    Log.d(TAG, "Message sent");
                    isMessageSent = true;
                    loadingIndicator.setVisibility(View.GONE);
                    //todo: implement ticks
                }
            });
        }
    }

    private void setLoadingText(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingIndicator.setText(text);
            }
        });
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
                    conversationsMessageRVAdapter.notifyMyDataChanged();
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
        if (messages.get(0).getMessageIndex() > 0) {
            topProgress.setVisibility(View.VISIBLE);
            conversation.getMessagesBefore(messages.get(0).getMessageIndex() - 1, LOAD_MESSAGE_COUNT, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> result) {
                    isOldMessagesLoading = false;
                    topProgress.setVisibility(View.GONE);
                    int resSize = result.size();
                    result.addAll(messages);
                    messages.clear();
                    messages.addAll(result);
                    int offset = msgRecyclerView.computeVerticalScrollOffset();
                    int position = msgRecyclerView.getVerticalScrollbarPosition();
                    int x = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
                    position += resSize + x;
                    conversationsMessageRVAdapter.notifyMyDataChanged();
                    msgRecyclerView.scrollToPosition(position);
                    msgRecyclerView.offsetChildrenVertical(offset);
                }
            });
        }
    }

    @Override
    public void onMessageAdded(final Message message) {
        Log.d(TAG, "Message added");
        messages.add(message);
        conversationsMessageRVAdapter.notifyMyItemInserted(messages.size() - 1);
        msgRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                msgRecyclerView.smoothScrollToPosition(conversationsMessageRVAdapter.getItemCount() - 1);
            }
        });
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

    private void showMediaChooserOptions() {
        int yOffset = -sendMsgParent.getHeight() - dpToPx(72);
        mediaChooserPopup.showAsDropDown(sendMsgParent, 0, yOffset);
        mediaChooserPopup.update();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void handleMediaChooserOptions() {
        ImageButton cameraIB = mediaChooserPopupLayout.findViewById(R.id.ib_mcop_camera);
        ImageButton galleryIB = mediaChooserPopupLayout.findViewById(R.id.ib_mcop_gallery);
        ImageButton attachIB = mediaChooserPopupLayout.findViewById(R.id.ib_mcop_attach_opt);
        ImageButton videoIB = mediaChooserPopupLayout.findViewById(R.id.ib_mcop_video);

        cameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaChooserAction = MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE;
                if (checkCameraAndStoragePermissions()) {
                    requestCameraAndStoragePermissions();
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE);
                }
            }
        });

        videoIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaChooserAction = MEDIA_CHOOSER_ACTION_CAMERA_VIDEO_CAPTURE;
                if (checkCameraAndStoragePermissions()) {
                    requestCameraAndStoragePermissions();
                } else {
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(videoIntent, MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE);
                }
            }
        });

        galleryIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaChooserAction = MEDIA_CHOOSER_ACTION_GALLERY;
                if (checkCameraAndStoragePermissions()) {
                    requestCameraAndStoragePermissions();
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("*/*");
                    startActivityForResult(galleryIntent, MEDIA_CHOOSER_ACTION_GALLERY);
                }
            }
        });

        attachIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaChooserAction = MEDIA_CHOOSER_ACTION_ATTACH;
                if (checkCameraAndStoragePermissions()) {
                    requestCameraAndStoragePermissions();
                } else {
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("file/*");
                    startActivityForResult(fileIntent, MEDIA_CHOOSER_ACTION_ATTACH);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE && resultCode == RESULT_OK) {

        } else if (requestCode == MEDIA_CHOOSER_ACTION_CAMERA_VIDEO_CAPTURE && resultCode == RESULT_OK) {

        } else if (requestCode == MEDIA_CHOOSER_ACTION_GALLERY && resultCode == RESULT_OK) {

        } else if (requestCode == MEDIA_CHOOSER_ACTION_ATTACH && resultCode == RESULT_OK) {

        } else {
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCameraAndStoragePermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestCameraAndStoragePermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (mediaChooserAction) {
                    case MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE);
                        break;
                    case MEDIA_CHOOSER_ACTION_CAMERA_VIDEO_CAPTURE:
                        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(videoIntent, MEDIA_CHOOSER_ACTION_CAMERA_IMAGE_CAPTURE);
                        break;
                    case MEDIA_CHOOSER_ACTION_GALLERY:
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("*/*");
                        startActivityForResult(galleryIntent, MEDIA_CHOOSER_ACTION_GALLERY);
                        break;
                    case MEDIA_CHOOSER_ACTION_ATTACH:
                        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        fileIntent.setType("file/*");
                        startActivityForResult(fileIntent, MEDIA_CHOOSER_ACTION_ATTACH);
                        break;
                    default:
                        Toast.makeText(context, "Please choose a valid option!", Toast.LENGTH_SHORT).show();
                        break;
                }

            } else {
                Toast.makeText(context, "Please grant all permissions to conitnue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //private ActivityResultLauncher<String> cameraLauncherForImages = registerForActivityResult(new);

    private String saveImageInLocalStorage(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //todo:set the receiver name instead of the local conversation.getFriendlyName()
        File sharedImageDir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + conversation.getFriendlyName());
        if (!sharedImageDir.exists()) {
            sharedImageDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File f = new File(sharedImageDir, imageFileName);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        MediaScannerConnection.scanFile(this,
                new String[]{f.getPath()},
                new String[]{"image/jpeg"}, null);
        fo.close();
        Log.d(TAG, "File Saved::--->" + f.getAbsolutePath());

        return f.getAbsolutePath();
    }
}