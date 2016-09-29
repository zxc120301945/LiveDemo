package com.you.edu.live.teacher.contract;

/**
 * 同时刷新的回调接口（单一原则）
 *
 * @author XingRongJing
 */
public interface IRefreshChangeListener {

    /**
     * 首页 单个页签刷新页面时  其他页面也刷新
     *
     * @param isRefresh        是否刷新
     * @param operator         操作符
     * @param isRefreshTabName 现在下拉的是哪个页签
     */
    public void onRefreshChange(boolean isRefresh, int operator, String isRefreshTabName);
}
