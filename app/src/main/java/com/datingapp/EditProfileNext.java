package com.datingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
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

public class EditProfileNext extends AppCompatActivity implements View.OnClickListener {

    private ProfileModel profileModel;
    private ImageView ivBack, ivHobbies;
    private Button btnNext;
    private EditText etWork, etEducation, etHobbies, etInterest, etGeneral, etWeekend;
    private String work = "", education = "", hobbies = "", interest = "", general = "", weekend = "";
    private ScrollView parentLayout;
    private ManageSession manageSession;
    private boolean editStatus = false;
    private LinearLayout llHobbies, llBack;
    private ArrayList<String> stringArrayList;
    ProgressDialog progressDialog;
    private String ethnicity = "", orientation = "";
    private SearchableSpinner spWork, spEducation;
    private ArrayList<BlogDTO> workList, educationList;
    private int workPosition = 0, educationPosition = 0;
    private RelativeLayout rlWork, rlEducation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_next);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        profileModel = (ProfileModel) bundle.getSerializable("ProfileModel");
        Log.i(getClass().getName(), ">>>>" + profileModel.getLookingfor());
        Log.i(getClass().getName(), ">>>>" + profileModel.getUserfullname());
        Log.i(getClass().getName(), ">>>>" + profileModel.getGender());
        Log.i(getClass().getName(), ">>>>" + profileModel.getMaternitystatus());
        initView();
