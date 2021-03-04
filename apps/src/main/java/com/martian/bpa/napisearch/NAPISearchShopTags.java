/***********************************************************************
 * Created by simpson on 15. 11. 7.
 * NAPISearchShopTags.java
 *
 * 내용 :
 *     NAPI_SEARCH_SHOP에서 사용되는 필드의 태그 정의.
 ***********************************************************************/
package com.martian.bpa.napisearch;

/**
 * Created by simpson on 2015-11-07.
 */
public class NAPISearchShopTags {
    public static final String FIELD_TITLE="title";
    public static final String FIELD_LINK="link";
    public static final String FIELD_DESC="description";
    public static final String FIELD_LASTBUILDDATE="lastBuildDate";
    public static final String FIELD_TOTAL="total";
    public static final String FIELD_START="start";
    public static final String FIELD_DISPLAY="display";
    public static final String FIELD_ITEM_IMAGE="image";
    public static final String FIELD_ITEM_LPRICE="lprice";
    public static final String FIELD_ITEM_HPRICE="hprice";
    public static final String FIELD_ITEM_MALLNAME="mallName";
    public static final String FIELD_ITEM_PRODUCTID="productId";
    public static final String FIELD_ITEM_PRODUCTTYPE="productType";

    // extern reference definition
    public static final String FIELD_MESSAGE="message";
    public static final String FIELD_ERRORCODE="error_code";

    // delimiter, header
    public static final String FIELD_ITEM    = "item";
    public static final String FIELD_RSS     = "rss";
    public static final String FIELD_CHANNEL = "channel";

    // extra tags
    public static final String FIELD_EXT_ACTIVITY         = "activity";
    public static final String FIELD_EXT_LASTPRICE        = "lastPrice";
    public static final String FIELD_EXT_LASTUPDATE       = "lastUpdate";
    public static final String FIELD_EXT_USERCHECKEDPRICE = "userCheckedPrice";
    public static final String FIELD_EXT_GOODSIMAGE       = "goodsImage";
    public static final String FIELD_EXT_REGDATE          = "regDate";
    public static final String FIELD_EXT_PREPRICE         = "prePrice";

    // internal only
    public static final int TAG_FIELD_TITLE=0;
    public static final int TAG_FIELD_LINK=1;
    public static final int TAG_FIELD_DESC=2;
    public static final int TAG_FIELD_LASTBUILDDATE=3;
    public static final int TAG_FIELD_TOTAL=4;
    public static final int TAG_FIELD_START=5;
    public static final int TAG_FIELD_DISPLAY=6;
    public static final int TAG_FIELD_ITEM=7;
    public static final int TAG_FIELD_ITEM_TITLE=8;
    public static final int TAG_FIELD_ITEM_LINK=9;
    public static final int TAG_FIELD_ITEM_IMAGE=10;
    public static final int TAG_FIELD_ITEM_LPRICE=11;
    public static final int TAG_FIELD_ITEM_HPRICE=12;
    public static final int TAG_FIELD_ITEM_MALLNAME=13;
    public static final int TAG_FIELD_ITEM_PRODUCTID=14;
    public static final int TAG_FIELD_ITEM_PRODUCTTYPE=15;
    public static final int TAG_FIELD_MESSAGE=16;
    public static final int TAG_FIELD_ERRORCODE=17;
    public static final int TAG_FIELD_NONE=18;

    // data search level
    public static final int VALID_GOODS_TYPE_COMPAREPRICE=1;
    public static final int VALID_GOODS_TYPE_COMPAREPRICE_NO_MATCHING=2;
    public static final int VALID_GOODS_TYPE_COMPAREPRICE_MATCHING=3;
    public static final int OLD_GOODS_TYPE_COMPAREPRICE=4;
    public static final int OLD_GOODS_TYPE_NO_MATCHING=5;
    public static final int OLD_GOODS_TYPE_MATCHING=6;
    public static final int INVALID_GOODS_TYPE_COMPAREPRICE=7;
    public static final int INVALID_GOODS_TYPE_COMPAREPRICE_NO_MATCHING=8;
    public static final int INVALID_GOODS_TYPE_COMPAREPRICE_MATCHING=9;
    public static final int WILL_GOODS_TYPE_COMPAREPRICE=10;
    public static final int WILL_GOODS_TYPE_COMPAREPRICE_NO_MATCHING=11;
    public static final int WILL_GOODS_TYPE_COMPAREPRICE_MATCHING=12;
}
