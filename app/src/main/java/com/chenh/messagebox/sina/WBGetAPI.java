package com.chenh.messagebox.sina;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.chenh.messagebox.ContentActivity;
import com.chenh.messagebox.LocalItem;
import com.chenh.messagebox.util.CenterController;
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

    private Handler mHandler;

    private static WBGetAPI wbGetAPI;
    public static void createInstance(Context context,Handler handler){
        if (wbGetAPI==null){
            wbGetAPI=new WBGetAPI(context);
            wbGetAPI.mHandler=handler;
        }
    }
    public static WBGetAPI getWbGetAPI(){
        return wbGetAPI;
    }
    private WBGetAPI(Context context){
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

        if (mAccessToken!=null){
            CenterController.getCenterController().setSinaModule(true);
        }
    }


    public void getNewWB(){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(LocalItem.getLocalItem().weiboIdEnd, 0L, 10, 1, false, 0, false, mListener);
        }
    }

    public void getNextWB(){
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            mStatusesAPI.friendsTimeline(0L, LocalItem.getLocalItem().weiboIdStart, 10, 1, false, 0, false, mListener);
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
                        while (LocalItem.getLocalItem().updating){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        LocalItem.getLocalItem().addItem(statuses.statusList);
                        CenterController.getCenterController().setWeiboLoadState(CenterController.WEIBO_LOADED);
                        //mHandler.sendMessage(mHandler.obtainMessage(ContentActivity.REFRESH_ITEMS,""));
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

    public void sendNotification(int what){
        mHandler.sendMessage(mHandler.obtainMessage(what,""));
    }
}
