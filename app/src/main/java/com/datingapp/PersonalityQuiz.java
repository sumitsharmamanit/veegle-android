package com.datingapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.Model.BlogDTO;
import com.datingapp.adapter.BlogAdapter;
import com.datingapp.adapter.QuestionsAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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

public class PersonalityQuiz extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private RelativeLayout parentLayout;
    private TextView tvYes, tvNo, tvQuestion;
    private ImageView ivQuiz, ivBack;
    private StateProgressBar stateProgressBar;
    private ArrayList<BlogDTO> blogDTOArrayList;
    private ManageSession manageSession;
    private String questionId = "", questionAnswer = "", url = "", answerUrl = "", againUrl = "";
    private int qStatus = 0;
    private LinearLayout llBack, llQuiz, llHalf, llButton;
    private boolean partner = false;
    private RecyclerView rlPersonality;
    private LinearLayoutManager linearLayoutManager;
    private InterstitialAd interstitialAd; // 12


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_quiz);

        if (getIntent().hasExtra(Constant.PARTNER_PREFERENCE)) {
//            Toast.makeText(this, "Partner", Toast.LENGTH_SHORT).show();
            url = Constant.PARTNER_PREFERENCE;
            answerUrl = Constant.ADD_PARTNER_PREFERENCE_QUESTION;
            againUrl = Constant.PARTNER_PREFERENCE_REPEAT;
            partner = true;
        } else {
            url = Constant.SHOW_QUESTION_LIST;
            answerUrl = Constant.ADD_QUESTION_ANSWER;
            againUrl = Constant.PERSONALITY_QUIZ_REPEAT;
            partner = false;
        }

        initView();
        new OutLook().hideKeyboard(PersonalityQuiz.this);

        new OutLook().hideKeyboard(this);

        interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));


