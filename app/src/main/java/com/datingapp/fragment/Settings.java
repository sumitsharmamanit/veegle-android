package com.datingapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.EditProfileThirdActivity;
import com.datingapp.HomeActivity;
import com.datingapp.Login_SignupActivity;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.SettingsBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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

//import com.datingapp.PayPalActivity;


public class Settings extends BaseFragment implements View.OnClickListener {

    private SettingsBinding settingsBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
    //    private DataBase dataBase;
    private int a = 0;
    private Dialog dialog;
    public static String FACEBOOK_URL = "https://www.facebook.com/veegal.app.98";
    public static String FACEBOOK_PAGE_ID = "veegal.app.98";
    private ArrayList<BlogDTO> educationList;
    private String spinnerReason = "", enterREason = "", education = "";
    private String[]  itemsDeActivate = {"Technical issues", "App crashes frequently", "Not many users to match with", "Too many fake profiles", "I didn't find any matches", "I don't understand all the features of this App", "I don't find this app interesting", "I'm in a relationship now", "Too many adverts", "I prefer other apps"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsBinding = DataBindingUtil.inflate(inflater, R.layout.settings, container, false);
        return settingsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
//        dataBase = new DataBase(context);

        initToolBar();


//        getContext().registerReceiver(notify, new IntentFilter("notify"));
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        setValues();
    }

