package com.datingapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Suraj Shakya on 2018/05/24.
 */

public class OutLook {

    public static long days;
    public static boolean editStatus = false;
    public static String ADDRESS = "";
    public static Double LATITUDE = 0.0;
    public static Double LONGITUDE = 0.0;
    public static int USER_VALUE = 0;
    public static int SENDER_VALUE = 0;

    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    public String formatDateShow(String inputDate) {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = format.parse(inputDate);
            format = new SimpleDateFormat("dd-MM-yyyy");
            date = format.format(newDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("", "printHashKey() Hash Key: " + hashKey);
                System.out.println("hashKey : " + hashKey);
                Toast.makeText(pContext, "" + hashKey, Toast.LENGTH_SHORT).show();
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("", "printHashKey()", e);
        }
    }


    public String getUpdateTime(String updateTime, Context mContext) {
        Date currentDate = new Date();
        long diff, second, minute, hour, day, year;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//             dateFormat.setTimeZone(TimeZone.getTimeZone(String.valueOf(TIME_ZONE)));
            Date updateDate = dateFormat.parse(updateTime);
            diff = currentDate.getTime() - updateDate.getTime();
            second = diff / 1000;
            minute = second / 60;
            hour = minute / 60;
            day = hour / 24;
            year = day / 365;
            if (second <= 59) {
                if (second <= 1)
                    return mContext.getResources().getString(
                            R.string.a_moment_ago);
                return second
                        + " "
                        + mContext.getResources().getString(
                        R.string.seconds_ago);
            } else if (minute <= 59) {
                if (minute == 1)
                    return minute
                            + " "
                            + mContext.getResources().getString(
                            R.string.minute_ago);
                else
                    return minute
                            + " "
                            + mContext.getResources().getString(
                            R.string.minutes_ago);
            } else if (hour <= 23) {
                if (hour == 1)
                    return hour
                            + " "
                            + mContext.getResources().getString(
                            R.string.hour_ago);
                else
                    return hour
                            + " "
                            + mContext.getResources().getString(
                            R.string.hours_ago);
            } else if (day <= 364) {
                days = day;
                if (day == 1)
                    return day
                            + " "
                            + mContext.getResources().getString(
                            R.string.day_ago);
                else
                    return day
                            + " "
                            + mContext.getResources().getString(
                            R.string.days_ago);
            } else {
                if (year == 1)
                    return year
                            + " "
                            + mContext.getResources().getString(
                            R.string.year_ago);
                else
                    return year
                            + " "
                            + mContext.getResources().getString(
                            R.string.years_ago);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // return "a moment ago";
        return " ";
    }

    public String getIMEIorDeviceId(Context context) {
        String deviceId = "";
        if (isEmulator()) return "ANDROID_EMULATOR";
        try {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        /*    if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {*/
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    public boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || Build.PRODUCT.equals("sdk")
                || Build.PRODUCT.contains("_sdk")
                || Build.PRODUCT.contains("sdk_");
    }

    Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^[0-9]{8,14}$");

    /**
     * method for mobile_app number validation
     */
    public boolean checkMobile(String mobile) {
        try {
            mobile = mobile.replaceAll("[^0-9]", "");
            if (MOBILE_NUMBER_PATTERN.matcher(mobile).matches())
                return true;
            else
                return false;
        } catch (Exception exception) {
            return false;
        }
    }

    // public String input = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public  static String input = "EEE, dd MMM yyyy HH:mm:ss z";
    //  public static String input = "yyyy-MM-dd HH:mm:ss";
    public String outputCal = "MMM dd, hh:mm a";

    public String formateCal(String inputDate) {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat(input, Locale.getDefault());
        try {
            Date newDate = format.parse(inputDate);

            format = new SimpleDateFormat(outputCal, Locale.getDefault());
            date = format.format(newDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("^([a-zA-Z0-9._-]+)@{1}(([a-zA-Z0-9_-]{1,67})|([a-zA-Z0-9-]+\\.[a-zA-Z0-9-]{1,67}))\\.(([a-zA-Z0-9]{2,6})(\\.[a-zA-Z0-9]{2,6})?)$");


    public boolean checkEmail(String email) {
        try {
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
        } catch (NullPointerException exception) {
            return false;
        }
    }


    /*--- Check For netWork Connetion---*/
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /*--- Check For netWork Connetion---*/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public String GetArrayPicker(final Context context, final String[] array, final OnTaskCompleted onTaskCompleted) {
        final String[] nameString = {""};
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.picker_view);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(array.length - 1);
        numberPicker.setDisplayedValues(array);
        TextView cancelTxt, OkayTxt;
        cancelTxt = (TextView) dialog.findViewById(R.id.cancelTxt);
        OkayTxt = (TextView) dialog.findViewById(R.id.OkayTxt);

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        OkayTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString[0] = "" + array[numberPicker.getValue()];

                onTaskCompleted.onTaskCompleted(nameString[0]);
                dialog.dismiss();
            }
        });
        return "" + array[numberPicker.getValue()];
    }

    public void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }
}
