package com.jiadu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.jiadu.dudu.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/11.
 */
public class MyImageView extends ImageView {

    private static final float INNERCIRCLE = 0.806f;
    private Bitmap mCenterCircle;
    private int mHeight;
    private int mWidth;
    private int mRealSize;
    private Paint mPaint = new Paint();

    private float mX=0;
    private float mY=0;
    private int mCenterCircleHeight;
    private int mCenterCirclewidth;

    private int mLightUpCount = 0;

    public int getLastDirection() {
        return mLastDirection;
    }

    private int mLastDirection = 0;
    private float mRatio = 1f;
    private Bitmap mPanel;
    private Bitmap mLightUp;
    private int mLightUpWidth;
    private int mLightUpHeight;

    private Timer mTimer = null;
    private TimerTask mTimerTask= null;

    public float getSpeedRatio() {
        return mSpeedRatio;
    }

    private float mSpeedRatio;

    private final int INTERVAL = 300; //publish的时间间隔 单位：毫秒

    private int mInterval = INTERVAL;

    private PublishListener mListener = null;



    public void setListener(PublishListener listener) {
        this.mListener = listener;
    }

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //加载中心圆bitmap
        mCenterCircle = BitmapFactory.decodeResource(getResources(), R.mipmap.yuan);
        mCenterCircleHeight = mCenterCircle.getHeight();
        mCenterCirclewidth = mCenterCircle.getWidth();
        //加载控制板的bitmap
        mPanel = BitmapFactory.decodeResource(getResources(), R.mipmap.fangxiang01);

        //加载灯的bitmap
        mLightUp = BitmapFactory.decodeResource(getResources(), R.mipmap.liang);

