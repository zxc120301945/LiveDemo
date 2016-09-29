package com.you.edu.live.teacher.presenter;

import android.util.Log;

import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.contract.IRecentLiveContract;
import com.you.edu.live.teacher.contract.IUserCourseContract;
import com.you.edu.live.teacher.model.RecentLiveModel;
import com.you.edu.live.teacher.model.UserCourseModel;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.model.bean.Live;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class RecentLivePresenter extends BaseMvpPresenter<IRecentLiveContract.IRecentLiveView, RecentLiveModel> implements IRecentLiveContract.IRecentLivePresenter {

    public RecentLivePresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new RecentLiveModel(httpApi, cache));
        }
    }

    @Override
    public void requestLiveCache() {
        if (this.isAttachModel()) {
            getModel().requestLiveCache(mCacheRequestListener);
        }
    }

    @Override
    public void requestRecentLive(String play_type, int size, int page, boolean isNeedCache) {

        if (this.isAttachModel()) {
            getModel().requestRecentLive(play_type, size, page, isNeedCache, mRecentLiveRequestListener);
        }
    }

    @Override
    public void requestGetLive(int coid, int chid) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().requestGetLive(coid, chid, mRoomInfoRequestListener);
        }
    }


    private IOperationListener<List<Live>> mCacheRequestListener = new IOperationListener<List<Live>>() {

        @Override
        public void onOperationSuccess(List<Live> data) {
            if (!isAttachView()) {
                return;
            }
            getView().onCacheSuccess(data);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
        }
    };

    private IOperationListener<List<Live>> mRecentLiveRequestListener = new IOperationListener<List<Live>>() {

        @Override
        public void onOperationSuccess(List<Live> data) {
            if (!isAttachView()) {
                return;
            }
            getView().onLiveSuccess(data);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
            getView().showError(error);
        }
    };

    private IOperationListener<Map<String,String>> mRoomInfoRequestListener = new IOperationListener<Map<String,String>>() {

        @Override
        public void onOperationSuccess(Map<String,String> map) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onRoomInfoSuccess(map);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().showError(error);
        }
    };
}
