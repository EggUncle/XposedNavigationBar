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

package com.egguncle.xposednavigationbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egguncle on 17-6-11.
 */

public class DialogItemAdapter extends RecyclerView.Adapter<DialogItemAdapter.DialogViewHolder> {
    private final static String TAG = "DialogItemAdapter";

    //传入的数据
    private List<AppInfo> appInfoData;
    //要添加的app
    private List<AppInfo> selectedData;

    private PackageManager pm;
    private Context mContext;

    // private Map<Integer,Boolean> selectMap;

    public DialogItemAdapter(Context context, List<AppInfo> data) {
        mContext = context;
        appInfoData = data;
        //     selectMap=new HashMap<>();
        selectedData = new ArrayList<>();
        pm = mContext.getPackageManager();
    }

    @Override
    public DialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_apps, parent, false));
    }

    @Override
    public void onBindViewHolder(final DialogViewHolder holder, final int position) {
        final AppInfo appInfo = appInfoData.get(position);
        final String pkgName = appInfo.getPackgeName();
        String label = appInfo.getLabel();
        holder.tvItemDialog.setText(label);

        holder.cbItemDialog.setChecked(selectedData.contains(appInfo));
//        if (selectMap.get(position)==null){
//            selectMap.put(position,false);
//            holder.cbItemDialog.setChecked(false);
//        }else{
//            holder.cbItemDialog.setChecked(selectMap.get(position));
//        }

        final int type = appInfo.getType();
        //如果这个item是一个app的快捷启动
        if (type == AppInfo.TYPE_APP) {
            try {
                Drawable icon = pm.getApplicationIcon(pkgName);
                holder.ivItemDialog.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (type == AppInfo.TYPE_SHORT_CUT) {
            //如果这个item是一个快捷方式的快捷启动
            String shortCutName = appInfo.getShortCutName();
            int flag = appInfo.getFlag();
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(pkgName,
                    shortCutName);
            intent.addFlags(flag);

            try {
                Drawable icon = pm.getActivityIcon(intent);
                holder.ivItemDialog.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        holder.cbItemDialog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = holder.cbItemDialog.isChecked();
                //   selectMap.put(position,checked);
                if (checked) {
                       selectedData.remove(appInfo);
                    holder.cbItemDialog.setChecked(false);

                } else {
                     selectedData.add(appInfo);
                    holder.cbItemDialog.setChecked(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        int count = appInfoData == null ? 0 : appInfoData.size();
        return count;
    }


    public List<AppInfo> getSelectedData() {
        Log.i(TAG, "getSelectedData: " + selectedData.size());
        return selectedData;
    }

    public class DialogViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemDialog;
        private TextView tvItemDialog;
        private CheckBox cbItemDialog;

        public DialogViewHolder(View itemView) {
            super(itemView);
            ivItemDialog = (ImageView) itemView.findViewById(R.id.iv_item_dialog);
            tvItemDialog = (TextView) itemView.findViewById(R.id.tv_item_dialog);
            cbItemDialog = (CheckBox) itemView.findViewById(R.id.cb_item_dialog);

        }
    }
}
