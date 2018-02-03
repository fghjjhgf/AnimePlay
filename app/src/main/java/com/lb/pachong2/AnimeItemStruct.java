package com.lb.pachong2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/1/28.
 */
public class AnimeItemStruct implements Parcelable {
    public String anime_name;
    public String anime_name_url;
    public String anime_episodes;
    public String getAnime_episodes_url;

    public AnimeItemStruct(String anime_name, String anime_name_url, String anime_episodes, String getAnime_episodes_url){
        this.setAnime_name(anime_name);
        this.setAnime_name_url(anime_name_url);
        this.setAnime_episodes(anime_episodes);
        this.setGetAnime_episodes_url(getAnime_episodes_url);

    }

    protected AnimeItemStruct(Parcel in) {
        anime_name = in.readString();
        anime_name_url = in.readString();
        anime_episodes = in.readString();
        getAnime_episodes_url = in.readString();
    }

    public static final Creator<AnimeItemStruct> CREATOR = new Creator<AnimeItemStruct>() {
        @Override
        public AnimeItemStruct createFromParcel(Parcel in) {
            AnimeItemStruct animeItemStruct = new AnimeItemStruct(in);

            return new AnimeItemStruct(in);
        }

        @Override
        public AnimeItemStruct[] newArray(int size) {
            return new AnimeItemStruct[size];
        }
    };

    public void setAnime_name(String anime_name) {
        this.anime_name = anime_name;
    }

    public void setAnime_name_url(String anime_name_url) {
        this.anime_name_url = anime_name_url;
    }

    public void setAnime_episodes(String anime_episodes) {
        this.anime_episodes = anime_episodes;
    }

    public void setGetAnime_episodes_url(String getAnime_episodes_url) {
        this.getAnime_episodes_url = getAnime_episodes_url;
    }

    public String getAnime_name() {

        return anime_name;
    }

    public String getAnime_name_url() {
        return anime_name_url;
    }

    public String getAnime_episodes() {
        return anime_episodes;
    }

    public String getGetAnime_episodes_url() {
        return getAnime_episodes_url;
    }

    @Override
    public int describeContents() {
        return 4;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(anime_name);
        dest.writeString(anime_name_url);
        dest.writeString(anime_episodes);
        dest.writeString(getAnime_episodes_url);
    }
}
