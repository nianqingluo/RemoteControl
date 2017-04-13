package com.jiadu.dudu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/4/11.
 */
public class PIDSetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt_back;
    private Button mBt_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pidset);

        initView();

        initData();
    }



    private void initData() {

        mBt_back.setOnClickListener(this);
        mBt_confirm.setOnClickListener(this);


    }

    private void initView() {
        mBt_back = (Button) findViewById(R.id.bt_pidset_back);

        mBt_confirm = (Button) findViewById(R.id.bt_pidset_confirm);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_pidset_back:
                finish();

            break;
            case R.id.bt_pidset_confirm:

                finish();
            break;


            default:
            break;
        }


    }
}
