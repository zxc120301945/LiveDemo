package com.you.edu.live.teacher.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOnClickItemListener;
import com.you.edu.live.teacher.contract.IRecentLiveContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Live;
import com.you.edu.live.teacher.presenter.RecentLivePresenter;
import com.you.edu.live.teacher.presenter.helper.ListRefreshHelper;
import com.you.edu.live.teacher.presenter.helper.ShanRefreshViewHelper;
import com.you.edu.live.teacher.view.BaseViewFragment;
import com.you.edu.live.teacher.view.activity.LivePostActivity;
import com.you.edu.live.teacher.view.activity.MainActivity;
import com.you.edu.live.teacher.widget.DividerItemDecoration;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.adapter.RecentLiveAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Title: 近期直播
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/7/4
 * @Time 下午15:56
 * @Version 1.0
 */
public class RecentLiveFragment extends BaseViewFragment implements IRecentLiveContract.IRecentLiveView, ShanRefreshViewHelper.IRefreshListener, IOnClickItemListener {

    @BindView(R.id.ll_recent_live_root)
    LinearLayout mLlRoot;
    @BindView(R.id.rv_list_page)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout mSwRefresh;

    private List<Live> mLiveList;
    private RecentLiveAdapter mAdapter;
    private RecentLivePresenter mPresenter;
    private ListRefreshHelper<Live> mLiveRefreshHelper;

    private boolean isFirst = true;
    private int mPage;
    private Unbinder mUnbinder;

