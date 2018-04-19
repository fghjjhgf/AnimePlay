package com.lb.pachong2.mediasource;

/**
 * Created by Administrator on 2018/4/16.
 */

public interface MediaResponseCallback {
    void sucessResponse(String mediaurl);
    void failResponse(String msg);
}
