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

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by egguncle on 17-8-26.
 * 对ActivityManager 进行hook 获取它的里面的mContext这个属性，
 * 然后调用forceStopPackage这个方法，
 */

public class AMHook {
    private final static String ACTIVITY_MANAGER = "android.app.ActivityManager";
    public final static String ACTION_FORCE_STOP_AC = "com.egguncle.xpnavbar.forcestoppackage";

    private static Context amContext;
    private static ActivityManager am;

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final Class<?> amClass = lpparam.classLoader.loadClass(ACTIVITY_MANAGER);
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
                AMHook.AMHookReceiver receiver = new AMHookReceiver();

                amContext.registerReceiver(receiver, intentFilter);
            }
        });
    }

    //增加一个广播接收器用来接收到需要强制停止的app的包名
    private static class AMHookReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long beforeMem = getAvailabaleMemory(context);
            ArrayList<String> pkgNames = intent.getStringArrayListExtra("data");
            try {
                for (String pkgName : pkgNames) {
                    XpLog.i("kill pkg : " + pkgName);
                    XposedHelpers.callMethod(am, "forceStopPackage", pkgName);
                }
            } catch (Exception e) {
                XpLog.e(e);
            }

            long afterMen = getAvailabaleMemory(context);
            long clearMem = afterMen - beforeMem;
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long totalMem = mi.totalMem / (1024 * 1024);
            XpLog.i("clear mem :" + clearMem + " MB");

            Toast.makeText(context,"clear mem "+ clearMem+" "+afterMen+"/"+totalMem,Toast.LENGTH_SHORT).show();
        }

        //获取可用内存大小
        private long getAvailabaleMemory(Context context) {
            // 获取android当前可用内存大小
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.availMem / (1024 * 1024);
        }
    }

}
