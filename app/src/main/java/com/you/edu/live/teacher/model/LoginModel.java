package com.you.edu.live.teacher.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;
import com.you.edu.live.teacher.utils.MD5Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @Title: Login Model
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/29
 * @Time 下午15:39
 * @Version
 */
public class LoginModel extends BaseMvpModel implements IHttpCallback<RespOut> {

    private IOperationListener<User> mIOperationListener;
    private IOperationListener<Bitmap> mIImageVerityCodeListener;
    private String mCookie;

    public LoginModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    public void setCookie(String cookie) {
        mCookie = cookie;
    }

    public String getCookie() {
        return mCookie;
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    image.setImageBitmap(bm);
                    if (LoginModel.this.getImageVerityCodeListener() == null) {
                        return;
                    }
                    //完成主界面更新,拿到数据
                    Bitmap bm = (Bitmap) msg.obj;
                    LoginModel.this.getImageVerityCodeListener().onOperationSuccess(bm);
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 请求图片验证码
     */
    public void requestImageVerifyCode(int verifyType, IOperationListener<Bitmap> listener) {
        this.setImageVerityCodeListener(listener);
//        Map<String, String> header = new HashMap<String, String>();
//        if (!TextUtils.isEmpty(this.getCookie())) {
//            header.put("Cookie", this.getCookie());
//        }
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("t", verifyType + "");
//        params.put("w", "100");
//        params.put("h", "40");
//        RequestTag tag = new RequestTag(this.getHttpTag(),
//                GlobalConfig.Operator.OPERAOR_IMAGE_VERIFY_CODE);
//        tag.isGetCookie = true;
//        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.USER_IMAGE_VERIFY_CODE,
//                params, header, this, tag);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(GlobalConfig.HttpUrl.USER_IMAGE_VERIFY_CODE+"?t="+verifyType
        +"&w=100&h=40")
                .build();
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //刷新ui，okhttp网络请求后，不是在主线程中，如果要刷新ui，必须的主线程中；
                if (response.isSuccessful()) {
                    InputStream is = response.body().byteStream();
                    Bitmap bm = BitmapFactory.decodeStream(is);
//                  //  image.setImageBitmap(bm);
                    Message msg = new Message();
                    msg.obj = bm;//可以是基本类型，可以是对象，可以是List、map等；
                    mHandler.sendMessage(msg);
                }
                Headers headers = response.headers();
//                Log.d(TAG, "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                String session = cookies.get(0);
                LoginModel.this.setCookie(session);
////                Log.d(TAG, "onResponse-size: " + cookies);
//                s = session.substring(0, session.indexOf(";"));
////                Log.i(TAG, "session is  :" + s);
            }
        });
    }

    /**
     * 请求登录
     *
     * @param userId   账号 手机或邮箱或帐号（二选一）
     * @param password 密码（前端h5加密）
     * @param verify   验证码(需要验证码的时候显示)
     * @param serial   设备号
     */
    public void requestLogin(String userId, String password, String verify,
                             String serial, IOperationListener<User> listener) {
        this.setLoginRequestListener(listener);

        Map<String, String> header = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.getCookie())) {
            header.put("Cookie", this.getCookie());
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("user", userId);
        if (!TextUtils.isEmpty(password)) {
            params.put("password", MD5Utils.GetMD5Code(password));
        }
        if (!TextUtils.isEmpty(verify)) {
            params.put("verify_code", verify);
        }
        if (!TextUtils.isEmpty(serial)) {
            params.put("serial_no", serial);
        }
        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_USER_LOGIN);
        tag.isGetCookie = true;
        this.getHttpApi().request(Request.Method.POST, GlobalConfig.HttpUrl.USER_LOGIN,
                params, header, this, tag);

    }

    /**
     * 第三方登录
     *
     * @param plat
     * @param userId
     * @param openId
     * @param username
     * @param listener
     */
    public void requestThirdPartyLogin(String plat, String userId,
                                       String openId, String username, String serial,
                                       IOperationListener<User> listener) {

        this.setLoginRequestListener(listener);
        Map<String, String> params = new HashMap<String, String>();
        params.put("tuid", userId);
        params.put("tlsite", plat);
        params.put("nickname", username);
        // if (!TextUtils.isEmpty(openId)) {
        // params.put("openid", openId);
        // }
        params.put("serial_no", serial);
        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_THIRD_LOGIN);
        tag.isGetCookie = true;
        this.getHttpApi().request(GlobalConfig.HttpUrl.THIRD_LOGIN, params,
                this, tag);
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
            case GlobalConfig.Operator.OPERATOR_USER_LOGIN:// 登录
                if (null == this.getLoginRequestListener()) {
                    return;
                }
                if (parser.isSuccess()) {
                    String jsonArr = parser.getValue(GlobalConfig.HttpJson.KEY_DATA);
                    User user = parser.getOne(jsonArr, User.class);
                    if (out.param.isGetCookie && !TextUtils.isEmpty(out.cookie)) {// 如果需要，设置cookie
                        if (null != user) {
                            user.setCookie(out.cookie);
                        }
                    }
                    this.getLoginRequestListener().onOperationSuccess(user);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getLoginRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
            case GlobalConfig.Operator.OPERAOR_IMAGE_VERIFY_CODE://获取图片验证码
                if (this.getImageVerityCodeListener() == null) {
                    return;
                }
                if (out.param.isGetCookie && !TextUtils.isEmpty(out.cookie)) {// 如果需要，设置cookie
                    this.setCookie(out.cookie);

                }
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
            case GlobalConfig.Operator.OPERATOR_USER_LOGIN:
                if (this.getLoginRequestListener() != null) {
                    this.getLoginRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_USER_LOGIN,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
        }
    }

    public void setLoginRequestListener(IOperationListener<User> listener) {
        this.mIOperationListener = listener;
    }

    public IOperationListener<User> getLoginRequestListener() {
        return mIOperationListener;
    }

    public void setImageVerityCodeListener(IOperationListener<Bitmap> listener) {
        this.mIImageVerityCodeListener = listener;
    }

    public IOperationListener<Bitmap> getImageVerityCodeListener() {
        return mIImageVerityCodeListener;
    }

}
