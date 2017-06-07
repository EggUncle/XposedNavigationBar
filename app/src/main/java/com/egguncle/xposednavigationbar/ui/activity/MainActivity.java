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

package com.egguncle.xposednavigationbar.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.egguncle.xposednavigationbar.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;


public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private Button btnTest;
    private ImageView img;


    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void initView() {
        btnTest = (Button) findViewById(R.id.btn_test);
        img = (ImageView) findViewById(R.id.img);


    }

    @Override
    void initVar() {


    }

    @Override
    void initAction() {
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRoot();
            }
        });
    }

    public boolean requestRoot() {
        boolean result = false;
        DataOutputStream dataOutputStream = null;

        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 模拟手势下拉
            String command = "input swipe 100 1 100 500 300 \n";
            String command2 = "input tap 100 100 \n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();

            result = true;
        } catch (Exception e) {

        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }

            } catch (IOException e) {

            }
        }
        return result;
    }
}
