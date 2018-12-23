package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.Consts;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyCallback;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * SFHCameraCallback类根据指定的Surface的生命周期来控制摄像头的打开、预览 、关闭；
 * 同时，在预览的时候，将SurfaceHoler与被打开的摄像头绑定，使之能显示摄像头预览的的画面；
 * 最后还提供调整界面显示区 控件的长宽，来保证预览视频不会变形。
 *
 * Created by STAR on 2016/8/17.
 */
public class SFHCameraCallback implements SurfaceHolder.Callback {
    public static final String TAG = "SFHCAMERACALLBACK";

    /**
     * 根据 view的宽/长  与 预览 图片的 宽/长的 大小 关系 来决定调整 view的宽度 或者 高度 。
     */
    public static final int ADJUST_VIEW_BY_RIATIO = 0;
    /**
     * 调整view的高度 ，使得view的 长宽 比 与 预览 图片的 一致 。
     */
    public static final int ADJUST_VIEW_WIDTH = 1;
    /**
     * 调整view的 宽度 ，使得view的 长宽 比 与 预览 图片的 一致 。
     */
    public static final int ADJUST_VIEW_HEIGHT = 2;

    public static int sPreviewWidth = 320;    // 预览图片宽度
    public static int sPreViewHeight = 240; // 预览图片高度

    private SurfaceHolder mSurfaceHolder;    // 用于显示预览界面的surfaceholder
    private Camera.PreviewCallback mPreviewCallback; // 用于获取预览数据回调函数
    private Activity mActivity;

    private Camera mOpenCamera;    // 已打开的摄像头
    //public int mOpenCameraId = -1; // 已打开的摄像头id
    public int mOrientation = 270;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();

    private View mAdjustView;    // 需要被调整长宽 比例的 view。通常为 视频 显示控件或者其 父控件 。
    private int mAdjustType = ADJUST_VIEW_BY_RIATIO;

    public void setAdjustType(int adjustType) {
        mAdjustType = adjustType;
    }

    @SuppressWarnings("deprecation")
    public SFHCameraCallback(SurfaceHolder holder,
                             Camera.PreviewCallback previewCallback, Activity activity) {

        this.mSurfaceHolder = holder;
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.mPreviewCallback = previewCallback;
        this.mActivity = activity;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        startPreView();
//        Log.e(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
//        Log.e(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
//        Log.e(TAG, "surfaceDestroyed");
    }

