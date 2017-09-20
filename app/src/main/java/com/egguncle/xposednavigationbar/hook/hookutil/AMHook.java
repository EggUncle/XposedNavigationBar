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

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.egguncle.xposednavigationbar.constant.XpNavBarAction.ACTION_FORCE_STOP_AC;

/**
 * Created by egguncle on 17-8-26.
 * 对ActivityManager 进行hook 获取它的里面的mContext这个属性，
 * 然后调用forceStopPackage这个方法，
 */

public class AMHook {
    private final static String ACTIVITY_MANAGER = "android.app.ActivityManager";

    private static Context amContext;
    private static ActivityManager am;

    public static void hook(ClassLoader classLoader) throws Throwable {
        final Class<?> amClass = classLoader.loadClass(ACTIVITY_MANAGER);
        Field mContext = amClass.getDeclaredField("mContext");
        mContext.setAccessible(true);
        Method method = amClass.getDeclaredMethod("forceStopPackage", String.class);
        method.setAccessible(true);
        XpLog.i("try to hook ams success");
        XposedHelpers.findAndHookConstructor(amClass, Context.class, Handler.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XpLog.i("hook am success");

                amContext = (Context) param.args[0];
                am = (ActivityManager) param.thisObject;

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_FORCE_STOP_AC);
                BroadcastReceiver receiver = new BroadcastReceiver(){
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long beforeMem = getAvailabaleMemory();
                        ArrayList<String> pkgNames = intent.getStringArrayListExtra("data");
                        try {
                            for (String pkgName : pkgNames) {
                                XpLog.i("kill pkg : " + pkgName);
                                XposedHelpers.callMethod(am, "forceStopPackage", pkgName);
                            }
                        } catch (Exception e) {
                            XpLog.e(e);
                        }

                        long afterMen = getAvailabaleMemory();
                        long clearMem = afterMen - beforeMem;
                        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                        am.getMemoryInfo(mi);
                        long totalMem = mi.totalMem / (1024 * 1024);
                        XpLog.i("clear mem :" + clearMem + " MB");

                        Toast.makeText(context,"clear mem "+ clearMem+" "+afterMen+"/"+totalMem,Toast.LENGTH_SHORT).show();
                    }

                    //获取可用内存大小
                    private long getAvailabaleMemory() {
                        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                        am.getMemoryInfo(mi);
                        return mi.availMem / (1024 * 1024);

                    }
                };

                amContext.registerReceiver(receiver, intentFilter);
            }
        });
    }



}
