package com.you.edu.live.teacher.view.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IAppUpdateContract;
import com.you.edu.live.teacher.contract.IMainContract;
import com.you.edu.live.teacher.contract.IRefreshChangeListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.AppUpdate;
import com.you.edu.live.teacher.model.bean.Organ;
import com.you.edu.live.teacher.model.bean.Teacher;
import com.you.edu.live.teacher.model.bean.TeacherInfo;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.SharedPreDao;
import com.you.edu.live.teacher.presenter.AppUpdatePresenter;
import com.you.edu.live.teacher.presenter.MainPresenter;
import com.you.edu.live.teacher.presenter.helper.MainFragmentHelper;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.view.activity.LoginActivity;
import com.you.edu.live.teacher.widget.CropCircleTransformation;
import com.you.edu.live.teacher.widget.DialogHelper;
import com.you.edu.live.teacher.widget.GlideCircleTransform;
import com.you.edu.live.teacher.widget.GlideRoundTransform;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.SystemBarTintManager;
import com.you.edu.live.teacher.widget.adapter.TabAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @Title: 首页
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/28
 * @Time 下午17.04
 * @Version
 */
public class MainActivity extends BaseViewActivity implements IMainContract.IMainView, IAppUpdateContract.IAppUpdateView {


    @BindView(R.id.main_tabs)
    TabLayout mMainTabLayout;
    @BindView(R.id.main_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.user_icon)
    ImageView mUserCover;
    @BindView(R.id.user_nick_name)
    TextView mUserNickName;
    @BindView(R.id.user_unlogin)
    TextView mUserUnlogin;
    @BindView(R.id.course_number)
    TextView mCourseNumber;
    @BindView(R.id.form_you_school)
    TextView mFormOrgan;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    private MainFragmentHelper mFragmentHelper;

