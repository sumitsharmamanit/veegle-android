package com.datingapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.datingapp.DatingApp;
import com.datingapp.HomeActivity;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.adapter.FullScreenImageAdapter;
import com.datingapp.adapter.VeiwPagerAdapter1;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.DetailsFragmentNextBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class DetailsFragmentNext extends BaseFragment implements BaseSliderView.OnSliderClickListener, OnMapReadyCallback, View.OnClickListener {

    private Context context;
    private ManageSession manageSession;
    private ProfileModel profileModel;
    private DetailsFragmentNextBinding detailsFragmentNextBinding;
    private ArrayList<String> imagesList;
    private LinearLayoutManager linearLayoutManager;
    private GoogleMap mMap;
    private GPSTracker gps;
    private Double latitude = 0.0, longitude = 0.0;
    private LatLng currentLoc;
    private ProgressDialog progressDialog;
    private ArrayList<String> reportList;
    SearchableSpinner spReport;
    private String report = "", spinnerReason = "", enterREason = "";
    private ArrayList<BlogDTO> educationList;
    private Socket socket;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            profileModel = (ProfileModel) getArguments().getSerializable("profileModel");
        }

//        DatingApp app = (DatingApp) getActivity().getApplication();
//        socket = app.getSocket();
//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        socket.on("offline_response", offlineResponse);
//
//        socket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        socket.disconnect();
//
//        socket.off(Socket.EVENT_CONNECT, onConnect);
//        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        socket.off("offline_response", offlineResponse);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsFragmentNextBinding = DataBindingUtil.inflate(inflater, R.layout.details_fragment_next, container, false);
        return detailsFragmentNextBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
        initView(view);
        initToolBar();
        ((HomeActivity) context).bottomVisibility(false);

        if (profileModel.getProfilevisit() != null && profileModel.getProfilevisit().equals("0")) {
            profileVisitedApi();
        } else {
            getAutoMatchNotifications();
        }

        setValues();

//        detailsFragmentNextBinding.speedView.setCenterCircleColor(R.color.gray);
        detailsFragmentNextBinding.speedView.setMaxSpeed(100);
        detailsFragmentNextBinding.speedView.setMinSpeed(0);
