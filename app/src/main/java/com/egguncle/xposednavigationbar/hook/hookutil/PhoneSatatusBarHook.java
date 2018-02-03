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
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-8-14.
 */

public class PhoneSatatusBarHook {

    private final static String PHONE_STATUS_BRA_CLASS = "com.android.systemui.statusbar.phone.PhoneStatusBar";
    private final static String PHONE_STATUS_BRA_CLASS_OREO = "com.android.systemui.statusbar.phone.StatusBar";

    public static void hook(ClassLoader classLoader) throws Throwable {
        String psbClassPath;
        if (Build.VERSION.SDK_INT >= 26) {
            psbClassPath = PHONE_STATUS_BRA_CLASS_OREO;
        } else {
            psbClassPath = PHONE_STATUS_BRA_CLASS;
        }

        //获取清除通知的方法
        Class<?> phoneStatusBarClass =
                classLoader.loadClass(psbClassPath);
        XposedHelpers.findAndHookMethod(phoneStatusBarClass,
                "start", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        BroadcastReceiver screenShotReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                int type = intent.getIntExtra(ConstantStr.TYPE, -1);
                                switch (type) {
                                    case ConstantStr.CLEAR_NOTIFICATIONS:
                                        XposedHelpers.callMethod(param.thisObject, "clearAllNotifications");
                                        break;
                                    case ConstantStr.RECENT_TASKS:
                                        //这个地方会导致8.0软重启，暂时这样粗糙的处理一下，慢慢适配
                                        try {
                                            XposedHelpers.callMethod(param.thisObject, "toggleRecentApps");
                                        } catch (Exception e) {

                                        }
                                        break;
                                }

                            }
                        };
                        IntentFilter filter = new IntentFilter(XpNavBarAction.ACTION_PHONE_STATUSBAR);
                        mContext.registerReceiver(screenShotReceiver, filter);
                    }
                });

    }
}
