package com.you.edu.live.teacher.model;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.contract.IOperationListener;
import com.you.edu.live.teacher.contract.IThirdActionListener;
import com.you.edu.live.teacher.model.bean.Room;
import com.you.edu.live.teacher.model.dao.RequestDataParser;
import com.you.edu.live.teacher.presenter.helper.ThirdShareHelper;
import com.you.edu.live.teacher.support.dao.ICache;
import com.you.edu.live.teacher.support.http.IHttpApi;
import com.you.edu.live.teacher.support.http.IHttpCallback;
import com.you.edu.live.teacher.support.http.RequestTag;
import com.you.edu.live.teacher.support.http.RespOut;


import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class CreateLiveModel extends BaseMvpModel implements IHttpCallback<RespOut> {


    private IOperationListener<Map<String, String>> mChidIOperationListener;
    private IOperationListener<Room> mRoomIdIOperationListener;
    private ThirdShareHelper mShareHelper;

    public CreateLiveModel(IHttpApi httpApi, ICache cache) {
        super(httpApi, cache);
        // TODO Auto-generated constructor stub
    }


    /**
     * 创建直播
     *
     * @param coid         课程coid
     * @param course_name  章节名称
     * @param chapter_type 章节类型：1 直播，2点播
     * @param weight       要添加的是第几小节    默认最后
     * @param pos          章节插入方式：true 章节上下节插入 默认false 添加在最后
     * @param chapter_time 直播小节的视频时长 （整数 秒）
     * @param play_start   直播小节的开始时间 2015-11-12 17:20
     * @param is_free      是否免费
     * @param resid        点播小节的资源id
     * @param thumbnail    点播小节的封面
     * @param chid         章节id,有更新，没有添加
     * @param description  直播公告
     */
    public void requestCreateClass(int coid, String course_name, int chapter_type, int weight, boolean pos, int chapter_time
            , String play_start, boolean is_free, String resid, String thumbnail, int chid, String description, IOperationListener<Map<String, String>> listener) {

        this.setChapterRequestListener(listener);

        Map<String, String> params = new HashMap<String, String>();
        params.put("coid", coid + "");
        params.put("course_name", course_name);
        params.put("chapter_type", chapter_type + "");
        if (weight != 0) {
            params.put("weight", weight + "");
        }

        if (pos != false) {
            params.put("pos", pos + "");
        }

        if (chapter_time != 0) {
            params.put("chapter_time", chapter_time + "");
        }

        if (!TextUtils.isEmpty(play_start)) {
            params.put("play_start", play_start);
        }

        if (is_free != false) {
            params.put("is_free", is_free + "");
        }

        if (!TextUtils.isEmpty(resid)) {
            params.put("resid", resid);
        }

        if (!TextUtils.isEmpty(description)) {
            params.put("description", description);
        }

        if (chid != 0) {
            params.put("chid", chid + "");
        }

        if (!TextUtils.isEmpty(thumbnail)) {
            params.put("thumbnail", thumbnail);
        }

        RequestTag tag = new RequestTag(this.getHttpTag(),
                GlobalConfig.Operator.OPERATOR_ADD_COURSE_HOUR);
        this.getHttpApi().request(Request.Method.POST, GlobalConfig.HttpUrl.ADD_COURSE_HOUR,
                params, this, tag);
    }

    /**
     * 微信朋友圈分享
     *
     * @param plat        分享平台
     * @param status      分享类型 直播还是预告
     * @param teacherName 老师真实姓名
     * @param date        时间
     * @param className   课程名称
     * @param shareUrl    分享url
     * @param thumb       分享图片
     * @param listener    回调
     */
    public void shareToWeixinFriends(Platform plat, String status, String teacherName,
                                     String date, String className, String shareUrl, String thumb, IThirdActionListener listener) {
        if (plat == null) {
            return;
        }

        Wechat.ShareParams sp = new Wechat.ShareParams();

        sp.setTitle(this.buildWechatShare(status, teacherName, date, className, shareUrl));

        sp.setText(this.buildWechatShare(status, teacherName, date, className, shareUrl));

        if (!TextUtils.isEmpty(shareUrl)) {
            sp.setUrl(shareUrl);
        }
        if (!TextUtils.isEmpty(thumb)) {
            sp.setImageUrl(thumb);
        }

        sp.setShareType(Platform.SHARE_WEBPAGE);
        // sp.setImagePath(mResultFilePath);//本地图片

        if (null == mShareHelper) {
            mShareHelper = new ThirdShareHelper(listener);
        }
        plat.setPlatformActionListener(mShareHelper);
        plat.share(sp);
    }

    /**
     * 微信分享
     *
     * @param plat        分享平台
     * @param status      分享类型 直播还是预告
     * @param teacherName 老师真实姓名
     * @param date        时间
     * @param className   课程名称
     * @param shareUrl    分享url
     * @param thumb       分享图片
     * @param listener    回调
     */
    public void shareToWeixinComments(Platform plat, String status, String teacherName,
                                      String date, String className, String shareUrl, String thumb, IThirdActionListener listener) {
        if (plat == null) {
            return;
        }

        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        if (!TextUtils.isEmpty(className)) {
            sp.setTitle(className);
        }

        sp.setText(this.buildWechatShare(status, teacherName, date, className, shareUrl));

//			sp.setText(desc);
        if (!TextUtils.isEmpty(shareUrl)) {
            sp.setUrl(shareUrl);
        }
        if (!TextUtils.isEmpty(thumb)) {
            sp.setImageUrl(thumb);
        }

        sp.setShareType(Platform.SHARE_WEBPAGE);
        // sp.setImagePath(mResultFilePath);//本地图片

        if (null == mShareHelper) {
            mShareHelper = new ThirdShareHelper(listener);
        }
        plat.setPlatformActionListener(mShareHelper);
        plat.share(sp);

    }

    /**
     * sina微博分享
     *
     * @param plat        分享平台
     * @param status      分享类型 直播还是预告
     * @param teacherName 老师真实姓名
     * @param date        时间
     * @param className   课程名称
     * @param shareUrl    分享url
     * @param thumb       分享图片
     * @param listener    回调
     */
    public void shareToSinaWeibo(Platform plat, String status, String teacherName,
                                 String date, String className, String shareUrl, String thumb, IThirdActionListener listener) {
        if (plat == null) {
            return;
        }

        plat.SSOSetting(false);
        if (!plat.isValid()) {
            plat.removeAccount();
            plat.authorize();
        }
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();

        sp.setText(this.buildSinaWeiboShare(status, teacherName, date, className, shareUrl));

        if (!TextUtils.isEmpty(thumb)) {
            sp.setImageUrl(thumb);// 远程图片--需申请statuses/upload_url_text权限
        }

        if (null == mShareHelper) {
            mShareHelper = new ThirdShareHelper(listener);
        }
        plat.setPlatformActionListener(mShareHelper); // 设置分享事件回调
        // 执行图文分享
        plat.share(sp);

    }

    private String buildWechatShare(String status, String teacherName, String date,
                                    String className, String shareUrl) {

        StringBuilder sb = new StringBuilder();
        sb.append("名师");
        if (!TextUtils.isEmpty(teacherName)) {
            sb.append(teacherName);
        }
        sb.append("|");
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("live")) {//直播
                sb.append("正在直播");
                sb.append("\"");
                if (!TextUtils.isEmpty(className)) {
                    sb.append(className);
                }
                sb.append("\"");
            } else if (status.equals("foreshow")) {//预告
                if (!TextUtils.isEmpty(date)) {
                    sb.append(date);
                }
                sb.append("直播");
                sb.append("\"");
                if (!TextUtils.isEmpty(className)) {
                    sb.append(className);
                }
                sb.append("\"");
            }
        }
        return sb.toString();
    }

    private String buildSinaWeiboShare(String status, String teacherName, String date, String className, String shareUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("#优酷学堂#");
        sb.append("名师");
        if (!TextUtils.isEmpty(teacherName)) {
            sb.append(teacherName);
        }
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("live")) {//直播
                sb.append("正在直播");
                sb.append("《");
                if (!TextUtils.isEmpty(className)) {
                    sb.append(className);
                }
                sb.append("》");
            } else if (status.equals("foreshow")) {//预告
                if (!TextUtils.isEmpty(date)) {
                    sb.append(date);
                }
                sb.append("直播");
                sb.append("《");
                if (!TextUtils.isEmpty(className)) {
                    sb.append(className);
                }
                sb.append("》");
            }
        }
        sb.append(",快戳！");
        sb.append(shareUrl);
        return sb.toString();
    }

