package com.example.smartdustbin.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationManager {

    public NotificationManager() {

    }

    public void startNotification(Context context) {
        Intent intent = new Intent(context, MyBroadCastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            long triggerTime = 1 * 60 * 1000; //set time here to run the notifications.

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, triggerTime, sender);
        }

    }

    ;
}
