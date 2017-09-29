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
import android.content.Intent;
import android.view.View;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;


/**
 * Created by egguncle on 17-6-18.
 */

public abstract class ScreenShot extends VibrateClick {
    protected Intent intent;

    protected abstract void screenshot(Context context);

    public ScreenShot() {
        intent = new Intent(XpNavBarAction.ACTION_PHONE_WINDOW_MANAGER);
        intent.putExtra(ConstantStr.TYPE, ConstantStr.TAKE_SCREENSHOT);
    }

    @Override
    void onVibrateClick(View v) {
        screenshot(v.getContext());
    }
}
