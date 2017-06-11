/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;


import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.util.SPUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private Switch swApp;
    private LinearLayout btnSettingBtns;
    private LinearLayout btnSettingOther;
    private LinearLayout btnAbout;


    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void initView() {
        swApp = (Switch) findViewById(R.id.sw_app);
    }

    @Override
    void initVar() {
        boolean act = SPUtil.getInstance(this).getActivation();
        swApp = (Switch) findViewById(R.id.sw_app);
        swApp.setChecked(act);
        btnSettingBtns = (LinearLayout) findViewById(R.id.btn_setting_btns);
        btnSettingOther = (LinearLayout) findViewById(R.id.btn_setting_other);
        btnAbout = (LinearLayout) findViewById(R.id.btn_about);

    }

    @Override
    void initAction() {
        swApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtil.getInstance(MainActivity.this).setActivation(b);
            }
        });
        btnSettingBtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SetFunActivity.class);
                startActivity(intent);
            }
        });
        btnSettingOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OtherSettingActivity.class));
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });


       // startActivity(new Intent(MainActivity.this,AppShortCutActivity.class));
    }

}
