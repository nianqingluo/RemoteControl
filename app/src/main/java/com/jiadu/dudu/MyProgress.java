package com.jiadu.dudu;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/4/13.
 */
public class MyProgress extends ProgressDialog {
    public MyProgress(Context context) {
        super(context);
    }

    public MyProgress(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


    }
}
