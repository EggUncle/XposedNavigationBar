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

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.Momo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by egguncle on 17-6-18.
 */

public class MomoAdapter extends RecyclerView.Adapter<MomoAdapter.MomoViewHolder> {
    private List<Momo> mListMomo;

    public MomoAdapter(List<Momo> listMomo) {
        mListMomo = listMomo;
    }

    @Override
    public MomoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MomoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_momo, parent, false));
    }

    @Override
    public void onBindViewHolder(MomoViewHolder holder, int position) {
        final Momo momo = mListMomo.get(position);
        holder.itemTvMomoContent.setText(momo.getContent());
        Date date = momo.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.itemTvMomoTime.setText(format.format(date));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.delete)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notifyItemRemoved(mListMomo.indexOf(momo));
                                mListMomo.remove(momo);
                                momo.delete();
                            }
                        })
                        .create().show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListMomo == null ? 0 : mListMomo.size();
    }

    public class MomoViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTvMomoContent;
        private TextView itemTvMomoTime;

        public MomoViewHolder(View itemView) {
            super(itemView);
            itemTvMomoContent = (TextView) itemView.findViewById(R.id.item_tv_momo_content);
            itemTvMomoTime = (TextView) itemView.findViewById(R.id.item_tv_momo_time);

        }
    }
}
