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

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.egguncle.xposednavigationbar.R;
import com.egguncle.xposednavigationbar.util.ImageUtil;

public class SelectIconActivity extends Activity {
    private static final int SELECT_PHOTO = 0;//调用相册照片
    private ImageView imgIcon;
    private EditText edDialog;
    private TextView tvDialog;
    private final static String TAG = "SelectIconActivity";
    private String imagePath;
    private int position;
    private String command;
    private boolean isCommand;
    private String iconPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_select_icon);
        //状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initVar();
        initView();
        initAction();
    }


    private void initView() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.d_custom_icon, null);
        imgIcon = (ImageView) dialogView.findViewById(R.id.iv_item_icon);
        edDialog = (EditText) dialogView.findViewById(R.id.ed_dialog);
        tvDialog = (TextView) dialogView.findViewById(R.id.tv_dialog);
        if (isCommand) {
            edDialog.setVisibility(View.VISIBLE);
            edDialog.setText(command);
            tvDialog.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(iconPath)) {
            try{
                imgIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
            }catch (Exception e){
                imgIcon.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            imgIcon.setImageResource(R.mipmap.ic_launcher);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.custom_icon)
                .setView(dialogView)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.putExtra("position", position);
                        intent.putExtra("imagepath", iconPath);
                        if (TextUtils.isEmpty(command)) {
                            intent.putExtra("command", edDialog.getText().toString());
                        }
                        setResult(HomeActivity.RESULT_OK, intent);
                        finish();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .create().show();

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //申请读取SD卡权限
                    if (ContextCompat.checkSelfPermission(SelectIconActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            ) {
                        ActivityCompat.requestPermissions(SelectIconActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        getImage();
                    }
                } else {
                    getImage();
                }
            }
        });
    }


    private void initVar() {
        position = getIntent().getIntExtra("position", 0);
        command = getIntent().getStringExtra("command");
        isCommand = getIntent().getBooleanExtra("isCommand", false);
        iconPath = getIntent().getStringExtra("iconpath");
    }


    private void initAction() {

    }

    /**
     * 调用系统相册获取图片
     */
    private void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);//调用相册照片
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onClick: 申请权限成功");
                    getImage();
                } else {
                    //权限申请未通过
                    Log.i(TAG, "onClick: 申请权限失败");
                }
                break;
            default:

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    iconPath = ImageUtil.handleImageOnKitKat(data);
                    imgIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
                }

                break;
            default:
                break;

        }
    }
}
