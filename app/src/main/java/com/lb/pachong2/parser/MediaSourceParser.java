package com.lb.pachong2.parser;

import android.util.Log;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.util.LocalLog;

import org.json.JSONException;
import org.json.JSONObject;
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
 * Created by Administrator on 2018/3/10.
 */

public class MediaSourceParser {

    private static String[] playerListUrl = {"http://www.maoyun.tv/mdparse/index.php?id=","http://jx.yylep.com/?url=","https://jx.maoyun.tv/d/?id="};

    private static String[] uselessURL = {"http://dilidili.97kn.com/?url="};

    public static String getMediaSourcePaser(String body){
        try{
            String sourceURL = getSourceUrl(body);
            String tsourceURL = "";
            if (sourceURL != null){
                String end = sourceURL.substring(sourceURL.length()-4);
                if (end.equals(".mp4")){
                    LocalLog.log("getMediaSourcePaser","the var url is a mp4");
                    return sourceURL;
                }else {
                    if (sourceURL.indexOf("www.yylep.com") != -1){
                        LocalLog.log("getMediaSourcePaser","the var url is domain of www.yylep.com");
                        tsourceURL = analysisEmptyYylep(sourceURL);
                        if (tsourceURL != null){
                            LocalLog.log("getMediaSourcePaser","the sourceURL is " + tsourceURL);
                            return tsourceURL;
                        }else {
                            tsourceURL = analysisMaoyun(sourceURL);
                            if (tsourceURL != null){
                                return tsourceURL;
                            }else {
                                tsourceURL = analysisYylep(sourceURL);
                                if (tsourceURL != null) {
                                    return tsourceURL;
                                }else {
                                    return null;
                                }
                            }
                        }
                    }else {
                        tsourceURL = analysisMaoyun(sourceURL);
                        if (tsourceURL != null){
                            return tsourceURL;
                        }else {
                            tsourceURL = analysisYylep(sourceURL);
                            if (tsourceURL != null) {
                                return tsourceURL;
                            }else {
                                return null;
                            }
                        }
                    }
                }
            }else {
                LocalLog.log("Not find var sourceUrl");
                return null;
            }
        }catch (Exception e){
            LocalLog.log(e.toString());
            return null;
        }
    }

    /*
    * direct url = "http://www.maoyun.tv/mdparse/url.php?xml=http://tw.iqiyi.com/v_19rrbtc5co.html&md5=ab5933fa068ab5d655c3b7dfdcd9loij&type=auto&hd=cq&wap=0&siteuser=&lg=&cip=cq&iqiyicip=cq"
    * */
    private static String analysisMaoyun (String url)  throws Exception{
        LocalLog.log("start analysisMaoyun");
        String baseurl = "http://www.maoyun.tv/mdparse/url.php?";
        String targeturl = playerListUrl[0] + url;
        LocalLog.log("targeturl",targeturl);
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                .url(targeturl)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            Response response = call.execute();
            Document document = Jsoup.parse(response.body().string());
            Element element = document.getElementById("hdMd5");
            String baseMD5 = element.attr("value");
            String turl = baseurl + "xml=" + url + "&md5="+ baseMD5 + "&type=auto&hd=cq&wap=0&siteuser=&lg=&cip=cq&iqiyicip=cq";
            LocalLog.log("turl : " + turl);
            Request request1 = new Request.Builder()
                    .url(turl)
                    .build();
            Call call1 = okHttpClient.newCall(request1);
            Response response1 = call1.execute();
            if (response1.isSuccessful()){
                return turl;
            }else {
                return null;
            }
        }catch (IOException e){
            LocalLog.log(e.toString());
            return null;
        }
    }

    /*
    * test url = http://m.dilidili.wang/watch3/62753/
    *
    * */
    private static String analysisYylep(String url)  throws Exception {
        LocalLog.log("start analysisYylep");
        String baseURL = "http://jx.yylep.com/vod/jx?";
        String turl = baseURL + url;
        LocalLog.log(turl);
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                .url(turl)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            Response response = call.execute();
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.getElementsByTag("pre");
            for (Element element : elements){
                String jsonstr = element.html();
                try{
                    JSONObject jsonObject = new JSONObject(jsonstr);
                    String ss = jsonObject.getString("data");
                    return  "http:" + ss;
                } catch (JSONException e) {
                    LocalLog.log(e.toString());
                    return null;
                }
            }
        }catch (IOException e){
            LocalLog.log(e.toString());
            return null;
        }
        return null;
    }

    private static String analysisJxMaoyun(String url){
        return null;
    }

    /*
    * test url = "http://m.dilidili.wang/watch3/62768/" in dilidili
    * direct test url = "https://www.yylep.com/f-877-h5/8c9b67e2.html?pan=ty"
    *
    * */
    private static String analysisEmptyYylep (String url)  throws Exception{
        LocalLog.log("start analysisEmptyYylep");
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                //.addHeader("referer","http://m.dilidili.wang/")
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            Response response = call.execute();
            Document doc = Jsoup.parse(response.body().string());
            Elements elements = doc.select("script");

            Pattern p = Pattern.compile("(?is)var url=\'(.+?)\'");

            for (Element element : elements){
                Matcher m = p.matcher(element.html());
                if (m.find()){
                    LocalLog.log("analysisEmptyYylep: ","var url=" + m.group(1));
                    return m.group(1);
                }else {
                    LocalLog.log(url);
                    LocalLog.log("not find var url");
                    return null;
                }
            }

        }catch (IOException e){
            LocalLog.log(e.toString());
            return null;
        }
        return null;
    }

    private static String getSourceUrl(String body) throws Exception{
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
    }
}
