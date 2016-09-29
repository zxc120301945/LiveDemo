package com.you.edu.live.teacher.model;

import android.text.TextUtils;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.model.bean.User;
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
public class UserCourseModel extends BaseMvpModel implements IHttpCallback<RespOut>, IDaoCallback<RespOut> {

    private IOperationListener<List<Course>> mIOperationListener;
    private IOperationListener<List<Course>> mCacheIOperationListener;


    public UserCourseModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    /**
     * 课程缓存
     */
    public void requestCourseCache(IOperationListener<List<Course>> listener) {

        this.setCacheRequestListener(listener);

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_TEACHER_COURSES);

        this.getCache().getCacheAsString(
                GlobalConfig.HttpUrl.TEACHER_COURSE_LIST, this, tag);
    }

    /**
     * 老师课程
     *
     * @param course_type 课程类型：0为所有 10直播 20点播；默认为0
     * @param re_page     每页条数，默认为10条
     * @param page        当前页，默认1
     * @param keyword     课程名称关键字
     * @param isNeedCache 是否缓存
     * @param listener
     */
    public void requestTeacherCourse(int course_type, int re_page, int page, String keyword, boolean isNeedCache, IOperationListener<List<Course>> listener) {

        this.setCoursesRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("course_type",course_type+"");
        params.put("pre_page", re_page + "");
        params.put("page", page + "");
        if(!TextUtils.isEmpty(keyword)){
            params.put("keyword", keyword);
        }

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_TEACHER_COURSES);
        if (isNeedCache) {
            tag.cacheUrl = GlobalConfig.HttpUrl.TEACHER_COURSE_LIST;
        }
        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.TEACHER_COURSE_LIST,
                params, this, tag);
    }

    @Override
    public void onDaoResponse(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_TEACHER_COURSES:// 老师课程
                if (this.getCacheRequestListener() == null) {
                    return;
                }

                List<Course> courseList = null;
                String mResult = (String) out.result;
                if (!TextUtils.isEmpty(mResult)) {// 缓存不为空
                    RequestDataParser parser = new RequestDataParser(mResult);
                    String jsonArr = parser
                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
                    courseList = parser.getArray(jsonArr,
                            Course.class);
                }
                this.getCacheRequestListener().onOperationSuccess(courseList);
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
            case GlobalConfig.Operator.OPERATOR_TEACHER_COURSES:// 老师课程
                if (this.getCoursesRequestListener() != null) {
                    this.getCoursesRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_USER_LOGIN,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
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
            case GlobalConfig.Operator.OPERATOR_TEACHER_COURSES:// 老师课程
                if (this.getCoursesRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {
                    String jsonArr = parser
                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
                    List<Course> courseList = parser.getArray(jsonArr,
                            Course.class);
                    this.getCoursesRequestListener().onOperationSuccess(courseList);
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
        }
    }

    public void setCoursesRequestListener(IOperationListener<List<Course>> listener) {
        mIOperationListener = listener;
    }

    public IOperationListener<List<Course>> getCoursesRequestListener() {
        return mIOperationListener;
    }

    public void setCacheRequestListener(IOperationListener<List<Course>> listener) {
        mCacheIOperationListener = listener;
    }

    public IOperationListener<List<Course>> getCacheRequestListener() {
        return mCacheIOperationListener;
    }

}
