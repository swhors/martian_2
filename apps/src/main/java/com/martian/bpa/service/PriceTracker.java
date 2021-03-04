package com.martian.bpa.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.martian.bpa.MainActivity;
import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.napisearch.NaverApi;
import com.martian.bpa.napisearch.SearchResultShop;
import com.martian.bpa.util.UiUtil;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;

import java.util.ArrayList;

/**
 * Created by USER on 11/17/2015.
 */
public class PriceTracker {
    private static String TAG = "BPA.PriceTracker";

    private static final int DEFAULT_NAPI_RESULT_NUM = 10;

    private static final int MESSAGE_SEARCH_REQUEST = 0;
    private static final int MESSAGE_SEARCH_RESPONSE = 1;

    private SearchHandler mSearchHandler = null;

    private Context mContext;
    private ServiceController mServiceController;
    private GoodsItem[] mTrackingItem;
    private int mTrackingIndex = 0;
    private NaverApi mNaverSearch;
    private TrackingResult mTrackingResult;
    private PriceUpdater mPriceUpdater;
    private boolean mNeedCopyLastPriceToUserCheckedPrice;

    public PriceTracker(Context aContext, ServiceController aServiceController) {
        mContext = aContext;
        mServiceController = aServiceController;
        initVars(aContext);
    }

    private void initVars(Context aContext) {
        mSearchHandler = new SearchHandler();
        mTrackingItem = (GoodsItem[])DBUtil.getInstance(aContext).readAll(true);
        mTrackingResult = new TrackingResult(mTrackingItem.length);
        mPriceUpdater = new PriceUpdater(DBUtil.getInstance(aContext));
        mNeedCopyLastPriceToUserCheckedPrice = Util.getProperty(mContext, PropertySet.TAG_CONF_IS_USER_CHECKED_PRICE, false);
    }

    public void compare() {
        if (mTrackingItem.length > 0) {
            requestNAPI(mTrackingItem[0].getTitle());
        } else {
            mServiceController.finish();
        }
    }

    private NaverApi createNAPI() {
        return new NaverApi(new MainActivity.Callback() {
            @Override
            public void onDoPostCallback(SearchResultShop aResultList) {
                onCallbackOnNaverSearchApi(aResultList);
            }

            @Override
            public void onDoPostErrorMessage(String aMessage) {
                Log.i(TAG, "Error : " + aMessage);
            }
        }, "");
    }

    private void requestNAPI(String aTitle) {
        Log.i(TAG, "Request NAPI : " + aTitle);

        Message sMessage = new Message();
        sMessage.obj = aTitle;
        mSearchHandler.sendMessage(sMessage);
    }

    public void onCallbackOnNaverSearchApi(SearchResultShop aResult)
    {
        int sIndex = 0;
        ArrayList<GoodsItem> sGoodsArray = null;
        final GoodsItem sTrackingItem = mTrackingItem[mTrackingIndex];

        try {
            sGoodsArray = aResult.getItemByArrayList();

            if (sGoodsArray.size() > 0) {
                for (GoodsItem sNewGoodsItem : sGoodsArray) {
                    if (sNewGoodsItem.getProductId() != sTrackingItem.getProductId()) {
                        continue;
                    }

                    Log.i(TAG, "Response NAPI Idx : " + sIndex++ + ", Title : " + sNewGoodsItem.getTitle()
                            + ", LP : \\" + String.format("%,d", sNewGoodsItem.getLowPrice())
                            + ", (사용자확인 \\" + String.format("%,d", sTrackingItem.getUserCheckedPrice()) + ")");

                    mTrackingResult.setPrice(sNewGoodsItem.getLowPrice(), sTrackingItem.getUserCheckedPrice());

                    GoodsItem aUpdateGoodsItem = mPriceUpdater.createNewGoodsItem(sNewGoodsItem, sTrackingItem, mNeedCopyLastPriceToUserCheckedPrice);
                    mPriceUpdater.updateNewPrice(aUpdateGoodsItem);
                }
            }
        }
        catch (SearchResultShop.SearchResultNoItemException e)
        {
            Log.d(TAG, e.getMessage());
        }
        catch (SearchResultShop.SearchResultNullItemException e)
        {
            Log.d(TAG, e.getMessage());
        }

        mSearchHandler.sendEmptyMessage(MESSAGE_SEARCH_RESPONSE);
    }

    class SearchHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MESSAGE_SEARCH_REQUEST:
                    mNaverSearch = createNAPI();
                    mNaverSearch.execute((String) msg.obj, String.valueOf(PriceTracker.DEFAULT_NAPI_RESULT_NUM));
                    break;

                case MESSAGE_SEARCH_RESPONSE:
                    mTrackingIndex++;
                    if (mTrackingIndex < mTrackingItem.length) {
                        requestNAPI(mTrackingItem[mTrackingIndex].getTitle());
                    } else {
                        Log.i(TAG, mTrackingResult.createResultMessage());
                        UiUtil.showNotification(mContext, mTrackingResult.createResultMessage());

                        Util.setProperty(mContext, PropertySet.TAG_CONF_IS_USER_CHECKED_PRICE, false);

                        mServiceController.finish();
                    }
                    break;

                default:
                    break;
            }
        }
    };
}
