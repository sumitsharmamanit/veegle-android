package com.datingapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.datingapp.HomeActivity;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.MyPreferenceBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPreference extends BaseFragment {

    private MyPreferenceBinding myPreferenceBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
//    private DataBase dataBase;
    private int a = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myPreferenceBinding = DataBindingUtil.inflate(inflater, R.layout.my_preference, container, false);
        return myPreferenceBinding.getRoot();
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

//        myPreferenceBinding.bubble.getConfigBuilder()
//                .min(0)
//                .max(50)
//                .progress(20)
//                .sectionCount(5)
//                .trackColor(ContextCompat.getColor(context, R.color.gray))
//                .secondTrackColor(ContextCompat.getColor(context, R.color.chat_text))
//                .thumbColor(ContextCompat.getColor(context, R.color.chat_text))
//                .showSectionText()
//                .sectionTextColor(ContextCompat.getColor(context, R.color.chat_text))
//                .sectionTextSize(18)
//                .showThumbText()
//                .alwaysShowBubble()
//                .thumbTextColor(ContextCompat.getColor(context, R.color.red))
//                .thumbTextSize(18)
//                .bubbleColor(ContextCompat.getColor(context, R.color.green))
//                .bubbleTextSize(18)
//                .showSectionMark()
//                .seekBySection()
//                .autoAdjustSectionMark()
//                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
//                .build();

//
//        myPreferenceBinding.parentLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                myPreferenceBinding.bubble.correctOffsetWhenContainerOnScrolling();
//            }
//        });
    }

    private void initView() {
        myPreferenceBinding.switchMen.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                preferenceApi("men", "1", "", "", "", "");
                                preferenceApiRetrofit("men", "1", "", "", "", "");
                            } else {
//                                preferenceApi("men", "0", "", "", "", "");
                                preferenceApiRetrofit("men", "0", "", "", "", "");
                            }
                        }
                    }
                });

        myPreferenceBinding.seekBarDistance.getConfigBuilder()
                .min(0)
                .max(1000)
                .sectionCount(20)
                .showSectionText()
                .sectionTextPosition(SignSeekBar.TextPosition.BOTTOM_SIDES)
                .trackColor(ContextCompat.getColor(context, R.color.hint_color))
                .secondTrackColor(ContextCompat.getColor(context, R.color.btn_color))
                .thumbColor(ContextCompat.getColor(context, R.color.btn_color))
                .sectionTextColor(ContextCompat.getColor(context, R.color.white))
                .sectionTextSize(10)
                .thumbTextColor(ContextCompat.getColor(context, R.color.white))
                .thumbTextSize(18)
                .thumbRadius(10)
                .signColor(ContextCompat.getColor(context, R.color.green))
                .signTextSize(18)
                .autoAdjustSectionMark()
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        myPreferenceBinding.switchWomen.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                preferenceApi("women", "1", "", "", "", "");
                                preferenceApiRetrofit("women", "1", "", "", "", "");
                            } else {
//                                preferenceApi("women", "0", "", "", "", "");
                                preferenceApiRetrofit("women", "0", "", "", "", "");
                            }
                        }
                    }
                });


        myPreferenceBinding.switchDistance.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
//                                preferenceApi("miles", "1", "", "", "", "");
                                preferenceApiRetrofit("miles", "1", "", "", "", "");
                            } else {
//                                preferenceApi("km", "0", "", "", "", "");
                                preferenceApiRetrofit("km", "0", "", "", "", "");
                            }
                        }
                    }
                });

        myPreferenceBinding.switchPause.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (a != 0) {
                            if (isChecked) {
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time =&gt; " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                System.out.println("Time here " + formattedDate);

                                c.add(Calendar.HOUR_OF_DAY, 8);
                                String formattedDate1 = df.format(c.getTime());
                                System.out.println("Updated Time here " + formattedDate1);

//                                preferenceApi("pauseeighthours", "1", "", "", formattedDate, formattedDate1);
                                preferenceApiRetrofit("pauseeighthours", "1", "", "", formattedDate, formattedDate1);
                            } else {
//                                preferenceApi("pauseeighthours", "0", "", "", "", "");
                                preferenceApiRetrofit("pauseeighthours", "0", "", "", "", "");
                            }
                        }
                    }
                });

        if (manageSession.getProfileModel().getFromage() != null && !manageSession.getProfileModel().getFromage().equals("") && manageSession.getProfileModel().getToage() != null && !manageSession.getProfileModel().getToage().equals("")) {
            try {
                myPreferenceBinding.ageSeekBar.setValue(Float.parseFloat(manageSession.getProfileModel().getFromage()), Float.parseFloat(manageSession.getProfileModel().getToage()));
                myPreferenceBinding.tvLeft.setText(manageSession.getProfileModel().getFromage());
                myPreferenceBinding.tvRight.setText(manageSession.getProfileModel().getToage());
            } catch (Exception e) {
                myPreferenceBinding.ageSeekBar.setValue(18, 70);
            }
        } else {
            myPreferenceBinding.ageSeekBar.setValue(18, 70);
        }

        myPreferenceBinding.ageSeekBar.setIndicatorTextDecimalFormat("0");
        myPreferenceBinding.distanceSeekBar.setIndicatorTextDecimalFormat("0");
        myPreferenceBinding.ageSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                myPreferenceBinding.tvLeft.setText(((int) leftValue) + "");
                myPreferenceBinding.tvRight.setText(((int) rightValue) + "");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
