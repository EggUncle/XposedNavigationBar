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

package com.egguncle.xposednavigationbar.hook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnBackLight;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnClearAllNotifications;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnClearBackground;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnMusicNext;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnMusicStartOrStop;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnNavBarGoHome;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnOpenActPanel;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnQuickNotice;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnScreenOff;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnStatusBarController;
import com.egguncle.xposednavigationbar.hook.btnFunc.BtnVolume;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by egguncle on 17-6-10.
 */

public class BtnFuncFactory {
    private int mIconScale;
    private ViewGroup mRootViewGroup;
    private ViewPager mViewPager;
    //用于加载图片资源
    private Map<String, byte[]> mMapImgRes;

    public BtnFuncFactory(int iconscale,
                          ViewGroup rootViewGroup,
                          ViewPager viewPager,
                          Map<String, byte[]> mapImgRes ){
        mIconScale=iconscale;
        mRootViewGroup=rootViewGroup;
        mMapImgRes=mapImgRes;
        mViewPager=viewPager;
    }


    public View.OnClickListener getBtnFuncOfName(String name){
        switch (name) {
            case FuncName.BACK:
                break;
            case FuncName.DOWN:
                return  new BtnStatusBarController();
            case FuncName.QUICK_NOTICE:
                return new BtnQuickNotice();
            case FuncName.CLEAR_NOTIFICATION:
                return new BtnClearAllNotifications();
            case FuncName.SCREEN_OFF:
                return new BtnScreenOff();
            case FuncName.CLEAR_MEM:
               return new BtnClearBackground();
            case FuncName.VOLUME:
                return new BtnVolume(mRootViewGroup,byte2Bitmap(mMapImgRes.get(FuncName.BACK)));
            case FuncName.LIGHT:
                return new BtnBackLight(mRootViewGroup,byte2Bitmap(mMapImgRes.get(FuncName.BACK)));
            case FuncName.HOME:
               return new BtnNavBarGoHome(mViewPager);
            case FuncName.START_ACTS:
                return new BtnOpenActPanel();
            case FuncName.NEXT_PLAY:
                return new BtnMusicNext();
            case FuncName.PLAY_MUSIC:
                return new BtnMusicStartOrStop();
        }
        return null;
    }

    /**
     * 创建按钮并且设置对应功能
     *
     * @param context
     * @param name    按钮功能的标识名称
     */
    public void createBtnAndSetFunc(final Context context, LinearLayout line, final String name) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        p.gravity = Gravity.CENTER;
        //   XposedBridge.log("====" + iconScale);
        //  p.width= (int) (p.width*(iconScale/100.0));
        ImageButton btn = new ImageButton(context);
        if (mIconScale != 100) {
            btn.setImageBitmap(zoomBitmap(mMapImgRes.get(name), mIconScale));
        } else {
            btn.setImageBitmap(byte2Bitmap(mMapImgRes.get(name)));
        }
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        btn.setBackgroundColor(Color.alpha(255));
        btn.setOnClickListener(getBtnFuncOfName(name));
        line.addView(btn, p);
    }

    private  Bitmap byte2Bitmap(byte[] imgBytes) {
        return BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
    }

    /**
     * 缩放图片
     *
     * @param bmByte
     * @param scale
     * @return
     */
    private  Bitmap zoomBitmap(byte[] bmByte, int scale) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);

        Matrix matrix = new Matrix();
        matrix.postScale((float) (scale / 100.0), (float) (scale / 100.0));
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return bitmap;
    }


}
