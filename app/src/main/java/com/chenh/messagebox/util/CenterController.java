package com.chenh.messagebox.util;

import android.content.Context;
import android.os.Handler;

import com.chenh.messagebox.ContentActivity;
import com.chenh.messagebox.sina.WBGetAPI;
import com.chenh.messagebox.twiiter.TwitterGetAPI;

import twitter4j.Twitter;

/**
 * Created by chenh on 2016/7/26.
 */
public class CenterController {

    private static CenterController centerController;
    private Handler mHandler;


    public static CenterController getCenterController(){
        return centerController;
    }

    public static void createCenterController(Context context, Handler handler){
        centerController=new CenterController();
        centerController.mHandler=handler;
        WBGetAPI.createInstance(context,handler);

    }


    public void setSinaModule(boolean sinaModule) {
        this.sinaModule = sinaModule;
    }

    public void setTwitterModule(boolean twitterModule) {
        this.twitterModule = twitterModule;
    }

    public void setFacebookModule(boolean facebookModule) {
        this.facebookModule = facebookModule;
    }

    public void setInstagramModule(boolean instagramModule) {
        this.instagramModule = instagramModule;
    }

    private boolean sinaModule;
    private boolean twitterModule;
    private boolean facebookModule;
    private boolean instagramModule;



    public static final int WEIBO_SLEEP=1;
    public static final int WEIBO_LOADING=2;
    public static final int WEIBO_LOADED=3;

    public static final int TWITTER_SLEEP=1;
    public static final int TWITTER_LOADING=2;
    public static final int TWITTER_LOADED=3;

    public static final int INSTAGRAM_SLEEP=1;
    public static final int INSTAGRAM_LOADING=2;
    public static final int INSTAGRAM_LOADED=3;

    public static final int FACEBOOK_SLEEP=1;
    public static final int FACEBOOK_LOADING=2;
    public static final int FACEBOOK_LOADED=3;


    private int weiboLoadState;
    private int twitterLoadState;
    private int instagramLoadState;
    private int facebookLoadState;

    public void getNext(){
        if (!canLoad())
            return;

        if (sinaModule)
            WBGetAPI.getWbGetAPI().getNextWB();
        if (twitterModule)
            TwitterGetAPI.getTwitterAPI().getNextTwitter();
        viewSync();
    }

    public void refresh(){
        if (!canLoad())
            return;
        if (sinaModule)
            WBGetAPI.getWbGetAPI().getNewWB();
        if (twitterModule)
            TwitterGetAPI.getTwitterAPI().getNewTwitter();
        viewSync();
    }

    private boolean canLoad(){
        if (weiboLoadState==WEIBO_LOADING||twitterLoadState==TWITTER_LOADING
                ||instagramLoadState==INSTAGRAM_LOADING||facebookLoadState==FACEBOOK_LOADING){
            mHandler.sendMessage(mHandler.obtainMessage(ContentActivity.REFRESH_ITEMS,""));
            return false;
        }
        if (sinaModule)
            weiboLoadState=WEIBO_LOADING;
        if (twitterModule)
            twitterLoadState=TWITTER_LOADING;
        if (instagramModule)
            instagramLoadState=INSTAGRAM_LOADING;
        if (facebookModule)
            facebookLoadState=FACEBOOK_LOADING;

        return true;
    }

    private void viewSync(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (weiboLoadState==2||twitterLoadState==2||instagramLoadState==2||facebookLoadState==2){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendMessage(mHandler.obtainMessage(ContentActivity.REFRESH_ITEMS,""));
                if (sinaModule)
                    weiboLoadState=1;
                if (twitterModule)
                    twitterLoadState=1;
                if (instagramModule)
                    instagramLoadState=1;
                if (facebookModule)
                    facebookLoadState=1;
            }
        }).start();
    }





    public void setWeiboLoadState(int weiboLoadState) {
        this.weiboLoadState = weiboLoadState;
    }

    public void setTwitterLoadState(int twitterLoadState) {
        this.twitterLoadState = twitterLoadState;
    }
}
