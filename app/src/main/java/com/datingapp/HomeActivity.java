package com.datingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.fragment.ChatFragment;
import com.datingapp.fragment.DetailsFragment;
import com.datingapp.fragment.HomeFragment;
import com.datingapp.fragment.MessageList;
import com.datingapp.fragment.MyAccount;
import com.datingapp.fragment.NotificationFragment;
import com.datingapp.fragment.NotificationListFragment;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.makeramen.roundedimageview.RoundedImageView;

//import android.support.design.widget.TabLayout;

public class HomeActivity extends AppCompatActivity {

    private ImageView home, blog, user;
    private  ImageView notification, message;
    //    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private ViewPager mViewPager;
    FrameLayout frameLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private NotificationCompat.Builder notificationBuilder;
    private int currentNotificationID = 0;
    NotificationManager notificationManager;
    private Toolbar toolbar;
    public TextView mTitle;
    private  TextView tvNotificationCount, tvMessageCount;
    private Context context;
    private LinearLayout lin1, lin2, lin3, lin4, lin5;
    private View v1, v2, v3, v4, v5;

    private  ManageSession manageSession;
    //    private DataBase dataBase;
    private RelativeLayout rlBottom;
    private boolean doubleBackToExitPressedOnce = false, messageClickStatus = false;
    private RelativeLayout parentLayout;
    HomeFragment homeFragment;
    private String mapStatus = "", builderStatus = "", user_d = "", name = "", image = "";
    public RoundedImageView ivProfile;
    private Intent intent;
    private AdView mAdView; //20
    private InterstitialAd interstitialAd; //11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
//        dataBase = new DataBase(context);
        manageSession = new ManageSession(context);
        initView();
        tvNotificationCount = (TextView) findViewById(R.id.tv_notification_count);
        tvMessageCount = (TextView) findViewById(R.id.tv_msg_count);

        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_LOST"));

        intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("map")) {
                mapStatus = intent.getStringExtra("map");
                Log.i(getClass().getName(), "mapStatus id is >>>>>>>" + mapStatus);
                OutLook.editStatus = false;
            } else if (intent.hasExtra("type")) {
                builderStatus = intent.getStringExtra("type");
                if (intent.hasExtra("to_user_id")) {
                    user_d = intent.getStringExtra("to_user_id");
                    image = intent.getStringExtra("profile_image");
                    name = intent.getStringExtra("userfullname");
                }

                if (builderStatus.equals("Approve")) {
                    builderStatus = "";
                }
            }
        }

        final int newColor = getResources().getColor(R.color.white);
        home.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);


