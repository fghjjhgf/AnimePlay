package com.lb.pachong2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lb.pachong2.AnimeItemStruct;
import com.lb.pachong2.ConstantString;
import com.lb.pachong2.R;
import com.lb.pachong2.adapter.AnimeListRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/27.
 */
public class AnimeListFragment extends Fragment {
    private String TAG = "AnimeListFragment";
    private View mView;
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<AnimeItemStruct> animeItemStructList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.anime_list_fragment,container,false);

        String str = getArguments().getString("TEST");
        Log.d(TAG, str);

        animeItemStructList = getArguments().getParcelableArrayList(ConstantString.ANIMELISTSTRING);
        Log.d(TAG, animeItemStructList.get(0).getAnime_episodes());

        init();
        return mView;
    }

    private void init(){
        getData();
        findview();

        AnimeListRecyclerAdapter animeListRecyclerAdapter = new AnimeListRecyclerAdapter(getContext(),animeItemStructList);
        recyclerView.setAdapter(animeListRecyclerAdapter);
    }

    private void getData(){

    }

    private void findview(){
        recyclerView = (RecyclerView)mView.findViewById(R.id.anime_recyclerview);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
