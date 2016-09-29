package com.you.edu.live.teacher.view.picker;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.you.edu.live.teacher.R;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class LiveLengthSelectorDatePicker extends FrameLayout {

    private String[] mMins = {"15分钟", "20分钟", "30分钟", "45分钟", "60分钟",
            "90分钟", "120分钟", "150分钟", "3小时", "4小时", "6小时", "8小时"};

    private Context mContext;
    private NumberPicker mTimePicker;

    public LiveLengthSelectorDatePicker(Context context) {
        this(context, null);
    }

    public LiveLengthSelectorDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.view_one_numberpicker, this, true);
        mTimePicker = (NumberPicker) findViewById(R.id.numberpicker_mins);
        mTimePicker.setMinValue(0);
        mTimePicker.setMaxValue(mMins.length - 1);
        mTimePicker.setDisplayedValues(mMins);
        mTimePicker.setValue(0);
    }

    /**
     * 获取直播时长 例：15分钟
     * @return
     */
    public String getLiveLengthDate(){
        int index = mTimePicker.getValue();
        String[] displayedValues = mTimePicker.getDisplayedValues();
        return displayedValues[index];
    }
    /**
     * 获取直播时长 秒
     */
    public int getLiveLengthTime() {
        int index = mTimePicker.getValue();
        String[] displayedValues = mTimePicker.getDisplayedValues();
        String string = displayedValues[index];
        if (string.contains("小时")) {
            String pattern1 = "小";
            Pattern pat1 = Pattern.compile(pattern1);
            String[] rsr1 = pat1.split(string);
            String s = rsr1[0];
            if (!TextUtils.isEmpty(s)) {
                int hour = Integer.parseInt(s);
                return hour * 3600;
            }
        }else if(string.contains("分钟")){
            String pattern1 = "分";
            Pattern pat1 = Pattern.compile(pattern1);
            String[] rsr1 = pat1.split(string);
            String s = rsr1[0];
            if (!TextUtils.isEmpty(s)) {
                int min = Integer.parseInt(s);
                return min * 60;
            }
        }
        return 0;
    }
}