//        getEthnicity();

        getEthnicityApi();
        new OutLook().hideKeyboard(EditProfileNext.this);
    }

    private void setValues() {
        etWork.setText(manageSession.getProfileModel().getWork());
        etWork.setSelection(etWork.getText().length());
        etEducation.setText(manageSession.getProfileModel().getEducation());
        etEducation.setSelection(etEducation.getText().length());
        etInterest.setText(manageSession.getProfileModel().getInterest());
        etInterest.setSelection(etInterest.getText().length());
        etHobbies.setText(manageSession.getProfileModel().getHobbies());
//        etWork.setSelection(etWork.getText().length());
        etGeneral.setText(manageSession.getProfileModel().getGeneralroutine());
        etGeneral.setSelection(etGeneral.getText().length());
        etWeekend.setText(manageSession.getProfileModel().getWeekendroutine());
        etWeekend.setSelection(etWeekend.getText().length());

    }

    private void initView() {
        stringArrayList = new ArrayList<>();
        workList = new ArrayList<>();
        educationList = new ArrayList<>();
        manageSession = new ManageSession(this);
        etWork = (EditText) findViewById(R.id.et_work);
        etEducation = (EditText) findViewById(R.id.et_education);
        etHobbies = (EditText) findViewById(R.id.et_hobbies);
        etInterest = (EditText) findViewById(R.id.et_interest);
        etGeneral = (EditText) findViewById(R.id.et_general_routine);
        etWeekend = (EditText) findViewById(R.id.et_weekend_routine);

        ivHobbies = (ImageView) findViewById(R.id.iv_hobbies);
        ivHobbies.setOnClickListener(this);
        parentLayout = (ScrollView) findViewById(R.id.parentLayout);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        etHobbies.setOnClickListener(this);
        llBack.setOnClickListener(this);

        rlEducation = (RelativeLayout) findViewById(R.id.rl_education);
        rlEducation.setOnClickListener(this);
        rlWork = (RelativeLayout) findViewById(R.id.rl_work);
        rlWork.setOnClickListener(this);

        spWork = (SearchableSpinner) findViewById(R.id.sp_work);
        spWork.setTitle("Select Work");
        spWork.setPositiveButton("Close");

        spEducation = (SearchableSpinner) findViewById(R.id.sp_education);
        spEducation.setTitle("Select Education");
        spEducation.setPositiveButton("Close");

        if (manageSession.getProfileModel() != null && !manageSession.getProfileModel().getEthnicity().equals("")) {
            editStatus = true;
            setValues();
        }

        spWork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                work = workList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                education = educationList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        etWeekend.setHorizontallyScrolling(false);
//        etWeekend.setMaxLines(Integer.MAX_VALUE);

//        etWeekend.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        etGeneral.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        etWeekend.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        etGeneral.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


        etWork.addTextChangedListener(new TextWatcher() {

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
                        etWork.setText(s.subSequence(0, s.length() - 8));
                        etWork.setSelection(s.length() - 8);
                    }else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        etWork.setText(s.subSequence(0, s.length() - 7));
                        etWork.setSelection(s.length() - 7);
                    }else if (s.toString().toLowerCase().contains("google")) {
                        etWork.setText(s.subSequence(0, s.length() - 6));
                        etWork.setSelection(s.length() - 6);
                    }else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        etWork.setText(s.subSequence(0, s.length() - 5));
                        etWork.setSelection(s.length() - 5);
                    }else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        etWork.setText(s.subSequence(0, s.length() - 9));
                        etWork.setSelection(s.length() - 9);
                    }else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        etWork.setText(s.subSequence(0, s.length() - 4));
                        etWork.setSelection(s.length() - 4);
                    }else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        etWork.setText(s.subSequence(0, s.length() - 3));
                        etWork.setSelection(s.length() - 3);
                    }else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+") || s.toString().toLowerCase().contains("ga") || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc") || s.toString().toLowerCase().contains("mt") || s.toString().toLowerCase().contains("rt") || s.toString().toLowerCase().contains("yt")) {
                        etWork.setText(s.subSequence(0, s.length() - 2));
                        etWork.setSelection(s.length() - 2);
                    }else if (s.toString().toLowerCase().contains("@")) {
                        etWork.setText(s.subSequence(0, s.length() - 1));
                        etWork.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())){
                        etWork.setText(s.subSequence(0, s.length() - 8));
                        etWork.setSelection(s.length() - 8);
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });


        etGeneral.addTextChangedListener(new TextWatcher() {

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
                        etGeneral.setText(s.subSequence(0, s.length() - 8));
                        etGeneral.setSelection(s.length() - 8);
                    }else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 7));
                        etGeneral.setSelection(s.length() - 7);
                    }else if (s.toString().toLowerCase().contains("google")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 6));
                        etGeneral.setSelection(s.length() - 6);
                    }else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 5));
                        etGeneral.setSelection(s.length() - 5);
                    }else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 9));
                        etGeneral.setSelection(s.length() - 9);
                    }else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 4));
                        etGeneral.setSelection(s.length() - 4);
                    }else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 3));
                        etGeneral.setSelection(s.length() - 3);
                    }else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+") || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc") || s.toString().toLowerCase().contains("yt")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 2));
                        etGeneral.setSelection(s.length() - 2);
                    }else if (s.toString().toLowerCase().contains("@")) {
                        etGeneral.setText(s.subSequence(0, s.length() - 1));
                        etGeneral.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())){
                        etGeneral.setText(s.subSequence(0, s.length() - 8));
                        etGeneral.setSelection(s.length() - 8);
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        etWeekend.addTextChangedListener(new TextWatcher() {

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
                        etWeekend.setText(s.subSequence(0, s.length() - 8));
                        etWeekend.setSelection(s.length() - 8);
                    }else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 7));
                        etWeekend.setSelection(s.length() - 7);
                    }else if (s.toString().toLowerCase().contains("google")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 6));
                        etWeekend.setSelection(s.length() - 6);
                    }else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 5));
                        etWeekend.setSelection(s.length() - 5);
                    }else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 9));
                        etWeekend.setSelection(s.length() - 9);
                    }else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 4));
                        etWeekend.setSelection(s.length() - 4);
                    }else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 3));
                        etWeekend.setSelection(s.length() - 3);
                    }else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+")  || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc")  || s.toString().toLowerCase().contains("yt")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 2));
                        etWeekend.setSelection(s.length() - 2);
                    }else if (s.toString().toLowerCase().contains("@")) {
                        etWeekend.setText(s.subSequence(0, s.length() - 1));
                        etWeekend.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())){
                        etWeekend.setText(s.subSequence(0, s.length() - 8));
                        etWeekend.setSelection(s.length() - 8);
                    }
                }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                new OutLook().hideKeyboard(this);
                work = etWork.getText().toString().trim();
