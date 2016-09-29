package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.INewPWContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.NewPWModel;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

public class NewPWPresenter extends BaseMvpPresenter<INewPWContract.INewPWView, NewPWModel> implements INewPWContract.INewPWPresenter {

    public NewPWPresenter(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
        this.attachModel(new NewPWModel(httpApi, cache));
    }

    public void requestGetPwd(String password, String serial_no, String cookie) {
        if (this.isAttachView()) {
            getView().showLoading();
        }
        if (this.getModel() != null) {
            this.getModel().requestGetPwd(password, serial_no, cookie,
                    mGetPwdRequestListener);
        }
    }

    private IOperationListener<User> mGetPwdRequestListener = new IOperationListener<User>() {

        @Override
        public void onOperationSuccess(User user) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoading();
            getView().onSuccess(user);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoading();
            getView().showError(errorCode, error);
        }

    };
}
