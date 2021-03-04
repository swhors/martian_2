package com.martian.bpa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.DateUtil;
import com.martian.bpa.util.dbutil.DBUtil;
import com.martian.bpa.util.loader.ImageLoader;

import java.util.Date;

public class GoodsInfoViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GoodsInfoViewActivity";
    private ImageView mGoodsImageView;
    private TextView mHighPriceView;
    private TextView mLowPriceView;
    private TextView mTitleView;
    private TextView mGoodsIDView;
    private TextView mMallView;
    private TextView mLastPrice;
    private TextView mLastUpdate;
    private Button   mUpdateTest;

    GoodsItem mGoodsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info_view);
        initCtrl();
        initData();
        initView();
    }

    void initCtrl(){
        mGoodsImageView = (ImageView)findViewById(R.id.iv_goodsimage);
        mHighPriceView = (TextView)findViewById(R.id.tv_hprice);
        mLowPriceView = (TextView)findViewById(R.id.tv_lprice);
        mTitleView = (TextView)findViewById(R.id.tv_goodstitle);
        mGoodsIDView = (TextView)findViewById(R.id.tv_goodsid);
        mMallView = (TextView)findViewById(R.id.tv_mallname);
        mLastPrice = (TextView)findViewById(R.id.tv_lastprice);
        mLastUpdate = (TextView)findViewById(R.id.tv_lastupdate);
        mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity((new Intent(GoodsInfoViewActivity.this, WebViewActivity.class))
                        .putExtra(WebViewActivity.TAG_INTENTNAME, mGoodsItem.getLink()));
            }
        });
        mGoodsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity((new Intent(GoodsInfoViewActivity.this, WebViewActivity.class))
                        .putExtra(WebViewActivity.TAG_INTENTNAME, mGoodsItem.getImage()));
            }
        });
    }

    void initData(){
        mGoodsItem = getIntent().getParcelableExtra(GoodsItem.TAG_INTENTNAME);
    }

    void initView(){
        (new ImageLoader(new MainActivity.ImageLoadrInterface() {
            private final String LOG_TAG="MainActivity.ImageLoadrInterface";
            @SuppressLint("LongLogTag")
            @Override
            public void ImageLoad(Bitmap aBmp) {
                mGoodsImageView.setImageBitmap(aBmp);
            }
        }, mGoodsItem.getImage())).execute();

        mTitleView.setText(Html.fromHtml(
                "<a href=" +
                        mGoodsItem.getLink() +
                        ">" +
                        mGoodsItem.getTitle().replaceAll("<\\/b>|<b>", "") +
                        "</a>"));
        mGoodsIDView.setText(String.valueOf(mGoodsItem.getProductId()));
        mHighPriceView.setText(String.valueOf(mGoodsItem.getHighPrice()));
        mLowPriceView.setText(String.valueOf(mGoodsItem.getLowPrice()));
        mMallView.setText(mGoodsItem.getMallName());
        mLastPrice.setText(String.valueOf(mGoodsItem.getLastPrice()));
        mLastUpdate.setText(mGoodsItem.getLastUpdate());
    }
}
