package com.martian.bpa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class GoodsItemViewWithGridAdapter
        extends BaseAdapter
        implements View.OnClickListener, View.OnLongClickListener{
    public static final int TAG_RESULT_CODE_DELETE = 2;
    public static final int TAG_RESULT_CODE_CANCEL = 1;
    public static final int TAG_SCREENCOLOR_DESELECTED = 0x0000FFFF;
    public static final int TAG_SCREENCOLOR_SELECTED = Color.CYAN;

    interface OnClickedCallback{
        void onLongClickedCallback(boolean aItemSelected, int aItemCount);
        void onClickedCallback(boolean aSelect, boolean aAddItem);
    }

    class CheckedBoxItem implements Comparable<CheckedBoxItem>{
        public CheckedBoxItem(long aID) {
            mGoodsId = aID;
            mCheckState = false;
        }
        private long mGoodsId;
        private boolean mCheckState;

        public long getGoodsId() {
            return mGoodsId;
        }
        public boolean getCheckState() {
            return mCheckState;
        }
        public void setCheckState(boolean aVal) {
            Log.d(LOG_TAG, "setCheckState + " + aVal + ", " + mCheckState);
            mCheckState = aVal;
        }

        @Override
        public int compareTo(CheckedBoxItem another) {
            if (mGoodsId == another.getGoodsId()) return 0;
            else if(mGoodsId > another.getGoodsId()) return 1;
            return -1;
        }
    }

    class CtrlHolder{
        public CtrlHolder(){
        }
        ImageView      mGoodsImage;
        TextView       mTitle;
        TextView       mCheckedPrice;
        TextView       mLastPrice;
        TextView       mLastPriceResult;
        TextView       mLowPrice;
        CheckBox       mChecked;
        TextView       mGoodsID;
        View           mBackGroundView;
        GoodsItem      mGoodItem;
    }

    ArrayList<GoodsItem> mGoodsItems = new ArrayList<GoodsItem>();
    ArrayList<CheckedBoxItem> mCheckboxCols = new ArrayList<CheckedBoxItem>();
    ArrayList<GoodsItem> mSelectedItems = new ArrayList<GoodsItem>();

    private boolean mFlag4ForcedSelStateReset = false;
    private boolean mAllCheckBoxShowed = false;
    private Bitmap  mAddBitmap = null;

    GoodsItemViewWithGridAdapter mAdapter;
    Activity mParentActivity;
    OnClickedCallback mClickedCB = null;

    public ArrayList<GoodsItem> getSelectedItems(){return mSelectedItems;}

    public void onLongClickedCallback() {
        mAllCheckBoxShowed = false;
        notifyDataSetChanged();
    }

    private int getCBItemNoByID(long aID) {
        int sCnt = 0;
        for (CheckedBoxItem s:mCheckboxCols)
        {
            if (s.getGoodsId() == aID)
                break;
            else
                sCnt++;
        }
        return sCnt;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cb_checked)
        {
            CheckBox sCB = (CheckBox)v;
            CtrlHolder sHolder = (CtrlHolder)sCB.getTag();
            GoodsItem sItem = (GoodsItem)sHolder.mGoodItem;

            int sCBPos = getCBItemNoByID(sItem.getProductId());
            if (sCBPos >= 0) {
                mCheckboxCols.get(sCBPos).setCheckState(sCB.isChecked());
                if (sCB.isChecked() == true) {
                    if (Collections.binarySearch(mSelectedItems, sItem) < 0) {
                        mSelectedItems.add(sItem);
                        mClickedCB.onLongClickedCallback(
                                true, mSelectedItems.size());
                    }
                } else {
                    mSelectedItems.remove(sItem);
                    if (mSelectedItems.size() == 0) {
                        mAllCheckBoxShowed = false;
                        mClickedCB.onLongClickedCallback(false, 0);
                        notifyDataSetChanged();
                    }
                }
            }
        }
        else if (v.getId() == R.id.iv_goodsimage) {
            mClickedCB.onClickedCallback(false, true);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ////////////////////////////////////////////////
        // GridView의 아이템이 편집 모드로 바뀜.
        CtrlHolder sHolder = (CtrlHolder)v.getTag();
        Log.d(LOG_TAG, "LongClick + " + mAllCheckBoxShowed);

        if (mAllCheckBoxShowed == false) {
            mAllCheckBoxShowed = true;
            notifyDataSetChanged();
        } else {
            if (mSelectedItems.size() == 0) {
                mAllCheckBoxShowed = false;
                mClickedCB.onLongClickedCallback(false, 0);
                notifyDataSetChanged();
            }
        }
        return true;
    }

    public GoodsItemViewWithGridAdapter(Activity aActivity, OnClickedCallback aCB) {
        mAdapter = this;
        mParentActivity = aActivity;
        mClickedCB = aCB;

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
            mCheckboxCols.add(new CheckedBoxItem(aItem.getProductId()));
            mGoodsItems.add(0, aItem);
        }
    }

    public void addItems(ArrayList<GoodsItem> aItems) {
        for (GoodsItem sItem : aItems) {
            if (Collections.binarySearch(mGoodsItems, sItem) < 0 ) {
                mCheckboxCols.add(new CheckedBoxItem(sItem.getProductId()));
                mGoodsItems.add((mGoodsItems.size()-1),sItem);
            }
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

    public void removeItemByID(Long aID) {
        for (GoodsItem sItem : mGoodsItems) {
            if (sItem.getProductId() == aID)
            {
                mGoodsItems.remove(sItem);
                break;
            }
        }
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
    private void changeVisible(View aView, CtrlHolder aHolder, boolean aVisible)
    {
        changeVisible(aView,aHolder,aVisible, false);
    }

    private void changeVisible(View aView, CtrlHolder aHolder, boolean aVisible, boolean aGone)
    {
        int sVisible = (aVisible==true?View.VISIBLE:View.INVISIBLE);
        if (aGone == true) {
            sVisible = View.GONE;
        }
        aHolder.mLowPrice.setVisibility(sVisible);
        aHolder.mLastPrice.setVisibility(sVisible);
        aHolder.mGoodsID.setVisibility(sVisible);
        aHolder.mCheckedPrice.setVisibility(sVisible);
        aHolder.mLastPriceResult.setVisibility(sVisible);
        aView.findViewById(R.id.tv_first_price_title).setVisibility(sVisible);
        aView.findViewById(R.id.tv_last_price_title).setVisibility(sVisible);
        aView.findViewById(R.id.tv_preprice_title).setVisibility(sVisible);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CtrlHolder sHolder;
        final DBUtil     sDBUtil;

        final GoodsItem sData = mGoodsItems.get(position);
        //Log.d(LOG_TAG, sData.toStringWithExt());

        if (convertView == null) {
            sHolder = new CtrlHolder();
            LayoutInflater inflater = (LayoutInflater) mParentActivity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            if (sData.getImage().equals("plus")) {
                convertView = inflater.inflate(R.layout.goodsitemviewingriddoubleonlyimage, null);
            } else {
                convertView = inflater.inflate(R.layout.goodsitemviewingriddouble, null);
            }

            sHolder.mCheckedPrice = (TextView) convertView.findViewById(R.id.tv_user_checked_price);
            sHolder.mLastPrice = (TextView) convertView.findViewById(R.id.tv_last_price);
            sHolder.mLastPriceResult = (TextView) convertView.findViewById(R.id.tv_last_price_result);
            sHolder.mGoodsImage = (ImageView) convertView.findViewById(R.id.iv_goodsimage);
            sHolder.mLowPrice = (TextView) convertView.findViewById(R.id.tv_first_price);
            sHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
            sHolder.mChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);
            sHolder.mGoodsID = (TextView) convertView.findViewById(R.id.tv_goodsid);
            sHolder.mBackGroundView = convertView;
            convertView.setOnLongClickListener(this);
            sHolder.mChecked.setOnClickListener(this);

            convertView.setTag(sHolder);
        }
        else
        {
            sHolder =  (CtrlHolder)convertView.getTag();
        }

        sHolder.mTitle.setText(sData.getTitle());

        if (sData.getItemType() == GoodsItem.ITEM_TYPE_ITEM) {
            sDBUtil = new DBUtil(convertView.getContext());
            try {
                sHolder.mGoodsImage.setImageBitmap(sDBUtil.readGoodsImage(sData.getProductId()));
            } catch (DBUtil.DBUtilLoadImageNullException e) {
                new ImageLoader(new MainActivity.ImageLoadrInterface() {
                    @Override
                    public void ImageLoad(Bitmap aBmp) {
                        sHolder.mGoodsImage.setImageBitmap(aBmp);
                        sDBUtil.insertGoodsImage(aBmp, sData.getProductId());
                    }
                }, sData.getImage());
            }
            sHolder.mLowPrice.setText(String.valueOf(sData.getLowPrice()));
            sHolder.mLastPrice.setText(String.valueOf(sData.getLastPrice()));
            sHolder.mChecked.setTag(sHolder);
            sHolder.mGoodsID.setText(String.valueOf(sData.getProductId()));
            changeVisible(convertView, sHolder, true);

            sHolder.mGoodItem = sData;

            sHolder.mChecked.setVisibility(mAllCheckBoxShowed == true ? View.VISIBLE : View.INVISIBLE);

            if ( mAllCheckBoxShowed == true) {
                //sHolder.mChecked.setFocusable(false);
                int sCBPos = Collections.binarySearch(
                        mCheckboxCols,
                        new CheckedBoxItem(sData.getProductId()));

                if (sCBPos >= 0) {
                    sHolder.mChecked.setChecked(mCheckboxCols.get(sCBPos).getCheckState());
                } else {
                    sHolder.mChecked.setChecked(false);
                }
            }
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
            Log.d(LOG_TAG, "ITEM_TYPE_ITEM NONE title=" + sData.getTitle());

            sHolder.mTitle.setVisibility(View.GONE);
            sHolder.mChecked.setVisibility(View.GONE);
            sHolder.mChecked.setTag(sData);
            changeVisible(convertView, sHolder, false, true);
            convertView.setBackgroundColor(0xF0EFEFEE);

            LinearLayout.LayoutParams sParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
            sHolder.mGoodsImage.setLayoutParams(sParams);
            sHolder.mGoodsImage.setImageBitmap(mAddBitmap);
            sHolder.mGoodsImage.setOnClickListener(this);
            //sHolder.mGoodsImage.setBackgroundResource(R.color.trans);
            sHolder.mGoodsImage.setBackgroundColor(0xFFFFFF); //0xF0EFEFEE
            //sHolder.mGoodsImage.getBackground().setAlpha(50);
            sHolder.mGoodsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        return convertView;
    }
}
/*
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
        */