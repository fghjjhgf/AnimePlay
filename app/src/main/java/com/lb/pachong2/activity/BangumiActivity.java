package com.lb.pachong2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lb.pachong2.R;
import com.lb.pachong2.adapter.BangumiListRecyclerAdapter;
import com.lb.pachong2.network.Network;
import com.lb.pachong2.network.ResponeCallBack;
import com.lb.pachong2.util.BangumiItemStringParcelable;
import com.lb.pachong2.util.ConstantString;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

public class BangumiActivity extends AppCompatActivity {

    private String TAG = "BangumiActivity";

    private RecyclerView recyclerView;
    private String bangumiURL;
    private String bangumiName;
    private ResponeCallBack bangumiResponeCallBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private Toolbar toolbar;
    private ImageButton backImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void findView(){
        recyclerView = (RecyclerView)findViewById(R.id.bangumi_esipodes_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.bangumi_SwipeRefreshLayout);
        textView = (TextView)findViewById(R.id.bangumi_name_textview);
        backImageButton = (ImageButton)findViewById(R.id.bangumi_backbutton);
    }

    private void init(){
        findView();
        initListener();
        getData();
        setView();

        swipeRefreshLayout.setRefreshing(true);

        Network.setBangumiResponeCallBack(bangumiResponeCallBack);
        Network.getEpisodeMobileList(bangumiURL);


    }

    private void initListener(){
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bangumiResponeCallBack = new ResponeCallBack() {
            @Override
            public void requestFail(Request request, IOException e) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestSuccess(String result) {
                ArrayList<BangumiItemStringParcelable> items = new ArrayList<BangumiItemStringParcelable>();

                try{
                    JSONArray jsonArray = new JSONArray(result);
                    Parcel parcel = Parcel.obtain();
                    for (int i = 0;i < jsonArray.length();i++){
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        BangumiItemStringParcelable bangumiItemStringParcelable = BangumiItemStringParcelable.CREATOR.createFromParcel(parcel);
                        bangumiItemStringParcelable.setEpisodesName(jsonArray1.getString(0));
                        bangumiItemStringParcelable.setEpisodesUrl(jsonArray1.getString(1));
                        items.add(bangumiItemStringParcelable);
                    }
                    parcel.recycle();
                    BangumiListRecyclerAdapter bangumiListRecyclerAdapter = new BangumiListRecyclerAdapter(getApplicationContext(),items,bangumiName);
                    recyclerView.setAdapter(bangumiListRecyclerAdapter);

                }catch (Exception e){
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Network.getEpisodeMobileList(bangumiURL);
            }
        });
    }

    private void getData(){
        Intent intent = getIntent();
        bangumiURL = intent.getStringExtra(ConstantString.BANGUMIURL);
        bangumiName = intent.getStringExtra(ConstantString.BANGUMINAME);
    }

    private void setView(){
        if (bangumiName != null){
            textView.setText(bangumiName);
        }
    }

}
