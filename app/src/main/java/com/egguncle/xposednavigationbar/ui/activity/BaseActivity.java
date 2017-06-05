/*
 * Create by EggUncle on 17-6-5 下午3:14
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-5 下午3:09
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by egguncle on 17-6-4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initView();
        initVar();
        initAction();
    }

    abstract int getLayoutId();
    abstract void initView();
    abstract void initVar();
    abstract void initAction();
}
