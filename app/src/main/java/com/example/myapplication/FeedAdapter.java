package com.example.myapplication;


import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VideoViewHolder>{

    private List<Video> videoItemList;
    public void setData(List<Video> videoList){
        videoItemList = videoList;
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
   //     private TextView mID;
        private TextView muser_name;
        private TextView mstdID;
        private View videoView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView;
     //       mID = itemView.findViewById(R.id.mID);
            muser_name = itemView.findViewById(R.id.muser_name);
            mstdID = itemView.findViewById(R.id.mstdID);
            coverSD = itemView.findViewById(R.id.sd_cover);
        }

    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        VideoViewHolder holder = new VideoViewHolder(view);


        holder.coverSD.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Video video = videoItemList.get(position);
                Intent intent = new Intent(v.getContext(), video_play.class);
                intent.putExtra("videoUrl", video.getvideoUrl());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(holder.videoView.getContext());
        circularProgressDrawable.setStrokeWidth(7f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        Video videoItem = videoItemList.get(position);
        int height = (int) ((float) holder.coverSD.getWidth() / videoItem.getImageW() * videoItem.getImageH());
        holder.coverSD.setImageURI(videoItem.getImageUrl());
        holder.coverSD.setMinimumHeight(height);

        Glide.with(holder.videoView.getContext())
                .load(videoItem.getImageUrl())
                .placeholder(circularProgressDrawable)
                .into(holder.coverSD);

       // holder.mID.setText(videoItem.getId());
        holder.muser_name.setText(videoItem.getusername());
        holder.mstdID.setText(videoItem.getStudentId());
    }

    @Override
    public int getItemCount() {
        return videoItemList==null?0:videoItemList.size();
    }
}