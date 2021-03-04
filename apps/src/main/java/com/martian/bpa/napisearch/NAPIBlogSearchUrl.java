/*********************************************************************************
 * Created by simpson on 2015-11-10.
 *********************************************************************************/
package com.martian.bpa.napisearch;
/*********************************************************************************
 * Protocol :
 * - key	string (필수) : 이용 등록을 통해 받은 key 스트링을 입력합니다.
 * - target	string (필수) : blog,
 *                          서비스를 위해서는 무조건 지정해야 합니다.
 * - query	string (필수) : 검색을 원하는 질의, UTF-8 인코딩 입니다.
 * - display	integer   : 기본값 10, 최대 100 검색결과 출력건수를 지정합니다.
 *                          최대 100까지 가능합니다.
 * - start	integer       : 기본값 1, 최대 1000 검색의 시작위치를 지정할 수 있습니다.
 *                          최대 1000까지 가능합니다.
 * - sort	string        : sim (기본값), date	정렬 옵션입니다.
 *                          * sim : 유사도순 (기본값)
 *                          * date : 날짜순
 *********************************************************************************/
public class NAPIBlogSearchUrl extends NAPISearchUrl implements NAPIUserKey{
    static private final String URL_TARGET="blog";
    public NAPIBlogSearchUrl (String aQuery)
    {
        super(USER_KEY, URL_TARGET, aQuery, true);
    }
    static public NAPISearchUrl getInstance(String aQuery)
    {
        return new NAPIShopSearchUrl(aQuery);
    }
}
