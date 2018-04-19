package com.datingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Movie;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private int SPLASH_TIME_OUT = 7000;
    private final String TAG = "SplashActivity";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Context context;
    private ManageSession manageSession;
    public static String chatStatus = "";

    //    private VideoView videoView;
//    private Uri video;
//    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this;
        manageSession = new ManageSession(context);

//        printHashKey(context);

      /*  if (!chatStatus.equals("")){
            if (manageSession.getLoginPreference().equals("2")) {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                Intent intent = new Intent(SplashScreenActivity.this, Login_SignupActivity.class);
                startActivity(intent);
                finishAffinity();
            }
            chatStatus = "";
        }*/
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    if (manageSession.getLoginPreference().equals("1")) {
                        Intent intent = new Intent(SplashScreenActivity.this, WaitingListActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else if (manageSession.getLoginPreference().equals("2")) {
                        chatStatus = "Splash";
                        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else if (manageSession.getLoginPreference().equals("3")) {
//                        Intent  intent = new Intent(SplashScreenActivity.this , LocationActivity.class);
                        Intent intent = new Intent(SplashScreenActivity.this, AfterLocationSelect.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, Login_SignupActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            }

        }, SPLASH_TIME_OUT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Intent intent = new Intent(SplashScreenActivity.this, Login_SignupActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                Toast.makeText(this, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public void printHashKey(Context pContext) {
//        try {
//            PackageInfo info = pContext.getPackageManager().getPackageInfo("com.veegleapp", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("", "printHashKey() Hash Key: " + hashKey);
//                System.out.println("hashKey : "+hashKey);
//                Toast.makeText(pContext,""+hashKey,Toast.LENGTH_SHORT).show();
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("", "printHashKey()", e);
//        }
//    }
}
