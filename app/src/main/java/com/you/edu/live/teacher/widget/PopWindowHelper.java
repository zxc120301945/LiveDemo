package com.you.edu.live.teacher.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;


import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.utils.DeviceUtils;

import java.lang.reflect.Method;

/**
 * PopupWindow助手
 * 
 * @author XingRongJing
 * 
 */
public class PopWindowHelper implements OnClickListener {

	/** 默认 **/
	private static final float DEFAULT_PROPORTION = 0.75f;
	private PopupWindow mPopWindow;
	private onClickRootListener mListener;

	public PopWindowHelper(Context ctx,View root) {
		this.prepareDatas(ctx,root, 0, true);
	}

	public PopWindowHelper(Context ctx,View root, boolean isFullScreen) {
		prepareDatas(ctx,root, 0, isFullScreen);
	}

	public PopWindowHelper(Context ctx,View root, int style, boolean isFullScreen) {
		this.prepareDatas(ctx,root, style, isFullScreen);
	}

	private void prepareDatas(Context ctx,View root, int style, boolean isFullScreen) {
		root.setOnClickListener(this);
		if (isFullScreen) {
			mPopWindow = new PopupWindow(root, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			ColorDrawable dw = new ColorDrawable(ctx.getResources().getColor(R.color.translucent));
			mPopWindow.setBackgroundDrawable(dw);
		} else {
			mPopWindow = new PopupWindow(root, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			ColorDrawable dw = new ColorDrawable();
			mPopWindow.setBackgroundDrawable(dw);
		}
		if (0 == style) {
			mPopWindow.setAnimationStyle(R.style.floating_anim_style);
		} else {
			mPopWindow.setAnimationStyle(style);
		}
		// 设置PopupWindow外部区域是否可触摸
		mPopWindow.setOutsideTouchable(true);
		// mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setFocusable(true);
		mPopWindow.setTouchable(true);

	}

	public void setPopWindowBackground(int color) {
		ColorDrawable dw = null;
		if (color <= 0) {
			dw = new ColorDrawable();
		} else {
			dw = new ColorDrawable(color);
		}
		if (null != mPopWindow) {
			mPopWindow.setBackgroundDrawable(dw);
		}
	}

	public void setOutsideTouchable(boolean touchable) {
		if (mPopWindow != null) {
			mPopWindow.setOutsideTouchable(touchable);
		}
	}

	/**
	 * Set whether this window is touch modal or if outside touches will be sent
	 * to other windows behind it.
	 */
	public void setPopupWindowTouchModal(boolean touchModal) {
		if (null == mPopWindow) {
			return;
		}
		Method method;
		try {

			method = PopupWindow.class.getDeclaredMethod("setTouchModal",
					boolean.class);
			method.setAccessible(true);
			method.invoke(mPopWindow, touchModal);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置内容区域的宽高(默认宽度的75%)
	 * 
	 * @param contentView
	 *            屏幕宽度占比
	 */
	public void setContentViewParams(View contentView) {
		this.setContentViewParams(contentView, DEFAULT_PROPORTION);
	}

	/**
	 * 设置内容区域的宽高
	 * 
	 * @param contentView
	 * @param proportion
	 *            屏幕宽度占比
	 */
	public void setContentViewParams(View contentView, float proportion) {
		if (null == contentView) {
			return;
		}
		int width = (int) (DeviceUtils.getScreenWidth(contentView.getContext()) * proportion);
		LayoutParams param = contentView.getLayoutParams();
		if (null != param) {
			param.height = LayoutParams.WRAP_CONTENT;
			param.width = width;
			contentView.setLayoutParams(param);
		}
	}

	/**
	 * 设置内容区域的宽高
	 * 
	 * @param contentView
	 *            屏幕宽
	 */
	public void setContentViewParams(View contentView, int width) {
		if (null == contentView) {
			return;
		}
		LayoutParams param = contentView.getLayoutParams();
		if (null != param) {
			param.height = LayoutParams.WRAP_CONTENT;
			param.width = width;
			contentView.setLayoutParams(param);
		}
	}

	/**
	 * 设置内容区域的宽高
	 * 
	 * @param contentView
	 *            内容部分
	 */
	public void setContentViewParams(View contentView, int width, int height) {
		if (null == contentView) {
			return;
		}
		LayoutParams param = contentView.getLayoutParams();
		if (null != param) {
			param.height = height;
			param.width = width;
			contentView.setLayoutParams(param);
		}
	}

	/**
	 * 设置悬浮窗样式
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 */
	public void setPopupwindowStyle(int color, int width, int height) {
		if (mPopWindow == null) {
			return;
		}
		ColorDrawable dw = new ColorDrawable(color);
		mPopWindow.setBackgroundDrawable(dw);
		mPopWindow.setWidth(width);
		mPopWindow.setHeight(height);
	}

	public boolean isShowing() {
		return null == mPopWindow ? false : mPopWindow.isShowing();
	}

	public void showAtLocation(View anchor, int xoff, int yoff, int gravity) {
		if (null != mPopWindow) {
			mPopWindow.showAtLocation(anchor, gravity, xoff, yoff);
		}
	}

	public void showAtLocation(View anchor) {
		this.showAtLocation(anchor, Gravity.CENTER, 0, 0);

	}

	public void showAtLocationRight(View anchor) {
		this.showAtLocation(anchor, Gravity.RIGHT, 0, 0);
	}

	public void showAsDropDown(View anchor, int xoff, int yoff) {
		mPopWindow.showAsDropDown(anchor, xoff, yoff);
	}

	public void setOnDismissListener(OnDismissListener listener) {
		if (mPopWindow != null) {
			mPopWindow.setOnDismissListener(listener);
		}
	}

	public void dismiss() {
		if (null != mPopWindow) {
			mPopWindow.dismiss();
		}
	}

	public int getWidth(){
		if (null != mPopWindow) {
			return mPopWindow.getWidth();
		}
		return 0;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.dismiss();
		if (this.getOnClickRootListener() != null) {
			this.getOnClickRootListener().onClick();
		}
	}


	public interface onClickRootListener {
		public void onClick();
	}

	public void setOnClickRootListener(onClickRootListener listener) {
		this.mListener = listener;
	}

	public onClickRootListener getOnClickRootListener() {
		return mListener;
	}
}
