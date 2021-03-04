/***********************************************************************
 * Created by simpson on 15. 11. 7.
 * DBUtil.java
 *
 * 내용 :
 *     SQLite3에 데이터를 저장하고 읽는 프로세스 정의.
 ***********************************************************************/

package com.martian.bpa.util.dbutil;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.Log;

import com.martian.bpa.MainActivity;
import com.martian.bpa.napisearch.GoodsItem;
import com.martian.bpa.napisearch.NAPISearchShopTags;
import com.martian.bpa.util.loader.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.text.BreakIterator;
import java.util.ArrayList;

/**
 * Created by simpson on 2015-11-07.
 */
public class DBUtil extends SQLiteOpenHelper{

    //////////////////////////////////////////
    // Definition Exception
    //
    static public class DBUtilLoadImageNullException extends Exception{
        private final String mMessage = "Can't read goods image.";

        @Override
        public String getMessage(){return mMessage;}
    }
    private final static String LOG_TAG        = "DBUtil";
    private final static int    VERSION        = 7;
    private final static String SQL_TABLE      = "goodlist";
    private final static String DB_NAME        = "TrackingGoods";
    private final static int    NAME_FIELD_LEN = 128;
    private final static int    URL_FIELD_LEN  = 512;
    private final static int    DATA_FIELD_LEN = 64;

    //////////////////////////////////////////
    // Definition of Inner Class for Column Info
    //
    private static class COLINFO {
        public String mName;
        public String mType;
        public int mColNum;

        public COLINFO(String aName, String aType, int aColNum) {
            mName = aName;
            mType = aType;
            mColNum = aColNum;
        }
    }

