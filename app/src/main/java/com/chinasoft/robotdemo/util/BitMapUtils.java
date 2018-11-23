package com.chinasoft.robotdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by XHF on 2018/11/23.
 */

public class BitMapUtils {

    //尺寸压缩
    public static Bitmap bitmapRoom(Bitmap srcBitmap, int newWidth, int newHeight) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / srcWidth;
        float scaleHeight = ((float) newHeight) / srcHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight, matrix, true);
        if (resizedBitmap != null) {
            return resizedBitmap;
        } else {
            return srcBitmap;
        }
    }

    //质量压缩
    public static Bitmap SmallQuality(Bitmap bitmap) {
        ByteArrayOutputStream fos = null;
        ByteArrayInputStream ins = null;
        Bitmap quality = null;
        try {
            fos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);  //测试 标准80 现在返回50
            ins = new ByteArrayInputStream(fos.toByteArray());
            quality = BitmapFactory.decodeStream(ins);
            ins.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quality;
    }

}
