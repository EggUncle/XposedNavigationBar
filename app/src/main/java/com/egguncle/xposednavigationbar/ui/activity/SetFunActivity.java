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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.hook.util.HookUtil;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;
import com.egguncle.xposednavigationbar.ui.adapter.RcvHomeAdapter;
import com.egguncle.xposednavigationbar.ui.touchHelper.MyItemTouchHelpCallBack;
import com.egguncle.xposednavigationbar.ui.touchHelper.MyItemTouchHelper;
import com.egguncle.xposednavigationbar.util.SPUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetFunActivity extends BaseActivity {
    private final static String TAG = "SetFunActivity";

    private RecyclerView rcvSetting;
    private FloatingActionButton fabSetting;
    private CoordinatorLayout parentView;

    private RcvHomeAdapter rcvHomeAdapter;
    private ArrayList<ShortCut> shortCutList;

    //一个被设置和没被设置的处理逻辑
    private List<Integer> selectList;
    //  private List<String> notSelectList;

    private SPUtil spUtil;
    private boolean tapsNotAppear;

    private  String[] funcs ;

    @Override
    int getLayoutId() {
        return R.layout.activity_set_fun;
    }

    @Override
    void initView() {
        getSupportActionBar().setTitle(getResources().getString(R.string.setting_btn));
        rcvSetting = (RecyclerView) findViewById(R.id.rcv_setting);
        rcvSetting.setLayoutManager(new LinearLayoutManager(this));
        fabSetting = (FloatingActionButton) findViewById(R.id.fab_setting);
        parentView = (CoordinatorLayout) findViewById(R.id.parent_view);


    }

    @Override
    void initVar() {
        selectList = new ArrayList<>();
        //notSelectList = new ArrayList<>();
        shortCutList = new ArrayList<>();
        rcvHomeAdapter = new RcvHomeAdapter(this,shortCutList);
        rcvSetting.setAdapter(rcvHomeAdapter);
        //设置rcv可拖动
        MyItemTouchHelper myItemTouchHelper = new MyItemTouchHelper(onItemTouchCallbackListener);
        myItemTouchHelper.attachToRecyclerView(rcvSetting);

        //获取原有的设置数据
        spUtil = SPUtil.getInstance(this);
        List<ShortCut> list = spUtil.getAllShortCutData();
        if (list != null && list.size() != 0) {
            shortCutList.addAll(list);
            rcvHomeAdapter.notifyDataSetChanged();
//            for (ShortCut sc : shortCutList) {
//                selectList.add(sc.getName());
//            }
        }
//        for (String s : funcs) {
//            if (!list.contains(s)) {
//                notSelectList.add(s);
//            }
//        }

        funcs = getResources().getStringArray(R.array.shortcut_names);
    }

    @Override
    void initAction() {
        tapsNotAppear = spUtil.getTapsStatus();
        if (tapsNotAppear) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_taps, null);
            final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkBox);
            AlertDialog dialogTaps = new AlertDialog.Builder(SetFunActivity.this)
                    .setTitle(getResources().getString(R.string.taps))
                    .setView(dialogView)
                    .setPositiveButton(getResources().getString(R.string.i_know), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (checkBox.isChecked()) {
                                        spUtil.nolongerTaps();
                                        Log.i(TAG, "onClick: no longer appear");
                                    }
                                }
                            }
                    ).create();
            dialogTaps.show();
        }

        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectList.clear();
                //   if (notSelectList.size() != 0) {
                AlertDialog dialog = new AlertDialog.Builder(SetFunActivity.this)
                        .setTitle(getResources().getString(R.string.select_shortcut))
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //限制最大按钮数
                                if (selectList.size() > 10) {

                                } else {
                                    //将设置的都加到select中，remove notselect中对应内容
                                    for (Integer code : selectList) {
                                        Log.i(TAG, "onClick: " + code);
                                        addToShortCutList(code);
                                        //   notSelectList.remove(s);
                                    }
                                    rcvHomeAdapter.notifyDataSetChanged();
                                }

                            }
                        })
                        .setMultiChoiceItems(funcs, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                //String name = funcs[which];
                                if (isChecked) {
                                    selectList.add(which);
                                } else {
                                    selectList.remove((Integer)which);
                                }
                            }
                        }).create();
                dialog.show();
//                } else {
//                    Snackbar.make(parentView, getResources().getString(R.string.no_select), Snackbar.LENGTH_SHORT).show();
//                }
            }
        });
    }

    /**
     * 根据选择的内容，来向shortcutlist添加数据
     *
     * @param code  对应的功能码
     */
    private void addToShortCutList(int  code) {
        if (shortCutList.size() >= 10) {
            Snackbar.make(parentView,
                    getResources().getString(R.string.taps_too_mang_sc),
                    Snackbar.LENGTH_SHORT).show();
        } else {

//            Log.i(TAG, "addToShortCutList: ");
//            if (!"".equals(shortCutName)) {
                Log.i(TAG, "addToShortCutList: add ");
                ShortCut sc = new ShortCut();
                sc.setCode(code);
             //   sc.setShortCutName(shortCutName);
                shortCutList.add(sc);
            //}
        }
    }

    private MyItemTouchHelpCallBack.OnItemTouchCallbackListener onItemTouchCallbackListener = new MyItemTouchHelpCallBack.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            // 滑动删除的时候，从数据源移除，并刷新这个Item。
            if (shortCutList != null) {
                shortCutList.remove(adapterPosition);
                rcvHomeAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (shortCutList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(shortCutList, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                rcvHomeAdapter.notifyItemMoved(srcPosition, targetPosition);
//                for (ShortCut sc : shortCutList) {
//                    Log.i(TAG, "onMove: " + sc.getName());
//                }
                Log.i(TAG, "onMove: ---");
                return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.set_fun_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_save) {
            //创建快捷方式名字的list，发送广播便于即时更新，免于重启
            ArrayList<Integer> shortCutCodes = new ArrayList<>();
            for (int i = 0; i < shortCutList.size(); i++) {
                ShortCut sc = shortCutList.get(i);
                sc.setPostion(i);
                //暂时只有一页
                sc.setPage(0);
                sc.setOpen(true);

                shortCutCodes.add(sc.getCode());
            }
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("data", shortCutList);
            int iconSize=spUtil.getIconSize();
            int homePosition=spUtil.getHomePointPosition();
            boolean rootDown=spUtil.getRootDown();
            XpNavBarSetting setting=new XpNavBarSetting(shortCutList,homePosition,iconSize,rootDown);
            intent.putExtra("data",setting);
            intent.setAction(HookUtil.ACT_NAV_BAR_DATA);
            sendBroadcast(intent);

            spUtil.saveShortCut(shortCutList);
            Snackbar.make(parentView, getResources().getString(R.string.save_success), Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case SetFunActivity.RESULT_OK:

                int position=data.getIntExtra("position",0);
                String imgPath=data.getStringExtra("imagepath");
                String command=data.getStringExtra("command");
                ShortCut sc=shortCutList.get(position);
                if (command!=null){
                    sc.setShellStr(command);
                }
                Log.i(TAG, "onActivityResult: "+position+" "+imgPath);

                sc.setIconPath(imgPath);
                break;
        }
    }
}
