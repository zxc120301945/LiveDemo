package com.you.edu.live.teacher.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.contract.IOnLongClickListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Chapter;
import com.you.edu.live.teacher.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class ChapterAdapter extends GridLayoutAdapter<Chapter> {

    private Context mCtx;
    private IOnClickItemListener mListener;
    private IOnLongClickListener mOnLongListener;
    private CourseHourViewHolder mViewHolder;

    public ChapterAdapter(Context ctx, List<Chapter> list) {
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
                R.layout.item_chapter, parent, false);
        mViewHolder = new CourseHourViewHolder(view);
        return mViewHolder;
    }

    @Override
    protected void onBindHeaderView(View headerView) {

    }

    @Override
    protected void onBindItemView(RecyclerView.ViewHolder holder, Chapter item, int position) {
        if (holder == null) {
            return;
        }
        if (holder instanceof CourseHourViewHolder) {
            CourseHourViewHolder viewHolder = (CourseHourViewHolder) holder;
            viewHolder.setCourseHourView(item);
        }
    }

    public class CourseHourViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        @BindView(R.id.rl_chapter_item_root)
        RelativeLayout mRlChapterItemRoot;
        @BindView(R.id.tv_course_hour_name)
        TextView mTvHourName;
        @BindView(R.id.tv_live_time)
        TextView mTvLiveTime;
        @BindView(R.id.tv_hour_sign)
        TextView mTvHourSign;
        @BindView(R.id.tv_live_classroom)
        TextView mTvClassRoom;
        private Unbinder mUnbind;

        public CourseHourViewHolder(View itemView) {
            super(itemView);
            mUnbind = ButterKnife.bind(this, itemView);
        }

        public void setCourseHourView(Chapter hour) {
            if (hour != null) {
                mTvHourName.setText(hour.getOrder_num() + "." + hour.getChapter_name());
                long play_start = hour.getPlay_start();
                int play_status = hour.getPlay_status();
                String chapter_time_msg = hour.getChapter_time_msg();
                String play_start_msg = hour.getPlay_start_msg();
                if (!TextUtils.isEmpty(play_start_msg)) {
                    mTvLiveTime.setText(play_start_msg + "直播");
                }
                if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_SOON) {//正在直播和即将直播
                    if (TimeUtil.isToday(play_start)) {
                        mTvClassRoom.setEnabled(true);
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_light_red_rectangle_selector);
                        mTvClassRoom.setTextColor(new Color().WHITE);
                        mTvClassRoom.setText(mCtx.getResources().getString(R.string.live_class));
                    } else if (TimeUtil.isTomorrowDay(play_start)) {
                        mTvClassRoom.setEnabled(true);
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_light_red_rectangle_selector);
                        mTvClassRoom.setTextColor(new Color().WHITE);
                        mTvClassRoom.setText(mCtx.getResources().getString(R.string.live_class));
                    } else {
                        mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                        mTvClassRoom.setEnabled(false);
                        mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle);
                        mTvClassRoom.setText(mCtx.getResources().getString(R.string.live_class));
                    }

                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_CANCEL) {//直播取消
                    mTvClassRoom.setEnabled(false);
                    mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle);
                    mTvClassRoom.setText(mCtx.getResources().getString(R.string.live_cancel));
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_END_NO_UPLOAD || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_END_UPLOADING
                        || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_END_UPLOAD_FAIL || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_CHECKING
                        || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_TRANSCODING) {//直播结束，未上传
                    mTvClassRoom.setEnabled(false);
                    mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle_2);
                    mTvClassRoom.setText(mCtx.getResources().getString(R.string.transcoding));
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_CHECK_FAIL) {//审核失败
                    mTvClassRoom.setEnabled(false);
                    mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle);
                    mTvClassRoom.setText(mCtx.getResources().getString(R.string.auditing_error));
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_FAIL) {//转码失败
                    mTvClassRoom.setEnabled(false);
                    mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle);
                    mTvClassRoom.setText(mCtx.getResources().getString(R.string.transcoding_error));
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_REPLAY) {//回放
                    mTvClassRoom.setEnabled(true);
                    mTvClassRoom.setTextColor(mCtx.getResources().getColor(R.color.tv_color_deep_blue));
                    mTvClassRoom.setBackgroundResource(R.drawable.common_btn_bg_azure_rectangle_selector);
                    mTvClassRoom.setText(mCtx.getResources().getString(R.string.watch_playback));
                    if (!TextUtils.isEmpty(chapter_time_msg)) {
                        mTvLiveTime.setText(chapter_time_msg);
                    }
                }

                String course_name = hour.getCourse_name();
                if (!TextUtils.isEmpty(course_name)) {
                    mTvHourSign.setText(course_name);
                }
                mTvHourName.setTag(R.id.tv_course_hour_name, hour);
                mRlChapterItemRoot.setOnLongClickListener(this);
            }
        }

        @OnClick({R.id.tv_live_classroom, R.id.rl_chapter_item_root})
        public void onClickBtn() {
            Chapter item = (Chapter) mTvHourName.getTag(R.id.tv_course_hour_name);
            if (item != null) {
                if (mTvClassRoom.isEnabled()) {
                    if (ChapterAdapter.this.getOnClickItemListener() != null) {
                        ChapterAdapter.this.getOnClickItemListener().onClickItem(GlobalConfig.ClickYype.COURSE_HOUR_CLICK, item);
                    }
                }
            }
        }

        public void unbind() {
            if (mUnbind != null) {
                mUnbind.unbind();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (ChapterAdapter.this.getOnLongClickItemListener() != null) {
                Chapter item = (Chapter) mTvHourName.getTag(R.id.tv_course_hour_name);
                ChapterAdapter.this.getOnLongClickItemListener().onLongClickItem(GlobalConfig.ClickYype.CHAPTER_DELETE_CLICK, item);
            }
            return false;
        }
    }

    public void setOnClickItemListener(IOnClickItemListener listener) {
        mListener = listener;
    }

    public IOnClickItemListener getOnClickItemListener() {
        return mListener;
    }


    public void setOnLongClickItemListener(IOnLongClickListener listener) {
        mOnLongListener = listener;
    }

    public IOnLongClickListener getOnLongClickItemListener() {
        return mOnLongListener;
    }
}
