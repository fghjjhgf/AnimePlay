package com.lb.pachong2.parser;

import com.lb.pachong2.util.LocalLog;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2018/3/14.
 */

public class MaoyunMediaParser {
    private static String TAG = "MaoyunMediaParser";
    private static String domain = "http://www.maoyun.tv/mdparse/index.php?id=";

    public static String getSourceURL(String url){

        return null;
    }

    private static String getmedia1(){
        return null;
    }

    private static String getmedia2(){
        return null;
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
    public static String getMD5(String md5){
        String abc = encode(md5 + "!abef987");
        String _a = abc.substring(10,22);
        String _b = abc.substring(24,30);
        return "ab59" + _b + "8ab5d6" + _a + "loij";
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
            LocalLog.log(TAG,"md5: " + str.toString());
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encode(String string)  {
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
