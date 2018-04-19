package com.datingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.datingapp.Model.ProfileModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin1 on 28/06/2018.
 */
public class ManageSession {

    private static final String SESSION_NAME = "com.datingapp.util";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private final String AD_COUNT = "ad_count";

    public ManageSession(Context context) {
        mSharedPreferences = context.getSharedPreferences(SESSION_NAME,
                Context.MODE_PRIVATE);
        prefsEditor = mSharedPreferences.edit();
    }

    public String getLoginPreference() {
        return mSharedPreferences.getString("getLoginPreference", "");
    }

    public void setLoginPreference(String getLoginPreference) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLoginPreference", getLoginPreference);
        prefsEditor.commit();
    }

    public int adCount() {
        return mSharedPreferences.getInt(AD_COUNT, 1);
    }

    public void setadCount(int adCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt(AD_COUNT, adCount);
        prefsEditor.commit();
    }


/*
    public static boolean setPreference(Context context, String key, String value) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getPreference(Context context, String key) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void getClearPreference(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }
    public static void getLogout(Context context) {
        settings = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }
*/






/*
    //For Merging
    public void setNotification(Boolean value) {
        editor.putBoolean("Notification", value);
        editor.commit();
    }

    public boolean isNotification() {
        return settings.getBoolean("Notification", true);
    }

    public void setSound(Boolean value) {
        editor.putBoolean("Sound", value);
        editor.commit();
    }

    public boolean isSound() {
        return settings.getBoolean("Sound", false);
    }


    public void setVibration(Boolean value) {
        editor.putBoolean("Vibration", value);
        editor.commit();
    }

    public boolean isVibration() {
        return settings.getBoolean("Vibration", false);
    }
*/

    public String getSoundString() {
        return mSharedPreferences.getString("getSoundString", "");
    }

    public void setSoundString(String getSoundString) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getSoundString", getSoundString);
        prefsEditor.commit();
    }

    public String getVibrationString() {
        return mSharedPreferences.getString("getVibrationString", "");
    }

    public void setVibrationString(String getVibrationString) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getVibrationString", getVibrationString);
        prefsEditor.commit();
    }

    public ProfileModel getProfileModel() {
        String urlsJSONString = mSharedPreferences.getString("profile", "");
        if (urlsJSONString == null)
            return null;
        Type type = new TypeToken<ProfileModel>() {
        }.getType();
        ProfileModel profileModel = new Gson().fromJson(urlsJSONString, type);
        return profileModel;
    }

    public void setProfileModel(ProfileModel profileModel) {
        prefsEditor = mSharedPreferences.edit();
        if (profileModel == null)
            prefsEditor.putString("profile", null);
        else {
            String urlsJSONString = new Gson().toJson(profileModel);
            prefsEditor.putString("profile", urlsJSONString);
        }
        prefsEditor.commit();
    }

    //FCM
    public String getFCMToken() {
        return mSharedPreferences.getString("FCMToken", "");
    }

    public void setFCMToken(String FCMToken) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("FCMToken", FCMToken);
        prefsEditor.commit();
    }


    public Uri getImageUri() {
        String imageUri = mSharedPreferences.getString("getImageUri", "");
        if (imageUri == null || imageUri.equals(""))
            return null;
        return Uri.parse(imageUri);
    }

    public void setImageUri(Uri imageUri) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImageUri", imageUri.toString());
        prefsEditor.commit();
    }

    public String getImagePath() {
        return mSharedPreferences.getString("getImagePath", "");
    }

    public void setImagePath(String imagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImagePath", imagePath);
        prefsEditor.commit();
    }

    public String getCropImagePath() {
        return mSharedPreferences.getString("getCropImagePath", "");
    }

    public void setCropImagePath(String cropImagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getCropImagePath", cropImagePath);
        prefsEditor.commit();
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        prefsEditor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return mSharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeMessageShow(boolean isFirstTime) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isFirstTimeMessageShow", isFirstTime);
        prefsEditor.commit();
    }

    public boolean isFirstTimeMessageShow() {
        return mSharedPreferences.getBoolean("isFirstTimeMessageShow", false);
    }

