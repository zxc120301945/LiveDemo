package com.you.edu.live.teacher.view.custom.danmu;

import android.os.AsyncTask;

/**
 * 弹幕代理
 * 
 * @author XingRongJing
 * 
 */
public class DanmuProxy {

	private OnDisappearListener mDisappearListener;
	/** 消失时间 **/
	private long mDuration = 2 * 1000;

	public OnDisappearListener getDisappearListener() {
		return mDisappearListener;
	}

	public void setDisappearListener(OnDisappearListener mDisappearListener) {
		this.mDisappearListener = mDisappearListener;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long mDuration) {
		this.mDuration = mDuration;
	}

	public void sendDanmu(IDanmu danmu) {
		DisappearTask task = new DisappearTask(mDisappearListener, danmu);
		task.execute(mDuration);
	}

	public interface OnDisappearListener {
		public void disappear(IDanmu danmu);
	}

	private class DisappearTask extends AsyncTask<Long, Integer, String> {

		private OnDisappearListener mDisappearListener;
		private IDanmu mDanmu;

		public DisappearTask(OnDisappearListener listener, IDanmu danmu) {
			this.mDisappearListener = listener;
			this.mDanmu = danmu;
		}

		@Override
		protected String doInBackground(Long... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(params[0]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (null != mDisappearListener) {
				mDisappearListener.disappear(mDanmu);
			}
		}
	}

}
