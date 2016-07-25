package com.chenh.messagebox.twiiter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chenh.messagebox.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterAuthActivity extends AppCompatActivity {
    private static final int LOAD_URL=1;
    private WebView mWebView;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_auth);

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case LOAD_URL:
                        mWebView.loadUrl((String) msg.obj);
                        break;

                }
            }
        };
        mWebView= (WebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        getUrl();
    }

     private void getUrl(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 Twitter twitter = LocalTwitterTool.getTwitter();
                 RequestToken requestToken = null;
                 try {
                      requestToken =LocalTwitterTool.getRequestToken();
                 } catch (TwitterException e) {
                     e.printStackTrace();
                 }
                 mHandler.sendMessage(mHandler.obtainMessage(LOAD_URL,requestToken.getAuthorizationURL()));
             }
         }).start();

     }

    private static void storeAccessToken(int useId, AccessToken accessToken){
        //store accessToken.getToken()
        //store accessToken.getTokenSecret()
    }
}
