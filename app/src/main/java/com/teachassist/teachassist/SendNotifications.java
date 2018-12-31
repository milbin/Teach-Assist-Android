package com.teachassist.teachassist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;

import static com.teachassist.teachassist.App.CHANNEL_1_ID;
import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;

public class SendNotifications extends ContextWrapper {
    private NotificationManagerCompat notificationManager;

    public SendNotifications(Context base){
        super(base);

    }

    public NotificationManagerCompat getManager(){
        if(notificationManager == null){
            notificationManager = NotificationManagerCompat.from(this);
        }
        return notificationManager;
    }


    public Notification sendOnChannel(String channel, final Class<? extends Activity> activityToOpen, int subject, String title, String body, String Average){
        Intent activityIntent = new Intent(this, MarksViewMaterial.class);

        SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, "");
        String password = sharedPreferences.getString(PASSWORD, "");

        activityIntent.putExtra("username", username);
        activityIntent.putExtra("password", password);
        activityIntent.putExtra("subject",subject);
        activityIntent.putExtra("subject Mark", Average);
        int currentTime = (int) System.currentTimeMillis();
        PendingIntent contentIntent= PendingIntent.getActivity(this, /*request code*/currentTime+subject, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ta_logo_v3)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();//TODO add real icon
        return notification;

    }
}
