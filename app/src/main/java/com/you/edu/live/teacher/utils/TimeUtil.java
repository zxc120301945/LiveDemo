package com.you.edu.live.teacher.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public abstract class TimeUtil {

    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_NO_YEARANDSECOND = "MM-dd HH:mm";
    public static final String DATE_FORMAT_MM_DD = "MM-dd";
    public static final String DATE_FORMAT_HH_MM = "HH:mm";

    public static final long DAY_MILLSECONDS = 1000 * 60 * 60 * 24;
    public static final long HOUR_MILLSECONDS = 1000 * 60 * 60;
    public static final long MINUTE_MILLSECONDS = 1000 * 60;

    public static final int MAX_FATALISM = 120;

    /**
     * 年
     **/
    public static final int PROCUREMENT_TYPE_YEAR = 1;
    /**
     * 月
     **/
    public static final int PROCUREMENT_TYPE_MONTH = 2;
    /**
     * 日
     **/
    public static final int PROCUREMENT_TYPE_DAY = 3;
    /**
     * 星期
     **/
    public static final int PROCUREMENT_TYPE_WAY = 4;

    // 将日期类型的转换成String类型
    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT_NO_YEARANDSECOND);
    }

    // 将日期类型的转换成String类型
    public static String formatDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }

    // 将日期类型的转换成String类型
    public static String formatNoYearDate(Date date) {
        return TimeUtil.formatDate(date, DATE_FORMAT_NO_YEARANDSECOND);
    }

    // 将String类型的数据转换成时间格式
    public static Date getDateByStrToYMD(String str) {
        Date date = null;
        if (str != null && str.trim().length() > 0) {
            DateFormat dFormat = new SimpleDateFormat(DATE_FORMAT_FULL);
            try {
                date = dFormat.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 将字符串时间转为long
     *
     * @param time
     * @return
     */
    public static long parseDate(String time) {
        return TimeUtil.parseDate(time, DATE_FORMAT_FULL);
    }

    /**
     * long型时间戳转换成日期的方法
     *
     * @param mill
     * @return
     */
    public static String convertDate(long mill) {
        Date date = new Date(mill);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //进行格式化
            return strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取现在月份
     *
     * @return
     */
    public static String getMonth() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); //获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;//获取当前月份;
        String strMonth = "";
        if (mMonth < 10) {
            return "0" + String.valueOf(mMonth);
        } else {
            return String.valueOf(mMonth);
        }
    }

    /**
     * 获取现在的年月日星期
     *
     * @param getType 根据传递的type获取对应的数值 不传表示获取所有
     * @return
     */
    public static String getDate(int getType) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码

        String way = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取现在是星期几
        if ("1".equals(way)) {
            way = "天";
        } else if ("2".equals(way)) {
            way = "一";
        } else if ("3".equals(way)) {
            way = "二";
        } else if ("4".equals(way)) {
            way = "三";
        } else if ("5".equals(way)) {
            way = "四";
        } else if ("6".equals(way)) {
            way = "五";
        } else if ("7".equals(way)) {
            way = "六";
        }

        if (getType == TimeUtil.PROCUREMENT_TYPE_YEAR) {
            return year;
        } else if (getType == TimeUtil.PROCUREMENT_TYPE_MONTH) {
            return month;
        } else if (getType == TimeUtil.PROCUREMENT_TYPE_DAY) {
            return day;
        } else if (getType == TimeUtil.PROCUREMENT_TYPE_WAY) {
            return way;
        } else {
            return year + "年" + month + "月" + day + "日" + "星期" + way;
        }
    }

    public static String getDate() {
        return getDate(0);
    }

    /**
     * 判断是否是闰年
     *
     * @param yearsString 年
     * @return
     */
    public static boolean isLeapYear(String yearsString) {
        int year = Integer.parseInt(yearsString);
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取未来三个月的时间
     *
     * @return
     */
    public static List<String> getDaysOfNextMonths() {

        String year = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_YEAR);
        String month = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_MONTH);
        String day = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_DAY);
        String way = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_WAY);

        if (TextUtils.isEmpty(year) || TextUtils.isEmpty(month) || TextUtils.isEmpty(day) || TextUtils.isEmpty(way)) {
            return null;
        }

        List<String> dayList = new ArrayList<String>();

        List<String> temp = new ArrayList<String>();

        int yearNumber = Integer.parseInt(year);
        int monthNumber = Integer.parseInt(month);

        List<String> daysOfTheMonthList = getDaysOfTheMonth(day, way);//这个月剩余天数
        temp.addAll(daysOfTheMonthList);

        temp = getNextMonth(temp, yearNumber, monthNumber);

        for (int i = 0; i < TimeUtil.MAX_FATALISM; i++) {
            dayList.add(temp.get(i));
        }
        return dayList;
    }


    public static List<String> getNextMonth(List<String> temp, int year, int month) {
        if (month == 12) {
            year++;
            month = 1;
        } else {
            month++;
        }
        List<String> daysOfMonth = TimeUtil.getDaysOfMonth(year, month);
        temp.addAll(daysOfMonth);
        if (temp.size() < TimeUtil.MAX_FATALISM) {
            getNextMonth(temp, year, month);
        }
        return temp;
    }

    /**
     * 获取本月天数
     *
     * @param day
     * @param way
     * @return
     */
    public static List<String> getDaysOfTheMonth(String day, String way) {
        String month = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_MONTH);

        List<String> dayList = new ArrayList<String>();
        int days = getDayOfMonth();//获取这个月的天数

        int dayNumber = Integer.parseInt(day);

        int wayNumber = 0;
        if (way.equals("天")) {
            wayNumber = 1;
        } else if (way.equals("一")) {
            wayNumber = 2;
        } else if (way.equals("二")) {
            wayNumber = 3;
        } else if (way.equals("三")) {
            wayNumber = 4;
        } else if (way.equals("四")) {
            wayNumber = 5;
        } else if (way.equals("五")) {
            wayNumber = 6;
        } else if (way.equals("六")) {
            wayNumber = 7;
        }

        for (int j = dayNumber; j <= days; j++) {
            if (wayNumber == 1) {
                dayList.add(month + "月" + j + "日" + "星期天");
            } else if (wayNumber == 2) {
                dayList.add(month + "月" + j + "日" + "星期一");
            } else if (wayNumber == 3) {
                dayList.add(month + "月" + j + "日" + "星期二");
            } else if (wayNumber == 4) {
                dayList.add(month + "月" + j + "日" + "星期三");
            } else if (wayNumber == 5) {
                dayList.add(month + "月" + j + "日" + "星期四");
            } else if (wayNumber == 6) {
                dayList.add(month + "月" + j + "日" + "星期五");
            } else if (wayNumber == 7) {
                dayList.add(month + "月" + j + "日" + "星期六");
                wayNumber = 0;
            }
            wayNumber++;
        }

        dayList.remove(0);
        dayList.add(0, "今天");
        return dayList;
    }

    /**
     * 根据年月获取对应月份的天数 例:2016年12月01星期四~2016年12月30星期六集合
     *
     * @param year
     * @param month
     * @return
     */
    public static List<String> getDaysOfMonth(int year, int month) {

        String week = getWeek(year, month);
        int wayNumber = 0;
        if (week.equals("星期一")) {
            wayNumber = 2;
        } else if (week.equals("星期二")) {
            wayNumber = 3;
        } else if (week.equals("星期三")) {
            wayNumber = 4;
        } else if (week.equals("星期四")) {
            wayNumber = 5;
        } else if (week.equals("星期五")) {
            wayNumber = 6;
        } else if (week.equals("星期六")) {
            wayNumber = 7;
        } else if (week.equals("星期日")) {
            wayNumber = 1;
        }
        int daysByYearMonth = getDaysByYearMonth(year, month);
        List<String> dayList = new ArrayList<String>();
        for (int i = 1; i <= daysByYearMonth; i++) {
            if (wayNumber == 1) {
                dayList.add(month + "月" + i + "日" + "星期天");
            } else if (wayNumber == 2) {
                dayList.add(month + "月" + i + "日" + "星期一");
            } else if (wayNumber == 3) {
                dayList.add(month + "月" + i + "日" + "星期二");
            } else if (wayNumber == 4) {
                dayList.add(month + "月" + i + "日" + "星期三");
            } else if (wayNumber == 5) {
                dayList.add(month + "月" + i + "日" + "星期四");
            } else if (wayNumber == 6) {
                dayList.add(month + "月" + i + "日" + "星期五");
            } else if (wayNumber == 7) {
                dayList.add(month + "月" + i + "日" + "星期六");
                wayNumber = 0;
            }
            wayNumber++;
        }
        dayList.size();
        return dayList;
    }


    /**
     * 根据日期取得星期几
     */
    public static String getWeek(int year, int month) {
        String monthNumber = "";
        if (month != 11 || month != 12) {
            monthNumber = "0" + month;
        } else {
            monthNumber = month + "";
        }

        String strDate = year + "-" + monthNumber + "-" + "01";// 定义日期字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        Date date = null;
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
            System.out.println("输入的日期格式不合理！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        return week;
    }

    /**
     * 获取当月天数
     *
     * @return
     */
    public static int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 将字符串时间转为long
     *
     * @param time
     * @return
     */
    public static long parseDate(String time, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        try {
            Date date = df.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        String name = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
                + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY)
                + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND);
        return name;
    }

    public static String getCurrentDate() {
        return TimeUtil.formatDate(new Date(), DATE_FORMAT_FULL);
    }

    public static String getCurrentDate(String format) {
        return TimeUtil.formatDate(new Date(), format);
    }

    public static String getDate(Date date, String format) {
        return TimeUtil.formatDate(date, format);
    }

    public static long getCurrentTimeBySeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static Date getUtcDate(Date date) {
        long dateZoneOffset = date.getTime();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        long zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        long dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        // cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        dateZoneOffset = dateZoneOffset + zoneOffset + dstOffset;
        Date newDate = new Date(dateZoneOffset);
        return newDate;
    }

    /**
     * 将秒转为毫秒并返回一个Date
     *
     * @param seconds
     * @return
     */
    public static Date getDateBySeconds(long seconds) {
        return new Date(seconds * 1000);
    }

    /**
     * 是不是白天(早8-晚8算白天,其余算黑夜)
     *
     * @param seconds
     * @return
     */
    public static boolean isTimeSun(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour = sdf.format(TimeUtil.getDateBySeconds(seconds));
        int temp = Integer.valueOf(hour);
        if (temp >= 8 && temp <= 20) {
            return true;
        }
        return false;
    }

    public static boolean isToday(long seconds) {
        Date date = TimeUtil.getDateBySeconds(seconds);
        String time = TimeUtil.formatDate(date, DATE_FORMAT_YYYY_MM_DD);
        Date nowDate = new Date(System.currentTimeMillis());
        String nowTime = TimeUtil.formatDate(nowDate, DATE_FORMAT_YYYY_MM_DD);
        return time.equals(nowTime);
    }

    public static boolean isLastDay(long seconds) {
        Date date = TimeUtil.getDateBySeconds(seconds);
        String time = TimeUtil.formatDate(date, DATE_FORMAT_YYYY_MM_DD);
        Date nowDate = new Date(System.currentTimeMillis() - 24 * 60 * 60
                * 1000);
        String nowTime = TimeUtil.formatDate(nowDate, DATE_FORMAT_YYYY_MM_DD);
        return time.equals(nowTime);
    }

    public static boolean isTomorrowDay(long seconds) {
        Date date = TimeUtil.getDateBySeconds(seconds);
        String time = TimeUtil.formatDate(date, DATE_FORMAT_YYYY_MM_DD);
        Date nowDate = new Date(System.currentTimeMillis() + 24 * 60 * 60
                * 1000);
        String nowTime = TimeUtil.formatDate(nowDate, DATE_FORMAT_YYYY_MM_DD);
        return time.equals(nowTime);
    }

    /**
     * 时间（毫秒）格式化
     *
     * @param timeMs
     * @return
     */
    public static String formatMillTime(long timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder,
                Locale.getDefault());
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 获取时间提示（如果是当天，则显示多少分钟或小时以前；否则显示月日即可）
     *
     * @param ctx
     * @param timeSeconds
     * @return
     */
    public static String getTimeTips(Context ctx, long timeSeconds) {
        return TimeUtil.getTimeTips(ctx, timeSeconds, DATE_FORMAT_MM_DD);
    }

    /**
     * 获取时间提示（如果是当天，则显示多少分钟或小时以前；如果是昨天，则显示昨天 HH:mm；如果是明天，则显示明天
     * HH：mm；否则就显示format格式的时间）
     *
     * @param ctx
     * @param timeSeconds
     * @param format
     * @return
     */
    public static String getTimeTips(Context ctx, long timeSeconds,
                                     String format) {
        Date date = TimeUtil.getDateBySeconds(timeSeconds);
        StringBuilder sb = new StringBuilder();
        if (TimeUtil.isToday(timeSeconds)) {
            final long getTime = date.getTime();
            final long currTime = System.currentTimeMillis();
            // 计算服务器返回时间与当前时间差值
            final long seconds = (currTime - getTime) / 1000;
            final long minute = seconds / 60;
            final long hours = minute / 60;
            if (hours > 0) {
                sb.append(hours);
                sb.append("小时前");
            } else if (minute > 1) {
                sb.append(minute);
                sb.append("分钟前");
            } else {
                sb.append("刚刚");
            }
        } else if (TimeUtil.isLastDay(timeSeconds)) {
            sb.append("昨天");
            sb.append(TimeUtil.getDate(date, DATE_FORMAT_HH_MM));
        } else if (TimeUtil.isTomorrowDay(timeSeconds)) {
            sb.append("明天");
            sb.append(TimeUtil.getDate(date, DATE_FORMAT_HH_MM));
        } else {
            sb.append(TimeUtil.formatDate(date, format));
        }
        return sb.toString();
    }

    /**
     * 根据秒返回分钟数（多算）
     *
     * @param timeSeconds
     * @return
     */
    public static long getTimesBySeconds(long timeSeconds) {
        long temp = timeSeconds % 60;
        long minutes = (timeSeconds / 60);
        if (temp != 0) {
            minutes++;
        }
        return minutes;
    }

    /**
     * 是否在minutes以内
     *
     * @param minutes
     * @param timeSeconds
     * @return
     */
    public static boolean isMinutesLess(int minutes, long timeSeconds) {
        long less = (timeSeconds - System.currentTimeMillis() / 1000) / 60;
        return less <= minutes;
    }

    /**
     * 根据秒返回今明两天时间（如果是今天，则显示多少小时后，或多少分钟前（5分钟内则提示“即将开始”）；如果是明天，则显示明天HH：mm）
     *
     * @param lessMinutes 少于几分钟,变为"即将"
     * @param timeSeconds
     * @return
     */
    public static String getTimeBySeconds(int lessMinutes, long timeSeconds) {
        StringBuilder sb = new StringBuilder();
        if (TimeUtil.isToday(timeSeconds)) {
            Date date = TimeUtil.getDateBySeconds(timeSeconds);
            final long getTime = date.getTime();
            final long currTime = System.currentTimeMillis();
            // 计算服务器返回时间与当前时间差值
            final long seconds = (getTime - currTime) / 1000;
            final long minute = seconds / 60;
            final long hours = minute / 60;
            if (hours > 0) {
                sb.append(hours);
                sb.append("小时后");
            } else if (minute > 0) {
                if (minute <= lessMinutes) {
                    sb.append("即将");
                } else {
                    sb.append(minute);
                    sb.append("分钟后");
                }
            } else {
                sb.append("即将");
            }
            return sb.toString();
        } else if (TimeUtil.isTomorrowDay(timeSeconds)) {
            sb.append("明天");
            sb.append(TimeUtil.formatDate(new Date(timeSeconds * 1000),
                    DATE_FORMAT_HH_MM));
            return sb.toString();
        } else {
            sb.append(TimeUtil.formatDate(new Date(timeSeconds * 1000),
                    DATE_FORMAT_NO_YEARANDSECOND));
            return sb.toString();
        }
    }

    /**
     * 根据秒返回时间（形如dd天hh小时mm分ss秒）
     *
     * @param timeSeconds 秒为单位
     * @return
     */
    public static String getTimeBySeconds(long timeSeconds) {
        StringBuilder sb = new StringBuilder();
        Date date = TimeUtil.getDateBySeconds(timeSeconds);
        final long getTime = date.getTime();
        final long currTime = System.currentTimeMillis();
        // 计算服务器返回时间与当前时间差值
        final long millseconds = (getTime - currTime);
        if (millseconds <= 0) {
            return "00天00小时00分00秒";
        }
        long days = millseconds / DAY_MILLSECONDS;
        sb.append(TimeUtil.formatStr(days));
        sb.append("天");
        long hours = (millseconds - days * DAY_MILLSECONDS) / HOUR_MILLSECONDS;
        long minutes = (millseconds - days * DAY_MILLSECONDS - hours
                * HOUR_MILLSECONDS)
                / MINUTE_MILLSECONDS;
        long second = (millseconds - days * DAY_MILLSECONDS - hours
                * HOUR_MILLSECONDS - minutes * MINUTE_MILLSECONDS) / 1000;
        sb.append(TimeUtil.formatStr(hours));
        sb.append("小时");
        sb.append(TimeUtil.formatStr(minutes));
        sb.append("分");
        sb.append(TimeUtil.formatStr(second));
        sb.append("秒");
        return sb.toString();

    }

    private static String formatStr(long num) {
        if (num >= 10) {
            return num + "";
        } else if (num >= 0) {
            return "0" + num;
        }
        return "";
    }

    /**
     * 根据毫秒获取分钟数（大于30秒算一分钟，否则忽略）
     *
     * @param millseconds
     * @return
     */
    public static long getMimutesByMillseconds(long millseconds) {
        if (millseconds <= 0) {
            return 0l;
        }
        long totalSeconds = millseconds / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        if (seconds >= 30) {
            ++minutes;
        }
        if (hours > 0) {
            minutes += hours * 60;
        }
        return minutes;
    }
}
