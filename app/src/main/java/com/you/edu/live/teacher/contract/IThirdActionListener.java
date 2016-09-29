package com.you.edu.live.teacher.contract;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;

/**
 * 第三方回调接口（从子线程到主线程）
 * 
 * @author XingRongJing
 * 
 */
public interface IThirdActionListener {

	/**
	 * 第三方分享/登录成功
	 * 
	 * @param plat
	 * @param arg1
	 * @param info
	 */
	public void onComplete(Platform plat, HashMap<String, Object> info);

	/**
	 * 第三方分享/登录失败
	 * 
	 * @param plat
	 * @param arg1
	 * @param error
	 */
	public void onError(Platform plat, String error);
	
	public void onCancel(Platform plat);
}
