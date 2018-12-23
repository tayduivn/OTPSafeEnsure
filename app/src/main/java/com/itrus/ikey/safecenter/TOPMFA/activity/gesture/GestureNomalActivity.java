package com.itrus.ikey.safecenter.TOPMFA.activity.gesture;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.LocalBroadcastUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.OnCompleteListener;
import com.leo.gesturelibray.view.CustomLockView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 校验手势密码
 *
 * @Date 2018/8/14 15:25
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */

public class GestureNomalActivity extends BaseActivity implements View.OnClickListener, OnCompleteListener {


    @BindView(R.id.lv_lock)
    CustomLockView lv_lock;
    @BindView(R.id.tvHint)
    TextView tvHint;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    private boolean isNetVerify = false;
    private boolean loginVerify = false;
    private boolean initGes = false;

    private boolean isFromFragment = false;
    private boolean isFromWelcome = false;

    private long exitTime;
    private String userName = "";

    /**
     * 跳转到本页面
     *
     * @param activity
     */
    public static void go2GestureNomalActivity(Context activity, boolean isFromWelcom, boolean isFromFragment, String userName) {
        Intent intent = new Intent(activity, GestureNomalActivity.class);
        intent.putExtra(AppConstants.IS_FROM_WELCOME, isFromWelcom);
        intent.putExtra(AppConstants.IS_FROM_FRAGMENT, isFromFragment);
        intent.putExtra(AppConstants.USER_NAME, userName);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(savedInstanceState);
        initEvent();
    }

    /**
     * 初始化界面控件UI
     */
    private void initView() {
        setContentView(R.layout.activity_gesture_nomal);
        ButterKnife.bind(this);
    }

    /**
     * 初始化界面数据
     *
     * @param savedInstanceState
     */
    private void initData(@Nullable Bundle savedInstanceState) {
        //默认为设置模式
        tvHint.setText(R.string.gesture_verify_password);
        isFromWelcome = getIntent().getBooleanExtra(AppConstants.IS_FROM_WELCOME, false);
        isFromFragment = getIntent().getBooleanExtra(AppConstants.IS_FROM_FRAGMENT, false);
        userName = getIntent().getStringExtra(AppConstants.USER_NAME);
        if (TextUtils.isEmpty(userName)) {
            userName = PreferenceManager.getDefaultSharedPreferences(ctx).getString(AppConstants.USER_NAME, "");
        }
        if (!TextUtils.isEmpty(userName)) tv_user_name.setText(userName);
        String oldPwd = SpUtil.getsp().getString(AppConstants.GES_PWD, AppConstants.EMPTY);
        Log.d("TEST11", "oldPwd:" + oldPwd);
        lv_lock.setMode(LockMode.VERIFY_PASSWORD);
        lv_lock.setErrorNumber(AppConstants.GESTURE_PWD_ERROR_COUNT);
        lv_lock.setClearPasssword(false);
        lv_lock.setOldPassword(oldPwd);
    }

    @Override
    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
        super.wrapLocalBroadcastFilter(filter);
        filter.addAction(AppConstants.ACTION.CLOSE_GESTURE);
    }

    @Override
    protected void dealLocalBroadcast(Context context, Intent intent) {
        super.dealLocalBroadcast(context, intent);
        switch (intent.getAction()) {
            case AppConstants.ACTION.CLOSE_GESTURE://关闭手势的广播通知
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvHint.setText(R.string.gesture_verify_password1);
    }

    /**
     * 设置相关监听
     */
    private void initEvent() {
        lv_lock.setOnCompleteListener(this);
        findViewById(R.id.tv_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                if (isFromFragment) {
                    finish();
                } else {
                    MyApplication.getInstance().clearActivity();
                    System.exit(0);
                }
                break;

        }
    }

    @Override
    public void onComplete(String password, int[] indexs) {
        Log.d("TEST11", "password:" + password);
        tvHint.setText(R.string.gesture_pwd_right);
        if (isFromWelcome) {//跳转首页
            MainActivity.go2MainActivity(ctx, userName);
        }
       // LocalBroadcastUtil.sendLocalBroadcast(new Intent(AppConstants.ACTION.FRAGMENT_VERIFY_GESTURE));
        finish();
    }


    @Override
    public void onError(String errorTimes) {
        tvHint.setText(R.string.gesture_pwd_error_again);
    }

    @Override
    public void onPasswordIsShort(int passwordMinLength) {
        tvHint.setText(getString(R.string.gesture_pwd_min_point_count, passwordMinLength + ""));
    }

    @Override
    public void onAginInputPassword(LockMode mode, String password, int[] indexs) {
        tvHint.setText(R.string.gesture_again_input_pwd);
    }

    @Override
    public void onInputNewPassword() {
        tvHint.setText(R.string.gesture_input_new_pwd);
    }

    @Override
    public void onEnteredPasswordsDiffer() {
        tvHint.setText(R.string.gesture_input_pwd_different);
    }

    @Override
    public void onErrorNumberMany() {
        tvHint.setText(R.string.gesture_input_pwd_over_count);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showMsg(getString(R.string.gesture_pwd_exit));
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
