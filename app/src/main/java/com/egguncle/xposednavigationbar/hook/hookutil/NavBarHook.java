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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.btnFunc.MusicControllerPanel;
import com.egguncle.xposednavigationbar.hook.util.BtnFuncFactory;
import com.egguncle.xposednavigationbar.hook.util.MyClipBoard;
import com.egguncle.xposednavigationbar.hook.util.XpLog;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;
import com.egguncle.xposednavigationbar.util.ImageUtil;
import com.egguncle.xposednavigationbar.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-8-14.
 */

public class NavBarHook {
    private static BtnFuncFactory btnFuncFactory;

    private static MusicControllerPanel musicControllerPanel;
    private static LinearLayout exNavbar;
    private static LinearLayout onHomeNavbar;
    private static ViewGroup rootNavbarView;

    public static void hook(ClassLoader classLoader) throws Throwable {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            XpLog.i("hook on Marshmallow");
            hookNavBarBeforeNougat(classLoader);
        } else {
            XpLog.i("hook on Nougat");
            hookNavBarOnNougat(classLoader);
        }
    }

    /**
     * hook android 7.0的导航栏
     * 在lineage os上hook资源文件的方法没生效，只能在这里做hook了
     */
    private static void hookNavBarOnNougat(ClassLoader classLoader) throws Throwable {
        final Class<?> navigationBarInflaterViewClass = classLoader.loadClass("com.android.systemui.statusbar.phone.NavigationBarInflaterView");
        XposedHelpers.findAndHookMethod(navigationBarInflaterViewClass, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                FrameLayout navbar = (FrameLayout) XposedHelpers.getObjectField(param.thisObject, "mRot0");
                FrameLayout navbarBtns = (FrameLayout) navbar.getChildAt(0);
                hookNavBar(navbar, navbarBtns);
            }
        });
    }

    /**
     * hook android 7.0以下的导航栏
     */
    private static void hookNavBarBeforeNougat(ClassLoader classLoader) throws Throwable {
        final Class<?> navigationBarInflaterViewClass = classLoader.loadClass("com.android.systemui.statusbar.phone.NavigationBarView");
        XposedHelpers.findAndHookMethod(navigationBarInflaterViewClass, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View navBarView = (View) param.thisObject;
                View[] mRotatedViews = (View[]) XposedHelpers.getObjectField(navBarView, "mRotatedViews");
                Resources res = navBarView.getResources();
                ViewGroup rootView = (ViewGroup) mRotatedViews[Surface.ROTATION_0];
                ViewGroup navBtnsRot0 = (ViewGroup) mRotatedViews[Surface.ROTATION_0]
                        .findViewById(res.getIdentifier("nav_buttons", "id", "com.android.systemui"));
                hookNavBar(rootView, navBtnsRot0);
            }
        });
    }

    private static void hookNavBar(ViewGroup rootView, ViewGroup navbarView) {
        rootNavbarView = rootView;
        Context context = rootView.getContext();
        ViewPager vpXpHook = new ViewPager(context);

        exNavbar = new LinearLayout(context);
        musicControllerPanel = new MusicControllerPanel(context);
        onHomeNavbar = new LinearLayout(context);

        initExNavbar(vpXpHook, exNavbar);
        initHomeNavbar(onHomeNavbar, vpXpHook);
        initMusicPanel(musicControllerPanel);
        initVpHook(vpXpHook, navbarView, exNavbar, musicControllerPanel, onHomeNavbar);

        initBroadcast(context);
        initClipBoardListener(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        rootView.addView(vpXpHook, 0, params);

        setNavbarHeight(context, DataHook.navbarHeight);
    }

    private static void initVpHook(final ViewPager vpXpHook, final ViewGroup navbarView,
                                   final ViewGroup exNavbar, ViewGroup musicPanel, ViewGroup onHomeNavbar) {
        final List<View> list = new ArrayList<View>();
        list.add(musicPanel);
        list.add(onHomeNavbar);
        list.add(exNavbar);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(list.get(position));
            }
        };
        vpXpHook.setAdapter(pagerAdapter);
        vpXpHook.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    XpLog.i("apper NavigationBar");
                    navbarView.setVisibility(View.VISIBLE);
                } else {
                    XpLog.i("hide NavigationBar");
                    navbarView.setVisibility(View.GONE);
                    if (exNavbar.getChildCount() == 0) {
                        vpXpHook.setCurrentItem(1);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpXpHook.setCurrentItem(1);
    }

    private static void initHomeNavbar(LinearLayout homeNavbar, final ViewPager vp) {
        XpLog.i("initHomeNavbar");
        Context context = homeNavbar.getContext();

        ImageButton btnCall = new ImageButton(context);
        btnCall.setImageBitmap(ImageUtil.byte2Bitmap(DataHook.mapImgRes.get(ConstantStr.FUNC_SMALL_POINT_CODE)));
        btnCall.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnCall.setBackgroundColor(Color.alpha(255));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        homeNavbar.addView(btnCall, params);

        setHomePointPosition(homeNavbar);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vp.setCurrentItem(2);
            }
        });
    }

    private static void initMusicPanel(LinearLayout musicPanel) {
        Context context = musicPanel.getContext();
        musicControllerPanel = new MusicControllerPanel(context);
        musicControllerPanel.initPanel();
    }

    private static void initExNavbar(ViewPager vpXpHook, LinearLayout exNavbar) {
        btnFuncFactory = new BtnFuncFactory(vpXpHook, exNavbar);
        for (ShortCut sc : DataHook.shortCutList) {
            btnFuncFactory.createBtnAndSetFunc(exNavbar, sc);
        }
    }

    private static void initBroadcast(Context context) {
        IntentFilter dataFilter = new IntentFilter();
        dataFilter.addAction(XpNavBarAction.ACT_NAV_BAR_DATA);
        BroadcastReceiver navbarDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    XpNavBarSetting setting = intent.getParcelableExtra("data");
                    updateNavbarData(context, setting);
                } catch (Exception e) {
                    XpLog.e(e);
                }
            }
        };
        context.registerReceiver(navbarDataReceiver, dataFilter);

