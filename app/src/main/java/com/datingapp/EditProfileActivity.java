package com.datingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.fragment.HomeFragment;
import com.datingapp.fragment.MyAccount;
import com.datingapp.fragment.TimeLineFragment;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnTaskCompleted;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText aboutMeTxt, userName;
    private TextView lokkingforTxt, genderTxt, bdayTxt, femaleTxt, childrenTxt;
    private Button btnNext;
    private Context context;
    private String lookingFor = "";
    private String aboutMe = "";
    private String name = "";
    private String gender = "";
    private static String birthday = "";
    private String status = "";
    private String children = "";
    private String imagePath = "";
    private LinearLayout llGender, llStatus, llBirthday, llChildren;
    private RoundedImageView ivProfileOne, ivProfileTwo, ivProfileThree, ivProfileFour, ivProfileFive, ivProfile;
    private ImageView ivPick, ivBack, ivTimeLine;
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    private ProgressDialog progress;
    private MultipartBody.Part profileImage = null;
    //    private DataBase dataBase;
    public ScrollView parentLayout;
    private ManageSession manageSession;
    private boolean editStatus = false;
    private SearchableSpinner spStatus, spChildren, spGender, spLooking;
    private int statusPosition = 0, childPosition = 0, genderPosition = 0, lookPosition = 0, imageType = 0;
    private RelativeLayout rlLooking;
    private GPSTracker gpsTracker;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    GoogleApiClient googleApiClient;
    private String lat = "", lng = "";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private LinearLayout llMain, llEmail;
    private FrameLayout editContainer;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String[] itemsGender = {"Male", "Female"};
    private int position55 = -1;
//    private InterstitialAd interstitialAd; //12


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        new OutLook().hideKeyboard(EditProfileActivity.this);
        initView();

        new OutLook().hideKeyboard(EditProfileActivity.this);

      /*  interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        showInterstitialAds();*/
    }

    private void setValues() {
//        ivPick.setVisibility(View.GONE);

        aboutMeTxt.setText(manageSession.getProfileModel().getAboutme());
        aboutMeTxt.setSelection(aboutMeTxt.getText().length());
        userName.setText(manageSession.getProfileModel().getUserfullname());
        userName.setSelection(userName.getText().length());
        lokkingforTxt.setText(manageSession.getProfileModel().getLookingfor());
        genderTxt.setText(manageSession.getProfileModel().getGender());

        if (manageSession.getProfileModel().getDob() != null && !manageSession.getProfileModel().getDob().equals("0000-00-00"))
            bdayTxt.setText(manageSession.getProfileModel().getDob());

        femaleTxt.setText(manageSession.getProfileModel().getMaternitystatus());
        childrenTxt.setText(manageSession.getProfileModel().getChildren());

        lookingFor = manageSession.getProfileModel().getLookingfor();
        gender = manageSession.getProfileModel().getGender();
        birthday = manageSession.getProfileModel().getDob();
        status = manageSession.getProfileModel().getMaternitystatus();
        children = manageSession.getProfileModel().getChildren();

        position55 = Arrays.asList(itemsGender).indexOf(manageSession.getProfileModel().getGender());

        new OutLook().hideKeyboard(EditProfileActivity.this);
    }

    public void showInterstitialAds(){
//        AdRequest adRequest1 = new AdRequest.Builder()
//                .build();
//
//        // Load ads into Interstitial Ads
//        interstitialAd.loadAd(adRequest1);
//
//        interstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                showInterstitial();
//            }
//        });
    }

    private void showInterstitial() {
//        if (interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }
    }

    public void initView() {
        context = EditProfileActivity.this;
        manageSession = new ManageSession(context);
        progress = new ProgressDialog(context);
        gpsTracker = new GPSTracker(context);
        progress.setMessage("Please Wait...");
        progress.setCancelable(false);
//        dataBase = new DataBase(context);

        rlLooking = (RelativeLayout) findViewById(R.id.rl_looking);
        rlLooking.setOnClickListener(this);

        aboutMeTxt = (EditText) findViewById(R.id.aboutMeTxt);
        userName = (EditText) findViewById(R.id.userName);

        lokkingforTxt = (TextView) findViewById(R.id.lokkingforTxt);
        genderTxt = (TextView) findViewById(R.id.genderTxt);
        bdayTxt = (TextView) findViewById(R.id.bdayTxt);
        femaleTxt = (TextView) findViewById(R.id.femaleTxt);
        childrenTxt = (TextView) findViewById(R.id.childrenTxt);

        llEmail = (LinearLayout) findViewById(R.id.ll_email);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manageSession.getProfileModel() != null && manageSession.getProfileModel().getEthnicity().equals("")) {
                    dialogForName("This field change only once (one time editable). So please correct the one for getting a better matches.");
                } else {
                    dialogForName("Sorry! you already changed this field you can not edit more beacuse this is one time editable.");
                }
            }
        });

        llBirthday = (LinearLayout) findViewById(R.id.ll_birthday);
        llChildren = (LinearLayout) findViewById(R.id.ll_children);
        llGender = (LinearLayout) findViewById(R.id.ll_gender);
        llStatus = (LinearLayout) findViewById(R.id.ll_status);

        ivPick = (ImageView) findViewById(R.id.iv_pick);
        ivProfileOne = (RoundedImageView) findViewById(R.id.iv_profile_one);
        ivProfileTwo = (RoundedImageView) findViewById(R.id.iv_profile_two);
        ivProfileThree = (RoundedImageView) findViewById(R.id.iv_profile_three);
        ivProfileFour = (RoundedImageView) findViewById(R.id.iv_profile_four);
        ivProfileFive = (RoundedImageView) findViewById(R.id.iv_profile_five);
        ivProfile = (RoundedImageView) findViewById(R.id.iv_profile);

        ivBack = (ImageView) findViewById(R.id.imgBack);
        ivTimeLine = (ImageView) findViewById(R.id.iv_timeline);
        llMain = (LinearLayout) findViewById(R.id.ll_main);
        editContainer = (FrameLayout) findViewById(R.id.edit_container);
        ivBack.setOnClickListener(this);
        ivTimeLine.setOnClickListener(this);

        parentLayout = (ScrollView) findViewById(R.id.parentLayout);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

        ivPick.setOnClickListener(this);
        ivProfileOne.setOnClickListener(this);
        ivProfileTwo.setOnClickListener(this);
        ivProfileThree.setOnClickListener(this);
        ivProfileFour.setOnClickListener(this);
        ivProfileFive.setOnClickListener(this);
