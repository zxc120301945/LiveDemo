package com.you.edu.live.teacher.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

public abstract class FileUtil {

	/***
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 存在为true，否则为false
	 */
	public static boolean fileIsExist(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			return false;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取一个文件名（当前时间+3位随机数）
	 * 
	 * @return
	 */
	public static String getFileNameByTimestamp() {
		StringBuilder sb = new StringBuilder();
		String time = TimeUtil.formatDate(new Date(System.currentTimeMillis()),
				"yyyyMMddHHmmss");
		sb.append(time);
		sb.append(CommonUtil.getRandom(3));
		return sb.toString();
	}

	/**
	 * 获取长度为len的随机数
	 * 
	 * @param len
	 * @return
	 */
	public static String getRandom(int len) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			int temp = random.nextInt(10);
			sb.append(temp);
		}
		return sb.toString();
	}

	/**
	 * 格式化文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formetFileSize(long fileS) {
		if (0 == fileS) {
			return "0B";
		}
		DecimalFormat df = new DecimalFormat("#.0");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 一个文件或文件夹是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		return new File(path).exists();
	}

	/**
	 * 创建一个目录-已存在则直接返回
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createDir(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		if (FileUtil.isFileExist(path)) {
			return true;
		}
		File file = new File(path);
		if (file.isDirectory()) {
			return file.mkdirs();
		}
		return false;
	}

	/**
	 * 删除文件夹（级联删除，即其下面所有文件均删除）
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDirectory(String dir) {
		if (null == dir) {
			return false;
		}
		File file = new File(dir);

		if (file == null || !file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}
		file.delete();
		return true;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return 是否成功
	 */
	public static boolean deleteFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	public static boolean writeDatasToFile(String filePath, int rotate,
			byte[] datas, int maxWidth, int maxHeight) {
		if (rotate != 0) {
			Bitmap result = BitmapUtil.decode(datas);
			Bitmap temp = BitmapUtil.rotateBitmap(result, rotate);
			result.recycle();
			result = null;
			return FileUtil.compress(temp, filePath);
		}
		File pictureFile = new File(filePath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pictureFile);
			fos.write(datas);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (Exception e) {

			}
		}

		return false;
	}

	/**
	 * 拷贝图片文件到指定文件(简单来说就是压缩图片,再重新保存)-默认是保存为jpg格式
	 * 
	 * @param filePath
	 * @param saveFilePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static boolean copyBitmapFile(String filePath, String saveFilePath,
			int maxWidth, int maxHeight) {
		Bitmap bm = BitmapUtil.decode(filePath, maxWidth, maxHeight);
		return FileUtil.compress(bm, saveFilePath);
	}

	/**
	 * 根据maxWidth、maxHeight将byte[]数据写到本地
	 * 
	 * @param data
	 * @param filepath
	 *            本地完整路径
	 * @param rotate
	 *            旋转角度，0则不旋转
	 * @param maxWidth
	 * @param maxHeight
	 * @param isClearWhitePixel
	 *            是否去白(即去掉白色像素)
	 * @return
	 */
	public static boolean writeByteToFile(byte[] data, String filepath,
			int rotate, int maxWidth, int maxHeight, boolean isClearWhitePixel) {
		Bitmap bm = BitmapUtil.decode(data, maxWidth, maxHeight);
		Bitmap result = null;
		if (isClearWhitePixel) {
			result = BitmapUtil.getClearWhiteBitmap(bm);
		} else {
			result = bm;
		}
		if (0 == rotate) {
			return FileUtil.compress(result, filepath);
		} else {
			Bitmap temp = BitmapUtil.rotateBitmap(result, rotate);
			return FileUtil.compress(temp, filepath);
		}

	}

	public static boolean compress(Bitmap bitmap, CompressFormat format,
			String filepath) {
		File file = new File(filepath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(format, 80, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != out) {
					out.close();
				}
				if (null != bitmap&&!bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
//					System.gc();
				}
			} catch (Exception e) {

			}
		}
		return true;

	}

	public static boolean compress(Bitmap bitmap, String filepath) {
		return FileUtil.compress(bitmap, CompressFormat.JPEG, filepath);
	}

	/**
	 * 拷贝assets文件到sdcard下
	 * 
	 * @param ctx
	 * @param assetPath
	 * @param sdcardPath
	 *            文件路径加上文件名
	 * @return
	 */
	public static boolean copyAssetsFileToSdcard(Context ctx, String assetPath,
			String sdcardPath) {
		if (null == ctx) {
			return false;
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			InputStream is = ctx.getAssets().open(assetPath);
			File file = new File(sdcardPath);
			file.createNewFile();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buffer = new byte[1024 * 8];
			int length = bis.read(buffer);
			while (length > 0) {
				bos.write(buffer, 0, length);
				length = bis.read(buffer);
			}
			bos.flush();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception fnfe) {
			fnfe.printStackTrace();
		} finally {
			try {
				if (null != bos) {
					bos.close();
				}
				if (null != bis) {
					bis.close();
				}
			} catch (IOException e) {
			}
		}

		return false;
	}

}
