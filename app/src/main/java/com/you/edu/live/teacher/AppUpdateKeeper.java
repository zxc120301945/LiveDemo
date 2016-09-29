package com.you.edu.live.teacher;

import java.io.File;

import org.xutils.common.Callback.Cancelable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.you.edu.live.teacher.model.GlobalConfig;
import com.you.edu.live.teacher.presenter.AppUpdatePresenter;
import com.you.edu.live.teacher.presenter.helper.AppForceUpdateViewHelper;
import com.you.edu.live.teacher.support.http.DownloadNotificationKeeper;
import com.you.edu.live.teacher.support.http.listener.ProgressCallBack;
import com.you.edu.live.teacher.support.http.model.Download;
import com.you.edu.live.teacher.utils.CachePathUtils;
import com.you.edu.live.teacher.utils.FileUtil;
import com.you.edu.live.teacher.widget.DialogHelper;

/**
 * 版本升级管家
 *
 * @author XingRongJing
 */
public class AppUpdateKeeper {

    private static final int NOTIFY_ID = 2;
    private static final String APK_NAME = "YouLiveTeacher.apk";
    private static AppUpdateKeeper mUpdateKeeper;
    private DownloadNotificationKeeper mNotificationHelper;
    private AppForceUpdateViewHelper mForceUpdateViewHelper;
    private Context mCtx;
    /**
     * 是否强制升级
     **/
    private boolean mIsForceAppUpdate = false;
    /**
     * App升级Dialog
     **/
    private DialogHelper mAppUpdateDialog, mDownloadDialog;

    private AppUpdateKeeper(Context ctx) {
        if (null == ctx) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.mCtx = ctx.getApplicationContext();
        mNotificationHelper = new DownloadNotificationKeeper(this.mCtx, null,
                NOTIFY_ID);
    }

    static AppUpdateKeeper getAppUpdateKeeper(Context ctx) {
        if (null == mUpdateKeeper) {
            mUpdateKeeper = new AppUpdateKeeper(ctx);
        }
        return mUpdateKeeper;
    }

    public void requestAppUpdateInfo(Context ctx) {
        this.getPresenter(ctx).requestAppUpdateInfo(ctx);
    }

    public void onClickDownloadApk(Context ctx, String url) {
        String apkPath = AppHelper.getAppHelper().getAppUpdateKeeper(ctx)
                .buildLocalApkPath();
        // 删除之前历史
        FileUtil.deleteFile(apkPath);
        String userAgent = AppHelper.getAppHelper().getHttpConfigKeeper(ctx)
                .getUserAgent(ctx);
        Download download = new Download();
        download.setUrl(url);
        download.setLocalUrl(apkPath);
        download.setResName(APK_NAME);
        this.startDownloadApk(download, userAgent);
    }

    public AppUpdatePresenter getPresenter(Context ctx) {
        return new AppUpdatePresenter(AppHelper.getAppHelper().getHttpApi(ctx),
                AppHelper.getAppHelper().getCache(ctx));
    }

    /**
     * 开始下载Apk
     *
     * @param download 文件
     */
    private void startDownloadApk(Download download, String userAgent) {
        if (null == download) {
            return;
        }
        String url = download.getUrl();
        String localPath = download.getLocalUrl();
        if (TextUtils.isEmpty(localPath) || TextUtils.isEmpty(url)) {
            return;
        }

        Cancelable AppUpgradeListener = XutilsKeeper.DownLoadFile(url, localPath, new AppUpgradeListener(download));

//		HttpUtils http = new HttpUtils();
//		http.configUserAgent(userAgent);
//		HttpHandler<File> httpHanlder = http.download(url, localPath, false,
//				false, new AppUpgradeListener(download));
//		download.setHttpHanlder(httpHanlder);
    }

