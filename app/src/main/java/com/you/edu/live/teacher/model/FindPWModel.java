package com.you.edu.live.teacher.model;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
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

public class FindPWModel extends BaseMvpModel implements IHttpCallback<RespOut> {

    private IOperationListener<Boolean> mIValidCodeOperationListener;
    private IOperationListener<Boolean> mIVerifyCodeOperationListener;
    private User mUser;
    private Context mCtx;
    private String mCookie;
    private IOperationListener<String> mICookieOperationListener;

    public FindPWModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    public void setCookie(String cookie) {
        this.mCookie = cookie;
    }

    public String getCookie() {
        return mCookie;
    }

    public void setContext(Context ctx) {
        this.mCtx = ctx;
    }

    public Context getContext() {
        return mCtx;
    }

    /**
     * 发送短信
     *
     * @param phoneNumber 手机号 必传
     * @param type        1:注册业务，2:一键注册登陆，3:找回密码，4：完善用户手机，5:修改绑定手机
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
     * 手机验证码验证
     *
     * @param phoneNumber 手机号
     * @param type        业务类型
     * @param validCode   验证码
     */
    public void requestVerifyCode(String phoneNumber, String type,
                                  String validCode, IOperationListener<Boolean> listener,
                                  IOperationListener<String> mICookieOperationListener) {

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(validCode)) {
            return;
        }
        Map<String, String> header = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.getCookie())) {
            header.put("Cookie", this.getCookie());
        }
        this.setVerifyCodeRequestListener(listener);
        this.setCookieRequestListener(mICookieOperationListener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phoneNumber);
        params.put("code", validCode);
        if (type != null && !type.equals("")) {
            params.put("type", type);
        }
//		AppHelper.getAppHelper().getUser(getContext());
        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE);
        this.getHttpApi().request(Method.POST,
                GlobalConfig.HttpUrl.PHONE_VERIFY_CODE, params, header, this,
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
            case GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE:// 验证成功
                if (this.getVerifyCodeRequestListener() == null
                        && this.getCookieRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    this.getCookieRequestListener().onOperationSuccess(getCookie());
                    this.getVerifyCodeRequestListener().onOperationSuccess(true);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorTips = parser.getErrorMsg();
                    this.getVerifyCodeRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE,
                            errorTips);
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
            case GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE:// 验证失败
                if (this.getVerifyCodeRequestListener() != null) {
                    this.getVerifyCodeRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE,
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

    public void setCookieRequestListener(IOperationListener<String> listener) {
        this.mICookieOperationListener = listener;
    }

    public IOperationListener<String> getCookieRequestListener() {
        return mICookieOperationListener;
    }

    public void setVerifyCodeRequestListener(
            IOperationListener<Boolean> listener) {
        this.mIVerifyCodeOperationListener = listener;
    }

    public IOperationListener<Boolean> getVerifyCodeRequestListener() {
        return mIVerifyCodeOperationListener;
    }

}
