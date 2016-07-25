package com.chenh.messagebox.twiiter;

import java.util.IllegalFormatCodePointException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

/**
 * Created by chenh on 2016/7/25.
 */
public class LocalTwitterTool {

    private static Twitter mTwitter;

    private static RequestToken requestToken;

    public static Twitter getTwitter(){
        if (mTwitter==null){
            mTwitter = TwitterFactory.getSingleton();
            mTwitter.setOAuthConsumer("H3R0gZH1d8twPDznmiSh9Hihm","4Q9v5h2WVflWMEmmzTzAyLiWbNpt2t57FyXk1JEPLpmT5JdCZ8");
        }
        return mTwitter;
    }

    public static RequestToken getRequestToken() throws TwitterException {
        if (requestToken==null){
            requestToken=mTwitter.getOAuthRequestToken();
        }
        return requestToken;
    }



}
