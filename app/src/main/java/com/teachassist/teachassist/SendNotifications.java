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



    public static final String channel1ID = "Channel1ID";
    public static final String channel1Name = "Course1";
    public static final String channel2ID = "Channel2ID";
    public static final String channel2Name = "Course2";
    public static final String channel3ID = "Channel3ID";
    public static final String channel3Name = "Course3";
    public static final String channel4ID = "Channel4ID";
    public static final String channel4Name = "Course4";

    public SendNotifications(Context base){
        super(base);

    }

    public NotificationManagerCompat getManager(){
        if(notificationManager == null){
            notificationManager = NotificationManagerCompat.from(this);
        }
        return notificationManager;
    }


    public Notification sendOnChannel(String channel, final Class<? extends Activity> activityToOpen, int subject, String title, String body){
        Intent activityIntent = new Intent(this, activityToOpen);

        SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, "");
        String password = sharedPreferences.getString(PASSWORD, "");

        activityIntent.putExtra("username", username);
        activityIntent.putExtra("password", password);
        activityIntent.putExtra("subject",subject);
        PendingIntent contentIntent= PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();//TODO add real icon
        return notification;

    }
}