//        IntentFilter navbarColorFilter = new IntentFilter();
//        navbarColorFilter.addAction(XpNavBarAction.ACT_NAV_BAR_COLOR);
//        BroadcastReceiver navbarColorReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                boolean changeColor = intent.getBooleanExtra("color", false);
//                if (changeColor) {
//                    XpLog.i("change color");
//                    changeNavbarIconsColor(rootNavbarView, 0.7f);
//                } else {
//                    XpLog.i("do not change color");
//                    changeNavbarIconsColor(rootNavbarView, 1.0f);
//                }
//            }
//        };
//        context.registerReceiver(navbarColorReceiver, navbarColorFilter);
    }


    private static void initClipBoardListener(Context context) {
        MyClipBoard.startListenClipboard(context);
    }

    public static void setHomePointPosition(LinearLayout homeNavbar) {
        int position = DataHook.homePointPosition;
        ImageButton pointbtn = (ImageButton) homeNavbar.getChildAt(0);
        if (position == SPUtil.LEFT) {
            homeNavbar.setGravity(Gravity.LEFT);
        } else if (position == SPUtil.RIGHT) {
            homeNavbar.setGravity(Gravity.RIGHT);
        } else if (position == SPUtil.DISMISS) {
            pointbtn.setVisibility(View.GONE);
        }
    }

    private static void updateNavbarData(Context context, XpNavBarSetting setting) {
        List<ShortCut> shortCutData = setting.getShortCutData();
        int iconScale = setting.getIconSize();
        int homePosition = setting.getHomePointPosition();
        boolean rootDown = setting.isRootDown();
        int clearMemLevel = setting.getClearMenLevel();
        boolean chameleonNavbar = setting.isChameleonNavbar();
        int navbarHeight = setting.getNavbarHeight();
        boolean vibrate = setting.isVibrate();

        setHomePointPosition(onHomeNavbar);
        DataHook.rootDown = rootDown;
        DataHook.iconScale = iconScale;
        DataHook.clearMenLevel = clearMemLevel;
        DataHook.chameleonNavbar = chameleonNavbar;
        DataHook.homePointPosition = homePosition;
        DataHook.vibrate = vibrate;
        musicControllerPanel.updateIconSize();

        btnFuncFactory.clearAllBtn();
        if (shortCutData != null && shortCutData.size() != 0) {
            DataHook.shortCutList = shortCutData;
            for (ShortCut sc : shortCutData) {
                btnFuncFactory.createBtnAndSetFunc(exNavbar, sc);
            }
        }

        if (DataHook.navbarHeight != navbarHeight) {
            setNavbarHeight(context, navbarHeight);
            DataHook.navbarHeight = navbarHeight;
        }
    }

    private static void setNavbarHeight(Context context, int navbarHeight) {
        Intent intent = new Intent(XpNavBarAction.ACTION_PHONE_WINDOW_MANAGER);
        intent.putExtra(ConstantStr.TYPE, ConstantStr.NAVBAR_H);
        intent.putExtra(ConstantStr.NAVBAR_HEIGHT, navbarHeight);
        context.sendBroadcast(intent);
    }

    private static void changeNavbarIconsColor(ViewGroup navbarView, float scale) {
        ArrayList<ImageView> iconList = new ArrayList<>();
        findImgView(iconList, navbarView);
        for (ImageView icon : iconList) {
            Bitmap bpIcon = ImageUtil.drawableToBitmap(icon.getDrawable());
            icon.setImageBitmap(ImageUtil.handleImageEffect(bpIcon, scale));
        }
    }

    private static void findImgView(ArrayList<ImageView> list, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ImageView) {
                list.add((ImageView) child);
            } else if (child instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) child;
                findImgView(list, vg);
            }
        }
    }
}