//        ivProfileSix.setOnClickListener(this);

        lokkingforTxt.setOnClickListener(this);
        llBirthday.setOnClickListener(this);
        llChildren.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llStatus.setOnClickListener(this);

//        aboutMeTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        aboutMeTxt.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if (manageSession.getProfileModel().getProfileImage() != null && !manageSession.getProfileModel().getProfileImage().equals("")) {
            imagePath = manageSession.getProfileModel().getProfileImage();
//            if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equalsIgnoreCase("1"))
//                Picasso.with(context).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getProfileImage()).resize(250, 250).centerCrop().into(ivProfile);
//            else

            if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
//                String image = manageSession.getProfileModel().getProfileImage().replaceAll("(?<!https:)\\/\\/", "/");
//                if (manageSession.getProfileModel().getSocialType().equals("3")) {
//                    Picasso.with(context).load(image).into(ivProfile);
//                } else {
                new DownloadImageTask().execute(manageSession.getProfileModel().getProfileImage());
//                }
            } else {
                Picasso.with(this).load(manageSession.getProfileModel().getProfileImage()).resize(250, 250).centerCrop().into(ivProfile);
            }
        }

        if (manageSession.getProfileModel().getUserPics() != null && manageSession.getProfileModel().getUserPics().size() > 0) {
//            ivPick.setVisibility(View.GONE);
//            imageStatus = true;

            if (manageSession.getProfileModel().getUserPics().get(0).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(0).getPicUrl().equals("")) {
                imagePath = manageSession.getProfileModel().getUserPics().get(0).getPicUrl();
                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(0).getPicUrl()).resize(250, 250).centerCrop().into(ivProfileOne);
            }

            if ((manageSession.getProfileModel().getUserPics().size() >= 2) && manageSession.getProfileModel().getUserPics().get(1).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(1).getPicUrl().equals("")) {
                imagePath = manageSession.getProfileModel().getUserPics().get(1).getPicUrl();
                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(1).getPicUrl()).resize(250, 250).centerCrop().into(ivProfileTwo);
            }

            if ((manageSession.getProfileModel().getUserPics().size() >= 3) && manageSession.getProfileModel().getUserPics().get(2).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(2).getPicUrl().equals("")) {
                imagePath = manageSession.getProfileModel().getUserPics().get(2).getPicUrl();
                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(2).getPicUrl()).resize(250, 250).centerCrop().into(ivProfileThree);
            }

            if ((manageSession.getProfileModel().getUserPics().size() >= 4) && manageSession.getProfileModel().getUserPics().get(3).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(3).getPicUrl().equals("")) {
                imagePath = manageSession.getProfileModel().getUserPics().get(3).getPicUrl();
                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(3).getPicUrl()).resize(250, 250).centerCrop().into(ivProfileFour);
            }

            if ((manageSession.getProfileModel().getUserPics().size() >= 5) && manageSession.getProfileModel().getUserPics().get(4).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(4).getPicUrl().equals("")) {
                imagePath = manageSession.getProfileModel().getUserPics().get(4).getPicUrl();
                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(4).getPicUrl()).resize(250, 250).centerCrop().into(ivProfileFive);
            }

