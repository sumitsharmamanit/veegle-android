package com.datingapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.CameraActivity;
import com.datingapp.HomeActivity;
import com.datingapp.MapActivity;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.WaitingListActivity;
import com.datingapp.adapter.BlogAdapter;
import com.datingapp.adapter.HomeAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationListFragment extends BaseFragment {

    private RecyclerView rvHome;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private ProgressDialog progressDialog;
    //    private DataBase dataBase;
    private RelativeLayout parentLayout;
    private ArrayList<ProfileModel> profileModelArrayList;
    private ArrayList<BlogDTO> blogDTOArrayList;
    private String url = "", imagePath = "";
    private MultipartBody.Part profileImage = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    private ManageSession manageSession;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private boolean waitingStatus = false;
    private RelativeLayout rlToolBar;
    private ImageView ivBack;
    private TextView toolbarText;
    private HomeAdapter homeAdapter;
    private TextView tvNoRecord;
//    private Socket socket;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.PROFILE_VISIT_LIST)) {
                url = getArguments().getString(Constant.PROFILE_VISIT_LIST);
            }

            if (getArguments().containsKey(Constant.LIKE_LIST)) {
                url = getArguments().getString(Constant.LIKE_LIST);
            }

            if (getArguments().containsKey(Constant.BLOCK_LIST)) {
                url = getArguments().getString(Constant.BLOCK_LIST);
            }

            if (getArguments().containsKey(Constant.MATCH)) {
                url = getArguments().getString(Constant.MATCH);
            }

            if (getArguments().containsKey(Constant.SHOW_BLOG)) {
                if (getArguments().containsKey("activity")) {
                    waitingStatus = true;
                }
                url = getArguments().getString(Constant.SHOW_BLOG);
            }

            if (getArguments().containsKey(Constant.INVITATION_LIST)) {
                url = getArguments().getString(Constant.INVITATION_LIST);
            }

            if (getArguments().containsKey(Constant.AUTO_MATCH)) {
                url = getArguments().getString(Constant.AUTO_MATCH);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rv_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        initView(view);

        if (waitingStatus) {
            rlToolBar.setVisibility(View.VISIBLE);
            toolbarText.setText("Blog");
            rvHome.setNestedScrollingEnabled(false);
        } else {
            initToolBar();

            rvHome.setPadding(6, 6, 6, 80);
            rvHome.setClipToPadding(false);
        }

        if (url.equals(Constant.SHOW_BLOG)) {
//            getBlogList(url);
        /*    try {
                DatingApp app = (DatingApp) getActivity().getApplication();
                socket = app.getSocket();

                Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());
                try {
                    JSONObject jobject = new JSONObject();
                    jobject.put("id", manageSession.getProfileModel().getUser_id());
                    Log.v("offline", "==> " + jobject);
                    socket.emit("offline", jobject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("User Socket Exception", " " + e);
            }*/

            getBlogListApi();
        } else {
//            getList(url);
            getListApi();
        }
    }


    private void initView(View view) {
        manageSession = new ManageSession(context);
        profileModelArrayList = new ArrayList<>();
        blogDTOArrayList = new ArrayList<>();
//        dataBase = new DataBase(context);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
        rvHome = (RecyclerView) view.findViewById(R.id.rv_home);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHome.setLayoutManager(linearLayoutManager);

        rlToolBar = (RelativeLayout) view.findViewById(R.id.relParent);
        ivBack = (ImageView) view.findViewById(R.id.imgBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popFragment();
                ((WaitingListActivity) context).showWaitData();
            }
        });

        toolbarText = (TextView) view.findViewById(R.id.tv_text);

        tvNoRecord = (TextView) view.findViewById(R.id.tv_norecord);


    }

    public void popFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        if (url.equals(Constant.SHOW_BLOG)) {
            ((HomeActivity) getActivity()).createBackButton("Blog", false);
        } else if (url.equals(Constant.PROFILE_VISIT_LIST)) {
            ((HomeActivity) getActivity()).createBackButton("Profile Visit List", true);
        } else if (url.equals(Constant.LIKE_LIST)) {
            ((HomeActivity) getActivity()).createBackButton("Like List", true);
        } else if (url.equals(Constant.BLOCK_LIST)) {
            ((HomeActivity) getActivity()).createBackButton("Block List", true);
        } else if (url.equals(Constant.MATCH)) {
            ((HomeActivity) getActivity()).createBackButton("Match List", true);
        } else if (url.equals(Constant.AUTO_MATCH)) {
            ((HomeActivity) getActivity()).createBackButton("Auto-Match List", true);
        } else if (url.equals(Constant.INVITATION_LIST)) {
            ((HomeActivity) getActivity()).createBackButton("Invitation List", true);
        }
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void getListApi() {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = null;
        if (url.equals(Constant.PROFILE_VISIT_LIST)) {
            call = apiInterface.profileVisitApi(map);
        } else if (url.equals(Constant.LIKE_LIST)) {
            call = apiInterface.getLikeList(map);
        } else if (url.equals(Constant.BLOCK_LIST)) {
            call = apiInterface.getBlockUserApi(map);
        } else if (url.equals(Constant.MATCH)) {
            call = apiInterface.matchListApi(map);
        } else if (url.equals(Constant.INVITATION_LIST)) {
            call = apiInterface.getInviteList(map);
        } else if (url.equals(Constant.AUTO_MATCH)) {
            call = apiInterface.getAutoMatchList(map);
        }
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
                            profileModelArrayList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<ProfileModel>>() {
                            }.getType());
                            homeAdapter = new HomeAdapter(context, profileModelArrayList, onItemClickListener, onItemClickReject, onItemClickAccept, onItemClickDelete, url);
                            rvHome.setAdapter(homeAdapter);
                        } else {
//                                Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();

                            tvNoRecord.setText(message);
                            tvNoRecord.setVisibility(View.VISIBLE);
                            rvHome.setVisibility(View.GONE);
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
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    OnItemClickListener.OnItemClickCallback onItemClickListener = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            if (!url.equals(Constant.SHOW_BLOG)) {
                Bundle bundle = new Bundle();
                DetailsFragment detailsFragment = new DetailsFragment();
                bundle.putString("id", profileModelArrayList.get(position).getUser_id());
                bundle.putString("position", position + "");
                detailsFragment.setArguments(bundle);
//            replaceFragmentWithBack(R.id.output, detailsFragment, "DetailsFragment", "HomeFragment");

                addFragmentWithoutRemove(R.id.output, detailsFragment, "DetailsFragment");
            }
        }
    };

    OnItemClickListener.OnItemClickCallback onItemClickReject = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

            acceptOrRejectAndDelete(profileModelArrayList.get(position).getUser_id(), profileModelArrayList.get(position).getInviteunique(), "reject", position, view, profileModelArrayList.get(position).getUsername(), profileModelArrayList.get(position).getProfileImage());
        }
    };

    OnItemClickListener.OnItemClickCallback onItemClickAccept = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
