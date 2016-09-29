package com.you.edu.live.teacher.model.bean;

import java.io.Serializable;

/**
 * 机构信息
 */
public class Organ implements Serializable {
    private int orgid;
    private String organ_name;

    public int getOrgid() {
        return orgid;
    }

    public void setOrgid(int orgid) {
        this.orgid = orgid;
    }

    public String getOrgan_name() {
        return organ_name;
    }

    public void setOrgan_name(String organ_name) {
        this.organ_name = organ_name;
    }
}
