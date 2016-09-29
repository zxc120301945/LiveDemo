package com.you.edu.live.teacher.widget.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class TabAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments;
    List<String> mTitles;

    public TabAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.mFragments = fragments;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return null == mFragments ? null : mFragments.get(position);
    }

    @Override
    public int getCount() {
        return null == mFragments ? 0 : mFragments.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        return null == mTitles ? super.getPageTitle(position) : mTitles
                .get(position);
    }

}
