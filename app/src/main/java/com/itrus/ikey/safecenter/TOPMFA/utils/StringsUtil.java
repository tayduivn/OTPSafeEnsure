package com.itrus.ikey.safecenter.TOPMFA.utils;


import android.content.Context;

import com.google.gson.Gson;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.Info1;
import com.leo.gesturelibray.crypto.Base64;

/**
 * Created by STAR on 2016/8/17.
 */
public class StringsUtil {
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static int getChineseCharacterCount(String str) {
        int ccCount = 0;
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))) {
                ccCount++;
            }
        }

        return ccCount;
    }

    public static String getDevicesInfo(Context ctx) {
        String AndroidVersion = DeviceUtil.getBuildVersion();   //android 系统版本号 5.1
        String phoneBrand =  DeviceUtil.getPhoneBrand();        //获取手机品牌
        String phoneVerision = DeviceUtil.getPhoneModel();      //获取手机型号
        String IMEI = ExampleUtil.getImei(ctx, "");
        String version = ExampleUtil.GetVersion(ctx);
        Info1 info1 = new Info1();
        Info1.AppBean appBean = new Info1.AppBean();
        appBean.setVersion(version);
        Info1.OsBean osBean = new Info1.OsBean();
        osBean.setVersion(AndroidVersion);
        osBean.setType("Android");
        osBean.setPhone(phoneBrand+" "+phoneVerision);
        Info1.PositionBean positionBean = new Info1.PositionBean();
        positionBean.setX("123.465");
        positionBean.setY("456.789");
        info1.setSerial(IMEI);
        info1.setOs(osBean);
        info1.setApp(appBean);
        info1.setPosition(positionBean);
        Gson gson = new Gson();
        String info1gosn = gson.toJson(info1);
        String b64json = Base64.encode(info1gosn.getBytes());
        return b64json;
    }
}
