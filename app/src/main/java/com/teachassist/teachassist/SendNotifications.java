package com.teachassist.teachassist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Calendar;

import static com.teachassist.teachassist.App.CHANNEL_1_ID;

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


    public Notification sendOnChannel(String channel, String title, String body){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .build();//TODO add real icon
        notificationManager.notify(1, notification);
        return notification;

    }
}
