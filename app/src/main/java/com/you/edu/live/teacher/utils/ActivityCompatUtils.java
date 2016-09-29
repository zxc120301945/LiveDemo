package com.you.edu.live.teacher.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.you.edu.live.teacher.R;


/**
 * activity跳转动画处理类（辅助AppTheme使用，见Manifest）
 *
 * @author XingRongJing
 */
public class ActivityCompatUtils {

    /**
     * Activity开启进入（从右往左）
     *
     * @param activity
     * @param intent
     */
    public static void startActivityLand(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_in,
                R.anim.slide_none);
    }

    /**
     * Activity开启进入（从右往左）
     *
     * @param activity
     * @param intent
     * @param requestCode
     */
    public static void startActivityForResultLand(Activity activity,
                                                  Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_right_in,
                R.anim.slide_none);
    }

    /**
     * Activity开启进入（从下往上）
     *
     * @param activity
     * @param intent
     */
    public static void startActivityPortrait(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.slide_none);
    }

    /**
     * Activity开启进入（从下往上）
     *
     * @param activity
     * @param intent
     * @param requestCode
     */
    public static void startActivitytForResultPortrait(Activity activity,
                                                       Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.slide_none);
    }

    /**
     * Fragment里开启Activity进入（从右往左）
     *
     * @param intent
     */
    public static void startActivitytLand(Fragment fragment, Intent intent) {
        fragment.startActivity(intent);
        ((Activity) fragment.getContext()).overridePendingTransition(
                R.anim.slide_right_in, R.anim.slide_none);
    }

    /**
     * Fragment里开启Activity进入（从右往左）
     *
     * @param intent
     * @param requestCode
     */
    public static void startActivityForResultLand(Fragment fragment,
                                                  Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
        ((Activity) fragment.getContext()).overridePendingTransition(
                R.anim.slide_right_in, R.anim.slide_none);
    }

    /**
     * Fragment里开启Activity进入（从下往上）
     *
     * @param intent
     */
    public static void startActivityPortrait(Fragment fragment, Intent intent) {
        fragment.startActivity(intent);
        ((Activity) fragment.getContext()).overridePendingTransition(
                R.anim.slide_in_from_bottom, R.anim.slide_none);
    }

    /**
     * Fragment里开启Activity进入（从下往上）
     *
     * @param fragment
     * @param intent
     * @param requestCode
     */
    public static void startActivityForResultPortrait(Fragment fragment,
                                                      Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
        ((Activity) fragment.getContext()).overridePendingTransition(
                R.anim.slide_in_from_bottom, R.anim.slide_none);
    }

    /**
     * finish activity（（从左往右））
     *
     * @param act
     */
    public static void finishActivityLand(Activity act) {
        act.overridePendingTransition(R.anim.slide_none, R.anim.slide_right_out);
    }

    /**
     * finish activity（（从上往下））
     *
     * @param act
     */
    public static void finishActivityProtrait(Activity act) {
        act.overridePendingTransition(R.anim.slide_none,
                R.anim.slide_out_to_bottom);
    }
}
