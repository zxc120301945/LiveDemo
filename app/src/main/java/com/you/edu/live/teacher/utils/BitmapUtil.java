package com.you.edu.live.teacher.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

/**
 * Bitmap工具类
 * 
 * @author XingRongJing
 * 
 */
public abstract class BitmapUtil {

	/** 颜色透明阀值 **/
	private static final int THRESHOLD_ALPHA = 100;

	/**
	 * 
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * 
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 将100以上的像素点透明，其余的变为color
	 * 
	 * @param sourceImg
	 * @param color
	 * @return
	 */
	public static Bitmap clearWhiteAndReplaceColor(Bitmap sourceImg, int color) {

		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

		.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

		for (int i = 0; i < argb.length; i++) {

			// int grey = argb[i] & 0xff;
			int tempColor = argb[i];
			int r = Color.red(tempColor);
			int g = Color.green(tempColor);
			int b = Color.blue(tempColor);
			// int modif_color = Math.max(b, Math.max(r, g));
			// int modif_color = Math.min(b, Math.min(r, g));
			// int modif_color = (r + g + b) / 3;
			int modif_color = (int) (r * 0.3 + g * 0.59 + b * 0.11);
			if (modif_color > THRESHOLD_ALPHA) {// 大于100则默认为白色，透明化，否则就color色
				argb[i] = (0 << 24) | (argb[i] & 0x00FFFFFF);
			} else {
				argb[i] = color;
			}
		}

		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;

	}

	/**
	 * 将100以上的像素点透明，其余的变黑
	 * 
	 * @param sourceImg
	 * @return
	 */
	public static Bitmap clearWhiteAndReplaceBlackColor(Bitmap sourceImg) {
		return BitmapUtil.clearWhiteAndReplaceColor(sourceImg, Color.BLACK);
	}

	/**
	 * 设置Bitmap透明度
	 * 
	 * @param sourceImg
	 * @param number
	 * @return
	 */
	public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

		.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

		number = number * 255 / 100;

		for (int i = 0; i < argb.length; i++) {

			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
		}

		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	/**
	 * 获取一张去白的图片
	 * 
	 * @param sourceImg
	 * @param threshold
	 *            阈值（0-255之间-255是白色,0是黑色）
	 * @return
	 */
	public static Bitmap getClearWhiteBitmap(Bitmap sourceImg, int threshold) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
		int number = 0;

