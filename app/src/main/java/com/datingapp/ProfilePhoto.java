package com.datingapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.fragment.MyAccount;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

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
import java.net.URL;
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

public class ProfilePhoto extends AppCompatActivity {

    private ImageView imgBack, img_thumbnailbig;
    private LinearLayout linGallery, linCamera, linPickImage, linCancel, linReady, linText, backBtn, linSocial;
    private ProgressDialog progressDialog;
    private Button btnCancel, btnReady;
    private Context context;
    //    private DataBase dataBase;
    private ScrollView parentLayout;
    MultipartBody.Part fileToUpload;
    public String profileImagePAth = "";
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    MultipartBody.Part profileImage = null;
    private ManageSession manageSession;
    private GPSTracker gpsTracker;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    GoogleApiClient googleApiClient;
    private String lat = "", lng = "";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);
        initView();
        ClickListener();

//        Toast.makeText(context, "On Create", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        context = this;
        manageSession = new ManageSession(context);
//        gpsTracker = new GPSTracker(context);
//        initialliseLocation(ProfilePlinMediahoto.this);

//        progress = new ProgressDialog(this);
//        progress.setMessage("Please Wait...");
//        progress.setCancelable(false);

//        dataBase = new DataBase(context);
        imgBack = findViewById(R.id.imgBack);
        parentLayout = (ScrollView) findViewById(R.id.parentLayout);
        backBtn = (LinearLayout) findViewById(R.id.backBtn);
        linGallery = findViewById(R.id.linGallery);
        linCamera = findViewById(R.id.linCamera);
        linSocial = findViewById(R.id.linMedia);
        linPickImage = findViewById(R.id.linPickImage);
        linCancel = findViewById(R.id.linCancel);
        linText = findViewById(R.id.linText);
        linReady = findViewById(R.id.linReady);
        btnCancel = findViewById(R.id.btnCancel);
        btnReady = findViewById(R.id.btnReady);
        img_thumbnailbig = findViewById(R.id.img_thumbnailbig);


        if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
            linSocial.setVisibility(View.VISIBLE);
        } else {
            linSocial.setVisibility(View.GONE);
        }
    }

    private void ClickListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (gpsTracker.canGetLocation()) {
//                    lat = gpsTracker.getLatitude() + "";
//                    lng = gpsTracker.getLongitude() + "";
//
//                    Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                if (manageSession.getProfileModel().getSocialType() == null) {
                    UpLoadImageApi();
                } else if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
                    if (profileImagePAth.equals("")) {
//                        userImageViaSocial();
                        userImageViaSocialApi();
                    } else {
                        UpLoadImageApi();
                    }
                }
