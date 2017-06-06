/*
 * Create by EggUncle on 17-6-5 下午3:14
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-5 下午3:14
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.egguncle.xposednavigationbar.R;


public class MainActivity extends BaseActivity {
    private final static String TAG="MainActivity";
    private Button btnTest;
    private ImageView img;




    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void initView() {
        btnTest = (Button) findViewById(R.id.btn_test);
        img = (ImageView) findViewById(R.id.img);


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
//                Intent intent=new Intent("com.egguncle.xposednavigationbar.QuickNotificationActivity");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                Intent intent = new Intent("com.egguncle.xposednavigationbar.BlackActivity");
                //使用这种启动标签，可以避免在打开软件本身以后再通过快捷键呼出activity时仍然显示软件的界面的bug
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                View v = getWindow().getDecorView();
//                v.setDrawingCacheEnabled(true);
//                v.buildDrawingCache();
//
//                Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//                v.setDrawingCacheEnabled(false);
//                v.destroyDrawingCache();

             //   img.setImageBitmap(bitmap);
            }
        });
    }
}
