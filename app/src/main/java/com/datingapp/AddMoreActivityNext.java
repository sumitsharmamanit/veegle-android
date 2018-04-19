package com.datingapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AddMoreActivityNext extends AppCompatActivity {

    private Button btnInfo, btnClear, btnContinue;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_next);

        initView();
    }

    private void initView() {
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnInfo = (Button) findViewById(R.id.btn_info);

        btnContinue = (Button) findViewById(R.id.btn_continue);
        ivBack = (ImageView) findViewById(R.id.iv_back);

//        GradientDrawable drawable = (GradientDrawable) btnInfo.getBackground();
//        drawable.setColor(getResources().getColor(R.color.backgroudncolor));
//        float values[] = {5f, 0f, 0f, 0f};
//        drawable.setCornerRadii(values);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getResources().getColor(R.color.backgroudncolor));
//        shape.setStroke(3, Color.YELLOW);
        shape.setCornerRadii(new float[] { 5, 0, 0, 0, 0, 0, 0, 0 });
        btnInfo.setBackgroundDrawable(shape);


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddMoreActivityNext.this, ProfilePhoto.class));
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
