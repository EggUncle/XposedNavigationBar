/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2018 egguncle cicadashadow@gmail.com
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

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by songyucheng on 2018/1/18.
 */

public class MoKeeUtil {

    public static void hook(ClassLoader classLoader) throws Throwable {
        try {
            final Class<?> slideTouchEvent =
                    classLoader.loadClass("com.android.systemui.singlehandmode.SlideTouchEvent");
            XposedHelpers.findAndHookMethod(slideTouchEvent, "isSupportSingleHand", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });
        } catch (Exception e) {

        }
    }

}