////////////////////////////

    public void setAutoMatched(int getAutoMatched) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getAutoMatched", getAutoMatched);
        prefsEditor.commit();
    }

    public int getAutoMatched() {
        return mSharedPreferences.getInt("getAutoMatched", 0);
    }

    public void setMatched(int getMatched) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getMatched", getMatched);
        prefsEditor.commit();
    }

    public int getMatched() {
        return mSharedPreferences.getInt("getMatched", 0);
    }

    public void setProfileCount(int getProfileCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getProfileCount", getProfileCount);
        prefsEditor.commit();
    }

    public int getProfileCount() {
        return mSharedPreferences.getInt("getProfileCount", 0);
    }

    public void setLikeCount(int getLikeCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getLikeCount", getLikeCount);
        prefsEditor.commit();
    }

    public int getLikeCount() {
        return mSharedPreferences.getInt("getLikeCount", 0);
    }

    public void setInviteCount(int getInviteCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getInviteCount", getInviteCount);
        prefsEditor.commit();
    }

    public int getInviteCount() {
        return mSharedPreferences.getInt("getInviteCount", 0);
    }

    public void setBlockCount(int getBlockCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getBlockCount", getBlockCount);
        prefsEditor.commit();
    }

    public int getBlockCount() {
        return mSharedPreferences.getInt("getBlockCount", 0);
    }


    public void setTotalCount(int setTotalCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("setTotalCount", setTotalCount);
        prefsEditor.commit();
    }

    public int getTotalCount() {
        return mSharedPreferences.getInt("setTotalCount", 0);
    }


    public void setMessageCount(int getMessageCount) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getMessageCount", getMessageCount);
        prefsEditor.commit();
    }

    public int getMessageCount() {
        return mSharedPreferences.getInt("getMessageCount", 0);
    }

    public void setFirstTimeEditProfile(boolean isFirstTime) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isFirstTimeEditProfile", isFirstTime);
        prefsEditor.commit();
    }

    public boolean isFirstTimeEditProfile() {
        return mSharedPreferences.getBoolean("isFirstTimeEditProfile", true);
    }

    public String getLatitude() {
        return mSharedPreferences.getString("getLatitude", "");
    }


    public void setLatitude(String getLatitude) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLatitude", getLatitude);
        prefsEditor.commit();
    }

    public String getLongitude() {
        return mSharedPreferences.getString("getLogitude", "");
    }


    public void setLongitude(String getLogitude) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLogitude", getLogitude);
        prefsEditor.commit();
    }

    //////////////////

    public int getPerQuestion() {
        return mSharedPreferences.getInt("getPerQuestion", 0);
    }

    public void setPerQuestion(int getPerQuestion) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getPerQuestion", getPerQuestion);
        prefsEditor.commit();
    }

    public int getPerAnswer() {
        return mSharedPreferences.getInt("getPerAnswer", 0);
    }

    public void setPerAnswer(int getPerAnswer) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getPerAnswer", getPerAnswer);
        prefsEditor.commit();
    }

    public int getPartnerQuestion() {
        return mSharedPreferences.getInt("getPartnerQuestion", 0);
    }

    public void setPartnerQuestion(int getPartnerQuestion) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getPartnerQuestion", getPartnerQuestion);
        prefsEditor.commit();
    }

    public int getPartnerAnswer() {
        return mSharedPreferences.getInt("getPartnerAnswer", 0);
    }

    public void setPartnerAnswer(int getPartnerAnswer) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putInt("getPartnerAnswer", getPartnerAnswer);
        prefsEditor.commit();
    }

    public List<String> getAcceptInviteList() {
        String userJSONString = mSharedPreferences.getString("getAcceptInviteList", "");
        if (userJSONString.equals(""))
            return new ArrayList<String>();

        Type listType = new TypeToken<List<String>>() {}.getType();
        Gson gson = new Gson();

        List<String> target2 = gson.fromJson(userJSONString, listType);
        return target2;
    }

    public void setAcceptInviteList(List<String> getAcceptInviteList) {
        prefsEditor = mSharedPreferences.edit();
        if (getAcceptInviteList == null)
            prefsEditor.putString("getAcceptInviteList", new ArrayList<String>()+"");
        else {
            String userJSONString = new Gson().toJson(getAcceptInviteList);
            prefsEditor.putString("getAcceptInviteList", userJSONString);
        }
        prefsEditor.commit();
    }

}
