package com.datingapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.CreateAccountActivity;
import com.datingapp.HomeActivity;
import com.datingapp.R;
import com.datingapp.WaitingListActivity;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.TermsConditionBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.squareup.picasso.Picasso;

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

public class TermsAndCondition extends BaseFragment {

    private TermsConditionBinding termsConditionBinding;
    private ManageSession manageSession;
    private Context context;
    private ProgressDialog progressDialog;
//    private DataBase dataBase;
    private boolean needHelp = false, signUpStatus = false;
    private RelativeLayout rlToolBar;
    private ImageView ivBack;
    private TextView toolbarText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.TERMS_CONDITION)) {
                needHelp = true;
            }

            if (getArguments().containsKey("activity")) {
                signUpStatus = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        termsConditionBinding = DataBindingUtil.inflate(inflater, R.layout.terms_condition, container, false);
        return termsConditionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
//        dataBase = new DataBase(context);
        initView(view);

        if (signUpStatus) {
            rlToolBar.setVisibility(View.VISIBLE);
            toolbarText.setText("Terms & Condition");
        } else {
            initToolBar();
            rlToolBar.setVisibility(View.GONE);
        }
//        initToolBar();

        if (!needHelp) {
//            getTermsAndCondition("term");
            getTermsAndConditionApi("term");
        }else{
//            getTermsAndCondition("need");
            getTermsAndConditionApi("need");
        }
    }

    private void initView(View view) {
        rlToolBar = (RelativeLayout) view.findViewById(R.id.relParent);
        ivBack = (ImageView) view.findViewById(R.id.imgBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popFragment();
                ((CreateAccountActivity) context).showWaitData();
            }
        });

        toolbarText = (TextView) view.findViewById(R.id.tv_text);

    }

    public void popFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        initToolBar();
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        if (needHelp) {
            ((HomeActivity) getActivity()).createBackButton("Need Help", true);
        } else {
            ((HomeActivity) getActivity()).createBackButton("Terms & Condition", true);
        }
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void loadPDF(String value) {

        termsConditionBinding.webView.getSettings().setJavaScriptEnabled(true);
        termsConditionBinding.webView.getSettings().setLoadWithOverviewMode(true);
        termsConditionBinding.webView.getSettings().setUseWideViewPort(false);
        termsConditionBinding.webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + value);
        // Force links and redirects to open in the WebView instead of in a browser
        termsConditionBinding.webView.setWebViewClient(new WebViewClient());
        termsConditionBinding.webView.getSettings().setSupportZoom(true);
        termsConditionBinding.webView.getSettings().setDisplayZoomControls(false);
        termsConditionBinding.webView.getSettings().setBuiltInZoomControls(true);
    }

    public void loadData(String description) {
        termsConditionBinding.webView.getSettings().setJavaScriptEnabled(true);
        termsConditionBinding.webView.loadData(description, "text/html; charset=utf-8", "UTF-8");
//        Log.d("description", jsonObject.optString("description"));
    }


    public void getTermsAndConditionApi(final String type) {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("type", type);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.termsConditionApi(map);
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
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if (status.equals("1")) {
//                                Log.i(getClass().getName(), "Link is >>>>>>>>" + response.getString("needHelp"));
//                                Log.i(getClass().getName(), "termsCondition Link is >>>>>>>>" + response.getString("termsCondition"));


//                                if (needHelp) {
//                                    loadPDF(response.getString("needHelp"));
//                                } else {
//                                    loadPDF(response.getString("termsCondition"));
//                                }


//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                                    termsConditionBinding.tvTerms.setText(Html.fromHtml(jsonObject.optString("termsCondition"), Html.FROM_HTML_MODE_COMPACT));
//                                }else {
                                    termsConditionBinding.tvTerms.setText(Html.fromHtml(jsonObject.optString("termsCondition")));
//                                }

//                            if (type.equals("need")){
//
//                                String mimeType = "text/html";
//                                String encoding = "UTF-8";
//                                String html = jsonObject.optString("termsCondition");
//
//                                termsConditionBinding.webView.getSettings();
//                                termsConditionBinding.webView.setBackgroundColor(Color.TRANSPARENT);
//
//                                termsConditionBinding.webView.loadDataWithBaseURL("", html, mimeType, encoding, "");
//                            }else {
//                            termsConditionBinding.tvTerms.setText(jsonObject.optString("termsCondition"));
//                            }

                        } else {
                            Snackbar.make(termsConditionBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(termsConditionBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}

