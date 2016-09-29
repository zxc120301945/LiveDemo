package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.User;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public interface INewPWContract {
    public interface INewPWPresenter {

        public void requestGetPwd(String password, String serial_no, String cookie);

    }

    public interface INewPWView extends IMvpBaseContract.IMvpView {

        public void showLoading();

        public void hideLoading();

        public void onSuccess(User user);

        public void showError(int errorCode, String error);
    }
}
