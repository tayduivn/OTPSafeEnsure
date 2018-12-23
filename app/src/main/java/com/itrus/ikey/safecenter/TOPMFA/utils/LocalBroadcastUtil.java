package com.itrus.ikey.safecenter.TOPMFA.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;


public final class LocalBroadcastUtil {

    /**
     * 发送请求返回广播
     *
     * @param intent
     * @param code
     */
    public static void sendLocalBroadcast(Intent intent,String code,String extra) {
        intent.putExtra(code, extra);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    /**
     * 发送请求返回广播
     *
     * @param intent
     */
    public static void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    /**
     * 发送请求返回广播
     *
     * @param actionList
     * @param Action
     * @param code
     */
    public static void sendLocalBroadcast(String Action, int code, String... actionList) {
        Intent intent = new Intent();
        for (String s : actionList) {
            intent.setAction(s);
        }
        intent.putExtra(Action, code);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    /**
     * 注册请求返回的广播
     *
     * @param mBroadcastReceiver
     * @param filterAction
     */
    public static void registerLocalBroadCast(BroadcastReceiver mBroadcastReceiver, String... filterAction) {
        IntentFilter filter = new IntentFilter();
        for (String action : filterAction) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * 注册请求返回的广播
     *
     * @param mBroadcastReceiver
     * @param filter
     */
    public static void registerLocalBroadCast(BroadcastReceiver mBroadcastReceiver,
                                              IntentFilter filter) {
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).registerReceiver(
                mBroadcastReceiver, filter);
    }

    /**
     * 解注册广播
     *
     * @param mBroadcastReceiver
     */
    public static void unregisterLocalBroadCast(BroadcastReceiver mBroadcastReceiver) {
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).unregisterReceiver(mBroadcastReceiver);
    }

}