    /**
     * @return id 转换的cameraid
     */
    public int switchCamera() {
        int num = Camera.getNumberOfCameras();
        for (int i = 0; i < num; i++) {
            if (i != Consts.mOpenCameraId) {
                stopPreview();
                closeCamera();
                openCamera(i);
                startPreView();
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取已经打开的摄像头
     *
     * @return
     */
    public Camera getOpenCamera() {
        return mOpenCamera;
    }

    /**
     * 打开默认摄像头
     */
    public void openCamera() {
        int cameraId = CameraHelper.getFutureCameraId(mActivity);
        openCamera(cameraId);
    }

    /**
     * 打开指定的摄像头
     *
     * @param cameraId
     */
    public void openCamera(int cameraId) {
        // 指定的摄像头跟已打开的摄像头一致,不在重复打开
        if (mOpenCamera != null && Consts.mOpenCameraId == cameraId) {
            return;
        }
        // 其他摄像头 正在用，关闭其他摄像头
        else if (mOpenCamera != null) {
            closeCamera();
        }
        openRealCamera(cameraId);
    }

    private void openRealCamera(int cameraId) {
        Consts.mOpenCameraId = cameraId;
        try {
            mOpenCamera = Camera.open(Consts.mOpenCameraId);
        } catch (Exception e) {
            if (e.toString().contains("Camera permission")) {
//                Log.e("bh", "摄像头无有权限");
                DialogUtil.showAlertYesNo(mActivity, "无法获取摄像头数据，请在\n手机应用权限管理中打开\nBhFaceID的摄像头权限。", new MyCallback<Boolean>() {
                    @Override
                    public void onCallback(Boolean yes) {
                        if (yes) {
                            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                            mActivity.finish();
                        }
                    }
                });

            } else {
                Toast.makeText(mActivity, "打开摄像头失败,请检查权限或重启机器", Toast.LENGTH_LONG).show();
            }
            e.printStackTrace();
        }
    }

    private void takePicture() {
        if (mOpenCamera != null) {
            mOpenCamera.takePicture(null, rawCallback, jpegCallback);
        }
    }

    private PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            /* 要处理raw data?写?否 */
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
			/* 取得相片 */
            try {
				/* 取得相片Bitmap对象 */

                Bitmap bmp = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                Log.i("left", "" + DrawFaceRectUtil.left);
                Log.i("top", "" + DrawFaceRectUtil.top);
                Log.i("bottom", "" + DrawFaceRectUtil.bottom);
                Log.i("right", "" + DrawFaceRectUtil.right);
                @SuppressWarnings("static-access")
                Bitmap bmpNew = bmp.createBitmap(bmp, DrawFaceRectUtil.right, DrawFaceRectUtil.bottom,
                        sPreviewWidth, sPreViewHeight);
                String imgPath_z1 = "/storage/emulated/0/a.jpg";
                FileOutputStream aFile = null;
                aFile = new FileOutputStream(imgPath_z1);
                bmpNew.compress(Bitmap.CompressFormat.JPEG, 100, aFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 对到焦点拍照
     */
    public final class AutoFocusCallback implements
            Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
	/* 对到焦点拍照 */
            if (focused) {
                takePicture();
            }
        }
    }

    /**
     * 拍照
     */
    public void cameraTaking() {
        mOpenCamera.autoFocus(mAutoFocusCallback);
    }

    /**
     * 开始预览
     */
    public void startPreView() {

        if (mOpenCamera == null)
            return;

        try {
            mOpenCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            closeCamera();
            return;
        }
        int[] futurePreviewSize = CameraHelper.getFuturePreviewSize(mActivity, mOpenCamera); // 设置预览分辨率
        Camera.Parameters parameters = mOpenCamera.getParameters();
        sPreviewWidth = futurePreviewSize[0];
        sPreViewHeight = futurePreviewSize[1];
        parameters.setPreviewSize(sPreviewWidth, sPreViewHeight);
        mOpenCamera.setParameters(parameters);
        mOpenCamera.setPreviewCallback(mPreviewCallback);
        mOrientation = CameraHelper.getDisplayRotation(
                mActivity, Consts.mOpenCameraId);
        mOpenCamera.setDisplayOrientation(mOrientation);
        mOpenCamera.startPreview();

    }

    /**
     * 结束预览
     */
    public void stopPreview() {
        if (mOpenCamera != null) {
            mOpenCamera.stopPreview();
        }
    }

    /**
     * 关闭摄像头
     */
    public void closeCamera() {
        if (mOpenCamera == null) {
            return;
        }
        mOpenCamera.setPreviewCallback(null);
        mOpenCamera.stopPreview();
        mOpenCamera.release();
        Consts.mOpenCameraId = -1;
        mOpenCamera = null;
        Log.i("SFHCamera", "the camera is close");
    }

    /**
     * 是否是前置摄像头
     *
     * @return true 前置摄像头， false 后置摄像头。
     */
    public boolean facingFront() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(Consts.mOpenCameraId, cameraInfo);
        return cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * 获取当前camera的图片需要旋转的角度
     *
     * @return 图片旋转角度
     */
    public int getImageRotation() {
        return CameraHelper.getImageRotation(mActivity, Consts.mOpenCameraId);
    }

    /**
     * 设置需要调整与摄像长宽比例的view（比如用来显示摄像头内容的view，保证显示的时候画面不失真）
     *
     * @param view
     */
    public void setAdjustView(View view) {
        this.mAdjustView = view;
    }

    /**
     * 根据摄像头的预览的长宽比来调整的界面显示区域的长宽比。
     */
    public void adjustSize() {
        if (mAdjustView == null) {
            return;
        }
        ViewGroup.LayoutParams params = mAdjustView.getLayoutParams();
        float width = mAdjustView.getWidth();
        float height = mAdjustView.getHeight();
        float ratio = width / height;
        float cameraRatio;
        if (mOrientation == 0 || mOrientation == 180) {
            cameraRatio = (float) sPreviewWidth / (float) sPreViewHeight;
        } else {
            cameraRatio = (float) sPreViewHeight / (float) sPreviewWidth;
        }

        if (mAdjustType == ADJUST_VIEW_BY_RIATIO) {
            // 调整width
            if (ratio > cameraRatio) {
                width = height * cameraRatio;
            } else {
                height = width / cameraRatio;
            }
        }
        if (mAdjustType == ADJUST_VIEW_WIDTH) {
            width = height * cameraRatio;
        }

        if (mAdjustType == ADJUST_VIEW_HEIGHT) {
            height = width / cameraRatio;
        }

        params.width = (int) width;
        params.height = (int) height;
    }

}
