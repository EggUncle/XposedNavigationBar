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


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.Color;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.egguncle.xposednavigationbar.BuildConfig;
import com.egguncle.xposednavigationbar.FinalStr.FuncName;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.btnFunc.MusicControllerPanel;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.ShortCutData;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;
import com.egguncle.xposednavigationbar.ui.adapter.MyViewPagerAdapter;
import com.egguncle.xposednavigationbar.util.ImageUtil;
import com.egguncle.xposednavigationbar.util.SPUtil;
import com.google.gson.Gson;

import static de.robv.android.xposed.XposedBridge.log;


/**
 * Created by egguncle on 17-6-1.
 * <p>
 * 一个hook模块，为了在android设备的底部导航栏虚拟按键上实现功能扩展
 */

public class HookUtil implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private final static String TAG = "HookUtil";

    public final static String ACT_BROADCAST = "com.egguncle.xpnavbar.broadcast";
    public final static String ACT_NAVBAR_SHOW = "com.egguncle.xpnavbar.shownavbar";
    public final static String ACT_CHANGE_ROOT_EXPAND_STATUS_BAR = "com.egguncle.xpnavbar.root_expand";
    public final static String ACT_NAV_BAR_DATA = "com.egguncle.xpnavbar.navbardata";

    private static final String SHORT_CUT_DATA = "short_cut_data";
    public static final String USE_ROOT_EXPAND_STATUS_BAR = "use_root_expand_status_bar";

    //剪贴板内容
    private static ArrayList<String> clipboardData;

    //用于获取phonestatusbar对象和clearAllNotifications方法等
    private static Object phoneStatusBar;
    private static Method clearAllNotificationsMethod;

    private static View navbarView;
    private static WindowManager windowManager;
    private static Method addNavigationBarMethod;

    private static boolean expandStatusBarWithRoot;

    //用于加载图片资源
    private Map<Integer, byte[]> mapImgRes = new HashMap<>();

    //用于获取保存的快捷按键设置
    private static ArrayList<ShortCut> shortCutList;
    private int mIconScale;

    private LinearLayout vpLine;
    private LinearLayout linepage1;

    private int homePointPosition;

    private BtnFuncFactory btnFuncFactory;

    private XSharedPreferences pre;

    private void initHook(StartupParam startupParam) throws Throwable {
        pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
        pre.makeWorldReadable();

        String json = pre.getString(SHORT_CUT_DATA, "");
        expandStatusBarWithRoot = pre.getBoolean(SPUtil.ROOT_DOWN, false);

        //获取主导行栏小点的位置
        homePointPosition = pre.getInt(FuncName.HOME_POINT, 0);
        //获取快捷按钮设置数据
        Gson gson = new Gson();
        //在第一次激活重新启动的时候，可能因为没有设置任何快捷按钮，导致这里报错
        try {
            shortCutList = gson.fromJson(json, ShortCutData.class).getData();
        } catch (Exception e) {
            shortCutList = new ArrayList<>();
        }

        //获取图片缩放大小
        mIconScale = pre.getInt(FuncName.ICON_SIZE, 100);
        //初始化剪贴板内容集合
        clipboardData = new ArrayList<>();

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
        byte[] startActs = XposedHelpers.assetAsByteArray(res, "start_acts.png");
        byte[] playMusic = XposedHelpers.assetAsByteArray(res, "ic_music.png");
        byte[] pauseMusic = XposedHelpers.assetAsByteArray(res, "ic_pause.png");
        byte[] previousMusic = XposedHelpers.assetAsByteArray(res, "ic_previous.png");
        byte[] nextMusic = XposedHelpers.assetAsByteArray(res, "ic_next.png");
        byte[] scanWeChat = XposedHelpers.assetAsByteArray(res, "wechat_qr.png");
        byte[] scanAlipay = XposedHelpers.assetAsByteArray(res, "alipay_qr.png");
        byte[] screenshot = XposedHelpers.assetAsByteArray(res, "ic_image.png");
        byte[] navBack = XposedHelpers.assetAsByteArray(res, "ic_sysbar_back.png");
        byte[] navHome = XposedHelpers.assetAsByteArray(res, "ic_sysbar_home.png");
        byte[] navRecent = XposedHelpers.assetAsByteArray(res, "ic_sysbar_recent.png");
        byte[] clipBoard = XposedHelpers.assetAsByteArray(res, "ic_clipboard.png");
        byte[] command = XposedHelpers.assetAsByteArray(res, "ic_command.png");
        byte[] navHide = XposedHelpers.assetAsByteArray(res, "ic_nav_down.png");

        mapImgRes.put(FuncName.FUNC_BACK_CODE, backImg);
        mapImgRes.put(FuncName.FUNC_CLEAR_MEM_CODE, clearMenImg);
        mapImgRes.put(FuncName.FUNC_CLEAR_NOTIFICATION_CODE, clearNotificationImg);
        mapImgRes.put(FuncName.FUNC_DOWN_CODE, downImg);
        mapImgRes.put(FuncName.FUNC_LIGHT_CODE, lightImg);
        mapImgRes.put(FuncName.FUNC_QUICK_NOTICE_CODE, quickNoticesImg);
        mapImgRes.put(FuncName.FUNC_SCREEN_OFF_CODE, screenOffImg);
        //  mapImgRes.put(FuncName.UP, upImg);
        mapImgRes.put(FuncName.FUNC_VOLUME_CODE, volume);
        mapImgRes.put(FuncName.FUNC_SMALL_POINT_CODE, smallPonit);
        mapImgRes.put(FuncName.FUNC_HOME_CODE, home);
        mapImgRes.put(FuncName.FUNC_START_ACTS_CODE, startActs);
        mapImgRes.put(FuncName.FUNC_PLAY_MUSIC_CODE, playMusic);
        mapImgRes.put(FuncName.FUNC_NEXT_PLAY_CODE, nextMusic);
        mapImgRes.put(FuncName.FUNC_PREVIOUS_PLAY_CODE, previousMusic);
        mapImgRes.put(FuncName.FUNC_WECHAT_SACNNER_CODE, scanWeChat);
        mapImgRes.put(FuncName.FUNC_ALIPAY_SACNNER_CODE, scanAlipay);
        mapImgRes.put(FuncName.FUNC_SCREEN_SHOT_CODE, screenshot);
        mapImgRes.put(FuncName.FUNC_NAV_BACK_CODE, navBack);
        mapImgRes.put(FuncName.FUNC_NAV_HOME_CODE, navHome);
        mapImgRes.put(FuncName.FUNC_NAV_RECENT_CODE, navRecent);
        mapImgRes.put(FuncName.FUNC_CLIPBOARD_CODE, clipBoard);
        mapImgRes.put(FuncName.FUNC_COMMAND_CODE, command);
        mapImgRes.put(FuncName.FUNC_NAV_HIDE_CODE, navHide);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        initHook(startupParam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        //  XSharedPreferences pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
        //过滤包名
        if (!resparam.packageName.equals("com.android.systemui"))
            return;

        XposedBridge.log("hook resource ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            resparam.res.hookLayout(resparam.packageName, "layout", "navigation_bar", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    XposedBridge.log("hook layout");
                    //hook0NavBar(liparam);
                    hookNavBar(liparam);
                }
            });
        } else {
            resparam.res.hookLayout(resparam.packageName, "layout", "navigation_layout", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    XposedBridge.log("hook layout on nougat");
                    hookNavBarOnNougat(liparam);
                }
            });
        }


    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //       XposedBridge.log(lpparam.packageName);
        //过滤包名
        if (lpparam.packageName.equals("com.android.systemui")) {
            XposedBridge.log("filter package systemui");
            hookPhoneStatusBar(lpparam);
        }
    }

    /**
     * 因为这个模块的功能部分的数据存储其实是使用SharedPreferences以json形式存储在机器上的，而xp读取它
     * 则它需要使用MODE_WORLD_READABLE这个模式，但是在android Nougat中，使用这个权限会报错，在源码中检索后发现
     * ContextImpl中有一个checkMode方法来对其进行检测，所以此处其实是对ContextImpl的checkmode进行hook，越过其对
     * sp模式的检测导致报错
     * 这里仅仅是一个尝试
     */
    public void hookSharedPreferences(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> contextImplClass = lpparam.classLoader.loadClass("android.app.ContextImpl");
        XposedHelpers.findAndHookMethod(contextImplClass, "checkMode", int.class, new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("===replace success===");
                XSharedPreferences sharedPreferences = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");
                String json = sharedPreferences.getString(SHORT_CUT_DATA, "null");
                XposedBridge.log(json);
                return null;
            }
        });
