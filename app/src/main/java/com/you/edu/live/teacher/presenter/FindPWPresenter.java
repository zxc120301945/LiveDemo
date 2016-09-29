package com.you.edu.live.teacher.presenter;


import android.content.Context;

import com.you.edu.live.teacher.contract.IFindPWContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.FindPWModel;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

public class FindPWPresenter extends BaseMvpPresenter<IFindPWContract.IFindPWView, FindPWModel> implements IFindPWContract.IFindPWPresenter  {

	public FindPWPresenter(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
		this.attachModel(new FindPWModel(httpApi, cache));
	}

	public void requestPhoneValidCode(String phoneNumber, String type) {
		if (this.getModel() != null) {
			this.getModel().requestPhoneValidCode(phoneNumber, type,
					mValidCodeRequestListener);
		}
	}

	public void requestVerifyCode(String phoneNumber, String type,
			String validCode) {
		if(this.isAttachView()){
			this.getView().showLoading();
		}
		if (this.getModel() != null) {
			this.getModel().requestVerifyCode(phoneNumber, type, validCode,
					mVerifyCodeRequestListener, mCookieRequestListener);
		}
	}

	public void setContext(Context ctx) {
		if (this.getModel() != null) {
			this.getModel().setContext(ctx);
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

	private IOperationListener<String> mCookieRequestListener = new IOperationListener<String>() {

		@Override
		public void onOperationSuccess(String cookie) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			getView().onCookieSuccess(cookie);
		}

		@Override
		public void onOperationFails(int operator, String error) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			// getView().showError(operator, error);
		}

	};

	private IOperationListener<Boolean> mVerifyCodeRequestListener = new IOperationListener<Boolean>() {

		@Override
		public void onOperationSuccess(Boolean isSuccess) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			getView().hideLoading();
			getView().onVerifyCodeSuccess(isSuccess);
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
