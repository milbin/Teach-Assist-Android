package com.teachassist.teachassist;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class onNotificationReceived extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        System.out.println(token +"NEW TOKEN HERE");
        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor =   sharedPreferences.edit();
        editor.putString("token", token);
        editor.putBoolean("hasRegistered", false);
        editor.apply();
    }
}
