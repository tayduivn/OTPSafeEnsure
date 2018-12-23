package com.itrus.ikey.safecenter.TOPMFA.base;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;

/**
 * Created by STAR on 2016/8/17.
 */
public class GlobalConfig {

    //    public static final String BASEURL = "http://192.168.100.156:9080/hawk/services/rest/";       //局域网地址
    //public static final String BASEURL = "http://124.205.224.180:9075/hawk/services/rest/";       //局域网外网地址
//    public static final String BASEURL = "http://119.254.102.224:8080/hawk/services/rest/";       //青云地址Hawk
   // public static final String BASEURL = "http://124.205.224.180:9075/hawk/services/rest/c/";       //局域网外网地址
//    public static final String BASEURL = "http://192.168.2.52:8080/hawk/services/rest/c/";
//    public static final String BASEURL = "http://192.168.102.153:8080/sso/services/rest/c/";       //局域网外网地址
    public static final String BASEURL = "http://otp.topca.cn/services/rest/c/";       //云地址


   // public static final String BASEURL = "http://192.168.2.96:8081/hawk/services/rest/c/";

    public static String getBaseurl() {
        String baseurl = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
        baseurl = sp.getString("baseurl", null);
        if (Validator.isBlank(baseurl)) {
            baseurl = BASEURL;
        }
        return baseurl;
    }

}
