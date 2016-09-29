package com.you.edu.live.teacher.model;

import android.text.TextUtils;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.model.bean.Live;
import com.you.edu.live.teacher.model.bean.Room;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.dao.IDaoCallback;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class RecentLiveModel extends BaseMvpModel implements IHttpCallback<RespOut>, IDaoCallback<RespOut> {

    private IOperationListener<List<Live>> mIOperationListener;
    private IOperationListener<List<Live>> mCacheIOperationListener;
    private IOperationListener<Map<String, String>> mRoomInfoIOperationListener;


    public RecentLiveModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    /**
     * 近期直播
     */
    public void requestLiveCache(IOperationListener<List<Live>> listener) {

        this.setCacheRequestListener(listener);

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_RECENT_LIVES);

        this.getCache().getCacheAsString(
                GlobalConfig.HttpUrl.TEACHER_LIVE_LIST, this, tag);
    }

    /**
     * 近期直播
     *
     * @param play_type   值 play 要讲的课    end 结束的课
     * @param size        页面大小
     * @param page        当前页，默认1
     * @param isNeedCache 是否缓存
     * @param listener
     */
    public void requestRecentLive(String play_type, int size, int page, boolean isNeedCache, IOperationListener<List<Live>> listener) {

        this.setCoursesRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(play_type)) {
            params.put("play_type", play_type);
        }
        params.put("size", size + "");
        params.put("page", page + "");

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_RECENT_LIVES);
        if (isNeedCache) {
            tag.cacheUrl = GlobalConfig.HttpUrl.TEACHER_LIVE_LIST;
        }
        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.TEACHER_LIVE_LIST,
                params, this, tag);
    }

    /**
     * 直播信息
     *
     * @param coid     课程id
     * @param chid     课时id
     * @param listener
     */
    public void requestGetLive(int coid, int chid, IOperationListener<Map<String, String>> listener) {

        this.setRoomInfoRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("chid", chid + "");
        params.put("coid", coid + "");

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_GET_LIVE_INFO);
        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.GET_LIVE_INFO,
                params, this, tag);
    }

    @Override
    public void onDaoResponse(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_RECENT_LIVES:// 直播列表
                if (this.getCacheRequestListener() == null) {
                    return;
                }

                List<Live> liveList = null;
                String mResult = (String) out.result;
                if (!TextUtils.isEmpty(mResult)) {// 缓存不为空
                    RequestDataParser parser = new RequestDataParser(mResult);
                    String jsonArr = parser
                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
                    liveList = parser.getArray(jsonArr,
                            Live.class);
                }
                this.getCacheRequestListener().onOperationSuccess(liveList);
                break;

        }
    }

    @Override
    public void onResponseError(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_RECENT_LIVES:// 直播列表
                if (this.getCoursesRequestListener() != null) {
                    this.getCoursesRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_RECENT_LIVES,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_GET_LIVE_INFO:// 直播信息
                if (this.getRoomInfoRequestListener() != null) {
                    this.getRoomInfoRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_RECENT_LIVES,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
        }
    }

    @Override
    public void onResponse(RespOut response) {
        if (null == response || null == response.param) {
            return;
        }
        RequestDataParser parser = new RequestDataParser(response.resp);
        int operator = (int) response.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_RECENT_LIVES:// 直播列表
                if (this.getCoursesRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    String jsonArr = parser
                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
                    List<Live> liveList = parser.getArray(jsonArr,
                            Live.class);
                    this.getCoursesRequestListener().onOperationSuccess(liveList);
                    String cacheUrl = response.param.cacheUrl;
                    if (!TextUtils.isEmpty(cacheUrl) && null != this.getCache()) {// 需要缓存此json结果
                        this.getCache().saveCache(cacheUrl, response.resp);
                    }
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getCoursesRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_GET_LIVE_INFO://直播信息
                if (this.getRoomInfoRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {

                    String jsonRoom = parser.getValue("room");
                    Room roomInfo = parser.getOne(jsonRoom, Room.class);

                    Map<String, String> map = new HashMap<String, String>();
                    if (roomInfo != null) {
                        if (!TextUtils.isEmpty(roomInfo.getRoom_id())) {
                            map.put("room_id", roomInfo.getRoom_id());
                        }
                        if (!TextUtils.isEmpty(roomInfo.getSocket_host())) {
                            map.put("socket_host", roomInfo.getSocket_host());
                        }
                    }
                    this.getRoomInfoRequestListener().onOperationSuccess(map);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getRoomInfoRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
                break;
        }
    }

    public void setCoursesRequestListener(IOperationListener<List<Live>> listener) {
        mIOperationListener = listener;
    }

    public IOperationListener<List<Live>> getCoursesRequestListener() {
        return mIOperationListener;
    }

    public void setCacheRequestListener(IOperationListener<List<Live>> listener) {
        mCacheIOperationListener = listener;
    }

    public IOperationListener<List<Live>> getCacheRequestListener() {
        return mCacheIOperationListener;
    }

    public void setRoomInfoRequestListener(IOperationListener<Map<String, String>> listener) {
        mRoomInfoIOperationListener = listener;
    }

    public IOperationListener<Map<String, String>> getRoomInfoRequestListener() {
        return mRoomInfoIOperationListener;
    }
}
