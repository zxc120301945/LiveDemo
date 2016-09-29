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

public class RegisterModel extends BaseMvpModel implements
		IHttpCallback<RespOut> {

	private IOperationListener<User> mIUserRegisterOperationListener;
	private IOperationListener<Boolean> mIValidCodeOperationListener;
	private String mCookie;

	public RegisterModel(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
	}

	public void setCookie(String cookie) {
		this.mCookie = cookie;
	}

	public String getCookie() {
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
	 * 注册
	 * 
	 * @param phoneNumber
	 *            手机号
	 * @param validCode
	 *            验证码
	 * @param nick_name
	 *            昵称(可不要)
	 * @param password
	 *            密码
	 * @param serial_no
	 *            设备号
	 * @param listener
	 */
	public void requestUserRegister(String phoneNumber, String validCode,
			String nick_name, String password, String serial_no,
			IOperationListener<User> listener) {

		if (phoneNumber.equals("") && validCode.equals("")
				&& serial_no.equals("") && password.equals("")) {
			return;
		}

		this.setUserRegisterRequestListener(listener);

		Map<String, String> header = new HashMap<String, String>();
		if (!TextUtils.isEmpty(this.getCookie())) {
			header.put("Cookie", this.getCookie());
		}

		String MD5Password = MD5Utils.GetMD5Code(password);

		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", phoneNumber);
		params.put("code", validCode);
		params.put("serial_no", serial_no);
		params.put("password", password);
		if (nick_name != null && !nick_name.equals("")) {
			params.put("nick_name", nick_name);
		}
		RequestTag tag = new RequestTag(this.getHttpTag(),
				GlobalConfig.Operator.OPERATOR_USER_REGISTER);
		tag.isGetCookie = true;
		this.getHttpApi().request(Method.POST,
				GlobalConfig.HttpUrl.USER_REGISTER, params, header, this, tag);

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
		case GlobalConfig.Operator.OPERATOR_USER_REGISTER:// 注册

			if (getUserRegisterRequestListener() == null) {
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
				getUserRegisterRequestListener().onOperationSuccess(user);
			} else {
				int errorCode = parser.getRespCode();
				String errorMsg = parser.getErrorMsg();
				getUserRegisterRequestListener().onOperationFails(errorCode,
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
			if (null != this.getUserRegisterRequestListener()) {
				this.getUserRegisterRequestListener().onOperationFails(
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

	public void setUserRegisterRequestListener(IOperationListener<User> listener) {
		this.mIUserRegisterOperationListener = listener;
	}

	public IOperationListener<User> getUserRegisterRequestListener() {
		return mIUserRegisterOperationListener;
	}

}
