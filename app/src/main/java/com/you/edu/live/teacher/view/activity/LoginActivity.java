package com.you.edu.live.teacher.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.ILoginContract;
import com.you.edu.live.teacher.contract.IThirdActionListener;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.SharedPreDao;
import com.you.edu.live.teacher.presenter.LoginPresenter;
import com.you.edu.live.teacher.presenter.helper.ThirdShareHelper;
import com.you.edu.live.teacher.utils.BitmapUtil;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.utils.RegularUtils;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.SoftKeyboardHelper;
import com.you.edu.live.teacher.widget.SystemBarTintManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * @Title: 登录界面
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/29
 * @Time 下午14:38
 * @Version 1.0
 */
public class LoginActivity extends BaseViewActivity implements View.OnFocusChangeListener, ILoginContract.ILoginView, SoftKeyboardHelper.OnKeyboardShownListener, IThirdActionListener, View.OnClickListener {

    @BindView(R.id.ed_user_id)
    EditText mUserId;
    @BindView(R.id.ll_id_item)
    LinearLayout mLlIdItem;
    @BindView(R.id.ed_user_password)
    EditText mUserPassword;
    @BindView(R.id.rl_password_item)
    RelativeLayout mRlPasswordItem;
    @BindView(R.id.bt_click_sign_in)
    Button mBtClickSignIn;
    @BindView(R.id.ll_image_code_item)
    LinearLayout mLlImageCodeItem;
    @BindView(R.id.tv_no_password_login)
    TextView mTvNoPasswordLogin;
    @BindView(R.id.division_line)
    View mDivisionLine;
    @BindView(R.id.other_login_mode)
    LinearLayout mOtherLoginMode;
    @BindView(R.id.login_root)
    RelativeLayout mLoginRoot;
    @BindView(R.id.iv_image_verify_code)
    ImageView mIvImageVerifyCode;
    @BindView(R.id.ed_image_code)
    EditText mEdIVerifyCode;
    @BindView(R.id.ll_ed_image_code_backgound)
    LinearLayout mLlImageCodeBackgound;

    private static final int REQ_CODE_NO_PASSWORD = 1;
    private static final int REQ_CODE_REGISTER = 2;
    private static final int REQ_CODE_FORGET_PASSWORD = 3;

    /**
     * 密码最多16位 最少6位
     **/
    private static final int LIMIT_PASSWORD_MIN_LENGTH = 6;
    private static final int LIMIT_PASSWORD_MAX_LENGTH = 16;

    /**
     * 选中类型
     */
    private static final int ID_TYPE = 1;
    private static final int PW_TYPE = 2;
    private static final int VERIFY_CODE = 3;
    private static final int NOMAL_TYPE = 4;

    private static final String PLAT_SINA_WEIBO = "sina";
    private static final String PLAT_WECHAT = "wechat";

    /**
     * 是否退出应用
     **/
    private boolean mDoubleExit = false;
    private LoginPresenter mPresenter;
    private SoftKeyboardHelper mKeyboardHelper;
    private String mDeviceSerial;
    private ProgresDialogHelper mProgresDialogHelper;
    private Unbinder mUnbinder;
    private ThirdShareHelper mShareHelper;

    private boolean isLoginWeChat = false;

