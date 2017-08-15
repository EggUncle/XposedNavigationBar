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

import android.content.res.Resources;
import android.content.res.XModuleResources;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.ShortCutData;
import com.egguncle.xposednavigationbar.util.SPUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-8-14.
 * 用于获取所需要的各种资源
 */

public class DataHook {
    //用于加载图片资源
    public static Map<Integer, byte[]> mapImgRes = new HashMap<>();
    public static int iconScale;
    public static boolean expandStatusBarWithRoot;
    //用于获取保存的快捷按键设置
    public static ArrayList<ShortCut> shortCutList;
    public static int homePointPosition;

    public static void init(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        XSharedPreferences pre = new XSharedPreferences("com.egguncle.xposednavigationbar", "XposedNavigationBar");

        String json = pre.getString(ConstantStr.SHORT_CUT_DATA, "");
        expandStatusBarWithRoot = pre.getBoolean(SPUtil.ROOT_DOWN, false);

        //获取主导行栏小点的位置
        homePointPosition = pre.getInt(ConstantStr.HOME_POINT, 0);
        //获取快捷按钮设置数据
        Gson gson = new Gson();
        //在第一次激活重新启动的时候，可能因为没有设置任何快捷按钮，导致这里报错
        try {
            shortCutList = gson.fromJson(json, ShortCutData.class).getData();
        } catch (Exception e) {
            shortCutList = new ArrayList<>();
        }

        //获取图片缩放大小
        iconScale = pre.getInt(ConstantStr.ICON_SIZE, 100);
        //初始化剪贴板内容集合

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

        mapImgRes.put(ConstantStr.FUNC_BACK_CODE, backImg);
        mapImgRes.put(ConstantStr.FUNC_CLEAR_MEM_CODE, clearMenImg);
        mapImgRes.put(ConstantStr.FUNC_CLEAR_NOTIFICATION_CODE, clearNotificationImg);
        mapImgRes.put(ConstantStr.FUNC_DOWN_CODE, downImg);
        mapImgRes.put(ConstantStr.FUNC_LIGHT_CODE, lightImg);
        mapImgRes.put(ConstantStr.FUNC_QUICK_NOTICE_CODE, quickNoticesImg);
        mapImgRes.put(ConstantStr.FUNC_SCREEN_OFF_CODE, screenOffImg);
        //  mapImgRes.put(ConstantStr.UP, upImg);
        mapImgRes.put(ConstantStr.FUNC_VOLUME_CODE, volume);
        mapImgRes.put(ConstantStr.FUNC_SMALL_POINT_CODE, smallPonit);
        mapImgRes.put(ConstantStr.FUNC_HOME_CODE, home);
        mapImgRes.put(ConstantStr.FUNC_START_ACTS_CODE, startActs);
        mapImgRes.put(ConstantStr.FUNC_PLAY_MUSIC_CODE, playMusic);
        mapImgRes.put(ConstantStr.FUNC_NEXT_PLAY_CODE, nextMusic);
        mapImgRes.put(ConstantStr.FUNC_PREVIOUS_PLAY_CODE, previousMusic);
        mapImgRes.put(ConstantStr.FUNC_WECHAT_SACNNER_CODE, scanWeChat);
        mapImgRes.put(ConstantStr.FUNC_ALIPAY_SACNNER_CODE, scanAlipay);
        mapImgRes.put(ConstantStr.FUNC_SCREEN_SHOT_CODE, screenshot);
        mapImgRes.put(ConstantStr.FUNC_NAV_BACK_CODE, navBack);
        mapImgRes.put(ConstantStr.FUNC_NAV_HOME_CODE, navHome);
        mapImgRes.put(ConstantStr.FUNC_NAV_RECENT_CODE, navRecent);
        mapImgRes.put(ConstantStr.FUNC_CLIPBOARD_CODE, clipBoard);
        mapImgRes.put(ConstantStr.FUNC_COMMAND_CODE, command);
        mapImgRes.put(ConstantStr.FUNC_NAV_HIDE_CODE, navHide);
    }

}
