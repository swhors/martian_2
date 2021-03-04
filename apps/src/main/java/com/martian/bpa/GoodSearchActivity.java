package com.martian.bpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fivehundredpx.android.blur.BlurringView;
import com.martian.bpa.listview.SearchListAdapter;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.napisearch.NAPISearchShopTags;
import com.martian.bpa.napisearch.NaverApi;
import com.martian.bpa.napisearch.SearchResultShop;
import com.martian.bpa.util.Define;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

/////////////////////////////////////////////////////////////////
// MainActivity Class
public class GoodSearchActivity extends BlurrableActivity {
    // Definition
    private static final String LOG_TAG = "GoodSearchActivity";
    private static final String CLASS_NAME="com.martian.bpa.GoodSearchActivity";
    static public boolean mForcedRedraw = false;
    // Member
    private NaverApi          mNaverSearch;
    private ListView          mListView;
    private EditText          mSearchBox;
    private SearchListAdapter mAdapter;
    private int               mSelectedItem = -1;
    private static GoodSearchActivity mObj = null;
    private boolean mFirstTab;
    private long mFirstTime;
    private Method mSetFirstTab;
    private Method mSetFirstTime;

    private boolean mItemAdded = false;

    private ProgressDialog mProgressDialog;

    private DBUtil mMainDB;

    private BlurViewInfo mBlurViewInfo = null;

    ///////////////////////////////////////////////////////////////////////////////
    // Definition for callback.
    public interface Callback{
        void onDoPostCallback(SearchResultShop aResult);
        void onDoPostErrorMessage(String aMessage);
    }
    public interface ImageLoadrInterface{
        void ImageLoad(Bitmap aBmp);
    }

    public void setFirstTime(long aVal) { mFirstTime = aVal; }
    public void setFirstTab(boolean aVal) { mFirstTab = aVal; }

