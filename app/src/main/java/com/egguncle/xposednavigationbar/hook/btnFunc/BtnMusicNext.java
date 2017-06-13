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

import android.media.session.MediaSession;
import android.view.View;

import com.egguncle.xposednavigationbar.hook.hookFunc.MusicController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-12.
 */

public class BtnMusicNext implements MusicController, View.OnClickListener {
    private final static String TAG = "BtnMusicNext";

    @Override
    public void onClick(View view) {
       // nextMusic(HookUtil.getMediaSession());
    }

    @Override
    public void nextMusic(MediaSession session) {
        XposedBridge.log("nextMusic: ");
//        Object mcb=HookUtil.getMcb();
//        Handler h= (Handler) mcb;
//        Message msg=Message.obtain();
//        msg.what=7;
//        h.sendMessage(msg);
//        XposedBridge.log("next method success");
        if (session != null) {
            //反射获取下一曲方法
            try {
                Method next = MediaSession.class.getDeclaredMethod("dispatchNext");
                next.setAccessible(true);
                //调用下一曲方法
                next.invoke(session);
                XposedBridge.log("next method success");
            } catch (NoSuchMethodException e) {
                XposedBridge.log(e.getMessage());
            } catch (InvocationTargetException e) {
                XposedBridge.log(e.getMessage());
            } catch (IllegalAccessException e) {
                XposedBridge.log(e.getMessage());
            }
        }else{
            XposedBridge.log("next music is null ");
        }


    }

    @Override
    public void startOrPauseMusic(MediaSession session) {

    }
}
