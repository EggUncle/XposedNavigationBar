/*
 * Create by EggUncle on 17-6-5 下午3:14
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-5 下午3:14
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.egguncle.xposednavigationbar.R;


public class MainActivity extends BaseActivity {
    private final static String TAG="MainActivity";
    private Button btnTest;


    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void initView() {
        btnTest = (Button) findViewById(R.id.btn_test);

    }

    @Override
    void initVar() {


    }

    @Override
    void initAction() {
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                Intent intent=new Intent("com.egguncle.xposednavigationbar.QuickNotificationActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
