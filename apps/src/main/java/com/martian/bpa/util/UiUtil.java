package com.martian.bpa.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;

import com.martian.bpa.DBDataListView;

/**
 * Created by USER on 11/19/2015.
 */
public class UiUtil {
    public static int NOTIFICATION_ID = 20151006;

    public static void showNotification(Context context, String message) {
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        // 작은 아이콘 이미지.
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);

        // 알림이 출력될 때 상단에 나오는 문구.
        builder.setTicker("문석 중...");

        // 알림 출력 시간.
        builder.setWhen(System.currentTimeMillis());

        // 알림 제목.
        builder.setContentTitle("최저가 알리미");

        // 프로그래스 바.
        //builder.setProgress(100, 50, false);

        // 알림 내용.
        builder.setContentText(message);

        // 알림시 사운드, 진동, 불빛을 설정 가능.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);

        // 알림 터치시 반응.
        Intent intent= new Intent(context, DBDataListView.class);
        intent.putExtra(DBDataListView.TAG_PARAMETER, DBDataListView.TAG_CALLER_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);

//        // 우선순위.
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);
//
//        // 행동 최대3개 등록 가능.
//        builder.addAction(R.mipmap.ic_launcher, "Show", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Hide", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Remove", pendingIntent);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(UiUtil.NOTIFICATION_ID, builder.build());
    }

    public static void showNotificationInBoxStyle(Context context, String[] messages) {
        Notification.InboxStyle notification = new Notification.InboxStyle(
                new Notification.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
        );

        // 요약 정보.
        notification.setSummaryText("최저가 알리미");

        for(String message : messages) {
            notification.addLine(message);
        }

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(UiUtil.NOTIFICATION_ID, notification.build());
    }

    public static int getChanges(int sOriginalPrice, int aNewPrice) {
        return aNewPrice - sOriginalPrice;
    }

    public static String getLastPriceResultString(Context aContext, int aChanges) {
        String sLastPriceResult = "";
        if (aChanges > 0) {
            sLastPriceResult = " (" + Util.toCurrency(aContext, aChanges) + " UP)";
        } else if (aChanges < 0) {
            sLastPriceResult = " (" + Util.toCurrency(aContext, aChanges) + " DN)";
        }

        return sLastPriceResult;
    }

    public static int geTextColor(int aChanges) {
        int sTextColor = Color.GRAY;
        if (aChanges > 0) {
            sTextColor = Color.RED;
        } else if (aChanges < 0) {
            sTextColor = Color.BLUE;
        }

        return sTextColor;
    }
}
