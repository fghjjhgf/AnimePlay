package com.lb.pachong2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/3/10.
 */

public class ControlApplication {

    private static OkHttpClient mOkHttpClient;
    private static CookieJar mCookieJar;

    public static int getActiveNetworkType(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getType();
        } else {
            return -1;
        }
    }

    public static OkHttpClient getmOkHttpClient(){
        if (mOkHttpClient == null) {

            mCookieJar = new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            };
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .cookieJar(mCookieJar)
                    .build();
        }

        return mOkHttpClient;
    }
}
