/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle
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

package com.egguncle.xposednavigationbar.hook;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.ShortCutData;
import com.egguncle.xposednavigationbar.ui.adapter.MyViewPagerAdapter;
import com.google.gson.Gson;

import static android.view.View.VISIBLE;

/**
 * Created by egguncle on 17-6-1.
 * <p>
 * 一个hook模块，为了在android设备的底部导航栏虚拟按键上实现类似mac touch bar的效果
 */

public class HookUtil implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private final static String TAG = "HookUtil";

    //启动快速备忘
    private final static String ACTION_QUICK_NOTICE = "com.egguncle.xposednavigationbar.QuickNotificationActivity";
    //启动后台清理
    private final static String ACTION_CLEAR_BACK = "com.egguncle.xposednavigationbar.ClearMemActivity";

//    private final static String IMG_BACK = "back";
//    private final static String CLEAR_MEM = "clear_mem";
//    private final static String CLEAR_NOTIFICATION = "clear_notification";
//    private final static String DOWN = "down";
//    private final static String LIGHT = "light";
//    private final static String QUICK_NOTICES = "quick_notices";
//    private final static String SCREEN_OFF = "screen_off";
//    private final static String UP = "up";
//    private final static String VOLUME = "volume";
//    private final static String SMALL_PONIT = "small_ponit";

    private static final String SHORT_CUT_DATA = "short_cut_data";

    //状态栏是否展开
    private boolean statusBarExpend = false;

    //用于获取phonestatusbar对象和clearAllNotifications方法
    private Object phoneStatusBar;
    private Method clearAllNotificationsMethod;

    //用于加载图片资源
    private Map<String, byte[]> mapImgRes = new HashMap<>();
    //用于获取保存的快捷按键设置
    private List<ShortCut> shortCutList;

    //扩展出来的主界面
    private ViewPager vpXphook;

    private String homePointPosition;


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //读取sp，查看程序是否被允许激活
        XSharedPreferences pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
        boolean activation = pre.getBoolean("activation", false);
        String json = pre.getString(SHORT_CUT_DATA, "");
        if ("".equals(json)) {
            return;
        }
        homePointPosition = pre.getString(FuncName.HOME_POINT, FuncName.LEFT);
        XposedBridge.log(homePointPosition + "=====");
        Gson gson = new Gson();
        shortCutList = gson.fromJson(json, ShortCutData.class).getData();
        for (ShortCut sc : shortCutList) {
            XposedBridge.log(sc.getName() + " " + sc.getShortCutName() + " " + sc.getPage() + " " + sc.getPostion());
        }

        if (activation) {
            //加载图片资源文件
            Resources res = XModuleResources.createInstance(startupParam.modulePath, null);
            byte[] backImg = XposedHelpers.assetAsByteArray(res, "back.png");
            byte[] clearMenImg = XposedHelpers.assetAsByteArray(res, "clear_mem.png");
            byte[] clearNotificationImg = XposedHelpers.assetAsByteArray(res, "clear_notification.png");
            byte[] downImg = XposedHelpers.assetAsByteArray(res, "down.png");
            byte[] lightImg = XposedHelpers.assetAsByteArray(res, "light.png");
            byte[] quickNoticesImg = XposedHelpers.assetAsByteArray(res, "quick_notices.png");
            byte[] screenOffImg = XposedHelpers.assetAsByteArray(res, "screenoff.png");
            //  byte[] upImg = XposedHelpers.assetAsByteArray(res, "up.png");
            byte[] volume = XposedHelpers.assetAsByteArray(res, "volume.png");
            byte[] smallPonit = XposedHelpers.assetAsByteArray(res, "small_point.png");
            byte[] home = XposedHelpers.assetAsByteArray(res, "ic_home.png");
            mapImgRes.put(FuncName.BACK, backImg);
            mapImgRes.put(FuncName.CLEAR_MEM, clearMenImg);
            mapImgRes.put(FuncName.CLEAR_NOTIFICATION, clearNotificationImg);
            mapImgRes.put(FuncName.DOWN, downImg);
            mapImgRes.put(FuncName.LIGHT, lightImg);
            mapImgRes.put(FuncName.QUICK_NOTICE, quickNoticesImg);
            mapImgRes.put(FuncName.SCREEN_OFF, screenOffImg);
            //  mapImgRes.put(FuncName.UP, upImg);
            mapImgRes.put(FuncName.VOLUME, volume);
            mapImgRes.put(FuncName.SMALL_POINT, smallPonit);
            mapImgRes.put(FuncName.HOME, home);
        }

        // BitmapFactory.decodeByteArray(img,0,img.length);
    }

    private Bitmap byte2Bitmap(byte[] imgBytes) {
        return BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        XSharedPreferences pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
        boolean activation = pre.getBoolean("activation", false);
        if (!activation) {
            return;
        }
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
                vpXphook = new ViewPager(context);
                parentView.addView(vpXphook);
                //   TextView textView1 = new TextView(context);
                //第一个界面，与原本的导航栏重合，实际在导航栏的下层
                LinearLayout linepage1 = new LinearLayout(context);
                if (!homePointPosition.equals(FuncName.DISMISS)) {
                    //用于呼出整个扩展导航栏的一个小点
                    ImageButton btnCall = new ImageButton(context);
                    btnCall.setImageBitmap(byte2Bitmap(mapImgRes.get(FuncName.SMALL_POINT)));
                    btnCall.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    btnCall.setBackgroundColor(Color.alpha(255));
                    LinearLayout.LayoutParams line1Params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    if (homePointPosition.equals(FuncName.LEFT)) {
                        //line1Params.gravity = Gravity.LEFT;
                        linepage1.setGravity(Gravity.LEFT);
                    } else if (homePointPosition.equals(FuncName.RIGHT)) {
                        //  line1Params.gravity = Gravity.RIGHT;
                        linepage1.setGravity(Gravity.RIGHT);
                    }
                    linepage1.addView(btnCall, line1Params);
                    //点击这个按钮，跳转到扩展部分
                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vpXphook.setCurrentItem(1);
                        }
                    });
                }

                //viewpage的第二页
                //整个页面的基础
                final FrameLayout framePage2 = new FrameLayout(context);
                LinearLayout vpLine = new LinearLayout(context);
                vpLine.setPadding(40, 0, 0, 0);
                framePage2.addView(vpLine);
                vpLine.setOrientation(LinearLayout.HORIZONTAL);
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                p.weight = 1;

                for (ShortCut sc : shortCutList) {
                    createBtnAndSetFunc(context, framePage2, vpLine, sc.getShortCutName());
                }
                //将这些布局都添加到viewpageadapter中
                List<View> list1 = new ArrayList<View>();
                list1.add(linepage1);
                list1.add(framePage2);

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
        XSharedPreferences pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
        boolean activation = pre.getBoolean("activation", false);
        if (!activation) {
            return;
        }
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
     * 创建按钮并且设置对应功能
     *
     * @param context
     * @param frame
     * @param name    按钮功能的标识名称
     */
    public void createBtnAndSetFunc(final Context context, final FrameLayout frame, LinearLayout line, final String name) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        ImageButton btn = new ImageButton(context);
        btn.setImageBitmap(byte2Bitmap(mapImgRes.get(name)));
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setBackgroundColor(Color.alpha(255));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (name) {
                    case FuncName.BACK:
                        break;
                    case FuncName.DOWN:
                        btnExpandAllStatusBar(context);
                        break;
                    case FuncName.QUICK_NOTICE:
                        btnQuickNotice(context);
                        break;
                    case FuncName.CLEAR_NOTIFICATION:
                        btnClearNotification(context);
                        break;
                    case FuncName.SCREEN_OFF:
                        btnScreenOff(context);
                        break;
                    case FuncName.CLEAR_MEM:
                        btnClearMem(context);
                        break;
                    case FuncName.VOLUME:
                        btnVolume(context, frame);
                        break;
                    case FuncName.LIGHT:
                        btnLight(context, frame);
                        break;
                    case FuncName.HOME:
                        goHomeNavBar();
                        break;
                }
            }
        });
        line.addView(btn, p);
    }

    /**
     * 回到主导航栏
     */
    public void goHomeNavBar() {
        vpXphook.setCurrentItem(0);
    }

