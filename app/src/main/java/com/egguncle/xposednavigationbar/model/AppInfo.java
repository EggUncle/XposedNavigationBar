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

package com.egguncle.xposednavigationbar.model;

import android.graphics.drawable.Drawable;

import org.litepal.crud.DataSupport;

/**
 * 用于保存app或者快捷开关的信息
 */

public class AppInfo extends DataSupport {
    //快捷启动的类型，app或者是快捷方式
    public final static int TYPE_APP=0;
    public final static int TYPE_SHORT_CUT=1;

    private int type;
    //两种方式均需要包名和应用本身的名字
    //包名
    private String packgeName;
    //快捷方式的名字，或者对应app的名字
    private String label;

    //下面这些是快捷方式特有的
    private int flag;
    //快捷方式启动的目标的名字
    private String shortCutName;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPackgeName() {
        return packgeName;
    }

    public void setPackgeName(String packgeName) {
        this.packgeName = packgeName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getShortCutName() {
        return shortCutName;
    }

    public void setShortCutName(String shortCutName) {
        this.shortCutName = shortCutName;
    }

    @Override
    public boolean equals(Object obj) {
        boolean b=false;
        if (obj instanceof AppInfo){
            AppInfo appInfo= (AppInfo) obj;
            b=(this.type==appInfo.getType())&&(this.packgeName.equals(appInfo.getPackgeName()))
                    &&(this.label.equals(appInfo.getLabel()));
        }

        return b;
    }
}