		for (int i = 0; i < argb.length; i++) {
			int color = argb[i];
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);
			// int modif_color = Math.max(b, Math.max(r, g));
			// int modif_color = Math.min(b, Math.min(r, g));
			// int modif_color = (r + g + b) / 3;
			int modif_color = (int) (r * 0.3 + g * 0.59 + b * 0.11);
			if (modif_color > threshold) {
				argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
			}
		}
		Bitmap result =Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888); 
		if(null!=sourceImg&&!sourceImg.isRecycled()){
			sourceImg.recycle();
			sourceImg = null;
		}
		return result;
	}

	/**
	 * 将指定颜色（sourceColor）替换为其他颜色（color）
	 * 
	 * @param sourceImg
	 * @param sourceColor
	 * @param color
	 * @return
	 */
	public static Bitmap replaceColor(Bitmap sourceImg, int sourceColor,
			int color) {

		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

		for (int i = 0; i < argb.length; i++) {
			int tempColor = argb[i];
			if (tempColor == sourceColor) {
				argb[i] = color;
			}
		}
		Bitmap temp =  Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		 
		if(null!=sourceImg&!sourceImg.isRecycled()){
			sourceImg.recycle();
			sourceImg = null;
		}
		return temp;

	}

	/**
	 * 获取一张去白的图片（透明化120以上的（至于为什么用120作比较，这是一个经验值吧，设置为100也可以，可以根据效果来调整。）
	 * 
	 * @param sourceImg
	 * @return
	 */
	public static Bitmap getClearWhiteBitmap(Bitmap sourceImg) {
		return BitmapUtil.getClearWhiteBitmap(sourceImg, THRESHOLD_ALPHA);
	}

	/**
	 * 旋转bitmap
	 * 
	 * @param source
	 * @param degree
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap source, int degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap temp = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
		if(null!=source&&!source.isRecycled()){
			source.recycle();
			source = null;
		}
		return temp;
	}

	/**
	 * 根据filepath转为bitmap(maxWidth与maxHeight均为0时不压缩)
	 * 
	 * @param mMaxWidth
	 * @param mMaxHeight
	 * @return bitmap 或 null
	 */
	public static Bitmap decode(Resources res, int resId, int mMaxWidth,
			int mMaxHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			if (mMaxWidth == 0 && mMaxHeight == 0) {
				decodeOptions.inPreferredConfig = Config.RGB_565;
				// bitmap = BitmapFactory.decodeFile(filepath, decodeOptions);
				bitmap = BitmapFactory.decodeResource(res, resId);
			} else {
				// If we have to resize this image, first get the natural
				// bounds.
				decodeOptions.inJustDecodeBounds = true;
				// BitmapFactory.decodeFile(filepath, decodeOptions);
				BitmapFactory.decodeResource(res, resId, decodeOptions);
				int actualWidth = decodeOptions.outWidth;
				int actualHeight = decodeOptions.outHeight;

				// Then compute the dimensions we would ideally like to decode
				// to.
				int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
						actualWidth, actualHeight);
				int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
						actualHeight, actualWidth);

				// Decode to the nearest power of two scaling factor.
				decodeOptions.inJustDecodeBounds = false;
				// TODO(ficus): Do we need this or is it okay since API 8
				// doesn't
				// support it?
				// decodeOptions.inPreferQualityOverSpeed =
				// PREFER_QUALITY_OVER_SPEED;
				decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
						actualHeight, desiredWidth, desiredHeight);
				Bitmap tempBitmap = BitmapFactory.decodeResource(res, resId,
						decodeOptions);
				// If necessary, scale down to the maximal acceptable size.
				if (tempBitmap != null
						&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
								.getHeight() > desiredHeight)) {
					bitmap = Bitmap.createScaledBitmap(tempBitmap,
							desiredWidth, desiredHeight, true);
					tempBitmap.recycle();
					tempBitmap = null;
				} else {
					bitmap = tempBitmap;
				}

			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 根据filepath转为bitmap(maxWidth与maxHeight均为0时不压缩)
	 * 
	 * @param filepath
	 * @param mMaxWidth
	 * @param mMaxHeight
	 * @return bitmap 或 null
	 */
	public static Bitmap decode(String filepath, int mMaxWidth, int mMaxHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			if (mMaxWidth == 0 && mMaxHeight == 0) {
				decodeOptions.inPreferredConfig = Config.RGB_565;
				bitmap = BitmapFactory.decodeFile(filepath, decodeOptions);
			} else {
				// If we have to resize this image, first get the natural
				// bounds.
				decodeOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filepath, decodeOptions);
				int actualWidth = decodeOptions.outWidth;
				int actualHeight = decodeOptions.outHeight;

				// Then compute the dimensions we would ideally like to decode
				// to.
				int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
						actualWidth, actualHeight);
				int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
						actualHeight, actualWidth);

				// Decode to the nearest power of two scaling factor.
				decodeOptions.inJustDecodeBounds = false;
				// TODO(ficus): Do we need this or is it okay since API 8
				// doesn't
				// support it?
				// decodeOptions.inPreferQualityOverSpeed =
				// PREFER_QUALITY_OVER_SPEED;
				decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
						actualHeight, desiredWidth, desiredHeight);
				Bitmap tempBitmap = BitmapFactory.decodeFile(filepath,
						decodeOptions);
				// If necessary, scale down to the maximal acceptable size.
				if (tempBitmap != null
						&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
								.getHeight() > desiredHeight)) {
					bitmap = Bitmap.createScaledBitmap(tempBitmap,
							desiredWidth, desiredHeight, true);
					if(!tempBitmap.isRecycled()){
						tempBitmap.recycle();
					}
					tempBitmap = null;
				} else {
					bitmap = tempBitmap;
				}

			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap decode(String filepath) {
		return BitmapUtil.decode(filepath, 0, 0);
	}

	public static Bitmap decode(Uri uri) {
		return BitmapUtil.decode(uri, 0, 0);
	}

	public static Bitmap decode(Uri uri, int mMaxWidth, int mMaxHeight) {
		return BitmapUtil.decode(null == uri ? "" : uri.getPath(), mMaxWidth,
				mMaxHeight);
	}

	public static Bitmap decode(byte[] data) {
		return BitmapUtil.decode(data, 0, 0);
	}

	/**
	 * The real guts of parseNetworkResponse. Broken out for readability.
	 */
	public static Bitmap decode(byte[] data, int mMaxWidth, int mMaxHeight) {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		Bitmap bitmap = null;
		if (mMaxWidth == 0 && mMaxHeight == 0) {
			decodeOptions.inPreferredConfig = Config.RGB_565;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					decodeOptions);
		} else {
			// If we have to resize this image, first get the natural bounds.
			decodeOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
			int actualWidth = decodeOptions.outWidth;
			int actualHeight = decodeOptions.outHeight;

			// Then compute the dimensions we would ideally like to decode to.
			int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
					actualWidth, actualHeight);
			int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
					actualHeight, actualWidth);

			// Decode to the nearest power of two scaling factor.
			decodeOptions.inJustDecodeBounds = false;
			// TODO(ficus): Do we need this or is it okay since API 8 doesn't
			// support it?
			// decodeOptions.inPreferQualityOverSpeed =
			// PREFER_QUALITY_OVER_SPEED;
			decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
					actualHeight, desiredWidth, desiredHeight);
//			decodeOptions.inSampleSize = 4;
			Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length, decodeOptions);

			// If necessary, scale down to the maximal acceptable size.
			if (tempBitmap != null
					&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
							.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
						desiredHeight, true);
				if(!tempBitmap.isRecycled()){
					tempBitmap.recycle();
					tempBitmap = null;
				}
			} else {
				bitmap = tempBitmap;
			}
		}
		return bitmap;
	}

	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 * 
	 * @param maxPrimary
	 *            Maximum size of the primary dimension (i.e. width for max
	 *            width), or zero to maintain aspect ratio with secondary
	 *            dimension
	 * @param maxSecondary
	 *            Maximum size of the secondary dimension, or zero to maintain
	 *            aspect ratio with primary dimension
	 * @param actualPrimary
	 *            Actual size of the primary dimension
	 * @param actualSecondary
	 *            Actual size of the secondary dimension
	 */
	private static int getResizedDimension(int maxPrimary, int maxSecondary,
			int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * Returns the largest power-of-two divisor for use in downscaling a bitmap
	 * that will not result in the scaling past the desired dimensions.
	 * 
	 * @param actualWidth
	 *            Actual width of the bitmap
	 * @param actualHeight
	 *            Actual height of the bitmap
	 * @param desiredWidth
	 *            Desired width of the bitmap
	 * @param desiredHeight
	 *            Desired height of the bitmap
	 */
	// Visible for testing.
	private static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	public static Point getBitmpActualWh(byte[] data) {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
		int actualWidth = decodeOptions.outWidth;
		int actualHeight = decodeOptions.outHeight;
		decodeOptions.inJustDecodeBounds = false;
		return new Point(actualWidth, actualHeight);
	}
}
