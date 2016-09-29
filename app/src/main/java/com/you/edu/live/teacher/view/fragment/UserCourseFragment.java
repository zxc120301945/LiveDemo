package com.you.edu.live.teacher.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.contract.IUserCourseContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Course;
import com.you.edu.live.teacher.presenter.UserCoursePresenter;
import com.you.edu.live.teacher.presenter.helper.ListRefreshHelper;
import com.you.edu.live.teacher.presenter.helper.ShanRefreshViewHelper;
import com.you.edu.live.teacher.utils.ActivityCompatUtils;
import com.you.edu.live.teacher.utils.TimeUtil;
import com.you.edu.live.teacher.view.BaseViewFragment;
import com.you.edu.live.teacher.view.activity.ChapterActivity;
import com.you.edu.live.teacher.view.activity.MainActivity;
import com.you.edu.live.teacher.widget.DividerItemDecoration;
import com.you.edu.live.teacher.widget.adapter.UserCourseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Title: 我的课程
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/7/4
 * @Time 下午15:56
 * @Version 1.0
 */
public class UserCourseFragment extends BaseViewFragment implements IUserCourseContract.IUserCourseView, ShanRefreshViewHelper.IRefreshListener, IOnClickItemListener {

    @BindView(R.id.ll_recent_live_root)
    LinearLayout mLlRoot;
    @BindView(R.id.rv_list_page)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout mSwRefresh;
    private List<Course> mCourseList;
    private UserCourseAdapter mAdapter;

    private boolean isFirst = true;
    private UserCoursePresenter mPresenter;
    private ListRefreshHelper mCourseRefreshHelper;

    private int mPage;
    private Unbinder mUnbinder;

    private boolean isPullDownRefresh = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getMvpPresenter().attachView(this);
        mCourseList = new ArrayList<Course>();
        mAdapter = new UserCourseAdapter(this.getContext(), mCourseList);
        mAdapter.setOnClickItemListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recyclerview, null);
        mUnbinder = ButterKnife.bind(this, view);

        //配置RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(
                this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);

        String time = TimeUtil.getCurrentTime();
