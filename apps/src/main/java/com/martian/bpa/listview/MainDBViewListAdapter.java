/***********************************************************************
 * Created by simpson on 15. 11. 7.
 * DBViewListAdapter.java
 *
 * 내용 :
 *     DBDataListView 안의 리스트 뷰의 리스트 어댑터 구현 클래스.
 ***********************************************************************/

package com.martian.bpa.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.martian.bpa.MainActivity;
import com.martian.bpa.R;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;
import com.martian.bpa.util.loader.ImageLoader;

import java.util.ArrayList;

public class MainDBViewListAdapter extends BaseAdapter{
    private static final String LOG_TAG = "MainDBViewListAdapter";

    private Context mCtx;

    private class ViewHolder
    {
        ImageView mGoodsImage;
        TextView  mTitleView;
        TextView  mFirstPriceView;
        TextView  mUserCheckedPrice;
        TextView  mLastPrice;
        TextView  mLastPriceResult;
    }

    private ArrayList<GoodsItem> mGoodsCols;

    private enum PriceState {
        PRICE_EQUALS,
        PRICE_UP,
        PRICE_DOWN
    }

    public MainDBViewListAdapter(Context aCtx)
    {
        super();
        mCtx = aCtx;
        mGoodsCols = new ArrayList<GoodsItem>();
    }

    public void addData(GoodsItem aGoods) {
        mGoodsCols.add(aGoods);
    }

    public void addData(GoodsItem [] aGoodsCols) {
        for (GoodsItem sGoods : aGoodsCols)
        {
            //Log.d(LOG_TAG, "addData\n" + sGoods.toString());
            mGoodsCols.add(sGoods);
        }
    }

    public void removeData(GoodsItem aGoods)
    {
        mGoodsCols.remove(aGoods);
    }

    public void removeData(int aPosition)
    {
        mGoodsCols.remove(aPosition);
    }

    public void removeAllData()
    {
        mGoodsCols.clear();
    }

    private PriceState getPriceState(GoodsItem aData) {
        PriceState sPriceState = PriceState.PRICE_EQUALS;
        if (aData.getUserCheckedPrice() < aData.getLastPrice()) {
            sPriceState = PriceState.PRICE_UP;
        } else if (aData.getUserCheckedPrice() > aData.getLastPrice()) {
            sPriceState = PriceState.PRICE_DOWN;
        }

        return sPriceState;
    }

    private String getLastPriceResultString(PriceState aPriceState) {
        String sLastPriceResult = "";
        if (aPriceState == PriceState.PRICE_UP) {
            sLastPriceResult = " (UP)";
        } else if (aPriceState == PriceState.PRICE_DOWN) {
            sLastPriceResult = " (DOWN)";
        }

        return sLastPriceResult;
    }

    private int geTextColor(PriceState aPriceState) {
        int sTextColor = Color.GRAY;
        if (aPriceState == PriceState.PRICE_UP) {
            sTextColor = Color.RED;
        } else if (aPriceState == PriceState.PRICE_DOWN) {
            sTextColor = Color.BLUE;
        }

        return sTextColor;
    }

    ////////////////////////////////////////////////////////////////////////////
    // gettor
    @Override
    public int getCount() {
        return mGoodsCols.size();
    }
    @Override
    public Object getItem(int aPosition) {
        return mGoodsCols.get(aPosition);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public ArrayList<GoodsItem> getGoodsCols() {return mGoodsCols;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder sHolder;

        DBUtil sDbUtil = null;

        final GoodsItem sData = mGoodsCols.get(position);

        if (convertView == null) {
            sHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem4maindbinfo, null);

            sHolder.mTitleView = (TextView) convertView.findViewById(R.id.tv_title);
            sHolder.mFirstPriceView = (TextView) convertView.findViewById(R.id.tv_first_price);
            sHolder.mUserCheckedPrice = (TextView) convertView.findViewById(R.id.tv_user_checked_price);
            sHolder.mLastPrice = (TextView) convertView.findViewById(R.id.tv_last_price);
            sHolder.mLastPriceResult = (TextView) convertView.findViewById(R.id.tv_last_price_result);
            sHolder.mGoodsImage = (ImageView) convertView.findViewById(R.id.iv_goodsimage);
            ///////////////////////////////////////////////////////////////////
            // Only used in Debug.If you want to know real data, unmark below.
            // Log.d(LOG_TAG, "Data :" + sData.toString());

            convertView.setTag(sHolder);
        }
        else
        {
            sHolder = (ViewHolder) convertView.getTag();
        }

        PriceState sPriceState = getPriceState(sData);

        sHolder.mTitleView.setText(sData.getTitle());
        sHolder.mFirstPriceView.setText(Util.toCurrency(mCtx, sData.getLowPrice()));
        sHolder.mUserCheckedPrice.setText(Util.toCurrency(mCtx, sData.getUserCheckedPrice()));
        sHolder.mLastPrice.setText(Util.toCurrency(mCtx, sData.getLastPrice()));
        sHolder.mLastPrice.setTextColor(geTextColor(sPriceState));
        sHolder.mLastPriceResult.setText(getLastPriceResultString(sPriceState));
        sHolder.mLastPriceResult.setTextColor(geTextColor(sPriceState));

        try {
            sDbUtil = MainActivity.getMainDB();

            sHolder.mGoodsImage.setImageBitmap(
                    MainActivity.
                            getMainDB().
                            readGoodsImage(
                                    sData.getProductId()));
            Log.d(LOG_TAG, "getView, success");
        } catch (MainActivity.MainActivityNullException e) {
            Log.d(LOG_TAG, e.getMessage());
            ImageLoader sImageLoader = new ImageLoader(
                    new MainActivity.ImageLoadrInterface() {
                        @Override
                        public void ImageLoad(Bitmap aBmp) {
                            sHolder.mGoodsImage.setImageBitmap(aBmp);
                        }
                    }, sData.getImage());
            sImageLoader.execute();
        } catch (DBUtil.DBUtilLoadImageNullException e) {
            Log.d(LOG_TAG, e.getMessage());
            final DBUtil finalSDbUtil = sDbUtil;
            ImageLoader sImageLoader = new ImageLoader(
                    new MainActivity.ImageLoadrInterface() {
                        @Override
                        public void ImageLoad(Bitmap aBmp) {
                            sHolder.mGoodsImage.setImageBitmap(aBmp);
                            Log.d(LOG_TAG, "getView " + "DBUtilLoadImageNullException");
                            finalSDbUtil.insertGoodsImage(aBmp, sData.getProductId());
                        }
                    }, sData.getImage());
            sImageLoader.execute();
        }
        return convertView;
    }
}
