/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datingapp.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.datingapp.HomeActivity;
import com.datingapp.Login_SignupActivity;
import com.datingapp.R;
import com.datingapp.SplashScreenActivity;
import com.datingapp.WaitingListActivity;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.fragment.NotificationDialog;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.NotificationReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private ManageSession appSession;
    private NotificationManager notificationManager;
    private NotificationReceiver notificationReceiver;
    private Context context;


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NewToken", "Arrived->" + s);
        new ManageSession(this).setFCMToken(s);
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.

        context = getApplicationContext();
        appSession = new ManageSession(getApplicationContext());
        notificationReceiver = new NotificationReceiver();

        getApplicationContext().registerReceiver(notify, new IntentFilter("notify"));
        getApplicationContext().registerReceiver(rejectReceiver, new IntentFilter("rejectReceiver"));

        IntentFilter filter = new IntentFilter("count");
//        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getApplicationContext().registerReceiver(notificationReceiver, filter);
//        getApplicationContext().registerReceiver(notificationReceiver, new IntentFilter("count"));

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

//        Log.d(TAG, "Report Message Body :>>> " + remoteMessage);
//         Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Body :>>>" + remoteMessage.getData().toString());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//            Toast.makeText(getApplicationContext(), "Notification With Messages", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Message_Notification_Body:" + remoteMessage.getNotification().getBody());

            if (remoteMessage.getNotification().getBody() != null && !remoteMessage.getNotification().getBody().equals("")) {
//                int count = appSession.getMessageCount();
//                count = count + 1;
//                appSession.setMessageCount(1);
//                ((HomeActivity) context).setNotificationCount();

                Intent intent1 = new Intent();
                intent1.setAction("count");
                intent1.putExtra("type", "chat");
                sendBroadcast(intent1);
            }
        }


        boolean isAppOpen = false;
        if (isAppIsInBackground(getApplicationContext())) {
            isAppOpen = false;
        } else {
            isAppOpen = true;
        }
        try {
            if (isAppOpen) {
                //  Log.d("Executed app", "Application isAppOpen"+">>"+appSession.getNOtStatus());

                Map<String, String> data = remoteMessage.getData();
                if (appSession.getLoginPreference().equals("2")) {

                    Intent intent = new Intent(getBaseContext(), NotificationDialog.class);
                    Bundle bundle = new Bundle();
                    String data1 = data.get("data");
                    JSONObject jsonObject = new JSONObject(data1);
                    Log.i(TAG, "Inner Body : >>>>" + jsonObject.toString());
                    Log.i(TAG, "Inner Body : >>>>" + jsonObject.optJSONObject("payload").toString());

                    if (jsonObject.optJSONObject("payload").optString("type").equals("block") || jsonObject.optJSONObject("payload").optString("type").equals("accept") || jsonObject.optJSONObject("payload").optString("type").equals("reject") || jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("feedback") || jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("Approve") || jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("Veegle")) {

                        bundle.putString("title", "" + jsonObject.optString("title"));
                        bundle.putString("filename", "" + jsonObject.optString("image"));
                        bundle.putString("type", "" + jsonObject.optJSONObject("payload").optString("type"));
                        bundle.putString("inviteunique", "" + jsonObject.optJSONObject("payload").optString("inviteunique"));
                        bundle.putString("message", "" + jsonObject.optString("message"));
                        bundle.putString("notification_id", "" + jsonObject.optJSONObject("payload").optString("user_id"));
                        bundle.putString("full_name", "" + jsonObject.optJSONObject("payload").optString("userfullname"));
                        bundle.putString("profile_image", "" + jsonObject.optJSONObject("payload").optString("profile_image"));


                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.putExtras(bundle);
                        getApplicationContext().startActivity(intent);
                    } else {
                        Intent intent1 = new Intent();
                        intent1.setAction("count");
                        intent1.putExtra("type", jsonObject.optJSONObject("payload").optString("type"));
                        sendBroadcast(intent1);
                    }
                }
            } else {
                sendNotification(remoteMessage.getData(), new Intent());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver notify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("notify")) {
//                Toast.makeText(context, "Accept", Toast.LENGTH_SHORT).show();
                acceptOrRejectAndDelete(intent.getStringExtra("user_id"), intent.getStringExtra("inviteunique"), "accept", intent.getStringExtra("profile_image"), intent.getStringExtra("userfullname"));
            }
        }
    };

    private BroadcastReceiver rejectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("rejectReceiver")) {
//                Toast.makeText(context, "reject", Toast.LENGTH_SHORT).show();
                acceptOrRejectAndDelete(intent.getStringExtra("user_id"), intent.getStringExtra("inviteunique"), "reject", intent.getStringExtra("profile_image"), intent.getStringExtra("userfullname"));
            }
        }
    };

    public void acceptOrRejectAndDelete(final String inviteId, String inviteToken, final String type, final String image, final String name) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Log.i(getClass().getName(), "Parameter is ===> inviteId => " + inviteId + "  inviteToken => " + inviteToken + "  type => " + type + "  user_id => " + appSession.getProfileModel().getUser_id());
        Call<ResponseBody> call = apiInterface.removeInvite(Constant.APPTOKEN, appSession.getProfileModel().getUser_id(), inviteId, inviteToken, type);
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
                    try {
                        jsonObject = new JSONObject(str);
                        String response1 = jsonObject.optString("status");
                        if (response1.equals("1")) {
                            notificationManager.cancel(11);
                          /*  if (type.equals("accept")){
                                Intent intent = new Intent();
                                if (appSession.getLoginPreference().equals("2")) {
                                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("type", "accept");
                                    intent.putExtra("to_user_id", inviteId);
                                    intent.putExtra("user_id", appSession.getProfileModel().getUser_id());
                                    intent.putExtra("profile_image", image);
                                    intent.putExtra("userfullname", name);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(getApplicationContext(), Login_SignupActivity.class);
                                    startActivity(intent);
                                }
                            }*/
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LogView", "" + call.toString());
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getApplicationContext().unregisterReceiver(notify);
            getApplicationContext().unregisterReceiver(rejectReceiver);
            getApplicationContext().unregisterReceiver(notificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Map<String, String> data, Intent intent) throws JSONException {
        Log.i(TAG, "Notification >>>" + data.toString());
        String data1 = data.get("data");
        JSONObject jsonObject = new JSONObject(data1);
        try {
            Bundle bundle = new Bundle();

            if (jsonObject.has("notification")) {
                bundle.putString("type", "chat");
            } else {
                bundle.putString("type", "" + jsonObject.optJSONObject("payload").optString("type"));

                if (jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("accept")) {
                    bundle.putString("to_user_id", "" + jsonObject.optJSONObject("payload").optString("user_id"));
                    bundle.putString("userfullname", "" + jsonObject.optJSONObject("payload").optString("userfullname"));
                    bundle.putString("profile_image", "" + jsonObject.optJSONObject("payload").optString("profile_image"));
                }
            }
            bundle.putString("new", "" + "new");
            if (appSession.getLoginPreference().equals("2")) {
                SplashScreenActivity.chatStatus = "Splash";
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtras(bundle);
            } else {
                if (jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("Approve")) {
//                    SplashScreenActivity.chatStatus = "Splash";
                    appSession.setFirstTimeLaunch(true);
                    intent = new Intent(getApplicationContext(), WaitingListActivity.class);
                    intent.putExtras(bundle);
                } else {
                    intent = new Intent(getApplicationContext(), Login_SignupActivity.class);
                    intent.putExtras(bundle);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
//                0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
//        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.launcher_background);
//            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            notificationBuilder.setColor(getResources().getColor(R.color.transparent));
            notificationBuilder.setLargeIcon(bitmap);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }


        try {
            notificationBuilder.setContentTitle(jsonObject.optString("title"));
            notificationBuilder.setContentText(jsonObject.optString("message"));
//             notificationBuilder.setLargeIcon(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = null;
        if (!jsonObject.optString("image").equals(""))
            bitmap = getBitmapFromURL(jsonObject.optString("image"));

        notificationBuilder.setAutoCancel(true);
        // notificationBuilder.setSound(defaultSoundUri);
//        notificationBuilder.setContentIntent(pendingIntent);


//        if (appSession.isSound()) {
        if (appSession.getSoundString().equals("1")) {
            Log.i(getClass().getName(), "Sound >>>> true");
            notificationBuilder.setSound(defaultSoundUri);
        } else {
            Log.i(getClass().getName(), "Sound >>>> false");
            notificationBuilder.setSound(null);
        }

//        if (appSession.isVibration()) {
        if (appSession.getVibrationString().equals("1")) {
            Log.i(getClass().getName(), "Vibration >>>> true");
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
            notificationBuilder.setVibrate(pattern);
        } else {
            Log.i(getClass().getName(), "Vibration >>>> false");
            notificationBuilder.setVibrate(new long[]{0, 0});
        }
        notificationBuilder.setContentIntent(pendingIntent);


        ///////////////////////////

        if (jsonObject.optJSONObject("payload").optString("type").equalsIgnoreCase("invite")) {
            Intent acceptIntent = new Intent("notify");
            acceptIntent.putExtra("type", jsonObject.optJSONObject("payload").optString("type"));
            //        context.sendBroadcast(intent);
            acceptIntent.putExtra("user_id", jsonObject.optJSONObject("payload").optString("user_id"));
            acceptIntent.putExtra("inviteunique", jsonObject.optJSONObject("payload").optString("inviteunique"));
            acceptIntent.putExtra("profile_image", jsonObject.optJSONObject("payload").optString("profile_image"));
            acceptIntent.putExtra("userfullname", jsonObject.optJSONObject("payload").optString("userfullname"));


            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, acceptIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent acceptIntent1 = new Intent("rejectReceiver");
            acceptIntent1.putExtra("type", jsonObject.optJSONObject("payload").optString("type"));
            //        context.sendBroadcast(intent);
            acceptIntent1.putExtra("user_id", jsonObject.optJSONObject("payload").optString("user_id"));
            acceptIntent1.putExtra("inviteunique", jsonObject.optJSONObject("payload").optString("inviteunique"));
            acceptIntent1.putExtra("profile_image", jsonObject.optJSONObject("payload").optString("profile_image"));
            acceptIntent1.putExtra("userfullname", jsonObject.optJSONObject("payload").optString("userfullname"));

            PendingIntent snoozePendingIntent1 =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, acceptIntent1, PendingIntent.FLAG_CANCEL_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.addAction(0, "Accept", snoozePendingIntent);
                notificationBuilder.addAction(0, "Reject", snoozePendingIntent1);
            }
        }

/////////////////////////////

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(jsonObject.optString("message")));
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (bitmap != null) {
            NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
            s.setSummaryText(jsonObject.optString("message"));
            notificationBuilder.setStyle(s);
        }

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        notificationBuilder.setChannelId(CHANNEL_ID);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(11, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
