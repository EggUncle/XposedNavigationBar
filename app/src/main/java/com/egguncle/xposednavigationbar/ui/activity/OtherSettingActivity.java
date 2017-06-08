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

package com.egguncle.xposednavigationbar.ui.activity;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.util.SPUtil;

public class OtherSettingActivity extends BaseActivity {
    private LinearLayout btnHomePoint;
    private TextView tvHomePosition;

    private SPUtil spUtil;

    private String[] homePointStr = {
            FuncName.LEFT,
            FuncName.RIGHT,
            FuncName.DISMISS
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

    }

    @Override
    void initVar() {
        spUtil = SPUtil.getInstance(this);
        String homePositon = spUtil.getHomePointPosition();
        tvHomePosition.setText(homePositon);
    }

    @Override
    void initAction() {
        btnHomePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(OtherSettingActivity.this);
                builder.setTitle(getResources().getString(R.string.need_reboot))
                        .setSingleChoiceItems(homePointStr, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                spUtil.setHomePointPosition(homePointStr[i]);
                                tvHomePosition.setText(homePointStr[i]);
                            }
                        }).setPositiveButton("确定",null);
                builder.create().show();
            }
        });
    }
}
