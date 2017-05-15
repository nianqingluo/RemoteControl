package com.jiadu.dudu;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/5/12.
 */
public class MyApplication extends Application {
    public static Context getContext() {
        return mContext;
    }
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext= getApplicationContext();
    }
}
