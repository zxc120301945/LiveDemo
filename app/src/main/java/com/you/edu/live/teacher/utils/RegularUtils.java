package com.you.edu.live.teacher.utils;

import android.text.TextUtils;

/**
 * 正则匹配工具类
 * 
 * @author Administrator
 * 
 */
public class RegularUtils {

	/** 国内手机号规则 **/
	public static final String PHONE_REGULAR = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
	/** 六位以上密码规则 **/
	public static final String NUMBER_PASSWORD_REGULAR = "\\d{7,16}$";
	/** 六位以下密码规则 **/
	public static final String SIX_PASSWORD_REGULAR = "[\\u4e00-\\u9fa5，——`·《》“”：；【】。！/!~@#$￥%……& *（）()-_=+\"|''’‘{}？?、.a-zA-Z0-9\\d]+";
	/** 邮箱规则 **/
	public static final String EMAIL_REGULAR = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

	public RegularUtils() {

	}

	/**
	 * 国内手机号匹配
	 * 
	 * @param phoneNumber
	 *            手机号
	 * @param regular
	 *            正则
	 */
	public static boolean phoneMatching(String phoneNumber, String regular) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}
		return phoneNumber.matches(regular);
	}

	/**
	 * 密码匹配
	 * 
	 * @param password
	 *            密码
	 * @param regular
	 *            规则
	 * @return
	 */
	public static boolean passwordMatching(String password, String regular) {
		if (TextUtils.isEmpty(password)) {
			return false;
		}
		return password.matches(regular);
	}

	/**
	 * 邮箱账号匹配
	 * 
	 * @param userId
	 *            账号
	 * @param regular
	 *            规则
	 * @return
	 */
	public static boolean emailMatching(String userId, String regular) {
		if (TextUtils.isEmpty(userId)) {
			return false;
		}
		return userId.matches(regular);
	}

	/**
	 * 密码验证是否是数字字母混合
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isLetterDigit(String password) {
		boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字
		boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母
		for (int i = 0; i < password.length(); i++) {
			if (Character.isDigit(password.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
				isDigit = true;
			}
			if (Character.isLetter(password.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
				isLetter = true;
			}
		}
		// String regex = "^[a-zA-Z0-9]+$";
		return isDigit && isLetter && password.matches(SIX_PASSWORD_REGULAR);
	}

}
