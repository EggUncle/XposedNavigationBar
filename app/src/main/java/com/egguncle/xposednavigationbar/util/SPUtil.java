/*
 * Create by EggUncle on 17-6-4 上午11:27
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-4 上午11:27
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


    private SPUtil() {

    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                instance = new SPUtil();
                mSharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
            }
        }
        return instance;
    }

    public static void saveAppInfo(String packageName){}


}
