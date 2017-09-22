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

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by egguncle on 17-6-7.
 * 快捷小功能的定义类
 */

public class ShortCut implements Parcelable{
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

    //自定义图标路径
    private String iconPath;

    //如果是shell快捷指令，则应该还有一个指令内容
    private String shellStr;

    public ShortCut(){}

    protected ShortCut(Parcel in) {
        code = in.readInt();
        open = in.readByte() != 0;
        page = in.readInt();
        postion = in.readInt();
        iconPath = in.readString();
        shellStr = in.readString();
    }

    public static final Creator<ShortCut> CREATOR = new Creator<ShortCut>() {
        @Override
        public ShortCut createFromParcel(Parcel in) {
            return new ShortCut(in);
        }

        @Override
        public ShortCut[] newArray(int size) {
            return new ShortCut[size];
        }
    };

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

    public String getShellStr() {
        return shellStr;
    }

    public void setShellStr(String shellStr) {
        this.shellStr = shellStr;
    }
    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeByte((byte) (open ? 1 : 0));
        parcel.writeInt(page);
        parcel.writeInt(postion);
        parcel.writeString(iconPath);
        parcel.writeString(shellStr);
    }
}
