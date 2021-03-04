package com.martian.bpa.alarm;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.service.TrackingService;
import com.martian.bpa.util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "BPA.AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            boolean isTrackingEnabled = Util.getProperty(context, PropertySet.TAG_CONF_TRACKIGN_ENABLED, true);
            if (isTrackingEnabled) {
                TrackingAlarm.getInstance(context).setAlarm(TrackingAlarm.INTERVAL_SEC);
            }
        } else if (intent.getAction().equals("com.martian.bpa.ALARM_ALERT")) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int second = c.get(Calendar.MINUTE);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            Log.i(TAG, "Wake up Alarm: " + dateFormat.format(c.getTime()) + ", hour: " + hour);

            if (TrackingService.isTimeToTracking(hour)) {
                Intent sIntent = new Intent(context, TrackingService.class);
                sIntent.setPackage("com.martian.bpa.SERVICE_START");
                context.startService(sIntent);
            }
        }
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
