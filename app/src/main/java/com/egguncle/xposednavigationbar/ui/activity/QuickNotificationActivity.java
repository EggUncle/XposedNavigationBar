/*
 * Create by EggUncle on 17-6-5 下午3:14
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 17-6-5 下午3:14
 */

package com.egguncle.xposednavigationbar.ui.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.egguncle.xposednavigationbar.R;

/**
 * 用作快速备忘的activity，由于dialog的出现需要一个activity对象，所以无法通过systemuiapplication启动
 * 使用这个activity，将其背景设置为透明，打开后出现一个弹窗进行快速记录，
 * 以此在观感上有一种直接在桌面上直接创建对话框的感觉
 */
public class QuickNotificationActivity extends Activity {

    private final static String TAG = "QuickNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_notification);

        //状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        View view = LayoutInflater.from(this).inflate(R.layout.quick_notice_dialog_layout, null);
        final EditText ed = (EditText) view.findViewById(R.id.ed_dialog);
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("快速备忘")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noticeStr = ed.getText().toString();
                        Log.i(TAG, "onClick: " + noticeStr);

                        Notification.Builder builder = new Notification.Builder(QuickNotificationActivity.this);
                        builder.setContentText(noticeStr)
                                .setContentTitle("备忘")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher);
                               // .setAutoCancel(true);
                        Notification notice = builder.build();
                        //使用通知内容的hashcode来作为通知的id，即防止了内容重复造成多个通知，
                        // 也给了不同的通知不同的ID，以显示多个通知
                        int notifyId=noticeStr.hashCode();
                        nm.notify(notifyId, notice);
                        Log.i(TAG, "onClick: "+notifyId);
                    }
                })
                .setView(view)
                //当对话框关闭后，就finish整个activity
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                nm.cancelAll();
                                finish();
                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Log.i(TAG, "onDismiss: ");
                        finish();
                    }
                }).create().show();


    }
}