//                preferenceApi("age", "", myPreferenceBinding.tvLeft.getText().toString(), myPreferenceBinding.tvRight.getText().toString(), "", "");
                preferenceApiRetrofit("age", "", myPreferenceBinding.tvLeft.getText().toString(), myPreferenceBinding.tvRight.getText().toString(), "", "");

            }
        });

        if (manageSession.getProfileModel().getTodistance() != null && !manageSession.getProfileModel().getTodistance().equals("")) {
            myPreferenceBinding.distanceSeekBar.setValue(Float.parseFloat(manageSession.getProfileModel().getTodistance()));
            myPreferenceBinding.seekBarDistance.setProgress(Float.parseFloat(manageSession.getProfileModel().getTodistance()));
            myPreferenceBinding.tvDistance.setText(manageSession.getProfileModel().getTodistance());
            myPreferenceBinding.tvMiles.setText(" (" + new DecimalFormat(".##").format(convertKmsToMiles(Float.parseFloat(manageSession.getProfileModel().getTodistance()))) + " miles)");
        } else {
            myPreferenceBinding.distanceSeekBar.setValue(50);
            myPreferenceBinding.seekBarDistance.setProgress(50);
            myPreferenceBinding.tvMiles.setText(" (" + new DecimalFormat(".##").format(50) + " miles)");
        }

        myPreferenceBinding.distanceSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                myPreferenceBinding.tvDistance.setText(((int) leftValue) + "");

                myPreferenceBinding.tvMiles.setText(" (" + new DecimalFormat(".##").format(convertKmsToMiles(leftValue)) + " miles)");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
//                preferenceApi("todistance", myPreferenceBinding.tvDistance.getText().toString(), "", "", "", "");

                preferenceApiRetrofit("todistance", myPreferenceBinding.tvDistance.getText().toString(), "", "", "", "");
            }
        });





        myPreferenceBinding.seekBarDistance.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                myPreferenceBinding.tvDistance.setText(((int) progress) + "");

                myPreferenceBinding.tvMiles.setText(" (" + new DecimalFormat(".##").format(convertKmsToMiles(progress)) + " miles)");

                preferenceApiRetrofit("todistance", progress+"", "", "", "", "");
            }
        });
    }

    public float convertKmsToMiles(float kms) {
        float miles = (float) (0.621371 * kms);
        return miles;
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("My Preference", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setValues() {
        if (manageSession.getProfileModel().getMen() != null && manageSession.getProfileModel().getMen().equals("1"))
            myPreferenceBinding.switchMen.setChecked(true);

        if (manageSession.getProfileModel().getWomen() != null && manageSession.getProfileModel().getWomen().equals("1"))
            myPreferenceBinding.switchWomen.setChecked(true);

        if (manageSession.getProfileModel().getPauseeighthours() != null && manageSession.getProfileModel().getPauseeighthours().equals("1"))
            myPreferenceBinding.switchPause.setChecked(true);

        if (manageSession.getProfileModel().getKmormiles() != null && manageSession.getProfileModel().getKmormiles().equals("1"))
            myPreferenceBinding.switchDistance.setChecked(true);

        a++;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
    }

    public void preferenceApiRetrofit(final String type, final String value, String fromAge, String toAge, String startTime, String endTime) {
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
        map.put("fromage", fromAge);
        map.put("toage", toAge);
        map.put("pausestarttime", startTime);
        map.put("pauseendtime", endTime);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.preferenceApi(map);
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
                            if (type.equals("men"))
                                profileModel.setMen(value);
                            else if (type.equals("women"))
                                profileModel.setWomen(value);
                            else if (type.equals("pauseeighthours"))
                                profileModel.setPauseeighthours(value);
                            else if (type.equals("age")) {
                                profileModel.setFromage(myPreferenceBinding.tvLeft.getText().toString());
                                profileModel.setToage(myPreferenceBinding.tvRight.getText().toString());
                            } else if (type.equals("todistance")) {
                                profileModel.setTodistance(myPreferenceBinding.tvDistance.getText().toString());
                            } else if (type.equals("miles")) {
                                profileModel.setKmormiles("1");
                            } else if (type.equals("km")) {
                                profileModel.setKmormiles("0");
                            }

                            manageSession.setProfileModel(profileModel);

//                                Snackbar.make(myPreferenceBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(myPreferenceBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(myPreferenceBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}