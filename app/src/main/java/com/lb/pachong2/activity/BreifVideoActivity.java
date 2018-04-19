package com.lb.pachong2.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lb.pachong2.R;
import com.lb.pachong2.adapter.BreifBangumiListRecyclerAdapter;
import com.lb.pachong2.mediasource.JxMaoyunAnalysis;
import com.lb.pachong2.mediasource.MaoyunAnalysis;
import com.lb.pachong2.mediasource.MediaResponseCallback;
import com.lb.pachong2.mediasource.SourceMediaAnalysis;
import com.lb.pachong2.mediasource.YylepAnalysis;
import com.lb.pachong2.mediasource.YylepEmptyAnalysis;
import com.lb.pachong2.network.Network;
import com.lb.pachong2.network.ResponeCallBack;
import com.lb.pachong2.parser.MobileBangumiParser;
import com.lb.pachong2.services.MaoyunMediaService;
import com.lb.pachong2.util.BangumiItemStringParcelable;
import com.lb.pachong2.util.ConstantString;
import com.lb.pachong2.util.LocalLog;
import com.lb.pachong2.widget.KonIjkVideoView;
import com.lb.pachong2.widget.media.AndroidMediaController;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class BreifVideoActivity extends AppCompatActivity implements ServiceConnection {

    private static String TAG = "BreifVideoActivity";

    public static String EPISODEURL = "FULLSCREENPLAYEREPISODEURL";
    public static String BANGUMINAME = "FULLSCREENPLAYERBANGUMINAME";

    private Toolbar toolbar;
    private ImageButton backImageButton;
    private TextView episodenameTextview;
    private TableLayout mHudView;
    private KonIjkVideoView ijkVideoView;

    private Button preButton;
    private Button nextButton;
    private Button quanpingButton;

    private Button line1Button;
    private Button line2Button;
    private Button line3Button;
    private Button line4Button;
    private Button line5Button;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout mediaFrameLayout;

    private AndroidMediaController mMediaController;
    private String bangumiURL;
    private String bangumiName;
    private ArrayList<BangumiItemStringParcelable> items;
    private Intent intentService;
    private ResponeCallBack bangumiResponeCallBack;

    public final static int START_ANALYSIS = 101;
    public final static int FAIL_GET_MEIDA_URL = 201;
    public final static int SUCCESS_GET_MEDIA_URL = 202;
    public final static int FIN_GET_MEDIA_URL = 203;
    public final static int INIT_RECYCLER = 204;
    public final static int MSG_SETTITLE = 205;
    public final static int MSG_PLAY_VIDEO = 206;

    private BreifBangumiListRecyclerAdapter.MediaSourceCallback mediaSourceCallback;
    private boolean mIsLandscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_breif_video);

        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.breif_video_toolbar);;
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),true, rotationObserver);


        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        findView();
        getData();
        setView();

        initListener();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void findView(){
        backImageButton = (ImageButton)findViewById(R.id.breif_video_backbutton);
        episodenameTextview = (TextView)findViewById(R.id.breif_video_episode_name);
        mHudView = (TableLayout)findViewById(R.id.breif_video_hud_view);
        ijkVideoView = (KonIjkVideoView)findViewById(R.id.breif_video_ijkVideoview);
        preButton = (Button)findViewById(R.id.pre_episode_button);
        nextButton = (Button)findViewById(R.id.next_episode_button);
        quanpingButton = (Button)findViewById(R.id.quanping_button);
        mediaFrameLayout = (FrameLayout)findViewById(R.id.media_viewgroup_framelayout);
        line1Button = (Button)findViewById(R.id.line1_button);
        line2Button = (Button)findViewById(R.id.line2_button);
        line3Button = (Button)findViewById(R.id.line3_button);
        line4Button = (Button)findViewById(R.id.line4_button);
        line5Button = (Button)findViewById(R.id.line5_button);
        recyclerView = (RecyclerView)findViewById(R.id.breif_video_episode_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.breif_video_swiperefreshlayout);
    }

    private void getData(){
        Intent intent = getIntent();
        bangumiURL = intent.getStringExtra(ConstantString.EPISODESURL);
        bangumiName = intent.getStringExtra(ConstantString.BANGUMINAME);
        items = intent.getParcelableArrayListExtra(ConstantString.EPISODELIST);
    }

    private void setView(){
        if (bangumiName != null){
            episodenameTextview.setText(bangumiName);
        }
    }

    private void init(){

        swipeRefreshLayout.setRefreshing(true);

        Network.setBreifVideoResponeCallBack(bangumiResponeCallBack);
        Network.getBreifVideoMobileList(bangumiURL);

        intentService = new Intent(this, MaoyunMediaService.class);
        intentService.putExtra(MaoyunMediaService.MSG_HANDLE,MaoyunMediaService.MSG_NOTEMPTY);
        intentService.putExtra(MaoyunMediaService.MSG_BANGUMIURL,bangumiURL);

        bindService(intentService,this,BIND_AUTO_CREATE);
    }

    private MediaResponseCallback mediaResponseCallback = new MediaResponseCallback() {
        @Override
        public void sucessResponse(String mediaurl) {
            LocalLog.log(TAG,"sucessResponse");
            LocalLog.log(TAG,"mediaurl is : " + mediaurl);
            Message smsg = new Message();
            smsg.what = MSG_PLAY_VIDEO;
            smsg.obj = mediaurl;
            uiHandle.sendMessage(smsg);
        }

        @Override
        public void failResponse(String msg) {
            LocalLog.log(TAG,"failResponse");
            Message fmsg = new Message();
            fmsg.what = FAIL_GET_MEIDA_URL;
            uiHandle.sendMessage(fmsg);
        }
    };

    private void initListener(){

        //line1
        line1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JxMaoyunAnalysis jxMaoyunAnalysis = new JxMaoyunAnalysis();
                jxMaoyunAnalysis.setMediaResponseCallback(mediaResponseCallback);
                jxMaoyunAnalysis.getMediaSource(bangumiURL);
            }
        });

        //line2
        line2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaoyunAnalysis maoyunAnalysis = new MaoyunAnalysis();
                maoyunAnalysis.setMediaResponseCallback(mediaResponseCallback);
                maoyunAnalysis.getMediaSource(bangumiURL);
            }
        });

        //line3
        line3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YylepAnalysis yylepAnalysis = new YylepAnalysis();
                yylepAnalysis.setMediaResponseCallback(mediaResponseCallback);
                yylepAnalysis.getMediaSource(bangumiURL);
            }
        });

        //line4
        line4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YylepEmptyAnalysis yylepEmptyAnalysis = new YylepEmptyAnalysis();
                yylepEmptyAnalysis.setMediaResponseCallback(mediaResponseCallback);
                yylepEmptyAnalysis.getMediaSource(bangumiURL);
            }
        });

        line5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceMediaAnalysis sourceMediaAnalysis = new SourceMediaAnalysis();
                sourceMediaAnalysis.setMediaResponseCallback(mediaResponseCallback);
                sourceMediaAnalysis.getMediaSource(bangumiURL);
            }
        });

        //next episode
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = MaoyunMediaService.getBody();
                String mediaurl = MobileBangumiParser.getNextEpisodeURL(body);
                LocalLog.log(TAG,"mediaurl : "+mediaurl);
                setNextNewMediaURL(mediaurl);
            }
        });

        //pre episode
        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = MaoyunMediaService.getBody();
                String mediaurl = MobileBangumiParser.getNextEpisodeURL(body);
                LocalLog.log(TAG,"mediaurl : "+mediaurl);
                setPreNewMediaURL(mediaurl);
            }
        });

        quanpingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLandscape == false) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Network.getBreifVideoMobileList(bangumiURL);
            }
        });

        bangumiResponeCallBack = new ResponeCallBack() {
            @Override
            public void requestFail(Request request, IOException e) {
                LocalLog.log(TAG,"bangumiResponeCallBack request fail" + "\n" + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void requestSuccess(String result) {
                LocalLog.log(TAG,"bangumiResponeCallBack request success");
                Message message = new Message();
                message.what = INIT_RECYCLER;
                message.obj = result;
                uiHandle.sendMessage(message);

            }
        };

        mediaSourceCallback = new BreifBangumiListRecyclerAdapter.MediaSourceCallback() {
            @Override
            public void setMediaSource(String msg, String url, String title) {
                Intent intent = new Intent();
                intent.putExtra(MaoyunMediaService.MSG_HANDLE,MaoyunMediaService.MSG_NOTEMPTY);
                intent.putExtra(MaoyunMediaService.MSG_BANGUMIURL,bangumiURL);
                MaoyunMediaService.setHandle(intent);
                Message message = new Message();
                message.what = MSG_SETTITLE;
                message.obj = title;
                uiHandle.sendMessage(message);
            }
        };
    }

    private void setNextNewMediaURL(String url){
        if (url != null){
            Intent intent = new Intent();
            intent.putExtra(MaoyunMediaService.MSG_HANDLE,MaoyunMediaService.MSG_NOTEMPTY);
            intent.putExtra(MaoyunMediaService.MSG_BANGUMIURL,url);
            MaoyunMediaService.setHandle(intent);
            String[] ss = bangumiName.split(" ");
            Integer integer = new Integer(ss[1]);
            integer = integer + 1;
            String title = ss[0] + " " + integer;
            Message message = new Message();
            message.what = MSG_SETTITLE;
            message.obj = title;
            uiHandle.sendMessage(message);
        }else {
            Message msg = new Message();
            msg.what = FAIL_GET_MEIDA_URL;
            uiHandle.sendMessage(msg);
        }
    }

    private void setPreNewMediaURL(String url){
        if (url != null){
            Intent intent = new Intent();
            intent.putExtra(MaoyunMediaService.MSG_HANDLE,MaoyunMediaService.MSG_NOTEMPTY);
            intent.putExtra(MaoyunMediaService.MSG_BANGUMIURL,url);
            MaoyunMediaService.setHandle(intent);
            String[] ss = bangumiName.split(" ");
            Integer integer = new Integer(ss[1]);
            integer = integer - 1;
            String title = ss[0] + " " + integer;
            Message message = new Message();
            message.what = MSG_SETTITLE;
            message.obj = title;
            uiHandle.sendMessage(message);
        }else {
            Message msg = new Message();
            msg.what = FAIL_GET_MEIDA_URL;
            uiHandle.sendMessage(msg);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            mIsLandscape = true;
            //横屏 视频充满全屏
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mediaFrameLayout.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mediaFrameLayout.setLayoutParams(layoutParams);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mIsLandscape = false;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mediaFrameLayout.getLayoutParams();
            float scale = Resources.getSystem().getDisplayMetrics().density;
            layoutParams.height = (int)(220 * scale + 0.5f);
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mediaFrameLayout.setLayoutParams(layoutParams);
        }
    }

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (selfChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }
    };

    private void initIjkVideoView(String str){
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        ijkVideoView.setMediaController(mMediaController);
        ijkVideoView.setHudView(mHudView);

        ijkVideoView.setVideoURI(Uri.parse(str));
        ijkVideoView.start();
    }

    private void startVideo(String str){
        ijkVideoView.setVideoURI(Uri.parse(str));
        ijkVideoView.start();
    }

    @Override
    public void onBackPressed() {
        if (mIsLandscape == false){
            super.onBackPressed();
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ijkVideoView.isPlaying()){
            ijkVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //if (!ijkVideoView.isBackgroundPlayEnabled()) {
        //    ijkVideoView.stopPlayback();
        //    ijkVideoView.release(true);
        //    ijkVideoView.stopBackgroundPlay();
        //} else {
        //    ijkVideoView.enterBackground();
        //}

    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(rotationObserver);
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
        unbindService(this);
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocalLog.log("FullscreenPlayerActivity","Bind success");
        MaoyunMediaService.Binder binder = (MaoyunMediaService.Binder) service;
        MaoyunMediaService maoyunMediaService = binder.getService();
        maoyunMediaService.setCallback(new MaoyunMediaService.Callback() {
            @Override
            public void onDataGet(String data) {
                LocalLog.log(TAG,"media: " + data);
                Message msg = new Message();
                msg.what = FIN_GET_MEDIA_URL;
                msg.obj = data;
                uiHandle.sendMessage(msg);
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private Handler uiHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case FIN_GET_MEDIA_URL:
                    String str = (String)msg.obj;
                    if (str.equals(MaoyunMediaService.EMPTYURL)){
                        Toast.makeText(getApplicationContext(),"no url2",Toast.LENGTH_LONG).show();
                    }else {
                        initIjkVideoView(str);
                        //startVideo(str);
                    }
                    break;
                case INIT_RECYCLER:
                    LocalLog.log(TAG,"recycler init");
                    LocalLog.log(TAG,"items length: " + items.toString());
                    String[] ss = bangumiName.split(" ");
                    String bn = ss[0];
                    BreifBangumiListRecyclerAdapter breifBangumiListRecyclerAdapter = new BreifBangumiListRecyclerAdapter(getApplicationContext(),items,bn);
                    breifBangumiListRecyclerAdapter.setCallback(mediaSourceCallback);
                    recyclerView.setAdapter(breifBangumiListRecyclerAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case FAIL_GET_MEIDA_URL:
                    Toast.makeText(getApplicationContext(),"no url2",Toast.LENGTH_LONG).show();
                    break;
                case MSG_SETTITLE:
                    String title = (String) msg.obj;
                    episodenameTextview.setText(title);
                    break;
                case MSG_PLAY_VIDEO:
                    if (ijkVideoView.isPlaying())
                        ijkVideoView.stopBackgroundPlay();
                    String medieurl = (String)msg.obj;
                    startVideo(medieurl);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
