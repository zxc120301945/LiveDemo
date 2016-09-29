package com.you.edu.live.teacher.presenter.helper;

import java.util.List;

public class ShanRefreshDataHelper<T> {

	private static final int MAX_LOAD = 30;
	private static final int PAGE_FIRST = 1;
	private int mPage = PAGE_FIRST;
	private int mMaxLoad = MAX_LOAD;
	/** 是否还有更多（分页加载的依据） **/
	private boolean mHasMore = true;

	ShanRefreshDataHelper() {
	}

	public int getPage() {
		return mPage;
	}

	public void setPage(int mPage) {
		this.mPage = mPage;
	}

	public int getMaxLoad() {
		return mMaxLoad;
	}

	public void setMaxLoad(int mMaxLoad) {
		this.mMaxLoad = mMaxLoad;
	}

	public boolean isHasMore() {
		return mHasMore;
	}

	public void setHasMore(boolean mHasMore) {
		this.mHasMore = mHasMore;
	}

	public void pagePlus() {
		++mPage;
	}

	public void reset() {
		this.setPage(PAGE_FIRST);
		this.setHasMore(false);
	}

	/**
	 * 下拉/上拉完毕调此方法处理数据逻辑
	 * 
	 * @param datas
	 *            此次请求服务器返回的数据集合（非全部数据集合）
	 */
	public void onRefreshComplete(List<T> datas) {
		this.pagePlus();
		if (!this.isEmptyDatas(datas)) {
			// this.setHasMore(true);
			this.setHasMore(datas.size() >= this.getMaxLoad());
		} else {
			this.setHasMore(false);
		}
	}
	
	public boolean isEmptyDatas(List<T> datas) {
		return null == datas ? true : datas.isEmpty();
	}

	public void clearList(List<T> datas) {
		if (null != datas) {
			datas.clear();
		}
	}

}
