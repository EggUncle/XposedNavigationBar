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

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egguncle.xposednavigationbar.R;

public class AboutActivity extends BaseActivity {
    private TextView tvMarket;
    private Button btnAlipayDonate;
    private Button btnWechatDonate;




    @Override
    int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.about_app));
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvMarket = (TextView) findViewById(R.id.tv_market);
        btnAlipayDonate = (Button) findViewById(R.id.btn_alipay_donate);
        btnWechatDonate = (Button) findViewById(R.id.btn_wechat_donate);
    }

    @Override
    void initVar() {

    }

    @Override
    void initAction() {
        tvMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Uri uri = Uri.parse("market://details?id="+getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(AboutActivity.this, "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btnAlipayDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 View dialogView= LayoutInflater.from(AboutActivity.this).inflate(R.layout.scanner_qr,null);
                 ImageView imgQr= (ImageView) dialogView.findViewById(R.id.iv_qr);
                AlertDialog.Builder builder=new AlertDialog.Builder(AboutActivity.this);
                builder.setView(dialogView).setNegativeButton(R.string.no,null);
                builder.create().show();
                imgQr.setImageResource(R.drawable.alipay_qr);
            }
        });
        btnWechatDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView= LayoutInflater.from(AboutActivity.this).inflate(R.layout.scanner_qr,null);
                ImageView imgQr= (ImageView) dialogView.findViewById(R.id.iv_qr);
                AlertDialog.Builder builder=new AlertDialog.Builder(AboutActivity.this);
                builder.setView(dialogView).setNegativeButton(R.string.no,null);
                builder.create().show();
                imgQr.setImageResource(R.drawable.wechat_qr);
            }
        });
    }
}
