package com.you.edu.live.teacher.view.picker;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.utils.TimeUtil;

import java.util.Calendar;
import java.util.regex.Pattern;


/**
 * 时间选择器
 *
 * @author XingRongJing
 */
public class StartLiveSelectorDatePicker extends FrameLayout {

    private static final int INDEX_DAY_TODAY = 0;
    private static final int INDEX_DAY_TOMMOROW = 1;
    private static final int INDEX_MIN_0 = 0;
    private static final int INDEX_MIN_10 = 1;
    private static final int INDEX_MIN_20 = 2;
    private static final int INDEX_MIN_30 = 3;
    private static final int INDEX_MIN_40 = 4;
    private static final int INDEX_MIN_50 = 5;
    private Context mContext;
    private NumberPicker mDayPicker;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinsPicker;
    private Calendar mCalendar;

    //	private String[] mDays = { "今天", "明天" };
    // private String[] mHours = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
    // "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
    // "20", "21", "22", "23" };
    private String[] mMins = {"00", "10", "20", "30", "40", "50"};

    public StartLiveSelectorDatePicker(Context context, String[] mDays) {
        this(context, null, mDays);
    }

    public StartLiveSelectorDatePicker(Context context, AttributeSet attrs, String[] mDays) {
        super(context, attrs);
        mContext = context;
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.view_three_numberpicker, this, true);
        mDayPicker = (NumberPicker) findViewById(R.id.date_day);
        mHoursPicker = (NumberPicker) findViewById(R.id.date_hour);
        mMinsPicker = (NumberPicker) findViewById(R.id.date_min);

        mCalendar = Calendar.getInstance();
        mDayPicker.setMinValue(0);
        mDayPicker.setMaxValue(mDays.length - 1);
        mDayPicker.setDisplayedValues(mDays);
        mDayPicker.setValue(0);

        mHoursPicker.setMinValue(0);
        mHoursPicker.setMaxValue(23);
        // mHoursPicker.setDisplayedValues(mHours);
        mHoursPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        // mHoursPicker.setValue(0);
//        System.out.println(" hour = " + mCalendar.get(Calendar.HOUR_OF_DAY));
        mHoursPicker.setValue(mCalendar.get(Calendar.HOUR_OF_DAY));

        mMinsPicker.setMinValue(0);
        mMinsPicker.setMaxValue(mMins.length - 1);
        mMinsPicker.setDisplayedValues(mMins);
        mMinsPicker.setValue(this.getMinIndexByCurrTime());

    }

    private int getMinIndexByCurrTime() {
        int currMin = mCalendar.get(Calendar.MINUTE);
        if (currMin < 10) {
            return INDEX_MIN_10;
        } else if (currMin < 20) {
            return INDEX_MIN_20;
        } else if (currMin < 30) {
            return INDEX_MIN_30;
        } else if (currMin < 40) {
            return INDEX_MIN_40;
        } else if (currMin < 50) {
            return INDEX_MIN_50;
        } else {
            return INDEX_MIN_0;
        }
    }
    /**
     * 获取选择的时间（秒）
     *
     * @return
     */
    public String getDate() {
        int dayIndex = mDayPicker.getValue();
        String[] displayedValues = mDayPicker.getDisplayedValues();
        String string = displayedValues[dayIndex];
        int minsIndex = mMinsPicker.getValue();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDay(displayedValues[dayIndex]));
        sb.append(" ");
        sb.append(mHoursPicker.getValue());
        sb.append(":");
        sb.append(mMins[minsIndex]);
        return sb.toString();
//		return TimeUtil.parseDate(sb.toString(),
//				TimeUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM) / 1000;
    }

    /**
     * 获取时间轮的时间 转换成秒
     *
     * @return
     */
    public long getTime() {
        String startTime = this.getDate();
        return TimeUtil.parseDate(startTime.toString(),
                TimeUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM) / 1000;
    }

    /**
     * 获取时间轮的时间 例:2016-7-7 15:30
     *
     * @return
     */
    private String getDay(String time) {
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String year_number = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_YEAR);
        String month_number = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_MONTH);
        String day_number = TimeUtil.getDate(TimeUtil.PROCUREMENT_TYPE_DAY);
        sb.append(year_number);
        sb.append("-");
        if (time.contains("今天")) {
            sb.append(month_number);
            sb.append("-");
            sb.append(day_number);
            return sb.toString();
        }

        if (time.contains("月")) {
            String pattern = "月";
            Pattern pat = Pattern.compile(pattern);
            String[] rsr = pat.split(time);
            String month = rsr[0];
            String str = rsr[1];
            sb.append(month);
            sb.append("-");
            if (!TextUtils.isEmpty(str)) {
                if (str.contains("日")) {
                    String pattern1 = "日";
                    Pattern pat1 = Pattern.compile(pattern1);
                    String[] rsr1 = pat1.split(str);
                    String day = rsr1[0];
                    sb.append(day);
                }
            }
        }
        return sb.toString();
    }

    public String getHourAndMims() {
        return mHoursPicker.getValue() + ":" + mMins[mMinsPicker.getValue()];
    }

}
