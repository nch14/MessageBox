package com.chenh.messagebox.util;

import android.content.Context;
import android.os.Handler;

import com.chenh.messagebox.sina.WBGetAPI;
import com.chenh.messagebox.twiiter.TwitterGetAPI;

import twitter4j.Twitter;

/**
 * Created by chenh on 2016/7/26.
 */
public class CenterController {

    private static CenterController centerController;


    public static CenterController getCenterController(){
        return centerController;
    }

    public static void createCenterController(Context context, Handler mHandler){
        WBGetAPI.createInstance(context,mHandler);
    }



    public void getNext(){

    }

    public void refresh(){

    }












}
