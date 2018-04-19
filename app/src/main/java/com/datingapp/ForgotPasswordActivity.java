package com.datingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.OutLook;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout imgBack;
    private ProgressDialog progressDialog;
    private Button btnResetPassword;
    private EditText emailId;
    private RelativeLayout parentLayout;
    private Context context;
    private LinearLayout llEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
    }

    private void initView(){
        context             = this;

        parentLayout        = (RelativeLayout) findViewById(R.id.parentLayout);
        imgBack             = (LinearLayout)findViewById(R.id.imgBack);
        btnResetPassword    = (Button) findViewById(R.id.btnResetPassword);
        emailId             = (EditText) findViewById(R.id.emailId);

        imgBack.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);

        llEmail = (LinearLayout) findViewById(R.id.linEmail);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailId.requestFocus();
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(emailId, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }


    private boolean checkValidation(){
        if(!new OutLook().isNetworkConnected(context)){
            new OutLook().isNetworkConnected(context);
            return false;
        }else if (emailId.getText().toString().trim().length() == 0){
            Snackbar.make(parentLayout,getResources().getString(R.string.enter_login_txt),Snackbar.LENGTH_SHORT).show();
            return false;
        }else if (!new OutLook().checkEmail(emailId.getText().toString())){
            Snackbar.make(parentLayout, "Please enter valid email.",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    public void forgotPasswordApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(ForgotPasswordActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("useremail", emailId.getText().toString());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.forgotApi(map);
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

                        if(status.equals("1")){
                            Snackbar snackbar = Snackbar.make(parentLayout, "" + message, Snackbar.LENGTH_SHORT);
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    onBackPressed();
                                }
                            });
                            snackbar.show();
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

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.imgBack){
            onBackPressed();
        }

        if(v.getId() == R.id.btnResetPassword){
            if(checkValidation()){
                new OutLook().hideKeyboard(ForgotPasswordActivity.this);
//                ForgotPasswordAPI();

                forgotPasswordApi();
            }
        }
    }
}
