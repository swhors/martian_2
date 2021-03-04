package com.martian.bpa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.service.TrackingService;
import com.martian.bpa.util.DateUtil;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;
import com.martian.bpa.listview.DBViewListAdapter;
import com.martian.bpa.napisearch.GoodsItem;

public class DBDataListView extends AppCompatActivity {
    final private        String LOG_TAG       = "DBDataListView";
    final public  static String TAG_PARAMETER = "CallerType";

    static public final int TAG_CALLER_MAIN    = 1; // normal, MainActivity.
    static public final int TAG_CALLER_SERVICE = 2; // Service.

    private ListView          mListView;
    private DBViewListAdapter mAdapter;
    private int               mSelectedItem = -1;
    DBUtil                    mDBUtil;

    private int mCallerType = TAG_CALLER_MAIN;

    public int getCallerType(){return mCallerType;}
    public void setCallerType(int aType){mCallerType=aType;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbdatalist);

        if (mCallerType == TAG_CALLER_SERVICE)
        {

        }
        initCtrl();
        initData();
        initView();
    }

    void initData() {
        mDBUtil = DBUtil.getInstance(this);
        mCallerType = getIntent().getIntExtra(TAG_PARAMETER, TAG_CALLER_MAIN);
        Util.setProperty(this, PropertySet.TAG_CONF_IS_USER_CHECKED_PRICE, true);
    }

    void initView() {
//        mListView.setSelector(new PaintDrawable( 0xffff0000 ));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAdapter.addData((GoodsItem[])mDBUtil.readAll(true));
        mAdapter.notifyDataSetChanged();
    }

    void initCtrl() {
        mListView = (ListView)findViewById(R.id.list_4_datalist);
        mAdapter = new DBViewListAdapter(this);
        mListView.setAdapter(mAdapter);
        ///////////////////////////////////////////////////////////////////////////////
        // 3) set ItemClickListener.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final private String LOG_TAG = "AdapterView.OnItemClickListener";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = position;
                //Log.d(LOG_TAG, "mSelectedItem=" + mSelectedItem);
                //Log.d(LOG_TAG, mAdapter.getItem(mSelectedItem).toString());
            }
        });

        ((Button)findViewById(R.id.btn_delete)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedItem >= 0) {
                            GoodsItem sGoods = (GoodsItem) mAdapter.getItem(mSelectedItem);
                            mDBUtil.removeGoods(sGoods);
                            mAdapter.removeData(sGoods);
                            mAdapter.notifyDataSetChanged();
                            mSelectedItem = -1;
                            mListView.clearChoices();
                        }
                    }
                }
        );
        ((Button)findViewById(R.id.btn_update)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(LOG_TAG, "R.id.btn_update + setOnClickListener");
                        if (mSelectedItem >= 0) {
                            GoodsItem sGoods = (GoodsItem) mAdapter.getItem(mSelectedItem);
                            sGoods.setLastPrice(sGoods.getLastPrice() + 100);
                            sGoods.setLastUpdate(DateUtil.getCurrentDate());
                            Log.d(LOG_TAG, sGoods.toStringWithExt());
                            mDBUtil.updateGoods(sGoods);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        ((Button)findViewById(R.id.btn_test_up_price)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedItem >= 0) {
                            GoodsItem sGoods = (GoodsItem) mAdapter.getItem(mSelectedItem);
                            sGoods.setUserCheckedPrice(sGoods.getUserCheckedPrice() + 1000);
                            mDBUtil.updateGoodsForTest(sGoods);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        ((Button)findViewById(R.id.btn_test_down_price)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedItem >= 0) {
                            GoodsItem sGoods = (GoodsItem) mAdapter.getItem(mSelectedItem);
                            sGoods.setUserCheckedPrice(sGoods.getUserCheckedPrice() - 1000);
                            mDBUtil.updateGoodsForTest(sGoods);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        ((Button)findViewById(R.id.btn_test_tracking)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sIntent = new Intent(DBDataListView.this, TrackingService.class);
                        sIntent.setPackage("com.martian.bpa.SERVICE_START");
                        startService(sIntent);
                    }
                }
        );
        ((Button)findViewById(R.id.btn_test_no_user_checked)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.setProperty(DBDataListView.this, PropertySet.TAG_CONF_IS_USER_CHECKED_PRICE, false);
                    }
                }
        );
    }
}
