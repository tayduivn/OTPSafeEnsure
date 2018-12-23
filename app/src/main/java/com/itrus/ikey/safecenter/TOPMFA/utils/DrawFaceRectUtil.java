package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;

/**
 * 画人脸框
 * Created by STAR on 2016/8/17.
 */

public class DrawFaceRectUtil {
    /**
     * 画笔
     */
    private Paint mPaint;

    public static float xScale;
    public static float yScale;
    public static int left;
    public static int right;
    public static int top;
    public static int bottom;

    /**
     * 绘制图片的时候，界面是否左右翻转
     */
    private boolean mIsReverse = false;

    public DrawFaceRectUtil() {
        mPaint = new Paint();

        mPaint.setColor(Color.BLUE);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPaint.setTextSize(30);
    }

    /**
     * 设置画笔颜色
     *
     * @param color 颜色
     */
    public void setUpPaintColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 是否界面翻转
     *
     * @param isReverse 是否翻转
     */
    public void setIsReverse(boolean isReverse) {
        this.mIsReverse = isReverse;
    }

    public void clean(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
    }

    public void drawPoints(Canvas canvas, float[] pointX, float[] pointY, int pointNum, int imageWidth, int imageHeight) {

        /** 清空画布 */
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        if (pointNum != 0) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            /** 计算从 图片坐标 到 画布坐标的 缩放比例 */
            xScale = (float) canvasWidth / (float) imageWidth;
            yScale = (float) canvasHeight / (float) imageHeight;

            for (int i = 0; i < pointNum; i++) {
                if (mIsReverse == false)
                    canvas.drawPoint(pointX[i] * xScale, pointY[i] * yScale, mPaint);
                else if (mIsReverse == true) {

                    canvas.drawPoint(canvasWidth - pointX[i] * xScale, pointY[i] * yScale, mPaint);
                }
            }
        }

    }

    /**
     * 在画布绘制人脸框
     *
     * @param canvas      画布
     * @param rectList    人脸列表
     * @param imageWidth  图片宽度
     * @param imageHeight 图片高度
     */
    public void drawFaces(Canvas canvas, int[] rectList, int imageWidth, int imageHeight) {
        /** 清空画布 */
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        if (rectList == null) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            /** 计算从 图片坐标 到 画布坐标的 缩放比例 */
            RectF canvasRect = new RectF();

            canvasRect.left = (float) canvasWidth * 10;// 100;
            canvasRect.right = (float) canvasWidth * 10;// 630;
            canvasRect.top = (float) canvasHeight * 10;// 120;
            canvasRect.bottom = (float) canvasHeight * 10;// 660;
            /** 水平方向翻转界面 */
            if (mIsReverse) {
                canvasRect.left = canvasWidth - canvasRect.left;
                canvasRect.right = canvasWidth - canvasRect.right;
            }
            canvas.drawRect(canvasRect, mPaint);

        } else {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            /** 计算从 图片坐标 到 画布坐标的 缩放比例 */
            xScale = (float) canvasWidth / (float) imageWidth;
            yScale = (float) canvasHeight / (float) imageHeight;

            RectF canvasRect = new RectF();
            // 人脸框
            canvasRect.left = (float) rectList[0] * xScale;
            canvasRect.right = (float) (rectList[2] + rectList[0]) * xScale;
            canvasRect.top = (float) rectList[1] * yScale;
            canvasRect.bottom = (float) (rectList[1] + rectList[3]) * yScale;

            left = rectList[0];
            right = rectList[2] + rectList[0];
            top = rectList[1];
            bottom = rectList[1] + rectList[3];

            /** 水平方向翻转界面 */
            if (mIsReverse) {
                canvasRect.left = canvasWidth - canvasRect.left;
                canvasRect.right = canvasWidth - canvasRect.right;
            }
            canvas.drawRect(canvasRect, mPaint);
        }
    }

}