//        Fragment fragment;
        if (OutLook.editStatus) {
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            popAllFragment();

            try {
                MyAccount myAccount = new MyAccount();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.output, myAccount);
                transaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            user.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//            home.setColorFilter(null);
//            notification.setColorFilter(null);
//            message.setColorFilter(null);
//            blog.setColorFilter(null);

            setMenuIconColorCode(user, newColor);

            OutLook.editStatus = false;
        } else {
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (!mapStatus.equals("")) {
//                popAllFragment();

//                home.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                notification.setColorFilter(null);
//                user.setColorFilter(null);
//                message.setColorFilter(null);
//                blog.setColorFilter(null);

                setMenuIconColorCode(home, newColor);

                Bundle bundle = new Bundle();
                bundle.putString("id", mapStatus);
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(bundle);
                try {
                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.output, detailsFragment);
//                transaction1.addToBackStack("DetailsFragment");
                    transaction1.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!builderStatus.equals("")) {
                NotificationListFragment notificationListFragment = new NotificationListFragment();
                Bundle bundle = new Bundle();

//                notification.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                home.setColorFilter(null);
//                user.setColorFilter(null);
//                message.setColorFilter(null);
//                blog.setColorFilter(null);

                setMenuIconColorCode(notification, newColor);

                if (builderStatus.equals("invite")) {
                    manageSession.setInviteCount(0);
                    bundle.putString(Constant.INVITATION_LIST, Constant.INVITATION_LIST);
                    notificationListFragment.setArguments(bundle);

                    try {
                        Fragment notificationFragment = new NotificationFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, notificationFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        FragmentManager manager1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = manager1.beginTransaction();
                        transaction1.add(R.id.output, notificationListFragment);
                        transaction1.addToBackStack("NotificationListFragment");
                        transaction1.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
               /* }else if (builderStatus.equals("accept")) {
                    manageSession.setInviteCount(0);
                    bundle.putString(Constant.INVITATION_LIST, Constant.INVITATION_LIST);
                    bundle.putString("accept", "accept");
                    notificationListFragment.setArguments(bundle);

                    Fragment notificationFragment = new NotificationFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, notificationFragment);
                    transaction.commitAllowingStateLoss();

                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    transaction1.add(R.id.output, notificationListFragment);
                    transaction1.addToBackStack("NotificationListFragment");
                    transaction1.commitAllowingStateLoss();*/
                } else if (builderStatus.equals("like")) {
                    manageSession.setLikeCount(0);
                    bundle.putString(Constant.LIKE_LIST, Constant.LIKE_LIST);
                    notificationListFragment.setArguments(bundle);

                    try {
                        Fragment notificationFragment = new NotificationFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, notificationFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        FragmentManager manager1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = manager1.beginTransaction();
                        transaction1.add(R.id.output, notificationListFragment);
                        transaction1.addToBackStack("NotificationListFragment");
                        transaction1.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
                } else if (builderStatus.equals("automatch")) {
                    manageSession.setAutoMatched(0);
                    bundle.putString(Constant.AUTO_MATCH, Constant.AUTO_MATCH);
                    notificationListFragment.setArguments(bundle);

                    try {
                        Fragment notificationFragment = new NotificationFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, notificationFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        FragmentManager manager1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = manager1.beginTransaction();
                        transaction1.add(R.id.output, notificationListFragment);
                        transaction1.addToBackStack("NotificationListFragment");
                        transaction1.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (builderStatus.equals("visit")) {
                    manageSession.setProfileCount(0);
                    bundle.putString(Constant.PROFILE_VISIT_LIST, Constant.PROFILE_VISIT_LIST);
                    notificationListFragment.setArguments(bundle);

                    try {
                        Fragment notificationFragment = new NotificationFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, notificationFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        FragmentManager manager1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = manager1.beginTransaction();
                        transaction1.add(R.id.output, notificationListFragment);
                        transaction1.addToBackStack("NotificationListFragment");
                        transaction1.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
                } else if (builderStatus.equals("accept") || builderStatus.equals("chat")) {
                    manageSession.setMessageCount(0);
                    setNotificationCount(context);

//                    message.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                    home.setColorFilter(null);
//                    user.setColorFilter(null);
//                    notification.setColorFilter(null);
//                    blog.setColorFilter(null);

                    setMenuIconColorCode(message, newColor);

                    if (builderStatus.equals("chat")) {
                        try {
                            Fragment messageList = new MessageList();
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.output, messageList);
                            transaction.commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Fragment messageList = new MessageList();
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.output, messageList);
                            transaction.commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Bundle bundle1 = new Bundle();
                        if (!user_d.equals("")) {
                            bundle1.putString("shop_id", user_d);
                            bundle1.putString("shop_name", name);
                            bundle1.putString("profile_image", image);

                            try {
                                Fragment chatFragment = new ChatFragment();
                                chatFragment.setArguments(bundle1);
                                FragmentManager manager1 = getSupportFragmentManager();
                                FragmentTransaction transaction1 = manager1.beginTransaction();
                                transaction1.replace(R.id.output, chatFragment);
                                transaction1.addToBackStack("ChatFragment");
                                transaction1.commitAllowingStateLoss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else if (builderStatus.equals("block") || builderStatus.equals("Veegle")) {
//                    home.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                    message.setColorFilter(null);
//                    user.setColorFilter(null);
//                    notification.setColorFilter(null);
//                    blog.setColorFilter(null);

                    setMenuIconColorCode(home, newColor);

                    try {
                        homeFragment = new HomeFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, homeFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    manageSession.setBlockCount(0);
//                    bundle.putString(Constant.BLOCK_LIST, Constant.BLOCK_LIST);
//                    notificationListFragment.setArguments(bundle);
//                    replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");

                } else {
                    try {
                        Fragment notificationFragment = new NotificationFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.output, notificationFragment);
                        transaction.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        FragmentManager manager1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = manager1.beginTransaction();
                        transaction1.add(R.id.output, notificationListFragment);
                        transaction1.addToBackStack("NotificationListFragment");
                        transaction1.commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                builderStatus = "";
            } else if (!SplashScreenActivity.chatStatus.equals("")) {

                try {
                    homeFragment = new HomeFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, homeFragment);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SplashScreenActivity.chatStatus = "";
            } else {
//                homeFragment = new HomeFragment();
//                FragmentManager manager = getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.output, homeFragment);
//                transaction.commitAllowingStateLoss();

                manageSession.setMessageCount(0);
                setNotificationCount(context);

//                message.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                home.setColorFilter(null);
//                user.setColorFilter(null);
//                notification.setColorFilter(null);
//                blog.setColorFilter(null);

                setMenuIconColorCode(message, newColor);

                try {
                    Fragment messageList = new MessageList();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, messageList);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Fragment messageList1 = new MessageList();
                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.output, messageList1);
                    transaction1.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        setNotificationCount(context);


        lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (!messageClickStatus)
                    popAllFragment();

                messageClickStatus = false;

//                mViewPager.setVisibility(View.GONE);
//                tabLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);

                try {
                    Fragment fragment;
                    fragment = new HomeFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, fragment);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                home.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                notification.setColorFilter(null);
//                message.setColorFilter(null);
//                blog.setColorFilter(null);
//                user.setColorFilter(null);

                setMenuIconColorCode(home, newColor);

            }
        });
        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (!messageClickStatus)
                    popAllFragment();

                messageClickStatus = false;

//                mViewPager.setVisibility(View.GONE);
//                tabLayout.setVisibility(View.GONE);

                manageSession.setTotalCount(0);
                setNotificationCount(context);

                frameLayout.setVisibility(View.VISIBLE);

                try {
                    Fragment fragment;
                    fragment = new NotificationFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, fragment);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                notification.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                home.setColorFilter(null);
//                message.setColorFilter(null);
//                blog.setColorFilter(null);
//                user.setColorFilter(null);

                setMenuIconColorCode(notification, newColor);

            }
        });

        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                popAllFragment();
                messageClickStatus = true;

//                mViewPager.setVisibility(View.GONE);
//                tabLayout.setVisibility(View.GONE);

                manageSession.setMessageCount(0);
                setNotificationCount(context);

                frameLayout.setVisibility(View.VISIBLE);
                try {
                    Fragment fragment;
                    fragment = new MessageList();
                    // fragment = new ProductListNewFragment();

                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, fragment);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                message.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                home.setColorFilter(null);
//                notification.setColorFilter(null);
//                blog.setColorFilter(null);
//                user.setColorFilter(null);

                setMenuIconColorCode(message, newColor);
            }
        });
        lin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialogForBlindDates();

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                if (!messageClickStatus)
                    popAllFragment();

                messageClickStatus = false;

//                mViewPager.setVisibility(View.GONE);
//                tabLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);

                try {
                    Fragment fragment;
                    fragment = new NotificationListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.SHOW_BLOG, Constant.SHOW_BLOG);
                    fragment.setArguments(bundle);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, fragment);
                    transaction.commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                blog.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                home.setColorFilter(null);
//                notification.setColorFilter(null);
//                message.setColorFilter(null);
//                user.setColorFilter(null);

                setMenuIconColorCode(blog, newColor);
            }
        });

        lin5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
                if (!messageClickStatus)
                    popAllFragment();

                messageClickStatus = false;

