package com.you.edu.live.teacher.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * 缓存路径管理工具（优点是会随App卸载而删除缓存文件）
 * 
 * @author XingRongJing
 * 
 */
public abstract class CachePathUtils {

	/**
	 * 获取File缓存路径（如果sdcard可用，则为sdcard/Android/data/包名/files；否则为data/data/包名/）
	 * files
	 * 
	 * @param context
	 * @return
	 */
	public static String getCacheFilePath(Context context) {
		if (null == context) {
			return null;
		}
		File file = null;
		if (SdcardUtil.isSdcardExist()) {
			file = context.getExternalFilesDir("");
		} else {
			file = context.getFilesDir();
		}
		return null == file ? null : file.getPath();
	}

	/**
	 * 获取File缓存路径，（如果sdcard可用，则为sdcard/Android/data/包名/files/dir；否则为data/data/包名
	 * / files/dir ,默认情况如果dir不存在则会mkdirs）
	 * 
	 * @param context
	 * @return
	 */
	public static String getCacheFilePath(Context context, String dir) {
		if (null == context) {
			return null;
		}
		File file = null;
		if (SdcardUtil.isSdcardExist()) {
			file = context.getExternalFilesDir(dir);
			return null == file ? null : file.getPath();
		} else {
			return getFileDir(context, dir);
		}
	}

	/**
	 * 获取Pictures路径（如果sdcard可用，则sdcard/Android/data/包名/files/Pictures，否则为data/
	 * data/包名/ files/Pictures）
	 * 
	 * @param context
	 * @return
	 */
	public static String getCacheFilePicPath(Context context) {
		if (null == context) {
			return null;
		}
		File file = null;
		if (SdcardUtil.isSdcardExist()) {
			file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			return null == file ? null : file.getPath();
		} else {
			return getFileDir(context, Environment.DIRECTORY_PICTURES);
		}
	}

	/**
	 * 获取文件路径，如果dir不存在，则创建并返回，否则直接返回即可
	 * 
	 * @param context
	 * @param dirName
	 * @return
	 */
	private static String getFileDir(Context context, String dirName) {
		File file = context.getFilesDir();
		if (null != file) {
			File temp = new File(file.getPath() + "/" + dirName);
			if (!temp.exists()) {
				temp.mkdirs();
			}
			return temp.getPath();
		}
		return null;
	}

}
