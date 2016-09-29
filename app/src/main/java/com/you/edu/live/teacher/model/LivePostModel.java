package com.you.edu.live.teacher.model;

import android.text.TextUtils;
import android.util.Log;

import com.laifeng.sopcastsdk.entity.StreamInfo;
import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.contract.ILivePostContract;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

import java.util.HashMap;
import java.util.Map;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * 做直播数据处理
 * 作者：XingRongJing on 2016/6/28.
 */
public class LivePostModel extends BaseMvpModel implements ILivePostContract.ILivePostModel, IHttpCallback<RespOut> {

    private static final String TAG = "LivePostModel";
    private WebSocket mWsSocket;
    private IOperationListener<StreamInfo> mStreamInfoOperationListener;
    private IOperationListener<String> mLiveStateChangedListener;
    /**
     * 章节id（用于替代来疯的roomId）
     **/
    private int mChid = 0;
    private String mRoomId;
    /**
     * 直播开始或结束时的参数（用于失败重试）
     **/
    private Map<String, String> mLiveStateChangedParams;
    private int mLiveStateChangedRetryCount = 0;

    public LivePostModel(IHttpApi api) {
        super(api);
    }

    @Override
    public void requestCreateStream(String roomId, int stream_type, String streamId, int chid, IOperationListener<StreamInfo> listener) {
        this.mStreamInfoOperationListener = listener;
        this.mChid = chid;
        mRoomId = roomId;
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(roomId)) {
            params.put("room_id", roomId);
        }
        params.put("stream_type", stream_type + "");
        if (!TextUtils.isEmpty(streamId)) {
            params.put("stream_id", streamId);
        }
        RequestTag tag = new RequestTag(this.getHttpTag(), GlobalConfig.Operator.OPERATOR_CREATE_STREAM);
        this.getHttpApi().request(GlobalConfig.HttpUrl.URL_CREATE_STREAM, params, this, tag);
    }

    @Override
    public void requestLiveStartOrEnd(String roomId, boolean isStart, boolean isPilot, boolean isPilotToLive, IOperationListener<String> listener) {
        this.mLiveStateChangedListener = listener;
        mLiveStateChangedParams = new HashMap<>();
        if (!TextUtils.isEmpty(roomId)) {
            mLiveStateChangedParams.put("room_id", roomId);
        }
        if (isStart) {
            mLiveStateChangedParams.put("type", "1");
        } else {
            mLiveStateChangedParams.put("type", "0");
        }
        if (isPilot) {
            mLiveStateChangedParams.put("is_try", "1");
        }
        if (isPilotToLive) {
            mLiveStateChangedParams.put("try_2_live", "1");
        }
        int httptag = this.getHttpTag();
        if (!isStart) {
            httptag = -1;
        }
        RequestTag tag = new RequestTag(httptag, GlobalConfig.Operator.OPERATOR_LIVE_STATE_CHANGED);
        this.getHttpApi().request(GlobalConfig.HttpUrl.URL_LIVE_START_END, mLiveStateChangedParams, this, tag);
    }

    @Override
    public void connectSocket(String socketIp, WebSocketConnectionHandler mWsHandler) {
        if (null == mWsSocket) {
            mWsSocket = new WebSocketConnection();
        }
        try {
//            String wsuri = "ws://" + ip + ":" + port;
            if (TextUtils.isEmpty(socketIp)) {
                socketIp = GlobalConfig.HttpUrl.SOCKET_HOST;
            }
            String wsuri = "ws://" + socketIp;
            if (AppHelper.mIsLog) {
                Log.d(TAG, "shan-->socket connect--> " + wsuri);
            }
            mWsSocket.connect(wsuri, mWsHandler);
        } catch (WebSocketException e) {
            Log.e(TAG, "shan-->socket connect exception = " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void disconnectSocket() {
        if (null != mWsSocket) {
            mWsSocket.disconnect();
            mWsSocket = null;
        }
    }

    @Override
    public boolean isSocketConnected() {
        return null == mWsSocket ? false : mWsSocket.isConnected();
    }

    @Override
    public void sendMessage(String msg) {
        if (this.isSocketConnected()) {
            mWsSocket.sendTextMessage(msg);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.disconnectSocket();

    }

    @Override
    public void onResponse(RespOut response) {
        if (null == response || null == response.param) {
            return;
        }
        int operator = response.param.operator;
        RequestDataParser parser = new RequestDataParser(response.resp);
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_CREATE_STREAM://创建流请求成功
                if (null == mStreamInfoOperationListener) return;
                if (parser.isSuccess()) {
                    String streamId = parser.getValue(GlobalConfig.HttpJson.KEY_STREAM_ID);
                    String token = parser.getValue(GlobalConfig.HttpJson.KEY_UPLOAD_TOKEN);
                    int uid = parser.getValueInt(GlobalConfig.HttpJson.KEY_YT_UID);
                    int lfRoomId = mChid;//将我们章节id当做来疯roomId
//                    String streamAlias = "stream_alias_" + streamId + "_" + lfRoomId;//如果有roomId（来疯），再加上roomId
                    String streamAlias = mRoomId;//为了妥协后台，否则上面的更好
                    StreamInfo stream = new StreamInfo(GlobalConfig.APPID, token, streamAlias, streamId, uid, lfRoomId);
                    mStreamInfoOperationListener.onOperationSuccess(stream);
                } else {
                    int errorCode = parser.getRespCode();
                    String error = parser.getErrorMsg();
                    mStreamInfoOperationListener.onOperationFails(errorCode, error);

                }
                break;
            case GlobalConfig.Operator.OPERATOR_LIVE_STATE_CHANGED://直播开始或结束通知请求成功
                if (parser.isSuccess()) {
                    if (null != mLiveStateChangedListener) {
                        String streamId = parser.getValue(GlobalConfig.HttpJson.KEY_STREAM_ID);
                        mLiveStateChangedListener.onOperationSuccess(streamId);
                    }
                    mLiveStateChangedParams = null;
                } else {
                    if (this.isOverRetryCount(mLiveStateChangedRetryCount)) {
                        if (null != mLiveStateChangedListener) {
                            mLiveStateChangedListener.onOperationFails(parser.getRespCode(), parser.getErrorMsg());
                        }
                        mLiveStateChangedParams = null;
                    } else {
                        this.retryRequestLiveStartOrEnd(mLiveStateChangedParams, response.param);
                    }
                }
                break;
        }
    }

    @Override
    public void onResponseError(RespOut response) {
        if (null == response || null == response.param) {
            return;
        }
        int operator = response.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_CREATE_STREAM://创建流失败
                if (null != mStreamInfoOperationListener) {
                    mStreamInfoOperationListener.onOperationFails(GlobalConfig.HTTP_RESP_ERROR_CODE, GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_LIVE_STATE_CHANGED://直播开始或结束通知失败
                if (this.isOverRetryCount(mLiveStateChangedRetryCount)) {
                    if (null != mLiveStateChangedListener) {
                        mLiveStateChangedListener.onOperationFails(GlobalConfig.HTTP_RESP_ERROR_CODE, GlobalConfig.HTTP_ERROR_TIPS);
                    }
                    mLiveStateChangedParams = null;
                } else {
                    this.retryRequestLiveStartOrEnd(mLiveStateChangedParams, response.param);
                }
                break;
        }
    }

    /**
     * 是否超过最大重试次数
     *
     * @param currRetryCount
     * @return
     */
    private boolean isOverRetryCount(int currRetryCount) {
        return currRetryCount >= GlobalConfig.MAX_RETRY_COUNT;
    }

    /**
     * 重新请求直播开始或结束
     *
     * @param params
     * @param tag
     */
    private void retryRequestLiveStartOrEnd(Map<String, String> params, RequestTag tag) {
        mLiveStateChangedRetryCount++;
        this.getHttpApi().request(GlobalConfig.HttpUrl.URL_LIVE_START_END, params, this, tag);
    }
}
