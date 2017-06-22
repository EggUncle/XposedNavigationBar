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
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;
import com.egguncle.xposednavigationbar.util.ImageUtil;

import java.util.Map;

/**
 * Created by egguncle on 17-6-21.
 * <p>
 * 音量控制面板
 */

public class MusicControllerPanel extends LinearLayout {
    private Map<Integer, byte[]> mMapImgRes;
    private int mIconScale;
    private Context mContext;

    public MusicControllerPanel(Context context) {
        this(context, null);
    }

    public MusicControllerPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicControllerPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(HORIZONTAL);
        this.mContext=context;
    }

    public void setData( Map<Integer, byte[]> mapImgRes, int iconScale){
        this.mMapImgRes=mapImgRes;
        this.mIconScale=iconScale;
    }

    public void initPanel(){
        initView(mContext,mMapImgRes,mIconScale);
    }


    /**
     * 初始化面板按钮
     */
    private void initView(Context context, Map<Integer, byte[]> mapImgRes, int iconScale) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        p.gravity = Gravity.CENTER;
        //   XposedBridge.log("====" + iconScale);
        //  p.width= (int) (p.width*(iconScale/100.0));
        ImageButton previousBtn = new ImageButton(context);
        ImageButton playBtn = new ImageButton(context);
        ImageButton nextBtn = new ImageButton(context);
        if (iconScale != 100) {
            previousBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(FuncName.FUNC_PREVIOUS_PLAY_CODE), iconScale));
            playBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(FuncName.FUNC_PLAY_MUSIC_CODE), iconScale));
            nextBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(FuncName.FUNC_NEXT_PLAY_CODE), iconScale));
        } else {
            previousBtn.setImageBitmap(ImageUtil.byte2Bitmap(mapImgRes.get(FuncName.FUNC_PREVIOUS_PLAY_CODE)));
            playBtn.setImageBitmap(ImageUtil.byte2Bitmap(mapImgRes.get(FuncName.FUNC_PLAY_MUSIC_CODE)));
            nextBtn.setImageBitmap(ImageUtil.byte2Bitmap(mapImgRes.get(FuncName.FUNC_NEXT_PLAY_CODE)));
        }
        previousBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        playBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        nextBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        previousBtn.setBackgroundColor(Color.alpha(255));
        playBtn.setBackgroundColor(Color.alpha(255));
        nextBtn.setBackgroundColor(Color.alpha(255));

        previousBtn.setOnClickListener(new BtnMusicController(BtnMusicController.PREVIOUS));
        playBtn.setOnClickListener(new BtnMusicController(BtnMusicController.START_OR_STOP));
        nextBtn.setOnClickListener(new BtnMusicController(BtnMusicController.NEXT));

        this.addView(previousBtn, p);
        this.addView(playBtn, p);
        this.addView(nextBtn, p);
    }

}
