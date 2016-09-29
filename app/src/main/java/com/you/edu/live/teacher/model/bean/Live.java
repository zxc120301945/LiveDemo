package com.you.edu.live.teacher.model.bean;

import com.you.edu.live.teacher.widget.adapter.RecyclerViewAdapter;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class Live implements RecyclerViewAdapter.Item {

    private static final int TYPE_ITEM = 2;


    private int chid;
    private int uid;
    private int coid;
    private String room_id;
    private String chapter_name;
    private String thumb;
    private String cover;
    private long play_start;
    private String chapter_time;
    private int weight;
    private String course_name;
    private Format format_time;
    private String play_status_desc;//播放状态
    private int play_status;//直播状态 55 即将直播 59 正在直播

    public int getPlay_status() {
        return play_status;
    }

    public void setPlay_status(int play_status) {
        this.play_status = play_status;
    }

    public String getPlay_status_desc() {
        return play_status_desc;
    }

    public void setPlay_status_desc(String play_status_desc) {
        this.play_status_desc = play_status_desc;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_time() {
        return chapter_time;
    }

    public void setChapter_time(String chapter_time) {
        this.chapter_time = chapter_time;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Format getFormat_time() {
        return format_time;
    }

    public void setFormat_time(Format format_time) {
        this.format_time = format_time;
    }

    public long getPlay_start() {
        return play_start;
    }

    public void setPlay_start(long play_start) {
        this.play_start = play_start;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public static int getTypeItem() {
        return TYPE_ITEM;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int getItemType() {
        return TYPE_ITEM;
    }

    public class Format {
        private String week;
        private String time;
        private String month;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }
    }
}
