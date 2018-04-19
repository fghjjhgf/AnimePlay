package com.lb.pachong2.mediasource;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/17.
 */

public class YylepEmptyAnalysis extends BaseMediaAnalysis{
    private static String TAG = YylepEmptyAnalysis.class.getSimpleName();
    private static String mediaurl = null;
    private final String baseURL = "http://www.yylep.com/site/haokan?id=";

    @Override
    public void setMediasource(String sourceurl) {
        LocalLog.log(TAG,"Base start analysisEmptyYylep");
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        String _url = baseURL + sourceurl;
        LocalLog.log(TAG,"_url is " + _url);
        Request request = new Request.Builder()
                .url(_url)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mediaurl = null;
                    LocalLog.log(TAG,"analysisEmptyYylep request fail");
                    mediaResponseCallback.failResponse(mediaurl);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    LocalLog.log(TAG,body);
                    Document doc = Jsoup.parse(body);
                    Elements elements = doc.select("script");

                    Pattern p = Pattern.compile("(?is)var url = \'(.+?)\'");

                    for (Element element : elements){
                        Matcher m = p.matcher(element.html());
                        if (m.find()){
                            LocalLog.log(TAG,"analysisEmptyYylep: var url=" + m.group(1));
                            mediaurl = m.group(1);
                            mediaResponseCallback.sucessResponse(mediaurl);
                            return;
                        }else {

                        }
                    }
                    mediaurl = null;
                    LocalLog.log(TAG,"analysisEmptyYylep not find var url");
                    mediaResponseCallback.failResponse(mediaurl);
                }
            });
        }catch (Exception e){
            mediaurl = null;
            LocalLog.log(TAG,e.getMessage());
            LocalLog.log(TAG,"continue from analysisEmptyYylep Exception");
            mediaResponseCallback.failResponse(mediaurl);
        }
    }

}
