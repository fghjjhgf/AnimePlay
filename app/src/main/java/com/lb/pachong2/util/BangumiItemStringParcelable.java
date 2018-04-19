package com.lb.pachong2.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/2/4.
 */
public class BangumiItemStringParcelable implements Parcelable {
    private String episodesName;
    private String episodesUrl;

    public BangumiItemStringParcelable(String episodesName, String episodesUrl){
        this.setEpisodesName(episodesName);
        this.setEpisodesUrl(episodesUrl);
    }

    protected BangumiItemStringParcelable(Parcel in) {
        episodesName = in.readString();
        episodesUrl = in.readString();
    }

    public static final Creator<BangumiItemStringParcelable> CREATOR = new Creator<BangumiItemStringParcelable>() {
        @Override
        public BangumiItemStringParcelable createFromParcel(Parcel in) {
            return new BangumiItemStringParcelable(in);
        }

        @Override
        public BangumiItemStringParcelable[] newArray(int size) {
            return new BangumiItemStringParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 2;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(episodesName);
        dest.writeString(episodesUrl);
    }

    public String getEpisodesUrl() {
        return episodesUrl;
    }

    public void setEpisodesUrl(String episodesUrl) {
        this.episodesUrl = episodesUrl;
    }

    public String getEpisodesName() {
        return episodesName;
    }

    public void setEpisodesName(String episodesName) {
        this.episodesName = episodesName;
    }
}
