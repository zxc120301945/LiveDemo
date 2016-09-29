package com.you.edu.live.teacher.model.bean;

import java.io.Serializable;

import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

/**
 * 作者：XingRongJing on 2016/6/28.
 */
public class User implements Serializable,RecyclerViewAdapter.Item{
    private int uid;
    // 头像
   private String photo;
    // 用户名
    private String user_name;
    // 昵称
    private String nick_name;
    //用户类型 2为老师
    private int user_type;

    private String cookie;
    private String yktk;
    private String ytid;
    private String yt_uid;

    public String getYt_uid() {
        return yt_uid;
    }

    public void setYt_uid(String yt_uid) {
        this.yt_uid = yt_uid;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getYktk() {
        return yktk;
    }

    public void setYktk(String yktk) {
        this.yktk = yktk;
    }

    public String getYtid() {
        return ytid;
    }

    public void setYtid(String ytid) {
        this.ytid = ytid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
    @Override
    public int getItemType() {
        return 0;
    }
}
