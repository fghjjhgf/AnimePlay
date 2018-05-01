package com.lb.pachong2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lb.pachong2.R;
import com.lb.pachong2.activity.BangumiActivity;
import com.lb.pachong2.util.AnimeItemStruct;
import com.lb.pachong2.util.ConstantString;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/28.
 */
public class AnimeListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "AnimeListRecyclerAdapter";
    private ArrayList<AnimeItemStruct> animeItemStructList = new ArrayList<>();
    private Context context;

    public AnimeListRecyclerAdapter(Context context, ArrayList<AnimeItemStruct> animeItemStructList){
        this.context = context;
        this.animeItemStructList = animeItemStructList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_list_item,parent,false);
        return new AnimeListRecyclerItem(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AnimeListRecyclerItem animeListRecyclerItem = (AnimeListRecyclerItem)holder;
        animeListRecyclerItem.leftTextview.setText(animeItemStructList.get(position).getAnime_name());
        animeListRecyclerItem.leftTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BangumiActivity.class);
                i.putExtra(ConstantString.BANGUMIURL,animeItemStructList.get(position).getAnime_name_url());
                i.putExtra(ConstantString.BANGUMINAME,animeItemStructList.get(position).getAnime_name());
                context.startActivity(i);
            }
        });
        animeListRecyclerItem.rightTextview.setText(animeItemStructList.get(position).getAnime_episodes());
        animeListRecyclerItem.rightTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, animeItemStructList.get(position).getGetAnime_episodes_url());
                try{
                    //Intent i = new Intent(context, BreifVideoActivity.class);
                    //i.putExtra(ConstantString.EPISODESURL,animeItemStructList.get(position).getGetAnime_episodes_url());
                    //i.putExtra(ConstantString.BANGUMINAME,animeItemStructList.get(position).getAnime_name());
                    //i.putExtra(ConstantString.EPISODELIST,animeItemStructList);
                    //context.startActivity(i);
                    Intent i = new Intent(context, BangumiActivity.class);
                    i.putExtra(ConstantString.BANGUMIURL,animeItemStructList.get(position).getAnime_name_url());
                    i.putExtra(ConstantString.BANGUMINAME,animeItemStructList.get(position).getAnime_name());
                    context.startActivity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return animeItemStructList.size();
    }

    class AnimeListRecyclerItem extends RecyclerView.ViewHolder{
        private TextView leftTextview;
        private TextView rightTextview;

        public AnimeListRecyclerItem(View itemView) {
            super(itemView);
            leftTextview = (TextView)itemView.findViewById(R.id.anime_item_name);
            rightTextview = (TextView)itemView.findViewById(R.id.anime_itme_episodes);

        }
    }
}
