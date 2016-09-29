package com.you.edu.live.teacher.presenter.helper;

import com.you.edu.live.teacher.view.custom.danmu.IDanmu;

import java.util.Stack;


/**
 * 弹幕交流管家（管理弹幕进出栈、弹幕优先级、弹幕缓存等）
 *
 * @author XingRongJing
 */
public class DanmuKeeper {

    /**
     * 优先级一级缓存-评论优先
     **/
    private Stack<IDanmu> mCachesPriorityFirst;
    /**
     * 优先级二级缓存-消息次之
     **/
    private Stack<IDanmu> mCachesPrioritySecond;

    DanmuKeeper() {
        mCachesPriorityFirst = new Stack<>();
        mCachesPrioritySecond = new Stack<>();
    }

    /**
     * 进栈
     *
     * @param danmu
     */
    public void pushDanmu(IDanmu danmu) {
        if (null == danmu) {
            return;
        }
        int priority = danmu.getPoriority();
        switch (priority) {
            case IDanmu.PORIORITY_FIRST:
                if (null != mCachesPriorityFirst) {
                    mCachesPriorityFirst.push(danmu);
                }
                break;
            case IDanmu.PORIORITY_SECOND:
                if (null != mCachesPrioritySecond) {
                    mCachesPrioritySecond.push(danmu);
                }
                break;
        }
    }

    /**
     * 获取并移除栈底Danmu Ps：按照优先级弹出
     *
     * @return
     */
    public IDanmu pomDanmu() {
        IDanmu priorityFirstDanmu = this.pomDanmu(mCachesPriorityFirst);
        if (null != priorityFirstDanmu) {
            return priorityFirstDanmu;
        }
        return this.pomDanmu(mCachesPrioritySecond);
    }

    /**
     * 获取并移除栈底Danmu
     *
     * @return
     */
    private IDanmu pomDanmu(Stack<IDanmu> caches) {
        if (this.isEmpty(caches)) {
            return null;
        }
        int index = 0;
        IDanmu danmu = caches.get(index);
        caches.remove(index);
        return danmu;
    }

    public boolean isEmpty(Stack<IDanmu> caches) {
        return null == caches ? true : caches.isEmpty();
    }

    public void destroy() {
        if (null != mCachesPriorityFirst) {
            mCachesPriorityFirst.clear();
            mCachesPriorityFirst = null;
        }

        if (null != mCachesPrioritySecond) {
            mCachesPrioritySecond.clear();
            mCachesPrioritySecond = null;
        }
    }

}