    private void initView() {
        settingsBinding.llLogout.setOnClickListener(this);
        settingsBinding.llDeactivate.setOnClickListener(this);
        settingsBinding.llLikeUs.setOnClickListener(this);
        settingsBinding.llTerms.setOnClickListener(this);
        settingsBinding.llAbout.setOnClickListener(this);

        settingsBinding.switchHi.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("hi", "1");
                                notifiyPreferenceApiRetrofit("hi", "1");
                            } else {
//                                notifiyPreferenceApi("hi", "0");
                                notifiyPreferenceApiRetrofit("hi", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchCrushes.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("crushes", "1");
                                notifiyPreferenceApiRetrofit("crushes", "1");
                            } else {
//                                notifiyPreferenceApi("crushes", "0");
                                notifiyPreferenceApiRetrofit("crushes", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchMessage.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("message", "1");
                                notifiyPreferenceApiRetrofit("message", "1");
                            } else {
//                                notifiyPreferenceApi("message", "0");
                                notifiyPreferenceApiRetrofit("message", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchAutomatch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("automatch", "1");
                                notifiyPreferenceApiRetrofit("automatch", "1");
                            } else {
//                                notifiyPreferenceApi("automatch", "0");
                                notifiyPreferenceApiRetrofit("automatch", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchFeedback.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("feedback", "1");
                                notifiyPreferenceApiRetrofit("feedback", "1");
                            } else {
//                                notifiyPreferenceApi("feedback", "0");
                                notifiyPreferenceApiRetrofit("feedback", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchFacebook.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("facebook", "1");
                                notifiyPreferenceApiRetrofit("facebook", "1");
                            } else {
//                                notifiyPreferenceApi("facebook", "0");
                                notifiyPreferenceApiRetrofit("facebook", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchInstagram.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("instagram", "1");
                                notifiyPreferenceApiRetrofit("instagram", "1");
                            } else {
//                                notifiyPreferenceApi("instagram", "0");
                                notifiyPreferenceApiRetrofit("instagram", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchTwitter.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("twitter", "1");
                                notifiyPreferenceApiRetrofit("twitter", "1");
                            } else {
//                                notifiyPreferenceApi("twitter", "0");
                                notifiyPreferenceApiRetrofit("twitter", "0");
                            }
                        }
                    }
                });

        //////////////////


        settingsBinding.switchMatch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("match", "1");
                                notifiyPreferenceApiRetrofit("match", "1");
                            } else {
//                                notifiyPreferenceApi("match", "0");
                                notifiyPreferenceApiRetrofit("match", "0");
                            }
                        }
                    }
                });


        settingsBinding.cbMatchMobile.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("match", "1");
                                notifiyPreferenceApiRetrofit("match", "1");
                            } else {
//                                notifiyPreferenceApi("match", "0");
                                notifiyPreferenceApiRetrofit("match", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbMatchEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("match", "1");
                                notifiyPreferenceApiRetrofit("matchemail", "1");
                            } else {
//                                notifiyPreferenceApi("match", "0");
                                notifiyPreferenceApiRetrofit("matchemail", "0");
                            }
                        }
                    }
                });


        settingsBinding.switchVisit.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("visituser", "1");
                                notifiyPreferenceApiRetrofit("visituser", "1");
                            } else {
//                                notifiyPreferenceApi("visituser", "0");
                                notifiyPreferenceApiRetrofit("visituser", "0");
                            }
                        }
                    }
                });


        settingsBinding.cbVisitMobile.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("visituser", "1");
                                notifiyPreferenceApiRetrofit("visituser", "1");
                            } else {
//                                notifiyPreferenceApi("visituser", "0");
                                notifiyPreferenceApiRetrofit("visituser", "0");
                            }
                        }
                    }
                });


        settingsBinding.cbVisitEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("visituser", "1");
                                notifiyPreferenceApiRetrofit("visituseremail", "1");
                            } else {
//                                notifiyPreferenceApi("visituser", "0");
                                notifiyPreferenceApiRetrofit("visituseremail", "0");
                            }
                        }
                    }
                });


        settingsBinding.switchLike.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("likeuser", "1");
                                notifiyPreferenceApiRetrofit("likeuser", "1");
                            } else {
//                                notifiyPreferenceApi("likeuser", "0");
                                notifiyPreferenceApiRetrofit("likeuser", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbLikeMobile.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("likeuser", "1");
                                notifiyPreferenceApiRetrofit("likeuser", "1");
                            } else {
//                                notifiyPreferenceApi("likeuser", "0");
                                notifiyPreferenceApiRetrofit("likeuser", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbLikeEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("likeuser", "1");
                                notifiyPreferenceApiRetrofit("likeuseremail", "1");
                            } else {
//                                notifiyPreferenceApi("likeuser", "0");
                                notifiyPreferenceApiRetrofit("likeuseremail", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchInvite.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("invite", "1");
                                notifiyPreferenceApiRetrofit("invite", "1");
                            } else {
//                                notifiyPreferenceApi("invite", "0");
                                notifiyPreferenceApiRetrofit("invite", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbInviteMobile.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("invite", "1");
                                notifiyPreferenceApiRetrofit("invite", "1");
                            } else {
//                                notifiyPreferenceApi("invite", "0");
                                notifiyPreferenceApiRetrofit("invite", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbInviteEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("invite", "1");
                                notifiyPreferenceApiRetrofit("inviteemail", "1");
                            } else {
//                                notifiyPreferenceApi("invite", "0");
                                notifiyPreferenceApiRetrofit("inviteemail", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchBlock.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("block", "1");
                                notifiyPreferenceApiRetrofit("block", "1");
                            } else {
//                                notifiyPreferenceApi("block", "0");
                                notifiyPreferenceApiRetrofit("block", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbBlockMobile.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("block", "1");
                                notifiyPreferenceApiRetrofit("block", "1");
                            } else {
//                                notifiyPreferenceApi("block", "0");
                                notifiyPreferenceApiRetrofit("block", "0");
                            }
                        }
                    }
                });

        settingsBinding.cbBlockEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("block", "1");
                                notifiyPreferenceApiRetrofit("blockemail", "1");
                            } else {
//                                notifiyPreferenceApi("block", "0");
                                notifiyPreferenceApiRetrofit("blockemail", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchNotification.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("notificationoff", "1");
                                notifiyPreferenceApiRetrofit("notificationoff", "1");
                            } else {
//                                notifiyPreferenceApi("notificationoff", "0");
                                notifiyPreferenceApiRetrofit("notificationoff", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchEmail.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                notifiyPreferenceApi("notificationoff", "1");
                                notifiyPreferenceApiRetrofit("email", "1");
                            } else {
//                                notifiyPreferenceApi("notificationoff", "0");
                                notifiyPreferenceApiRetrofit("email", "0");
                            }
                        }
                    }
                });

        settingsBinding.switchSound.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
                                manageSession.setSoundString("1");
                            } else {
                                if (!manageSession.getProfileModel().getNotificationoff().equals("0"))
                                    manageSession.setSoundString("0");
                            }
                        }
                    }
                });

        settingsBinding.switchVibration.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
                                manageSession.setVibrationString("1");
                            } else {
                                if (!manageSession.getProfileModel().getNotificationoff().equals("0"))
                                    manageSession.setVibrationString("0");
                            }
                        }
                    }
                });
    }

    public void showDialogForDeActivate(final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please select a reason");
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, R.layout.list_item_deactivate, itemsDeActivate){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                return view;
            }

            @Override
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
//                TextView text1 = (TextView) view.findViewById(R.id.tv_reason);
//                text1.setTextColor(Color.RED);
                return view;
            }


        };
        builder.setAdapter(stringArrayAdapter,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int item){
                        String itemName=stringArrayAdapter.getItem(item);
                        editText.setText(itemsDeActivate[item]);
                    }
                });

        //list of items

