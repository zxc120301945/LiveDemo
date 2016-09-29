package com.you.edu.live.teacher.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.you.edu.live.teacher.R;


/**
 * ProgressDialog助手
 *
 * @author xingrongjing
 * @see LoadingHelper
 */
public class ProgresDialogHelper {

    private Dialog mDialog;
    private TextView mTvLoading;


    private ImageView mIvProgress;
    private ProgressBar mPBarProgress;
    private AnimationDrawable mAnimDrawable;
    private boolean mIsUserDismiss = true;
    /**
     * 是否gif加载（false则progressbar）
     **/
    private boolean mIsGif = true;
    private Context mCtx;

    public ProgresDialogHelper(Context ctx) {
        this(ctx, R.layout.view_progress, R.id.loading_tv_text,
                R.id.loading_iv_pro, R.id.loading_probar, true);
    }

    public ProgresDialogHelper(Context ctx, boolean isGif) {
        this(ctx, R.layout.view_progress, R.id.loading_tv_text,
                R.id.loading_iv_pro, R.id.loading_probar, isGif);

    }

    public ProgresDialogHelper(Context ctx, int resId, int resTvId,
                               int resIvId, int resProBarId, boolean isGif) {
        this.mCtx = ctx;
        mDialog = new Dialog(ctx, R.style.dialog_progress_style);
        mDialog.setContentView(resId);
        mTvLoading = (TextView) mDialog.findViewById(resTvId);
        mIvProgress = (ImageView) mDialog.findViewById(resIvId);
        mPBarProgress = (ProgressBar) mDialog.findViewById(resProBarId);
        this.setIsGif(isGif);
        this.handleViewsByLoadingStyle(isGif);

    }

    private void handleViewsByLoadingStyle(boolean isGif) {
        // 设置采取哪种加载方式（ProgressBar或gif）
        if (isGif) {
            if (null != mPBarProgress) {
                mPBarProgress.setVisibility(View.GONE);
            }
            if (null != mIvProgress) {
                mIvProgress.setVisibility(View.VISIBLE);
//                if(null!=mCtx){
//                    int maxWidth = mCtx.getResources().getDimensionPixelSize(R.dimen.loading_width);
//                    Glide.with(mCtx).load(R.drawable.loading).asGif().placeholder(R.drawable.loading).fitCenter().override(maxWidth, maxWidth).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIvProgress);
//                }
            }

        } else {
            if (null != mPBarProgress) {
                mPBarProgress.setVisibility(View.VISIBLE);
            }
            if (null != mIvProgress) {
                mIvProgress.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 是否gif加载方式 （否则为ProgressBar）
     *
     * @return
     */
    public boolean isGif() {
        return mIsGif;
    }

    /**
     * 设置是否gif加载方式 （否则为ProgressBar）
     *
     * @param mIsGif
     * @return
     */
    public void setIsGif(boolean mIsGif) {
        this.mIsGif = mIsGif;
        this.handleViewsByLoadingStyle(mIsGif);
    }


    public void show() {
        this.setIsUserDismiss(true);
        if (null != mDialog && !mDialog.isShowing()) {
            mDialog.show();
            if (this.isGif()) {
                this.startProgress();
            }
        }
    }

    public void setIsUserDismiss(boolean isUserDismiss) {
        this.mIsUserDismiss = isUserDismiss;
    }

    public boolean isUserDismiss() {
        return mIsUserDismiss;
    }

    public void dismiss() {
        this.setIsUserDismiss(false);
        if (null != mDialog) {
            mDialog.dismiss();
            if (this.isGif()) {
                this.stopProgress();

            }
        }
    }

    public void setLoadingText(String loading) {
        if (null != mTvLoading) {
            mTvLoading.setText(loading);
        }
    }

    public void setCancelable(boolean cancel) {
        if (null != mDialog) {
            mDialog.setCancelable(cancel);
        }
    }

    public void setCanceledOnTouchOutside(boolean enable) {
        if (null != mDialog) {
            mDialog.setCanceledOnTouchOutside(enable);
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        if (null != mDialog) {
            mDialog.setOnCancelListener(listener);
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        if (null != mDialog) {
            mDialog.setOnDismissListener(listener);
        }
    }

    public boolean isShow() {
        return null == mDialog ? false : mDialog.isShowing();
    }

    private void startProgress() {
        if(null==mIvProgress){
			return;
		}
		if (null == mAnimDrawable) {
			mAnimDrawable = (AnimationDrawable) mIvProgress.getDrawable();
		}
		mAnimDrawable.start();
//		mIvProgress.setVisibility(View.VISIBLE);
//		Glide.with(mCtx).load(R.drawable.loading).asGif() /*.override(200,240)*/.diskCacheStrategy(DiskCacheStrategy.NONE).into(mIvProgress);
    }

    private void stopProgress() {
        if (null != mIvProgress) {
//            Glide.clear(mIvProgress);
//			mIvProgress.setVisibility(View.GONE);
            mIvProgress.clearAnimation();
        }
    }

}
