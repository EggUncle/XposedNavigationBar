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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.FinalStr.FuncName;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.ShortCut;
import com.egguncle.xposednavigationbar.util.CodeToFuncName;

import java.util.Iterator;
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
        this.mCodeToFuncName=CodeToFuncName.getInstance(mContext);
    }

    @Override
    public RcvHomeAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(RcvHomeAdapter.HomeViewHolder holder, int position) {
        final ShortCut shortCut = dataList.get(position);
       // String shortCutName = shortCut.getShortCutName();
        int code = shortCut.getCode();
        holder.itemTvName.setText(mCodeToFuncName.getFuncNameFromCode(code));
        final int p = position;
        holder.itemImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemRemoved(dataList.indexOf(shortCut));
                dataList.remove(shortCut);
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

//    public void setImgIcon(String name, ImageView img) {
//        switch (name) {
//            case FuncName.DOWN:
//                img.setImageResource(R.drawable.down);
//                break;
//            case FuncName.QUICK_NOTICE:
//                img.setImageResource(R.drawable.quick_notices);
//                break;
//            case FuncName.CLEAR_NOTIFICATION:
//                img.setImageResource(R.drawable.clear_notification);
//                break;
//            case FuncName.CLEAR_MEM:
//                img.setImageResource(R.drawable.clear_mem);
//                break;
//            case FuncName.SCREEN_OFF:
//                img.setImageResource(R.drawable.screenoff);
//                break;
//            case FuncName.VOLUME:
//                img.setImageResource(R.drawable.volume);
//                break;
//            case FuncName.LIGHT:
//                img.setImageResource(R.drawable.light);
//                break;
//        }
//    }
}
