package com.martian.bpa.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by simpson on 2015-11-17.
 */
public class DateUtil {
    // Public
    public final static String TIME_ZONE   = "Asia/Seoul";
    public final static String TIME_FORMAT1 = "%k:%M";
    public final static String DATE_FORMAT = "HH:mm yyyy/MM/dd";
    private static final String LOG_TAG    = "DateUtil";

    static public String getCurrentDate() {
        Date sDate = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat(DATE_FORMAT);
        sFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return sFormat.format(sDate);
    }

    static public String getCurrentTime() {
        Time sTime = new Time(TimeZone.getTimeZone(TIME_ZONE).getID());
        sTime.setToNow();
        return sTime.format(TIME_FORMAT1);
    }

    public static String Date2String(Date aDate)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return simpleDateFormat.format(aDate);
    }

    public static Date String2Date(String aDate)
    {
        Date sDate;
        SimpleDateFormat sFormat = new SimpleDateFormat(DATE_FORMAT);
        sFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        try {
            sDate = sFormat.parse(aDate);
        } catch (ParseException e) {
            sDate = null;
        }
        return sDate;
    }
}
