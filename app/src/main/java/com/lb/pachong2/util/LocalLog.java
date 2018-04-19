package com.lb.pachong2.util;

import android.util.Log;

/**
 * Created by Administrator on 2018/3/4.
 */

public class LocalLog {
    public static void log(String tag, String log){
        Log.d(tag,log);
    }

    public static void log(String log){
        Log.d("LocalLog", log);
    }

    public static void log(){

    }
}