//            if ((manageSession.getProfileModel().getUserPics().get(5) != null) && manageSession.getProfileModel().getUserPics().get(5).getPicUrl() != null && !manageSession.getProfileModel().getUserPics().get(5).getPicUrl().equals("")) {
//                imagePathSix = manageSession.getProfileModel().getUserPics().get(5).getPicUrl();
//                Picasso.with(this).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getUserPics().get(5).getPicUrl()).fit().into(ivProfileSix);
//            }
        }

        if (manageSession.getProfileModel().getLookingfor() != null) {
            lokkingforTxt.setText(manageSession.getProfileModel().getLookingfor());
            lookingFor = manageSession.getProfileModel().getLookingfor();
        }

        if (manageSession.getProfileModel().getUserfullname() != null) {
            userName.setText(manageSession.getProfileModel().getUserfullname());
            name = manageSession.getProfileModel().getUserfullname();
        }


            if (manageSession.getProfileModel().getDob() != null && !manageSession.getProfileModel().getDob().equals("0000-00-00")) {
            bdayTxt.setText(manageSession.getProfileModel().getDob());
            birthday = manageSession.getProfileModel().getDob();
        }

        if (manageSession.getProfileModel().getGender() != null) {
            genderTxt.setText(manageSession.getProfileModel().getGender());
            gender = manageSession.getProfileModel().getGender();
        }

