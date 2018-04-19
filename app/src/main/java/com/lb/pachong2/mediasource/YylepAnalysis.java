package com.lb.pachong2.mediasource;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.util.LocalLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/16.
 */

public class YylepAnalysis extends BaseMediaAnalysis {
    private static String TAG = YylepAnalysis.class.getSimpleName();
    private static String mediaurl = null;

    @Override
    public void getMediaSource(String bangumiurl) {
        super.getMediaSource(bangumiurl);
        LocalLog.log(TAG,"start analysisYylep");
        String baseURL = "http://jx.yylep.com/vod/jx?";
        String turl = baseURL + bangumiurl;
        LocalLog.log(TAG,turl);
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                .url(turl)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mediaurl = null;
                    LocalLog.log(TAG,"analysisYylep request fail");
                    mediaResponseCallback.failResponse(mediaurl);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document document = Jsoup.parse(response.body().string());
                    Elements elements = document.getElementsByTag("pre");
                    for (Element element : elements){
                        String jsonstr = element.html();
                        try{
                            JSONObject jsonObject = new JSONObject(jsonstr);
                            String ss = jsonObject.getString("data");
                            mediaurl =   "http:" + ss;
                            mediaResponseCallback.sucessResponse(mediaurl);
                        } catch (JSONException e) {
                            LocalLog.log(TAG,e.getMessage());
                            mediaurl = null;
                            mediaResponseCallback.failResponse(mediaurl);
                        }
                    }
                }
            });

        }catch (Exception e){
            mediaurl = null;
            LocalLog.log(TAG,e.getMessage());
            mediaResponseCallback.failResponse(mediaurl);
        }
    }

    /**
    * @param url url is sourceurl
    * */
    public void analysisYylep(String url) {

    }

}
