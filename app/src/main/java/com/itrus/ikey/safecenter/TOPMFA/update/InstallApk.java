package com.itrus.ikey.safecenter.TOPMFA.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/18 16:52
 */
public class InstallApk {

    public static void startInstall(Context context, File apkfile) {
        if (!apkfile.exists()) {
            return;
        }
        /**
         * TODO 需要做版本兼容处理
         */
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);

    }
}
