package com.datingapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.CameraActivity;
import com.datingapp.EditProfileActivity;
import com.datingapp.HomeActivity;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.adapter.ImageAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.FragmentTimelineBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeLineFragment extends BaseFragment implements View.OnClickListener {

    private Context context;
    private ManageSession manageSession;
    private ProgressDialog progressDialog;
    private FragmentTimelineBinding fragmentTimelineBinding;
    private boolean status = false;
    private String userId = "", imagePath = "", statusText = "", userImage = "", userName = "";
    private GridLayoutManager gridLayoutManager;
    private List<ProfileModel.Friends> friendsList;
    private MultipartBody.Part profileImage = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    private ImageAdapter imageAdapter;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            if (getArguments().containsKey("activity")) {
                status = true;
            }

            if (getArguments().containsKey("id")) {
                userId = getArguments().getString("id");
                userImage = getArguments().getString("image");
                userName = getArguments().getString("name");

                Log.i(getClass().getName(), "userId > " + userId + " userImage > " + userImage + " userName > " + userName);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTimelineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        return fragmentTimelineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
        initView();
        setValues();

        if (status) {
            fragmentTimelineBinding.parentLayout.setBackground(null);
            fragmentTimelineBinding.rvList.setNestedScrollingEnabled(false);
        }
//        getTimeLineList();

        getTimeLineListApi();
    }

    private void setValues() {
        if (!status) {
            fragmentTimelineBinding.relParent.setVisibility(View.GONE);
            initToolBar();
            Picasso.with(context).load(userImage).resize(150, 150).centerCrop().into(fragmentTimelineBinding.ivProfile);
            fragmentTimelineBinding.tvUserName.setText(userName);

//            fragmentTimelineBinding.parentLayout.setBackground(null);
        } else {
            userId = manageSession.getProfileModel().getUser_id();
            Picasso.with(context).load(manageSession.getProfileModel().getProfileImage()).resize(150, 150).centerCrop().into(fragmentTimelineBinding.ivProfile);
            fragmentTimelineBinding.tvUserName.setText(manageSession.getProfileModel().getUserfullname());

            ((EditProfileActivity) context).parentLayout.setSmoothScrollingEnabled(true);
        }
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Timeline", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initView() {
        friendsList = new ArrayList<>();
        imageAdapter = new ImageAdapter(context, friendsList, onItemClickCallback, true);
        gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        fragmentTimelineBinding.rvList.setLayoutManager(gridLayoutManager);
        fragmentTimelineBinding.imgBack.setOnClickListener(this);
        fragmentTimelineBinding.ivPhoto.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(false);
        if (!status)
            initToolBar();
        switch (item.getItemId()) {
            case R.id.menu_live:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        menu.findItem(R.id.menu_live).setIcon(getResources().getDrawable(R.drawable.photo)).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        if (!status)
            initToolBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                ((EditProfileActivity) context).onBackPressed();
                break;
            case R.id.iv_photo:
                dialogBlog(context);
                break;
        }
    }


    public void getTimeLineListApi() {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", userId);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.showTimelineApi(map);
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

                        statusText = jsonObject.optString("timelinetext");

                        if (status.equals("1")) {
                            fragmentTimelineBinding.rvList.setVisibility(View.VISIBLE);
                            fragmentTimelineBinding.tvNorecord.setVisibility(View.GONE);

                            fragmentTimelineBinding.tvStatus.setText(statusText);
                            friendsList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<ProfileModel.Friends>>() {
                            }.getType());
                            if (friendsList.size() > 0) {
                                imageAdapter = new ImageAdapter(context, friendsList, onItemClickCallback, true);
                                fragmentTimelineBinding.rvList.setAdapter(imageAdapter);
                            }
                        } else {
//                                Snackbar.make(fragmentTimelineBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();

                            fragmentTimelineBinding.rvList.setVisibility(View.GONE);
                            fragmentTimelineBinding.tvNorecord.setVisibility(View.VISIBLE);
                            fragmentTimelineBinding.tvNorecord.setText(message);
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
                Snackbar.make(fragmentTimelineBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

        }
    };

    public void dialogBlog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_blog);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        TextView tvUpload = (TextView) dialog.findViewById(R.id.tv_upload);
        final EditText etBlog = (EditText) dialog.findViewById(R.id.et_blog);
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.iv_close);

        etBlog.setHint("status...");
        if (!statusText.equals(""))
            etBlog.setText(statusText);
