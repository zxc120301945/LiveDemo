package com.you.edu.live.teacher.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.ICreateLiveContract;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Room;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.presenter.CreateLivePresenter;
import com.you.edu.live.teacher.presenter.helper.ThirdShareHelper;
import com.you.edu.live.teacher.utils.TimeUtil;
import com.you.edu.live.teacher.view.BaseViewActivity;
import com.you.edu.live.teacher.view.picker.LiveLengthSelectorDatePicker;
import com.you.edu.live.teacher.view.picker.StartLiveSelectorDatePicker;
import com.you.edu.live.teacher.view.switchbtn.SwitchButton;
import com.you.edu.live.teacher.widget.PopWindowHelper;
import com.you.edu.live.teacher.widget.ProgresDialogHelper;
import com.you.edu.live.teacher.widget.SoftKeyboardHelper;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @Title: 新建直播
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/7/19
 * @Time 上午11:02
 * @Version 1.0.3
 */
public class CreateLiveActivity extends BaseViewActivity implements ICreateLiveContract.ICreateLiveView, SoftKeyboardHelper.OnKeyboardShownListener, View.OnClickListener {

    //title
    @BindView(R.id.titlebar_back_rl_root)
    RelativeLayout mTitlebarRoot;
    @BindView(R.id.titlebar_back_tv_title)
    TextView mTitlebarTitle;
    @BindView(R.id.titlebar_back_iv_back)
    ImageView mTitlebarIvBack;

    //View
    @BindView(R.id.ll_create_live_root)
    LinearLayout mLlCreateLiveRoot;
    @BindView(R.id.tv_chapter_name_des)
    EditText mEtChapterName;
    @BindView(R.id.tv_start_live)
    TextView mTvStartLive;
    @BindView(R.id.tv_live_length)
    TextView mTvLiveLength;
    @BindView(R.id.btn_start_live)
    TextView mBtnStartLive;
    @BindView(R.id.btn_try_live)
    TextView mBtnPilotLive;
    @BindView(R.id.btn_save_live)
    TextView mBtnSaveLive;

    @BindView(R.id.iv_share_sina_weibo)
    ImageView mIvShareSinaWeibo;
    @BindView(R.id.iv_share_wechat)
    ImageView mIvShareWechat;
    @BindView(R.id.iv_share_wechat_friends)
    ImageView mIvShareWechatFriends;

    private static final int RECOND_REFRESH = 1;

    public static final String TITLE = "优酷学堂";

    private static final int TYPE_START_LIVE = 1;
    private static final int TYPE_PILOT_LIVE = 2;
    private static final int TYPE_SAVE_LIVE = 3;

    private static final int TYPE_SHARE_NOMAL = 0;
    private static final int TYPE_SHARE_WEIBO = 1;
    private static final int TYPE_SHARE_WEICHAT = 2;
    private static final int TYPE_SHARE_FRIENDS = 3;
    @BindView(R.id.swb_free_btn)
    SwitchButton mSwbFreeBtn;

    private int mShareType = TYPE_SHARE_NOMAL;

    private int mType = TYPE_START_LIVE;

    private Unbinder mUnbinder;
    private int mCoid;
    private int mChid;
    private String mRoomId;
    private String mSocketIp;
    private CreateLivePresenter mPresenter;

    private PopWindowHelper mLiveLengthPWTimeHelper;//直播时长popupwindow
    private PopWindowHelper mStartLivePWTimeHelper;//开始直播popupwindow
    private StartLiveSelectorDatePicker mStartLivePicker;
    private LiveLengthSelectorDatePicker mLiveLengthPicker;

    private long mStartTime = 0;//直播时间 int类型
    private String mStartDate = "";//直播时间 例：2016-7-7 15:20

    private int mLiveLength = 0;//直播时间 秒类型
    private String mLiveLengthDate = "";//直播时长 例：15分钟
    private SoftKeyboardHelper mSoftKeyHelper;
    private ProgresDialogHelper mProDialogHelper;

