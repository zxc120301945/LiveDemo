package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.model.bean.TeacherInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public interface IMainContract {

    public interface IMainPresenter {
        public void requestTeacherInfo(boolean isFirstRefresh,boolean isNeedCache);

        public void requestCache();

    }

    public interface IMainView extends IMvpBaseContract.IMvpView {
        /**
         * 加载/缓冲View
         *
         */
        public void showLoadingView();

        /**
         * 隐藏加载/缓冲View
         *
         */
        public void hideLoadingView();

        public void onTeacherInfoSuccess(TeacherInfo info);

        public void onCacheSuccess(TeacherInfo info);

        public void showError(String error);
    }
}
