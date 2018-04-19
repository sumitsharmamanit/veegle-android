package com.datingapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.datingapp.DatingApp;
import com.datingapp.EditProfileActivity;
import com.datingapp.HomeActivity;
import com.datingapp.MapActivity;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.databinding.MyAccountBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAccount extends BaseFragment implements View.OnClickListener {

    private MyAccountBinding myAccountBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
//    Socket socket;
//    private DataBase dataBase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myAccountBinding = DataBindingUtil.inflate(inflater, R.layout.my_account, container, false);
        return myAccountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
//        dataBase = new DataBase(context);

        initView();
        initToolBar();
        setValues();
    }

    private void initView() {
        myAccountBinding.llMyPreference.setOnClickListener(this);
        myAccountBinding.llInviteFriend.setOnClickListener(this);
        myAccountBinding.llSetting.setOnClickListener(this);
        myAccountBinding.llAbout.setOnClickListener(this);
        myAccountBinding.llNeed.setOnClickListener(this);
        myAccountBinding.llProfile.setOnClickListener(this);
        myAccountBinding.llFeedback.setOnClickListener(this);
        myAccountBinding.llFacebook.setOnClickListener(this);

        myAccountBinding.parentLayout.setPadding(0, 0, 0, 80);
        myAccountBinding.parentLayout.setClipToPadding(false);
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("My Account", false);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(true);
        initToolBar();
        switch (item.getItemId()) {
            case R.id.menu_live:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setValues() {
        if (manageSession.getProfileModel().getProfileImage() != null) {
//            if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equalsIgnoreCase("1"))
//                Picasso.with(context).load(Constant.IMAGE_SERVER_PATH + manageSession.getProfileModel().getProfileImage()).resize(250, 250).centerCrop().into(myAccountBinding.ivProfile);
//            else


            try {
                if (manageSession.getProfileModel().getSocialType() != null && !manageSession.getProfileModel().getSocialType().equals("1")) {
//                    if (manageSession.getProfileModel().getSocialType().equals("3")) {
//                        String image = manageSession.getProfileModel().getProfileImage().replaceAll("(?<!https:)\\/\\/", "/");
//                        Picasso.with(context).load(image).error(R.drawable.user).placeholder(R.drawable.user).into(myAccountBinding.ivProfile);

//                        Toast.makeText(context, "1111111111", Toast.LENGTH_SHORT).show();
//                    } else {
                        new DownloadImageTask().execute(manageSession.getProfileModel().getProfileImage());
//                    }
                } else {
//                    Toast.makeText(context, "44444444444", Toast.LENGTH_SHORT).show();
                    if (manageSession.getProfileModel().getGender() != null && manageSession.getProfileModel().getGender().equalsIgnoreCase("Male")) {
                        Log.i(getClass().getName(), "Link is >>>>>>" + manageSession.getProfileModel().getProfileImage());
                        Picasso.with(context).load(manageSession.getProfileModel().getProfileImage()).placeholder(R.drawable.user).resize(250, 250).centerCrop().into(myAccountBinding.ivProfile);
                    } else {
                        Log.i(getClass().getName(), "Path Name is >>>>>>" + manageSession.getProfileModel().getProfileImage());
                        Picasso.with(context).load(manageSession.getProfileModel().getProfileImage()).placeholder(R.drawable.user).resize(250, 250).centerCrop().into(myAccountBinding.ivProfile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (manageSession.getProfileModel().getGender() != null && manageSession.getProfileModel().getGender().equalsIgnoreCase("Male")){
//                Log.i(getClass().getName(), "Link is >>>>>>"+manageSession.getProfileModel().getProfileImage());
//                Picasso.with(context).load(manageSession.getProfileModel().getProfileImage()).placeholder(R.drawable.user).resize(250, 250).centerCrop().into(myAccountBinding.ivProfile);
//            }else {
//                Picasso.with(context).load(manageSession.getProfileModel().getProfileImage()).placeholder(R.drawable.user).resize(250, 250).centerCrop().into(myAccountBinding.ivProfile);
//            }
        }

        myAccountBinding.tvName.setText(manageSession.getProfileModel().getUserfullname());

/*        try {
            DatingApp app = (DatingApp) getActivity().getApplication();
            socket = app.getSocket();

            Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

            try {
                JSONObject jobject = new JSONObject();
                jobject.put("id", manageSession.getProfileModel().getUser_id());
                Log.v("offline", "==> " + jobject);
                socket.emit("offline", jobject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("User Socket Exception", " " + e);
        }*/
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
            myAccountBinding.ivProfile.setImageBitmap(result);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_about:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                replaceFragmentWithBack(R.id.output, new AboutApp(), "AboutApp", "MyAccount");
                break;
            case R.id.ll_feedback:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                Bundle bundle1 = new Bundle();
                AboutApp aboutApp = new AboutApp();
                bundle1.putString(Constant.ADD_FEEDBACK, Constant.ADD_FEEDBACK);
                aboutApp.setArguments(bundle1);
                replaceFragmentWithBack(R.id.output, aboutApp, "AboutApp", "MyAccount");
                break;
            case R.id.ll_need:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                TermsAndCondition termsAndCondition = new TermsAndCondition();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.TERMS_CONDITION, Constant.TERMS_CONDITION);
                termsAndCondition.setArguments(bundle);
//                replaceFragmentWithBack(R.id.output, termsAndCondition, "TermsAndCondition", "MyAccount");

                addFragmentWithoutRemove(R.id.output, termsAndCondition, "TermsAndCondition");
//                getTermsAndCondition();
                break;
            case R.id.ll_my_preference:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                replaceFragmentWithBack(R.id.output, new MyPreference(), "MyPreference", "MyAccount");
                break;
            case R.id.ll_invite_friend:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                replaceFragmentWithBack(R.id.output, new InviteFriends(), "InviteFriends", "MyAccount");
                break;
            case R.id.ll_setting:
                ((HomeActivity) context).showInterstitialAds();
                ((HomeActivity) context).hideAds();
                replaceFragmentWithBack(R.id.output, new Settings(), "Settings", "MyAccount");
                break;
            case R.id.ll_profile:
                OutLook.editStatus = true;
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;
            case R.id.ll_facebook:
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse("https://www.facebook.com/veegleapp"));
                startActivity(facebookIntent);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setValues();
        ((HomeActivity) context).showAds();
    }
}
