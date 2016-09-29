package com.you.edu.live.teacher.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.laifeng.sopcastsdk.LFLiveView;
import com.laifeng.sopcastsdk.entity.StreamInfo;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.contract.ILivePostContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.LivePostModel;
import com.you.edu.live.teacher.model.bean.Comments;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.WebSocketDao;
import com.you.edu.live.teacher.presenter.helper.CountdownHelper;
import com.you.edu.live.teacher.presenter.helper.LiveUserHelper;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.widget.adapter.LiveUserAdapter;
import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnectionHandler;

/**
 * 作者：XingRongJing on 2016/6/28.
 */
public class LivePostPresenter extends BaseMvpPresenter<ILivePostContract.ILivePostView, LivePostModel> implements ILivePostContract.ILivePostPresenter, LFLiveView.OnLiveListener {

    /** 直播课无效-如直播课程已结束或已过了一小时 **/
    private static final int ERROR_CODE_LIVE_INVALID = 11052;
    private static final String TAG = LivePostPresenter.class.getName();
    /**
     * 是否正在直播
     **/
    private boolean mIsLiving = false;
    /**
     * 是否试播
     **/
    private boolean mIsPilot = true;
    /**
     * 是否直播转试播
     **/
//    private boolean mIsPilotToLive = false;
    private LiveUserHelper mUsersHelper;
    private CountdownHelper mCountHelper;
    private int mDialogOperator;

    public LivePostPresenter(IHttpApi api) {
        super(api);
        if (!this.isAttachModel()) {
            this.attachModel(new LivePostModel(api));
        }
        this.buildLiveUserHelper();
    }

    @Override
    public void requestCreateStream(String roomId, int stream_type, String streamId, int chid) {
        if (this.isAttachView()) {
            this.getView().showLiveLoadingView(false);
        }
        if (this.isAttachModel()) {
            this.getModel().requestCreateStream(roomId, stream_type, streamId, chid, mStreamInfoListener);
        }
    }

    @Override
    public void requestLiveStartOrEnd(String roomId, boolean isStart, boolean isPilot, boolean isPilotToLive) {
        if (this.isAttachModel()) {//暂时不要回调
            this.getModel().requestLiveStartOrEnd(roomId, isStart, isPilot, isPilotToLive, null);
        }
    }

    @Override
    public boolean isSocketConnected() {
        return this.isAttachModel() ? this.getModel().isSocketConnected() : false;
    }


    @Override
    public void connectSocket(String socketIp) {
        if (this.isAttachView()) {
            this.getView().onSocketConnecting();
        }
        if (this.isAttachModel()) {
            this.getModel().connectSocket(socketIp, mWsConnHandler);
        }
    }

    @Override
    public void disconnectSocket() {
        if (this.isAttachModel()) {
            this.getModel().disconnectSocket();
        }
    }

    @Override
    public void sendMessage(String msg) {
        if (!TextUtils.isEmpty(msg) && this.isSocketConnected()) {
            this.getModel().sendMessage(msg);
        }
    }

    @Override
    public void onConnecting() {//直播连接中，调startLive()之后
        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->Live onConnecting() " );
        }
        if (this.isAttachView()) {
            this.getView().showLiveLoadingView(false);
        }
        this.setIsLiving(false);
    }

    @Override
    public void onLiving() {//直播正在直播（包括暂停状态）
        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->Live onLiving() " );
        }
        if (this.isAttachView()) {
            this.getView().hideLiveLoadingView(false);
            this.getView().onStartLiving(false);
        }
        this.setIsLiving(true);
    }

    @Override
    public void onReconnecting() {//直播重连中，流出问题时SDK自动重连
        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->Live onReconnecting() " );
        }
        if (this.isAttachView()) {
            this.getView().showLiveLoadingView(true);
        }
        this.setIsLiving(false);
    }

    @Override
    public void onReLiving() {//直播重连成功，进入Living状态
        if (AppHelper.mIsLog) {
            Log.d(TAG, "shan-->Live onReLiving() " );
        }
        if (this.isAttachView()) {
            this.getView().hideLiveLoadingView(true);
            this.getView().onStartLiving(true);
        }
        this.setIsLiving(true);
    }

    @Override
    public void onStartError(LFLiveView.StartError error) {//直播调用startLive()之后出错
        String errorInfo = null != error ? error.name() : "开始直播错误";
        if (AppHelper.mIsLog) {
            Log.e(TAG, "shan-->Live onStartError() : " + errorInfo);
        }
        if (this.isAttachView()) {
            this.getView().hideLiveLoadingView(false);
            this.getView().showLiveErrorView(errorInfo);
        }
        this.setIsLiving(false);
    }

    @Override
    public void onStop(LFLiveView.StopCase stopCase) {//直播异常，如多次重连之后失败，无网络，暂停超过3分钟等（stopLive（）之后不会调此方法）
        String errorInfo = null != stopCase ? stopCase.name() : "直播出错";
        if (AppHelper.mIsLog) {
            Log.e(TAG, "shan-->Live onStop() : " + errorInfo);
        }
        if (this.isAttachView()) {
//            this.getView().showLiveErrorView(errorInfo);
            this.getView().hideLiveLoadingView(true);
            this.getView().showLiveStopError(errorInfo);

        }
        this.setIsLiving(false);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.stopCountDown();
        mCountHelper = null;
    }

    public boolean isLiving() {
        return mIsLiving;
    }

    private void setIsLiving(boolean mIsLiving) {
        this.mIsLiving = mIsLiving;
    }

    public boolean isPilot() {
        return mIsPilot;
    }

    public void setIsPilot(boolean mIsPilot) {
        this.mIsPilot = mIsPilot;
    }

