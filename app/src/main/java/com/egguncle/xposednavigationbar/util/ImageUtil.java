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

package com.egguncle.xposednavigationbar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by egguncle on 17-6-21.
 */

public class ImageUtil {
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
}
