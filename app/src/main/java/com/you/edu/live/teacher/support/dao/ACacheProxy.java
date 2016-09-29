package com.you.edu.live.teacher.support.dao;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

import java.util.concurrent.ExecutorService;


/**
 * 缓存管家(目前主要是缓存http的字符串请求，缓存于context.getCacheDir()下，会随着app卸载被删除，默认最大缓存5M）
 *
 * @author XingRongJing
 */
public class ACacheProxy extends BaseCache implements ICache {

    private static final int MAX_SIZE = 1000 * 1000 * 5; // 5 mb
    private ACache mCache;

    public ACacheProxy(Context ctx, ExecutorService mExecutors,
                       Handler mHandler) {
        super(mExecutors, mHandler);
        // TODO Auto-generated constructor stub
        if (null == ctx) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        mCache = ACache.get(ctx, MAX_SIZE, Integer.MAX_VALUE);
    }


    private ACache getCache() {
        return mCache;
    }

    @Override
    public void saveCache(String key, String content) {
        this.saveCache(key, content, null, 0);
    }

    @Override
    public void saveCache(String key, String content, int expireTime) {
        this.saveCache(key, content, null, expireTime);
    }

    @Override
    public void saveCache(String key, String content,
                          IDaoCallback<RespOut> listener, int expireTime) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(content)) {
            return;
        }
        this.getExecutors().execute(
                new PutCacheTask(this.getCache(), key, content, listener,
                        expireTime));
    }

    @Override
    public void getCacheAsString(String key, IDaoCallback<RespOut> listener) {
        if (TextUtils.isEmpty(key)) {
            if (null != listener) {
                listener.onDaoResponse(new RespOut(null));
            }
            return;
        }
        this.getExecutors().execute(
                new GetCacheAsStringTask(this.getCache(), key, listener, null));
    }

    @Override
    public void getCacheAsString(String key, IDaoCallback<RespOut> listener,
                                 RequestTag tag) {
        if (TextUtils.isEmpty(key)) {
            if (null != listener) {
                listener.onDaoResponse(new RespOut(tag));
            }
            return;
        }
        this.getExecutors().execute(
                new GetCacheAsStringTask(this.getCache(), key, listener, tag));
    }

    @Override
    public boolean remove(String key) {
        if (null == this.getCache() || TextUtils.isEmpty(key)) {
            return false;
        }
        return this.getCache().remove(key);
    }

    @Override
    public void removeAll() {
        if (null == this.getCache()) {
            return;
        }
        this.getCache().clear();
    }

    @Override
    public long getCacheSize() {
        if (null == this.getCache()) {
            return 0l;
        }
        return this.getCache().getCacheSize();
    }

    @Override
    public void destroy() {
        if (null != this.getExecutors()) {
            this.getExecutors().shutdown();
        }

        mCache = null;
    }

    private class PutCacheTask implements Runnable {
        private String mKey, mContent;
        private IDaoCallback<RespOut> mListener;
        private ACache mCache;
        /**
         * 缓存过期时间--秒为单位
         **/
        private int mExpireTime = 0;

        public PutCacheTask(ACache cache, String key, String content,
                            IDaoCallback<RespOut> listener, int expireTime) {
            this.mCache = cache;
            this.mKey = key;
            this.mContent = content;
            this.mListener = listener;
            this.mExpireTime = expireTime;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean success = false;
            if (null != mCache) {
                if (mExpireTime > 0) {
                    mCache.put(mKey, mContent, mExpireTime);
                } else {

                    mCache.put(mKey, mContent);
                }
                success = true;
            }
            final RespOut resp = new RespOut(null);
            resp.isSuccess = success;
            getHandler().post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (null != mListener) {

                        mListener.onDaoResponse(resp);
                    }

                }
            });

        }

    }

    private class GetCacheAsStringTask implements Runnable {
        private String mKey;
        private IDaoCallback<RespOut> mListener;
        private ACache mCache;
        private RequestTag mTag;

        public GetCacheAsStringTask(ACache cache, String key,
                                    IDaoCallback<RespOut> listener, RequestTag tag) {
            this.mCache = cache;
            this.mKey = key;
            this.mListener = listener;
            this.mTag = tag;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean success = false;
            String result = null;
            final RespOut resp = new RespOut(mTag);
            if (null != mCache) {
                result = mCache.getAsString(mKey);
                success = true;
            }
            resp.isSuccess = success;
            resp.result = result;
            getHandler().post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (null != mListener) {
                        mListener.onDaoResponse(resp);
                    }
                }
            });

        }

    }

}
