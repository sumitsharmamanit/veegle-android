package com.datingapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.fragment.NotificationListFragment;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class WaitingListActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linEditProfile, llBlog, llMain, llRefer;
    private ProgressDialog progressDialog;
    private Context context;
    private ScrollView parentLayout;
    //    private DataBase dataBase;
    private MultipartBody.Part profileImage = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    private String imagePath = "";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private ManageSession manageSession;
    private FrameLayout editContainer;
    private InterstitialAd interstitialAd; // 12


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);
        initView();

        if (manageSession.getProfileModel() != null && manageSession.getProfileModel().getEthnicity().equals("")){
//         && manageSession.getProfileModel().getSocialType() != null, && !manageSession.getProfileModel().getSocialType().equals("1")
            OutLook.editStatus = false;
            startActivity(new Intent(WaitingListActivity.this, EditProfileActivity.class));
        } else {
            CheckApprovedApi();
        }
//        CheckApprovedApi();
    }

    public void initView() {
        context = this;
//        dataBase = new DataBase(context);
        manageSession = new ManageSession(context);
        parentLayout = (ScrollView) findViewById(R.id.parentLayout);
        linEditProfile = (LinearLayout) findViewById(R.id.linEditProfile);
        llBlog = (LinearLayout) findViewById(R.id.ll_blog);
        llMain = (LinearLayout) findViewById(R.id.ll_main);
        llRefer = (LinearLayout) findViewById(R.id.ll_refer);
        llRefer.setOnClickListener(this);
        linEditProfile.setOnClickListener(this);
        llBlog.setOnClickListener(this);
        editContainer = (FrameLayout) findViewById(R.id.edit_container);

        interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        showInterstitialAds();
    }

    public void showInterstitialAds(){
        AdRequest adRequest1 = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        interstitialAd.loadAd(adRequest1);

        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    private void showInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }


    public void CheckApprovedApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(WaitingListActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(WaitingListActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.userApprovedApi(map);
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
                        String verifyEmail = jsonObject.optString("verifyemail");
                        String Approvedstatus = jsonObject.optString("ApprovedStatus");
                        manageSession.setPerQuestion(jsonObject.optInt("personality_quiz_question"));
                        manageSession.setPerAnswer(jsonObject.optInt("personality_quiz_answer"));
                        manageSession.setPartnerQuestion(jsonObject.optInt("partner_question"));
                        manageSession.setPartnerAnswer(jsonObject.optInt("partner_answer"));

                        if (status.equals("1")) {
                            if (Approvedstatus.equals("1")) {
                                manageSession.setLoginPreference("2");
                                manageSession.setFirstTimeLaunch(true);


                                JSONObject object = jsonObject.optJSONObject("data");
                                GsonBuilder builder = new GsonBuilder();
                                Gson gson = builder.serializeNulls().create();
                                ProfileModel bean = gson.fromJson(object.toString(), ProfileModel.class);
//                                dataBase.clearProfile();
                                manageSession.setProfileModel(bean);
//                                dataBase.addProfileData(bean);

                                manageSession.setVibrationString("1");
                                manageSession.setSoundString("1");
                                manageSession.setFirstTimeMessageShow(true);

                                Intent intent = new Intent(WaitingListActivity.this, WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            if (verifyEmail.equals("0")) {
                                Snackbar snackbar = Snackbar
                                        .make(parentLayout, "Please verify your email in your mail account.", Snackbar.LENGTH_LONG)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                snackbar.setActionTextColor(getResources().getColor(R.color.btn_color));
                                snackbar.show();
//                                dialogOK(context, "", "", "Ok", false);
                            } else {
                                Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
                            }
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
    public void onClick(View v) {
        if (v.getId() == R.id.linEditProfile) {
            OutLook.editStatus = false;
            startActivity(new Intent(WaitingListActivity.this, EditProfileActivity.class));
        } else if (v.getId() == R.id.ll_blog) {
//            dialogBlog(this);

            try {
            Fragment fragment;
            editContainer.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("activity", "activity");
            bundle.putString(Constant.SHOW_BLOG, Constant.SHOW_BLOG);
            fragment = new NotificationListFragment();
            fragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.edit_container, fragment);
            transaction.addToBackStack("NotificationListFragment");
            transaction.commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.ll_refer) {
//            String shareBody = "Veegle play store link";
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Veegle");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//            startActivity(Intent.createChooser(sharingIntent, "Share..."));


            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Veegle");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/search?q=com.veegleapp&hl=en \n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share to"));
            } catch(Exception e) {
                //e.toString();
            }
        }
    }

    public void showWaitData() {
        llMain.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);
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

        new OutLook().hideKeyboard(this);

        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dailogImageChooser(context);

                if (hasPermissions(WaitingListActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent = new Intent(WaitingListActivity.this, CameraActivity.class);
                    startActivityForResult(intent, 123);
                } else {
                    ActivityCompat.requestPermissions(WaitingListActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
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
                            byte[]  data = blog.getBytes("UTF-8");
                            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            addBlog(imagePath, base64, dialog);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        addBlog(imagePath, blog, dialog);
//                        dialog.dismiss();
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
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                editContainer.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
            } else {
//                finish();
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    CameraActivity.type = "";
                    Intent intent3 = new Intent(WaitingListActivity.this, CameraActivity.class);
                    startActivityForResult(intent3, 123);
                } else {
                    Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void addBlog(final String path, String text, final Dialog dialog) {
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
                    Log.e("LogView", "" + str);
                    progressDialog.dismiss();
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equalsIgnoreCase("1")) {
//                            Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            }
        });
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

    public void dialogOK(final Context context, String title, String message,
                         String btnText, final boolean isFinish) {
        // https://www.google.com/design/spec/components/dialogs.html#dialogs-specs
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage(Html.fromHtml(message));
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Nirmala.ttf");
//        alertDialogBuilder.setView(new TextView(context));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish)
                    ((Activity) context).finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
