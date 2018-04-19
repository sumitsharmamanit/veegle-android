package com.datingapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.util.Constant;
import com.datingapp.util.FlowLayout;
import com.datingapp.util.OnItemClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


public class BlindDateAdapter extends PagerAdapter {

    private Context context;
    private List<ProfileModel> profileModelList;
    private LayoutInflater inflater;
    private OnItemClickListener.OnItemClickCallback onItemClickCross, onItemClickLike, onItemClickDislike;
    private RoundedImageView ivProfile;
    private TextView tvName, tvAddress, tvPercentage, tvNoHobbies;
    private EditText etMessage;
    private ImageView ivClose, ivLike, ivDislike;
    private FlowLayout flowLayout;

    // constructor
    public BlindDateAdapter(Context context,
                            List<ProfileModel> profileModelList, OnItemClickListener.OnItemClickCallback onItemClickCross, OnItemClickListener.OnItemClickCallback onItemClickLike, OnItemClickListener.OnItemClickCallback onItemClickDislike) {
        this.context = context;
        this.profileModelList = profileModelList;
        this.onItemClickCross = onItemClickCross;
        this.onItemClickLike = onItemClickLike;
        this.onItemClickDislike = onItemClickDislike;
    }

    @Override
    public int getCount() {
        return this.profileModelList.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup viewLayout = (ViewGroup) inflater.inflate(R.layout.list_item_viewpager, container, false);
        container.addView(viewLayout);

        ivClose = (ImageView) viewLayout.findViewById(R.id.iv_close);
        ivLike = (ImageView) viewLayout.findViewById(R.id.iv_like);
        ivDislike = (ImageView) viewLayout.findViewById(R.id.iv_dislike);
        ivProfile = (RoundedImageView) viewLayout.findViewById(R.id.iv_profile);

        tvName = (TextView) viewLayout.findViewById(R.id.tv_name);
        tvAddress = (TextView) viewLayout.findViewById(R.id.tv_address);
        tvPercentage = (TextView) viewLayout.findViewById(R.id.tv_percentage);

        tvNoHobbies = (TextView) viewLayout.findViewById(R.id.tv_no_hobbies);
        flowLayout = (FlowLayout) viewLayout.findViewById(R.id.flow_container);

        // close button click event
        ivClose.setOnClickListener(new OnItemClickListener(position, onItemClickCross));

        ivLike.setOnClickListener(new OnItemClickListener(position, onItemClickLike));
        ivDislike.setOnClickListener(new OnItemClickListener(position, onItemClickDislike));

        tvName.setText(profileModelList.get(position).getUserfullname());
        tvPercentage.setText(profileModelList.get(position).getPercentage());
        tvAddress.setText("(" + profileModelList.get(position).getAddress() + ")");

        if (profileModelList.get(position).getLikeStatus() != null && profileModelList.get(position).getLikeStatus().equals("1") && profileModelList.get(position).getLikeStatusnearby() != null && profileModelList.get(position).getLikeStatusnearby().equals("1")) {
            ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_4));
        } else if (profileModelList.get(position).getLikeStatus() != null && profileModelList.get(position).getLikeStatus().equals("1") && profileModelList.get(position).getLikeStatusnearby() != null && profileModelList.get(position).getLikeStatusnearby().equals("0")) {
            ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_1));
        } else if (profileModelList.get(position).getLikeStatus() != null && profileModelList.get(position).getLikeStatus().equals("0") && profileModelList.get(position).getLikeStatusnearby() != null && profileModelList.get(position).getLikeStatusnearby().equals("1")) {
            ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_3));
        } else {
            ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_2));
        }

        if (profileModelList.get(position).getProfileImage() != null) {
            Picasso.with(context).load(profileModelList.get(position).getProfileImage()).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.male)).placeholder(context.getResources().getDrawable(R.drawable.male)).into(ivProfile);
        }

        List<String> hobbiesList = Arrays.asList(profileModelList.get(position).getHobbies().split(","));
        if (!profileModelList.get(position).getHobbies().equals("")) {
            for (int i = 0; i < hobbiesList.size(); i++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                float scale = context.getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (3 * scale + 0.5f);
                int dpAsPixels1 = (int) (7 * scale + 0.5f);
                int dpAsPixels2 = (int) (2 * scale + 0.5f);
                int dpAsPixels3 = (int) (15 * scale + 0.5f);
                int dpAsPixels4 = (int) (1 * scale + 0.5f);
                lparams.setMargins(dpAsPixels, dpAsPixels2, dpAsPixels, dpAsPixels2);
                TextView textView = new TextView(context);
                textView.setPadding(dpAsPixels1, dpAsPixels4, dpAsPixels1, dpAsPixels);
                textView.setLayoutParams(lparams);
                textView.setText(hobbiesList.get(i));
                textView.setTextColor(context.getResources().getColor(R.color.black));
                textView.setTextSize(10);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(dpAsPixels3);
                drawable.setStroke(1, context.getResources().getColor(R.color.black));
                textView.setBackground(drawable);
//                textView.setBackground(getResources().getDrawable(R.drawable.button_bg_rounder_withborderline));
//                detailsFragmentNextBinding.flHobbies.addView(textView);

                Log.i(getClass().getName(), "Hobbies is " + textView.getText().toString());
//                detailsFragmentNextBinding.llNobbies.addView(textView);
//                flexboxLayout.addView(textView);

                flowLayout.addView(textView);

            }
        } else {
            tvNoHobbies.setVisibility(View.VISIBLE);
            flowLayout.setVisibility(View.GONE);
        }
        return viewLayout;
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

