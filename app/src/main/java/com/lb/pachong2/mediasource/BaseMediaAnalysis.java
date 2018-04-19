package com.lb.pachong2.mediasource;

import android.util.Log;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.util.LocalLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/17.
 */

public class BaseMediaAnalysis {
    private static String TAG = BaseMediaAnalysis.class.getSimpleName();
    private static String prebangumiurl = null;
    private static String body = null;
    private static String sourceURL = null;

    protected MediaResponseCallback mediaResponseCallback = new MediaResponseCallback() {
        @Override
        public void sucessResponse(String mediaurl) {

        }

        @Override
        public void failResponse(String msg) {

        }
    };

    public void getMediaSource(String bangumiurl){
        if (bangumiurl.equals(prebangumiurl)){
            setMediasource(sourceURL);
            return;
        }
        prebangumiurl = bangumiurl;

        final Request request = new Request.Builder()
                .url(bangumiurl)
                .build();
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LocalLog.log(TAG,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                body = response.body().string();
                sourceURL = getSourceUrl(body);
                setMediasource(sourceURL);
            }
        });
    }

    protected void setMediasource(String sourceurl){

    }

    public void setMediaResponseCallback(MediaResponseCallback mediaResponseCallback){
        this.mediaResponseCallback = mediaResponseCallback;
    }


    /**
     * @return source url
     * */
    private static String getSourceUrl(String body){
        try{
            Log.d("getSourceUrl bodu ", body);
            Document doc = Jsoup.parse(body);
            Elements elements = doc.select("script");

            Log.d("getSourceUrl elements: ",elements.toString());

            Pattern p = Pattern.compile("(?is)var sourceUrl = \"(.+?)\"");

            for (Element element : elements){
                LocalLog.log("getSourceUrl element: " + element.toString());
                Matcher m = p.matcher(element.html());
                if (m.find()){
                    LocalLog.log("MediaSourceParser: ","var sourceUrl =  " + m.group(1));
                    String str = m.group(1);
                    str = str.replace("https//","http://");
                    return str;
                }
            }
            return null;
        }catch (Exception e){
            LocalLog.log(TAG,e.getMessage());
        }
        return null;
    }
}
