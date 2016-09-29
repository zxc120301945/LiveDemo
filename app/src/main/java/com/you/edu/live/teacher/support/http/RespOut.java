package com.you.edu.live.teacher.support.http;

public class RespOut {

	public RespOut(RequestTag param) {
		this.param = param;
	}

	public RequestTag param;
	/** 请求结果-json字符串 **/
	public String resp;
	/** 请求结果-对象，resp为空时有效 **/
	public Object result;
	/** 请求是否成功 **/
	public boolean isSuccess = false;
	/** Cookie信息 **/
	public String cookie;
}
