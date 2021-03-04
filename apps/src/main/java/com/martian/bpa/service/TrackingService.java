package com.martian.bpa.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.martian.bpa.util.UiUtil;

/**
 * Created by USER on 11/16/2015.
 */
public class TrackingService extends Service implements ServiceController {
    private static String TAG = "BPA.TrackingService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
//        UiUtil.showNotification(this, "서비스가 종료 되었습니다.");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand");
        UiUtil.showNotification(this, "가격 확인 중 입니다... (Debug Message)");

        PriceTracker sPriceTracker = new PriceTracker(getApplicationContext(), this);
        sPriceTracker.compare();

        return 0;
    }

    @Override
    public void finish() {
        Log.i(TAG, "stopSelf");
        stopSelf();
    }

    public static boolean isTimeToTracking(int hour) {
        return (hour >= 7 && hour <= 22);
    }
}
