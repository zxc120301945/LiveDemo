package com.you.edu.live.teacher.model.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class TeacherInfo implements Serializable {
    private Teacher user;
    private Organ organ;
    private int insale_cnt;

    public int getInsale_cnt() {
        return insale_cnt;
    }

    public void setInsale_cnt(int insale_cnt) {
        this.insale_cnt = insale_cnt;
    }

    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    public Teacher getUser() {
        return user;
    }

    public void setUser(Teacher user) {
        this.user = user;
    }
}
