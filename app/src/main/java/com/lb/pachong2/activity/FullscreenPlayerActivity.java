package com.lb.pachong2.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lb.pachong2.R;
import com.lb.pachong2.network.Network;
import com.lb.pachong2.network.ResponeCallBack;
import com.lb.pachong2.services.MaoyunMediaService;
import com.lb.pachong2.util.ConstantString;
import com.lb.pachong2.util.LocalLog;
import com.lb.pachong2.widget.media.AndroidMediaController;
import com.lb.pachong2.widget.media.IjkVideoView;

import java.io.IOException;

import okhttp3.Request;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class FullscreenPlayerActivity extends AppCompatActivity implements ServiceConnection {

    public static String EPISODEURL = "FULLSCREENPLAYEREPISODEURL";
    public static String BANGUMINAME = "FULLSCREENPLAYERBANGUMINAME";

    private String TAG = "FullscreenPlayerActivity";
    private IjkVideoView ijkVideoView;
    private String bangumi_name = "";
    private String episode_url = "";
    private String testurl = "http://vd3.bdstatic.com/mda-ic9y8kptcq7ip6mv/mda-ic9y8kptcq7ip6mv.mp4";

    private AndroidMediaController mMediaController;
    private TableLayout mHudView;
    private Boolean mBackPressed = false;
    private ImageButton backButton;
    private TextView banguminameTextView;
    private Toolbar toolbar;

    public final static int START_ANALYSIS = 101;
    public final static int FAIL_GET_MEIDA_URL = 201;
    public final static int SUCCESS_GET_MEDIA_URL = 202;
    public final static int FIN_GET_MEDIA_URL = 203;

    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_player);
        toolbar = (Toolbar)findViewById(R.id.fullscreen_toolbar);;
        setSupportActionBar(toolbar);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        findView();
        getData();
        setView();
////
        intentService = new Intent(this, MaoyunMediaService.class);
        if (episode_url != null){
            intentService.putExtra(EPISODEURL,episode_url);
        }
////
        bindService(intentService,this,BIND_AUTO_CREATE);

        //TEST URL http://www.maoyun.tv/mdparse/index.php?id=http://www.iqiyi.com/v_19rrbe17uo.html
        //MaoyunMediaParser.encode("04153c060c2d05678b3484e95d6f54a9" + "!abef987");
        //MaoyunMediaParser.encode("123456");

        //String str = "\\x24\\x28\\x27\\x23\\x68\\x64\\x4d\\x64\\x35\\x27\\x29\\x2e\\x76\\x61\\x6c\\x28\\x27\\x30\\x34\\x31\\x35\\x33\\x63\\x30\\x36\\x30\\x63\\x32\\x64\\x30\\x35\\x36\\x37\\x38\\x62\\x33\\x34\\x38\\x34\\x65\\x39\\x35\\x64\\x36\\x66\\x35\\x34\\x61\\x39\\x27\\x29\\x3b";
        //str = str.replace("\\x","");
        //LocalLog.log(TAG,"str: " +str + "\nDECODE: " + MaoyunMediaService.toStringHex1(str));
        //LocalLog.log(TAG,"$('#hdMd5').val('04153c060c2d05678b3484e95d6f54a9');");
        //String str1 = "$('#hdMd5').val('04153c060c2d05678b3484e95d6f54a9');";
        //LocalLog.log(TAG,str1.substring(17,17+32));
    }

    private void findView(){

        ijkVideoView= (IjkVideoView) findViewById(R.id.video_view);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        backButton = (ImageButton)findViewById(R.id.fullscreen_player_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        banguminameTextView = (TextView)findViewById(R.id.fullscreen_player_episode_name);
    }

    public void initIjkPlayer(String url){
        LocalLog.log(TAG,"MEDIA URL: " + url);
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        ijkVideoView.setMediaController(mMediaController);
        ijkVideoView.setHudView(mHudView);

        ijkVideoView.setVideoURI(Uri.parse(url));
        ijkVideoView.start();
    }

    private void setView(){
        if (bangumi_name != null){
            banguminameTextView.setText(bangumi_name);
        }
    }

    private void getData(){
        Intent i = getIntent();
        episode_url = i.getStringExtra(ConstantString.EPISODESURL);
        //episode_url = "http://www.dilidili.wang/watch3/62781/";
        bangumi_name = i.getStringExtra(ConstantString.BANGUMINAME);
        LocalLog.log(TAG," episode_url " +episode_url + " " + bangumi_name);

    }

    ResponeCallBack responeCallBack = new ResponeCallBack() {
        @Override
        public void requestFail(Request request, IOException e) {
            LocalLog.log(TAG,"request: " + request.toString());
            LocalLog.log(TAG,e.toString());
        }

        @Override
        public void requestSuccess(String result) {
            if (result != Network.EMPTYSTR){
                initIjkPlayer(result);
            }else {
                Toast.makeText(getApplicationContext(),"no url",Toast.LENGTH_LONG).show();
            }

        }
    };

    public Handler uiHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case START_ANALYSIS:
                    LocalLog.log("");
                    break;
                case FAIL_GET_MEIDA_URL:
                    Toast.makeText(getApplicationContext(),"no url1",Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS_GET_MEDIA_URL:

                    break;
                case FIN_GET_MEDIA_URL:
                    String url = (String) msg.obj;
                    if (url.equals(MaoyunMediaService.EMPTYURL)){
                        Toast.makeText(getApplicationContext(),"no url2",Toast.LENGTH_LONG).show();
                    }else {
                        initIjkPlayer(url);
                    }

                    break;
                default:
                        break;

            }
            return false;
        }
    });

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocalLog.log("FullscreenPlayerActivity","Bind success");
        MaoyunMediaService.Binder binder = (MaoyunMediaService.Binder) service;
        MaoyunMediaService maoyunMediaService = binder.getService();
        maoyunMediaService.setCallback(new MaoyunMediaService.Callback() {
            @Override
            public void onDataGet(String data) {
                LocalLog.log(TAG,data);
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

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !ijkVideoView.isBackgroundPlayEnabled()) {
            ijkVideoView.stopPlayback();
            ijkVideoView.release(true);
            ijkVideoView.stopBackgroundPlay();
        } else {
            ijkVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
        //stopService(intentService);
        unbindService(this);
    }
}
