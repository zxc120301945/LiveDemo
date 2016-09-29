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

public class LoginPhoneModel extends BaseMvpModel implements
		IHttpCallback<RespOut> {

	private IOperationListener<Boolean> mIValidCodeOperationListener;
	private IOperationListener<User> mIOneKeyLoginOperationListener;
	private IOperationListener<User> mIUserRegisterOperationListener;
	private String mCookie;

	public LoginPhoneModel(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
	}

	private void setCookie(String cookie) {
		this.mCookie = cookie;
	}

	private String getCookie() {
		return mCookie;
	}

	/**
	 * 请求手机验证码
	 * 
	 * @param phoneNumber
	 *            手机号 必传
	 * @param type
	 *            1:注册业务，2:一键注册登陆，3:找回密码，4：完善用户手机，5:修改绑定手机
	 */
	public void requestPhoneValidCode(String phoneNumber, String type,
			IOperationListener<Boolean> listener) {

		this.setValidCodeRequestListener(listener);

		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", phoneNumber);
		if (type != null && !type.equals("")) {
			params.put("type", type);
		}
		RequestTag tag = new RequestTag(this.getHttpTag(),
				GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE);
		tag.isGetCookie = true;
		this.getHttpApi().request(Method.POST,
				GlobalConfig.HttpUrl.USER_LOGIN_PHONE_VALID_CODE, params, this,
				tag);
	}

	/**
	 * 一键登录
	 * 
	 * @param phoneNumber
	 *            手机号码
	 * @param validCode
	 *            短信验证码
	 * @param serial_no
	 *            手机设备号
	 * @param listener
	 */
	public void requestOneKeyLogin(String phoneNumber, String validCode,
			String serial_no, IOperationListener<User> listener) {
		if (phoneNumber.equals("") && validCode.equals("")
				&& serial_no.equals("")) {
			return;
		}

		this.setOneKeyLoginRequestListener(listener);

		Map<String, String> header = new HashMap<String, String>();
		if (!TextUtils.isEmpty(this.getCookie())) {
			header.put("Cookie", this.getCookie());
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", phoneNumber);
		params.put("code", validCode);
		params.put("serial_no", serial_no);
		RequestTag tag = new RequestTag(this.getHttpTag(),
				GlobalConfig.Operator.OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN);
		tag.isGetCookie = true;
		this.getHttpApi().request(Method.POST,
				GlobalConfig.HttpUrl.USER_MOBILE_PHONE_A_KEY_LOGIN, params,
				header, this, tag);
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
		case GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE:// 获取验证码
			if (this.getValidCodeRequestListener() == null) {
				return;
			}

			if (!parser.isSuccess()) {
				int errorCode = parser.getRespCode();
				String errorTips = parser.getErrorMsg();
				this.getValidCodeRequestListener()
						.onOperationFails(
								GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE,
								errorTips);

			} else {
				if (out.param.isGetCookie && !TextUtils.isEmpty(out.cookie)) {// 如果需要，设置cookie
					this.setCookie(out.cookie);
				}
				this.getValidCodeRequestListener().onOperationSuccess(true);
			}
			break;
		case GlobalConfig.Operator.OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN:// 一键登录

			if (getOneKeyLoginRequestListener() == null) {
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
				getOneKeyLoginRequestListener().onOperationSuccess(user);
			} else {
				int errorCode = parser.getRespCode();
				String errorMsg = parser.getErrorMsg();
				getOneKeyLoginRequestListener()
						.onOperationFails(
								GlobalConfig.Operator.OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN,
								errorMsg);
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
		case GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE:// 短信验证码请求失败
			if (null != this.getValidCodeRequestListener()) {
				this.getValidCodeRequestListener()
						.onOperationFails(
								GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE,
								GlobalConfig.HTTP_ERROR_TIPS);
			}
			break;
		case GlobalConfig.Operator.OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN:// 一键登录失败
			if (null != this.getOneKeyLoginRequestListener()) {
				this.getOneKeyLoginRequestListener().onOperationFails(
						GlobalConfig.HTTP_RESP_ERROR_CODE,
						GlobalConfig.HTTP_ERROR_TIPS);
			}
			break;
		}
	}

	public void setValidCodeRequestListener(IOperationListener<Boolean> listener) {
		this.mIValidCodeOperationListener = listener;
	}

	public IOperationListener<Boolean> getValidCodeRequestListener() {
		return mIValidCodeOperationListener;
	}

	public void setOneKeyLoginRequestListener(IOperationListener<User> listener) {
		this.mIOneKeyLoginOperationListener = listener;
	}

	public IOperationListener<User> getOneKeyLoginRequestListener() {
		return mIOneKeyLoginOperationListener;
	}

}
