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

import java.util.List;

/**
 * Created by egguncle on 17-8-10.
 * 因为在Nougat上无法正常使用全局读写权限的sharedpreference,
 * 所以这里做一个妥协，将所有设置保存在一个类中，仍然使用json存储在sp中，
 * 在Nougat上虽然无法直接获取sp内容来进行扩展状态的初始化，但是可以在使用中进行进程间通信来设置扩展功能
 */

public class XpNavBarSetting implements Parcelable {
    //快捷设置的内容数据
    private List<ShortCut> mShortCutData;

    private int mHomePointPosition;
    private int mIconSize;
    private boolean mRootDown;
    private int mClearMenLevel;
    private boolean mChameleonNavbar;
    private int mNavbarHeight;
    private boolean mVibrate;

    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int DISMISS = 2;

    public XpNavBarSetting(List<ShortCut> shortCutData, int homePointPosition, int iconSize,
                           boolean rootDown, int clearMenLevel, boolean chameleonNavbar, int navbarHeight, boolean vibrate) {
        mShortCutData = shortCutData;
        mHomePointPosition = homePointPosition;
        mIconSize = iconSize;
        mRootDown = rootDown;
        mClearMenLevel = clearMenLevel;
        mChameleonNavbar = chameleonNavbar;
        mNavbarHeight = navbarHeight;
        mVibrate = vibrate;
    }


    protected XpNavBarSetting(Parcel in) {
        mShortCutData = in.createTypedArrayList(ShortCut.CREATOR);
        mHomePointPosition = in.readInt();
        mIconSize = in.readInt();
        mRootDown = in.readByte() != 0;
        mClearMenLevel = in.readInt();
        mChameleonNavbar = in.readByte() != 0;
        mNavbarHeight = in.readInt();
        mVibrate = in.readByte() != 0;
    }

    public static final Creator<XpNavBarSetting> CREATOR = new Creator<XpNavBarSetting>() {
        @Override
        public XpNavBarSetting createFromParcel(Parcel in) {
            return new XpNavBarSetting(in);
        }

        @Override
        public XpNavBarSetting[] newArray(int size) {
            return new XpNavBarSetting[size];
        }
    };

    public List<ShortCut> getShortCutData() {
        return mShortCutData;
    }

    public int getHomePointPosition() {
        return mHomePointPosition;
    }


    public int getIconSize() {
        return mIconSize;
    }

    public boolean isRootDown() {
        return mRootDown;
    }

    public boolean isChameleonNavbar() {
        return mChameleonNavbar;
    }

    public int getClearMenLevel() {
        return mClearMenLevel;
    }

    public int getNavbarHeight() {
        return mNavbarHeight;
    }

    public boolean isVibrate() {
        return mVibrate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mShortCutData);
        dest.writeInt(mHomePointPosition);
        dest.writeInt(mIconSize);
        dest.writeByte((byte) (mRootDown ? 1 : 0));
        dest.writeInt(mClearMenLevel);
        dest.writeByte((byte) (mChameleonNavbar ? 1 : 0));
        dest.writeInt(mNavbarHeight);
        dest.writeByte((byte) (mVibrate ? 1 : 0));
    }
}
