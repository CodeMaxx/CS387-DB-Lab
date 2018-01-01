package me.harshithgoka.socmed.Misc;

import android.app.Service;
import android.graphics.Typeface;
import android.os.Handler;

import java.lang.reflect.Type;


/**
 * Created by harshithgoka on 03/10/17.
 */

public final class Constants {
    public static String URL = "http://10.0.2.2:8080/twitter-backend/";

    public static int MAX_LIMIT = 10;

    public static String Name = "";
    public static String MISCSTATE = "MiscState";
    public static String LOGINSTATE = "LoginState";
    public static String USERSTATE = "UserState";
    public static String NAME = "Name";

    public static String WHAT = "what";
    public static String INTENT_DATA = "IntentData";
    public static String POST_ID = "PostId";
    public static String COMMENT_TEXT = "CommentText";
    public static String SRC_FRAGMENT = "SrcFragment";
    public static String POST_POS = "PostPos";
    public static String USER_DATA = "UserData";
    public static String POSTS = "Posts";
    public static String POST_TEXT = "PostText";
    public static String POST_IMG = "PostImg";
    public static String OFFSET = "Offset";
    public static String LIMIT = "Limit";


    public static int ACK = 0;
    public static int GET_NETWORK_STATE = 1;
    public static int GET_FEED = 2;
    public static int GET_MY_POSTS = 3;
    public static int WRITE_POST = 4;
    public static int WRITE_COMMENT = 5;
    public static int GET_USER_POSTS = 6;
    public static int FOLLOW = 7;
    public static int ALREADY_FOLLOWED = 8;
    public static int UNFOLLOW = 9;
    public static int NOT_FOLLOWED = 10;

    public static int TRUE = 93;
    public static int FALSE = 39;

    public enum NETWORK_STATE {
        LOGGED_IN, NOT_LOGGED_IN, NOT_CONNECTED

    }

    public enum POSTS_TYPE {
        FEED, MY_POSTS, USER_POSTS
    }

    public static Handler currHandler = null;

}
