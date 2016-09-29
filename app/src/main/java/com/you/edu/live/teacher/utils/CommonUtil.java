package com.you.edu.live.teacher.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * 作者：XingRongJing on 2016/7/1.
 */
public abstract class CommonUtil {
    /**
     * 格式化数字（四舍五入），如小于1000，则直接显示；1000-9999，则显示1.0k-9.9k；9999-无限，则显示1.0万-XX.0万
     *
     * @param number
     * @return
     */
    public static String numberFormat(long number) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat df1 = new DecimalFormat("##.0");
        if (number < 999) {// 百
            sb.append(number);
        } else if (number < 99999) {// 千(四舍五入的规则)
            float k = (number / (float) 1000);
            sb.append(df1.format(k));
            sb.append("k");
        } else if (number < 9999999) {// 万-百万(四舍五入的规则)
            float w = (number / (float) 10000);
            sb.append(df1.format(w));
            sb.append("w");
        } else {// 千万
            float w = (number / (float) 10000000);
            sb.append(df1.format(w));
            sb.append("kw");
        }
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
     * 状态栏高度算法
     *
     * @param activity
     * @return
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static String urlEncode(String msg) {
        if(TextUtils.isEmpty(msg)){
            return null;
        }
        try {
            return URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
