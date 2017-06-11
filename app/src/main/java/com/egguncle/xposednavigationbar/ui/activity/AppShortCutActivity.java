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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.AppInfo;
import com.egguncle.xposednavigationbar.ui.adapter.AppActAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个activity用来当作快速启动app或者app快捷阿方式的按钮
 */
public class AppShortCutActivity extends Activity {
    private ImageView ivAdd;
    private ImageView ivClose;
    private RecyclerView rcvApp;

    //表格布局一行的数量
    private final static int SPAN_COUNT = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_short_cut);
        initView();
        initVar();
        initAction();
    }


    private void initView() {
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        rcvApp = (RecyclerView) findViewById(R.id.rcv_app);
        rcvApp.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
    }

    private void initVar() {
        List<AppInfo> appInfos = new ArrayList<>();
        AppActAdapter appActAdapter = new AppActAdapter(this, appInfos);
        rcvApp.setAdapter(appActAdapter);

        appInfos.addAll(loadAppWithoutSystemApp());
        appInfos.addAll(loadAppShortCut());
    }

    private void initAction() {

    }

    /**
     * 加载系统应用以外的app
     *
     * @return
     */
    private List<AppInfo> loadAppWithoutSystemApp() {
        List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfoList) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //非系统应用
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                appInfo.setPackgeName(packageInfo.packageName);
                appInfo.setType(AppInfo.TYPE_APP);
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    private List<AppInfo> loadAppShortCut() {
        //获取到所有快捷方式
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        List<ResolveInfo> shortcuts = getPackageManager().queryIntentActivities(
                shortcutsIntent, 0);
        List<AppInfo> appInfoList = new ArrayList<>();

        PackageManager pm = getPackageManager();
        for (ResolveInfo resolveInfo : shortcuts) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            String pkgName = activityInfo.packageName;
            String shortName = activityInfo.name;

            int flag = activityInfo.flags;
            String label = activityInfo.loadLabel(pm).toString();

            AppInfo appInfo = new AppInfo();
            appInfo.setLabel(label);
            appInfo.setPackgeName(pkgName);
            appInfo.setShortCutName(shortName);
            appInfo.setFlag(flag);
            appInfo.setType(AppInfo.TYPE_SHORT_CUT);
            appInfoList.add(appInfo);
        }

        return appInfoList;
    }
}
