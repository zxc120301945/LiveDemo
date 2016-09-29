package com.you.edu.live.teacher.model;

/**
 * 全局配置常量类
 * 作者：XingRongJing on 2016/6/28.
 */
public interface GlobalConfig {

    public static final String DIR = "YouLiveTeacher";
    /**
     * 来疯分配的AppID
     **/
    public static final String APPID = "201";
    public static final int MAX_RETRY_COUNT = 3;
    public static final int INVALID = -1;
    public static final int HTTP_RESP_ERROR_CODE = INVALID;
    public static final String HTTP_ERROR_TIPS = "网络异常";

    public static interface HttpUrl {
        /**
         * 开发环境
         **/
//        public static final String BASE_URL = "http://10.10.221.39:8181";
//        public static final String HTML_URL = "http://etest.youku.com/v/course/play?";
//        public static final String SOCKET_HOST = "10.10.221.39:8080";
//          public static final String BASE_URL_DOMAIN = "http://etest.youku.com/user/RedirectUrl?";

//        /**
//         * 测试环境
//         **/
//        public static final String BASE_URL = "http://10.10.221.38:8181";
//        public static final String HTML_URL = "http://test.e.youku.com/v/course/play?";
//        public static final String BASE_URL_DOMAIN = "http://test.e.youku.com/user/RedirectUrl?";
//        public static final String SOCKET_HOST = "10.10.221.38:8080";

        /**
         * 正式环境
         **/
        public static final String BASE_URL = "http://e.youku.com";
        public static final String HTML_URL = "http://e.youku.com/v/course/play?";
        public static final String BASE_URL_DOMAIN = "http://e.youku.com/user/RedirectUrl?";
        public static final String SOCKET_HOST = "s.e.youku.com:80";


        /**
         * App更新
         **/
        public static final String APP_UPGRADE = BASE_URL + "/app/live/upgrade";
        /**
         * 用户登录
         **/
        public static final String USER_LOGIN = BASE_URL + "/user/login";
        /**
         * 登录注册发送短信
         */
        public static final String USER_LOGIN_PHONE_VALID_CODE = BASE_URL
                + "/user/login/SendCode";
        /**
         * 注册
         **/
        public static final String USER_REGISTER = BASE_URL
                + "/user/login/Register";
        /**
         * 图片验证码
         */
        public static final String USER_IMAGE_VERIFY_CODE = BASE_URL + "/public/verify_code";
        /**
         * 手机一键登录
         */
        public static final String USER_MOBILE_PHONE_A_KEY_LOGIN = BASE_URL
                + "/user/login/RegisterLoginByCode";
        /**
         * 手机验证码验证
         **/
        public static final String PHONE_VERIFY_CODE = BASE_URL
                + "/user/login/VerifyCode";
        /**
         * 找回密码
         **/
        public static final String USER_GET_PASSWORD = BASE_URL
                + "/user/login/GetPwd";
        /**
         * 第三方登陆
         **/
        public static final String THIRD_LOGIN = BASE_URL
                + "/user/login/ThirdApp";
        /**
         * 老师课程列表
         */
        public static final String TEACHER_COURSE_LIST = BASE_URL + "/course/user/List";
        /**
         * 近期直播列表
         */
        public static final String TEACHER_LIVE_LIST = BASE_URL + "/course/playChapter";

        /**
         * 老师信息
         */
        public static final String TEACHER_INFO = BASE_URL + "/user/teacher/MyInfo";

        /**
         * 课时列表
         */
        public static final String COURSE_HOUE = BASE_URL + "/course/user/ChapterList";

        /**
         * 创建课时
         */
        public static final String ADD_COURSE_HOUR = BASE_URL + "/course/addChapter";

        /**
         * 删除课时
         */
        public static final String DELETE_CHAPTER = BASE_URL + "/course/delChapter";

        /**
         * 直播信息
         */
        public static final String GET_LIVE_INFO = BASE_URL + "/live/GetLive";
        /**
         * 创建流
         **/
        public static final String URL_CREATE_STREAM = BASE_URL + "/live/CreateStream";
        /**
         * 直播开始或结束
         **/
        public static final String URL_LIVE_START_END = BASE_URL + "/live/SendLive";
    }

    public static interface HttpJson {
        public final static String KEY_DATA = "data";
        public final static String KEY_LIST = "list";
        public final static String KEY_STREAM_ID = "stream_id";
        public final static String KEY_UPLOAD_TOKEN = "upload_token";
        public final static String KEY_YT_UID = "yt_uid";
    }


