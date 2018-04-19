package com.lb.pachong2.mediasource;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.parser.MaoyunMediaParser;
import com.lb.pachong2.util.LocalLog;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/16.
 */

public class MaoyunAnalysis extends BaseMediaAnalysis{
    private static String TAG = MaoyunAnalysis.class.getSimpleName();
    private static String baseurl = "http://www.maoyun.tv/mdparse/index.php?id=";
    private static String mediaurl = null;


    public MaoyunAnalysis(){}

    /**
    *@param url meadia is the sourceUrl
    *
    * */
    @Override
    protected void setMediasource (String url){
        LocalLog.log(TAG,"start analysisMaoyun");
        final String murl = url;
        String targeturl = baseurl + url;
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
                    LocalLog.log(TAG,"analysisMaoyun request fail");

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
                                .add("id", murl)
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
                                mediaurl = null;
                                LocalLog.log(TAG,e.getMessage());
                                mediaResponseCallback.failResponse(mediaurl);
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
                                    mediaResponseCallback.sucessResponse(mediaurl);
                                }catch (Exception e){
                                    mediaurl = null;
                                    LocalLog.log(TAG,e.getMessage());
                                    mediaResponseCallback.failResponse(mediaurl);
                                }
                            }
                        });
                    }
                }
            });

        }catch (Exception e){
            mediaurl = null;
            LocalLog.log(TAG,e.getMessage());
            mediaResponseCallback.failResponse(mediaurl);
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
    * from tools.js?ver=20171118
    *function sign(str){
    *    var abc = md5(str+'!abef987');
    *    var _a = abc.substring(10,22);
    *    var _b = abc.substring(24,30);
    *    return 'ab59' + _b + '8ab5d6' + _a + 'loij';
    *    }
    *
    * */
    private static String getMD5(String md5){
        String abc = encode(md5 + "!abef987");
        String _a = abc.substring(10,22);
        String _b = abc.substring(24,30);
        return "ab59" + _b + "8ab5d6" + _a + "loij";
    }

    private static String encode(String string)  {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            String retmd5 = hexString.toString();
            LocalLog.log(TAG,"md5: " + retmd5);
            return retmd5;
        }catch (Exception e){
            LocalLog.log(TAG,e.getMessage());
        }
        return null;
    }
}
