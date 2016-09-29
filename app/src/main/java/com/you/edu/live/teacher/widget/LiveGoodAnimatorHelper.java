package com.you.edu.live.teacher.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 直播时点赞动画助手
 * 作者：XingRongJing on 2016/7/4.
 */
public class LiveGoodAnimatorHelper {
    private AnimatorSet mAnimatorSet;

    public LiveGoodAnimatorHelper(View target) {
        mAnimatorSet = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 2.0f, 1.0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 2.0f, 1.0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(target, "alpha", 0.4f, 0.8f, 1.0f);
        mAnimatorSet.playTogether(animator1, animator2, animator3);
    }

    public void setDuration(long duration) {
        if (null != mAnimatorSet) {
            mAnimatorSet.setDuration(duration);
        }
    }

    public boolean isStarted() {
        return null == mAnimatorSet ? false : mAnimatorSet.isStarted();
    }

    public boolean isRunning() {
        return null == mAnimatorSet ? false : mAnimatorSet.isRunning();
    }

    public void start() {
        if (this.isStarted()) {
            return;
        }
        mAnimatorSet.start();
    }


    public void cancel() {
        if (null != mAnimatorSet) {
            mAnimatorSet.cancel();
        }
    }
}