    private final static COLINFO SQL_COL_PRODUCTID = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_PRODUCTID, "Integer", 0);
    private final static COLINFO SQL_COL_TITLE = new COLINFO(
            NAPISearchShopTags.FIELD_TITLE, "CHAR(" + NAME_FIELD_LEN + ")", 1);
    private final static COLINFO SQL_COL_LINK = new COLINFO(
            NAPISearchShopTags.FIELD_LINK, "CHAR(" + URL_FIELD_LEN + ")", 2);
    private final static COLINFO SQL_COL_IMAGE = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_IMAGE, "CHAR(" + URL_FIELD_LEN + ")", 3);
    private final static COLINFO SQL_COL_LOWPRICE = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_LPRICE, "Integer", 4);
    private final static COLINFO SQL_COL_HIGHPRICE = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_HPRICE, "Integer", 5);
    private final static COLINFO SQL_COL_MALLNAME = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_MALLNAME, "CHAR(" + NAME_FIELD_LEN + ")", 6);
    private final static COLINFO SQL_COL_PRODUCTTYPE = new COLINFO(
            NAPISearchShopTags.FIELD_ITEM_PRODUCTTYPE, "Integer", 7);
    private final static COLINFO SQL_COL_ACTIVE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_ACTIVITY, "Integer", 8);
    private final static COLINFO SQL_COL_LASTPRICE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_LASTPRICE, "Integer", 9);
    private final static COLINFO SQL_COL_LASTUPDATE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_LASTUPDATE, "CHAR(" + DATA_FIELD_LEN + ")", 10);
    private final static COLINFO SQL_COL_USERCHECKEDPRICE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, "Integer", 11);
    private final static COLINFO SQL_COL_PREPRICE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_PREPRICE, "Integer", 12);
    private final static COLINFO SQL_COL_GOODSIMAGE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_GOODSIMAGE, "BLOB", 13);
    private final static COLINFO SQL_COL_REGDATE = new COLINFO(
            NAPISearchShopTags.FIELD_EXT_REGDATE, "DATETIME", 14);

    private final static String SQL_COL1_NAME = SQL_COL_PRODUCTID.mName;
    private final static String SQL_COL1_TYPE = SQL_COL_PRODUCTID.mType;
    private final static String SQL_COL2_NAME = SQL_COL_TITLE.mName;
    private final static String SQL_COL2_TYPE = SQL_COL_TITLE.mType;
    private final static String SQL_COL3_NAME = SQL_COL_LINK.mName;
    private final static String SQL_COL3_TYPE = SQL_COL_LINK.mType;
    private final static String SQL_COL4_NAME = SQL_COL_IMAGE.mName;
    private final static String SQL_COL4_TYPE = SQL_COL_IMAGE.mType;
    private final static String SQL_COL5_NAME = SQL_COL_LOWPRICE.mName;
    private final static String SQL_COL5_TYPE = SQL_COL_LOWPRICE.mType;
    private final static String SQL_COL6_NAME = SQL_COL_HIGHPRICE.mName;
    private final static String SQL_COL6_TYPE = SQL_COL_HIGHPRICE.mType;
    private final static String SQL_COL7_NAME = SQL_COL_MALLNAME.mName;
    private final static String SQL_COL7_TYPE = SQL_COL_MALLNAME.mType;
    private final static String SQL_COL8_NAME = SQL_COL_PRODUCTTYPE.mName;
    private final static String SQL_COL8_TYPE = SQL_COL_PRODUCTTYPE.mType;
    private final static String SQL_COL9_NAME = SQL_COL_ACTIVE.mName;
    private final static String SQL_COL9_TYPE = SQL_COL_ACTIVE.mType;
    private final static String SQL_COL10_NAME = SQL_COL_LASTPRICE.mName;
    private final static String SQL_COL10_TYPE = SQL_COL_LASTPRICE.mType;
    private final static String SQL_COL11_NAME = SQL_COL_LASTUPDATE.mName;
    private final static String SQL_COL11_TYPE = SQL_COL_LASTUPDATE.mType;
    private final static String SQL_COL12_NAME = SQL_COL_USERCHECKEDPRICE.mName;
    private final static String SQL_COL12_TYPE = SQL_COL_USERCHECKEDPRICE.mType;
    private final static String SQL_COL13_NAME = SQL_COL_PREPRICE.mName;
    private final static String SQL_COL13_TYPE = SQL_COL_PREPRICE.mType;
    private final static String SQL_COL14_NAME = SQL_COL_GOODSIMAGE.mName; // image binary
    private final static String SQL_COL14_TYPE = SQL_COL_GOODSIMAGE.mType; // image binary
    private final static String SQL_COL15_NAME = SQL_COL_REGDATE.mName;    // regdate
    private final static String SQL_COL15_TYPE = SQL_COL_REGDATE.mType;    // regdate

    private final static String SQL_CREATE_TABLE = "create table " + DBUtil.SQL_TABLE + " (" +
            DBUtil.SQL_COL1_NAME + " " + DBUtil.SQL_COL1_TYPE + " primary key," +
            DBUtil.SQL_COL2_NAME + " " + DBUtil.SQL_COL2_TYPE + "," +
            DBUtil.SQL_COL3_NAME + " " + DBUtil.SQL_COL3_TYPE + "," +
            DBUtil.SQL_COL4_NAME + " " + DBUtil.SQL_COL4_TYPE + "," +
            DBUtil.SQL_COL5_NAME + " " + DBUtil.SQL_COL5_TYPE + "," +
            DBUtil.SQL_COL6_NAME + " " + DBUtil.SQL_COL6_TYPE + "," +
            DBUtil.SQL_COL7_NAME + " " + DBUtil.SQL_COL7_TYPE + "," +
            DBUtil.SQL_COL8_NAME + " " + DBUtil.SQL_COL8_TYPE + "," +
            DBUtil.SQL_COL9_NAME + " " + DBUtil.SQL_COL9_TYPE + "," +
            DBUtil.SQL_COL10_NAME + " " + DBUtil.SQL_COL10_TYPE + "," +
            DBUtil.SQL_COL11_NAME + " " + DBUtil.SQL_COL11_TYPE + "," +
            DBUtil.SQL_COL12_NAME + " " + DBUtil.SQL_COL12_TYPE + "," +
            DBUtil.SQL_COL13_NAME + " " + DBUtil.SQL_COL13_TYPE + "," +
            DBUtil.SQL_COL14_NAME + " " + DBUtil.SQL_COL14_TYPE + "," +
            DBUtil.SQL_COL15_NAME + " " + DBUtil.SQL_COL15_TYPE + " )";

    private final static String SQL_DELETE_DATA = "delete from " + SQL_TABLE + " where ";
    private final static String SQL_UPGRADE = "drop table if exists " + SQL_TABLE;

    private Context mCtx;

    private static DBUtil mDBUtil = null;

    public static DBUtil getInstance(Context aCtx) {
        if (mDBUtil == null) {
            mDBUtil = new DBUtil(aCtx);
        }
        return mDBUtil;
    }

    public DBUtil(Context aCtx, String aName, SQLiteDatabase.CursorFactory aFactory, int aVersion) {
        super(aCtx, aName, aFactory, aVersion);
        mCtx = aCtx;
    }

    public DBUtil(Context aCtx) {
        super(aCtx, DB_NAME, null, VERSION);
        mCtx = aCtx;
    }

    @Override
    public void onCreate(SQLiteDatabase aDB) {
        //Log.d(LOG_TAG, "onCreate : 1");
        aDB.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase aDB, int aOldVersion, int aNewVersion) {
        //Log.d(LOG_TAG, "onUpgrade : 1");
        aDB.execSQL(SQL_UPGRADE);
        aDB.execSQL(SQL_CREATE_TABLE);
    }

    private void showDBErrorMsg(String aTitle, String aMsg) {
        (new AlertDialog.Builder(mCtx)).
                setTitle(LOG_TAG + " : " + aTitle).
                setMessage(aMsg).
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).
                show();
    }

    public boolean insertGoodsImage(String aImageUrl, final long aPID)
    {
        ImageLoader sImgLoader = new ImageLoader(new MainActivity.ImageLoadrInterface() {
            @Override
            public void ImageLoad(Bitmap aBmp) {
                insertGoodsImage(aBmp, aPID);
            }
        }, aImageUrl);
        sImgLoader.execute();
        return false;
    }

    public boolean insertGoodsImage(Bitmap aBitmap, long aPID) {
        boolean sRet = false;
        ContentValues sValue = new ContentValues();
        SQLiteDatabase sDB = getWritableDatabase();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] sByteImg = baos.toByteArray();

        sValue.put(NAPISearchShopTags.FIELD_EXT_GOODSIMAGE, sByteImg);

        try {
            sDB.update(DBUtil.SQL_TABLE, sValue,
                    DBUtil.SQL_COL_PRODUCTID.mName + "=" + aPID,
                    null);
            sRet = true;
        } catch(Exception e) {
            showDBErrorMsg("insertGoodsImage", e.getMessage().toString());
        }

        if (sDB.isOpen() == true) {
            sDB.close();
        }
        return sRet;
    }

    public Bitmap readGoodsImage(Long aGoodsID) throws DBUtilLoadImageNullException {
        byte [] sByteImage = null;
        Bitmap  sImage = null;
        String sQuery = "select " + DBUtil.SQL_COL14_NAME +
                " from " + DBUtil.SQL_TABLE +
                " where " + DBUtil.SQL_COL_PRODUCTID.mName  + "=" + aGoodsID +
                ";";
        //Log.d(LOG_TAG, "readGoodsImage : query=" + sQuery);
        SQLiteDatabase sDB = getReadableDatabase();

        try {
            Cursor sCursor = sDB.rawQuery(sQuery, null);

            //Log.d(LOG_TAG, "readGoodsImage : RowNum    = " + sCursor.getCount());
            //Log.d(LOG_TAG, "readGoodsImage : ColumnNum = " + sCursor.getColumnCount());
            if (sCursor.moveToFirst())
            {
                sByteImage = sCursor.getBlob(0);
                sImage = Bitmap.createBitmap(
                        BitmapFactory.decodeByteArray(
                                sByteImage,
                                0,
                                sByteImage.length));
            }
        } catch (Exception e) {
            sImage = null;
            Log.d(LOG_TAG, "Exception : readGoodsImage + " + e.getMessage());
        }

        if (sDB.isOpen() == true) {
            sDB.close();
        }

        if (sImage == null)
            throw new DBUtilLoadImageNullException();
        return sImage;
    }

    public Object readAll(boolean aReturnIsArray)
    {
        ArrayList<GoodsItem> sList = new ArrayList<GoodsItem>();
        String sQuery = "select " +
                DBUtil.SQL_COL1_NAME + "," +
                DBUtil.SQL_COL2_NAME + "," +
                DBUtil.SQL_COL3_NAME + "," +
                DBUtil.SQL_COL4_NAME + "," +
                DBUtil.SQL_COL5_NAME + "," +
                DBUtil.SQL_COL6_NAME + "," +
                DBUtil.SQL_COL7_NAME + "," +
                DBUtil.SQL_COL8_NAME + "," +
                DBUtil.SQL_COL9_NAME + "," +
                DBUtil.SQL_COL10_NAME + "," +
                DBUtil.SQL_COL11_NAME + "," +
                DBUtil.SQL_COL12_NAME + "," +
                DBUtil.SQL_COL13_NAME +
                " from " + DBUtil.SQL_TABLE + " " +
                " order by " + DBUtil.SQL_COL15_NAME +" DESC;";
        Log.d(LOG_TAG, "readAll : query=" + sQuery);
        SQLiteDatabase sDB = getReadableDatabase();
        try {
            Cursor sCursor = sDB.rawQuery(sQuery, null);
            if (sCursor.moveToFirst()) {
                do {
                    Log.d(LOG_TAG, "readAll : RowNum    = " + sCursor.getCount());
                    Log.d(LOG_TAG, "readAll : ColumnNum = " + sCursor.getColumnCount());
                    if (sCursor.getString(DBUtil.SQL_COL_TITLE.mColNum) != null) {
                        GoodsItem sGood = new GoodsItem();
                        sGood.setProductId(Long.parseLong(sCursor.getString(DBUtil.SQL_COL_PRODUCTID.mColNum)));
                        sGood.setTitle(sCursor.getString(DBUtil.SQL_COL_TITLE.mColNum));
                        sGood.setLink(sCursor.getString(DBUtil.SQL_COL_LINK.mColNum));
                        sGood.setImage(sCursor.getString(DBUtil.SQL_COL_IMAGE.mColNum));
                        sGood.setLowPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_LOWPRICE.mColNum)));
                        sGood.setHighPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_HIGHPRICE.mColNum)));
                        sGood.setMallName(sCursor.getString(DBUtil.SQL_COL_MALLNAME.mColNum));
                        sGood.setProductType(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_PRODUCTTYPE.mColNum)));
                        sGood.setActive(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_ACTIVE.mColNum)) == 0 ? false : true);
                        sGood.setLastPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_LASTPRICE.mColNum)));
                        sGood.setLastUpdate(sCursor.getString(DBUtil.SQL_COL_LASTUPDATE.mColNum));
                        sGood.setUserCheckedPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_USERCHECKEDPRICE.mColNum)));
                        sGood.setPrePrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_PREPRICE.mColNum)));
                        //Log.d(LOG_TAG, "readAll : 5\n" + sGood.toStringWithExt());
                        sList.add(sGood);
                    }
                } while (sCursor.moveToNext());
            }
            sCursor.close();
        } catch (Exception e) {
            showDBErrorMsg("readAll", e.getMessage().toString());
        }
        if (aReturnIsArray == true)
            return (GoodsItem [])sList.toArray(new GoodsItem[sList.size()]);
        return sList;
    }

    public GoodsItem readGoods(long aProductId) {
        String sQuery = "select " +
                DBUtil.SQL_COL1_NAME + "," +
                DBUtil.SQL_COL2_NAME + "," +
                DBUtil.SQL_COL3_NAME + "," +
                DBUtil.SQL_COL4_NAME + "," +
                DBUtil.SQL_COL5_NAME + "," +
                DBUtil.SQL_COL6_NAME + "," +
                DBUtil.SQL_COL7_NAME + "," +
                DBUtil.SQL_COL8_NAME + "," +
                DBUtil.SQL_COL9_NAME + "," +
                DBUtil.SQL_COL10_NAME + "," +
                DBUtil.SQL_COL11_NAME + "," +
                DBUtil.SQL_COL12_NAME + "," +
                DBUtil.SQL_COL13_NAME +
                " from " + DBUtil.SQL_TABLE + " " +
                " where " + SQL_COL1_NAME + " = " + aProductId;
        Log.d(LOG_TAG, "readAll : query=" + sQuery);
        GoodsItem sGood = null;
        SQLiteDatabase sDB = getReadableDatabase();
        try {
            Cursor sCursor = sDB.rawQuery(sQuery, null);
            if (sCursor.moveToFirst()) {
                Log.d(LOG_TAG, "readAll : RowNum    = " + sCursor.getCount());
                Log.d(LOG_TAG, "readAll : ColumnNum = " + sCursor.getColumnCount());
                if (sCursor.getString(DBUtil.SQL_COL_TITLE.mColNum) != null) {
                    sGood = new GoodsItem();
                    sGood.setProductId(Long.parseLong(sCursor.getString(DBUtil.SQL_COL_PRODUCTID.mColNum)));
                    sGood.setTitle(sCursor.getString(DBUtil.SQL_COL_TITLE.mColNum));
                    sGood.setLink(sCursor.getString(DBUtil.SQL_COL_LINK.mColNum));
                    sGood.setImage(sCursor.getString(DBUtil.SQL_COL_IMAGE.mColNum));
                    sGood.setLowPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_LOWPRICE.mColNum)));
                    sGood.setHighPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_HIGHPRICE.mColNum)));
                    sGood.setMallName(sCursor.getString(DBUtil.SQL_COL_MALLNAME.mColNum));
                    sGood.setProductType(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_PRODUCTTYPE.mColNum)));
                    sGood.setActive(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_ACTIVE.mColNum)) == 0 ? false : true);
                    sGood.setLastPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_LASTPRICE.mColNum)));
                    sGood.setLastUpdate(sCursor.getString(DBUtil.SQL_COL_LASTUPDATE.mColNum));
                    sGood.setUserCheckedPrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_USERCHECKEDPRICE.mColNum)));
                    sGood.setPrePrice(Integer.parseInt(sCursor.getString(DBUtil.SQL_COL_PREPRICE.mColNum)));
                }
            }
            sCursor.close();
        } catch (Exception e) {
            showDBErrorMsg("readAll", e.getMessage().toString());
        }

        return sGood;
    }

    public boolean insertGoods(GoodsItem aGoods)
    {
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            //Log.d(LOG_TAG, "insertGoods : 1");
            ContentValues sValues = aGoods.getContentValues();
            //Log.d(LOG_TAG, "insertGoods : 2");
            sDB.insert(DBUtil.SQL_TABLE, null, sValues);
            sDB.close();
            return true;
        } catch(Exception e) {
            showDBErrorMsg("insertGoods", e.getMessage().toString());
        }

        if (sDB.isOpen() == true) {
            sDB.close();
        }
        return false;
    }

    public boolean removeGoods(long aID)
    {
        boolean sRet = false;
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            String sSQL = SQL_DELETE_DATA +
                    SQL_COL_PRODUCTID.mName + "=\'" + aID + "\';";
            //Log.d(LOG_TAG, "removeGoods : 6");
            //Log.d(LOG_TAG, "removeGoods : " + sSQL);
            sDB.execSQL(sSQL);
            sRet = true;
        } catch(Exception e) {
            showDBErrorMsg("removeGoods", e.getMessage().toString());
        }
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return sRet;
    }

    public boolean removeGoods(GoodsItem aGoods)
    {
        boolean sRet = false;
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            String sSQL = SQL_DELETE_DATA +
                    SQL_COL_TITLE.mName + "=\'" + aGoods.getTitle() + "\';";
            //Log.d(LOG_TAG, "removeGoods : 6");
            //Log.d(LOG_TAG, "removeGoods : " + sSQL);
            sDB.execSQL(sSQL);
            sRet = true;
        } catch(Exception e) {
            showDBErrorMsg("removeGoods", e.getMessage().toString());
        }
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return sRet;
    }

    public boolean updateGoods(GoodsItem aGoods)
    {
        boolean sRet = false;
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            //Log.d(LOG_TAG, "updateGoods : 1");
            //Log.d(LOG_TAG, "updateGoods : "+ aGoods.toString());
            ContentValues sValues = aGoods.getContentValues4Update();
            sDB.update(DBUtil.SQL_TABLE, sValues,
                    DBUtil.SQL_COL_PRODUCTID.mName + "=" + aGoods.getProductId(),
                    null);
            sDB.close();
            sRet = true;
        } catch(Exception e) {
            showDBErrorMsg("removeGoods", e.getMessage().toString());
        }
        //Log.d(LOG_TAG, "updateGoods : 5");
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return sRet;
    }

    public boolean updateGoodsValue(GoodsItem aGoods)
    {
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            //Log.d(LOG_TAG, "updateGoods : 1");
            //Log.d(LOG_TAG, "updateGoods : "+ aGoods.toString());
            ContentValues sValues = aGoods.getContentValues4UpdatePrice();
            sDB.update(DBUtil.SQL_TABLE, sValues,
                    DBUtil.SQL_COL_PRODUCTID.mName + "=" + aGoods.getProductId(),
                    null);
            sDB.close();
            return true;
        } catch(Exception e) {
            showDBErrorMsg("updateGoodsValue", e.getMessage().toString());
        }
        //Log.d(LOG_TAG, "updateGoods : 5");
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return false;
    }

    public boolean updateGoodsForTest(GoodsItem aGoods)
    {
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            //Log.d(LOG_TAG, "updateGoods : 1");
            //Log.d(LOG_TAG, "updateGoods : "+ aGoods.toString());
            ContentValues sValues = aGoods.getContentValues4UpdateForTest();
            sDB.update(DBUtil.SQL_TABLE, sValues,
                    DBUtil.SQL_COL_PRODUCTID.mName + "=" + aGoods.getProductId(),
                    null);
            sDB.close();
            return true;
        } catch(Exception e) {
            showDBErrorMsg("updateGoodsForTest", e.getMessage().toString());
        }
        //Log.d(LOG_TAG, "updateGoods : 5");
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return false;
    }

    public boolean updateGoodsForTest(Long aID, boolean aActive, int aLastPrice, String aLastUpdate, int aLowPrice, int aCheckedPrice)
    {
        SQLiteDatabase sDB = getWritableDatabase();
        try {
            //Log.d(LOG_TAG, "updateGoods : 1");
            //Log.d(LOG_TAG, "updateGoods : "+ aGoods.toString());
            ContentValues sValues = new ContentValues();
            sValues.put(NAPISearchShopTags.FIELD_EXT_ACTIVITY, (aActive==true?1:0));
            sValues.put(NAPISearchShopTags.FIELD_EXT_LASTPRICE, aLastPrice);
            sValues.put(NAPISearchShopTags.FIELD_EXT_LASTUPDATE,aLastUpdate);
            sValues.put(NAPISearchShopTags.FIELD_ITEM_LPRICE, aLowPrice);
            sValues.put(NAPISearchShopTags.FIELD_EXT_USERCHECKEDPRICE, aCheckedPrice);
            sDB.update(DBUtil.SQL_TABLE, sValues,
                    DBUtil.SQL_COL_PRODUCTID.mName + "=" + aID,
                    null);
            sDB.close();
            return true;
        } catch(Exception e) {
            showDBErrorMsg("updateGoodsForTest", e.getMessage().toString());
        }
        //Log.d(LOG_TAG, "updateGoods : 5");
        if (sDB.isOpen() == true) {
            sDB.close();
        } else {
            /* do nothing. */
        }
        return false;
    }
}