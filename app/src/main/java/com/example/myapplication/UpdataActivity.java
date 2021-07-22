package com.example.myapplication;


import android.app.Activity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.MainActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.iceteck.silicompressorr.SiliCompressor;

public class UpdataActivity extends AppCompatActivity {


    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 202;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";

    private Retrofit retrofit;

    private IApi service;
    private Uri coverImageUri;
    private Uri videoUri;
    private SimpleDraweeView coverSD;
    private SimpleDraweeView videoSD;
    private EditText extraContentEditText;
    private Button btn_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_updata);
        coverSD = findViewById(R.id.sd_cover);
        videoSD = findViewById(R.id.sd_video);

        btn_submit = findViewById(R.id.btn_submit);


        extraContentEditText = findViewById(R.id.et_extra_content);

        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "选择视频");
            }
        });



        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);
            }
        }

        if (REQUEST_CODE_VIDEO == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                videoSD.setImageURI(coverImageUri);
            }
        }
    }

    //  根据压缩状况修改界面


    private void initNetwork() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 生成api对象
        service = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }
    private void submit() {

        byte[] coverImageData = readDataFromUri(coverImageUri);
        byte[] videoData = readDataFromUri(videoUri);

        btn_submit.setText("正在上传");
        findViewById(R.id.btn_submit).setEnabled(false);
        Runnable button_recover = new Runnable() {
            @Override
            public void run() {
                btn_submit.setText("重新提交");
                findViewById(R.id.btn_submit).setEnabled(true);

            }
        };
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            btn_submit.setText("提交");

            return;
        }

        if (videoData == null || videoData.length == 0) {
            Toast.makeText(this, "视频为空", Toast.LENGTH_SHORT).show();
            btn_submit.setText("提交");
            return;
        }
        String content = extraContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入视频备注", Toast.LENGTH_SHORT).show();
            btn_submit.setText("提交");
            return;
        }
        if ( coverImageData.length + videoData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            btn_submit.setText("提交");
            return;
        }
        RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData);
        MultipartBody.Part coverImage = MultipartBody.Part.createFormData("cover_image", "cover.png", requestImage);

        RequestBody requestVideo = RequestBody.create(MediaType.parse("multipart/form-data"), videoUri.toString());
        MultipartBody.Part video = MultipartBody.Part.createFormData("video", "video.mp4", requestVideo);

        Call<UploadResponse> resp = service.submitVideo(Constants .STUDENT_ID, Constants.USER_NAME, "",
                coverImage, video , Constants.token);
        resp.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(final Call<UploadResponse> call, final Response<UploadResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(UpdataActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                }
                final UploadResponse repoList = response.body();
                if (repoList == null ) {
                    Toast.makeText(UpdataActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(UpdataActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}

