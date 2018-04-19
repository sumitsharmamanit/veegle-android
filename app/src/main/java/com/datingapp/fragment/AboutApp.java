package com.datingapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datingapp.BuildConfig;
import com.datingapp.CameraActivity;
import com.datingapp.HomeActivity;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.AboutAppBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutApp extends BaseFragment implements View.OnClickListener {

    private AboutAppBinding aboutAppBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
    private String imagePath = "", message = "";
    private MultipartBody.Part profileImage = null;
    public final int REQUEST_CHECK_SETTINGS = 7070;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private boolean feedStatus = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.ADD_FEEDBACK)) {
                feedStatus = true;
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        aboutAppBinding = DataBindingUtil.inflate(inflater, R.layout.about_app, container, false);
        return aboutAppBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);

        initView();
        initToolBar();

        if (feedStatus) {
            aboutAppBinding.tvText.setVisibility(View.GONE);
            aboutAppBinding.tvAppVersion.setVisibility(View.GONE);
            aboutAppBinding.tvAboutApp.setVisibility(View.GONE);
        } else {
            aboutAppBinding.etFeedback.setVisibility(View.GONE);
            aboutAppBinding.tvSubmit.setVisibility(View.GONE);
            aboutAppBinding.tvUpload.setVisibility(View.GONE);
            aboutAppBinding.tvFeedText.setVisibility(View.GONE);
            getAboutAppApi();
        }
    }

    private void initView() {
        aboutAppBinding.tvUpload.setOnClickListener(this);
        aboutAppBinding.tvSubmit.setOnClickListener(this);

        aboutAppBinding.tvAppVersion.setText("VERSION " + BuildConfig.VERSION_NAME);
//        aboutAppBinding.etFeedback.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        aboutAppBinding.etFeedback.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        if (feedStatus)
            ((HomeActivity) getActivity()).createBackButton("Feedback", true);
        else
            ((HomeActivity) getActivity()).createBackButton("About App", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                message = aboutAppBinding.etFeedback.getText().toString();
                if (!message.equals("")) {
                    if (imagePath.equals("")) {
//                        addFeedbackWithoutImage();
                        addFeedbackWithoutImageApi();
                    } else {
                        addFeedBack();
                    }
                } else {
                    Snackbar.make(aboutAppBinding.parentLayout, "Please enter feedback.", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_upload:
                if (hasPermissions(context, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent1 = new Intent(context, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
                break;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public void getAboutAppApi() {
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
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.aboutAppApi(map);
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
                            aboutAppBinding.tvAboutApp.setText(jsonObject.optJSONObject("data").optString("about"));
                        } else {
                            Snackbar.make(aboutAppBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(aboutAppBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void addFeedBack() {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        try {
            File profileImageFile = new File(imagePath);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("feedimage", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody message1 = RequestBody.create(MediaType.parse("message"), message);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.addFeedback(user_id, app_token, profileImage, message1);
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
                            Snackbar.make(aboutAppBinding.parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                            imagePath = "";
                            aboutAppBinding.etFeedback.setText("");
                        } else {
                            Snackbar.make(aboutAppBinding.parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
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


    public void addFeedbackWithoutImageApi() {
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
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("message", message);
        map.put("feedimage", "");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.addappFeedbackWithOutImage(map);
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
                            Snackbar.make(aboutAppBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            imagePath = "";
                            aboutAppBinding.etFeedback.setText("");
                        } else {
                            Snackbar.make(aboutAppBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(aboutAppBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

}