    private boolean isPullDownRefresh = false;
    private ProgresDialogHelper mProgresDialogHelper;
    private long mPlayStart;
    private String mRoomId;
    private int mChid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getMvpPresenter().attachView(this);
        mLiveList = new ArrayList<Live>();
        mAdapter = new RecentLiveAdapter(mLiveList);
        mAdapter.setContext(this.getContext());
        mAdapter.setOnClickItemListener(this);
        mProgresDialogHelper = new ProgresDialogHelper(this.getContext());
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

//        ListRefreshHelper<Live> tempHelper = mRefreshHelper;
//        mRefreshHelper = new ListRefreshHelper<Live>(mSwRefresh,
//                mRecyclerView, mAdapter, this);
////        mRefreshHelper.setIsFootView(true);
//        if (null != tempHelper) {
//            mRefreshHelper.setHasMore(tempHelper.isHasMore());
//            mRefreshHelper.setIsRefreshing(tempHelper.isRefreshing());
//        }
        if (mLiveRefreshHelper == null) {
            mLiveRefreshHelper = new ListRefreshHelper<Live>(mSwRefresh,
                    mRecyclerView, mAdapter, this);
        }
        if (isFirst) {
            this.getMvpPresenter().requestLiveCache();
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mLlRoot.setPadding(0,
//                    CommonUtil.getStatusHeight(this.getActivity()), 0, 0);
//        }
        return view;
    }

    public ListRefreshHelper getListRefreshHelper() {
        if (mLiveRefreshHelper != null) {
            return mLiveRefreshHelper;
        }
        return null;
    }

    public void setIsClearList(boolean isRefreshing) {
        isPullDownRefresh = isRefreshing;
    }

    public MainActivity getMainActivity() {
        if (this.getActivity() instanceof MainActivity) {
            return (MainActivity) this.getActivity();
        }
        return null;
    }

    @Override
    public void showLoadingView() {
        if (mProgresDialogHelper != null) {
            mProgresDialogHelper.show();
        }
    }

    @Override
    public void hideLoadingView() {
        if (mProgresDialogHelper != null && mProgresDialogHelper.isShow()) {
            mProgresDialogHelper.dismiss();
        }
    }

    @Override
    public void onCacheSuccess(List<Live> liveList) {
        if (liveList != null) {
            mLiveList.clear();
            mLiveList.addAll(liveList);
        }
        if (!this.isNetworkEnable()) {
            isFirst = false;
        }
        if (mLiveRefreshHelper != null) {
            mLiveRefreshHelper.onRefreshAuto();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLiveSuccess(List<Live> liveList) {
        if (mLiveRefreshHelper != null) {
            if (mLiveRefreshHelper.isPullDownRefresh()) {
                mLiveRefreshHelper.clearList(mLiveList);
            } else if (isPullDownRefresh) {
                mLiveList.clear();
            }
            mLiveRefreshHelper.onRefreshDatasComplete(liveList);
            mLiveRefreshHelper.onRefreshViewComplete(false);
        }
        if (liveList != null) {
            mLiveList.addAll(liveList);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRoomInfoSuccess(Map<String, String> map) {
        if (map != null) {
            String socketIp = map.get("socket_host");
            Intent intent = new Intent(this.getContext(), LivePostActivity.class);
            if (!TextUtils.isEmpty(mRoomId)) {
                intent.putExtra("roomId", mRoomId);
            }
            if (!TextUtils.isEmpty(socketIp)) {
                intent.putExtra("socketIp", socketIp);
            }
            intent.putExtra("chid", mChid);
            intent.putExtra("startTime", mPlayStart);
            this.startActivity(intent);
        }
    }

    @Override
    public void showError(String error) {
        this.showToast(error);
        if (mLiveRefreshHelper != null) {
            mLiveRefreshHelper.onRefreshViewComplete(false);
        }
    }

    @Override
    public RecentLivePresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new RecentLivePresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(this.getContext()));
        }
        return mPresenter;
    }

    public void setRefreshPage(int page) {
        this.mPage = page;
        if (mLiveRefreshHelper != null) {
            mLiveRefreshHelper.setPage(mPage);
        }
    }

    @Override
    public void onPullDownRefresh() {
        AppHelper.getAppHelper().getUser(this.getActivity());
        if (mLiveRefreshHelper != null) {
            mLiveRefreshHelper.reset();
        }
        AppHelper.getAppHelper().getUser(this.getContext());
        mPage = 1;
        mLiveRefreshHelper.setPage(mPage);
        isPullDownRefresh = true;
//        AppHelper.getAppHelper().getDataChangeHelper().notifyChangeRefresh(true, AppHelper.getAppHelper().getDataChangeHelper().OPERATOR_MAIN_REFRESH, "RecentLiveFragment");
        this.getMvpPresenter().requestRecentLive(null, mLiveRefreshHelper.getMaxLoad(), mLiveRefreshHelper.getPage(), true);
        if (!isFirst) {
            MainActivity main = this.getMainActivity();
            if (main != null) {
                main.getMvpPresenter().requestTeacherInfo(false, true);
                if (main.getFragmentsHelper().getFragment(0) instanceof UserCourseFragment) {
                    UserCourseFragment fragment = (UserCourseFragment) main.getFragmentsHelper().getFragment(0);
                    fragment.setIsClearList(true);
                    fragment.setRefreshPage(mPage);
                    fragment.getMvpPresenter().requestTeacherCourse(0, mLiveRefreshHelper.getMaxLoad(), mLiveRefreshHelper.getPage(), "", true);
                }
            }
        }
        isFirst = false;
    }

    @Override
    public void onPullUpLoadMore() {
        mPage++;
        mLiveRefreshHelper.setPage(mPage);
        this.getMvpPresenter().requestRecentLive(null, mLiveRefreshHelper.getMaxLoad(), mLiveRefreshHelper.getPage(), false);
        isPullDownRefresh = false;
    }

    @Override
    public void onClickItem(int operator, Object item) {
        if (operator == 0) {
            return;
        }
        if (operator == GlobalConfig.ClickYype.LIVE_CLASS_ROOM_CLICK) {//跳转直播教室
            if (item != null) {
                Live live = (Live) item;
                if (live != null) {
                    int coid = live.getCoid();
                    mChid = live.getChid();
                    this.getMvpPresenter().requestGetLive(coid, mChid);
                    mPlayStart = live.getPlay_start();
                    mRoomId = live.getRoom_id();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.unBind();
        }
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mPresenter = null;
        super.onDestroy();
    }
}
