/***********************************************************************
 * Created by simpson on 15. 11. 5.
 * SearchResultShop.java
 *
 * 내용 :
 *     검색결과 자료 구조 클래스.
 *     상품 정보의 상위 클래스 임.
 *     이 클래스의 하위 클래스는 GoodsItem 임.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SearchResultShop
{
    static final String ERROR_MSG_HAVE_NO_ITEM="Have no Item.";
    static final String ERROR_MSG_NULL_PTR_ITEM="A item is a null pointer.";
    public class SearchResultNoItemException extends Exception
    {
        private String mMessage = ERROR_MSG_HAVE_NO_ITEM;
        public void setMessgae(String aMsg){mMessage = aMsg;}
        @Override
        public String getMessage(){

            return mMessage;
        }
    }

    public class SearchResultNullItemException extends Exception
    {
        private String mMessage = ERROR_MSG_NULL_PTR_ITEM;
        public void setMessgae(String aMsg){mMessage = aMsg;}
        @Override
        public String getMessage(){

            return mMessage;
        }
    }

    // Member
    public String               mTitle;
    public String               mDescription;
    public String               mLink;
    public Date                 mLastBuildDate;
    public int                  mTotal;
    public int                  mStart;
    public int                  mDisplay;
    public ArrayList<GoodsItem> mItem = new ArrayList<GoodsItem>();

    // Constructor
    public SearchResultShop()
    {
    }

    // Method for Settor
    public void setTitle(String aTitle){mTitle=aTitle;}
    public void setDescription(String aDesc){mDescription=aDesc;}
    public void setLink(String aLink){mLink=aLink;}
    public void setLastBuildDate(Date aDate){mLastBuildDate=aDate;}
    public void setTotal(int aTotal){mTotal=aTotal;}
    public void setStart(int aStart){mStart=aStart;}
    public void setDisplay(int aNum){mDisplay=aNum;}
    public void addItem(GoodsItem aItem){mItem.add(aItem);}
    public void addItem(String aTitle,
                        String aLink,
                        String aImage,
                        int    aLowPrice,
                        int    aHighPrice,
                        String aMallName,
                        int    aProductId,
                        int    aProductType){
        mItem.add(new GoodsItem(aTitle,
                                aLink,
                                aImage,
                                aLowPrice,
                                aHighPrice,
                                aMallName,
                                aProductId,
                                aProductType));
        }

    // Method for gettor
    public String getTitle(){return mTitle;}
    public String getDescription(){return mDescription;}
    public String getLink(){return mLink;}
    public Date   getLastBuildDate(){return mLastBuildDate;}
    public int getTotal(){return mTotal;}
    public int getStart(){return mStart;}
    public int getDisplay(){return mDisplay;}
    public GoodsItem getItem(int aPosition){return mItem.get(aPosition);}
    public GoodsItem [] getItem() throws
            SearchResultNoItemException,
            SearchResultNullItemException {
        if (mItem == null)
            throw new SearchResultNullItemException();
        else if(mItem.size() == 0)
            throw new SearchResultNoItemException();
        return (GoodsItem[])mItem.toArray(new GoodsItem[mItem.size()]);
    }

    public ArrayList<GoodsItem> getItemByArrayList() throws
            SearchResultNoItemException,
            SearchResultNullItemException {
        if (mItem == null)
            throw new SearchResultNullItemException();
        else if(mItem.size() == 0)
            throw new SearchResultNoItemException();
        return mItem;
    }

    // Method
    public String toString(){
        String sItemsText = new String();
        Iterator<GoodsItem> sIterator = mItem.iterator();

        while (sIterator.hasNext()) {
            sItemsText += (sIterator.next().toString() + "\n");
        }

        return "<rss version=\"2.2\">" +
                "<channel>" +
                sItemsText +
                "</channel>" +
                "</rss>";
    }
}