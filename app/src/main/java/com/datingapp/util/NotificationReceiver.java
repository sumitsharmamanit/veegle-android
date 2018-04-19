package com.datingapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.datingapp.HomeActivity;

public class NotificationReceiver extends BroadcastReceiver {

    private String type = "";
    private ManageSession manageSession;
    private int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        manageSession = new ManageSession(context);

        if (intent.getStringExtra("type") == null)
            return;

        if (intent.getAction().matches("count") && count == 0) {
            if (intent != null){
                Log.i(getClass().getName(), "NotificationReceiver_>>>>"+ intent.getStringExtra("type"));
                type = intent.getStringExtra("type");

                if (type.equals("invite")) {
                    Log.i(getClass().getName(), "Invite >>>>" + manageSession.getInviteCount());
                    manageSession.setInviteCount((manageSession.getInviteCount() + 1));
                    Log.i(getClass().getName(), "????" + manageSession.getInviteCount());
//                } else if (type.equals("accept")){
//                        Log.i(getClass().getName(), "Invite >>>>"+manageSession.getInviteCount());
//                        manageSession.setInviteCount((manageSession.getInviteCount() + 1));
//                        Log.i(getClass().getName(), "????"+manageSession.getInviteCount());
                } else if (type.equals("like")){
                    Log.i(getClass().getName(), "like >>>>"+manageSession.getLikeCount());
                    manageSession.setLikeCount((manageSession.getLikeCount() + 1));
                    Log.i(getClass().getName(), "like ????"+manageSession.getLikeCount());
                } else if (type.equals("visit")){
                    Log.i(getClass().getName(), "Visit >>>>"+manageSession.getProfileCount());
                    manageSession.setProfileCount((manageSession.getProfileCount() + 1));
                    Log.i(getClass().getName(), "Visit ????"+manageSession.getProfileCount());
                } else if (type.equals("block")){
//                    Log.i(getClass().getName(), "block >>>>"+manageSession.getBlockCount());
//                    manageSession.setBlockCount((manageSession.getBlockCount() + 1));
//                    Log.i(getClass().getName(), "block ????"+manageSession.getBlockCount());
                } else if (type.equals("chat")){
                Log.i(getClass().getName(), "chat >>>>"+manageSession.getMessageCount());
                manageSession.setMessageCount((manageSession.getMessageCount() + 1));
                Log.i(getClass().getName(), "chat ????"+manageSession.getMessageCount());
            } else if (type.equals("automatch")){
                    Log.i(getClass().getName(), "chat >>>>"+manageSession.getAutoMatched());
                    manageSession.setAutoMatched((manageSession.getAutoMatched() + 1));
                    Log.i(getClass().getName(), "chat ????"+manageSession.getAutoMatched());
                }

            Log.i(getClass().getName(), "getTotalCount >>>>"+manageSession.getTotalCount());
                manageSession.setTotalCount((manageSession.getInviteCount() + manageSession.getLikeCount() + manageSession.getProfileCount() +
                        manageSession.getBlockCount() + manageSession.getMatched() + manageSession.getAutoMatched()));

                Log.i(getClass().getName(), "getTotalCount ????"+manageSession.getTotalCount());


                ++count;
              /*  HomeActivity homeActivity = new HomeActivity();
                homeActivity.setNotificationCount(context);*/
                context.sendBroadcast(new Intent("INTERNET_LOST"));

            }
        }
    }
}
