package com.datingapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.datingapp.Model.ChatMessageDTO;
import com.datingapp.R;
import com.datingapp.util.CommonUtils;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    public List<ChatMessageDTO> chatMessageDTOS;
    ManageSession appSession;
    String user_id;
    public ImageView ivCapture;
    private OnItemClickListener.OnItemClickCallback onImageMe;
    private OnItemClickListener.OnItemClickCallback onImageFrom;
    private View.OnClickListener onMediaClick;
    private static int videoViewSender = -256;
    private static int videViewReceiver = -257;
    private static int audioViewSender = -258;
    private static int audioViewReceiver = -259;
    private static int gifViewSender = -260;
    private static int gifViewReceiver = -261;
    private static int imageSender = -262;
    private static int imageReceiver = -263;
    private SeekBar.OnSeekBarChangeListener listener;

    Bitmap bitmap;
    //  private OnItemClickListener.OnItemClickCallback onimageclickcallback;

    public ChatAdapter(Context context,
                       OnItemClickListener.OnItemClickCallback onImageMe,
                       OnItemClickListener.OnItemClickCallback onImageFrom,
                       View.OnClickListener onMediaClick, SeekBar.OnSeekBarChangeListener listener
    ) {
        this.context = context;
        this.chatMessageDTOS = new ArrayList<>();
        this.onImageMe = onImageMe;
        this.onImageFrom = onImageFrom;
        appSession = new ManageSession(context);
        this.onMediaClick = onMediaClick;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == videoViewSender) {
            return new SenderVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout_video_sender, parent, false));
        } else if (viewType == videViewReceiver) {
            return new ReceiverVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout_video_receiver, parent, false));
        } else if (viewType == audioViewSender) {
            return new AudioSenderViewHolder(LayoutInflater.from(context).inflate(R.layout.play_audio_layout_sender, parent, false));
        } else if (viewType == audioViewReceiver) {
            return new AudioReceiverViewHolder(LayoutInflater.from(context).inflate(R.layout.play_audio__layout_receiver, parent, false));
        } else if (viewType == gifViewSender) {
            return new GIFSenderViewHolder(LayoutInflater.from(context).inflate(R.layout.gif_receiver, parent, false));
        } else if (viewType == gifViewReceiver) {
            return new GIFReceiverViewHolder(LayoutInflater.from(context).inflate(R.layout.gif_sender, parent, false));
        } else if (viewType == imageSender) {
            return new ImageSender(LayoutInflater.from(context).inflate(R.layout.image_sender, parent, false));
        } else if (viewType == imageReceiver) {
            return new ImageReceiver(LayoutInflater.from(context).inflate(R.layout.image_receiver, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).bind(position);
        } else if (holder instanceof ReceiverVideoViewHolder) {
            ((ReceiverVideoViewHolder) holder).bind(position);
        } else if (holder instanceof SenderVideoViewHolder) {
            ((SenderVideoViewHolder) holder).bind(position);
        } else if (holder instanceof AudioSenderViewHolder) {
            ((AudioSenderViewHolder) holder).bind(position);
        } else if (holder instanceof AudioReceiverViewHolder) {
            ((AudioReceiverViewHolder) holder).bind(position);
        } else if (holder instanceof GIFReceiverViewHolder) {
            ((GIFReceiverViewHolder) holder).bind(position);
        } else if (holder instanceof GIFSenderViewHolder) {
            ((GIFSenderViewHolder) holder).bind(position);
        } else if (holder instanceof ImageSender) {
            ((ImageSender) holder).bind(position);
        } else if (holder instanceof ImageReceiver) {
            ((ImageReceiver) holder).bind(position);
        }
        Log.e("date", "date->" + chatMessageDTOS.get(position).getCreated_at());
    }

    public class ImageReceiver extends RecyclerView.ViewHolder {
        public ImageView iv_sender_video, iv_status, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public ImageReceiver(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {
            iv_play.setTag(position);
            iv_play.setOnClickListener(onMediaClick);
            //here we have used uploading flag to manage download.
            // uploading == true means it is downloading
            File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

            if (!file.exists() && !chatMessageDTOS.get(position).getUploading()) { // while user downloaded the video file is set over here.

                CommonUtils.loadVideoThumbFromURL(context, iv_sender_video, Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());

                iv_play.setVisibility(View.VISIBLE);

                loader.setVisibility(View.GONE);

                iv_play.setImageResource(R.drawable.ic_file_download);

                iv_play.setContentDescription(Constant.download);

            } else if (!file.exists() && chatMessageDTOS.get(position).getUploading()) {

                loader.setVisibility(View.VISIBLE);

                iv_play.setVisibility(View.INVISIBLE);

            } else {

                loader.setVisibility(View.INVISIBLE);

                iv_play.setVisibility(View.INVISIBLE);

                iv_play.setImageResource(R.drawable.ic_play);

                iv_play.setContentDescription(Constant.play);

                Picasso.with(context).load(file).into(iv_sender_video);

            }

            if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                try {
                    String myDate = "";
                    if (chatMessageDTOS.get(position).getStatus() != null && chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("date")) {
                        myDate = chatMessageDTOS.get(position).getCreated_at();
                    } else {
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    }
                    tv_date.setText(myDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageMe));

        }
    }


    public class ReceiverVideoViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_sender_video, iv_status, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public ReceiverVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {

            iv_play.setTag(position);
            iv_play.setOnClickListener(onMediaClick);
            //here we have used uploading flag to manage download.
            // uploading == true means it is downloading
            File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

            if (!file.exists() && !chatMessageDTOS.get(position).getUploading()) { // while user downloaded the video file is set over here.

                CommonUtils.loadVideoThumbFromURL(context, iv_sender_video, Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());

                iv_play.setVisibility(View.VISIBLE);

                loader.setVisibility(View.GONE);

                iv_play.setImageResource(R.drawable.ic_file_download);

                iv_play.setContentDescription(Constant.download);

            } else if (!file.exists() && chatMessageDTOS.get(position).getUploading()) {

                loader.setVisibility(View.VISIBLE);

                iv_play.setVisibility(View.INVISIBLE);

            } else {

                loader.setVisibility(View.INVISIBLE);

                iv_play.setVisibility(View.VISIBLE);

                iv_play.setImageResource(R.drawable.ic_play);
                iv_play.setContentDescription(Constant.play);

                CommonUtils.loadVideoThumb(context, iv_sender_video, file);

            }

            if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                try {
                    String myDate = "";
                    if (chatMessageDTOS.get(position).getStatus() != null && chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("date")) {
                        myDate = chatMessageDTOS.get(position).getCreated_at();
                    } else {
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    }
                    tv_date.setText(myDate);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageMe));
        }
    }


    public class ImageSender extends RecyclerView.ViewHolder {
        public ImageView iv_sender_video, iv_status, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public ImageSender(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {

            iv_play.setOnClickListener(onMediaClick);
            iv_play.setTag(position);

            if (chatMessageDTOS.get(position).getUploading()) {

                CommonUtils.loadVideoThumb(context, iv_sender_video, new File(CommonUtils.folder_path + chatMessageDTOS.get(position).getOrig_name()));
                iv_play.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.VISIBLE);

            } else if (!chatMessageDTOS.get(position).getUploading() && chatMessageDTOS.get(position).getState().equals(Constant.error)) {
                // error while uploading.
                CommonUtils.loadVideoThumb(context, iv_sender_video, new File(CommonUtils.folder_path + chatMessageDTOS.get(position).getOrig_name()));
                iv_play.setVisibility(View.VISIBLE);
                loader.setVisibility(View.INVISIBLE);
                iv_play.setImageResource(R.drawable.ic_upload);
                iv_play.setContentDescription(Constant.upload);

            } else {

                iv_play.setVisibility(View.VISIBLE);
                // hide loader.
                File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

                if (file != null && file.exists()) {
                     CommonUtils.loadVideoThumb(context, iv_sender_video, file);
                 //   Picasso.with(context).load(file.getAbsolutePath()).into(iv_sender_video);
                    iv_play.setImageResource(R.drawable.ic_play);
                    iv_play.setContentDescription(Constant.play);
                    iv_play.setVisibility(View.INVISIBLE);

                } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {
                    CommonUtils.loadVideoThumbFromURL(context, iv_sender_video, Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());
                    iv_play.setImageResource(R.drawable.ic_file_download);
                    iv_play.setVisibility(View.VISIBLE);
                    iv_play.setContentDescription(Constant.download);
                }

                loader.setVisibility(View.GONE);
            }

            if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                try {
                    String myDate = "";
                    if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().isEmpty()) {
                        if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("send")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));
                        } else {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                        }

                        if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                        }
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    } else {
                        if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                        } else {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                        }
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    }
                    tv_date.setText(myDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageFrom));

        }
    }

    public class SenderVideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_sender_video, iv_status, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public SenderVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {
            iv_play.setOnClickListener(onMediaClick);
            iv_play.setTag(position);

            if (chatMessageDTOS.get(position).getUploading()) {

                CommonUtils.loadVideoThumb(context, iv_sender_video, new File(CommonUtils.folder_path + chatMessageDTOS.get(position).getOrig_name()));
                iv_play.setImageResource(R.drawable.ic_play);
                iv_play.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.VISIBLE);

            } else {

                iv_play.setVisibility(View.VISIBLE);
                // hide loader.
                File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

                if (file != null && file.exists()) {
                    CommonUtils.loadVideoThumb(context, iv_sender_video, file);
                    iv_play.setImageResource(R.drawable.ic_play);
                    iv_play.setContentDescription(Constant.play);
                } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {

                    CommonUtils.loadVideoThumbFromURL(context, iv_sender_video, Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());
                    iv_play.setImageResource(R.drawable.ic_file_download);
                    iv_play.setContentDescription(Constant.download);
                }

                loader.setVisibility(View.GONE);
            }

            if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                try {
                    String myDate = "";
                    if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().isEmpty()) {
                        if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("send")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));
                        } else {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                        }

                        if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                        }
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    } else {
                        if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                        } else {
                            iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                        }
                        myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                    }

                    tv_date.setText(myDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageMe));

        }
    }

    @Override
    public int getItemCount() {
        return chatMessageDTOS.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageDTOS.get(position).getType().equals(Constant.video) &&
                IsSender(position))
            return videoViewSender;
        else if (chatMessageDTOS.get(position).getType().equals(Constant.video) && !IsSender(position)) {
            return videViewReceiver;
        } else if (chatMessageDTOS.get(position).getType().equals(Constant.audio) && IsSender(position)) {
            // audio is from sender
            return audioViewSender;
        } else if (chatMessageDTOS.get(position).getType().equals(Constant.audio) && !IsSender(position)) {
            // audio is from sender
            return audioViewReceiver;
        } else if (chatMessageDTOS.get(position).getType().equals("IMAGE") && chatMessageDTOS.get(position).getOrig_name() != null && chatMessageDTOS.get(position).getOrig_name().endsWith(".gif") ||
                (chatMessageDTOS.get(position).getOrig_name().endsWith(".GIF"))
                        && IsSender(position)) {
            return gifViewSender;
        } else if (chatMessageDTOS.get(position).getType().equals(Constant.GIF) && chatMessageDTOS.get(position).getOrig_name() != null && (chatMessageDTOS.get(position).getOrig_name().endsWith(".gif") ||
                (chatMessageDTOS.get(position).getOrig_name().endsWith(".GIF"))) && !IsSender(position)) {
            return gifViewReceiver;
        } else if (chatMessageDTOS.get(position).getType().equals(Constant.image) && IsSender(position)) {
            return imageSender;
        } else if (chatMessageDTOS.get(position).getType().equals(Constant.image) && !IsSender(position)) {
            return imageReceiver;
        } else
            return position;
    }

    public boolean IsSender(int pos) {
        return chatMessageDTOS.get(pos).getSender_id().equals(appSession.getProfileModel().getUser_id());
    }


    public class GIFSenderViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_sender_video, iv_status, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public GIFSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {
            try {
                iv_play.setContentDescription(Constant.playGif);
                iv_play.setOnClickListener(onMediaClick);
                iv_play.setTag(position);

                if (chatMessageDTOS.get(position).getUploading()) {

                    CommonUtils.loadVideoThumb(context, iv_sender_video, new File(chatMessageDTOS.get(position).getOrig_name()));
                    //  iv_sender_video.setImageURI(Uri.fromFile(new File(chatMessageDTOS.get(position).getOrig_name())));
                    iv_play.setImageResource(R.drawable.ic_gif);
                    iv_play.setVisibility(View.INVISIBLE);
                    loader.setVisibility(View.VISIBLE);
                    chatMessageDTOS.get(position).setState(Constant.download);

                } else if (chatMessageDTOS.get(position).getState().equals(Constant.playGif)) {

                    iv_play.setImageResource(R.drawable.ic_gif);
                    iv_play.setVisibility(View.GONE); // remove this if you want a click base play.
                    CommonUtils.loadGIF(context, iv_sender_video, CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name()), position, ChatAdapter.this);

                } else {

                    iv_play.setVisibility(View.GONE); // remove this if you want a click base play.
                    //   iv_play.setVisibility(View.VISIBLE);
                    // hide loader.
                    File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

                    if (file != null && file.exists()) {

                        iv_sender_video.setImageURI(Uri.fromFile(file));
                        iv_play.setImageResource(R.drawable.ic_gif);
                        iv_play.setContentDescription(Constant.playGif);
                        chatMessageDTOS.get(position).setState(Constant.pauseGIF);

                    } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {

                        CommonUtils.loadVideoThumbFromURL(context, iv_sender_video, Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());
                        //  Picasso.with(context).load(Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name()).fit().into(iv_sender_video);
                        iv_play.setImageResource(R.drawable.ic_file_download);
                        iv_play.setContentDescription(Constant.download);
                        chatMessageDTOS.get(position).setState(Constant.download);
                    }

                    loader.setVisibility(View.GONE);
                }

                if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                    try {
                        String myDate = "";
                        if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().isEmpty()) {
                            if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("send")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));
                            } else {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                            }

                            if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                            }
                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        } else {
                            if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                            } else {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                            }
                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        }
                        tv_date.setText(myDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageMe));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class GIFReceiverViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_sender_video, iv_play;
        public TextView tv_date;
        public ProgressBar loader;

        public GIFReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_sender_video = itemView.findViewById(R.id.iv_sender_video);

            iv_play = itemView.findViewById(R.id.iv_play);
            tv_date = itemView.findViewById(R.id.tv_date);
            loader = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(int position) {
            try {
                if (chatMessageDTOS.get(position).getUploading()) {

                    //   CommonUtils.loadVideoThumb(context, iv_sender_video, new File(chatMessageDTOS.get(position).getOrig_name()));
                    iv_sender_video.setImageURI(Uri.fromFile(new File(chatMessageDTOS.get(position).getOrig_name())));
                    iv_play.setImageResource(R.drawable.ic_gif);
                    iv_play.setVisibility(View.INVISIBLE);
                    loader.setVisibility(View.VISIBLE);
                    chatMessageDTOS.get(position).setState(Constant.download);

                } else if (chatMessageDTOS.get(position).getState().equals(Constant.playGif)) {

                    CommonUtils.loadGIF(context, iv_sender_video, CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name()), position, ChatAdapter.this);

                } else {

                    iv_play.setVisibility(View.VISIBLE);
                    // hide loader.
                    File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());

                    if (file != null && file.exists()) {

                        iv_sender_video.setImageURI(Uri.fromFile(file));
                        iv_play.setImageResource(R.drawable.ic_gif);
                        iv_play.setContentDescription(Constant.playGif);
                        chatMessageDTOS.get(position).setState(Constant.playGif);

                    } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {

                        Picasso.with(context).load(Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name()).fit().into(iv_sender_video);
                        iv_play.setImageResource(R.drawable.ic_file_download);
                        iv_play.setContentDescription(Constant.download);
                        chatMessageDTOS.get(position).setState(Constant.download);

                    }

                    loader.setVisibility(View.GONE);

                }

                if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                    try {
                        String myDate = "";
                        if (chatMessageDTOS.get(position).getStatus() != null && chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("date")) {

                            myDate = chatMessageDTOS.get(position).getCreated_at();
                        } else {

                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        }
                        tv_date.setText(myDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                iv_sender_video.setOnClickListener(new OnItemClickListener(position, onImageMe));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AudioReceiverViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_start_time, tv_date, tv_end_time;
        private SeekBar seekbar;
        private ImageView iv_play, iv_status;
        private ProgressBar progress_bar;

        public AudioReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_status = itemView.findViewById(R.id.iv_status);
            seekbar = itemView.findViewById(R.id.seekbar);
            iv_play = itemView.findViewById(R.id.iv_play);
            progress_bar = itemView.findViewById(R.id.progress_bar);
        }

        public void setDuration(String start, String end) {
            this.tv_start_time.setText(start);
            this.tv_end_time.setText(end);
        }

        public void resetSeekBar() {
            try {
                seekbar.setMax(0);
                seekbar.setProgress(0);
                tv_start_time.setText("00:00");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("DefaultLocale")
        public void updateSeekBar(String state, int percent, int duration) {
            try {

                if (state.equals(Constant.play))
                    iv_play.setImageResource(R.drawable.ic_pause);
                else if (state.equals(Constant.pause)) {
                    iv_play.setImageResource(R.drawable.ic_play);
                } else {
                    iv_play.setImageResource(R.drawable.ic_play);
                }

                iv_play.setVisibility(View.VISIBLE);

                Log.e("seekBarMax", seekbar.getMax() + "<==>");
                Log.e("seekBarDuration", duration + "<====>");
                //   if (seekbar.getMax() <= 0) {
                seekbar.setMax(duration);
                Log.e("duration", "duration" + duration);
                //  }
                if (percent != seekbar.getProgress())
                    seekbar.setProgress(percent);
                if (percent >= 0)
                    tv_start_time.setText(CommonUtils.getTimeString(percent));
                else {
                    tv_start_time.setText("00:00");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void handleUploading() {
            try {
                Log.e("ifififif", "1");
                iv_play.setVisibility(View.INVISIBLE);
                progress_bar.setVisibility(View.VISIBLE);
                seekbar.setEnabled(false);
                setDuration("", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void handleAudioPlay(int position) {
            try {
                tv_end_time.setText(CommonUtils.getDuration(CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name()).getAbsolutePath()));
                updateSeekBar(chatMessageDTOS.get(position).getState(), chatMessageDTOS.get(position).getSeekPercentage(), chatMessageDTOS.get(position).getDuration());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void resetData(int position) {
            try {
                resetSeekBar();
                File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());
                iv_play.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);

                if (file != null && file.exists()) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    iv_play.setContentDescription(Constant.play);
                    seekbar.setEnabled(true);
                    setDuration("00:00", CommonUtils.getDuration(file.getAbsolutePath()));
                    Log.e("else 2", "true");
                } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {
                    iv_play.setImageResource(R.drawable.ic_file_download);
                    iv_play.setContentDescription(Constant.download);
                    seekbar.setEnabled(false);
                    setDuration("", "");
                }
                progress_bar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void bind(int position) {
            try {
                iv_play.setTag(position);
                iv_play.setOnClickListener(onMediaClick);
                seekbar.setTag(position);

                if (chatMessageDTOS.get(position).getSeekPercentage() == -1 && chatMessageDTOS.get(position).getUploading()) {

                    handleUploading();
                    seekbar.setOnSeekBarChangeListener(null);
                    seekbar.setContentDescription("0");

                } else if (chatMessageDTOS.get(position).getSeekPercentage() >= 0
                        && !chatMessageDTOS.get(position).getState().equals(Constant.stop)
                        && !chatMessageDTOS.get(position).getState().equals(Constant.pause)
                        && !chatMessageDTOS.get(position).getUploading()) {

                    if (listener != null && seekbar.getContentDescription().equals("0")) {
                        seekbar.setOnSeekBarChangeListener(listener);
                        seekbar.setContentDescription("1");
                    }

                    iv_play.setContentDescription(Constant.play);
                    handleAudioPlay(position);

                } else if (chatMessageDTOS.get(position).getState().equals(Constant.pause)) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    handleAudioPlay(position);
                } else {
                    seekbar.setOnSeekBarChangeListener(null);
                    seekbar.setContentDescription("0");
                    resetData(position);
                }

                if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                    try {
                        String myDate = "";
                        if (chatMessageDTOS.get(position).getStatus() != null && chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("date")) {
                            myDate = chatMessageDTOS.get(position).getCreated_at();
                        } else {
                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        }
                        tv_date.setText(myDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AudioSenderViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_start_time, tv_date, tv_end_time;
        private SeekBar seekbar;
        private ImageView iv_play, iv_status;
        private ProgressBar progress_bar;

        public AudioSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_status = itemView.findViewById(R.id.iv_status);
            seekbar = itemView.findViewById(R.id.seekbar);
            iv_play = itemView.findViewById(R.id.iv_play);
            progress_bar = itemView.findViewById(R.id.progress_bar);
            seekbar.setContentDescription("0");
            if (listener != null)
                seekbar.setOnSeekBarChangeListener(listener);
        }

        public void setDuration(String start, String end) {
            tv_start_time.setText(start);
            tv_end_time.setText(end);
            Log.d("end", end);
        }

        public void handleUploading() {
            try {
                Log.e("ifififif", "1");
                iv_play.setVisibility(View.INVISIBLE);
                progress_bar.setVisibility(View.VISIBLE);
                seekbar.setEnabled(false);
                setDuration("", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void handleAudioPlay(int position) {
            try {
                tv_end_time.setText(CommonUtils.getDuration(CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name()).getAbsolutePath()));
                updateSeekBar(chatMessageDTOS.get(position).getState(), chatMessageDTOS.get(position).getSeekPercentage(), chatMessageDTOS.get(position).getDuration());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void resetData(int position) {
            try {
                resetSeekBar();
                File file = CommonUtils.IsFileExist(chatMessageDTOS.get(position).getOrig_name());
                iv_play.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);

                if (file != null && file.exists()) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    iv_play.setContentDescription(Constant.play);
                    seekbar.setEnabled(true);
                    setDuration("00:00", CommonUtils.getDuration(file.getAbsolutePath()));
                    Log.e("else 2", "true");
                } else if (!chatMessageDTOS.get(position).getOrig_name().isEmpty()) {
                    iv_play.setImageResource(R.drawable.ic_file_download);
                    iv_play.setContentDescription(Constant.download);
                    seekbar.setEnabled(false);
                    setDuration("", "");
                }
                progress_bar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void resetSeekBar() {
            try {
                seekbar.setMax(0);
                seekbar.setProgress(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("DefaultLocale")
        public void updateSeekBar(String state, int percent, int duration) {
            try {

                if (state.equals(Constant.play))
                    iv_play.setImageResource(R.drawable.ic_pause);
                else if (state.equals(Constant.pause)) {
                    iv_play.setImageResource(R.drawable.ic_play);
                } else {
                    iv_play.setImageResource(R.drawable.ic_play);
                }

                iv_play.setVisibility(View.VISIBLE);

                Log.e("seekBarMax", seekbar.getMax() + "<==>");
                Log.e("seekBarDuration", duration + "<====>");
                //   if (seekbar.getMax() <= 0) {
                seekbar.setMax(duration);
                Log.e("duration", "duration" + duration);
                //  }
                if (percent != seekbar.getProgress())
                    seekbar.setProgress(percent);
                if (percent >= 0)
                    tv_start_time.setText(CommonUtils.getTimeString(percent));
                else {
                    tv_start_time.setText("00:00");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void bind(int position) {
            try {
                iv_play.setTag(position);
                iv_play.setOnClickListener(onMediaClick);
                seekbar.setTag(position);

                if (chatMessageDTOS.get(position).getSeekPercentage() == -1 && chatMessageDTOS.get(position).getUploading()) {

                    handleUploading();
                    seekbar.setOnSeekBarChangeListener(null);
                    seekbar.setContentDescription("0");

                } else if (chatMessageDTOS.get(position).getSeekPercentage() >= 0
                        && !chatMessageDTOS.get(position).getState().equals(Constant.stop)
                        && !chatMessageDTOS.get(position).getState().equals(Constant.pause)
                        && !chatMessageDTOS.get(position).getUploading()) {

                    if (listener != null && seekbar.getContentDescription().equals("0")) {
                        seekbar.setOnSeekBarChangeListener(listener);
                        seekbar.setContentDescription("1");
                    }

                    iv_play.setContentDescription(Constant.play);
                    handleAudioPlay(position);

                } else if (chatMessageDTOS.get(position).getState().equals(Constant.pause)) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    handleAudioPlay(position);
                } else {
                    seekbar.setOnSeekBarChangeListener(null);
                    seekbar.setContentDescription("0");
                    resetData(position);
                }


                if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().isEmpty()) {
                    try {
                        String myDate = "";
                        if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().isEmpty()) {
                            if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("send")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));
                            } else {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                            }

                            if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                            }
                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        } else {
                            if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                            } else {
                                iv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                            }
                            myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                        }
                        tv_date.setText(myDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_my_msg, tv_mychat_date, tv_otherchat_date, tv_otherchat_msg;
        ImageView ivCaptureFrom, ivCaptureMe, ivStatus;
        LinearLayout llChatOther, llChatMe, llImageMe, llImageOther;


        public MyViewHolder(View itemView) {
            super(itemView);

            ivStatus = (ImageView) itemView.findViewById(R.id.iv_status);
            ivCaptureMe = (ImageView) itemView.findViewById(R.id.iv_capture_me);
            ivCaptureFrom = (ImageView) itemView.findViewById(R.id.iv_capture_from);
            tv_my_msg = (TextView) itemView.findViewById(R.id.tv_my_msg);
            tv_mychat_date = (TextView) itemView.findViewById(R.id.tv_mychat_date);

            tv_otherchat_date = (TextView) itemView.findViewById(R.id.tv_otherchat_date);
            tv_otherchat_msg = (TextView) itemView.findViewById(R.id.tv_other_msg);

            llChatOther = (LinearLayout) itemView.findViewById(R.id.ll_chat_from);
            llChatMe = (LinearLayout) itemView.findViewById(R.id.ll_chat_me);
            llImageMe = (LinearLayout) itemView.findViewById(R.id.ll_image_me);
            llImageOther = (LinearLayout) itemView.findViewById(R.id.ll_image_other);

        }


        public void bind(int position) {
            try {
                user_id = appSession.getProfileModel().getUser_id();

                Date date = new Date();
                long cd_l1 = date.getTime();
//        Log.v("Base Image URl", " "+appSession.getUrls().getResult().getBaseUrl());
                if (user_id.equalsIgnoreCase(chatMessageDTOS.get(position).getSender_id())) {
                    llChatOther.setVisibility(View.GONE);
                    llChatMe.setVisibility(View.VISIBLE);
                    if (chatMessageDTOS.get(position).getOrig_name() != null &&

                            chatMessageDTOS.get(position).getType().equals(Constant.imagetype) &&

                            !chatMessageDTOS.get(position).getOrig_name().isEmpty()) {

                        try {
                            //  byte[] imageBytes = Base64.decode(chatMessageDTOS.get(position).getFile(), Base64.DEFAULT);
                            Picasso.with(context).load(Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name()).error(R.drawable.logo).into(ivCaptureMe);
                            Log.e("URL", Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getFile());
                            ivCaptureMe.setVisibility(View.VISIBLE);
                            llImageMe.setVisibility(View.VISIBLE);
                            tv_my_msg.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new OutLook().USER_VALUE++;
                    } else if (chatMessageDTOS.get(position).getMessage() != null && !chatMessageDTOS.get(position).getMessage().equalsIgnoreCase("")) {

                        try {
                            byte[] data = Base64.decode(chatMessageDTOS.get(position).getMessage(), Base64.DEFAULT);
                            String text = new String(data, "UTF-8");
                            tv_my_msg.setText(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                holder.tv_my_msg.setText(chatMessageDTOS.get(position).getMessage());
                        tv_my_msg.setVisibility(View.VISIBLE);

                        new OutLook().USER_VALUE++;

                    } else {
                        tv_my_msg.setVisibility(View.GONE);
                        Picasso.with(context).load(R.drawable.logo).error(R.drawable.logo).into(ivCaptureMe);
                        ivCaptureMe.setVisibility(View.GONE);
                        llImageMe.setVisibility(View.GONE);

                        llChatOther.setVisibility(View.GONE);
                        llChatMe.setVisibility(View.GONE);

//                new OutLook().USER_VALUE++;
                    }

                    if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().equalsIgnoreCase("")) {
                        try {
                            String myDate = "";
                            if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().equals("")) {
                                if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("send")) {
                                    ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));
                                } else {
                                    ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                                }

                                if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                    ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                                }
                                myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());

                            } else {
                                if (chatMessageDTOS.get(position).getMessageseen() != null && chatMessageDTOS.get(position).getMessageseen().equalsIgnoreCase("1")) {
                                    ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double));
                                } else {
                                    ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.single));
                                }
                                Log.d("createdAt", chatMessageDTOS.get(position).getCreated_at());
                                myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                            }
                            Log.e(getClass().getName(), "Date >>>>>>>>>>" + myDate);
                            Log.e(getClass().getName(), "chatMessageDTOS Date >>>>>>>>>>" + chatMessageDTOS.get(position).getCreated_at());

                            tv_mychat_date.setText("" + myDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } else {
                    llChatOther.setVisibility(View.VISIBLE);
                    llChatMe.setVisibility(View.GONE);


                    if (chatMessageDTOS.get(position).getOrig_name() != null && !chatMessageDTOS.get(position).getOrig_name().isEmpty()) {

                        try {
                            Log.e("path", Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name());
                            Picasso.with(context).load(Constant.CHAT_FILES_PATH + chatMessageDTOS.get(position).getOrig_name()).error(R.drawable.logo).into(ivCaptureFrom);
                            ivCaptureFrom.setVisibility(View.VISIBLE);
                            llImageOther.setVisibility(View.VISIBLE);
                            tv_otherchat_msg.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new OutLook().SENDER_VALUE++;
                    } else if (chatMessageDTOS.get(position).getMessage() != null &&
                            !chatMessageDTOS.get(position).getMessage().equalsIgnoreCase("")) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    holder.tv_otherchat_msg.setText(Html.fromHtml(chatMessageDTOS.get(position).getMessage(), Html.FROM_HTML_MODE_COMPACT));
//                } else {
//                    holder.tv_otherchat_msg.setText(Html.fromHtml(chatMessageDTOS.get(position).getMessage()));
//                }


                        try {
                            byte[] data = Base64.decode(chatMessageDTOS.get(position).getMessage(), Base64.DEFAULT);
                            String text = new String(data, "UTF-8");
                            tv_otherchat_msg.setText(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                holder.tv_otherchat_msg.setText(chatMessageDTOS.get(position).getMessage());
                        tv_otherchat_msg.setVisibility(View.VISIBLE);

                        new OutLook().SENDER_VALUE++;

                    } else {
                        tv_otherchat_msg.setVisibility(View.GONE);
                        Picasso.with(context).load(R.drawable.logo).error(R.drawable.logo).into(ivCaptureFrom);
                        ivCaptureFrom.setVisibility(View.GONE);
                        llImageOther.setVisibility(View.GONE);

                        llChatOther.setVisibility(View.GONE);
                        llChatMe.setVisibility(View.GONE);

//                new OutLook().SENDER_VALUE++;
                    }


                    if (chatMessageDTOS.get(position).getCreated_at() != null && !chatMessageDTOS.get(position).getCreated_at().equalsIgnoreCase("")) {
                        try {
                            String myDate = "";
                            if (chatMessageDTOS.get(position).getStatus() != null && !chatMessageDTOS.get(position).getStatus().equals("")) {
                                if (chatMessageDTOS.get(position).getStatus().equalsIgnoreCase("date")) {
                                    myDate = chatMessageDTOS.get(position).getCreated_at();
                                } else {
                                    myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                                }
                            } else {
                                myDate = new OutLook().formateCal(chatMessageDTOS.get(position).getCreated_at());
                            }

                            tv_otherchat_date.setText("" + myDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

                ivCaptureFrom.setOnClickListener(new OnItemClickListener(position, onImageFrom));
                ivCaptureMe.setOnClickListener(new OnItemClickListener(position, onImageMe));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void add(ChatMessageDTO message) {
        chatMessageDTOS.add(message);
    }

    public void addimage(ChatMessageDTO imagestring) {
        chatMessageDTOS.add(imagestring);
    }

    public void add(List<ChatMessageDTO> messages) {
        chatMessageDTOS.addAll(messages);
    }

    private void saveToInternalStorage(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Veegle");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Veegle/media/images");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Veegle/media/images"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            //out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 10, out);

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}

