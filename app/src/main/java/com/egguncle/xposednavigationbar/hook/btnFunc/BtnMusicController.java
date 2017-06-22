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
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.MusicController;


/**
 * Created by egguncle on 17-6-21.
 */

public class BtnMusicController implements MusicController,View.OnClickListener {
    public final static int PREVIOUS=1;
    public final static int START_OR_STOP=2;
    public final static int NEXT=3;

    public int mType;
    public boolean isPlaying;

    public BtnMusicController(int type){
        this.mType=type;
    }

    @Override
    public void onClick(View view) {
        switch (mType){
            case PREVIOUS:previousMusic();break;
            case START_OR_STOP:startOrPauseMusic();break;
            case NEXT:nextMusic();break;
        }
    }

    @Override
    public void nextMusic() {
       new Thread(new MusicRunnable(NEXT)).start();
    }

    @Override
    public void startOrPauseMusic() {
        new Thread(new MusicRunnable(START_OR_STOP)).start();
    }

    @Override
    public void previousMusic() {
        new Thread(new MusicRunnable(PREVIOUS)).start();
    }

    private class MusicRunnable implements Runnable {
        private int mType;

        public MusicRunnable(int type) {
            mType = type;
        }
        @Override
        public void run() {
            switch (mType) {
                case PREVIOUS: {
                    Instrumentation mInst = new Instrumentation();
                    mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                }
                break;
                case NEXT: {
                    Instrumentation mInst = new Instrumentation();
                    mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
                }
                break;
                case START_OR_STOP:{
                    if (isPlaying) {
                        Instrumentation mInst = new Instrumentation();
                        mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PAUSE);
                        isPlaying = false;
                    } else {
                        Instrumentation mInst = new Instrumentation();
                        mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PLAY);
                        isPlaying = true;
                    }
                }
            }
        }
    }
}
