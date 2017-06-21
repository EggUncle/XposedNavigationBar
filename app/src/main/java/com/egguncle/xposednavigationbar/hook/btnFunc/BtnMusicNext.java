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
import android.media.session.MediaSession;
import android.view.KeyEvent;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.MusicController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-12.
 */

public class BtnMusicNext implements MusicController, View.OnClickListener {
    private final static String TAG = "BtnMusicNext";

    @Override
    public void onClick(View view) {
        nextMusic();
    }

    @Override
    public void nextMusic() {
        XposedBridge.log("nextMusic: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation mInst = new Instrumentation();
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
//                KeyEvent keyEvent=new KeyEvent(KeyEvent.FLAG_LONG_PRESS, KeyEvent.KEYCODE_POWER);
//                mInst.sendKeySync(keyEvent);
                XposedBridge.log("nextMusic: success");
            }
        }).start();
     //   sendEvent();

    }

    @Override
    public void startOrPauseMusic() {

    }

    @Override
    public void previousMusic() {

    }
}
