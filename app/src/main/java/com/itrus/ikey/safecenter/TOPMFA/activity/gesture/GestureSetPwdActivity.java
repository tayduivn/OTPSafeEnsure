package com.itrus.ikey.safecenter.TOPMFA.activity.gesture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.Consts;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.EventBusMessage;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.utils.LocalBroadcastUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.PasswordUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.OnCompleteListener;
import com.leo.gesturelibray.view.CustomLockView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置手势密码
 *
 * @Date 2018/8/14 15:25
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */

public class GestureSetPwdActivity extends BaseActivity implements View.OnClickListener, OnCompleteListener {


    @BindView(R.id.lv_lock)
    CustomLockView lv_lock;
    @BindView(R.id.tvHint)
    TextView tvHint;
    @BindView(R.id.tv_title)
    TextView tvTitle;


    private boolean isNetVerify = false;
    private boolean loginVerify = false;
    private boolean initGes = false;
    private String FROM = "";


    public static LockMode lockMode;

    /**
     * 跳转到本页面
     *
     * @param activity
     */
    public static void go2GestureSetPwdActivity(Context activity) {
        Intent intent = new Intent(activity, GestureSetPwdActivity.class);
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
        setContentView(R.layout.activity_gesture_set_pwd);
        ButterKnife.bind(this);
    }

    /**
     * 初始化界面数据
     *
     * @param savedInstanceState
     */
    private void initData(@Nullable Bundle savedInstanceState) {
        //默认为设置模式
        setLockMode(this.lockMode);
    }

    /**
     * 设置相关监听
     */
    private void initEvent() {
        findViewById(R.id.tv_left).setOnClickListener(this);
        lv_lock.setOnCompleteListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;

        }
    }

    @Override
    public void onComplete(String password, int[] indexs) {
        tvHint.setText(getPassWordHint(password));
        if (isNetVerify) {
            AppUtil.verifyUserpin(SpUtil.getsp().getString("certPIN", ""), ctx);
            EventBusMessage msg = new EventBusMessage();
            msg.setState(1);
//                msg.setMessage(getIntent().getStringExtra("random"));
            EventBus.getDefault().post(msg);
        }
        if (loginVerify) {//设置OK了
            /**
             * TODO
             */
//                setResult(LOGIN_AUTH_RESULT_CODE);
        }
        // LocalBroadcastUtil.sendLocalBroadcast(new Intent(EventConstants.JUMPHOMETOO));
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


    /**
     * 设置解锁模式
     */
    private void setLockMode(LockMode mode) {
        int str = R.string.gesture_clear_password;
        switch (mode) {
            case CLEAR_PASSWORD:
                tvTitle.setText("清除密码");
                str = R.string.gesture_clear_password;
                setLockMode(LockMode.CLEAR_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case EDIT_PASSWORD:
                tvTitle.setText("修改密码");
                str = R.string.gesture_edit_password;
                setLockMode(LockMode.EDIT_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case SETTING_PASSWORD:
                tvTitle.setText("设置密码");
                str = R.string.gesture_setting_password;
                setLockMode(LockMode.SETTING_PASSWORD, null, str);
                break;
            case VERIFY_PASSWORD:
                tvTitle.setText("验证密码");
                str = R.string.gesture_verify_password;
                setLockMode(LockMode.VERIFY_PASSWORD, PasswordUtil.getPin(this), str);
                break;
        }
        tvHint.setText(str);
    }

    /**
     * 密码输入模式
     */
    private void setLockMode(LockMode mode, String password, int msgId) {
        lv_lock.setMode(mode);
        lv_lock.setErrorNumber(Consts.GESTURE_PWD_ERROR_COUNT);
        lv_lock.setClearPasssword(false);
        if (mode != LockMode.SETTING_PASSWORD) {
            tvHint.setText(R.string.gesture_has_setting_password_hint);
            lv_lock.setOldPassword(password);
        } else {
            tvHint.setText(R.string.gesture_need_setting_password_hint);
        }
        tvHint.setText(msgId);
    }

    /**
     * 密码相关操作完成回调提示
     */
    private int getPassWordHint(String password) {
        int str = 0;
        switch (lv_lock.getMode()) {
            case SETTING_PASSWORD:

                str = R.string.gesture_pwd_set_success;
                savePassword(password);
                EventBusMessage msg = new EventBusMessage();
                if (initGes) {
                    if (!Validator.isBlank(FROM) && FROM.equals(Contants.FROM_WELCOME_ACTIVITY)) {
                        msg.setState(9);
                    } else {
                        msg.setState(7);
                    }
                } else {
                    msg.setState(3);
                }
                EventBus.getDefault().post(msg);
                break;
            case EDIT_PASSWORD:

                str = R.string.gesture_pwd_update_success;
                savePassword(password);
                break;
            case VERIFY_PASSWORD:

                if (isNetVerify)
                    break;
                str = R.string.gesture_pwd_right;
                savePassword(password);
                LocalBroadcastUtil.sendLocalBroadcast(new Intent(AppConstants.ACTION.FRAGMENT_VERIFY_GESTURE));
                break;
            case CLEAR_PASSWORD:

                str = R.string.gesture_pwd_has_clear;
                break;
        }
        showMsg(str);
        return str;
    }


    /**
     * 保存密码
     */
    private void savePassword(String password) {
        SpUtil.getsp().putString(AppConstants.GES_PWD, password);   //GesPWD 手势密码
        SpUtil.getsp().putBoolean(AppConstants.HAS_GES_PWD, true);   //hasGesPWD 是否有手势密码
        if (initGes) {
            SpUtil.getsp().putBoolean(AppConstants.INIT_GES_PWD, true);   //initGesPWD 初始化时设置手势
        }
        finish();
    }


}
