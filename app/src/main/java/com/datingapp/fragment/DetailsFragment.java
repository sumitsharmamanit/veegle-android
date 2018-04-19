package com.datingapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.datingapp.HomeActivity;
import com.datingapp.Model.BlogDTO;
import com.datingapp.Model.ProfileModel;
import com.datingapp.R;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.databinding.FragmentDetailsBinding;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OutLook;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends BaseFragment implements DefaultSliderView.OnSliderClickListener, View.OnClickListener {

    private FragmentDetailsBinding fragmentDetailsBinding;
    private Context context;
    private ManageSession manageSession;
    private ProgressDialog progressDialog;
    private String id = "", report = "", homeFragmentPosition = "", spinnerReason = "", enterREason = "";
    private ArrayList<String> imagesList;
    private ProfileModel profileModel;
    private ArrayList<String> reportList;
    SearchableSpinner spReport;
    public static int a = 0;
    private ArrayList<BlogDTO> educationList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            id = getArguments().getString("id");
            homeFragmentPosition = getArguments().getString("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        return fragmentDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        manageSession = new ManageSession(context);
        initView();
        initToolBar();
        ((HomeActivity) context).bottomVisibility(false);
        getUserDetailsApi();


//        sliderLayout = view.findViewById(R.id.slider_lay);
//        indicator = view.findViewById(R.id.custom_indicator);

        fragmentDetailsBinding.cvOne.setEnabled(true);
        fragmentDetailsBinding.cvTwo.setEnabled(true);
        fragmentDetailsBinding.llName.setEnabled(true);
        fragmentDetailsBinding.viewLine.setEnabled(true);
    }

    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) context).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Details", true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).setNavigationIconColor(AppConstants.LIGHT);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((HomeActivity) context).hideAds();
    }

    private void initView() {
        reportList = new ArrayList<>();
        imagesList = new ArrayList<>();
        profileModel = new ProfileModel();
        fragmentDetailsBinding.cvOne.setOnClickListener(this);
        fragmentDetailsBinding.cvTwo.setOnClickListener(this);
        fragmentDetailsBinding.llName.setOnClickListener(this);
        fragmentDetailsBinding.viewLine.setOnClickListener(this);
        fragmentDetailsBinding.tvBlock.setOnClickListener(this);
        fragmentDetailsBinding.tvAbuse.setOnClickListener(this);
        fragmentDetailsBinding.ivOptions.setOnClickListener(this);
        fragmentDetailsBinding.ivLike.setOnClickListener(this);
        fragmentDetailsBinding.ivDislike.setOnClickListener(this);
        fragmentDetailsBinding.cvThree.setOnClickListener(this);
        fragmentDetailsBinding.parentLayout.setOnClickListener(this);

        fragmentDetailsBinding.ivTriangle.setColorFilter(getResources().getColor(R.color.white));
    }

    public void getUserDetailsApi() {
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
        map.put("to_user_id", id);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.userDetailsApi(map);
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
                            profileModel = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), ProfileModel.class);

                            if (profileModel.getSocialType() != null && !profileModel.getSocialType().equals("1")) {
                                String to = profileModel.getProfileImage().replaceAll("(?<!http:)//", "/");
                                imagesList.add(to);
                            } else {
                                imagesList.add(profileModel.getProfileImage());
                            }

                            fragmentDetailsBinding.tvName.setText(profileModel.getUsername() + ", " + profileModel.getAge());
//                                if (profileModel.getIsOnline().equals("1"))

                            if (OutLook.days <= 90)
                                fragmentDetailsBinding.tvActive.setText("- " + new OutLook().getUpdateTime(profileModel.getUpdatedOn(), context));
                            else
                                fragmentDetailsBinding.tvActive.setText("- In Active");
//                                else
//                                    fragmentDetailsBinding.tvActive.setText("- Not Active Today");

                            fragmentDetailsBinding.tvMin.setText("- " + profileModel.getDistancebetweenusers());
                            fragmentDetailsBinding.tvPercentage.setText(profileModel.getPercentage());

                            fragmentDetailsBinding.speedView.setMaxSpeed(100);
                            fragmentDetailsBinding.speedView.setMinSpeed(0);
                            if (!profileModel.getSpeedometer().equals(""))
                                fragmentDetailsBinding.speedView.speedTo(Float.parseFloat(profileModel.getSpeedometer()), 10000);

                            if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                                fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_4));
                            } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("0")) {
                                fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_1));
                            } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("0") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                                fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_3));
                            } else {
                                fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_2));
                            }
