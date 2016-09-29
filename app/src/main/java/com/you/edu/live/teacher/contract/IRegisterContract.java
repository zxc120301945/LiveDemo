package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.User;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public interface IRegisterContract {

    public interface IRegisterPresenter {

        public void requestPhoneValidCode(String phoneNumber, String type);

        public void requestUserRegister(String phoneNumber, String validCode,
                                        String nick_name, String password, String serial_no);

    }

    public interface IRegisterView extends IMvpBaseContract.IMvpView {

        public void showLoading();

        public void hideLoading();

        public void onValidCodeSuccess(Boolean isValidCodeSuccess);

        public void onUserRegisterSuccess(User user);

        public void showError(int operator, String error);
    }
}
