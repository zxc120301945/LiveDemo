package com.you.edu.live.teacher.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class UserCourseAdapter extends GridLayoutAdapter<Course> {

    private Context mCtx;
    private IOnClickItemListener mListener;
    private UserCourseViewHolder mViewHolder;

    public UserCourseAdapter(Context ctx, List<Course> list) {
        super(list);
        mCtx = ctx;
    }

    public void unbind() {
        if (mViewHolder != null) {
            mViewHolder.unbind();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user_course, parent, false);
        mViewHolder = new UserCourseViewHolder(view);
        return mViewHolder;
    }

    @Override
    protected void onBindHeaderView(View headerView) {

    }

    @Override
    protected void onBindItemView(RecyclerView.ViewHolder holder, Course item, int position) {
        if (holder == null) {
            return;
        }
        if (holder instanceof UserCourseViewHolder) {
            UserCourseViewHolder viewHolder = (UserCourseViewHolder) holder;
            viewHolder.setContext(mCtx);
            viewHolder.setUserCourseItem(item);
        }
    }

    public void setOnClickItemListener(IOnClickItemListener listener) {
        mListener = listener;
    }

    public IOnClickItemListener getonClickItemListener() {
        return mListener;
    }

    public class UserCourseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_root)
        RelativeLayout mRlRoot;
        @BindView(R.id.course_cover)
        ImageView mCourseCover;
        @BindView(R.id.tv_course_name)
        TextView mCourseName;
        @BindView(R.id.tv_course_number)
        TextView mCourseNumber;
        @BindView(R.id.tv_live_state)
        TextView mLiveState;
        private Context mCtx;
        private int mCoverWidth;
        private int mCoverHeight;
        private IOnClickItemListener mOnClickItemListener;
        private Unbinder mUnbinder;

        public UserCourseViewHolder(View itemView) {
            super(itemView);
            mUnbinder = ButterKnife.bind(this, itemView);
        }


        public void setContext(Context ctx) {
            mCtx = ctx;
            int screenWidth = DeviceUtils.getScreenWidth(ctx);
            mCoverWidth = screenWidth / 3;
            mCoverHeight = (int) (mCoverWidth * 9) / 16;
        }

        public Context getContext() {
            return mCtx;
        }

        public void unbind() {
            if (mUnbinder != null) {
                mUnbinder.unbind();
            }
        }

        public void setUserCourseItem(Course item) {
            if (item == null) {
                return;
            }
            mCourseName.setText(item.getCourse_name());
            mCourseNumber.setText("共" + item.getCourse_hour() + "课时");
            if (this.getContext() != null) {
                if (mCoverWidth != 0 && mCoverHeight != 0) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCourseCover.getLayoutParams();
                    params.width = mCoverWidth;
                    params.height = mCoverHeight;
                    mCourseCover.setLayoutParams(params);

                    Glide.with(this.getContext()).load(item.getCourse_thumb())
                            .override(mCoverWidth, mCoverHeight).centerCrop()
                            .into(mCourseCover);
                }
            }
            int play_status = item.getPlay_status();
            if (play_status == GlobalConfig.ItemPlayStatus.PLAY_STATUS ||
                    play_status == GlobalConfig.ItemPlayStatus.PLAY_STATUS_TODAY) {////今日直播[换算]
                mLiveState.setText(mCtx.getResources().getString(R.string.today_live));
                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.main_tv_color_red));
            } else if (play_status == GlobalConfig.ItemPlayStatus.PLAY_STATUS_NO_LIVE) {//即将直播
                mLiveState.setText(mCtx.getResources().getString(R.string.soon_live));
                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.tv_color_blue));
            } else if (play_status == GlobalConfig.ItemPlayStatus.PLAY_STATUS_PLAY_BACK) {//视频／回放
                mLiveState.setText(mCtx.getResources().getString(R.string.no_live));
                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.main_tv_color_black));
            } else if (play_status == GlobalConfig.ItemPlayStatus.PLAY_STATUS_HANDLE) {//回放处理中
                mLiveState.setText(mCtx.getResources().getString(R.string.live_treatment));
                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.main_tv_color_black));
            }

//                mLiveState.setText(mCtx.getResources().getString(R.string.course_overdue));
//                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.main_tv_color_black));
//                mLiveState.setText(mCtx.getResources().getString(R.string.course_halt_the_sales));
//                mLiveState.setTextColor(mCtx.getResources().getColor(R.color.main_tv_color_black));


            mRlRoot.setTag(R.id.rl_root, item);
        }


        @OnClick(R.id.rl_root)
        public void onClickItem() {
            Course item = (Course) mRlRoot.getTag(R.id.rl_root);
            if (item != null) {
                if (UserCourseAdapter.this.getonClickItemListener() != null) {
                    UserCourseAdapter.this.getonClickItemListener().onClickItem(GlobalConfig.ClickYype.COURSE_CLICK, item);
                }
            }
        }
    }
}