        mLightUpWidth = mLightUp.getWidth();
        mLightUpHeight = mLightUp.getHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRealSize==0){
            mHeight = getHeight();
            mWidth = getWidth();
            mRealSize =  mHeight < mWidth ? mHeight : mWidth;

            int height = mPanel.getHeight();
            int width = mPanel.getWidth();

            if (mRealSize != height){
                mRatio = mRealSize *1.0f /height;
            }

            if (mRatio!=1){
                //重新制作centerCircle的bitmap
                Bitmap bitmap = Bitmap.createBitmap((int)(mCenterCirclewidth*mRatio),(int)(mCenterCircleHeight*mRatio),mCenterCircle.getConfig());
                Canvas canvas1 = new Canvas(bitmap);
                Matrix matrix = new Matrix();
                matrix.setScale(mRatio,mRatio);
                canvas1.drawBitmap(mCenterCircle,matrix,mPaint);
                mCenterCircle = bitmap;
                mCenterCirclewidth = bitmap.getWidth();
                mCenterCircleHeight = bitmap.getHeight();

                //重新制作lightup的bitmap
                Bitmap bitmap2 = Bitmap.createBitmap((int)(mLightUpWidth*mRatio),(int)(mLightUpHeight*mRatio),mLightUp.getConfig());

                Canvas canvas2 = new Canvas(bitmap2);

                Matrix matrix2 = new Matrix();

                matrix.setScale(mRatio,mRatio);

                canvas2.drawBitmap(mLightUp,matrix2,mPaint);

                mLightUp = bitmap2;
                mLightUpWidth = bitmap2.getWidth();
                mLightUpHeight = bitmap2.getHeight();

            }

            reInitXY();
        }

        canvas.drawBitmap(mCenterCircle,mX-mCenterCirclewidth/2,mY-mCenterCircleHeight/2, mPaint);
        Matrix matrix = new Matrix();
        matrix.setTranslate((mWidth-mLightUpWidth)/2.0f,(mHeight-mLightUpHeight)/2.0f);
        matrix.postTranslate(-(mRealSize*1.0f*594/664/2),0);
        for (int i = 0; i < mLightUpCount; i++) {
            matrix.postRotate(5,mWidth/2.f,mHeight/2.f);

            canvas.drawBitmap(mLightUp,matrix,mPaint);
        }

        System.out.println("realSize:"+mRealSize);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mX = event.getX();
        mY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{

                SRCAndCenterPoint();

                mTimer = new Timer();

                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {

                        if (mListener != null){

                            mListener.publishToRos();
                        }
                    }
                };

                mTimer.schedule(mTimerTask,0, mInterval);
            }
                break;
            case MotionEvent.ACTION_MOVE:{

                SRCAndCenterPoint();

            }
            break;
            case MotionEvent.ACTION_UP:{
                mTimer.cancel();
                if (mListener != null) {
                    mListener.stopPublish();
                }
                mSpeedRatio=0.f;
                setImageResource(R.mipmap.fangxiang01);
                mLastDirection=0;
                mLightUpCount = 0;
                reInitXY();
            }
            break;
            case MotionEvent.ACTION_CANCEL:{
                mTimer.cancel();
                if (mListener != null) {

                    mListener.stopPublish();
                }
                mSpeedRatio=0.f;
                setImageResource(R.mipmap.fangxiang01);
                mLastDirection=0;
                mLightUpCount = 0;
                reInitXY();
            }
            break;

            default:
            break;
        }

        invalidate();

        return true;
    }

    private void SRCAndCenterPoint() {
        float v = mX - mWidth / 2;
        float v1 = -mY + mHeight / 2;
        int i = direction(v,v1);

        if (i==1 || i==3){
            mX = mWidth/2;

            if(mY>(mRealSize*INNERCIRCLE/2+mHeight/2)){
                mY=mRealSize*INNERCIRCLE/2+mHeight/2;
            }
            else if (mY<(mHeight/2-mRealSize*INNERCIRCLE/2)){
                mY = mHeight/2-mRealSize*INNERCIRCLE/2;
            }

            mSpeedRatio = Math.abs(mY-mHeight/2.0f)/(mRealSize*INNERCIRCLE/2.0f);

            mLightUpCount = (int) (mSpeedRatio*72);

        }
        else if (i == 2 || i == 4){
            mY = mHeight/2;

            if (mX>mRealSize*INNERCIRCLE/2+mWidth/2.0f){
                mX=mRealSize*INNERCIRCLE/2+mWidth/2.0f;
            }
            else if (mX<mWidth/2.0f-mRealSize*INNERCIRCLE/2){
                mX=mWidth/2.0f-mRealSize*INNERCIRCLE/2;
            }

            mSpeedRatio = Math.abs(mX - mWidth/2.0f)/(mRealSize*INNERCIRCLE/2.0f);

            mLightUpCount = (int) (mSpeedRatio*72);
        }

        if (mLastDirection != i){
            changeSRC(i);
        }
        mLastDirection = i;
    }

    private void changeSRC(int i) {

        switch (i){
            case 1:
                setImageResource(R.mipmap.fangxiang02);
            break;
            case 2:
                setImageResource(R.mipmap.fangxiang04);
            break;
            case 3:
                setImageResource(R.mipmap.fangxiang05);
            break;
            case 4:
                setImageResource(R.mipmap.fangxiang03);
            break;

            default:
            break;
        }
    }


    /**
     * 让XY回到中心点
     */
    private void reInitXY(){
        mX = mWidth/2;
        mY = mHeight/2;
    }

    private int direction(float v, float v1) {

        if (v1>0 && Math.abs(v) <= Math.abs(v1)){

            return 1;
        }
        else if (v1<0 && Math.abs(v) <= Math.abs(v1)){

            return 3;
        }
        else if (v>0 && Math.abs(v) >= Math.abs(v1)){
            return 2;
        }
        else if (v<0 && Math.abs(v) >= Math.abs(v1)){

            return 4;
        }

        return 0;
    }

    /**
     * @param interval 每次发送消息的时间间隔
     */
    public void setInterval(int interval) {
        mInterval = interval;
    }

    public interface PublishListener{

        void publishToRos();
        void stopPublish();

    }

}
