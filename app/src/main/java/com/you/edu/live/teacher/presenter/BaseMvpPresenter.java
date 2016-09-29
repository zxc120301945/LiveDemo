package com.you.edu.live.teacher.presenter;

import com.you.edu.live.teacher.contract.IMvpBaseContract;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.lang.ref.WeakReference;

/**
 * MvpPresenter基类
 * 作者：XingRongJing on 2016/6/28.
 */
public abstract class BaseMvpPresenter<V extends IMvpBaseContract.IMvpView, M extends IMvpBaseContract.IMvpModel> implements IMvpBaseContract.IMvpPresenter<V, M> {
    private WeakReference<V> mViewRef;
    //    private WeakReference<M> mModelRef;
    private M mModel;
    /**
     * 请求助手
     **/
    private IHttpApi mHttpApi;
    /**
     * 本地缓存助手--如该model不需缓存，则不用设置
     **/
    private ICache mCache;

    public BaseMvpPresenter() {
        this(null);
    }

    public BaseMvpPresenter(IHttpApi api) {
        this(api, null);
    }

    public BaseMvpPresenter(IHttpApi api, ICache cache) {
        this.mHttpApi = api;
        this.mCache = cache;
    }

    @Override
    public void attachView(V view) {
        if (this.isAttachView()) {
            return;
        }
        mViewRef = new WeakReference<V>(view);
    }

    @Override
    public void detachView() {
        if (this.isAttachView()) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    @Override
    public boolean isAttachView() {
        return null != mViewRef && null != mViewRef.get();
    }

    @Override
    public V getView() {
        return this.isAttachView() ? mViewRef.get() : null;
    }

    @Override
    public void attachModel(M model) {
        if (this.isAttachModel()) {
            return;
        }
        this.mModel = model;
    }

    @Override
    public void detachModel() {
        if (this.isAttachModel()) {
            mModel = null;

        }
    }

    @Override
    public boolean isAttachModel() {
        return null != mModel;
    }

    @Override
    public M getModel() {
        return mModel;

    }

    @Override
    public void destroy() {
        this.detachView();
        if (this.isAttachModel()) {
            this.getModel().destroy();
        }
        this.detachModel();
    }
}
