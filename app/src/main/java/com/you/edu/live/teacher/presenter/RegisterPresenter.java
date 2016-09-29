package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.contract.IRegisterContract;
import com.you.edu.live.teacher.model.RegisterModel;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

public class RegisterPresenter extends
		BaseMvpPresenter<IRegisterContract.IRegisterView, RegisterModel> implements IRegisterContract.IRegisterPresenter {

	public RegisterPresenter(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
		this.attachModel(new RegisterModel(httpApi, cache));
	}

	public void requestPhoneValidCode(String phoneNumber, String type) {
		if (this.getModel() != null) {
			this.getModel().requestPhoneValidCode(phoneNumber, type,
					mValidCodeRequestListener);
		}
	}

	public void requestUserRegister(String phoneNumber, String validCode,
			String nick_name, String password, String serial_no) {
		if(this.isAttachView()){
			this.getView().showLoading();
		}
		if (this.getModel() != null) {
			this.getModel().requestUserRegister(phoneNumber, validCode,
					nick_name, password, serial_no,
					mUserRegisterRequestListener);
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

	private IOperationListener<User> mUserRegisterRequestListener = new IOperationListener<User>() {

		@Override
		public void onOperationSuccess(User user) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			getView().hideLoading();
			getView().onUserRegisterSuccess(user);
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
