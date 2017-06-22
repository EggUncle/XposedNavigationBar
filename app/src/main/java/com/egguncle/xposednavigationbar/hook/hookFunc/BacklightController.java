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

package com.egguncle.xposednavigationbar.hook.hookFunc;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by egguncle on 17-6-10.
 */

public interface BacklightController {
    /**
     * 设置背光亮度
     * 这个方法确实有效，目前已知的问题是调整亮度后，
     * 通知栏的亮度拖动条并不会拖动，还有就是修改亮度这一个功能的效果无法在虚拟机上看出来
     * @param context
     * @param light
     */
    void setBackgroundLight(Context context, int light) ;
}
