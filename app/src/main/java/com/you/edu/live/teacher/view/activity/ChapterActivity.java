package com.you.edu.live.teacher.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IChapterContract;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.contract.IOnLongClickListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Chapter;
import com.you.edu.live.teacher.presenter.ChapterPresenter;
import com.you.edu.live.teacher.presenter.helper.ListRefreshHelper;
import com.you.edu.live.teacher.presenter.helper.ShanRefreshViewHelper;
import com.you.edu.live.teacher.utils.CommonUtil;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.widget.DialogHelper;
import com.you.edu.live.teacher.widget.DividerItemDecoration;
import com.you.edu.live.teacher.widget.PopWindowHelper;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.SystemBarTintManager;
import com.you.edu.live.teacher.widget.adapter.ChapterAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class ChapterActivity extends BaseViewActivity implements IChapterContract.IChapterView, ShanRefreshViewHelper.IRefreshListener, IOnClickItemListener, IOnLongClickListener, PopWindowHelper.onClickRootListener {

    @BindView(R.id.ll_chapter_root)
    LinearLayout mLlRoot;
    @BindView(R.id.rv_class_hour_list_page)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_class_hour_refresh)
    SwipeRefreshLayout mSwRefresh;
    @BindView(R.id.titlebar_back_rl_root)
    RelativeLayout mTitlebarRoot;
    @BindView(R.id.titlebar_back_tv_title)
    TextView mTitlebarTitle;
    @BindView(R.id.titlebar_back_iv_back)
    ImageView mTitlebarIvBack;
    @BindView(R.id.titlebar_back_iv_icon)
    ImageView mTitlebarIvIcon;

    private static final int ALL_CLASS_HOUR_CODE = 1;
    private static final int LIVE_CLASS_HOUR_CODE = 2;
    @BindView(R.id.iv_class_hour_empty_icon)
    ImageView mIvEmptyIcon;

    private int mCode = ALL_CLASS_HOUR_CODE;

    private Unbinder mUnbinder;
    private ChapterPresenter mPresenter;
    private ListRefreshHelper mRefreshHelper;
    private List<Chapter> mHourList;
    private ChapterAdapter mAdapter;

    private int mPage;
    private boolean isFirst = true;
    private int mCoid;
    private PopWindowHelper mPWHelper;
    private int mType = 0;//类型：0全部、1为直播、2为点播；默认0
    private ProgresDialogHelper mProgresDialogHelper;
    private DialogHelper mDialogHelper;

    private int mDeleteChid = 0;

    private static final int RECOND_REFRESH = 1;
    private int mTitleWidth;
    private int mMargin8;
    private TextView mTvPWAllClassHour;
    private TextView mTvPWLiveClassHour;
    private int mScreenWidth;
    private int mScreenHeight;

    private boolean isOpenPW = false;
    private ImageView mIvClassCheck;
    private ImageView mIvLiveCheck;
    private int mChid;
    private long mPlayStart;
    private String mRoomId;

    private boolean mIsChapterEmpty = false;
    private LinearLayout mLlAllClassHourRoot;
    private LinearLayout mLlLiveClassHourRoot;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        /**
         * 更改状态栏颜色 只适用于系统版本4.4及以上
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager
                    .setStatusBarTintResource(R.color.common_tv_color_best);// 通知栏所需颜色
        }

        this.setContentView(R.layout.activity_chapter);
        mUnbinder = ButterKnife.bind(this);
        this.prepareData();
        this.prepareView();
        this.preparePopupwindow();
        this.getMvpPresenter().attachView(this);
    }

    private void prepareData() {
        mCoid = this.getIntent().getIntExtra("coid", -1);
        mHourList = new ArrayList<Chapter>();
        mAdapter = new ChapterAdapter(this, mHourList);
        mAdapter.setOnClickItemListener(this);
        mAdapter.setOnLongClickItemListener(this);
        mProgresDialogHelper = new ProgresDialogHelper(this);
        mDialogHelper = new DialogHelper(this);
        mMargin8 = this.getResources().getDimensionPixelSize(
                R.dimen.margin_8);

        /*
         * 获取顶部SelectorTitle的高度
		 */
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mScreenHeight = DeviceUtils.getScreenHeight(this);
        mScreenWidth = DeviceUtils.getScreenWidth(this);
