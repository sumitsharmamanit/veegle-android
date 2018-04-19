package com.datingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileThirdActivity extends AppCompatActivity implements View.OnClickListener {

    private ProfileModel profileModel;
    ProgressDialog progressDialog;
    private ScrollView parentLayout;
    private Button btnSave;
    private EditText etHeight, etWeight, etAge;
    private LinearLayout llEthnicity, llOrientation, llBodyType, llPersonality, llHeight, llWeight, llPartner;
    private TextView tvEthnicity, tvOrientation, tvBodyType, tvPerQuestion, tvPartnerQuestion;
    private String height = "", weight = "", ethnicity = "", bodyType = "", orientation = "", age = "";
    private String[] ethnicityArray = {""}, orientationArray = {""}, itemsHeight = {"<4'7\" (<140cm)", "4'8\" (142cm)", "4'9\" (145cm)", "4'10\" (147cm)", "4'11\" (150cm)", "5' (153cm)", "5'1\" (155cm)", "5'2\" (158cm)", "5'3\" (160cm)", "5'4\" (163cm)", "5'5\" (165cm)", "5'6\" (168cm)", "5'7\" (170cm)", "5'8\" (173cm)", "5'9\" (175cm)", "5'10\" (178cm)", "5'11\" (180cm)", "6' (183cm)", "6'1\" (186cm)", "6'2\" (188cm)", "6'3\" (191cm)", "6'4\" (193cm)", ">6'4\" (>193cm)"}
    , itemsWeight = {"<86 lbs (<39kg)", "88 lbs (40kg)", "90 lbs (41kg)", "93 lbs (42kg)", "95 lbs (43kg)", "97 lbs (44kg)", "99 lbs (45kg)", "101 lbs (46kg)", "104 lbs (47kg)", "106 lbs (48kg)", "108 lbs (49kg)", "110 lbs (50kg)", "112 lbs (51kg)", "114 lbs (52kg)", "116 lbs (53kg)", "118 lbs (54kg)", "120 lbs (55kg)", "122 lbs (56kg)", "124 lbs (57kg)", "126 lbs (58kg)", "128 lbs (59kg)", "130 lbs (60kg)", "132 lbs (61kg)", "134 lbs (62kg)", "136 lbs (63kg)", "138 lbs (64kg)", "140 lbs (65kg)", "142 lbs (66kg)", "144 lbs (67kg)", "146 lbs (68kg)", "148 lbs (69kg)", "150 lbs (70kg)", "152 lbs (71kg)", "154 lbs (72kg)", "156 lbs (73kg)", "158 lbs (74kg)", "160 lbs (75kg)", "162 lbs (76kg)", "164 lbs (77kg)", "168 lbs (78kg)", "170 lbs (79kg)", "172 lbs (80kg)", "174 lbs (81kg)", "174 lbs (82kg)", "176 lbs (83kg)", "178 lbs (84kg)", "180 lbs (85kg)", "182 lbs (86kg)", "184 lbs (87kg)", "186 lbs (88kg)", "188 lbs (89kg)", "190 lbs (90kg)", "192 lbs (91kg)", "194 lbs (92kg)", "196 lbs (93kg)", "198 lbs (94kg)", "200 lbs (95kg)", "202 lbs (96kg)", "204 lbs (97kg)", "206 lbs (98kg)", "208 lbs (99kg)", "210 lbs (100kg)", "212 lbs (101kg)", "214 lbs (102kg)", "216 lbs (103kg)", "218 lbs (104kg)", "220 lbs (105kg)", "222 lbs (106kg)", "224 lbs (107kg)", "226 lbs (108kg)", "228 lbs (109kg)", "230 lbs (110kg)", "232 lbs (111kg)", "234 lbs (112kg)", "236 lbs (113kg)", "238 lbs (114kg)", "240 lbs (115kg)", "242 lbs (116kg)", "244 lbs (117kg)", "246 lbs (118kg)", "248 lbs (119kg)", "250 lbs (120kg)", ">250 lbs (>120kg)"};
//    private DataBase dataBase;
    private ManageSession manageSession;
    private boolean editStatus = false;
    private LinearLayout llBack;
    private SearchableSpinner spEthnicity, spOrientation, spBodyType;
    private ArrayList<String> ethnicityList, orientationList;
    JSONArray jsonArray, jsonArray1;
    private int ethPosition = 0, orientationPosition = 0, bodyPosition = 0, position55 = -1, weightPosition = -1;
    private InterstitialAd interstitialAd; // 12

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_edit_profile);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (intent.hasExtra("ethnicity")){
            try {
                jsonArray = new JSONArray(intent.getStringExtra("ethnicity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (intent.hasExtra("orientation")){
            try {
                jsonArray1 = new JSONArray(intent.getStringExtra("orientation"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        profileModel = (ProfileModel) bundle.getSerializable("ProfileModel");

        initView();

//        getEthnicity();


        new OutLook().hideKeyboard(EditProfileThirdActivity.this);

        interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        showInterstitialAds();
    }

    public void showInterstitialAds(){
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

    private void setValues() {
        etHeight.setText(manageSession.getProfileModel().getHeight());
        etHeight.setSelection(etHeight.getText().length());
        etWeight.setText(manageSession.getProfileModel().getWeight());
        etWeight.setSelection(etWeight.getText().length());
        etAge.setText(manageSession.getProfileModel().getAge());
        etAge.setSelection(etAge.getText().length());
        tvBodyType.setText(manageSession.getProfileModel().getBodytype());
        tvEthnicity.setText(manageSession.getProfileModel().getEthnicity());
        tvOrientation.setText(manageSession.getProfileModel().getOreintation());

//        bodyType = manageSession.getProfileModel().getBodytype();
//        ethnicity = manageSession.getProfileModel().getEthnicity();
//        orientation = manageSession.getProfileModel().getOreintation();


        if (editStatus){
//            for (String value : items) {
//                if (manageSession.getProfileModel().getHeight().equals(value)){
            position55 = Arrays.asList(itemsHeight).indexOf(manageSession.getProfileModel().getHeight());
//                }
//            }
        }


        if (editStatus){
//            for (String value : items) {
//                if (manageSession.getProfileModel().getHeight().equals(value)){
            weightPosition = Arrays.asList(itemsWeight).indexOf(manageSession.getProfileModel().getWeight());
//                }
//            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvPerQuestion.setText("("+manageSession.getPerAnswer() +"/"+ manageSession.getPerQuestion()+")");
        tvPartnerQuestion.setText("("+manageSession.getPartnerAnswer() +"/"+ manageSession.getPartnerQuestion()+")");
    }

    private void initView() {
        ethnicityList = new ArrayList<>();
        orientationList = new ArrayList<>();
//        dataBase = new DataBase(this);
        manageSession = new ManageSession(this);
        parentLayout = (ScrollView) findViewById(R.id.parentLayout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        llEthnicity = (LinearLayout) findViewById(R.id.ll_ethnicity);
        llOrientation = (LinearLayout) findViewById(R.id.ll_orientation);
        llBodyType = (LinearLayout) findViewById(R.id.ll_body_type);

        llWeight = (LinearLayout) findViewById(R.id.ll_weight);
        llHeight = (LinearLayout) findViewById(R.id.ll_height);
        llPartner = (LinearLayout) findViewById(R.id.ll_partner);
        llPartner.setOnClickListener(this);
        llWeight.setOnClickListener(this);
        llHeight.setOnClickListener(this);

        llEthnicity.setOnClickListener(this);
        llOrientation.setOnClickListener(this);
        llBodyType.setOnClickListener(this);

        tvEthnicity = (TextView) findViewById(R.id.tv_ethnicity);
        tvOrientation = (TextView) findViewById(R.id.tv_orientation);
        tvBodyType = (TextView) findViewById(R.id.tv_body_type);

        etHeight = (EditText) findViewById(R.id.et_height);
        etWeight = (EditText) findViewById(R.id.et_weight);
        etAge = (EditText) findViewById(R.id.et_age);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llPersonality = (LinearLayout) findViewById(R.id.ll_personality);
        llPersonality.setOnClickListener(this);
        llBack.setOnClickListener(this);
        etHeight.setOnClickListener(this);
        etWeight.setOnClickListener(this);

        spEthnicity = (SearchableSpinner) findViewById(R.id.sp_ethnicity);
        spEthnicity.setTitle("Select Ethnicity");
        spEthnicity.setPositiveButton("Close");

        spOrientation = (SearchableSpinner) findViewById(R.id.sp_orientation);
        spOrientation.setTitle("Select Orientation");
        spOrientation.setPositiveButton("Close");

        spBodyType = (SearchableSpinner) findViewById(R.id.sp_body_type);
        spBodyType.setTitle("Select BodyType");
        spBodyType.setPositiveButton("Close");

        tvPerQuestion = (TextView) findViewById(R.id.tv_personality_count);
        tvPartnerQuestion = (TextView) findViewById(R.id.tv_partner_count);

        if (manageSession.getProfileModel() != null && !manageSession.getProfileModel().getEthnicity().equals("")){
            editStatus = true;
            setValues();
        }

        final String[] value = {"Slim", "Atheletic", "Average", "Muscular", "A few extra pounds", "Big and bold"};
        ArrayAdapter<String> oriAdapter = new ArrayAdapter<String>(EditProfileThirdActivity.this,
                R.layout.spinner_textview, value);
        spBodyType.setAdapter(oriAdapter);

        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < value.length; i++) {

                if (manageSession.getProfileModel().getBodytype().equalsIgnoreCase(value[i])) {
                    spBodyType.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555"+value[i]);
                    break;
                }
            }
        } else {
            spBodyType.setSelection(bodyPosition);
        }

        spEthnicity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ethnicity = ethnicityList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spOrientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orientation = orientationList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spBodyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bodyType = value[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setValuesOnSpinner();

    }

    private void setValuesOnSpinner() {
        ethnicityArray = new String[jsonArray.length()];
        orientationArray = new String[jsonArray1.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = (JSONObject) jsonArray.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ethnicityArray[i] = jsonObject1.optString("ethnicity");
            ethnicityList.add(jsonObject1.optString("ethnicity"));

            Log.i(getClass().getName(), "Ethnicity >>>"+jsonObject1.optString("ethnicity"));
        }

        for (int i = 0; i < jsonArray1.length(); i++) {
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = (JSONObject) jsonArray1.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orientationArray[i] = jsonObject1.optString("Orientation");
            orientationList.add(jsonObject1.optString("Orientation"));
            Log.i(getClass().getName(), "Orientation >>>"+jsonObject1.optString("Orientation"));
        }


        ArrayAdapter<String> ethniAdapter = new ArrayAdapter<String>(EditProfileThirdActivity.this,
                R.layout.spinner_textview, ethnicityList);
        spEthnicity.setAdapter(ethniAdapter);

        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < ethnicityList.size(); i++) {
                if (manageSession.getProfileModel().getEthnicity().equalsIgnoreCase(ethnicityList.get(i))) {
                    spEthnicity.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555"+ethnicityList.get(i));
                    break;
                }
            }
        } else {
            spEthnicity.setSelection(ethPosition);
        }

        ArrayAdapter<String> oriAdapter = new ArrayAdapter<String>(EditProfileThirdActivity.this,
                R.layout.spinner_textview, orientationList);
        spOrientation.setAdapter(oriAdapter);

        if (editStatus) {
            Log.i(getClass().getName(), "?????? 1111");
            for (int i = 0; i < orientationList.size(); i++) {

                if (manageSession.getProfileModel().getOreintation().equalsIgnoreCase(orientationList.get(i))) {
                    spOrientation.setSelection(i);
                    Log.i(getClass().getName(), "??????  44444444444 55555555"+orientationList.get(i));
                    break;
                }
            }
        } else {
            spOrientation.setSelection(orientationPosition);
        }

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileThirdActivity.this);
        builder.setTitle("Select Height");

        //list of items

        builder.setSingleChoiceItems(itemsHeight, position55,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item selected logic

                        etHeight.setText(itemsHeight[which]);
                        position55 = which;
                        dialog.dismiss();
                    }
                });

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

    public void showDialogForWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileThirdActivity.this);
        builder.setTitle("Select Weight");

        //list of items

        builder.setSingleChoiceItems(itemsWeight, weightPosition,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item selected logic

                        etWeight.setText(itemsWeight[which]);
                        weightPosition = which;
                        dialog.dismiss();
                    }
                });


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




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_ethnicity:
//                OnTaskCompleted onTaskCompleted1 = new OnTaskCompleted() {
//                    @Override
//                    public void onTaskCompleted(String response) {
//                        ethnicity = response;
//                        tvEthnicity.setText(response);
//                    }
//                };
//
//                ethnicity = Constant.GetArrayPicker(EditProfileThirdActivity.this, ethnicityArray, onTaskCompleted1);
//                tvEthnicity.setText(ethnicity);

//                spEthnicity.performClick();

                spEthnicity.onTouch(llEthnicity.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
            case R.id.ll_orientation:
//                OnTaskCompleted onTaskCompleted2 = new OnTaskCompleted() {
//                    @Override
//                    public void onTaskCompleted(String response) {
//                        orientation = response;
//                        tvOrientation.setText(response);
//                    }
//                };
//
//                orientation = Constant.GetArrayPicker(EditProfileThirdActivity.this, orientationArray, onTaskCompleted2);
//                tvOrientation.setText(orientation);

                spOrientation.onTouch(llOrientation.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
            case R.id.ll_body_type:
//                OnTaskCompleted onTaskCompleted = new OnTaskCompleted() {
//                    @Override
//                    public void onTaskCompleted(String response) {
//                        bodyType = response;
//                        tvBodyType.setText(response);
//                    }
//                };
//
//                final String[] value = {"Slim", "Atheletic", "Average", "Muscular", "A few extra pounds", "Big and bold"};
//                bodyType = Constant.GetArrayPicker(EditProfileThirdActivity.this, value, onTaskCompleted);
//                tvBodyType.setText(bodyType);

                spBodyType.onTouch(llBodyType.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
            case R.id.btnSave:
                new OutLook().hideKeyboard(this);
                height = etHeight.getText().toString();
                weight = etWeight.getText().toString();
                age = etAge.getText().toString();

                if (isValid()){
                    ethPosition = spEthnicity.getSelectedItemPosition();
                    orientationPosition = spOrientation.getSelectedItemPosition();
                    bodyPosition = spBodyType.getSelectedItemPosition();
//                    updateUserProfile();

                    updateUserProfileApi();
                }
                break;
            case R.id.ll_personality:
                startActivity(new Intent(this, PersonalityQuiz.class));
                break;
            case R.id.ll_partner:
                Intent intent = new Intent(EditProfileThirdActivity.this, PersonalityQuiz.class);
                intent.putExtra(Constant.PARTNER_PREFERENCE, Constant.PARTNER_PREFERENCE);
                startActivity(intent);
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_height:
//                etHeight.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(etHeight, InputMethodManager.SHOW_IMPLICIT);

                showDialog();
                break;
            case R.id.ll_weight:
//                etWeight.requestFocus();
//                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm1.showSoftInput(etWeight, InputMethodManager.SHOW_IMPLICIT);

                showDialogForWeight();
                break;
            case R.id.et_height:
                showDialog();
                break;
            case R.id.et_weight:
                showDialogForWeight();
                break;
        }
    }

    private boolean isValid() {
        if (height == null || height.equals("")){
            Snackbar.make(parentLayout, "Please select your height.", Snackbar.LENGTH_SHORT).show();
            etHeight.requestFocus();
            return false;
        } else if (weight == null || weight.equals("")){
            Snackbar.make(parentLayout, "Please select your weight.", Snackbar.LENGTH_SHORT).show();
            etWeight.requestFocus();
            return false;
//        } else if (age == null || age.equals("")){
//            Snackbar.make(parentLayout, "Please enter your age.", Snackbar.LENGTH_SHORT).show();
//            etAge.requestFocus();
//            return false;
        } else if (bodyType == null || bodyType.equals("")){
            Snackbar.make(parentLayout, "Please select body type.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if (ethnicity == null || ethnicity.equals("")){
            Snackbar.make(parentLayout, "Please select ethnicity.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if (orientation == null || orientation.equals("")){
            Snackbar.make(parentLayout, "Please select orientation.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void updateUserProfileApi() {
        Log.i(getClass().getName(), ">>>>"+Constant.APPTOKEN);
        Log.i(getClass().getName(), ">>>>"+manageSession.getProfileModel().getUser_id());
        Log.i(getClass().getName(), ">>>>"+profileModel.getUserfullname());
        Log.i(getClass().getName(), ">>>>"+profileModel.getProfileImage());

        Log.i(getClass().getName(), ">>>>"+profileModel.getUseremail());
        Log.i(getClass().getName(), ">>>>"+ manageSession.getProfileModel().getUseremail());

        Log.i(getClass().getName(), ">>>>"+ manageSession.getProfileModel().getProfileImage());


        Log.i(getClass().getName(), ">>>>"+profileModel.getDob());
        Log.i(getClass().getName(), ">>>>"+profileModel.getWork());
        Log.i(getClass().getName(), ">>>>"+profileModel.getInterest());

        Log.i(getClass().getName(), ">>>>"+profileModel.getEducation());
        Log.i(getClass().getName(), ">>>>"+profileModel.getHobbies());
        Log.i(getClass().getName(), ">>>>"+profileModel.getGeneralroutine());
        Log.i(getClass().getName(), ">>>>"+profileModel.getWeekendroutine());

        Log.i(getClass().getName(), ">>>>"+profileModel.getMaternitystatus());
        Log.i(getClass().getName(), ">>>>"+profileModel.getChildren());
        Log.i(getClass().getName(), ">>>>"+profileModel.getLookingfor());
        Log.i(getClass().getName(), ">>>>"+profileModel.getGender());

        Log.i(getClass().getName(), ">>>>"+ethnicity);
        Log.i(getClass().getName(), ">>>>"+orientation);
        Log.i(getClass().getName(), ">>>>"+age);
        Log.i(getClass().getName(), ">>>>"+weight);
        Log.i(getClass().getName(), ">>>>"+height);
        Log.i(getClass().getName(), ">>>>"+bodyType);

        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(EditProfileThirdActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(EditProfileThirdActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("userfullname", profileModel.getUserfullname());
        map.put("aboutme", profileModel.getAboutme());
        map.put("profile_image", manageSession.getProfileModel().getProfileImage());
        map.put("useremail", manageSession.getProfileModel().getUseremail());
        map.put("dob", profileModel.getDob());

        map.put("work", profileModel.getWork());
//                .addBodyParameter("interest", profileModel.getInterest())
        map.put("education", profileModel.getEducation());
        map.put("hobbies", profileModel.getHobbies());
        map.put("generalroutine", profileModel.getGeneralroutine());
        map.put("weekendroutine", profileModel.getWeekendroutine());

        map.put("bodytype", bodyType);
        map.put("height", height);
        map.put("weight", weight);
//                .addBodyParameter("age", age)

        map.put("maternitystatus", profileModel.getMaternitystatus());
        map.put("children", profileModel.getChildren());

        map.put("oreintation", orientation);
        map.put("lookingfor", profileModel.getLookingfor());
//                .addBodyParameter("lookingfor", profileModel.getLookingfor())
        map.put("gender", profileModel.getGender());
        map.put("ethnicity", ethnicity);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.updateProfileApi(map);
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


                        if(status.equals("true")){
                            manageSession.setPerQuestion(jsonObject.optJSONObject("data").optInt("personality_quiz_question"));
                            manageSession.setPerAnswer(jsonObject.optJSONObject("data").optInt("personality_quiz_answer"));
                            manageSession.setPartnerQuestion(jsonObject.optJSONObject("data").optInt("partner_question"));
                            manageSession.setPartnerAnswer(jsonObject.optJSONObject("data").optInt("partner_answer"));

                            ProfileModel profileModel1 = new Gson().fromJson(jsonObject.optJSONObject("data").toString(), ProfileModel.class);
                            manageSession.setProfileModel(profileModel1);

                            manageSession.setFirstTimeEditProfile(false);

                            if (OutLook.editStatus){
                                Intent intent = new Intent(EditProfileThirdActivity.this , HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(EditProfileThirdActivity.this , WaitingListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        else {
                            Snackbar.make(parentLayout,message, Snackbar.LENGTH_SHORT).show();
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
}
