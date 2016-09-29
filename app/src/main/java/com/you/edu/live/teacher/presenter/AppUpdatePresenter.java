package com.you.edu.live.teacher.presenter;

import android.content.Context;

import com.you.edu.live.teacher.contract.IAppUpdateContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.AppUpdateModel;
import com.you.edu.live.teacher.model.bean.AppUpdate;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;


/**
 * app升级
 * @author WanZhiYuan
 *
 */
public class AppUpdatePresenter extends
		BaseMvpPresenter<IAppUpdateContract.IAppUpdateView, AppUpdateModel> implements IAppUpdateContract.IAppUpdatePresenter  {

	public AppUpdatePresenter(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
		this.attachModel(new AppUpdateModel(httpApi, cache));
	}

	public void requestAppUpdateInfo(Context ctx) {
		if (this.getModel() != null) {
			this.getModel().requestAppUpdateInfo(ctx, mGetPwdRequestListener);
		}
	}

	private IOperationListener<AppUpdate> mGetPwdRequestListener = new IOperationListener<AppUpdate>() {

		@Override
		public void onOperationSuccess(AppUpdate appInfo) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			getView().onSuccess(appInfo);
		}

		@Override
		public void onOperationFails(int errorCode, String error) {
			// TODO Auto-generated method stub
			if (!isAttachView()) {
				return;
			}
			getView().showError(errorCode, error);
		}

	};

}
