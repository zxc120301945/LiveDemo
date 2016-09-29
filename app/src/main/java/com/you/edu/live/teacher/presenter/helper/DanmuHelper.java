package com.you.edu.live.teacher.presenter.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.bean.Comments;
import com.you.edu.live.teacher.view.custom.danmu.CommentsDanmu;
import com.you.edu.live.teacher.view.custom.danmu.DanmuProxy;
import com.you.edu.live.teacher.view.custom.danmu.IDanmu;
import com.you.edu.live.teacher.view.custom.danmu.MessageDanmu;
import com.you.edu.live.teacher.widget.GlideCircleTransform;

/**
 * 弹幕助手（对外）
 */
public class DanmuHelper implements DanmuProxy.OnDisappearListener {

    /**
     * 展示（存在）时间
     **/
    private static final int DURATION_SHOW_COMMENT = 5000;
    private static final int DURATION_SHOW_MSG = 1500;
    /**
     * 消失时动画时间
     **/
    private static final int DURATION_DISAPPEAR_ANIM_COMMENT = 1000;
    private static final int DURATION_DISAPPEAR_ANIM_MSG = 500;
    /**
     * 最大展示数量
     **/
    private static final int MAX_SHOW_COMMNETS = 5;
    private Context mCtx;
    private ViewGroup mContainer;
    private DanmuKeeper mDanmuKeeper;
    private int mAvatorWidth = 0, mAvatorHeight = 0;
    /**
     * 横屏或竖屏（评论UI显示不一致）
     **/
    private boolean mIsLand = true;

    public DanmuHelper(ViewGroup container) {
        this(container, null);
    }

    public DanmuHelper(ViewGroup container, OnClickListener mClickListener) {
        if (null == container) {
            throw new IllegalArgumentException("Container cannot be null");
        }
        this.mContainer = container;
        mCtx = mContainer.getContext();
        mDanmuKeeper = new DanmuKeeper();
        mAvatorHeight = mAvatorWidth = mCtx.getResources()
                .getDimensionPixelSize(R.dimen.avator_thumb_width_mini);
    }

    /**
     * 是否超出最大展示数
     *
     * @return
     */
    private boolean isOverShow() {
        return null == mContainer ? true
                : mContainer.getChildCount() >= MAX_SHOW_COMMNETS;
    }

    public boolean isLand() {
        return mIsLand;
    }

    public void setIsLand(boolean mIsLand) {
        this.mIsLand = mIsLand;
    }

    /**
     * 发布评论弹幕
     *
     * @param comment
     */
    public void sendDanmuComment(Comments comment) {
        if (null == comment) {
            return;
        }
        IDanmu danmu = this.buildDanmuComment(comment);
        if (this.isOverShow()) {
            mDanmuKeeper.pushDanmu(danmu);
        } else {
            this.sendDanmu(danmu);
        }
    }

    /**
     * 发布文本弹幕
     *
     * @param msg
     */
    public void sendDanmuMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        IDanmu danmu = this.buildDanmuMsg(msg);
        if (this.isOverShow()) {
            mDanmuKeeper.pushDanmu(danmu);
        } else {
            this.sendDanmu(danmu);
        }
    }

    public void destroy() {
        if (null != mDanmuKeeper) {
            mDanmuKeeper.destroy();
        }
    }

    private void sendDanmu(IDanmu danmu) {
        if (null == danmu) {
            return;
        }
        int type = danmu.getType();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 15;
        if (IDanmu.TYPE_COMMENT == type) {
            mContainer.addView((CommentsDanmu) danmu, lp);

        } else if (IDanmu.TYPE_MESSAGE == type) {
            mContainer.addView((MessageDanmu) danmu, lp);
        }
        danmu.sendDanmu();
    }

    @SuppressWarnings("unchecked")
    private CommentsDanmu buildDanmuComment(Comments comment) {
        CommentsDanmu danmu = null;
        if (mIsLand) {
            danmu = (CommentsDanmu) LayoutInflater.from(mCtx).inflate(
                    R.layout.view_live_danmu_item_comment, null);
        } else {
//			danmu = (CommentsDanmu) LayoutInflater.from(mCtx).inflate(
//					R.layout.comments_list_item, null);
        }
        ImageView mIvAvator = (ImageView) danmu
                .findViewById(R.id.comments_list_item_iv_avator);
        TextView mTvUsername = (TextView) danmu
                .findViewById(R.id.comments_list_item_tv_username);
        TextView mTvContent = (TextView) danmu
                .findViewById(R.id.comments_list_item_tv_content);
        Glide.with(mCtx)
                .load(comment.getAvator())
                .override(mAvatorWidth, mAvatorHeight).bitmapTransform(new GlideCircleTransform(mCtx))
                .placeholder(R.drawable.user_avator)
                .error(R.drawable.user_avator).into(mIvAvator);

        mTvUsername.setText(comment.getUsername());
        mTvContent.setText(comment.getContent());
        danmu.getBackground().setAlpha(70);
        danmu.setPoriority(IDanmu.PORIORITY_FIRST);
        danmu.setDuration(DURATION_SHOW_COMMENT);
        danmu.setDisappearListener(this);
        danmu.setGravity(Gravity.BOTTOM);
        danmu.setTag(comment);
        return danmu;
    }

    private MessageDanmu buildDanmuMsg(String msg) {
        MessageDanmu danmu = (MessageDanmu) LayoutInflater.from(mCtx).inflate(
                R.layout.view_live_danmu_item_msg, null);
        danmu.setText(msg);
        danmu.setDuration(DURATION_SHOW_MSG);
        danmu.setDisappearListener(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 15;
        danmu.getBackground().setAlpha(70);
        danmu.setGravity(Gravity.BOTTOM);
        danmu.setPoriority(IDanmu.PORIORITY_SECOND);
        danmu.setTag(msg);
        return danmu;
    }

    private void removeDanmu(IDanmu danmu) {
        if (null == danmu) {
            return;
        }
        int type = danmu.getType();
        if (IDanmu.TYPE_COMMENT == type) {
            mContainer.removeView((CommentsDanmu) danmu);

        } else if (IDanmu.TYPE_MESSAGE == type) {
            mContainer.removeView((MessageDanmu) danmu);
        }
        // 如果有缓存，则自动新增一个弹幕（优先弹出评论）
        IDanmu temp = mDanmuKeeper.pomDanmu();
        if (null != temp) {
            this.sendDanmu(temp);
        }

    }

    @Override
    public void disappear(final IDanmu danmu) {
        // TODO Auto-generated method stub
        ObjectAnimator animator = ObjectAnimator.ofFloat(danmu, "alpha", 1.0f,
                0.5f, 0.0f);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                if (null == danmu) {
                    return;
                }
                DanmuHelper.this.removeDanmu(danmu);
            }

        });
        int poriority = danmu.getPoriority();
        if (IDanmu.PORIORITY_FIRST == poriority) {
            animator.setDuration(DURATION_DISAPPEAR_ANIM_COMMENT).start();
        } else if (IDanmu.PORIORITY_SECOND == poriority) {
            animator.setDuration(DURATION_DISAPPEAR_ANIM_MSG).start();
        }

    }

}
