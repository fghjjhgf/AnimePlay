package com.lb.pachong2.util;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/3/7.
 */

public class TimeUtil {
    public static void getTime(){
        //获取系统的日期
        //Calendar calendar = Calendar.getInstance();
        //年
        //int year = calendar.get(Calendar.YEAR);
        //月
        //int month = calendar.get(Calendar.MONTH);
        //日
        //int day = calendar.get(Calendar.DAY_OF_MONTH);
        //获取系统时间
        //小时
        //int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        //int minute = calendar.get(Calendar.MINUTE);
        //秒
        //int second = calendar.get(Calendar.SECOND);
    }

    public static int getWeekDay(){
        Calendar calendar = Calendar.getInstance();

        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        return week == 0 ? 7 : week;
    }
}
