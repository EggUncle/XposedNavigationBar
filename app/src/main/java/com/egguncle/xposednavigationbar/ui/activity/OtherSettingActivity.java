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

package com.egguncle.xposednavigationbar.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.util.HookUtil;
import com.egguncle.xposednavigationbar.util.SPUtil;

public class OtherSettingActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "OtherSettingActivity";

    private LinearLayout btnHomePoint;
    private TextView tvHomePosition;
    private LinearLayout btnClearMemLevel;
    private TextView tvClearMemLevel;
    private LinearLayout btnIconSize;
    private TextView tvIconSize;
    //  private Switch swHook90;
    private Switch swRootDown;

    private SPUtil spUtil;

    private String[] homePointStr = {
            FuncName.LEFT,
            FuncName.RIGHT,
            FuncName.DISMISS
    };

    private String[] clearMemLevels = {
            "50", "100", "130", "200", "170", "300", "400", "500"
    };

    @Override
    int getLayoutId() {
        return R.layout.activity_other_setting;
    }

    @Override
    void initView() {
        getSupportActionBar().setTitle(getResources().getString(R.string.setting_other));
        btnHomePoint = (LinearLayout) findViewById(R.id.btn_home_point);
        tvHomePosition = (TextView) findViewById(R.id.tv_home_position);
        btnClearMemLevel = (LinearLayout) findViewById(R.id.btn_clear_mem_level);
        tvClearMemLevel = (TextView) findViewById(R.id.tv_clear_mem_level);
        btnIconSize = (LinearLayout) findViewById(R.id.btn_icon_size);
        tvIconSize = (TextView) findViewById(R.id.tv_icon_size);
        //  swHook90 = (Switch) findViewById(R.id.sw_hook_90);
        swRootDown = (Switch) findViewById(R.id.sw_root_down);
    }

    @Override
    void initVar() {
        spUtil = SPUtil.getInstance(this);
        int homePositon = spUtil.getHomePointPosition();
        tvHomePosition.setText(homePointStr[homePositon]);
        int clearMemLevel = spUtil.getClearMemLevel();
        tvClearMemLevel.setText(clearMemLevel + "");
        int iconSize = spUtil.getIconSize();
        tvIconSize.setText(iconSize + "");
        boolean isRootDown = spUtil.getRootDown();
        swRootDown.setChecked(isRootDown);
//        boolean isHook90=spUtil.getHookHorizontal();
//        swHook90.setChecked(isHook90);
    }

    @Override
    void initAction() {
        btnHomePoint.setOnClickListener(this);
        btnClearMemLevel.setOnClickListener(this);
        btnIconSize.setOnClickListener(this);
//        swHook90.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                spUtil.setHookHorizontal(isChecked);
//            }
//        });
        swRootDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spUtil.setRootDown(isChecked);
                Intent intent = new Intent();
                intent.putExtra(HookUtil.USE_ROOT_EXPAND_STATUS_BAR, isChecked);
                intent.setAction(HookUtil.ACT_CHANGE_ROOT_EXPAND_STATUS_BAR);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_point: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(OtherSettingActivity.this);
                builder.setTitle(getResources().getString(R.string.need_reboot))
                        .setSingleChoiceItems(homePointStr, 0, new DialogInterface.OnClickListener() {
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(OtherSettingActivity.this);
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
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_icon_size, null);
                final TextView tvImgSize = (TextView) dialogView.findViewById(R.id.tv_img_size);
                final SeekBar skImgSize = (SeekBar) dialogView.findViewById(R.id.sk_img_size);
                //设置范围30～150
                skImgSize.setMax(120);
                int nowSize = Integer.parseInt(tvIconSize.getText().toString());
                skImgSize.setProgress(nowSize - 30);
                tvImgSize.setText(nowSize + " %");
                skImgSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        tvImgSize.setText(30 + i + " %");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(OtherSettingActivity.this);
                builder.setTitle(getResources().getString(R.string.need_reboot))
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int imgSize = skImgSize.getProgress() + 30;
                                tvIconSize.setText(imgSize + "");
                                spUtil.setIconSize(imgSize);
                            }
                        }).create().show();
            }
            break;
        }
    }
}