//        detailsFragmentNextBinding.speedView.setSpeedAt(Integer.parseInt(profileModel.getPercentage().replace("%", "")));
        if (!profileModel.getSpeedometer().equals(""))
            detailsFragmentNextBinding.speedView.speedTo(Float.parseFloat(profileModel.getSpeedometer()), 10000);

        DatingApp app = (DatingApp) getActivity().getApplication();
        socket = app.getSocket();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("offline_response", offlineResponse);

        socket.connect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        socket.disconnect();

        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("offline_response", offlineResponse);
    }

    OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (!isConnected) {
                    getUserList();
//                    }
                }
            });
        }
    };

    public void getUserList() {
        try {
            Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

            JSONObject jobject = new JSONObject();
            jobject.put("id", manageSession.getProfileModel().getUser_id());
//            Log.v("get User List", "==> " + jobject);
//            socket.emit("getUserList", jobject);
            socket.emit("offline", jobject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Socket Exception ==> ", " " + e);
        }
    }

    private Emitter.Listener offlineResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "Offline", Toast.LENGTH_LONG).show();

                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>> Offline");
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
//                    isConnected = false;
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "diconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "Error connecting", Toast.LENGTH_LONG).show();

                    getUserList();
                }
            });
        }
    };

    private void setValues() {
        if (profileModel != null) {
            if (profileModel.getSocialType() != null && !profileModel.getSocialType().equals("1")) {
                String to = profileModel.getProfileImage().replaceAll("(?<!http:)//", "/");
                imagesList.add(to);
            } else {
                imagesList.add(profileModel.getProfileImage());
            }
            if (profileModel.getUserPics() != null && profileModel.getUserPics().size() > 0) {
                for (int i = 0; i < profileModel.getUserPics().size(); i++) {
                    Log.i(getClass().getName(), "User Pics is inner " + i);
                    imagesList.add(Constant.IMAGE_SERVER_PATH + profileModel.getUserPics().get(i).getPicUrl());
                }
            }
            showSlider();

            if (profileModel.getCrossPath() != null && profileModel.getCrossPath().size() > 0)
                setMap();
            else {
                detailsFragmentNextBinding.tvMapText.setVisibility(View.GONE);
                detailsFragmentNextBinding.mvMapView.setVisibility(View.GONE);
            }


            if (profileModel.getFriends() != null && profileModel.getFriends().size() > 0) {
//                detailsFragmentNextBinding.rvFriends.setAdapter(new ImageAdapter(context, profileModel.getFriends(), onItemClickCallback, false));
            } else {
//                detailsFragmentNextBinding.rvFriends.setVisibility(View.GONE);
//                detailsFragmentNextBinding.tvNoFriend.setVisibility(View.VISIBLE);
            }

            detailsFragmentNextBinding.tvPercentage.setText(profileModel.getPercentage());
            detailsFragmentNextBinding.tvName.setText(profileModel.getUsername() + ", " + profileModel.getAge());
//            if (profileModel.getIsOnline().equals("1"))
//                detailsFragmentNextBinding.tvActive.setText("- Active Today");
//            else
//                detailsFragmentNextBinding.tvActive.setText("- Not Active Today");

            if (OutLook.days <= 90)
                detailsFragmentNextBinding.tvActive.setText("- " + new OutLook().getUpdateTime(profileModel.getUpdatedOn(), context));
            else
                detailsFragmentNextBinding.tvActive.setText("- In Active");

            detailsFragmentNextBinding.tvMin.setText("- " + profileModel.getDistancebetweenusers());
            detailsFragmentNextBinding.tvPopularity.setText("- " + profileModel.getPopulerity());
//            hobbies
            if (profileModel.getCountry() != null && !profileModel.getCountry().equals("")) {
                detailsFragmentNextBinding.tvAddress.setText("(" + profileModel.getAddress() + ", " + profileModel.getCountry() + ")");
                detailsFragmentNextBinding.tvAddress.setVisibility(View.VISIBLE);
            } else if (profileModel.getAddress() != null && !profileModel.getAddress().equals("")) {
                detailsFragmentNextBinding.tvAddress.setText("(" + profileModel.getAddress() + ")");
                detailsFragmentNextBinding.tvAddress.setVisibility(View.VISIBLE);
            } else {
                detailsFragmentNextBinding.tvAddress.setVisibility(View.GONE);
            }
            detailsFragmentNextBinding.tvAbout.setText(profileModel.getAboutme());
            detailsFragmentNextBinding.tvRelationship.setText(profileModel.getMaternitystatus());
            detailsFragmentNextBinding.tvChildren.setText(profileModel.getChildren());
            detailsFragmentNextBinding.tvEthnicity.setText(profileModel.getEthnicity());
            detailsFragmentNextBinding.tvOrientation.setText(profileModel.getOreintation());
            detailsFragmentNextBinding.tvBodyType.setText(profileModel.getBodytype());
            detailsFragmentNextBinding.tvHeight.setText(profileModel.getHeight());
            detailsFragmentNextBinding.tvWeight.setText(profileModel.getWeight());
            detailsFragmentNextBinding.tvCompany.setText(profileModel.getWork());
            detailsFragmentNextBinding.tvEducation.setText(profileModel.getEducation());
            detailsFragmentNextBinding.tvGeneralRoutine.setText(profileModel.getGeneralroutine());
            detailsFragmentNextBinding.tvWeekendRoutine.setText(profileModel.getWeekendroutine());

            int height[] = new int[2];
            height = getDeviceHeightWidth();
            Log.i(getClass().getName(), "Total height is > " + height[0]);
//            detailsFragmentNextBinding.slider.getLayoutParams().height = (height[1] - (int)(height[1]/3));
            detailsFragmentNextBinding.slider.getLayoutParams().height = (height[0] - (int) (height[0] / (2.5)));
            detailsFragmentNextBinding.slider.requestLayout();
//            detailsFragmentNextBinding.slider.setMinimumHeight((height[1] - (int)(height[1]/3)));
            Log.i(getClass().getName(), "Slider height is > " + (height[0] - (int) (height[0] / 4)));


            if (profileModel.getHobbies() != null && !profileModel.getHobbies().equals("")) {

                try {
                    List<String> hobbiesList = Arrays.asList(profileModel.getHobbies().split(","));
                    for (int i = 0; i < hobbiesList.size(); i++) {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        float scale = getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (3 * scale + 0.5f);
                        int dpAsPixels1 = (int) (7 * scale + 0.5f);
                        int dpAsPixels2 = (int) (2 * scale + 0.5f);
                        int dpAsPixels3 = (int) (15 * scale + 0.5f);
                        int dpAsPixels4 = (int) (1 * scale + 0.5f);
                        lparams.setMargins(dpAsPixels, dpAsPixels2, dpAsPixels, dpAsPixels2);
                        TextView textView = new TextView(context);
                        textView.setPadding(dpAsPixels1, dpAsPixels4, dpAsPixels1, dpAsPixels);
                        textView.setLayoutParams(lparams);
                        textView.setText(hobbiesList.get(i));
                        textView.setTextColor(getResources().getColor(R.color.white));
                        textView.setTextSize(12);
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setCornerRadius(dpAsPixels3);
                        drawable.setStroke(1, getResources().getColor(R.color.white));
                        textView.setBackground(drawable);
//                textView.setBackground(getResources().getDrawable(R.drawable.button_bg_rounder_withborderline));
//                detailsFragmentNextBinding.flHobbies.addView(textView);

                        Log.i(getClass().getName(), "Hobbies is " + textView.getText().toString());
//                detailsFragmentNextBinding.llNobbies.addView(textView);
//                flexboxLayout.addView(textView);

                        detailsFragmentNextBinding.flowContainer.addView(textView);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                detailsFragmentNextBinding.tvNoHobbies.setVisibility(View.VISIBLE);
                detailsFragmentNextBinding.flowContainer.setVisibility(View.GONE);
            }

       /*     if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1")) {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_1));
            } else {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_2));
            }*/

            if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_4));
            } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("0")) {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_1));
            } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("0") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_3));
            } else {
                detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_2));
            }
        }
    }

    public void setMap() {
        try {
            if (detailsFragmentNextBinding.mvMapView != null) {
                detailsFragmentNextBinding.mvMapView.onCreate(null);
                detailsFragmentNextBinding.mvMapView.onResume();
                detailsFragmentNextBinding.mvMapView.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
//            gps = new GPSTracker(context);

            MapsInitializer.initialize(context);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (mMap != null) {
//                if (gps.canGetLocation()) {
                latitude = Double.parseDouble(profileModel.getCrossPath().get(0).getUserLat());
                longitude = Double.parseDouble(profileModel.getCrossPath().get(0).getUserLong());
//                    currentLoc = new LatLng(latitude, longitude);


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));


                mMap.setMinZoomPreference(16.0f);
                mMap.setMaxZoomPreference(16.0f);
//                }

                for (int i = 0; i < profileModel.getCrossPath().size(); i++) {
                    currentLoc = new LatLng(Double.parseDouble(profileModel.getCrossPath().get(i).getUserLat()), Double.parseDouble(profileModel.getCrossPath().get(i).getUserLong()));

                    Log.i(getClass().getName(), "Lat >>>>>> " + profileModel.getCrossPath().get(i).getUserLat() + " Long >>>>>> " + profileModel.getCrossPath().get(i).getUserLong());

                    if (profileModel.getCrossPath().get(i).getSocialType() != null && !profileModel.getCrossPath().get(i).getSocialType().equals("1")) {
                        String to = profileModel.getCrossPath().get(i).getProfileImage().replaceAll("(?<!http:)//", "/");
                        new DownloadImageTask(currentLoc, profileModel).execute(to);
                    } else {
                        new DownloadImageTask(currentLoc, profileModel).execute(profileModel.getCrossPath().get(i).getProfileImage());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmp = null;
        LatLng lng;
        ProfileModel profileModel;

        public DownloadImageTask(LatLng lng, ProfileModel profileModel) {
            this.profileModel = profileModel;
            this.lng = lng;
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

//            if (profileModel.getUser_id().equals(manageSession.getProfileModel().getUser_id())) {
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.pin2));
//            }

//            if (profileModel.getGender().equalsIgnoreCase("Male")) {
//                if (profileModel.getProfileImage() != null) {
//                    Picasso.with(context).load(String.valueOf(result)).resize(50, 50).centerCrop().placeholder(R.drawable.male).error(R.drawable.male).into(ivProfile);
//                }
//            } else {
//                if (profileModel.getProfileImage() != null) {
//                    Picasso.with(context).load(String.valueOf(result)).resize(50, 50).centerCrop().placeholder(R.drawable.female).error(R.drawable.female).into(ivProfile);
//                }
//            }


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

        }
    }

    private void initView(View view) {
        reportList = new ArrayList<>();
        imagesList = new ArrayList<>();
        gps = new GPSTracker(context);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        detailsFragmentNextBinding.rvFriends.setLayoutManager(linearLayoutManager);

        detailsFragmentNextBinding.ivTimeline.setOnClickListener(this);

        detailsFragmentNextBinding.tvBlock.setOnClickListener(this);
        detailsFragmentNextBinding.tvAbuse.setOnClickListener(this);
        detailsFragmentNextBinding.ivOptions.setOnClickListener(this);
        detailsFragmentNextBinding.ivLike.setOnClickListener(this);
        detailsFragmentNextBinding.ivDislike.setOnClickListener(this);
        detailsFragmentNextBinding.ivMessage.setOnClickListener(this);

        detailsFragmentNextBinding.ivTriangle.setColorFilter(getResources().getColor(R.color.white));
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Details", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void showSlider() {
        Log.i(getClass().getName(), "Size is >" + imagesList.size());
//        for(String name : imagesList){
        for (int i = 0; i < imagesList.size(); i++) {

//            }
            Log.i(getClass().getName(), "Value is inner " + imagesList.get(i));

            DefaultSliderView textSliderView = new DefaultSliderView(context);
            // initialize a SliderLayout
            textSliderView
//                    .description(name)
                    .image(imagesList.get(i))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", imagesList.get(i));

            detailsFragmentNextBinding.slider.addSlider(textSliderView);
        }
        detailsFragmentNextBinding.slider.stopAutoCycle();
        detailsFragmentNextBinding.slider.setPresetTransformer(SliderLayout.Transformer.Stack);
//        detailsFragmentNextBinding.slider.setCustomIndicator(detailsFragmentNextBinding.customIndicator);
        detailsFragmentNextBinding.slider.setCustomAnimation(new DescriptionAnimation());
        detailsFragmentNextBinding.slider.setDuration(5000);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
        showFullImage(detailsFragmentNextBinding.slider.getCurrentPosition());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        initToolBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_timeline:
                TimeLineFragment timeLineFragment = new TimeLineFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", profileModel.getUser_id());
                bundle.putString("image", profileModel.getProfileImage());
                bundle.putString("name", profileModel.getUsername());
                Log.i(getClass().getName(), "Name is >" + profileModel.getUsername());
                timeLineFragment.setArguments(bundle);
                addFragmentWithoutRemove(R.id.output, timeLineFragment, "TimeLineFragment");
                break;
            case R.id.iv_options:
                detailsFragmentNextBinding.llBlock.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_block:
                dialogBlock();
                break;
            case R.id.tv_abuse:
                reportList = new ArrayList<>();
                dialogReport();
                break;
            case R.id.iv_like:
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
//                getLikeOrRemove(profileModel.getUser_id(), "1");

                getLikeOrRemoveApi(profileModel.getUser_id(), "1");
                break;
            case R.id.iv_dislike:
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
//                getLikeOrRemove(profileModel.getUser_id(), "2");

                getLikeOrRemoveApi(profileModel.getUser_id(), "2");
                break;
            case R.id.slider:
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
                break;
            case R.id.iv_message:
                Fragment fragment = new ChatFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("detail", true);
                bundle1.putString("shop_id", profileModel.getUser_id());
                bundle1.putString("shop_name", profileModel.getUsername());
                if (profileModel.getSocialType() != null && !profileModel.getSocialType().equals("1")) {
                    String to = profileModel.getProfileImage().replaceAll("(?<!http:)//", "/");
                    bundle1.putString("profile_image", to);
                } else {
                    bundle1.putString("profile_image", profileModel.getProfileImage());
                }
                bundle1.putString("details", "details");

//                        MessageListAdapter.msgPosition=position;


                fragment.setArguments(bundle1);

                replaceFragmentWithBack(R.id.output, fragment, "ChatFragment", "MessageList");
                break;
        }
    }

    public void dialogBlock() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_user);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.red));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnYes.setBackground(gradientDrawable1);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(context.getResources().getColor(R.color.bg_btn));
        gradientDrawable2.setCornerRadius(dpAsPixels);
        btnNo.setBackground(gradientDrawable2);

        ImageView ivCancel = (ImageView) dialog.findViewById(R.id.iv_close);

        final EditText etReason = (EditText) dialog.findViewById(R.id.et_block_reason);
        etReason.setVisibility(View.VISIBLE);

        TextView tvHeader = (TextView) dialog.findViewById(R.id.tv_header);
        final RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_education);

        final SearchableSpinner spEducation = (SearchableSpinner) dialog.findViewById(R.id.sp_education);

        tvHeader.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

        spEducation.setTitle("Select Reason");
        spEducation.setPositiveButton("Close");

        educationList = new ArrayList<>();
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setName("Pretending to someone");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Fake Account");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Nudity");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Abusive");
        educationList.add(blogDTO);

        spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerReason = educationList.get(position).getName();
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTextColor(context.getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spEducation.onTouch(relativeLayout.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
            }
        });

        ArrayAdapter<BlogDTO> oriAdapter = new ArrayAdapter<BlogDTO>(context,
                R.layout.spinner_textview_one, educationList);
        spEducation.setAdapter(oriAdapter);

        spEducation.setSelection(0);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                blockUser(dialog);
                try {
                    enterREason = etReason.getText().toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                blockUserApi(dialog);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }


    public void blockUserApi(final Dialog dialog) {
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
        map.put("blocked_user_id", profileModel.getUser_id());
        map.put("reason", "block");
        map.put("feedbackreason", spinnerReason);
        map.put("anotherreason", enterREason);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.blockUserApi(map);
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
                            HomeFragment.blockId = profileModel.getUser_id();
                            dialog.dismiss();
                            detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            popFragment();
                            popFragment();
                        } else {
                            dialog.dismiss();
                            detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(detailsFragmentNextBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void popFragment() {
        FragmentManager fragmentManager = ((HomeActivity) context).getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    public void dialogReport() {
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

        final TextView tvReport = (TextView) dialog.findViewById(R.id.tv_reprt);
        final RelativeLayout rlReport = (RelativeLayout) dialog.findViewById(R.id.rl_reprt);
        rlReport.setVisibility(View.VISIBLE);
        tvUpload.setVisibility(View.GONE);
        final EditText etBlog = (EditText) dialog.findViewById(R.id.et_blog);
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        btnDone.setText("Ok");
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.iv_close);
        spReport = (SearchableSpinner) dialog.findViewById(R.id.sp_reprt);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        spReport.setTitle("Report");
        spReport.setPositiveButton("Close");
        spReport.setSelection(0);

        reportList.add("It's spam");
        reportList.add("It's inappropriate");
        reportList.add("Others");

        ArrayAdapter<String> reprtAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_textview, reportList);
        spReport.setAdapter(reprtAdapter);

        spReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTextColor(context.getResources().getColor(R.color.black));
                report = reportList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        float scale = getResources().getDisplayMetrics().density;
//        GradientDrawable gradientDrawable = new GradientDrawable();
//        gradientDrawable.setStroke(((int) (1 * scale + 0.5f)), context.getResources().getColor(R.color.jumbo));
//        gradientDrawable.setCornerRadius(((int) (5 * scale + 0.5f)));
//
//        GradientDrawable gradientDrawable2 = new GradientDrawable();
//        gradientDrawable2.setStroke(((int) (1 * scale + 0.5f)), context.getResources().getColor(R.color.jumbo));
//        gradientDrawable2.setCornerRadius(((int) (5 * scale + 0.5f)));
//
//        tvUpload.setBackground(gradientDrawable);
//        rlReport.setBackground(gradientDrawable);
//        etBlog.setBackground(gradientDrawable2);

//        GradientDrawable drawable = (GradientDrawable) tvUpload.getBackground();
//        drawable.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);
//
//        GradientDrawable drawable1 = (GradientDrawable) etBlog.getBackground();
//        drawable1.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);
//
//        GradientDrawable drawable2 = (GradientDrawable) rlReport.getBackground();
//        drawable2.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);


        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.btn_color));
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnDone.setBackground(gradientDrawable1);

        new OutLook().hideKeyboard(getActivity());

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spReport.setVisibility(View.VISIBLE);
                spReport.onTouch(tvReport.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                tvReport.setVisibility(View.GONE);

            }
        });

        rlReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spReport.setVisibility(View.VISIBLE);
                spReport.onTouch(rlReport.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                tvReport.setVisibility(View.GONE);

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String blog = etBlog.getText().toString();
                if (!report.equals("Others")) {
//                    reportAbuse(dialog, blog);
                    reportAbuseApi(dialog, blog);

                } else {
                    if (!blog.equals("")) {
//                        reportAbuse(dialog, blog);

                        reportAbuseApi(dialog, blog);
                    } else {
                        Snackbar.make(detailsFragmentNextBinding.parentLayout, "Please enter for report.", Snackbar.LENGTH_SHORT).show();
                    }

                }

            }
        });
        dialog.show();
    }

    public void reportAbuseApi(final Dialog dialog, String text) {
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
        map.put("report_user_id", profileModel.getUser_id());
        map.put("type", report);
        map.put("reason", text);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.reportAbuseApi(map);
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
                            dialog.dismiss();
                            detailsFragmentNextBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(detailsFragmentNextBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    public void getLikeOrRemoveApi(final String id, final String status) {
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
        map.put("user_to_id", id);
        map.put("like_status", status);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.setLikeApi(map);
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
                        String status11 = jsonObject.optString("response");
                        String message = jsonObject.optString("message");

                        if (status11.equals("true")) {
                            if (status.equals("2")) {
                                HomeFragment.blockId = id;
                                popFragment();
                                popFragment();
                            } else {
//                                    Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
//                                    detailsFragmentNextBinding.ivLike.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                profileModel.setLikeStatus("1");

                                if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                                    detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_4));
                                } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("0")) {
                                    detailsFragmentNextBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_1));
                                }
