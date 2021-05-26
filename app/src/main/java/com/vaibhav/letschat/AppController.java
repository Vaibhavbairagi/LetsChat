package com.vaibhav.letschat;

import android.app.Application;
import android.content.Context;

import com.twilio.conversations.ConversationsClient;
import com.vaibhav.letschat.utils.AppPreferences;
import com.vaibhav.letschat.utils.ConversationsPreferences;

public class AppController extends Application {

    private static AppController appController;

    private ConversationsClient conversationsClient;

    private ConversationsPreferences conversationsPreferences;
    private AppPreferences appPreferences;

    private static Context context;

    public static AppController getInstance() {
        return appController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        context = this;
        conversationsPreferences = new ConversationsPreferences(getApplicationContext());
        appPreferences = new AppPreferences(getApplicationContext());
    }

    public void setConversationsClient(ConversationsClient conversationsClient) {
        this.conversationsClient = conversationsClient;
    }

    public ConversationsClient getConversationsClient() {
        return this.conversationsClient;
    }

    public static Context getContext(){
        return context;
    }

}
