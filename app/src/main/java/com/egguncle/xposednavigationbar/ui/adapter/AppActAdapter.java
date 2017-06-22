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

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.AppInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by egguncle on 17-6-11.
 */

public class AppActAdapter extends RecyclerView.Adapter<AppActAdapter.AppViewHolder> {
    private List<AppInfo> mAppInfoList;
    private Context mContext;
    private PackageManager pm;

    private final static String TAG = "AppActAdapter";

    public AppActAdapter(Context context, List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
        mContext = context;
        pm = mContext.getPackageManager();
    }

    @Override
    public AppActAdapter.AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(AppActAdapter.AppViewHolder holder, int position) {
        final AppInfo appInfo = mAppInfoList.get(position);
        final String pkgName = appInfo.getPackgeName();
        String label = appInfo.getLabel();
        holder.itemTvAppName.setText(label);
        int type = appInfo.getType();
        //如果这个item是一个app的快捷启动
        if (type == AppInfo.TYPE_APP) {
            try {
                Drawable icon = pm.getApplicationIcon(pkgName);
                holder.itemIvIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: start app");
                    try {
                        Intent intent = pm.getLaunchIntentForPackage(pkgName);
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        //如果无法打开，可能是被冻结在冰箱里面了
                        requestRootToStartSc(view.getContext(), pkgName);
                    }

                }
            });
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
                holder.itemIvIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: start shortcut");
                    try {
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        //如果无法打开，可能是被冻结在冰箱里面了
                        requestRootToStartSc(view.getContext(), pkgName);
                    }


                }
            });
        }


    }

    /**
     * 当app被冻结在冰箱里时，使用root权限来解冻应用
     *
     * @param context
     * @param pkgName
     */
    private void requestRootToStartSc(Context context, final String pkgName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.cant_start_act);
        builder.setMessage(R.string.taps_start_apps);
        builder.setNegativeButton(R.string.no, null);
        builder.setPositiveButton(R.string.root_request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rootAction(pkgName);
            }
        });
        builder.create().show();
    }

    /**
     * root 解冻操作
     *
     * @param pkgName
     */
    private void rootAction(final String pkgName) {
        //先申请root权限
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            final Process finalProcess = process;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // boolean result = false;
                    DataOutputStream dataOutputStream = null;

                    try {
                        dataOutputStream = new DataOutputStream(finalProcess.getOutputStream());
                        // 解冻命令为：pm enable pkgName
                        String command = "pm enable " + pkgName + " \n";
                        dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                        dataOutputStream.flush();
                        dataOutputStream.writeBytes("exit\n");
                        dataOutputStream.flush();
                        finalProcess.waitFor();
                    } catch (Exception e) {

                    } finally {
                        try {
                            if (dataOutputStream != null) {
                                dataOutputStream.close();
                            }

                        } catch (IOException e) {

                        }
                    }

                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mAppInfoList == null ? 0 : mAppInfoList.size();
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
