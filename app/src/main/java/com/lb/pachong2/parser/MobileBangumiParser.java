package com.lb.pachong2.parser;

import com.lb.pachong2.util.LocalLog;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Administrator on 2018/3/7.
 */

public class MobileBangumiParser {

    public static String getBangumiList(String body){
        LocalLog.log("getBangumiList body :", body);
        Document doc = Jsoup.parse(body);
        JSONArray jsonArray = new JSONArray();
        Elements els = doc.getElementsByClass("episodeWrap show9");
        for (Element e1 : els){
            Elements links = e1.getElementsByTag("li");
            for (Element link : links){
                Elements alinks = link.getElementsByTag("a");
                JSONArray ja = new JSONArray();
                String episodeName = "";
                String episodeLinks = "";
                for (Element a : alinks){
                    episodeName = a.text();
                    episodeLinks = a.attr("href");
                    ja.put(episodeName);
                    ja.put(episodeLinks);
                    jsonArray.put(ja);
                    LocalLog.log("getBangumiList", jsonArray.toString());
                }
            }

        }

        jsonArray.remove(jsonArray.length()-1);
        return jsonArray.toString();
    }

    public static String getBangumiName(String body){
        Document doc = Jsoup.parse(body);
        Elements els = doc.getElementsByClass("txt fr");
        for (Element el : els){
            Elements tags = el.getElementsByTag("h1");
            for (Element tag : tags){
                return tag.text();
            }
        }
        return "no name";
    }

    public static String getBangumiMainImageURL(String body){
        Document doc = Jsoup.parse(body);
        Elements els = doc.getElementsByClass("img fl");
        for (Element el : els){
            Elements imgs = el.getElementsByTag("imag");
            for (Element img : imgs){
                LocalLog.log("getBangumiMainImageURL",img.attr("src"));
                return img.attr("src");
            }
        }
        LocalLog.log("getBangumiMainImageURL","no url");
        return "no url";
    }

    public static String getPreEpisodeURL(String body){
        Document doc = Jsoup.parse(body);
        Elements els = doc.getElementsByClass("info-left");
        for (Element el : els){
            Elements as = el.getElementsByTag("a");
            for (Element a : as){
                return a.attr("href");
            }
        }
        return null;
    }

    public static String getNextEpisodeURL(String body){
        Document doc = Jsoup.parse(body);
        Elements els = doc.getElementsByClass("info-right");
        for (Element el : els){
            Elements as = el.getElementsByTag("a");
            for (Element a : as){
                return a.attr("href");
            }
        }
        return null;
    }
}
