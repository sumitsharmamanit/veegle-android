package com.datingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BlogDTO> blogDTOArrayList;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback, onItemLike, onItemDisLike;
    private boolean status = false;


    public BlogAdapter(Context context, ArrayList<BlogDTO> blogDTOArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, boolean status) {
        super();
        this.context = context;
        this.blogDTOArrayList = blogDTOArrayList;
        this.status = status;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (status) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_hobbies, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_blob, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (status) {
            holder.tvHobbies.setText(blogDTOArrayList.get(position).getHobbies());
            holder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
            if (blogDTOArrayList.get(position).getHobbiesImage() != null) {
                Picasso.with(context).load(blogDTOArrayList.get(position).getHobbiesImage()).resize(250, 250).centerCrop().into(holder.roundedImageView);
            }

            if (blogDTOArrayList.get(position).getStatus() != null && !blogDTOArrayList.get(position).getStatus().equals("")) {
                holder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.check_hob));
            } else {
                holder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.uncheck_hob));
            }
//            .resize(180, 180).centerCrop()
        } else {
            holder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
            holder.tvName.setText(blogDTOArrayList.get(position).getUsername() + ", " + blogDTOArrayList.get(position).getAge());


            try {
                byte[] data = Base64.decode(blogDTOArrayList.get(position).getDescription(), Base64.DEFAULT);
                String text = new String(data, "UTF-8");
                holder.tvBlog.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (blogDTOArrayList.get(position).getBlogimage() != null) {
                Picasso.with(context).load(blogDTOArrayList.get(position).getBlogimage()).fit().centerCrop().into(holder.ivProfile);
            }
        }
    }


    @Override
    public int getItemCount() {
        return blogDTOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvBlog, tvHobbies;
        private ImageView ivProfile, ivCheck;
        private RoundedImageView roundedImageView;


        public ViewHolder(View itemView) {
            super(itemView);
            if (status) {
                roundedImageView = (RoundedImageView) itemView.findViewById(R.id.iv_profile);
                ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);
                tvHobbies = (TextView) itemView.findViewById(R.id.tv_hobbies);
            } else {
                tvName = (TextView) itemView.findViewById(R.id.tv_name);
                tvBlog = (TextView) itemView.findViewById(R.id.tv_blog);
                ivProfile = (ImageView) itemView.findViewById(R.id.iv_profile);
            }
        }
    }
}