/******************************************************************
 * NaverApi.java
 * swhors@naver.com
 * 2015/11/04
 *
 * 설명 :
 *    1)"NAVER Search API"를 호출하여 쇼핑 검색 결과를 가져오는 클래스.
 *    2)검색 결과는 SearchResultShop 클래스를 생성하여 파싱한다.
 *    3)파싱된 결과는 onPostExecute에서 MainActivity의 Callback 함수를
 *      호출하여 ListView에 추가하게 된다.
 ******************************************************************/

package com.martian.bpa.napisearch;

import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.martian.bpa.MainActivity;

public class NaverApi extends AsyncTask<String, Void, Boolean> {
    static public final int DEFAULT_SEARCH_NUM = 10;

    private final String LOG_TAG = "NaverApi";
    private final int MAX_DATA_COUNT = 10;

    private String mStatus = new String();
    private String mKeyword;
    private int mDisplayCount = 0;
    private int mSearchedDataCount = 0;
    private boolean mHasMessage = false;
    private String mErrorMessage = null;
    private int mSearchLevel = NAPISearchShopTags.VALID_GOODS_TYPE_COMPAREPRICE;

    SearchResultShop mResultShop;

    MainActivity.Callback mCallback;

    /**
     * Called when the activity is first created.
     */
    public NaverApi(MainActivity.Callback aCallback) {
        mCallback = aCallback;
        mKeyword = null;
        mSearchedDataCount = 0;
        mResultShop = null;
    }

    public void setSearchLevel(int aVal) {
        mSearchLevel = aVal;
    }

    public int getSearchLevel() {
        return mSearchLevel;
    }

    public NaverApi(MainActivity.Callback aCallback, String aKeyWord) {
        mCallback = aCallback;
        mKeyword = aKeyWord;
        mSearchedDataCount = 0;
        mResultShop = null;
    }

    public int getSearchCount() {
        return mSearchedDataCount;
    }

    public String getStatusText() {
        return mStatus;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        mKeyword = params[0];
        if (params.length == 1) {
            mDisplayCount = DEFAULT_SEARCH_NUM;
        } else {
            mDisplayCount = Integer.parseInt(params[1]);
        }
        return searchData(mKeyword, mDisplayCount);
    }

    @Override
    protected void onPostExecute(Boolean aResult) {
        Log.d(LOG_TAG, "onPostExecute");
        if (aResult == true) {
            mCallback.onDoPostCallback(mResultShop);
        } else {
            if (mHasMessage == true) {
                mCallback.onDoPostErrorMessage(mErrorMessage);
            } else {
                mCallback.onDoPostErrorMessage("Unknown Error");
            }
        }
    }

    SearchResultShop getResult(){return mResultShop;}

    private boolean searchData(String aKeyword, int aDisplayCount) {
        boolean sResult = true;

        mKeyword = aKeyword;

        try {
            boolean sHasFirst = false;
            boolean sHasItem = false;
            NAPISearchUrl sSearchUrl = NAPIShopSearchUrl.getInstance(aKeyword);
            sSearchUrl.setDisplay(aDisplayCount);
            sSearchUrl.setSortType(NAPISearchUrl.eSortType.sort_sim);
            URL sUrl = new URL(sSearchUrl.toString());

            Log.d(LOG_TAG, "URL :" + sSearchUrl.toString());

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            parserCreator.setNamespaceAware(true);
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(sUrl.openStream(), null);

            int sParserEvent = parser.getEventType();
            String sData = new String();
            String sName = new String();

            mResultShop = new SearchResultShop();
            GoodsItem sItem = null;

            Log.d(LOG_TAG, "URL : " + sSearchUrl.toString());

            while (sParserEvent != XmlPullParser.END_DOCUMENT) {
                switch (sParserEvent) {
                    case XmlPullParser.START_TAG:
                        sName = parser.getName();
                        sHasFirst = true;
                        if (sName.equals(NAPISearchShopTags.FIELD_ITEM)) {
                            sHasItem = true;
                            if (sItem == null) {
                                sItem = new GoodsItem();
                            }
                        } else {
                            /* do nothing. */
                        }

                        break;
                    case XmlPullParser.TEXT:
                        sData = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(LOG_TAG, "ParseEvent :" + "END_TAG");
                        Log.d(LOG_TAG, "ParseEvent :" + parser.getName() + "...");
                        if (sHasFirst == true) {
                            Log.d(LOG_TAG, "Field -4: ");
                            if (parser.getName().equals(NAPISearchShopTags.FIELD_MESSAGE)) {
                                Log.d(LOG_TAG, "Field -3: ");
                                mHasMessage = true;
                                mErrorMessage = sData;
                            } else {
                                if (sHasItem==true) {
                                    Log.d(LOG_TAG, "Field -2: ");
                                    if (parser.getName().equals(NAPISearchShopTags.FIELD_ITEM)) {
                                    /* do nothing. */
                                    } else {
                                        NaverSearchXmlField sField =
                                                NaverSearchXmlField.getInstance(sName, sData, sHasItem);
                                        Log.d(LOG_TAG, "Field -1: name=" + sName + ",data=" + sData);
                                        sField.getType().setValue(sItem, sData);
                                        Log.d(LOG_TAG, "Field 0: " + sField.toString());
                                    }
                                } else {
                                    if (parser.getName().equals(NAPISearchShopTags.FIELD_ERRORCODE))
                                    {
                                        mHasMessage = true;
                                        mErrorMessage = sData;
                                    } else {
                                        NaverSearchXmlField sField =
                                                NaverSearchXmlField.getInstance(sName, sData, sHasItem);
                                        sField.getType().setValue(mResultShop, sData);
                                        Log.d(LOG_TAG, "Field 1: " + sField.toString());
                                    }
                                }
                            }
                        } else {
                            Log.d(LOG_TAG, "Field 111 + " + parser.getName());
                            if (parser.getName().equals(NAPISearchShopTags.FIELD_ITEM)) {
                                Log.d(LOG_TAG, "TITLE=" + sItem.getTitle() + ", Type=" + sItem.getProductType() + "(" + mSearchLevel + ")");
                                if ((sItem.getProductType() > 0) &&
                                        (sItem.getProductType() <=mSearchLevel)) {
                                    NaverSearchXmlField.eXmlFieldItemType.
                                            FIELD_ITEM.setValue(mResultShop, sItem);
                                    mSearchedDataCount++;
                                    Log.d(LOG_TAG, "Item : " + sItem.toString());
                                    sItem = null;
                                }
                                sHasItem = false;
                            } else if (parser.getName().equals(NAPISearchShopTags.FIELD_CHANNEL)) {
                                /* do nothing. */
                            } else if (parser.getName().equals(NAPISearchShopTags.FIELD_RSS)) {
                                /* do nothing. */
                            } else {
                                Log.d(LOG_TAG, "ParseEvent :" + "END_TAG (don't have start-tag. ignore)");
                            }
                        }
                        sHasFirst = false;
                        break;
                    default:
                        Log.d(LOG_TAG, "Default :");
                }
                sParserEvent = parser.next();
            }
            mStatus = "success";
            Log.d(LOG_TAG, "Status :" + mStatus);
        }
        catch (XmlPullParserException e)
        {
            mStatus = "XmlPullParserException";
            Log.d(LOG_TAG, "Status :" + mStatus);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            mStatus = "Exception";
            Log.d(LOG_TAG, "Status :" + mStatus);
            e.printStackTrace();
            sResult = false;
        }

        return sResult;
    }
}
