package com.chenh.messagebox.twiiter;

import java.io.File;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 * Created by chenh on 2016/7/26.
 */
public class TwitterSendAPI {


    public static void send(final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Status status = LocalTwitterTool.getTwitter().updateStatus(text);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void send(){}
    /**
     * Usage: java twitter4j.examples.tweets.UploadMultipleImages [text] [file1] [file2] ...
     *
     * @param args message
     */
    public static void send(final String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (args.length < 1) {
                    System.out.println("Usage: java twitter4j.examples.tweets.UploadMultipleImages [text] [file1] [file2] ...");
                    //System.exit(-1);
                }
                try {
                    Twitter twitter =LocalTwitterTool.getTwitter();

                    long[] mediaIds = new long[args.length-1];
                    for (int i=1; i<args.length; i++) {
                        System.out.println("Uploading...[" + i + "/" + (args.length-1) + "][" + args[i] + "]");
                        UploadedMedia media = twitter.uploadMedia(new File(args[i]));
                        System.out.println("Uploaded: id=" + media.getMediaId()
                                + ", w=" + media.getImageWidth() + ", h=" + media.getImageHeight()
                                + ", type=" + media.getImageType() + ", size=" + media.getSize());
                        mediaIds[i-1] = media.getMediaId();
                    }
                    StatusUpdate update = new StatusUpdate(args[0]);
                    update.setMediaIds(mediaIds);
                    Status status = twitter.updateStatus(update);
                } catch (TwitterException te) {
                    te.printStackTrace();
                }
            }
        }).start();

    }
}
