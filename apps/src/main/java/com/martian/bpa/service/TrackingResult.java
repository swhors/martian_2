package com.martian.bpa.service;

/**
 * Created by USER on 11/18/2015.
 */
public class TrackingResult {
    private int mTotalTrackingCount = 0;
    private int mPriceUpCount;
    private int mPriceDownCount;

    public TrackingResult(int aTotalTrackingCount) {
        mTotalTrackingCount = aTotalTrackingCount;
    }

    public void setPrice(int aNewPrice, int aOriginalPrice) {
        if (aNewPrice > aOriginalPrice) {
            mPriceUpCount++;
        } else if (aNewPrice < aOriginalPrice) {
            mPriceDownCount++;
        }
    }

    public String createResultMessage() {
        return "총 " + mTotalTrackingCount + "건 중 가격상승: " + mPriceUpCount + "건, 가격하락: " + mPriceDownCount + "건";
    }
}
