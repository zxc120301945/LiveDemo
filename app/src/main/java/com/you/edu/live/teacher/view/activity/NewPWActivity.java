package com.you.edu.live.teacher.view.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.you.edu.live.teacher.contract.INewPWContract;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.presenter.NewPWPresenter;
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
 * 新密码
 *
 * @author WanZhiYuan
 */
public class NewPWActivity extends BaseViewActivity implements
        OnFocusChangeListener, SoftKeyboardHelper.OnKeyboardShownListener, INewPWContract.INewPWView {

    @BindView(R.id.titlebar_back_iv_back)
    ImageView mIvBack;
    @BindView(R.id.new_pw_root)
    LinearLayout mNewPwRoot;
    @BindView(R.id.new_password_item)
    LinearLayout mNewPasswordItem;
    @BindView(R.id.algin_new_password_item)
    LinearLayout mAlginNewPasswordItem;
    @BindView(R.id.titlebar_back_tv_title)
    TextView mTvTitle;
    @BindView(R.id.et_enter_new_password)
    EditText mEtEnterNewPassword;
    @BindView(R.id.et_confirm_new_password)
    EditText mEtConfirmNewPassword;
    @BindView(R.id.tv_matching_password)
    TextView mTvMatchingPassword;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    private static final int NEW_PW_TYPE = 1;
    private static final int ALGIN_PASSOWRD_TYPE = 2;
    private static final int NOMAL_TYPE = 3;

    /**
     * 密码最多16位 最少6位
     **/
    private static final int LIMIT_PASSWORD_MIN_LENGTH = 6;
    private static final int LIMIT_PASSWORD_MAX_LENGTH = 16;

    private ProgresDialogHelper mProgresDialogHelper;
    private NewPWPresenter mPresenter;

    private SoftKeyboardHelper mKeyboardHelper;
    private String mCookie;
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
        this.setContentView(R.layout.activity_new_password);
        mUnbinder = ButterKnife.bind(this);
        this.getMvpPresenter().attachView(this);
        this.prepareView();
        this.prepareData();
    }

    private void prepareView() {
        mIvBack.setImageResource(R.drawable.black_back);
        mTvTitle.setText(getString(R.string.reset_password));
        mTvMatchingPassword.setText(getString(R.string.password_remind));
        mTvMatchingPassword.setTextColor(getResources().getColor(
                R.color.login_et_hint_color));

        this.setViewBg(NOMAL_TYPE);

        mEtEnterNewPassword.setOnFocusChangeListener(this);
        mEtConfirmNewPassword.setOnFocusChangeListener(this);

    }

    private void prepareData() {
        mProgresDialogHelper = new ProgresDialogHelper(this);
        mKeyboardHelper = new SoftKeyboardHelper(mNewPwRoot, this);
        this.setBtnEnable(mBtnConfirm, false, -1);
        mEtEnterNewPassword.addTextChangedListener(mTvWatcher);
        mEtConfirmNewPassword.addTextChangedListener(mTvWatcher);

        mCookie = getIntent().getStringExtra("cookie");

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.et_enter_new_password:
                this.setViewBg(NEW_PW_TYPE);
                break;
            case R.id.et_confirm_new_password:
                this.setViewBg(ALGIN_PASSOWRD_TYPE);
                break;
        }
    }

    @OnClick(R.id.new_pw_root)
    public void onClickRoot() {
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    /**
     * 确认
     */
    @OnClick(R.id.btn_confirm)
    public void onClickComfirm() {
        if (!this.isNetworkEnable()) {
            this.showToast(this.getString(R.string.no_network));
            return;
        }
        String newPW = mEtEnterNewPassword.getText().toString().trim();
        String comfirmNewPw = mEtConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPW)) {
            this.showToast(getString(R.string.please_enter_new_password));
            return;
        }
        if (TextUtils.isEmpty(comfirmNewPw)) {
            this.showToast(getString(R.string.please_enter_again_new_password));
            return;
        }

        if (!newPW.equals(comfirmNewPw)) {
            mTvMatchingPassword.setText(getString(R.string.two_input_atypism));
            mTvMatchingPassword.setTextColor(new Color().RED);
        } else {
            if (RegularUtils.passwordMatching(newPW,
                    RegularUtils.NUMBER_PASSWORD_REGULAR)) {
                this.getMvpPresenter().requestGetPwd(newPW,
                        DeviceUtils.getDeviceSerial(), mCookie);
            } else {
                boolean isComfirm = RegularUtils.isLetterDigit(newPW);
                if (isComfirm) {
                    this.getMvpPresenter().requestGetPwd(newPW,
                            DeviceUtils.getDeviceSerial(), mCookie);
                } else {
                    this.showToast(getString(R.string.password_remind));
                }
                mTvMatchingPassword
                        .setText(getString(R.string.password_remind));
                mTvMatchingPassword.setTextColor(getResources().getColor(
                        R.color.login_et_hint_color));
            }
        }

    }

    /**
     * 返回
     */
    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickBack() {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.finish();
    }

    @Override
    public void onSuccess(User user) {
        // TODO Auto-generated method stub

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
    public void showError(int errorCode, String error) {
        // TODO Auto-generated method stub
        this.showToast(error);
    }

    private void onLoginSuccess() {
        this.setResult(RESULT_OK);
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        this.finish();
    }

    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        // String regex = "^[a-zA-Z0-9]+$";
        // String regular =
        // "[\\u4e00-\\u9fa5，——`·《》“”：；【】。！/!~@#$￥%……& *（）()-_=+\"|''’‘{}？?、.a-zA-Z0-9\\d]+";
        boolean isRight = isDigit
                && isLetter
                && RegularUtils.passwordMatching(str,
                RegularUtils.SIX_PASSWORD_REGULAR);
        return isRight;

    }

    private TextWatcher mTvWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

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
            String newPW = mEtEnterNewPassword.getText().toString().trim();
            String comfirmNewPw = mEtConfirmNewPassword.getText().toString()
                    .trim();
            if (!TextUtils.isEmpty(newPW) && !TextUtils.isEmpty(comfirmNewPw)) {
                if ((newPW.length() >= LIMIT_PASSWORD_MIN_LENGTH && newPW
                        .length() <= LIMIT_PASSWORD_MAX_LENGTH)
                        || (comfirmNewPw.length() >= LIMIT_PASSWORD_MIN_LENGTH && comfirmNewPw
                        .length() <= LIMIT_PASSWORD_MAX_LENGTH)) {

                    if (!newPW.equals(comfirmNewPw)) {
                        mTvMatchingPassword
                                .setText(getString(R.string.two_input_atypism));
                        mTvMatchingPassword.setTextColor(new Color().RED);
                        NewPWActivity.this.setBtnEnable(mBtnConfirm, false, -1);
                    } else {
                        mTvMatchingPassword
                                .setText(getString(R.string.password_remind));
                        mTvMatchingPassword.setTextColor(getResources()
                                .getColor(R.color.login_et_hint_color));
                        NewPWActivity.this.setBtnEnable(mBtnConfirm, true,
                                R.drawable.common_btn_bg_sky_blue_rectangle_selector);
                    }
                } else {
                    mTvMatchingPassword
                            .setText(getString(R.string.password_remind));
                    mTvMatchingPassword.setTextColor(getResources().getColor(
                            R.color.login_et_hint_color));
                    NewPWActivity.this.setBtnEnable(mBtnConfirm, false, -1);
                }
            } else {
                mTvMatchingPassword
                        .setText(getString(R.string.password_remind));
                mTvMatchingPassword.setTextColor(getResources().getColor(
                        R.color.login_et_hint_color));
                NewPWActivity.this.setBtnEnable(mBtnConfirm, false, -1);
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
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        mPresenter = null;
    }

    private void setViewBg(int type) {
        if (type == NEW_PW_TYPE) {
            mAlginNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
        } else if (type == ALGIN_PASSOWRD_TYPE) {
            mAlginNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_selected);
            mNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        } else if (type == NOMAL_TYPE) {
            mNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
            mAlginNewPasswordItem
                    .setBackgroundResource(R.drawable.common_et_bg_rectangle_normal);
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
//        this.overridePendingTransition(-1, R.anim.slide_out_to_bottom);
    }

    @Override
    public void onKeyboardShown(boolean shown) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showLoading() {
        // TODO Auto-generated method stub
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.show();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public void hideLoading() {
        // TODO Auto-generated method stub
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.dismiss();
        }
        if (mKeyboardHelper != null && mKeyboardHelper.isSoftInputOpen()) {
            mKeyboardHelper.hideSoftInput();
        }
    }

    @Override
    public NewPWPresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new NewPWPresenter(this.getHttpApi(), AppHelper
                    .getAppHelper().getCache(this));
        }
        return mPresenter;
    }
}
