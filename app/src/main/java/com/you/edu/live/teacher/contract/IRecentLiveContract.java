package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.model.bean.Live;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public interface IRecentLiveContract {

    public interface IRecentLivePresenter {

        public void requestRecentLive(String play_type, int size, int page, boolean isNeedCache);

        public void requestGetLive(int coid, int chid);

        public void requestLiveCache();
    }

    public interface IRecentLiveView extends IMvpBaseContract.IMvpView {

        /**
         * 加载/缓冲View
         */
        public void showLoadingView();

        /**
         * 隐藏加载/缓冲View
         */
        public void hideLoadingView();

        public void onCacheSuccess(List<Live> liveList);

        public void onLiveSuccess(List<Live> liveList);

        public void onRoomInfoSuccess(Map<String,String> map);

        public void showError(String error);
    }
}
