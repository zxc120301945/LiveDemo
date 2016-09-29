package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.ICreateLiveContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.contract.IThirdActionListener;
import com.you.edu.live.teacher.model.CreateLiveModel;
import com.you.edu.live.teacher.model.bean.Room;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class CreateLivePresenter extends BaseMvpPresenter<ICreateLiveContract.ICreateLiveView, CreateLiveModel> implements ICreateLiveContract.ICreateLivePresenter {

    public CreateLivePresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new CreateLiveModel(httpApi, cache));
        }
    }

    @Override
    public void requestCreateClass(int coid, String course_name, int chapter_type, int weight, boolean pos, int chapter_time, String play_start, boolean is_free, String resid, String thumbnail, int chid, String description) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().requestCreateClass(coid, course_name, chapter_type, weight, pos, chapter_time, play_start, is_free, resid, thumbnail, chid, description, mChapterRequestListener);
        }
    }

    @Override
    public void requestGetLive(int coid, String chid) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
//        if (this.isAttachModel()) {
//            getModel().requestGetLive(coid,chid,mRoomRequestListener);
//        }
    }

    @Override
    public void shareToWeixinFriends(Platform plat, String status, String teacherName, String date,
                                     String className, String shareUrl, String thumb) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().shareToWeixinFriends(plat, status, teacherName, date, className, shareUrl, thumb, mShareListener);
        }
    }

    @Override
    public void shareToWeixinComments(Platform plat, String status, String teacherName, String date, String className,
                                      String shareUrl, String thumb) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().shareToWeixinComments(plat, status, teacherName, date, className, shareUrl, thumb, mShareListener);
        }
    }

    @Override
    public void shareToSinaWeibo(Platform plat, String status, String teacherName, String date,
                                 String className, String shareUrl, String thumb) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().shareToSinaWeibo(plat, status, teacherName, date, className, shareUrl, thumb, mShareListener);
        }
    }


    private IOperationListener<Map<String, String>> mChapterRequestListener = new IOperationListener<Map<String, String>>() {

        @Override
        public void onOperationSuccess(Map<String, String> data) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onChapterSuccess(data);
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

    private IThirdActionListener mShareListener = new IThirdActionListener() {

        @Override
        public void onComplete(Platform plat, HashMap<String, Object> info) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onShareComplete(plat,info);
        }

        @Override
        public void onError(Platform plat, String error) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onShareError(plat,error);
        }

        @Override
        public void onCancel(Platform plat) {
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onShareCancel(plat);
        }
    };
}
