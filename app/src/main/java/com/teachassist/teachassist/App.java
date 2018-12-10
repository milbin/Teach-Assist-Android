package com.teachassist.teachassist;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import java.util.Calendar;

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
            System.out.println(Calendar.getInstance().getTime());
            NotificationChannel course1 = new NotificationChannel(CHANNEL_1_ID, "course1", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course2 = new NotificationChannel(CHANNEL_2_ID, "course2", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course3 = new NotificationChannel(CHANNEL_3_ID, "course3", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel course4 = new NotificationChannel(CHANNEL_4_ID, "course4", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(course1);
            manager.createNotificationChannel(course2);
            manager.createNotificationChannel(course3);
            manager.createNotificationChannel(course4);

            Calendar calendar = Calendar.getInstance();
            Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()/* + AlarmManager.INTERVAL_HOUR*/, 60000, pendingIntent);

        }
    }
}