//                                    DetailsFragment detailsFragment = new DetailsFragment();
//                                    detailsFragment.setImages();
                                DetailsFragment.a = 1;
                            }
                        } else {
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(detailsFragmentNextBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    public void profileVisitedApi() {
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
        map.put("visit_userid", profileModel.getUser_id());
        map.put("reason", "Block");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.profilevisitedbyUserApi(map);
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
                        getAutoMatchNotifications();
                        if (status.equals("1")) {

                        } else {
                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(detailsFragmentNextBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void getAutoMatchNotifications() {
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
        Call<ResponseBody> call = apiInterface.callAutoMatch(map);
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

                        } else {
//                            Snackbar.make(detailsFragmentNextBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(detailsFragmentNextBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) context).hideAds();
    }

    public void showFullImage(int position) {

        final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_background)));
//        nagDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nagDialog.setContentView(R.layout.full_view_pager);
        FullScreenImageAdapter adapter1;
        VeiwPagerAdapter1 viewPager = (VeiwPagerAdapter1) nagDialog.findViewById(R.id.pager);
        ImageView imageView = (ImageView) nagDialog.findViewById(R.id.img_close);


        //viewPager.onInterceptTouchEvent();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nagDialog.dismiss();
            }
        });
//        ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.img_close);
//        final ZoomableImageView ivPreview = (ZoomableImageView) nagDialog.findViewById(R.id.imgFullImage);

        OnItemClickListener.OnItemClickCallback onItemClickCross = new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                nagDialog.dismiss();
            }
        };

        adapter1 = new FullScreenImageAdapter(context, imagesList, onItemClickCross, true);

        viewPager.setAdapter(adapter1);

        // displaying selected image first
        viewPager.setCurrentItem(position);


        // ivPreview.setImage(ImageSource.resource(R.drawable.com_facebook_button_send_icon));

        // ivPreview.setBackgroundDrawable(dd);
     /*   Picasso.with(context).load(productDTO.getProImages().get(position)))
                .placeholder(R.drawable.img_default).into(ivPreview);*/

        //Uri myUri = Uri.parse(productDTO.getProImages().get(position));


//        Log.d("Iamge Path",productDTO.getProImages().get(position));

//        Picasso.with(context).load(productDTO.getProImages().get(position)).into(ivPreview);


//        Picasso.with(context).load(stringList.get(position)).into(ivPreview);
//
//
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nagDialog.dismiss();
//            }
//        });


        nagDialog.show();
    }

}
