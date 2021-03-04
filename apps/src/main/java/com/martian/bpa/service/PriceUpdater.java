package com.martian.bpa.service;

import com.martian.bpa.MainActivity;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.dbutil.DBUtil;

/**
 * Created by USER on 11/19/2015.
 */
public class PriceUpdater {
    private DBUtil mDBUtil;

    public PriceUpdater(DBUtil aDBUtil) {
        mDBUtil = aDBUtil;
    }

    public GoodsItem createNewGoodsItem(GoodsItem aNewGoodsItem, GoodsItem aTrackingItem, boolean aNeedCopyLastPriceToUserCheckedPrice) {
        GoodsItem sGoodsItem = aNewGoodsItem;
        if (aNeedCopyLastPriceToUserCheckedPrice) {
            sGoodsItem.setUserCheckedPrice(aTrackingItem.getLastPrice());
        } else {
            sGoodsItem.setUserCheckedPrice(aTrackingItem.getUserCheckedPrice());
        }
        sGoodsItem.setLastPrice(aNewGoodsItem.getLowPrice());

        return sGoodsItem;
    }

    public void updateNewPrice(GoodsItem aGoodsItem) {
        mDBUtil.updateGoods(aGoodsItem);
    }
}
