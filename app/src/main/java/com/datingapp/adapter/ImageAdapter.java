package com.datingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.util.OnItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<ProfileModel.Friends> friendsArrayList;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;
    private boolean timelineStatus = false;


    public ImageAdapter(Context context, List<ProfileModel.Friends> friendsArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, boolean timelineStatus) {
        super();
        this.context = context;
        this.friendsArrayList = friendsArrayList;
        this.onItemClickCallback = onItemClickCallback;
        this.timelineStatus = timelineStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (timelineStatus){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline, parent, false);
            return new ViewHolder(view);
        }else {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_images, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (timelineStatus){
            if (friendsArrayList.get(position).getTimelineimage() != null) {
                Picasso.with(context).load(friendsArrayList.get(position).getTimelineimage()).resize(200, 200).centerCrop().into(holder.ivTimeLine);
            }
        }else {
        holder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
            if (friendsArrayList.get(position).getProfileImage() != null) {
                Picasso.with(context).load(friendsArrayList.get(position).getProfileImage()).resize(150, 150).centerCrop().into(holder.ivProfile);
            }
        }
    }


    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView ivProfile, ivTimeLine;

        public ViewHolder(View itemView) {
            super(itemView);
            if (timelineStatus){
                ivTimeLine = (RoundedImageView) itemView.findViewById(R.id.iv_timeline_image);
            }else {
            ivProfile = (RoundedImageView) itemView.findViewById(R.id.iv_profile);
            }
            }
        }
}