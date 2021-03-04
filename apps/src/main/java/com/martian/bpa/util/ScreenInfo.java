package com.martian.bpa.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * Created by simpson on 15. 12. 2.
 */
/////////////////////////////////////////////////////////////////
// ScreenInfo Class

public class ScreenInfo {
    final public static int TAG_DEFAULT_WIDTH_MIN_SIZE = 160;
    private int mHeight;
    private int mWidth;
    private int mDensity;
    private DisplayMetrics mMetrix;

    public static DEVICE_TYPE mDeviceType;

    public enum DEVICE_TYPE {
        PHONE,
        TABLET
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getDensity() {
        return mDensity;
    }

    public DisplayMetrics getMetrix() {
        return mMetrix;
    }

    public ScreenInfo(DisplayMetrics aMetrix) {
        mMetrix = aMetrix;
        mHeight = mMetrix.heightPixels;
        mWidth = mMetrix.widthPixels;
        mDensity = mMetrix.densityDpi;
    }

    static public ScreenInfo getDeviceSize(Activity aActivity) {
        DisplayMetrics sDM = new DisplayMetrics();
        aActivity.getWindowManager().getDefaultDisplay().getMetrics(sDM);
        return new ScreenInfo(sDM);
    }

    static public double getScreenInches(Activity aActivity) {
        double sScreenInches = 0;
        ScreenInfo sSize = ScreenInfo.getDeviceSize(aActivity);
        if (sSize != null) {
            double wi = (double) sSize.getWidth() / (double) sSize.getDensity();
            double hi = (double) sSize.getHeight() / (double) sSize.getDensity();
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            sScreenInches = Math.sqrt(x + y);
        }

        return sScreenInches;
    }

    public static int getScreenSizeType(Context aContext) {
        return aContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
    }
}
