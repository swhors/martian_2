package com.martian.bpa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.util.ScreenInfo;
import com.martian.bpa.util.Util;
import com.martian.bpa.util.dbutil.DBUtil;

import java.util.List;
import java.util.Stack;

import static android.content.Intent.ACTION_VIEW;

/**
 * Created by USER on 12/2/2015.
 */
public class PopupWebViewActivity extends Activity {
    private static final String NAVER_SHOP_MOBILE_URL = "http://m.shopping.naver.com/detail/detail.nhn?nv_mid=";
    private static final String NAVER_PRICE_COMPARE_ELEM_ID_NAME = "priceCompareArea";
    private static final String NAVER_DETAIL_CONTENTS_ELEM_ID_NAME = "detail_contents";
    private static final int TAG_PAGE_FINISHED = 16777216 * 2;

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mTitle;
    private TextView mLaunchBrowser;
    private TextView mTrackingEnable;
    private TextView mTrackingDisable;
    private TextView mBrowserBack;
    private TextView mBrowserForward;
    private GoodsItem mGoodsItem;
    private Intent    mIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCtl();
        initData();
        initView();
    }

    private void initCtl() {
        setContentView(R.layout.activity_popup_webbiew);

        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mLaunchBrowser = (TextView) findViewById(R.id.tv_launch_browser);
        mLaunchBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(mWebView.getUrl());
            }
        });
        mTrackingEnable = (TextView) findViewById(R.id.tv_tracking_enable);
        mTrackingEnable.setOnClickListener(mTrackingClickListener);
        mTrackingDisable = (TextView) findViewById(R.id.tv_tracking_disable);
        mBrowserBack = (TextView) findViewById(R.id.tv_browser_back);
        mBrowserForward = (TextView) findViewById(R.id.tv_browser_forward);
        mIntent = getIntent();
    }

    View.OnClickListener mTrackingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final GoodsItem sGoods = mGoodsItem;
            DBUtil sDBUtil = DBUtil.getInstance(PopupWebViewActivity.this);
            sGoods.setUserCheckedPrice(sGoods.getLowPrice());
            sGoods.setLastPrice(sGoods.getLowPrice());
            if (sDBUtil.insertGoods(sGoods) == true) {
                sDBUtil.insertGoodsImage(sGoods.getImage(), sGoods.getProductId());
                try {
                    MainActivity.getGoodsViewAdapater().addItem(sGoods);
                    setTrackingButtonLayout(true);
                    Util.setRedrawFlag(mIntent);

                } catch (MainActivity.MainActivityNullException e) {
                    sDBUtil.removeGoods(sGoods.getProductId());
                }
            }
        }
    };

    private void initData() {
        mGoodsItem = getIntent().getParcelableExtra(GoodsItem.TAG_INTENTNAME);
    }

    private void initView() {
        mTitle.setText(mGoodsItem.getTitle());

        setTrackingButtonLayout(isTrackingGood(mGoodsItem.getProductId()));

        refreshWebViewControl(mWebView);

        if (ScreenInfo.mDeviceType == ScreenInfo.DEVICE_TYPE.PHONE) {
            resizeWindow(0.9f, 0.9f);
        } else {
            resizeWindow(0.8f, 0.85f);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
            }
        });

        if (mGoodsItem.getProductId() != 0) {
            openUrl(NAVER_SHOP_MOBILE_URL + mGoodsItem.getProductId());
        }

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WebView.HitTestResult sHitTestResult = ((WebView) view).getHitTestResult();
                processPageTouchAndLinkClick(sHitTestResult);
                return false;
            }

            private void processPageTouchAndLinkClick(WebView.HitTestResult aHitTestResult) {
                if (aHitTestResult != null) {
                    int sHistoryIndex = mWebView.copyBackForwardList().getCurrentIndex();
                    if (mUrlHistoryPoints.isEmpty()
                            || (mUrlHistoryPoints.get(mUrlHistoryPoints.size() - 1) != sHistoryIndex)) {
                        mCurrentHistoryIndex = sHistoryIndex;
                    }
                }
            }
        });
    }

    private void resizeWindow(float aRatioX, float aRatioY) {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams sParams = getWindow().getAttributes();
        sParams.width = (int) (size.x * aRatioX);
        sParams.height = (int) (size.y * aRatioY);
    }

    private boolean isTrackingGood(long aProductId) {
        return DBUtil.getInstance(this).readGoods(aProductId) != null;
    }

    private void setTrackingButtonLayout(boolean aIsTrackingGood) {
        if (aIsTrackingGood) {
            mTrackingEnable.setVisibility(View.INVISIBLE);
            mTrackingDisable.setVisibility(View.VISIBLE);
        } else {
            mTrackingEnable.setVisibility(View.VISIBLE);
            mTrackingDisable.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshWebViewControl(final WebView aWebView) {
        if (aWebView.canGoBack()) {
            mBrowserBack.setOnClickListener(mOnClickListener);
            mBrowserBack.setTextColor(getResources().getColor(R.color.menuTextColorEnable));
            mBrowserBack.setEnabled(true);
        } else {
            mBrowserBack.setOnClickListener(null);
            mBrowserBack.setTextColor(getResources().getColor(R.color.menuTextColorDisable));
            mBrowserBack.setEnabled(false);
        }

        if (aWebView.canGoForward() ) {
            mBrowserForward.setOnClickListener(mOnClickListener);
            mBrowserForward.setTextColor(getResources().getColor(R.color.menuTextColorEnable));
            mBrowserForward.setEnabled(true);
        } else {
            mBrowserForward.setOnClickListener(null);
            mBrowserForward.setTextColor(getResources().getColor(R.color.menuTextColorDisable));
            mBrowserForward.setEnabled(false);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View aView) {
            if (aView == mBrowserBack) {
                if (mUrlHistoryPoints.size() > 0) {
                    int sTargetIndex = mUrlHistoryPoints.pop();
                    while (mWebView.copyBackForwardList().getCurrentIndex() > sTargetIndex) {
                        mWebView.goBack();
                    }
                }
            } else {
                int aHistoryIndex = mWebView.copyBackForwardList().getCurrentIndex();
                mUrlHistoryPoints.add(aHistoryIndex);
                mWebView.goForward();
            }
        }
    };

    private void openBrowser(String aUrl) {
        Uri sUri = Uri.parse(aUrl);

        Intent sIntent = new Intent(ACTION_VIEW);
        sIntent.setPackage("com.android.chrome");

        List<ResolveInfo> activitiesList = getPackageManager().queryIntentActivities(sIntent, -1);
        if (activitiesList.size() == 0) {
            sIntent.setPackage(null);
        }

        sIntent.setData(sUri);
        startActivity(sIntent);
    }

    private void openUrl(String aUrl) {
        // 웹뷰에서 자바스크립트실행가능
        mWebView.getSettings().setJavaScriptEnabled(true);
        // WebViewClient 지정
        mWebView.setWebViewClient(new WebViewClientClass());
        // 구글홈페이지 지정
        mWebView.loadUrl(aUrl);
//        mWebView.loadUrl("http://www.google.com");
    }

    private Stack<Integer> mUrlHistoryPoints = new Stack<>();
    private int mCurrentHistoryIndex = -1;
    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            refreshWebViewControl(view);

            if (mCurrentHistoryIndex > -1) {
                mUrlHistoryPoints.add(mCurrentHistoryIndex);
                mCurrentHistoryIndex = -1;
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
            refreshWebViewControl(view);
            view.setTag(TAG_PAGE_FINISHED, false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.INVISIBLE);
            refreshWebViewControl(view);
            view.setTag(TAG_PAGE_FINISHED, true);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Toast.makeText(PopupWebViewActivity.this, "로딩오류" + error.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (!(boolean) view.getTag(TAG_PAGE_FINISHED)) {
                view.loadUrl(createScrollToElementJavascript(NAVER_DETAIL_CONTENTS_ELEM_ID_NAME));
            }
        }

        private String createScrollToElementJavascript(String aElementId) {
            return "javascript:var div = document.getElementById('" + aElementId + "');" +
                    "function getOffsetTop(el) {\n" +
                    "  var top = 0;\n" +
                    "  if (el.offsetParent) {\n" +
                    "    do {\n" +
                    "      top += el.offsetTop;\n" +
                    "    } while (el = el.offsetParent);\n" +
                    "    return [top];\n" +
                    "  }\n" +
                    "}\n" +
                    "window.scroll(0, getOffsetTop(div));";
        }
    }
}
