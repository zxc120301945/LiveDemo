package com.you.edu.live.teacher.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Live;
import com.you.edu.live.teacher.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class RecentLiveAdapter extends GridLayoutAdapter<Live> {

    private IOnClickItemListener mListener;
    private Context context;
    private RecentLiveViewHolder mViewHolder;

    public RecentLiveAdapter(List<Live> list) {
        super(list);
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    public void unBind() {
        if (mViewHolder != null) {
            mViewHolder.unbind();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chapter, parent, false);
        mViewHolder = new RecentLiveViewHolder(view);
        return mViewHolder;
    }

    @Override
    protected void onBindHeaderView(View headerView) {

    }

    @Override
    protected void onBindItemView(RecyclerView.ViewHolder holder, Live item, int position) {
        if (holder == null) {
            return;
        }

        if (holder instanceof RecentLiveViewHolder) {
            RecentLiveViewHolder viewHolder = (RecentLiveViewHolder) holder;
            viewHolder.setView(item);
        }

    }


    public class RecentLiveViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_live_time)
        TextView mLiveTime;
        @BindView(R.id.tv_course_hour_name)
        TextView mPeriodName;
        @BindView(R.id.tv_hour_sign)
        TextView mCourseName;
        @BindView(R.id.tv_live_classroom)
        TextView mTvClassRoom;

        Unbinder mUnbinder;
        private String mMonth;
        private String mTime;
        private String mWeek;

        public RecentLiveViewHolder(View itemView) {
            super(itemView);
            mUnbinder = ButterKnife.bind(this, itemView);
        }

        public void setView(Live item) {
            if (item != null) {
//                String thisMonth = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_MONTH);
//                String thisDay = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_DAY);
//                String toDay = thisMonth + "月" + thisDay + "日";
                int play_status = item.getPlay_status();
                long play_start = item.getPlay_start();
                String play_status_desc = item.getPlay_status_desc();
                Live.Format formatTime = item.getFormat_time();
                if (formatTime != null) {
                    mMonth = formatTime.getMonth();
                    mTime = formatTime.getTime();
                    mWeek = formatTime.getWeek();
                }
                if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS) {//正在直播
                    mTvClassRoom.setTextColor(new Color().WHITE);
                    mLiveTime.setTextColor(context.getResources().getColor(R.color.main_tv_color_red));
                    mTvClassRoom.setEnabled(true);
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_light_red_rectangle_selector);
                    mLiveTime.setText(play_status_desc);
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_SOON) {//即将直播
                    if (TimeUtil.isToday(play_start)) {
                        mTvClassRoom.setTextColor(new Color().WHITE);
                        mLiveTime.setText("今天" + mTime + mWeek + "直播");
                        mLiveTime.setTextColor(context.getResources().getColor(R.color.main_tv_color_red));
                        mTvClassRoom.setEnabled(true);
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_light_red_rectangle_selector);
                    } else if (TimeUtil.isTomorrowDay(play_start)) {
                        mTvClassRoom.setTextColor(new Color().WHITE);
                        mLiveTime.setText("明天" + mTime + mWeek + "直播");
                        mLiveTime.setTextColor(context.getResources().getColor(R.color.main_tv_color_red));
                        mTvClassRoom.setEnabled(true);
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_light_red_rectangle_selector);
                    } else {
                        mLiveTime.setText(mMonth + mTime + mWeek + "直播");
                        mLiveTime.setTextColor(context.getResources().getColor(R.color.tv_color_gray));
                        mTvClassRoom.setEnabled(false);
                        mTvClassRoom.setTextColor(context.getResources().getColor(R.color.tv_color_deep_blue));
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle);
                    }
                }

                String chapter_name = item.getChapter_name();
                if (!TextUtils.isEmpty(chapter_name)) {
                    mPeriodName.setText(chapter_name);
                }
                String course_name = item.getCourse_name();
                if (!TextUtils.isEmpty(course_name)) {
                    mCourseName.setText(course_name);
                }
                mTvClassRoom.setTag(R.id.tv_live_classroom, item);
            }
        }

        /**
         * 进入直播教室
         */
        @OnClick({R.id.tv_live_classroom, R.id.rl_chapter_item_root})
        public void onClickLiveClassRoom() {
            Live item = (Live) mTvClassRoom.getTag(R.id.tv_live_classroom);
            if (item != null) {
                if (mTvClassRoom.isEnabled()) {
                    if (RecentLiveAdapter.this.getonClickItemListener() != null) {
                        RecentLiveAdapter.this.getonClickItemListener().onClickItem(GlobalConfig.ClickYype.LIVE_CLASS_ROOM_CLICK, item);
                    }
                }
            }
        }

        public void unbind() {
            if (mUnbinder != null) {
                mUnbinder.unbind();
            }
        }
    }

    public void setOnClickItemListener(IOnClickItemListener listener) {
        mListener = listener;
    }

    public IOnClickItemListener getonClickItemListener() {
        return mListener;
    }
}
