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
import com.lb.pachong2.services.MaoyunMediaService;
import com.lb.pachong2.util.BangumiItemStringParcelable;
import com.lb.pachong2.util.ConstantString;
import com.lb.pachong2.util.LocalLog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/20.
 */

public class BreifBangumiListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "BreifBangumiListRecycler";
    private ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList = new ArrayList<>();
    private Context context;
    private String bangumiName;
    private MediaSourceCallback mediaSourceCallback;

    public BreifBangumiListRecyclerAdapter(Context context, ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList){
        this.context = context;
        this.bangumiItemStringParcelableArrayList = bangumiItemStringParcelableArrayList;
    }

    public BreifBangumiListRecyclerAdapter(Context context, ArrayList<BangumiItemStringParcelable> bangumiItemStringParcelableArrayList,String bangumiName){
        this.context = context;
        this.bangumiItemStringParcelableArrayList = bangumiItemStringParcelableArrayList;
        this.bangumiName = bangumiName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_item,parent,false);
        return new BreifBangumiListRecyclerAdapter.BangumiListItemView(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BreifBangumiListRecyclerAdapter.BangumiListItemView bangumiListItemView = (BreifBangumiListRecyclerAdapter.BangumiListItemView)holder;
        bangumiListItemView.episodesNameTextView.setText(bangumiItemStringParcelableArrayList.get(position).getEpisodesName());
        bangumiListItemView.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(context, FullscreenPlayerActivity.class);
                Intent i = new Intent(context, BreifVideoActivity.class);
                i.putExtra(ConstantString.EPISODESURL,bangumiItemStringParcelableArrayList.get(position).getEpisodesUrl());
                LocalLog.log("BangumiListRecyclerAdapter",bangumiItemStringParcelableArrayList.get(position).getEpisodesUrl());
                i.putExtra(ConstantString.BANGUMINAME,bangumiName );
                i.putExtra(ConstantString.EPISODELIST,bangumiItemStringParcelableArrayList);
                mediaSourceCallback.setMediaSource(MaoyunMediaService.MSG_NOTEMPTY,bangumiItemStringParcelableArrayList.get(position).getEpisodesUrl(),
                        bangumiName + " " + bangumiItemStringParcelableArrayList.get(position).getEpisodesName());
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

    public void setCallback(MediaSourceCallback mediaSourceCallback){
        this.mediaSourceCallback = mediaSourceCallback;
    }

    public interface MediaSourceCallback{
        void setMediaSource(String msg, String url, String title);
    }
}
