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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;


import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.HookUtil;
import com.egguncle.xposednavigationbar.model.Momo;
import com.egguncle.xposednavigationbar.util.SPUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private Switch swApp;
    private LinearLayout btnSettingBtns;
    private LinearLayout btnSettingOther;
    private LinearLayout btnAbout;
    private LinearLayout btnLanguage;
    private LinearLayout btnMomo;




    private String[] languages={"简体中文", "English"};

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
        btnLanguage = (LinearLayout) findViewById(R.id.btn_language);
        btnMomo = (LinearLayout) findViewById(R.id.btn_momo);
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
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.about_language))
                        .setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "onClick: "+languages[which]);
                                Resources resources = getResources();
                                DisplayMetrics dm = resources.getDisplayMetrics();
                                Configuration config = resources.getConfiguration();
                                if (languages[which].equals("English")) {
                                    SPUtil.getInstance(MainActivity.this).setLanguage(SPUtil.LANGUAGE_ENGLICH);
                                    config.setLocale(Locale.ENGLISH);
                                } else {
                                    SPUtil.getInstance(MainActivity.this).setLanguage(SPUtil.LANGUAGE_CHINESE);
                                    config.setLocale(Locale.SIMPLIFIED_CHINESE);
                                }
                                resources.updateConfiguration(config, dm);
                                dialog.dismiss();
                                Intent it = new Intent(MainActivity.this, MainActivity.class);
                                //清空任务栈确保当前打开activit为前台任务栈栈顶
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(it);
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        });

        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MomoActivity.class));
            }
        });
    }


}