//        int titleHeight = mTitlebarRoot.getMeasuredHeight();
//        mPWHeight = mScreenWidth - titleHeight;
    }

    private void prepareView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mLlRoot.setPadding(0,
                    CommonUtil.getStatusHeight(this), 0, 0);
        }

        mTitlebarIvBack.setImageResource(R.drawable.white_back);
        mTitlebarIvBack.setBackgroundResource(R.drawable.common_btn_bg_light_blue_rectangle_selector);
        mTitlebarRoot.setBackgroundColor(getResources().getColor(R.color.common_tv_color_best));
        mTitlebarTitle.setTextColor(new Color().WHITE);
        mTitlebarTitle.setText(getString(R.string.all_class_hour));
        mDialogHelper.setBtnSure(this.getString(R.string.delete));
        mTitlebarIvIcon.setVisibility(View.VISIBLE);

        mSwRefresh.setVisibility(View.VISIBLE);
        mIvEmptyIcon.setVisibility(View.GONE);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTitlebarTitle.measure(w, h);
        mTitleWidth = mTitlebarTitle.getMeasuredWidth();


        //配置RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(
                this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        if (mRefreshHelper == null) {
            mRefreshHelper = new ListRefreshHelper<Chapter>(mSwRefresh,
                    mRecyclerView, mAdapter, this);
        }
        mRefreshHelper.setIsFootView(false);
        if (isFirst) {
            if (this.isNetworkEnable()) {
                if (mRefreshHelper != null) {
                    mRefreshHelper.onRefreshAuto();
                }
            } else {
                this.showToast(getString(R.string.no_network_retry));
            }
            isFirst = false;
        }
    }

    private void preparePopupwindow() {
        View selectorView = View.inflate(this,
                R.layout.view_popupwindow, null);
        mLlAllClassHourRoot = (LinearLayout) selectorView.findViewById(R.id.ll_all_class_hour_root);
        mLlLiveClassHourRoot = (LinearLayout) selectorView.findViewById(R.id.ll_live_class_hour_root);
        mTvPWAllClassHour = (TextView) selectorView.findViewById(R.id.tv_all_class_hour_des);
        mTvPWLiveClassHour = (TextView) selectorView.findViewById(R.id.tv_live_class_hour_des);
        mIvClassCheck = (ImageView) selectorView.findViewById(R.id.iv_all_class_hour_check);
        mIvLiveCheck = (ImageView) selectorView.findViewById(R.id.iv_live_class_hour_check);
        mIvClassCheck.setVisibility(View.VISIBLE);
        mIvLiveCheck.setVisibility(View.INVISIBLE);
        mPWHelper = new PopWindowHelper(this, selectorView,
                R.style.course_all_floating_anim_style, false);
        mPWHelper.setPopupwindowStyle(new Color().TRANSPARENT,
                mScreenWidth, mScreenHeight);
        /** 设置悬浮窗点击事件可以被外界获取 **/
        mPWHelper.setPopupWindowTouchModal(false);
        mPWHelper.setOutsideTouchable(false);
        mPWHelper.setOnClickRootListener(this);

    }

    /**
     * 悬浮窗
     */
    @OnClick(R.id.titlebar_back_tv_title)
    public void onClickTilte() {
        if (!isOpenPW) {
            mTitlebarIvIcon.setImageResource(R.drawable.up_small_triangle);
            if (!mIsChapterEmpty) {
                mLlAllClassHourRoot.setEnabled(true);
                mLlLiveClassHourRoot.setEnabled(true);
                if (mCode == ALL_CLASS_HOUR_CODE) {
                    mTvPWAllClassHour.setTextColor(this.getResources().getColor(R.color.main_color));
                    mTvPWLiveClassHour.setTextColor(new Color().BLACK);
                    mIvClassCheck.setVisibility(View.VISIBLE);
                    mIvLiveCheck.setVisibility(View.INVISIBLE);
                    mTitlebarTitle.setText(getString(R.string.all_class_hour));
                } else {
                    mTvPWAllClassHour.setTextColor(new Color().BLACK);
                    mTvPWLiveClassHour.setTextColor(this.getResources().getColor(R.color.main_color));
                    mIvClassCheck.setVisibility(View.INVISIBLE);
                    mIvLiveCheck.setVisibility(View.VISIBLE);
                    mTitlebarTitle.setText(getString(R.string.live_class_hour));
                }
            } else {
                mLlAllClassHourRoot.setEnabled(false);
                mLlLiveClassHourRoot.setEnabled(false);
                mTvPWAllClassHour.setTextColor(this.getResources().getColor(R.color.main_color));
                mTvPWLiveClassHour.setTextColor(new Color().GRAY);
                mIvClassCheck.setVisibility(View.VISIBLE);
                mIvLiveCheck.setVisibility(View.INVISIBLE);
                mTitlebarTitle.setText(getString(R.string.all_class_hour));
            }
            if (mPWHelper != null) {
                mPWHelper.showAsDropDown(mTitlebarTitle, (-((mTitleWidth / 2) - (mPWHelper.getWidth() / 2))),
                        mMargin8);
            }
            isOpenPW = !isOpenPW;
        } else {
            mTitlebarIvIcon.setImageResource(R.drawable.down_small_triangle);
            if (mPWHelper != null && mPWHelper.isShowing()) {
                mPWHelper.dismiss();
                isOpenPW = !isOpenPW;
            }
        }
    }

    /**
     * 返回
     */
    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickBack() {
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(-1, R.anim.slide_right_out);
    }

    /**
     * 全部课时
     *
     * @param v
     */
    public void onClickallClassHour(View v) {
        mType = 0;
        mCode = ALL_CLASS_HOUR_CODE;
        mTitlebarTitle.setText(getString(R.string.all_class_hour));
        if (mRefreshHelper != null) {
            mRefreshHelper.onRefreshAuto();
        }
        if (mPWHelper != null) {
            mPWHelper.dismiss();
            isOpenPW = !isOpenPW;
            mTitlebarIvIcon.setImageResource(R.drawable.down_small_triangle);
        }
    }

    /**
     * 直播课时
     *
     * @param v
     */
    public void onClickLiveClassHour(View v) {
        mType = 1;
        mCode = LIVE_CLASS_HOUR_CODE;
        mTitlebarTitle.setText(getString(R.string.live_class_hour));
        if (mRefreshHelper != null) {
            mRefreshHelper.onRefreshAuto();
        }
        if (mPWHelper != null) {
            mPWHelper.dismiss();
            isOpenPW = !isOpenPW;
            mTitlebarIvIcon.setImageResource(R.drawable.down_small_triangle);
        }
    }

    /**
     * 新建直播
     */
    @OnClick(R.id.btn_create_live)
    public void onClickCreateLive() {
        Intent intent = new Intent(this, CreateLiveActivity.class);
        intent.putExtra("coid", mCoid);
        this.startActivityForResult(intent, RECOND_REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == RECOND_REFRESH) {
            if (mRefreshHelper != null) {
                mRefreshHelper.onRefreshAuto();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mPWHelper != null && mPWHelper.isShowing()) {
            mPWHelper.dismiss();
        }
        if (mAdapter != null) {
            mAdapter.unbind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void showLoadingView() {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.show();
        }

    }

    @Override
    public void hideLoadingView() {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
    }

    @Override
    public void onChapterSuccess(List<Chapter> hourList) {

        if (hourList != null && !hourList.isEmpty()) {
            mIsChapterEmpty = false;
            mRecyclerView.setVisibility(View.VISIBLE);
            mIvEmptyIcon.setVisibility(View.GONE);
            if (mRefreshHelper != null) {
                if (mRefreshHelper.isPullDownRefresh()) {
                    mRefreshHelper.clearList(mHourList);
                }
                mRefreshHelper.onRefreshDatasComplete(hourList);
                mRefreshHelper.onRefreshViewComplete(false);
            }
            mHourList.addAll(hourList);
            mAdapter.notifyDataSetChanged();
        } else {
            mRefreshHelper.onRefreshViewComplete(false);
            mRecyclerView.setVisibility(View.GONE);
            mIvEmptyIcon.setVisibility(View.VISIBLE);
            mIsChapterEmpty = true;
        }

    }

    @Override
    public void onLiveChapterSuccess(List<Chapter> hourList) {

        if (hourList != null && !hourList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mIvEmptyIcon.setVisibility(View.GONE);
            if (mRefreshHelper != null) {
                if (mRefreshHelper.isPullDownRefresh()) {
                    mRefreshHelper.clearList(mHourList);
                }
                mRefreshHelper.onRefreshDatasComplete(hourList);
                mRefreshHelper.onRefreshViewComplete(false);
            }
            mHourList.addAll(hourList);
            mAdapter.notifyDataSetChanged();
        } else {
            mRefreshHelper.onRefreshViewComplete(false);
            mRecyclerView.setVisibility(View.GONE);
            mIvEmptyIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteChapterSuccess(String msg) {
        this.showToast(msg);
        if (mRefreshHelper != null) {
            mRefreshHelper.onRefreshAuto();
        }
    }

    @Override
    public void onRoomInfoSuccess(Map<String, String> map) {
        if (map != null) {
            String socketIp = map.get("socket_host");

            Intent intent = new Intent(this, LivePostActivity.class);
            if (!TextUtils.isEmpty(mRoomId)) {
                intent.putExtra("roomId", mRoomId);
            }
            if (!TextUtils.isEmpty(socketIp)) {
                intent.putExtra("socketIp", socketIp);
            }
            intent.putExtra("chid", mChid);
            intent.putExtra("startTime", mPlayStart);
            this.startActivityForResult(intent, RECOND_REFRESH);
        }
    }

    @Override
    public void showError(String error) {
        mRefreshHelper.onRefreshViewComplete(false);
        this.showToast(error);
    }

    @Override
    public ChapterPresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new ChapterPresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(this));
        }
        return mPresenter;
    }

    @Override
    public void onPullDownRefresh() {
        AppHelper.getAppHelper().getUser(this);
        if (null != mRefreshHelper) {
            mRefreshHelper.reset();
        }
        AppHelper.getAppHelper().getUser(this);

//        mRefreshHelper.reset();
//        mPage = 1;
        if (mType == 0) {
            this.getMvpPresenter().requestChapter(mCoid, mType);
        } else if (mType == 1) {
            this.getMvpPresenter().requestLiveChapter(mCoid, mType);
        }

    }

    @Override
    public void onPullUpLoadMore() {
//        mPage++;
//        mRefreshHelper.setPage(mPage);
    }

    @Override
    public void onClickItem(int operator, Object item) {
        if (operator == 0) {
            return;
        }
        if (operator == GlobalConfig.ClickYype.COURSE_HOUR_CLICK) {//跳转
            Chapter chapter = (Chapter) item;
            if (chapter != null) {
                int play_status = chapter.getPlay_status();
                if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_REPLAY) {//回放 跳转html5
                    String chapter_name = chapter.getChapter_name();
                    int coid = chapter.getCoid();
                    int chid = chapter.getChid();
                    String url = GlobalConfig.HttpUrl.HTML_URL + "coid=" + coid + "&" + "chid=" + chid;
                    Intent intent = new Intent(this, Html5TabActivity.class);
                    intent.putExtra("title", chapter_name);
                    intent.putExtra("url", url);
                    this.startActivity(intent);
                } else if (play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS || play_status == GlobalConfig.ItemPlayStatus.LIVE_STATUS_SOON) {
                    //跳转直播
                    int coid = chapter.getCoid();
                    mChid = chapter.getChid();
                    this.getMvpPresenter().requestGetLive(coid, mChid);
                    mPlayStart = chapter.getPlay_start();
                    mRoomId = chapter.getRoom_id();
                }
            }
        }
    }

    @Override
    public void onLongClickItem(int operator, Object item) {
        if (operator == 0) {
            return;
        }
        if (item != null) {
            Chapter chapter = (Chapter) item;
            if (chapter != null) {
                mDialogHelper.setMessage("是否删除课时");
                mDeleteChid = chapter.getChid();
                mDialogHelper.show();
            }
        }
    }

    public void onClickDialogCancel(View view) {// 注销-取消
        if (null != mDialogHelper) {
            mDialogHelper.dismiss();
        }
        mDeleteChid = 0;
    }

    public void onClickDialogSure(View view) {// 注销-确定
        if (null != mDialogHelper) {
            mDialogHelper.dismiss();
        }
        if (mDeleteChid != 0) {
            this.getMvpPresenter().requestDeleteChapter(mDeleteChid);
        }
    }

    /**
     * 隐藏popupwindow回调
     */
    @Override
    public void onClick() {
        mTitlebarIvIcon.setImageResource(R.drawable.down_small_triangle);
        isOpenPW = !isOpenPW;
    }

//    @OnClick(R.id.titlebar_back_rl_root)
//    public void onClickRoot() {
//        if (!isOpenPW) {
//            mTitlebarIvIcon.setImageResource(R.drawable.down_small_triangle);
//        } else {
//            mTitlebarIvIcon.setImageResource(R.drawable.up_small_triangle);
//            if (mPWHelper != null && mPWHelper.isShowing()) {
//                mPWHelper.dismiss();
//            }
//        }
//        isOpenPW = !isOpenPW;
//    }
}
