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

/**
 * Created by egguncle on 17-6-4.
 * XP框架中似乎不好实现数据库存储，所以用一个SharedPreferences来存储数据
 */

public class SPUtil {
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SPUtil instance;
    private static final String SP_NAME = "XposedNavigationBar";
    private static final String ACTIVATION="activation";


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
     * @param b
     */
    public  void setActivation(boolean b){
        mEditor.putBoolean(ACTIVATION,b);
        mEditor.commit();
    }

    /**
     * 获取当前模块是否激活
     * @return
     */
    public  boolean getActivation(){
        return mSharedPreferences.getBoolean(ACTIVATION,false);
    }


}
