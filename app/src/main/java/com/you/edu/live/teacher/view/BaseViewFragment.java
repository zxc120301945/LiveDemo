package com.you.edu.live.teacher.view;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;


public abstract class BaseViewFragment extends Fragment {

    /**
     * 页面名字-用于百度统计（默认是类名）
     **/
    private String mPageName = this.getClass().getSimpleName();
    //	private LoadingHelper mLoadingHelper;
    private boolean mIsFirst = true;


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		StatService.onPageStart(this.getContext(), mPageName);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//		StatService.onPageEnd(this.getContext(), mPageName);
    }

    protected void setPageName(String pageName) {
        this.mPageName = pageName;
    }

    public boolean isFirst() {
        return mIsFirst;
    }

    public void setIsFirst(boolean mIsFirst) {
        this.mIsFirst = mIsFirst;
    }

//	protected void setLoadingHelper(LoadingHelper loadingHelper) {
//		this.mLoadingHelper = loadingHelper;
//	}
//
//	protected LoadingHelper getLoadingHelper() {
//		return mLoadingHelper;
//	}

    protected void showToast(String msg) {
        AppHelper.getAppHelper().showToast(this.getContext(), msg);
    }

    protected IHttpApi getHttpApi() {
        return AppHelper.getAppHelper().getHttpApi(this.getContext());
    }

    protected ICache getCache() {
        return AppHelper.getAppHelper().getCache(this.getContext());
    }

    protected boolean isNetworkEnable() {
        return AppHelper.getAppHelper().isNetworkEnable(this.getContext());
    }

    protected boolean isWifiConnected(Context ctx) {
        return AppHelper.getAppHelper().isWifiConnected(this.getContext());
    }
}
