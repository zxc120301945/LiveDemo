package com.you.edu.live.teacher.model.bean;

import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class Chapter implements RecyclerViewAdapter.Item {

    private final int ITEMTYPE = 2;

    private int chid;
    private int coid;
    //章节类型
    private int chapter_type;
    //章节名称
    private String chapter_name;
    private long play_start;
    //章节时间
    private long chapter_time;
    private int trial_type;
    private int play_status;
    private float weight;
    private int order_num;
    private String course_name;
    private int play_end;
    private long create_time;
    private long update_time;
    private String url;
    private String play_start_msg;
    private String room_id;
    private String chapter_time_msg;

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public long getChapter_time() {
        return chapter_time;
    }

    public void setChapter_time(long chapter_time) {
        this.chapter_time = chapter_time;
    }

    public String getChapter_time_msg() {
        return chapter_time_msg;
    }

    public void setChapter_time_msg(String chapter_time_msg) {
        this.chapter_time_msg = chapter_time_msg;
    }

    public int getChapter_type() {
        return chapter_type;
    }

    public void setChapter_type(int chapter_type) {
        this.chapter_type = chapter_type;
    }

    public int getChid() {
        return chid;
    }

    public void setChid(int chid) {
        this.chid = chid;
    }

    public int getCoid() {
        return coid;
    }

    public void setCoid(int coid) {
        this.coid = coid;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public int getPlay_end() {
        return play_end;
    }

    public void setPlay_end(int play_end) {
        this.play_end = play_end;
    }

    public long getPlay_start() {
        return play_start;
    }

    public void setPlay_start(long play_start) {
        this.play_start = play_start;
    }

    public String getPlay_start_msg() {
        return play_start_msg;
    }

    public void setPlay_start_msg(String play_start_msg) {
        this.play_start_msg = play_start_msg;
    }

    public int getPlay_status() {
        return play_status;
    }

    public void setPlay_status(int play_status) {
        this.play_status = play_status;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getTrial_type() {
        return trial_type;
    }

    public void setTrial_type(int trial_type) {
        this.trial_type = trial_type;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int getItemType() {
        return ITEMTYPE;
    }
}
