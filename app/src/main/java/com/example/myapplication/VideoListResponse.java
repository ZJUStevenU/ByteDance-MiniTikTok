package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoListResponse {
    @SerializedName("feeds")
    public List<Video> feeds;
    @SerializedName("success")
    public boolean success;
}

