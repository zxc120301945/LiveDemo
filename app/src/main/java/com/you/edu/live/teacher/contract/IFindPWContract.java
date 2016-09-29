package com.you.edu.live.teacher.contract;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public interface IFindPWContract {

    public interface IFindPWPresenter {

        public void requestPhoneValidCode(String phoneNumber, String type);

        public void requestVerifyCode(String phoneNumber, String type,
                                      String validCode);

    }

    public interface IFindPWView extends IMvpBaseContract.IMvpView {

        public void showLoading();

        public void hideLoading();

        public void onValidCodeSuccess(Boolean isValidCodeSuccess);

        public void onCookieSuccess(String cookie);

        public void onVerifyCodeSuccess(Boolean message);

        public void showError(int operator, String error);
    }
}
