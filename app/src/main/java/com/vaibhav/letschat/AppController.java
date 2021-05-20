package com.vaibhav.letschat;

import android.app.Application;

import com.twilio.conversations.ConversationsClient;

public class AppController extends Application {

    private static AppController appController;

    private ConversationsClient conversationsClient;

    public static AppController getInstance() {
        return appController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
    }

    public void setConversationsClient(ConversationsClient conversationsClient) {
        this.conversationsClient = conversationsClient;
    }

    public ConversationsClient getConversationsClient() {
        return this.conversationsClient;
    }

}