    private int mVerifyCodeWidth = 0;//图片验证码宽度
    private int mVerifyCodeHeight = 0;//图片验证码高度

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        /**
         * 更改状态栏颜色 只适用于系统版本4.4及以上
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager
                    .setStatusBarTintResource(R.color.colorPrimary);// 通知栏所需颜色
        }
        this.setContentView(R.layout.activity_login);
        mUnbinder = ButterKnife.bind(this);
        this.prepareData();
        this.prepareView();
    }

    private void prepareData() {
        this.getMvpPresenter().attachView(this);
        mDeviceSerial = DeviceUtils.getDeviceSerial();
        mProgresDialogHelper = new ProgresDialogHelper(this);
        mKeyboardHelper = new SoftKeyboardHelper(mLoginRoot, this);
        ShareSDK.initSDK(this);
    }

    private void prepareView() {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mTvNoPasswordLogin.measure(w, h);
        int measuredHeight = mTvNoPasswordLogin.getMeasuredHeight();

        ViewGroup.LayoutParams params = mDivisionLine.getLayoutParams();
        params.height = measuredHeight;
        mDivisionLine.setLayoutParams(params);

        mRlPasswordItem.measure(w, h);
        int passwordHeight = mRlPasswordItem.getMeasuredHeight();

        mRlPasswordItem
                .setBackgroundResource(R.drawable.common_et_bg_rectangle);
        mLlIdItem
                .setBackgroundResource(R.drawable.common_et_bg_rectangle);

        int padding = this.getResources().getDimensionPixelSize(
                R.dimen.margin_16);

        int screenWidth = DeviceUtils.getScreenWidth(this);
        int verifyWidth = screenWidth / 3;

        if (passwordHeight != 0) {

            ViewGroup.LayoutParams layoutParams = mLlImageCodeItem.getLayoutParams();
            layoutParams.height = passwordHeight + padding;
            mLlImageCodeItem.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams1 = mIvImageVerifyCode.getLayoutParams();
            layoutParams1.width = verifyWidth;
            layoutParams1.height = passwordHeight + padding;
            mIvImageVerifyCode.setLayoutParams(layoutParams1);

            mVerifyCodeWidth = verifyWidth;
            mVerifyCodeHeight = passwordHeight + padding;

            this.getVerifyCode();

        }

        mIvImageVerifyCode.setOnClickListener(this);
        mUserId.setOnFocusChangeListener(this);
        mUserPassword.setOnFocusChangeListener(this);
        mUserId.addTextChangedListener(mTvWatcher);
        mUserPassword.addTextChangedListener(mTvWatcher);

    }

    /**
     * 获取图片验证码
     */
    private void getVerifyCode() {
        if (mVerifyCodeHeight == 0 || mVerifyCodeWidth == 0) {
            return;
        }
        this.getMvpPresenter().requestImageVerifyCode(3);

    }

