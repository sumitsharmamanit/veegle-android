package com.datingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.datingapp.Model.ProfileModel;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.GPSTracker;
import com.datingapp.util.InstagramApp;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class AfterLocationSelect extends AppCompatActivity {

    private Button btnContinueDating;
    private ImageView ivFacebook, ivTwitter, ivInstagram;
    private static String CONSUMER_KEY = "8TaYNOLJdXh3n7T7tRZY4iHQr";
    private static String CONSUMER_SECRET = "3eEgVhN37Q4zLxTd4pAW9R2J7XVKhJfFIlgBPc7OBLnu3fcszC";
    private Twitter twitter;
    private RequestToken requestToken = null;
    private AccessToken accessToken;
    private String oauth_url, oauth_verifier;
    private Dialog auth_dialog;
    private WebView web;
    private ProgressDialog progress;
    private InstagramApp mApp;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private RelativeLayout parentLayout;
//    private DataBase dataBase;
    private ManageSession manageSession;
    private Context context;
    private String notiificationId = "";
    GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 5555;
    //    private GPSTracker gps;
    private GPSTracker gpsTracker;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
//                displayInfoDialogView();
                mApp.resetAccessToken();
//                displayInfoDialogView();

//                socialApi("3", userInfoHashmap.get(InstagramApp.TAG_ID), userInfoHashmap.get(InstagramApp.TAG_USERNAME), "", userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE));
                socialApiRetrofil("3", userInfoHashmap.get(InstagramApp.TAG_ID), userInfoHashmap.get(InstagramApp.TAG_USERNAME), "", userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE));

            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(AfterLocationSelect.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_location_select);

        btnContinueDating = findViewById(R.id.btnContinueDating);
        ivFacebook = findViewById(R.id.imgFB);
        ivTwitter = findViewById(R.id.imgTwitter);
        ivInstagram = findViewById(R.id.imgInstagram);

        initView();
