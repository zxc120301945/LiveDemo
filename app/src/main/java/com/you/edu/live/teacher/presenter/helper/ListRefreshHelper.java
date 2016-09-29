package com.you.edu.live.teacher.presenter.helper;

import java.util.List;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;


/**
 * 列表数据刷新助手
 *
 * @param <T> 数据Bean类型
 * @author XingRongJing
 */
public class ListRefreshHelper<T extends RecyclerViewAdapter.Item> {
    private ShanRefreshDataHelper<T> mDataHelper;
    private ShanRefreshViewHelper<T> mViewHelper;

    public ListRefreshHelper(SwipeRefreshLayout srLayout,
                             RecyclerView recyclerView, RecyclerViewAdapter<T> adapter,
                             ShanRefreshViewHelper.IRefreshListener listener) {
        mViewHelper = new ShanRefreshViewHelper<T>(srLayout, recyclerView,
                adapter, listener);
        mDataHelper = new ShanRefreshDataHelper<T>();
    }

    public void rebindRefreshViewForFragment(SwipeRefreshLayout srLayout,
                                             RecyclerView recyclerView) {
        if (null != mViewHelper) {
            mViewHelper.rebindRefreshViewForFragment(srLayout, recyclerView);
        }
    }

    /**
     * 是否有加载更多的脚布局
     *
     * @return
     */
    public void setIsFootView(boolean isFootView) {
        if (!this.isViewHelperNull()) {
            mViewHelper.setIsFootView(isFootView);
        }
    }

    private boolean isDataHelperNull() {
        return null == mDataHelper;
    }

    private boolean isViewHelperNull() {
        return null == mViewHelper;
    }

    public void setRefreshListener(ShanRefreshViewHelper.IRefreshListener listener) {
        if (!this.isViewHelperNull()) {
            mViewHelper.setRefreshListener(listener);
        }
    }

    public void setOnScrollListener(ShanRefreshViewHelper.IScrollListener mScrollSwitchListener) {
        if (!this.isViewHelperNull()) {
            mViewHelper.setOnScrollListener(mScrollSwitchListener);
        }
    }

    public boolean isPullDownRefresh() {
        if (!this.isViewHelperNull()) {
            return mViewHelper.isPullDownRefresh();
        }
        return false;
    }

    public void setIsRefreshing(boolean isRefreshing) {
        if (!this.isViewHelperNull()) {
            mViewHelper.setIsRefreshing(isRefreshing);
        }
    }

    public boolean isRefreshing() {
        if (!this.isViewHelperNull()) {
            return mViewHelper.isRefreshing();
        }
        return false;
    }

    /**
     * 初次进入时自动下拉刷新
     */
    public void onRefreshAuto() {
        if (!this.isViewHelperNull()) {
            mViewHelper.onRefreshAuto();
        }
    }

    // public void setFooterModeLoading() {
    // if (this.isViewHelperNull()) {
    // return;
    // }
    // mViewHelper.setFooterModeLoading();
    //
    // }
    //
    // public void removeFooter() {
    // if (this.isViewHelperNull()) {
    // return;
    // }
    // mViewHelper.removeFooter();
    // }
    //
    // public void setFooterModeNoMore() {
    // if (this.isViewHelperNull()) {
    // return;
    // }
    // mViewHelper.setFooterModeNoMore();
    // }
    //
    // public void setFooterModeNoNetwork() {
    // if (this.isViewHelperNull()) {
    // return;
    // }
    // mViewHelper.setFooterModeNoNetwork();
    //
    // }

    /**
     * 当请求列表完成时调用(仅限成功时，处理数据)
     *
     * @param datas
     */
    public void onRefreshDatasComplete(List<T> datas) {
        if (!this.isDataHelperNull()) {
            mDataHelper.onRefreshComplete(datas);
            this.setHasMore(mDataHelper.isHasMore());
        }

    }

    /**
     * 当请求列表完成时调用(处理View)
     *
     * @param isSuccess 是否请求成功
     */
    public void onRefreshViewComplete(boolean isSuccess) {
        if (!this.isViewHelperNull()) {
            mViewHelper.setIsNeedLoadingMore(null == mDataHelper ? false
                    : mDataHelper.isHasMore());
            // if (!this.isPullDownRefresh()) {// 加载更多且失败，则显示点击加载更多
            // if (!isSuccess) {
            // this.setFooterModeNoNetwork();
            // }
            // }
            mViewHelper.onRefreshComplete();
        }
    }

    public boolean isHasMore() {
        if (this.isDataHelperNull()) {
            return false;
        }
        return mDataHelper.isHasMore();
    }

    public void setHasMore(boolean hasMore) {
        if (this.isDataHelperNull()) {
            return;
        }
        mDataHelper.setHasMore(hasMore);
        mViewHelper.setIsNeedLoadingMore(hasMore);
    }

    public int getPage() {
        if (this.isDataHelperNull()) {
            return 0;
        }
        return mDataHelper.getPage();
    }

    public void setPage(int mPage) {
        if (this.isDataHelperNull()) {
            return;
        }
        mDataHelper.setPage(mPage);
    }

    public int getMaxLoad() {
        if (this.isDataHelperNull()) {
            return 0;
        }
        return mDataHelper.getMaxLoad();
    }

    public void setMaxLoad(int mMaxLoad) {
        if (this.isDataHelperNull()) {
            return;
        }
        mDataHelper.setMaxLoad(mMaxLoad);
    }

    public void pagePlus() {
        if (!this.isDataHelperNull()) {
            mDataHelper.pagePlus();
        }
    }

    public void reset() {
        if (!this.isDataHelperNull()) {
            mDataHelper.reset();
        }

    }

    public boolean isEmptyDatas(List<T> datas) {
        if (!this.isDataHelperNull()) {
            return mDataHelper.isEmptyDatas(datas);
        }
        return true;
    }

    public void clearList(List<T> datas) {
        if (!this.isDataHelperNull() && null != datas) {
            datas.clear();
        }
    }
}
