package com.you.edu.live.teacher.support.http;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Http基本请求Api-引用Volley
 *
 * @author XingRongJing
 */
public class HttpApi implements IHttpApi {

    private static final String TAG = "HttpApi";
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private static final int SOCKET_TIMEOUT = 15 * 1000;
    private static final int SOCKET_TIMEOUT_SHORT = 5 * 1000;

    private static HttpApi mHttpApi;
    private RequestQueue mReqQueue;
    private String mUserAgent;
    private String mCookie;

    private HttpApi(String userAgent) {

        this.mUserAgent = userAgent;
        mReqQueue = CustomVolley.newRequestQueue();
    }

    public static IHttpApi getHttpApi(String userAgent) {
        if (null == mHttpApi) {
            mHttpApi = new HttpApi(userAgent);
        }
        return mHttpApi;
    }

    public static IHttpApi getHttpApi() {
        return HttpApi.getHttpApi(null);
    }

    @Override
    public void request(String url, Map<String, String> params,
                        IHttpCallback<RespOut> listener, RequestTag tag) {
        this.request(Method.POST, url, params, listener, tag);
    }

    @Override
    public void request(String url, Map<String, String> params,
                        Map<String, String> headers, IHttpCallback<RespOut> listener,
                        RequestTag tag) {
        this.request(Method.POST, url, params, headers, listener, tag);
    }

    @Override
    public void request(int method, String url, Map<String, String> params,
                        IHttpCallback<RespOut> listener, RequestTag tag) {
        this.request(method, url, params, null, listener, tag);
    }

    @Override
    public void request(int method, String url, Map<String, String> params,
                        Map<String, String> headers, IHttpCallback<RespOut> listener,
                        RequestTag tag) {
        if (null == mReqQueue) {
            throw new IllegalStateException("RequestQueue cannot be null");
        }
//		if (!(url.startsWith(GlobalConfig.HttpUrl.PLAY_SERVICE_GET_JSON) || url
//				.startsWith(GlobalConfig.HttpUrl.URL_PLAYLOG_PUSH))) {// 排除播放服务url，否则会错误
//		}
        if(null!=tag&&tag.isAutoSetHeaders){
            headers = this.getHeaders(headers);
        }
        CustomRequest req = null;
        switch (method) {
            case Method.GET:
                String realUrl = null;
                if (null != params) {
                    realUrl = url + encodeParameters(params);
                } else {
                    realUrl = url;
                }
                req = new CustomRequest(method, realUrl, listener, tag, null,
                        headers);
                break;
            default:
                req = new CustomRequest(method, url, listener, tag, params, headers);
                break;
        }
        // 解决多次请求的问题，但超时时间会较长(见http://blog.csdn.net/lonewolf521125/article/details/45477187)
        // req.setRetryPolicy(new
        // DefaultRetryPolicy(SOCKET_TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 1、自定义超时 2、避免重试导致多次请求
//		if (url.startsWith(GlobalConfig.HttpUrl.URL_MAIN_SPECIAL_SOURCE)) {// 闪屏预加载，超时设置短些
//			req.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT_SHORT, 0,
//					0));
//		} else {
//        req.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT, 0, 0));
//		}
        req.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT, 0, 0));
        req.setTag(tag.tag);

        mReqQueue.add(req);
    }

    @Override
    public void cancelAll(final Object tag) {
        if (null != mReqQueue) {
            mReqQueue.cancelAll(new RequestFilter() {

                @Override
                public boolean apply(Request<?> request) {
                    // TODO Auto-generated method stub
                    Object oldTag = request.getTag();
                    if (oldTag instanceof Integer && tag instanceof Integer) {
                        int temp1 = (Integer) oldTag;
                        int temp2 = (Integer) tag;
                        return temp1 == temp2;
                    }
                    return oldTag == tag;
                }
            });
        }
    }

    /**
     * 构建带UserAgent和Cookie的Headers，有则用之，无则建之
     *
     * @param headers
     * @return
     */
    private Map<String, String> getHeaders(Map<String, String> headers) {
        if (null == headers) {
            headers = new HashMap<>();
            if (!TextUtils.isEmpty(this.getUserAgent())) {
                headers.put(KEY_USER_AGENT, this.getUserAgent());
            }
            if (!TextUtils.isEmpty(this.getCookie())) {
                headers.put(KEY_COOKIE, this.getCookie());
            }
        } else {
            if (!headers.containsKey(KEY_USER_AGENT)
                    && !TextUtils.isEmpty(this.getUserAgent())) {
                headers.put(KEY_USER_AGENT, this.getUserAgent());
            }
            if (!headers.containsKey(KEY_COOKIE)
                    && !TextUtils.isEmpty(this.getCookie())) {
                headers.put(KEY_COOKIE, this.getCookie());
            }
        }
        return headers;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded
     * encoded string. Default-encode: UTF-8
     */
    public String encodeParameters(Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return "";
        }
        StringBuilder encodedParams = new StringBuilder();
        encodedParams.append("?");
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!TextUtils.isEmpty(key)) {
                    encodedParams.append(URLEncoder.encode(key,
                            DEFAULT_PARAMS_ENCODING));
                    encodedParams.append("=");
                }
                if (!TextUtils.isEmpty(value)) {
                    encodedParams.append(URLEncoder.encode(value,
                            DEFAULT_PARAMS_ENCODING));
                    encodedParams.append("&");
                }
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            Log.e(TAG, "shan-->encodeParameters() " + uee.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "shan-->encodeParameters() " + e.getMessage());
        }
        return "";
    }

    @Override
    public String getUserAgent() {
        return this.mUserAgent;
    }

    @Override
    public void setUserAgent(String userAgent) {
        // TODO Auto-generated method stub
        this.mUserAgent = userAgent;
    }

    @Override
    public void setCookie(String cookie) {
        // TODO Auto-generated method stub
        this.mCookie = cookie;
    }

    @Override
    public String getCookie() {
        // TODO Auto-generated method stub
        return this.mCookie;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        if (null != mReqQueue) {
            mReqQueue.stop();
            mReqQueue = null;
        }
    }

}
