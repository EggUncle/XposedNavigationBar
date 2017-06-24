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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.ui.activity.ClipboardActivity;

import java.util.ArrayList;

/**
 * Created by egguncle on 17-6-24.
 */

public class DialogClipAdapter extends RecyclerView.Adapter<DialogClipAdapter.ClipViewHolder> {
    private ArrayList<String> mClipData;
    private ClipboardManager clipboardManager;

    public DialogClipAdapter(ArrayList<String> clipData){
        mClipData=clipData;
    }

    @Override
    public DialogClipAdapter.ClipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        clipboardManager = (ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        return new ClipViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_clip,parent,false));
    }

    @Override
    public void onBindViewHolder(DialogClipAdapter.ClipViewHolder holder,  int position) {
        final String clipContent=mClipData.get(position);
        holder.tvItemClipContent.setText(clipContent);
        holder.btnItemClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制剪贴板内容
                ClipData clipData = ClipData.newPlainText("clip", clipContent);
                clipboardManager.setPrimaryClip(clipData);
                Context context=view.getContext();
                if (context instanceof Activity){
                    Toast.makeText(context,R.string.clip_success,Toast.LENGTH_SHORT).show();
                    //点击按钮后关闭剪贴板
                    ((Activity)context).finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mClipData.size();
    }

    public class ClipViewHolder extends RecyclerView.ViewHolder{
        private TextView tvItemClipContent;
        private ImageView btnItemClip;


        public ClipViewHolder(View itemView) {
            super(itemView);
            tvItemClipContent = (TextView) itemView.findViewById(R.id.tv_item_clip_content);
            btnItemClip = (ImageView) itemView.findViewById(R.id.btn_item_clip);

        }
    }
}
