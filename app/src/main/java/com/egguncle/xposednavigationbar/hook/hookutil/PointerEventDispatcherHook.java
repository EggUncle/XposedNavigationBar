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

package com.egguncle.xposednavigationbar.hook.hookutil;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class PointerEventDispatcherHook {

    private final static String POINTER_EVENT_DISPATCHER_PATH="com.android.server.wm.PointerEventDispatcher";

    public static void hook(ClassLoader loader) {
        final Class<?> CLASS_POINTER_EVENT_DISPATCHER = XposedHelpers.findClass(POINTER_EVENT_DISPATCHER_PATH, loader);
        XposedHelpers.findAndHookMethod(CLASS_POINTER_EVENT_DISPATCHER, "onInputEvent", InputEvent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                try {
                    if (param.args[0] instanceof MotionEvent) {
                        MotionEvent event = (MotionEvent) param.args[0];
                        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
                            PhoneWindowManagerHook.gesturesListener.onPointerEvent(event);
                        }
                    }
                } catch (Exception e) {
                    XpLog.e(e);
                }
            }
        });
    }
}
