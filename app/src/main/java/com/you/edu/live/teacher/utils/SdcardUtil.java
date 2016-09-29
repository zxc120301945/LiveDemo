package com.you.edu.live.teacher.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * Sdcard相关操作类-如果有内置存储和外置存储时，一般获取到的是内置存储路径，此时如果想强行获取
 * 外置存储路径，则采用ExtSdcardInfo
 * @author XingRongJing
 *
 */
public abstract class SdcardUtil {

	public static boolean isSdcardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

//	public static String getSdcardPath() {
//		if (!SdcardUtil.isSdcardExist()) {
//			return "";
//		}
//		return Environment.getExternalStorageDirectory().getPath();
//	}

	/**
	 * 获取sd卡剩余大小
	 */
	public static long getAvailaleSize() {
		if (SdcardUtil.isSdcardExist()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long availCount = sf.getAvailableBlocks();
			long blockSize = sf.getBlockSize();
			long availSize = availCount * blockSize;
			return availSize;
		}
		return 0;

	}
}
