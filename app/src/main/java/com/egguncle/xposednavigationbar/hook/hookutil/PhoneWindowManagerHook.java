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

package com.egguncle.xposednavigationbar.hook.hookutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by egguncle on 17-9-15.
 */

public class PhoneWindowManagerHook {

    private final static String PHONE_WINDOW_MANAGER_M = "com.android.server.policy.PhoneWindowManager";
    private final static String PHONE_WINDOW_MANAGER_L = "com.android.internal.policy.impl.PhoneWindowManager";

    public static void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String pwmClassPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pwmClassPath = PHONE_WINDOW_MANAGER_M;
        } else {
            pwmClassPath = PHONE_WINDOW_MANAGER_L;
        }

        Class<?> pwmClass = loadPackageParam.classLoader.loadClass(pwmClassPath);
        XposedBridge.hookAllMethods(pwmClass, "init", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                BroadcastReceiver screenShotReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                XposedHelpers.callMethod(param.thisObject, "takeScreenshot", 1);
                            } else {
                                XposedHelpers.callMethod(param.thisObject, "takeScreenshot");
                            }
                        } catch (Exception e) {
                            XpLog.e(e);
                        }

                    }
                };
                IntentFilter filter = new IntentFilter(XpNavBarAction.ACTION_SCREENSHOT);
                mContext.registerReceiver(screenShotReceiver, filter);
            }
        });
    }

}
