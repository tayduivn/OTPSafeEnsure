package com.itrus.ikey.safecenter.TOPMFA.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;


/**
 * @Date 2018/8/14 18:02
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */

public class HomeWatcherReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "HomeReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    private boolean isGesture = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // android.intent.action.CLOSE_SYSTEM_DIALOGS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                isGesture = true;
                AppConstants.isGesture = true;
            } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // 长按Home键 或者 activity切换键

            } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
//				if (Constants.isGesture) {
//					isGesture = false;
//					Constants.isGesture = false;
//					return;
//				}
                // 锁屏
//				if (CommonUtils.goGesturePasswordActivity(context)) {
//					Intent intent2 = new Intent(context,
//							GesturePasswordActivity.class);
//					intent2.putExtra("fromActivity", "Foreground");
//					context.startActivity(intent2);
//				}
            } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // samsung 长按Home键
            }

        }
        if (Intent.ACTION_SCREEN_ON.equals(action)) {

            // 开屏

        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

            // 锁屏
            AppConstants.isGesture = true;

        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {

            // 解锁

        }
    }
}
