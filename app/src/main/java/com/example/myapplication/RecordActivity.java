package com.example.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.app.Activity;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final String TAG = "RecordActivity";
    private SurfaceView mSurfaceview;
    private Button btn_start_or_stop;
    private Button btn_play;
    private Button btn_turn;
    private boolean isRecording = false;//是否正在录像
    private boolean isPlay = false;//是否正在播放录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private ImageView mImageView;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private TextView time;
    private int text = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            time.setText("录制"+text+"秒");
            handler.postDelayed(this,1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mImageView = (ImageView) findViewById(R.id.imageview);
        btn_start_or_stop = (Button) findViewById(R.id.btn_start_or_stop);
        btn_turn = (Button) findViewById(R.id.btn_turn);
        btn_play = (Button) findViewById(R.id.btn_play);
        time = (TextView)findViewById(R.id.time);
        btn_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null){
                    File file = new File(path);
                    if(file.exists()){
                        Intent intent = new Intent(RecordActivity.this,PlayActivity.class);
                        intent.putExtra("path",path);
                        startActivity(intent);
                    }else {
                        Toast.makeText(RecordActivity.this,"视频文件不存在",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RecordActivity.this,"文件路径不存在",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_start_or_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay){
                    if (mediaPlayer != null){
                        isPlay = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                if (!isRecording){
                    handler.postDelayed(runnable,1000);
                    mImageView.setVisibility(View.GONE);
                    if (mRecorder == null){
                        mRecorder = new MediaRecorder();
                        text = 0;
                    }
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    if (camera != null){
                        camera.setDisplayOrientation(90);
                        camera.unlock();
                        mRecorder.setCamera(camera);
                    }
                    try{
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                        mRecorder.setVideoSize(640,480);
                        mRecorder.setVideoFrameRate(30);
                        mRecorder.setVideoEncodingBitRate(4 * 1024 * 1024);
                        mRecorder.setOrientationHint(90);
                        mRecorder.setMaxDuration(30 * 1000);//设置录制最长时间为30秒
                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());//设置录制视频时的预览画面
                        path = getSdPath();
                        if (path != null){
                            File dir = new File(path + "/recordtest");
                            if (!dir.exists()){
                                dir.mkdir();
                            }
                            path = dir + "/" + getDate() + ".mp4";
                            mRecorder.setOutputFile(path);
                            mRecorder.prepare();
                            mRecorder.start();
                            isRecording = true;
                            btn_start_or_stop.setText("停止");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    if (isRecording){
                        try {
                            handler.removeCallbacks(runnable);
                            mRecorder.stop();
                            mRecorder.reset();
                            mRecorder.release();
                            mRecorder = null;
                            btn_start_or_stop.setText("开始");
                            if (camera != null){
                                camera.release();
                                camera = null;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        isRecording = false;
                    }
                }
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay = true;
                mImageView.setVisibility(View.GONE);
                if (mediaPlayer == null){
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.reset();
                Uri uri = Uri.parse(path);
                if (uri == null){
                    Toast.makeText(RecordActivity.this,"请先录制视频",Toast.LENGTH_SHORT).show();
                    return;
                }
                mediaPlayer = MediaPlayer.create(RecordActivity.this,uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(mSurfaceHolder);
                try{
                    mediaPlayer.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });
        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRecording){
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }
    /**
     * 获取系统时间
     *
     * @return
     */
    private static String getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int second = calendar.get(Calendar.SECOND);
        String date = "" + year + (month + 1) + day + hour + minute + second;
        return date;
    }
    /**
     * 获取SD path
     *
     * @return
     */
    public String getSdPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            return sdDir.toString();
        }
        return null;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
