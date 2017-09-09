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

package com.egguncle.xposednavigationbar.ui.activity;

import android.content.Intent;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;
import com.egguncle.xposednavigationbar.util.SPUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 因为在Android 7.0上的限制，所以这里为7.0加一个activity，第一次开机的时候，
 * 点击导航栏上的小点就打开这个activity，读取sp的数据并载入
 */
public class InitActivity extends BaseActivity {

    @Override
    int getLayoutId() {
        return R.layout.a_init;
    }

    @Override
    void initView() {

    }

    @Override
    void initVar() {

    }

    @Override
    void initAction() {
        SPUtil spUtil = SPUtil.getInstance(this);
        ArrayList<ShortCut> shortCutList = spUtil.getAllShortCutData();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("data", shortCutList);
        int iconSize = spUtil.getIconSize();
        int homePosition = spUtil.getHomePointPosition();
        boolean rootDown = spUtil.getRootDown();
        int clearMenLevel=spUtil.getClearMemLevel();
        boolean chameleonNavbar=spUtil.isChameleonNavBar();
        XpNavBarSetting setting = new XpNavBarSetting(shortCutList, homePosition, iconSize, rootDown,clearMenLevel,chameleonNavbar);
        intent.putExtra("data", setting);
        intent.setAction(XpNavBarAction.ACT_NAV_BAR_DATA);
        sendBroadcast(intent);
        finish();
    }
}
