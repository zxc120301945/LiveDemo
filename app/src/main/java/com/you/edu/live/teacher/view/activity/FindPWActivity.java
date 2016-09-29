package com.you.edu.live.teacher.view.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
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
import com.you.edu.live.teacher.contract.IFindPWContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.presenter.FindPWPresenter;
import com.you.edu.live.teacher.utils.ActivityCompatUtils;
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
 * 找回密码
 *
 * @author WanZhiYuan
 */
public class FindPWActivity extends BaseViewActivity implements Callback, SoftKeyboardHelper.OnKeyboardShownListener, OnFocusChangeListener, IFindPWContract.IFindPWView {

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
    TextView mLoginBtnValidCode;
    @BindView(R.id.division_line_phone_two)
    View mLineTwo;

    private static final int PHONE_TYPE = 1;
    private static final int VALID_CODE_TYPE = 2;
    private static final int NOMAL_TYPE = 3;

    private static final int REQ_CODE_NEXT = 1;

    private FindPWPresenter mPresenter;

    public static final String TRUE = "true";
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
    private String mCookie;

    private SoftKeyboardHelper mKeyboardHelper;
    private Unbinder mUnbinder;

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
        this.getMvpPresenter().setContext(this);
        this.prepareView();
        this.prepareData();

    }

    private void prepareView() {
        mBtLogin.setText(getString(R.string.next));
        mTvTitle.setText(getString(R.string.find_password));
        mTvPromptMessage.setVisibility(View.GONE);
        mLineTwo.setVisibility(View.GONE);
        mIvBack.setImageResource(R.drawable.login_close);
        mIvRight.setVisibility(View.GONE);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(getString(R.string.login));
        mTvRight.setTextColor(new Color().BLACK);
        mTvTitle.setTextColor(new Color().BLACK);
        mLlEnterPassword.setVisibility(View.GONE);
        mLoginEtPhone.addTextChangedListener(mTvWatcher);
        mLoginetValidCode.addTextChangedListener(mTvWatcher);

        mLoginBtnValidCode.setText(this.getString(R.string.login_valid_code));

        mLoginEtPhone.setOnFocusChangeListener(this);
        mLoginetValidCode.setOnFocusChangeListener(this);

    }

    private void prepareData() {
        mDialogHelper = new ProgresDialogHelper(this);
        mKeyboardHelper = new SoftKeyboardHelper(mLoginFreeRoot, this);
        this.setBtnEnable(mBtLogin, false, -1);
        this.setBtnEnable(mLoginBtnValidCode, false, -1);
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

    @OnClick(R.id.login_free_root)
    public void onClickRoot() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 右侧登录
     */
    @OnClick(R.id.titlebar_back_tv_right)
    public void onClickTitleLogin() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        if (mDialogHelper != null) {
            mDialogHelper.dismiss();
        }
        this.finish();
    }

    /**
     * 验证码
     */
    @OnClick(R.id.login_btn_valid_code)
    public void onClickValideCode() {
        this.onEtFocus(mLoginetValidCode);
        if (this.isNetworkEnable()) {
            this.fetchValidCode();
        } else {
            this.showToast(this.getString(R.string.no_network));
        }
    }

    /**
     * 下一步
     */
    @OnClick(R.id.btn_login)
    public void onClickNext() {
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

        this.getMvpPresenter().requestVerifyCode(phone, "3", validCode);

    }

    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickBack() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        if (mDialogHelper != null) {
            mDialogHelper.dismiss();
        }
        this.finish();
    }

    public void fetchValidCode() {
        String phone = mLoginEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != LIMIT_PHONE_LENGTH) {
            this.showToast(getString(R.string.please_enter_phone_number));
        } else {
            if (RegularUtils.phoneMatching(phone, RegularUtils.PHONE_REGULAR)) {
                if (mKeyboardHelper != null
                        && mKeyboardHelper.isSoftInputOpen()) {
                    mKeyboardHelper.hideSoftInput();
                }
                this.getMvpPresenter().requestPhoneValidCode(phone, "3");
            } else {
                this.showToast(getString(R.string.please_enter_phone_number));
                this.setBtnEnable(mLoginBtnValidCode, true,
                        R.drawable.common_btn_bg_light_green_rectangle_selected);
            }
        }
    }

    @Override
    public void onCookieSuccess(String cookie) {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(cookie)) {
            this.mCookie = cookie;
        }
    }

    @Override
    public void onValidCodeSuccess(Boolean isValidCodeSuccess) {
        // TODO Auto-generated method stub
        if (isValidCodeSuccess) {
            mLoginBtnValidCode.setText(this
                    .getString(R.string.algin_valid_code) + "（" + mCount + "）");
            this.startTimer();
        }
    }

    @Override
    public void onVerifyCodeSuccess(Boolean message) {
        // TODO Auto-generated method stub
        if (message) {
            Intent intent = new Intent(this, NewPWActivity.class);
            if (!TextUtils.isEmpty(mCookie)) {
                intent.putExtra("cookie", mCookie);
            }
            this.startActivityForResult(intent, REQ_CODE_NEXT);
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }

    }

    @Override
    public void showError(int operator, String error) {
        // TODO Auto-generated method stub
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_USER_LOGIN_PHONE_VALID_CODE:// 获取短信失败
                if (TextUtils.isEmpty(error)) {
                    this.showToast(this.getString(R.string.no_network));
                } else {
                    this.showToast(error);
                }
                this.setBtnEnable(mLoginBtnValidCode, true,
                        R.drawable.common_btn_bg_light_green_rectangle_selected);
                break;
            case GlobalConfig.Operator.OPERATOR_PHONE_VERIFY_CODE:// 验证失败
                if (TextUtils.isEmpty(error)) {
                    this.showToast(this.getString(R.string.invalid_code));
                } else {
                    this.showToast(error);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        if (REQ_CODE_NEXT == requestCode) {// 修改密码成功
            this.onLoginSuccess();
        }
    }

    private void onLoginSuccess() {
        this.setResult(Activity.RESULT_OK);
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        if (mDialogHelper != null) {
            mDialogHelper.dismiss();
        }
        this.finish();
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case MSG_CODE_COUNT:// 获取短信计时
                if (mCount > 0) {
                    mCount--;
                    this.setBtnEnable(mLoginBtnValidCode, false,
                            R.drawable.common_btn_bg_light_green_rectangle_selected);
                    mLoginBtnValidCode.setText(mCount + "秒");
                } else {
                    this.stopTimer();
                    mLoginBtnValidCode.setText(this
                            .getString(R.string.algin_valid_code));
                    this.setBtnEnable(mLoginBtnValidCode, true,
                            R.drawable.common_btn_bg_light_green_rectangle_selected);
                }
                break;

        }
        return false;
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
                    FindPWActivity.this.setBtnEnable(mLoginBtnValidCode, false,
                            -1);
                } else {
                    FindPWActivity.this.setBtnEnable(mLoginBtnValidCode, true,
                            R.drawable.common_btn_bg_light_green_rectangle_selected);
                }
            }
            // 限制注册-电话11位，验证码6位
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(validCode)
                    || phone.length() != LIMIT_PHONE_LENGTH
                    || validCode.length() != LIMIT_VALID_CODE) {
                FindPWActivity.this.setBtnEnable(mBtLogin, false, -1);
            } else {
                FindPWActivity.this.setBtnEnable(mBtLogin, true,
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
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        if (mDialogHelper != null) {
            mDialogHelper.dismiss();
        }
        this.stopTimer();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        mPresenter = null;
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
//        this.overridePendingTransition(-1, R.anim.slide_out_to_bottom);
        ActivityCompatUtils.finishActivityLand(this);
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
        // TODO Auto-generated method stub
        if (mDialogHelper != null) {
            mDialogHelper.show();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.setViewBg(NOMAL_TYPE);
    }

    @Override
    public void hideLoading() {
        // TODO Auto-generated method stub

        if (mDialogHelper != null) {
            mDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public FindPWPresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new FindPWPresenter(this.getHttpApi(), AppHelper
                    .getAppHelper().getCache(this));
        }
        return mPresenter;
    }
}
