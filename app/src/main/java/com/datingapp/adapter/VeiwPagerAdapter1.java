package com.datingapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VeiwPagerAdapter1 extends ViewPager implements ViewPager.PageTransformer {

    public VeiwPagerAdapter1(Context context) {
        super(context);
    }

    public VeiwPagerAdapter1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transformPage(View page, float position) {
        float absPosition = Math.abs(position);

        if (absPosition >= 1) {
            page.setElevation(5);
            page.setScaleY(3);
        } else {
            // This will be during transformation
            page.setElevation(((1 - absPosition) * 2 + 5));
            page.setScaleY((3 - 1) * absPosition + 1);
        }
    }
}