//                                setImages();

                            if (profileModel.getUserPics() != null && profileModel.getUserPics().size() > 0) {
                                for (int i = 0; i < profileModel.getUserPics().size(); i++) {
                                    Log.i(getClass().getName(), "User Pics is inner " + i);
                                    imagesList.add(Constant.IMAGE_SERVER_PATH + profileModel.getUserPics().get(i).getPicUrl());
                                }
                            }
                            showSlider();

                        } else {
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    fragmentDetailsBinding.parentLayout.setVisibility(View.GONE);
                    popFragment();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Snackbar.make(fragmentDetailsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void setImages() {
        profileModel.setLikeStatus("1");

        if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
            fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_4));
        } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("0")) {
            fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_1));
        } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("0") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
            fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_3));
        } else {
            fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_2));
        }
    }

    private void showSlider() {
        Log.i(getClass().getName(), "Size is >" + imagesList.size());
//        for(String name : imagesList){
        for (int i = 0; i < imagesList.size(); i++) {

//            }
            Log.i(getClass().getName(), "Value is inner " + imagesList.get(i));

            DefaultSliderView textSliderView = new DefaultSliderView(context);
            // initialize a SliderLayout
            textSliderView
//                    .description(name)
                    .image(imagesList.get(i))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", imagesList.get(i));

            fragmentDetailsBinding.slider.addSlider(textSliderView);
        }
//        fragmentDetailsBinding.slider.startAutoCycle();
        fragmentDetailsBinding.slider.stopAutoCycle();
        fragmentDetailsBinding.slider.setPresetTransformer(SliderLayout.Transformer.Stack);
//        fragmentDetailsBinding.slider.setCustomIndicator(fragmentDetailsBinding.customIndicator);
        fragmentDetailsBinding.slider.setCustomAnimation(new DescriptionAnimation());
        fragmentDetailsBinding.slider.setDuration(5000);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        DetailsFragmentNext detailsFragmentNext = new DetailsFragmentNext();
        bundle.putSerializable("profileModel", profileModel);
        detailsFragmentNext.setArguments(bundle);

        switch (view.getId()) {
            case R.id.cv_one:
                ((HomeActivity) context).showInterstitialAds();

                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                fragmentDetailsBinding.cvOne.setEnabled(false);
                fragmentDetailsBinding.cvTwo.setEnabled(false);
                fragmentDetailsBinding.llName.setEnabled(false);
                fragmentDetailsBinding.viewLine.setEnabled(false);
                hideFragmentWithOther(fragmentDetailsBinding.cvOne);

//                addFragmentWithoutRemove(R.id.output, detailsFragmentNext, "DetailsFragmentNext");
                break;
            case R.id.cv_two:
                ((HomeActivity) context).showInterstitialAds();

                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                fragmentDetailsBinding.cvOne.setEnabled(false);
                fragmentDetailsBinding.cvTwo.setEnabled(false);
                fragmentDetailsBinding.llName.setEnabled(false);
                fragmentDetailsBinding.viewLine.setEnabled(false);
                hideFragmentWithOther(fragmentDetailsBinding.cvTwo);

//                addFragmentWithoutRemove(R.id.output, detailsFragmentNext, "DetailsFragmentNext");
                break;
            case R.id.ll_name:
                ((HomeActivity) context).showInterstitialAds();

                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                fragmentDetailsBinding.cvOne.setEnabled(false);
                fragmentDetailsBinding.cvTwo.setEnabled(false);
                fragmentDetailsBinding.llName.setEnabled(false);
                fragmentDetailsBinding.viewLine.setEnabled(false);
                hideFragmentWithOther(fragmentDetailsBinding.llName);

//                addFragmentWithoutRemove(R.id.output, detailsFragmentNext, "DetailsFragmentNext");
                break;
            case R.id.view_line:
                ((HomeActivity) context).showInterstitialAds();

                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                fragmentDetailsBinding.cvOne.setEnabled(false);
                fragmentDetailsBinding.cvTwo.setEnabled(false);
                fragmentDetailsBinding.llName.setEnabled(false);
                fragmentDetailsBinding.viewLine.setEnabled(false);
                hideFragmentWithOther(fragmentDetailsBinding.viewLine);
//                addFragmentWithoutRemove(R.id.output, detailsFragmentNext, "DetailsFragmentNext");
                break;
            case R.id.iv_options:
                fragmentDetailsBinding.llBlock.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_block:
                dialogBlock();
                break;
            case R.id.tv_abuse:
                reportList = new ArrayList<>();
                dialogReport();
                break;
            case R.id.iv_like:
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
//                getLikeOrRemove(id, "1");

                getLikeOrRemoveApi(id, "1");
                break;
            case R.id.iv_dislike:
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
//                getLikeOrRemove(id, "2");

                getLikeOrRemoveApi(id, "2");
                break;
            case R.id.cv_three:
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                break;
            case R.id.slider:
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                break;
            case R.id.parentLayout:
                ((HomeActivity) context).showInterstitialAds();

                hideFragmentWithOther(fragmentDetailsBinding.viewLine);
                break;
        }
    }

    public void hideFragmentWithOther(View view) {
        Bundle bundle = new Bundle();
        DetailsFragmentNext detailsFragmentNext = new DetailsFragmentNext();
        bundle.putSerializable("profileModel", profileModel);
        detailsFragmentNext.setArguments(bundle);

        DetailsFragment detailsFragment = new DetailsFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Inflate transitions to apply
            Transition changeTransform = TransitionInflater.from(context).
                    inflateTransition(R.transition.change_image_transform);
            Transition explodeTransform = TransitionInflater.from(context).
                    inflateTransition(android.R.transition.explode);

            // Setup exit transition on first fragment
            detailsFragment.setSharedElementReturnTransition(changeTransform);
            detailsFragment.setExitTransition(explodeTransform);

            // Setup enter transition on second fragment
            detailsFragmentNext.setSharedElementEnterTransition(changeTransform);
            detailsFragmentNext.setEnterTransition(explodeTransform);

            // Find the shared element (in Fragment A)
//            RoundedImageView ivProfile = (RoundedImageView) findViewById(R.id.iv_profile);

            // Add second fragment by replacing first
            try {
            FragmentTransaction ft = getFragmentManager().beginTransaction()
//                    .replace(R.id.output, detailsFragment)
                    .add(R.id.output, detailsFragmentNext)
                    .addToBackStack("transaction")
                    .addSharedElement(view, "profileOne");
            // Apply the transaction
            ft.commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            addFragmentWithoutRemove(R.id.output, detailsFragmentNext, "DetailsFragmentNext");
        }
    }

    public void addFragmentWithoutRemove1(int containerViewId, Fragment fragment, String fragmentName) {
        String tag = (String) fragment.getTag();
        getActivity().getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.animator.bottom_to_top, R.animator.top_to_bottom)
                // remove fragment from fragment manager
                //fragmentTransaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag(tag));
                // add fragment in fragment manager
                .add(containerViewId, fragment, fragmentName)
                // add to back stack
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
        initToolBar();

        if (a == 1) {
            setImages();
            HomeFragment.favoriteStatus = homeFragmentPosition;
            a = 0;
        }

        fragmentDetailsBinding.cvOne.setEnabled(true);
        fragmentDetailsBinding.cvTwo.setEnabled(true);
        fragmentDetailsBinding.llName.setEnabled(true);
        fragmentDetailsBinding.viewLine.setEnabled(true);
    }

    public void dialogBlock() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_user);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        final Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.red));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnYes.setBackground(gradientDrawable1);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(context.getResources().getColor(R.color.bg_btn));
        gradientDrawable2.setCornerRadius(dpAsPixels);
        btnNo.setBackground(gradientDrawable2);

        ImageView ivCancel = (ImageView) dialog.findViewById(R.id.iv_close);
        final EditText etReason = (EditText) dialog.findViewById(R.id.et_block_reason);
        etReason.setVisibility(View.VISIBLE);

        TextView tvHeader = (TextView) dialog.findViewById(R.id.tv_header);
        final RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_education);

        final SearchableSpinner spEducation = (SearchableSpinner) dialog.findViewById(R.id.sp_education);

        tvHeader.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

        spEducation.setTitle("Select Reason");
        spEducation.setPositiveButton("Close");
        spEducation.setVisibility(View.VISIBLE);

        educationList = new ArrayList<>();
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setName("Pretending to someone");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Fake Account");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Nudity");
        educationList.add(blogDTO);
        blogDTO = new BlogDTO();
        blogDTO.setName("Abusive");
        educationList.add(blogDTO);

        spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerReason = educationList.get(position).getName();
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTextColor(context.getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spEducation.onTouch(relativeLayout.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
            }
        });

        ArrayAdapter<BlogDTO> oriAdapter = new ArrayAdapter<BlogDTO>(context,
                R.layout.spinner_textview_one, educationList);
        spEducation.setAdapter(oriAdapter);

        spEducation.setSelection(0);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                blockUser(dialog);
                try {
                    enterREason = etReason.getText().toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                blockUserApi(dialog);

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    public void blockUserApi(final Dialog dialog) {
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
        map.put("blocked_user_id", id);
        map.put("reason", "block");
        map.put("feedbackreason", spinnerReason);
        map.put("anotherreason", enterREason);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.blockUserApi(map);
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
                            HomeFragment.blockId = profileModel.getUser_id();
                            dialog.dismiss();
                            fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                            popFragment();
                        } else {
                            dialog.dismiss();
                            fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(fragmentDetailsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void popFragment() {
        FragmentManager fragmentManager = ((HomeActivity) context).getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    public void dialogReport() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_blog);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        TextView tvUpload = (TextView) dialog.findViewById(R.id.tv_upload);

        final TextView tvReport = (TextView) dialog.findViewById(R.id.tv_reprt);
        final RelativeLayout rlReport = (RelativeLayout) dialog.findViewById(R.id.rl_reprt);
        rlReport.setVisibility(View.VISIBLE);
        tvUpload.setVisibility(View.GONE);
        final EditText etBlog = (EditText) dialog.findViewById(R.id.et_blog);
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        btnDone.setText("Ok");
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.iv_close);
        spReport = (SearchableSpinner) dialog.findViewById(R.id.sp_reprt);
//        etBlog.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        etBlog.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        etBlog.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                if (s.length() > 0) {
                    if (s.toString().toLowerCase().contains("facebook") || s.toString().toLowerCase().contains("official") || s.toString().toLowerCase().contains("linkedin") || s.toString().toLowerCase().contains("modified") || s.toString().toLowerCase().contains("snapchat")) {
                        etBlog.setText(s.subSequence(0, s.length() - 8));
                        etBlog.setSelection(s.length() - 8);
                    } else if (s.toString().toLowerCase().contains("youtube") || s.toString().toLowerCase().contains("retweet") || s.toString().toLowerCase().contains("twitter") || s.toString().toLowerCase().contains("google+")) {
                        etBlog.setText(s.subSequence(0, s.length() - 7));
                        etBlog.setSelection(s.length() - 7);
                    } else if (s.toString().toLowerCase().contains("google")) {
                        etBlog.setText(s.subSequence(0, s.length() - 6));
                        etBlog.setSelection(s.length() - 6);
                    } else if (s.toString().toLowerCase().contains("gmail") || s.toString().toLowerCase().contains("insta") || s.toString().toLowerCase().contains("tweet") || s.toString().toLowerCase().contains("yahoo")) {
                        etBlog.setText(s.subSequence(0, s.length() - 5));
                        etBlog.setSelection(s.length() - 5);
                    } else if (s.toString().toLowerCase().contains("analytics") || s.toString().toLowerCase().contains("instagram")) {
                        etBlog.setText(s.subSequence(0, s.length() - 9));
                        etBlog.setSelection(s.length() - 9);
                    } else if (s.toString().toLowerCase().contains("snap") || s.toString().toLowerCase().contains("chat") || s.toString().toLowerCase().contains("kick") || s.toString().toLowerCase().contains("gram") || s.toString().toLowerCase().contains(".com") || s.toString().toLowerCase().contains("mail")) {
                        etBlog.setText(s.subSequence(0, s.length() - 4));
                        etBlog.setSelection(s.length() - 4);
                    } else if (s.toString().toLowerCase().contains("fbo") || s.toString().toLowerCase().contains(".co") || s.toString().toLowerCase().contains(".in")) {
                        etBlog.setText(s.subSequence(0, s.length() - 3));
                        etBlog.setSelection(s.length() - 3);
                    } else if (s.toString().toLowerCase().contains("fb") || s.toString().toLowerCase().contains("g+") || s.toString().toLowerCase().contains("ga") || s.toString().toLowerCase().contains("ig") || s.toString().toLowerCase().contains("sc") || s.toString().toLowerCase().contains("mt") || s.toString().toLowerCase().contains("rt") || s.toString().toLowerCase().contains("yt")) {
                        etBlog.setText(s.subSequence(0, s.length() - 2));
                        etBlog.setSelection(s.length() - 2);
                    } else if (s.toString().toLowerCase().contains("@")) {
                        etBlog.setText(s.subSequence(0, s.length() - 1));
                        etBlog.setSelection(s.length() - 1);
                    } else if (new OutLook().checkMobile(s.toString())) {
                        etBlog.setText(s.subSequence(0, s.length() - 8));
                        etBlog.setSelection(s.length() - 8);
                    }
                }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        spReport.setTitle("Report");
        spReport.setPositiveButton("Close");
        spReport.setSelection(0);

        reportList.add("It's spam");
        reportList.add("It's inappropriate");
        reportList.add("Others");

        ArrayAdapter<String> reprtAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_textview, reportList);
        spReport.setAdapter(reprtAdapter);

        spReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTextColor(context.getResources().getColor(R.color.black));
                report = reportList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        float scale = getResources().getDisplayMetrics().density;