//        getQuestions();
        getQuestionsApi();
    }

    private void initView() {
        manageSession = new ManageSession(this);
        blogDTOArrayList = new ArrayList<>();
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        tvYes = (TextView) findViewById(R.id.tv_yes);
        tvNo = (TextView) findViewById(R.id.tv_no);
        tvQuestion = (TextView) findViewById(R.id.tv_question);
        ivQuiz = (ImageView) findViewById(R.id.iv_quiz);
        ivBack = (ImageView) findViewById(R.id.imgBack);
        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);

        stateProgressBar = (StateProgressBar) findViewById(R.id.state_progress_bar);
        stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FIVE);
        stateProgressBar.enableAnimationToCurrentState(true);
        stateProgressBar.checkStateCompleted(true);

        llButton = (LinearLayout) findViewById(R.id.ll_button);
        llQuiz = (LinearLayout) findViewById(R.id.ll_quiz);
        llHalf = (LinearLayout) findViewById(R.id.ll_half);

        rlPersonality = (RecyclerView) findViewById(R.id.rl_personality);
        linearLayoutManager = new LinearLayoutManager(PersonalityQuiz.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlPersonality.setLayoutManager(linearLayoutManager);
    }

    public void showInterstitialAds() {
        AdRequest adRequest1 = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        interstitialAd.loadAd(adRequest1);

        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    private void showInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }


    public void getQuestionsApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(PersonalityQuiz.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(PersonalityQuiz.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ResponseBody> call;
        if (partner) {
            call = apiInterface.partnerListApi(map);
        } else {
            call = apiInterface.questionListApi(map);
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
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        showInterstitialAds();

                        if (status.equals("1")) {
                            blogDTOArrayList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<BlogDTO>>() {
                            }.getType());

                            if (blogDTOArrayList.size() > 0) {
                                tvQuestion.setText(blogDTOArrayList.get(0).getQuestion());
                                Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(0).getQuesimage()).fit().into(ivQuiz);

                                qStatus++;
                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                            }
                        } else {
//                                tvYes.setVisibility(View.GONE);
//                                tvNo.setVisibility(View.GONE);
//                                Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();

//                            getQuestionsAgain();

                            getQuestionsAgainApi();
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


    public void getQuestionsAgainApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(PersonalityQuiz.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(PersonalityQuiz.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ResponseBody> call;
        if (partner) {
            call = apiInterface.partneranswerDetailsApi(map);
        } else {
            call = apiInterface.answerDetailsquestionIdApi(map);
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
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if (status.equals("1")) {
                            blogDTOArrayList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<BlogDTO>>() {
                            }.getType());

                            if (blogDTOArrayList.size() > 0) {
                                   /* tvQuestion.setText(blogDTOArrayList.get(0).getQuestion());
                                    Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(0).getQuesimage()).fit().into(ivQuiz);

                                    if (blogDTOArrayList.get(0).getAnswer() != null && !blogDTOArrayList.get(0).getAnswer().equals("")){
                                        if (blogDTOArrayList.get(0).getAnswer().equalsIgnoreCase("no")){
                                            tvNo.setBackgroundColor(getResources().getColor(R.color.green));
                                            tvYes.setBackgroundColor(getResources().getColor(R.color.white));
                                        }else if (blogDTOArrayList.get(0).getAnswer().equalsIgnoreCase("yes")) {
                                            tvYes.setBackgroundColor(getResources().getColor(R.color.green));
                                            tvNo.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
                                    }
                                    qStatus++;
                                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);*/

                                llButton.setVisibility(View.GONE);
                                llQuiz.setVisibility(View.GONE);
                                llHalf.setVisibility(View.GONE);
                                stateProgressBar.setVisibility(View.GONE);
                                tvQuestion.setVisibility(View.GONE);
                                rlPersonality.setVisibility(View.VISIBLE);
                                rlPersonality.setAdapter(new QuestionsAdapter(PersonalityQuiz.this, blogDTOArrayList, onItemClickListener, true));
                            }
                        } else {
                            tvYes.setVisibility(View.GONE);
                            tvNo.setVisibility(View.GONE);
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

    OnItemClickListener.OnItemClickCallback onItemClickListener = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {

        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_yes:
                try {
                    if (qStatus <= blogDTOArrayList.size()) {
                        if (blogDTOArrayList.get((qStatus - 1)) != null) {
                            questionId = blogDTOArrayList.get((qStatus - 1)).getQuestionId();
                            questionAnswer = "yes";

                            Log.i(getClass().getName(), "qStatus =>" + qStatus);
                            Log.i(getClass().getName(), "questionId =>" + questionId);
                            Log.i(getClass().getName(), "qStatus =>" + questionAnswer);


//                    putAnswer();
                            putAnswerRetrofit();
                        }
                    } else {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    onBackPressed();
                    e.printStackTrace();
                }
                break;
            case R.id.tv_no:
                try {
                    if (qStatus <= blogDTOArrayList.size()) {
                        if (blogDTOArrayList.get((qStatus - 1)) != null) {
                            questionId = blogDTOArrayList.get((qStatus - 1)).getQuestionId();
                            questionAnswer = "no";

//                    putAnswer();
                            putAnswerRetrofit();
                        }
                    } else {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    onBackPressed();
                    e.printStackTrace();
                }
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }


    public void putAnswerRetrofit() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(PersonalityQuiz.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(PersonalityQuiz.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call;
        if (partner) {
            call = apiInterface.putAnswerPartner(Constant.APPTOKEN, manageSession.getProfileModel().getUser_id(), questionId, questionAnswer);
        } else {
            call = apiInterface.putAnswer(Constant.APPTOKEN, manageSession.getProfileModel().getUser_id(), questionId, questionAnswer);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                    progressDialog.dismiss();
                    try {
                        jsonObject = new JSONObject(str);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("1")) {
                            manageSession.setPerQuestion(jsonObject.optInt("personality_quiz_question"));
                            manageSession.setPerAnswer(jsonObject.optInt("personality_quiz_answer"));
                            manageSession.setPartnerQuestion(jsonObject.optInt("partner_question"));
                            manageSession.setPartnerAnswer(jsonObject.optInt("partner_answer"));

                            if (qStatus <= blogDTOArrayList.size()) {
                                qStatus++;


//                    stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FIVE);
                                if (qStatus == 2) {
                                    try {
                                        tvQuestion.setText(blogDTOArrayList.get(1).getQuestion());
                                        Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(1).getQuesimage()).fit().into(ivQuiz);
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

                                        if (blogDTOArrayList.get(1).getAnswer() != null && !blogDTOArrayList.get(1).getAnswer().equals("")) {
                                            if (blogDTOArrayList.get(1).getAnswer().equalsIgnoreCase("no")) {
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.white));
                                            } else if (blogDTOArrayList.get(1).getAnswer().equalsIgnoreCase("yes")) {
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.white));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onBackPressed();
                                    }
                                } else if (qStatus == 3) {
                                    try {
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                                        tvQuestion.setText(blogDTOArrayList.get(2).getQuestion());
                                        Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(2).getQuesimage()).fit().into(ivQuiz);


                                        if (blogDTOArrayList.get(2).getAnswer() != null && !blogDTOArrayList.get(2).getAnswer().equals("")) {
                                            if (blogDTOArrayList.get(2).getAnswer().equalsIgnoreCase("no")) {
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.white));
                                            } else if (blogDTOArrayList.get(2).getAnswer().equalsIgnoreCase("yes")) {
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.white));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onBackPressed();
                                    }
                                } else if (qStatus == 4) {
                                    try {
                                        tvQuestion.setText(blogDTOArrayList.get(3).getQuestion());
                                        Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(3).getQuesimage()).fit().into(ivQuiz);
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);

                                        if (blogDTOArrayList.get(3).getAnswer() != null && !blogDTOArrayList.get(3).getAnswer().equals("")) {
                                            if (blogDTOArrayList.get(3).getAnswer().equalsIgnoreCase("no")) {
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.white));
                                            } else if (blogDTOArrayList.get(3).getAnswer().equalsIgnoreCase("yes")) {
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.white));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onBackPressed();
                                    }
                                } else if (qStatus == 5) {
                                    try {
                                        tvQuestion.setText(blogDTOArrayList.get(4).getQuestion());
                                        Picasso.with(PersonalityQuiz.this).load(blogDTOArrayList.get(4).getQuesimage()).fit().into(ivQuiz);
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                                        stateProgressBar.setAllStatesCompleted(true);


                                        if (blogDTOArrayList.get(4).getAnswer() != null && !blogDTOArrayList.get(4).getAnswer().equals("")) {
                                            if (blogDTOArrayList.get(4).getAnswer().equalsIgnoreCase("no")) {
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.white));
                                            } else if (blogDTOArrayList.get(4).getAnswer().equalsIgnoreCase("yes")) {
                                                tvYes.setBackgroundColor(getResources().getColor(R.color.green));
                                                tvNo.setBackgroundColor(getResources().getColor(R.color.white));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onBackPressed();
                                    }
                                } else {
                                    onBackPressed();
                                }
                            }
                        } else {
                            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }


}