//    /**
//     * 通过需要的名字来获取对应的图片ID
//     *
//     * @param name
//     * @return
//     */
//    public int getIconIdFromName(String name) {
//        switch (name) {
//            case FuncName.BACK:
//                return R.drawable.back;
//            case FuncName.DOWN:
//                return R.drawable.down;
//            case FuncName.QUICK_NOTICE:
//                return R.drawable.quick_notices;
//            case FuncName.CLEAR_NOTIFICATION
//                return R.drawable.clear_notification;
//            case FuncName.SCREEN_OFF:
//                return R.drawable.screenoff;
//            case FuncName.CLEAR_MEM:
//                return R.drawable.clear_mem;
//            case FuncName.VOLUME:
//                return R.drawable.volume;
//            case FuncName.LIGHT:
//                return R.drawable.light;
//        }
//        return 0;
//    }

    public void btnExpandAllStatusBar(Context context) {
        if (statusBarExpend) {
            collapseStatusBar(context);
            statusBarExpend = false;
        } else {
            expandAllStatusBar(context);
            statusBarExpend = true;
        }
    }

    public void btnQuickNotice(Context context) {
        quickNotification(context);
    }

    public void btnClearNotification(Context context) {
        clearAllNotifications(context);
    }

    public void btnScreenOff(Context context) {
        try {
            screenOff(context);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void btnClearMem(Context context) {
        clearBackground(context);
    }

    public void btnLight(Context context, FrameLayout framePage) {
        LinearLayout line = new LinearLayout(context);
        line.setBackgroundColor(Color.BLACK);
        setBacklightBrightness(context, line, framePage);
        framePage.addView(line);
    }

    public void btnVolume(Context context, FrameLayout framePage) {
        LinearLayout line = new LinearLayout(context);
        line.setBackgroundColor(Color.BLACK);
        setPhoneVolume(context, line, framePage);
        framePage.addView(line);
    }

    /**
     * 完全展开通知栏
     */
    public void expandAllStatusBar(Context context) {
        //如果在6.0环境下，尝试申请root权限来解决通知栏展开缓慢的问题
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && requestRoot()) {
            //申请成功后会模拟手势进行下拉
            //如果失败了，就按照普通的方式下拉
            //  expandAllStatusBarWithOutRoot(context);
            XposedBridge.log("testestset");
        } else {
            expandAllStatusBarWithOutRoot(context);
        }
    }

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
            XposedBridge.log("申请root");
            process = Runtime.getRuntime().exec("su");
            result = true;
            XposedBridge.log("申请成功");
            final Process finalProcess = process;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // boolean result = false;
                    DataOutputStream dataOutputStream = null;

                    try {
                        dataOutputStream = new DataOutputStream(finalProcess.getOutputStream());
                        // 模拟手势下拉
                        String command = "input swipe 100 10 100 500 400 \n";
                        String command2 = "input tap 200 150 \n";
                        dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                        SystemClock.sleep(500);
                        dataOutputStream.write(command2.getBytes(Charset.forName("utf-8")));
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
            XposedBridge.log("申请 失败");
        }

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

        ImageButton btnBack = new ImageButton(context);
        btnBack.setImageBitmap(byte2Bitmap(mapImgRes.get(FuncName.BACK)));
        btnBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBack.setBackgroundColor(Color.alpha(255));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootGroup.removeView(viewGroup);
            }
        });
        SeekBar seekBar = new SeekBar(context);
        final int screenMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
        // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度
        if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
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
                //设置回原来的亮度模式
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, screenMode);
            }
        });
        viewGroup.addView(btnBack, btnParam);
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

        ImageButton btnBack = new ImageButton(context);
        btnBack.setImageBitmap(byte2Bitmap(mapImgRes.get(FuncName.BACK)));
        btnBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBack.setBackgroundColor(Color.alpha(255));
        btnBack.setOnClickListener(new View.OnClickListener() {
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
        //获取最大音量值
        final AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(maxVolume);
        XposedBridge.log("max volume is "+maxVolume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setVolume(am, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        viewGroup.addView(btnBack, btnParam);
        viewGroup.addView(seekBar, seekBarParam);

    }

    /**
     * 调整声言
     *
     * @param am
     * @param volume
     */
    private void setVolume(AudioManager am, int volume) {

        //调整媒体声言，不播放声言也不振动
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}

