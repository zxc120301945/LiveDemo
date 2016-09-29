package com.you.edu.live.teacher.contract;

import android.content.Context;

import com.you.edu.live.teacher.model.bean.AppUpdate;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public interface IAppUpdateContract {
    public interface IAppUpdatePresenter {

        public void requestAppUpdateInfo(Context ctx);

    }

    public interface IAppUpdateView extends IMvpBaseContract.IMvpView {

        public void onSuccess(AppUpdate appInfo);

        public void showError(int operator, String error);
    }
}
