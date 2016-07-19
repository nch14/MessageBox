package com.chenh.messagebox.sina;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.chenh.messagebox.Item;
import com.chenh.messagebox.LocalItem;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by chenh on 2016/7/17.
 */
public class WBGetAPI {
    private static final String TAG = WBGetAPI.class.getName();

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    private static WBGetAPI wbGetAPI;
    public static void createInstance(Context context){
        wbGetAPI=new WBGetAPI(context);
    }
    public static WBGetAPI getWbGetAPI(){
        return wbGetAPI;
    }
    private WBGetAPI(Context context){
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
    }


    public void getWB(){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
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
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        for(int i=0;i<statuses.statusList.size();i++){
                            LocalItem.addItem(new Item(statuses.statusList.get(i)));
                            Log.v("context",statuses.statusList.get(i).text);
                        }
                        /*Toast.makeText(WBStatusAPIActivity.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();*/
                    }
                }else {
                    //Toast.makeText(WBStatusAPIActivity.this, response, Toast.LENGTH_LONG).show();
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
