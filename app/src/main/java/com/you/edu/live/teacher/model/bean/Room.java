package com.you.edu.live.teacher.model.bean;

/**
 * 直播房间信息
 */
public class Room {
    private String room_id;

    private String socket_host;

    public String getSocket_host() {
        return socket_host;
    }

    public void setSocket_host(String socket_host) {
        this.socket_host = socket_host;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}
