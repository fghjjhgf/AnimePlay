package com.lb.pachong2.network;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by Administrator on 2018/3/10.
 */

public interface ResponeCallBack {
    void requestFail(Request request, IOException e);
    void requestSuccess(String result);
}