package com.you.edu.live.teacher.model.bean;

import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

/**
 * @Title: 课程
 * @Description:
 * @Author WanZhiYuan
 * @Date 16/6/30
 * @Time 上午11:57
 * @Version
 */
public class Course implements RecyclerViewAdapter.Item {

    private final int ITEMTYPE = 2;


    private int uid;
    private long sell_start;
    private long sell_end;
    private String course_name;
    private int course_type;
    private int course_hour;
    private int play_status;
    private int play_start;
    private String course_thumb;
    private int course_status;
    private float price;

    private int coid;

    private String nick_name;

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getCoid() {
        return coid;
    }

    public void setCoid(int coid) {
        this.coid = coid;
    }

    public int getCourse_hour() {
        return course_hour;
    }

    public void setCourse_hour(int course_hour) {
        this.course_hour = course_hour;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getCourse_status() {
        return course_status;
    }

    public void setCourse_status(int course_status) {
        this.course_status = course_status;
    }

    public String getCourse_thumb() {
        return course_thumb;
    }

    public void setCourse_thumb(String course_thumb) {
        this.course_thumb = course_thumb;
    }

    public int getCourse_type() {
        return course_type;
    }

    public void setCourse_type(int course_type) {
        this.course_type = course_type;
    }

    public int getPlay_start() {
        return play_start;
    }

    public void setPlay_start(int play_start) {
        this.play_start = play_start;
    }

    public int getPlay_status() {
        return play_status;
    }

    public void setPlay_status(int play_status) {
        this.play_status = play_status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getSell_end() {
        return sell_end;
    }

    public void setSell_end(long sell_end) {
        this.sell_end = sell_end;
    }

    public long getSell_start() {
        return sell_start;
    }

    public void setSell_start(long sell_start) {
        this.sell_start = sell_start;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public int getItemType() {
        return ITEMTYPE;
    }
}
