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
import android.view.KeyEvent;

import com.egguncle.xposednavigationbar.hook.hookFunc.MusicController;


/**
 * Created by egguncle on 17-6-21.
 */

public class BtnMusicController extends MusicController {

    private Instrumentation mInst;

    public BtnMusicController(int type) {
        super(type);
        mInst = new Instrumentation();
    }

    @Override
    protected void nextMusic() {
        mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    @Override
    protected void startOrPauseMusic() {
        if (isPlaying) {
            mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PAUSE);
            isPlaying = false;
        } else {
            mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PLAY);
            isPlaying = true;
        }
    }

    @Override
    protected void previousMusic() {
        mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }
}
