package com.datingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.fragment.NotificationListFragment;
import com.datingapp.fragment.TermsAndCondition;
import com.datingapp.util.Constant;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button btnCreateAccount;
    private LinearLayout backBtn;
    private ImageView imgBack, imgCalendar;
    private TextView edtBirthday;
    private LinearLayout linDOB, llName, llEmail, llPassword, llTerms;
    DatePickerDialog datePickerDialog;
    CoordinatorLayout coordinatorLayout;
    private EditText edtEmailID, edtUserName, edtPassword;
    String s_dob, s_email, s_username, s_password, s_gender = "", lookingFor = "", notiificationId = "";
    ProgressDialog progressDialog;
    private Context context;
//    private DataBase dataBase;
    private ManageSession manageSession;
    GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 7777;
    private WebView webView;
    private FrameLayout editContainer;
    private RelativeLayout rlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Intent intent = getIntent();
        lookingFor = intent.getStringExtra("lookingfor");
        initView();

        notiificationId = manageSession.getFCMToken();
        if (notiificationId.equals("")) {
            notiificationId = FirebaseInstanceId.getInstance().getToken();
            manageSession.setFCMToken(notiificationId);
        }

        ClickListener();
        initialliseLocation(CreateAccountActivity.this);
    }

    private void initView() {
        AppsContants.sharedpreferences = getSharedPreferences(AppsContants.MyPREFERENCES, Context.MODE_PRIVATE);
        s_gender = AppsContants.sharedpreferences.getString(AppsContants.GENDER, "");
        context = this;
        manageSession = new ManageSession(this);
//        dataBase = new DataBase(context);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        imgBack = findViewById(R.id.imgBack);
        imgCalendar = findViewById(R.id.imgCalendar);
        edtBirthday = findViewById(R.id.edtBirthday);
        linDOB = findViewById(R.id.linDOB);

        edtEmailID = findViewById(R.id.edtEmailID);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        backBtn = (LinearLayout) findViewById(R.id.backBtn);

        llEmail = (LinearLayout) findViewById(R.id.linEmail);
        llPassword = (LinearLayout) findViewById(R.id.linPassword);
        llName = (LinearLayout) findViewById(R.id.linUsername);
        llTerms = (LinearLayout) findViewById(R.id.ll_terms);
        webView = (WebView) findViewById(R.id.web_view);

        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        editContainer = (FrameLayout) findViewById(R.id.edit_container);
    }


    private void ClickListener() {
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
        llName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtUserName.requestFocus();
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(edtUserName, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Intent intent = new Intent(CreateAccountActivity.this , LocationActivity.class);
                // startActivity(intent);

                new OutLook().hideKeyboard(CreateAccountActivity.this);

                s_email = edtEmailID.getText().toString();
                s_username = edtUserName.getText().toString();
                s_password = edtPassword.getText().toString().trim();
                s_dob = edtBirthday.getText().toString();

                boolean isError = false;


                if (s_email.isEmpty()) {
                    isError = true;
                    edtEmailID.requestFocus();
                    //Toast.makeText(SignupActivity.this, "Email Address is Invalid", Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.enter_login_txt), Snackbar.LENGTH_LONG);

                    snackbar.show();
                } else if (!new OutLook().checkEmail(s_email)) {
                    Snackbar.make(coordinatorLayout, "Please enter valid email.", Snackbar.LENGTH_SHORT).show();
//                } else if (!(s_email.contains("gmail.com") || s_email.contains("yahoo.com") || s_email.contains("hotmail.com") || s_email.contains("aol.com") || s_email.contains("live.com") || s_email.contains("rediffmail.com") || s_email.contains("ymail.com") || s_email.contains("outlook.com"))) {
//                    Snackbar.make(coordinatorLayout, "Please enter valid email domain.", Snackbar.LENGTH_SHORT).show();
                } else if (s_username.equals("")) {
                    isError = true;
                    edtUserName.requestFocus();
                    //  Toast.makeText(SignupActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please enter name.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (s_password.equals("")) {
                    isError = true;
                    edtPassword.requestFocus();
                    // Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please enter password.", Snackbar.LENGTH_LONG);

                    snackbar.show();

                } else if (s_dob.equals("")) {
                    isError = true;
                    edtBirthday.requestFocus();
                    //Toast.makeText(SignupActivity.this, "Please enter dob", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please select dob.", Snackbar.LENGTH_LONG);

                    snackbar.show();

                } else if (!isError) {
                    if (new NetConnection().isConnected(CreateAccountActivity.this)) {

//                        createAccount();
                        createAccountApi();

                    } else {
                        // Toast.makeText(SignupActivity.this, "Internet connection error", Toast.LENGTH_LONG).show();

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Internet connection error", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }

                }

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent  intent = new Intent(CreateAccountActivity.this , LocationActivity.class);
                startActivity(intent);*/

                selectDate(edtBirthday);
            }
        });

        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(edtBirthday);
            }
        });

        linDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(edtBirthday);
            }
        });

        llTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getTermsAndCondition();

                try {
                Fragment fragment;
                editContainer.setVisibility(View.VISIBLE);
                rlMain.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "activity");
                fragment = new TermsAndCondition();
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.edit_container, fragment);
                transaction.addToBackStack("TermsAndCondition");
                transaction.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void showWaitData() {
        rlMain.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void selectDate(TextView et) {
        DialogFragment dialogfragment = new DatePickerDialogClass(et);
        dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
    }


    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        TextView editText;

        public DatePickerDialogClass() {

        }

        @SuppressLint("ValidFragment")
        public DatePickerDialogClass(TextView editText) {
            this.editText = editText;
        }

        @SuppressLint("NewApi")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    R.style.CustomDatePickerDialogTheme, this, year, month, day);

//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.DatePickerTheme, this, year, month, day);

            datepickerdialog.getDatePicker().setCalendarViewShown(false);
            datepickerdialog.getDatePicker().setSpinnersShown(true);
//            datepickerdialog.getDatePicker().setMaxDate(new Date().getTime());
            datepickerdialog.getDatePicker().setLayoutMode(1);

            try {
                datepickerdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }catch (Exception e){
                e.printStackTrace();
            }

            calendar.add(Calendar.YEAR, -18);
            datepickerdialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return datepickerdialog;
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            editText.setText(new OutLook().formatDateShow(year+"-"+(month + 1)+"-"+dayOfMonth));
//            editText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }
    }

    public void createAccountApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", "datingapp12345");
        map.put("user_email", edtEmailID.getText().toString());
        map.put("username", edtUserName.getText().toString());
        map.put("password", edtPassword.getText().toString());
        map.put("device_token", notiificationId);
        map.put("gender", s_gender);
        map.put("dob", edtBirthday.getText().toString());
        map.put("device_type", "Android");
        map.put("lookingfor", lookingFor);
        map.put("imei", new OutLook().getIMEIorDeviceId(context));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.registerApi(map);
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
                            JSONObject object = jsonObject.optJSONObject("data");
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.serializeNulls().create();
                            ProfileModel bean = gson.fromJson(object.toString(), ProfileModel.class);
//                            dataBase.clearProfile();
//                            dataBase.addProfileData(bean);
                            manageSession.setProfileModel(bean);
                            manageSession.setLoginPreference("3");
//                                Intent  intent = new Intent(CreateAccountActivity.this , LocationActivity.class);
                            Intent intent = new Intent(CreateAccountActivity.this, AfterLocationSelect.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(coordinatorLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
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
                            status.startResolutionForResult(CreateAccountActivity.this, REQUEST_CHECK_SETTINGS);
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

//                        initialliseLocation(CreateAccountActivity.this);
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
