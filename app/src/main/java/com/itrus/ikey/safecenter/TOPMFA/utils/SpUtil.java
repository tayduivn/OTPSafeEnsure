package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;

/**
 * Created by star on 2017/3/29/029.
 */

public class SpUtil {
    public static SharedPreferencesUtil getsp() {
        SharedPreferences spdefautl = PreferenceManager.getDefaultSharedPreferences(MyApplication.getTopActivity());
        String onlyUserName = spdefautl.getString("onlyusername", "");
        SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(onlyUserName);
        return sp;
    }

    public static SharedPreferencesUtil getsp(String onlyUserName){
        SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(onlyUserName);
        return sp;
    }

    public static SharedPreferencesUtil getspForBinding(){
        SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance("InitBinding");
        return sp;
    }




}
