package com.datingapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.datingapp.HomeActivity;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.DeactivateAccountBinding;
import com.datingapp.databinding.SettingsBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
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

public class DeactivateAccount extends BaseFragment implements View.OnClickListener {

    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
    private String[]  itemsDeActivate = {"Technical issues", "App crashes frequently", "Not many users to match with", "Too many fake profiles", "I didn't find any matches", "I don't understand all the features of this App", "I don't find this app interesting", "I'm in a relationship now", "Too many adverts", "I prefer other apps"};
    private DeactivateAccountBinding deactivateAccountBinding;
    private String spinnerReason = "", enterREason = "", education = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deactivateAccountBinding = DataBindingUtil.inflate(inflater, R.layout.deactivate_account, container, false);
        return deactivateAccountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
//        dataBase = new DataBase(context);

        initToolBar();
        initView();
    }

    private void initView() {
        deactivateAccountBinding.btnYes.setOnClickListener(this);
        deactivateAccountBinding.rlEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        spEducation.onTouch(relativeLayout.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                showDialogForDeActivate(deactivateAccountBinding.etEducation);
            }
        });

        deactivateAccountBinding.etEducation.setFocusable(false);
//                etEducation.setEnabled(false);

        deactivateAccountBinding.etEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForDeActivate(deactivateAccountBinding.etEducation);
            }
        });

    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Deactivate Account", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        initToolBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                try {
                    enterREason = deactivateAccountBinding.etBlockReason.getText().toString().trim();
                    education =  deactivateAccountBinding.etEducation.getText().toString().trim();
                    if (education != null && !education.equals("")) {
                        if (enterREason != null && !enterREason.equals("")) {
                            notifiyPreferenceApiRetrofit("deactiveaccount", "1");
                        } else {
                            deactivateAccountBinding.etBlockReason.requestFocus();
                            dialogOK(context, "", "Please give another reason.", "Ok", false);
                        }
                    } else {
                        dialogOK(context, "", "Please select a reason.", "Ok", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void showDialogForDeActivate(final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please select a reason");
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, R.layout.list_item_deactivate, itemsDeActivate){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                return view;
            }

            @Override
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
//                TextView text1 = (TextView) view.findViewById(R.id.tv_reason);
//                text1.setTextColor(Color.RED);
                return view;
            }


        };
        builder.setAdapter(stringArrayAdapter,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int item){
                        String itemName=stringArrayAdapter.getItem(item);
                        editText.setText(itemsDeActivate[item]);
                    }
                });

        //list of items

//        builder.setSingleChoiceItems(itemsWeight, weightPosition,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // item selected logic
//
//                        etWeight.setText(itemsWeight[which]);
//                        weightPosition = which;
//                        dialog.dismiss();
//                    }
//                });


        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton("",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void dialogOK(final Context context, String title, String message,
                         String btnText, final boolean isFinish) {
        // https://www.google.com/design/spec/components/dialogs.html#dialogs-specs
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage(Html.fromHtml(message));
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Nirmala.ttf");
//        alertDialogBuilder.setView(new TextView(context));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish)
                    ((Activity) context).finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void notifiyPreferenceApiRetrofit(final String type, final String value) {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("type", type);
        map.put("value", value);

        if (enterREason != null && !enterREason.equals("")) {
            map.put("feedbackreason", education);
            map.put("anotherreason", enterREason);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.notifpreferenceApi(map);
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
                        String status = jsonObject.optString("response");
                        String message = jsonObject.optString("data");

                        if (status.equals("true")) {
                            ProfileModel profileModel = manageSession.getProfileModel();

                            if (type.equals("deactiveaccount")) {
                                profileModel.setDeactiveAccount(value);
                            }

                            manageSession.setProfileModel(profileModel);

                            ((HomeActivity) context).onBackPressed();

                        } else {
                            Snackbar.make(deactivateAccountBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(deactivateAccountBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}
