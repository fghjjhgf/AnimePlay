package com.lb.pachong2.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lb.pachong2.R;
import com.lb.pachong2.adapter.AnimeListFragmentAdapter;
import com.lb.pachong2.fragment.AnimeListFragment;
import com.lb.pachong2.network.Network;
import com.lb.pachong2.network.ResponeCallBack;
import com.lb.pachong2.util.AnimeItemStruct;
import com.lb.pachong2.util.ConstantString;
import com.lb.pachong2.util.LocalLog;
import com.lb.pachong2.util.TimeUtil;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private String content = "Empty";
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public ResponeCallBack animeResponeCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kon");
        setSupportActionBar(toolbar);

        findview();
        init();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //DownloadText dt = new DownloadText();
                //dt.start();
                content = read();
                Document d = Jsoup.parse(content);
                Element e1 = d.getElementById("weekdiv1");
                Elements child = e1.children();
                Log.d(TAG, "" + child.size());
            }
        });
    }

    private void findview(){
        viewPager = (ViewPager)findViewById(R.id.main_page_viewpager);
        tabLayout = (TabLayout)findViewById(R.id.main_page_tablayout);
    }

    public void initAnimeRecyclerContent(){
        animeResponeCallBack = new ResponeCallBack() {
            @Override
            public void requestFail(Request request, IOException e) {
                LocalLog.log(request.toString() + "\n" + e.toString());
            }

            @Override
            public void requestSuccess(String result) {

                List<String> tabNameList = new ArrayList<>();
                List<Fragment> fragmentList = new ArrayList<>();
                tabNameList.add("周一");
                tabNameList.add("周二");
                tabNameList.add("周三");
                tabNameList.add("周四");
                tabNameList.add("周五");
                tabNameList.add("周六");
                tabNameList.add("周七");

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        //每一天的数据设置
                        ArrayList<AnimeItemStruct> items=new ArrayList<AnimeItemStruct>();
                        Parcel parcel = Parcel.obtain();
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        Log.d(TAG, "jsonArray1: " + jsonArray1.length());
                        for (int j = 0;j<jsonArray1.length();j+=4)
                        {
                            AnimeItemStruct animeItemStruct = AnimeItemStruct.CREATOR.createFromParcel(parcel);
                            animeItemStruct.setAnime_name(jsonArray1.getString(j));
                            animeItemStruct.setAnime_name_url(jsonArray1.getString(j+1));
                            animeItemStruct.setAnime_episodes(jsonArray1.getString(j+2));
                            animeItemStruct.setGetAnime_episodes_url(jsonArray1.getString(j+3));
                            items.add(animeItemStruct);
                        }
                        parcel.recycle();
                        AnimeListFragment animeListFragment = new AnimeListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstantString.ANIMELISTSTRING, items);
                        bundle.putString("TEST", "TEST");
                        animeListFragment.setArguments(bundle);
                        fragmentList.add(animeListFragment);
                    }
                } catch (Exception e){
                    Log.d(TAG, "error getdata: ");
                    e.printStackTrace();
                }

                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                for (int i=0;i<tabNameList.size();i++)
                    tabLayout.addTab(tabLayout.newTab().setText(tabNameList.get(i)));

                AnimeListFragmentAdapter animeListFragmentAdapter = new AnimeListFragmentAdapter(getSupportFragmentManager(),fragmentList,tabNameList);
                viewPager.setAdapter(animeListFragmentAdapter);
                tabLayout.setupWithViewPager(viewPager);

                viewPager.setCurrentItem(TimeUtil.getWeekDay() - 1);

            }
        };
    }

    private void initListener(){
        initAnimeRecyclerContent();
    }

    private void init(){
        //getdata();
        initListener();
        Network.setMainRequestCallBack(animeResponeCallBack);
        Network.getMobileAnimeListString(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //根据msg.what的值来处理不同的UI操作
                case 1:
                    break;
                default:
                    break;
            }

        }
    };

    public boolean isExternalStorageWritable(){
        String state= Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(Context context,String albumName){
        File file=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),albumName);
        if (!file.mkdirs()){
            Log.d(TAG,"Directory not create");
        }
        return file;
    }

    private void write(String content){
        if (isExternalStorageWritable()){
//            File sdCard=Environment.getExternalStorageDirectory();
            File sdCard= getAlbumStorageDir(MainActivity.this,"test");
            try {
                File targetFile=new File(sdCard.getCanonicalPath()+"1.txt");
                RandomAccessFile raf=new RandomAccessFile(targetFile,"rw");
                raf.seek(targetFile.length());
                raf.write(content.getBytes());
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String read(){
        if (isExternalStorageWritable()){
//            File sdCard=Environment.getExternalStorageDirectory();
            File sdCard=getAlbumStorageDir(MainActivity.this,"test");
            try {
                FileInputStream fis=new FileInputStream(sdCard.getCanonicalPath()+"1.txt");
                StringBuilder sb=new StringBuilder("");
                byte[] buff=new byte[1024];
                int hasRead=0;
                while ((hasRead=fis.read(buff))!=-1){
                    sb.append(new String(buff,0,hasRead));
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
