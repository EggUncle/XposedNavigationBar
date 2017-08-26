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

package com.egguncle.xposednavigationbar.util;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.egguncle.xposednavigationbar.MyApplication;

/**
 * Created by egguncle on 17-6-21.
 */

public class ImageUtil {
    private final static String TAG = "ImageUtil";


    public static Bitmap byte2Bitmap(byte[] imgBytes) {
        return BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
    }

    /**
     * 缩放图片
     *
     * @param bmByte
     * @param scale
     * @return
     */
    public static Bitmap zoomBitmap(byte[] bmByte, int scale) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);

        Matrix matrix = new Matrix();
        matrix.postScale((float) (scale / 100.0), (float) (scale / 100.0));
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return bitmap;
    }


    public static Bitmap zoomBitmap(String imgPath, int scale) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            Bitmap bm;

            bm = BitmapFactory.decodeFile(imgPath);

            Matrix matrix = new Matrix();
            matrix.postScale((float) (scale / 100.0), (float) (scale / 100.0));
            Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap zommBitmap(Bitmap bm, int scale) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;

        Matrix matrix = new Matrix();
        matrix.postScale((float) (scale / 100.0), (float) (scale / 100.0));
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * @param data
     * @return 图片路径
     */
    public static String handleImageOnKitKat(Intent data) {
        String imagePath = "";
        Log.i(TAG, "handleImageOnKitKat: ");

        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(MyApplication.getContext(), uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的ID
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通的方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取图片的真实路径
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }
}
