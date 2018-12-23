package com.itrus.ikey.safecenter.TOPMFA.update;

import android.app.Notification;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/12 16:47
 */
public class UpdateFunGO {

    private static Thread download;
    private static Thread thread_update;

    private static volatile UpdateFunGO sInst = null;

    public static void manualStart(Context context) {
        DownloadKey.ISManual = true;
        if (!DownloadKey.LoadManual) {
            DownloadKey.LoadManual = true;
            new UpdateFunGO(context);
        } else Toast.makeText(context, "正在更新中,请稍等", Toast.LENGTH_LONG).show();
    }

    public static UpdateFunGO init(Context context) {
        UpdateFunGO inst = sInst;
        if (inst == null) {
            synchronized (UpdateFunGO.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new UpdateFunGO(context);
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    private UpdateFunGO(Context context) {
        DownloadKey.FROMACTIVITY = context;
        if (DownloadKey.TOShowDownloadView != 2) {
            thread_update = new Thread(new HandleUpdateResult(context));
            thread_update.start();
        }
    }

    public static void showDownloadView(Context context) {
        DownloadKey.saveFileName =
                GetAppInfo.getAppPackageName(context) + ".apk";
        if (UpdateKey.DialogOrNotification == 1) {

        } else if (UpdateKey.DialogOrNotification == 2) {
            Notification.Builder builder = notificationInit(context);
            download = new Download(context, builder);
            download.start();
        }
    }

    private static Notification.Builder notificationInit(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("开始下载")
                .setContentTitle(GetAppInfo.getAppName(context))
                .setContentText("正在更新")
                .setWhen(System.currentTimeMillis());
        return builder;
    }

    public static void onResume(Context context) {
        if (DownloadKey.TOShowDownloadView == 2) {
            showDownloadView(context);
        } else {
            if (sInst != null) sInst = null;
        }
    }

    public static void onStop(Context context) {
        if (DownloadKey.TOShowDownloadView == 2 && UpdateKey.DialogOrNotification == 2) {
            download.interrupt();
        }
        if (DownloadKey.FROMACTIVITY != null) {
            DownloadKey.FROMACTIVITY = null;
        }
        if (thread_update != null) {
            thread_update.interrupt();
        }
        if (DownloadKey.ISManual) {
            DownloadKey.ISManual = false;
        }
        if (DownloadKey.LoadManual) {
            DownloadKey.LoadManual = false;
        }
    }

}
