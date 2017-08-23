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
import android.content.Intent;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.util.MainHookUtil;
import com.egguncle.xposednavigationbar.hook.hookFunc.NavClipboard;
import com.egguncle.xposednavigationbar.hook.util.MyClipBoard;
import com.egguncle.xposednavigationbar.hook.util.XpLog;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-23.
 */

public class BtnNavClipboard implements NavClipboard, View.OnClickListener {
    private final static String ACTION_CLIPBOARD = "com.egguncle.xposednavigationbar.ui.activity.ClipboardActivity";

    @Override
    public void onClick(View view) {
        getClipboadrData(view.getContext());
    }

    @Override
    public void getClipboadrData(Context context) {
        ArrayList<String> data = MyClipBoard.getClipboardData();
        for (String s : data) {
            XpLog.i(s);
        }
        Intent intent = new Intent(ACTION_CLIPBOARD);
        intent.putStringArrayListExtra("data",data);
        //使用这种启动标签，可以避免在打开软件本身以后再通过快捷键呼出备忘对话框时仍然显示软件的界面的bug
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void showClipboardData() {

    }
}
