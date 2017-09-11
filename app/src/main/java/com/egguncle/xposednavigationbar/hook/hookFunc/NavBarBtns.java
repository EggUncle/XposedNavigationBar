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

import android.view.View;

import com.egguncle.xposednavigationbar.hook.util.ScheduledThreadPool;

/**
 * Created by egguncle on 17-6-21.
 */

public abstract class NavBarBtns implements View.OnClickListener, View.OnLongClickListener {
    private int mType;
    public final static int BTN_BACK = 1;
    public final static int BTN_HOME = 2;
    public final static int BTN_RECENT = 3;
    public final static int BTN_HIDE = 4;
    public final static int BTN_LONG_HOME = 5;

    protected abstract void goBack();

    protected abstract void goHome();

    protected abstract void longHome();

    protected abstract void goRecent();

    protected abstract void hide();

    protected abstract void show();

    public NavBarBtns(int type) {
        mType = type;
    }

    @Override
    public void onClick(View v) {
        switch (mType) {
            case BTN_BACK:
                goBack();
                break;
            case BTN_HOME:
                goHome();
                break;
            case BTN_RECENT:
                goRecent();
                break;
            case BTN_HIDE:
                hide();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                longHome();
            }
        }).start();
        return true;
    }
}