//        initialliseLocation(AfterLocationSelect.this);
        clickListener();

        notiificationId = manageSession.getFCMToken();
        if (notiificationId.equals("")) {
            notiificationId = FirebaseInstanceId.getInstance().getToken();
            manageSession.setFCMToken(notiificationId);
        }
    }

    private void clickListener() {
        btnContinueDating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(AfterLocationSelect.this, VerifyProfile.class));
                startActivity(new Intent(AfterLocationSelect.this, ProfilePhoto.class));
            }
        });

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFacebookTask();
            }
        });


        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (requestToken == null)
                new TokenGet().execute();
            }
        });

        ivInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectOrDisconnectUser();
            }
        });
    }

    private void initView() {
        context = this;
//        dataBase = new DataBase(this);
        manageSession = new ManageSession(this);
        gpsTracker = new GPSTracker(context);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        mApp = new InstagramApp(this, Constant.CLIENT_ID,
                Constant.CLIENT_SECRET, Constant.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                mApp.fetchUserName(handler);
//                Toast.makeText(AfterLocationSelect.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(AfterLocationSelect.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        if (mApp.hasAccessToken()) {
            mApp.fetchUserName(handler);
//            Toast.makeText(AfterLocationSelect.this, "Success", Toast.LENGTH_SHORT).show();
        }
    }

    private void callFacebookTask() {
        startActivityForResult(new Intent(this, FacebookActivity.class), 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == 2) {
            Log.d("Facebook Result", " " + data.getStringExtra("result"));
            try {
                JSONObject jsonObject = new JSONObject(data.getStringExtra("result"));
                String name = jsonObject.optString("email");
                String socialId = jsonObject.optString("id");
                String fullName = jsonObject.optString("first_name") + " " + jsonObject.optString("last_name");
                String firstName = jsonObject.optString("first_name");
                String lastName = jsonObject.optString("last_name");

                String type = "facebook";
                // send email and id to your web server
                String imageUrl = "https://graph.facebook.com/" + socialId + "/picture?type=large&height=400&width=400";

//                String imageUrl = "https://graph.facebook.com/" +socialId+ "/picture?type=large";

                Log.i(getClass().getName(), "id : " + socialId);
                Log.i(getClass().getName(), "imageURL : " + imageUrl);
                Log.i(getClass().getName(), "email : " + name);
                Log.i(getClass().getName(), "fullName : " + fullName);
                Log.i(getClass().getName(), "me : " + jsonObject.toString());


//                socialApi("2", socialId, firstName, name, imageUrl);

                socialApiRetrofil("2", socialId, firstName, name, imageUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        else {
//            if (requestCode == REQUEST_CHECK_SETTINGS) {
//                initialliseLocation(AfterLocationSelect.this);
//            }
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress = new ProgressDialog(AfterLocationSelect.this);
//            progress.setMessage("Please wait ...");
//            progress.setIndeterminate(true);
//            progress.setCancelable(false);
//            progress.setCanceledOnTouchOutside(false);
//            progress.show();

            progress = new ProgressDialog(AfterLocationSelect.this,
                    R.style.AppTheme_Dark_Dialog1);
            progress.setIndeterminate(true);
            progress = ProgressDialog.show(AfterLocationSelect.this, null, null);
            progress.setContentView(R.layout.progress_layout);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                requestToken = twitter.getOAuthRequestToken();
                oauth_url = requestToken.getAuthorizationURL();
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return oauth_url;
        }

        @Override
        protected void onPostExecute(String oauth_url) {
            if (oauth_url != null) {
                progress.dismiss();
                Log.e("URL", oauth_url);
                auth_dialog = new Dialog(AfterLocationSelect.this);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(oauth_url);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");

                            auth_dialog.dismiss();
                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            auth_dialog.dismiss();
                            Toast.makeText(AfterLocationSelect.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);


            } else {

                Toast.makeText(AfterLocationSelect.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, User> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress = new ProgressDialog(AfterLocationSelect.this);
//            progress.setMessage("Fetching Data ...");
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setIndeterminate(true);
//            progress.show();

            progress = new ProgressDialog(AfterLocationSelect.this,
                    R.style.AppTheme_Dark_Dialog1);
            progress.setIndeterminate(true);
            progress = ProgressDialog.show(AfterLocationSelect.this, null, null);
            progress.setContentView(R.layout.progress_layout);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);

        }


        @Override
        protected User doInBackground(String... args) {

            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                User user = twitter.showUser(accessToken.getUserId());
//                profile_url = user.getOriginalProfileImageURL();
                Log.e(getClass().getName(), "ACCESS_TOKEN " + accessToken.getToken());
                Log.e(getClass().getName(), "ACCESS_TOKEN_SECRET " + accessToken.getTokenSecret());
                Log.e(getClass().getName(), "NAME " + user.getName());
                Log.e(getClass().getName(), "IMAGE_URL " + user.getOriginalProfileImageURL());
                Log.e(getClass().getName(), "Info " + accessToken.getScreenName() + " " + user.getId() + "   > " + user.getScreenName());
                Log.e(getClass().getName(), "Email Id is " + user.getURLEntity());
                Log.e(getClass().getName(), "User Details " + user.toString());


                return user;
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                progress.dismiss();

//                Toast.makeText(AfterLocationSelect.this, "FullName > " + user.getName() + "\n" + "Image > " + user.getOriginalProfileImageURL() + "\n" + "UserName > " + user.getScreenName(), Toast.LENGTH_LONG).show();

//                socialApi("4", user.getId() + "", user.getScreenName(), "", user.getOriginalProfileImageURL());

                socialApiRetrofil("4", user.getId() + "", user.getScreenName(), "", user.getOriginalProfileImageURL());
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progress != null)
            progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null)
            progress.dismiss();
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    AfterLocationSelect.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    Toast.makeText(AfterLocationSelect.this, "Please again click.", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    public void socialApiRetrofil(String socialType, String socialId, String userName, String email, String profile) {
        new OutLook().hideKeyboard(this);
        progress = new ProgressDialog(AfterLocationSelect.this,
                R.style.AppTheme_Dark_Dialog1);
        progress.setIndeterminate(true);
        progress = ProgressDialog.show(AfterLocationSelect.this, null, null);
        progress.setContentView(R.layout.progress_layout);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("social_type", socialType);
        map.put("social_id", socialId);
        map.put("username", userName);
        map.put("device_token", notiificationId);
        map.put("device_type", Constant.DEVICE_TYPE);
        map.put("user_email", email);
        map.put("user_profile_pic", profile);
        map.put("imei", new OutLook().getIMEIorDeviceId(AfterLocationSelect.this));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.socialLoginApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progress.isShowing()) {
                    progress.dismiss();
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
                            JSONObject object = jsonObject.optJSONObject("data");
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.serializeNulls().create();
                            ProfileModel bean = gson.fromJson(object.toString(), ProfileModel.class);
//                            dataBase.clearProfile();
//                            dataBase.addProfileData(bean);

                            manageSession.setPerQuestion(jsonObject.optJSONObject("data").optInt("personality_quiz_question"));
                            manageSession.setPerAnswer(jsonObject.optJSONObject("data").optInt("personality_quiz_answer"));
                            manageSession.setPartnerQuestion(jsonObject.optJSONObject("data").optInt("partner_question"));
                            manageSession.setPartnerAnswer(jsonObject.optJSONObject("data").optInt("partner_answer"));

                            manageSession.setProfileModel(bean);

                            if (object.getString("activestatus").equals("0")) {
                                if (!manageSession.getLoginPreference().equals("1")) {
//                                        startActivity(new Intent(AfterLocationSelect.this, VerifyProfile.class));
                                    startActivity(new Intent(AfterLocationSelect.this, ProfilePhoto.class));
                                } else {
                                    manageSession.setLoginPreference("1");
                                    Intent intent = new Intent(AfterLocationSelect.this, WaitingListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                manageSession.setLoginPreference("2");
                                SplashScreenActivity.chatStatus = "Splash";
                                Intent intent = new Intent(AfterLocationSelect.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

}
