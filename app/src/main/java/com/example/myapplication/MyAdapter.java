package com.example.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Video;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.VideoViewHolder>{
    private List<Video> data;
    public void setData(List<Video> messageList){
        data = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root =LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new VideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
        private TextView mID;
        private TextView muser_name;
        private TextView mstdID;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
      //      mID = itemView.findViewById(R.id.mID);
            muser_name = itemView.findViewById(R.id.muser_name);
            mstdID = itemView.findViewById(R.id.mstdID);
            coverSD = itemView.findViewById(R.id.sd_cover);
        }
        public void bind(Video video){

            coverSD.setImageURI(video.getImageUrl());
            mID.setText("ID: "+video.getId());
            muser_name.setText("user: "+video.getusername());
            mstdID.setText("SID: "+video.getStudentId());
        }
    }

}
