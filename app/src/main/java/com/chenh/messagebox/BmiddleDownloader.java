/*
package com.chenh.messagebox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by chenh on 2016/7/17.
 *//*

public class BmiddleDownloader<Token> extends HandlerThread {

    private static final String TAG = "BmiddleDownloader";
    private static final int MESSAGE_DOWNLOAD=0;

    Handler mHandler;
    Map<Token,String> requestMap= Collections.synchronizedMap(new HashMap<Token, String>());

    public BmiddleDownloader(){
        super(TAG);
    }


    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==MESSAGE_DOWNLOAD){
                    Token token=(Token)msg.obj;
                    Log.i(TAG,"Got a request for URL:"+requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }


    public void queueBmiddle(Token token, String url){
        Log.i(TAG,"Got an URL:"+ url);
    }


    private void handleRequest(final Token token){
        */
/*try {
            final String url=requestMap.get(token);
            if (url==null){
                return;
            }
            byte[] bitmapBytes = new Flick
        }*//*

    }


}
*/
