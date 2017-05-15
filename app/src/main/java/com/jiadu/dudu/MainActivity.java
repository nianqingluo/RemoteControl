package com.jiadu.dudu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jiadu.dudu.bluetooth.BluetoothConnectActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBt_wifi;
    private Button mBt_bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        
        initData();
    }

    private void initData() {
        mBt_wifi.setOnClickListener(this);
        mBt_bluetooth.setOnClickListener(this);
    }

    private void initView() {
        mBt_wifi = (Button) findViewById(R.id.bt_wifi);

        mBt_bluetooth = (Button) findViewById(R.id.bt_bluetooth);
        
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_wifi:{
                Intent initent = new Intent(this,WifiConnectActivity.class);
                startActivity(initent);

            }
            break;
            case R.id.bt_bluetooth:{
                Intent initent = new Intent(this,BluetoothConnectActivity.class);
                startActivity(initent);

            }
            break;

            default:
            break;
        }

    }
}

