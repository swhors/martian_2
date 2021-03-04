/******************************************************************
 * NAPIShopSearchUrl.java
 * swhors@naver.com
 * 2015/11/05
 *
 * 설명 :
 *    1)NAPISearchUrl의 "target=shop"에 대한 확장 클래스 입니다..
 ******************************************************************/

package com.martian.bpa.napisearch;

public class NAPIShopSearchUrl extends NAPISearchUrl implements NAPIUserKey{
    static private final String URL_TARGET="shop";

    public NAPIShopSearchUrl(String aQuery)
    {
        super(USER_KEY, URL_TARGET, aQuery, true);
    }

    static public NAPISearchUrl getInstance(String aQuery)
    {
        return new NAPIShopSearchUrl(aQuery);
    }

}
