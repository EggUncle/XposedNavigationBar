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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.egguncle.xposednavigationbar.hook.util.XpLog;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;

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
        final int[] theme = (int[]) internalThemeField.get(null);
        final int theme_colorPrimaryDark = internalColorPrimaryDarkField.getInt(null);

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
                Activity activity = (Activity) param.thisObject;
                TypedArray typedArray = activity.getTheme().obtainStyledAttributes(theme);
                int colorPrimaryDark = typedArray.getColor(theme_colorPrimaryDark, Color.TRANSPARENT);
                typedArray.recycle();
                if (colorPrimaryDark != Color.TRANSPARENT && colorPrimaryDark != Color.BLACK)
                    activity.getWindow().setNavigationBarColor(colorPrimaryDark);
            }
        });
    }

}
