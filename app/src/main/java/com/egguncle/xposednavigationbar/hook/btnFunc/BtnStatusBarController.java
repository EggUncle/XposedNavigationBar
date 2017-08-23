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

package com.egguncle.xposednavigationbar.hook.btnFunc;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.StatusBarController;
import com.egguncle.xposednavigationbar.hook.util.MainHookUtil;
import com.egguncle.xposednavigationbar.hook.util.NavBarHook;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnStatusBarController implements StatusBarController, View.OnClickListener {
    private static boolean statusExpand = false;
    public final static int FOR_EXPAND = 0;
    public final static int FOR_NOTIFICATION = 1;
    private int mType;

    public BtnStatusBarController() {
    }

    @Override
    public void onClick(View view) {
        if (statusExpand) {
            collapseStatusBar(view.getContext());
            statusExpand = false;
        } else {
            expandAllStatusBar(view.getContext());
            statusExpand = true;
        }
    }

    @Override
    public void expandAllStatusBar(Context context) {
        if (NavBarHook.isExpandStatusBarWithRoot()) {
            //如果在6.0环境下，尝试申请root权限来解决通知栏展开缓慢的问题
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && requestRoot()) {
                //申请成功后会模拟手势进行下拉
                //如果失败了，就按照普通的方式下拉
                //  expandAllStatusBarWithOutRoot(context);
            } else {
                expandAllStatusBarWithOutRoot(context);
            }
        }else{
            expandAllStatusBarWithOutRoot(context);
        }
    }

    @Override
    public void expandAllStatusBarWithOutRoot(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method expand;
//            if (Build.VERSION.SDK_INT <= 16) {
//              由于支持的系统版本为5.0～6.0,所以不对版本做适配
//            } else {
            expand = statusBarManager.getClass().getMethod("expandSettingsPanel");
            expand.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    public void expandStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method expand;
            expand = statusBarManager.getClass().getMethod("expandNotificationsPanel");
            expand.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    public void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            collapse = statusBarManager.getClass().getMethod("collapsePanels");
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    public boolean requestRoot() {
        //先申请root权限
        Process process = null;
        boolean result = false;
        try {
            // XpLog.i("申请root");
            process = Runtime.getRuntime().exec("su");
            result = true;
            // XpLog.i("申请成功");
            final Process finalProcess = process;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // boolean result = false;
                    DataOutputStream dataOutputStream = null;

                    try {
                        dataOutputStream = new DataOutputStream(finalProcess.getOutputStream());
                        // 模拟手势下拉 这个地方模拟点击有写别的rom会点击到别的快捷开关，所以做两次下拉
                        String command = "input swipe 100 10 100 1000 200 \n";
                        // String command2 = "input tap 200 150 \n";
                        dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                        SystemClock.sleep(200);
                        dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                        dataOutputStream.flush();
                        dataOutputStream.writeBytes("exit\n");
                        dataOutputStream.flush();
                        finalProcess.waitFor();


                    } catch (Exception e) {

                    } finally {
                        try {
                            if (dataOutputStream != null) {
                                dataOutputStream.close();
                            }

                        } catch (IOException e) {

                        }
                    }

                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            XpLog.i("申请 失败");
        }

        return result;
    }
}
