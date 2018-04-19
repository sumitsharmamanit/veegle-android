package com.datingapp.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.EditProfileNext;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ProfileModel> profileModelArrayList;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback, onItemLike, onItemDisLike, onItemReject, onItemAccept, onItemDelete;
    private boolean status = false;
    private String url = "", spinnerReason = "";
    ;
    private ProgressDialog progressDialog;
    private ManageSession manageSession;
    private ArrayList<BlogDTO> educationList;

    public HomeAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, OnItemClickListener.OnItemClickCallback onItemLike, OnItemClickListener.OnItemClickCallback onItemDisLike) {
        super();
        this.context = context;
        this.profileModelArrayList = profileModelArrayList;
        this.onItemLike = onItemLike;
        this.onItemDisLike = onItemDisLike;
        this.onItemClickCallback = onItemClickCallback;
        manageSession = new ManageSession(context);
        status = false;
    }

    public HomeAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, OnItemClickListener.OnItemClickCallback onItemReject, OnItemClickListener.OnItemClickCallback onItemAccept, OnItemClickListener.OnItemClickCallback onItemDelete, String url) {
        super();
        this.context = context;
        this.profileModelArrayList = profileModelArrayList;
        this.onItemClickCallback = onItemClickCallback;
        status = true;
        manageSession = new ManageSession(context);
        this.url = url;
        this.onItemAccept = onItemAccept;
        this.onItemDelete = onItemDelete;
        this.onItemReject = onItemReject;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (status) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_one, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_home, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (status) {
            if (url.equals(Constant.MATCH)) {
                holder.tvName.setText(profileModelArrayList.get(position).getUserfullname());
                holder.tvMin.setVisibility(View.GONE);
            } else if (url.equals(Constant.AUTO_MATCH)) {
                holder.tvName.setText(profileModelArrayList.get(position).getUserfullname());
                holder.tvMin.setText(new OutLook().getUpdateTime(profileModelArrayList.get(position).getCreatedDate(), context));
            } else {
                holder.tvName.setText(profileModelArrayList.get(position).getUsername());
                try {
                    if (url.equals(Constant.INVITATION_LIST)) {
                        holder.tvMin.setText(profileModelArrayList.get(position).getMessage() + ", " + new OutLook().getUpdateTime(profileModelArrayList.get(position).getCreatedDate(), context));

                        if (profileModelArrayList.get(position).getInvitionStatus() != null && profileModelArrayList.get(position).getInvitionStatus().equals("accept")) {
                            holder.llReject.setVisibility(View.GONE);
                        } else {
                            holder.llReject.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.llReject.setVisibility(View.GONE);
                        holder.tvMin.setText(new OutLook().getUpdateTime(profileModelArrayList.get(position).getCreatedDate(), context));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (profileModelArrayList.get(position).getProfileImage() != null) {
                if (profileModelArrayList.get(position).getSocialType() != null && !profileModelArrayList.get(position).getSocialType().equals("1")) {
                    String to = profileModelArrayList.get(position).getProfileImage().replaceAll("(?<!http:)//", "/");
                    Picasso.with(context).load(to).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.user)).placeholder(context.getResources().getDrawable(R.drawable.user)).into(holder.ivProfile);
                } else {
                    Picasso.with(context).load(profileModelArrayList.get(position).getProfileImage()).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.user)).placeholder(context.getResources().getDrawable(R.drawable.user)).into(holder.ivProfile);
                }
            }

            if (url.equals(Constant.BLOCK_LIST)) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        public boolean onClick(View view) {
                        dialogBlock(position);
//                            return false;
//                        }
                    }
                });
            } else {
                holder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
            }

            holder.tvAccept.setOnClickListener(new OnItemClickListener(position, onItemAccept));
            holder.tvReject.setOnClickListener(new OnItemClickListener(position, onItemReject));
            holder.tvDelete.setOnClickListener(new OnItemClickListener(position, onItemDelete));
        } else {
            holder.ivFavorite.setOnClickListener(new OnItemClickListener(position, onItemLike));
            holder.ivUnFavorite.setOnClickListener(new OnItemClickListener(position, onItemDisLike));
            holder.tvName.setText(profileModelArrayList.get(position).getUserfullname());
            holder.tvPercentage.setText(profileModelArrayList.get(position).getPercentage());
            if (profileModelArrayList.get(position).getLikeStatus() != null && profileModelArrayList.get(position).getLikeStatus().equals("1") && profileModelArrayList.get(position).getLikeStatusnearby() != null && profileModelArrayList.get(position).getLikeStatusnearby().equals("1")) {
                holder.ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_4));
            } else if (profileModelArrayList.get(position).getLikeStatus() != null && profileModelArrayList.get(position).getLikeStatus().equals("1") && profileModelArrayList.get(position).getLikeStatusnearby() != null && (profileModelArrayList.get(position).getLikeStatusnearby().equals("0") || profileModelArrayList.get(position).getLikeStatusnearby().equals("2"))) {
                holder.ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_1));
            } else if (profileModelArrayList.get(position).getLikeStatus() != null && profileModelArrayList.get(position).getLikeStatus().equals("0") && profileModelArrayList.get(position).getLikeStatusnearby() != null && profileModelArrayList.get(position).getLikeStatusnearby().equals("1")) {
                holder.ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_3));
            } else {
                holder.ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_2));
            }

            if (profileModelArrayList.get(position).getGender().equalsIgnoreCase("Male")) {
                if (profileModelArrayList.get(position).getProfileImage() != null) {
                    if (profileModelArrayList.get(position).getSocialType() != null && !profileModelArrayList.get(position).getSocialType().equals("1")) {
                        String to = profileModelArrayList.get(position).getProfileImage().replaceAll("(?<!http:)//", "/");
                        Picasso.with(context).load(to).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.male)).placeholder(context.getResources().getDrawable(R.drawable.male)).into(holder.ivProfile);
                    } else {
                        Picasso.with(context).load(profileModelArrayList.get(position).getProfileImage()).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.male)).placeholder(context.getResources().getDrawable(R.drawable.male)).into(holder.ivProfile);
                    }
                }
            } else {
                if (profileModelArrayList.get(position).getProfileImage() != null) {
                    if (profileModelArrayList.get(position).getSocialType() != null && !profileModelArrayList.get(position).getSocialType().equals("1")) {
                        String to = profileModelArrayList.get(position).getProfileImage().replaceAll("(?<!http:)//", "/");
                        Picasso.with(context).load(to).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.female)).placeholder(context.getResources().getDrawable(R.drawable.female)).into(holder.ivProfile);
                    } else {
                        Picasso.with(context).load(profileModelArrayList.get(position).getProfileImage()).resize(250, 250).centerCrop().error(context.getResources().getDrawable(R.drawable.female)).placeholder(context.getResources().getDrawable(R.drawable.female)).into(holder.ivProfile);
                    }
                }
            }
            holder.ivProfile.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
        }


        if (profileModelArrayList.get(position).getIsOnline() != null && profileModelArrayList.get(position).getIsOnline().equals("1")) {
            if (profileModelArrayList.get(position).getOnlineHalfHour() != null && profileModelArrayList.get(position).getOnlineHalfHour().equalsIgnoreCase("online")) {
                holder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.on));
            } else  if (profileModelArrayList.get(position).getOnlineHalfHour() != null && profileModelArrayList.get(position).getOnlineHalfHour().equalsIgnoreCase("offline")) {
//                holder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.on));
                holder.ivStatus.setColorFilter(context.getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
            } else  if (profileModelArrayList.get(position).getOnlineHalfHour() != null && profileModelArrayList.get(position).getOnlineHalfHour().equalsIgnoreCase("offlineseven")) {
                holder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.off));
            }
        } else {
            holder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.off));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return profileModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvPercentage, tvMin, tvAccept, tvReject, tvDelete, tvMsg;
        private ImageView ivUnFavorite, ivFavorite, ivStatus;
        private RoundedImageView ivProfile;
        private LinearLayout llReject;
        private View viewReject;

        public ViewHolder(View itemView) {
            super(itemView);
            ivStatus = (ImageView) itemView.findViewById(R.id.iv_online);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivProfile = (RoundedImageView) itemView.findViewById(R.id.iv_profile);
            if (status) {
                tvMin = (TextView) itemView.findViewById(R.id.tv_time);
                llReject = (LinearLayout) itemView.findViewById(R.id.ll_reject);
                viewReject = (View) itemView.findViewById(R.id.view_reject);
                tvAccept = (TextView) itemView.findViewById(R.id.tv_accept);
                tvReject = (TextView) itemView.findViewById(R.id.tv_reject);
                tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);

                tvMsg = (TextView) itemView.findViewById(R.id.tv_msg);

                tvMin.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            } else {
                tvPercentage = (TextView) itemView.findViewById(R.id.tv_percentage);
                ivFavorite = (ImageView) itemView.findViewById(R.id.iv_like);
                ivUnFavorite = (ImageView) itemView.findViewById(R.id.iv_dislike);
            }
        }
    }

    public void dialogBlock(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_user);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no);


        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.red));
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnYes.setBackground(gradientDrawable1);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(context.getResources().getColor(R.color.bg_btn));
        gradientDrawable2.setCornerRadius(dpAsPixels);
        btnNo.setBackground(gradientDrawable2);

        ImageView ivCancel = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText("Are you sure you want to unblock this user?");

  /*      final RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_education);

        final SearchableSpinner spEducation = (SearchableSpinner) dialog.findViewById(R.id.sp_education);
        spEducation.setTitle("Select Reason");
        spEducation.setPositiveButton("Close");

        educationList = new ArrayList<>();
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setName("Pretending to someone");
        educationList.set(0, blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Fake Account");
        educationList.set(1, blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Nudity");
        educationList.set(2, blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Abusive");
        educationList.set(3, blogDTO);

        spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerReason = educationList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        spEducation.onTouch(relativeLayout.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
            }
        });

        ArrayAdapter<BlogDTO> oriAdapter = new ArrayAdapter<BlogDTO>(context,
                R.layout.spinner_textview, educationList);
        spEducation.setAdapter(oriAdapter);

        spEducation.setSelection(0);
*/

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                blockUser(dialog, position);
                blockUserApi(dialog, position);

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void blockUserApi(final Dialog dialog, final int position) {
//        new OutLook().hideKeyboard(context);
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("blocked_user_id", profileModelArrayList.get(position).getBlockUserId());
        map.put("reason", "unblock");
        map.put("feedbackreason", "");
        map.put("anotherreason", "");


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.blockUserApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String str = sb.toString();
                    JSONObject jsonObject = null;
                    Log.e("LogView", "" + str.toString());
                    try {
                        jsonObject = new JSONObject(str);
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if (status.equals("1")) {
                            dialog.dismiss();
                            profileModelArrayList.remove(position);
                            notifyDataSetChanged();
                        } else {
                            dialog.dismiss();
//                                Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
//                Snackbar.make(ho, context.getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

}