package com.you.edu.live.teacher.model;

import android.content.Context;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.AppUpdate;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;


public class AppUpdateModel extends BaseMvpModel implements
		IHttpCallback<RespOut> {

	private IOperationListener<AppUpdate> mIOperationListener;
	private Context mCtx;

	public AppUpdateModel(IHttpApi httpApi, ICache cache) {
		super(httpApi, cache);
		// TODO Auto-generated constructor stub
	}

	public void setContext(Context ctx) {
		this.mCtx = ctx;
	}

	public Context getContext() {
		return mCtx;
	}

	/**
	 * 请求服务器上的apk信息
	 * 
	 */
	public void requestAppUpdateInfo(Context ctx,
			IOperationListener<AppUpdate> listener) {
		this.setAppUpdateInfoRequestListener(listener);
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("appversion", this.getCoreApp().getAppVersion());
//		Map<String, String> headers = AppHelper
//				.getAppHelper()
//				.getHttpConfigKeeper(ctx)
//				.getHttpHeaders(
//						AppHelper.getAppHelper().getUser(ctx).getCookie());
		RequestTag tag = new RequestTag(this.hashCode(),
				GlobalConfig.Operator.OPERATOR_APP_UPDATE);
		this.getHttpApi().request(Request.Method.GET,
				GlobalConfig.HttpUrl.APP_UPGRADE, null, null, this, tag);
	}

	@Override
	public void onResponse(RespOut out) {
		// TODO Auto-generated method stub
		if (null == out || null == out.param) {
			return;
		}
		RequestDataParser parser = new RequestDataParser(out.resp);
		int operator = (int) out.param.operator;
		switch (operator) {
		case GlobalConfig.Operator.OPERATOR_APP_UPDATE:// App升级信息获取成功
			if (this.getAppUpdateInfoRequestListener() == null) {
				return;
			}
			if (parser.isSuccess()) {// App升级信息获取成功
				String jsonData = parser
						.getValue(GlobalConfig.HttpJson.KEY_DATA);
				AppUpdate update = parser.getOne(jsonData, AppUpdate.class);
				if (null == update) {
					return;
				}
				this.getAppUpdateInfoRequestListener().onOperationSuccess(
						update);
			}
			break;
		}
	}

	@Override
	public void onResponseError(RespOut out) {
		// TODO Auto-generated method stub
		if (null == out || null == out.param) {
			return;
		}
		int operator = (int) out.param.operator;
		switch (operator) {
		case GlobalConfig.Operator.OPERATOR_APP_UPDATE:
			this.getAppUpdateInfoRequestListener().onOperationFails(
					GlobalConfig.Operator.OPERATOR_APP_UPDATE,
					GlobalConfig.HTTP_ERROR_TIPS);
			break;
		}
	}

	public void setAppUpdateInfoRequestListener(
			IOperationListener<AppUpdate> listener) {
		this.mIOperationListener = listener;
	}

	public IOperationListener<AppUpdate> getAppUpdateInfoRequestListener() {
		return mIOperationListener;
	}
}
