package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import com.itrus.ikey.safecenter.TOPMFA.entitiy.Consts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/**
 * 保存图像
 * <p/>
 * Created by STAR on 2016/8/17.
 */
public class Register {

    public Boolean saveImage(byte[] data, Camera camera, int[] faceRects, String bigPicture, String smallPicture) {
        int left = 0;
        int bottom = 0;
        int top = 0;
        left = faceRects[0];
        top = faceRects[1];
        bottom = faceRects[1] + faceRects[3];
        Log.i("huakuang", "left =" + left + "" + "   top = " + top + "" + "  button=" + bottom + "");
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height), 100, baos);
        // Log.i("waaa",
        // "width="+camera.getParameters().getPreviewSize().width+""+"hig="+camera.getParameters().getPreviewSize().height+"");//width=320,hig=240
        byte[] dataBy = baos.toByteArray();
        Matrix matrix = new Matrix(); // 翻转图片，原图片长宽交换。
        if (Consts.mOpenCameraId == 1) {
            matrix.postRotate(270);
        } else {
            matrix.postRotate(90);
        }
        matrix.postScale(-1, 1); // 镜像水平翻转
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataBy, 0, dataBy.length);
        Bitmap nbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // 原数12 10
        File bigFile = new File(bigPicture);
        File smallFile = new File(smallPicture);
        FileOutputStream bigOut = null;
        FileOutputStream smallOut = null;
        try {
            bigOut = new FileOutputStream(bigFile);
            smallOut = new FileOutputStream(smallFile);
            nbmp.compress(Bitmap.CompressFormat.JPEG, 100, bigOut);
            bigOut.close();
            nbmp.compress(Bitmap.CompressFormat.JPEG, 35, smallOut);
            smallOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

//		try {
//			File smallFile = new File(smallPicture);
//			FileOutputStream smallOut = new FileOutputStream(smallFile);
//			Bitmap biduiBitmap = resizeBitmap(nbmp, 150, 150);
//			biduiBitmap.compress(Bitmap.CompressFormat.PNG, 80, smallOut);
//			Log.i("biduiBitmap.getHeight", "" + biduiBitmap.getHeight());
//			Log.i("biduiBitmap.getWidth()", "" + biduiBitmap.getWidth());
//			// picNewRes.compress(Bitmap.CompressFormat.PNG, 100, newOut);
//			smallOut.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
        return true;
    }


    public Boolean saveImage(byte[] data, Camera camera, String imagePath) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height), 80, baos);
        byte[] dataBy = baos.toByteArray();
        Matrix matrix = new Matrix(); // 翻转图片，原图片长宽交换。
        if (Consts.mOpenCameraId == 1) {
            matrix.postRotate(270);
        } else {
            matrix.postRotate(90);
        }
        matrix.postScale(-1, 1); // 镜像水平翻转
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataBy, 0, dataBy.length);
        Bitmap nbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // 原数12 10
        File bigFile = new File(imagePath);
        FileOutputStream bigOut = null;
        try {
            bigOut = new FileOutputStream(bigFile);
            nbmp.compress(Bitmap.CompressFormat.PNG, 80, bigOut);
            bigOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {

        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            return bitmap;
        }

        int width = originWidth;
        int height = originHeight;

        // 若图片过宽, 则保持长宽比缩放图片
        if (originWidth > maxWidth) {
            width = maxWidth;

            double i = originWidth * 1.0 / maxWidth;
            height = (int) Math.floor(originHeight / i);

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }

        // 若图片过长, 则从上端截取
        if (height > maxHeight) {
            height = maxHeight;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        // Log.i(TAG, width + " width");
        // Log.i(TAG, height + " height");

        return bitmap;
    }
}
