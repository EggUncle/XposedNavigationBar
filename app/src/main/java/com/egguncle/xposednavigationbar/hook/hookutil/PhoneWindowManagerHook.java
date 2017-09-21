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
import android.content.res.Resources;
import android.os.Build;
import android.view.WindowManager;

import com.egguncle.xposednavigationbar.BuildConfig;
import com.egguncle.xposednavigationbar.constant.ConstantStr;
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
    public static GesturesListener gesturesListener;

    public static int screenH;
    public static int navbarH;

    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
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
                final Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                screenH = wm.getDefaultDisplay().getHeight();
                Resources resources = mContext.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                navbarH = resources.getDimensionPixelSize(resourceId);
                if (navbarH == 0) {
                    navbarH = 200;
                }

                BroadcastReceiver screenShotReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            int type = intent.getIntExtra(ConstantStr.TYPE, -1);
                            switch (type) {
                                case ConstantStr.TAKE_SCREENSHOT:
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                        XposedHelpers.callMethod(param.thisObject, "takeScreenshot", 1);
                                    } else {
                                        XposedHelpers.callMethod(param.thisObject, "takeScreenshot");
                                    }
                                    break;
                                case ConstantStr.HIDE_NAVBAR: {
                                    setNavBarDimensions(param.thisObject, 0);
                                }
                                break;
//                                case ConstantStr.SHOW_NAVBAR: {
//                                   setNavBarDimensions(param.thisObject,navbarH);
//                                }
                            }
                        } catch (Exception e) {
                            XpLog.e(e);
                        }

                    }
                };
                IntentFilter filter = new IntentFilter(XpNavBarAction.ACTION_PHONE_WINDOW_MANAGER);
                mContext.registerReceiver(screenShotReceiver, filter);

                gesturesListener = new GesturesListener(mContext, new GesturesListener.Callbacks() {
                    @Override
                    public void onSwipeFromTop() {

                    }

                    @Override
                    public void onSwipeFromBottom() {
                        setNavBarDimensions(param.thisObject, navbarH);
                    }

                    @Override
                    public void onSwipeFromRight() {

                    }

                    @Override
                    public void onDebug() {

                    }
                });
            }
        });
    }

    public static void setNavBarDimensions(Object sPhoneWindowManager, int hp) {
        int[] navigationBarHeightForRotation;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            navigationBarHeightForRotation = (int[]) XposedHelpers.getObjectField(
                    sPhoneWindowManager, "mNavigationBarHeightForRotation");
        } else {
            navigationBarHeightForRotation = (int[]) XposedHelpers.getObjectField(
                    sPhoneWindowManager, "mNavigationBarHeightForRotationDefault");
        }

        final int portraitRotation = XposedHelpers.getIntField(sPhoneWindowManager, "mPortraitRotation");
        final int upsideDownRotation = XposedHelpers.getIntField(sPhoneWindowManager, "mUpsideDownRotation");
        if (navigationBarHeightForRotation[portraitRotation] == hp)
            return;

        navigationBarHeightForRotation[portraitRotation] =
                navigationBarHeightForRotation[upsideDownRotation] =
                        hp;
        XposedHelpers.callMethod(sPhoneWindowManager, "updateRotation", false);
    }

}