    /**
     * 操作符
     *
     * @author Administrator
     */
    public static interface Operator {
        public static final int OPERATOR_USER_LOGIN = 1;
        public static final int OPERATOR_THIRD_LOGIN = OPERATOR_USER_LOGIN + 1;
        public static final int OPERATOR_TEACHER_COURSES = OPERATOR_THIRD_LOGIN + 1;
        public static final int OPERATOR_RECENT_LIVES = OPERATOR_TEACHER_COURSES + 1;
        public static final int OPERAOTR_TEACHER_INFO = OPERATOR_RECENT_LIVES + 1;
        public static final int OPERATOR_COURSE_HOUR = OPERAOTR_TEACHER_INFO + 1;
        public static final int OPERATOR_ADD_COURSE_HOUR = OPERATOR_COURSE_HOUR + 1;
        public static final int OPERATOR_GET_LIVE_INFO = OPERATOR_ADD_COURSE_HOUR + 1;
        public static final int OPERATOR_CREATE_STREAM = OPERATOR_GET_LIVE_INFO + 1;
        public static final int OPERATOR_LIVE_STATE_CHANGED = OPERATOR_CREATE_STREAM + 1;
        public static final int OPERATOR_DELETE_CHAPTER = OPERATOR_LIVE_STATE_CHANGED + 1;
        public static final int OPERATOR_USER_LOGIN_PHONE_VALID_CODE = OPERATOR_DELETE_CHAPTER + 1;
        public static final int OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN = OPERATOR_USER_LOGIN_PHONE_VALID_CODE + 1;
        public static final int OPERATOR_USER_REGISTER = OPERATOR_USER_MOBILE_PHONE_A_KEY_LOGIN + 1;
        public static final int OPERATOR_PHONE_VERIFY_CODE = OPERATOR_USER_REGISTER + 1;
        public static final int OPERATOR_USER_GET_PASSWORD = OPERATOR_PHONE_VERIFY_CODE + 1;
        public static final int OPERATOR_APP_UPDATE = OPERATOR_USER_GET_PASSWORD + 1;
        public static final int OPERATOR_LIVE_CLASS = OPERATOR_APP_UPDATE + 1;
        public static final int OPERAOR_IMAGE_VERIFY_CODE = OPERATOR_LIVE_CLASS + 1;
        /**
         * 直播异常dialog
         **/
        public static final int OPERATOR_DIALOG_LIVE_ERROR = OPERATOR_LIVE_STATE_CHANGED + 1;
        /**
         * 直播退出提示dialog
         **/
        public static final int OPERATOR_DIALOG_LIVE_EXIT = OPERATOR_DIALOG_LIVE_ERROR + 1;
        /**
         * 直播到开始时间提示dialog
         **/
        public static final int OPERATOR_DIALOG_LIVE_START = OPERATOR_DIALOG_LIVE_EXIT + 1;
    }

    /**
     * 本地SharePrefrence常量
     *
     * @author XingRongJing
     */
    public static interface SharedPreferenceDao {
        public static final String FILENAME_USER = "user";
        public static final String FILENAME_CONFIG = "config";
        public static final String KEY_USER_GUIDE = "key_user_guide";
        public static final String KEY_USER_INFO = "key_user_info";
        public static final String KEY_IS_NOTICE_BIND = "key_is_notice_bind";
        public static final String KEY_PLAYLOG_TOKEN = "key_playlog_token";

    }


    /**
     * 点击类型
     */
    public static interface ClickYype {
        public static final int COURSE_CLICK = 1;
        public static final int COURSE_HOUR_CLICK = 2;
        public static final int LIVE_CLASS_ROOM_CLICK = 3;
        public static final int CHAPTER_DELETE_CLICK = 4;
    }

    /**
     * 条目状态
     */
    public static interface ItemPlayStatus {

        public static final int COURSE_STATUS_UNPUBLISH = 10;//未发布
        public static final int COURSE_STATUS_SELLING = 20;//售卖中
        public static final int COURSE_STATUS_SELLING_STOP = 29;//已停售 课程已下架
        public static final int COURSE_STATUS_EXPIRED = 90;//已过期

        public static final int PLAY_STATUS = 30;//正在直播
        public static final int PLAY_STATUS_NO_LIVE = 20;//即将直播
        public static final int PLAY_STATUS_TODAY = 21;//今日直播[换算]
        public static final int PLAY_STATUS_PLAY_BACK = 10;//视频／回放
        public static final int PLAY_STATUS_HANDLE = 15;//回放处理中
        public static final int PLAY_STATUS_OVERDUE = 0;//已过期（课程维度的过期。也就是当前时间大于sell_end）[换算]

        public static final int LIVE_STATUS = 59;//正在直播
        public static final int LIVE_STATUS_SOON = 55;//即将直播
        public static final int LIVE_STATUS_CANCEL = 56;//直播取消
        public static final int LIVE_STATUS_END_NO_UPLOAD = 42;//直播结束，未上传
        public static final int LIVE_STATUS_END_UPLOADING = 41;//直播结束，上传中
        public static final int LIVE_STATUS_END_UPLOAD_FAIL = 40;//直播结束，上传失败
        public static final int LIVE_STATUS_TRANSCODING = 31;//转码中
        public static final int LIVE_STATUS_FAIL = 30;//转码失败
        public static final int LIVE_STATUS_CHECKING = 21;//审核中
        public static final int LIVE_STATUS_CHECK_FAIL = 22;//审核失败
        public static final int LIVE_STATUS_REPLAY = 19;//回放
    }
}
