package com.teachassist.teachassist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.teachassist.teachassist.App.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SendNotifications sendNotifications = new SendNotifications(context);
        Notification notification = sendNotifications.sendOnChannel(CHANNEL_1_ID, "title", "body");
        sendNotifications.getManager().notify(1 , notification);

    }
}
