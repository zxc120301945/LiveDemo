package com.you.edu.live.teacher.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.you.edu.live.teacher.R;


/**
 * @author XingRongJing
 *         // * @see view_loading.xml
 *         // * @see ProgressDialogHelper
 */
public class LoadingHelper {

    private View mProgress;
    private View mRetry;
    private ProgressBar mProBar;
    private TextView mTvProgress, mTvErrorFirst, mTvErrorSecond;
    private ImageView mIvProgress;
    /**
     * 是否是gif（false则为ProgressBar）
     **/
    private boolean mIsGif = true;
    private Context mCtx;
    private AnimationDrawable mAnimDrawable;

    public LoadingHelper(Context ctx, View progress) {
        this(ctx, progress, null, null, null, true);
    }

    public LoadingHelper(Context ctx, View progress, boolean mIsGif) {
        this(ctx, progress, null, null, null, mIsGif);
    }

    public LoadingHelper(Context ctx, View progress, TextView progressTips,
                         ImageView ivProgress, ProgressBar probar, boolean mIsGif) {
        if (null == progress) {
            throw new IllegalArgumentException("Progress's view is null");
        }
        mCtx = ctx;
        mProgress = progress;
        mTvProgress = progressTips;
        mProBar = probar;
        mIvProgress = ivProgress;
        this.mIsGif = mIsGif;
        this.prepareViews(ctx);
    }

    private void prepareViews(Context ctx) {
        if (null == mTvProgress) {
            mTvProgress = (TextView) mProgress
                    .findViewById(R.id.loading_tv_text);
        }
        if (null == mRetry) {
            mRetry = (View) mProgress.findViewById(R.id.loading_ll_no_network);
        }
        if (null == mIvProgress) {
            mIvProgress = (ImageView) mProgress
                    .findViewById(R.id.loading_iv_pro);
        }
        if (null == mTvErrorFirst) {
            mTvErrorFirst = (TextView) mProgress
                    .findViewById(R.id.loading_tv_retry_tips);
        }
        if (null == mTvErrorSecond) {
            mTvErrorSecond = (TextView) mProgress
                    .findViewById(R.id.loading_tv_retry_down);
        }
        if (null == mProBar) {
            mProBar = (ProgressBar) mProgress.findViewById(R.id.loading_probar);
        }

        this.handleViewsByLoadingStyle(mIsGif);
    }

    private void handleViewsByLoadingStyle(boolean isGif) {
        // 设置采取哪种加载方式（ProgressBar或gif）
        if (isGif) {
            if (null != mProBar) {
                mProBar.setVisibility(View.GONE);
            }
            if (null != mIvProgress) {
                mIvProgress.setVisibility(View.VISIBLE);
//                int maxWidth = DeviceUtils.getScreenWidth(mCtx) / 4;
//                int maxHeight = DeviceUtils.getScreenHeight(mCtx) / 4;
//                if(null!=mCtx){
//                    int maxWidth = mCtx.getResources().getDimensionPixelSize(R.dimen.loading_width);
//                    Glide.with(mCtx).load(R.drawable.loading).asGif().placeholder(R.drawable.loading).fitCenter().override(maxWidth, maxWidth).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIvProgress);
//                }
            }

        } else {
            if (null != mProBar) {
                mProBar.setVisibility(View.VISIBLE);
            }
            if (null != mIvProgress) {
                mIvProgress.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 是否gif加载方式（否则Progressbar）
     *
     * @return
     */
    public boolean isGifLoading() {
        return mIsGif;
    }

    /**
     * 是否gif加载方式（否则Progressbar）
     *
     * @param mIsGif
     * @return
     */
    public void setIsGifLoading(boolean mIsGif) {
        this.mIsGif = mIsGif;
        if ((mIsGif && this.mIsGif) || (!mIsGif && !this.mIsGif)) {
            return;
        }
        this.handleViewsByLoadingStyle(mIsGif);
    }

    public void setRetryView(View retry) {
        this.mRetry = retry;
    }

    public void setLoadingText(String loadingTips) {
        if (null != mTvProgress) {
            mTvProgress.setText(loadingTips);
        }
    }

    public void setLoadingRetryTextFirst(String retryTips) {
        if (null != mTvErrorFirst) {
            mTvErrorFirst.setText(retryTips);
        }
    }

    public void setLoadingRetryTextSecond(String retryTips) {
        if (null != mTvErrorSecond) {
            mTvErrorSecond.setText(retryTips);
        }
    }

    /**
     * 开始加载时调用
     */
    public void showLoading() {
        if (View.VISIBLE == mProgress.getVisibility()) {
            return;
        }
        if (this.isGifLoading()) {
            this.startProgress();
        }
        this.showView(mProgress);
    }

    /**
     * 结束加载时调用
     */
    public void hideLoading() {
        if (View.GONE == mProgress.getVisibility()) {
            return;
        }
        if (this.isGifLoading()) {
            this.stopProgress();
        }
        this.hideView(mProgress);
    }

    /**
     * 加载失败时调用
     */
    public void showRetry() {
        this.showView(mProgress);
        this.showView(mRetry);
        if (this.isGifLoading()) {
            this.stopProgress();
            this.hideView(mIvProgress);
        } else {
            this.hideView(mProBar);
        }
        this.hideView(mTvProgress);
    }

    /**
     * 点击加载失败的界面重新加载时调用
     */
    public void hideRetry(boolean isShowProgress) {
        if (isShowProgress) {
            if (this.isGifLoading()) {
                this.showView(mIvProgress);
                this.stopProgress();
            } else {
                this.showView(mProBar);
            }
            this.showView(mTvProgress);
        }
        this.hideView(mRetry);
    }

    private void startProgress() {
        if (null == mAnimDrawable) {
            mAnimDrawable = (AnimationDrawable) mIvProgress.getDrawable();
        }
        mAnimDrawable.start();
//        if (null != mIvProgress) {
//            mIvProgress.setVisibility(View.VISIBLE);
//        }
    }

    private void stopProgress() {
        if (null == mIvProgress) {
            return;
        }
        mIvProgress.clearAnimation();
//        if (null != mIvProgress) {
//            mIvProgress.setVisibility(View.GONE);
//        }
    }

    private void showView(View view) {
        if (null == view) {
            return;
        }
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        if (null == view) {
            return;
        }
        view.setVisibility(View.GONE);
    }

}
