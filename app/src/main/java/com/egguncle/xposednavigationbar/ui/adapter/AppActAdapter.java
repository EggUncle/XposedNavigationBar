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

package com.egguncle.xposednavigationbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.AppInfo;

import java.util.List;

/**
 * Created by egguncle on 17-6-11.
 */

public class AppActAdapter extends RecyclerView.Adapter<AppActAdapter.AppViewHolder> {
    private List<AppInfo> mAppInfoList;
    private Context mContext;
    private PackageManager pm;

    public AppActAdapter(Context context,List<AppInfo> appInfoList){
        mAppInfoList=appInfoList;
        mContext=context;
        pm=mContext.getPackageManager();
    }

    @Override
    public AppActAdapter.AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app,parent,false));
    }

    @Override
    public void onBindViewHolder(AppActAdapter.AppViewHolder holder, int position) {
        AppInfo appInfo=mAppInfoList.get(position);
        final String pkgName=appInfo.getPackgeName();
        String label=appInfo.getLabel();
        holder.itemTvAppName.setText(label);
        int type=appInfo.getType();
        //如果这个item是一个app的快捷启动
        if (type==AppInfo.TYPE_APP){
            try {
                Drawable icon=pm.getApplicationIcon(pkgName);
                holder.itemIvIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = pm.getLaunchIntentForPackage(pkgName);
                    view.getContext().startActivity(intent);
                }
            });
        }else if(type==AppInfo.TYPE_SHORT_CUT){
            //如果这个item是一个快捷方式的快捷启动
            String shortCutName=appInfo.getShortCutName();
            int flag=appInfo.getFlag();
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(pkgName,
                    shortCutName);
            intent.addFlags(flag);

            try {
                Drawable icon=pm.getActivityIcon(intent);
                holder.itemIvIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.getContext().startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mAppInfoList==null?0:mAppInfoList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIvIcon;
        private TextView itemTvAppName;

        public AppViewHolder(View itemView) {
            super(itemView);
            itemIvIcon = (ImageView) itemView.findViewById(R.id.item_iv_icon);
            itemTvAppName = (TextView) itemView.findViewById(R.id.item_tv_app_name);

        }
    }
}
