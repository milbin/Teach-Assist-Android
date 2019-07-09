package com.teachassist.teachassist;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class onNotificationReceived extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        System.out.println(token +"NEW TOKEN HERE");
        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor =   sharedPreferences.edit();
        editor.putString("token", token);
        editor.putBoolean("hasRegistered", false);
        editor.apply();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
}
