package com.you.edu.live.teacher.view.custom.danmu;


public interface IDanmu  {

	/** 弹幕类型-评论(图文) **/
	public static final int TYPE_COMMENT =1;
	/** 弹幕类型-消息(文本) **/
	public static final int TYPE_MESSAGE = 2;
	/** 优先级一级，即最优先 **/
	public static final int PORIORITY_FIRST = 1;
	/** 优先级二级，即次级优先 **/
	public static final int PORIORITY_SECOND = 2;
	
	public DanmuProxy.OnDisappearListener getDisappearListener();

	public void setDisappearListener(DanmuProxy.OnDisappearListener mDisappearListener);

	public long getDuration();

	public void setDuration(long mDuration);
	
	public int getType();

	public void setType(int type);
	
	public int getPoriority();

	public void setPoriority(int poriority);


	public void sendDanmu();
}
