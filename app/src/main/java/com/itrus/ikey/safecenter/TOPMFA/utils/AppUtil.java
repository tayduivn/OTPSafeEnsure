package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.content.Context;
import android.text.TextUtils;

import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.raapi.implement.ClientForAndroid;
import com.itrus.raapi.implement.ClientForiKey;
import com.itrus.raapi.implement.Helper;

import java.io.File;

/**
 * @Date 2018/8/14 17:47
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */

public class AppUtil {

    /**
     * 判断是否跳转到手势密码
     *
     * @return
     */
    public static boolean goGesturePasswordActivity() {
        return SpUtil.getsp().getBoolean(AppConstants.HAS_GES_PWD, false);
    }

    public static String sign(String random, Context context) {
        ClientForiKey clientForiKey = ClientForAndroid.getInstance(context);
        String certs[] = clientForiKey.filterCert("", "", "", 0, 0);
        if (certs.length == 0) {
            return null;
        }
        String signMsg = clientForiKey.signMessage(random, certs[0], "SM3", 2);
        return signMsg;
    }

    public static int verifyUserpin(String password, Context context) {
        int ret = -1;
        ClientForiKey clientForiKey = ClientForAndroid.getInstance(context);
        ret = clientForiKey.setLicense(Helper.getAppName(context));
        if (ret == -1)
            return ret;
        String appPath = context.getFilesDir().getAbsolutePath() + "/" + MainActivity.USERNAME;
        File file = new File(appPath);
        if (!file.exists()) {
            file.mkdir();
        }
        ret = clientForiKey.setSystemDBDir(appPath);
        if (ret != 0)
            return ret;
        ret = clientForiKey.verifyUserPIN(Contants.CERT_PIN);
        if (ret != 0)
            return ret;
        return ret;
    }

    public static int updatePassword(String password, Context context) {
        int ret = -1;
        ClientForiKey clientForiKey = ClientForAndroid.getInstance(context);
        ret = clientForiKey.setLicense(Helper.getAppName(context));
        if (ret == -1)
            return ret;
        String appPath = context.getFilesDir().getAbsolutePath() + "/" + MainActivity.USERNAME;
        File file = new File(appPath);
        if (!file.exists()) {
            file.mkdir();
        }
        ret = clientForiKey.setSystemDBDir(appPath);
        if (ret != 0)
            return ret;
        ret = clientForiKey.initUserPIN("ADMIN", password);
        if (ret != 0)
            return ret;
        ret = clientForiKey.verifyUserPIN(password);
        if (ret != 0)
            return ret;
        return ret;
    }

    public static int setPassword(String password, Context context) {
        int ret = -1;
        ClientForiKey clientForiKey = ClientForAndroid.getInstance(context);
        ret = clientForiKey.setLicense(Helper.getAppName(context));
        if (ret == -1)
            return ret;
        String appPath = context.getFilesDir().getAbsolutePath() + "/" + MainActivity.USERNAME;
        File file = new File(appPath);
        if (!file.exists()) {
            file.mkdir();
        }
        ret = clientForiKey.setSystemDBDir(appPath);
        if (ret != 0)
            return ret;
        ret = clientForiKey.setAdminPIN("", "ADMIN");
        if (ret != 0)
            return ret;
        ret = clientForiKey.initUserPIN("ADMIN", Contants.CERT_PIN);
        if (ret != 0)
            return ret;
        ret = clientForiKey.verifyUserPIN(Contants.CERT_PIN);
        if (ret != 0)
            return ret;
        return ret;
    }


}
