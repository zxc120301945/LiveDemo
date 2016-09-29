package com.you.edu.live.teacher.contract;

import com.you.edu.live.teacher.model.bean.Course;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public interface IUserCourseContract {

    public interface IUserCoursePresenter {

        public void requestTeacherCourse(int course_type, int re_page, int page, String keyword, boolean isNeedCache);

        public void requestCourseCache();
    }

    public interface IUserCourseView extends IMvpBaseContract.IMvpView {

        public void onCacheSuccess(List<Course> courseList);

        public void onCoursesSuccess(List<Course> courseList);

        public void showError(String error);
    }
}
