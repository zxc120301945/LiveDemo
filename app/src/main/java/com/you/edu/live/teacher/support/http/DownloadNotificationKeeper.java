package com.you.edu.live.teacher.support.http;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.support.http.model.Download;
import com.you.edu.live.teacher.utils.FileUtil;


public class DownloadNotificationKeeper {

	private static final int NOTIFY_ID = 1;

	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Context mCtx;
	private Class<?> mCls;
	private int mNotifyId = NOTIFY_ID;

	public DownloadNotificationKeeper(Context ctx) {
		this(ctx, null);
	}

	public DownloadNotificationKeeper(Context ctx, Class<?> cls) {
		this(ctx, cls, NOTIFY_ID);
	}

	public DownloadNotificationKeeper(Context ctx, Class<?> cls, int notifyId) {
		this.mCtx = ctx.getApplicationContext();
		mNotificationManager = (NotificationManager) mCtx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.mCls = cls;
		this.mNotifyId = notifyId;
	}

	public void updateNotification(Download download) {
		if (null == download) {
			return;
		}
		RemoteViews contentView = null;
		String name = download.getResName();
		if (null == mNotification) {
			int icon = R.drawable.app_logo;
			CharSequence tickerText = "开始下载-" + name;
			mNotification = new Notification();
			mNotification.tickerText = tickerText;
			mNotification.icon = icon;
			// 放置在"正在运行"栏目中
			contentView = new RemoteViews(mCtx.getPackageName(),
					R.layout.notification_download);
			// 指定个性化视图
			mNotification.contentView = contentView;
		} else {
			contentView = mNotification.contentView;
		}
		int state = download.getState();

		long currSize = download.getCurrLength();
		long totalSize = download.getLength();
		contentView.setTextViewText(R.id.download_notify_tv_filename, name);
		if (Download.STATE_COMPLETE == state) {
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			contentView.setViewVisibility(R.id.download_notify_tv_progress,
					View.GONE);
			contentView.setViewVisibility(R.id.download_notify_probar_progress,
					View.GONE);
			contentView.setTextViewText(R.id.download_notify_tv_size, "下载完成");
		} else if (Download.STATE_ERROR == state) {
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			contentView.setViewVisibility(R.id.download_notify_tv_progress,
					View.GONE);
			contentView.setViewVisibility(R.id.download_notify_probar_progress,
					View.GONE);
			contentView.setTextViewText(R.id.download_notify_tv_size, "下载失败");
		} else {
			mNotification.flags = Notification.FLAG_ONGOING_EVENT;
			int percent = (int) ((float) currSize / totalSize * 100);
			if (percent >= 0 && percent <= 100) {
				contentView.setTextViewText(R.id.download_notify_tv_progress,
						percent + "%");
				contentView.setProgressBar(
						R.id.download_notify_probar_progress, 100, percent,
						false);
			}
			contentView.setTextViewText(
					R.id.download_notify_tv_size,
					FileUtil.formetFileSize(currSize) + "/"
							+ FileUtil.formetFileSize(totalSize));
		}

		if (null != mCls && null == mNotification.contentIntent) {
			// intent为null,表示点击通知时不跳转
			Intent intent = new Intent(mCtx, mCls);
			PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0,
					intent, 0);
			mNotification.contentIntent = contentIntent;
		}
		mNotificationManager.notify(mNotifyId, mNotification);
	}

	public void cancelNotification() {
		if (null != mNotificationManager) {
			mNotificationManager.cancel(mNotifyId);
		}
	}

}
