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

package com.egguncle.xposednavigationbar.util;

import android.content.Context;

import com.egguncle.xposednavigationbar.R;

/**
 * Created by egguncle on 17-6-16.
 * 用于功能code和内容的转换
 */

public class CodeToFuncName {
    private Context mContext;
    private static String[] funcNames;
    private static CodeToFuncName mCodeToFuncName;

    private CodeToFuncName(Context context) {
        //避免内存泄漏
        this.mContext = context.getApplicationContext();
        funcNames = mContext.getResources().getStringArray(R.array.shortcut_names);
    }

    public static CodeToFuncName getInstance(Context context) {
        if (mCodeToFuncName == null) {
            mCodeToFuncName = new CodeToFuncName(context);
        }
        return mCodeToFuncName;
    }

    /**
     * 通过功能值获得该功能的名字
     *
     * @param code
     * @return
     */
    public String getFuncNameFromCode(int code) {
        if (funcNames.length > code) {
            return funcNames[code];
        } else {
            return "";
        }
    }


}
