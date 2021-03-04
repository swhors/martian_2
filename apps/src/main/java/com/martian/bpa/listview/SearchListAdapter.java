/***********************************************************************
 * Created by simpson on 15. 11. 4.
 * SearchListAdapter.java
 *
 * 내용 :
 *     MainActivity 안의 리스트 뷰의 리스트 어댑터 구현 클래스.
 ***********************************************************************/

package com.martian.bpa.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.martian.bpa.R;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.ScreenInfo;
import com.martian.bpa.util.Util;

import java.net.URL;
import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter {

    private static final String LOG_TAG = "SearchListAdpter";

    class ViewHolder
    {
        TextView  mTvTitle;
        TextView  mTvLowPrice;
    }

    public Context mContext = null;

    ArrayList<GoodsItem> mGoodsCols = new ArrayList<GoodsItem>();

    public SearchListAdapter(Context aCtx)
    {
        super();
        mContext = aCtx;
    }

//    private void insertHeader()
//    {
//        GoodsItem sGoodsItem = new GoodsItem();
//        sGoodsItem.setTitle("Title");
//        sGoodsItem.setLowPrice(0);
//        mGoodsCols.add(sGoodsItem);
//    }

    public void removeAll(boolean aRefresh)
    {
        mGoodsCols.clear();
        if (aRefresh==true) {
//            insertHeader();
            notifyDataSetChanged();
        }
    }

    public void refreshView()
    {
//        if (mGoodsCols.size() == 0)
//        {
//            insertHeader();
//        }
        notifyDataSetChanged();
        //Log.d(LOG_TAG, "refreshView");
    }

    public boolean addData(ArrayList<GoodsItem> aItem, boolean aRefresh)
    {
        if (mGoodsCols.addAll(aItem) == true)
        {
            if (aRefresh == true)
            {
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public boolean addData(GoodsItem aItem, boolean aRefresh)
    {
        if (mGoodsCols.add(aItem) == true)
        {
            if (aRefresh == true)
            {
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder sHolder;
        URL        sUrl;
        Bitmap     sBmp;

        GoodsItem sGoodItem = mGoodsCols.get(position);

        //Log.d(LOG_TAG, "getView 0");
        if (convertView == null) {
            sHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            if (ScreenInfo.mDeviceType == ScreenInfo.DEVICE_TYPE.PHONE) {
                convertView = inflater.inflate(R.layout.search_list_item_phone, null);
            } else {
                convertView = inflater.inflate(R.layout.search_list_item_tablet, null);
            }

            sHolder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            sHolder.mTvLowPrice = (TextView) convertView.findViewById(R.id.tv_low_price);

            ///////////////////////////////////////////////////////////////////
            // Only used in Debug.If you want to know real data, unmark below.
            // Log.d(LOG_TAG, "Data :" + sData.toString());

            convertView.setTag(sHolder);
        }
        else
        {
            sHolder = (ViewHolder) convertView.getTag();
        }
        sHolder.mTvTitle.setText(sGoodItem.getTitle());
        sHolder.mTvLowPrice.setText(Util.toCurrency(mContext, sGoodItem.getLowPrice()));

        //Log.d(LOG_TAG, "getView 1\n" + sGoodItem.toString());

        return convertView;
    }
}