    private boolean isCheckWeibo = false;
    private boolean isCheckWeiChat = false;
    private boolean isCheckFriends = false;
    private ThirdShareHelper mShareHelper;

    private String mShareStatus;
    private String mShareTeacherName;
    private String mShareClassName;
    private String mShareUrl;
    private String mShareDate;
    private String mShareThumb;

    private boolean isShareFirst = true;

    private boolean isShareWeChat = false;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
//        /**
//         * 更改状态栏颜色 只适用于系统版本4.4及以上
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager
//                    .setStatusBarTintResource(R.color.translucent);// 通知栏所需颜色
//        }
        this.setContentView(R.layout.activity_create_live);
        mUnbinder = ButterKnife.bind(this);
        this.getMvpPresenter().attachView(this);
        this.prepareData();
        this.prepareView();
    }

    private void prepareData() {

        mCoid = getIntent().getIntExtra("coid", -1);
        List<String> dayList = TimeUtil.getDaysOfNextMonths();
        String[] days = (String[]) dayList.toArray(new String[dayList.size()]);//获取的未来120天的时间
        mSoftKeyHelper = new SoftKeyboardHelper(mLlCreateLiveRoot, this);
        mProDialogHelper = new ProgresDialogHelper(this);
        this.prepareLiveLengthPW();
        this.prepareStartLivePW(days);
        ShareSDK.initSDK(this);

    }

    private void prepareStartLivePW(String[] days) {

        View start_live_selector = View.inflate(this,
                R.layout.view_start_time_selector, null);//开始直播时间轮
        RelativeLayout root = (RelativeLayout) start_live_selector.findViewById(R.id.rl_sratr_live_root);
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param1.addRule(RelativeLayout.BELOW, R.id.rl_picker_title);//此控件在id为rl_live_length_root的控件的下边
        mStartLivePicker = new StartLiveSelectorDatePicker(this, days);
        ColorDrawable dw = new ColorDrawable(this.getResources().getColor(R.color.trailer_add_time_picker_bg));
        mStartLivePicker.setBackground(dw);
        root.addView(mStartLivePicker, param1);

        if (mStartLivePWTimeHelper == null) {
            mStartLivePWTimeHelper = new PopWindowHelper(this, start_live_selector);
        }

        mIvShareSinaWeibo.setOnClickListener(this);
        mIvShareWechat.setOnClickListener(this);
        mIvShareWechatFriends.setOnClickListener(this);
    }

    private void prepareLiveLengthPW() {
        View live_length_selector = View.inflate(this,
                R.layout.view_live_length_selector, null);//直播时长时间轮
        mLiveLengthPicker = (LiveLengthSelectorDatePicker) live_length_selector.findViewById(R.id.picker_mins);
        if (mLiveLengthPWTimeHelper == null) {
            mLiveLengthPWTimeHelper = new PopWindowHelper(this, live_length_selector);
        }
    }

    private void prepareView() {
        mTitlebarRoot.setBackgroundColor(new Color().WHITE);
        mTitlebarTitle.setText(getString(R.string.create_live));
        mTitlebarTitle.setTextColor(getResources().getColor(R.color.tv_color_deep_blue));
        mTvStartLive.setVisibility(View.GONE);
        mTvLiveLength.setVisibility(View.GONE);
        mBtnStartLive.setVisibility(View.GONE);
        mBtnPilotLive.setVisibility(View.VISIBLE);
        mBtnSaveLive.setVisibility(View.VISIBLE);
        mEtChapterName.addTextChangedListener(mWatcher);
        mTitlebarIvBack.setImageResource(R.drawable.black_back);
        mTitlebarIvBack.setBackgroundResource(R.drawable.common_text_bg_selector);

        this.setImageViewIcon(TYPE_SHARE_NOMAL);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mLlCreateLiveRoot.setPadding(0,
//                    CommonUtil.getStatusHeight(this), 0, 0);
//        }
    }


    /**
     * 返回
     */
    @OnClick(R.id.titlebar_back_iv_back)
    public void onClickBack() {
        this.finish();
    }


    @Override
    protected void onDestroy() {
        this.getMvpPresenter().detachView();
        this.getMvpPresenter().destroy();
        mPresenter = null;
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        ShareSDK.stopSDK();
        super.onDestroy();
    }

    @Override
    public void showLoadingView() {
        if (mProDialogHelper != null) {
            mProDialogHelper.show();
        }
    }

    @Override
    public void hideLoadingView() {
        if (mProDialogHelper != null && mProDialogHelper.isShow()) {
            mProDialogHelper.dismiss();
        }
    }

    @Override
    public void onChapterSuccess(Map<String, String> chapterInfo) {
        if (mProDialogHelper != null) {
            mProDialogHelper.show();
        }

        if (chapterInfo != null) {
            String chId = chapterInfo.get("chid");
            String coid = chapterInfo.get("coid");
            String cover = chapterInfo.get("cover");
            mChid = Integer.parseInt(chId);
            mRoomId = chapterInfo.get("room_id");
            mSocketIp = chapterInfo.get("socket_host");
            if (!TextUtils.isEmpty(chId) && !TextUtils.isEmpty(coid)) {
                mShareUrl = GlobalConfig.HttpUrl.HTML_URL + "coid=" + coid + "&" + "chid=" + mChid;
            }
            if (!TextUtils.isEmpty(cover)) {
                mShareThumb = cover;
            }
            User user = AppHelper.getAppHelper().getUser(this);
            if (user != null) {
                mShareTeacherName = user.getUser_name();
            }
            if (mShareType == TYPE_SHARE_WEIBO) {//微博分享
                isShareWeChat = false;
                this.shareToSinaWeibo(mShareStatus, mShareTeacherName, mShareDate, mShareClassName, mShareUrl, mShareThumb);
            } else if (mShareType == TYPE_SHARE_WEICHAT) {//微信分享
                isShareWeChat = true;
                this.shareToWeixinComments(mShareStatus, mShareTeacherName, mShareDate, mShareClassName, mShareUrl, mShareThumb);
            } else if (mShareType == TYPE_SHARE_FRIENDS) {//朋友圈分享
                isShareWeChat = true;
                this.shareToWeixinFriends(mShareStatus, mShareTeacherName, mShareDate, mShareClassName, mShareUrl, mShareThumb);
            } else {
                this.jumpActivity();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == RECOND_REFRESH) {
            this.setResult(Activity.RESULT_OK);
            this.finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mProDialogHelper != null && mProDialogHelper.isShow()) {
            mProDialogHelper.dismiss();
        }
    }

    @Override
    public void onRoomSuccess(Room room) {
//        if (room != null) {
//            String room_id = room.getRoom_id();
//            if (!TextUtils.isEmpty(room_id)) {
//                Intent intent = new Intent();
//                if (!TextUtils.isEmpty(mChid)) {
//                    int chid = Integer.parseInt(mChid);
//                    intent.putExtra("chid", chid);
//                }
//                intent.putExtra("room_id", room_id);
//            }
//        }
    }

    @Override
    public void showError(String error) {
        this.showToast(error);
    }

    @Override
    public void onShareComplete(Platform plat, HashMap<String, Object> info) {
        this.showToast(this.getString(R.string.share_complete));
        if (SinaWeibo.NAME.equals(plat.getName())) {
            this.jumpActivity();
        }
    }

    @Override
    public void onShareError(Platform plat, String error) {
        if (SinaWeibo.NAME.equals(plat.getName())) {
            if (isShareFirst) {
                this.shareToSinaWeibo(mShareStatus, mShareTeacherName, mShareDate, mShareClassName, mShareUrl, mShareThumb);
                isShareFirst = false;
            } else {
                this.showToast(error);
                this.jumpActivity();
            }
        } else {
            this.showToast(error);
        }
    }

    @Override
    public void onShareCancel(Platform plat) {
        if (SinaWeibo.NAME.equals(plat.getName())) {
            this.jumpActivity();
        }
    }

    @Override
    public CreateLivePresenter getMvpPresenter() {
        if (mPresenter == null) {
            mPresenter = new CreateLivePresenter(this.getHttpApi(), AppHelper.getAppHelper().getCache(this));
        }
        return mPresenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShareWeChat) {
            this.jumpActivity();
        }
    }

    /**
     * 匹配字数
     */
    public TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int mTextMaxlenght = 0;
            Editable editable = mEtChapterName.getText();
            String str = editable.toString().trim();

            //得到最初字段的长度大小，用于光标位置的判断
            int selEndIndex = Selection.getSelectionEnd(editable);
            // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
            //如果是汉字则为两个字符
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                //32-122包含了空格，大小写字母，数字和一些常用的符号，
                //如果在这个范围内则算一个字符，
                //如果不在这个范围比如是汉字的话就是两个字符
                if (charAt >= 32 && charAt <= 122) {
                    mTextMaxlenght++;
                } else {
                    mTextMaxlenght += 2;
                }
                // 当最大字符大于60时，进行字段的截取，并进行提示字段的大小
                if (mTextMaxlenght > 60) {
                    // 截取最大的字段
                    String newStr = str.substring(0, i);
                    mEtChapterName.setText(newStr);
                    // 得到新字段的长度值
                    editable = mEtChapterName.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    // 设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
//                    CreateLiveActivity.this.showToast(getString(R.string.please_input_excess_thirty_number));
                    CreateLiveActivity.this.showToast("请不要超过2-30个字，4-60个字符");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 选择开始直播时间
     */
    public void onClickStartLiveTime(View v) {
        if (mStartLivePWTimeHelper != null) {
            mStartLivePWTimeHelper.showAtLocation(v);
        }
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
    }

    /**
     * 选择直播时间中的立即直播按钮
     *
     * @param v
     */
    public void onClickStartLive(View v) {
        if (mStartLivePWTimeHelper != null && mStartLivePWTimeHelper.isShowing()) {
            mStartLivePWTimeHelper.dismiss();
        }
        mStartDate = "";
        mStartTime = 0;
        mTvStartLive.setVisibility(View.VISIBLE);
        mTvStartLive.setText(getString(R.string.start_live));
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
        this.setBtnVisibility(true);
    }

    /**
     * 选择直播时间完成
     *
     * @param v
     */
    public void onClickStartLiveDateComplete(View v) {
        long startLiveTime = mStartLivePicker.getTime();
        String date = mStartLivePicker.getDate();
        long systemTime = System.currentTimeMillis();


        if (this.isTrailerTime(startLiveTime, systemTime)) {
            this.showToast(this.getString(R.string.trailer_time));
        } else {
            mTvStartLive.setVisibility(View.VISIBLE);
            if (this.isNowDate(date, startLiveTime)) {
                mTvStartLive.setText(getString(R.string.start_live));
                mStartDate = "";
                mStartTime = 0;
                this.setBtnVisibility(true);
            } else {
                mTvStartLive.setText(mStartLivePicker.getDate());
                mStartTime = startLiveTime;
                mStartDate = mStartLivePicker.getDate();
                this.setBtnVisibility(false);
            }
            if (mStartLivePWTimeHelper != null && mStartLivePWTimeHelper.isShowing()) {
                mStartLivePWTimeHelper.dismiss();
            }
        }
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
    }

    /**
     * 选择直播时长
     */
    public void onClickLiveLength(View v) {
        if (mLiveLengthPWTimeHelper != null) {
            mLiveLengthPWTimeHelper.showAtLocation(v);
        }
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
    }

    /**
     * 选择直播时长完成
     *
     * @param v
     */
    public void onClickLiveLengthComplete(View v) {
        if (mLiveLengthPWTimeHelper != null && mLiveLengthPWTimeHelper.isShowing()) {
            mLiveLengthPWTimeHelper.dismiss();
        }
        if (mLiveLengthPicker != null) {
            mLiveLength = mLiveLengthPicker.getLiveLengthTime();
            mLiveLengthDate = mLiveLengthPicker.getLiveLengthDate();
            mTvLiveLength.setVisibility(View.VISIBLE);
            mTvLiveLength.setText(mLiveLengthDate);
        }
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
    }

    /**
     * 是否是现在时间
     *
     * @return
     */
    private boolean isNowDate(String date, long startLiveTime) {

        String year = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_YEAR);
        String month = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_MONTH);
        String day = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_DAY);

        boolean today = TimeUtil.isToday(startLiveTime);
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int hour = t.hour;    // 0-23
        int minute = t.minute;
        if (today) {
            String[] split = date.split("\\s+");
            String s = split[1];
            if (!TextUtils.isEmpty(s)) {
                String[] split1 = s.split(":");
                String hours = split1[0];
                String min = split1[1];
                int h = Integer.parseInt(hours);
                int m = Integer.parseInt(min);
                if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(min)) {
                    if (hour == h && minute == m) {
                        return true;
                    }
                }
            }
        }
