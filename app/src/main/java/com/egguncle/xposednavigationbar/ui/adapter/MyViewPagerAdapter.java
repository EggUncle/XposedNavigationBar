/*
 * Create by EggUncle on 17-6-2 下午4:15
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-2 下午4:15
 */

package com.egguncle.xposednavigationbar.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by egguncle on 17-6-2.
 */

public class MyViewPagerAdapter extends PagerAdapter {
    List<View> viewList;

    public MyViewPagerAdapter(List<View> list){
        this.viewList=list;
    }


    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
}
