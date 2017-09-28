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

package com.egguncle.xposednavigationbar.hook.btnFunc;

import android.app.Instrumentation;
import android.content.Context;
import android.view.KeyEvent;

import com.egguncle.xposednavigationbar.hook.hookFunc.NavBarBtns;

/**
 * Created by egguncle on 17-6-21.
 * Android原有的返回、home、最近任务键,以及显示和隐藏
 */

public class BtnsNavbar extends NavBarBtns{

    private Instrumentation mInst;

    public BtnsNavbar(int type) {
        super(type);
        mInst = new Instrumentation();
    }

    @Override
    protected void goBack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            }
        }).start();
    }

    @Override
    protected void goHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
            }
        }).start();
    }

    @Override
    protected void longHome() {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.FLAG_LONG_PRESS, KeyEvent.KEYCODE_HOME);
        mInst.sendKeySync(keyEvent);
    }

    @Override
    protected void goRecent(Context context) {
       context.sendBroadcast(intent);
    }

    @Override
    protected void hide(Context context) {
        context.sendBroadcast(intent);
    }

    @Override
    protected void show() {

    }
}
