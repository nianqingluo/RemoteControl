package com.jiadu.dudu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.eaibot.library.listener.OnTaskListener;
import com.eaibot.library.ros.RosClient;
import com.jiadu.util.SharePreferenceUtil;

/**
 * Created by Administrator on 2017/4/10.
 */
public class WifiConnectActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIv_back;
    private Button mBt_delete;
    private Button mBt_cancel;
    private Button mBt_confirm;
    private EditText mEt_ip;
    private final String KEY_IP = "key_ip";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wificonnect);
        
        initView();
        
        initData();
    }

    private void initData() {
        mIv_back.setOnClickListener(this);

        mBt_delete.setOnClickListener(this);

        mBt_cancel.setOnClickListener(this);

        mBt_confirm.setOnClickListener(this);


        String ip = SharePreferenceUtil.getSring(this, KEY_IP);

        if (!TextUtils.isEmpty(ip)){
            mEt_ip.setText(ip);

            mEt_ip.setSelection(ip.length());
        }else {
            mEt_ip.setSelection(mEt_ip.getText().toString().length());
        }

    }

    private void initView() {
        mIv_back = (ImageView) findViewById(R.id.iv_wificonnect_back);

        mBt_delete = (Button) findViewById(R.id.bt_wificonnect_delete);

        mBt_cancel = (Button) findViewById(R.id.bt_wificonnect_cancel);

        mBt_confirm = (Button) findViewById(R.id.bt_wificonnect_confirm);

        mEt_ip = (EditText) findViewById(R.id.et_wificonnect_ip);

    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()){
            case R.id.iv_wificonnect_back:

                finish();

            break;
            case R.id.bt_wificonnect_delete:

                mEt_ip.setText("");

                break;
            case R.id.bt_wificonnect_cancel:

                finish();

            break;
            case R.id.bt_wificonnect_confirm:

//                Intent intent = new Intent(WifiConnectActivity.this, ControlActivity.class);
//                startActivity(intent);
//                finish();
                showDialog(1);

                mEt_ip.setEnabled(false);
                mBt_cancel.setEnabled(false);
                mBt_confirm.setEnabled(false);

                SharePreferenceUtil.putString(this,KEY_IP,mEt_ip.getText().toString());

                new RosClient(this, mEt_ip.getText().toString().trim(), new OnTaskListener() {
                    @Override
                    public void onSuccess() {
                        dismissDialog(1);

                        Intent intent = new Intent(WifiConnectActivity.this, ControlActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(WifiConnectActivity.this,"连接ros失败",Toast.LENGTH_SHORT).show();
                        dismissDialog(1);

                        mEt_ip.setEnabled(true);
                        mBt_cancel.setEnabled(true);
                        mBt_confirm.setEnabled(true);
                    }
                });
            break;
            default:
            break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case 1:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("莫慌...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            default:
                break;
        }

        return null;
    }
}
