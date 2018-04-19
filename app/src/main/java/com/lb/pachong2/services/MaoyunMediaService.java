package com.lb.pachong2.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.parser.MaoyunMediaParser;
import com.lb.pachong2.util.LocalLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/15.
 */

public class MaoyunMediaService extends Service {
    private static String TAG = "MaoyunMediaService";
    public static String EMPTYURL = "EMPTYSTR";
    private boolean connecting = false;
    private static Callback callback;
    private String testurl = "http://vd3.bdstatic.com/mda-ic9y8kptcq7ip6mv/mda-ic9y8kptcq7ip6mv.mp4";
    private static String[] playerListUrl = {"http://www.maoyun.tv/mdparse/index.php?id=","http://jx.yylep.com/?url=","https://jx.maoyun.tv/d/?id="};
    private static String[] uselessURL = {"http://dilidili.97kn.com/?url="};
    private static String mediaurl = EMPTYURL;
    private static String url = "";
    private static String handleStr = "";
    private static String sourceURL = null;

    public final static String MSG_EMPTY = "MSG_EMPTY";
    public final static String MSG_NOTEMPTY = "MSG_NOTEMPTY";
    public final static String MSG_HANDLE = "MSG_HANDLE";
    public final static String MSG_BANGUMIURL = "MSG_BANGUMIURL";

