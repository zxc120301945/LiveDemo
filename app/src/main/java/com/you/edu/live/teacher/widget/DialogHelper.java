package com.you.edu.live.teacher.widget;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.utils.DeviceUtils;


/**
 * Dialog助手
 * 
 * @author XingRongJing
 * 
 */
public class DialogHelper {

	private static final int INVALID = -1;
	private Dialog mDialog;
	private TextView mTvTips, mTvSure,mTvCancel;
	private static final boolean mNotScreenWidth = false;
	private int mOperator = INVALID;

	public DialogHelper(Context ctx) {
		View dialog = LayoutInflater.from(ctx).inflate(R.layout.dialog_common,
				null);
		this.prepareRes(ctx, dialog, INVALID);
		this.setWindowParams(ctx, mNotScreenWidth);
	}

	public DialogHelper(Context ctx, View view) {
		this(ctx, view, INVALID);
	}

	public DialogHelper(Context ctx, View view, int style) {
		this(ctx, view, style, mNotScreenWidth);
	}

	/**
	 * 
	 * @param ctx
	 * @param view
	 * @param style
	 * @param screenWidth
	 *            传true代表dialog宽度是整个屏幕的宽度
	 */
	public DialogHelper(Context ctx, View view, int style, boolean screenWidth) {
		this.prepareRes(ctx, view, style);
		this.setWindowParams(ctx, screenWidth);

	}

	public int getOperator() {
		return mOperator;
	}

	public void setOperator(int mOperator) {
		this.mOperator = mOperator;
	}

	private void prepareRes(Context ctx, View view, int style) {
		mTvTips = (TextView) view.findViewById(R.id.dialog_common_tv_msg);
		mTvSure = (TextView) view.findViewById(R.id.dialog_common_btn_sure);
		mTvCancel = (TextView)view.findViewById(R.id.dialog_common_btn_cancel);
		if (INVALID == style) {
			mDialog = new Dialog(ctx, R.style.dialog_style);
		} else {
			mDialog = new Dialog(ctx, style);
		}
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
	}

	public void setDissmissListener(OnDismissListener listener) {
		if (null != mDialog) {
			mDialog.setOnDismissListener(listener);
		}
	}

	/**
	 * 设置Dialog宽高
	 * 
	 * @param context
	 * @param screenWidth
	 *            传true代表dialog宽度是整个屏幕的宽度
	 */
	private void setWindowParams(Context context, boolean screenWidth) {
		int mScreenHeight = 0, mScreenWidth = 0;
		// mScreenHeight = CommonUtil.getScreenHeight(context);
		mScreenWidth = DeviceUtils.getScreenWidth(context);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		// p.height = (int) (mScreenHeight * 0.6); // 高度设置为屏幕的0.6
		if (screenWidth) {
			p.width = (int) (mScreenWidth * 1.0); // 宽度设置整个屏幕的宽度
		} else {
			p.width = (int) (mScreenWidth * 0.8); // 宽度设置为屏幕的0.8
		}
		p.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(p);
	}

	public void resetWindowWidth(int width){
		if(null!=mDialog&&width>0){
			Window dialogWindow = mDialog.getWindow();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			p.width = width;
			p.height = LayoutParams.WRAP_CONTENT;
			dialogWindow.setAttributes(p);
		}
	}

	public void setMessage(String tips) {
		if (null != mTvTips) {
			mTvTips.setText(tips);
		}
	}

	public void setBtnSure(String sureBtnText) {
		if (null != mTvSure) {
			mTvSure.setText(sureBtnText);
		}
	}
	
	public void setBtnCancel(String text) {
		if (null != mTvCancel) {
			mTvCancel.setText(text);
		}
	}

	public void setBtnSureBg(int resId){
		if(null!=mTvSure){
			mTvSure.setBackgroundResource(resId);
		}
	}

	public void setBtnCancelBg(int resId){
		if(null!=mTvCancel){
			mTvCancel.setBackgroundResource(resId);
		}
	}
	
	public void setBtnSureClickListener(OnClickListener listener) {
		if (null != mTvSure) {
			mTvSure.setOnClickListener(listener);
		}
	}
	
	public void setBtnCancelClickListener(OnClickListener listener) {
		if (null != mTvCancel) {
			mTvCancel.setOnClickListener(listener);
		}
	}


	public void setCanceledOnTouchOutside(boolean isCancel) {
		if (null != mDialog) {
			mDialog.setCanceledOnTouchOutside(isCancel);
		}
	}

	public void setCancelable(boolean isCancel) {
		if (null != mDialog) {
			mDialog.setCancelable(isCancel);
		}
	}
	
	public boolean isShowing(){
		if(null != mDialog){
			return mDialog.isShowing();
		}
		return false;
	}

	public void show() {
		if (null != mDialog && !mDialog.isShowing()) {
			mDialog.show();
		}
		// this.setWindowParams();
	}

	public void dismiss() {
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
