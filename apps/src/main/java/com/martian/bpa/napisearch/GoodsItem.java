/***********************************************************************
 * Created by simpson on 15. 11. 5.
 * GoodsItem.java
 *
 * 내용 :
 *     검색결과 자료 구조 클래스.
 *     상품 정보의 하위 클래스 임.
 *     이 클래스의 상위 클래스는 SearchResultShop 임.
 *
 *     기본 검색 데이터 구조 :
 *       SearchResultShop
 *        + GoodsItem
 *        + GoodsItem
 *        + GoodsItem
 *        ....
 *
 ***********************************************************************/

package com.martian.bpa.napisearch;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.martian.bpa.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class GoodsItem implements Parcelable, Comparable<GoodsItem>
{
    final static public String LOG_TAG="GoodsItem";
    final static public String TAG_INTENTNAME="GoodsItem";
    final static public String TAG_PRODUCT_ID="ProductId";
    final static public String TAG_TITLE="Title";

    final static public int ITEM_TYPE_ITEM = 1; // shopping item
    final static public int ITEM_TYPE_NONE = 0; // etc

    // Member
    private String mTitle;
    private String mLink;
    private String mImage;
    private int    mLowPrice;
    private int    mHighPrice;
    private String mMallName;
    private long   mProductId;
    private int    mProductType;
    private int    mLastPrice;
    private String mLastUpdate;
    private int    mUserCheckedPrice;
    private int    mItemType = ITEM_TYPE_ITEM;
    private int    mPrePrice = 0;

    private boolean mActive = false;

    @Override
    public int compareTo(GoodsItem another) {
        GoodsItem sItem = (GoodsItem)another;
        if (getProductId() == sItem.getProductId())
            return 0;
        else if (getProductId() > sItem.getProductId())
            return 1;
        return -1;
    }

    // Constructor
    public GoodsItem()
    {
        mLastPrice = 0;
        mPrePrice = 0;
        mLastUpdate = DateUtil.getCurrentDate();
    }

    public GoodsItem(String aTitle,
                     String aLink,
                     String aImage,
                     int    aLowPrice,
                     int    aHighPrice,
                     String aMallName,
                     int    aProductId,
                     int    aProductType,
                     int    aItemType)
    {
        mTitle = aTitle;
        mLink  = aLink;;
        mImage = aImage;
        mLowPrice = aLowPrice;
        mHighPrice = aHighPrice;
        mMallName = aMallName;
        mProductId = aProductId;
        mProductType = aProductType;
        mLastPrice = 0;
        mLastUpdate = DateUtil.getCurrentDate();
        mItemType = aItemType;
        mPrePrice = 0;
    }

    public GoodsItem(String aTitle,
                     String aLink,
                     String aImage,
                     int    aLowPrice,
                     int    aHighPrice,
                     String aMallName,
                     int    aProductId,
                     int    aProductType)
    {
        mTitle = aTitle;
        mLink  = aLink;;
        mImage = aImage;
        mLowPrice = aLowPrice;
        mHighPrice = aHighPrice;
        mMallName = aMallName;
        mProductId = aProductId;
        mProductType = aProductType;
        mLastPrice = 0;
        mLastUpdate = DateUtil.getCurrentDate();
        mPrePrice = 0;
    }

    protected GoodsItem(Parcel in) {
        mTitle = in.readString();
        mLink = in.readString();
        mImage = in.readString();
        mLowPrice = in.readInt();
        mHighPrice = in.readInt();
        mMallName = in.readString();
        mProductId = in.readLong();
        mProductType = in.readInt();
        mActive = in.readByte() != 0;
        mLastPrice = in.readInt();
        mLastUpdate = in.readString();
        mUserCheckedPrice = in.readInt();
        mPrePrice = in.readInt();
    }

    public static final Creator<GoodsItem> CREATOR = new Creator<GoodsItem>() {
        @Override
        public GoodsItem createFromParcel(Parcel in) {
            return new GoodsItem(in);
        }

        @Override
        public GoodsItem[] newArray(int size) {
            return new GoodsItem[size];
        }
    };

    // Method for Settor
    public void setTitle(String aTitle){mTitle=aTitle;}
    public void setLink(String aLink){mLink=aLink;}
    public void setImage(String aImage){mImage=aImage;}
    public void setLowPrice(int aPrice){mLowPrice=aPrice;}
    public void setHighPrice(int aPrice){mHighPrice=aPrice;}
    public void setMallName(String aMallName){mMallName=aMallName;}
    public void setProductId(long aID){mProductId=aID;}
    public void setProductType(int aType){mProductType=aType;}
    public void setActive(boolean aActive){mActive=aActive;}
    public void setLastPrice(int aLastPrice){mLastPrice=aLastPrice;}
    public void setLastUpdate(String aLastUpdate){mLastUpdate=aLastUpdate;}
    public void setLastUpdateForDate(Date aLastUpdate){mLastUpdate=DateUtil.Date2String(aLastUpdate);}
    public void setUserCheckedPrice(int aPrice){mUserCheckedPrice=aPrice;}
    public void setItemType(int aType){mItemType=aType;}
    public void setPrePrice(int aPrice){mPrePrice=aPrice;}

    // Method fot gettor
    public String getTitle(){return Html.fromHtml(mTitle).toString();}
    public String getLink(){return mLink;}
    public String getImage(){return mImage;}
    public int getLowPrice(){return mLowPrice;}
    public int getHighPrice(){return mHighPrice;}
    public String getMallName(){return mMallName;}
    public long getProductId(){return mProductId;}
    public int getProductType(){return mProductType;}
    public boolean getActive(){return mActive;}
    public int getLastPrice(){return mLastPrice;}
    public String getLastUpdate(){return mLastUpdate;}
    public Date getLastUpdateByDate(){return DateUtil.String2Date(mLastUpdate);}
    public int getUserCheckedPrice(){return mUserCheckedPrice;}
    public int getItemType(){return mItemType;}
    public int getPrePrice(){return mPrePrice;}

    public String toString(){
        return "<item>\n" +
               "    <title>" + mTitle +"</title>\n" +
               "    <link>" + mLink +"</link>\n" +
               "    <image>" + mImage +"</image>\n" +
               "    <lprice>" + mLowPrice +"</lprice>\n" +
               "    <hprice>" + mHighPrice +"</hprice>\n" +
               "    <mallName>" + mMallName +"</mallName>\n" +
               "    <productId>" + mProductId + "</productId>\n" +
               "    <productType>" + mProductType + "</productType>\n" +
               "</item>";
    }

    public String toStringWithExt(){
        return toString() + "\n" +
                "<ext>\n" +
                "    <lastPrice>" + mLastPrice + "</lastPrice>\n" +
                "    <lastUpdate>" + mLastUpdate + "</lastUpdate>\n" +
                "    <prePrice>" + mPrePrice + "</prePrice>\n" +
                "    <userCheckPrice>" + mUserCheckedPrice + "</userCheckPrice>\n" +
                "</ext>";
    }

    /////////////////////////////////////////////////////////////
    // Return contentvalue for insert to db.
    public ContentValues getContentValues()
    {
        ContentValues sValues = new ContentValues();
        sValues.put(NAPISearchShopTags.FIELD_ITEM_PRODUCTID, getProductId());
        sValues.put(NAPISearchShopTags.FIELD_TITLE, getTitle());
        sValues.put(NAPISearchShopTags.FIELD_LINK, getLink());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_IMAGE, getImage());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_LPRICE, getLowPrice());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_HPRICE, getHighPrice());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_MALLNAME, getMallName());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_PRODUCTTYPE, getProductType());
        sValues.put(NAPISearchShopTags.FIELD_EXT_ACTIVITY, (getActive()==true?1:0));
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTPRICE, getLastPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTUPDATE,getLastUpdate());
        sValues.put(NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, getUserCheckedPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_PREPRICE, getPrePrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_REGDATE, System.currentTimeMillis());
        return sValues;
    }

    public ContentValues getContentValues4UpdatePrice()
    {
        ContentValues sValues = new ContentValues();
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTPRICE, getLastPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTUPDATE,getLastUpdate());
        sValues.put(NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, getUserCheckedPrice());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_LPRICE, getLowPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_PREPRICE, getPrePrice());
        return sValues;
    }

    public ContentValues getContentValues4Update()
    {
        ContentValues sValues = new ContentValues();
        sValues.put(NAPISearchShopTags.FIELD_EXT_ACTIVITY, (getActive()==true?1:0));
        sValues.put(NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, getUserCheckedPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTPRICE, getLastPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTUPDATE,getLastUpdate());
        sValues.put(NAPISearchShopTags.FIELD_EXT_PREPRICE, getPrePrice());
        return sValues;
    }

    public ContentValues getContentValues4UpdateForTest()
    {
        ContentValues sValues = new ContentValues();
        sValues.put(NAPISearchShopTags.FIELD_EXT_ACTIVITY, (getActive()==true?1:0));
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTPRICE, getLastPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_LASTUPDATE,getLastUpdate());
        sValues.put(NAPISearchShopTags.FIELD_ITEM_LPRICE, getLowPrice());
        sValues.put(NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, getUserCheckedPrice());
        return sValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mLink);
        dest.writeString(mImage);
        dest.writeInt(mLowPrice);
        dest.writeInt(mHighPrice);
        dest.writeString(mMallName);
        dest.writeLong(mProductId);
        dest.writeInt(mProductType);
        dest.writeByte((byte) (mActive ? 1 : 0));
        dest.writeInt(mLastPrice);
        dest.writeString(getLastUpdate());
        dest.writeInt(mUserCheckedPrice);
        dest.writeInt(mPrePrice);
    }
}
