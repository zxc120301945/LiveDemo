package com.you.edu.live.teacher.model.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.you.edu.live.teacher.model.GlobalConfig;


/**
 * SharedPrefrence工具类
 * 
 * @author rongjing
 * 
 */
public abstract class SharedPreDao {

	private static final String TAG = SharedPreDao.class.getSimpleName();

	public static void saveBoolean(Context context, String key, boolean enable) {
		SharedPreferences sp = context.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, enable);
		editor.commit();
	}

	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		boolean enable = sp.getBoolean(key, false);
		return enable;
	}

	public static void saveString(Context ctx, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context ctx, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	public static void saveInt(Context ctx, String key, int value) {
		SharedPreferences sp = ctx.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context ctx, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(
				GlobalConfig.SharedPreferenceDao.FILENAME_CONFIG,
				Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}

	public static void saveObject(Context ctx, String filename, String key,
			Object obj) {
		if (null == obj) {
			return;
		}
		SharedPreferences sp = ctx.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			String info = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString(key, info);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, "shan-->saveObject：" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static <E> E getObject(Context ctx, String filename, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		String base64Str = sp.getString(key, "");
		return base64StringtoObject(base64Str);
	}

	public static <E> List<E> getAllObject(Context ctx, String filename) {
		SharedPreferences sp = ctx.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		Map<String, ?> maps = sp.getAll();
		if (null == maps) {
			return null;
		}
		List<E> results = new ArrayList<E>();
		Set<String> sets = maps.keySet();
		Iterator<String> it = sets.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String base64Str = sp.getString(key, "");
			if (TextUtils.isEmpty(base64Str)) {
				continue;
			}
			E e = base64StringtoObject(base64Str);
			if (null != e) {
				results.add(e);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private static <E> E base64StringtoObject(String base64Str) {
		if (TextUtils.isEmpty(base64Str)) {
			return null;
		}
		ByteArrayInputStream bais = null;
		try {
			byte[] base64Bytes = Base64.decode(base64Str.getBytes(),
					Base64.DEFAULT);
			bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			E e = (E) ois.readObject();
			return e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bais) {
					bais.close();
				}
			} catch (Exception e) {

			}
		}
		return null;
	}

	public static void deleteAll(Context ctx, String filename) {
		SharedPreferences sp = ctx.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	public static void deleteOne(Context ctx, String filename, String key) {
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(filename)) {
			return;
		}
		SharedPreferences sp = ctx.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
}
