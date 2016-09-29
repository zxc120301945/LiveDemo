package com.you.edu.live.teacher.support.dao;

/**
 * 本地操作回调
 * 
 * @author XingRongJing
 * 
 * @param <T>
 */
public interface IDaoCallback<T> {

	/**
	 * 本地数据处理回调
	 * 
	 * @param out
	 */
	public void onDaoResponse(T out);
}
