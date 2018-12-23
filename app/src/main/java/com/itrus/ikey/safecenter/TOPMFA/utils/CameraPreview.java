package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by STAR on 2016/8/17.
 */
public class CameraPreview extends SurfaceView {

    private static final String TAG = "CameraPreview";
    private int sPreviewWidth = 320; // 默认预览图片宽度
    private int sPreViewHeight = 240; // 默认预览图片高度

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet set) {
        super(context, set);
    }

    public void setPreviewSize(int w, int h) {
        sPreviewWidth = w;
        sPreViewHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.e(TAG, "onMeasure");
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
//		final int height = resolveSize(getSuggestedMinimumHeight(),
//				heightMeasureSpec);

        float ratio;
        if (sPreViewHeight >= sPreviewWidth)
            ratio = (float) sPreViewHeight / (float) sPreviewWidth;
        else
            ratio = (float) sPreviewWidth / (float) sPreViewHeight;

        // One of these methods should be used, second method squishes preview
        // slightly
        setMeasuredDimension(width, (int) (width * ratio));

    }
}
