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
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.egguncle.xposednavigationbar.hook.hookFunc.ScreenOff;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnScreenOff extends ScreenOff{

    @Override
    protected void screenOff(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        XposedHelpers.callMethod(pm,"goToSleep",SystemClock.uptimeMillis());
    }

    @Override
    protected void showPowerMenu(Context context) {
        Instrumentation mInst = new Instrumentation();
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER);
        mInst.sendKeySync(keyEvent);
    }
}