//    /**
//     * 直播信息
//     *
//     * @param coid     课程id
//     * @param chid     课时id
//     * @param listener
//     */
//    public void requestGetLive(int coid, String chid, IOperationListener<Room> listener) {
//
//        this.setRoomIdRequestListener(listener);
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("chid", chid + "");
//        params.put("coid", coid + "");
//
//        RequestTag tag = new RequestTag(this.getHttpTag(),
//                GlobalConfig.Operator.OPERATOR_CREATE_LIVE);
//        this.getHttpApi().request(Request.Method.GET, GlobalConfig.HttpUrl.CREATE_LIVE,
//                params, this, tag);
//    }

    @Override
    public void onResponseError(RespOut out) {
        if (null == out || null == out.param) {
            return;
        }
        int operator = (int) out.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_ADD_COURSE_HOUR:// 创建课时失败
                if (this.getChapterRequestListener() != null) {
                    this.getChapterRequestListener().onOperationFails(
                            GlobalConfig.Operator.OPERATOR_USER_LOGIN,
                            GlobalConfig.HTTP_ERROR_TIPS);
                }
                break;
//            case GlobalConfig.Operator.OPERATOR_CREATE_LIVE://获取直播信息失败
//                if (this.getChapterRequestListener() != null) {
//                    this.getChapterRequestListener().onOperationFails(
//                            GlobalConfig.Operator.OPERATOR_CREATE_LIVE,
//                            GlobalConfig.HTTP_ERROR_TIPS);
//                }
//                break;
        }
    }

    @Override
    public void onResponse(RespOut response) {
        if (null == response || null == response.param) {
            return;
        }
        RequestDataParser parser = new RequestDataParser(response.resp);
        int operator = (int) response.param.operator;
        switch (operator) {
            case GlobalConfig.Operator.OPERATOR_ADD_COURSE_HOUR:// 创建课时成功
                if (this.getChapterRequestListener() == null) {
                    return;
                }
                if (parser.isSuccess()) {

                    String chid = parser.getValue("chid");
                    String coid = parser.getValue("coid");
                    String room_id = parser.getValue("room_id");
                    String socket_host = parser.getValue("socket_host");
                    String cover = parser.getValue("cover");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("chid", chid);
                    map.put("coid", coid);
                    map.put("room_id", room_id);
                    map.put("socket_host", socket_host);
                    map.put("cover", cover);
//                    String jsonArr = parser
//                            .getValue(GlobalConfig.HttpJson.KEY_LIST);
//                    List<Course> courseList = parser.getArray(jsonArr,
//                            Course.class);
                    this.getChapterRequestListener().onOperationSuccess(map);
                } else {
                    int errorCode = parser.getRespCode();
                    String errorMsg = parser.getErrorMsg();
                    this.getChapterRequestListener()
                            .onOperationFails(errorCode, errorMsg);
                }
                break;
//            case GlobalConfig.Operator.OPERATOR_CREATE_LIVE://直播信息
//                if (this.getRoomIdRequestListener() == null) {
//                    return;
//                }
//                if (parser.isSuccess()) {
//                    String json = parser.getValue("room");
//                    Room room = parser.getOne(json, Room.class);
//                    this.getRoomIdRequestListener().onOperationSuccess(room);
//                } else {
//                    int errorCode = parser.getRespCode();
//                    String errorMsg = parser.getErrorMsg();
//                    this.getRoomIdRequestListener()
//                            .onOperationFails(errorCode, errorMsg);
//                }
//                break;
        }
    }

    public void setChapterRequestListener(IOperationListener<Map<String, String>> listener) {
        mChidIOperationListener = listener;
    }

    public IOperationListener<Map<String, String>> getChapterRequestListener() {
        return mChidIOperationListener;
    }

    public void setRoomIdRequestListener(IOperationListener<Room> listener) {
        mRoomIdIOperationListener = listener;
    }

    public IOperationListener<Room> getRoomIdRequestListener() {
        return mRoomIdIOperationListener;
    }

}
