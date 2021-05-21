package com.vaibhav.letschat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConversationsPreferences {
    SharedPreferences conversationPreferences;
    private static ConversationsPreferences convPref;

    public ConversationsPreferences(Context context) {
        conversationPreferences = context.getSharedPreferences("ConversationsPreferences", Context.MODE_PRIVATE);
        convPref = this;
    }

    public static ConversationsPreferences getInstance(){
        return convPref;
    }

    public void saveCurrentUserId(String userId){
        conversationPreferences.edit().putString("CurrentUserId",userId).apply();
    }

    public String getCurrentUserId(){
        return conversationPreferences.getString("CurrentUserId", null);
    }

    public void saveAccessToken(String accessToken) {
        conversationPreferences.edit().putString("AccessToken", accessToken).apply();
    }

    public String getAccessToken() {
        return conversationPreferences.getString("AccessToken", null);
    }

    public String getTokenCreationTime() {
        return conversationPreferences.getString("TokenCreationTime", null);
    }

    public void saveTokenCreationTime(String tokenCreationTime) {
        conversationPreferences.edit().putString("TokenCreationTime", tokenCreationTime).apply();
    }

    public void saveRegisteredFCMToken(String fcmToken) {
        conversationPreferences.edit().putString("RegisteredFCMToken", fcmToken).apply();
    }

    public String getRegisteredFCMToken(){
        return conversationPreferences.getString("RegisteredFCMToken",null);
    }


}
