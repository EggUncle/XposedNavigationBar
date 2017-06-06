/*
 * Create by EggUncle on 17-6-6 上午10:54
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-6 上午10:54
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.R;

import java.util.List;

import de.robv.android.xposed.XposedBridge;


public class ClearMemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_mem);
        //状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        long beforeMem = getAvailabaleMemory(this);
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        if (processInfoList != null && processInfoList.size() != 0) {
            for (int i = 0; i < processInfoList.size(); i++) {
                ActivityManager.RunningAppProcessInfo processInfo = processInfoList.get(i);
                if (processInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = processInfo.pkgList;
                    for (String pkgName : pkgList) {
                        am.killBackgroundProcesses(pkgName);
                    }
                }
            }
        }
        long afterMen = getAvailabaleMemory(this);
        long clearMem = afterMen - beforeMem;
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long totalMem = mi.totalMem / (1024 * 1024);
        Toast.makeText(this, "clear " + clearMem + "m " + afterMen + "/" + totalMem+"m", Toast.LENGTH_SHORT).show();

        finish();
    }

    //获取可用内存大小
    private long getAvailabaleMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem / (1024 * 1024);
    }
}
