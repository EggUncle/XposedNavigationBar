/*
 * Create by EggUncle on 17-6-2 下午2:27
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-2 下午2:19
 */

package com.egguncle.xposednavigationbar.hook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.egguncle.xposednavigationbar.ui.adapter.MyViewPagerAdapter;

import static android.view.View.VISIBLE;

/**
 * Created by egguncle on 17-6-1.
 * <p>
 * 一个hook模块，为了在android设备的底部导航栏虚拟按键上实现类似mac touch bar的效果
 */

public class HookUtil implements IXposedHookLoadPackage, IXposedHookInitPackageResources {

    private final static String TAG = "HookUtil";
    //状态栏是否展开
    private boolean statusBarExpend = false;

    //用于获取phonestatbar对象
    private Object obj;
    private Method clearAllNotifications;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {

        //过滤包名
        if (!resparam.packageName.equals("com.android.systemui"))
            return;

        XposedBridge.log("hook resource ");

        resparam.res.hookLayout(resparam.packageName, "layout", "navigation_bar", new XC_LayoutInflated() {

            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                //垂直状态下的导航栏整体布局
                FrameLayout navBarBg = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("rot0", "id", "com.android.systemui"));
                //垂直状态下的导航栏三大按钮布局
                final LinearLayout lineBtn = (LinearLayout) liparam.view.findViewById(liparam.res.getIdentifier("nav_buttons", "id", "com.android.systemui"));
                final Context context = navBarBg.getContext();

                LinearLayout parentView = new LinearLayout(context);
                //加入一个viewpager，第一页为空，是导航栏本身的功能
                ViewPager vpXphook = new ViewPager(context);
                parentView.addView(vpXphook);
                TextView textView1 = new TextView(context);

                //viewpage的第二页
                LinearLayout vpLine = new LinearLayout(context);
                vpLine.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                Button btn1 = new Button(context);
              //  btn1.setBackgroundColor(Color.alpha(255));
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      launchActivity(view.getContext(),"com.egguncle.imagetohtml");
                    }
                });
                Button btn2 = new Button(context);
                Button btn3 = new Button(context);
                Button btn4 = new Button(context);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (statusBarExpend) {
                            collapseStatusBar(view.getContext());
                            statusBarExpend = false;
                        } else {
                            expandAllStatusBar(view.getContext());
                            statusBarExpend = true;
                        }
                    }
                });

                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        quickNotification(context);
                    }
                });
                btn4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //这个方法只能清除对应应用里面的通知
//                        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//                        nm.cancelAll();
                        try {
                            //反射取到这个清除所有通知的方法
                            clearAllNotifications.invoke(obj);
                            //方法执行后，不会马上清除所有的消息，而是在通知栏下拉，通知内容变得可见后才清除。
                            //所以在这里调用一次下拉通知栏的方法
                            expandStatusBar(context);
                            //再收起通知栏
                         //   collapseStatusBar(context);

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

                vpLine.addView(btn1, p);
                vpLine.addView(btn2, p);
                vpLine.addView(btn3, p);
                vpLine.addView(btn4, p);

                //  textView2.setBackgroundColor(Color.BLUE);
                List<View> list1 = new ArrayList<View>();
                list1.add(textView1);
                list1.add(vpLine);
                MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(list1);

                vpXphook.setAdapter(pagerAdapter);
                vpXphook.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            //当移动到第一页的时候，显示出导航栏，上升动画
                            XposedBridge.log("apper NavigationBar");
                            lineBtn.setVisibility(View.VISIBLE);
//                            int navBarHeight = lineBtn.getHeight();
//                            TranslateAnimation animaUp = new TranslateAnimation(0, 0, navBarHeight, 0);
//                            animaUp.setDuration(300);
//                            animaUp.setFillAfter(true);
//                            lineBtn.startAnimation(animaUp);
                        } else {
                            //当移动到非第一页的时候，隐藏导航栏本身的功能，来实现自己的一些功能。
                            XposedBridge.log("hide NavigationBar");
//                            int navBarHeight = lineBtn.getHeight();
//                            TranslateAnimation animDown = new TranslateAnimation(0, 0, 0, navBarHeight);
//                            animDown.setDuration(300);
//                            animDown.setFillAfter(true);

                            if (lineBtn.getVisibility() == VISIBLE) {
                                lineBtn.setVisibility(View.GONE);
                            }
                            //      lineBtn.startAnimation(animDown);

                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                navBarBg.addView(parentView, 0, params);

            }
        });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //过滤包名
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;
        XposedBridge.log("filter package");
        Class<?> c = lpparam.classLoader.loadClass("com.android.systemui.statusbar.phone.PhoneStatusBar");
        Method m=c.getDeclaredMethod("clearAllNotifications");
        m.setAccessible(true);
        //获取到clearAllNotifications方法
        clearAllNotifications=m;
      //  XposedBridge.log("====hook success====");
        XposedHelpers.findAndHookMethod(c,
                "start", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //在哲理获取到PhoneStatusBar对象
                obj=param.thisObject;
                XposedBridge.log("====hook success====");
            }
        });

    }

    /**
     * 完全展开通知栏
     */
    public void expandAllStatusBar(Context context) {
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

    /**
     * 展开通知栏(只展开一小部分的那种
     */
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


    /**
     * 收起通知栏
     */
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

    /**
     * 快速备忘，在通知栏添加一条通知
     */
    public void quickNotification(Context context) {
        Intent intent = new Intent("com.egguncle.xposednavigationbar.QuickNotificationActivity");
        //使用这种启动标签，可以避免在打开软件本身以后再通过快捷键呼出备忘对话框时仍然显示软件的界面的bug
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动其他app
     * @param context
     * @param pkgName 对应app的包名
     */
    public void launchActivity(Context context,String pkgName){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }
}
