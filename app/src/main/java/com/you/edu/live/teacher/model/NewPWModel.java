package com.you.edu.live.teacher.model;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;
import com.you.edu.live.teacher.utils.MD5Utils;

public class NewPWModel extends BaseMvpModel implements IHttpCallback<RespOut> {

	private IOperationListener<User> mListener;

	public NewPWModel(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 找回密码
	 * 
	 * @param password
	 *            密码
	 * @param serial_no
	 *            设备号
	 * @param listener
	 */
	public void requestGetPwd(String password, String serial_no, String cookie,
			IOperationListener<User> listener) {

		if (TextUtils.isEmpty(password)) {
			return;
		}

		this.setOnGetPwdListener(listener);

		Map<String, String> header = new HashMap<String, String>();
		if (!TextUtils.isEmpty(cookie)) {
			header.put("Cookie", cookie);
		}

		String MD5PW = MD5Utils.GetMD5Code(password);

		Map<String, String> params = new HashMap<String, String>();
		params.put("password", MD5PW);
		if (!TextUtils.isEmpty(serial_no)) {
			params.put("serial_no", serial_no);
		}

		RequestTag tag = new RequestTag(this.getHttpTag(),
				GlobalConfig.Operator.OPERATOR_USER_GET_PASSWORD);
		tag.isGetCookie = true;
		this.getHttpApi().request(Method.POST,
				GlobalConfig.HttpUrl.USER_GET_PASSWORD, params, header, this,
				tag);
	}

	@Override
	public void onResponse(RespOut out) {
		// TODO Auto-generated method stub
		if (null == out || null == out.param) {
			return;
		}
		RequestDataParser parser = new RequestDataParser(out.resp);
		int operator = (int) out.param.operator;
		switch (operator) {
		case GlobalConfig.Operator.OPERATOR_USER_GET_PASSWORD:// 成功
			if (this.getOnGetPwdListener() == null) {
				return;
			}

			if (parser.isSuccess()) {
				String jsonArr = parser
						.getValue(GlobalConfig.HttpJson.KEY_DATA);
				User user = parser.getOne(jsonArr, User.class);
				if (out.param.isGetCookie && !TextUtils.isEmpty(out.cookie)) {// 如果需要，设置cookie
					if (null != user) {
						user.setCookie(out.cookie);
					}
				}
				this.getOnGetPwdListener().onOperationSuccess(user);
			} else {
				int errorCode = parser.getRespCode();
				String errorMsg = parser.getErrorMsg();
				this.getOnGetPwdListener()
						.onOperationFails(errorCode, errorMsg);
			}
			break;
		}
	}

	@Override
	public void onResponseError(RespOut out) {
		// TODO Auto-generated method stub
		if (null == out || null == out.param) {
			return;
		}
		int operator = (int) out.param.operator;
		switch (operator) {
		case GlobalConfig.Operator.OPERATOR_USER_GET_PASSWORD:// 失败
			if (this.getOnGetPwdListener() != null) {
				this.getOnGetPwdListener().onOperationFails(
						GlobalConfig.HTTP_RESP_ERROR_CODE,
						GlobalConfig.HTTP_ERROR_TIPS);
			}
			break;
		}
	}

	public void setOnGetPwdListener(IOperationListener<User> listener) {
		this.mListener = listener;
	}

	public IOperationListener<User> getOnGetPwdListener() {
		return mListener;
	}

}
