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

package com.egguncle.xposednavigationbar.hook.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by egguncle on 17-8-14.
 * 剪贴板监听器
 */

public class MyClipBoard {

    //剪贴板内容
    private static ArrayList<String> clipboardData = new ArrayList<>();

    /**
     * 开始监听剪贴板
     */
    public static void startListenClipboard(final Context context) {

        final ClipboardManager clipboard = (ClipboardManager) context.
                getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                //  XpLog.i("onPrimaryClipChanged");
                //获取剪贴板内容，先判断该内容是否为空
                if (clipboard.hasPrimaryClip()) {
                    ClipData clipData = clipboard.getPrimaryClip();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; ++i) {

                        ClipData.Item item = clipData.getItemAt(i);
                        CharSequence str = item
                                .coerceToText(context);
                        //因为复制历史记录里面某一条文字到剪贴板的时候，也会导致剪贴板内容变动，此处避免 添加重复内容到剪贴板历史
                        if (!clipboardData.contains(str.toString())) {
                            clipboardData.add(str.toString());
                        }
                    }
                }
            }
        });
    }

    public static ArrayList<String> getClipboardData() {
        return clipboardData;
    }
}
