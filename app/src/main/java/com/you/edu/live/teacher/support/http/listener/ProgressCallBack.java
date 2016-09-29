package com.you.edu.live.teacher.support.http.listener;

import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;

/**
 * 
 * 带有加载中的请求(本地/远程)操作的回调
 * @author WanZhiYuan
 *
 * @param <ResultType>
 */
public class ProgressCallBack<ResultType> implements
		Callback.ProgressCallback<ResultType> {

	/**
	 * 成功
	 */
	@Override
	public void onSuccess(ResultType result){}

	/**
	 * 失败
	 */
	@Override
	public void onError(Throwable ex, boolean isOnCallback){}

	/**
	 * 取消and暂停
	 */
	@Override
	public void onCancelled(CancelledException cex){}
	
	/**
	 * 取消and暂停
	 */
	@Override
	public void onFinished(){}

	/**
	 * 开始之前的会调用
	 */
	@Override
	public void onWaiting(){}

	/**
	 * 不知道什么时候调用
	 */
	@Override
	public void onStarted(){}

	/**
	 * 加载中
	 */
	@Override
	public void onLoading(long total, long current, boolean isDownloading){}

}