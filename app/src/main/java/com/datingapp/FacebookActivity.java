package com.datingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

public class FacebookActivity extends Activity {

    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());



        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                         getFBInfo();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                        setResult(0);
                        finish();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "Please click again.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        setResult(0);
                        finish();
                    }
                });

        facebookLogin();
    }

    private void facebookLogin(){


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"
        ));

    }

    public void getFBInfo() {


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        JSONObject jsonObject = response.getJSONObject();

                        Log.d("result",jsonObject+"");

                        Intent intent =new Intent();
                        intent.putExtra("result",jsonObject+"");
                        setResult(2,intent);
                          finish();


                        Log.d("Facebook login info", response.getJSONObject() + "");


                    }
                }
        ).executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v("Exception Facebook", " " + resultCode);


        callbackManager.onActivityResult(requestCode, resultCode, data);


        super.onActivityResult(requestCode, resultCode, data);
    }


}
