package com.you.edu.live.teacher;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

/**
 * Http配置信息管家
 * 
 * @author XingRongJing
 * 
 */
public class HttpConfigKeeper {

	private Context mCtx;
	private String mUserAgent;

	private static HttpConfigKeeper mHttpKeeper;

	static HttpConfigKeeper getHttpConfigKeeper(Context ctx) {
		if (null == mHttpKeeper) {
			mHttpKeeper = new HttpConfigKeeper(ctx);
		}
		return mHttpKeeper;
	}

	HttpConfigKeeper(Context ctx) {
		this.mCtx = ctx;
	}

	public Map<String, String> getHttpHeaders(String cookie) {
		Map<String, String> headers = new HashMap<String, String>();
		if (!TextUtils.isEmpty(cookie)) {
			headers.put("Cookie", cookie);
		}
		headers.put("User-Agent", getUserAgent(mCtx));
		return headers;
	}

	/**
	 * 形如YouLive/1.0(HM 2A;Android 4.4.4)
	 * 
	 * @param ctx
	 * @return
	 */
	public String getUserAgent(Context ctx) {
		if (TextUtils.isEmpty(mUserAgent)) {
			StringBuilder sb = new StringBuilder();
			String appVersion = AppHelper.getAppHelper().getAppVersion(ctx);
			if (TextUtils.isEmpty(appVersion)) {
				appVersion = "volley/0";
			}
			sb.append("WriteForTa").append("/").append(appVersion).append("(")
					.append(android.os.Build.MODEL).append(";")
					.append("Android ")
					.append(android.os.Build.VERSION.RELEASE).append(")");
			mUserAgent = sb.toString();

		}
		return mUserAgent;
	}

}
