package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.ILoginPhoneContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.LoginPhoneModel;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

public class LoginPhonePresenter extends
        BaseMvpPresenter<ILoginPhoneContract.ILoginPhoneView, LoginPhoneModel> implements ILoginPhoneContract.ILoginPhonePresenter {

    public LoginPhonePresenter(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
        this.attachModel(new LoginPhoneModel(httpApi, cache));
    }

    public void requestPhoneValidCode(String phoneNumber, String type) {
        if (this.getModel() != null) {
            this.getModel().requestPhoneValidCode(phoneNumber, type,
                    mValidCodeRequestListener);
        }
    }

    public void requestOneKeyLogin(String phoneNumber, String validCode,
                                   String serial_no) {
        if (this.isAttachView()) {
            this.getView().showLoading();
        }
        if (this.getModel() != null) {
            this.getModel().requestOneKeyLogin(phoneNumber, validCode,
                    serial_no, mOneKeyLoginRequestListener);
        }
    }

    private IOperationListener<Boolean> mValidCodeRequestListener = new IOperationListener<Boolean>() {

        @Override
        public void onOperationSuccess(Boolean isValidCodeSuccess) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().onValidCodeSuccess(isValidCodeSuccess);
        }

        @Override
        public void onOperationFails(int operator, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().showError(operator, error);
        }

    };

    private IOperationListener<User> mOneKeyLoginRequestListener = new IOperationListener<User>() {

        @Override
        public void onOperationSuccess(User user) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoading();
            getView().onOneKeyLoginSuccess(user);
        }

        @Override
        public void onOperationFails(int operator, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoading();
            getView().showError(operator, error);
        }

    };
}
