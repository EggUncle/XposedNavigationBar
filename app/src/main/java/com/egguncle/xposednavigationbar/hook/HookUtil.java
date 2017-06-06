/*
 * Create by EggUncle on 17-6-2 下午2:27
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-2 下午2:19
 */

package com.egguncle.xposednavigationbar.hook;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
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

    //启动快速备忘
    private final static String ACTION_QUICK_NOTICE = "com.egguncle.xposednavigationbar.QuickNotificationActivity";
    //启动后台清理
    private final static String ACTION_CLEAR_BACK = "com.egguncle.xposednavigationbar.ClearMemActivity";

    //状态栏是否展开
    private boolean statusBarExpend = false;

    //用于获取phonestatusbar对象和clearAllNotifications方法
    private Object phoneStatusBar;
    private Method clearAllNotificationsMethod;


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
                //整个页面的基础
                final FrameLayout frameLayout = new FrameLayout(context);
                LinearLayout vpLine = new LinearLayout(context);
                frameLayout.addView(vpLine);
                vpLine.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
//                Button btn1 = new Button(context);
//                btn1.setText("启动应用");
                Button btn2 = new Button(context);
                btn2.setText("下拉通知");
                Button btn3 = new Button(context);
                btn3.setText("快速备忘");
                Button btn4 = new Button(context);
                btn4.setText("清除通知");
                Button btn5 = new Button(context);
                btn5.setText("息屏");
                Button btn6 = new Button(context);
                btn6.setText("清理后台");
                Button btn7 = new Button(context);
                btn7.setText("屏幕亮度");
                Button btn8 = new Button(context);
                btn8.setText("声音调整");
//                btn1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        launchActivity(view.getContext(), "com.egguncle.imagetohtml");
//                    }
//                });

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
                        clearAllNotifications(context);

                    }
                });

                btn5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            screenOff(view.getContext());
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                });

                btn6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearBackground(view.getContext());
                    }
                });

                btn7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout line = new LinearLayout(context);
                        line.setBackgroundColor(Color.BLACK);
                        setBacklightBrightness(context, line, frameLayout);
                        frameLayout.addView(line);

                    }
                });
                btn8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout line = new LinearLayout(context);
                        line.setBackgroundColor(Color.BLACK);
                        setPhoneVolume(context, line, frameLayout);
                        frameLayout.addView(line);
                    }
                });

                // vpLine.addView(btn1, p);
                vpLine.addView(btn2, p);
                vpLine.addView(btn3, p);
                vpLine.addView(btn4, p);
                vpLine.addView(btn5, p);
                vpLine.addView(btn6, p);
                vpLine.addView(btn7, p);
                vpLine.addView(btn8, p);

                //  textView2.setBackgroundColor(Color.BLUE);
                List<View> list1 = new ArrayList<View>();
                list1.add(textView1);
                list1.add(frameLayout);

                //亮度调整模块
//                LinearLayout line = new LinearLayout(context);
//                setBacklightBrightness(context, line);
//                list1.add(line);

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

