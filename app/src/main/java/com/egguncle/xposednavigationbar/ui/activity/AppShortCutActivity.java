/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle cicadashadow@gmail.com
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.model.AppInfo;
import com.egguncle.xposednavigationbar.ui.adapter.AppActAdapter;
import com.egguncle.xposednavigationbar.ui.adapter.DialogItemAdapter;
import com.egguncle.xposednavigationbar.ui.touchHelper.MyItemTouchHelpCallBack;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 这个activity用来当作快速启动app或者app快捷阿方式的按钮
 */
public class AppShortCutActivity extends Activity implements View.OnClickListener {
    private ImageButton ivRemove;
    private ImageButton ivAdd;
    private ImageButton ivClose;
    private RecyclerView rcvApp;

    private RelativeLayout shortCutPanel;
    private CloseReceiver receiver;

    private final static String TAG = "AppShortCutActivity";

    //显示被选择的快捷方式
    private List<AppInfo> selectAppInfos;
    private AppActAdapter appActAdapter;

    //被删除的列表
    private List<AppInfo> deleteAppInfos;

    //表格布局一行的数量
    private final static int SPAN_COUNT = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_app_short_cut);
        initView();
        initVar();
        initAction();
    }


    private void initView() {
        //状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        shortCutPanel = (RelativeLayout) findViewById(R.id.short_cut_panel);
        ivRemove = (ImageButton) findViewById(R.id.iv_remove);
        ivAdd = (ImageButton) findViewById(R.id.iv_add);
        ivClose = (ImageButton) findViewById(R.id.iv_close);
        rcvApp = (RecyclerView) findViewById(R.id.rcv_app);
        rcvApp.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

    }

    private void initVar() {
        deleteAppInfos = new ArrayList<>();
        selectAppInfos = new ArrayList<>();
        selectAppInfos.addAll(DataSupport.findAll(AppInfo.class));
        appActAdapter = new AppActAdapter(this, selectAppInfos);
        rcvApp.setAdapter(appActAdapter);

        //设置rcv可拖动
//        MyItemTouchHelper myItemTouchHelper = new MyItemTouchHelper(onItemTouchCallbackListener);
//        myItemTouchHelper.attachToRecyclerView(rcvApp);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(XpNavBarAction.ACT_CLOSE_ACT_PANEL);
        receiver=new CloseReceiver();
        registerReceiver(receiver,intentFilter);
    }

    private void initAction() {
        ivClose.setOnClickListener(this);
        //添加新的快捷方式
        ivAdd.setOnClickListener(this);
        ivRemove.setOnClickListener(this);

        //设置面板的回调，当滑动使其隐藏时，finsh这个activity
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(shortCutPanel);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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

    private MyItemTouchHelpCallBack.OnItemTouchCallbackListener onItemTouchCallbackListener = new MyItemTouchHelpCallBack.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            // 滑动删除的时候，从数据源移除，并刷新这个Item。
            if (selectAppInfos != null) {
                selectAppInfos.remove(adapterPosition);
                appActAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (selectAppInfos != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(selectAppInfos, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                appActAdapter.notifyItemMoved(srcPosition, targetPosition);
                Log.i(TAG, "onMove: ---");
                return true;
            }
            return false;
        }
    };


    //在destory中做最后对应的保存
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: update all");
        for (AppInfo appInfo : selectAppInfos) {
            appInfo.save();
            Log.i(TAG, "onDestroy: update" + appInfo.getLabel());
        }
        for (AppInfo appInfo : deleteAppInfos) {
            appInfo.delete();
            Log.i(TAG, "onDestroy: delete" + appInfo.getLabel());
        }
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close: {
                finish();
            }
            break;
            case R.id.iv_add: {
                ivAdd.setClickable(false);
                //快捷方式列表
                List<AppInfo> appInfos = new ArrayList<>();
                View dialogView = View.inflate(view.getContext(), R.layout.d_apps, null);
                RecyclerView rcvDialogApps = (RecyclerView) dialogView.findViewById(R.id.rcv_dialog_apps);
                rcvDialogApps.setLayoutManager(new LinearLayoutManager(view.getContext()));
                final DialogItemAdapter adapter = new DialogItemAdapter(view.getContext(), appInfos);
                rcvDialogApps.setAdapter(adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getResources().getString(R.string.select_apps))
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //不添加重复的
                                for (AppInfo appInfo : adapter.getSelectedData()) {
                                    if (!selectAppInfos.contains(appInfo)) {
                                        //    selectAppInfos.addAll(adapter.getSelectedData());
                                        selectAppInfos.add(appInfo);
                                    }
                                }

                                appActAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .create().show();

                //去除已经选中的app
                List<AppInfo> appDataWithoutSystem = loadAppWithoutSystemApp();
                appDataWithoutSystem.removeAll(selectAppInfos);
                appInfos.addAll(appDataWithoutSystem);
                List<AppInfo> notSelectShort = loadAppShortCut();
                notSelectShort.removeAll(selectAppInfos);
                appInfos.addAll(notSelectShort);

                adapter.notifyDataSetChanged();
                ivAdd.setClickable(true);
            }
            break;
            case R.id.iv_remove: {
                ivRemove.setClickable(false);
                //快捷方式列表
                List<AppInfo> appInfos = new ArrayList<>();
                View dialogView = View.inflate(view.getContext(), R.layout.d_apps, null);
                RecyclerView rcvDialogApps = (RecyclerView) dialogView.findViewById(R.id.rcv_dialog_apps);
                rcvDialogApps.setLayoutManager(new LinearLayoutManager(view.getContext()));
                final DialogItemAdapter adapter = new DialogItemAdapter(view.getContext(), appInfos);
                rcvDialogApps.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getResources().getString(R.string.delect_apps))
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //不添加重复的
                                for (AppInfo appInfo : adapter.getSelectedData()) {
                                    Log.i(TAG, "onClick: " + appInfo.getLabel());
                                    deleteAppInfos.add(appInfo);
                                    selectAppInfos.remove(appInfo);

                                }
                                appActAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .create().show();
                appInfos.addAll(selectAppInfos);
                adapter.notifyDataSetChanged();
                ivRemove.setClickable(true);
            }
            break;
        }
    }

    private static class CloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

        }
    }
}
