package com.you.edu.live.teacher.support.http.model;

import java.io.Serializable;

/**
 * 资料下载对象
 *
 * @author XingRongJing
 */
public class Download implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int resId;
    private int memberId;
    /**
     * 资源名称
     **/
    private String resName;
    /**
     * 资源url
     **/
    private String url;
    /**
     * 本地文件路径
     **/
    private String localUrl;
    /**
     * 资源缩略图
     **/
    private String resThumb;
    /**
     * 当前已下载长度
     **/
    private long currLength;
    /**
     * 总长度
     **/
    private long length;
    /**
     * 当前状态-默认等待
     **/
    private int state = STATE_WAITING;
    /**
     * 网络请求处理
     **/
    // @Column(ignore = true)
    // private HttpHandler<File> httpHanlder;
    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResThumb() {
        return resThumb;
    }

    public void setResThumb(String resThumb) {
        this.resThumb = resThumb;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public long getCurrLength() {
        return currLength;
    }

    public void setCurrLength(long currLength) {
        this.currLength = currLength;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    // public HttpHandler<File> getHttpHanlder() {
    // return httpHanlder;
    // }
    //
    // public void setHttpHanlder(HttpHandler<File> httpHanlder) {
    // this.httpHanlder = httpHanlder;
    // }

    public void retryCountPlus() {
        retryCount++;
    }

    public boolean isOverMaxRetry() {
        return retryCount >= MAX_RETRY_COUNT;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        Download download = (Download) o;
        return /*
                 * this.url.equals(download.getUrl()) &&
				 */resId == download.getResId();
        // && memberId == download.getMemberId();
    }

    // @Override
    // public String toString() {
    // return "Download [id=" + id + ", resName=" + resName + ", resId="
    // + resId + ", memberId=" + memberId + ", url=" + url
    // + ", localUrl=" + localUrl + ", currLength=" + currLength
    // + ", length=" + length + ", state=" + state + ", httpHanlder="
    // + httpHanlder + ", retryCount=" + retryCount + "]";
    // }

    @Override
    public String toString() {
        return "Download [id=" + id + ", resName=" + resName + ", resId="
                + resId + ", memberId=" + memberId + ", url=" + url
                + ", localUrl=" + localUrl + ", currLength=" + currLength
                + ", length=" + length + ", state=" + state + ", retryCount="
                + retryCount + "]";
    }

    public static final int STATE_WAITING = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_ERROR = 4;
    public static final int STATE_COMPLETE = 5;

}
