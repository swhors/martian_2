package com.martian.bpa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {
    final static String TAG_INTENTNAME="WevViewActivity";

    private WebView mWebView;
    private String  mUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initCtrl();
        initData();
        initView();
    }

    private void initCtrl() {
        mWebView = (WebView)findViewById(R.id.web_view_page);
    }

    private void initData() {
        mUrl = (getIntent().getStringExtra(TAG_INTENTNAME));
    }

    private void initView() {
        mWebView.loadUrl(mUrl);
    }

}
