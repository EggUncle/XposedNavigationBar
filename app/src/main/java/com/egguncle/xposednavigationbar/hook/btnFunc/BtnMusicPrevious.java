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
import android.view.KeyEvent;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.MusicController;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-14.
 */

public class BtnMusicPrevious implements MusicController, View.OnClickListener {
    @Override
    public void nextMusic() {

    }

    @Override
    public void startOrPauseMusic() {

    }

    @Override
    public void previousMusic() {
        XposedBridge.log("nextMusic: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation mInst = new Instrumentation();
                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                XposedBridge.log("nextMusic: success");
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        previousMusic();
    }
}
