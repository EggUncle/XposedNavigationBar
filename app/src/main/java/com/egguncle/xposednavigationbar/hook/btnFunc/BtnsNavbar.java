/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle
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
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.NavBarBtns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by egguncle on 17-6-21.
 * Android原有的返回、home、最近任务键
 */

public class BtnsNavbar implements NavBarBtns, View.OnClickListener {
    public final static int BTN_BACK = 1;
    public final static int BTN_HOME = 2;
    public final static int BTN_RECENT = 3;


    public int mType;

    public BtnsNavbar(int type) {
        this.mType = type;
    }

    @Override
    public void onClick(View view) {
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
        }
    }

    @Override
    public void goBack() {
        new Thread(new NavBarRunnable(BTN_BACK)).start();
    }

    @Override
    public void goHome() {
        new Thread(new NavBarRunnable(BTN_HOME)).start();
    }

    @Override
    public void goRecent() {
        showRecentlyApp();
    }

    /**
     * 使用反射实现打开最近任务
     */
    public void showRecentlyApp() {
        Class serviceManagerClass;
        try {
            serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClass.getMethod("getService",
                    String.class);
            IBinder retbinder = (IBinder) getService.invoke(
                    serviceManagerClass, "statusbar");
            Class statusBarClass = Class.forName(retbinder
                    .getInterfaceDescriptor());
            Object statusBarObject = statusBarClass.getClasses()[0].getMethod(
                    "asInterface", IBinder.class).invoke(null,
                    new Object[]{retbinder});
            Method clearAll = statusBarClass.getMethod("toggleRecentApps");
            clearAll.setAccessible(true);
            clearAll.invoke(statusBarObject);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class NavBarRunnable implements Runnable {
        private int mType;

        public NavBarRunnable(int type) {
            mType = type;
        }
        @Override
        public void run() {
            switch (mType) {
                case BTN_BACK: {
                    Instrumentation mInst = new Instrumentation();
                    mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                }
                break;
                case BTN_HOME: {
                    Instrumentation mInst = new Instrumentation();
                    mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
                }
                break;
            }
        }
    }
}
