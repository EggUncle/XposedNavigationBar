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

package com.egguncle.xposednavigationbar.hook.util;

import android.view.WindowManager;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by egguncle on 17-8-14.
 */

public class PhoneSatatusBarHook {
    //用于获取phonestatusbar对象和clearAllNotifications方法等
    private static Object phoneStatusBar;
    private static Method clearAllNotificationsMethod;
    private static WindowManager windowManager;

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //获取清除通知的方法
        Class<?> phoneStatusBarClass =
                lpparam.classLoader.loadClass("com.android.systemui.statusbar.phone.PhoneStatusBar");
        clearAllNotificationsMethod = phoneStatusBarClass.getDeclaredMethod("clearAllNotifications");
        clearAllNotificationsMethod.setAccessible(true);
        XposedHelpers.findAndHookMethod(phoneStatusBarClass,
                "start", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //在这里获取到PhoneStatusBar对象
                        phoneStatusBar = param.thisObject;
                        //获取到windowmanager
                        windowManager = (WindowManager) XposedHelpers.getObjectField(phoneStatusBar, "mWindowManager");
                    }
                });
    }

    public static Object getPhoneStatusBar() {
        return phoneStatusBar;
    }

    public static Method getClearAllNotificationsMethod() {
        return clearAllNotificationsMethod;
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }
}
