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
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CheckBox;

import com.egguncle.xposednavigationbar.BuildConfig;
import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.constant.ConstantStr;
import com.egguncle.xposednavigationbar.constant.XpNavBarAction;
import com.egguncle.xposednavigationbar.hook.util.MainHookUtil;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.model.XpNavBarSetting;
import com.egguncle.xposednavigationbar.ui.fragment.MomoFragment;
import com.egguncle.xposednavigationbar.ui.fragment.SetFunFragment;
import com.egguncle.xposednavigationbar.ui.fragment.SetOtherFragment;
import com.egguncle.xposednavigationbar.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private final static String TAG = HomeActivity.class.getName();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FloatingActionButton fabAddFunc;
    private SetFunFragment setFunFragment;
    private SetOtherFragment setOtherFragment;
    private MomoFragment momoFragment;

    private String[] funcs;
    private String[] languages = {"简体中文", "English"};

    private List<Integer> selectList;
    private ArrayList<ShortCut> shortCutList;
    private SPUtil spUtil;

    private ViewPager mViewPager;


    @Override
    int getLayoutId() {
        return R.layout.a_home;
    }

    @Override
    void initView() {
        checkActivated();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabAddFunc = (FloatingActionButton) findViewById(R.id.fab);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    void initVar() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        funcs = getResources().getStringArray(R.array.shortcut_names);
        setFunFragment = new SetFunFragment();
        setOtherFragment = new SetOtherFragment();
        momoFragment = new MomoFragment();
        selectList = new ArrayList<>();
        spUtil = SPUtil.getInstance(this);
        shortCutList = spUtil.getAllShortCutData();
    }

    @Override
    void initAction() {
        Boolean tapsNotAppear = spUtil.getTapsStatus();
        if (tapsNotAppear) {
            View dialogView = getLayoutInflater().inflate(R.layout.d_taps, null);
            final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkBox);
            AlertDialog dialogTaps = new AlertDialog.Builder(this)
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

        fabAddFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   if (notSelectList.size() != 0) {
                selectList.clear();
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle(getResources().getString(R.string.select_shortcut))
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setFunFragment.coverData(selectList);
                            }
                        })
                        .setMultiChoiceItems(funcs, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                //String name = funcs[which];
                                if (isChecked) {
                                    selectList.add(which);
                                } else {
                                    selectList.remove((Integer) which);
                                }
                            }
                        }).create();
                dialog.show();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    fabAddFunc.hide();
                } else {
                    fabAddFunc.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save: {
                shortCutList = setFunFragment.getShortCutCodes();
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("data", shortCutList);
                int iconSize = spUtil.getIconSize();
                int homePosition = spUtil.getHomePointPosition();
                boolean rootDown = spUtil.getRootDown();
                int clearMemLevel = spUtil.getClearMemLevel();
                XpNavBarSetting setting = new XpNavBarSetting(shortCutList, homePosition, iconSize, rootDown, clearMemLevel);
                intent.putExtra("data", setting);
                intent.setAction(XpNavBarAction.ACT_NAV_BAR_DATA);
                sendBroadcast(intent);
                spUtil.saveShortCut(shortCutList);
                Snackbar.make(mViewPager, getResources().getString(R.string.save_success), Snackbar.LENGTH_SHORT).show();
                return true;
            }
            case R.id.menu_language: {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.about_language))
                        .setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "onClick: " + languages[which]);
                                if (languages[which].equals("English")) {
                                    spUtil.setLanguage(SPUtil.LANGUAGE_ENGLICH);
                                } else {
                                    spUtil.setLanguage(SPUtil.LANGUAGE_CHINESE);
                                }
                                Snackbar.make(mViewPager, getResources().getString(R.string.set_language_success), Snackbar.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
            case R.id.menu_about: {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            case R.id.menu_manual: {
                startActivity(new Intent(this, ManualActivity.class));
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case HomeActivity.RESULT_OK:

                int position = data.getIntExtra("position", 0);
                String imgPath = data.getStringExtra("imagepath");
                String command = data.getStringExtra("command");
                ShortCut sc = shortCutList.get(position);
                if (command != null) {
                    sc.setShellStr(command);
                }
                Log.i(TAG, "onActivityResult: " + position + " " + imgPath);

                sc.setIconPath(imgPath);
                break;
        }
    }

    private void checkActivated() {
        if(getActivatedVersion()!=BuildConfig.VERSION_CODE){
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.not_activated)
                    .setNegativeButton(android.R.string.ok,null)
                    .show();
        }
    }

    private int getActivatedVersion() {
        return -1;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return setFunFragment;
                case 1:
                    return setOtherFragment;
                case 2:
                    return momoFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_set_func);
                case 1:
                    return getResources().getString(R.string.tab_set_other);
                case 2:
                    return getResources().getString(R.string.tab_momo_history);
            }
            return null;
        }
    }
}
