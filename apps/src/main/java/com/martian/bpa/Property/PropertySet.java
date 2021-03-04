/**
 * Created by simpson on 2015-11-08.
 */

package com.martian.bpa.Property;

import android.os.Parcel;
import android.os.Parcelable;

import com.martian.bpa.napisearch.NaverApi;

public class PropertySet implements Parcelable {
    public final static String LOG_TAG = "PropertySet";

    public final static String TAG_PREFERENCE = "martian-2-conf";

    public final static String TAG_CONF_SEARCH_DATACOUNT = "Property_Search_DataCount";
    public final static String TAG_CONF_TRACKIGN_ENABLED = "Property_Tracking_Enabled";
    public final static String TAG_CONF_IS_USER_CHECKED_PRICE = "Property_Is_User_Checked_Price";
    public final static String TAG_INTENTNAME = "PropertySet";

    private int mDisplayCount = NaverApi.DEFAULT_SEARCH_NUM;

    public PropertySet(Parcel in) {
        readFromParcel(in);
    }

    public PropertySet() {

    }

    private void readFromParcel(Parcel in) {
        mDisplayCount = in.readInt();
    }

    public static final Creator<PropertySet> CREATOR = new Creator<PropertySet>() {
        @Override
        public PropertySet createFromParcel(Parcel in) {
            return new PropertySet(in);
        }

        @Override
        public PropertySet[] newArray(int size) {
            return new PropertySet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDisplayCount);
    }

    public void setDisplayCount(int aDisplayCount) {
        mDisplayCount = aDisplayCount;
    }

    public int getDisplayCount() {
        return mDisplayCount;
    }
}