//        if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equalsIgnoreCase("1")) {
//            userName.setEnabled(false);
//            userName.setFocusable(false);
//        } else

        if (manageSession.getProfileModel() != null && manageSession.getProfileModel().getEthnicity().equals("")) {
            userName.setEnabled(true);
            userName.setFocusable(true);
        } else {
            userName.setEnabled(false);
            userName.setFocusable(false);
        }


        spStatus = (SearchableSpinner) findViewById(R.id.sp_status);
        spStatus.setTitle("Select Status");
        spStatus.setPositiveButton("Close");

        if (manageSession.getProfileModel() != null && !manageSession.getProfileModel().getEthnicity().equals("")) {
            editStatus = true;
//            imageStatus = true;
            setValues();
        }


        final String[] value1 = {"Single", "Taken", "In a complicated Relationship", "Married"};
        ArrayAdapter<String> oriAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                R.layout.spinner_textview, value1);
        spStatus.setAdapter(oriAdapter);


        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < value1.length; i++) {

                if (manageSession.getProfileModel().getMaternitystatus().equalsIgnoreCase(value1[i])) {
                    spStatus.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555");
                    break;
                }
            }
        } else {
            spStatus.setSelection(statusPosition);
        }

        spLooking = (SearchableSpinner) findViewById(R.id.sp_looking);
        spLooking.setTitle("Looking for");
        spLooking.setPositiveButton("Close");

        final String[] value = {"Make New Friends", "Chat Only", "Date", "Serious Relationship", "Something Casual/Fun"};
        ArrayAdapter<String> lookAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                R.layout.spinner_textview, value);
        spLooking.setAdapter(lookAdapter);

        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < value.length; i++) {

                if (manageSession.getProfileModel().getLookingfor().equalsIgnoreCase(value[i])) {
                    spLooking.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555");
                    break;
                }
            }
        } else {
            spLooking.setSelection(lookPosition);
        }

        spChildren = (SearchableSpinner) findViewById(R.id.sp_children);
        spChildren.setTitle("Select Children");
        spChildren.setPositiveButton("Close");

        final String[] value3 = {"May be, Someday", "Yes, they live with me", "Yes, they are grown up", "Not willing to have"};
        ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                R.layout.spinner_textview, value3);
        spChildren.setAdapter(childAdapter);

        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < value3.length; i++) {

                if (manageSession.getProfileModel().getChildren().equalsIgnoreCase(value3[i])) {
                    spChildren.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555");
                    break;
                }
            }
        } else {
            spChildren.setSelection(childPosition);
        }


        spLooking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lookingFor = value[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = value1[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spChildren.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                children = value3[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        aboutMeTxt.addTextChangedListener(new TextWatcher() {

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
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 8));
                        aboutMeTxt.setSelection(s.length() - 8);
                    } else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 7));
                        aboutMeTxt.setSelection(s.length() - 7);
                    } else if (s.toString().toLowerCase().contains("google")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 6));
                        aboutMeTxt.setSelection(s.length() - 6);
                    } else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 5));
                        aboutMeTxt.setSelection(s.length() - 5);
                    } else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 9));
                        aboutMeTxt.setSelection(s.length() - 9);
                    } else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 4));
                        aboutMeTxt.setSelection(s.length() - 4);
                    } else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 3));
                        aboutMeTxt.setSelection(s.length() - 3);
                    } else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+") || s.toString().toLowerCase().contains("ga") || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc") || s.toString().toLowerCase().contains("mt") || s.toString().toLowerCase().contains("rt") || s.toString().toLowerCase().contains("yt")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 2));
                        aboutMeTxt.setSelection(s.length() - 2);
                    } else if (s.toString().toLowerCase().contains("@")) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 1));
                        aboutMeTxt.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())) {
                        aboutMeTxt.setText(s.subSequence(0, s.length() - 8));
                        aboutMeTxt.setSelection(s.length() - 8);
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmp = null;

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            ivProfile.setImageBitmap(result);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lokkingforTxt:
                OnTaskCompleted onTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String response) {
                        lookingFor = response;
                        lokkingforTxt.setText(response);
                    }
                };

                final String[] value = {"Make New Friends", "Chat Only", "Date", "Serious Relationship", "Something Casual/Fun"};
                lookingFor = new OutLook().GetArrayPicker(context, value, onTaskCompleted);
                lokkingforTxt.setText(lookingFor);
                break;
            case R.id.rl_looking:
                spLooking.onTouch(rlLooking.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
            case R.id.ll_birthday:
//                if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equalsIgnoreCase("1")) {
//                    selectDate(bdayTxt);
//                } else if (manageSession.isFirstTimeEditProfile()) {
//                    selectDate(bdayTxt);
//                }


                if (manageSession.getProfileModel() != null && manageSession.getProfileModel().getEthnicity().equals("")) {
                    dialogForName("This field change only once (one time editable). So please correct the one for getting a better matches.");
                    selectDate(bdayTxt);
                } else {
                    dialogForName("Sorry! you already changed this field you can not edit more beacuse this is one time editable.");
                }
                break;
            case R.id.ll_status:

                spStatus.onTouch(llStatus.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));

                break;
            case R.id.ll_gender:
           /*     if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equalsIgnoreCase("1")) {
//                    OnTaskCompleted onTaskCompleted2 = new OnTaskCompleted() {
//                        @Override
//                        public void onTaskCompleted(String response) {
//                            gender = response;
//                            genderTxt.setText(gender);
//                        }
//                    };
//
//                    final String[] value2 = {"Male", "Female"};
//                    gender = new OutLook().GetArrayPicker(context, value2, onTaskCompleted2);
//                    genderTxt.setText(gender);

                    showDialog();
                } else if (manageSession.isFirstTimeEditProfile()) {
                    showDialog();
                }*/

                if (manageSession.getProfileModel() != null && manageSession.getProfileModel().getEthnicity().equals("")) {
                    showDialog();
                    dialogForName("This field change only once (one time editable). So please correct the one for getting a better matches.");
                } else {
                    dialogForName("Sorry! you already changed this field you can not edit more beacuse this is one time editable.");
                }
                break;
            case R.id.ll_children:
