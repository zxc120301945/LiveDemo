package com.you.edu.live.teacher.model;

import android.text.TextUtils;

import com.android.volley.Request;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.model.bean.TeacherInfo;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.dao.IDaoCallback;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: Main Model
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/30
 * @Time 下午16:43
 * @Version
 */
public class MainModel extends BaseMvpModel implements IHttpCallback<RespOut>,IDaoCallback<RespOut> {

    private IOperationListener<TeacherInfo> mTeacherInfoIOperationListener;
    private IOperationListener<TeacherInfo> mCacheIOperationListener;

    public MainModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }

    public void requestCache(IOperationListener<TeacherInfo> listener){
        this.setCacheRequestListener(listener);

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERAOTR_TEACHER_INFO);

        this.getCache().getCacheAsString(
                GlobalConfig.HttpUrl.TEACHER_INFO, this, tag);
    }

    /**
     * 老师信息
     */
    public void requestTeacherInfo(boolean isNeedCache,IOperationListener<TeacherInfo> listener) {
        this.setTeacherInfoRequestListener(listener);
        Map<String, String> params = new HashMap<String, String>();

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERAOTR_TEACHER_INFO);
        if (isNeedCache) {
            tag.cacheUrl = GlobalConfig.HttpUrl.TEACHER_INFO;
        }
        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.TEACHER_INFO,
                params, this, tag);
    }

    @Override
    public void onResponse(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        RequestDataParser parser = new RequestDataParser(out.resp);
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERAOTR_TEACHER_INFO:// 老师信息
                if(this.getTeacherInfoRequestListener() == null){
                    return;
                }
                if(parser.isSuccess()){
                    String jsonArr = parser.getValue(GlobalConfig.HttpJson.KEY_DATA);
                    TeacherInfo info = parser.getOne(jsonArr, TeacherInfo.class);
                    this.getTeacherInfoRequestListener().onOperationSuccess(info);
                    String cacheUrl = out.param.cacheUrl;
                    if (!TextUtils.isEmpty(cacheUrl) && null != this.getCache()) {// 需要缓存此json结果
                        this.getCache().saveCache(cacheUrl, out.resp);
                    }
                }else{
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getTeacherInfoRequestListener()
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
            case GlobalConfig.Operator.OPERAOTR_TEACHER_INFO:
                if (this.getTeacherInfoRequestListener() != null) {
                    this.getTeacherInfoRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_TEACHER_COURSES,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
        }
    }

    @Override
    public void onDaoResponse(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERAOTR_TEACHER_INFO:// 老师信息
                if (this.getCacheInfoRequestListener() == null) {
                    return;
                }
                TeacherInfo teacherInfo = null;
                String mResult = (String) out.result;
                if (!TextUtils.isEmpty(mResult)) {// 缓存不为空
                    RequestDataParser parser = new RequestDataParser(mResult);
                    String jsonArr = parser
                            .getValue(GlobalConfig.HttpJson.KEY_DATA);
                    teacherInfo = parser.getOne(jsonArr,
                            TeacherInfo.class);
                }
                this.getCacheInfoRequestListener().onOperationSuccess(teacherInfo);
                break;
        }
    }

    public void setTeacherInfoRequestListener(IOperationListener<TeacherInfo> listener) {
        mTeacherInfoIOperationListener = listener;
    }

    public IOperationListener<TeacherInfo> getTeacherInfoRequestListener() {
        return mTeacherInfoIOperationListener;
    }

    public void setCacheRequestListener(IOperationListener<TeacherInfo> listener) {
        this.mCacheIOperationListener = listener;
    }

    public IOperationListener<TeacherInfo> getCacheInfoRequestListener() {
        return mCacheIOperationListener;
    }


}
