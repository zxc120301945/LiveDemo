package com.you.edu.live.teacher.model.dao;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.you.edu.live.teacher.model.GlobalConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器返回json数据解析助手
 * 
 * @author XingRongJing
 * 
 */
public class RequestDataParser {

	private static final String TAG = "RequestDataParser";
	private int mRespCode = GlobalConfig.INVALID;
	private String mResult;
	private String mMsg;
	private int mId = GlobalConfig.INVALID;
	private Gson mGson;
	private JSONObject mJsonResult;

	/**
	 * @param result
	 *            Json String
	 * 
	 */
	public RequestDataParser(String result) {
		mGson = new Gson();
		try {
			mJsonResult = new JSONObject(result);
			if (mJsonResult.has(KEY_CODE)) {
				mRespCode = mJsonResult.getInt(KEY_CODE);
			}
			if (mJsonResult.has(KEY_MSG)) {
				mMsg = mJsonResult.getString(KEY_MSG);
			}
			if (mJsonResult.has(KEY_DATA)) {// 查询操作成功
				mResult = mJsonResult.getString(KEY_DATA);
			} else if (mJsonResult.has(KEY_ID)) {// 增加、删除、修改操作成功
				mId = mJsonResult.getInt(KEY_ID);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 操作是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return RESP_CODE_SUCCESS == mRespCode;
	}

	/**
	 * 是否登录互斥（即单一账号登录原则）
	 * 
	 * @return
	 */
	public boolean isExclusiveSuccess() {
		return RESP_CODE_EXCLUSIVE_LOGIN == mRespCode;
	}

	/**
	 * 操作失败时的RespCode
	 * 
	 * @return
	 */
	public int getRespCode() {
		return mRespCode;
	}

	/**
	 * 获取成功时的json数据（失败时，此值为空）
	 * 
	 * @return
	 */
	public String getSuccessData() {
		return this.mResult;
	}

	/**
	 * 操作失败时获取失败信息
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		return mMsg;
	}

	/**
	 * 查询列表成功时获取结果集
	 * 
	 * @param cls
	 * @return
	 */
	public <E> List<E> getArray(Class<E> cls) {
		return this.getArray(mResult, cls);
	}

	/**
	 * 获取列表结果集
	 * 
	 * @param jsonArray
	 *            json数组
	 * @param cls
	 *            实体类
	 * @return
	 */
	public <E> List<E> getArray(String jsonArray, Class<E> cls) {
		if (TextUtils.isEmpty(jsonArray)) {
			return null;
		}
		try {
			JSONArray array = new JSONArray(jsonArray);
			List<E> datas = new ArrayList<E>();
			int count = array.length();
			for (int i = 0; i < count; i++) {
				JSONObject json = array.getJSONObject(i);
				try {
					E e = mGson.fromJson(json.toString(), cls);
					datas.add(e);
				} catch (Exception e) {
					Log.e(TAG, "shan-->getArray() " + e.getMessage());
				}
			}
			return datas;
		} catch (Exception e) {
			Log.e(TAG, "shan-->getArray() " + e.getMessage());
			return null;
		}
	}

	/**
	 * 查询单个数据成功时获取结果
	 * 
	 * @param cls
	 * @return
	 */
	public <E> E getOne(Class<E> cls) {
		return this.getOne(mResult, cls);
	}

	public <E> E getOne(String json, Class<E> cls) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		try {
			E e = mGson.fromJson(json, cls);
			return e;
		} catch (Exception e) {
			Log.e(TAG, "shan-->getOne() " + e.getMessage());
		}
		return null;
	}

	/**
	 * 增加、删除、修改成功时，获取服务器生成id
	 * 
	 * @return
	 */
	public int getId() {
		return this.mId;
	}

	/**
	 * 根据某个键获取某个json值（可获取第二级(即data级的值)）
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		try {
			if (mJsonResult.has(key)) {
				return mJsonResult.get(key) + "";
			} else {
				JSONObject dataJson = new JSONObject(this.getSuccessData());
				if (dataJson.has(key)) {
					return dataJson.get(key) + "";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据某个键获取某个json值（可获取第二级(即data级的值)）
	 * 
	 * @param key
	 * @return
	 */
	public int getValueInt(String key) {
		try {
			if (mJsonResult.has(key)) {
				return mJsonResult.getInt(key);
			} else {
				JSONObject dataJson = new JSONObject(this.getSuccessData());
				if (dataJson.has(key)) {
					return dataJson.getInt(key);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/** 登录成功响应码 **/
	public final static int RESP_CODE_SUCCESS = 1;
	/** 登录互斥响应码 **/
	public static final int RESP_CODE_EXCLUSIVE_LOGIN = 10623;
	public final static String KEY_DATA = "data";
	public final static String KEY_CODE = "code";
	public final static String KEY_MSG = "msg";
	public final static String KEY_ID = "id";

}
