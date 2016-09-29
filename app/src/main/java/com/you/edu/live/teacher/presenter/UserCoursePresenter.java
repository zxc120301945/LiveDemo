package com.you.edu.live.teacher.presenter;

import android.util.Log;

import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.contract.IUserCourseContract;
import com.you.edu.live.teacher.model.UserCourseModel;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class UserCoursePresenter extends BaseMvpPresenter<IUserCourseContract.IUserCourseView, UserCourseModel> implements IUserCourseContract.IUserCoursePresenter {

    public UserCoursePresenter(IHttpApi httpApi, ICache cache) {
        super();
        if (!this.isAttachModel()) {
            this.attachModel(new UserCourseModel(httpApi, cache));
        }
    }

    @Override
    public void requestTeacherCourse(int course_type, int re_page, int page, String keyword, boolean isNeedCache) {
        if (this.isAttachModel()) {
            getModel().requestTeacherCourse(course_type, re_page, page, keyword, isNeedCache, mUserCourseRequestListener);
        }
    }

    @Override
    public void requestCourseCache() {
        if (this.isAttachModel()) {
            getModel().requestCourseCache(mCacheRequestListener);
        }
    }

    private IOperationListener<List<Course>> mCacheRequestListener = new IOperationListener<List<Course>>() {

        @Override
        public void onOperationSuccess(List<Course> data) {
            if (!isAttachView()) {
                return;
            }
            getView().onCacheSuccess(data);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
        }
    };

    private IOperationListener<List<Course>> mUserCourseRequestListener = new IOperationListener<List<Course>>() {

        @Override
        public void onOperationSuccess(List<Course> data) {
            if (!isAttachView()) {
                return;
            }
            getView().onCoursesSuccess(data);
        }

        @Override
        public void onOperationFails(int errorCode, String error) {
            if (!isAttachView()) {
                return;
            }
            getView().showError(error);
        }
    };
}
