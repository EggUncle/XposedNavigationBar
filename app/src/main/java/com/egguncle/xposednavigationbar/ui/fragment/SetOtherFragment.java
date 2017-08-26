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

package com.egguncle.xposednavigationbar.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.MyApplication;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.util.SPUtil;

/**
 * Created by egguncle on 17-8-11.
 */

public class SetOtherFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = SetOtherFragment.class.getName();

    private LinearLayout btnHomePoint;
    private TextView tvHomePosition;
    private LinearLayout btnClearMemLevel;
    private TextView tvClearMemLevel;
    private LinearLayout btnIconSize;
    private TextView tvIconSize;
    //  private Switch swHook90;
    private Switch swRootDown;
    private LinearLayout settingAboutMarshmallow;

    private SPUtil spUtil;
    private Context mContext;

    public SetOtherFragment() {
        mContext = MyApplication.getContext();
    }

    private String[] homePointStr = {
            ConstantStr.LEFT,
            ConstantStr.RIGHT,
            ConstantStr.DISMISS
    };

    private String[] clearMemLevels = {
            "50", "100", "130", "200", "170", "300", "400", "500"
    };

    @Override
    void initView(View view) {
        btnHomePoint = (LinearLayout) view.findViewById(R.id.btn_home_point);
        tvHomePosition = (TextView) view.findViewById(R.id.tv_home_position);
        btnClearMemLevel = (LinearLayout) view.findViewById(R.id.btn_clear_mem_level);
        tvClearMemLevel = (TextView) view.findViewById(R.id.tv_clear_mem_level);
        btnIconSize = (LinearLayout) view.findViewById(R.id.btn_icon_size);
        tvIconSize = (TextView) view.findViewById(R.id.tv_icon_size);
        //  swHook90 = (Switch) findViewById(R.id.sw_hook_90);
        swRootDown = (Switch) view.findViewById(R.id.sw_root_down);
        settingAboutMarshmallow = (LinearLayout) view.findViewById(R.id.setting_about_marshmallow);
    }

    @Override
    void initAction() {
        //在Android M 上有一个通知栏下拉动画缓慢的bug，这里为它添加一个设置选项，只有M可见
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            settingAboutMarshmallow.setVisibility(View.VISIBLE);
        }

        btnHomePoint.setOnClickListener(this);
        btnClearMemLevel.setOnClickListener(this);
        btnIconSize.setOnClickListener(this);
        swRootDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spUtil.setRootDown(isChecked);
            }
        });
    }

    @Override
    void initVar() {
        spUtil = SPUtil.getInstance(mContext);
        int homePositon = spUtil.getHomePointPosition();
        tvHomePosition.setText(homePointStr[homePositon]);
        int clearMemLevel = spUtil.getClearMemLevel();
        tvClearMemLevel.setText(clearMemLevel + "");
        int iconSize = spUtil.getIconSize();
        tvIconSize.setText(iconSize + "");
        boolean isRootDown = spUtil.getRootDown();
        swRootDown.setChecked(isRootDown);
    }

    @Override
    int getLayoutId() {
        return R.layout.f_set_other;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_point: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setSingleChoiceItems(homePointStr, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spUtil.setHomePointPosition(i);
                        tvHomePosition.setText(homePointStr[i]);
                    }
                }).setPositiveButton(R.string.ok, null);
                builder.create().show();
            }
            break;
            case R.id.btn_clear_mem_level: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder
                        //.setTitle(getResources().getString(R.string.need_reboot))
                        .setSingleChoiceItems(clearMemLevels, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                spUtil.setClearMemLevel(Integer.parseInt(clearMemLevels[i]));
                                Log.i(TAG, "onClick: " + clearMemLevels[i]);
                                tvClearMemLevel.setText(clearMemLevels[i]);
                            }
                        }).setPositiveButton(R.string.ok, null);
                builder.create().show();
            }
            break;
            case R.id.btn_icon_size: {
                View dialogView = View.inflate(view.getContext(), R.layout.d_icon_size, null);
                final TextView tvImgSize = (TextView) dialogView.findViewById(R.id.tv_img_size);
                final SeekBar skImgSize = (SeekBar) dialogView.findViewById(R.id.sk_img_size);
                //设置范围10～100
                skImgSize.setMax(90);
                int nowSize = Integer.parseInt(tvIconSize.getText().toString());
                skImgSize.setProgress(nowSize - 10);
                tvImgSize.setText(nowSize + " %");
                skImgSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        tvImgSize.setText(10 + i + " %");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int imgSize = skImgSize.getProgress() + 10;
                                tvIconSize.setText(imgSize + "");
                                spUtil.setIconSize(imgSize);
                            }
                        }).create().show();
            }
            break;
        }
    }
}
