package com.lb.pachong2.mediasource;

import com.lb.pachong2.ControlApplication;
import com.lb.pachong2.util.LocalLog;

import java.io.IOException;
import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/17.
 */

public class JxMaoyunAnalysis extends BaseMediaAnalysis {
    private static String TAG = JxMaoyunAnalysis.class.getSimpleName();
    private final static String baseurl = "https://jx.maoyun.tv/?id=";
    private final static String mediabaseurl = "https://jx.maoyun.tv/url.php?";

    @Override
    protected void setMediasource(String sourceurl) {
        final String _sourceurl = sourceurl;
        LocalLog.log(TAG,"start JxMaoyunAnalysis");
        String targeturl = baseurl + sourceurl;
        OkHttpClient okHttpClient = ControlApplication.getmOkHttpClient();
        final Request request = new Request.Builder()
                .url(targeturl)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LocalLog.log(TAG,e.getMessage());
                mediaResponseCallback.failResponse("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                String _md5 = getMD5valueofChange(body);
                String signMD5 = getMD5(_md5);
                LocalLog.log(TAG,"signMD5 is " + signMD5);

                String targeturl = mediabaseurl + "xml=" + _sourceurl + "&md5=" + signMD5 + "&type=auto&hd=cq&wap=0&siteuser=&lg=&cip=cq&iqiyicip=cq";
                LocalLog.log(TAG,"mediaurl is " + targeturl);
                mediaResponseCallback.sucessResponse(targeturl);

            }
        });
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
