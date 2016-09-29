package com.you.edu.live.teacher.presenter.helper;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.view.fragment.RecentLiveFragment;
import com.you.edu.live.teacher.view.fragment.UserCourseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/28 0028.
 */
public class MainFragmentHelper {

    public static final int INDEX_PAGE_USER_COURSE = 0;
    public static final int INDEX_PAGE_RECENT_LIVE = 1;


    private List<String> mTitles;
    private List<Fragment> mFragments;
    private Context mCtx;

    public MainFragmentHelper(Context ctx) {
        if (null == ctx) {
            throw new IllegalArgumentException(" Context cannot be null");
        }
        mFragments = new ArrayList<Fragment>();
        mTitles = new ArrayList<String>();

        this.mCtx = ctx;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    public void setFragments(List<Fragment> mFragments) {
        this.mFragments = mFragments;
    }

    public List<String> getPageTitles() {
        return mTitles;
    }

    public void setPageTitles(List<String> mPageTitles) {
        this.mTitles = mPageTitles;
    }

    /**
     * 初始化Fragment
     */
    public void prepareFragments() {
        UserCourseFragment courseFragment = new UserCourseFragment();
        RecentLiveFragment recentLiveFragment = new RecentLiveFragment();

        this.addFragment(courseFragment, mCtx.getString(R.string.user_course));
        this.addFragment(recentLiveFragment, mCtx.getString(R.string.recent_live));
    }

    public void addFragment(Fragment fragment, String title) {
        if (null == mFragments) {
            mFragments = new ArrayList<Fragment>();
        }
        mFragments.add(fragment);
        if (null == mTitles) {
            mTitles = new ArrayList<String>();
        }
        mTitles.add(title);
        // mTabLayout.addTab(mTabLayout.newTab().setText(title));
    }

    public Fragment removeFragment(int index) {
        try {
            if (null != mTitles && index >= 0 && index < mTitles.size()) {
                mTitles.remove(index);
            }
            if (null != mFragments && index >= 0 && index < mFragments.size()) {
                return mFragments.remove(index);
            }
            // if (null != mTabLayout) {
            // mTabLayout.removeTabAt(index);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 当选中某个页签时
//     *
//     */
//    public void onPageSelected(int index) {
//
//        Fragment fragment = this.getFragment(index);
//        if (null == fragment) {
//            return;
//        }
//        switch (index) {
//            case MainFragmentHelper.INDEX_PAGE_USER_COURSE:// 我的课程
//                ((UserCourseFragment) fragment).onSelected();
//                break;
//            case MainFragmentHelper.INDEX_PAGE_RECENT_LIVE:// 近期直播
//                ((RecentLiveFragment) fragment).onSelected();
//                break;
//
//        }
//    }

    public Fragment getFragment(int index) {
        if (this.isEmpty()) {
            return null;
        }
        int size = mFragments.size();
        if (size < index) {
            return null;
        }
        return mFragments.get(index);
    }

    public boolean isEmpty() {
        return null == mFragments ? true : mFragments.isEmpty();
    }

    public void clear() {
        if (null != mFragments) {
            mFragments.clear();
        }
        if (null != mTitles) {
            mTitles.clear();
        }
    }

    public void destroy() {
        this.clear();
        mFragments = null;
        mTitles = null;
        mCtx = null;
    }
}
