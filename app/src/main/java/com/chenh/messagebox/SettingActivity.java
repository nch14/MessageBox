package com.chenh.messagebox;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chenh.messagebox.fb.FBGetApi;
import com.chenh.messagebox.in.InsAuthActivity;
import com.chenh.messagebox.sina.AccessTokenKeeper;
import com.chenh.messagebox.sina.Constants;
import com.chenh.messagebox.twiiter.FinishPinDialog;
import com.chenh.messagebox.twiiter.LocalTwitterTool;
import com.chenh.messagebox.twiiter.TwitterAuthActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "weibosdk";

    /** 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;

    private AuthInfo mAuthInfo;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;

    private CallbackManager callbackManager;




    Switch weiboSwitch;
    Switch qzoneSwitch;
    Switch twitterSwitch;
    Switch facebookSwitch;

    private TextView mTwitterPin;
    private ImageView mTwitterPinButton;
    private boolean hasTwitterHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("授权管理");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView imageView=(ImageView)findViewById(R.id.imageView3);
        imageView.setImageResource(R.drawable.head_59);

        weiboSwitch= (Switch) findViewById(R.id.switch_weibo);
        weiboSwitch.setOnClickListener(new MyOnCheckedChangeListener());

        qzoneSwitch= (Switch) findViewById(R.id.switch_qzone);
        qzoneSwitch.setOnClickListener(new MyOnCheckedChangeListener());

        twitterSwitch= (Switch) findViewById(R.id.switch_twitter);
        twitterSwitch.setOnClickListener(new MyOnCheckedChangeListener());

        facebookSwitch= (Switch) findViewById(R.id.switch_facebook);
        facebookSwitch.setOnClickListener(new MyOnCheckedChangeListener());

        mTwitterPin= (TextView) findViewById(R.id.twitter_pin);
        mTwitterPin.setOnClickListener(new MyOnCheckedChangeListener());
        mTwitterPinButton=(ImageView)findViewById(R.id.imageView2);
        mTwitterPinButton.setOnClickListener(new MyOnCheckedChangeListener());

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(SettingActivity.this, mAuthInfo);

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
            weiboSwitch.setChecked(true);
        }

        twitter4j.auth.AccessToken accessToken =loadAccessToken(0);
        if (accessToken!=null){
            hasTwitterHistory=true;
            twitterSwitch.setChecked(true);
            LocalTwitterTool.getTwitter().setOAuthAccessToken(accessToken);
        }

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SettingActivity.this, "facebook_account_oauth_Success", Toast.LENGTH_SHORT);
                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                //TODO：got the token，Notify server，and do something
            }

            @Override
            public void onCancel() {
                Toast.makeText(SettingActivity.this, "facebook_account_oauth_Cancel", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(SettingActivity.this, "facebook_account_oauth_Error", Toast.LENGTH_SHORT);
                Log.e(TAG, "e: " + e);
            }
        });
    }



    class MyOnCheckedChangeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            FragmentManager fm=null;
            FinishPinDialog dialog=null;
            switch (id){
                case R.id.switch_weibo:
                    if (weiboSwitch.isChecked()){
                        // SSO 授权, ALL IN ONE   如果手机安装了微博客户端则使用客户端授权,没有则进行网页授权
                        mSsoHandler.authorize(new AuthListener());
                    }else {
                        AccessTokenKeeper.clear(getApplicationContext());
                        mAccessToken = new Oauth2AccessToken();
                        updateTokenView(false);
                    }
                    break;
                case R.id.switch_qzone:
                    if(qzoneSwitch.isChecked()){
                        startActivity(new Intent(SettingActivity.this, InsAuthActivity.class));
                    }
                    break;
                case R.id.switch_twitter:
                    if (twitterSwitch.isChecked()){
                        //进行网页授权
                        startActivity(new Intent(SettingActivity.this, TwitterAuthActivity.class));
                    }else {

                    }
                    break;
                case R.id.switch_facebook:
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken == null || accessToken.isExpired()) {
                        LoginManager.getInstance().logInWithReadPermissions(SettingActivity.this, Arrays.asList("public_profile", "user_friends"));
                    }
                    //FBGetApi.getFB();
                    break;
                case R.id.twitter_pin:
                    fm=getFragmentManager();
                    dialog= FinishPinDialog.newInstance();
                    dialog.show(fm,"");
                    break;
                case R.id.imageView2:
                    fm=getFragmentManager();
                    dialog= FinishPinDialog.newInstance();
                    dialog.show(fm,"");
                    break;
            }

        }
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        FBGetApi.getFB();

    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(SettingActivity.this, mAccessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                //Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            /*Toast.makeText(WBAuthActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();*/
        }

        @Override
        public void onCancel() {
           /* Toast.makeText(WBAuthActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();*/
        }
    }

    public void finishTwitterPin(final String pin){
        mTwitterPin.setText(pin);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter twitter = TwitterFactory.getSingleton();
                RequestToken requestToken = null;
                try {
                    requestToken = LocalTwitterTool.getRequestToken();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                twitter4j.auth.AccessToken accessToken = null;
                try{
                    if(pin.length() > 0){
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    }else{
                        accessToken = twitter.getOAuthAccessToken();
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
                //persist to the accessToken for future reference.
                try {
                    storeAccessToken((int)twitter.verifyCredentials().getId() , accessToken);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void  readItems(){

    }

    private void storeAccessToken(int useId, twitter4j.auth.AccessToken accessToken){
        File filesDir=getFilesDir();
        File todoFile=new File(filesDir,"twitter.txt");
        ArrayList<String> keys=new ArrayList<>();
        keys.add(accessToken.getToken());
        keys.add(accessToken.getTokenSecret());
        try {
            FileUtils.writeLines(todoFile,keys);
        }catch (IOException e){
            e.printStackTrace();
        }
        //store accessToken.getToken()
        //store accessToken.getTokenSecret()
    }

    private twitter4j.auth.AccessToken loadAccessToken(int useId){
        File filesDir=getFilesDir();
        File todoFile=new File(filesDir,"twitter.txt");
        ArrayList<String> keys;
        try {
            keys=new ArrayList<String>(FileUtils.readLines(todoFile));
            String token = keys.get(0);// load from a persistent store
            String tokenSecret = keys.get(1); // load from a persistent store
            return new twitter4j.auth.AccessToken(token, tokenSecret);
        }catch (IOException e){
            keys=new ArrayList<>();
            return null;
        }
    }

    /**
     * 显示当前weibo Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        //mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
        //Toast.makeText(SettingActivity.this,String.format(format, mAccessToken.getToken(), date),Toast.LENGTH_LONG).show();

        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        //mTokenText.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(SettingActivity.this,ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
