package com.you.edu.live.teacher.view.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.ILoginPhoneContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.presenter.LoginPhonePresenter;
import com.you.edu.live.teacher.utils.DeviceUtils;
import com.you.edu.live.teacher.utils.RegularUtils;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.SoftKeyboardHelper;
import com.you.edu.live.teacher.widget.SystemBarTintManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @Title: 免密登录
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/7/12
 * @Time 下午14:00
 * @Version 1.0
 */
public class LoginPWFreeActivity extends BaseViewActivity implements
        Callback, SoftKeyboardHelper.OnKeyboardShownListener,
        OnFocusChangeListener, ILoginPhoneContract.ILoginPhoneView {

    /**
     * title View
     **/
    @BindView(R.id.titlebar_back_iv_back)
    ImageView mIvBack;
    @BindView(R.id.titlebar_back_iv_right)
    ImageView mIvRight;
    @BindView(R.id.titlebar_back_tv_right)
    TextView mTvRight;
    @BindView(R.id.titlebar_back_tv_title)
    TextView mTvTitle;

    /**
     * 内容View
     **/
    @BindView(R.id.login_free_root)
    LinearLayout mLoginFreeRoot;
    @BindView(R.id.ll_phone_item)
    LinearLayout mLlPhoneItem;
    @BindView(R.id.ll_valid_code_item)
    LinearLayout mLlValidCodeItem;
    @BindView(R.id.ll_enter_password)
    LinearLayout mLlEnterPassword;
    @BindView(R.id.tv_prompt_message)
    TextView mTvPromptMessage;
    @BindView(R.id.login_et_phone)
    EditText mLoginEtPhone;
    @BindView(R.id.login_et_valid_code)
    EditText mLoginetValidCode;
    @BindView(R.id.btn_login)
    Button mBtLogin;
    @BindView(R.id.login_btn_valid_code)
    TextView mLoginTvValidCode;
    @BindView(R.id.division_line_phone_two)
    View mLineTwo;

    private static final int PHONE_TYPE = 1;
    private static final int VALID_CODE_TYPE = 2;
    private static final int NOMAL_TYPE = 3;

    private static final int MSG_CODE_COUNT = 1;

    /**
     * 是否正在计数
     **/
    private boolean mIsCount = false;
    /**
     * 手机号最多11位
     **/
    private static final int LIMIT_PHONE_LENGTH = 11;
    private static final int LIMIT_VALID_CODE = 6;

    private Handler mHandler = new Handler(this);
    private static final int COUNT = 60;
    private int mCount = COUNT;

    private Timer mTimer;

    private ProgresDialogHelper mDialogHelper;

    private SoftKeyboardHelper mKeyboardHelper;

    private boolean mFindPw = false;
    private Unbinder mUnbinder;
    private LoginPhonePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        // TODO Auto-generated method stub
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
        this.setContentView(R.layout.activity_login_phone);
        mUnbinder = ButterKnife.bind(this);
        this.getMvpPresenter().attachView(this);
        this.prepareView();
        this.prepareData();
    }

    private void prepareData() {
        mDialogHelper = new ProgresDialogHelper(this);
        mKeyboardHelper = new SoftKeyboardHelper(mLoginFreeRoot, this);
        this.setBtnEnable(mBtLogin, false, -1);
        this.setBtnEnable(mLoginTvValidCode, false, -1);
    }

    private void prepareView() {
        mTvPromptMessage.setVisibility(View.GONE);
        mLineTwo.setVisibility(View.GONE);
        mIvBack.setImageResource(R.drawable.login_close);
        mIvRight.setVisibility(View.GONE);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(getString(R.string.login));
        mTvTitle.setText(getString(R.string.login_password_free));
        mTvRight.setTextColor(new Color().BLACK);
        mTvTitle.setTextColor(new Color().BLACK);
        mBtLogin.setText(getString(R.string.login));
        mLlEnterPassword.setVisibility(View.GONE);
        mLoginEtPhone.addTextChangedListener(mTvWatcher);
        mLoginetValidCode.addTextChangedListener(mTvWatcher);

        mLoginTvValidCode.setText(this.getString(R.string.login_valid_code));

        this.setViewBg(NOMAL_TYPE);

        mLoginEtPhone.setOnFocusChangeListener(this);
        mLoginetValidCode.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login_et_phone:
                this.setViewBg(PHONE_TYPE);
                break;
            case R.id.login_et_valid_code:
                this.setViewBg(VALID_CODE_TYPE);
                break;
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        this.overridePendingTransition(-1, R.anim.slide_out_to_bottom);
    }

    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickClose() {
        if (mDialogHelper.isShow()) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.finish();
    }

    /**
     * 隐藏软键盘
     */
    @OnClick(R.id.login_free_root)
    public void onClickRoot() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 右侧登录按钮
     */
    @OnClick(R.id.titlebar_back_tv_right)
    public void onClickTitleLogin() {
        this.setViewBg(NOMAL_TYPE);
        if (mDialogHelper.isShow()) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.finish();
    }

    /**
     * 获取验证码
     */
    @OnClick(R.id.login_btn_valid_code)
    public void onClickValidCode() {
        this.onEtFocus(mLoginetValidCode);
        if (this.isNetworkEnable()) {
            this.fetchValidCode();
            this.setBtnEnable(mLoginTvValidCode, true,
                    R.drawable.common_btn_bg_light_green_rectangle_selector);
        } else {
            this.showToast(this.getString(R.string.no_network));
        }
    }

    /**
     * 登录
     */
    @OnClick(R.id.btn_login)
    public void onClickLogin() {
        String phone = mLoginEtPhone.getText().toString().trim();
        String validCode = mLoginetValidCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != LIMIT_PHONE_LENGTH) {
            this.showToast(this.getString(R.string.please_enter_phone_number));
            return;
        }
        if (TextUtils.isEmpty(validCode)
                || validCode.length() != LIMIT_VALID_CODE) {
            this.showToast(this.getString(R.string.valid_code_no_valid));
            return;
        }

        this.getMvpPresenter().requestOneKeyLogin(phone, validCode,
                DeviceUtils.getDeviceSerial());
    }

    public void fetchValidCode() {

        String phone = mLoginEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != LIMIT_PHONE_LENGTH) {
            this.showToast(getString(R.string.please_enter_phone_number));
        } else {
            if (RegularUtils.phoneMatching(phone, RegularUtils.PHONE_REGULAR)) {
                this.getMvpPresenter().requestPhoneValidCode(phone, "");
                if (mKeyboardHelper != null
                        && mKeyboardHelper.isSoftInputOpen()) {
                    mKeyboardHelper.hideSoftInput();
                }
            } else {
                this.showToast(getString(R.string.please_enter_phone_number));
            }
        }
    }

    /**
     * 获取短信后开始计时
     */
    public void startTimer() {
        mIsCount = true;
        mCount = COUNT;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!mIsCount) {
                    return;
                }
                if (null != mHandler) {
                    mHandler.sendEmptyMessage(MSG_CODE_COUNT);
                }
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        mIsCount = false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case MSG_CODE_COUNT:// 获取短信计时
                if (mCount > 0) {
                    mCount--;
                    this.setBtnEnable(mLoginTvValidCode, false,
                            R.drawable.common_btn_bg_light_green_rectangle_selector);
                    mLoginTvValidCode.setText(mCount + "秒");
                /* + this.getString(R.string.valid_code_retry) */
                } else {
                    this.stopTimer();
                    mLoginTvValidCode.setText(this
                            .getString(R.string.algin_valid_code));
                    this.setBtnEnable(mLoginTvValidCode, true,
                            R.drawable.common_btn_bg_light_green_rectangle_selector);
                }
                break;

        }
        return false;
    }

    private void onEtFocus(EditText et) {
        if (null == et) {
            return;
        }
        et.requestFocus();
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
    }

    private TextWatcher mTvWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String phone = mLoginEtPhone.getText().toString().trim();
            String validCode = mLoginetValidCode.getText().toString().trim();
            if (!mIsCount) {
                if (TextUtils.isEmpty(phone)
                        || phone.length() != LIMIT_PHONE_LENGTH) {// 限制验证码按钮
                    LoginPWFreeActivity.this.setBtnEnable(mLoginTvValidCode,
                            false, -1);
                } else {
                    LoginPWFreeActivity.this
                            .setBtnEnable(
                                    mLoginTvValidCode,
                                    true,
                                    R.drawable.common_btn_bg_light_green_rectangle_selector);
                }
            }
            // 限制注册-电话11位，验证码6位
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(validCode)
                    || phone.length() != LIMIT_PHONE_LENGTH
                    || validCode.length() != LIMIT_VALID_CODE) {
                LoginPWFreeActivity.this.setBtnEnable(mBtLogin, false, -1);
            } else {
                LoginPWFreeActivity.this.setBtnEnable(mBtLogin, true,
                        R.drawable.common_btn_bg_sky_blue_rectangle_selector);
            }
        }
    };

    private void setBtnEnable(View view, boolean enable, int resId) {
        if (view == null) {
            return;
        }
        view.setEnabled(enable);
        view.setClickable(true);
        if (enable) {
            view.setBackgroundResource(resId);
        } else {
            view.setBackgroundResource(R.drawable.common_btn_bg_rectangle_selector_no_recommended);
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mDialogHelper != null && mDialogHelper.isShow()) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.stopTimer();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        mPresenter = null;
    }

    private void setViewBg(int type) {
        if (type == PHONE_TYPE) {
            mLlValidCodeItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mLlPhoneItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
        } else if (type == VALID_CODE_TYPE) {
            mLlValidCodeItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
            mLlPhoneItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        } else if (type == NOMAL_TYPE) {
            mLlValidCodeItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mLlPhoneItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        }
    }

    @Override
    public void onKeyboardShown(boolean shown) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showLoading() {
        if (mDialogHelper != null) {
            mDialogHelper.show();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public void hideLoading() {
        if (null != mDialogHelper) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public void onOneKeyLoginSuccess(User user) {
        if (user != null) {
            if (user.getUser_type() == 2) {
                AppHelper.getAppHelper().setUser(user);
                this.setResult(Activity.RESULT_OK);
                this.finish();
            } else {
                this.showToast(getString(R.string.not_get_teacher_certification));
            }
        }

        if (mDialogHelper != null && mDialogHelper.isShow()) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }

    }

    @Override
    public void onValidCodeSuccess(Boolean isValidCodeSuccess) {
        if (isValidCodeSuccess) {
            mLoginTvValidCode.setText(this
                    .getString(R.string.algin_valid_code) + "（" + mCount + "）");
            this.startTimer();
        }
    }

    @Override
    public void showError(int operator, String error) {
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE:
                this.setBtnEnable(mLoginTvValidCode, true,
                        R.drawable.common_btn_bg_light_green_rectangle_selector);
                if (TextUtils.isEmpty(error)) {
                    this.showToast(this.getString(R.string.no_network));
                } else {
                    this.showToast(error);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN:
                if (TextUtils.isEmpty(error)) {
                    this.showToast(this.getString(R.string.login_fails));
                } else {
                    this.showToast(error);
                }
                break;
        }
    }

    @Override
    public LoginPhonePresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new LoginPhonePresenter(this.getHttpApi(), AppHelper
                    .getAppHelper().getCache(this));
        }
        return mPresenter;
    }
}
