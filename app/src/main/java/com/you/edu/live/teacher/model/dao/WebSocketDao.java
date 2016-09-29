package com.you.edu.live.teacher.model.dao;

import com.google.gson.JsonObject;
import com.you.edu.live.teacher.model.bean.Comments;
import com.you.edu.live.teacher.model.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author XingRongJing
 */
public abstract class WebSocketDao {

	/** 类型-心跳 **/
	public static final String TYPE_PING = "ping";
	/** 类型-心跳响应 **/
	public static final String TYPE_PONG = "pong";
	/** 类型-登录 **/
	public static final String TYPE_LOGIN = "login";
	/** 类型-直播流 **/
	public static final String TYPE_COURSE = "course";
	/** 类型-消息历史 **/
	public static final String TYPE_HISTORY = "history";
	/** 类型-说话 **/
	public static final String TYPE_SAY = "say";
	/** 类型-通知 **/
	public static final String TYPE_NOTIFY = "login_notify";
	/** 类型-有人退出 **/
	public static final String TYPE_LOGOUT = "logout";
	/** 类型-点赞 **/
	public static final String TYPE_GOOD = "praise";
	/** 类型-出错 **/
	public static final String TYPE_ERROR = "error";
	/** 动作-获取直播url **/
	public static final String ACTION_GET_LIVE_URL = "get_live_url";
	/** 动作-获取直播url **/
	public static final String ACTION_PUSH_LIVE_URL = "push_live_url";
	/** 动作-举报 **/
	public static final String ACTION_PUSH_CHAPTER_DEL = "push_chapter_del";
	/** 动作-直播延时  **/
	public static final String ACTION_LIVE_DELAY = "push_live_delay";
	/** 播主-主动停止直播 **/
	public static final int CLOSE_BY_OWNER = 1;
	/** 键 **/
	private static final String KEY_TYPE = "type";
	private static final String KEY_USER_INFO = "user_info";
	private static final String KEY_DEVICE_ID = "devid";
	private static final String KEY_ROOM_ID = "room_id";
	private static final String KEY_UID = "uid";
	private static final String KEY_YKTK = "yktk";
	private static final String KEY_YSUID = "ysuid";
	public static final String KEY_NICK_NAME = "nick_name";
	private static final String KEY_PHOTO = "photo";
	private static final String KEY_USER = "user";
	private static final String KEY_USERS = "users";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_TO_USER = "to_user";
	private static final String KEY_FROM_USER = "from_user";
	private static final String KEY_ACTION = "action";
	private static final String KEY_APP_URL = "appurl";
	private static final String KEY_ROOM_INFO = "room_info";
	private static final String KEY_LIST = "list";
	private static final String KEY_COUNT = "cnt";
	private static final String KEY_GOOD = "praise";
	private static final String KEY_IS_CLOSE = "isclose";
	public static final String KEY_MSG = "msg";
	public static final String KEY_NUM = "num";

