package com.you.edu.live.teacher.model.bean;

/**
 * app升级实体
 * 
 * @author XingRongJing
 * 
 */
public class AppUpdate {

	/** 唯一id **/
	private int upid;
	/** 应用类型 1：哟直播 2：为Ta写字 **/
	private int atype;
	/** App类型 1：Android 3：ios **/
	private int uptype;
	/** 当前版本 **/
	private String cur_version;
	/** 最小版本 **/
	private String min_version;
	/** 下载url **/
	private String down_url;
	/** 描述 **/
	private String up_desc;
	/** 提醒天数 **/
	private int alert_day;
	/** 提醒次数 **/
	private int alert_cnt;
	private long update_time;
	private long create_time;

	public int getUpid() {
		return upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	public int getAtype() {
		return atype;
	}

	public void setAtype(int atype) {
		this.atype = atype;
	}

	public int getUptype() {
		return uptype;
	}

	public void setUptype(int uptype) {
		this.uptype = uptype;
	}

	public String getCur_version() {
		return cur_version;
	}

	public void setCur_version(String cur_version) {
		this.cur_version = cur_version;
	}

	public String getMin_version() {
		return min_version;
	}

	public void setMin_version(String min_version) {
		this.min_version = min_version;
	}

	public String getUp_desc() {
		return up_desc;
	}

	public void setUp_desc(String up_desc) {
		this.up_desc = up_desc;
	}

	public String getDown_url() {
		return down_url;
	}

	public void setDown_url(String down_url) {
		this.down_url = down_url;
	}

	public int getAlert_day() {
		return alert_day;
	}

	public void setAlert_day(int alert_day) {
		this.alert_day = alert_day;
	}

	public int getAlert_cnt() {
		return alert_cnt;
	}

	public void setAlert_cnt(int alert_cnt) {
		this.alert_cnt = alert_cnt;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

}
