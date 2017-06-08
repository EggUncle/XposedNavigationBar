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
        try {
            //酷安有位用红米note4x的朋友出现了一些问题，报错点可能在这里
            Toast.makeText(this, "clear " + clearMem + "m " + afterMen + "/" + totalMem + "m", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
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
