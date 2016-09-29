package com.you.edu.live.teacher.contract;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public interface IOperationListener<T> {

        /** 请求成功的回调 **/
        public void onOperationSuccess(T data);

        /** 请求失败的回调 **/
        public void onOperationFails(int errorCode, String error);
}