//    public void setIsPilotToLive(boolean mIsPilotToLive) {
//        this.mIsPilotToLive = mIsPilotToLive;
//    }
//
//    public boolean isPilotToLive() {
//        return mIsPilotToLive;
//    }

    public int getDialogOperator() {
        return mDialogOperator;
    }

    public void setDialogOperator(int mDialogOperator) {
        this.mDialogOperator = mDialogOperator;
    }

    /**
     * 构建一个用户列表管理助手
     */
    private void buildLiveUserHelper() {
        if (null == mUsersHelper) {
            List<User> users = new ArrayList<>();
            LiveUserAdapter mUserAdapter = new LiveUserAdapter(users);
            mUsersHelper = new LiveUserHelper(users, mUserAdapter);
        }
    }

    public RecyclerViewAdapter<User> getUserAdapter() {
        return null == mUsersHelper ? null : mUsersHelper.getUserAdapter();
    }
    public  LiveUserHelper getTestUserHelper(){
        return mUsersHelper;
    }

    public int getUsersCount() {
        return null == mUsersHelper ? 0 : mUsersHelper.getSize();
    }

    /**
     * 开始倒计时
     *
     * @param startTime 开始时间（秒）
     */
    public void startCountDown(long startTime, CountdownHelper.ICountDownListener listener) {
        if (null == mCountHelper) {
            mCountHelper = new CountdownHelper();
        }
        long millSeconds = startTime * 1000 - System.currentTimeMillis();
        if (millSeconds <= 0) {
//            if (null != listener) {
//                listener.onCountDownFinish();
//            }
            return;
        }
        mCountHelper.startTimer(millSeconds, 1000, listener);
    }

    public void stopCountDown() {
        if (null != mCountHelper) {
            mCountHelper.stopTimer();
        }
    }

    private IOperationListener<StreamInfo> mStreamInfoListener = new IOperationListener<StreamInfo>() {
        @Override
        public void onOperationSuccess(StreamInfo data) {
            if (isAttachView()) {
                getView().hideLiveLoadingView(false);
                getView().onCreateStreamSuccess(data);
            }
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (isAttachView()) {
                getView().hideLiveLoadingView(false);
                getView().showLiveErrorView(error);
                if(ERROR_CODE_LIVE_INVALID==errorCode){
                    getView().onCreateStreamFailsWhenLiveInvalid(error);
                }
            }
        }
    };


    /**
     * Socket回调监听
     */
    private WebSocketConnectionHandler mWsConnHandler = new WebSocketConnectionHandler() {
        @Override
        public void onOpen() {//Socket连接成功
            super.onOpen();
            if (AppHelper.mIsLog) {
                Log.d(TAG, "shan-->socket-onOpen() ");
            }
            if (LivePostPresenter.this.isAttachView()) {
                LivePostPresenter.this.getView().onSocketConnected();
            }
        }

        @Override
        public void onClose(int code, String reason) {//Socket关闭，主动关闭，reason为空
            super.onClose(code, reason);
            if (AppHelper.mIsLog) {
                Log.d(TAG, "shan-->socket-onClose() " + reason);
            }
            if (LivePostPresenter.this.isAttachView()) {
                LivePostPresenter.this.getView().onSocketDisconnected(reason);
            }
        }

        @Override
        public void onTextMessage(String payload) {//Socket收到消息
            super.onTextMessage(payload);
            if (TextUtils.isEmpty(payload)) {
                return;
            }
            String type = WebSocketDao.getType(payload);
            if (TextUtils.isEmpty(type)) {
                return;
            }
            if (WebSocketDao.TYPE_PING.equals(type)) {// 心跳响应
                LivePostPresenter.this.sendMessage(WebSocketDao.buildPong());
                return;
            }
            if (AppHelper.mIsLog) {
                Log.d(TAG, "shan-->live View receive: " + payload);
            }
            this.handleAppMessage(type, payload);
        }

        private void handleAppMessage(String type, String msg) {
            if (WebSocketDao.TYPE_LOGIN.equals(type)) {// 登录信令成功

                List<User> users = WebSocketDao.getUsers(msg, -1);
                if (null == mUsersHelper) {
                    return;
                }

                mUsersHelper.removeAll();// 清空列表，防止再次连接时数据重复
                mUsersHelper.addUsers(users);
            } else if (WebSocketDao.TYPE_SAY.equals(type)) {// 说话
                Comments c = WebSocketDao.getComment(msg);
                if (LivePostPresenter.this.isAttachView()) {
                    LivePostPresenter.this.getView().onReceiveMessage(c);
                }
            } else if (WebSocketDao.TYPE_GOOD.equals(type)) {// 点赞
                int num = WebSocketDao.getIntValueByKey(msg,
                        WebSocketDao.KEY_NUM);
                if (LivePostPresenter.this.isAttachView()) {
                    LivePostPresenter.this.getView().onReceivePraise(num);
                }
            } else if (WebSocketDao.TYPE_NOTIFY.equals(type)) {// 来人了
                User user = WebSocketDao.getUser(msg);
                if (null == user) {
                    return;
                }
                int uid = user.getUid();
//                if (uid == getOwnerId()) {// 避免主播出现在用户列表
//                    return;
//                }
                if (null == mUsersHelper || mUsersHelper.isExit(uid)) {// 去重，如已存在，则不再插入
                    return;
                }
                // Step 1:添加用户列表
                mUsersHelper.addUserFront(user);
                // Step 2：通知view，发布一个弹幕介绍
                if (LivePostPresenter.this.isAttachView()) {
                    LivePostPresenter.this.getView().onReceiveUserChanged(uid, user.getNick_name(), true);
                }
            } else if (WebSocketDao.TYPE_LOGOUT.equals(type)) {// 有人走了
                int uid = WebSocketDao.getUid(msg);
                if (null != mUsersHelper && mUsersHelper.removeUser(uid)) {
                    String nickName = WebSocketDao.getValueByKey(msg,
                            WebSocketDao.KEY_NICK_NAME);
                    if (LivePostPresenter.this.isAttachView()) {
                        LivePostPresenter.this.getView().onReceiveUserChanged(uid, nickName, false);
                    }
                }
            } else if (WebSocketDao.TYPE_COURSE.equals(type)) {
                String action = WebSocketDao.getAction(msg);
                if (TextUtils.isEmpty(action)) {
                    return;
                }
                if (WebSocketDao.ACTION_PUSH_LIVE_URL.equals(action)) {
                    int isClose = WebSocketDao.getClose(msg);
                    if (WebSocketDao.CLOSE_BY_OWNER == isClose) {// 直播结束
                        getView().onReceiveLiveFinish();
                    } else {// 直播开始
//                        getView().onReceiveLiveStart();
                    }
                } else if (WebSocketDao.ACTION_PUSH_CHAPTER_DEL.equals(action)
                        || WebSocketDao.ACTION_LIVE_DELAY.equals(action)) {// 举报或延时
                    String tips = WebSocketDao.getValueByKey(msg,
                            WebSocketDao.KEY_MSG);
                    LivePostPresenter.this.getView().onReceiveSocketNotice(tips, false);

                }
            } else if (WebSocketDao.TYPE_ERROR.equals(type)) {//错误
                String error = WebSocketDao.getValueByKey(msg,
                        WebSocketDao.KEY_MSG);
                if (LivePostPresenter.this.isAttachView()) {
                    LivePostPresenter.this.getView().onReceiveSocketNotice(error, true);
                }
            }
        }
    };

}
