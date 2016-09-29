package com.you.edu.live.teacher.presenter.helper;



import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.support.http.model.Download;
import com.you.edu.live.teacher.utils.CommonUtil;


public class AppForceUpdateViewHelper {

	private View mRootView;
	private TextView mTvFilename, mTvSize, mTvProgress;
	private ProgressBar mProBar;
	private boolean isError = false;

	public AppForceUpdateViewHelper(View root) {
		this.mRootView = root;
		if (null == mRootView) {
			throw new IllegalArgumentException("Root view cannot be null");
		}
		mRootView.setBackgroundResource(R.color.translucent);
		mTvFilename = (TextView) mRootView
				.findViewById(R.id.download_notify_tv_filename);
		mTvSize = (TextView) mRootView
				.findViewById(R.id.download_notify_tv_size);
		mTvProgress = (TextView) mRootView
				.findViewById(R.id.download_notify_tv_progress);
		mProBar = (ProgressBar) mRootView
				.findViewById(R.id.download_notify_probar_progress);
	}
	
	public void setClickable(boolean enable){
		if(null!=mRootView){
			mRootView.setClickable(enable);
		}
	}

	public void setTextPercent(int percent) {
		if (null != mTvProgress) {
			mTvProgress.setText(percent + "%");
		}
	}

	public void setProgressBar(int percent) {
		if (null != mProBar) {
			mProBar.setProgress(percent);
		}
	}

	public void setTextSize(long currLength, long totalLength) {
		if (null != mTvSize) {
			mTvSize.setText(CommonUtil.formetFileSize(currLength) + "/"
					+ CommonUtil.formetFileSize(totalLength));
		}
	}

	public void setTextState(String stateMsg) {
		if (null != mTvSize) {
			mTvSize.setText(stateMsg);
		}
	}

	public void setTextFilename(String filename) {
		if (null != mTvFilename) {
			mTvFilename.setText(filename);
		}
	}

	public void updateProgress(Download download) {
		int state = download.getState();
		long currSize = download.getCurrLength();
		long totalSize = download.getLength();
		this.setTextFilename(download.getResName());
		switch (state) {
		case Download.STATE_DOWNLOADING:
			int percent = (int) ((float) currSize / totalSize * 100);
			if (percent >= 0 && percent <= 100) {
				this.setTextPercent(percent);
				this.setProgressBar(percent);
			}
			this.setTextSize(currSize, totalSize);
			break;
		case Download.STATE_COMPLETE:
			this.setTextState("下载完成");
			break;
		case Download.STATE_ERROR:
			this.setTextState("下载失败，点击重试");
			break;
		}

	}
}
