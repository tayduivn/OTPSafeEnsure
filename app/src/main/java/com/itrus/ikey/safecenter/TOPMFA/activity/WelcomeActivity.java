package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;


import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.gesture.GestureNomalActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;


import com.itrus.ikey.safecenter.TOPMFA.utils.SharedPreferencesUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.leo.gesturelibray.enums.LockMode;
import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * Created by STAR on 2016/8/2.
 */
public class WelcomeActivity extends Activity {

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ctx = this;
        PermissionGen.with(this)
                .addRequestCode(111)
                .permissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .request();

    }

    @PermissionSuccess(requestCode = 111)
    public void openContact() {
        toMain();
    }

    @PermissionFail(requestCode = 111)
    public void failContact() {
        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void toMain() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                go2Acts();
            }
        }.start();
        
        }

    private void go2Acts() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String username = sp.getString(AppConstants.USER_NAME, AppConstants.EMPTY);

        if (TextUtils.isEmpty(username)) {
            LoginByPasswordActivity.go2LoginByPasswordActivity(ctx);
        } else {
            boolean isBinding = sp.getBoolean(AppConstants.IS_BINDING, false);
            boolean initGesPwd = sp.getBoolean(AppConstants.INIT_GES_PWD, false);
            boolean openPwd = sp.getBoolean(AppConstants.HAS_GES_PWD, false);
            if (isBinding) {
                if (initGesPwd) {
                    if (openPwd) {// 绑定过二维码  设置过手势密码 已开启手势校验
                        GestureNomalActivity.go2GestureNomalActivity(ctx, true, false, username);
                    } else {// 绑定过二维码  设置过手势密码 已关闭手势校验
                        MainActivity.go2MainActivity(ctx, username);
                    }
                } else {//绑定过二维码 未设置手势密码
                    InitGestureActivity.go2InitGestureActivity(ctx, LockMode.SETTING_PASSWORD, "设置手势密码", username);
                }
            } else {//未绑定二维码
                LoginByPasswordActivity.go2LoginByPasswordActivity(ctx);
            }
        }
        finish();
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }
}
