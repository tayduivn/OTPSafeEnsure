package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyCallback;
import com.itrus.ikey.safecenter.TOPMFA.widget.view.CustomProgressDialog;

/**
 * 窗口工具类,提供可重用的窗口
 * <p/>
 * Created by STAR on 2016/8/24.
 */
public class DialogUtil {

    private static CustomProgressDialog progressDialogMy;
    private static ProgressDialog progDialog;
    private static Toast mToast;//为了实现疯狂模式下toast延时消失的问题
    private static Toast mToastCust;
    private static AlertDialog alertDialog;

    public static void showProgress(Activity ctx, String msg) {
        if (progDialog == null) {
            progDialog = new ProgressDialog(ctx);
        } else {
            if (progDialog.isShowing()) {
                return;
            }
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        //progDialog.setCancelable(false);//按返回取消
        progDialog.setCanceledOnTouchOutside(false);//点区域外不取消
        if (!Validator.isBlank(msg)) {
            progDialog.setMessage(msg);
        }
        progDialog.show();
    }

    /**
     * 显示等待条
     */
    public static void showProgress(Context ctx) {
        progressDialogMy = CustomProgressDialog.createDialog(ctx);
        progressDialogMy.setCanceledOnTouchOutside(false);//点区域外quxiao
//         添加按键监听
        progressDialogMy.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if (arg1 == KeyEvent.KEYCODE_BACK) {

//                    if ((progressDialogMy != null) && progressDialogMy.isShowing()) {
//                        progressDialogMy.cancel();
//                    }
                }
                return false;
            }
        });
        progressDialogMy.show();
    }

    /**
     * 隐藏progress
     */
    public static void dismissProgress() {
        if ((progressDialogMy != null) && progressDialogMy.isShowing()) {
            progressDialogMy.dismiss();
        }
        if (progDialog != null) {
            progDialog.dismiss();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public static void showToastOnUIThread(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastCust(act, msg);
            }
        });

    }

    /**
     * 此方法可以避免疯狂模式下点击按钮造成的长时间显示toast的问题
     *
     * @param ctx
     * @param msg
     */
    public static void showToastCust(Context ctx, String msg) {
        if (mToast == null) {
            mToast = new Toast(ctx);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_LONG);
            View toastRoot = ((Activity) ctx).getLayoutInflater().inflate(R.layout.toast_my, null);
            mToast.setView(toastRoot);
        }
        TextView message = (TextView) mToast.getView().findViewById(R.id.tv_toast);
        message.setText(msg);
        mToast.show();
    }

    public static void showToastCust(Context ctx, int resId) {
        if (mToast == null) {
            mToast = new Toast(ctx);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_LONG);
            View toastRoot = ((Activity) ctx).getLayoutInflater().inflate(R.layout.toast_my, null);
            mToast.setView(toastRoot);
        }
        TextView message = (TextView) mToast.getView().findViewById(R.id.tv_toast);
        message.setText(resId);
        mToast.show();
    }

    public static void showNoNet(Context ctx) {
        showToastCust(ctx, "网络不可用，请检查网络！");
    }


    public static void showToastNoNet(Context ctx) {
        View toastRoot = ((Activity) ctx).getLayoutInflater().inflate(
                R.layout.toast_my, null);
        TextView message = (TextView) toastRoot.findViewById(R.id.tv_toast);
        message.setText("网络不可用！");

        Toast toastStart = new Toast(ctx);
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    public static void showAlertOnUIThread(final Activity ctx, final String msg, final MyCallback callback) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlert(ctx, msg, callback);
            }
        });
    }


    public static void showAlert(final Activity ctx, final String msg, final MyCallback callback) {
        new AlertDialog.Builder(ctx)
                .setTitle("提示信息")
                .setIcon(android.R.drawable.ic_dialog_alert)//图标
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (callback != null) {
                                    callback.onCallback(true);
                                }
                                dialog.cancel();
                            }
                        }
                ).show();
    }

    public static void showAlertYesNo(final Activity ctx, final String msg, final MyCallback callback) {
        new AlertDialog.Builder(ctx)
                .setMessage(msg)
                .setTitle("提示信息")
                .setIcon(android.R.drawable.ic_dialog_alert)//图标
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callback.onCallback(true);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callback.onCallback(false);
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showAlertYesOrNoOnUIThread(final Activity ctx, final String msg, final MyCallback callback) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlertYesNo(ctx, msg, callback);
            }
        });
    }

    public static void showAlertOkCancel(final Activity ctx, final String msg, final MyCallback callback) {
        new AlertDialog.Builder(ctx)
                .setMessage(msg)
                .setTitle("提示信息")
                .setIcon(android.R.drawable.ic_dialog_alert)//图标
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callback.onCallback(true);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callback.onCallback(false);
                        dialog.dismiss();
                    }
                }).show();
    }


}