	/**
	 * 构建心跳响应
	 * 
	 * @return
	 */
	public static String buildPong() {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_PONG);
		return json.toString();
	}

	/**
	 * 构建匿名登录json
	 * 
	 * @param deviceId
	 * @param roomId
	 * @return
	 */
	public static String buildLoginAnonymous(String deviceId, String roomId) {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_LOGIN);
		JsonObject userInfo = new JsonObject();
		userInfo.addProperty(KEY_DEVICE_ID, deviceId);
		json.add(KEY_USER_INFO, userInfo);
		json.addProperty(KEY_ROOM_ID, roomId);
		return json.toString();
	}

	/**
	 * 构建登录json
	 * 
	 * @param uid
	 * @param yktk
	 * @param ysuid
	 * @param roomId
	 * @return
	 */
	public static String buildLogin(int uid, String yktk, String ysuid,
			String roomId) {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_LOGIN);
		JsonObject userInfo = new JsonObject();
		userInfo.addProperty(KEY_UID, uid);
		userInfo.addProperty(KEY_YKTK, yktk);
		userInfo.addProperty(KEY_YSUID, ysuid);
		json.add(KEY_USER_INFO, userInfo);
		json.addProperty(KEY_ROOM_ID, roomId);
		return json.toString();
	}

	/**
	 * 构建说话json
	 * 
	 * @param content
	 * @param toUid
	 *            根某个人说话时需要，否则为0
	 * @return
	 */
	public static String buildSay(String content, int toUid) {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_SAY);
		json.addProperty(KEY_CONTENT, content);
		if (0 != toUid) {
			json.addProperty(KEY_TO_USER, toUid);
		}
		return json.toString();
	}

	/**
	 * 构建获取直播流url
	 * 
	 * @param roomId
	 * @return
	 */
	public static String buildGetLiveUrl(String roomId) {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_COURSE);
		json.addProperty(KEY_ACTION, ACTION_GET_LIVE_URL);
		json.addProperty(KEY_ROOM_ID, roomId);
		return json.toString();
	}

	/**
	 * 构建推直播流url
	 * 
	 * @param roomId
	 * @return
	 */
	public static String buildPushLiveUrl(String roomId) {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_COURSE);
		json.addProperty(KEY_ACTION, ACTION_PUSH_LIVE_URL);
		json.addProperty(KEY_ROOM_ID, roomId);
		return json.toString();
	}

	/**
	 * 构建点赞msg
	 * 
	 * @return
	 */
	public static String buildGood() {
		JsonObject json = new JsonObject();
		json.addProperty(KEY_TYPE, TYPE_GOOD);
		return json.toString();
	}

	public static int getUid(String resp) {
		return getIntValueByKey(resp, KEY_UID);
	}

	public static String getType(String resp) {
		return getValueByKey(resp, KEY_TYPE);
	}

	public static String getAction(String resp) {
		return getValueByKey(resp, KEY_ACTION);
	}

	public static int getClose(String resp) {
		return getIntValueByKey(resp, KEY_IS_CLOSE);
	}

	public static String getAppUrl(String resp) {
		return getValueByKey(resp, KEY_APP_URL);
	}

	public static String getValueByKey(String resp, String key) {
		try {
			JSONObject json = new JSONObject(resp);
			if (json.has(key)) {
				return json.getString(key);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static int getIntValueByKey(String resp, String key) {
		try {
			JSONObject json = new JSONObject(resp);
			if (json.has(key)) {
				return json.getInt(key);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取消息
	 * 
	 * @param resp
	 * @return
	 */
	public static Comments getComment(String resp) {
		try {
			Comments c = new Comments();
			JSONObject json = new JSONObject(resp);
			c.setContent(json.getString(KEY_CONTENT));
			JSONObject fromUserJson = json.getJSONObject(KEY_FROM_USER);
			c.setAvator(fromUserJson.getString(KEY_PHOTO));
			c.setUsername(fromUserJson.getString(KEY_NICK_NAME));
			c.setUid(fromUserJson.getInt(KEY_UID));
			return c;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从房间信息获取播主信息
	 * 
	 * @param resp
	 * @return
	 */
	public static User getOwner(String resp) {
		try {
			JSONObject json = new JSONObject(resp);
//			JSONObject roomJson = json.getJSONObject(KEY_ROOM_INFO);
			JSONObject roomJson = json.getJSONObject(KEY_USER);
			User user = new User();
			if (roomJson.has(KEY_UID)) {
				user.setUid(roomJson.getInt(KEY_UID));
			}
			if (roomJson.has(KEY_NICK_NAME)) {
				user.setUser_name(roomJson.getString(KEY_NICK_NAME));
			}
			if (roomJson.has(KEY_PHOTO)) {
				user.setPhoto(roomJson.getString(KEY_PHOTO));
			}
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取某人信息
	 * 
	 * @param resp
	 * @return
	 */
	public static User getUser(String resp) {
		try {
			JSONObject json = new JSONObject(resp);
			JSONObject userJson = json.getJSONObject(KEY_USER);
			User user = new User();
			if (userJson.has(KEY_UID)) {
				user.setUid(userJson.getInt(KEY_UID));
			}
			if (userJson.has(KEY_NICK_NAME)) {
				user.setNick_name(userJson.getString(KEY_NICK_NAME));
			}
			if (userJson.has(KEY_PHOTO)) {
				user.setPhoto(userJson.getString(KEY_PHOTO));
			}
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取在线人员信息列表
	 * 
	 * @param resp
	 * @param selfUid
	 * @return
	 */
	public static List<User> getUsers(String resp, int selfUid) {
		try {
			List<User> users = new ArrayList<>();
			JSONObject json = new JSONObject(resp);
			JSONObject jsonUsers = json.getJSONObject(KEY_USERS);
			JSONObject listUsers = jsonUsers.getJSONObject(KEY_LIST);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = listUsers.keys();
			while (iterator.hasNext()) {
				String json_key = iterator.next();
				JSONObject json_value = listUsers.getJSONObject(json_key);
				User user = new User();
				if (json_value.has(KEY_UID)) {
					int tempUid = json_value.getInt(KEY_UID);
					if (tempUid == selfUid) {//去掉自己（用于主播断网再次获取所有用户列表时，去掉自己）
						continue;
					}
					user.setUid(tempUid);
				}
				if (json_value.has(KEY_NICK_NAME)) {
					user.setUser_name(json_value.getString(KEY_NICK_NAME));
				}
				if (json_value.has(KEY_PHOTO)) {
					user.setPhoto(json_value.getString(KEY_PHOTO));
				}
				users.add(user);
			}
			return users;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取点赞数量
	 * 
	 * @param resp
	 * @return
	 */
	public static int getGoodCount(String resp) {
		try {
			JSONObject json = new JSONObject(resp);
			JSONObject jsonUsers = json.getJSONObject(KEY_USERS);
			JSONObject countJson = jsonUsers.getJSONObject(KEY_COUNT);
			return countJson.getInt(KEY_GOOD);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