//        Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeInMillis(systemTime);
//        int mMinuts = mCalendar.get(Calendar.MINUTE);
        return false;
    }

    /*
     * 两个时间对比
	 */
    private boolean isTrailerTime(long start_time, long system_Time) {
        Date date = this.getDateBySeconds(start_time);
        long time = date.getTime();
        long seconds = (time - system_Time) / 1000;
        long minute = seconds / 60;
        return minute <= -1;
    }

    private Date getDateBySeconds(long seconds) {
        return new Date(seconds * 1000);
    }


    @OnClick(R.id.ll_create_live_root)
    public void onClickRoot() {
        if (mSoftKeyHelper != null && mSoftKeyHelper.isSoftInputOpen()) {
            mSoftKeyHelper.hideSoftInput();
        }
    }

    private void setBtnVisibility(boolean isBtnStartLive) {
        if (isBtnStartLive) {
            mBtnStartLive.setVisibility(View.VISIBLE);
            mBtnPilotLive.setVisibility(View.GONE);
            mBtnSaveLive.setVisibility(View.GONE);
        } else {
            mBtnStartLive.setVisibility(View.GONE);
            mBtnPilotLive.setVisibility(View.VISIBLE);
            mBtnSaveLive.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 底下的button按钮
     *
     * @param view
     */
    @OnClick({R.id.btn_start_live, R.id.btn_try_live, R.id.btn_save_live})
    public void onClickButton(View view) {
        String chapterName = mEtChapterName.getText().toString().trim();
        if (TextUtils.isEmpty(chapterName)) {
            this.showToast(getString(R.string.no_chapter_name));
            return;
        }
        if (chapterName.length() < 2) {
            this.showToast("请输入最低2个课程名称");
            return;
        }
        mShareClassName = chapterName;
        switch (view.getId()) {
            case R.id.btn_start_live://立即直播
                if (mLiveLength == 0) {
                    this.showToast(getString(R.string.input_live_length));
                    return;
                }
                if (mTvStartLive.getVisibility() != View.VISIBLE) {
                    this.showToast(getString(R.string.input_start_live_time));
                    return;
                }
                String time = mTvStartLive.getText().toString().toString();
                if (TextUtils.isEmpty(time)) {
                    this.showToast(getString(R.string.input_start_live_time));
                    return;
                }
                if (mStartTime == 0) {
                    mStartDate = null;
                }
                mShareDate = mStartDate;
                mShareStatus = "live";
                this.getMvpPresenter().requestCreateClass(mCoid, chapterName, 10, 0, false, mLiveLength, mStartDate, mSwbFreeBtn.isChecked(), null, null, 0, null);
                mType = TYPE_START_LIVE;
                break;
            case R.id.btn_try_live://试播并保存
                if (mLiveLength == 0) {
                    this.showToast(getString(R.string.input_live_length));
                    return;
                }
                if (mStartTime == 0) {
                    this.showToast(getString(R.string.please_input_start_live_time));
                    return;
                }
                if (TextUtils.isEmpty(mStartDate)) {
                    mStartDate = mStartLivePicker.getDate();
                }
                mShareDate = mStartDate;
                mShareStatus = "foreshow";
                this.getMvpPresenter().requestCreateClass(mCoid, chapterName, 10, 0, false, mLiveLength, mStartDate, mSwbFreeBtn.isChecked(), null, null, 0, null);
                mType = TYPE_PILOT_LIVE;
                break;
            case R.id.btn_save_live://保存
                if (mLiveLength == 0) {
                    this.showToast(getString(R.string.input_live_length));
                    return;
                }
                if (mStartTime == 0) {
                    this.showToast(getString(R.string.please_input_start_live_time));
                    return;
                }
                if (TextUtils.isEmpty(mStartDate)) {
                    mStartDate = mStartLivePicker.getDate();
                }
                mShareDate = mStartDate;
                mShareStatus = "foreshow";
                this.getMvpPresenter().requestCreateClass(mCoid, chapterName, 10, 0, false, mLiveLength, mStartDate, mSwbFreeBtn.isChecked(), null, null, 0, null);
                mType = TYPE_SAVE_LIVE;
                break;
        }
    }

    /**
     * 点击分享按钮
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_sina_weibo://微博
                isCheckWeibo = !isCheckWeibo;
                if (isCheckWeibo) {
                    isCheckFriends = isCheckWeiChat = false;
                    this.setImageViewIcon(TYPE_SHARE_WEIBO);
                } else {
                    this.setImageViewIcon(TYPE_SHARE_NOMAL);
                }
                break;
            case R.id.iv_share_wechat://微信
                isCheckWeiChat = !isCheckWeiChat;
                if (isCheckWeiChat) {
                    isCheckFriends = isCheckWeibo = false;
                    this.setImageViewIcon(TYPE_SHARE_WEICHAT);
                } else {
                    this.setImageViewIcon(TYPE_SHARE_NOMAL);
                }
                break;
            case R.id.iv_share_wechat_friends://朋友圈
                isCheckFriends = !isCheckFriends;
                if (isCheckFriends) {
                    isCheckWeiChat = isCheckWeibo = false;
                    this.setImageViewIcon(TYPE_SHARE_FRIENDS);
                } else {
                    this.setImageViewIcon(TYPE_SHARE_NOMAL);
                }
                break;
        }
    }

    /**
     * 设置分享图标
     *
     * @param type
     */
    private void setImageViewIcon(int type) {
        mShareType = type;
        if (type == TYPE_SHARE_NOMAL) {
            mIvShareSinaWeibo.setImageResource(R.drawable.sina_weibo_icon_normal);
            mIvShareWechat.setImageResource(R.drawable.wechat_icon_normal);
            mIvShareWechatFriends.setImageResource(R.drawable.wechat_friend_icon_normal);
        } else if (type == TYPE_SHARE_WEIBO) {
            mIvShareSinaWeibo.setImageResource(R.drawable.sina_weibo_icon_selected);
            mIvShareWechat.setImageResource(R.drawable.wechat_icon_normal);
            mIvShareWechatFriends.setImageResource(R.drawable.wechat_friend_icon_normal);
        } else if (type == TYPE_SHARE_WEICHAT) {
            mIvShareSinaWeibo.setImageResource(R.drawable.sina_weibo_icon_normal);
            mIvShareWechat.setImageResource(R.drawable.wechat_icon_selected);
            mIvShareWechatFriends.setImageResource(R.drawable.wechat_friend_icon_normal);
        } else if (type == TYPE_SHARE_FRIENDS) {
            mIvShareSinaWeibo.setImageResource(R.drawable.sina_weibo_icon_normal);
            mIvShareWechat.setImageResource(R.drawable.wechat_icon_normal);
            mIvShareWechatFriends.setImageResource(R.drawable.wechat_friend_icon_selected);
        }
    }

    /**
     * 微信朋友圈分享
     */
    public void shareToWeixinFriends(String status, String teacherName, String date, String className, String shareUrl, String thumb) {

        Platform plat = ShareSDK.getPlatform(WechatMoments.NAME);
        if (!plat.isClientValid()) {
            this.showToast(getString(R.string.login_no_wechat_app));
            return;
        }
        this.getMvpPresenter().shareToWeixinFriends(plat, status, teacherName, date, className, shareUrl, thumb);
    }

    /**
     * 微信分享
     */
    public void shareToWeixinComments(String status, String teacherName, String date, String className, String shareUrl, String thumb) {
        Platform plat = ShareSDK.getPlatform(Wechat.NAME);
        if (!plat.isClientValid()) {
            this.showToast(getString(R.string.login_no_wechat_app));
            return;
        }
        this.getMvpPresenter().shareToWeixinComments(plat, status, teacherName, date, className, shareUrl, thumb);
    }

    /**
     * 微博分享
     */
    public void shareToSinaWeibo(String status, String teacherName, String date, String className, String shareUrl, String thumb) {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!weibo.isClientValid()) {
            this.showToast(getString(R.string.share_no_weibo_app));
            return;
        }
        this.getMvpPresenter().shareToSinaWeibo(weibo, status, teacherName, date, className, shareUrl, thumb);

    }

    /**
     * 跳转对应页面
     */
    private void jumpActivity() {
        if (mType == TYPE_START_LIVE) {
            Intent intent = new Intent(this, LivePostActivity.class);
            if (!TextUtils.isEmpty(mRoomId)) {
                intent.putExtra("roomId", mRoomId);
            }
            intent.putExtra("chid", mChid);
            if (!TextUtils.isEmpty(mSocketIp)) {
                intent.putExtra("socketIp", mSocketIp);
            }
            if (mStartTime != 0) {
                intent.putExtra("startTime", mStartTime);
            }
            if (mProDialogHelper != null && mProDialogHelper.isShow()) {
                mProDialogHelper.dismiss();
            }
            this.startActivityForResult(intent, RECOND_REFRESH);
        } else if (mType == TYPE_PILOT_LIVE) {
            if (mStartTime == 0) {
                this.showToast(getString(R.string.please_input_start_live_time));
                return;
            }
            Intent intent = new Intent(this, LivePostActivity.class);
            if (!TextUtils.isEmpty(mRoomId)) {
                intent.putExtra("roomId", mRoomId);
            }
            intent.putExtra("chid", mChid);
            if (!TextUtils.isEmpty(mSocketIp)) {
                intent.putExtra("socketIp", mSocketIp);
            }
            if (mStartTime != 0) {
                intent.putExtra("startTime", mStartTime);
            }
            intent.putExtra("startTime", mStartTime);
            if (mProDialogHelper != null && mProDialogHelper.isShow()) {
                mProDialogHelper.dismiss();
            }
            this.startActivityForResult(intent, RECOND_REFRESH);
        } else if (mType == TYPE_SAVE_LIVE) {
            this.showToast(getString(R.string.save_success));
            this.setResult(Activity.RESULT_OK);
            this.finish();
        }
        if (mProDialogHelper != null && mProDialogHelper.isShow()) {
            mProDialogHelper.dismiss();
        }
    }


    @Override
    public void onKeyboardShown(boolean shown) {

    }
}
