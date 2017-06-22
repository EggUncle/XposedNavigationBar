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

/**
 * Created by egguncle on 17-6-7.
 * 快捷小功能的定义类
 */

public class ShortCut {
    //快捷小功能的名字对应的编码
    private int code;
//    //快捷小功能的标识符
//    private String shortCutName;
    //是否开启了这个功能
    private boolean open;
    //第几页
    private int page;
    //该页的第几个位置
    private int postion;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public int getCode() {return code;}

    public void setCode(int code) {this.code = code;}

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
//
//    public String getShortCutName() {
//        return shortCutName;
//    }
//
//    public void setShortCutName(String shortCutName) {
//        this.shortCutName = shortCutName;
//    }
}
