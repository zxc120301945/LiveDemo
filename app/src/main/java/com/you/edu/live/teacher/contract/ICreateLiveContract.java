package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.Room;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public interface ICreateLiveContract {
    public interface ICreateLivePresenter {


        public void requestCreateClass(int coid, String course_name, int chapter_type, int weight, boolean pos, int chapter_time
                , String play_start, boolean is_free, String resid, String thumbnail, int chid, String description
        );

        public void requestGetLive(int coid, String chid);

        public void shareToWeixinFriends(Platform plat, String status, String teacherName,
                                         String date, String className, String shareUrl, String thumb);

        public void shareToWeixinComments(Platform plat, String status, String teacherName,
                                          String date, String className, String shareUrl, String thumb);

        public void shareToSinaWeibo(Platform plat, String status, String teacherName,
                                     String date, String className, String shareUrl, String thumb);

    }

    public interface ICreateLiveView extends IMvpBaseContract.IMvpView {
        /**
         * 加载/缓冲View
         */
        public void showLoadingView();

        /**
         * 隐藏加载/缓冲View
         */
        public void hideLoadingView();

        public void onChapterSuccess(Map<String, String> chapter);

        public void onRoomSuccess(Room room);

        public void showError(String error);

        public void onShareComplete(Platform plat,HashMap<String, Object> info);

        public void onShareError(Platform plat, String error);

        public void onShareCancel(Platform plat);
    }
}
