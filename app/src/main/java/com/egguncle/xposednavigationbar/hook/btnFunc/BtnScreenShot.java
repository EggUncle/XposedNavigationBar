/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle
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
import android.view.View;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.hookFunc.ScreenShot;

import java.io.File;
import java.io.IOException;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-18.
 */

public class BtnScreenShot implements ScreenShot,View.OnClickListener {
    @Override
    public void onClick(View view) {
        try {
            screenshot(view.getContext());
        }catch (Exception e){
            XposedBridge.log(e.getMessage());
        }
    }

    @Override
    public void screenshot(Context context) {
        String screenShotPath="/sdcard/Pictures/Screenshots";
        File file=new File(screenShotPath);
        //如果截图文件夹不存在则创建
        if (!file.exists()){
            file.mkdirs();
        }
        long timecurrentTimeMillis = System.currentTimeMillis();
        String cmd="screencap -p /sdcard/Pictures/Screenshots/"+timecurrentTimeMillis+".png";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
