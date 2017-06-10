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

package com.egguncle.xposednavigationbar.hook.hookFunc;

import android.content.Context;

/**
 * Created by egguncle on 17-6-10.
 */

public interface StatusBarController {

    /**
     * 完全展开通知栏
     */
    void expandAllStatusBar(Context context);

    void expandAllStatusBarWithOutRoot(Context context);

    /**
     * 展开通知栏(只展开一小部分的那种
     */
    void expandStatusBar(Context context);
    /**
     * 收起通知栏
     */
    void collapseStatusBar(Context context);

    /**
     * 请求root权限，用于处理android6.0通知栏展开缓慢的问题
     */
    boolean requestRoot();
}
