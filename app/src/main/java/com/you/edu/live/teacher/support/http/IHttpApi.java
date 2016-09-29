package com.you.edu.live.teacher.support.http;

import java.util.Map;

public interface IHttpApi {
	public static final String KEY_USER_AGENT = "User-Agent";
	public static final String KEY_COOKIE = "Cookie";

	public void request(String url, Map<String, String> params,
						IHttpCallback<RespOut> listener, RequestTag tag);

	public void request(String url, Map<String, String> params,
						Map<String, String> headers, IHttpCallback<RespOut> listener,
						RequestTag tag);

	public void request(int method, String url, Map<String, String> params,
						IHttpCallback<RespOut> listener, RequestTag tag);

	public void request(int method, String url, Map<String, String> params,
						Map<String, String> headers, IHttpCallback<RespOut> listener,
						RequestTag tag);

	/**
	 * 取消请求（根据tag）
	 * 
	 * @param tag
	 */
	public void cancelAll(Object tag);

	public void setUserAgent(String userAgent);

	public String getUserAgent();

	public void setCookie(String cookie);

	public String getCookie();
	
	public void destroy();
}
