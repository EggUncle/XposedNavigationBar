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
import android.view.View;
import android.widget.Button;

import com.egguncle.xposednavigationbar.hook.hookFunc.ScreenOff;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnScreenOff implements ScreenOff, View.OnClickListener,View.OnLongClickListener {

    @Override
    public void onClick(View view) {
        try {
            screenOff(view.getContext());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        view.setOnLongClickListener(this);
    }

    @Override
    public void screenOff(Context context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Method goToSleep = pm.getClass().getMethod("goToSleep", long.class);
        goToSleep.invoke(pm, SystemClock.uptimeMillis());
    }

    @Override
    public boolean onLongClick(View view) {
        //长按呼出关机菜单
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation mInst = new Instrumentation();
                KeyEvent keyEvent=new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER);
                mInst.sendKeySync(keyEvent);
            }
        }).start();
        return true;
    }


}
