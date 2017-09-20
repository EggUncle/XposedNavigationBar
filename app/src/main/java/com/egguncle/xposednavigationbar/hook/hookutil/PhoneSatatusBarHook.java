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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-8-14.
 */

public class PhoneSatatusBarHook {
    //用于获取phonestatusbar对象和clearAllNotifications方法等
    private static Object phoneStatusBar;

    private final static String PHONE_STATUS_BRA_CLASS = "com.android.systemui.statusbar.phone.PhoneStatusBar";
//    private final static String NOTIFICATION_STACK_SCROLL_LAYOUT =
//            "com.android.systemui.statusbar.stack.NotificationStackScrollLayout";
//    private final static String I_STATUS_BAR_SERVICE = "com.android.internal.statusbar.IStatusBarService";

    public static void hook(ClassLoader classLoader) throws Throwable {
        //获取清除通知的方法
        Class<?> phoneStatusBarClass =
                classLoader.loadClass(PHONE_STATUS_BRA_CLASS);
//        Class<?> istatusbarservice = lpparam.classLoader.loadClass(I_STATUS_BAR_SERVICE);
//        final Method onClearAllNotifications = istatusbarservice.getMethod("onClearAllNotifications", int.class);
//
//        XposedHelpers.findAndHookMethod(phoneStatusBarClass, "clearAllNotifications", new XC_MethodReplacement() {
//            @Override
//            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                XpLog.i("try to replace clearAllNotifications");
//                ViewGroup mStackScroller = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mStackScroller");
//
//                int numChildren = mStackScroller.getChildCount();
//                final ArrayList<View> viewsToHide = new ArrayList<View>(numChildren);
//                for (int i = 0; i < numChildren; i++) {
//                    final View child = mStackScroller.getChildAt(i);
//                    boolean b = (boolean) XposedHelpers.callMethod(mStackScroller, "canChildBeDismissed", child);
//                    if (b) {
//                        if (child.getVisibility() == View.VISIBLE) {
//                            viewsToHide.add(child);
//                        }
//                    }
//                }
//                if (viewsToHide.isEmpty()) {
//                    XposedHelpers.callMethod(param.thisObject, "animateCollapsePanels", 0);
//                    return null;
//                }
//                final Object mBarService = XposedHelpers.getObjectField(param.thisObject, "mBarService");
//                final int mCurrentUserId = (int) XposedHelpers.getObjectField(param.thisObject, "mCurrentUserId");
//                XposedHelpers.callMethod(param.thisObject, "addPostCollapseAction", new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            onClearAllNotifications.invoke(mBarService, mCurrentUserId);
//                        } catch (Exception e) {
//                            XpLog.e(e);
//                        }
//                    }
//                });
//
//                XposedHelpers.callMethod(param.thisObject, "performDismissAllAnimations", viewsToHide);
//                XpLog.i("--- clearAllNotifications ---");
//                return null;
//            }
//        });
        XposedHelpers.findAndHookMethod(phoneStatusBarClass,
                "start", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        BroadcastReceiver screenShotReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                int type = intent.getIntExtra(ConstantStr.TYPE, -1);
                                switch (type) {
                                    case ConstantStr.CLEAR_NOTIFICATIONS:
                                        XposedHelpers.callMethod(param.thisObject, "clearAllNotifications");
                                        break;
                                    case ConstantStr.RECENT_TASKS:
                                        XposedHelpers.callMethod(param.thisObject, "toggleRecentApps");
                                        break;
                                    case ConstantStr.HIDE_NAVBAR: {
                                        View navbarView = (View) XposedHelpers.getObjectField(param.thisObject, "mNavigationBarView");
                                        if (navbarView.isAttachedToWindow()) {
                                            WindowManager wm = (WindowManager) XposedHelpers.getObjectField(param.thisObject, "mWindowManager");
                                            wm.removeView(navbarView);
                                        }
                                    }
                                    break;
                                    case ConstantStr.SHOW_NAVBAR: {
                                        View navbarView = (View) XposedHelpers.getObjectField(param.thisObject, "mNavigationBarView");
                                        if (!navbarView.isAttachedToWindow()) {
                                            WindowManager wm = (WindowManager) XposedHelpers.getObjectField(param.thisObject, "mWindowManager");
                                            wm.addView(navbarView, (ViewGroup.LayoutParams) XposedHelpers.callMethod(param.thisObject, "getNavigationBarLayoutParams"));
                                        }
                                    }
                                    break;
                                }

                            }
                        };
                        IntentFilter filter = new IntentFilter(XpNavBarAction.ACTION_PHONE_STATUSBAR);
                        mContext.registerReceiver(screenShotReceiver, filter);
                    }
                });

    }
}
