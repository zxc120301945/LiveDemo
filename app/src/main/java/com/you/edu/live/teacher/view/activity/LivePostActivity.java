package com.you.edu.live.teacher.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laifeng.sopcastsdk.LFLiveLog;
import com.laifeng.sopcastsdk.LFLiveView;
import com.laifeng.sopcastsdk.camera.CameraUtils;
import com.laifeng.sopcastsdk.camera.exception.CameraDisabledException;
import com.laifeng.sopcastsdk.camera.exception.NoCameraException;
import com.laifeng.sopcastsdk.entity.StreamInfo;
import com.laifeng.sopcastsdk.entity.VideoConfig;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.ILivePostContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Comments;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.WebSocketDao;
import com.you.edu.live.teacher.presenter.LivePostPresenter;
import com.you.edu.live.teacher.presenter.helper.CountdownHelper;
import com.you.edu.live.teacher.presenter.helper.DanmuHelper;
import com.you.edu.live.teacher.presenter.helper.SlideGuestureHelper;
import com.you.edu.live.teacher.utils.CommonUtil;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.utils.TimeUtil;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.widget.DialogHelper;
import com.you.edu.live.teacher.widget.LiveGoodAnimatorHelper;
import com.you.edu.live.teacher.widget.LoadingHelper;
import com.you.edu.live.teacher.widget.SystemBarTintManager;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 教师做直播界面
 * 作者：XingRongJing on 2016/6/28.
 */
public class LivePostActivity extends BaseViewActivity implements ILivePostContract.ILivePostView, CountdownHelper.ICountDownListener, SlideGuestureHelper.ISlideGuestureListener {
    /**
     * 使用来疯SDK需是flv流（1:插件flv流 2：rtmp多麦流 3：网络流 4：rtmp流）
     **/
    private static final int STREAM_TYPE = 1;
    private static final String TIME_FORMAT = "HH" + "点" + "mm" + "分";
    private static final String TIME_FORMAT_MM_DD = "MM" + "月" + "dd" + "日" + "HH" + "点" + "mm" + "分";
    private static final int REQ_CODE_PERMISSION = 1;
    @BindView(R.id.live_post_lfliveview)
    LFLiveView mLFLiveView;
    @BindView(R.id.live_post_rl_init_loading)
    RelativeLayout mRlInitLoading;//初始化加载中
    @BindView(R.id.live_post_tv_user_count)
    TextView mTvUserCount;//用户人数
    @BindView(R.id.live_post_rv_users)
    RecyclerView mRvUsers;//用户头像集
    @BindView(R.id.live_post_tv_good_count)
    TextView mTvGoodCount;//点赞数
    @BindView(R.id.live_post_iv_camera_switch)
    ImageView mIvCameraSwitch;//摄像头切换
    @BindView(R.id.live_post_iv_light_switch)
    ImageView mIvLightSwitch;//闪关灯控制
    @BindView(R.id.live_post_tv_stop)
    TextView mBtnStopLive;//停止直播
    @BindView(R.id.live_post_ll_right_top_btns)
    LinearLayout mLlRightTopBtns;//右上角三个btn的父类
    @BindView(R.id.live_post_ll_messages)
    LinearLayout mLlMessagesContainer;//聊天消息容器
    @BindView(R.id.live_post_tv_pilot_tips)
    TextView mTvPilotTips;//试播提示文案
    @BindView(R.id.live_post_tv_start_play)
    TextView mBtnStartLive;//开始直播
    @BindView(R.id.live_post_rl_pilot)
    RelativeLayout mRlPilot;//试播父view
    @BindView(R.id.live_post_tv_play_error_tips)
    TextView mTvPlayErrorTips;//播放出错时提示文案
    @BindView(R.id.live_post_tv_play_retry)
    TextView mBtnPlayRetry;//播放出错点击重试
    @BindView(R.id.live_post_rl_error)
    RelativeLayout mRlError;//播放出错父类
    @BindView(R.id.live_post_probar_buffering)
    ProgressBar mProbarBuffering;//直播缓存中
    @BindView(R.id.live_post_rl_views)
    RelativeLayout mRlAllViews;//所有控制view
    @BindView(R.id.live_post_iv_slide_icon)
    ImageView mIvSlide;//手势提示icon

    private Unbinder mUnbinder;

