package com.datingapp.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.HomeActivity;
import com.datingapp.MapActivity;
import com.datingapp.R;
import com.datingapp.SplashScreenActivity;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationDialog extends Activity implements OnClickListener {
    private Context context = this;
    private TextView tvMessage, tvTitleDailogBox;
    private TextView tv_no, tv_yes;
    String title = "", message = "", type = "", id = "", image = "", orderId = "", orderStatus = "", status = "",
            storeStatus = "", notification_id = "", inviteUnique = "", fullName = "", profileImage = "";
    String yes_no = "";
    private ManageSession appSession;
    Bundle bundle;
    ImageView iv_banner;
    private ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_notification);
            appSession = new ManageSession(this);
            context = this;


            Window window = getWindow();
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(0));
            Dialog dialog = new Dialog(context);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            // dialog.setContentView(R.layout.dialog_box_ok);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            tvMessage = (TextView) findViewById(R.id.tv_message);
            iv_banner = (ImageView) findViewById(R.id.iv_banner);
            tvTitleDailogBox = (TextView) findViewById(R.id.title_dailog_box);
            tvMessage.setMovementMethod(new ScrollingMovementMethod());
            tv_yes = (TextView) findViewById(R.id.tv_yes);
            tv_no = (TextView) findViewById(R.id.tv_no);
            tv_yes.setOnClickListener(this);
            tv_no.setOnClickListener(this);
            bundle = getIntent().getExtras();

            if (bundle != null) {
                type = bundle.getString("type");
                message = bundle.getString("message");
                title = bundle.getString("title");
                id = bundle.getString("user_type");
                image = bundle.getString("filename");
                notification_id = bundle.getString("notification_id");
                inviteUnique = bundle.getString("inviteunique");

                fullName = bundle.getString("full_name");
                profileImage = bundle.getString("profile_image");


                if (type.equalsIgnoreCase("invite")) {
                    tv_yes.setText("Accept");
                    tv_yes.setVisibility(View.VISIBLE);
                    tv_no.setText("Reject");
                    tv_no.setVisibility(View.VISIBLE);
                } else if (type.equalsIgnoreCase("accept")) {
                    tv_yes.setText("Ok");
                    tv_yes.setVisibility(View.VISIBLE);
                    tv_no.setText("Ignore");
                    tv_no.setVisibility(View.VISIBLE);
                }

            }
            tvMessage.setText(Html.fromHtml(message));
            tvTitleDailogBox.setText(Html.fromHtml(title));
            if (!image.equals("")) {
                iv_banner.setVisibility(View.VISIBLE);
                Picasso.with(context).load(image)
                        .error(R.drawable.logo).placeholder(R.drawable.logo).into(iv_banner);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no:


                if (type.equalsIgnoreCase("invite")) {
                    acceptOrRejectAndDelete(notification_id, inviteUnique, "reject");
                }  else {
                    finish();
                }

                break;
            case R.id.tv_yes:
             /*   if (type.equals("BID_ACCEPTED")){
                    SplashScreenActivity.status = "BID_ACCEPTED";
                    GoNOtification();
                }else if (type.equals("ACTIVE_LISTING")){
                    SplashScreenActivity.status = "ACTIVE_LISTING";
                    GoNOtification();
//                    finish();
                }else if (type.equals("ASSIGN_QOUTES")){
                    SplashScreenActivity.status = "ASSIGN_QOUTES";
                    GoNOtification();
//                    finish();
                }else if (type.equals("PAY_TIME_OVER")){
                    SplashScreenActivity.status = "PAY_TIME_OVER";
                    GoNOtification();
//                    finish();
                }else if (type.equals("PAY_CANCEL")){
                    SplashScreenActivity.status = "PAY_CANCEL";
                    GoNOtification();
                }else if (type.equals("PAY_SUCCESS")){
                    SplashScreenActivity.status = "PAY_SUCCESS";
                    GoNOtification();
                }else if (type.equals("COMMON_NOTIFY")){
                    SplashScreenActivity.status = "COMMON_NOTIFY";
                    GoNOtification();
//                    finish();
                }else {*/
                if (type.equalsIgnoreCase("invite")) {
                    acceptOrRejectAndDelete(notification_id, inviteUnique, "accept");
                } else {
                    if (type.equalsIgnoreCase("accept")) {
                        SplashScreenActivity.chatStatus = "Splash";
                        Intent intent = new Intent(NotificationDialog.this, HomeActivity.class);
                        intent.putExtra("type", "accept");
                        intent.putExtra("to_user_id", notification_id);
                        intent.putExtra("user_id", appSession.getProfileModel().getUser_id());
                        intent.putExtra("profile_image", profileImage);
                        intent.putExtra("userfullname", fullName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                }
                /*}*/

                break;
        }
    }


    private void GoNOtification() {
        try {
            Intent intent;
            if (appSession.getLoginPreference().equals("2")) {

                intent = new Intent(this, HomeActivity.class);

//                }

                // Toast.makeText(context,"sfsdf",Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 11) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                } else {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void acceptOrRejectAndDelete(final String inviteId, String inviteToken, final String type) {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(context, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Log.i(getClass().getName(), "Parameter is ===> inviteId => " + inviteId + "  inviteToken => " + inviteToken + "  type => " + type + "  user_id => " + appSession.getProfileModel().getUser_id());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.removeInvite(Constant.APPTOKEN, inviteId, appSession.getProfileModel().getUser_id(), inviteToken, type);
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
                        String response1 = jsonObject.optString("status");
                        if (response1.equals("1")) {
                            Toast.makeText(getApplicationContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

//                            Log.i(getClass().getName(), "Response >>>>>> "+jsonObject.optJSONArray("data").toString());
                        }
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LogView", "" + call.toString());
                t.printStackTrace();
//                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
//                Snackbar.make(parentLayout, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}
