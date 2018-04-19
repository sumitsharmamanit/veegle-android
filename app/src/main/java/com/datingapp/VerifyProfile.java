package com.datingapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.PermissionUtil;
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
import com.makeramen.roundedimageview.RoundedImageView;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyProfile extends AppCompatActivity {

    private Button btnReady;
    private LinearLayout backBtn;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    private RoundedImageView ivGesture;
    private String imagePath = "";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ProgressDialog progressDialog;
    private GPSTracker gpsTracker;
    GoogleApiClient googleApiClient;
    private String lat = "", lng = "";
    private ManageSession manageSession;
    MultipartBody.Part profileImage = null;
    private TextView tvChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_profile);

        backBtn = (LinearLayout) findViewById(R.id.backBtn);
        btnReady = findViewById(R.id.btnReady);
        ivGesture = (RoundedImageView) findViewById(R.id.iv_gesture);
        tvChange = (TextView) findViewById(R.id.tv_change);
        manageSession = new ManageSession(this);
//        gpsTracker = new GPSTracker(this);
//        initialliseLocation(VerifyProfile.this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivGesture.setImageDrawable(getResources().getDrawable(R.drawable.female_2));
                imagePath = "";
            }
        });

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

                if (hasPermissions(VerifyProfile.this, PERMISSIONS)) {
//                    if (gpsTracker.canGetLocation()) {
//                        lat = gpsTracker.getLatitude() + "";
//                        lng = gpsTracker.getLongitude() + "";
//
//                        Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                    Uri photoUri = Uri.fromFile(getOutputPhotoFile());
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    intent1.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(intent1, REQUEST_CAMERA);
//                    } else {
//                        initialliseLocation(VerifyProfile.this);
//                    }
                } else {
                    ActivityCompat.requestPermissions(VerifyProfile.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        });

    }


    private void onCaptureImageResult(Intent data) {
        try {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(getResources().getString(R.string.app_name) + Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        if (!destination.exists()) {
            destination.mkdir();
        }

        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
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
        ivGesture.setImageBitmap(thumbnail);
        UpLoadImageApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    Uri photoUri = Uri.fromFile(getOutputPhotoFile());
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    intent1.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(intent1, REQUEST_CAMERA);
                } else {
                    Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void UpLoadImageApi() {
//        progress.show();

        progressDialog = new ProgressDialog(VerifyProfile.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(VerifyProfile.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);


        try {

            Log.i(getClass().getName(), "profile_image > " + profileImage + "user_id > " + manageSession.getProfileModel().getUser_id() + "app_token > " + Constant.APPTOKEN + "user_id > " + Constant.APPTOKEN + " type " + "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File profileImageFile = new File(imagePath);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("gesture_image", profileImageFile.getName(), propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("user_id"), manageSession.getProfileModel().getUser_id());
        RequestBody app_token = RequestBody.create(MediaType.parse("app_token"), Constant.APPTOKEN);
        RequestBody latitude = RequestBody.create(MediaType.parse("userlat"), "");
        RequestBody longitude = RequestBody.create(MediaType.parse("userlong"), "");
        RequestBody address = RequestBody.create(MediaType.parse("address"), "");
//        RequestBody image = RequestBody.create(MediaType.parse("image"), "");
        RequestBody type = RequestBody.create(MediaType.parse("type"), "gesture");


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
//                            ProfileModel profileModel = manageSession.getProfileModel();

//                            profileModel.setProfileImage(jsonObject.optJSONObject("data").optString("profile_image"));

//                            manageSession.setProfileModel(profileModel);

                            manageSession.setLoginPreference("1");
                            Intent intent = new Intent(VerifyProfile.this, WaitingListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
           /* if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (gpsTracker.canGetLocation()) {
                    lat = gpsTracker.getLatitude() + "";
                    lng = gpsTracker.getLongitude() + "";

                    Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());

//                    if (gpsTracker.getLatitude() == 0.0 || gpsTracker.getLongitude() == 0.0) {
//                        initialliseLocation(VerifyProfile.this);
//                        Toast.makeText(VerifyProfile.this, "Initialize", Toast.LENGTH_SHORT).show();
//                    }
                } else {
//                    Intent intent = getIntent();
//                    finish();
//                    startActivity(intent);
                }
            } else*/
            if (requestCode == SELECT_FILE) {
//                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
//        else {
//            if (requestCode == REQUEST_CHECK_SETTINGS) {
//                initialliseLocation(VerifyProfile.this);
//            }
//        }
    }
}
