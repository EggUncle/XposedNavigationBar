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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;


import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-9-8.
 */

public class PhoneWindowHook {

    private final static String PHONE_WINDOW_M = "com.android.internal.policy.PhoneWindow";
    private final static String PHONE_WINDOW = "com.android.internal.policy.impl.PhoneWindow";

    public static void hook(ClassLoader loader) throws Throwable {
        String pwClassPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pwClassPath = PHONE_WINDOW_M;
        } else {
            pwClassPath = PHONE_WINDOW;
        }
        Class<?> pwClass = loader.loadClass(pwClassPath);
        XposedHelpers.findAndHookMethod(pwClass, "setStatusBarColor", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int color = Integer.valueOf(param.args[0].toString());
                ((Window) param.thisObject).setNavigationBarColor(color);
            }
        });

        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Activity activity = (Activity) param.thisObject;
                    Rect rect = new Rect();
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                    Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();

                    int color = bmp.getPixel(rect.top + 3, 1);
                    int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
                    Resources resources = activity.getResources();
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    int navbarHeight = resources.getDimensionPixelSize(resourceId);
                    int color2 = bmp.getPixel(screenHeight - navbarHeight - 3, 1);

                    activity.getWindow().setStatusBarColor(color);
                    activity.getWindow().setNavigationBarColor(color2);
                } catch (Exception e) {
                    XpLog.e(e);
                }
            }
        });
    }

}
