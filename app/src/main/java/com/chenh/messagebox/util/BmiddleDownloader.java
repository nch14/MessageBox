package com.chenh.messagebox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chenh on 2016/7/17.
 */
public class BmiddleDownloader {

    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void downBitMap(final ArrayList<String> urls, final ArrayList<Bitmap> bitmaps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<urls.size();i++){
                    String url=urls.get(i).replace("thumbnail","bmiddle");
                    bitmaps.add(returnBitMap(url));
                }
            }
        }).start();
    }

}