//        GradientDrawable gradientDrawable = new GradientDrawable();
//        gradientDrawable.setStroke(((int) (1 * scale + 0.5f)), context.getResources().getColor(R.color.jumbo));
//        gradientDrawable.setCornerRadius(((int) (5 * scale + 0.5f)));
//
//        GradientDrawable gradientDrawable2 = new GradientDrawable();
//        gradientDrawable2.setStroke(((int) (1 * scale + 0.5f)), context.getResources().getColor(R.color.jumbo));
//        gradientDrawable2.setCornerRadius(((int) (5 * scale + 0.5f)));
//
//        tvUpload.setBackground(gradientDrawable);
//        rlReport.setBackground(gradientDrawable);
//        etBlog.setBackground(gradientDrawable2);

//        GradientDrawable drawable = (GradientDrawable) tvUpload.getBackground();
//        drawable.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);
//
//        GradientDrawable drawable1 = (GradientDrawable) etBlog.getBackground();
//        drawable1.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);
//
//        GradientDrawable drawable2 = (GradientDrawable) rlReport.getBackground();
//        drawable2.setColorFilter(context.getResources().getColor(R.color.gray_holo_dark), PorterDuff.Mode.SRC_ATOP);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.btn_color));
        int dpAsPixels = (int) (30 * scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnDone.setBackground(gradientDrawable1);

        new OutLook().hideKeyboard(getActivity());

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spReport.setVisibility(View.VISIBLE);
                spReport.onTouch(tvReport.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                tvReport.setVisibility(View.GONE);

            }
        });

        rlReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spReport.setVisibility(View.VISIBLE);
                spReport.onTouch(rlReport.getRootView(), MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 1, 1, 1));
                tvReport.setVisibility(View.GONE);

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String blog = etBlog.getText().toString();
                if (!report.equals("Others")) {
//                    reportAbuse(dialog, blog);
                    reportAbuseApi(dialog, blog);
                } else {
                    if (!blog.equals("")) {
//                        reportAbuse(dialog, blog);
                        reportAbuseApi(dialog, blog);
                    } else {
                        Snackbar.make(fragmentDetailsBinding.parentLayout, "Please enter for report.", Snackbar.LENGTH_SHORT).show();
                    }

                }

            }
        });
        dialog.show();
    }


    public void reportAbuseApi(final Dialog dialog, String text) {
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
        map.put("report_user_id", id);
        map.put("type", report);
        map.put("reason", text);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.reportAbuseApi(map);
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
                            dialog.dismiss();
                            fragmentDetailsBinding.llBlock.setVisibility(View.GONE);
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(fragmentDetailsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void getLikeOrRemoveApi(final String id, final String status) {
        new OutLook().hideKeyboard(getActivity());
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(getActivity(), null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);
        map.put("user_id", manageSession.getProfileModel().getUser_id());
        map.put("user_to_id", id);
        map.put("like_status", status);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.setLikeApi(map);
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
                        String status11 = jsonObject.optString("response");
                        String message = jsonObject.optString("message");

                        if (status11.equals("true")) {
                            if (status.equals("2")) {
                                HomeFragment.blockId = id;
                                popFragment();
                            } else {
                                HomeFragment.favoriteStatus = homeFragmentPosition;
                                Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
//                                    fragmentDetailsBinding.ivLike.setImageDrawable(getResources().getDrawable(R.drawable.heart_detail_1));
                                profileModel.setLikeStatus("1");

                                if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("1")) {
                                    fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_4));
                                } else if (profileModel.getLikeStatus() != null && profileModel.getLikeStatus().equals("1") && profileModel.getLikeStatusnearby() != null && profileModel.getLikeStatusnearby().equals("0")) {
                                    fragmentDetailsBinding.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_detail_1));
                                }
                            }
                        } else {
                            Snackbar.make(fragmentDetailsBinding.parentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(fragmentDetailsBinding.parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}