//                OnTaskCompleted onTaskCompleted3 = new OnTaskCompleted() {
//                    @Override
//                    public void onTaskCompleted(String response) {
//                        children = response;
//                        childrenTxt.setText(children);
//                    }
//                };
//
//                final String[] value3 = {"May be, Someday", "Yes, they live with me", "Yes, they are grown up", "Not willing to have"};
//                children = Constant.GetArrayPicker(context, value3, onTaskCompleted3);
//                childrenTxt.setText(children);

                spChildren.onTouch(llChildren.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
            case R.id.iv_pick:
//                if (imagePathOne.equals("") || imagePathThree.equals("") || imagePathTwo.equals("") || imagePathFour.equals("") || imagePathFive.equals("") || imagePathSix.equals(""))
//                    dailogImageChooser(context);
//                else
//                    ivPick.setVisibility(View.GONE);

                imageType = 6;
//                imagePath = "";


//                dailogImageChooser(context);
                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent1 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

                break;
            case R.id.btnNext:
                new OutLook().hideKeyboard(this);
                aboutMe = aboutMeTxt.getText().toString().trim();
                name = userName.getText().toString();

                if (isValid()) {
                    statusPosition = spStatus.getSelectedItemPosition();
                    lookPosition = spLooking.getSelectedItemPosition();
                    childPosition = spChildren.getSelectedItemPosition();

                    ProfileModel profileModel = new ProfileModel();
                    profileModel.setAboutme(aboutMe);
                    profileModel.setUserfullname(name);
                    profileModel.setGender(gender);
                    profileModel.setDob(birthday);
                    profileModel.setLookingfor(lookingFor);
                    profileModel.setMaternitystatus(status);
                    profileModel.setChildren(children);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ProfileModel", profileModel);
                    Intent intent = new Intent(EditProfileActivity.this, EditProfileNext.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.iv_profile_one:
                imageType = 1;

//                if (imageStatus) {
//                    imagePathOne = "";


//                    dailogImageChooser(context);
                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

//                }
                break;
            case R.id.iv_profile_two:
                imageType = 2;

//                if (imageStatus) {
//                    imagePathTwo = "";
//                    dailogImageChooser(context);

                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent2 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent2, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

//                }
                break;
            case R.id.iv_profile_three:
                imageType = 3;

//                if (imageStatus) {
//                    imagePathThree = "";
//                    dailogImageChooser(context);

                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent3 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent3, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }


//                }
                break;
            case R.id.iv_profile_four:
                imageType = 4;

//                if (imageStatus) {
//                    imagePathFour = "";
//                    dailogImageChooser(context);
                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent4 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent4, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

//                }
                break;
            case R.id.iv_profile_five:
                imageType = 5;

//                if (imageStatus) {
//                    imagePathFive = "";
//                    dailogImageChooser(context);
                if (hasPermissions(EditProfileActivity.this, PERMISSIONS)) {
                    CameraActivity.type = "";
                    Intent intent5 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent5, 123);
                } else {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

//                }
                break;
//            case R.id.iv_profile:
//                if (imageStatus) {
//                    imagePathSix = "";
//                    dailogImageChooser(context);
//                }
//                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.iv_timeline:
                try {
                Fragment fragment;
                editContainer.setVisibility(View.VISIBLE);
                llMain.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "activity");
                fragment = new TimeLineFragment();
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.edit_container, fragment);
                transaction.addToBackStack("TimeLineFragment");
                transaction.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Select Gender");

        //list of items

        builder.setSingleChoiceItems(itemsGender, position55,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item selected logic

                        genderTxt.setText(itemsGender[which]);
                        gender = itemsGender[which];
                        position55 = which;
                        dialog.dismiss();
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton("",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private boolean isValid() {
        if (aboutMe == null || aboutMe.equals("")) {
            Snackbar.make(parentLayout, "Please enter about yourself.", Snackbar.LENGTH_SHORT).show();
            aboutMeTxt.requestFocus();
            return false;
//        } else if (lookingFor == null || lookingFor.equals("")) {
//            Snackbar.make(parentLayout, "Please select looking for.", Snackbar.LENGTH_SHORT).show();
//            return false;
        } else if (name == null || name.equals("")) {
            Snackbar.make(parentLayout, "Please enter name.", Snackbar.LENGTH_SHORT).show();
            userName.requestFocus();
            return false;
        } else if (gender == null || gender.equals("")) {
            Snackbar.make(parentLayout, "Please select gender.", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (birthday == null || birthday.equals("")) {
            Snackbar.make(parentLayout, "Please select birth date.", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (status == null || status.equals("")) {
            Snackbar.make(parentLayout, "Please select status.", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (children == null || children.equals("")) {
            Snackbar.make(parentLayout, "Please select children.", Snackbar.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void selectDate(TextView et) {
        DialogFragment dialogfragment = new DatePickerDialogClass(et);
        dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        TextView tvDate;

        public DatePickerDialogClass() {

        }

        @SuppressLint("ValidFragment")
        public DatePickerDialogClass(TextView tvDate) {
            this.tvDate = tvDate;
        }

        @SuppressLint("NewApi")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.CustomDatePickerDialogTheme, this, year, month, day);
            datepickerdialog.getDatePicker().setCalendarViewShown(false);
            datepickerdialog.getDatePicker().setSpinnersShown(true);
//            datepickerdialog.getDatePicker().setMaxDate(new Date().getTime());
            datepickerdialog.getDatePicker().setLayoutMode(1);

            calendar.add(Calendar.YEAR, -18);
            datepickerdialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return datepickerdialog;
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            tvDate.setText(new OutLook().formatDateShow(year + "-" + (month + 1) + "-" + dayOfMonth));
//            tvDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            birthday = new OutLook().formatDateShow(year + "-" + (month + 1) + "-" + dayOfMonth);
        }
    }


    public void updateImageApi(final String path, final String ImageType) {

        progress = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progress.setIndeterminate(true);
        progress = ProgressDialog.show(context, null, null);
        progress.setContentView(R.layout.progress_layout);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);

        try {
            File profileImageFile = new File(path);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("profile_image", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody type = RequestBody.create(MediaType.parse("type"), ImageType);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.imageUpdate(user_id, app_token, profileImage, type);
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
                    progress.dismiss();
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equalsIgnoreCase("1")) {

                            if (ImageType.equals("1")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(0).setPicUrl(jsonObject.optString("data"));
//                                ivProfileOne.setImageBitmap(bitmap);
                                Picasso.with(context).load(new File(imagePath)).resize(250, 250).centerCrop().into(ivProfileOne);
                            }

                            if (ImageType.equals("2")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(1).setPicUrl(jsonObject.optString("data"));
//                                ivProfileTwo.setImageBitmap(bitmap);
                                Picasso.with(context).load(new File(imagePath)).resize(250, 250).centerCrop().into(ivProfileTwo);
                            }
                            if (ImageType.equals("3")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(2).setPicUrl(jsonObject.optString("data"));
//                                ivProfileThree.setImageBitmap(bitmap);
                                Picasso.with(context).load(new File(imagePath)).resize(250, 250).centerCrop().into(ivProfileThree);
                            }
                            if (ImageType.equals("4")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(3).setPicUrl(jsonObject.optString("data"));
//                                ivProfileFour.setImageBitmap(bitmap);
                                Picasso.with(context).load(new File(imagePath)).resize(250, 250).centerCrop().into(ivProfileFour);
                            }
                            if (ImageType.equals("5")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(4).setPicUrl(jsonObject.optString("data"));
//                                ivProfileFive.setImageBitmap(bitmap);
                                Picasso.with(context).load(new File(imagePath)).resize(250, 250).centerCrop().into(ivProfileFive);
                            }
//                            if (ImageType.equals("6")) {
//                                if (manageSession.getProfileModel().getUserPics() != null)
//                                    manageSession.getProfileModel().getUserPics().get(5).setPicUrl(jsonObject.optString("data"));
//                                ivProfile.setImageBitmap(bitmap);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    public void UpLoadImageApi() {

//        progress.show();

        progress = new ProgressDialog(EditProfileActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progress.setIndeterminate(true);
        progress = ProgressDialog.show(EditProfileActivity.this, null, null);
        progress.setContentView(R.layout.progress_layout);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);


//        try {
//            if (gpsTracker.canGetLocation()) {
//                lat = gpsTracker.getLatitude() + "";
//                lng = gpsTracker.getLongitude() + "";
//
//                Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//            } else {
//                initialliseLocation(context);
//            }

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            File profileImageFile = new File(imagePath);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("profile_image", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody latitude = RequestBody.create(MediaType.parse("userlat"), "");
        RequestBody longitude = RequestBody.create(MediaType.parse("userlong"), "");
        RequestBody address = RequestBody.create(MediaType.parse("address"), "");
//        RequestBody image = RequestBody.create(MediaType.parse("image"), "");

        RequestBody type;
        if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1"))
            type = RequestBody.create(MediaType.parse("type"), "3");
        else
            type = RequestBody.create(MediaType.parse("type"), "1");


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.userImage(user_id, app_token, profileImage, latitude, longitude, address, type);
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
                    progress.dismiss();
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equalsIgnoreCase("1")) {

                            ProfileModel profileModel = manageSession.getProfileModel();

                            profileModel.setProfileImage(jsonObject.optJSONObject("data").optString("profile_image"));

                            manageSession.setProfileModel(profileModel);

                            Picasso.with(EditProfileActivity.this).load(jsonObject.optJSONObject("data").optString("profile_image")).resize(250, 250).centerCrop().into(ivProfile);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data);
   /*     if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (gpsTracker.canGetLocation()) {
                    lat = gpsTracker.getLatitude() + "";
                    lng = gpsTracker.getLongitude() + "";

                    Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                    UpLoadImageApi();
                }
            }
        } else*/

        if (resultCode == 2) {
            if (data.hasExtra("image")) {
                if (!data.getStringExtra("image").equals("")) {
                    manageSession.setImagePath("");
                    imagePath = data.getStringExtra("image");
                    manageSession.setImagePath(imagePath);
                    if (imageType == 1) {
                        updateImageApi(imagePath, "1");
                    } else if (imageType == 2) {
                        updateImageApi(imagePath, "2");
                    } else if (imageType == 3) {
                        updateImageApi(imagePath, "3");
                    } else if (imageType == 4) {
                        updateImageApi(imagePath, "4");
                    } else if (imageType == 5) {
                        updateImageApi(imagePath, "5");
                    } else if (imageType == 6) {
                        UpLoadImageApi();
                    }
                }

            }
        }

       /* else {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                initialliseLocation(context);
            }
        }*/
       /* switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        if (gpsTracker.canGetLocation()) {
                            lat = gpsTracker.getLatitude() + "";
                            lng = gpsTracker.getLongitude() + "";

                            Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                            UpLoadImageApi();
                        }

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        //  getActivity().finish();

                        initialliseLocation(context);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            case SELECT_FILE:
                onSelectFromGalleryResult(data);
                break;
            case REQUEST_CAMERA:
                onCaptureImageResult(data);
                break;
        }*/
    }


    public String getAddressFromLatLong(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

//            return address + " " + city + " " + state;
            return city;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
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
                    Intent intent3 = new Intent(EditProfileActivity.this, CameraActivity.class);
                    startActivityForResult(intent3, 123);
                } else {
                    Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void dialogForName(String text) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final TextView btnYes = (TextView) dialog.findViewById(R.id.tv_yes);
//        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no);


        TextView tvText = (TextView) dialog.findViewById(R.id.tv_message);
        tvText.setText(text);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

}
