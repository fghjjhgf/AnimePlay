package com.lb.pachong2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    private String TAG = "PlayerActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findView();
        getData();
        init();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    private void findView(){
        textView = (TextView)findViewById(R.id.player_text);
    }

    private void getData(){

    }

    private void init(){

    }
}
