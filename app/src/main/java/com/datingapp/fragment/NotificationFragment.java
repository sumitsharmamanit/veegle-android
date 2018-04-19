package com.datingapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.datingapp.DatingApp;
import com.datingapp.HomeActivity;
import com.datingapp.MapActivity;
import com.datingapp.Model.MessageDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.PersonalityQuiz;
import com.datingapp.R;
import com.datingapp.adapter.BlindDateAdapter;
import com.datingapp.adapter.MessageListAdapter;
import com.datingapp.adapter.VeiwPagerAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.FragmentNotificationBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends BaseFragment implements View.OnClickListener {

    private FragmentNotificationBinding fragmentNotificationBinding;
    private ManageSession manageSession;
    private Context context;
//    private Socket socket;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNotificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return fragmentNotificationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        initView();
        initToolBar();
        initToolBar();
        manageSession = new ManageSession(context);

        setNotificationBadges();

       /* try {
            DatingApp app = (DatingApp) getActivity().getApplication();
            socket = app.getSocket();

            try {
                Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                JSONObject jobject = new JSONObject();
                jobject.put("id", manageSession.getProfileModel().getUser_id());
                Log.v("offline", "==> " + jobject);
                socket.emit("offline", jobject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.v("User Socket Exception", " " + e);
        }*/

    }

    public void setNotificationBadges() {
        if (manageSession.getLikeCount() > 0) {
            fragmentNotificationBinding.tvLikeCount.setVisibility(View.VISIBLE);
            fragmentNotificationBinding.tvLikeCount.setText(manageSession.getLikeCount() + "");
        } else {
            fragmentNotificationBinding.tvLikeCount.setVisibility(View.GONE);
        }

        if (manageSession.getInviteCount() > 0) {
            fragmentNotificationBinding.tvInviteCount.setVisibility(View.VISIBLE);
            fragmentNotificationBinding.tvInviteCount.setText(manageSession.getInviteCount() + "");
        } else {
            fragmentNotificationBinding.tvInviteCount.setVisibility(View.GONE);
        }

        if (manageSession.getBlockCount() > 0) {
            fragmentNotificationBinding.tvBlockCount.setVisibility(View.VISIBLE);
            fragmentNotificationBinding.tvBlockCount.setText(manageSession.getBlockCount() + "");
        } else {
            fragmentNotificationBinding.tvBlockCount.setVisibility(View.GONE);
        }

        if (manageSession.getProfileCount() > 0) {
            fragmentNotificationBinding.tvVisitCount.setText(manageSession.getProfileCount() + "");
            fragmentNotificationBinding.tvVisitCount.setVisibility(View.VISIBLE);
        } else {
            fragmentNotificationBinding.tvVisitCount.setVisibility(View.GONE);
        }

        if (manageSession.getAutoMatched() > 0) {
            fragmentNotificationBinding.tvAutoCount.setText(manageSession.getAutoMatched() + "");
            fragmentNotificationBinding.tvAutoCount.setVisibility(View.VISIBLE);
        } else {
            fragmentNotificationBinding.tvAutoCount.setVisibility(View.GONE);
        }
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Notifications", false);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initView() {
        fragmentNotificationBinding.rlProfileVisit.setOnClickListener(this);
        fragmentNotificationBinding.rlInvite.setOnClickListener(this);
        fragmentNotificationBinding.rlLike.setOnClickListener(this);
        fragmentNotificationBinding.rlBlock.setOnClickListener(this);
        fragmentNotificationBinding.rlMatch.setOnClickListener(this);
        fragmentNotificationBinding.rlAutoMatch.setOnClickListener(this);

        fragmentNotificationBinding.scrollView.setPadding(0, 0, 0, 80);
        fragmentNotificationBinding.scrollView.setClipToPadding(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(true);
        initToolBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_live:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
        }
        initToolBar();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        NotificationListFragment notificationListFragment = new NotificationListFragment();
        Bundle bundle = new Bundle();

        if (view == fragmentNotificationBinding.rlProfileVisit) {
            manageSession.setProfileCount(0);
            bundle.putString(Constant.PROFILE_VISIT_LIST, Constant.PROFILE_VISIT_LIST);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        } else if (view == fragmentNotificationBinding.rlInvite) {
            manageSession.setInviteCount(0);
            bundle.putString(Constant.INVITATION_LIST, Constant.INVITATION_LIST);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        } else if (view == fragmentNotificationBinding.rlLike) {
            manageSession.setLikeCount(0);
            bundle.putString(Constant.LIKE_LIST, Constant.LIKE_LIST);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        } else if (view == fragmentNotificationBinding.rlBlock) {
            manageSession.setBlockCount(0);
            bundle.putString(Constant.BLOCK_LIST, Constant.BLOCK_LIST);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        } else if (view == fragmentNotificationBinding.rlMatch) {
            bundle.putString(Constant.MATCH, Constant.MATCH);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        } else if (view == fragmentNotificationBinding.rlAutoMatch) {
            manageSession.setAutoMatched(0);
            bundle.putString(Constant.AUTO_MATCH, Constant.AUTO_MATCH);
            notificationListFragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, notificationListFragment, "NotificationListFragment", "NotificationFragment");
        }
    }
}

/*

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        context = getActivity();
//        manageSession = new ManageSession(context);
//
//        DatingApp app = (DatingApp) getActivity().getApplication();
//        socket = app.getSocket();
//
//
//
//        socket.on(Socket.EVENT_CONNECT,onConnect);
//        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
//        socket.on("offline_response", offlineResponse);
//        socket.connect();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        socket.disconnect();
//        socket.off(Socket.EVENT_CONNECT, onConnect);
//        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        socket.off("offline_response", offlineResponse);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off("offline_response", offlineResponse);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                    if(!isConnected) {
                        try {
                            JSONObject jobject = new JSONObject();
                            jobject.put("id", manageSession.getProfileModel().getUser_id());

//            socket.emit("getUserList", jobject);
                            Toast.makeText(context, "Emitted >>"+jobject.toString(), Toast.LENGTH_SHORT).show();

                            socket.emit("offline", jobject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                            mSocket.emit("add user", mUsername);
                        isConnected = true;
//                    }


                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(getClass().getName(), "diconnected");
                    isConnected = false;
                }
            });
        }
    };

    private Emitter.Listener offlineResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "offlineResponse", Toast.LENGTH_LONG).show();
                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNotificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return fragmentNotificationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        initView();
        initToolBar();
        initToolBar();
        manageSession = new ManageSession(context);

        setNotificationBadges();

        DatingApp app = (DatingApp) getActivity().getApplication();
        socket = app.getSocket();



        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on("offline_response", offlineResponse);
        socket.connect();

  */
/*      try {
            DatingApp app = (DatingApp) getActivity().getApplication();
            socket = app.getSocket();

            try {
                Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                JSONObject jobject = new JSONObject();
                jobject.put("id", manageSession.getProfileModel().getUser_id());
                Log.v("offline", "==> " + jobject);
                socket.emit("offline", jobject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.v("User Socket Exception", " " + e);
        }*//*


    }
*/

