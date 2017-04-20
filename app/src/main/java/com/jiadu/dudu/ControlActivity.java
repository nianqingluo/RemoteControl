package com.jiadu.dudu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eaibot.library.constants.BroadcastConstant;
import com.eaibot.library.ros.DashgoPublisher;
import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;
import com.jiadu.util.LogUtil;
import com.jiadu.util.SharePreferenceUtil;
import com.jiadu.view.MyImageView;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/4/10.
 */
public class ControlActivity extends RosAppActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String KEY_MAXLINEARSPEED = "maxlinearspeed";
    public static final String KEY_MAXANGLESPEED = "maxanglespeed";
    private Button mBt_paramset;
    private Button mBt_pidset;
    private TextView mTv_linerspeed;
    private TextView mTv_anglespeed;
    private SeekBar mSb_linearspeed;
    private SeekBar mSb_anglespeed;
    private Button mBt_back;

    private float mMaxLinearSpeed = 0.2f;
    private float mMaxAngleSpeed = 12.f;

    private Mediator mMediator = null;

    private DecimalFormat mDecimalFormat=new DecimalFormat(".00");
    private DecimalFormat mDecimalFormat2=new DecimalFormat(".0");

    private BroadcastReceiver rosServiceBroadcastRceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastConstant.ROS_CLOSE_FINISHED)){
                if(intent.getBooleanExtra("rosFinished" , false)){
                    finish();
                }
            }
        }
    };

    private NodeConfiguration nodeConfiguration;

    private DashgoPublisher dashgoPublisher;

    public DashgoPublisher getDashgoPublisher() {
        return dashgoPublisher;
    }

    private MyImageView mMyiv_control;

    public ControlActivity() {
        super("Dashgo Control", "Dashgo Control");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setDefaultMasterName("eaibot");
        setDefaultAppName("dashgo");
        setMainWindowResource(R.layout.activity_control);
        super.onCreate(savedInstanceState);

        initView();

        initData();

    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        super.init(nodeMainExecutor);

        String name = Thread.currentThread().getName();

        LogUtil.debugLog("localName:"+name);

        nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
        dashgoPublisher = new DashgoPublisher();
        nodeMainExecutor.execute(dashgoPublisher, nodeConfiguration.setNodeName("eaibot/dashgo_demo"));

        dashgoPublisher.setMaxLinearSpeed(mMaxLinearSpeed);

        dashgoPublisher.setMaxAngularSpeed(mMaxAngleSpeed/20.0f);

        mMediator = new Mediator(this,mMyiv_control);
    }

    private void initData() {

        mBt_paramset.setOnClickListener(this);

        mBt_pidset.setOnClickListener(this);

        mBt_back.setOnClickListener(this);

        String maxspeed = SharePreferenceUtil.getSring(this, KEY_MAXLINEARSPEED);
        if (!TextUtils.isEmpty(maxspeed)){
            try {
                mMaxLinearSpeed = Float.parseFloat(maxspeed);
                mSb_linearspeed.setProgress((int) (1000*(mMaxLinearSpeed-0.1f)));
                mTv_linerspeed.setText("最大线速度 "+mMaxLinearSpeed+"m/s");
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

            }catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        mSb_linearspeed.setOnSeekBarChangeListener(this);

        mSb_anglespeed.setOnSeekBarChangeListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstant.ROS_CLOSE_FINISHED);
        intentFilter.addAction(BroadcastConstant.ROS_INIT_START);
        registerReceiver(rosServiceBroadcastRceiver,intentFilter);

    }

    private void initView() {

        mBt_paramset = (Button) findViewById(R.id.bt_control_paramset);

        mBt_pidset = (Button) findViewById(R.id.bt_control_PIDset);

        mTv_linerspeed = (TextView) findViewById(R.id.tv_control_linearspeed);

        mTv_anglespeed = (TextView) findViewById(R.id.tv_control_anglespeed);

        mSb_linearspeed = (SeekBar) findViewById(R.id.sb_control_linearspeed);

        mSb_anglespeed = (SeekBar) findViewById(R.id.sb_control_anglespeed);

        mBt_back = (Button) findViewById(R.id.bt_control_back);

        mMyiv_control = (MyImageView) findViewById(R.id.myiv_control_control);

    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()){
            case R.id.bt_control_paramset: {

                Intent intent = new Intent(this, ParamSetActivity.class);

                startActivity(intent);
            }
            break;
            case R.id.bt_control_PIDset: {

                Intent intent = new Intent(this, PIDSetActivity.class);

                startActivity(intent);
            }
            break;
            case R.id.bt_control_back: {

                finish();
            }
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

                dashgoPublisher.setMaxLinearSpeed(mMaxLinearSpeed);

            }
                break;
            case R.id.sb_control_anglespeed:{

                int progress = seekBar.getProgress();

                mMaxAngleSpeed = 9.0f+progress*6.0f/100;

                mTv_anglespeed.setText("最大角速度 "+Float.parseFloat(mDecimalFormat2.format(mMaxAngleSpeed))+"°/s");

                SharePreferenceUtil.putString(this,KEY_MAXANGLESPEED,mDecimalFormat2.format(mMaxAngleSpeed));

                dashgoPublisher.setMaxAngularSpeed(mMaxAngleSpeed/20.f);

            }
            break;
            default:
            break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        unregisterReceiver(rosServiceBroadcastRceiver);
    }
}
