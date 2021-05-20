package com.vaibhav.letschat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    SharedPreferences conversationPreferences;
    private static AppPreferences appPreferences;

    public AppPreferences(Context context) {
        conversationPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        appPreferences = this;
    }

    public static AppPreferences getInstance(){
        return appPreferences;
    }

    public void saveFCMToken(String accessToken) {
        conversationPreferences.edit().putString("FCMToken", accessToken).apply();
    }

    public String getFCMToken() {
        return conversationPreferences.getString("FCMToken", null);
    }
}
