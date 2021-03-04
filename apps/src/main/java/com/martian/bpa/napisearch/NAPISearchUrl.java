/******************************************************************
 * NAPISearchUrl.java
 * swhors@naver.com
 * 2015/11/05
 *
 * 설명 :
 *    1)"Naver API Calling URL"을 생성하는 클래스.
 *    2)Query에 대하여 UTF-8 인코딩 처리를 한다.
 ******************************************************************/
/******************************************************************
 * NAVER API Usage Example
 *  :
 *   "http://openapi.naver.com/search?"
 *             + "key=44c9f5da862a13d53f7eb86b8dac9b4a"
 *             + "&query=" + sQuery //여기는 쿼리를 넣으세요(검색어)
 *             + "&target=local&start=1&display=4"
 ******************************************************************/

package com.martian.bpa.napisearch;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class NAPISearchUrl
{
    private static final String LOG_TAG = "NAPISearchUrl";

    static private final int TAG_SORT_TYPE_SIM=1;
    static private final int TAG_SORT_TYPE_DATE=2;
    static private final int TAG_SORT_TYPE_ASC=3;
    static private final int TAG_SORT_TYPE_DSC=4;
    static private final int TAG_SORT_TYPE_BASE = TAG_SORT_TYPE_SIM;

    // type definition
    public enum eSortType{
        sort_sim {
            @Override
            public int getId() {
                return TAG_SORT_TYPE_SIM;
            }

            @Override
            public String toString() {
                return SORT_TYPE_SIM;
            }
        }, sort_date {
            @Override
            public int getId() {
                return TAG_SORT_TYPE_DATE;
            }

            @Override
            public String toString() {
                return SORT_TYPE_DATE;
            }
        }, sort_asc {
            @Override
            public int getId() {
                return TAG_SORT_TYPE_ASC;
            }

            @Override
            public String toString() {
                return SORT_TYPE_VALUE_ASC;
            }
        }, sort_dsc {
            @Override
            public int getId() {
                return TAG_SORT_TYPE_DSC;
            }

            @Override
            public String toString() {
                return SORT_TYPE_VALUE_DSC;
            }
        };
        abstract public int getId();
        abstract public String toString();
    };

    // definition of constant variable.
    public final int    MAX_SORT_TYPE=4;
    static public final String SORT_TYPE_SIM="sim";
    static public final String SORT_TYPE_DATE="date";
    static public final String SORT_TYPE_VALUE_ASC="asc";
    static public final String SORT_TYPE_VALUE_DSC="dsc";
    public final int START_POS_BEGIN=1;
    public final int START_POS_END=1000;
    public final int START_POS_DEFAULT=START_POS_BEGIN;
    public final int RESULT_DISPLAY_NUM_MIN=1;
    public final int RESULT_DISPLAY_NUM_MAC=100;
    public final int RESULT_DISPLAY_DEFAULT=10;
    public final String NAPI_SEARCH_QUERY="http://openapi.naver.com/search?";

    private String mStringType[]={SORT_TYPE_SIM,
        SORT_TYPE_DATE,
        SORT_TYPE_VALUE_ASC,
        SORT_TYPE_VALUE_DSC,
        ""};

    /////////////////////////////////////////////////////
    // member variable
    //

    // 이용 등록을 통해 받은 key 스트링을 입력합니다. (needed)
    public String mKey;

    // 서비스를 위해서는 무조건 지정해야 합니다.
    // (needed, like as shop/local...)
    public String mTarget;

    // 검색을 원하는 질의, UTF-8 인코딩 입니다. (needed)
    public String mQuery;

    // integer : 기본값 10, 최대 100
    // 검색결과 출력건수를 지정합니다. 최대 100까지 가능합니다.
    public int    mDisplay = RESULT_DISPLAY_DEFAULT;

    // integer : 기본값 1, 최대 1000
    // 검색의 시작위치를 지정할 수 있습니다. 최대 1000까지 가능합니다.
    public int    mStartPosition = START_POS_DEFAULT;

    // originally string :
    // 정렬타입.
    // sim : 유사도순 (기본값)
    // date : 날짜순
    // asc : 가격오름차순
    // dsc : 가격내림차순
    public eSortType mSortType = eSortType.sort_date;

    // constructor
    public NAPISearchUrl(String aKey,
                         String aTarget,
                         String aQuery,
                         int    aDisplay,
                         int    aStartPosition,
                         eSortType aSortType)
    {
        mKey = aKey;
        mTarget = aTarget;
        mQuery = aQuery;
        mDisplay = aDisplay;
        mStartPosition = aStartPosition;
        mSortType = aSortType;
    }

    public NAPISearchUrl(String aKey,
                         String aTarget,
                         String aQuery,
                         boolean aConvQuerytoUtf8)
    {
        mKey = aKey;
        mTarget = aTarget;
        if (aConvQuerytoUtf8==true)
        {
            try {
                setQueryUsingUTF8(aQuery);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else
        {
            mQuery = aQuery;
        }
    }

    // Method for gettor
    public String getKey(){return mKey;}
    public String getTarget(){return mTarget;}
    public String getQuery(){return mQuery;}
    public int getDisplay(){return mDisplay;}
    public int getStartPosition(){return mStartPosition;}
    public String getSortTypeString(){
        return mStringType[Integer.valueOf(mSortType.toString())];
    }
    public eSortType getSortType(){return mSortType;}

    // Method for settor
    public void setKey(String aKey){mKey=aKey;}
    public void setTarget(String aTarget){mTarget=aTarget;}
    public void setQuery(String aQuery){mQuery=aQuery;}
    public void setDisplay(int aDisplay){mDisplay=aDisplay;}
    public void setStartPosition(int aPosition){mStartPosition=aPosition;}
    public void setSortType(eSortType aSortType){mSortType=aSortType;}

    // Extenede Method
    //
    public void setSortTypeDsc(){setSortType(eSortType.sort_dsc);}
    public void setSortTypeSim(){setSortType(eSortType.sort_sim);}
    public void setSortTypeAsc(){setSortType(eSortType.sort_asc);}
    public void setSortTypeDate(){setSortType(eSortType.sort_date);}

    public void setQueryUsingUTF8(String aQuery)
            throws UnsupportedEncodingException
    {
        mQuery = URLEncoder.encode(aQuery, "utf-8");
    }

    public String toString(){
        //Log.d(LOG_TAG, mSortType.toString());
        return NAPI_SEARCH_QUERY+"key="+mKey+
                "&query="+mQuery+
                "&target="+mTarget+
                "&start="+mStartPosition+
                "&display="+mDisplay+
                "&sort="+mStringType[mSortType.getId()-TAG_SORT_TYPE_BASE];
    }
}