//        etBlog.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        etBlog.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        etBlog.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                if (s.length() > 0) {
                    if (s.toString().toLowerCase().contains("facebook") || s.toString().toLowerCase().contains("official") || s.toString().toLowerCase().contains("linkedin") || s.toString().toLowerCase().contains("modified") || s.toString().toLowerCase().contains("snapchat")) {
                        etBlog.setText(s.subSequence(0, s.length() - 8));
                        etBlog.setSelection(s.length() - 8);
                    } else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        etBlog.setText(s.subSequence(0, s.length() - 7));
                        etBlog.setSelection(s.length() - 7);
                    } else if (s.toString().toLowerCase().contains("google")) {
                        etBlog.setText(s.subSequence(0, s.length() - 6));
                        etBlog.setSelection(s.length() - 6);
                    } else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        etBlog.setText(s.subSequence(0, s.length() - 5));
                        etBlog.setSelection(s.length() - 5);
                    } else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        etBlog.setText(s.subSequence(0, s.length() - 9));
                        etBlog.setSelection(s.length() - 9);
                    } else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        etBlog.setText(s.subSequence(0, s.length() - 4));
                        etBlog.setSelection(s.length() - 4);
                    } else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        etBlog.setText(s.subSequence(0, s.length() - 3));
                        etBlog.setSelection(s.length() - 3);
                    } else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+") || s.toString().toLowerCase().contains("ga") || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc") || s.toString().toLowerCase().contains("mt") || s.toString().toLowerCase().contains("rt") || s.toString().toLowerCase().contains("yt")) {
                        etBlog.setText(s.subSequence(0, s.length() - 2));
                        etBlog.setSelection(s.length() - 2);
                    } else if (s.toString().toLowerCase().contains("@")) {
                        etBlog.setText(s.subSequence(0, s.length() - 1));
                        etBlog.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())) {
                        etBlog.setText(s.subSequence(0, s.length() - 8));
                        etBlog.setSelection(s.length() - 8);
                    }
                }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

//        GradientDrawable gradientDrawable = new GradientDrawable();
//        gradientDrawable.setStroke(1, context.getResources().getColor(R.color.jumbo));
//        gradientDrawable.setCornerRadius(5);
//        tvUpload.setBackground(gradientDrawable);
//        etBlog.setBackground(gradientDrawable);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.btn_color));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnDone.setBackground(gradientDrawable1);

        new OutLook().hideKeyboard(getActivity());

        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dailogImageChooser(context);
                if (hasPermissions(context, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent1 = new Intent(context, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String blog = etBlog.getText().toString();
                if (!blog.equals("")) {
                    if (!imagePath.equals("")) {
                        addTimeLineImage(imagePath, blog, dialog);
                        dialog.dismiss();
                    } else {
                        Snackbar.make(fragmentTimelineBinding.parentLayout, "Please upload photo.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(fragmentTimelineBinding.parentLayout, "Please enter staus.", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    public void addTimeLineImage(final String path, String text, final Dialog dialog) {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);


        try {
            File profileImageFile = new File(path);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("timelineimage", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody type = RequestBody.create(MediaType.parse("description"), text);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.addTimeLine(user_id, app_token, profileImage, type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                    progressDialog.dismiss();
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equalsIgnoreCase("1")) {
                            fragmentTimelineBinding.rvList.setVisibility(View.VISIBLE);
                            fragmentTimelineBinding.tvNorecord.setVisibility(View.GONE);

                            statusText = jsonObject.optString("timelinetext");
                            fragmentTimelineBinding.tvStatus.setText(statusText);
                            ProfileModel profileModel = new ProfileModel();
                            ProfileModel.Friends friends = profileModel.new Friends();
                            friends.setTimelineimage(jsonObject.optString("timelineimage"));
//                            if (friendsList.size() > 0) {

                            friendsList.add(friends);

                            if (friendsList.size() == 1) {
                                Bundle bundle = new Bundle();
                                bundle.putString("activity", "activity");
                                TimeLineFragment timeLineFragment = new TimeLineFragment();
                                timeLineFragment.setArguments(bundle);
                                replaceFragmentWithoutBack1(R.id.edit_container, timeLineFragment, "TimeLineFragment");
                            } else {
                                imageAdapter.notifyDataSetChanged();
                            }
//                            } else {
//                                friendsList.add(friends);
//                                imageAdapter = new ImageAdapter(context, friendsList, onItemClickCallback, true);
//                                imageAdapter.notifyDataSetChanged();
//                            }

                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void replaceFragmentWithoutBack1(int containerViewId, Fragment fragment, String fragmentTag) {
        try {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerViewId, fragment, fragmentTag)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == SELECT_FILE)
//                onSelectFromGalleryResult(data);
//            else if (requestCode == REQUEST_CAMERA)
//                onCaptureImageResult(data);
//        }
        if (resultCode == 2) {
            if (data.hasExtra("image")) {
                if (!data.getStringExtra("image").equals("")) {
                    manageSession.setImagePath("");
                    imagePath = data.getStringExtra("image");
                    manageSession.setImagePath(imagePath);
                }

            }
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    CameraActivity.type = "";
                    Intent intent3 = new Intent(context, CameraActivity.class);
                    startActivityForResult(intent3, 123);
                } else {
                    Toast.makeText(context, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
