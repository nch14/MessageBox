package com.chenh.messagebox.sina;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.chenh.messagebox.ContentActivity;
import com.chenh.messagebox.Item;
import com.chenh.messagebox.LocalItem;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenh on 2016/7/18.
 */
public class WBJumpAPI {


    private static final String TAG = WBGetAPI.class.getName();

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    Handler mHandler;

    String uid;


    public WBJumpAPI(Context context,Handler mHandler,String uid){
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        this.mHandler=mHandler;
        this.uid=uid;
    }

    public void getBase62(String mid){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.queryMID(new long[]{Long.parseLong(mid)},1,mListener);
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
                    String  s=new JSONObject(response).getString("mid");
                    mHandler.sendMessage(mHandler.obtainMessage(ContentActivity.JUMP_TO_WEIBO,uid+"/"+s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            //Toast.makeText(WBStatusAPIActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
