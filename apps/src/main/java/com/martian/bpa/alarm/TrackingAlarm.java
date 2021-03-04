package com.martian.bpa.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by USER on 11/23/2015.
 */
public class TrackingAlarm {
    private static final String TAG = "BPA.Alarm";

    public static final String ALARM_ALERT_ACTION = "com.martian.bpa.ALARM_ALERT";
    public static final long INTERVAL_SEC = 60 * 60;
//    public static final long INTERVAL_SEC = 60 * 5;

    private static TrackingAlarm mTrackingAlarm = null;

    private Context mContext;
    private Intent mBroadcastIntent;

    public static TrackingAlarm getInstance(Context aContext) {
        if (mTrackingAlarm == null) {
            mTrackingAlarm = new TrackingAlarm(aContext);
        }
        return mTrackingAlarm;
    }

    private TrackingAlarm(Context aContext) {
        mContext = aContext;

        initData();
    }

    private void initData() {
        mBroadcastIntent = new Intent(TrackingAlarm.ALARM_ALERT_ACTION);
    }

    public void setAlarm(long second) {
        Log.i(TAG, "Set Alarm");

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

//        Intent intent = new Intent(TrackingAlarm.ALARM_ALERT_ACTION);
//        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 0, mBroadcastIntent, 0);

//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + second, pIntent);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), second * 1000, pIntent);    }

    public void releaseAlarm() {
        Log.i(TAG, "Release Alarm");

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

//        Intent intent = new Intent(TrackingAlarm.ALARM_ALERT_ACTION);
//        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 0, mBroadcastIntent, 0);
        alarmManager.cancel(pIntent);
        pIntent.cancel();

        // 주석을 풀면 먼저 실행되는 알람이 있을 경우, 제거하고
        // 새로 알람을 실행하게 된다. 상황에 따라 유용하게 사용 할 수 있다.
//      alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, pIntent);
    }

    public boolean isAlarmActivated()
    {
        PendingIntent pIntent;
        pIntent = PendingIntent.getBroadcast(mContext, 0, mBroadcastIntent, PendingIntent.FLAG_NO_CREATE);

        return pIntent != null;
    }
}