    ///////////////////////////////////////////////////////////////////////////////
    // Method for Callback Process.
    public void onCallbackOnNaverSearchApi(SearchResultShop aResult)
    {
        int sCount = 0;
        int sDataSize = 0;
        GoodsItem[]sGoods = null;
        ArrayList<GoodsItem> sGoodsArray = null;

        try {
            sGoods = aResult.getItem();
            sGoodsArray = aResult.getItemByArrayList();

            Log.d(LOG_TAG,
                    "ListCount  :" + mNaverSearch.getSearchCount());
            Log.d(LOG_TAG,
                    "First Data :" + (mNaverSearch.getSearchCount() > 0 ? sGoodsArray.get(0).toString() : " "));

            sDataSize = sGoodsArray.size();

            if (sDataSize <= 0) {
                (new AlertDialog.Builder(GoodSearchActivity.this)).
                        setMessage(mNaverSearch.getStatusText()).
                        setTitle("Error").
                        setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                        show();
            } else {
                for (GoodsItem sData : sGoods) {
                    mAdapter.addData(sData, false);
                    if (++sCount == sDataSize) break;
                }
                mAdapter.refreshView();
            }
        }
        catch (SearchResultShop.SearchResultNoItemException e)
        {
            Log.d(LOG_TAG, e.getMessage());
        }
        catch (SearchResultShop.SearchResultNullItemException e)
        {
            Log.d(LOG_TAG, e.getMessage());
        }
        mProgressDialog.dismiss();
    }
    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////
    // Initialization for Dialog Controller.
    private void initCtl()
    {
        setContentView(R.layout.activity_goodssearch);
        Log.d(LOG_TAG, "initCtl 1");
        //////////////////////////////////////////////////////////////////////////
        // initialize of key EditText
        mSearchBox = (EditText)findViewById(R.id.et_user_keyword);
        //////////////////////////////////////////////////////////////////////////
        // Initialize of listview
        // 1) get controller.
        mListView = (ListView)findViewById(R.id.list_4_result);
        Log.d(LOG_TAG, "initCtl 2");
        //////////////////////////////////////////////////////////////////////////
        // 2) set ItemSelectedListener.
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final private String LOG_TAG = "AdapterView.OnItemSelectedListener";

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* do nothing. */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* do nothing. */
            }
        });

        //////////////////////////////////////////////////////////////////////////
        // 3) set ItemClickListener.
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            final private String LOG_TAG = "AdapterView.OnItemClickListener";
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(LOG_TAG, "onItemClick(" + position + ")");
//                mSelectedItem = position;
//                if (detectDoubleClick() == true) {
//                    Intent sIntent = new Intent(GoodSearchActivity.this, GoodsInfoViewActivity.class);
//                    sIntent.putExtra(GoodsItem.TAG_INTENTNAME, (GoodsItem) parent.getItemAtPosition(position));
//                    startActivity(sIntent);
//
//                }
//            }
//        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = position;
                if (detectDoubleClick() == true) {
                    Intent sIntent = new Intent(GoodSearchActivity.this, GoodsInfoViewActivity.class);
                    sIntent.putExtra(GoodsItem.TAG_INTENTNAME, (GoodsItem) parent.getItemAtPosition(position));
                    startActivity(sIntent);
                } else {
                    GoodsItem sGoodsItem = (GoodsItem) parent.getItemAtPosition(position);
                    Intent sIntent = new Intent(GoodSearchActivity.this, PopupWebViewActivity.class);
                    sIntent.putExtra(GoodsItem.TAG_PRODUCT_ID, sGoodsItem.getProductId());
                    sIntent.putExtra(GoodsItem.TAG_TITLE, sGoodsItem.getTitle());
                    sIntent.putExtra(GoodsItem.TAG_INTENTNAME, sGoodsItem);
                    sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
                    startBlurBackgroundActivity(sIntent, mBlurViewInfo);
                }
            }
        });

        //////////////////////////////////////////////////////////////////////////
        // set onClickListener in Search Button.
        //
        ((Button)findViewById(R.id.btn_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sKeyword = ((EditText) findViewById(R.id.et_user_keyword)).
                        getText().toString();

                if (mAdapter.getCount() > 0) {
                    mAdapter.removeAll(true);
                }
                mNaverSearch = new NaverApi(new MainActivity.Callback() {
                    @Override
                    public void onDoPostCallback(SearchResultShop aResultList) {
                        onCallbackOnNaverSearchApi(aResultList);
                    }

                    @Override
                    public void onDoPostErrorMessage(String aMessage) {
                        (new AlertDialog.Builder(GoodSearchActivity.this)).setTitle("Error")
                                .setMessage(aMessage)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        mProgressDialog.dismiss();
                    }
                }, sKeyword);
                mProgressDialog.show();
                mNaverSearch.setSearchLevel(NAPISearchShopTags.VALID_GOODS_TYPE_COMPAREPRICE);
                mNaverSearch.execute(sKeyword, String.valueOf(MainActivity.getSearchDataCount()));
            }
        });
        //////////////////////////////////////////////////////////////////////////
        // set onClickListener in tracking button.
        ((Button)findViewById(R.id.btn_tracking)).
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(LOG_TAG, "btn_tracking 0 (" + mSelectedItem + ")");
                                if ( mSelectedItem >= 0 ) {
                                    Log.d(LOG_TAG, "btn_tracking 1");
                                    GoodsItem sGoods
                                            = (GoodsItem)mAdapter.getItem(mSelectedItem);
                                    mSelectedItem = -1;
                                    mListView.clearChoices();
                                    sGoods.setUserCheckedPrice(sGoods.getLowPrice());
                                    sGoods.setLastPrice(sGoods.getLowPrice());
                                    if (mMainDB.insertGoods(sGoods) == true)
                                    {
                                        mMainDB.insertGoodsImage(sGoods.getImage(), sGoods.getProductId());
                                        try {
                                            MainActivity.getGoodsViewAdapater().addItem(sGoods);
                                            Log.d(LOG_TAG, "btn_tracking 2");
                                        } catch (MainActivity.MainActivityNullException e) {
                                            mMainDB.removeGoods(sGoods.getProductId());
                                        } finally {
                                            Log.d(LOG_TAG, "btn_tracking 3");
                                            mItemAdded = true;
                                        }
                                    }
                                }
                            }
                        }
                );

        //////////////////////////////////////////////////////////////////////////
        // ProgressDialog
        mProgressDialog = new ProgressDialog(GoodSearchActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        BlurringView sBlurringView = (BlurringView) findViewById(R.id.blurring_view);
        View sBlurredView = findViewById(R.id.blurred_view);
        mBlurViewInfo = new BlurViewInfo(sBlurringView, sBlurredView);
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Initialization for Data.
    //
    private void initData()
    {
        Log.d(LOG_TAG, "initData 0");
        mObj = this;
        mMainDB = DBUtil.getInstance(this);

        // ...... ...... ......... ...... ............ ......
        mFirstTab = false;
        mFirstTime = 0;
        try {
            mSetFirstTab = GoodSearchActivity.class.getMethod("setFirstTab", boolean.class);
            mSetFirstTime = GoodSearchActivity.class.getMethod("setFirstTime", long.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        Log.d(LOG_TAG, "initData 1");

    }

    /////////////////////////////////////////////////////////////////
    // Initialization for Dialog View.
    private void initView()
    {
        Log.d(LOG_TAG, "initView 0");
        mAdapter = new SearchListAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.refreshView();
        Log.d(LOG_TAG, "initView 1");
    }

    @Override
    public void onResume(){
        Log.d(LOG_TAG, "onResume + 0");
        super.onResume();
        if (GoodSearchActivity.mForcedRedraw == true)
        {
            Log.d(LOG_TAG, "onResume + 1");
            GoodSearchActivity.mForcedRedraw = false;
            Util.setRedrawFlag(getIntent());
        }
    }
    @Override
    public void onBackPressed(){
        Log.d(LOG_TAG, "onBackPressed");
        if (GoodSearchActivity.mForcedRedraw == true) {
            Util.setRedrawFlag(getIntent());
            GoodSearchActivity.mForcedRedraw = false;
        }
        setResult(mItemAdded == true ? RESULT_OK : RESULT_CANCELED);
        mItemAdded = false;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCtl();
        initData();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean detectDoubleClick() {
        try {
            return Util.detectDoubleClick(this, mFirstTab, mFirstTime, mSetFirstTab, mSetFirstTime);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }
}