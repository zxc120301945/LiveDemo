package com.you.edu.live.teacher.presenter.helper;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

public class ShanRefreshViewHelper<T extends RecyclerViewAdapter.Item>
        implements OnRefreshListener, OnRecyclerViewScrollListener.IScrollToBottomListener {

    private RecyclerViewAdapter<T> mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnRecyclerViewScrollListener mScrollListener;
    private IRefreshListener mRefreshListener;
    private IScrollListener mScrollSwitchListener;// 专用于字列表背景图切换的接口
    // /** 无网时，点击footer **/
    // private IItemClickFooterNoNetwork mItemFooterNoNetworkListener;
    /**
     * 是否正在刷新（下拉或上拉）
     **/
    public boolean mIsRefreshing = false;
    /**
     * 是否下拉刷新
     **/
    public boolean mPullDownRefresh = false;
    /**
     * 是否加载更多
     **/
    private boolean mIsLoadingMore = false;
    /**
     * 是否需要加载更多
     **/
    private boolean mIsNeedLoadingMore = true;
    private Context mCtx;
    /**
     * 是否需要加载更多
     **/
    private boolean mIsFootView = true;

    ShanRefreshViewHelper(SwipeRefreshLayout srLayout,
                          RecyclerView recyclerView, RecyclerViewAdapter<T> adapter,
                          IRefreshListener listener) {
        if (null == recyclerView) {
            throw new IllegalArgumentException("RecyclerView cannot be null");
        }
        if (null == adapter) {
            throw new IllegalArgumentException(
                    "RecyclerViewAdapter<Item> cannot be null");
        }
        // if (null == srLayout) {
        // throw new IllegalArgumentException(
        // "SwipeRefreshLayout cannot be null");
        // }

        this.mSwipeRefreshLayout = srLayout;
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        this.mRefreshListener = listener;
        mCtx = mRecyclerView.getContext();
        this.prepareDatas();
    }

    private void prepareDatas() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setProgressViewOffset(true, -20, 50);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color,
                    R.color.main_bg_color);
        }
        mScrollListener = new OnRecyclerViewScrollListener(this);
        mRecyclerView.addOnScrollListener(mScrollListener);
        // if (null != mAdapter) {
        // mAdapter.setItemClickFooterNoNetwork(mItemFooterNoNetworkListener);
        // }
    }

    public void rebindRefreshViewForFragment(SwipeRefreshLayout srLayout,
                                             RecyclerView recyclerView) {
        this.mSwipeRefreshLayout = srLayout;
        this.mRecyclerView = recyclerView;
        this.prepareDatas();
    }

    private void showToastNoNetwork() {

        AppHelper.getAppHelper().showToast(mCtx,
                mCtx.getResources().getString(R.string.no_network));
    }

    // public void setFooterModeLoading() {
    // if (null == mAdapter) {
    // return;
    // }
    // mAdapter.setFooterMode(FooterMode.MODE_LOADING);
    // this.addFooterLoadingView();
    //
    // }
    //
    // public FooterMode getFooterMode() {
    // if (null == mAdapter) {
    // return null;
    // }
    // return mAdapter.getFooterMode();
    // }
    //
    // public void removeFooter() {
    // if (null != mAdapter) {
    // mAdapter.removeFooter();
    // }
    // }
    //
    // public void setFooterModeNoMore() {
    // if (null == mAdapter) {
    // return;
    // }
    // mAdapter.setFooterMode(FooterMode.MODE_NO_NORE);
    // this.addFooterLoadingView();
    // }
    //
    // public void setFooterModeNoNetwork() {
    // if (null == mAdapter) {
    // return;
    // }
    // mAdapter.setFooterMode(FooterMode.MODE_NO_NETWORK);
    // this.addFooterLoadingView();
    //
    // }

    /**
     * 是否有加载更多的脚布局
     *
     * @return
     */
    public void setIsFootView(boolean isFootView) {
        this.mIsFootView = isFootView;
    }

    public boolean getIsFootView() {
        return mIsFootView;
    }

    public void setRefreshListener(IRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    /**
     * 是否正在刷新（上拉或下拉）
     *
     * @return
     */
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setIsRefreshing(boolean mIsRefreshing) {
        this.mIsRefreshing = mIsRefreshing;
    }

    public boolean isPullDownRefresh() {
        return mPullDownRefresh;
    }

    public void setPullDownRefresh(boolean mPullDownRefresh) {
        this.mPullDownRefresh = mPullDownRefresh;
    }

    public void setIsLoadingMore(boolean isLoadingMore) {
        this.mIsLoadingMore = isLoadingMore;
    }

    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    public boolean isNeedLoadingMore() {
        return mIsNeedLoadingMore;
    }

    public void setIsNeedLoadingMore(boolean mIsNeedLoadingMore) {
        this.mIsNeedLoadingMore = mIsNeedLoadingMore;
    }

    /**
     * 初次进入时自动下拉刷新
     */
    public void onRefreshAuto() {
        if (!AppHelper.getAppHelper().isNetworkEnable(mCtx)) {
            this.showToastNoNetwork();
            return;
        }
        if (this.isRefreshing()) {
            return;
        }
        AppHelper.getAppHelper().getHandler().post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mSwipeRefreshLayout.setRefreshing(true);
                ShanRefreshViewHelper.this.onRefresh();
            }

        });
        // mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(
        // new ViewTreeObserver.OnGlobalLayoutListener() {
        //
        // @Override
        // public void onGlobalLayout() {
        // // TODO Auto-generated method stub
        // mSwipeRefreshLayout.getViewTreeObserver()
        // .removeGlobalOnLayoutListener(this);
        // mSwipeRefreshLayout.setRefreshing(true);
        // ShanRefreshViewHelper.this.onRefresh();
        // }
        // });
    }

    /**
     * 上拉/下拉加载完成时调用，更新UI与重置
     */
    public void onRefreshComplete() {
        this.setIsRefreshing(false);
        if (this.isPullDownRefresh()) {
            mSwipeRefreshLayout.setRefreshing(false);
            this.setPullDownRefresh(false);
        } else {
            if (null != mAdapter) {
                if (this.getIsFootView()) {
                    mAdapter.setFooterView(GlobalConfig.INVALID);
                }
            }
            this.setIsLoadingMore(false);
        }
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        this.setIsRefreshing(true);
        this.setPullDownRefresh(true);
        this.setIsLoadingMore(false);
        if (null != mRefreshListener) {
            mRefreshListener.onPullDownRefresh();
        }
    }

    public void setOnScrollListener(IScrollListener mScrollSwitchListener) {
        this.mScrollSwitchListener = mScrollSwitchListener;
    }

    /**
     * 滚动切换字列表背景图的接口，只用于切换背景
     *
     * @author WanZhiYuan
     */
    public interface IScrollListener {

        public void onScrollToUp();

        public void onScrollToBottom();
    }

    public interface IRefreshListener {

        /**
         * 下拉刷新
         */
        public void onPullDownRefresh();

        /**
         * 上拉加载更多
         */
        public void onPullUpLoadMore();

    }

    @Override
    public void onScrollToBottom() {
        // TODO Auto-generated method stub
        if (this.getIsFootView()) {

            if (this.isRefreshing()) {
                return;
            }
            if (!AppHelper.getAppHelper().isNetworkEnable(mCtx)) {
                // && this.isNeedLoadingMore()) {// 没网且还有更多，则显示网络异常
                this.showToastNoNetwork();
                return;
            }
            // if (null != this.getCoreApp()
            // && !this.getCoreApp().isNetworkConnected()
            // && this.isNeedLoadingMore()) {// 没网且还有更多，则显示网络异常
            // this.setFooterModeNoNetwork();
            // this.addFooterLoadingView();
            // return;
            // }
            if (!this.isNeedLoadingMore()) {
                // this.setFooterModeNoMore();
                // this.addFooterLoadingView();
                return;
            }
            // this.setFooterModeLoading();
            this.addFooterLoadingView();

            this.setIsRefreshing(true);
            this.setPullDownRefresh(false);
            this.setIsLoadingMore(true);
            if (null != mRefreshListener) {
                mRefreshListener.onPullUpLoadMore();
            }
        }
    }

    private void addFooterLoadingView() {
        if (this.getIsFootView()) {
            mAdapter.setFooterView(R.layout.view_item_footer);
            // mAdapter.setFooterViewLoadingResId(R.id.item_footer_ll_loading);
            // mAdapter.setFooterViewNoMoreResId(R.id.item_footer_tv_no_more);
            // mAdapter.setFooterViewNoNetworkResId(R.id.item_footer_tv_no_network);
            if (mAdapter.hasHeader()) {
                mRecyclerView
                        .smoothScrollToPosition(mAdapter.getItemCount() + 1);
            } else {
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        }

    }

    @Override
    public void onScrollToUp() {
        // TODO Auto-generated method stub
        if (null != mScrollSwitchListener) {
            mScrollSwitchListener.onScrollToUp();
        }
    }

    @Override
    public void onScrollBottom() {
        // TODO Auto-generated method stub
        if (this.isRefreshing()) {
            return;
        }
        if (null != mScrollSwitchListener) {
            mScrollSwitchListener.onScrollToBottom();
        }
    }

}
