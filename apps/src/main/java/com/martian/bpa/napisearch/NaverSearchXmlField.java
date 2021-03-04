/***********************************************************************
 * Created by simpson on 15. 11. 5.
 * NaverSearchXmlField.java
 *
 * 내용 :
 *     XML로 받은 상품 정보를 파싱하여 데이터를 입력하는 클래스.
 ***********************************************************************/
package com.martian.bpa.napisearch;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/***********************************************************************
 * Data Example :
 *
 * <rss version="2.0">
 * <channel>
 *   <title>Naver Open API - shop ::'노트북'</title>
 *   <link>http://search.naver.com</link>
 *   <description>Naver Search Result</description>
 *   <lastBuildDate>Wed, 17 Jul 2013 15:21:11 +0900</lastBuildDate>
 *   <total>2126628</total>
 *   <start>1</start>
 *   <display>5</display>
 *   <item>
 *      <title>LG전자 엑스노트 SD550-PD60K</title>
 *      <link>
 *              http://openapi.naver.com/l?AAAB2LQQ6DIBAAX7MeDWUF9sJBov6D4i
 *              KmKaKlTfx9bZM5TCaZ/c3HaWF0QAocwTgA9X9x4DTQralnYftKW2kefFojx
 *              HwPhIEUKi9C6HyUPAutYuiITZMOjjbVWgB7kNPF7y1rXtrsP3y0YXtecfGV
 *              25wy4LTOgIM2qI1E0uILF+w9wpIAAAA=
 *      </link>
 *      <image>
 *              http://shopping.phinf.naver.net/main_6736723/6736723860.20130423153212.jpg
 *      </image>
 *      <lprice>683890</lprice>
 *      <hprice>1756370</hprice>
 *      <mallName>네이버</mallName>
 *      <productId>6736723860</productId>
 *      <productType>1</productType>
 *   </item>
 *   ...
 * </channel>
 * </rss>
 *
 ***********************************************************************/
public class NaverSearchXmlField
{
    // definition of constant field
    private static final String LOG_TAG = "NaverSearchXmlField";

    // definition inner type;
    public enum eXmlFieldItemType{

