package com.you.edu.live.teacher.model;

import android.text.TextUtils;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.Chapter;
import com.you.edu.live.teacher.model.bean.Room;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class ChapterModel extends BaseMvpModel implements IHttpCallback<RespOut> {

    private IOperationListener<List<Chapter>> mCourseHourIOperationListener;
    private IOperationListener<String> mDeleteChapterIOperationListener;
    private IOperationListener<Map<String, String>> mRoomInfoIOperationListener;

    public ChapterModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    /**
     * 课时列表
     *
     * @param coid 课程id
     * @param type 类型：0全部、1为直播、2为点播；默认0
     */
    public void requestChapterDetail(int coid, int type, IOperationListener<List<Chapter>> listener) {
        this.setCourseHourRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("coid", coid + "");
        if(type == 0){
            params.put("type", type + "");
            RequestTag tag = new RequestTag(this.getHttpTag(),
                    GlobalConfig.Operator.OPERATOR_COURSE_HOUR);
            this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.COURSE_HOUE,
                    params, this, tag);
        }else if((type == 1)){
            params.put("type", type + "");
            RequestTag tag = new RequestTag(this.getHttpTag(),
                    GlobalConfig.Operator.OPERATOR_LIVE_CLASS);
            this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.COURSE_HOUE,
                    params, this, tag);
        }


    }

    /**
     * 删除课时
     *
     * @param chid
     * @param listener
     */
    public void requestDeleteChapter(int chid, IOperationListener<String> listener) {
        this.setDeleteChapterRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("chid", chid + "");
        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_DELETE_CHAPTER);
        this.getHttpApi().request(Request.Method.POST, GlobalConfig.HttpUrl.DELETE_CHAPTER,
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
    public void onResponse(RespOut respOut) {
        if (null == respOut || null == respOut.param) {
            return;
        }
        RequestDataParser parser = new RequestDataParser(respOut.resp);
        int operator = (int) respOut.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_COURSE_HOUR://课时列表
                if (this.getCourseHourRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    String jsonArr = parser.getValue(GlobalConfig.HttpJson.KEY_LIST);
                    List<Chapter> hourList = parser.getArray(jsonArr, Chapter.class);
                    this.getCourseHourRequestListener().onOperationSuccess(hourList);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getCourseHourRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
                break;
            case  GlobalConfig.Operator.OPERATOR_LIVE_CLASS://直播课时列表
                if (this.getCourseHourRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    String jsonArr = parser.getValue(GlobalConfig.HttpJson.KEY_LIST);
                    List<Chapter> hourList = parser.getArray(jsonArr, Chapter.class);
                    this.getCourseHourRequestListener().onOperationSuccess(hourList);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getCourseHourRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_DELETE_CHAPTER://删除课时
                if (this.getDeleteChapterRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    String msg = parser.getValue("msg");
                    this.getDeleteChapterRequestListener().onOperationSuccess(msg);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getDeleteChapterRequestListener()
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

//                    String jsonArr = parser
//                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
//                    List<Course> courseList = parser.getArray(jsonArr,
//                            Course.class);
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

    @Override
    public void onResponseError(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_TEACHER_COURSES:
                if (this.getCourseHourRequestListener() != null) {
                    this.getCourseHourRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_TEACHER_COURSES,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
            case GlobalConfig.Operator.OPERATOR_DELETE_CHAPTER:
                if (this.getDeleteChapterRequestListener() != null) {
                    this.getDeleteChapterRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_DELETE_CHAPTER,
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

    public void setCourseHourRequestListener(IOperationListener<List<Chapter>> listener) {
        mCourseHourIOperationListener = listener;
    }

    public IOperationListener<List<Chapter>> getCourseHourRequestListener() {
        return mCourseHourIOperationListener;
    }

    public void setDeleteChapterRequestListener(IOperationListener<String> listener) {
        mDeleteChapterIOperationListener = listener;
    }

    public IOperationListener<String> getDeleteChapterRequestListener() {
        return mDeleteChapterIOperationListener;
    }

    public void setRoomInfoRequestListener(IOperationListener<Map<String, String>> listener) {
        mRoomInfoIOperationListener = listener;
    }

    public IOperationListener<Map<String, String>> getRoomInfoRequestListener() {
        return mRoomInfoIOperationListener;
    }
}
