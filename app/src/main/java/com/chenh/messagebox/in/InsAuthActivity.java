package com.chenh.messagebox.in;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chenh.messagebox.R;

public class InsAuthActivity extends AppCompatActivity {
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_auth);
//https://api.instagram.com/oauth/authorize/?client_id=abd0b1b0d3ee4185a7e5cb66b57392ea&redirect_uri=www.baidu.com&response_type=token
        String url="https://api.instagram.com/oauth/authorize/?client_id="+R.string.instagram_app_id+"&redirect_uri=www.baidu.com&response_type=token";

        mWebView= (WebView) findViewById(R.id.WebView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //mWebView.loadUrl(url);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
