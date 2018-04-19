package com.lb.pachong2.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/1/22.
 */
public class GetHtml {

    private String HostURL = "http://m.dilidili.wang/";

    public String gethtml(){
        try{
            URL url = new URL(HostURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("Upgrade-Insecure-Requests","1");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            con.connect();

            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "connect fail";
    }

    public String gethtml(String urlstr){
        try{
            URL url = new URL(urlstr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("Upgrade-Insecure-Requests","1");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            con.connect();

            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "connect fail";
    }


}


