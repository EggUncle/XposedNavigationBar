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
import com.egguncle.xposednavigationbar.hook.hookutil.DataHook;
import com.egguncle.xposednavigationbar.hook.hookutil.MainHookUtil;
import com.egguncle.xposednavigationbar.hook.util.BtnFuncFactory;
import com.egguncle.xposednavigationbar.hook.util.XpLog;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.util.ImageUtil;

import java.util.Map;

/**
 * Created by egguncle on 17-6-21.
 * <p>
 * 音量控制面板
 */

public class MusicControllerPanel extends LinearLayout {

    public MusicControllerPanel(Context context) {
        this(context, null);
    }

    public MusicControllerPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicControllerPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(HORIZONTAL);
    }

    public void initPanel() {
        BtnFuncFactory btnFuncFactory = new BtnFuncFactory(this);

        ShortCut scPreMusic = new ShortCut();
        ShortCut scPlayMusic = new ShortCut();
        ShortCut scNextMusic = new ShortCut();

        scPreMusic.setCode(ConstantStr.FUNC_PREVIOUS_PLAY_CODE);
        scPlayMusic.setCode(ConstantStr.FUNC_PLAY_MUSIC_CODE);
        scNextMusic.setCode(ConstantStr.FUNC_NEXT_PLAY_CODE);

        btnFuncFactory.createBtnAndSetFunc(this, scPreMusic);
        btnFuncFactory.createBtnAndSetFunc(this, scPlayMusic);
        btnFuncFactory.createBtnAndSetFunc(this, scNextMusic);
    }

    /**
     * 更新音乐控制面板的图标大小
     */
    public void updateIconSize() {
        this.removeAllViews();
        initPanel();
    }

}
