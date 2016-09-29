package com.you.edu.live.teacher;

import com.you.edu.live.teacher.contract.IRefreshChangeListener;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据变化反馈助手
 *
 * @author WanZhiYuan
 */
public class DataChangeHelper {

    /**
     * User操作符
     **/
    public static final int OPERATOR_MAIN_REFRESH = 1;

    private List<IRefreshChangeListener> mRefreshChangeListeners;
    private static DataChangeHelper mInstance;

    private DataChangeHelper() {
        mRefreshChangeListeners = new ArrayList<IRefreshChangeListener>();
    }

    static DataChangeHelper getDataChangeHouseKeeper() {
        if (null == mInstance) {
            mInstance = new DataChangeHelper();
        }
        return mInstance;
    }

    public void registerChangeRefreshListener(IRefreshChangeListener listener) {
        if (null == mRefreshChangeListeners || mRefreshChangeListeners.contains(listener)) {
            return;
        }
        mRefreshChangeListeners.add(listener);
    }

    public void unregisterChangeRefreshListener(IRefreshChangeListener listener) {
        if (null == mRefreshChangeListeners || !mRefreshChangeListeners.contains(listener)) {
            return;
        }
        mRefreshChangeListeners.remove(listener);
    }


    public void notifyChangeRefresh(boolean isRefresh, int operator,String isRefreshTabName) {
        if (null == mRefreshChangeListeners) {
            return;
        }
        int size = mRefreshChangeListeners.size();
        for (int i = 0; i < size; i++) {
            IRefreshChangeListener listener = mRefreshChangeListeners.get(i);
            listener.onRefreshChange(isRefresh, operator,isRefreshTabName);
        }
    }


    public void clearAll() {
        if (null != mRefreshChangeListeners) {
            mRefreshChangeListeners.clear();
        }
    }

    public void destroy() {
        this.clearAll();
        mRefreshChangeListeners = null;
    }

}
