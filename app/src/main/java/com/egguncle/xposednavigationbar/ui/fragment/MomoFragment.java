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

package com.egguncle.xposednavigationbar.ui.fragment;


import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.model.Momo;
import com.egguncle.xposednavigationbar.ui.adapter.MomoAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egguncle on 17-8-11.
 */

public class MomoFragment extends  BaseFragment{
    private RecyclerView rcvMomo;

    private List<Momo> momoList;
    private MomoAdapter adapter;


    @Override
    void initView(View view) {
        rcvMomo = (RecyclerView)view.findViewById(R.id.rcv_momo);
        rcvMomo.setLayoutManager(new LinearLayoutManager(view.getContext()));
        momoList=new ArrayList<>();
        adapter=new MomoAdapter(momoList);
        rcvMomo.setAdapter(adapter);
    }

    @Override
    void initAction() {

    }

    @Override
    void initVar() {
        momoList.addAll(DataSupport.findAll(Momo.class));
        adapter.notifyDataSetChanged();
    }

    @Override
    int getLayoutId() {
        return R.layout.f_momo;
    }
}
