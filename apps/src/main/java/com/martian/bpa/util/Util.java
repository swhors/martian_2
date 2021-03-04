/**
 * Created by simpson on 15. 11. 9.
 */
package com.martian.bpa.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Calendar;

public class Util
{
    static final String LOG_TAG="Util";

    private static DecimalFormat mFormat = new DecimalFormat("###,###,###,###");

    static public boolean detectDoubleClick(Object aObject,
                                            boolean aFirstTab,
                                            long aFirstTime,
                                            Method aSetFirstTab,
                                            Method aSetFirstTime) throws InvocationTargetException, IllegalAccessException {
        boolean sRet = false;

        Object[] sParameters1 = new Object[1];
        Object[] sParameters2 = new Object[1];

        if (aFirstTab == false) {
            //
            // 탭 간격 : xx
            // 탭 상태 : 이전 탭이 더블이었음. 현재는 Single임.
            //
            Log.d(LOG_TAG, "detectDoubleClick : 0");
            sParameters1[0] = true;
            sParameters2[0] = Calendar.getInstance().getTimeInMillis();
            aSetFirstTab.invoke(aObject, sParameters1);
            aSetFirstTime.invoke(aObject, sParameters2);
        }
        else
        {
            long sThisTime = Calendar.getInstance().getTimeInMillis();

            if (aFirstTime < sThisTime)
            {
                //Check if times are within our max delay
                if ((sThisTime - aFirstTime) <= 500)
                {
                    //
                    // 탭 간격 : below 500ms
                    // 탭 상태 : 더블 탭
                    //
                    Log.d(LOG_TAG, "Double click..");
                    sRet = true;
                    sParameters1[0] = false;
                    aSetFirstTab.invoke(aObject, sParameters1);
                } else {
                    //
                    // 탭 간격 : over 500ms
                    // 탭 상태 : 500ms를 초과 했으므로, Single 탬 임..
                    //
                    sParameters1[0] = true;
                    sParameters2[0] = sThisTime;
                    aSetFirstTab.invoke(aObject, sParameters1);
                    aSetFirstTime.invoke(aObject, sParameters2);
                }
            }
            else
            {
                Log.d(LOG_TAG, "detectDoubleClick : illegal");
                //
                // 탭 간격 : ??
                // 탭 상태 : 비정상, 초기화 시킴.
                //
                sParameters1[0] = true;
                sParameters2[0] = sThisTime;

                aSetFirstTab.invoke(aObject, sParameters1);
                aSetFirstTime.invoke(aObject, sParameters2);
            }
        }
        return sRet;
    }

    public static void setProperty(Context aContext, String aProperty, boolean aValue) {
        SharedPreferences sPref = aContext.getSharedPreferences(PropertySet.TAG_PREFERENCE, 0);
        SharedPreferences.Editor sEditor = sPref.edit();
        sEditor.putBoolean(aProperty, aValue);
        sEditor.apply();
    }

    public static boolean getProperty(Context aContext, String aProperty, boolean aDefValue) {
        SharedPreferences sPref = aContext.getSharedPreferences(PropertySet.TAG_PREFERENCE, 0);
        return sPref.getBoolean(aProperty, aDefValue);
    }

    public static String toCurrency(Context aContext, int aValue) {
        return mFormat.format(aValue) + aContext.getString(R.string.currency);
    }

    public static boolean setRedrawFlag(Intent sIntent) {
        Boolean sRet = false;
        String sParentClassName = sIntent.getStringExtra(Define.TAG_PARENT_CLASSNAME);
        Log.d(LOG_TAG, "class_name=" + sParentClassName);

        try {
            Class sClass = Class.forName(sParentClassName);
            Field sField = sClass.getDeclaredField(Define.TAG_COMMONVAL_NAME);
            sField.setBoolean(null, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "initCtl + ");
            sRet = true;
        }
        return sRet;
    }
}