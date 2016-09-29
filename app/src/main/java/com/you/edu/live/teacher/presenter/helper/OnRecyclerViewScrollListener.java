package com.you.edu.live.teacher.presenter.helper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

public class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public static enum layoutManagerType {
        LINEAR_LAYOUT, GRID_LAYOUT, STAGGERED_GRID_LAYOUT
    }

    protected layoutManagerType mLayoutManagerType;
    private int[] mLastPosition;
    private int mLastVisibleItemPosition;
    private int mCurrentScrollState = 0;

    // private boolean mIsLoadingMore = false;
    // private boolean mIsRefreshing = false;

    private IScrollToBottomListener mScollToBottomListener;

    public OnRecyclerViewScrollListener(IScrollToBottomListener listener) {
        this.mScollToBottomListener = listener;
        // this.mIsRefreshing = isRefreshing;

    }

    // public boolean isLoadingMore() {
    // return mIsLoadingMore;
    // }
    //
    // public void setLoadingMore(boolean loadingMore) {
    // mIsLoadingMore = loadingMore;
    // }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        // TODO Auto-generated method stub
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView
                .getLayoutManager();
        if (mLayoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                mLayoutManagerType = layoutManagerType.LINEAR_LAYOUT;
            } else if (layoutManager instanceof GridLayoutManager) {
                mLayoutManagerType = layoutManagerType.GRID_LAYOUT;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutManagerType = layoutManagerType.STAGGERED_GRID_LAYOUT;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (mLayoutManagerType) {
            case LINEAR_LAYOUT:
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
                break;
            case GRID_LAYOUT:
                mLastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID_LAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (mLastPosition == null) {
                    mLastPosition = new int[staggeredGridLayoutManager
                            .getSpanCount()];
                }
                staggeredGridLayoutManager
                        .findLastVisibleItemPositions(mLastPosition);
                mLastVisibleItemPosition = findMax(mLastPosition);
                break;
            default:
                break;
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // TODO Auto-generated method stub
        super.onScrollStateChanged(recyclerView, newState);

        mCurrentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView
                .getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        if (visibleItemCount > 0
                && mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE
                && mLastVisibleItemPosition >= totalItemCount - 1) {// 滚动到底
            if (null != mScollToBottomListener) {
                mScollToBottomListener.onScrollToBottom();
                mScollToBottomListener.onScrollBottom();
            }
            // if (!isLoadingMore()&&!mIsRefreshing) {
            // // mIsLoadingMore = true;
            // this.setLoadingMore(true);
            // // onStart();
            // // onLoadMore();
            // if(null!=mLoadListener){
            // // mLoadListener.onLoadMorePrepare();
            // mLoadListener.onLoadMore();
            // }
            // }
        } else if (visibleItemCount > 0
                && mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE
                && mLastVisibleItemPosition <= totalItemCount - 1) {//向上滚动出最后一行或者没有滚动到最后一行
            if (null != mScollToBottomListener) {
                mScollToBottomListener.onScrollToUp();
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public interface IScrollToBottomListener {
        /**
         * 滚动到底时调用（可处理FooterLoadingView以及加载数据）
         */
        public void onScrollToBottom();

        /**
         * 向上滑动时调用(用来处理背景图的切换-只用于字列表)
         */
        public void onScrollToUp();

        /**
         * 向下滑动到最后一个调用(用来处理背景图的切换-只用于字列表)
         */
        public void onScrollBottom();
    }
}
