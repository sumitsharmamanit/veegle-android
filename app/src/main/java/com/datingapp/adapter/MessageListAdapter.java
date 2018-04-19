package com.datingapp.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datingapp.Model.MessageDTO;
import com.datingapp.R;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {
    Context context;
    private List<MessageDTO> messageListDTO;
    ManageSession appSession;
    public ImageView ivCapture;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;


    public MessageListAdapter(Context context, ArrayList<MessageDTO> list, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        this.context = context;
        this.onItemClickCallback = onItemClickCallback;
        this.messageListDTO = list;
        appSession = new ManageSession(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_message_one, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i(getClass().getName(), "onBindViewHolder ");
        Date date = new Date();
        long cd_l1 = date.getTime();
        if (messageListDTO.get(position).getCreated_at() != null) {
            SimpleDateFormat sdf;

            try {
                sdf = new SimpleDateFormat(OutLook.input);
            } catch (Exception e) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            }
            try {
                Date msg_Date = sdf.parse(messageListDTO.get(position).getCreated_at());
                long l1 = msg_Date.getTime();
                long diff = cd_l1 - l1;
                if (diff < 86400000) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                    String d2 = sdf1.format(msg_Date);
                    holder.tvTime.setText("" + d2);
                } else {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yyyy");
                    String d2 = sdf1.format(msg_Date);
                    holder.tvTime.setText("" + d2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {
            //   if (messageListDTO.get(position).getMessage() != null && !messageListDTO.get(position).getMessage().isEmpty()) {
            try {

                switch (messageListDTO.get(position).getMsg_type().toUpperCase()) {
                    case Constant.text:
                        byte[] data = Base64.decode(messageListDTO.get(position).getMessage(), Base64.NO_PADDING);
                        String text = new String(data, "UTF-8");
                        holder.tvMsg.setText(text);
                        break;
                    default:
                        holder.tvMsg.setText(messageListDTO.get(position).getMsg_type());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//                holder.tvMsg.setText(messageListDTO.get(position).getMessage());
            //    }
            //else {
            //  holder.tvMsg.setText("No Message");
            //}

            holder.tvTitle.setText("" + messageListDTO.get(position).getName());
            if (messageListDTO.get(position).getFilename() != null) {
                Picasso.with(context).load(Constant.IMAGE_SERVER_PATH + messageListDTO.get(position).getFilename()).resize(150, 150).placeholder(R.drawable.user).error(R.drawable.user).into(holder.ivProfilePicture);

            } else {
                Picasso.with(context).load(R.drawable.logo).error(R.drawable.logo).into(holder.ivProfilePicture);

            }


//            if (messageListDTO.get(position).getChatonline() != null && messageListDTO.get(position).getChatonline().equals("1")) {
//                holder.ivOnline.setImageDrawable(context.getResources().getDrawable(R.drawable.on));
//            } else {
//                holder.ivOnline.setImageDrawable(context.getResources().getDrawable(R.drawable.off));
//            }


            if (messageListDTO.get(position).getIsOnline() != null && messageListDTO.get(position).getIsOnline().equals("1")) {
                if (messageListDTO.get(position).getOnlineHalfHour() != null && messageListDTO.get(position).getOnlineHalfHour().equalsIgnoreCase("online")) {
                    holder.ivOnline.setImageDrawable(context.getResources().getDrawable(R.drawable.on));
                } else {
                    holder.ivOnline.setImageDrawable(context.getResources().getDrawable(R.drawable.on));
                    holder.ivOnline.setColorFilter(context.getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                }
            } else {
                holder.ivOnline.setImageDrawable(context.getResources().getDrawable(R.drawable.off));
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }


        if (messageListDTO.get(position).

                getUnread_msg_count() != null && !messageListDTO.get(position).

                getUnread_msg_count().

                equals(""))

        {
            try {
                int count = Integer.valueOf(messageListDTO.get(position).getUnread_msg_count());
                if (count > 0) {
                    holder.tvMsgCount.setVisibility(View.VISIBLE);
                    holder.tvMsgCount.setText(count + "");

                } else {
                    holder.tvMsgCount.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                }


            } catch (Exception e) {

            }
        }

        holder.itemView.setOnClickListener(new

                OnItemClickListener(position, onItemClickCallback));
    }

    @Override
    public int getItemCount() {
        return messageListDTO.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMsgCount, tvTitle, tvTime, tvMsg;
        View view;
        public LinearLayout ll;
        private ImageView ivOnline;
        public RoundedImageView ivProfilePicture;


        public MyViewHolder(View itemView) {

            super(itemView);
            tvMsgCount = (TextView) itemView.findViewById(R.id.tv_msg_count);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_date);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
//            view = (View) itemView.findViewById(R.id.view);
//            ll = (LinearLayout) itemView.findViewById(R.id.ll);
            ivProfilePicture = (RoundedImageView) itemView.findViewById(R.id.iv_profile);
            ivOnline = (ImageView) itemView.findViewById(R.id.iv_online);

            tvMsg.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            ivOnline.setVisibility(View.VISIBLE);

        }

    }

    public void filter(ArrayList<MessageDTO> newList) {
        messageListDTO = new ArrayList<>();
        messageListDTO.addAll(newList);
        notifyDataSetChanged();
    }

}