    /**
     * 安装Apk
     *
     * @param apkPath
     */
    public void startInstallApk(String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    /**
     * 构建本地apk下载地址
     *
     * @return
     */
    public String buildLocalApkPath() {
        String path = CachePathUtils.getCacheFilePath(mCtx, GlobalConfig.DIR);
        return path + "/" + APK_NAME;
    }

    /**
     * 是否强制升级（规则：App版本小于最小版本则强制）
     *
     * @param appVersion
     * @param minVersion
     * @return
     */
    public boolean isForceUpdate(String appVersion, String minVersion) {
        if (TextUtils.isEmpty(appVersion) || TextUtils.isEmpty(minVersion)) {
            return false;
        }
        if (appVersion.compareTo(minVersion) < 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要升级（规则：App版本小于当前本则提示）
     *
     * @param appVersion
     * @return
     */
    public boolean isNeedUpdate(String appVersion, String currVersion) {
        if (TextUtils.isEmpty(appVersion) || TextUtils.isEmpty(currVersion)) {
            return false;
        }
        if (appVersion.compareTo(currVersion) < 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要提示升级（规则：提示次数大于0,则需要提示,否则不提示）
     *
     * @param tipsCount
     * @return
     */
    public boolean isNeedUpdateTips(int tipsCount) {
        if (tipsCount > 0) {
            return true;
        }
        return false;
    }

    public boolean isForceAppUpdate() {
        return mIsForceAppUpdate;
    }

    public void setIsForceAppUpdate(boolean mIsForceAppUpdate) {
        this.mIsForceAppUpdate = mIsForceAppUpdate;
    }

    /**
     * 显示app更新Dialog
     *
     * @param ctx
     * @param downloadUrl
     * @param desc
     */
    public void showAppUpdateDialog(Context ctx, String downloadUrl, String desc) {
        View view = LayoutInflater.from(ctx).inflate(
                R.layout.dialog_app_upgrade, null);
        TextView tvAppUpdateTips = (TextView) view
                .findViewById(R.id.dialog_app_upgrade_tv_msg);
        tvAppUpdateTips.setText(desc);
        Button tvSure = (Button) view
                .findViewById(R.id.dialog_app_upgrade_btn_upgrade);
        tvSure.setTag(downloadUrl);
        if (this.isForceAppUpdate()) {// 强制刷新去掉“取消”按钮
            Button tvCancel = (Button) view
                    .findViewById(R.id.dialog_app_upgrade_btn_cancel);
            tvCancel.setVisibility(View.GONE);
        }
        mAppUpdateDialog = new DialogHelper(ctx, view);
        mAppUpdateDialog.setCanceledOnTouchOutside(!this.isForceAppUpdate());
        mAppUpdateDialog.setCancelable(!this.isForceAppUpdate());

        mAppUpdateDialog.show();
    }

    public void dismissAppUpdateDialog() {
        if (null != mAppUpdateDialog) {
            mAppUpdateDialog.dismiss();
            mAppUpdateDialog = null;
        }
    }

    public void dismissDownloadDialog() {
        if (null != mDownloadDialog) {
            mDownloadDialog.dismiss();
        }
    }

    public void showForceUpdateDownloadProDialog(Context ctx, String downUrl) {
        if (null == mDownloadDialog) {
            View view = LayoutInflater.from(ctx).inflate(
                    R.layout.notification_download, null);
            view.setTag(downUrl);
            mForceUpdateViewHelper = new AppForceUpdateViewHelper(view);
            mDownloadDialog = new DialogHelper(ctx, view);
            mDownloadDialog.setCanceledOnTouchOutside(false);
            mDownloadDialog.setCancelable(false);
        }
        mDownloadDialog.show();
    }

    // @Override
    // public void onDownloadTransfered(Download download, long total,
    // long transferedLength) {
    // // TODO Auto-generated method stub
    // if (mIsForceAppUpdate) {
    // if (null != mForceUpdateViewHelper) {
    // mForceUpdateViewHelper.updateProgress(download);
    // mForceUpdateViewHelper.setClickable(false);
    // }
    // } else {
    // if (null != mNotificationHelper) {
    // mNotificationHelper.updateNotification(download);
    // }
    // }
    // }
    //
    // @Override
    // public void onDownloadFinished(Download download) {
    // // TODO Auto-generated method stub
    // if (mIsForceAppUpdate) {
    // if (null != mForceUpdateViewHelper) {
    // mForceUpdateViewHelper.updateProgress(download);
    // }
    // this.dismissDownloadDialog();
    // this.startInstallApk(download.getLocalUrl());
    // } else {
    // if (null != mNotificationHelper) {
    // mNotificationHelper.updateNotification(download);
    // }
    // }
    // }
    //
    // @Override
    // public void onDownloadFailed(Download download, String error) {
    // // TODO Auto-generated method stub
    // if (mIsForceAppUpdate) {
    // if (null != mForceUpdateViewHelper) {
    // mForceUpdateViewHelper.updateProgress(download);
    // mForceUpdateViewHelper.setClickable(true);
    // }
    // } else {
    // if (null != mNotificationHelper) {
    // mNotificationHelper.updateNotification(download);
    // }
    // }
    // }

    // @Override
    // public void onDownloading(Download download) {
    // // TODO Auto-generated method stub
    //
    // }

    private class AppUpgradeListener extends ProgressCallBack<File> {
        private Download mDownload;

        // private long mTotalLength = 0;
        public AppUpgradeListener(Download download) {
            this.mDownload = download;
        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {
            // TODO Auto-generated method stub
            super.onLoading(total, current, isDownloading);
            if (null == mDownload) {
                return;
            }
            if (total != -1) {
                // mTotalLength = total;
                mDownload.setLength(total);
            }
            mDownload.setCurrLength(current);
            mDownload.setState(Download.STATE_DOWNLOADING);
            if (mIsForceAppUpdate) {
                if (null != mForceUpdateViewHelper) {
                    mForceUpdateViewHelper.updateProgress(mDownload);
                    mForceUpdateViewHelper.setClickable(false);
                }
            } else {
                if (null != mNotificationHelper) {
                    mNotificationHelper.updateNotification(mDownload);

                }
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            // TODO Auto-generated method stub
            super.onError(ex, isOnCallback);

            if (null == mDownload) {
                return;
            }
            mDownload.setState(Download.STATE_ERROR);
            if (mIsForceAppUpdate) {
                if (null != mForceUpdateViewHelper) {
                    mForceUpdateViewHelper.updateProgress(mDownload);
                    mForceUpdateViewHelper.setClickable(true);
                }
            } else {
                if (null != mNotificationHelper) {
                    mNotificationHelper.updateNotification(mDownload);
                }
            }
        }

        @Override
        public void onSuccess(File result) {
            // TODO Auto-generated method stub
            super.onSuccess(result);

            if (null == mDownload) {
                return;
            }
//			File result = res.result;
            mDownload.setLocalUrl(result.getPath());
            mDownload.setState(Download.STATE_COMPLETE);
            if (mIsForceAppUpdate) {
                if (null != mForceUpdateViewHelper) {
                    mForceUpdateViewHelper.updateProgress(mDownload);
                }
                AppUpdateKeeper.this.dismissDownloadDialog();
                AppUpdateKeeper.this.startInstallApk(mDownload.getLocalUrl());
            } else {
                if (null != mNotificationHelper) {
                    mNotificationHelper.updateNotification(mDownload);
                }
                AppUpdateKeeper.this.startInstallApk(mDownload.getLocalUrl());
            }
        }

        @Override
        public void onWaiting() {
            // TODO Auto-generated method stub
            super.onWaiting();
            Log.e("", "等待");
            Log.e("", "等待");
            Log.e("", "等待");
            Log.e("", "等待");
        }

    }

//	private class AppUpgradeListener extends RequestCallBack<File> {
//		private Download mDownload;
//
//		// private long mTotalLength = 0;
//		public AppUpgradeListener(Download download) {
//			this.mDownload = download;
//		}
//
//		@Override
//		public void onLoading(long total, long current, boolean isUploading) {
//			// TODO Auto-generated method stub
//			super.onLoading(total, current, isUploading);
//
//			if (null == mDownload) {
//				return;
//			}
//			if (total != -1) {
//				// mTotalLength = total;
//				mDownload.setLength(total);
//			}
//			mDownload.setCurrLength(current);
//			mDownload.setState(Download.STATE_DOWNLOADING);
//			if (mIsForceAppUpdate) {
//				if (null != mForceUpdateViewHelper) {
//					mForceUpdateViewHelper.updateProgress(mDownload);
//					mForceUpdateViewHelper.setClickable(false);
//				}
//			} else {
//				if (null != mNotificationHelper) {
//					mNotificationHelper.updateNotification(mDownload);
//
//				}
//			}
//		}
//
//		@Override
//		public void onFailure(HttpException arg0, String arg1) {
//			// TODO Auto-generated method stub
//			if (null == mDownload) {
//				return;
//			}
//			mDownload.setState(Download.STATE_ERROR);
//			if (mIsForceAppUpdate) {
//				if (null != mForceUpdateViewHelper) {
//					mForceUpdateViewHelper.updateProgress(mDownload);
//					mForceUpdateViewHelper.setClickable(true);
//				}
//			} else {
//				if (null != mNotificationHelper) {
//					mNotificationHelper.updateNotification(mDownload);
//				}
//			}
//		}
//
//		@Override
//		public void onSuccess(ResponseInfo<File> res) {
//			// TODO Auto-generated method stub
//			if (null == mDownload) {
//				return;
//			}
//			File result = res.result;
//			mDownload.setLocalUrl(result.getPath());
//			mDownload.setState(Download.STATE_COMPLETE);
//			if (mIsForceAppUpdate) {
//				if (null != mForceUpdateViewHelper) {
//					mForceUpdateViewHelper.updateProgress(mDownload);
//				}
//				AppUpdateKeeper.this.dismissDownloadDialog();
//				AppUpdateKeeper.this.startInstallApk(mDownload.getLocalUrl());
//			} else {
//				if (null != mNotificationHelper) {
//					mNotificationHelper.updateNotification(mDownload);
//				}
//				AppUpdateKeeper.this.startInstallApk(mDownload.getLocalUrl());
//			}
//		}
//	}

}
