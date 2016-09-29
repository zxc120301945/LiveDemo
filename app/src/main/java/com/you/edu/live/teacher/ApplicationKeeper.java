package com.you.edu.live.teacher;

import android.app.Application;

import org.xutils.x;

/**
 * Application管家 暂时用于xutils3初始化
 * 
 * @author WanZhiYuan
 * 
 */
public class ApplicationKeeper extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化
		x.Ext.init(this);
		// 是否输出debug日志, 开启debug会影响性能.
		x.Ext.setDebug(false);

	}
}
