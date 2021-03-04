/***********************************************************************
 * Created by simpson on 15. 11. 6.
 * ImageLoader.java
 *
 * 내용 :
 *     URL의 이미지를 로딩하여 ImageView에 표시하는 클래스.
 ***********************************************************************/

package com.martian.bpa.util.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.martian.bpa.MainActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = "ImageLoader";

    Bitmap mBmp;
    byte [] mBytes;
    String mImageUrl;

    MainActivity.ImageLoadrInterface mCallback;
    boolean mSuccess = false;

    public ImageLoader(MainActivity.ImageLoadrInterface aCallback, String aUrl)
    {
        mCallback = aCallback;
        mImageUrl = aUrl;
    }

    @Override
    protected String doInBackground(String... params) {
        URL sUrl;
        try {
            sUrl = new URL(mImageUrl);
            mBmp = BitmapFactory.decodeStream(sUrl.openConnection().getInputStream());
            mSuccess = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String aResult) {
        Log.d(LOG_TAG, "onPostExecute (0)");
        if (mSuccess == true)
        {
            mCallback.ImageLoad(mBmp);
        }
    }
}