    private static String body = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LocalLog.log(TAG,"bind success");
        url = intent.getStringExtra(MSG_BANGUMIURL);
        handleStr = intent.getStringExtra(MSG_HANDLE);
        return new Binder();
    }

    public class Binder extends android.os.Binder{
        public MaoyunMediaService getService(){
            return MaoyunMediaService.this;
        }
    }

    public static void setHandle(Intent intent){
        url = intent.getStringExtra(MSG_BANGUMIURL);
        handleStr = intent.getStringExtra(MSG_HANDLE);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        connecting = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                LocalLog.log(TAG,"start service");
                while (connecting == true ){
                    if(callback != null && handleStr.equals(MSG_NOTEMPTY) ){
                        sourceURL = null;
                        LocalLog.log(TAG,"set callback");
                        //callback.onDataGet(testurl);
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                LocalLog.log(TAG,url + " fail to request");
                                body = null;
                                mediaurl = EMPTYURL;
                                callback.onDataGet(mediaurl);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                LocalLog.log(TAG,url + " success request");
                                body = response.body().string();
                                getMediaSourcePaser(body);

                            }
                        });
                        handleStr = MSG_EMPTY;
                    }
                }
            }
        }).start();
    }

    public static String getBody(){ return body;}

    public void getMediaSourcePaser(String body){

        sourceURL = getSourceUrl(body);
        String tsourceURL = "";
        mediaurl = EMPTYURL;
        if (sourceURL != null){
            String end = sourceURL.substring(sourceURL.length()-4);
            if (end.equals(".mp4")){
                LocalLog.log("getMediaSourcePaser","the var url is a mp4");
                //return sourceURL;
                callback.onDataGet(sourceURL);
                return;
            }else {
                if (sourceURL.indexOf("www.yylep.com") != -1){
                    LocalLog.log(TAG," getMediaSourcePaser : the var url is domain of www.yylep.com");
                    analysisEmptyYylep(sourceURL);
                }else {
                    analysisMaoyun(sourceURL);
                }
            }
        }else {
            LocalLog.log("Not find var sourceUrl");
            callback.onDataGet(mediaurl);
        }
    }

    /*
   * direct url = "http://www.maoyun.tv/mdparse/url.php?xml=http://tw.iqiyi.com/v_19rrbtc5co.html&md5=ab5933fa068ab5d655c3b7dfdcd9loij&type=auto&hd=cq&wap=0&siteuser=&lg=&cip=cq&iqiyicip=cq"
   * */
    private static void analysisMaoyun (String url){
        LocalLog.log(TAG,"start analysisMaoyun");
        final String murl = url;
        String targeturl = playerListUrl[0] + url;
        LocalLog.log(TAG, "targeturl: "+targeturl);
        final OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                .url(targeturl)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mediaurl = EMPTYURL;
                    LocalLog.log(TAG,"analysisMaoyun request fail");
                    analysisYylep(sourceURL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String baseurl = "http://www.maoyun.tv/mdparse/url.php";
                    String body = response.body().string();
                    LocalLog.log(TAG,"body:\n" + body);
                    Document document = Jsoup.parse(body);
                    Element element = document.getElementById("hdMd5");
                    LocalLog.log(TAG,"hdMd5: " + element.toString());
                    String baseMD5 = element.attr("value");
                    String _md5 = getMD5valueofChange(body);
                    LocalLog.log(TAG,"decode set md5: " + _md5);
                    if (_md5 != null){
                        LocalLog.log(TAG,"baseMD5: " + baseMD5);
                        String signMD5 = MaoyunMediaParser.getMD5(_md5);
                        LocalLog.log(TAG,"signMD5: " + signMD5);

                        FormBody formBody = new FormBody.Builder()
                                .add("id", sourceURL)
                                .add("type", "auto")
                                .add("siteuser","")
                                .add("md5",signMD5)
                                .add("hd","")
                                .add("lg","")
                                .build();

                        Request request1 = new Request.Builder()
                                .url(baseurl)
                                .post(formBody)
                                .build();

                        LocalLog.log(TAG,"request1: " + request1.toString());

                        Call call1 = okHttpClient.newCall(request1);
                        call1.enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                mediaurl = EMPTYURL;
                                LocalLog.log(TAG,e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responsebody = response.body().string();
                                LocalLog.log(TAG,"responsebody: " + responsebody);
                                try{
                                    JSONObject jsonObject = new JSONObject(responsebody);
                                    String jsonurl = jsonObject.getString("url");
                                    mediaurl = URLDecoder.decode(jsonurl,"UTF-8");
                                    LocalLog.log(TAG,mediaurl);
                                    callback.onDataGet(mediaurl);
                                }catch (Exception e){
                                    mediaurl = EMPTYURL;
                                    LocalLog.log(TAG,e.getMessage());
                                }
                            }
                        });
                    }else {
                        analysisYylep(sourceURL);
                    }
                }
            });

        }catch (Exception e){
            LocalLog.log(TAG,e.getMessage());
            analysisYylep(sourceURL);
        }
    }

    /*
    * TEST URL http://www.maoyun.tv/mdparse/index.php?id=http://www.iqiyi.com/v_19rrbe17uo.html
    * */
    private static String getMD5valueofChange(String body){
        int start = body.indexOf("\\x");
        String _16X = body.substring(start,start+208);
        LocalLog.log(TAG,"16DEX: " + _16X);
        _16X = _16X.replace("\\x","");
        String decodestr = toStringHex1(_16X);
        LocalLog.log(TAG,decodestr);
        String _md5 = decodestr.substring(17,17+32);
        return _md5;
    }

    // 转化十六进制编码为字符串
    public static String toStringHex1(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                LocalLog.log(TAG,"toStringHex1 e :" + e.getMessage());
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            LocalLog.log(TAG,"toStringHex1 e1 :" + e1.getMessage());
        }
        return s;
    }
    /*
    * test url = http://m.dilidili.wang/watch3/62753/
    *
    * */
    private static void analysisYylep(String url) {
        LocalLog.log(TAG,"start analysisYylep");
        String baseURL = "http://jx.yylep.com/vod/jx?";
        String turl = baseURL + url;
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
                    mediaurl = EMPTYURL;
                    LocalLog.log(TAG,"analysisYylep request fail");
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
                            callback.onDataGet(mediaurl);
                        } catch (JSONException e) {
                            LocalLog.log(TAG,e.getMessage());
                            mediaurl = EMPTYURL;
                            callback.onDataGet(mediaurl);
                        }
                    }
                }
            });

        }catch (Exception e){
            mediaurl = EMPTYURL;
            LocalLog.log(TAG,e.getMessage());
            callback.onDataGet(mediaurl);
        }
    }

    private static void analysisJxMaoyun(String url){
    }

    /*
    * test url = "http://m.dilidili.wang/watch3/62768/" in dilidili
    * direct test url = "https://www.yylep.com/f-877-h5/8c9b67e2.html?pan=ty"
    *
    * */
    private static void analysisEmptyYylep (String url) {
        LocalLog.log(TAG,"start analysisEmptyYylep");
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        Request request = new Request.Builder()
                //.addHeader("referer","http://m.dilidili.wang/")
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mediaurl = EMPTYURL;
                    LocalLog.log(TAG,"analysisEmptyYylep request fail");
                    analysisMaoyun(sourceURL);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements elements = doc.select("script");

                    Pattern p = Pattern.compile("(?is)var url=\'(.+?)\'");

                    for (Element element : elements){
                        Matcher m = p.matcher(element.html());
                        if (m.find()){
                            LocalLog.log(TAG,"analysisEmptyYylep: var url=" + m.group(1));
                            mediaurl = m.group(1);
                            callback.onDataGet(mediaurl);
                        }else {
                            mediaurl = EMPTYURL;
                            LocalLog.log(TAG,"analysisEmptyYylep not find var url");
                            analysisMaoyun(sourceURL);
                        }
                    }
                }
            });
        }catch (Exception e){
            mediaurl = EMPTYURL;
            LocalLog.log(TAG,e.getMessage());
            LocalLog.log(TAG,"continue from analysisEmptyYylep Exception");
            analysisMaoyun(sourceURL);
        }
    }

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


    public void setCallback(Callback callback){
        this.callback = callback;

    }

    public static interface Callback{
        void onDataGet(String data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        connecting = false;
    }

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return ciphertext 密文
     */
    private static String encrypt(String plaintext) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = plaintext.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
