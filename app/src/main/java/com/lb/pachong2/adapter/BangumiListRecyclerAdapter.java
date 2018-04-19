package com.lb.pachong2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.pachong2.R;
import com.lb.pachong2.activity.BreifVideoActivity;
import com.lb.pachong2.util.BangumiItemStringParcelable;
import com.lb.pachong2.util.ConstantString;
import com.lb.pachong2.util.LocalLog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/4.
 */
public class BangumiListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "BangumiListRecyclerAdapter";
    private ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList = new ArrayList<>();
    private Context context;
    private String bangumiName;

    public BangumiListRecyclerAdapter(Context context, ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList){
        this.context = context;
        this.bangumiItemStringParcelableArrayList = bangumiItemStringParcelableArrayList;
    }

    public BangumiListRecyclerAdapter(Context context, ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList,String bangumiName){
        this.context = context;
        this.bangumiItemStringParcelableArrayList = bangumiItemStringParcelableArrayList;
        this.bangumiName = bangumiName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_item,parent,false);
        return new BangumiListItemView(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BangumiListItemView bangumiListItemView = (BangumiListItemView)holder;
        bangumiListItemView.episodesNameTextView.setText(bangumiItemStringParcelableArrayList.get(position).getEpisodesName());
        bangumiListItemView.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(context, FullscreenPlayerActivity.class);
                Intent i = new Intent(context, BreifVideoActivity.class);
                i.putExtra(ConstantString.EPISODESURL,bangumiItemStringParcelableArrayList.get(position).getEpisodesUrl());
                LocalLog.log("BangumiListRecyclerAdapter",bangumiItemStringParcelableArrayList.get(position).getEpisodesUrl());
                i.putExtra(ConstantString.BANGUMINAME,bangumiName + " " + bangumiItemStringParcelableArrayList.get(position).getEpisodesName());
                i.putExtra(ConstantString.EPISODELIST,bangumiItemStringParcelableArrayList);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bangumiItemStringParcelableArrayList.size();
    }

    class BangumiListItemView extends RecyclerView.ViewHolder{
        private TextView episodesNameTextView;
        private LinearLayout linearLayout;

        public BangumiListItemView(View itemView) {
            super(itemView);
            episodesNameTextView = itemView.findViewById(R.id.bangumi_item_episodes_name);
            linearLayout = itemView.findViewById(R.id.bangumi_item_linearlayout);
        }
    }
}
