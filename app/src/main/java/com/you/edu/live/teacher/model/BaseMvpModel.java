package com.you.edu.live.teacher.model;

import com.you.edu.live.teacher.contract.IMvpBaseContract;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

/**
 * 作者：XingRongJing on 2016/6/28.
 */
public class BaseMvpModel implements IMvpBaseContract.IMvpModel {
    /**
     * 取消该Model下所有请求的tag
     **/
    private int mHttpTag = this.hashCode();
    /**
     * 请求助手
     **/
    private IHttpApi mHttpApi;
    /**
     * 本地缓存助手--如该model不需缓存，则不用设置
     **/
    private ICache mCache;

    public BaseMvpModel() {
        this(null);
    }

    public BaseMvpModel(IHttpApi httpApi) {
        this(httpApi, null);
    }

    public BaseMvpModel(IHttpApi httpApi, ICache cache) {
        this.setHttpApi(httpApi);
        this.setCache(cache);
    }


    @Override
    public void setHttpApi(IHttpApi mHttpApi) {
        this.mHttpApi = mHttpApi;
    }

    @Override
    public IHttpApi getHttpApi() {
        return mHttpApi;
    }

    @Override
    public ICache getCache() {
        return mCache;
    }

    @Override
    public void setCache(ICache mCache) {
        this.mCache = mCache;
    }

    @Override
    public int getHttpTag() {
        return mHttpTag;
    }

    @Override
    public void setHttpTag(int mHttpTag) {
        this.mHttpTag = mHttpTag;
    }

    @Override
    public void destroy() {
        if (null != this.getHttpApi()) {
            this.getHttpApi().cancelAll(this.getHttpTag());
        }
    }
}
