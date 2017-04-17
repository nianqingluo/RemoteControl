package com.jiadu.dudu;

import com.eaibot.library.ros.DashgoPublisher;
import com.jiadu.util.LogUtil;
import com.jiadu.view.MyImageView;

/**
 * MyImageView 和 ContorlActivity的中介类
 * Created by Administrator on 2017/4/13.
 */
public class Mediator {


    private final ControlActivity mActivity;
    private final MyImageView mMiv;

    private final DashgoPublisher mDashgoPublisher;

    public Mediator(ControlActivity activity, MyImageView miv) {
        mActivity = activity;

        mDashgoPublisher = mActivity.getDashgoPublisher();

        mMiv = miv;

        mMiv.setListener(new MyPublishListener());
    }

    private class MyPublishListener implements MyImageView.PublishListener{
        @Override
        public void publishToRos() {

            try {
                switch (mMiv.getLastDirection()){
                    case 1://前进

                        mDashgoPublisher.publishVelocity(mMiv.getSpeedRatio(),0,0);

                    break;
                    case 2://右转

                        mDashgoPublisher.publishVelocity(0,0,mMiv.getSpeedRatio());

                    break;
                    case 3://后退
                        mDashgoPublisher.publishVelocity(-mMiv.getSpeedRatio(),0,0);

                    break;
                    case 4://左转
                        mDashgoPublisher.publishVelocity(0,0,-mMiv.getSpeedRatio());

                    break;

                    default:
                    break;
                }
            } catch (Exception e) {

                LogUtil.debugLog("fuck：publishToRos--"+e.getMessage());
            }
        }

        @Override
        public void stopPublish() {

            try {
                mDashgoPublisher.publishVelocity(0,0,0);
            } catch (Exception e) {
                LogUtil.debugLog("fuck：stopPublish--"+e.getMessage());
            }
        }
    }
}

