package com.martian.bpa;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.service.PriceUpdater;

/**
 * Created by USER on 11/27/2015.
 */
public class DBViewListTest extends ApplicationTestCase<Application> {
    public DBViewListTest() {
        super(Application.class);
    }

    public void testPriceIfUserDidNotCheckPrice() {
        final int ORIGINAL_LOW_PRICE = 1000;
        final int USER_CHECKED_PRICE = 1000;
        final int LAST_LOW_PRICE = 900;

        PriceUpdater sPriceUpdater = new PriceUpdater(null);

        GoodsItem sNewItem = createGoodsItem("Item From Naver API", ORIGINAL_LOW_PRICE, 0, 0);
        GoodsItem sTrackingItem = createGoodsItem("Tracking Item", ORIGINAL_LOW_PRICE, USER_CHECKED_PRICE, LAST_LOW_PRICE);

        boolean mNeedCopyLastPriceToUserCheckedPrice = false;

        GoodsItem aGoodsItem = sPriceUpdater.createNewGoodsItem(sNewItem, sTrackingItem, mNeedCopyLastPriceToUserCheckedPrice);

        final int expected = USER_CHECKED_PRICE;
        final int actual = aGoodsItem.getUserCheckedPrice();

        assertEquals(expected, actual);
    }

    public void testPriceIfUserCheckedPrice() {
        final int ORIGINAL_LOW_PRICE = 1000;
        final int USER_CHECKED_PRICE = 1000;
        final int LAST_LOW_PRICE = 900;

        PriceUpdater sPriceUpdater = new PriceUpdater(null);

        GoodsItem sNewItem = createGoodsItem("Item From Naver API", ORIGINAL_LOW_PRICE, 0, 0);
        GoodsItem sTrackingItem = createGoodsItem("Tracking Item", ORIGINAL_LOW_PRICE, USER_CHECKED_PRICE, LAST_LOW_PRICE);

        boolean mNeedCopyLastPriceToUserCheckedPrice = true;

        GoodsItem aGoodsItem = sPriceUpdater.createNewGoodsItem(sNewItem, sTrackingItem, mNeedCopyLastPriceToUserCheckedPrice);

        final int expected = LAST_LOW_PRICE;
        final int actual = aGoodsItem.getUserCheckedPrice();

        assertEquals(expected, actual);
    }

    private GoodsItem createGoodsItem(String aTitle, int aLowPrice, int aUserCheckedPrice, int aLastPrice) {
        GoodsItem sGoodsItem = new GoodsItem(aTitle, /* Title */
                                             null, /* Link */
                                             null, /* Image */
                                             aLowPrice, /* LowPrice */
                                             aLowPrice * 2, /* HighPrice */
                                             null, /* MallName */
                                             0, /* ProductId */
                                             0 /* ProductType */);
        sGoodsItem.setUserCheckedPrice(aUserCheckedPrice);
        sGoodsItem.setLastPrice(aLastPrice);

        return sGoodsItem;
    }
}
