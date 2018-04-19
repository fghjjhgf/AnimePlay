package com.lb.pachong2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lb.pachong2.R;
import com.lb.pachong2.util.ConstantString;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends AppCompatActivity {

    private String TAG = "PlayerActivity";
    private TextView playtime_textView;
    private String test;
    private String playurl = "http://vd3.bdstatic.com/mda-ic9y8kptcq7ip6mv/mda-ic9y8kptcq7ip6mv.mp4";
    //private String playurl = "http://192.168.1.100/1.mp4";
    private SeekBar seekBar;
    private ImageButton playbutton;
    private SurfaceView surfaceView;
    private IjkMediaPlayer ijkMediaPlayer;
    private SurfaceHolder surfaceHolder;
    private LinearLayout controlLinearLayout;
    private RelativeLayout topRelativeLayout;
    private ImageButton backbutton;
    private boolean updateseeknar = false;
    private int controlstatus = View.GONE;

    private final static int UPDATE_SYS_TIME_MSG = 101;
    private final static int UPDATE_VIDEO_BUFF = 102;
    private final static int UPDATE_VIDEO_SEEKBAR = 103;
    private final static int UPDATE_CONTROL = 104;
    private final static int INITIALIZE_SURFACE_WIDTH = 105;
    private final static int UPDATE_CONTROL_IM = 106;
    private final static int EMPTY_MES = 200;

    private final Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_VIDEO_SEEKBAR:
                    if (updateseeknar)
                    {
                        long curpos = ijkMediaPlayer.getCurrentPosition();
                        long duration = ijkMediaPlayer.getDuration();
                        seekBar.setProgress((int) ((float)curpos / duration * seekBar.getMax()));

                        String strcur = formatTime(ijkMediaPlayer.getCurrentPosition());
                        String total = formatTime(ijkMediaPlayer.getDuration());
                        playtime_textView.setText(strcur + "|" + total);
                    }
                    myhandler.sendEmptyMessageDelayed(UPDATE_VIDEO_SEEKBAR,1000);
                    break;
                case UPDATE_VIDEO_BUFF:
                    break;
                case INITIALIZE_SURFACE_WIDTH:
                    initIjkMedia();
                    break;
                case UPDATE_CONTROL:
                    try{
                        if (getControlstatus() == View.VISIBLE)
                        {
                            setControlstatus(View.GONE);
                            controlstatus = View.GONE;
                            myhandler.removeMessages(UPDATE_CONTROL);
                        }else if (getControlstatus() == View.GONE){
                            controlstatus = View.VISIBLE;
                            setControlstatus(View.VISIBLE);
                            myhandler.sendEmptyMessageDelayed(UPDATE_CONTROL,3000);
                        }
                    }catch(Exception e) {
                        Log.d(TAG, "handleMessage: UPDATE_CONTROL");
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_CONTROL_IM:
                    if (getControlstatus() == View.VISIBLE)
                    {
                        setControlstatus(View.GONE);
                        controlstatus = View.GONE;
                    }else if (getControlstatus() == View.GONE){
                        controlstatus = View.VISIBLE;
                        setControlstatus(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ijkMediaPlayer != null){
            if (ijkMediaPlayer.isPlaying()){
                pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ijkMediaPlayer != null){
            if (!ijkMediaPlayer.isPlaying()){
                play();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ijkMediaPlayer != null){
            ijkMediaPlayer.release();
        }
    }

    private void init(){
        findView();
        getData();
        initListener();

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        myhandler.sendEmptyMessage(UPDATE_SYS_TIME_MSG);
        //myhandler.sendEmptyMessageDelayed(INITIALIZE_SURFACE_WIDTH, 500);
        initIjkMedia();
        myhandler.sendEmptyMessageDelayed(UPDATE_CONTROL,3000);
    }

    private boolean initIjkMedia(){
        try{
            ijkMediaPlayer = new IjkMediaPlayer();
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    ijkMediaPlayer.setDisplay(surfaceHolder);
                    ijkMediaPlayer.setScreenOnWhilePlaying(true);
                    ijkMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
                    ijkMediaPlayer.prepareAsync();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
            ijkMediaPlayer.setDataSource(playurl);
            ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    play();
                }
            });
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void setControlstatus(int v){
        controlLinearLayout.setVisibility(v);
        topRelativeLayout.setVisibility(v);
    }

    private int getControlstatus(){
        return controlstatus;
    }

    private IjkMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            seekBar.setSecondaryProgress((int)((float)percent / 100 * seekBar.getMax()));
        }
    };

    private void play(){
        if (ijkMediaPlayer != null){
            updateseeknar = true;
            playbutton.setBackgroundResource(R.drawable.ic_media_play);
            myhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
            myhandler.sendEmptyMessageDelayed(UPDATE_CONTROL, 3000);
            ijkMediaPlayer.start();
        }
    }

    private void pause(){
        if (ijkMediaPlayer != null){
            updateseeknar = false;
            playbutton.setBackgroundResource(R.drawable.ic_media_pause);
            myhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
            myhandler.sendEmptyMessageDelayed(UPDATE_CONTROL,3000);
            ijkMediaPlayer.pause();
        }
    }

    private void initListener(){
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ijkMediaPlayer != null){
                    if (ijkMediaPlayer.isPlaying()){
                        pause();
                    }else {
                        play();
                    }
                }else {
                    Log.d(TAG, "ijkplayer is null");
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (ijkMediaPlayer == null) {
                    seekBar.setEnabled(false);
                } else {
                    seekBar.setEnabled(true);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int sp = seekBar.getProgress();
                long du = ijkMediaPlayer.getDuration() * sp / seekBar.getMax();
                ijkMediaPlayer.seekTo(du);
                play();

            }
        });

        controlLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        myhandler.sendEmptyMessage(UPDATE_CONTROL);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findView(){
        surfaceView = (SurfaceView)findViewById(R.id.vee_main_player_surfaceview);
        playtime_textView = (TextView)findViewById(R.id.vee_main_player_playtime);
        seekBar = (SeekBar)findViewById(R.id.vee_main_player_video_seekbar);
        playbutton = (ImageButton)findViewById(R.id.vee_main_player_playbutton);
        controlLinearLayout = (LinearLayout)findViewById(R.id.vee_main_player_bottom_control);
        backbutton = (ImageButton)findViewById(R.id.vee_main_player_backbutton);
        topRelativeLayout = (RelativeLayout)findViewById(R.id.vee_main_player_top_control);
    }

    private void getData(){
        Intent i = getIntent();
        test = i.getStringExtra(ConstantString.EPISODESURL);
    }

    private String formatTime(long time){
        String minutestr;
        String secondstr;
        long second = time / 1000;
        long minute = second / 60;
        if (minute == 0)
        {
            return "00:"+String.valueOf(second%60);
        }else if (minute < 10)
        {
            return "0" + minute + ":" + String.valueOf(second%60);
        }else
        {
            return String.valueOf(minute) + ":" + String.valueOf(second%60);
        }

    }
}
