/*
 * Create by EggUncle on 17-6-3 下午7:10
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-3 下午7:10
 */

package com.egguncle.xposednavigationbar.ui.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by egguncle on 17-6-3.
 * 自定义的viewpager，用于导航栏底部
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(Context context) {
        this(context,null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x=(int)ev.getX();
        int y= (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{

            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
