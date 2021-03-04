/***********************************************************************
 * Created by simpson on 15. 11. 7.
 * DBViewListAdapter.java
 *
 * 내용 :
 *     DBDataListView 안의 리스트 뷰의 리스트 어댑터 구현 클래스.
 ***********************************************************************/

package com.martian.bpa.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.martian.bpa.R;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.Util;

import java.util.ArrayList;

public class DBViewListAdapter extends BaseAdapter{
    private static final String LOG_TAG = "DBViewListAdapter";

    private Context mCtx;

    private class ViewHolder
    {
        TextView mTitleView;
        TextView mFirstPriceView;
//        TextView mMallView;
        TextView mUserCheckedPrice;
        TextView mLastPrice;
        TextView mLastUpdate;
        TextView mLastPriceResult;
    }

    private ArrayList<GoodsItem> mGoodsCols;

    public DBViewListAdapter(Context aCtx)
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

    private int getChanges(int sOriginalPrice, int aNewPrice) {
        return aNewPrice - sOriginalPrice;
    }

    private String getLastPriceResultString(int aChanges) {
        String sLastPriceResult = "";
        if (aChanges > 0) {
            sLastPriceResult = " (" + Util.toCurrency(mCtx, aChanges) + " UP)";
        } else if (aChanges < 0) {
            sLastPriceResult = " (" + Util.toCurrency(mCtx, aChanges) + " DOWN)";
        }

        return sLastPriceResult;
    }

    private int geTextColor(int aChanges) {
        int sTextColor = Color.GRAY;
        if (aChanges > 0) {
            sTextColor = Color.RED;
        } else if (aChanges < 0) {
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
        ViewHolder sHolder;

        GoodsItem sData = mGoodsCols.get(position);

        if (convertView == null) {
            sHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem4dbinfo, null);

            sHolder.mTitleView = (TextView) convertView.findViewById(R.id.tv_title);
//            sHolder.mMallView = (TextView) convertView.findViewById(R.id.tv_mallname);
            sHolder.mFirstPriceView = (TextView) convertView.findViewById(R.id.tv_first_price);
            sHolder.mUserCheckedPrice = (TextView) convertView.findViewById(R.id.tv_user_checked_price);
            sHolder.mLastPrice = (TextView) convertView.findViewById(R.id.tv_last_price);
//            sHolder.mLastUpdate = (TextView) convertView.findViewById(R.id.tv_last_update);
            sHolder.mLastPriceResult = (TextView) convertView.findViewById(R.id.tv_last_price_result);
            ///////////////////////////////////////////////////////////////////
            // Only used in Debug.If you want to know real data, unmark below.
            // Log.d(LOG_TAG, "Data :" + sData.toString());

            convertView.setTag(sHolder);
        }
        else
        {
            sHolder = (ViewHolder) convertView.getTag();
        }

        int sChanges = getChanges(sData.getUserCheckedPrice(), sData.getLastPrice());

        sHolder.mTitleView.setText(sData.getTitle());
        sHolder.mFirstPriceView.setText(Util.toCurrency(mCtx, sData.getLowPrice()));
        sHolder.mUserCheckedPrice.setText(Util.toCurrency(mCtx, sData.getUserCheckedPrice()));
        sHolder.mLastPrice.setText(Util.toCurrency(mCtx, sData.getLastPrice()));
        sHolder.mLastPrice.setTextColor(geTextColor(sChanges));
        sHolder.mLastPriceResult.setText(getLastPriceResultString(sChanges));
        sHolder.mLastPriceResult.setTextColor(geTextColor(sChanges));
        return convertView;
    }
}
