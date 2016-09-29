package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.IMainContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.MainModel;
import com.you.edu.live.teacher.model.bean.TeacherInfo;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class MainPresenter extends BaseMvpPresenter<IMainContract.IMainView, MainModel> implements IMainContract.IMainPresenter {

    public MainPresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new MainModel(httpApi, cache));
        }
    }



    @Override
    public void requestTeacherInfo(boolean isFirstRefresh,boolean isNeedCache) {
        if (this.isAttachView()) {
            if(isFirstRefresh){
                getView().showLoadingView();
            }
        }
        if (this.isAttachModel()) {
            getModel().requestTeacherInfo(isNeedCache,mTeacherInfoRequestListener);
        }
    }

    @Override
    public void requestCache() {
        if (this.isAttachModel()) {
            getModel().requestCache(mCacheRequestListener);
        }
    }


    private IOperationListener<TeacherInfo> mTeacherInfoRequestListener = new IOperationListener<TeacherInfo>() {

        @Override
        public void onOperationSuccess(TeacherInfo info) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onTeacherInfoSuccess(info);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().showError(error);
        }

    };

    private IOperationListener<TeacherInfo> mCacheRequestListener = new IOperationListener<TeacherInfo>() {

        @Override
        public void onOperationSuccess(TeacherInfo info) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().onCacheSuccess(info);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
        }

    };
}