        FIELD_TITLE{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setTitle((String)aValue);
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_TITLE;}
        },
        FIELD_LINK{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setLink((String) aValue);
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_LINK;}
        },
        FIELD_DESC{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setDescription((String) aValue);
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_DESC;}
        },
        FIELD_LASTBUILDDATE{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                String sValue = (String) aValue;

                //Log.d(LOG_TAG, "Date=" + sValue);
                SimpleDateFormat sFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                Date sDate = null;
                try {
                    sDate = sFormat.parse(sValue);
                    sResult.setLastBuildDate(sDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_LASTBUILDDATE;}
        },
        FIELD_TOTAL{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setTotal(Integer.valueOf((String)aValue));
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_TOTAL;}
        },
        FIELD_START{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setStart(Integer.valueOf((String)aValue));
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_START;}
        },
        FIELD_DISPLAY{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.setStart(Integer.valueOf((String)aValue));
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_DISPLAY;}
        },
        FIELD_ITEM{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                SearchResultShop sResult = (SearchResultShop)aResult;
                sResult.addItem((GoodsItem) aValue);
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_ITEM;}
        },
        FIELD_ITEM_TITLE{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setTitle((String)aValue);
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_TITLE;
            }
        },
        FIELD_ITEM_LINK{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setLink((String)aValue);
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_LINK;
            }
        },
        FIELD_ITEM_IMAGE{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setImage((String)aValue);
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_IMAGE;
            }
        },
        FIELD_ITEM_LPRICE{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setLowPrice(Integer.valueOf((String)aValue));
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_LPRICE;
            }
        },
        FIELD_ITEM_HPRICE{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setHighPrice(Integer.valueOf((String)aValue));
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_HPRICE;
            }
        },
        FIELD_ITEM_MALLNAME{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setMallName((String)aValue);
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_MALLNAME;
            }
        },
        FIELD_ITEM_PRODUCTID{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setProductId(Long.valueOf((String)aValue));
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_PRODUCTID;
            }
        },
        FIELD_ITEM_PRODUCTTYPE{
            @Override
            public void setValue(Object aItem, Object aValue) {
                GoodsItem sItem = (GoodsItem)aItem;
                sItem.setProductType(Integer.valueOf((String)aValue));
            }

            @Override
            public int getNum() {
                return NAPISearchShopTags.TAG_FIELD_ITEM_PRODUCTTYPE;
            }
        },
        FIELD_NONE{
            @Override
            public void setValue(Object aResult, Object aValue)
            {
                /* do nothing. */
            }
            @Override
            public int getNum(){return NAPISearchShopTags.TAG_FIELD_NONE;}
        };

        abstract public void setValue(Object aResult, Object aValue);
        abstract public int getNum();
    };

    // constructor
    public NaverSearchXmlField(String aName, String aData)
    {
        mName = aName;
        mData = aData;
        mType = eXmlFieldItemType.FIELD_NONE;
    }

    public NaverSearchXmlField(String aName, String aData, eXmlFieldItemType aType)
    {
        mName = aName;
        mData = aData;
        mType = aType;
    }

    // member
    private String mName;
    private String mData;
    private eXmlFieldItemType mType;

    // gettor
    public String getName(){return mName;}
    public String getData(){return mData;}
    public eXmlFieldItemType getType(){return mType;}

    // settor
    public void setName(String aName){mName=aName;}
    public void setData(String aData){mData=aData;}
    public void setType(eXmlFieldItemType aType){mType=aType;}

    //
    static NaverSearchXmlField getInstance(String aName, String aData, boolean aIsItem) {
        boolean sValid = true;
        eXmlFieldItemType sType = eXmlFieldItemType.FIELD_NONE;

        NaverSearchXmlField sField = null;

        if (aName.equals(NAPISearchShopTags.FIELD_TITLE)) {
            sType = aIsItem==false?eXmlFieldItemType.FIELD_TITLE:eXmlFieldItemType.FIELD_ITEM_TITLE;
        }
        else if (aName.equals(NAPISearchShopTags.FIELD_LINK)) {
            sType = aIsItem==false?eXmlFieldItemType.FIELD_LINK:eXmlFieldItemType.FIELD_ITEM_LINK;
        } else if (aName.equals(NAPISearchShopTags.FIELD_DESC)) {
            sType = eXmlFieldItemType.FIELD_DESC;
        } else if (aName.equals(NAPISearchShopTags.FIELD_LASTBUILDDATE)) {
            sType = eXmlFieldItemType.FIELD_LASTBUILDDATE;
        } else if (aName.equals(NAPISearchShopTags.FIELD_TOTAL)) {
            sType = eXmlFieldItemType.FIELD_TOTAL;
        } else if (aName.equals(NAPISearchShopTags.FIELD_START)) {
            sType = eXmlFieldItemType.FIELD_START;
        } else if (aName.equals(NAPISearchShopTags.FIELD_DISPLAY)) {
            sType = eXmlFieldItemType.FIELD_DISPLAY;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM)){
            sType = eXmlFieldItemType.FIELD_ITEM;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_LPRICE)){
            sType = eXmlFieldItemType.FIELD_ITEM_LPRICE;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_HPRICE)){
            sType = eXmlFieldItemType.FIELD_ITEM_HPRICE;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_IMAGE)){
            sType = eXmlFieldItemType.FIELD_ITEM_IMAGE;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_MALLNAME)){
            sType = eXmlFieldItemType.FIELD_ITEM_MALLNAME;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_PRODUCTID)){
            sType = eXmlFieldItemType.FIELD_ITEM_PRODUCTID;
        } else if (aName.equals(NAPISearchShopTags.FIELD_ITEM_PRODUCTTYPE)){
            sType = eXmlFieldItemType.FIELD_ITEM_PRODUCTTYPE;
        } else {
            sValid = false;
        }

        if (sValid == true)
        {
            sField = new NaverSearchXmlField(aName, aData, sType);
        }
        return sField;
    }

    @Override
    public String toString()
    {
        return "Name=" + mName + ",Data=" + mData;
    }
}