//        XposedHelpers.findAndHookMethod(contextImplClass, "getSharedPreferences", File.class, int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                //-rw-rw-r-- 1 u0_a72 u0_a72  400 2017-08-09 16:12 XposedNavigationBar.xml
//                //-rw-rw-r-- u0_a62   u0_a62  398 2017-08-05 05:39 XposedNavigationBar.xml
//
//                XposedBridge.log("===setReadable===");
//                //这个方法的第一个参数是文件，修改其存取权限尝试来绕过nougat的限制
//                File file=new File("/data/data/com.egguncle.xposednavigationbar/shared_prefs/XposedNavigationBar.xml");
//                file.setReadable(true, false);
//                XposedBridge.log(file.getPath());
//                file.getParentFile().setReadable(true, false);
//                XposedBridge.log(file.getParentFile().getPath());
//                file.getParentFile().getParentFile().setReadable(true, false);
//                XposedBridge.log(file.getParentFile().getParentFile().getPath());
//            }
//        });

        XposedHelpers.findAndHookMethod(contextImplClass, "setFilePermissionsFromMode", String.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("===MODE_WORLD_READABLE===");
                param.args[1] = Activity.MODE_WORLD_READABLE;
            }
        });
    }

    public void hookPhoneStatusBar(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //获取清除通知的方法
        Class<?> phoneStatusBarClass =
                lpparam.classLoader.loadClass("com.android.systemui.statusbar.phone.PhoneStatusBar");

        clearAllNotificationsMethod = phoneStatusBarClass.getDeclaredMethod("clearAllNotifications");
        clearAllNotificationsMethod.setAccessible(true);

        addNavigationBarMethod = phoneStatusBarClass.getDeclaredMethod("addNavigationBar");
        addNavigationBarMethod.setAccessible(true);
        //获取到clearAllNotifications和toggleRecentApps方法
        XposedHelpers.findAndHookMethod(phoneStatusBarClass,
                "start", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //在这里获取到PhoneStatusBar对象
                        phoneStatusBar = param.thisObject;
                        //获取到windowmanager
                        windowManager = (WindowManager) XposedHelpers.getObjectField(phoneStatusBar, "mWindowManager");
                        navbarView = (View) XposedHelpers.getObjectField(phoneStatusBar, "mNavigationBarView");
                        //隐藏导航栏
//                            XposedBridge.log("====remove navbar ====");
//                            windowManager.removeView(navbarView);
                        //显示导航栏
//                            XposedBridge.log("====add navbar ====");
//                            addNavigationBarMethod.invoke(phoneStatusBar);

                    }
                });

    }

    /**
     * Android 6.0 及以下hook布局的方法
     *
     * @param liparam
     */
    public void hookNavBar(XC_LayoutInflated.LayoutInflatedParam liparam) {
        LinearLayout navBarParentView = (LinearLayout) liparam.view;
        //垂直状态下的导航栏整体布局
        XposedBridge.log("hook navBarBg success");
        FrameLayout rootView = (FrameLayout) navBarParentView.findViewById(liparam.res.getIdentifier("rot0", "id", "com.android.systemui"));
        //垂直状态下的导航栏三大按钮布局
        final LinearLayout navBarView = (LinearLayout) rootView.findViewById(liparam.res.getIdentifier("nav_buttons", "id", "com.android.systemui"));
        hookNavBarFunc(rootView, navBarView);
    }

    /**
     * Android 7.0 的hook布局的方法
     *
     * @param liparam
     */
    public void hookNavBarOnNougat(XC_LayoutInflated.LayoutInflatedParam liparam) {
        final FrameLayout rootView = (FrameLayout) liparam.view;
        final FrameLayout navBarView = (FrameLayout) rootView.findViewById(liparam.res.getIdentifier("nav_buttons", "id", "com.android.systemui"));
        hookNavBarFunc(rootView, navBarView);
    }

    /**
     * 基础的hook方法
     *
     * @param rootView
     * @param navbarView
     */
    public void hookNavBarFunc(ViewGroup rootView, final ViewGroup navbarView) {
        Context context = rootView.getContext();
        LinearLayout parentView = new LinearLayout(context);
        //加入一个viewpager，第一页为空，是导航栏本身的功能
        final ViewPager vpXphook = new ViewPager(context);

        parentView.addView(vpXphook);

        //初始化左边的整个音乐面板
        MusicControllerPanel musicPanel = new MusicControllerPanel(context);
        musicPanel.setData(mapImgRes, mIconScale);
        musicPanel.initPanel();

        //第一个界面，与原本的导航栏重合，实际在导航栏的下层
        linepage1 = new LinearLayout(context);

        //用于呼出整个扩展导航栏的一个小点
        ImageButton btnCall = new ImageButton(context);
        btnCall.setImageBitmap(ImageUtil.byte2Bitmap(mapImgRes.get(FuncName.FUNC_SMALL_POINT_CODE)));
        btnCall.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnCall.setBackgroundColor(Color.alpha(255));
        LinearLayout.LayoutParams line1Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        linepage1.addView(btnCall, line1Params);
        setHomePointPosition(linepage1, homePointPosition);

        //点击这个按钮，跳转到扩展部分
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpXphook.setCurrentItem(2);

            }
        });


        //初始化广播接收器
        initBroadcast(context);
        //初始化剪贴板监听
        startListenClipboard(context);

        //viewpage的第二页
        //整个页面的基础
        final FrameLayout framePage2 = new FrameLayout(context);
        framePage2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        btnFuncFactory = new BtnFuncFactory(framePage2, vpXphook, mapImgRes);

        //   LinearLayout
        vpLine = new LinearLayout(context);
        // vpLine.setPadding(0, 0, 0, 0);
        framePage2.addView(vpLine);
        vpLine.setOrientation(LinearLayout.HORIZONTAL);
        vpLine.setGravity(Gravity.CENTER_VERTICAL);

        for (ShortCut sc : shortCutList) {
            // createBtnAndSetFunc(context, framePage2, vpLine, sc.getShortCutName());
            btnFuncFactory.createBtnAndSetFunc(context, vpLine, sc, mIconScale);
        }
        //将这些布局都添加到viewpageadapter中
        List<View> list = new ArrayList<View>();
        list.add(musicPanel);
        list.add(linepage1);
        list.add(framePage2);


        MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(list);

        vpXphook.setAdapter(pagerAdapter);
        vpXphook.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    //当移动到第一页的时候，显示出导航栏，上升动画
                    XposedBridge.log("apper NavigationBar");
                    navbarView.setVisibility(View.VISIBLE);
                } else {
                    //当移动到非第一页的时候，隐藏导航栏本身的功能，来实现自己的一些功能。
                    XposedBridge.log("hide NavigationBar");
                    navbarView.setVisibility(View.GONE);
                    if (vpLine.getChildCount() == 0) {
                        vpXphook.setCurrentItem(1);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpXphook.setCurrentItem(1);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        rootView.addView(parentView, 0, params);
    }

    public static Object getPhoneStatusBar() {
        return phoneStatusBar;
    }

    public static Method getClearAllNotificationsMethod() {
        return clearAllNotificationsMethod;
    }


    /**
     * 初始化广播，用于进程间通信
     *
     * @param context
     */
    private void initBroadcast(Context context) {
//        IntentFilter btnChangeFilter = new IntentFilter();
//        btnChangeFilter.addAction(ACT_BROADCAST);
//        BtnChangeReceiver btnReceiver = new HookUtil.BtnChangeReceiver();
//        context.registerReceiver(btnReceiver, btnChangeFilter);

//        IntentFilter navbarShowFilter = new IntentFilter();
//        navbarShowFilter.addAction(ACT_NAVBAR_SHOW);
//        NavBarShowReceiver navReceiver = new HookUtil.NavBarShowReceiver();
//        context.registerReceiver(navReceiver, navbarShowFilter);

        IntentFilter expandStatusFilter = new IntentFilter();
        expandStatusFilter.addAction(ACT_CHANGE_ROOT_EXPAND_STATUS_BAR);
        ExpandStatusBarReceiver expandStatusBarReceiver = new HookUtil.ExpandStatusBarReceiver();
        context.registerReceiver(expandStatusBarReceiver, expandStatusFilter);

        IntentFilter dataFilter = new IntentFilter();
        dataFilter.addAction(ACT_NAV_BAR_DATA);
        NavbarDataReceiver navbarDataReceiver = new HookUtil.NavbarDataReceiver();
        context.registerReceiver(navbarDataReceiver, dataFilter);
    }

    private void setHomePointPosition(LinearLayout ll, int position) {
       // ll = linepage1;
        ImageButton pointbtn = (ImageButton) ll.getChildAt(0);
        if (position == SPUtil.LEFT) {
            ll.setGravity(Gravity.LEFT);
        } else if (position == SPUtil.RIGHT) {
            ll.setGravity(Gravity.RIGHT);
        } else if (position == SPUtil.DISMISS) {
            pointbtn.setVisibility(View.GONE);
        }
    }

    /**
     * 开始监听剪贴板
     */
    private void startListenClipboard(final Context context) {

        final ClipboardManager clipboard = (ClipboardManager) context.
                getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                //  XposedBridge.log("onPrimaryClipChanged");
                //获取剪贴板内容，先判断该内容是否为空
                if (clipboard.hasPrimaryClip()) {
                    ClipData clipData = clipboard.getPrimaryClip();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; ++i) {

                        ClipData.Item item = clipData.getItemAt(i);
                        CharSequence str = item
                                .coerceToText(context);
                        //因为复制历史记录里面某一条文字到剪贴板的时候，也会导致剪贴板内容变动，此处避免 添加重复内容到剪贴板历史
                        if (!clipboardData.contains(str.toString())) {
                            clipboardData.add(str.toString());
                        }
                    }
                }
            }
        });
    }

    public static ArrayList<String> getClipdata() {
        return clipboardData;
    }

    public static View getNavbarView() {
        return navbarView;
    }

    public static Method getAddNavigationBarMethod() {
        return addNavigationBarMethod;
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static boolean isExpandStatusBarWithRoot() {
        return expandStatusBarWithRoot;
    }

    public static void setExpandStatusBarWithRoot(boolean expandStatusBarWithRoot) {
        HookUtil.expandStatusBarWithRoot = expandStatusBarWithRoot;
    }

    /**
     * 解析xpnvbarsetting的内容
     *
     * @param context
     * @param setting
     */
    public void xpNavBarDataAnalysis(Context context, XpNavBarSetting setting) {
        List<ShortCut> list = setting.getShortCutData();
        int iconSize = setting.getIconSize();
        int homePosition = setting.getHomePointPosition();
        boolean rootDown = setting.isRootDown();
        XposedBridge.log(homePosition+"  "+iconSize);

        updateNavBar(context, list, homePosition, iconSize, rootDown);
    }

    /**
     * 根据获取到的数据来更新导航栏
     *
     * @param shortCutData
     * @param homePointPosition
     * @param iconSize
     * @param rootDown
     */
    public void updateNavBar(Context context, List<ShortCut> shortCutData, int homePointPosition, int iconSize, boolean rootDown) {
        btnFuncFactory.clearAllBtn(vpLine);
        if (shortCutData != null && shortCutData.size() != 0) {
            for (ShortCut sc : shortCutData) {
                btnFuncFactory.createBtnAndSetFunc(context, vpLine, sc, iconSize);
            }
        }
        setHomePointPosition(linepage1,homePointPosition);
        expandStatusBarWithRoot=rootDown;
    }

//    private class BtnChangeReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //在更新以后有些用户没有进行重启，而直接进行了新功能的添加，app部分得到了更新，
//            //但是hook部分的更新需要重启后才生效，所以这里做一个异常捕获操作
//            try {
//                shortCutList = intent.getParcelableArrayListExtra("data");
//                btnFuncFactory.clearAllBtn(vpLine);
//                if (shortCutList != null && shortCutList.size() != 0) {
//                    for (ShortCut sc : shortCutList) {
//                        btnFuncFactory.createBtnAndSetFunc(context, vpLine, sc, mIconScale);
//                    }
//                }
//            } catch (Exception e) {
//                XposedBridge.log(e.getMessage());
//                Toast.makeText(context, "please reboot after update", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private class ExpandStatusBarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                expandStatusBarWithRoot = intent.getBooleanExtra(USE_ROOT_EXPAND_STATUS_BAR, false);
            } catch (Exception e) {
                XposedBridge.log(e.getMessage());
                Toast.makeText(context, "please reboot after update", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NavbarDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                XpNavBarSetting setting = intent.getParcelableExtra("data");
                xpNavBarDataAnalysis(context, setting);
            } catch (Exception e) {

            }
        }
    }

    private class NavBarShowReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                addNavigationBarMethod.invoke(phoneStatusBar);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}

