package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FeedAdapter mAdapter = new FeedAdapter();
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private FloatingActionButton mScrollTopButton;
    private TextView rec;
    public static void startMainActvity(AppCompatActivity activity){
        Intent intent = new Intent(activity , MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.mRecycler);
     //   recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        getData(null);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));




        findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UpdataActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onRestart() {
        super.onRestart();
        getData(null);
    }

    public void customCamera(View view) {
        requestPermission();
    }

    private void recordVideo() {
        CustomCameraActivity.startUI(this);
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData(String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Video>res=getMessagesFromRemote(studentId);
                if(res!=null){
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                                 mAdapter.setData(res);
                        }
                    });
                }
            }
        }).start();
    }


    private List<Video> getMessagesFromRemote(String studentId){
        String urlStr;
        if(studentId!=null){
            urlStr =String.format("%svideo?student_id=%s",Constants.BASE_URL,studentId);
        } else{
            urlStr =String.format("%svideo",Constants.BASE_URL);
        }

        VideoListResponse result=null;
        try {
            URL url=new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token",Constants.token);  //设置header中的token

            if (conn.getResponseCode() == 200){
                Log.d("1111111","kk");
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                result = new Gson().fromJson(reader, new TypeToken<VideoListResponse>() {
                }.getType());
                reader.close();
                in.close();

            } else{
                throw new IOException("没找到");
            }

        } catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"网络异常"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(result!=null&&result.feeds!=null){
            return result.feeds;  //拿到了值
        }
        return  null;
    }

}