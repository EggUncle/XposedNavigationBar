/*
 * Create by EggUncle on 17-6-4 上午10:02
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-5-25 下午2:45
 */

package com.egguncle.xposednavigationbar.model;

import android.graphics.drawable.Drawable;
/**
 * Created by egguncle on 17-5-25.
 * 用于保存app的信息
 */

public class AppInfo {
    private String packgeName;
    private String appName;
    private Drawable appIcon;

    public String getPackgeName() {
        return packgeName;
    }

    public void setPackgeName(String packgeName) {
        this.packgeName = packgeName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
