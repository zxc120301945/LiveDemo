package com.you.edu.live.teacher.support.http;

import com.android.volley.Response;

/**
 * 每个请求(本地/远程)操作的回调接口
 *
 * @author XingRongJing
 */
public interface IHttpCallback<T> extends Response.Listener<T> {

    public void onResponseError(T out);

}
