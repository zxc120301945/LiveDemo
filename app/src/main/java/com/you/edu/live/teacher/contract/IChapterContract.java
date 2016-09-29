package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.Chapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public interface IChapterContract {

    public interface IChapterPresenter {

        public void requestChapter(int coid, int type);

        public void requestDeleteChapter(int chid);

        public void requestGetLive(int coid, int chid);

        public void requestLiveChapter(int coid, int type);
    }

    public interface IChapterView extends IMvpBaseContract.IMvpView {

        /**
         * 加载/缓冲View
         */
        public void showLoadingView();

        /**
         * 隐藏加载/缓冲View
         */
        public void hideLoadingView();

        public void onChapterSuccess(List<Chapter> hourList);

        public void onLiveChapterSuccess(List<Chapter> hourList);

        public void onDeleteChapterSuccess(String msg);

        public void onRoomInfoSuccess(Map<String, String> map);

        public void showError(String error);
    }
}
