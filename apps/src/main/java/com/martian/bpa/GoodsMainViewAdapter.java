package com.martian.bpa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.dbutil.DBUtil;
import com.martian.bpa.util.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;

import static com.martian.bpa.napisearch.GoodsItem.LOG_TAG;

/**
 * Created by simpson on 15. 12. 2.
 */
public class GoodsMainViewAdapter
        extends BaseAdapter
        implements View.OnClickListener{

    class CtrlHolder{
        ImageView   mGoodsImage;
        TextView    mTitle;
        TextView    mLastPriceResult;
    }

    private ArrayList<GoodsItem> mGoodsItems = new ArrayList<GoodsItem>();
    private DBUtil     mDBUtil = null;
    private GoodsMainViewAdapter mAdapter;
    private Activity mParentActivity;
    private Bitmap mAddBitmap;

    private int    mHeigth = 0;
    private int    mWidth  = 0;

    public GoodsMainViewAdapter(Activity aActivity) {
        mAdapter = this;
        mParentActivity = aActivity;

        mGoodsItems.add(
                new GoodsItem(
                        "상품추가",
                        "",
                        "plus",
                        0,
                        0,
                        "BPA",
                        0,
                        0,
                        GoodsItem.ITEM_TYPE_NONE));
    }

    @Override
    public int getCount() {
        return mGoodsItems.size();
    }

    public void addItem(GoodsItem aItem) {
        if (Collections.binarySearch(mGoodsItems, aItem) < 0)
        {
            mGoodsItems.add(0, aItem);
        }
    }

    public void addItems(ArrayList<GoodsItem> aItems) {
        for (GoodsItem sItem : aItems) {
            if (Collections.binarySearch(mGoodsItems, sItem) < 0 )
                mGoodsItems.add((mGoodsItems.size()-1),sItem);
        }
    }

    public void removeAllItem() {
        mGoodsItems.clear();
        mGoodsItems.add(
                new GoodsItem(
                        "상품추가",
                        "",
                        "plus",
                        0,
                        0,
                        "BPA",
                        0,
                        0,
                        GoodsItem.ITEM_TYPE_NONE));
    }

    public void removeItem(GoodsItem aItem) {
        mGoodsItems.remove(aItem);
    }

    public void removeItem(int aPosition) {
        if (mGoodsItems.size() > 0) {
            mGoodsItems.remove(aPosition);
        }
    }

    @Override
    public void onClick(View v) {
        MainActivity.showSearchDlg();
    }

    @Override
    public Object getItem(int position) {
        return mGoodsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItemByID(Long aID) {
        Object sRetval = null;
        for (GoodsItem sItem : mGoodsItems) {
            if (sItem.getProductId() == aID)
            {
                sRetval = sItem;
                break;
            }
        }
        return sRetval;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CtrlHolder sHolder;
        final GoodsItem sData = mGoodsItems.get(position);
        Log.d(LOG_TAG, sData.toStringWithExt());

        if (convertView == null) {
            sHolder = new CtrlHolder();
            LayoutInflater inflater = (LayoutInflater) mParentActivity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.goodsitemviewingridsingle, null);

            sHolder.mLastPriceResult = (TextView) convertView.findViewById(R.id.tv_last_price_result);
            sHolder.mGoodsImage = (ImageView) convertView.findViewById(R.id.iv_goodsimage);
            sHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(sHolder);
        }
        else
        {
            sHolder =  (CtrlHolder)convertView.getTag();
        }

        sHolder.mTitle.setText(sData.getTitle());
        if (sData.getItemType() == GoodsItem.ITEM_TYPE_ITEM) {
            if (mDBUtil==null) mDBUtil = new DBUtil(convertView.getContext());
            try {
                sHolder.mGoodsImage.setImageBitmap(mDBUtil.readGoodsImage(sData.getProductId()));
            } catch (DBUtil.DBUtilLoadImageNullException e) {
                new ImageLoader(new MainActivity.ImageLoadrInterface() {
                    @Override
                    public void ImageLoad(Bitmap aBmp) {
                        sHolder.mGoodsImage.setImageBitmap(aBmp);
                        mDBUtil.insertGoodsImage(aBmp, sData.getProductId());
                    }
                }, sData.getImage());
            }
            mWidth = convertView.getWidth();
            mHeigth = convertView.getHeight();
        } else {
            if (mAddBitmap == null)
            {
                mAddBitmap = BitmapFactory.decodeResource(
                        mParentActivity.getResources(),
                        mParentActivity.getResources().getIdentifier(
                                sData.getImage(),
                                "drawable",
                                mParentActivity.getPackageName()
                        )
                );
            }

            sHolder.mTitle.setVisibility(View.GONE);
            sHolder.mLastPriceResult.setVisibility(View.GONE);
            convertView.setBackgroundColor(0xF0EFEFEE);
            RelativeLayout.LayoutParams sParams =
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            sParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            sHolder.mGoodsImage.setLayoutParams(sParams);
            sHolder.mGoodsImage.setImageBitmap(mAddBitmap);
            sHolder.mGoodsImage.setBackgroundColor(0xF0EFEFEE);
            sHolder.mGoodsImage.setOnClickListener(this);
            sHolder.mGoodsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            sHolder.mGoodsImage.setPadding(30, 30, 30, 30);

        }
        return convertView;
    }
}
