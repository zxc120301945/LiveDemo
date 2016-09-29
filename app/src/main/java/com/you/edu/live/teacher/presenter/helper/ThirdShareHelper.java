package com.you.edu.live.teacher.presenter.helper;

import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.mob.tools.utils.UIHandler;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.contract.IThirdActionListener;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 第三方登录/分享助手（主要处理从子线程到主线程的过程）
 * 
 * @author XingRongJing
 * 
 */
public class ThirdShareHelper implements PlatformActionListener, Callback {

	private static final String TAG = ThirdShareHelper.class.getSimpleName();
	private static final int MSG_AUTH_ERROR = 1;
	private static final int MSG_AUTH_COMPLETE = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	/** 没有安装微信客户端 **/
	private static final int MSG_ARG_NO_WEIXIN_APP = 1;

	private IThirdActionListener mListener;

	public ThirdShareHelper(IThirdActionListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		if (AppHelper.getAppHelper().mIsLog) {
			Log.d(TAG, "shan-->share--onCancel()"+arg1);
		}
		Message msg = new Message();
		msg.what = MSG_AUTH_CANCEL;
		msg.obj = arg0;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		if (AppHelper.mIsLog) {
			Log.d(TAG, "shan-->share--onComplete()");
		}
		Message msg = new Message();
		msg.what = MSG_AUTH_COMPLETE;
		msg.obj = arg0;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		if (AppHelper.mIsLog) {
			Log.e(TAG,
					"shan-->share--onError()"
							+ (null == arg2 ? "" : arg2.getMessage()));
		}
		Message msg = new Message();
		msg.what = MSG_AUTH_ERROR;

		String expName = arg2.getClass().getSimpleName();
		if ("WechatClientNotExistException".equals(expName)) {
			msg.arg1 = MSG_ARG_NO_WEIXIN_APP;
		}
		msg.obj = arg0;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MSG_AUTH_ERROR:// 失败
			if (null != mListener) {
				mListener.onError((Platform) msg.obj, "授权失败");
			}
			break;
		case MSG_AUTH_COMPLETE:// 成功
			if (null != mListener) {
				mListener.onComplete((Platform) msg.obj, null);
			}
			break;
		case MSG_AUTH_CANCEL:
			if (null != mListener) {
				mListener.onCancel((Platform) msg.obj);
			}
			break;
		}
		return false;
	}

}
