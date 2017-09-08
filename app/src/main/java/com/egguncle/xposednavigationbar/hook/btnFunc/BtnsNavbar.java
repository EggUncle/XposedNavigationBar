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
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.NavBarBtns;
import com.egguncle.xposednavigationbar.hook.hookutil.PhoneSatatusBarHook;
import com.egguncle.xposednavigationbar.hook.util.ScheduledThreadPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-6-21.
 * Android原有的返回、home、最近任务键,以及显示和隐藏
 */

public class BtnsNavbar implements NavBarBtns, View.OnClickListener, View.OnLongClickListener {
    public final static int BTN_BACK = 1;
    public final static int BTN_HOME = 2;
    public final static int BTN_RECENT = 3;

    public final static int BTN_HIDE = 4;

    public final static int BTN_LONG_HOME = 5;

    private Instrumentation mInst;

    public int mType;

    public BtnsNavbar(int type) {
        this.mType = type;
        mInst = new Instrumentation();
    }

    @Override
    public void onClick(final View view) {
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
    public void goBack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            }
        }).start();
    }

    @Override
    public void goHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
            }
        }).start();
    }

    @Override
    public void goRecent() {
        showRecentlyApp();
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    /**
     * 使用反射实现打开最近任务
     */
    public void showRecentlyApp() {
        XposedHelpers.callMethod(PhoneSatatusBarHook.getPhoneStatusBar(), "toggleRecentApps");
    }

    @Override
    public boolean onLongClick(View v) {
        ScheduledThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                KeyEvent keyEvent = new KeyEvent(KeyEvent.FLAG_LONG_PRESS, KeyEvent.KEYCODE_HOME);
                mInst.sendKeySync(keyEvent);
            }
        });
        return true;
    }
}
