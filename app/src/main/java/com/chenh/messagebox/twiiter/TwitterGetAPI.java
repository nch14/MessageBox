package com.chenh.messagebox.twiiter;

import android.os.Handler;

import com.chenh.messagebox.LocalItem;
import com.chenh.messagebox.util.CenterController;

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

    private long sinceId=0;
    private long maxId=0;

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
                if (maxId!=0)
                    paging.setSinceId(maxId+1);
                try {
                    statuses = LocalTwitterTool.getTwitter().getHomeTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                addItems(statuses);
            }
        }).start();

    }

    public void getNextTwitter(){
        //currentPage++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Status> statuses = null;
                Paging paging = new Paging(1, 10);
                if (sinceId!=0)
                    paging.setMaxId(sinceId-1);
                try {
                    statuses = LocalTwitterTool.getTwitter().getHomeTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                addItems(statuses);
            }
        }).start();

    }

    private void addItems(List<Status> statuses){
        if (statuses==null){
            LocalItem.getLocalItem().addTwitterItems((ArrayList<Status>) statuses);
            CenterController.getCenterController().setTwitterLoadState(CenterController.TWITTER_LOADED);
            return;
        }

        if (statuses.size()==0){
            LocalItem.getLocalItem().addTwitterItems((ArrayList<Status>) statuses);
            CenterController.getCenterController().setTwitterLoadState(CenterController.TWITTER_LOADED);
            return;
        }
        if(statuses.get(0).getId()>maxId||maxId==0){
            maxId=statuses.get(0).getId();
        }
        if (statuses.get(statuses.size()-1).getId()<sinceId||sinceId==0){
            sinceId=statuses.get(statuses.size()-1).getId();
        }

        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }

        while (LocalItem.getLocalItem().updating){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LocalItem.getLocalItem().addTwitterItems((ArrayList<Status>) statuses);
        CenterController.getCenterController().setTwitterLoadState(CenterController.TWITTER_LOADED);
    }
}
