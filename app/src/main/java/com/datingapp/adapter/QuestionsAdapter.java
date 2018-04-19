package com.datingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.datingapp.Model.BlogDTO;
import com.datingapp.R;
import com.datingapp.util.OnItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BlogDTO> blogDTOArrayList;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback, onItemLike, onItemDisLike;
    private boolean status = false;


    public QuestionsAdapter(Context context, ArrayList<BlogDTO> blogDTOArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, boolean status) {
        super();
        this.context = context;
        this.blogDTOArrayList = blogDTOArrayList;
        this.status = status;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_personality, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvQuestions.setText(blogDTOArrayList.get(position).getQuestion());
        holder.tvAnswer.setText(blogDTOArrayList.get(position).getAnswer().substring(0,1).toUpperCase() + blogDTOArrayList.get(position).getAnswer().substring(1));
        holder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
    }


    @Override
    public int getItemCount() {
        return blogDTOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvQuestions, tvAnswer;
        private ImageView ivProfile, ivCheck;
        private RoundedImageView roundedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvQuestions = (TextView) itemView.findViewById(R.id.tv_question);
            tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
        }
    }
}
