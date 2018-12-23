package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.content.Context;


/**
 * Created by STAR on 2016/8/3.
 */
public class PasswordUtil {

    /**
     * 获取设置过的密码
     */
    public static String getPin(Context context) {
        String password = SpUtil.getsp().getString("GesPWD", null);
        return password;
    }
}
