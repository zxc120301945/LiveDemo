package com.you.edu.live.teacher.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.dao.SharedPreDao;
import com.you.edu.live.teacher.view.BaseViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Title: AppGuideActivity
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/28
 * @Time 上午11:42
 * @Version
 */
public class AppGuideActivity extends BaseViewActivity implements ViewPager.OnPageChangeListener {

    private static final int[] pics = {R.drawable.ic_launcher,
            R.drawable.ic_launcher};

    @BindView(R.id.user_guide_btn_skip)
    Button mBtnStart;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.user_guide_ll_point)
    LinearLayout mLlPointContainer;
    @BindView(R.id.user_guide_iv_1)
    ImageView mUserGuideIvOne;

    private int mLastIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_guide);
        ButterKnife.bind(this);
        this.initData();
        this.initView();
    }

    private void initData() {
        SharedPreDao.saveBoolean(this,
                GlobalConfig.SharedPreferenceDao.KEY_USER_GUIDE, true);
    }

    private void initView() {
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        List<LinearLayout> views = new ArrayList<LinearLayout>();
        for (int i = 0; i < pics.length; i++) {
            LinearLayout iv = new LinearLayout(this);
            iv.setLayoutParams(mParams);
            iv.setBackgroundResource(pics[i]);
            views.add(iv);
        }

        GuideAdapter adapter = new GuideAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

        int size = mLlPointContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            if (0 == i) {
                mLlPointContainer.getChildAt(0).setEnabled(true);
            } else {
                mLlPointContainer.getChildAt(i).setEnabled(false);
            }
        }
    }

    /**
     * 跳转到登录页面按钮
     */
    @OnClick(R.id.user_guide_btn_skip)
    public void onClickSkip() {
        this.startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isFirst",true);
        this.startActivity(intent);
    }

    /**
     * viewpager改变监听器
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        this.setCurDot(position);
        if (position >= pics.length - 1) {
            mBtnStart.setVisibility(View.VISIBLE);
            mLlPointContainer.setVisibility(View.GONE);
        } else {
            mBtnStart.setVisibility(View.GONE);
            mLlPointContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || mLastIndex == positon) {
            return;
        }
        mLlPointContainer.getChildAt(positon).setEnabled(true);
        mLlPointContainer.getChildAt(mLastIndex).setEnabled(false);
        mLastIndex = positon;
    }


    public class GuideAdapter extends PagerAdapter {

        private List<LinearLayout> views;

        public GuideAdapter(List<LinearLayout> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void startUpdate(View container) {
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public Parcelable saveState() {
            return super.saveState();
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }
    }
}
