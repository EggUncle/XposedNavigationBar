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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

public class AboutActivity extends BaseActivity implements View.OnClickListener {


    private TextView tvVersionCode;
    private TextView tvMarket;
    private TextView tvAlipayDonate;
    private TextView tvWechatDonate;
    private TextView tvPaypalDonate;


    @Override
    int getLayoutId() {
        return R.layout.a_about;
    }

    @Override
    void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.about_app));
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvVersionCode = (TextView) findViewById(R.id.tv_version_code);
        tvMarket = (TextView) findViewById(R.id.tv_market);
        tvAlipayDonate = (TextView) findViewById(R.id.tv_alipay_donate);
        tvWechatDonate = (TextView) findViewById(R.id.tv_wechat_donate);
        tvPaypalDonate = (TextView) findViewById(R.id.tv_paypal_donate);

    }

    @Override
    void initVar() {
        tvVersionCode.setText(getAppVersionName(this));
    }

    @Override
    void initAction() {
        tvMarket.setOnClickListener(this);
        tvAlipayDonate.setOnClickListener(this);
        tvWechatDonate.setOnClickListener(this);
        tvPaypalDonate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_market: {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            break;
            case R.id.tv_alipay_donate: {
                View dialogView = LayoutInflater.from(AboutActivity.this).inflate(R.layout.scanner_qr, null);
                ImageView imgQr = (ImageView) dialogView.findViewById(R.id.iv_qr);
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                builder.setView(dialogView).setNegativeButton(R.string.no, null);
                builder.create().show();
                imgQr.setImageResource(R.drawable.alipay_qr);
            }
            break;
            case R.id.tv_wechat_donate: {
                View dialogView = LayoutInflater.from(AboutActivity.this).inflate(R.layout.scanner_qr, null);
                ImageView imgQr = (ImageView) dialogView.findViewById(R.id.iv_qr);
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                builder.setView(dialogView).setNegativeButton(R.string.no, null);
                builder.create().show();
                imgQr.setImageResource(R.drawable.wechat_qr);
            }
            break;
            case R.id.tv_paypal_donate: {
                String url = getResources().getString(R.string.paypal_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addCategory("android.intent.category.BROWSABLE");
                startActivity(Intent.createChooser(intent, "请选择浏览器"));
            }
            break;
        }
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }
}
