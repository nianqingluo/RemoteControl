package com.jiadu.dudu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/4/11.
 */
public class ParamSetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt_back;
    private Button mBt_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paramset);

        initView();

        initData();
    }

    private void initData() {

        mBt_back.setOnClickListener(this);
        mBt_confirm.setOnClickListener(this);
    }

    private void initView() {

        mBt_back = (Button) findViewById(R.id.bt_paramset_back);

        mBt_confirm = (Button) findViewById(R.id.bt_paramset_confirm);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_paramset_back:
                finish();
            break;
            case R.id.bt_paramset_confirm:
                finish();
            break;

            default:
            break;
        }
    }
}
