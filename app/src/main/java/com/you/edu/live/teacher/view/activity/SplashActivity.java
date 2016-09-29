package com.you.edu.live.teacher.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;

import com.you.edu.live.teacher.AppHelper;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.SharedPreDao;
import com.you.edu.live.teacher.view.BaseViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Title: 闪屏界面
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/7/19
 * @Time 上午11:02
 * @Version 1.0.3
 */
public class SplashActivity extends BaseViewActivity implements Callback {

    private static final int SPLASH_DELAY = 3 * 1000;
    private static final int MSG_SPLASH = 1;
    @BindView(R.id.app_version)
    TextView mAppVersion;
    private Handler mHandler;
    private Unbinder mUnbinder;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (!this.isTaskRoot()) { // 判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            // 如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;// finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_splash);
        mUnbinder = ButterKnife.bind(this);
        User user = SharedPreDao.getObject(this.getApplicationContext(),
                GlobalConfig.SharedPreferenceDao.FILENAME_USER,
                GlobalConfig.SharedPreferenceDao.KEY_USER_INFO);
        AppHelper.getAppHelper().setUser(user);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessageDelayed(MSG_SPLASH, SPLASH_DELAY);
        mAppVersion.setText(getVersion());
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SPLASH:
                // 首次启动应用
//                if (!SharedPreDao.getBoolean(this,
//                        GlobalConfig.SharedPreferenceDao.KEY_USER_GUIDE)) {
//                    Intent intent = new Intent(SplashActivity.this, AppGuideActivity.class);
//                    this.startActivity(intent);
//                } else {
                if (AppHelper.getAppHelper().getUser(SplashActivity.this) == null) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    this.startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    this.startActivity(intent);
                }
//                }
                this.finish();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);

        }
        mHandler = null;
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
//            return this.getString(R.string.version_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