    /**
     * 关闭键盘
     */
    @OnClick(R.id.login_root)
    public void onClickRoot() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 点击登录按钮
     */
    @OnClick(R.id.bt_click_sign_in)
    public void onClickSignIn() {
        if (!this.isNetworkEnable()) {
            this.showToast(getString(R.string.no_network));
            return;
        }

        String userId = mUserId.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();
        String verifyCode = mEdIVerifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            this.showToast("请输入验证码");
            return;
        }
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(mDeviceSerial)) {

            if (userId.contains(".com")) {// 是否是邮箱
                if (RegularUtils.emailMatching(userId,
                        RegularUtils.EMAIL_REGULAR)) {// 邮箱格式是否正确
                    this.getMvpPresenter().requestLogin(userId, password, verifyCode,
                            mDeviceSerial);
                } else {
                    this.showToast(getString(R.string.email_format_error));
                }
            } else {
                this.getMvpPresenter().requestLogin(userId, password, verifyCode,
                        mDeviceSerial);
            }
        } else {
            if (TextUtils.isEmpty(userId)) {
                this.showToast(getString(R.string.enter_userId_please));
            } else if (TextUtils.isEmpty(password)) {
                this.showToast(getString(R.string.enter_password_please));
            } else {
                this.showToast(getString(R.string.enter_please));
            }
        }

    }

    /**
     * 免密登录
     */
    @OnClick(R.id.tv_no_password_login)
    public void onClickNoPassword() {
        Intent intent = new Intent(this, LoginPWFreeActivity.class);
        // intent.putExtra("onlyFinish", mIsOnlyFinish);
        this.startActivityForResult(intent, REQ_CODE_NO_PASSWORD);
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 注册账号
     */
    @OnClick(R.id.tv_register_id)
    public void onClickRegister() {
        Intent intent = new Intent(this, RegisteredActivity.class);
        // intent.putExtra("onlyFinish", mIsOnlyFinish);
        this.startActivityForResult(intent, REQ_CODE_REGISTER);
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 忘记密码
     */
    @OnClick(R.id.tv_forget_password)
    public void onClickForgetPW() {
        Intent intent = new Intent(this, FindPWActivity.class);
        this.startActivityForResult(intent, REQ_CODE_FORGET_PASSWORD);
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 微信登录
     */
    @OnClick(R.id.iv_wechat_login)
    public void onClickWechatLogin() {
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        Platform plat = ShareSDK.getPlatform(this, Wechat.NAME);
        if (!plat.isClientValid()) {
            this.showToast(this.getString(R.string.login_no_wechat_app));
            return;
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.onThirdAuthorize(plat);

    }

    /**
     * 微博登录
     */
    @OnClick(R.id.iv_sina_login)
    public void onClickSinaLogin() {
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        Platform plat = ShareSDK.getPlatform(this, SinaWeibo.NAME);
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.onThirdAuthorize(plat);

    }


    private void onThirdAuthorize(Platform plat) {
        if (null == plat) {
            return;
        }
        if (plat.isValid()) {
            this.onThirdLogin(plat);
            return;
        }
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.show();
        }
        if (null == mShareHelper) {
            mShareHelper = new ThirdShareHelper(this);
        }
        plat.setPlatformActionListener(mShareHelper);
        if (SinaWeibo.NAME.equals(plat.getName())) {
            plat.SSOSetting(false);// false则优先调用客户端登录
            plat.authorize();

        } else if (Wechat.NAME.equals(plat.getName())) {
            isLoginWeChat = true;
            plat.SSOSetting(false);
            // plat.showUser(null);
            plat.authorize();
        }
//        else if (QQ.NAME.equals(plat.getName())) {
//            plat.SSOSetting(false);// false则优先调用客户端登录
//            plat.authorize();
//        }

    }

    /**
     * 第三方准备登录
     *
     * @param plat
     */
    private void onThirdLogin(Platform plat) {
        String userId = null;
        String username = plat.getDb().getUserName();
        String openId = null;
        String platStr = "";
        if (Wechat.NAME.equals(plat.getName())) {
            platStr = PLAT_WECHAT;
            // 微信的uid应该以unionid为主，而非openid
            String unionid = plat.getDb().get("unionid");
            openId = plat.getDb().getUserId();
            if (TextUtils.isEmpty(unionid)) {
                userId = openId;
            } else {
                userId = unionid;
            }
        } else if (SinaWeibo.NAME.equals(plat.getName())) {
            platStr = PLAT_SINA_WEIBO;
            userId = plat.getDb().getUserId();
        }
//        else if (QQ.NAME.equals(plat.getName())) {
//            platStr = PLAT_QQ;
//            userId = plat.getDb().getUserId();
//            // 获取资料
//            plat.getDb().getUserName();// 获取用户名字
//            plat.getDb().getUserIcon(); // 获取用户头像
//        }
        if (!TextUtils.isEmpty(userId)) {
            this.login(platStr, userId, openId, username);
        }
    }

    private void login(String plat, String userId, String openId,
                       String username) {
        this.getMvpPresenter().requestThirdPartyLogin(plat, userId, openId,
                username, DeviceUtils.getDeviceSerial());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        if (REQ_CODE_NO_PASSWORD == requestCode) {// 免密登录成功
            this.onLoginSuccess();
        }
        if (REQ_CODE_REGISTER == requestCode) { // 注册账号成功
            this.onLoginSuccess();
        }
        if (REQ_CODE_FORGET_PASSWORD == requestCode) {// 修改密码成功
            this.onLoginSuccess();
        }
    }

    private void onLoginSuccess() {
        if (AppHelper.getAppHelper().getUser(this) != null) {
            SharedPreDao.saveObject(this.getApplicationContext(),
                    GlobalConfig.SharedPreferenceDao.FILENAME_USER,
                    GlobalConfig.SharedPreferenceDao.KEY_USER_INFO, AppHelper
                            .getAppHelper().getUser(this));
        }

        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.setResult(RESULT_OK);
        this.finish();
    }

    private TextWatcher mTvWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String userId = mUserId.getText().toString().trim();
            String password = mUserPassword.getText().toString().trim();
            if ((!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password))
                    && password.length() >= LIMIT_PASSWORD_MIN_LENGTH
                    && password.length() <= LIMIT_PASSWORD_MAX_LENGTH) {
                LoginActivity.this.setBtnEnable(mBtClickSignIn, true,
                        R.drawable.common_btn_bg_sky_blue_rectangle_selector);
            } else {
                LoginActivity.this.setBtnEnable(mBtClickSignIn, false, -1);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }
    };

    private void setBtnEnable(Button btn, boolean enable, int resId) {
        if (btn == null) {
            return;
        }
        btn.setEnabled(enable);
        btn.setClickable(true);
        if (enable) {
            btn.setBackgroundResource(resId);
        } else {
            btn.setBackgroundResource(R.drawable.common_btn_bg_gray_rectangle_selector);
        }
    }

    /**
     * edittext是否获得焦点
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ed_user_id:
                this.setViewBg(ID_TYPE);
                break;
            case R.id.ed_user_password:
                this.setViewBg(PW_TYPE);
                break;
        }
    }

    private void setViewBg(int type) {
        if (type == ID_TYPE) {
            mRlPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mLlIdItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
        } else if (type == PW_TYPE) {
            mRlPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
            mLlIdItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        } else if (type == NOMAL_TYPE) {
            mRlPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mLlIdItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mLlImageCodeBackgound.setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDoubleExit) {
            super.onBackPressed();
            AppHelper.getAppHelper().exit();
        }
        // 两秒之内再次按返回键 退出应用
        mDoubleExit = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mDoubleExit = false;
            }
        }, 2000);
        AppHelper.getAppHelper().showToast(this, this.getString(R.string.exit));

    }

    @Override
    public void showLoadingView(boolean isBuffering) {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.show();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.setViewBg(NOMAL_TYPE);
    }

    @Override
    public void hideLoadingView(boolean isBuffering) {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        if (user != null) {
            if (user.getUser_type() == 2) {
                AppHelper.getAppHelper().setUser(user);
                this.onLoginSuccess();
            } else {
                this.showToast(getString(R.string.not_get_teacher_certification));
            }
        }
    }

    @Override
    public void onVerifyCodeSuccess(Bitmap data) {

//        Log.e("aa",data+"");
//            Glide.with(this).load(data)
//                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .override(mVerifyCodeWidth, mVerifyCodeHeight).fitCenter()
//                    .into(mIvImageVerifyCode);

        mIvImageVerifyCode.setImageBitmap(data);
    }

    @Override
    public void showError(String error) {
        this.showToast(error);
        this.getVerifyCode();
    }

    @Override
    public LoginPresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new LoginPresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(this));
        }
        return mPresenter;
    }

    @Override
    public void onKeyboardShown(boolean shown) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoginWeChat) {
            if (mProgresDialogHelper != null) {
                mProgresDialogHelper.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        mPresenter = null;
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onComplete(Platform plat, HashMap<String, Object> info) {
        if (this.isFinishing()) {
            return;
        }
        isLoginWeChat = false;
        // if (null != mProgresDialogHelper) {
        // mProgresDialogHelper.show();
        // }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.onThirdLogin(plat);
    }

    @Override
    public void onError(Platform plat, String error) {
        isLoginWeChat = false;
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        this.showToast(getString(R.string.no_network));
    }

    @Override
    public void onCancel(Platform plat) {
        isLoginWeChat = false;
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image_verify_code://获取图片验证码
                this.getVerifyCode();
                break;
        }
    }
}
