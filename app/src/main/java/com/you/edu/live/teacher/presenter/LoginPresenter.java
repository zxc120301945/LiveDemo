package com.you.edu.live.teacher.presenter;

import android.graphics.Bitmap;

import com.you.edu.live.teacher.contract.ILoginContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.LoginModel;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

/**
 * @Title: Login Presenter
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/29
 * @Time 下午15:39
 * @Version
 */
public class LoginPresenter extends BaseMvpPresenter<ILoginContract.ILoginView, LoginModel> implements ILoginContract.ILoginPresenter {

    public LoginPresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new LoginModel(httpApi, cache));
        }
    }

    @Override
    public void requestLogin(String userId, String password, String verify, String serial) {
        if (this.isAttachView()) {
            getView().showLoadingView(true);
        }
        if (this.isAttachModel()) {
            getModel().requestLogin(userId, password, verify, serial, mLoginRequestListener);
        }
    }

    @Override
    public void requestThirdPartyLogin(String plat, String userId, String openId, String username, String serial) {
        if (this.isAttachView()) {
            getView().showLoadingView(true);
        }
        if (this.isAttachModel()) {
            getModel().requestThirdPartyLogin(plat, userId, openId, username, serial, mLoginRequestListener);
        }
    }

    @Override
    public void requestImageVerifyCode(int verifyType) {
        if (this.isAttachModel()) {
            getModel().requestImageVerifyCode(verifyType,mImageVerityCodeListener);
        }
    }

    private IOperationListener<User> mLoginRequestListener = new IOperationListener<User>() {

        @Override
        public void onOperationSuccess(User user) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView(true);
            getView().onLoginSuccess(user);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView(true);
            getView().showError(error);
        }

    };

    private IOperationListener<Bitmap> mImageVerityCodeListener = new IOperationListener<Bitmap>() {

        @Override
        public void onOperationSuccess(Bitmap data) {
            if (!isAttachView()) {
                return;
            }
            getView().onVerifyCodeSuccess(data);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
        }
    };
}
