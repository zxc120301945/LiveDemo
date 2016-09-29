package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

/**
 * 作者：XingRongJing on 2016/6/28.
 */
public interface IMvpBaseContract {

    public interface IMvpView<P extends IMvpPresenter> {
        public P getMvpPresenter();
    }

    public interface IMvpModel {
        public void setHttpApi(IHttpApi mHttpApi);

        public IHttpApi getHttpApi();

        public ICache getCache();

        public void setCache(ICache mCache);

        public int getHttpTag();

        public void setHttpTag(int mHttpTag);

        public void destroy();
    }

    public interface IMvpPresenter<V extends IMvpView, M extends IMvpModel> {

        /**
         * Set or attach the view to this presenter
         */
        public void attachView(V view);

        /**
         * Will be called if the view has been destroyed. Typically this method will
         * be invoked from <code>Activity.detachView()</code> or
         * <code>Fragment.onDestroyView()</code>
         */
        public void detachView();

        public boolean isAttachView();

        public V getView();


        public void attachModel(M model);


        public void detachModel();

        public boolean isAttachModel();

        public M getModel();

        public void destroy();
    }
}
