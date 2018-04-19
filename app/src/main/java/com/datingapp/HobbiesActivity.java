package com.datingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.datingapp.Model.BlogDTO;
import com.datingapp.adapter.BlogAdapter;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.Constant;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HobbiesActivity extends AppCompatActivity {

    private RecyclerView rvHome;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private ProgressDialog progressDialog;
//    private DataBase dataBase;
    private RelativeLayout parentLayout, rlToolbar;
    private ImageView ivBack;
    private ArrayList<BlogDTO> blogDTOArrayList;
    private HashSet<String> stringArrayList;
    private Button btnSave;
    private String priviousHobbies = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_list);

        if (getIntent().getStringExtra("hobbies") != null){
            priviousHobbies = getIntent().getStringExtra("hobbies");
        }

        context = this;
        initView();
//        getHobbies();

        getHobbiesApi();
    }

    private void initView() {
        blogDTOArrayList = new ArrayList<>();
        stringArrayList = new HashSet<>();
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        rlToolbar = (RelativeLayout) findViewById(R.id.relParent);
        rlToolbar.setVisibility(View.VISIBLE);
        parentLayout.setBackgroundColor(getResources().getColor(R.color.white));
        ivBack = (ImageView) findViewById(R.id.imgBack);
        rvHome = (RecyclerView) findViewById(R.id.rv_home);
        gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvHome.setLayoutManager(gridLayoutManager);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setVisibility(View.VISIBLE);
      /*  ShapeDrawable shapeDrawable = new ShapeDrawable();

        float[] radii = new float[8];
        radii[0] = getResources().getDimension(R.dimen.footer_corners);
        radii[1] = getResources().getDimension(R.dimen.footer_corners);

        radii[2] = getResources().getDimension(R.dimen.footer_corners);
        radii[3] = getResources().getDimension(R.dimen.footer_corners);

        shapeDrawable.setShape(new RoundRectShape(radii, null, null));
        shapeDrawable.getPaint().setColor(getResources().getColor(R.color.btn_color));
        btnSave.setBackgroundDrawable(shapeDrawable);*/

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColor(context.getResources().getColor(R.color.btn_color));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30 *scale + 0.5f);
        gradientDrawable1.setCornerRadius(dpAsPixels);
        btnSave.setBackground(gradientDrawable1);

//        Drawable drawable = (Drawable) btnSave.getBackground();
//        drawable.setCornerRadius(30);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (stringArrayList.size() > 0){
                    Intent intent = new Intent(HobbiesActivity.this, EditProfileNext.class);
                    intent.putExtra("list", stringArrayList.toString());
                    setResult(101, intent);
                    finish();
//                }else {
//                    Snackbar.make(parentLayout, "Please select atLeast one hobbies.", Snackbar.LENGTH_SHORT).show();
//                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    OnItemClickListener.OnItemClickCallback onItemClickListener = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_check);
            if (blogDTOArrayList.get(position).getStatus() != null && !blogDTOArrayList.get(position).getStatus().equals("")){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.uncheck_hob));
                blogDTOArrayList.get(position).setStatus("");
                stringArrayList.remove(blogDTOArrayList.get(position).getHobbies().substring(0,1).toUpperCase() + blogDTOArrayList.get(position).getHobbies().substring(1));
            }else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.check_hob));
                blogDTOArrayList.get(position).setStatus("hghegf");
                stringArrayList.add(blogDTOArrayList.get(position).getHobbies().substring(0,1).toUpperCase() + blogDTOArrayList.get(position).getHobbies().substring(1));
            }
        }
    };



    public void getHobbiesApi() {
        new OutLook().hideKeyboard(this);
        progressDialog = new ProgressDialog(HobbiesActivity.this,
                R.style.AppTheme_Dark_Dialog1);
        progressDialog.setIndeterminate(true);
        progressDialog = ProgressDialog.show(HobbiesActivity.this, null, null);
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Map<String, String> map = new HashMap<>();
        map.put("app_token", Constant.APPTOKEN);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.showHobbiesApi(map);
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

                        if(status.equals("1")){
                            blogDTOArrayList = new Gson().fromJson(jsonObject.optJSONArray("data").toString(), new TypeToken<ArrayList<BlogDTO>>(){}.getType());

                            if (!priviousHobbies.equals("")){
//                                    Log.i(getClass().getName(), " priviousHobbies >>>>"+priviousHobbies);
                                List<String> elephantList = Arrays.asList(priviousHobbies.split(", "));
//                                    Log.i(getClass().getName(), "Size >>>>"+elephantList.size());
                                for (int i = 0; i < blogDTOArrayList.size(); i++) {
//                                        Log.i(getClass().getName(), " match "+blogDTOArrayList.get(i).getHobbies());
                                    for (int j = 0; j < elephantList.size(); j++) {
//                                            Log.i(getClass().getName(), "inner match "+elephantList.get(j).toLowerCase());
                                        if (blogDTOArrayList.get(i).getHobbies().equals(elephantList.get(j).toLowerCase())){
                                            blogDTOArrayList.get(i).setStatus("fdghsfs");
//                                                Log.i(getClass().getName(), " match hobbies >>>>"+blogDTOArrayList.get(i).getHobbies());
                                            stringArrayList.add(blogDTOArrayList.get(i).getHobbies().substring(0,1).toUpperCase() + blogDTOArrayList.get(i).getHobbies().substring(1));
//                                                elephantList.remove(j);
                                            break;
                                        }
                                    }
                                }
                            }
                            rvHome.setAdapter(new BlogAdapter(context, blogDTOArrayList, onItemClickListener, true));
                        }
                        else {
                            Snackbar.make(parentLayout,message, Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(parentLayout, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}
