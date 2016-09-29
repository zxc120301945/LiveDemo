package com.you.edu.live.teacher.support.http;

/**
 * 请求标记
 * 
 * @author XingRongJing
 * 
 */
public class RequestTag {

	private static final int INVALID = -1;
	/** 请求宿主的标记--主要用于取消请求 **/
	public int tag = INVALID;
	/** 请求操作符--用于区别操作类型 **/
	public int operator = INVALID;
	/** 缓存Url-如需缓存，则加上，否则为空 **/
	public String cacheUrl;
	/** 是否需要获取Cookie **/
	public boolean isGetCookie = false;
	/** 是否需要自动设置头信息（如UserAgent与cookie） **/
	public boolean isAutoSetHeaders = true;

	public RequestTag(int tag, int operator, String cacheUrl) {
		this(tag, operator);
		this.cacheUrl = cacheUrl;
	}

	public RequestTag(int tag, int operator) {
		this.tag = tag;
		this.operator = operator;
	}

	public RequestTag(int hashCode) {
		this(hashCode, INVALID);
	}

}
