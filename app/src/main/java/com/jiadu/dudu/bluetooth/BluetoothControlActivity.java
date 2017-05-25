package com.jiadu.dudu.bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jiadu.dudu.R;
import com.jiadu.util.SharePreferenceUtil;
import com.jiadu.view.MyImageView;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/5/12.
 */
public class BluetoothControlActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MyImageView.PublishListener {

    private Button mBt_pidset;
    private Button mBt_paramset;
    private TextView mTv_linerspeed;
    private TextView mTv_anglespeed;
    private SeekBar mSb_linearspeed;
    private SeekBar mSb_anglespeed;
    private ImageView mIv_back;
    private MyImageView mMyiv_control;

    private static final String KEY_MAXLINEARSPEED = "maxlinearspeed";
    private static final String KEY_MAXANGLESPEED = "maxanglespeed";

    private float mMaxLinearSpeed = 0.2f;
    private float mMaxAngleSpeed = 12.f;

    private DecimalFormat mDecimalFormat=new DecimalFormat(".00");
    private DecimalFormat mDecimalFormat2=new DecimalFormat(".0");
    private MyReceive mReceiver;

    private BluetoothLeService mBluetoothLeService = null;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalService)service).getLocalService();

            mBluetoothLeService.setMaxLinearSpeed(mMaxLinearSpeed);
            mBluetoothLeService.setMaxAngleSpeed(mMaxAngleSpeed);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService =null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initView();

        initData();

        registerBroadCast();
    }

    private void registerBroadCast() {

        IntentFilter filter=new IntentFilter(BluetoothLeService.CONNECTIONSTATEBROADCAST);

        mReceiver = new MyReceive();

        registerReceiver(mReceiver,filter);
    }

    private void initData() {


        mBt_paramset.setOnClickListener(this);

        mBt_pidset.setOnClickListener(this);

        mIv_back.setOnClickListener(this);

        String maxspeed = SharePreferenceUtil.getSring(this, KEY_MAXLINEARSPEED);
        if (!TextUtils.isEmpty(maxspeed)){
            try {
                mMaxLinearSpeed = Float.parseFloat(maxspeed);
                mSb_linearspeed.setProgress((int) (1000*(mMaxLinearSpeed-0.1f)));
                mTv_linerspeed.setText("最大线速度 "+mMaxLinearSpeed+"m/s");

                if (mBluetoothLeService!=null){
                    mBluetoothLeService.setMaxLinearSpeed(mMaxLinearSpeed);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        String maxanglespeed = SharePreferenceUtil.getSring(this, KEY_MAXANGLESPEED);

        if (!TextUtils.isEmpty(maxanglespeed)){

            try {

                mMaxAngleSpeed = Float.parseFloat(maxanglespeed);

                float c = (mMaxAngleSpeed-9.0f)/6.0f*100;

                mSb_anglespeed.setProgress((int) c);

                mTv_anglespeed.setText("最大角速度 "+ mMaxAngleSpeed +"°/s");

                if (mBluetoothLeService!=null){
                    mBluetoothLeService.setMaxAngleSpeed(mMaxAngleSpeed);
                }

            }catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        mMyiv_control.setListener(this);

        mMyiv_control.setInterval(200);

        mIv_back.setOnClickListener(this);

        mSb_linearspeed.setOnSeekBarChangeListener(this);

        mSb_anglespeed.setOnSeekBarChangeListener(this);

        Intent intent = new Intent(this,BluetoothLeService.class);
        bindService(intent,mConn,Context.BIND_AUTO_CREATE);

    }

    private void initView() {
        mBt_paramset = (Button) findViewById(R.id.bt_control_paramset);

        mBt_pidset = (Button) findViewById(R.id.bt_control_PIDset);

        mTv_linerspeed = (TextView) findViewById(R.id.tv_control_linearspeed);

        mTv_anglespeed = (TextView) findViewById(R.id.tv_control_anglespeed);

        mSb_linearspeed = (SeekBar) findViewById(R.id.sb_control_linearspeed);

        mSb_anglespeed = (SeekBar) findViewById(R.id.sb_control_anglespeed);

        mIv_back = (ImageView) findViewById(R.id.iv_control_back);

        mMyiv_control = (MyImageView) findViewById(R.id.myiv_control_control);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_control_back:
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()){
            case R.id.sb_control_linearspeed:
                mMaxLinearSpeed = 0.1f+progress*0.1f/100;

                mTv_linerspeed.setText("最大线速度 "+Float.parseFloat(mDecimalFormat.format(mMaxLinearSpeed))+"m/s");

                break;
            case R.id.sb_control_anglespeed:
                mMaxAngleSpeed = 9 + progress*6.0f/100;

                mTv_anglespeed.setText("最大角速度 "+ mDecimalFormat2.format(mMaxAngleSpeed)+"°/s");
                break;

            default:
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_control_linearspeed:{

                int progress = seekBar.getProgress();

                mMaxLinearSpeed = 0.1f + progress * 0.1f / 100;

                mTv_linerspeed.setText("最大线速度 " + Float.parseFloat(mDecimalFormat.format(mMaxLinearSpeed)) + "m/s");

                SharePreferenceUtil.putString(this, KEY_MAXLINEARSPEED, mDecimalFormat.format(mMaxLinearSpeed));

                mBluetoothLeService.setMaxLinearSpeed(mMaxLinearSpeed);

//                dashgoPublisher.setMaxLinearSpeed(mMaxLinearSpeed);

            }
            break;
            case R.id.sb_control_anglespeed:{

                int progress = seekBar.getProgress();

                mMaxAngleSpeed = 9.0f+progress*6.0f/100;

                mTv_anglespeed.setText("最大角速度 "+Float.parseFloat(mDecimalFormat2.format(mMaxAngleSpeed))+"°/s");

                SharePreferenceUtil.putString(this,KEY_MAXANGLESPEED,mDecimalFormat2.format(mMaxAngleSpeed));

                mBluetoothLeService.setMaxAngleSpeed(mMaxAngleSpeed);
//                dashgoPublisher.setMaxAngularSpeed(mMaxAngleSpeed/20.f);

            }
            break;
            default:
                break;
        }
    }

    @Override
    public void publishToRos() {

        mBluetoothLeService.send(mMyiv_control.getLastDirection(),mMyiv_control.getSpeedRatio());

    }

    @Override
    public void stopPublish() {
        mBluetoothLeService.send(1,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBluetoothLeService.close();
    }

    private class MyReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connectstate = intent.getBooleanExtra("CONNECTSTATE",false);
            if (!connectstate){
                finish();
            }
        }
    }
}
