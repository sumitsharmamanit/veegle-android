package com.datingapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GPSTracker gps;
    private MapView mvMapView;
    private Context context;
    private ProgressDialog progressDialog;
    private ManageSession manageSession;
    private LinearLayout parentLayout;
    private ArrayList<ProfileModel> profileModelArrayList, profileModels;
    private Double latitude = 0.0, longitude = 0.0;
    private LatLng currentLoc, localLatlng;
    private ImageView ivBack;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private GPSTracker gpsTracker;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    GoogleApiClient googleApiClient;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 2 secs */
    private long FASTEST_INTERVAL = 2000; /* 15000  2 sec */
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();

        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (hasPermissions(MapActivity.this, PERMISSIONS)) {
            initialliseLocation(context);
        } else {
            ActivityCompat.requestPermissions(MapActivity.this, PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    private void initView() {
        context = this;
        manageSession = new ManageSession(context);
        profileModelArrayList = new ArrayList<>();
        profileModels = new ArrayList<>();
        gpsTracker = new GPSTracker(context);
        mvMapView = (MapView) findViewById(R.id.mv_map_view);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        ivBack = (ImageView) findViewById(R.id.imgBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        checkLocation(); //check whether location service is enable or not in your  phone

    }

    public void setMap() {
        try {
            if (mvMapView != null) {
                mvMapView.onCreate(null);
                mvMapView.onResume();
                mvMapView.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            gps = new GPSTracker(context);

            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (mMap != null) {
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    currentLoc = new LatLng(latitude, longitude);
                    localLatlng = new LatLng(latitude, longitude);


                    if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
                        String to = manageSession.getProfileModel().getProfileImage().replaceAll("(?<!http:)//", "/");
                        new DownloadImageTask(localLatlng, manageSession.getProfileModel(), false).execute(to);
                    } else {
                        new DownloadImageTask(localLatlng, manageSession.getProfileModel(), false).execute(manageSession.getProfileModel().getProfileImage());
                    }

                    getLiveUserApi();


                    String dist = manageSession.getProfileModel().getTodistance();
                    long meter = Integer.parseInt(dist) * 1000;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
//                    mMap.addCircle(new CircleOptions()
//                            .center(new LatLng(latitude, longitude))
//                            .radius(meter)
//                            .strokeColor(getResources().getColor(R.color.half_color))
//                            .strokeWidth(5));
//                            .fillColor(Color.BLUE));
                }

//                mMap.setMinZoomPreference(16.0f);
//                mMap.setMaxZoomPreference(20.0f);


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        try {
                            ProfileModel profileModel = (ProfileModel) marker.getTag();
                            if (!profileModel.getUser_id().equals(manageSession.getProfileModel().getUser_id()))
                                dialogInvite(MapActivity.this, profileModel.getProfileImage(), profileModel.getUserfullname() + ", " + profileModel.getAge(), profileModel.getMaternitystatus(), profileModel.getPercentage(), profileModel.getUser_id(), profileModel.getGender());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmp = null;
        LatLng lng;
        ProfileModel profileModel;
        boolean loaderStatus;

        public DownloadImageTask(LatLng lng, ProfileModel profileModel, boolean loaderStatus) {
            this.profileModel = profileModel;
            this.lng = lng;
            this.loaderStatus = loaderStatus;
        }

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
//            bmImage.setImageBitmap(result);

            View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

            RoundedImageView ivProfile = (RoundedImageView) markerView.findViewById(R.id.iv_profile);
            ImageView imageView = (ImageView) markerView.findViewById(R.id.iv_pin);
            ivProfile.setImageBitmap(result);

            if (profileModel.getUser_id().equals(manageSession.getProfileModel().getUser_id())) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.pin2));
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            markerView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            markerView.draw(canvas);

            Marker marker = mMap.addMarker(new MarkerOptions().position(lng).
                    icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//                                    createCustomMarker1(MapActivity.this, R.drawable.female, "Janet John"))));



            ProfileModel profileModel1 = new ProfileModel();
            profileModel1.setUserfullname(profileModel.getUserfullname());
            profileModel1.setPercentage(profileModel.getPercentage());
            profileModel1.setProfileImage(profileModel.getProfileImage());
            profileModel1.setUser_id(profileModel.getUser_id());
            profileModel1.setMaternitystatus(profileModel.getMaternitystatus());
            profileModel1.setGender(profileModel.getGender());
            profileModel1.setAge(profileModel.getAge());

            marker.setTag(profileModel1);


        }
    }


    public void getLiveUserApi() {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        manageSession.setLatitude(latitude + "");
        manageSession.setLongitude(longitude + "");

//        }else {
//            latitude = Double.parseDouble(manageSession.getLatitude());
//            longitude = Double.parseDouble(manageSession.getLongitude());
//        }

        new OutLook().hideKeyboard(MapActivity.this);
        progressDialog = new ProgressDialog(MapActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(MapActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        Log.e("app_token", Constant.APPTOKEN);
        map.put("myuserid", manageSession.getProfileModel().getUser_id());
        Log.e("myuserid", manageSession.getProfileModel().getUser_id());
        map.put("userlat", latitude + "");
        map.put("userlong", longitude + "");
        Log.e("lat", latitude + "");
        Log.e("long", longitude + "");
        map.put("address", getAddressFromLatLong(latitude, longitude, true));
        Log.e("address", getAddressFromLatLong(latitude, longitude, true) + "");
        map.put("live", "1");
        map.put("country", getAddressFromLatLong(latitude, longitude, false));
        Log.e("country", getAddressFromLatLong(latitude, longitude, false) + "");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.nearByApi(map);
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
                        String status = jsonObject.optString("response");
                        String message = jsonObject.optString("message");

                        if (status.equals("true")) {
//                                profileModelArrayList = new Gson().fromJson(response.optJSONArray("user_list").toString(), new TypeToken<ArrayList<ProfileModel>>() {
//                                }.getType());

                            profileModels = new Gson().fromJson(jsonObject.optJSONArray("user_list").toString(), new TypeToken<ArrayList<ProfileModel>>() {
                            }.getType());

                            Log.i(getClass().getName(), "Size is > " + profileModels.size());
                            for (int i = 0; i < profileModels.size(); i++) {
                                Log.i(getClass().getName(), "position " + i);
                                if ((profileModels.get(i).getLikeStatus() != null && profileModels.get(i).getLikeStatus().equals("1")) || (profileModels.get(i).getLikeStatus() != null && profileModels.get(i).getLikeStatus().equals("0"))) {
                                    if ((profileModels.get(i).getBlockStatus() != null && profileModels.get(i).getBlockStatus().equals("0"))) {
                                        Log.i(getClass().getName(), "Inner loop " + i);
                                        profileModelArrayList.add(profileModels.get(i));
                                    }
                                }
                            }

                            Log.i(getClass().getName(), "Size is >" + profileModelArrayList.size());
                            if (profileModelArrayList != null && profileModelArrayList.size() > 0) {

                                for (int i = 0; i < profileModelArrayList.size(); i++) {

                                    currentLoc = new LatLng(Double.parseDouble(profileModelArrayList.get(i).getUserlat()), Double.parseDouble(profileModelArrayList.get(i).getUserlong()));

                                    if (profileModelArrayList.get(i).getSocialType() != null && !profileModelArrayList.get(i).getSocialType().equals("1")) {
                                        String to = profileModelArrayList.get(i).getProfileImage().replaceAll("(?<!http:)//", "/");
                                        new DownloadImageTask(currentLoc, profileModelArrayList.get(i), true).execute(to);
                                    } else {
                                        new DownloadImageTask(currentLoc, profileModelArrayList.get(i), true).execute(profileModelArrayList.get(i).getProfileImage());
                                    }
                                }
                            }

                            profileModels.clear();

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

    public Bitmap createCustomMarker1(final Context context, @DrawableRes int resource, String _name) {

        View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

        final RoundedImageView ivProfile = (RoundedImageView) markerView.findViewById(R.id.iv_profile);
        ivProfile.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        markerView.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);
        return bitmap;
    }


    public void dialogInvite(Context context, String image, String name, String status, String per, final String id, String gender) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final TextView tvMeet = (TextView) dialog.findViewById(R.id.tv_meet);
        final TextView tvDrink = (TextView) dialog.findViewById(R.id.tv_drink);
        final TextView tvDining = (TextView) dialog.findViewById(R.id.tv_dining);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvStatus = (TextView) dialog.findViewById(R.id.tv_status);
        TextView tvPer = (TextView) dialog.findViewById(R.id.tv_percentage);
        RoundedImageView ivProfile = (RoundedImageView) dialog.findViewById(R.id.iv_profile);
        ImageView ivCancel = (ImageView) dialog.findViewById(R.id.iv_close);

        if (gender.equalsIgnoreCase("Male")) {
            if (image != null) {
                Picasso.with(context).load(image).resize(150, 150).centerCrop().error(context.getResources().getDrawable(R.drawable.male)).placeholder(context.getResources().getDrawable(R.drawable.male)).into(ivProfile);
            }
        } else {
            if (image != null) {
                Picasso.with(context).load(image).resize(150, 150).centerCrop().error(context.getResources().getDrawable(R.drawable.female)).placeholder(context.getResources().getDrawable(R.drawable.female)).into(ivProfile);
            }
        }

        tvName.setText(name);
        tvPer.setText(per);
        if (!status.equals("")) {
            tvStatus.setText("(" + status + ")");
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        tvMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inviteApi(id, tvMeet.getText().toString());
                inviteApiRetrofit(id, tvMeet.getText().toString());
                dialog.dismiss();
            }
        });
        tvDining.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                inviteApi(id, tvDining.getText().toString());
                inviteApiRetrofit(id, tvDining.getText().toString());
                dialog.dismiss();
            }
        });
        tvDrink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                inviteApi(id, tvDrink.getText().toString());
                inviteApiRetrofit(id, tvDrink.getText().toString());
                dialog.dismiss();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                intent.putExtra("map", id);
                Log.i(getClass().getName(), "Map id is >>>>>>>" + id);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        dialog.show();
    }


    public void inviteApiRetrofit(String id, String message) {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(MapActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(MapActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("inviteid", id);
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("message", message);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.inviteUserApi(map);
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
                            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
//                        Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        setMap();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //Log.e("Application","Button Clicked2");
                        Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data);
        /*switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        if (gpsTracker.canGetLocation()) {
                            lat = gpsTracker.getLatitude() + "";
                            lng = gpsTracker.getLongitude() + "";

                            Constant.ADDRESS = getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude());
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                    Log.i(getClass().getName(), "latitude " + latitude + " longitude " + longitude);
                    setMap();
                }
            }
        } else {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                initialliseLocation(context);
            }
        }
    }



    public String getAddressFromLatLong(double latitude, double longitude, boolean status) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            if (status)
                return city;
            else
                return country;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }else {
            initialliseLocation(context);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    initialliseLocation(context);
                } else {
                    Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
//            Toast.makeText(context, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(getClass().getName(), "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(getClass().getName(), "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        OutLook.LATITUDE = location.getLatitude();
//        OutLook.LONGITUDE = location.getLongitude();

        manageSession.setLatitude(location.getLatitude() + "");
        manageSession.setLongitude(location.getLongitude() + "");
    }

    private boolean checkLocation() {
//        if(!isLocationEnabled())
//            initialliseLocation(context);
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
