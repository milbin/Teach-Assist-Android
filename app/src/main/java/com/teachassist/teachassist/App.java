package com.teachassist.teachassist;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

public class App extends Application {

    public static final String CHANNEL_1_ID = "course1";
    public static final String CHANNEL_2_ID = "course2";
    public static final String CHANNEL_3_ID = "course3";
    public static final String CHANNEL_4_ID = "course4";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel course1 = new NotificationChannel(CHANNEL_1_ID, "course1", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course2 = new NotificationChannel(CHANNEL_2_ID, "course2", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course3 = new NotificationChannel(CHANNEL_3_ID, "course3", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course4 = new NotificationChannel(CHANNEL_4_ID, "course4", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(course1);
            manager.createNotificationChannel(course2);
            manager.createNotificationChannel(course3);
            manager.createNotificationChannel(course4);

        }
    }
}
