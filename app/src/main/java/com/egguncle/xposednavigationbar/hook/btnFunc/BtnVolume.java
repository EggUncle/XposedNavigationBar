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

import android.content.Context;
import android.media.AudioManager;

import com.egguncle.xposednavigationbar.hook.hookFunc.LightAndVolumeController;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnVolume extends LightAndVolumeController {
    public BtnVolume(){
        super(LightAndVolumeController.VOLUME);
    }

    @Override
    protected void control(Context context, int value) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //调整媒体声言，不播放声言也不振动
        am.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
