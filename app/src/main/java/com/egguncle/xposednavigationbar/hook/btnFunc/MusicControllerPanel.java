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
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.hook.hookutil.MainHookUtil;
import com.egguncle.xposednavigationbar.hook.util.XpLog;
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
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        p.gravity = Gravity.CENTER;
        //   XpLog.i("====" + iconScale);
        //  p.width= (int) (p.width*(iconScale/100.0));
        ImageView previousBtn = new ImageView(context);
        ImageView playBtn = new ImageView(context);
        ImageView nextBtn = new ImageView(context);

        previousBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(ConstantStr.FUNC_PREVIOUS_PLAY_CODE), iconScale));
        playBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(ConstantStr.FUNC_PLAY_MUSIC_CODE), iconScale));
        nextBtn.setImageBitmap(ImageUtil.zoomBitmap(mapImgRes.get(ConstantStr.FUNC_NEXT_PLAY_CODE), iconScale));

        previousBtn.setScaleType(ImageView.ScaleType.CENTER);
        playBtn.setScaleType(ImageView.ScaleType.CENTER);
        nextBtn.setScaleType(ImageView.ScaleType.CENTER);

        try {
            previousBtn.setBackground(context.getResources().getDrawable(MainHookUtil.getBtnBgResId(), context.getTheme()));
            playBtn.setBackground(context.getResources().getDrawable(MainHookUtil.getBtnBgResId(), context.getTheme()));
            nextBtn.setBackground(context.getResources().getDrawable(MainHookUtil.getBtnBgResId(), context.getTheme()));
        } catch (Exception e) {
            XpLog.e(e);
        }


        previousBtn.setOnClickListener(new BtnMusicController(BtnMusicController.PREVIOUS));
        playBtn.setOnClickListener(new BtnMusicController(BtnMusicController.START_OR_STOP));
        nextBtn.setOnClickListener(new BtnMusicController(BtnMusicController.NEXT));

        Space sp11= new Space(context);
        Space sp12 = new Space(context);
        Space sp21= new Space(context);
        Space sp22 = new Space(context);
        Space sp31= new Space(context);
        Space sp32 = new Space(context);

        this.addView(sp11,p);
        this.addView(previousBtn, p);
        this.addView(sp12,p);

        this.addView(sp21,p);
        this.addView(playBtn, p);
        this.addView(sp22,p);

        this.addView(sp31,p);
        this.addView(nextBtn, p);
        this.addView(sp32,p);
    }

    /**
     * 更新音乐控制面板的图标大小
     * @param iconScale
     */
    public void updateIconSize(int iconScale){
        this.removeAllViews();
        initView(mContext,mMapImgRes,iconScale);
        XpLog.i("update musicpanel iconscale:"+iconScale);
    }

}
