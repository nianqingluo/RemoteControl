package com.jiadu.util;

import android.util.Log;

/**
 * Created by Administrator on 2017/2/13.
 */
public class LogUtil {

    private static boolean isDebug = true;
    private static String TAG = "dudurobot";

    public static void debugLog(String content){
        if(isDebug){

            Log.d(TAG,content);
        }
    }

}

