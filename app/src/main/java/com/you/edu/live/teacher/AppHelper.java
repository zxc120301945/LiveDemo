package com.you.edu.live.teacher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.model.bean.Organ;
import com.you.edu.live.teacher.model.bean.TeacherInfo;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.model.dao.SharedPreDao;
import com.you.edu.live.teacher.support.dao.ACacheProxy;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.HttpApi;
import com.you.edu.live.teacher.support.http.IHttpApi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by XingRongJIng on 2016/6/27.
 */
public class AppHelper {
    public static boolean mIsLog = false;

    public static final int CORE_THRED_POOL_SIZE = 5;
    private ExecutorService mExecutors;
    private Handler mHandler;
    private static AppHelper mHelper;
    private ICache mCache;
    private User mUser;
    private Organ mOrgan;
    private TeacherInfo mTeacherInfo;

    private AppHelper() {
    }

    public static AppHelper getAppHelper() {
        if (null == mHelper) {
            mHelper = new AppHelper();
        }
        return mHelper;
    }

    public User getUser(Context ctx) {
        if (null == mUser && null != ctx) {
            mUser = SharedPreDao.getObject(ctx,
                    GlobalConfig.SharedPreferenceDao.FILENAME_USER,
                    GlobalConfig.SharedPreferenceDao.KEY_USER_INFO);
        }
        if (null != mUser && !TextUtils.isEmpty(mUser.getCookie())) {
            this.getHttpApi(ctx).setCookie(mUser.getCookie());
        }
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public TeacherInfo getTeacher() {
        return mTeacherInfo;
    }

    public void setTeacher(TeacherInfo info) {
        mTeacherInfo = info;
    }

    public boolean isLogin(Context ctx) {
        return null != this.getUser(ctx);
    }

    public HttpConfigKeeper getHttpConfigKeeper(Context ctx) {
        return HttpConfigKeeper.getHttpConfigKeeper(ctx);
    }

    public AppUpdateKeeper getAppUpdateKeeper(Context ctx) {
        return AppUpdateKeeper.getAppUpdateKeeper(ctx);
    }

    /**
     * 注销
     *
     * @param ctx
     */
    public void logout(Context ctx) {
        this.setUser(null);
        SharedPreDao.deleteOne(ctx,
                GlobalConfig.SharedPreferenceDao.FILENAME_USER,
                GlobalConfig.SharedPreferenceDao.KEY_USER_INFO);
        ShareSDK.initSDK(ctx);
        ShareSDK.getPlatform(ctx, SinaWeibo.NAME).removeAccount();
        ShareSDK.getPlatform(ctx, Wechat.NAME).removeAccount();
    }

    /**
     * Http请求单一实例
     *
     * @param ctx
     * @return
     */
    public IHttpApi getHttpApi(Context ctx) {
        return HttpApi.getHttpApi(this.getUserAgent(ctx));
    }

    /**
     * 文件缓存单一实例
     *
     * @param ctx
     * @return
     */
    public ICache getCache(Context ctx) {
        if (null == mCache) {
            mCache = new ACacheProxy(ctx,
                    this.getExecutors(), this.getHandler());
        }
        return mCache;
    }

    public ExecutorService getExecutors() {
        if (null == mExecutors) {
            mExecutors = Executors.newFixedThreadPool(CORE_THRED_POOL_SIZE);
        }
        return mExecutors;
    }

    public Handler getHandler() {
        if (null == mHandler) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    public boolean isNetworkEnable(Context ctx) {
        if (null == ctx) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isWifiConnected(Context ctx) {
        if (null == ctx) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (int i = 0; i < infos.length; i++) {
                    NetworkInfo info = infos[i];
                    if (info.getState() == NetworkInfo.State.CONNECTED
                            && ConnectivityManager.TYPE_WIFI == info.getType()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void showToast(Context ctx, String msg) {
        if (null == ctx) {
            return;
        }
        this.showToast(ctx, msg, R.layout.view_toast, R.id.common_toast_tv_tips);
    }

    public void showToast(Context ctx, String msg, int layoutId, int textId) {
        if (null == ctx) {
            return;
        }
        View toastRoot = LayoutInflater.from(ctx).inflate(layoutId, null);
        if (null == toastRoot) {
            return;
        }
        TextView message = (TextView) toastRoot.findViewById(textId);
        if (null == message) return;
        message.setText(msg);
        Toast toastStart = new Toast(ctx);
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    /**
     * 获取应用版本信息
     *
     * @param context
     * @return
     */
    public String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserAgent(Context ctx) {
        StringBuilder sb = new StringBuilder();
        String appVersion = null;
        if (null != ctx) {
            appVersion = this.getAppVersion(ctx);
        } else {
            appVersion = "volley/0";
        }
        sb.append("TeacherLive").append("/").append(appVersion).append("(")
                .append(android.os.Build.MODEL).append(";").append("Android ")
                .append(android.os.Build.VERSION.RELEASE).append(")");
        return sb.toString();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 退出应用
     */
    public void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
