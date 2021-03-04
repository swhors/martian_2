package com.martian.bpa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.fivehundredpx.android.blur.BlurringView;

/**
 * Created by USER on 12/8/2015.
 */
public class BlurrableActivity extends Activity {
    public BlurViewInfo mBlurViewInfo = null;

    @Override
    protected void onResume() {
        if (mBlurViewInfo != null) {
            if (mBlurViewInfo.mBlurringView != null) {
                mBlurViewInfo.mBlurringView.setBlurredView(null);
                mBlurViewInfo.mBlurringView.invalidate();
            }
            mBlurViewInfo = null;
        }

        super.onResume();
    }

    public void startBlurBackgroundActivity(Intent aIntent, BlurViewInfo aBlurViewInfo) {
        BlurringView sBlurringView = aBlurViewInfo.mBlurringView;
        View blurredView = aBlurViewInfo.mBlurredView;
        blurredView.setBackgroundColor(Color.WHITE);
        sBlurringView.setBlurredView(blurredView);
        sBlurringView.invalidate();

        mBlurViewInfo = aBlurViewInfo;

        super.startActivity(aIntent);
    }

    protected class BlurViewInfo {
        private BlurringView mBlurringView;
        private View mBlurredView;

        public BlurViewInfo(BlurringView aBlurringView, View aBlurredView) {
            mBlurringView = aBlurringView;
            mBlurredView = aBlurredView;
        }
    }
}
