package com.you.edu.live.teacher.contract;

import android.graphics.Bitmap;

import com.you.edu.live.teacher.model.bean.User;

/**
 * 登录契约接口
 * 作者：WanZhiYuan on 2016/6/29.
 */
public interface ILoginContract {

    public interface ILoginPresenter {
        public void requestLogin(String userId, String password, String verify,
                                 String serial);

        public void requestThirdPartyLogin(String plat, String userId,
                                           String openId, String username, String serial);

        public void requestImageVerifyCode(int verifyType);

    }

    public interface ILoginView extends IMvpBaseContract.IMvpView {
        /**
         * 加载/缓冲View
         *
         * @param isBuffering
         */
        public void showLoadingView(boolean isBuffering);

        /**
         * 隐藏加载/缓冲View
         *
         * @param isBuffering
         */
        public void hideLoadingView(boolean isBuffering);

        public void onLoginSuccess(User user);

        public void onVerifyCodeSuccess(Bitmap data);

        public void showError(String error);
    }
}
