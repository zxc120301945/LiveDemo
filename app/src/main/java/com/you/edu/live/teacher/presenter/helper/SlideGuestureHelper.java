package com.you.edu.live.teacher.presenter.helper;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.you.edu.live.teacher.utils.DeviceUtils;

/**
 * 滑动手势助手
 * 作者：XingRongJing on 2016/7/4.
 */
public class SlideGuestureHelper {


    private static final double RADIUS_SLOP = Math.PI * 1 / 4;
    /**
     * 控制所有手势是否可用
     **/
    private boolean mGestureEnabled = false;
    private ISlideGuestureListener mSlideListener;
    /**
     * 触摸宽高--默认是屏幕宽高
     **/
    private int mTouchViewWidth = 0, mTouchViewHeight = 0;

    private GestureDetectorCompat mSlideGestureDetector;

    public SlideGuestureHelper(Context mContext) {
        this(mContext, null);
    }

    public SlideGuestureHelper(Context mContext, ISlideGuestureListener listener) {
        this(mContext, listener, false);
    }

    public SlideGuestureHelper(Context mContext, ISlideGuestureListener listener, boolean isGestureEnable) {
        if (null == mContext) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.mSlideListener = listener;
        this.setGestureEnabled(isGestureEnable);
        mSlideGestureDetector = new GestureDetectorCompat(mContext, new SlideGuestureListener());
        mTouchViewWidth = DeviceUtils.getScreenWidth(mContext);
        mTouchViewHeight = DeviceUtils.getScreenHeight(mContext);
    }

    public boolean isGestureEnabled() {
        return mGestureEnabled;
    }

    public void setGestureEnabled(boolean mGestureEnabled) {
        this.mGestureEnabled = mGestureEnabled;
    }

    public ISlideGuestureListener getSlideListener() {
        return mSlideListener;
    }

    public void setSlideListener(ISlideGuestureListener mSlideListener) {
        this.mSlideListener = mSlideListener;
    }

    public void setTouchViewWidth(int width) {
        this.mTouchViewWidth = width;
    }

    public void setmTouchViewHeight(int height) {
        this.mTouchViewHeight = height;
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (null == mSlideGestureDetector || !this.isGestureEnabled()) {
            return false;
        }
        if (mSlideGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return false;
    }

    private class SlideGuestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (null == mSlideListener || !isGestureEnabled() || null == e1
                    || null == e2) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            float disX = e1.getX() - e2.getX();
            float disY = e1.getY() - e2.getY();
            final double distance = Math.sqrt(Math.pow(disX, 2)
                    + Math.pow(disY, 2));
            final double radius = disY / distance;
            if (Math.abs(radius) > RADIUS_SLOP) {// 竖向滑动
                if (disY > 0) {
                    getSlideListener().onSlideUp();
                } else {
                    getSlideListener().onSlideDown();
                }
            } else {//横向滑动

            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public interface ISlideGuestureListener {
        /**
         * 向上滑动
         **/
        void onSlideUp();

        /**
         * 向下滑动
         **/
        void onSlideDown();
    }
}
