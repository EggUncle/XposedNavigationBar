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

package com.egguncle.xposednavigationbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.egguncle.xposednavigationbar.util.SPUtil;

import org.litepal.LitePalApplication;

import java.util.Locale;

/**
 * Created by egguncle on 17-6-16.
 */

public class MyApplication extends LitePalApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        String language = SPUtil.getInstance(this).getLanguage();
        if ("".equals(language)){
            language =Locale.getDefault().getLanguage();
        }
        if (language.equals(SPUtil.LANGUAGE_CHINESE)) {
            config.setLocale(Locale.getDefault());
        } else  {
            config.setLocale(Locale.ENGLISH);
        }
        resources.updateConfiguration(config, dm);

        mContext=getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
