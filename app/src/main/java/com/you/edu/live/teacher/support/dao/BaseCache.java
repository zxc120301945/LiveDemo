package com.you.edu.live.teacher.support.dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

import com.you.edu.live.teacher.AppHelper;


public class BaseCache {

	private ExecutorService mExecutors;
	private Handler mHandler;

	public BaseCache(ExecutorService mExecutors, Handler mHandler) {
		this.setExecutors(mExecutors);
		this.setHandler(mHandler);
		this.getHandler();
		this.getExecutors();
	}

	public Handler getHandler() {
		if (null == mHandler) {
			mHandler = new Handler();
		}
		return mHandler;
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public ExecutorService getExecutors() {
		if (null == mExecutors) {
			mExecutors = Executors
					.newFixedThreadPool(AppHelper.CORE_THRED_POOL_SIZE);
		}
		return mExecutors;
	}

	public void setExecutors(ExecutorService mExecutors) {
		this.mExecutors = mExecutors;
	}

}
