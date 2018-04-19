package com.datingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

//import com.datingapp.adapter.GooglePlacesAutocompleteAdapter;
import com.datingapp.util.Constant;
import com.datingapp.util.OutLook;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.Places;

public class LocationActivity extends AppCompatActivity {

    protected GoogleApiClient mGoogleApiClient;
    private LinearLayout backBtn;
    private ListView cityList;
    private EditText edEnterLocation;
//    GooglePlacesAutocompleteAdapter dataAdapter;
    private RelativeLayout parentLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initView();
        ClickListener();
    }

    private void initView(){

        context         = this;
        parentLayout    = (RelativeLayout) findViewById(R.id.parentLayout);
        edEnterLocation = (EditText) findViewById(R.id.edEnterLocation);
        backBtn         = (LinearLayout) findViewById(R.id.backBtn);
        cityList        = findViewById(R.id.cityList);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Places.GEO_DATA_API)
                .build();

//        dataAdapter = new GooglePlacesAutocompleteAdapter(LocationActivity.this, R.layout.autocomplete_list_item);
//        cityList.setAdapter(dataAdapter);

        edEnterLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
//                dataAdapter.getFilter().filter(s.toString());
            }
        });

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new OutLook().hideKeyboard(LocationActivity.this);
                OutLook.ADDRESS = cityList.getItemAtPosition(position).toString();
                edEnterLocation.setText(cityList.getItemAtPosition(position).toString());
                startActivity(new Intent(LocationActivity.this , AfterLocationSelect.class));
            }
        });

    }

    public void ClickListener(){

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
