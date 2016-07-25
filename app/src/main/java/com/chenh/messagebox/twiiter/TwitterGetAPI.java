package com.chenh.messagebox.twiiter;

import android.os.Handler;

import com.chenh.messagebox.LocalItem;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by chenh on 2016/7/25.
 */
public class TwitterGetAPI {

    private int currentPage=1;

    private static TwitterGetAPI getTwitterAPI;

    public static void createInstance(Handler mHandler){

    }

    public static TwitterGetAPI getTwitterAPI(){
        if (getTwitterAPI==null){
            getTwitterAPI=new TwitterGetAPI();
        }
        return getTwitterAPI;
    }



    public void getNewTwitter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Status> statuses = null;
                Paging paging = new Paging(1, 10);
                try {
                    statuses = LocalTwitterTool.getTwitter().getHomeTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                System.out.println("Showing home timeline.");
                for (Status status : statuses) {
                    System.out.println(status.getUser().getName() + ":" +
                            status.getText());
                }
                LocalItem.getLocalItem().addTwitterItems((ArrayList<Status>) statuses);
            }
        }).start();

    }

    public void getNextTwitter(){
        currentPage++;
        List<Status> statuses = null;
        Paging paging = new Paging(currentPage, 10);
        try {
            statuses = LocalTwitterTool.getTwitter().getHomeTimeline(paging);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Showing home timeline.");
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }
    }
}
