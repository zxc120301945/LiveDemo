package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.User;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public interface ILoginPhoneContract {

    public interface ILoginPhonePresenter {

        public void requestPhoneValidCode(String phoneNumber, String type);

        public void requestOneKeyLogin(String phoneNumber, String validCode,
                           String serial_no);

    }

    public interface ILoginPhoneView extends IMvpBaseContract.IMvpView {
        public void showLoading();

        public void hideLoading();

        public void onOneKeyLoginSuccess(User user);

        public void onValidCodeSuccess(Boolean isValidCodeSuccess);

        public void showError(int operator, String error);
    }
}
