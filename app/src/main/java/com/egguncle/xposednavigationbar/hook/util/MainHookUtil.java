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


import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;

import static de.robv.android.xposed.XposedBridge.log;


/**
 * Created by egguncle on 17-6-1.
 * <p>
 * 一个hook模块，为了在android设备的底部导航栏虚拟按键上实现功能扩展
 */

public class MainHookUtil implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private final static String TAG = "MainHookUtil";

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
       DataHook.init(startupParam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //       XposedBridge.log(lpparam.packageName);
        //过滤包名
        if (lpparam.packageName.equals("com.android.systemui")) {
            XposedBridge.log("filter package systemui");
            PhoneSatatusBarHook.hook(lpparam);
            NavBarHook.hook(lpparam);
        }
    }







}

