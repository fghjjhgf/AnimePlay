package com.lb.pachong2.parser;

import com.lb.pachong2.util.LocalLog;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/3/4.
 */

public class AnimeMobileListParser {

    private static String mobiledomain = "http://m.dilidili.wang/";

    public static String getAnimeList(String body){
        return getWeekNum(body);
    }

    private static String getWeekNum(String body){
        JSONArray jsonArray = new JSONArray();
        Document doc = Jsoup.parse(body);
        for(int i = 1;i < 8; i++){
            //every loop to get a day's anime list
            JSONArray ja = new JSONArray();
            String weekid = "weekdiv" + i;
            Element el = doc.getElementById(weekid);
            LocalLog.log(weekid + el.toString());
            if (el != null){
                Elements links = el.getElementsByClass("week_item");

                if (links != null){
                    // every week_item is a value if anime
                    for (Element element : links){
                        String animename = "";
                        String animelink = "";
                        String episodename = "";
                        String episodelink = "";

                        Elements week_item_lefts = element.getElementsByClass("week_item_left");
                        if (week_item_lefts != null){
                            for (Element week_item_left : week_item_lefts){
                                Elements as = week_item_left.getElementsByTag("a");
                                LocalLog.log("a link \n" + as.toString());
                                for (Element a : as){
                                    //the value of class is week_item_left
                                    animename = a.text();
                                    animelink = a.attr("href");
                                    LocalLog.log(TAG + " get week_item_left: " + animename + "  " + animelink);
                                }
                            }
                        }else {
                            LocalLog.log("class week_item_left is not exist");
                        }
                        Elements week_item_rights = element.getElementsByClass("week_item_right");
                        if (week_item_rights != null){
                            for (Element week_item_right : week_item_rights){
                                Elements as = week_item_right.getElementsByTag("a");
                                for (Element a : as){
                                    //the value of class is week_item_right
                                    episodename = a.text();
                                    episodelink = a.attr("href");
                                    LocalLog.log(TAG + "get week_item_right: " + episodename + "  " + episodelink);
                                }
                            }
                        }else {
                            LocalLog.log("class week_item_right is not exist");
                        }
                        ja.put(animename).put(animelink).put(episodename).put(episodelink);
                    }
                    jsonArray.put(ja);
                }else {
                    LocalLog.log();
                }

            } else {
                LocalLog.log("Can't not find " + weekid);
            }

        }
        if (jsonArray != null){
            return jsonArray.toString();
        }
        return null;
    }
}