//        builder.setSingleChoiceItems(itemsWeight, weightPosition,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // item selected logic
//
//                        etWeight.setText(itemsWeight[which]);
//                        weightPosition = which;
//                        dialog.dismiss();
//                    }
//                });


        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton("",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Settings", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        initToolBar();
    }

    private void setValues() {
        if (manageSession.getProfileModel().getHi() != null && manageSession.getProfileModel().getHi().equals("1"))
            settingsBinding.switchHi.setChecked(true);

        if (manageSession.getProfileModel().getCrushes() != null && manageSession.getProfileModel().getCrushes().equals("1"))
            settingsBinding.switchCrushes.setChecked(true);

        if (manageSession.getProfileModel().getMessage() != null && manageSession.getProfileModel().getMessage().equals("1"))
            settingsBinding.switchMessage.setChecked(true);

        if (manageSession.getProfileModel().getAutomatch() != null && manageSession.getProfileModel().getAutomatch().equals("1"))
            settingsBinding.switchAutomatch.setChecked(true);

        if (manageSession.getProfileModel().getFeedback() != null && manageSession.getProfileModel().getFeedback().equals("1"))
            settingsBinding.switchFeedback.setChecked(true);

        if (manageSession.getProfileModel().getFacebook() != null && manageSession.getProfileModel().getFacebook().equals("1"))
            settingsBinding.switchFacebook.setChecked(true);

        if (manageSession.getProfileModel().getInstagram() != null && manageSession.getProfileModel().getInstagram().equals("1"))
            settingsBinding.switchInstagram.setChecked(true);

        if (manageSession.getProfileModel().getTwitter() != null && manageSession.getProfileModel().getTwitter().equals("1"))
            settingsBinding.switchTwitter.setChecked(true);

        if (manageSession.getProfileModel().getDeactiveAccount() != null && manageSession.getProfileModel().getDeactiveAccount().equals("1"))
            settingsBinding.tvDeactivate.setText("Activate My Account");

        /////////////////////////

        if (manageSession.getProfileModel().getMatch() != null && manageSession.getProfileModel().getMatch().equals("1")) {
            settingsBinding.switchMatch.setChecked(true);
            settingsBinding.cbMatchMobile.setChecked(true);
        }

        if (manageSession.getProfileModel().getVisituser() != null && manageSession.getProfileModel().getVisituser().equals("1")) {
            settingsBinding.switchVisit.setChecked(true);
            settingsBinding.cbVisitMobile.setChecked(true);
        }

        if (manageSession.getProfileModel().getLikeuser() != null && manageSession.getProfileModel().getLikeuser().equals("1")) {
            settingsBinding.switchLike.setChecked(true);
            settingsBinding.cbLikeMobile.setChecked(true);
        }

        if (manageSession.getProfileModel().getInvite() != null && manageSession.getProfileModel().getInvite().equals("1")) {
            settingsBinding.switchInvite.setChecked(true);
            settingsBinding.cbInviteMobile.setChecked(true);
        }

        if (manageSession.getProfileModel().getBlock() != null && manageSession.getProfileModel().getBlock().equals("1")) {
            settingsBinding.switchBlock.setChecked(true);
            settingsBinding.cbBlockMobile.setChecked(true);
        }
//////
        if (manageSession.getProfileModel().getMatchEmail() != null && manageSession.getProfileModel().getMatchEmail().equals("1"))
            settingsBinding.cbMatchEmail.setChecked(true);

        if (manageSession.getProfileModel().getVisitUserEmail() != null && manageSession.getProfileModel().getVisitUserEmail().equals("1"))
            settingsBinding.cbVisitEmail.setChecked(true);

        if (manageSession.getProfileModel().getLikeUserEmail() != null && manageSession.getProfileModel().getLikeUserEmail().equals("1"))
            settingsBinding.cbLikeEmail.setChecked(true);

        if (manageSession.getProfileModel().getInviteEmail() != null && manageSession.getProfileModel().getInviteEmail().equals("1"))
            settingsBinding.cbInviteEmail.setChecked(true);

        if (manageSession.getProfileModel().getBlockEmail() != null && manageSession.getProfileModel().getBlockEmail().equals("1"))
            settingsBinding.cbBlockEmail.setChecked(true);
///////

        if (manageSession.getProfileModel().getNotificationoff() != null && manageSession.getProfileModel().getNotificationoff().equals("1")) {
            settingsBinding.switchNotification.setChecked(true);
        }

        if (manageSession.getProfileModel().getEmail() != null && manageSession.getProfileModel().getEmail().equals("1")) {
            settingsBinding.switchEmail.setChecked(true);
        }

        if (manageSession.getSoundString().equals("1"))
            settingsBinding.switchSound.setChecked(true);

        if (manageSession.getVibrationString().equals("1"))
            settingsBinding.switchVibration.setChecked(true);

        a++;
    }


    public void logoutApiRetrofit() {
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
        Call<ResponseBody> call = apiInterface.logoutApi(map);
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

                        OutLook.editStatus = false;
                        if (status.equals("1")) {
                            dialog.dismiss();
                            Snackbar.make(settingsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            manageSession.setProfileModel(null);
//                            dataBase.clearProfile();
                            manageSession.setLoginPreference("");
                            manageSession.setTotalCount(0);
                            manageSession.setMessageCount(0);
                            manageSession.setAutoMatched(0);
                            manageSession.setLikeCount(0);
                            manageSession.setMatched(0);
                            manageSession.setInviteCount(0);
                            manageSession.setBlockCount(0);
                            manageSession.setProfileCount(0);

                            Intent intent = new Intent(getActivity(), Login_SignupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            dialog.dismiss();
                            Snackbar.make(settingsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            manageSession.setProfileModel(null);
//                            dataBase.clearProfile();
                            manageSession.setLoginPreference("");
                            manageSession.setTotalCount(0);
                            manageSession.setTotalCount(0);
                            manageSession.setMessageCount(0);
                            manageSession.setAutoMatched(0);
                            manageSession.setLikeCount(0);
                            manageSession.setMatched(0);
                            manageSession.setInviteCount(0);
                            manageSession.setBlockCount(0);
                            manageSession.setProfileCount(0);
                            Intent intent = new Intent(getActivity(), Login_SignupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

//                                Snackbar.make(settingsBinding.parentLayout,message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(settingsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_logout:
                dialogBlock("Are you sure you want to logout?", "0", true);
                break;
            case R.id.ll_like_us:
//                Intent intent = new Intent(getActivity(), PayPalActivity.class);
//                startActivity(intent);

//                Intent intent = new Intent("notify");
//                intent.putExtra("type", "ACCEPT_APPOINTMENT");
//                context.sendBroadcast(intent);

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getActivity());
//                facebookIntent.setData(Uri.parse(facebookUrl));
                facebookIntent.setData(Uri.parse("https://www.facebook.com/veegleapp"));
                startActivity(facebookIntent);
                break;
            case R.id.ll_deactivate:
                if (manageSession.getProfileModel().getDeactiveAccount().equals("1")) {
                    dialogBlock("Are you sure you want to activate your account?", "0", false);
//                    notifiyPreferenceApi("deactiveaccount", "0");
                } else {
//                    dialogBlock("Are you sure you want to deactivate your account? \n Warning - your all data will be erase.", "1", false);

                    replaceFragmentWithBack(R.id.output, new DeactivateAccount(), "DeactivateAccount", "Settings");
                }
                break;
            case R.id.ll_terms:
                TermsAndCondition termsAndCondition = new TermsAndCondition();
//                replaceFragmentWithoutBack(R.id.output, termsAndCondition, "TermsAndCondition");
//                replaceFragmentWithBack(R.id.output, termsAndCondition, "TermsAndCondition", "Settings");
                addFragmentWithoutRemove(R.id.output, termsAndCondition, "TermsAndCondition");

//                getTermsAndCondition();
                break;
            case R.id.ll_about:
                addFragmentWithoutRemove(R.id.output, new AboutApp(), "AboutApp");
                break;
        }
    }

//    private BroadcastReceiver notify = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().matches("notify")) {
//
//                if (intent.getStringExtra("type").equals("ACCEPT_APPOINTMENT")) {
//                    Toast.makeText(context, "ACCEPT_APPOINTMENT", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        }
//    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        context.unregisterReceiver(notify);
    }


    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }


    public void notifiyPreferenceApiRetrofit(final String type, final String value) {
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
        map.put("type", type);
        map.put("value", value);

        if (enterREason != null && !enterREason.equals("")) {
            map.put("feedbackreason", education);
            map.put("anotherreason", enterREason);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.notifpreferenceApi(map);
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
                        String message = jsonObject.optString("data");

                        if (status.equals("true")) {
                            ProfileModel profileModel = manageSession.getProfileModel();
                            if (type.equals("hi"))
                                profileModel.setHi(value);
                            else if (type.equals("crushes"))
                                profileModel.setCrushes(value);
                            else if (type.equals("message"))
                                profileModel.setMessage(value);
                            else if (type.equals("feedback"))
                                profileModel.setFeedback(value);
                            else if (type.equals("automatch"))
                                profileModel.setAutomatch(value);
                            else if (type.equals("facebook"))
                                profileModel.setFacebook(value);
                            else if (type.equals("twitter"))
                                profileModel.setTwitter(value);
                            else if (type.equals("instagram"))
                                profileModel.setInstagram(value);
                            else if (type.equals("block"))
                                profileModel.setBlock(value);
                            else if (type.equals("visituser"))
                                profileModel.setVisituser(value);
                            else if (type.equals("invite"))
                                profileModel.setInvite(value);
                            else if (type.equals("likeuser"))
                                profileModel.setLikeuser(value);
                            else if (type.equals("visituseremail"))
                                profileModel.setVisitUserEmail(value);
                            else if (type.equals("matchemail"))
                                profileModel.setMatchEmail(value);
                            else if (type.equals("blockemail"))
                                profileModel.setBlockEmail(value);
                            else if (type.equals("inviteemail"))
                                profileModel.setInviteEmail(value);
                            else if (type.equals("likeuseremail"))
                                profileModel.setLikeUserEmail(value);
                            else if (type.equals("email"))
                                profileModel.setEmail(value);
                            else if (type.equals("notificationoff")) {
                                profileModel.setNotificationoff(value);
                            } else if (type.equals("match"))
                                profileModel.setMatch(value);
                            else if (type.equals("deactiveaccount")) {
                                dialog.dismiss();
                                profileModel.setDeactiveAccount(value);
                                if (value.equals("1"))
                                    settingsBinding.tvDeactivate.setText("Activate My Account");
                                else
                                    settingsBinding.tvDeactivate.setText("Deactivate My Account");

                                /*manageSession.setProfileModel(null);
                                manageSession.setLoginPreference("");
                                manageSession.setTotalCount(0);
                                manageSession.setAutoMatched(0);
                                manageSession.setLikeCount(0);
                                manageSession.setMessageCount(0);
                                manageSession.setMatched(0);
                                manageSession.setInviteCount(0);
                                manageSession.setBlockCount(0);
                                manageSession.setProfileCount(0);

                                manageSession.setFirstTimeLaunch(true);

                                Intent intent = new Intent(getActivity(), Login_SignupActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);*/
                            }


                            if (type.equals("notificationoff")) {
                                profileModel.setNotificationoff(value);

                                if (value.equals("1")) {
                                    if (manageSession.getSoundString().equals("1"))
                                        settingsBinding.switchSound.setChecked(true);
                                    else
                                        settingsBinding.switchSound.setChecked(false);

                                    if (manageSession.getVibrationString().equals("1"))
                                        settingsBinding.switchVibration.setChecked(true);
                                    else
                                        settingsBinding.switchVibration.setChecked(false);
                                } else {
                                    settingsBinding.switchVibration.setChecked(false);
                                    settingsBinding.switchSound.setChecked(false);
                                }
                            }

                            manageSession.setProfileModel(profileModel);

                        } else {
                            Snackbar.make(settingsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(settingsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }


    public void dialogBlock(String text, final String value, final boolean logoutStatus) {
        dialog = new Dialog(context);
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
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText(text);
        final EditText etReason = (EditText) dialog.findViewById(R.id.et_block_reason);
        final EditText etEducation = (EditText) dialog.findViewById(R.id.et_education);


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
        ImageView ivBlock = (ImageView) dialog.findViewById(R.id.iv_block);

        if (logoutStatus) {
            ivCancel.setVisibility(View.GONE);
            ivBlock.setVisibility(View.GONE);
            tvText.setPadding(0, 16, 0, 0);
        } else {
            if (value.equals("1")) {
                final RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_education);
                etEducation.setVisibility(View.VISIBLE);
                etReason.setVisibility(View.VISIBLE);

                final SearchableSpinner spEducation = (SearchableSpinner) dialog.findViewById(R.id.sp_education);

                relativeLayout.setVisibility(View.VISIBLE);

                spEducation.setTitle("Select Reason");
                spEducation.setPositiveButton("Close");

                educationList = new ArrayList<>();
                BlogDTO blogDTO = new BlogDTO();
                blogDTO.setName("Battery Draining");
                educationList.add(blogDTO);
                blogDTO = new BlogDTO();
                blogDTO.setName("Too Many Permissions");
                educationList.add(blogDTO);
                blogDTO = new BlogDTO();
                blogDTO.setName("Irrelevant Notifications");
                educationList.add(blogDTO);
                blogDTO = new BlogDTO();
                blogDTO.setName("Freezes/Hangs the Phone");
                educationList.add(blogDTO);
                blogDTO = new BlogDTO();
                blogDTO.setName("Others");
                educationList.add(blogDTO);

                spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerReason = educationList.get(position).getName();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        spEducation.onTouch(relativeLayout.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                        showDialogForDeActivate(etEducation);
                    }
                });

                etEducation.setFocusable(false);
//                etEducation.setEnabled(false);

                etEducation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogForDeActivate(etEducation);
                    }
                });

                ArrayAdapter<BlogDTO> oriAdapter = new ArrayAdapter<BlogDTO>(context,
                        R.layout.spinner_textview_one, educationList);
                spEducation.setAdapter(oriAdapter);

                spEducation.setSelection(0);
            }
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logoutStatus) {
//                    logoutApi();
                    logoutApiRetrofit();
                } else {
//                notifiyPreferenceApi("deactiveaccount", value);
                    if (value.equals("1")) {
                        try {
                            enterREason = etReason.getText().toString().trim();
                            education =  etEducation.getText().toString().trim();
                            if (education != null && !education.equals("")) {
                                if (enterREason != null && !enterREason.equals("")) {
                                    notifiyPreferenceApiRetrofit("deactiveaccount", value);
                                } else {
                                    dialogOK(context, "", "Please give another reason.", "Ok", false);
                                }
                            } else {
                                dialogOK(context, "", "Please select a reason.", "Ok", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        notifiyPreferenceApiRetrofit("deactiveaccount", value);
                    }
                }
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dialogOK(final Context context, String title, String message,
                         String btnText, final boolean isFinish) {
        // https://www.google.com/design/spec/components/dialogs.html#dialogs-specs
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage(Html.fromHtml(message));
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Nirmala.ttf");
//        alertDialogBuilder.setView(new TextView(context));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish)
                    ((Activity) context).finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}