//        this.showToast(s);
//        ListRefreshHelper<Course> tempHelper = mRefreshHelper;
//        mRefreshHelper = new ListRefreshHelper<Course>(mSwRefresh,
//                mRecyclerView, mAdapter, this);
//        mRefreshHelper.setIsFootView(true);
//        if (null != tempHelper) {
//            mRefreshHelper.setHasMore(tempHelper.isHasMore());
//            mRefreshHelper.setIsRefreshing(tempHelper.isRefreshing());
//        }
        if (mCourseRefreshHelper == null) {
            mCourseRefreshHelper = new ListRefreshHelper<Course>(mSwRefresh,
                    mRecyclerView, mAdapter, this);
        }

        if (isFirst) {
            this.getMvpPresenter().requestCourseCache();
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mLlRoot.setPadding(0,
//                    CommonUtil.getStatusHeight(this.getActivity()), 0, 0);
//        }
        return view;
    }

    public ListRefreshHelper getListRefreshHelper() {
        if (mCourseRefreshHelper != null) {
            return mCourseRefreshHelper;
        }
        return null;
    }

    public void setIsClearList(boolean isRefreshing) {
        isPullDownRefresh = isRefreshing;
    }

    @Override
    public void onCacheSuccess(List<Course> courseList) {
        if (mCourseList != null) {
            mCourseList.clear();
        }
        if (courseList != null) {
            mCourseList.addAll(courseList);
        }
        if (!this.isNetworkEnable()) {
            isFirst = false;
        }
        if (mCourseRefreshHelper != null) {
            mCourseRefreshHelper.onRefreshAuto();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCoursesSuccess(List<Course> courseList) {
        if (mCourseRefreshHelper != null) {
            if (mCourseRefreshHelper.isPullDownRefresh()) {
                mCourseRefreshHelper.clearList(mCourseList);
            } else if (isPullDownRefresh) {
                mCourseList.clear();
            }
            mCourseRefreshHelper.onRefreshDatasComplete(courseList);
            mCourseRefreshHelper.onRefreshViewComplete(false);
        }
        if (courseList != null) {
            mCourseList.addAll(courseList);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String error) {
        mCourseRefreshHelper.onRefreshViewComplete(false);
        this.showToast(error);
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.unbind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public UserCoursePresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new UserCoursePresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(UserCourseFragment.this.getContext()));
        }
        return mPresenter;
    }

    public MainActivity getMainActivity() {
        if (this.getActivity() instanceof MainActivity) {
            return (MainActivity) this.getActivity();
        }
        return null;
    }

    public void setRefreshPage(int page) {
        this.mPage = page;
        if (mCourseRefreshHelper != null) {
            mCourseRefreshHelper.setPage(mPage);
        }
    }

    @Override
    public void onPullDownRefresh() {
        AppHelper.getAppHelper().getUser(this.getActivity());
        if (mCourseRefreshHelper != null) {
            mCourseRefreshHelper.reset();
        }
        AppHelper.getAppHelper().getUser(this.getContext());
        mPage = 1;
        mCourseRefreshHelper.setPage(mPage);
        isPullDownRefresh = true;
//        AppHelper.getAppHelper().getDataChangeHelper().notifyChangeRefresh(true, AppHelper.getAppHelper().getDataChangeHelper().OPERATOR_MAIN_REFRESH, "UserCourseFragment");
        this.getMvpPresenter().requestTeacherCourse(0, mCourseRefreshHelper.getMaxLoad(), mCourseRefreshHelper.getPage(), "", true);
        if (!isFirst) {
            MainActivity main = this.getMainActivity();
            if (main != null) {
                main.getMvpPresenter().requestTeacherInfo(false, true);
                if (main.getFragmentsHelper().getFragment(1) instanceof RecentLiveFragment) {
                    RecentLiveFragment fragment = (RecentLiveFragment) main.getFragmentsHelper().getFragment(1);
                    ListRefreshHelper refreshHelper = fragment.getListRefreshHelper();
                    fragment.setIsClearList(true);
                    fragment.setRefreshPage(mPage);
                    fragment.getMvpPresenter().requestRecentLive(null, mCourseRefreshHelper.getMaxLoad(), mCourseRefreshHelper.getPage(), true);
                }
            }
        }
        isFirst = false;
    }

    @Override
    public void onPullUpLoadMore() {
        mPage++;
        mCourseRefreshHelper.setPage(mPage);
        this.getMvpPresenter().requestTeacherCourse(0, mCourseRefreshHelper.getMaxLoad(), mCourseRefreshHelper.getPage(), "", false);
        isPullDownRefresh = false;
    }

    @Override
    public void onClickItem(int operator, Object item) {
        if (operator == 0) {
            return;
        }
        if (operator == GlobalConfig.ClickYype.COURSE_CLICK) {
            Course course = (Course) item;
            if (course != null) {
                int course_status = course.getCourse_status();
                if (course_status == GlobalConfig.ItemPlayStatus.COURSE_STATUS_UNPUBLISH ||
                        course_status == GlobalConfig.ItemPlayStatus.COURSE_STATUS_SELLING_STOP) {//课程已下架
                    this.showToast(getResources().getString(R.string.course_halt_the_sales));
                } else if (course_status == GlobalConfig.ItemPlayStatus.COURSE_STATUS_EXPIRED) {//课程已过期
                    this.showToast(getResources().getString(R.string.course_overdue));
                } else {
                    Intent intent = new Intent(this.getContext(), ChapterActivity.class);
                    intent.putExtra("coid", course.getCoid());
                    ActivityCompatUtils.startActivitytLand(this, intent);
//                    this.startActivity(intent);
                }
            }
        }
    }
}
