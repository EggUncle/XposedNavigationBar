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
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.egguncle.xposednavigationbar.hook.hookFunc.BacklightController;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnBackLight implements BacklightController,View.OnClickListener {
    private ViewGroup mRootGroup;
    private Bitmap mBitmap;

    public BtnBackLight(ViewGroup rootGroup,Bitmap bitmap){
        mRootGroup=rootGroup;
        mBitmap=bitmap;
    }
    
    @Override
    public void onClick(final View view) {
        final ViewGroup  mViewGroup=new LinearLayout(view.getContext());
        LinearLayout.LayoutParams btnParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParam.weight = 1;
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams seekBarParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        seekBarParam.weight = 2;
        seekBarParam.gravity = Gravity.CENTER_VERTICAL;

        mRootGroup.addView(mViewGroup);
        ImageButton btnBack = new ImageButton(view.getContext());
        btnBack.setImageBitmap(mBitmap);
        btnBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBack.setBackgroundColor(Color.alpha(255));

        mViewGroup.setBackgroundColor(Color.BLACK);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRootGroup.removeView(mViewGroup);
            }
        });
        SeekBar seekBar = new SeekBar(view.getContext());
        final int screenMode = Settings.System.getInt(view.getContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
        // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度
        if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(view.getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        //获取当前亮度并设置
        int nowLight = Settings.System.getInt(view.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        seekBar.setProgress(nowLight);
        //亮度最小为30,最大为255
        seekBar.setMax(225);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setBackgroundLight(view.getContext(), i + 30);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //设置回原来的亮度模式
                Settings.System.putInt(view.getContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, screenMode);
            }
        });
        mViewGroup.addView(btnBack, btnParam);
        mViewGroup.addView(seekBar, seekBarParam);
    }
    
    

    @Override
    public void setBackgroundLight(Context context, int light) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            Method setBacklightBrightness = pm.getClass().getMethod("setBacklightBrightness", int.class);
            setBacklightBrightness.setAccessible(true);
            setBacklightBrightness.invoke(pm, light);
            XpLog.i("=====setBacklightBrightness");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            XpLog.i(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XpLog.i(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            XpLog.i(e.getMessage());
        }
    }
}