    private LivePostPresenter mPresenter;
    private StreamInfo mStreamInfo;
    private String mRoomId, mSocketIp;
    private int mChid = 0;
    /**
     * 直播开始时间（秒，立即直播时为0）
     **/
    private long mLiveStartTime = 0;
    /**
     * 是否到直播时间
     **/
    private boolean mIsLiveTimeUp = false;
    //直播弹幕管理
    private DanmuHelper mDanmuHelper;
    private SlideGuestureHelper mSlideHelper;
    private LiveGoodAnimatorHelper mGoodAnimHelper;
    private LoadingHelper mLoadingHelper;
    /**
     * 直播错误异常Dialog助手、结束直播提示Dialog助手、开始时间到点提示dialog
     **/
    private DialogHelper mLiveErrorDialogHelper, mLiveExitDialogHelper, mLiveStartDialogHelper;
    private boolean mIsFlashOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//5.0以上直接走style
            this.setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager
                    .setStatusBarTintResource(R.color.colorPrimary);// 通知栏所需颜色
        }
        this.setContentView(R.layout.activity_live_post);

        boolean isGranted = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (isGranted) {
            this.prepareViews();
            this.prepareDatas(savedInstanceState);
        } else { //申请camera与Record_audio权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    REQ_CODE_PERMISSION);
        }

    }

    private void prepareViews() {
        mUnbinder = ButterKnife.bind(this);
        this.getMvpPresenter().attachView(this);
        mLFLiveView.setLiveListener(this.getMvpPresenter());
        mLFLiveView.init();
        // 初始化view后  需要马上调用，不然可能设置无效
        mLFLiveView.setOrientation(LFLiveView.Orientation.LANDSCAPE);
        mLFLiveView.openBackCameraPriority(true);
        mLFLiveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != mSlideHelper) {
                    mSlideHelper.onTouchEvent(event);
                }
                return false;
            }
        });

    }

    private void prepareDatas(Bundle savedInstanceState) {
        if (!this.checkLivePermission(mLFLiveView)) {
            this.finish();
            return;
        }
        Intent intent = this.getIntent();
        mRoomId = intent.getStringExtra("roomId");
        mChid = intent.getIntExtra("chid", 0);
        mLiveStartTime = intent.getLongExtra("startTime", 0);
        mSocketIp = intent.getStringExtra("socketIp");
        if (null != savedInstanceState && TextUtils.isEmpty(mRoomId)) {
            mRoomId = savedInstanceState.getString("roomId");
            mChid = savedInstanceState.getInt("chid");
        }

        mDanmuHelper = new DanmuHelper(mLlMessagesContainer);
        mRvUsers.setLayoutManager(new LinearLayoutManager(this));
        mRvUsers.setAdapter(this.getMvpPresenter().getUserAdapter());
        mSlideHelper = new SlideGuestureHelper(this, this, false);
        mGoodAnimHelper = new LiveGoodAnimatorHelper(mTvGoodCount);
        mLoadingHelper = new LoadingHelper(this, mRlInitLoading, true);

        if (AppHelper.mIsLog) {
            LFLiveLog.isOpen(true);
        }
        this.setCommentLayoutParam(true);
        this.setBtnStartText(mLiveStartTime);

        this.getMvpPresenter().setIsPilot(true);//默认是试播
//        this.getMvpPresenter().setIsPilotToLive(false);
        //Step 1：创建流
        this.getMvpPresenter().requestCreateStream(mRoomId, STREAM_TYPE, null, mChid);
        //Step 2：连接socket
        this.getMvpPresenter().connectSocket(mSocketIp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mLFLiveView) {
            mLFLiveView.resumeLive();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mLFLiveView) {
            mLFLiveView.pauseLive();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (null != mPresenter) {
            mPresenter.destroy();
            mPresenter = null;
        }
        if (null != mLFLiveView) {
            mLFLiveView.release();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        if (null != outState) {
            outState.putString("roomId", mRoomId);
            outState.putInt("chid", mChid);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void showLiveLoadingView(boolean isBuffering) {
        if (null != mLoadingHelper) {
            mLoadingHelper.showLoading();
        }
        this.hideLivePilotView();
        if (isBuffering) {
            mIvSlide.setVisibility(View.GONE);
        } else {
            mTvGoodCount.setVisibility(View.GONE);
            mTvUserCount.setVisibility(View.GONE);
            mRvUsers.setVisibility(View.GONE);
            mIvSlide.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLiveLoadingView(boolean isBuffering) {
        if (this.getMvpPresenter().isPilot()) {
            this.showLivePilotView();
        }
        if (null != mLoadingHelper) {
            mLoadingHelper.hideLoading();
        }
        if (!isBuffering) {
            mTvGoodCount.setVisibility(View.VISIBLE);
            mTvUserCount.setVisibility(View.VISIBLE);
            mRvUsers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLiveErrorView(String error) {
        this.hideLivePilotView();
        mStreamInfo = null;//出错了需要重新创建流
        if (null != mTvPlayErrorTips) {
            mTvPlayErrorTips.setText(this.getString(R.string.live_play_error_tips) + "（" + error + "）");
        }
        if (null != mSlideHelper) {
            mSlideHelper.setGestureEnabled(false);
        }
        mRlError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLiveStopError(String error) {
        mStreamInfo = null;//出错了需要重新创建流
        if (null != mSlideHelper) {
            mSlideHelper.setGestureEnabled(false);
        }
        this.showDialogLiveError();
    }

    @Override
    public void onCreateStreamSuccess(StreamInfo streamInfo) {
        if (null != streamInfo) {
            //如果是直播转试播，则无需多余操作
            if (null != mStreamInfo && TextUtils.equals(streamInfo.getStreamId(), mStreamInfo.getStreamId())) {
                //开始上课-通知后台
                boolean isPilot = this.getMvpPresenter().isPilot();
                this.getMvpPresenter().requestLiveStartOrEnd(mRoomId, true, isPilot, true);
                if (!this.getMvpPresenter().isLiving()) {
                    mLFLiveView.startLive();//开始直播
                }
            } else {
                mStreamInfo = streamInfo;
                mLFLiveView.setStreamInfo(mStreamInfo);
                //SDK推荐值（宽高比：640*360，帧率：1300）
                VideoConfig videoConfig = new VideoConfig(640, 360, 1300);
                mLFLiveView.setVideoConfig(videoConfig);
                mLFLiveView.setOrientation(LFLiveView.Orientation.LANDSCAPE);
                if (!this.getMvpPresenter().isLiving()) {
                    mLFLiveView.startLive();//开始直播
                }
            }

        } else {
            if (this.getMvpPresenter().isLiving()) {//试播转直播失败时
                mLFLiveView.stopLive();
            }
            this.showLiveErrorView("StreamInfo is null");
        }
    }

    @Override
    public void onCreateStreamFailsWhenLiveInvalid(String error) {
        this.showToast(error);
        this.onClickFinish();
    }

    @Override
    public void onStartLiving(boolean isReliving) {
        if (this.getMvpPresenter().isPilot()) {
            this.showLivePilotView();
        } else {
            this.setCommentLayoutParam(false);
            this.hideLivePilotView();
        }
        if (null != mSlideHelper) {
            mSlideHelper.setGestureEnabled(true);
        }
        if (!this.getMvpPresenter().isSocketConnected()) {//如果Socket断开，则重新连接
            this.getMvpPresenter().connectSocket(mSocketIp);
        }
        if (!isReliving) {//非重连的直播开始，通知开始（开始失败、直播异常也走这个）
            boolean isPilot = this.getMvpPresenter().isPilot();
            this.getMvpPresenter().requestLiveStartOrEnd(mRoomId, true, isPilot, false);
        } else {//重连成功

        }
    }

    @Override
    public void showAllLiveInfoViews() {
        mRlAllViews.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllLiveInfoViews() {
        mRlAllViews.setVisibility(View.GONE);
    }

    @Override
    public void showLivePilotView() {
        mRlPilot.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLivePilotView() {
        mRlPilot.setVisibility(View.GONE);
    }

    @Override
    public void onSocketConnecting() {

    }

    @Override
    public void onSocketConnected() {
        User user = AppHelper.getAppHelper().getUser(this);
        // Step 1:socket登录信令
        if (null != user) {
            String loginMsg = WebSocketDao.buildLogin(user.getUid(),
                    user.getYktk(), "", mRoomId);
            this.getMvpPresenter().sendMessage(loginMsg);
        }
    }

    @Override
    public void onSocketDisconnected(String reason) {
//        this.getMvpPresenter().disconnectSocket();
    }

    @Override
    public void onReceiveMessage(Comments msg) {
        if (!this.isNeedDanmu()) {
            return;
        }
        if (null != mDanmuHelper) {
            mDanmuHelper.sendDanmuComment(msg);
        }
    }

    @Override
    public void onReceivePraise(int num) {
        if (null != mTvGoodCount) {
            mTvGoodCount.setText(CommonUtil.numberFormat(num));
        }
        if (!this.isNeedDanmu()) {
            return;
        }
        if (null != mGoodAnimHelper && !mGoodAnimHelper.isStarted()) {//来个动画
            mGoodAnimHelper.start();
        }

    }

    @Override
    public void onReceiveUserChanged(int uid, String username, boolean isComing) {
        if (null != mTvUserCount) {
            int num = this.getMvpPresenter().getUsersCount();
            mTvUserCount.setText(CommonUtil.numberFormat(num));
        }
        if (!this.isNeedDanmu()) {
            return;
        }
        if (null != mDanmuHelper) {
            String tips = isComing ? this.getString(R.string.live_user_coming) : this.getString(R.string.live_user_go);
            mDanmuHelper.sendDanmuMsg(username + tips);
        }
    }


    @Override
    public void onReceiveSocketNotice(String tips, boolean isError) {
        this.showToast(tips);
    }

    @Override
    public void onReceiveLiveFinish() {

    }

    @Override
    public void onCountDownTime(long seconds) {
//        mBtnStartLive.setText(" （" + seconds + "）");
    }

    @Override
    public void onCountDownFinish() {
        this.setBtnStartText(0);
        this.showDialogStartLive();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != mSlideHelper) {
            return mSlideHelper.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onSlideUp() {
        this.hideAllLiveInfoViews();
    }

    @Override
    public void onSlideDown() {
        this.showAllLiveInfoViews();
    }

    @Override
    public LivePostPresenter getMvpPresenter() {
        if (null == mPresenter) {
            mPresenter = new LivePostPresenter(this.getHttpApi());
        }
        return mPresenter;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        this.onClickFinish();
    }

    private int getDialogWidth() {
        return (int) (DeviceUtils.getScreenWidth(this) * 0.6);
    }

    /**
     * 直播异常Dialog
     **/
    private void showDialogLiveError() {
        if (null == mLiveErrorDialogHelper) {
            mLiveErrorDialogHelper = new DialogHelper(this);
            mLiveErrorDialogHelper.resetWindowWidth(this.getDialogWidth());
            mLiveErrorDialogHelper.setOperator(GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_ERROR);
            mLiveErrorDialogHelper.setMessage(this.getString(R.string.live_error_dialog_tips));
            mLiveErrorDialogHelper.setBtnCancel(this.getString(R.string.going));
            mLiveErrorDialogHelper.setBtnSureBg(R.drawable.common_disable_btn_bg_rectangle_selector);
            mLiveErrorDialogHelper.setCancelable(false);
            mLiveErrorDialogHelper.setBtnSure(this.getString(R.string.live_exit));
            mLiveErrorDialogHelper.setCanceledOnTouchOutside(false);
        }


        mLiveErrorDialogHelper.show();
        this.getMvpPresenter().setDialogOperator(mLiveErrorDialogHelper.getOperator());
    }

    /**
     * 直播中退出提示Dialog
     **/
    private void showDialogLiveExit() {
        if (null == mLiveExitDialogHelper) {
            mLiveExitDialogHelper = new DialogHelper(this);
            mLiveExitDialogHelper.resetWindowWidth(this.getDialogWidth());
            mLiveExitDialogHelper.setOperator(GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_EXIT);
            mLiveExitDialogHelper.setMessage(this.getString(R.string.live_exit_dialog_tips));
            mLiveExitDialogHelper.setBtnSure(this.getString(R.string.exit_course));
            mLiveExitDialogHelper.setBtnSureBg(R.drawable.common_disable_btn_bg_rectangle_selector);
        }
        mLiveExitDialogHelper.show();
        this.getMvpPresenter().setDialogOperator(mLiveExitDialogHelper.getOperator());
    }

    /**
     * 直播时间到提醒Dialog
     **/
    private void showDialogStartLive() {
        if (null == mLiveStartDialogHelper) {
            mLiveStartDialogHelper = new DialogHelper(this);
            mLiveStartDialogHelper.resetWindowWidth(this.getDialogWidth());
            mLiveStartDialogHelper.setOperator(GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_START);
            mLiveStartDialogHelper.setMessage(this.getString(R.string.live_start_live_dialog_tips));
            mLiveStartDialogHelper.setBtnCancel(this.getString(R.string.live_start_live_delay));
            mLiveStartDialogHelper.setBtnSure(this.getString(R.string.live_start_live_now));
            mLiveStartDialogHelper.setBtnCancelBg(R.drawable.common_disable_btn_bg_rectangle_selector);
            mLiveStartDialogHelper.setCanceledOnTouchOutside(false);
        }
        mLiveStartDialogHelper.show();
        this.getMvpPresenter().setDialogOperator(mLiveStartDialogHelper.getOperator());
    }

    private void dismissDialog(DialogHelper dialogHelper) {
        if (null != dialogHelper) {
            dialogHelper.dismiss();
        }
    }

    public void onClickDialogCancel(View view) {//Dialog-取消
        int operator = this.getMvpPresenter().getDialogOperator();
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_ERROR://直播异常，继续直播(需要重新建流了)
                if (!this.isNetworkEnable()) {
                    this.showToast(this.getString(R.string.no_network));
                    return;
                }
                this.dismissDialog(mLiveErrorDialogHelper);
                mStreamInfo = null;
                this.onPlayErrorRetry();
                break;
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_EXIT://取消退出直播
                this.dismissDialog(mLiveExitDialogHelper);
                break;
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_START://延迟上课
                this.dismissDialog(mLiveStartDialogHelper);
                break;
        }
    }

    public void onClickDialogSure(View view) {//Dialog-确定
        int operator = this.getMvpPresenter().getDialogOperator();
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_ERROR://出错后-老师不想玩了-下课
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_EXIT://直播中-老师不愿玩了-下课
                boolean isSendLive = true;
                //直播且是异常退出，发送SendLive（但统一改为试播结束，即异常了会结束流，但不会结束课程）
                if ((GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_ERROR == operator) && !this.getMvpPresenter().isPilot()) {
//                    isSendLive = false;
                    this.getMvpPresenter().setIsPilot(true);
                }
                this.dismissDialog(mLiveErrorDialogHelper);
                this.onStopPlay(isSendLive);
                this.setResult(RESULT_OK);
                this.finish();
                break;
            case GlobalConfig.Operator.OPERATOR_DIALOG_LIVE_START://正式上课
                this.onStartPlay();
                this.dismissDialog(mLiveStartDialogHelper);
                break;

        }
    }


    @OnClick({R.id.live_post_iv_camera_switch, R.id.live_post_iv_light_switch, R.id.live_post_tv_stop, R.id.live_post_tv_start_play, R.id.live_post_tv_play_retry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_post_iv_camera_switch:
                if (null != mLFLiveView) {
                    mLFLiveView.switchCamera();
                }
                break;
            case R.id.live_post_iv_light_switch:
                if (mIsFlashOpen) {
                    mIvLightSwitch.setImageResource(R.drawable.live_flash_open_selector);
                } else {
                    mIvLightSwitch.setImageResource(R.drawable.live_flash_close_selector);
                }
                mIsFlashOpen = !mIsFlashOpen;
                if (null != mLFLiveView) {
                    mLFLiveView.switchTorch();
                }
                break;
            case R.id.live_post_tv_stop://停止直播
                this.onClickFinish();
                break;
            case R.id.live_post_tv_start_play:
                if (!mIsLiveTimeUp) {
                    this.showToast(this.getString(R.string.live_time_unreached));
                    return;
                }
                this.onStartPlay();
                break;
            case R.id.live_post_tv_play_retry:
                this.onPlayErrorRetry();
                break;
        }
    }


    /**
     * 直播出错时重试
     **/
    private void onPlayErrorRetry() {
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        if (null != mRlError) {
            mRlError.setVisibility(View.GONE);
        }
        if (null == mStreamInfo) {
            this.getMvpPresenter().requestCreateStream(mRoomId, STREAM_TYPE, null, mChid);
        } else {
            this.getMvpPresenter().requestCreateStream(mRoomId, STREAM_TYPE, mStreamInfo.getStreamId(), mChid);
        }
    }

    private void onStartPlay() {
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        this.hideLivePilotView();
        if (this.getMvpPresenter().isPilot()) {
            this.getMvpPresenter().setIsPilot(false);
        }
        if (null != mStreamInfo) {//试播转直播-传递streamId之后可不重建直播流
            this.getMvpPresenter().requestCreateStream(mRoomId, STREAM_TYPE, mStreamInfo.getStreamId(), mChid);
        } else {
            this.getMvpPresenter().requestCreateStream(mRoomId, STREAM_TYPE, null, mChid);
        }
    }

    private void onClickFinish() {
        boolean isLiving = this.getMvpPresenter().isLiving();
        boolean isPilot = this.getMvpPresenter().isPilot();
        if (!isPilot /*&& isLiving*/) {//非试播，且正在直播，则提示
            this.showDialogLiveExit();
        } else {
            this.onStopPlay(true);
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    private void onStopPlay(boolean isSendLive) {
        boolean isPilot = this.getMvpPresenter().isPilot();
        boolean isLiving = this.getMvpPresenter().isLiving();
        if (isSendLive) {
            this.getMvpPresenter().requestLiveStartOrEnd(mRoomId, false, isPilot, false);
        }
        if (isLiving && null != mLFLiveView) {//先停止播放（或许不需要？）
            mLFLiveView.stopLive();
        }

    }

    private boolean isNeedDanmu() {
        return null == mRlAllViews ? false : View.VISIBLE == mRlAllViews.getVisibility();
    }

    /**
     * 设置评论区的布局（如果是试播（屏幕中间有按钮），则屏幕1/3；正式直播则2/3）
     *
     * @param isPilot
     */
    private void setCommentLayoutParam(boolean isPilot) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlMessagesContainer.getLayoutParams();
        if (null != params) {
            int screenWidth = DeviceUtils.getScreenWidth(this);
            if (isPilot) {
                params.width = screenWidth * 1 / 3;
            } else {
                params.width = screenWidth * 2 / 3;
            }
            mLlMessagesContainer.setLayoutParams(params);
        }
    }

    private void setBtnStartText(long mLiveStartTime) {
        String defaultTip = this.getString(R.string.live_start);
        long millSeconds = mLiveStartTime * 1000 - System.currentTimeMillis();
        if (mLiveStartTime > 0 && millSeconds > 0) {
            mIsLiveTimeUp = false;
            if (TimeUtil.isToday(mLiveStartTime)) {
                mBtnStartLive.setText(TimeUtil.formatDate(new Date(mLiveStartTime * 1000), TIME_FORMAT) + defaultTip);
            } else if (TimeUtil.isTomorrowDay(mLiveStartTime)) {
                mBtnStartLive.setText("明天" + TimeUtil.formatDate(new Date(mLiveStartTime * 1000), TIME_FORMAT) + defaultTip);
            } else {
                mBtnStartLive.setText(TimeUtil.formatDate(new Date(mLiveStartTime * 1000), TIME_FORMAT_MM_DD) + defaultTip);
            }
            mBtnStartLive.setBackgroundResource(R.drawable.common_btn_bg_arc_red_selector);
            this.getMvpPresenter().startCountDown(mLiveStartTime, this);
        } else {
            mIsLiveTimeUp = true;
            mBtnStartLive.setText(defaultTip);
            mBtnStartLive.setBackgroundResource(R.drawable.common_btn_bg_arc_blue_selector);
        }
        if (null != mBtnStartLive.getBackground()) {//透明度90%-255*0.9
            mBtnStartLive.getBackground().setAlpha(230);
        }
    }

    /**
     * 检查直播权限
     *
     * @param mLFLiveView
     * @return
     */
    private boolean checkLivePermission(LFLiveView mLFLiveView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {//此项无需了，app本身最低就18
            return false;
        }

        try {
            CameraUtils.checkCameraService(this);
        } catch (CameraDisabledException e) {
            this.showToast(this.getString(R.string.live_camera_no_permission));
            e.printStackTrace();
            return false;
        } catch (NoCameraException e) {
            this.showToast(this.getString(R.string.live_no_camera));
            e.printStackTrace();
            return false;
        }
        boolean openCheck = mLFLiveView.cameraCannotOpen();
        if (openCheck) {
            this.showToast(this.getString(R.string.live_camera_cannot_open));
            return false;
        }
        boolean supportCheck = mLFLiveView.cameraNotSupport();
        if (supportCheck) {
            this.showToast(this.getString(R.string.live_camera_cannot_support));
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQ_CODE_PERMISSION == requestCode) {
            boolean isGrant = (grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (isGrant) {
                this.prepareViews();
                this.prepareDatas(null);
            } else {
                this.showToast(this.getString(R.string.live_no_user_permission));
                this.finish();
            }
        }
    }
}
