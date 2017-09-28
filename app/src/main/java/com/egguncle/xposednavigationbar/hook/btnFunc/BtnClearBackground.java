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

package com.egguncle.xposednavigationbar.hook.btnFunc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.egguncle.xposednavigationbar.hook.hookFunc.ClearBackground;
import com.egguncle.xposednavigationbar.hook.hookutil.DataHook;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.util.ArrayList;
import java.util.List;

import static com.egguncle.xposednavigationbar.constant.XpNavBarAction.ACTION_FORCE_STOP_AC;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnClearBackground extends ClearBackground {

    @Override
    protected void clearBackground(Context context) {
        XpLog.i("clear mem level is " + DataHook.clearMenLevel);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<String> pkgNames = new ArrayList<>();
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        if (processInfoList != null && processInfoList.size() != 0) {
            for (int i = 0; i < processInfoList.size(); i++) {
                ActivityManager.RunningAppProcessInfo processInfo = processInfoList.get(i);
                if (processInfo.importance > DataHook.clearMenLevel) {
                    String[] pkgList = processInfo.pkgList;
                    for (String pkgName : pkgList) {
                        if (pkgName.contains("com.android")) {
                            continue;
                        }
                        pkgNames.add(pkgName);
                    }
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_FORCE_STOP_AC);
        intent.putStringArrayListExtra("data", pkgNames);
        context.sendBroadcast(intent);
        XpLog.i("has send pkgnames to kill");
    }

}
