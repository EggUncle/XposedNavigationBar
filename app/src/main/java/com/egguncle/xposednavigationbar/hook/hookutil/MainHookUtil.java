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


import android.content.res.XModuleResources;

import com.egguncle.xposednavigationbar.BuildConfig;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.util.XpLog;
import com.egguncle.xposednavigationbar.ui.activity.HomeActivity;
import com.egguncle.xposednavigationbar.util.SPUtil;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by egguncle on 17-6-1.
 * <p>
 * 一个hook模块，为了在android设备的底部导航栏虚拟按键上实现功能扩展
 */

public class MainHookUtil implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    private final static String TAG = "MainHookUtil";
    private final static String SYSTEM_UI = "com.android.systemui";
    private final static String ANDROID = "android";
    private static String MODULE_PATH;


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        DataHook.init(startupParam);

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //  XpLog.i(lpparam.packageName);
        XSharedPreferences pre = new XSharedPreferences(BuildConfig.APPLICATION_ID, SPUtil.SP_NAME);
        boolean chameleonNavbar = pre.getBoolean(SPUtil.CHAMELEON_NAVBAR, false);
        if (chameleonNavbar) {
            XpLog.i("hook phone window");
            PhoneWindowHook.hook(lpparam.classLoader);
        }

        switch (lpparam.packageName) {
            case ANDROID:
                try {
                    AMHook.hook(lpparam.classLoader);
                    PhoneWindowManagerHook.hook(lpparam);
                    PointerEventDispatcherHook.hook(lpparam.classLoader);
                } catch (Exception e) {
                    XpLog.e(e);
                }
                break;
            case SYSTEM_UI:
                try {
                    PhoneSatatusBarHook.hook(lpparam.classLoader);
                    NavBarHook.hook(lpparam.classLoader);
                } catch (Exception e) {
                    XpLog.e(e);
                }
                break;
//            case BuildConfig.APPLICATION_ID:
//                try {
//                    XposedHelpers.findAndHookMethod(HomeActivity.class.getName(), lpparam.classLoader,
//                            "getActivatedVersion", XC_MethodReplacement.returnConstant(BuildConfig.VERSION_CODE));
//                } catch (Exception e) {
//                    XpLog.e(e);
//                }
//                break;
        }
    }

    //获取drawable等资源的方法，Lineage OS无效
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {

    }

}

