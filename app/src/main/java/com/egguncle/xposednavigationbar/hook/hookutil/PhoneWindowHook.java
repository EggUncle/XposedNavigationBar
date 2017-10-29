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
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;


import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-9-8.
 */

public class PhoneWindowHook {

    private final static String PHONE_WINDOW_M = "com.android.internal.policy.PhoneWindow";
    private final static String PHONE_WINDOW = "com.android.internal.policy.impl.PhoneWindow";
    private final static String CLASS_SYTLE = "com.android.internal.R.styleable";

    public static void hook(ClassLoader loader) throws Throwable {
        String pwClassPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pwClassPath = PHONE_WINDOW_M;
        } else {
            pwClassPath = PHONE_WINDOW;
        }
        Class<?> internalStyleable = XposedHelpers.findClass(CLASS_SYTLE, loader);
        Field internalThemeField = XposedHelpers.findField(internalStyleable, "Theme");
        Field internalColorPrimaryDarkField = XposedHelpers.findField(internalStyleable, "Theme_colorPrimaryDark");
        Field internalColorPrimaryField=XposedHelpers.findField(internalStyleable,"Theme_colorPrimary");
        final int[] theme = (int[]) internalThemeField.get(null);
        final int themeColorPrimaryDark = internalColorPrimaryDarkField.getInt(null);
        final int themeColorPrimary=internalColorPrimaryField.getInt(null);

        Class<?> pwClass = loader.loadClass(pwClassPath);
        XposedHelpers.findAndHookMethod(pwClass, "setStatusBarColor", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int color = Integer.valueOf(param.args[0].toString());
                //((Window) param.thisObject).setNavigationBarColor(color);
            }
        });

        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                TypedArray typedArray = activity.getTheme().obtainStyledAttributes(theme);
                int colorPrimaryDark = typedArray.getColor(themeColorPrimaryDark, Color.TRANSPARENT);
                int colorPrimary=typedArray.getColor(themeColorPrimary,Color.TRANSPARENT);
                typedArray.recycle();
                if (colorPrimary != Color.TRANSPARENT && colorPrimary != Color.BLACK) {
                    activity.getWindow().setNavigationBarColor(colorPrimaryDark);
                   // activity.getWindow().setStatusBarColor(colorPrimary);
                }


//                Activity activity = (Activity) param.thisObject;
//                View decorView = activity.getWindow().getDecorView();
//                int color2 = decorView.getDrawingCacheBackgroundColor();
//                activity.getWindow().setNavigationBarColor(color2);
            }
        });

//        XposedHelpers.findAndHookMethod(pwClass, "setNavigationBarColor", int.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                int color = (int) param.args[0];
//                int red = Color.red(color);
//                int green = Color.green(color);
//                int blue = Color.blue(color);
//                XpLog.i("color is " + color);
//                XpLog.i("red is " + red);
//                XpLog.i("green is " + green);
//                XpLog.i("blue is " + blue);
//                //GRAY = 0.30 RED + 0.59 GREEN + 0.11 BLUE
//                //阀值100   小于100白色大于100灰色
//                //灰度0.7
//                float gray = (float) (0.3 * red + 0.59 * green + 0.11 * blue);
//                XpLog.i("gray is " + gray);
//                if (gray > 100) {
//                    XpLog.i("gray is bigger than 100");
//                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
//                    Intent intent = new Intent(XpNavBarAction.ACT_NAV_BAR_COLOR);
//                    intent.putExtra("color", true);
//                    context.sendBroadcast(intent);
//                } else {
//                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
//                    Intent intent = new Intent(XpNavBarAction.ACT_NAV_BAR_COLOR);
//                    intent.putExtra("color", false);
//                    context.sendBroadcast(intent);
//                }
//            }
//        });
    }
}