    /**
     * 是否退出应用
     **/
    private boolean mDoubleExit = false;
    private boolean isFirst = true;
    private int mCoverWidth;
    private int mCoverHeight;
    private DialogHelper mDialogHelper;
    private MainPresenter mPresenter;
    private ProgresDialogHelper mProgresDialogHelper;
    private Unbinder mUnbinder;
    private int mHeight;
    private AppUpdatePresenter mAppUpdatePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 更改状态栏颜色 只适用于系统版本4.4及以上
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager
                    .setStatusBarTintResource(R.drawable.main_title_gradual_chang_bge);// 通知栏所需颜色
        }
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        this.getMvpPresenter().attachView(this);
        this.getAppUpdatePresenter().attachView(this);
        this.prepareData();
        this.prepareView();
    }

    private void prepareData() {

        int screenWidth = DeviceUtils.getScreenWidth(this);
        int screenHeight = DeviceUtils.getScreenHeight(this);
        mHeight = screenHeight / 3;
//        ViewGroup.LayoutParams params = mMainTabLayout.getLayoutParams();
//        params.width = (int) (screenWidth /1.5f);
//        mMainTabLayout.setLayoutParams(params);
//        mCoverWidth = screenWidth / 4;
//        mCoverWidth = screenWidth;
//        mCoverHeight = (int) (screenWidth * 9) / 16;
        mCoverWidth = mCoverHeight = this.getResources()
                .getDimensionPixelSize(R.dimen.avator_thumb_width_small);


        mProgresDialogHelper = new ProgresDialogHelper(this);
        mDialogHelper = new DialogHelper(this);

        if (isFirst) {
            this.getMvpPresenter().requestCache();
            isFirst = false;
        }
        if (this.isNetworkEnable()) {// wifi连接，则检查app更新
            this.getAppUpdatePresenter().requestAppUpdateInfo(this);
        }
    }

    private void prepareView() {

//        ViewGroup.LayoutParams params = mAppBar.getLayoutParams();
//        params.height = mHeight;
//        params.width = AppBarLayout.LayoutParams.MATCH_PARENT;
//        mAppBar.setLayoutParams(params);

        //初始化我的课程和近期直播
        this.getFragmentsHelper().prepareFragments();
        this.resetViewPageFragments();

        mDialogHelper.setMessage(this.getString(R.string.exit_sign_on));
        mDialogHelper.setBtnSure(this.getString(R.string.logout));

//        ViewGroup.LayoutParams params = mUserCover.getLayoutParams();
//        params.width = mCoverWidth;
//        params.height = mCoverHeight;
//        mUserCover.setLayoutParams(params);
    }

    private void setView(TeacherInfo info) {

        if (info != null) {
            Teacher user = info.getUser();
            if (user != null) {
                String photo = user.getPhoto();
                if (!TextUtils.isEmpty(photo)) {
                    Glide.with(this).load(photo)
                            .override(mCoverWidth, mCoverHeight).centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .bitmapTransform(
                                    new CropCircleTransformation(Glide.get(
                                            this).getBitmapPool())).placeholder(R.drawable.main_head_portrait_nomal)
                            .error(R.drawable.main_head_portrait_nomal)
                            .into(mUserCover);
                }
                mUserNickName.setText(user.getNick_name());
            }
            Organ organ = info.getOrgan();
            if (organ != null) {
                mFormOrgan.setText(organ.getOrgan_name());
            }
            mCourseNumber.setText("发布了" + info.getInsale_cnt() + "个课程");

        }
    }

    /**
     * 登出
     */
    @OnClick(R.id.user_unlogin)
    public void onClickUnlogin() {
        if (AppHelper.getAppHelper().isLogin(this)) {
            if (mDialogHelper != null) {
                mDialogHelper.show();
            }
        }
    }

    public void onClickDialogCancel(View view) {// 注销-取消
        if (null != mDialogHelper) {
            mDialogHelper.dismiss();
        }
    }

    public void onClickDialogSure(View view) {// 注销-确定
        if (null != mDialogHelper) {
            mDialogHelper.dismiss();
        }
        AppHelper.getAppHelper().logout(this);
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    public MainFragmentHelper getFragmentsHelper() {
        if (mFragmentHelper == null) {
            mFragmentHelper = new MainFragmentHelper(this);
        }
        return mFragmentHelper;
    }

    private void resetViewPageFragments() {
        List<Fragment> mFragments = this.getFragmentsHelper().getFragments();
        List<String> mPageTitles = this.getFragmentsHelper().getPageTitles();
        TabAdapter mPageAdapter = new TabAdapter(
                this.getSupportFragmentManager(), mFragments, mPageTitles);
        mViewPager.setAdapter(mPageAdapter);
        mMainTabLayout.setupWithViewPager(mViewPager);
        mMainTabLayout.setTabsFromPagerAdapter(mPageAdapter);
        mMainTabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    protected void onDestroy() {
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDoubleExit) {
            super.onBackPressed();
            AppHelper.getAppHelper().exit();
        }
        // 两秒之内再次按返回键 退出应用
        mDoubleExit = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mDoubleExit = false;
            }
        }, 2000);
        AppHelper.getAppHelper().showToast(this, this.getString(R.string.exit));
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
    public void onTeacherInfoSuccess(TeacherInfo info) {
        if (info != null) {
            AppHelper.getAppHelper().setTeacher(info);
            this.setView(info);
        }
    }

    @Override
    public void onCacheSuccess(TeacherInfo info) {
        if (info != null) {
            AppHelper.getAppHelper().setTeacher(info);
        }
        this.setView(info);
        this.getMvpPresenter().requestTeacherInfo(true, true);
    }

    @Override
    public void showError(String error) {
        this.showToast(error);
    }

    @Override
    public MainPresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new MainPresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(this));
        }
        return mPresenter;
    }

    private AppUpdatePresenter getAppUpdatePresenter() {
        if (mAppUpdatePresenter == null) {
            mAppUpdatePresenter = AppHelper.getAppHelper().getAppUpdateKeeper(this)
                    .getPresenter(this);
        }
        return mAppUpdatePresenter;
    }

    @Override
    public void onSuccess(AppUpdate appInfo) {
        if (appInfo == null) {
            return;
        }
        String appVersion = AppHelper.getAppHelper().getAppVersion(this);
        String currVersion = appInfo.getCur_version();
        boolean isNeedUpdate = AppHelper.getAppHelper()
                .getAppUpdateKeeper(this).isNeedUpdate(appVersion, currVersion);
        if (isNeedUpdate) {// 需要提醒App更新
            boolean isForce = AppHelper.getAppHelper().getAppUpdateKeeper(this)
                    .isForceUpdate(appVersion, appInfo.getMin_version());
            AppHelper.getAppHelper().getAppUpdateKeeper(this)
                    .setIsForceAppUpdate(isForce);
            if (isForce) {// 强制升级，则直接忽略提醒次数
                AppHelper
                        .getAppHelper()
                        .getAppUpdateKeeper(this)
                        .showAppUpdateDialog(this, appInfo.getDown_url(),
                                appInfo.getUp_desc());
                return;
            }
            AppHelper
                    .getAppHelper()
                    .getAppUpdateKeeper(this)
                    .showAppUpdateDialog(this, appInfo.getDown_url(),
                            appInfo.getUp_desc());
            return;
        }
        int tipsCount = SharedPreDao.getInt(this, currVersion);
        if (tipsCount >= appInfo.getAlert_cnt()) {// 提醒次数已到，则直接忽略
            return;
        }
        ++tipsCount;
        SharedPreDao.saveInt(this, currVersion, tipsCount);
    }

    @Override
    public void showError(int operator, String error) {

    }

    public void onClickUpgrade(View view) {// 更新app
        String downUrl = view.getTag().toString();
        AppHelper.getAppHelper().getAppUpdateKeeper(this)
                .onClickDownloadApk(this, downUrl);
        AppHelper.getAppHelper().getAppUpdateKeeper(this)
                .dismissAppUpdateDialog();
        if (AppHelper.getAppHelper().getAppUpdateKeeper(this)
                .isForceAppUpdate()) {
            AppHelper.getAppHelper().getAppUpdateKeeper(this)
                    .showForceUpdateDownloadProDialog(this, downUrl);
        }
    }

    public void onClickUpgradeCancel(View view) {// 取消更新
        AppHelper.getAppHelper().getAppUpdateKeeper(this)
                .dismissAppUpdateDialog();
    }

    public void onClickForceUpdateRedownload(View view) {// 强制升级失败时,点击重新下载
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        String downUrl = view.getTag().toString();
        AppHelper.getAppHelper().getAppUpdateKeeper(this)
                .onClickDownloadApk(this, downUrl);

    }
}
