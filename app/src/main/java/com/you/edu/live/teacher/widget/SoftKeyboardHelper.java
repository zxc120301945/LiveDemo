package com.you.edu.live.teacher.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * 软键盘状态监听助手
 * 
 * @author XingRongJing
 * 
 */
public class SoftKeyboardHelper implements
		ViewTreeObserver.OnGlobalLayoutListener {

	// 软件盘的高度约为屏幕高度的1/3~1/2,此处取240px为阀值
	private static final int KEYBOARD_HEIGHT_THRESHOLD = 240;
	// 测量键盘标识
	private boolean mIsKeyboardShown = false;;
	private OnKeyboardShownListener mOnKeyboardShownListener;
	// private RelativeLayout mRlRoot;
	private ViewGroup mRlRoot;
	private Context mCtx;

	public SoftKeyboardHelper(ViewGroup root, OnKeyboardShownListener listener) {
		if (null == root) {
			throw new IllegalArgumentException("Root view cannot be null");
		}
		mCtx = root.getContext();
		this.mRlRoot = root;
		this.mOnKeyboardShownListener = listener;
	}

	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		Rect r = new Rect();
		mRlRoot.getWindowVisibleDisplayFrame(r);
		// 获取屏幕高度
		int screenHeight = mRlRoot.getRootView().getHeight();
		// 计算内容视图与屏幕高度差
		int heightDifference = screenHeight - (r.bottom - r.top);
		// 减去顶部状态栏高度
		int resourceId = mCtx.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			heightDifference -= mCtx.getResources().getDimensionPixelSize(
					resourceId);
		}

		if (heightDifference < KEYBOARD_HEIGHT_THRESHOLD) {// 低于阀值视为收起
			if (!mIsKeyboardShown) {
				return;
			}
			mIsKeyboardShown = false;

			if (mOnKeyboardShownListener != null) {
				mOnKeyboardShownListener.onKeyboardShown(false);
			}
		} else {

			if (mIsKeyboardShown) {
				return;
			}
			mIsKeyboardShown = true;

			if (mOnKeyboardShownListener != null) {
				mOnKeyboardShownListener.onKeyboardShown(true);
			}
		}

	}

	public boolean isSoftInputOpen() {
		return ((InputMethodManager) mCtx
				.getSystemService(Activity.INPUT_METHOD_SERVICE)).isActive();
	}

	public void showSoftInput() {
		((InputMethodManager) mCtx
				.getSystemService(Activity.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(((Activity) mCtx).getCurrentFocus()
						.getWindowToken(), InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftInput() {
		try {
			((InputMethodManager) mCtx
					.getSystemService(Activity.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(((Activity) mCtx)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public interface OnKeyboardShownListener {
		/**
		 * 软键盘是否弹出
		 * 
		 * @param shown
		 */
		void onKeyboardShown(boolean shown);
	}

}