//        resparam.res.hookLayout(resparam.packageName, "layout", "status_bar_recent_panel", new XC_LayoutInflated() {
//
//            @Override
//            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
//                XposedBridge.log("=========================hook linearlayout success");
//                LinearLayout recentLinear = (LinearLayout) liparam.view.findViewById(liparam.res.getIdentifier("recents_linear_layout", "id", "com.android.systemui"));
//                recentLinear.setBackgroundColor(Color.WHITE);
//                FrameLayout fg = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("recents_bg_protect", "id", "com.android.systemui"));
//                fg.setBackgroundColor(Color.BLUE);
//                int count = recentLinear.getChildCount();
//                for (int i = 0; i < count; i++) {
//                    View childView = recentLinear.getChildAt(i);
//                    XposedBridge.log("--" + childView.getClass().getName());
//                }
//            }
//        });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //过滤包名
        if (lpparam.packageName.equals("com.android.systemui")) {
            XposedBridge.log("filter package");
            //获取清除通知的方法
            Class<?> phoneStatusBarClass =
                    lpparam.classLoader.loadClass("com.android.systemui.statusbar.phone.PhoneStatusBar");
            Method method1 = phoneStatusBarClass.getDeclaredMethod("clearAllNotifications");
            method1.setAccessible(true);
            //获取到clearAllNotifications方法
            clearAllNotificationsMethod = method1;
            XposedBridge.log("====hook PhoneStatusBar success====");
            //       phoneStatusBar=XposedHelpers.findClass("com.android.systemui.statusbar.phone.PhoneStatusBar",lpparam.classLoader);
            XposedHelpers.findAndHookMethod(phoneStatusBarClass,
                    "start", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            //在这里获取到PhoneStatusBar对象
                            phoneStatusBar = param.thisObject;
                            XposedBridge.log("====hook clear notifications success====");
                        }
                    });

        } else if (lpparam.packageName.equals("android.os")) {


        }


    }

    /**
     * 完全展开通知栏
     */
    public void expandAllStatusBar(Context context) {
        //如果在6.0环境下，尝试申请root权限来解决通知栏展开缓慢的问题
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && requestRoot()) {
            //申请成功后会模拟手势进行下拉
        } else {

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
        Intent intent = new Intent(ACTION_QUICK_NOTICE);
        //使用这种启动标签，可以避免在打开软件本身以后再通过快捷键呼出备忘对话框时仍然显示软件的界面的bug
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动其他app
     *
     * @param context
     * @param pkgName 对应app的包名
     */
    public void launchActivity(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }

    /**
     * 清除所有通知
     *
     * @param context
     */
    public void clearAllNotifications(Context context) {
        if (clearAllNotificationsMethod == null || phoneStatusBar == null) {
            return;
        }
        try {
            //反射取到这个清除所有通知的方法
            clearAllNotificationsMethod.invoke(phoneStatusBar);
            //方法执行后，不会马上清除所有的消息，而是在通知栏下拉，通知内容变得可见后才清除。
            //所以在这里调用一次下拉通知栏的方法
            expandStatusBar(context);
            //再收起通知栏
            collapseStatusBar(context);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 息屏
     *
     * @param context
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public void screenOff(Context context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        Method goToSleep = pm.getClass().getMethod("goToSleep", long.class);
        goToSleep.invoke(pm, SystemClock.uptimeMillis());


    }

    /**
     * 清理后台 systemuiapplication这个进程没有killbrakground的权限，去启动透明activity并执行这个方法了
     *
     * @param context
     */
    public void clearBackground(Context context) {
        Intent intent = new Intent(ACTION_CLEAR_BACK);
        //使用这种启动标签，可以避免在打开软件本身以后再通过快捷键呼出备忘对话框时仍然显示软件的界面的bug
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


//    public void takeScreenShot(Context context) {
//
//    }

    /**
     * 请求root权限，用于处理android6.0通知栏展开缓慢的问题
     */
    public boolean requestRoot() {
        //先申请root权限
        Process process = null;
        boolean result = false;
        try {
            process = Runtime.getRuntime().exec("su");
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Process finalProcess = process;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // boolean result = false;
                DataOutputStream dataOutputStream = null;

                try {
                    // 申请su权限

                    dataOutputStream = new DataOutputStream(finalProcess.getOutputStream());
                    // 模拟手势下拉
                    String command = "input swipe 100 10 100 500 300 \n";
                    String command2 = "input tap 100 100 \n";
                    dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                    SystemClock.sleep(300);
                    dataOutputStream.write(command2.getBytes(Charset.forName("utf-8")));
                    dataOutputStream.flush();
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.flush();
                    finalProcess.waitFor();

                    //   result = true;
                } catch (Exception e) {

                } finally {
                    try {
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }

                    } catch (IOException e) {

                    }
                }
                //    return result;
            }
        }).start();
        return result;
    }


    /**
     * 设置背光亮度 在界面上再展开一个拖动条
     *
     * @param context
     * @param viewGroup 在这个viewgroup上创建拖动条
     */
    private void setBacklightBrightness(final Context context, final ViewGroup viewGroup, final ViewGroup rootGroup) {
        LinearLayout.LayoutParams btnParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParam.weight = 1;
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams seekBarParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        seekBarParam.weight = 2;
        seekBarParam.gravity = Gravity.CENTER_VERTICAL;

        Button button = new Button(context);
        button.setText("关闭");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootGroup.removeView(viewGroup);
            }
        });
        SeekBar seekBar = new SeekBar(context);
        //获取当前亮度并设置
        int nowLight = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        seekBar.setProgress(nowLight);
        //亮度最小为30,最大为255
        seekBar.setMax(225);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setBackgroundLight(context, i + 30);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        viewGroup.addView(button, btnParam);
        viewGroup.addView(seekBar, seekBarParam);

    }

    /**
     * 设置背光亮度
     * 这个方法确实有效，目前已知的问题是调整亮度后，
     * 通知栏的亮度拖动条并不会拖动，还有就是修改亮度这一个功能的效果无法在虚拟机上看出来
     *
     * @param context
     * @param light
     */
    private void setBackgroundLight(Context context, int light) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            Method setBacklightBrightness = pm.getClass().getMethod("setBacklightBrightness", int.class);
            setBacklightBrightness.setAccessible(true);
            setBacklightBrightness.invoke(pm, light);

            XposedBridge.log("=====setBacklightBrightness");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            XposedBridge.log(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XposedBridge.log(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            XposedBridge.log(e.getMessage());
        }
    }


    /**
     * 设置手机音量 （媒体）
     *
     * @param context
     * @param viewGroup
     * @param rootGroup
     */
    private void setPhoneVolume(final Context context, final ViewGroup viewGroup, final ViewGroup rootGroup) {
        LinearLayout.LayoutParams btnParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParam.weight = 1;
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams seekBarParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        seekBarParam.weight = 2;
        seekBarParam.gravity = Gravity.CENTER_VERTICAL;

        Button button = new Button(context);
        button.setText("关闭");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootGroup.removeView(viewGroup);
            }
        });
        SeekBar seekBar = new SeekBar(context);
        //获取当前媒体并设置
        int nowLight = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(nowLight);
        //亮度最小为0,最大为7
        seekBar.setMax(7);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setVolume(context, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        viewGroup.addView(button, btnParam);
        viewGroup.addView(seekBar, seekBarParam);

    }

    /**
     * 调整声言
     *
     * @param context
     * @param volume
     */
    private void setVolume(Context context, int volume) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //调整媒体声言，不播放声言也不振动
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}

