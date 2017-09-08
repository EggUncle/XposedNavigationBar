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

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnAlipayScanner;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnBackLight;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnClearAllNotifications;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnClearBackground;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnMusicController;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnNavBarGoHome;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnNavClipboard;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnOpenActPanel;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnQuickNotice;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnScreenOff;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnScreenShot;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnStartCommand;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnStatusBarController;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnVolume;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnWeChatScanner;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnsNavbar;
import com.egguncle.xposednavigationbar.hook.hookutil.MainHookUtil;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.util.ImageUtil;

import java.util.Map;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnFuncFactory {
    private ViewGroup mRootViewGroup;
    private ViewPager mViewPager;
    //用于加载图片资源
    private Map<Integer, byte[]> mMapImgRes;
    private ViewGroup mllExtPage;

    public BtnFuncFactory(ViewGroup rootViewGroup,
                          ViewPager viewPager, ViewGroup llExtPage,
                          Map<Integer, byte[]> mapImgRes) {
        mRootViewGroup = rootViewGroup;
        mMapImgRes = mapImgRes;
        mViewPager = viewPager;
        mllExtPage = llExtPage;
    }


    public View.OnClickListener getBtnFuncOfName(ShortCut sc) {
        switch (sc.getCode()) {
//            case ConstantStr.BACK:
//                break;
            case ConstantStr.FUNC_DOWN_CODE:
                return new BtnStatusBarController();
            case ConstantStr.FUNC_QUICK_NOTICE_CODE:
                return new BtnQuickNotice();
            case ConstantStr.FUNC_CLEAR_NOTIFICATION_CODE:
                return new BtnClearAllNotifications();
            case ConstantStr.FUNC_SCREEN_OFF_CODE:
                return new BtnScreenOff();
            case ConstantStr.FUNC_CLEAR_MEM_CODE:
                return new BtnClearBackground();
            case ConstantStr.FUNC_VOLUME_CODE:
                return new BtnVolume(mRootViewGroup, ImageUtil.byte2Bitmap(mMapImgRes.get(ConstantStr.FUNC_BACK_CODE)));
            case ConstantStr.FUNC_LIGHT_CODE:
                return new BtnBackLight(mRootViewGroup, ImageUtil.byte2Bitmap(mMapImgRes.get(ConstantStr.FUNC_BACK_CODE)));
            case ConstantStr.FUNC_HOME_CODE:
                return new BtnNavBarGoHome(mViewPager);
            case ConstantStr.FUNC_START_ACTS_CODE:
                return new BtnOpenActPanel();
            case ConstantStr.FUNC_NEXT_PLAY_CODE:
                return new BtnMusicController(BtnMusicController.NEXT);
            case ConstantStr.FUNC_PLAY_MUSIC_CODE:
                return new BtnMusicController(BtnMusicController.START_OR_STOP);
            case ConstantStr.FUNC_PREVIOUS_PLAY_CODE:
                return new BtnMusicController(BtnMusicController.PREVIOUS);
            case ConstantStr.FUNC_WECHAT_SACNNER_CODE:
                return new BtnWeChatScanner();
            case ConstantStr.FUNC_ALIPAY_SACNNER_CODE:
                return new BtnAlipayScanner();
            case ConstantStr.FUNC_SCREEN_SHOT_CODE:
                return new BtnScreenShot();
            case ConstantStr.FUNC_NAV_BACK_CODE:
                return new BtnsNavbar(BtnsNavbar.BTN_BACK);
            case ConstantStr.FUNC_NAV_HOME_CODE:
                return new BtnsNavbar(BtnsNavbar.BTN_HOME);
            case ConstantStr.FUNC_NAV_RECENT_CODE:
                return new BtnsNavbar(BtnsNavbar.BTN_RECENT);
            case ConstantStr.FUNC_CLIPBOARD_CODE:
                return new BtnNavClipboard();
            case ConstantStr.FUNC_COMMAND_CODE:
                return new BtnStartCommand(sc.getShellStr());
            case ConstantStr.FUNC_NAV_HIDE_CODE:
                return new BtnsNavbar(BtnsNavbar.BTN_HIDE);
        }
        return null;
    }

    /**
     * 设置某些按钮的长按事件
     *
     * @param code
     * @return
     */
    public View.OnLongClickListener getBtnLongFuncOfName(int code) {
        switch (code) {
//            case ConstantStr.BACK:
//                break;
            case ConstantStr.FUNC_SCREEN_OFF_CODE:
                return new BtnScreenOff();
            case ConstantStr.FUNC_HOME_CODE:
                return new BtnsNavbar(BtnsNavbar.BTN_LONG_HOME);
        }
        return null;
    }

    /**
     * 创建按钮并且设置对应功能
     *
     * @param context
     * @param line
     * @param sc
     */
    public void createBtnAndSetFunc(Context context, LinearLayout line, ShortCut sc, int iconsize) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        p.gravity = Gravity.CENTER;

        ImageView btn = new ImageView(context);
        Space sp1 = new Space(context);
        Space sp2 = new Space(context);

        String iconPath = sc.getIconPath();
        Bitmap iconBitmap = null;
        if (iconPath != null) {
            iconBitmap = ImageUtil.zoomBitmap(iconPath, iconsize);
        }
        if (iconBitmap == null) {
            iconBitmap = ImageUtil.byte2Bitmap(mMapImgRes.get(sc.getCode()));
        }
        iconBitmap = ImageUtil.zommBitmap(iconBitmap, iconsize);
        btn.setImageBitmap(iconBitmap);

        //给按钮设置水波纹点击背景，因为7.0上有一些rom有获取资源的限制所以这个功能暂时只在7.0以下的版本使用
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        try {
            btn.setBackground(context.getResources().getDrawable(MainHookUtil.getBtnBgResId(), context.getTheme()));
        } catch (Exception e) {
            XpLog.e(e);
        }
        //  }
        btn.setScaleType(ImageView.ScaleType.CENTER);
        btn.setOnClickListener(getBtnFuncOfName(sc));
        btn.setOnLongClickListener(getBtnLongFuncOfName(sc.getCode()));

        line.addView(sp1, p);
        line.addView(btn, p);
        line.addView(sp2, p);
    }

    public void clearAllBtn() {
        mllExtPage.removeAllViews();
    }


}
