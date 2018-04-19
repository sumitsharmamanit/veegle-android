package com.datingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private TextView txtForgotPassword;
    private ImageView imgBack;
    private Button btnLogin;
    private EditText edtEmailID, edtPassword;
    ProgressDialog progressDialog;
    private Context context;
    //    private DataBase dataBase;
    private RelativeLayout parentLayout;
    GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    private GPSTracker gps;
    private ManageSession manageSession;
    private LinearLayout llEmail, llPassword;
    private String notiificationId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initialliseLocation(LoginActivity.this);
        manageSession = new ManageSession(this);

        notiificationId = manageSession.getFCMToken();
        if (notiificationId.equals("")) {
            notiificationId = FirebaseInstanceId.getInstance().getToken();
            manageSession.setFCMToken(notiificationId);
        }

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(LoginActivity.this);
                double latitude1 = gps.getLatitude();
                double longitude1 = gps.getLongitude();

                Log.i(getClass().getName(), "Lat >> " + latitude1 + " Long >> " + longitude1);

                if (checkValidation()) {
                    new OutLook().hideKeyboard(LoginActivity.this);
//                    LoginAPI();
                    getLoginApi();
                }
            }
        });
    }

    private void initView() {
        context = this;
//        dataBase = new DataBase(context);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        imgBack = findViewById(R.id.imgBack);
        edtEmailID = findViewById(R.id.edtEmailID);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        llEmail = (LinearLayout) findViewById(R.id.linEmail);
        llPassword = (LinearLayout) findViewById(R.id.linPassword);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtEmailID.requestFocus();
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(edtEmailID, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        llPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPassword.requestFocus();
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(edtPassword, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private boolean checkValidation() {
        if (!new OutLook().isNetworkConnected(context)) {
            new OutLook().isNetworkConnected(context);
            return false;
        } else if (edtEmailID.getText().toString().trim().length() == 0) {
            Snackbar.make(parentLayout, getResources().getString(R.string.enter_login_txt), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!new OutLook().checkEmail(edtEmailID.getText().toString())) {
            Snackbar.make(parentLayout, "Please enter valid email.", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (edtPassword.getText().toString().trim().length() == 0) {
            Snackbar.make(parentLayout, getResources().getString(R.string.enterpassword_txt), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void getLoginApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(LoginActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("logintype", edtEmailID.getText().toString());
        map.put("password", edtPassword.getText().toString());
        map.put("device_token", notiificationId);
        map.put("device_type", Constant.DEVICE_TYPE);
        map.put("imei", new OutLook().getIMEIorDeviceId(context));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.loginApi(map);
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
                            manageSession.setPerQuestion(jsonObject.optJSONObject("data").optInt("personality_quiz_question"));
                            manageSession.setPerAnswer(jsonObject.optJSONObject("data").optInt("personality_quiz_answer"));
                            manageSession.setPartnerQuestion(jsonObject.optJSONObject("data").optInt("partner_question"));
                            manageSession.setPartnerAnswer(jsonObject.optJSONObject("data").optInt("partner_answer"));


                            JSONObject object = jsonObject.optJSONObject("data");
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.serializeNulls().create();
                            ProfileModel bean = gson.fromJson(object.toString(), ProfileModel.class);
//                            dataBase.clearProfile();
                            manageSession.setProfileModel(bean);
//                            dataBase.addProfileData(bean);
                            if (object.getString("activestatus").equals("0")) {
                                manageSession.setLoginPreference("1");
                                Intent intent = new Intent(LoginActivity.this, WaitingListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                manageSession.setLoginPreference("2");
                                SplashScreenActivity.chatStatus = "Splash";
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

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
                t.printStackTrace();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    public void registerDevice() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("app_token", Constant.APPTOKEN);
            map.put("logintype", edtEmailID.getText().toString());
            map.put("password", edtPassword.getText().toString());
            map.put("device_token", notiificationId);
            map.put("device_type", Constant.DEVICE_TYPE);
            map.put("imei", new OutLook().getIMEIorDeviceId(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialliseLocation(Context context) {
        this.context = context;
        googleApiClient = getInstance();
        if (googleApiClient != null) {
            //googleApiClient.connect();
            settingsrequest();
            googleApiClient.connect();
        }
    }

    public GoogleApiClient getInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    public void settingsrequest() {
        Log.e("settingsrequest", "Comes");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // Log.e("Application","Button Clicked");
//                        Toast.makeText(context, "Location is Enabled 11111", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
//                            Toast.makeText(context, "Location is Enabled 22222", Toast.LENGTH_SHORT).show();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //Log.e("Application","Button Clicked2");
//                        Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
//                        Toast.makeText(context, "Success Call Back", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        //  getActivity().finish();

                        initialliseLocation(LoginActivity.this);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

}
