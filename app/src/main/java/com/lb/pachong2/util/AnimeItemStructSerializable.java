package com.lb.pachong2.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/28.
 */
public class AnimeItemStructSerializable implements Serializable {
    private ArrayList<AnimeItemStruct> animeItemStructList = new ArrayList<>();

    public AnimeItemStructSerializable(){};

    public AnimeItemStructSerializable(ArrayList<AnimeItemStruct> animeItemStructList){
        this.animeItemStructList = animeItemStructList;
    }

    public List<AnimeItemStruct> getAnimeItemStructList(){
        return this.animeItemStructList;
    }

    public void setAnimeItemStructList(ArrayList<AnimeItemStruct> animeItemStructList){
        this.animeItemStructList = animeItemStructList;
    }

}
