package com.you.edu.live.teacher.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import com.you.edu.live.teacher.R;


public abstract class StringUtils {

	public static final String EMPTY = "";

	/**
	 * 转换时间显示
	 * 
	 * @param time
	 *            毫秒
	 * @return
	 */
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? EMPTY : str.trim();
	}

	/**
	 * 获取SpannableString(形如00天00小时00分00秒)
	 * @param context
	 * @param countDownTime
	 * @return
	 */
	public static SpannableString getSpannableStringCountdown(Context context,
			String countDownTime) {
		SpannableString styledText = new SpannableString(countDownTime);
		int appearanceNormal = R.style.course_count_down_time_normal;
		int appearanceBright = R.style.course_count_down_time_bright;
		styledText.setSpan(new TextAppearanceSpan(context, appearanceBright),
				0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceNormal),
				2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceBright),
				3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceNormal),
				5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceBright),
				7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceNormal),
				9, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceBright),
				10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, appearanceNormal),
				12, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styledText;
	}

}
