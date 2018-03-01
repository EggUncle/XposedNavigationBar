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
import android.view.View;

import com.egguncle.xposednavigationbar.hook.util.XpLog;
import com.egguncle.xposednavigationbar.hook.util.XpNavbarThreadPool;

/**
 * Created by egguncle on 17-6-25.
 */

public abstract class StartCommand extends NavbarBaseClick {
    private String mCommand;

    protected abstract void startCommand(String command,Context context);

    public StartCommand(String command) {
        mCommand = command;
        XpLog.i(command+"----");
    }

    @Override
    void onBaseClick(final View v) {
        XpNavbarThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                startCommand(mCommand,v.getContext());
            }
        });
    }
}
