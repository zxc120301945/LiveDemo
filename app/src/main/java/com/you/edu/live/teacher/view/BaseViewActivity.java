package com.you.edu.live.teacher.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.widget.SystemBarTintManager;


public abstract class BaseViewActivity extends AppCompatActivity {


//	private LoadingHelper mLoadingHelper;
    /**
     * 页面名字-用于百度统计（默认是类名）
     **/
    private String mPageName = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		StatService.onPageStart(this, mPageName);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//		StatService.onPageEnd(this, mPageName);
    }

    protected void setPageName(String pageName) {
        this.mPageName = pageName;
    }


//	protected void setLoadingHelper(LoadingHelper loadingHelper) {
//		this.mLoadingHelper = loadingHelper;
//	}

//	protected LoadingHelper getLoadingHelper() {
//		return mLoadingHelper;
//	}

    protected void showToast(String msg) {
        AppHelper.getAppHelper().showToast(this, msg);
    }

    protected IHttpApi getHttpApi() {
        return AppHelper.getAppHelper().getHttpApi(this);
    }

    protected ICache getCache() {
        return AppHelper.getAppHelper().getCache(this);
    }

    protected boolean isNetworkEnable() {
        return AppHelper.getAppHelper().isNetworkEnable(this);
    }

    protected boolean isWifiConnected(Context ctx) {
        return AppHelper.getAppHelper().isWifiConnected(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (this.setResultBack()) {
            super.onBackPressed();
        }
    }

    /**
     * 返回时调用（可在此设置返回值）
     *
     * @return true 是否finish此activity，默认是
     */
    protected boolean setResultBack() {
        return true;
    }

}
