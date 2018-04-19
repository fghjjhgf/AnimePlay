package com.lb.pachong2.network;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.parser.AnimeMobileListParser;
import com.lb.pachong2.parser.MediaSourceParser;
import com.lb.pachong2.parser.MobileBangumiParser;
import com.lb.pachong2.util.LocalLog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/4.
 */

public class Network {
    private static final String TAG = Network.class.getSimpleName();
    private static OkHttpClient mOkHttpClient;
    public static String EMPTYSTR = "EMPTYSTR";
    private final static String mobileDomain = "http://m.dilidili.wang/";
    private static ResponeCallBack mainResponeCallBack;
    private static ResponeCallBack bangumiResponeCallBack;
    private static ResponeCallBack mediaSourceResponeCallBack;
    private static ResponeCallBack breifVideoResponeCallBack;
    private static Handler handler = new Handler();

    public Network(){}

    public static void getMobileAnimeListString(@NonNull Context context){
        mOkHttpClient = getmOkHttpClient();
        final Request request = new Request.Builder()
                .url(mobileDomain)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "fail to load MobileDomain ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mainResponeCallBack != null){
                            mainResponeCallBack.requestFail(request,e);
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LocalLog.log(TAG + " onResponse: " + str);
                        final String result = AnimeMobileListParser.getAnimeList(str);
                        LocalLog.log(result);
                        if (mainResponeCallBack != null){
                            mainResponeCallBack.requestSuccess(result);
                        }
                    }
                });
            }
        });
    }

    public static void setMainRequestCallBack(ResponeCallBack mrequestCallBack){
        mainResponeCallBack = mrequestCallBack;
    }

    public static void getEpisodeMobileList(String url){
        mOkHttpClient = getmOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "fail to load MobileDomain ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bangumiResponeCallBack != null){
                            bangumiResponeCallBack.requestFail(request,e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("getEpisodeMobileList", str);
                        final String result = MobileBangumiParser.getBangumiList(str);
                        LocalLog.log("getEpisodeMobileList",result);
                        if (bangumiResponeCallBack != null){
                            bangumiResponeCallBack.requestSuccess(result);
                        }
                    }
                });
            }
        });
    }

    public static void getBreifVideoMobileList(String url){
        mOkHttpClient = getmOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "fail to load MobileDomain ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (breifVideoResponeCallBack != null){
                            breifVideoResponeCallBack.requestFail(request,e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("getBreifVideoMobileList", str);
                        final String result = MobileBangumiParser.getBangumiList(str);
                        LocalLog.log("getBreifVideoMobileList",result);
                        if (breifVideoResponeCallBack != null){
                            breifVideoResponeCallBack.requestSuccess(result);
                        }
                    }
                });
            }
        });
    }

    public static void getEpisodeSource(String url){
        mOkHttpClient = getmOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "fail to load MobileDomain ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaSourceResponeCallBack != null){
                            mediaSourceResponeCallBack.requestFail(request,e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("getEpisodeSource body ", str);
                        String ret = MediaSourceParser.getMediaSourcePaser(str);
                        if (ret == null){
                            LocalLog.log("getEpisodeSource:","no find");
                            ret = EMPTYSTR;
                        }
                        final String result = ret;
                        if (mediaSourceResponeCallBack != null){
                            mediaSourceResponeCallBack.requestSuccess(result);
                        }
                    }
                });
            }
        });
    }

    private static OkHttpClient getmOkHttpClient() {
        return ControlApplication.getmOkHttpClient();
    }

    public static void setBangumiResponeCallBack(ResponeCallBack mrequestCallBack){
        bangumiResponeCallBack = mrequestCallBack;
    }

    public static void setBreifVideoResponeCallBack(ResponeCallBack mrequestCallBack){
        breifVideoResponeCallBack = mrequestCallBack;
    }

    public static void setMediaSourceResponeCallBack(ResponeCallBack mrequestCallBack) {
        mediaSourceResponeCallBack = mrequestCallBack;
    }
}
