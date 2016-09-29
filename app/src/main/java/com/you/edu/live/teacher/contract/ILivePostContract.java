package com.you.edu.live.teacher.contract;

import com.laifeng.sopcastsdk.entity.StreamInfo;
import com.you.edu.live.teacher.model.bean.Comments;

import de.tavendo.autobahn.WebSocketConnectionHandler;

/**
 * 发布直播契约接口
 * 作者：XingRongJing on 2016/6/28.
 */
public interface ILivePostContract {


    interface ILivePostPresenter {


        /**
         * 创建直播流
         *
         * @param roomId
         * @param stream_type 1：插件flv 2：rtmp多麦流 3：网络流 4：rtmp流
         * @param streamId    流id（试播转直播时需要,否则填0或-1即可）
         * @param chid        课程id（自用）
         */
        void requestCreateStream(String roomId, int stream_type, String streamId, int chid);

        /**
         * 通知直播（试播）开始或结束
         *
         * @param roomId
         * @param isStart       1：开始 0：结束
         * @param isPilot       是否试播（1为试播；默认为0）
         * @param isPilotToLive 是否试播转直播（1：是，默认为0）
         */
        void requestLiveStartOrEnd(String roomId, boolean isStart, boolean isPilot, boolean isPilotToLive);

        void connectSocket(String socketIp);

        void disconnectSocket();

        boolean isSocketConnected();

        void sendMessage(String msg);

    }

    interface ILivePostModel {
        /**
         * 具体说明见ILivePostPrestener
         **/
        void requestCreateStream(String roomId, int stream_type, String streamId, int chid, IOperationListener<StreamInfo> listener);

        /**
         * 具体说明见ILivePostPrestener
         **/
        void requestLiveStartOrEnd(String roomId, boolean isStart, boolean isPilot, boolean isPilotToLive, IOperationListener<String> listener);

        void connectSocket(String socketIp, WebSocketConnectionHandler mWsHandler);

        void disconnectSocket();

        boolean isSocketConnected();

        void sendMessage(String msg);

    }

    interface ILivePostView extends IMvpBaseContract.IMvpView {
        /**
         * 展示直播初始加载/缓冲View
         *
         * @param isBuffering 是否缓存（true为缓冲，false为初始化加载）
         */
        void showLiveLoadingView(boolean isBuffering);

        /**
         * 隐藏直播初始加载/缓冲View
         *
         * @param isBuffering 是否缓存（true为缓冲，false为初始化加载）
         */
        void hideLiveLoadingView(boolean isBuffering);

        /**
         * 展示直播出错View（如创建流失败，开始直播错误等）
         */
        void showLiveErrorView(String error);

        /**
         * 展示直播异常view（如重连多次失败，没网，暂停超过3分钟等）
         *
         * @param error
         */
        void showLiveStopError(String error);


        /**
         * 显示所有直播信息view
         **/
        void showAllLiveInfoViews();

        /**
         * 隐藏所有直播信息view
         **/
        void hideAllLiveInfoViews();

        /**
         * 展示试播中的view
         **/
        void showLivePilotView();

        /**
         * 隐藏试播中的view
         **/
        void hideLivePilotView();

        /**
         * 创建流成功
         *
         * @param streamInfo
         */
        void onCreateStreamSuccess(StreamInfo streamInfo);

        /**
         * 创建流失败-因为直播无效或已过期
         * @param error
         */
        void onCreateStreamFailsWhenLiveInvalid(String error);

        /**
         * 直播开始
         *
         * @param isReliving 是否重新直播
         */
        void onStartLiving(boolean isReliving);

        /**
         * socket连接中
         **/
        void onSocketConnecting();

        /**
         * socket连接成功
         **/
        void onSocketConnected();

        /**
         * socket连接断开
         **/
        void onSocketDisconnected(String reason);

        /**
         * socket接收到一条消息
         **/
        void onReceiveMessage(Comments msg);

        /**
         * socket接收到赞
         *
         * @param num 总赞数
         **/
        void onReceivePraise(int num);

        /**
         * socket接收到有人来或走（不需管理User，仅处理人数以及发布弹幕通知）
         *
         * @param uid
         * @param username
         * @param isComing true：来  false：走
         **/
        void onReceiveUserChanged(int uid, String username, boolean isComing);


        /**
         * 接收到socket通知（如错误、直播延时、举报等）
         *
         * @param tips
         * @param isError 是否错误
         */
        void onReceiveSocketNotice(String tips, boolean isError);

        /**
         * socket接受到直播结束
         **/
        void onReceiveLiveFinish();

    }
}
