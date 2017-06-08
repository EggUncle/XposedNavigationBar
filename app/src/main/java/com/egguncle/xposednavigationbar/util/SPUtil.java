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

package com.egguncle.xposednavigationbar.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.ShortCutData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egguncle on 17-6-4.
 * XP框架中似乎不好实现数据库存储，所以用一个SharedPreferences来存储数据
 */

public class SPUtil {
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SPUtil instance;
    private static final String SP_NAME = "XposedNavigationBar";
    private static final String ACTIVATION = "activation";
    private static final String TAPS_APPEAR = "taps_appear";
    //在原导航键上添加一个小点，点击后出现扩展的部分
    private static final String HOME_POINT = "home_point";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String DISMISS = "notShow";

    //以json形式存储app的设置信息
    private static final String SHORT_CUT_DATA = "short_cut_data";


    private SPUtil() {

    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                instance = new SPUtil();
                //由于xp模块需要读取sp的内容，所以将sp的类型设置为MODE_WORLD_READABLE
                mSharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_WORLD_READABLE);
                mEditor = mSharedPreferences.edit();
            }
        }
        return instance;
    }

    /**
     * 模块激活开关
     *
     * @param b
     */
    public void setActivation(boolean b) {
        mEditor.putBoolean(ACTIVATION, b);
        mEditor.commit();
    }

    /**
     * 获取当前模块是否激活
     *
     * @return
     */
    public boolean getActivation() {
        return mSharedPreferences.getBoolean(ACTIVATION, false);
    }


    /**
     * 获取对应的shortcut
     *
     * @param shortCutName
     * @return
     */
    public ShortCut getShortCut(String shortCutName) {
        String status = mSharedPreferences.getString(shortCutName, "");
        if ("".equals(status)) {
            return null;
        } else {
            String[] str = status.split(" ");
            try {
                boolean open = str[0].equals(1);
                int page = Integer.parseInt(str[1]);
                int postion = Integer.parseInt(str[2]);
                ShortCut shortCut = new ShortCut();
                shortCut.setName(shortCutName);
                shortCut.setOpen(open);
                shortCut.setPage(page);
                shortCut.setPostion(postion);
                return shortCut;
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    /**
     * 存储对应shortcut的状态
     *
     * @param shortCut
     */
    public void saveShortCut(ShortCut shortCut) {
        String shortCutName = shortCut.getShortCutName();
        boolean open = shortCut.isOpen();
        int page = shortCut.getPage();
        int postion = shortCut.getPostion();
        //open的状态在sp里用
        int openStr = open ? 1 : 0;
        //构造字符串保存状态
        String status = openStr + " " + page + " " + postion;
        mEditor.putString(shortCutName, status);
        mEditor.commit();
    }

    /**
     * 存储app快捷方式信息
     *
     * @param list
     */
    public void saveShortCut(List<ShortCut> list) {
        ShortCutData data = new ShortCutData();
        data.setData(list);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        mEditor.putString(SHORT_CUT_DATA, json);
        mEditor.commit();
    }


    /**
     * 从sp的data标签里获得所有app快捷方式的存储信息
     *
     * @return
     */
    public List<ShortCut> getAllShortCutData() {
        Gson gson = new Gson();
        List<ShortCut> list = new ArrayList<>();
        String data = mSharedPreferences.getString(SHORT_CUT_DATA, "");
        if ("".equals(data)) {
            return null;
        }
        List<ShortCut> saveData = gson.fromJson(data, ShortCutData.class).getData();
        list.addAll(saveData);
        return list;
    }

    /**
     * 在打开设置界面的时候会有一个对话框提示，如果点击不再提示，则不再显示
     */
    public void nolongerTaps() {
        mEditor.putBoolean(TAPS_APPEAR, false);
        mEditor.commit();
    }

    public boolean getTapsStatus() {
        return mSharedPreferences.getBoolean(TAPS_APPEAR, true);
    }

    /**
     * 设置主导航栏上小点的位置 左 右 或者不显示
     *
     * @param str
     */
    public void setHomePointPosition(String str) {
        mEditor.putString(HOME_POINT, str);
        mEditor.commit();
    }

    public String getHomePointPosition(){
        return mSharedPreferences.getString(HOME_POINT,LEFT);
    }
}
