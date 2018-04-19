package com.datingapp.fragment;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.BuildConfig;
import com.datingapp.DatingApp;
import com.datingapp.HomeActivity;
import com.datingapp.MapActivity;
import com.datingapp.Model.ProfileModel;
import com.datingapp.PersonalityQuiz;
import com.datingapp.R;
import com.datingapp.adapter.BlindDateAdapter;
import com.datingapp.adapter.HomeAdapter;
import com.datingapp.adapter.VeiwPagerAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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

import static android.content.ContentValues.TAG;


public class HomeFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

//        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
//      LocationAssistant.Listener

    private RecyclerView rvHome;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private ProgressDialog progressDialog;
    private RelativeLayout parentLayout;
    private ArrayList<ProfileModel> profileModelArrayList, profileModels;
    private HomeAdapter homeAdapter;
    private GPSTracker gpsTracker;
    private double latitude = 0.0, longitude = 0.0;
    public static final int REQUEST_CHECK_SETTINGS = 6060;

    private GoogleApiClient googleApiClient;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 2 secs */
    private long FASTEST_INTERVAL = 2000; /* 15000  2 sec */
    private LocationManager locationManager;

    private ManageSession manageSession;
    public static String blockId = "", favoriteStatus = "";
    private boolean clickStatus = false;
    private TextView tvNoRecord;
    private Socket socket;
    private ArrayList<ProfileModel> profileModelArrayList123;
    private BlindDateAdapter blindDateAdapter;
    private VeiwPagerAdapter viewPager;
    private Dialog nagDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rv_list, container, false);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() == null)
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

    private Emitter.Listener offlineResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() == null)
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
            if(getActivity() == null)
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
            if(getActivity() == null)
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        initView(view);
        initToolBar();
        initToolBar();
        ((HomeActivity) context).bottomVisibility(true);


//        assistant = new LocationAssistant(context, this, LocationAssistant.Accuracy.HIGH, 5000, false);
//        assistant.setVerbose(true);
    /*    if (gpsTracker != null && gpsTracker.canGetLocation()) {
//            latitude = gpsTracker.getLatitude();
//            longitude = gpsTracker.getLongitude();

//            latitude = gpsTracker.getLatitude();
//            longitude = gpsTracker.getLongitude();

            clickStatus = true;
//            getLiveUser();
            getLiveUserApi();
        } else {
            initialliseLocation(context);
        }*/


        if (isLocationEnabled()) {
            clickStatus = true;
            getLiveUserApi();
        } else {
            initialliseLocation(context);
        }


        if (manageSession.isFirstTimeMessageShow()) {
            manageSession.setMessageCount(1);
            ((HomeActivity) context).setNotificationCount(context);
        }

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

    private void initView(View view) {
        manageSession = new ManageSession(context);
        profileModelArrayList = new ArrayList<>();
        profileModelArrayList123 = new ArrayList<>();
        profileModels = new ArrayList<>();

//        Crashlytics.getInstance().crash();


//        dataBase = new DataBase(context);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
        rvHome = (RecyclerView) view.findViewById(R.id.rv_home);
        gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvHome.setLayoutManager(gridLayoutManager);

        rvHome.setPadding(6, 6, 6, 80);
        rvHome.setClipToPadding(false);
        tvNoRecord = (TextView) view.findViewById(R.id.tv_norecord);

        // Item Decorator:
//        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
//        rvHome.setItemAnimator(new FadeInLeftAnimator());

        gpsTracker = new GPSTracker(getActivity());


        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        checkLocation(); //check whether location service is enable or not in your  phone

        Log.i(getClass().getName(), "Firebase Key >> " + manageSession.getFCMToken());
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Home", false);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(true);
        ((HomeActivity) context).bottomVisibility(true);
        initToolBar();

        if (!blockId.equals("")) {
            for (int i = 0; i < profileModelArrayList.size(); i++) {
                if (blockId.equals(profileModelArrayList.get(i).getUser_id())) {
                    profileModelArrayList.remove(i);
                    homeAdapter.notifyDataSetChanged();
                    blockId = "";
                    break;
                }
            }
        }

        if (favoriteStatus != null && !favoriteStatus.equals("")) {
            if (profileModelArrayList.size() > 0) {
                profileModelArrayList.get(Integer.parseInt(favoriteStatus)).setLikeStatus("1");
                homeAdapter.notifyDataSetChanged();
                favoriteStatus = "";
            }
        }

        clickStatus = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(true);
        switch (item.getItemId()) {
            case R.id.menu_live:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    OnItemClickListener.OnItemClickCallback onItemClickListener = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            Bundle bundle = new Bundle();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

            ((HomeActivity) context).hideAds();
            ((HomeActivity) context).showInterstitialAds();

            DetailsFragment detailsFragment = new DetailsFragment();
            bundle.putString("id", profileModelArrayList.get(position).getUser_id());
            bundle.putString("position", position + "");
            detailsFragment.setArguments(bundle);
//            replaceFragmentWithBack(R.id.output, detailsFragment, "DetailsFragment", "HomeFragment");

            HomeFragment homeFragment = new HomeFragment();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Inflate transitions to apply
                Transition changeTransform = TransitionInflater.from(context).
                        inflateTransition(R.transition.change_image_transform);
                Transition explodeTransform = TransitionInflater.from(context).
                        inflateTransition(android.R.transition.explode);


                // Setup exit transition on first fragment
                homeFragment.setSharedElementReturnTransition(changeTransform);
                homeFragment.setExitTransition(explodeTransform);

                // Setup enter transition on second fragment
                detailsFragment.setSharedElementEnterTransition(changeTransform);
                detailsFragment.setEnterTransition(explodeTransform);

                // Find the shared element (in Fragment A)
                RoundedImageView ivProfile = (RoundedImageView) view.findViewById(R.id.iv_profile);

                // Add second fragment by replacing first
                try {
                FragmentTransaction ft = getFragmentManager().beginTransaction()
//                        .replace(R.id.output, detailsFragment)
                        .add(R.id.output, detailsFragment)
                        .addToBackStack("transaction")
                        .addSharedElement(ivProfile, "profile");
                // Apply the transaction
                ft.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                // Code to run on older devices
//                Bundle bundle1 = new Bundle();
//                DetailsFragment detailsFragment1 = new DetailsFragment();
//                bundle1.putString("id", profileModelArrayList.get(position).getUser_id());
//                detailsFragment1.setArguments(bundle1);

//                replaceFragmentWithBack(R.id.output, detailsFragment, "DetailsFragment", "HomeFragment");

                addFragmentWithoutRemove(R.id.output, detailsFragment, "DetailsFragment");
//                addDetailsFragment(R.id.output, detailsFragment, "HomeFragment");

            }
        }
    };


    public void replaceFragmentWithBack1(int containerViewId,
                                         Fragment fragment,
                                         String fragmentTag,
                                         String backStackStateName) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
