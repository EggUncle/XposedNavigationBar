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

import android.util.Log;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.StartCommand;
import com.egguncle.xposednavigationbar.hook.util.ScheduledThreadPool;

import java.io.IOException;

/**
 * Created by egguncle on 17-6-25.
 */

public class BtnStartCommand implements StartCommand,View.OnClickListener{

    private String mCommand;

    public BtnStartCommand(String command){
        mCommand=command;
    }

    @Override
    public void onClick(View view) {
        ScheduledThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                startCommand(mCommand);
            }
        });
    }

    @Override
    public void startCommand(String command) {
        try {
            Log.i("testTag", "startCommand: "+command);
            Process p = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
