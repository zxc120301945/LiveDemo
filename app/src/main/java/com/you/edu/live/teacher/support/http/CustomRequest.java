package com.you.edu.live.teacher.support.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import  com.you.edu.live.teacher.AppHelper;

/**
 * 自定义Volley请求
 *
 * @author XingRongJing
 */
public class CustomRequest extends Request<RespOut> {

    private static final String TAG = "ZjCustomRequest";
    private IHttpCallback<RespOut> mListener;
    private RequestTag mReqParam;

    private Map<String, String> mHeaders;
    private Map<String, String> mParams;

    public CustomRequest(int method, String url,
                         IHttpCallback<RespOut> listener, RequestTag param) {
        super(method, url, null);
        // TODO Auto-generated constructor stub
        this.mListener = listener;
        this.mReqParam = param;
        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->sendReq：" + url);
        }
    }

    public CustomRequest(int method, String url,
                         IHttpCallback<RespOut> listener, RequestTag param,
                         Map<String, String> params, Map<String, String> mHeaders) {
        this(method, url, listener, param);
        this.mParams = params;
        this.mHeaders = mHeaders;
        // this.setHeaders(mHeaders);
    }


    @Override
    protected Response<RespOut> parseNetworkResponse(NetworkResponse response) {
        // TODO Auto-generated method stub
        String parsed;
        if (this.isCanceled()) {// 用户取消了，则不再回调
            return null;
        }
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->resp：" + parsed);
        }
        String cookie = null;
        if (null != mReqParam && mReqParam.isGetCookie
                && null != response.headers) {
            cookie = response.headers.get("Set-Cookie");
        }
        RespOut out = new RespOut(mReqParam);
        out.isSuccess = true;
        out.resp = parsed;
        out.cookie = cookie;
        return Response.success(out,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public void deliverError(VolleyError error) {// Main Thread
        // TODO Auto-generated method stub
        if (this.isCanceled()) {// 用户取消了，则不再回调
            return;
        }
        RespOut out = new RespOut(mReqParam);
        out.resp = error.getMessage();
        if (AppHelper.mIsLog) {
            NetworkResponse errorResp = error.networkResponse;
            String errorMsg = error.getMessage();
            Log.d(TAG, "shan-->errorCode："
                    + ((null == errorResp) ? -1 : errorResp.statusCode));
            Log.d(TAG, "shan-->error：" + errorMsg);
        }
        if (mListener != null) {
            mListener.onResponseError(out);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // TODO Auto-generated method stub
        if (null != mHeaders && !mHeaders.isEmpty()) {
            return mHeaders;
        }
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        return mParams;
    }

    @Override
    protected void deliverResponse(RespOut response) {// Main Thread
        // TODO Auto-generated method stub
        if (null != mListener) {
            mListener.onResponse(response);
        }
    }

}