//                .setCustomAnimations(R.anim.bounce, R.anim.bounce)
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    OnItemClickListener.OnItemClickCallback onItemClickListenerLike = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            if (clickStatus) {
                clickStatus = false;
//                getLikeOrRemove(profileModelArrayList.get(position).getUser_id(), "1", position);

                getLikeOrRemoveApi(profileModelArrayList.get(position).getUser_id(), "1", position);
            }
        }
    };

    OnItemClickListener.OnItemClickCallback onItemClickListenerDisLike = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
//            getLikeOrRemove(profileModelArrayList.get(position).getUser_id(), "2", position);

            getLikeOrRemoveApi(profileModelArrayList.get(position).getUser_id(), "2", position);
        }
    };

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

    public void getLiveUserApi() {
//        if (manageSession.getLatitude().equalsIgnoreCase("") || manageSession.getLongitude().equalsIgnoreCase("")){}

//        if (OutLook.LATITUDE == 0.0 || OutLook.LONGITUDE == 0.0) {
//            replaceFragmentWithoutBack1(R.id.output, new HomeFragment(), "HomeFragment");
//        } else {

        if (manageSession.getLatitude().equals("") || manageSession.getLongitude().equals("")) {
//            latitude = 0.0;
//            longitude = 0.0;
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        } else {
            latitude = Double.parseDouble(manageSession.getLatitude());
            longitude = Double.parseDouble(manageSession.getLongitude());
        }
//            latitude = OutLook.LATITUDE;
//            longitude = OutLook.LONGITUDE;
//        }

        new OutLook().hideKeyboard((HomeActivity) context);
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("myuserid", manageSession.getProfileModel().getUser_id());
        map.put("userlat", latitude + "");
        map.put("userlong", longitude + "");
        map.put("address", getAddressFromLatLong(latitude, longitude, true));
        map.put("live", "0");
        map.put("country", getAddressFromLatLong(latitude, longitude, false));


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
                        String messageCount = jsonObject.optString("message_count");
                        String quizstatus = jsonObject.optString("quizstatus");
                        String blindstatus = jsonObject.optString("blindstatus");
                        String versionCode = jsonObject.optString("version_code");
                        String partneranswer = jsonObject.optString("partneranswer");

                        try {
                            int count = Integer.parseInt(messageCount);
                            if (count > 0) {
//                            Toast.makeText(context, "Total Count >>>"+count, Toast.LENGTH_SHORT).show();
                                manageSession.setMessageCount(count);
                                ((HomeActivity) context).setNotificationCount(context);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (status.equals("true")) {
                            profileModels = new Gson().fromJson(jsonObject.optJSONArray("user_list").toString(), new TypeToken<ArrayList<ProfileModel>>() {
                            }.getType());

                            Log.i(getClass().getName(), "Size is > " + profileModels.size());


                            if (profileModels.size() > 0) {
                                tvNoRecord.setVisibility(View.GONE);
                                rvHome.setVisibility(View.VISIBLE);

                                for (int i = 0; i < profileModels.size(); i++) {
                                    Log.i(getClass().getName(), "position " + i);

                                    if (!profileModels.get(i).getUser_id().equals(manageSession.getProfileModel().getUser_id())) {
                                        if ((profileModels.get(i).getLikeStatus() != null && profileModels.get(i).getLikeStatus().equals("1")) || (profileModels.get(i).getLikeStatus() != null && profileModels.get(i).getLikeStatus().equals("0"))) {
                                            if ((profileModels.get(i).getBlockStatus() != null && profileModels.get(i).getBlockStatus().equals("0"))) {
                                                Log.i(getClass().getName(), "Inner loop " + i);
                                                profileModelArrayList.add(profileModels.get(i));
                                            }
                                        }
                                    }
                                }

                                Log.i(getClass().getName(), "Size is > " + profileModelArrayList.size());

                                homeAdapter = new HomeAdapter(context, profileModelArrayList, onItemClickListener, onItemClickListenerLike, onItemClickListenerDisLike);
                                rvHome.setAdapter(homeAdapter);
                                profileModels.clear();
                                Log.i(getClass().getName(), "Size is > " + profileModels.size());
                            } else {
                                tvNoRecord.setVisibility(View.VISIBLE);
                                rvHome.setVisibility(View.GONE);
                            }

                        } else {
//                                Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            tvNoRecord.setText(message);
                            tvNoRecord.setVisibility(View.VISIBLE);
                            rvHome.setVisibility(View.GONE);
                        }

                        if (quizstatus != null && quizstatus.equals("0")) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    getQuizStatus(false, "personality");
                                }
                            }, 1000);
                        } else if (partneranswer != null && !partneranswer.equals(manageSession.getPartnerQuestion()+"")){
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    getQuizStatus(false, "partner");
                                }
                            }, 1000);
                        }

                        if (blindstatus != null && blindstatus.equals("0")) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    getBlindDates();
                                }
                            }, 2000);
                        }

                        if (Integer.parseInt(versionCode) != BuildConfig.VERSION_CODE) {
                            dialogForName("New Veegle's version available. Please update app with new version and enjoy new features.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void getQuizStatus(final boolean blindDateStatus, String type) {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        if (!blindDateStatus) {
            map.put("type", type);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call;
        if (blindDateStatus) {
            call = apiInterface.updateBlindStatus(map);
        } else {
            call = apiInterface.getQuizStatus(map);
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
                        String status = jsonObject.optString("response");
                        String message = jsonObject.optString("message");

                        if (status.equals("1")) {
                            if (!blindDateStatus) {
                                if (manageSession.getPerQuestion() != manageSession.getPerAnswer()) {
                                    startActivity(new Intent(context, PersonalityQuiz.class));
                                } else if (manageSession.getPartnerQuestion() != manageSession.getPartnerAnswer()) {
                                    Intent intent = new Intent(context, PersonalityQuiz.class);
                                    intent.putExtra(Constant.PARTNER_PREFERENCE, Constant.PARTNER_PREFERENCE);
                                    startActivity(intent);
                                }
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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void getLikeOrRemoveApi(String id, final String status, final int position) {
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
                clickStatus = true;
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
//                                    homeAdapter.notifyItemRemoved(position);
                                profileModelArrayList.remove(position);
                                homeAdapter.notifyDataSetChanged();
                            } else {
                                profileModelArrayList.get(position).setLikeStatus("1");
                                homeAdapter.notifyDataSetChanged();
                            }
//                                profileModelArrayList = new Gson().fromJson(response.optJSONArray("user_list").toString(), new TypeToken<ArrayList<ProfileModel>>(){}.getType());
//                                rvHome.setAdapter(new HomeAdapter(context, profileModelArrayList, onItemClickListener, onItemClickListenerLike, onItemClickListenerDisLike));
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
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(((HomeActivity) context), REQUEST_CHECK_SETTINGS);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
//                        gpsTracker = new GPSTracker(context);
//                        if (gpsTracker.canGetLocation()) {
//                            latitude = gpsTracker.getLatitude();
//                            longitude = gpsTracker.getLongitude();


//                            getLiveUserApi();


//                        }
//
                        replaceFragmentWithoutBack1(R.id.output, new HomeFragment(), "HomeFragment");
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
        }

    }

    public void replaceFragmentWithoutBack1(int containerViewId, Fragment fragment, String fragmentTag) {
        if (context != null) {
            try {
            ((HomeActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerViewId, fragment, fragmentTag)
                    .commitAllowingStateLoss();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Snackbar.make(parentLayout, "Please again tab on Home Screen to refresh screen.", Snackbar.LENGTH_SHORT).show();
        }
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
            Toast.makeText(context, "Location not Detected", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        ((HomeActivity) context).showAds();
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    public void getBlindDates() {
        new OutLook().hideKeyboard(getActivity());

        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("app_token", Constant.APPTOKEN);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.getBlindDates(map);
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
                            profileModelArrayList123 = new Gson().fromJson(jsonObject.optJSONArray("match").toString(), new TypeToken<ArrayList<ProfileModel>>() {
                            }.getType());

                            getQuizStatus(true, "");
                            showDialogForBlindDates();
                        } else {
//                            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
//                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    public void showDialogForBlindDates() {
        nagDialog = new Dialog(context);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setContentView(R.layout.dialog_view_pager);
        Window window = nagDialog.getWindow();
        nagDialog.setCanceledOnTouchOutside(false);
        nagDialog.setCancelable(false);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));

        viewPager = (VeiwPagerAdapter) nagDialog.findViewById(R.id.pager);
        viewPager.setScrollDurationFactor(2); // make the animation twice as slow

        TabLayout tabLayout = (TabLayout) nagDialog.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);

        OnItemClickListener.OnItemClickCallback onItemClickCross = new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                nagDialog.dismiss();
            }
        };

        OnItemClickListener.OnItemClickCallback onItemClickLike = new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
//                Toast.makeText(context, "Like Button", Toast.LENGTH_SHORT).show();
                getLikeOrRemoveApi11(profileModelArrayList123.get(position).getUser_id(), "1", position);
            }
        };

        OnItemClickListener.OnItemClickCallback onItemClickDisLike = new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
//                getLikeOrRemoveApi(profileModelArrayList.get(position).getUser_id(), "2", position);

                viewPager.setCurrentItem(position + 1);
                if (position == (profileModelArrayList123.size() - 1)) {
                    nagDialog.dismiss();
                }
            }
        };


        blindDateAdapter = new BlindDateAdapter(context, profileModelArrayList123, onItemClickCross, onItemClickLike, onItemClickDisLike);

        viewPager.setAdapter(blindDateAdapter);

        nagDialog.show();
    }

    public void getLikeOrRemoveApi11(String id, final String status, final int position) {
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
                                if (profileModelArrayList123.size() == 1) {
                                    nagDialog.dismiss();
                                }
                                profileModelArrayList123.remove(position);
                                blindDateAdapter.notifyDataSetChanged();
                                viewPager.setAdapter(blindDateAdapter);
                            } else {

                                profileModelArrayList123.get(position).setLikeStatus("1");
                                blindDateAdapter.notifyDataSetChanged();
                                viewPager.setAdapter(blindDateAdapter);
                                viewPager.setCurrentItem(position + 1);

                                if (position == (profileModelArrayList123.size() - 1)) {
                                    nagDialog.dismiss();
                                }
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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


//    public void connectSocket() {
//        try {
//
//            DatingApp app = (DatingApp) getActivity().getApplication();
//            socket = app.getSocket();
//            Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());
//
//            getUserList();
//
//
//            socket.on("offline_response", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.v("offline_response", " " + args[0]);
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.v("User Socket Exception", " " + e);
//        }
//
//    }

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
        TextView tvTitle = (TextView) dialog.findViewById(R.id.title_dailog_box);
        tvTitle.setText("Alert");
        tvText.setText(text);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=com.veegleapp&hl=en")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        });

        dialog.show();
    }
}
