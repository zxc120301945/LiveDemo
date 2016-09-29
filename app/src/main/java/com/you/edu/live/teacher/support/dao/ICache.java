package com.you.edu.live.teacher.support.dao;


import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

public interface ICache {

    public void saveCache(String key, String content);

    /**
     * &#x7f13;&#x5b58;expireTime&#x79d2;&#x7684;&#x5b57;&#x7b26;&#x4e32;&#xff08;&#x8d85;&#x65f6;&#x5219;&#x4f1a;&#x5728;&#x83b7;&#x53d6;&#x65f6;&#x79fb;&#x9664;&#xff09;
     *
     * @param key
     * @param content
     * @param expireTime
     */
    public void saveCache(String key, String content, int expireTime);

    /**
     * 缓存content到本地（缓存时长为expireTime秒）
     *
     * @param key
     * @param content
     * @param listener   回调
     * @param expireTime 过期时间-秒为单位，如果小于0，则认为永久保存
     */
    public void saveCache(String key, String content,
                          IDaoCallback<RespOut> listener, int expireTime);

    /**
     * 获取字符串缓存
     *
     * @param key
     * @param listener
     */
    public void getCacheAsString(String key, IDaoCallback<RespOut> listener);

    /**
     * 获取字符串缓存
     *
     * @param key
     * @param listener
     * @param tag      回调tag
     */
    public void getCacheAsString(String key, IDaoCallback<RespOut> listener,
                                 RequestTag tag);

    /**
     * 删除某个缓存
     *
     * @param key
     * @return
     */
    public boolean remove(String key);

    /**
     * 删除所有缓存
     */
    public void removeAll();

    /**
     * 获取缓存大小
     */
    public long getCacheSize();

    public void destroy();
}
