/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2018 egguncle cicadashadow@gmail.com
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

package com.egguncle.xposednavigationbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.egguncle.xposednavigationbar.constant.ConstantStr.COMMAND_STR;

public class XpNavbarCommandReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String command = intent.getStringExtra(COMMAND_STR);
        try {
            Log.i("XpNavBar","startCommand: " + command);
            Process p = Runtime.getRuntime().exec(command);
            Toast.makeText(context,"run command success",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"run command failed",Toast.LENGTH_SHORT).show();
        }
    }
}
