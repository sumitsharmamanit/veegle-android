package com.datingapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datingapp.HomeActivity;
import com.datingapp.R;
import com.datingapp.databinding.InviteFriendsBinding;
import com.datingapp.util.ManageSession;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class InviteFriends extends BaseFragment implements View.OnClickListener {

    private InviteFriendsBinding inviteFriendsBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
//    private DataBase dataBase;
    ShareDialog shareDialog;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 999;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inviteFriendsBinding = DataBindingUtil.inflate(inflater, R.layout.invite_friends, container, false);
        return inviteFriendsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
//        dataBase = new DataBase(context);

        initToolBar();
        setValues();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Invite Friends", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setValues() {
        shareDialog = new ShareDialog(getActivity());
        inviteFriendsBinding.llContact.setOnClickListener(this);
        inviteFriendsBinding.llFacebook.setOnClickListener(this);
        inviteFriendsBinding.llEmail.setOnClickListener(this);
        inviteFriendsBinding.llInstagram.setOnClickListener(this);
        inviteFriendsBinding.llTwitter.setOnClickListener(this);
    }

    private void createInstagramIntent(String type) {

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "veegle.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            path = FileProvider.getUriForFile(context,
                    getActivity().getApplicationContext().getPackageName() + ".provider", f);
        } else {
            path = Uri.fromFile(f);
        }

        // Create the URI from the media
//        File media = new File(String.valueOf(R.drawable.background_splash));
//        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, path);
        share.putExtra(Intent.EXTRA_TEXT, "http://veegleapp.com/DatingApp/");

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_contact:
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("sms:"));
//                    i.setType("text/plain");
//                    i.putExtra(Intent.EXTRA_SUBJECT, "Veegle");

//                    String sAux = "\nLet me recommend you this application\n\n";
//                    sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";

                    i.putExtra("sms_body", "https://play.google.com/store/search?q=com.veegleapp&hl=en");
//                    i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/search?q=com.veegleapp&hl=en");
//                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, "");
                    startActivity(i);
                } catch (Exception e) {
                    e.toString();
                    e.printStackTrace();
                }

                break;
            case R.id.ll_email:
              /*  Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Veegle");
                sendIntent.putExtra(Intent.EXTRA_TEXT   , "https://play.google.com/store/search?q=com.veegleapp&hl=en");
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send mail..."));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }*/

                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("") +
                        "?subject=" + Uri.encode("Veegle") +
                        "&body=" + Uri.encode("https://play.google.com/store/search?q=com.veegleapp&hl=en");
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail..."));
                break;
            case R.id.ll_facebook:
               /* String fullUrl = "https://www.facebook.com/sharer/sharer.php?u=";
                try {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setClassName("com.facebook.katana",
                            "com.facebook.katana.ShareLinkActivity");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/search?q=com.veegleapp&hl=en");
                    startActivity(sharingIntent);

                } catch (Exception e) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(fullUrl));
                    i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/search?q=com.veegleapp&hl=en");
                    startActivity(i);
                }*/

//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                        .build();
//                content.getQuote();

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote("Veegle let's find your twin flame")
//                            .setContentTitle()
//                            .setImageUrl(Uri.parse("http://freebizoffer.com/apptech/DatingApp/admin_assets/images/veegle.png"))
//                            .setContentDescription("This app is dating application")
                            .setContentUrl(Uri.parse("https://play.google.com/store/search?q=com.veegleapp&hl=en"))
                            .build();
                    shareDialog.show(linkContent);  // Show facebook ShareDialog
                }
                break;
            case R.id.ll_instagram:
//                if (hasPermissions(context, PERMISSIONS)) {
//                    String type = "image/*";
//                    createInstagramIntent(type);
//                } else {
//                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
//                }

                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Veegle");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/search?q=com.veegleapp&hl=en \n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share to"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.ll_twitter:
                String message = "https://play.google.com/store/search?q=com.veegleapp&hl=en";
                shareTwitter(message);
                break;
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String type = "image/*";
                    createInstagramIntent(type);
                } else {
                    Toast.makeText(context, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void shareTwitter(String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
            startActivity(i);
//            Toast.makeText(context, "Twitter app isn't found", Toast.LENGTH_LONG).show();
        }
    }


    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf(getClass().getName(), "UTF-8 should always be supported", e);
            return "";
        }
    }
}