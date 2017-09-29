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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.hook.hookutil.DataHook;
import com.egguncle.xposednavigationbar.util.ImageUtil;

/**
 * Created by egguncle on 17-9-20.
 */

public abstract class LightAndVolumeController extends VibrateClick {
    public static final int LIGHT = 1;
    public static final int VOLUME = 2;

    private Bitmap backBitmap;
    private Bitmap funcBitmap;

    private static ViewGroup lightPanel;
    private static ViewGroup volumePanel;

    protected abstract void control(Context context, int value);

    private int mType;

    public LightAndVolumeController(int type) {
        mType = type;
        if (type == LIGHT) {
            funcBitmap = ImageUtil.byte2Bitmap(DataHook.mapImgRes.get(ConstantStr.FUNC_LIGHT_CODE));
        } else {
            funcBitmap = ImageUtil.byte2Bitmap(DataHook.mapImgRes.get(ConstantStr.FUNC_VOLUME_CODE));
        }
        backBitmap = ImageUtil.byte2Bitmap(DataHook.mapImgRes.get(ConstantStr.FUNC_BACK_CODE));
        backBitmap = ImageUtil.zommBitmap(backBitmap, DataHook.iconScale);
        funcBitmap = ImageUtil.zommBitmap(funcBitmap, DataHook.iconScale);
    }

    @Override
    void onVibrateClick(View v) {
        showDialog(v.getContext());
    }

    protected void showDialog(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int h = getNavbarHeight(context);
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(w, h, WindowManager.LayoutParams.TYPE_TOAST, 0, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.BOTTOM;

        ViewGroup viewGroup;
        if (mType == LIGHT) {
            viewGroup = getLightPanel(context);
        } else {
            viewGroup = getVolumePanel(context);
        }

        if (lightPanel != null && lightPanel.isAttachedToWindow()) {
            wm.removeView(lightPanel);
        }
        if (volumePanel != null && volumePanel.isAttachedToWindow()) {
            wm.removeView(volumePanel);
        }

        wm.addView(viewGroup, layoutParams);
    }

    private ViewGroup getPanel(Context context, int type) {
        final ViewGroup mViewGroup = new LinearLayout(context);
        LinearLayout.LayoutParams btnParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //   btnParam.weight = 1;
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams seekBarParam =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        seekBarParam.weight = 1;
        seekBarParam.gravity = Gravity.CENTER;

        ImageButton btnBack = new ImageButton(context);
        btnBack.setImageBitmap(backBitmap);
        btnBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnBack.setBackgroundColor(Color.alpha(255));

        SeekBar seekBar = getSeekBar(context, type);

        ImageButton btnFunc = new ImageButton(context);
        btnFunc.setImageBitmap(funcBitmap);
        btnFunc.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnFunc.setBackgroundColor(Color.alpha(255));

        mViewGroup.addView(btnBack, btnParam);
        mViewGroup.addView(seekBar, seekBarParam);
        mViewGroup.addView(btnFunc, btnParam);

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mViewGroup.setBackgroundColor(Color.BLACK);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(mViewGroup);
            }
        });

        return mViewGroup;
    }

    private synchronized ViewGroup getVolumePanel(Context context) {
        if (lightPanel == null) {
            lightPanel = getPanel(context, VOLUME);

        }
        return lightPanel;
    }

    private synchronized ViewGroup getLightPanel(Context context) {
        if (volumePanel == null) {
            volumePanel = getPanel(context, LIGHT);
        }
        return volumePanel;
    }

    private int getNavbarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private SeekBar getSeekBar(Context context, int type) {
        if (type == VOLUME) {
            return getVolumeSeekBar(context);
        } else {
            return getLightSeekBar(context);
        }
    }

    private SeekBar getVolumeSeekBar(final Context context) {
        SeekBar seekBar = new SeekBar(context);
        //获取当前媒体并设置
        int nowLight = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(nowLight);
        //获取最大音量值
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(maxVolume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                control(context, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return seekBar;
    }

    private SeekBar getLightSeekBar(final Context context) {
        SeekBar seekBar = new SeekBar(context);
        final int screenMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
        // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度
        if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        //获取当前亮度并设置
        int nowLight = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        seekBar.setProgress(nowLight);
        //亮度最小为30,最大为255
        seekBar.setMax(225);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                control(context, i + 30);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mType == LIGHT) {
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE, screenMode);
                }
            }
        });

        return seekBar;
    }
}
