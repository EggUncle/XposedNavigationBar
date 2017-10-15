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

import com.egguncle.xposednavigationbar.hook.util.XpNavbarThreadPool;

/**
 * Created by egguncle on 17-6-12.
 */

public abstract class MusicController extends VibrateClick {
    public final static int PREVIOUS = 1;
    public final static int START_OR_STOP = 2;
    public final static int NEXT = 3;
    protected boolean isPlaying;

    protected abstract void nextMusic();

    protected abstract void startOrPauseMusic();

    protected abstract void previousMusic();

    private int mType;

    public MusicController(int type) {
        mType = type;
    }

    @Override
    void onVibrateClick(View v) {
        XpNavbarThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                switch (mType) {
                    case PREVIOUS:
                        previousMusic();
                        break;
                    case START_OR_STOP:
                        startOrPauseMusic();
                        break;
                    case NEXT:
                        nextMusic();
                        break;
                }
            }
        });
    }
}
