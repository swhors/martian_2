package com.martian.bpa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.Define;
import com.martian.bpa.util.ScreenInfo;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;

import java.util.ArrayList;

/////////////////////////////////////////////////////////////////
// MainActivity Class
public class GoodsItemViewWithGridActivity
        extends AppCompatActivity
        implements View.OnClickListener {
    // Definition
    private static final String LOG_TAG = "GoodsItemViewWithGridActivity";
    private static final String CLASS_NAME="com.martian.bpa.GoodsItemViewWithGridActivity";
    public  static boolean      mForcedRedraw = false;

    // Member
    private GridView                     mActiveGoodsViews       = null;
    private GoodsItemViewWithGridAdapter mActiveGoodsViewAdapter = null;


    GoodsItemViewWithGridAdapter.OnClickedCallback mOnClickCB
            = new GoodsItemViewWithGridAdapter.OnClickedCallback() {
        @Override
        public void onLongClickedCallback(boolean aItemSelected, int aItemCount) {
            onLongClickedCallback(aItemSelected, aItemCount);
        }

        @Override
        public void onClickedCallback(boolean aSelect, boolean aAddItem) {
            onClickedCallback(aSelect, aAddItem);
        }
    };

    private ImageButton mDeleteBtn      = null;
    private ImageButton mDeselectBtn    = null;
    private DBUtil      mMainDB         = null;
    private boolean     mItemChanged    = false;
    private boolean     mShowGarbageBtn = false;
    private boolean     mIsTablet       = false;

    //////////////////////////////////////////////////////////////////
    // Gettor
    public boolean getIsTablet() {
        try {
            return MainActivity.isTablet();
        } catch (MainActivity.MainActivityNullException e) {
            e.printStackTrace();
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////
    // Settor
    public void setIsTablet(boolean aIsTablet){mIsTablet=aIsTablet;}

    //////////////////////////////////////////////////////////////////
    // Constructor
    public GoodsItemViewWithGridActivity() {
    }

    //////////////////////////////////////////////////////////////////
    // Initialization for Dialog Controller.
    private void initCtl() {
        setContentView(R.layout.goodsitemviewwithgrid);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));

        mIsTablet = (ScreenInfo.getScreenSizeType(this)>=
                       Configuration.SCREENLAYOUT_SIZE_LARGE?true:false);

        ////////////////////////////////////////////////////////////////////
        // GridView
        // 2) settup 4 gridview
        mActiveGoodsViews = (GridView)findViewById(R.id.gv_goodsgridview);
        // set gridviewer's columnnum
        mActiveGoodsViews.setNumColumns(
                mIsTablet == true ?
                        Define.TAG_4_GRIDVIEW_COLUMN_IN_TABLET :
                        Define.TAG_4_GRIDVIEW_COLUMN_IN_PHONE);

        mActiveGoodsViewAdapter = new GoodsItemViewWithGridAdapter(this, mOnClickCB);
        mActiveGoodsViews.setAdapter(mActiveGoodsViewAdapter);

        mDeleteBtn = (ImageButton)findViewById(R.id.btn_top_delete);
        mDeleteBtn.setOnClickListener(this);
        mDeselectBtn = (ImageButton)findViewById(R.id.btn_top_deselect);
        mDeselectBtn.setOnClickListener(this);
        ((ImageButton)findViewById(R.id.btn_top_search)).setOnClickListener(this);
    }

    ////////////////////////////////////////////////////////////////////////
    // Initialization for Data.
    //
    private void initData()
    {
        mMainDB = DBUtil.getInstance(this);
    }

    /////////////////////////////////////////////////////////////////
    // Initialization for Dialog View.
    private void initView()
    {
        mActiveGoodsViewAdapter.addItems((ArrayList<GoodsItem>) mMainDB.readAll(false));
        mActiveGoodsViewAdapter.notifyDataSetChanged();
    }

    //////////////////////////////////////////////////////////////////
    // Overrided operator
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.d(LOG_TAG, "onBackPressed + " + mItemChanged);
        if (GoodsItemViewWithGridActivity.mForcedRedraw == true) {
            Util.setRedrawFlag(getIntent());
            GoodsItemViewWithGridActivity.mForcedRedraw = false;
        }
        setResult(mItemChanged == true ? RESULT_OK : RESULT_CANCELED);
        finishActivity(Define.ACTIVITY_REQ_GOODSVIEWINGRID);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResume(){
        Log.d(LOG_TAG, "onResume + 0");
        super.onResume();
        if (GoodsItemViewWithGridActivity.mForcedRedraw == true)
        {
            Log.d(LOG_TAG, "onResume + 1");
            mActiveGoodsViewAdapter.removeAllItem();
            mActiveGoodsViewAdapter.addItems((ArrayList<GoodsItem>) mMainDB.readAll(false));
            mActiveGoodsViewAdapter.notifyDataSetChanged();
            GoodsItemViewWithGridActivity.mForcedRedraw = false;
            Util.setRedrawFlag(getIntent());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCtl();
        initData();
        initView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_top_delete) {
            ArrayList<GoodsItem> sSelItems = mActiveGoodsViewAdapter.getSelectedItems();
            if (sSelItems.size() > 0) {
                for (GoodsItem sItem : sSelItems) {
                    mMainDB.removeGoods(sItem.getProductId());
                    mActiveGoodsViewAdapter.removeItem(sItem);
                }
                sSelItems.clear();
                mActiveGoodsViewAdapter.notifyDataSetChanged();
                Util.setRedrawFlag(getIntent());
            }
            mDeleteBtn.setVisibility(View.INVISIBLE);
            mDeselectBtn.setVisibility(View.INVISIBLE);
            mShowGarbageBtn = false;
            mActiveGoodsViewAdapter.onLongClickedCallback();
            mItemChanged = true;
        } else if (v.getId() == R.id.btn_top_deselect) {
            ////////////////////////////////////////////////////
            // 선택 해제 버튼이 클릭 된 경우,
            // 1) 삭제큐를 비운다.
            // 2) 그리드 뷰를 갱신한다.
            // 3) 선택해제버튼과 삭제버튼을 숨긴다.
            // 4) 그리드 뷰 아이템의 체크박스를 숨긴다..
            //
            ArrayList<GoodsItem> sSelItems = mActiveGoodsViewAdapter.getSelectedItems();
            if (sSelItems.size() > 0) {
                sSelItems.clear();
            }
            mDeleteBtn.setVisibility(View.INVISIBLE);
            mDeselectBtn.setVisibility(View.INVISIBLE);
            mShowGarbageBtn = false;
            mActiveGoodsViewAdapter.onLongClickedCallback();
        } else if (v.getId() == R.id.btn_top_search) {
            Intent sIntent = new Intent(GoodsItemViewWithGridActivity.this, GoodSearchActivity.class);
            sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
            startActivityForResult(
                    sIntent,
                    Define.ACTIVITY_REQ_GOODSEARCH);

        } else if (v.getId() == R.id.gv_goodsgridview)
        {
            /* do nothing.*/
        }
    }

    public void onLongClickedCallback(boolean aItemSelected, int aItemCount) {
        if (aItemSelected == true) {
            if (mShowGarbageBtn == false) {
                /////////////////////////////////////////
                // GridView의 아이템이 선택 된 경우,
                // Garbage 버튼을 활성화 시킨다.
                mShowGarbageBtn = true;
                mDeleteBtn.setVisibility(View.VISIBLE);
                mDeselectBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (mShowGarbageBtn == true) {
                //////////////////////////////////////////
                // GridView의 아이템의 선택이 해제 된 경우,
                // 3. GridView 아이템의 CheckBox를 히든으로 한다.
                mShowGarbageBtn = false;
                mDeleteBtn.setVisibility(View.INVISIBLE);
                mDeselectBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onClickedCallback(boolean aSelect, boolean aAddItem) {
        if (aAddItem == true) {
            Intent sIntent = new Intent(GoodsItemViewWithGridActivity.this, GoodSearchActivity.class);
            sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
            startActivityForResult(
                    sIntent,
                    Define.ACTIVITY_REQ_GOODSEARCH);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Define.ACTIVITY_REQ_GOODSEARCH) {
            if (resultCode == RESULT_OK) {
                GoodsItemViewWithGridActivity.mForcedRedraw = true;
            } else if (resultCode == RESULT_CANCELED) {
                /* do nothing. */
            }
        }
    }

}
