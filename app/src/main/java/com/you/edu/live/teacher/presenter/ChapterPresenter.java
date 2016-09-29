package com.you.edu.live.teacher.presenter;

import android.util.Log;

import com.you.edu.live.teacher.contract.IChapterContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.ChapterModel;
import com.you.edu.live.teacher.model.bean.Chapter;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class ChapterPresenter extends BaseMvpPresenter<IChapterContract.IChapterView, ChapterModel> implements IChapterContract.IChapterPresenter {

    public ChapterPresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new ChapterModel(httpApi, cache));
        }
    }

    @Override
    public void requestChapter(int coid, int type) {
        if (this.isAttachModel()) {
            getModel().requestChapterDetail(coid, type, mChapterRequestListener);
        }
    }



    @Override
    public void requestDeleteChapter(int chid) {
        if (this.isAttachView()) {
            getView().showLoadingView();
        }
        if (this.isAttachModel()) {
            getModel().requestDeleteChapter(chid, mDeleteChapterRequestListener);
        }
    }

    @Override
    public void requestLiveChapter(int coid, int type) {
        if (this.isAttachModel()) {
            getModel().requestChapterDetail(coid, type, mLiveChapterRequestListener);
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

    private IOperationListener<List<Chapter>> mChapterRequestListener = new IOperationListener<List<Chapter>>() {

        @Override
        public void onOperationSuccess(List<Chapter> chapterList) {
            // TODO Auto-generated method stub
            if (ChapterPresenter.this.isAttachView()) {
                getView().onChapterSuccess(chapterList);
            }
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (ChapterPresenter.this.isAttachView()) {
                getView().showError(error);
            }
        }

    };

    private IOperationListener<List<Chapter>> mLiveChapterRequestListener = new IOperationListener<List<Chapter>>() {

        @Override
        public void onOperationSuccess(List<Chapter> chapterList) {
            // TODO Auto-generated method stub
            if (ChapterPresenter.this.isAttachView()) {
                getView().onLiveChapterSuccess(chapterList);
            }
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            // TODO Auto-generated method stub
            if (ChapterPresenter.this.isAttachView()) {
                getView().showError(error);
            }
        }

    };

    private IOperationListener<String> mDeleteChapterRequestListener = new IOperationListener<String>() {

        @Override
        public void onOperationSuccess(String msg) {
            // TODO Auto-generated method stub
            if (!isAttachView()) {
                return;
            }
            getView().hideLoadingView();
            getView().onDeleteChapterSuccess(msg);
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
