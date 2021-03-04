/******************************************************************
 * NAPIShopUrl.java
 * swhors@naver.com
 * 2015/11/05
 *
 * 설명 :
 *    1)NAPISearchUrl의 "target=shop"에 대한 확장 클래스 입니다..
 ******************************************************************/

package com.martian.bpa.napisearch;

public class NAPIShopUrl extends NAPISearchUrl implements NAPIUserKey{
    static private final String SHOP_TARGET="shop";

    public NAPIShopUrl(String aQuery)
    {
        super(USER_KEY, SHOP_TARGET, aQuery, true);
    }

    static public NAPISearchUrl getInstance(String aQuery)
    {
        NAPISearchUrl sUrl = new NAPIShopUrl(aQuery);
        sUrl.setSortTypeSim();
        return sUrl;
    }

}
