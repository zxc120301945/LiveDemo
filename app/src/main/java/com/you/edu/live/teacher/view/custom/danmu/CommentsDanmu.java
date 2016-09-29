package com.you.edu.live.teacher.view.custom.danmu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;



/**
 * 直播交流弹幕view
 * 
 * @author XingRongJing
 * 
 */
public class CommentsDanmu extends RelativeLayout implements IDanmu {

	private DanmuProxy mProxy = new DanmuProxy();

	public CommentsDanmu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CommentsDanmu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CommentsDanmu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DanmuProxy.OnDisappearListener getDisappearListener() {
		return mProxy.getDisappearListener();
	}

	@Override
	public void setDisappearListener(DanmuProxy.OnDisappearListener mDisappearListener) {
		mProxy.setDisappearListener(mDisappearListener);

	}

	@Override
	public long getDuration() {
		return mProxy.getDuration();
	}

	@Override
	public void setDuration(long mDuration) {
		mProxy.setDuration(mDuration);
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return IDanmu.TYPE_COMMENT;
	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendDanmu() {
		mProxy.sendDanmu(this);
	}

	@Override
	public int getPoriority() {
		// TODO Auto-generated method stub
		return IDanmu.PORIORITY_FIRST;
	}

	@Override
	public void setPoriority(int poriority) {
		// TODO Auto-generated method stub
		
	}

}