//            LinearLayout linearLayout
            acceptOrRejectAndDelete(profileModelArrayList.get(position).getUser_id(), profileModelArrayList.get(position).getInviteunique(), "accept", position, view, profileModelArrayList.get(position).getUsername(), profileModelArrayList.get(position).getProfileImage());
        }
    };

    OnItemClickListener.OnItemClickCallback onItemClickDelete = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

        }
    };


    public void getBlogListApi() {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.getBlogApi(map);
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
                            blogDTOArrayList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<BlogDTO>>() {
                            }.getType());
                            rvHome.setAdapter(new BlogAdapter(context, blogDTOArrayList, onItemClickListener, false));
                        } else {
//                                Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();

                            tvNoRecord.setText(message);
                            tvNoRecord.setVisibility(View.VISIBLE);
                            rvHome.setVisibility(View.GONE);
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
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (url.equals(Constant.SHOW_BLOG))
            menu.findItem(R.id.menu_live).setIcon(R.drawable.options).setVisible(true);
        else
            menu.findItem(R.id.menu_live).setVisible(true);

        ((HomeActivity) context).bottomVisibility(true);
        initToolBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_live:
                if (url.equals(Constant.SHOW_BLOG))
                    dialogBlog(getActivity());
                else
                    startActivity(new Intent(getActivity(), MapActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
                        try {
                            byte[] data = blog.getBytes("UTF-8");
                            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            updateBlog(imagePath, base64, dialog);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Snackbar.make(parentLayout, "Please upload photo.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(parentLayout, "Please enter blog text.", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
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

    public void updateBlog(final String path, String text, final Dialog dialog) {
//        progressDialog = new ProgressDialog(context,
//                R.style.AppTheme_Dark_Dialog1);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage(context.getResources().getString(R.string.loading_txt));
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        Log.i(getClass().getName(), "id " + manageSession.getProfileModel().getUser_id() + " path " + path + " text " + text);

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
            profileImage = MultipartBody.Part.createFormData("blogimage", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody type = RequestBody.create(MediaType.parse("description"), text);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
//        Call<ResponseBody> call = apiInterface.updateBlog(user_id, app_token, profileImage, type);
        Call<ResponseBody> call = apiInterface.addBlog(user_id, app_token, profileImage, type);
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
                            Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
//                            Toast.makeText(context, response1.toString(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {
                            Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LogView", "" + call.toString());
                t.printStackTrace();
//                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
//                Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    //function for choose the image from gallery or camera
    public void dailogImageChooser(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_chooser);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        TextView tvHeader = (TextView) dialog.findViewById(R.id.tv_header);
        TextView tvGallery = (TextView) dialog.findViewById(R.id.tv_gallery);
        TextView tvCamera = (TextView) dialog.findViewById(R.id.tv_camera);
//        tvHeader.setText(header);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(getResources().getString(R.string.app_name) + Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        if (!destination.exists()) {
            destination.mkdir();
        }

        Uri tempUri = getImageUri(getActivity(), thumbnail);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imagePath = getRealPathFromURI(tempUri);
//            ivProfileOne.setImageBitmap(thumbnail);


    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;


            imagePath = cursor.getString(columnIndex);
//                Bitmap bitmap = BitmapFactory.decodeFile(imagePathOne, options);
//                ivProfileOne.setImageBitmap(bitmap);

            cursor.close();

        }

    }


    public void acceptOrRejectAndDelete(final String inviteId, String inviteToken, final String type, final int position, final View itemView, final String name, final String image) {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.removeInvite(Constant.APPTOKEN, manageSession.getProfileModel().getUser_id(), inviteId, inviteToken, type);
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
                            Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
//                            itemView.setVisibility(View.GONE);

                            if (type.equals("reject")) {
                                profileModelArrayList.remove(position);
                                homeAdapter.notifyDataSetChanged();

                                if (profileModelArrayList.size() == 0) {
                                    tvNoRecord.setVisibility(View.VISIBLE);
                                    rvHome.setVisibility(View.GONE);
                                    tvNoRecord.setText("No Invitation available!");
                                }
                            } else {
                                profileModelArrayList.get(position).setInvitionStatus("accept");
                                homeAdapter.notifyDataSetChanged();


                                /************************************/
                                /* For New Changes */
                       /*         ((HomeActivity) context).colorMessageTab();
                                getActivity().getSupportFragmentManager().popBackStack();

                                Fragment messageList = new MessageList();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.output, messageList);
                                transaction.commit();

                                Fragment fragment = new ChatFragment();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("shop_id", inviteId);
                                bundle1.putString("shop_name", name);
                                bundle1.putString("profile_image", image);
                                bundle1.putString("details", "details");

                                fragment.setArguments(bundle1);
                                replaceFragmentWithBack(R.id.output, fragment, "ChatFragment", "MessageList");
*/

                                if (profileModelArrayList.size() == 0) {
                                    tvNoRecord.setVisibility(View.VISIBLE);
                                    rvHome.setVisibility(View.GONE);
                                    tvNoRecord.setText("No Invitation available!");
                                }
                            }
                        } else {
                            Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LogView", "" + call.toString());
                t.printStackTrace();
//                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
//                Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}
