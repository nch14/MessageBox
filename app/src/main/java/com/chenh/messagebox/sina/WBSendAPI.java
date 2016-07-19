package com.chenh.messagebox.sina;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;

import com.chenh.messagebox.ContentActivity;
import com.chenh.messagebox.Item;
import com.chenh.messagebox.LocalItem;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenh on 2016/7/19.
 */
public class WBSendAPI {

    private static final String TAG = WBGetAPI.class.getName();

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    Handler mHandler;

    String uid;


    public WBSendAPI(Context context, Handler mHandler){
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        this.mHandler=mHandler;
        this.uid=uid;
    }


    public void sendWeibo(String content){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.update(content,"0.0","0.0",mListener);
        }
    }

    public void sendWeiboWithPic(String content,Bitmap bitmap){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.upload(content,bitmap,"0.0","0.0",mListener);
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                try {
                    Status status=Status.parse(new JSONObject(response));
                    LocalItem.getLocalItem().addItem(new Item(status));
                    WBGetAPI.getWbGetAPI().sendNotification(ContentActivity.REFRESH_ITEMS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            WBGetAPI.getWbGetAPI().sendNotification(ContentActivity.SEND_FAILED);
            //Toast.makeText(WBStatusAPIActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}

