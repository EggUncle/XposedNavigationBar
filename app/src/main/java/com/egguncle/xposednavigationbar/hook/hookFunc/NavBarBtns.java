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
import android.content.Intent;
import android.view.View;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.XpNavbarThreadPool;

/**
 * Created by egguncle on 17-6-21.
 */

public abstract class NavBarBtns extends VibrateClick implements View.OnLongClickListener {
    private int mType;
    public final static int BTN_BACK = 1;
    public final static int BTN_HOME = 2;
    public final static int BTN_RECENT = 3;
    public final static int BTN_HIDE = 4;
    public final static int BTN_LONG_HOME = 5;

    protected abstract void goBack();

    protected abstract void goHome();

    protected abstract void longHome();

    protected abstract void goRecent(Context context);

    protected abstract void hide(Context context);

    protected abstract void show();

    protected Intent intent;

    public NavBarBtns(int type) {
        mType = type;

        if (type == BTN_RECENT) {
            intent = new Intent(XpNavBarAction.ACTION_PHONE_STATUSBAR);
            intent.putExtra(ConstantStr.TYPE, ConstantStr.RECENT_TASKS);
        } else if (type == BTN_HIDE) {
            intent = new Intent(XpNavBarAction.ACTION_PHONE_WINDOW_MANAGER);
            intent.putExtra(ConstantStr.TYPE, ConstantStr.HIDE_NAVBAR);
        }
    }

    @Override
    void onVibrateClick(View v) {
        switch (mType) {
            case BTN_BACK:
                XpNavbarThreadPool.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        goBack();
                    }
                });
                break;
            case BTN_HOME:
                XpNavbarThreadPool.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        goHome();
                    }
                });
                break;
            case BTN_RECENT:
                goRecent(v.getContext());
                break;
            case BTN_HIDE:
                hide(v.getContext());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        XpNavbarThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                longHome();
            }
        });
        return true;
    }
}
