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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.egguncle.xposednavigationbar.hook.hookFunc.VolumeController;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnVolume implements VolumeController,View.OnClickListener {
    private ViewGroup mRootGroup;
    private Bitmap mBackBitmap;
    public BtnVolume(ViewGroup rootGroup,Bitmap backBitmap){
        mRootGroup=rootGroup;
        mBackBitmap=backBitmap;
    }

    @Override
    public void onClick(View view) {
        final ViewGroup  mViewGroup=new LinearLayout(view.getContext());
        LinearLayout.LayoutParams btnParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParam.weight = 1;
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams seekBarParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        seekBarParam.weight = 2;
        seekBarParam.gravity = Gravity.CENTER_VERTICAL;

        mViewGroup.setBackgroundColor(Color.BLACK);
        mRootGroup.addView(mViewGroup);
        ImageButton btnBack = new ImageButton(view.getContext());
        btnBack.setImageBitmap(mBackBitmap);
        btnBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBack.setBackgroundColor(Color.alpha(255));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRootGroup.removeView(mViewGroup);
            }
        });
        SeekBar seekBar = new SeekBar(view.getContext());
        //获取当前媒体并设置
        int nowLight = ((AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(nowLight);
        //获取最大音量值
        final AudioManager am = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(maxVolume);
        XpLog.i("max volume is " + maxVolume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setVolume(am, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mViewGroup.addView(btnBack, btnParam);
        mViewGroup.addView(seekBar, seekBarParam);
    }

    @Override
    public void setVolume(AudioManager am, int volume) {
        //调整媒体声言，不播放声言也不振动
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
