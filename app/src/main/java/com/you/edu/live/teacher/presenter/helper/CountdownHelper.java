package com.you.edu.live.teacher.presenter.helper;

import android.os.CountDownTimer;

/**
 * 倒计时助手
 * 
 * @author XingRongJing
 * 
 */
public class CountdownHelper {

	/** 是否正在倒计时 **/
	private boolean mIsCounting = false;

	public CountDownTimer mCountDownTimer;
	private ICountDownListener mListener;

	public CountdownHelper() {
	}

	public ICountDownListener getListener() {
		return mListener;
	}

	public void setListener(ICountDownListener mListener) {
		this.mListener = mListener;
	}

	/**
	 * 开始倒计时
	 * @param millisInFuture
	 * 剩余的时间（毫秒）
	 * @param countDownInterval
	 * 每次更新间隔时间（毫秒，一般是1000毫秒，即1秒）
	 * @param listener
     */
	public void startTimer(long millisInFuture, long countDownInterval,
			ICountDownListener listener) {
		if (this.isCounting()) {
			return;
		}
		this.setListener(listener);
		this.setIsCounting(true);
		mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				if (null != getListener()) {
					getListener().onCountDownTime(millisUntilFinished / 1000);
				}
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				if (null != getListener()) {
					getListener().onCountDownFinish();
				}
				stopTimer();

			}
		};
		mCountDownTimer.start();

	}

	// public void startTimer(long millisInFuture, long countDownInterval) {
	// this.startTimer(millisInFuture, countDownInterval,null);
	// }

	public void stopTimer() {
		if (null != mCountDownTimer) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		this.setIsCounting(false);
	}

	public boolean isCounting() {
		return mIsCounting;
	}

	public void setIsCounting(boolean mIsCounting) {
		this.mIsCounting = mIsCounting;
	}

	public interface ICountDownListener {
		/**
		 * 倒计时时间更新
		 * 
		 * @param seconds
		 * 剩余时间（秒）
		 */
		public void onCountDownTime(long seconds);

		/**
		 * 倒计时完成
		 */
		public void onCountDownFinish();
	}

}
