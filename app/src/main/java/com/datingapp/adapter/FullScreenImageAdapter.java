package com.datingapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.datingapp.R;
import com.datingapp.util.CommonUtils;
import com.datingapp.util.Constant;
import com.datingapp.util.OnItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


public class FullScreenImageAdapter extends PagerAdapter {

    private Context context;
    private List<String> _imagePaths;
    private LayoutInflater inflater;
    private OnItemClickListener.OnItemClickCallback onItemClickCross;
    private boolean details;
    private View.OnClickListener listener;

    public FullScreenImageAdapter(Context context, List<String> imagePaths, View.OnClickListener listener,OnItemClickListener.OnItemClickCallback onItemClickCross) {
        this.context = context;
        this._imagePaths = imagePaths;
        this.details = false;
        this.listener = listener;
        this.onItemClickCross = onItemClickCross;
    }

    // constructor
    public FullScreenImageAdapter(Context context,
                                  List<String> imagePaths, OnItemClickListener.OnItemClickCallback onItemClickCross) {
        this.context = context;
        this._imagePaths = imagePaths;
        this.onItemClickCross = onItemClickCross;
        details = false;
    }

    // constructor
    public FullScreenImageAdapter(Context context,
                                  List<String> imagePaths, OnItemClickListener.OnItemClickCallback onItemClickCross, boolean detailss) {
        this.context = context;
        this._imagePaths = imagePaths;
        this.onItemClickCross = onItemClickCross;
        details = true;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView imgDisplay;
        ImageView btnClose;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imgDisplay = new PhotoView(context);

        if (details) {

            Picasso.with(context).load(_imagePaths.get(position)).into(imgDisplay);
            //this call is from DetailsFragmentNext

        } else {
            View view = inflater.inflate(R.layout.full_image_layout, container, false);
            ImageView iv_play = view.findViewById(R.id.iv_play);

            // this is from chatdetail fragment.
            try {
                String image = new JSONObject(_imagePaths.get(position)).getString("img");
                iv_play.setTag(image);

                if (listener != null) {
                    iv_play.setOnClickListener(listener);
                }

                if (isVideo(image)) {
                    /// show the play icon and make visibility visible.

                    iv_play.setVisibility(View.VISIBLE);
                    loadVideoThumbNail(image,view.<ImageView>findViewById(R.id.iv_image));

                } else {
                    //hide the play icon and make visibility gone.
                    iv_play.setVisibility(View.GONE);
                    loadImage(image, view.<ImageView>findViewById(R.id.iv_image));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        container.addView(imgDisplay, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return imgDisplay;
    }

    public boolean isVideo(String image) {
        try {
            if (image.endsWith(".mp4") || image.endsWith(".MP4")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadVideoThumbNail(String image, ImageView imgDisplay) {
        try {
            File file = CommonUtils.IsFileExist(image);
            if (!file.exists()) {
                CommonUtils.loadVideoThumbFromURL(context, imgDisplay, Constant.CHAT_FILES_PATH + image);
            } else {
                CommonUtils.loadVideoThumb(context, imgDisplay, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImage(String image, ImageView imgDisplay) {
        File file = CommonUtils.IsFileExist(image);
        if (file.exists())
            Glide.with(context).load(file).into(imgDisplay);
        else {
            Glide.with(context).load(Constant.CHAT_FILES_PATH + image).into(imgDisplay);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