//                mViewPager.setVisibility(View.GONE);
//                tabLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);

                try {
                    Fragment fragment;
                    fragment = new MyAccount();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.output, fragment);
                    transaction.commitAllowingStateLoss();

//                    user.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//                    home.setColorFilter(null);
//                    notification.setColorFilter(null);
//                    message.setColorFilter(null);
//                    blog.setColorFilter(null);

                    setMenuIconColorCode(user, newColor);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                startActivity(new Intent(context, FullScreenActivity.class));
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
//                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

    }

    private void showInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void hideAds() {
        mAdView.setVisibility(View.GONE);
    }

    public void showAds() {
        mAdView.setVisibility(View.VISIBLE);
    }


    public void showInterstitialAds() {
        if (manageSession.adCount() == 7) {
            manageSession.setadCount(1);

            AdRequest adRequest1 = new AdRequest.Builder()
                    .build();

            // Load ads into Interstitial Ads
            interstitialAd.loadAd(adRequest1);

            interstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        } else {
            int count = manageSession.adCount() + 1;
            manageSession.setadCount(count);
        }


    }

    public void setMenuIconColorCode(ImageView imageView, int newColor) {

        message.setColorFilter(null);
        home.setColorFilter(null);
        user.setColorFilter(null);
        notification.setColorFilter(null);
        blog.setColorFilter(null);

        message.setImageDrawable(getResources().getDrawable(R.drawable.messages));
        notification.setImageDrawable(getResources().getDrawable(R.drawable.notification));

        imageView.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//        imageView.setColorFilter(getResources().getColor(R.color.white));
    }

    public void setNotificationCount(Context context1) {
        try {
            manageSession = new ManageSession(context1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, 0, 0, 0);
//            tvNotificationCount = (TextView) findViewById(R.id.tv_notification_count);
//            tvMessageCount = (TextView) findViewById(R.id.tv_msg_count);
//            notification = (ImageView) findViewById(R.id.notification);
//            message = (ImageView) findViewById(R.id.message);

            if (manageSession.getTotalCount() > 0) {
                tvNotificationCount.setVisibility(View.VISIBLE);
                tvNotificationCount.setText(manageSession.getTotalCount() + "");
                layoutParams.setMargins(0, 5, 0, 0);
                notification.setLayoutParams(layoutParams);
            } else {
                tvNotificationCount.setVisibility(View.GONE);
                layoutParams.setMargins(0, 0, 0, 0);
                notification.setLayoutParams(layoutParams);
            }

            if (manageSession.getMessageCount() > 0) {
                tvMessageCount.setVisibility(View.VISIBLE);
//                Toast.makeText(context1, "Home Count >>>"+manageSession.getMessageCount(), Toast.LENGTH_SHORT).show();
                tvMessageCount.setText(manageSession.getMessageCount() + "");
                layoutParams.setMargins(0, 5, 0, 0);
                message.setLayoutParams(layoutParams);
            } else {
                tvMessageCount.setVisibility(View.GONE);
                layoutParams.setMargins(0, 0, 0, 0);
                message.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        rlBottom = (RelativeLayout) findViewById(R.id.bottom);
//        searchView = (SearchView) toolbar.findViewById(R.id.sv);

        ivProfile = (RoundedImageView) toolbar.findViewById(R.id.iv_profile);
        setSupportActionBar(toolbar);

        parentLayout = (RelativeLayout) findViewById(R.id.activity_blank);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

//        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
        frameLayout = (FrameLayout) findViewById(R.id.output);
        home = (ImageView) findViewById(R.id.home);
        notification = (ImageView) findViewById(R.id.notification);
        message = (ImageView) findViewById(R.id.message);
        blog = (ImageView) findViewById(R.id.blog);
        user = (ImageView) findViewById(R.id.user);

        tvNotificationCount = (TextView) findViewById(R.id.tv_notification_count);
        tvMessageCount = (TextView) findViewById(R.id.tv_msg_count);

        v1 = (View) findViewById(R.id.v1);
        v2 = (View) findViewById(R.id.v2);
        v3 = (View) findViewById(R.id.v3);
        v4 = (View) findViewById(R.id.v4);
        v5 = (View) findViewById(R.id.v5);

        lin1 = (LinearLayout) findViewById(R.id.Lin1);
        lin2 = (LinearLayout) findViewById(R.id.Lin2);
        lin3 = (LinearLayout) findViewById(R.id.Lin3);
        lin4 = (LinearLayout) findViewById(R.id.Lin4);
        lin5 = (LinearLayout) findViewById(R.id.Lin5);

    }


    public void bottomVisibility(boolean status) {
        if (status) {
            rlBottom.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
        } else {
            rlBottom.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {

            if (!mapStatus.equals("")) {
                mapStatus = "";
                super.onBackPressed();
                finish();
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Please click BACK again to exit", Snackbar.LENGTH_LONG)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doubleBackToExitPressedOnce = false;
                            }
                        });
                snackbar.setActionTextColor(getResources().getColor(R.color.btn_color));
                snackbar.show();

                try {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //mFilterAction = menu.findItem(R.id.menu_notification);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    //  mgr.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

                    fragmentManager.popBackStack();
                    Log.i(getClass().getName(),
                            "stack count: " + fragmentManager.getBackStackEntryCount());
                    //  Toast.makeText(this,"pop",Toast.LENGTH_SHORT).show();

                } else if (!mapStatus.equals("")) {
                    onBackPressed();
                }
                break;
            case R.id.menu_live:
//                startActivity(new Intent(HomeActivity.this, MapActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void popFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }


    private void showFragment(android.support.v4.app.Fragment targetFragment, String className) {
        try {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.output, targetFragment, className)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createBackButton(String title, boolean status) {
        Spannable text = new SpannableString(title);

        if (status) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_btn));
        } else {
            toolbar.setNavigationIcon(null);
        }

        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),
                0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mTitle.setText(text.toString());
    }

    public void setNavigationIconColor(int code) {
        Drawable mDrawable = toolbar.getNavigationIcon();
        switch (code) {
            case 1:
                mDrawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                toolbar.setNavigationIcon(mDrawable);
                break;
            case 2:
                mDrawable.setColorFilter(getResources().getColor(R.color.abc_secondary_text_material_dark), PorterDuff.Mode.SRC_ATOP);
                toolbar.setNavigationIcon(mDrawable);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == HomeFragment.REQUEST_CHECK_SETTINGS) {
                homeFragment.onActivityResult(requestCode, resultCode, data);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void popAllFragment() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        Log.i(getClass().getName(), "fragment count before " + count);
        for (int i = 0; i < count; ++i) {
            this.getSupportFragmentManager().popBackStack();
        }
    }

    public void colorMessageTab() {
        manageSession.setMessageCount(0);
        setNotificationCount(context);
        final int newColor = getResources().getColor(R.color.white);
        message.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        home.setColorFilter(null);
        notification.setColorFilter(null);
        blog.setColorFilter(null);
        user.setColorFilter(null);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNotificationCount(context);
        }
    };

}
