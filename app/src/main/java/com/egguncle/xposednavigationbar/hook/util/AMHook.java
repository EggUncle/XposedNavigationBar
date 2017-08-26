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

import android.content.Context;

import java.lang.reflect.Field;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by egguncle on 17-8-26.
 * 对ActivityManager 进行hook 获取它的里面的mContext这个属性，
 * 然后调用forceStopPackage这个方法，
 */

public class AMHook {

    private final static String ACTIVITY_MANAGER="android.app.ActivityManager";
    private final static String ACTIVITY_MANAGER_SERVICE="com.android.server.am.ActivityManagerService";

    private static Context amContext;

    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> activityManagerClass=lpparam.classLoader.loadClass(ACTIVITY_MANAGER);
        Field mContext=activityManagerClass.getDeclaredField("mContext");
        mContext.setAccessible(true);
    }

}