//                }else {
//                    initialliseLocation(ProfilePhoto.this);
//                }
            }
        });

        linGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                galleryIntent();

                CameraActivity.type = "2";

                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (hasPermissions(ProfilePhoto.this, PERMISSIONS)) {
//                    cameraIntent();


                    Intent intent1 = new Intent(ProfilePhoto.this, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(ProfilePhoto.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        });
        linCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.type = "1";
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (hasPermissions(ProfilePhoto.this, PERMISSIONS)) {
//                    cameraIntent();


                    Intent intent1 = new Intent(ProfilePhoto.this, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(ProfilePhoto.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }

            }
        });

        linSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!dataBase.getpProfileData().get(0).getSocialType().equals("1")) {
                if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
                    linPickImage.setVisibility(View.GONE);
                    linCancel.setVisibility(View.GONE);
                    linText.setVisibility(View.VISIBLE);
                    linReady.setVisibility(View.VISIBLE);
                    img_thumbnailbig.setVisibility(View.VISIBLE);
                    Log.i(getClass().getName(), "Image is >" + manageSession.getProfileModel().getProfileImage());
//                    Picasso.with(ProfilePhoto.this).load(manageSession.getProfileModel().getProfileImage()).resize(200, 200).centerCrop().error(R.drawable.female_1).placeholder(R.drawable.female_1).into(img_thumbnailbig);

//                    if (manageSession.getProfileModel().getSocialType().equals("3")) {

//                    String image = manageSession.getProfileModel().getProfileImage().replaceAll("(?<!https:)\\/\\/", "/");
//                    Picasso.with(context).load(image).error(R.drawable.female_1).placeholder(R.drawable.female_1).into(img_thumbnailbig);

                    new DownloadImageTask().execute(manageSession.getProfileModel().getProfileImage());

//                    } else {
//                        Picasso.with(ProfilePhoto.this).load(manageSession.getProfileModel().getProfileImage()).resize(200, 200).centerCrop().error(R.drawable.female_1).placeholder(R.drawable.female_1).into(img_thumbnailbig);
//                    }
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
            img_thumbnailbig.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    public void UpLoadImageApi() {

//        progress.show();

        progressDialog = new ProgressDialog(ProfilePhoto.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(ProfilePhoto.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);


        Log.i(getClass().getName(), "profile_image > " + profileImagePAth + "user_id > " + manageSession.getProfileModel().getUser_id() + "app_token > " + Constant.APPTOKEN + "user_id > " + Constant.APPTOKEN + " type " + "1");

        try {
            File profileImageFile = new File(profileImagePAth);
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
        RequestBody type = RequestBody.create(MediaType.parse("type"), "1");


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
                    progressDialog.dismiss();
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equalsIgnoreCase("1")) {
                            ProfileModel profileModel = manageSession.getProfileModel();

                            profileModel.setProfileImage(jsonObject.optJSONObject("data").optString("profile_image"));

                            manageSession.setProfileModel(profileModel);

//                            ManageSession.setLoginPreference(context, "loginStatus", "1");
//                            Intent intent = new Intent(ProfilePhoto.this, WaitingListActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                            startActivity(new Intent(ProfilePhoto.this, VerifyProfile.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }


    public void userImageViaSocialApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(ProfilePhoto.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(ProfilePhoto.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("app_token", Constant.APPTOKEN);
        map.put("userlat", "");
        map.put("userlong", "");
        map.put("address", "");
        map.put("image", manageSession.getProfileModel().getProfileImage());
        map.put("type", "2");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.socialImageApi(map);
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
                            ProfileModel profileModel = manageSession.getProfileModel();

                            profileModel.setProfileImage(jsonObject.optJSONObject("data").optString("image"));

                            manageSession.setProfileModel(profileModel);

//                                ManageSession.setLoginPreference(context, "loginStatus", "1");
//                                Intent intent = new Intent(ProfilePhoto.this, WaitingListActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);

                            startActivity(new Intent(ProfilePhoto.this, VerifyProfile.class));
                        } else {
                            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            if (data.hasExtra("image")) {
                if (!data.getStringExtra("image").equals("")) {
                    manageSession.setImagePath("");
                    profileImagePAth = data.getStringExtra("image");
                    manageSession.setImagePath(profileImagePAth);
                    Picasso.with(context).load(new File(profileImagePAth)).resize(250, 250).centerCrop().into(img_thumbnailbig);
                    img_thumbnailbig.setVisibility(View.VISIBLE);
                    linPickImage.setVisibility(View.GONE);
                    linCancel.setVisibility(View.GONE);
                    linText.setVisibility(View.VISIBLE);
                    linReady.setVisibility(View.VISIBLE);
                }

            }
        }
//        else {
//            if (requestCode == REQUEST_CHECK_SETTINGS) {
//                initialliseLocation(ProfilePhoto.this);
//            }
//        }
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
              /*  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    cameraIntent();
                    CameraActivity.type = "1";
                    Intent intent1 = new Intent(ProfilePhoto.this, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }*/


                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, display contacts fragment.
//                    Snackbar.make(parentLayout, R.string.permision_available_contacts,
//                            Snackbar.LENGTH_SHORT)
//                            .show();

//                    CameraActivity.type = "1";
                    Intent intent1 = new Intent(ProfilePhoto.this, CameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
//                    Log.i(getClass().getName(), "Contacts permissions were NOT granted.");
                    Snackbar.make(parentLayout, R.string.permissions_not_granted,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }

                break;
            }
        }
    }

}
