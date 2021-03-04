package com.martian.bpa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.napisearch.NaverApi;
import com.martian.bpa.napisearch.SearchResultShop;
import com.martian.bpa.service.TrackingService;
import com.martian.bpa.util.Define;
import com.martian.bpa.util.ScreenInfo;
import com.martian.bpa.util.dbutil.DBUtil;

import java.util.ArrayList;

/////////////////////////////////////////////////////////////////
// MainActivity Class
public class MainActivity extends AppCompatActivity implements
        Button.OnTouchListener,
        Button.OnClickListener {
    // Definition
    private static final String LOG_TAG = "MainActivity";
    private static final String CLASS_NAME="com.martian.bpa.MainActivity";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    final public  static String TAG_PARAMETER = "CallerType";
    static public final int TAG_CALLER_MAIN    = 1; // normal, MainActivity.
    static public final int TAG_CALLER_SERVICE = 2; // Service.

    static public boolean mForcedRedraw = false;
    static public void setForcedRedraw(boolean aVal) {mForcedRedraw=aVal;}
    static public boolean getForcedRedraw(){return mForcedRedraw;}

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_zoom:
            {
                Intent sIntent = new Intent(MainActivity.this, GoodsItemViewWithGridActivity.class);

                sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);

                startActivityForResult(
                        sIntent,
                        Define.ACTIVITY_REQ_GOODSVIEWINGRID);
                //startActivity( new Intent(MainActivity.this, GoodsItemViewWithGridActivity.class));
                break;
            }
            case R.id.btn_top_search:
            {
                Intent sIntent = new Intent(MainActivity.this, GoodSearchActivity.class);
                sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
                startActivityForResult(
                        sIntent,
                        Define.ACTIVITY_REQ_GOODSEARCH);
                break;
            }
        }
    }

    // Defini
    public static class MainActivityNullException extends Exception{
        final private String mMessage = "MainActivity Object is null.";

        @Override
        public String getMessage() {
            return mMessage;
        }
    }

    // Member
    private static MainActivity   mObj = null;
    private PopupMenu             mPopupMenu = null;
    private GridView              mActiveGoodsViews = null;
    private GoodsMainViewAdapter  mActiveGoodsViewAdapter = null;

    private ImageButton mDeleteBtn = null;
    private ImageButton mDeselectBtn = null;
    private DBUtil mMainDB;

    private boolean mShowGarbageBtn = false;
    private boolean mIsTablet = false;
    private GoodsItem mSelectedItem = null;

    private int mCallerType = TAG_CALLER_MAIN;

    //////////////////////////////////////////////////////////////////
    // Gettor
    public boolean getIsTablet(){return mIsTablet;}

    //////////////////////////////////////////////////////////////////
    // Settor
    public void getIsTablet(boolean aIsTablet){mIsTablet=aIsTablet;}

    //////////////////////////////////////////////////////////////////
    // Property
    private PropertySet mPropertySet = new PropertySet();

    public static GoodsMainViewAdapter getGoodsViewAdapater()  throws MainActivityNullException {
        if (mObj == null) {
            throw new MainActivityNullException();
        }
        return mObj.mActiveGoodsViewAdapter;
    }

    public static DBUtil getMainDB() throws MainActivityNullException {
        if (mObj == null)
        {
            throw new MainActivityNullException();
        }
        return mObj.mMainDB;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Button view = (Button) v;
                view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
            }
            break;
            case MotionEvent.ACTION_UP: {
                switch (v.getId()) {
                    case R.id.btn_search: {
                        Intent sIntent = new Intent(MainActivity.this, GoodSearchActivity.class);
                        sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
                        startActivityForResult(
                                sIntent,
                                Define.ACTIVITY_REQ_GOODSEARCH);
                        break;
                    }
                    case R.id.btn_test:
                        mPopupMenu.show();
                        break;
                }
            }
            case MotionEvent.ACTION_CANCEL: {
                Button view = (Button) v;
                view.getBackground().clearColorFilter();
                view.invalidate();
            }
            break;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Definition for callback.
    public interface Callback{
        void onDoPostCallback(SearchResultShop aResult);
        void onDoPostErrorMessage(String aMessage);
    }
    public interface ImageLoadrInterface{
        void ImageLoad(Bitmap aBmp);
    }

    public static int getSearchDataCount() {
        return mObj.mPropertySet.getDisplayCount();
    }

    public static void setSearchDataCount(int aDataCount){
        mObj.mPropertySet.setDisplayCount(aDataCount);
    }

    public static void updateSetting(PropertySet aProperty)
    {
        mObj.mPropertySet = aProperty;
        mObj.writeConf();
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

    private PopupMenu createPopupMenu() {
        PopupMenu sMenu = new PopupMenu(this, findViewById(R.id.btn_test));
        sMenu.getMenuInflater().inflate(R.menu.menu_test, sMenu.getMenu());
        sMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_test_tracking:
                        Intent sIntent = new Intent(MainActivity.this, TrackingService.class);
                        sIntent.setPackage("com.martian.bpa.SERVICE_START");
                        startService(sIntent);
                        break;
                    case R.id.menu_test_up_price:
                        if (mSelectedItem != null) {
                            mSelectedItem.setLowPrice(mSelectedItem.getLowPrice() + 1000);
                            mMainDB.updateGoodsForTest(mSelectedItem);
                        }
                        break;
                    case R.id.menu_test_down_price:
                        if (mSelectedItem != null) {
                            mSelectedItem.setLowPrice(mSelectedItem.getLowPrice() - 1000);
                            mMainDB.updateGoodsForTest(mSelectedItem);
                        }
                        break;
                }
                return true;
            }
        });
        return sMenu;
    }

    private boolean checkIsTablet(){
        return (ScreenInfo.getScreenSizeType(this) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE?true:false);
    }

    private int getScreenWidth() {
        return ScreenInfo.getDeviceSize(this).getWidth();
    }

    static public boolean isTablet() throws MainActivityNullException {
        if (mObj == null)
        {
            throw new MainActivityNullException();
        }
        return mObj.getIsTablet();
    }

    public static void showSearchDlg(){
        Intent sIntent = new Intent(mObj, GoodSearchActivity.class);
        sIntent.putExtra(Define.TAG_PARENT_CLASSNAME, CLASS_NAME);
        mObj.startActivityForResult(
                sIntent,
                Define.ACTIVITY_REQ_GOODSEARCH);
    }


    ////////////////////////////////////////////////////////////////////////
    // Initialization for Dialog Controller.
    private void initCtl() {
        mIsTablet = checkIsTablet();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////////////////////////////////////////////
        // 1) set ItemClickListener.

        ((Button) findViewById(R.id.btn_search)).setOnTouchListener(this);
        ((Button) findViewById(R.id.btn_test)).setOnTouchListener(this);
        ((ImageButton)findViewById(R.id.btn_zoom)).setOnClickListener(this);
        ((ImageButton)findViewById(R.id.btn_top_search)).setOnClickListener(this);

        ////////////////////////////////////////////////////////////////////
        // GridView
        // 2) settup 4 gridview
        mActiveGoodsViews = (GridView)findViewById(R.id.gv_goodsgridview);

        // set gridviewer's columnnum
        //mActiveGoodsViews.setNumColumns(mIsTablet==true?
        //        Define.TAG_4_GRIDVIEW_COLUMN_IN_TABLET:Define.TAG_4_GRIDVIEW_COLUMN_IN_PHONE);
        mActiveGoodsViewAdapter = new GoodsMainViewAdapter(this);

        mActiveGoodsViews.setAdapter(mActiveGoodsViewAdapter);

        mPopupMenu = createPopupMenu();
    }

    ////////////////////////////////////////////////////////////////////////
    // Initialization for Data.
    //
    private void initData()
    {
        mObj = this;
        mMainDB = DBUtil.getInstance(this);
        readConf();

        mCallerType = getIntent().getIntExtra(TAG_PARAMETER, TAG_CALLER_MAIN);

        if (ScreenInfo.getScreenSizeType(this) < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            ScreenInfo.mDeviceType = ScreenInfo.DEVICE_TYPE.PHONE;
        } else {
            ScreenInfo.mDeviceType = ScreenInfo.DEVICE_TYPE.TABLET;
        }
    }

    private void drawActiveGoodsView() {
        ArrayList<GoodsItem> sItems = (ArrayList<GoodsItem>) mMainDB.readAll(false);
        int sItemSize = sItems.size() + 1;
        int sScreenWidth = getScreenWidth();
        int sItemViewCount = mIsTablet == true ?
                Define.TAG_4_GRIDVIEW_COLUMN_COUNT_IN_TABLET :
                Define.TAG_4_GRIDVIEW_COLUMN_COUNT_IN_PHONE;
        int sItemViewWidth = sScreenWidth / sItemViewCount;
        int sItemAllWidth = sItemViewWidth * sItemSize;

        LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(
                sItemAllWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        mActiveGoodsViews.setColumnWidth(sItemViewWidth - Define.TAG_4_GRIDVIEW_COLUMN_WIDTH_MARGIN);
        mActiveGoodsViews.setLayoutParams(sParams);
        //mActiveGoodsViews.setMinimumHeight(sScreenWidth);
        mActiveGoodsViews.setHorizontalSpacing(20);
        mActiveGoodsViews.setStretchMode(GridView.STRETCH_SPACING);
        mActiveGoodsViews.setNumColumns(sItemSize);
        mActiveGoodsViewAdapter.removeAllItem();
        mActiveGoodsViewAdapter.addItems(sItems);
        mActiveGoodsViewAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "drawActiveGoodsView + sItemSize=" + sItemSize + ")");
    }

    /////////////////////////////////////////////////////////////////
    // Initialization for Dialog View.
    private void initView()
    {
        drawActiveGoodsView();
    }

    /////////////////////////////////////////////////////////////////
    // override function
    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop + 0");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause + 0");
        super.onPause();
    }

    @Override
    public void onRestart() {
        Log.d(LOG_TAG, "onRestart + 0");
        super.onRestart();
    }

    @Override
    public void onResume(){
        Log.d(LOG_TAG, "onResume + 0");
        super.onResume();
        if (MainActivity.mForcedRedraw == true)
        {
            Log.d(LOG_TAG, "onResume + 1");
            drawActiveGoodsView();
            MainActivity.mForcedRedraw = false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sIntent;

        //noinspection SimplifiableIfStatement
        switch (item.getItemId())
        {
            case R.id.action_settings:
                sIntent = new Intent(MainActivity.this, SettingActivity.class);
                sIntent.putExtra(PropertySet.TAG_INTENTNAME, mPropertySet);
                startActivity(sIntent);
                return true;
                //break;
            case R.id.menu_active_list:
                sIntent = new Intent(MainActivity.this, DBDataListView.class);
                sIntent.putExtra(DBDataListView.TAG_PARAMETER, DBDataListView.TAG_CALLER_MAIN);
                startActivity(sIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void writeConf() {
        SharedPreferences sPref = getSharedPreferences(PropertySet.TAG_PREFERENCE, 0);
        SharedPreferences.Editor sEditor = sPref.edit();
        sEditor.putInt(
                PropertySet.TAG_CONF_SEARCH_DATACOUNT,
                mPropertySet.getDisplayCount());
        sEditor.apply();
        Log.d(LOG_TAG, "writeConf end");
    }

    public boolean readConf() {
        SharedPreferences sPref = getSharedPreferences(PropertySet.TAG_PREFERENCE, 0);
        mPropertySet.setDisplayCount(
                sPref.getInt(
                        PropertySet.TAG_CONF_SEARCH_DATACOUNT,
                        NaverApi.DEFAULT_SEARCH_NUM
                ));
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult -1");
        if ((requestCode == Define.ACTIVITY_REQ_GOODSEARCH)||
            (requestCode == Define.ACTIVITY_REQ_GOODSVIEWINGRID)) {
            if (resultCode == RESULT_OK) {
                //drawActiveGoodsView();
            } else if (resultCode == RESULT_CANCELED) {
                /* do nothing. */
            }
        }
    }
}
