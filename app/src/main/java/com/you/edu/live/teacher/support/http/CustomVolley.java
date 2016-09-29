package com.you.edu.live.teacher.support.http;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

public class CustomVolley {

	/** Default on-disk cache directory for image. */
	public static final String DEFAULT_CACHE_DIR_IMAGE = "imageCache";

	/** Default on-disk cache directory for http. */
	public static final String DEFAULT_CACHE_DIR_HTTP = "httpCache";

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(HttpStack stack) {
		if (null == stack) {
			stack = new HurlStack();
		}
		Network network = new BasicNetwork(stack);
		RequestQueue queue = new RequestQueue(new NoCache(), network);
		queue.start();
		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue() {
		return CustomVolley.newRequestQueue(null);
	}

	/**
	 * Set Logs to enabled or disabled
	 * 
	 * @param isLoggable
	 *            <code>true</code> to enable Logs, <code>false</code> to
	 *            disable logs
	 */
	public static void setLoggable(boolean isLoggable) {
		VolleyLog.DEBUG = isLoggable;
	}

}
