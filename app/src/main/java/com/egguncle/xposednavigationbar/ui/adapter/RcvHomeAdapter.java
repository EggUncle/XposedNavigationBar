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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.constant.ConstantStr;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.ui.activity.SelectIconActivity;
import com.egguncle.xposednavigationbar.util.CodeToFuncName;

import java.util.List;

/**
 * Created by egguncle on 17-6-7.
 */

public class RcvHomeAdapter extends RecyclerView.Adapter<RcvHomeAdapter.HomeViewHolder> {
    private String TAG = "RcvHomeAdapter";
    private List<ShortCut> dataList;
    private Context mContext;
    private CodeToFuncName mCodeToFuncName;

    public RcvHomeAdapter(Context context, List<ShortCut> list) {
        this.mContext = context;
        this.dataList = list;
        this.mCodeToFuncName = CodeToFuncName.getInstance(mContext);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public RcvHomeAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(RcvHomeAdapter.HomeViewHolder holder, final int position) {
        final ShortCut shortCut = dataList.get(position);
        // String shortCutName = shortCut.getShortCutName();
        int code = shortCut.getCode();
        holder.itemTvName.setText(mCodeToFuncName.getFuncNameFromCode(code));
        holder.itemImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemRemoved(dataList.indexOf(shortCut));
                dataList.remove(shortCut);
            }
        });

        holder.itemTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SelectIconActivity.class);
                intent.putExtra("position", position);
                String iconPath = shortCut.getIconPath();
                intent.putExtra("iconpath", iconPath);
                if (mContext instanceof Activity) {
                    if (shortCut.getCode() == ConstantStr.FUNC_COMMAND_CODE) {
                        intent.putExtra("isCommand", true);
                        intent.putExtra("command", shortCut.getShellStr());
                    }
                    ((Activity) mContext).startActivityForResult(intent, 1);
                } else {
                    Log.i(TAG, "onClick: --------this not a activity-------");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTvName;
        private ImageView itemImgDelete;

        public HomeViewHolder(View itemView) {
            super(itemView);
            itemTvName = (TextView) itemView.findViewById(R.id.item_tv_name);
            itemImgDelete = (ImageView) itemView.findViewById(R.id.item_img_delete);
        }
    }

}
