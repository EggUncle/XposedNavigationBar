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


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.ui.adapter.DialogClipAdapter;

import java.util.ArrayList;

public class ClipboardActivity extends Activity {
    private final static String TAG = "ClipboardActivity";
    private RecyclerView rcvDialogApps;
    private ArrayList<String> clipData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_clipboard);
        initView();
        initVar();
        initAction();
    }


    private void initView() {
        //状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }


    private void initVar() {
        ArrayList<String> data = getIntent().getStringArrayListExtra("data");
        clipData = new ArrayList<>();
        clipData.addAll(data);
    }


    private void initAction() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.d_clip, null);
        rcvDialogApps = (RecyclerView) dialogView.findViewById(R.id.rcv_dialog_clip);
        rcvDialogApps.setLayoutManager(new LinearLayoutManager(this));
        rcvDialogApps.setHasFixedSize(true);
        DialogClipAdapter adapter = new DialogClipAdapter(clipData);
        rcvDialogApps.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.clipboard))
                .setView(dialogView)
                .setNegativeButton(R.string.no,null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Log.i(TAG, "onDismiss: ");
                        finish();
                    }
                }).create().show();
    }
}