//                education = etEducation.getText().toString();
                interest = etInterest.getText().toString();
                hobbies = etHobbies.getText().toString();
                general = etGeneral.getText().toString().trim();
                weekend = etWeekend.getText().toString().trim();

                if (isValid()) {
                    profileModel.setWork(work);
                    profileModel.setEducation(education);
                    profileModel.setInterest(interest);
                    profileModel.setHobbies(hobbies);
                    profileModel.setGeneralroutine(general);
                    profileModel.setWeekendroutine(weekend);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ProfileModel", profileModel);
                    Intent intent = new Intent(EditProfileNext.this, EditProfileThirdActivity.class);
                    intent.putExtra("ethnicity", ethnicity);
                    intent.putExtra("orientation", orientation);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.et_hobbies:
                Intent intent = new Intent(this, HobbiesActivity.class);
                intent.putExtra("hobbies", etHobbies.getText().toString());

//                Toast.makeText(this, "Edit Hobbies clicked", Toast.LENGTH_SHORT).show();

//                startActivityForResult(new Intent(this, HobbiesActivity.class), 101);
                startActivityForResult(intent, 101);
                break;
            case R.id.iv_hobbies:
//                if (hobbies != null && !hobbies.equals("")) {
//                    Intent intent = new Intent(EditProfileNext.this, HobbiesActivity.class);
//                    intent.putExtra("list", hobbies);
//                    startActivityForResult(intent, 101);
//                } else {
                    startActivityForResult(new Intent(this, HobbiesActivity.class), 101);
//                }
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_work:
//                spWork.onTouch(rlWork.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));

                etWork.requestFocus();
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(etWork, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.rl_education:
                spEducation.onTouch(rlEducation.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            if (data != null) {
                hobbies = data.getStringExtra("list");
                hobbies = hobbies.replace("[", "");
                hobbies = hobbies.replace("]", "");
                etHobbies.setText(hobbies);
            }
        }
    }

    private boolean isValid() {
        if (work == null || work.equals("")) {
            Snackbar.make(parentLayout, "Please enter work.", Snackbar.LENGTH_SHORT).show();
            etWork.requestFocus();
            return false;
        } else if (education == null || education.equals("")) {
            Snackbar.make(parentLayout, "Please select education.", Snackbar.LENGTH_SHORT).show();
            etEducation.requestFocus();
            return false;
//        } else if (interest == null || interest.equals("")) {
//            Snackbar.make(parentLayout, "Please enter interest.", Snackbar.LENGTH_SHORT).show();
//            etInterest.requestFocus();
//            return false;
        }

//        else if (hobbies == null || hobbies.equals("")) {
//            Snackbar.make(parentLayout, "Please enter your hobbies.", Snackbar.LENGTH_SHORT).show();
//            etHobbies.requestFocus();
//            return false;
//        } else if (general == null || general.equals("")) {
//            Snackbar.make(parentLayout, "Please enter general routine.", Snackbar.LENGTH_SHORT).show();
//            etGeneral.requestFocus();
//            return false;
//        } else if (weekend == null || weekend.equals("")) {
//            Snackbar.make(parentLayout, "Please enter weekend routine.", Snackbar.LENGTH_SHORT).show();
//            etWeekend.requestFocus();
//            return false;
//        }
        return true;
    }


    public void getEthnicityApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(EditProfileNext.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(EditProfileNext.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.getEthnicityApi(map);
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
                    JSONObject json = null;
                    Log.e("LogView", "" + str.toString());
                    try {
                        json = new JSONObject(str);
                        String status = json.optString("status");
                        String message = json.optString("message");

                        if(status.equals("1")){
                            JSONObject jsonObject = json.optJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("Ethnicity");
                            JSONArray jsonArray1 = jsonObject.optJSONArray("Orientation");
                            JSONArray jsonArray2 = jsonObject.optJSONArray("education");
                            JSONArray jsonArray3 = jsonObject.optJSONArray("work");

                            ethnicity = jsonArray.toString();
                            orientation = jsonArray1.toString();

                            workList = new Gson().fromJson(jsonArray3.toString(), new TypeToken<ArrayList<BlogDTO>>(){}.getType());
                            educationList = new Gson().fromJson(jsonArray2.toString(), new TypeToken<ArrayList<BlogDTO>>(){}.getType());



                            ArrayAdapter<BlogDTO> workAdapter = new ArrayAdapter<BlogDTO>(EditProfileNext.this,
                                    R.layout.spinner_textview, workList);
                            spWork.setAdapter(workAdapter);

                            if (editStatus) {
                                Log.i(getClass().getName(), "?????? 1111");
                                for (int i = 0; i < workList.size(); i++) {
                                    if (manageSession.getProfileModel().getWork().equalsIgnoreCase(workList.get(i).getName())) {
                                        spWork.setSelection(i);
                                        Log.i(getClass().getName(), "??????  44444444444 55555555"+workList.get(i).getName());
                                        break;
                                    }
                                }
                            } else {
                                spWork.setSelection(workPosition);
                            }

                            ArrayAdapter<BlogDTO> oriAdapter = new ArrayAdapter<BlogDTO>(EditProfileNext.this,
                                    R.layout.spinner_textview, educationList);
                            spEducation.setAdapter(oriAdapter);

                            if (editStatus) {
                                Log.i(getClass().getName(), "?????? 1111");
                                for (int i = 0; i < educationList.size(); i++) {

                                    if (manageSession.getProfileModel().getEducation().equalsIgnoreCase(educationList.get(i).getName())) {
                                        spEducation.setSelection(i);
                                        Log.i(getClass().getName(), "??????  44444444444 55555555"+educationList.get(i).getName());
                                        break;
                                    }
                                }
                            } else {
                                spEducation.setSelection(educationPosition);
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
