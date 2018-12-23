package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.EventBusMessage;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.utils.EventConstants;
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
 * 手势密码Frg
 */
public class GestureCipherActivity extends BaseActivity {


    @BindView(R.id.tv_hint)
    TextView tvHint;

    @BindView(R.id.lv_lock)
    CustomLockView lv_lock;

    @BindView(R.id.tv_left)
    TextView tvLeft;


    private boolean isNetVerify = false;
    private boolean loginVerify = false;
    private boolean initGes = false;

    private String FROM = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_update_gesture);
        ButterKnife.bind(this);
        initView();
    }


    public void initView() {

        //允许最大输入次数
        lv_lock.setErrorNumber(AppConstants.GESTURE_PWD_ERROR_COUNT);
        //密码最少位数
        lv_lock.setPasswordMinLength(4);
//        //编辑密码或设置密码时，是否将密码保存到本地，配合setSaveLockKey使用
//        lv_lock.setSavePin(true);
//        //保存密码Key
//        lv_lock.setSaveLockKey(Contants.PASS_KEY);
        lv_lock.setOnCompleteListener(onCompleteListener);
        //默认为设置模式
        setLockMode(LockMode.EDIT_PASSWORD);


        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * 密码输入模式
     */
    private void setLockMode(LockMode mode, String password, String msg) {
        lv_lock.setMode(mode);
        lv_lock.setErrorNumber(AppConstants.GESTURE_PWD_ERROR_COUNT);
        lv_lock.setClearPasssword(false);
        if (mode != LockMode.SETTING_PASSWORD) {
            tvHint.setText("请输入已经设置过的密码");
            lv_lock.setOldPassword(password);
        } else {
            tvHint.setText("请输入要设置的密码");
        }
        tvHint.setText(msg);
    }


    /**
     * 密码输入监听
     */
    OnCompleteListener onCompleteListener = new OnCompleteListener() {
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
            LocalBroadcastUtil.sendLocalBroadcast(new Intent(EventConstants.JUMPHOMETOO));
        }

        @Override
        public void onError(String errorTimes) {
            tvHint.setText(R.string.gesture_pwd_error_again);
        }

        @Override
        public void onPasswordIsShort(int passwordMinLength) {
            tvHint.setText("密码不能少于" + passwordMinLength + "个点");
        }

        @Override
        public void onAginInputPassword(LockMode mode, String password, int[] indexs) {
            tvHint.setText("请再次输入密码");
        }


        @Override
        public void onInputNewPassword() {
            tvHint.setText("请输入新密码");
        }

        @Override
        public void onEnteredPasswordsDiffer() {
            tvHint.setText("两次输入的密码不一致");
        }

        @Override
        public void onErrorNumberMany() {
            tvHint.setText("密码错误次数超过限制，不能再输入");
        }

    };


    /**
     * 密码相关操作完成回调提示
     */
    private String getPassWordHint(String password) {
        String str = null;
        switch (lv_lock.getMode()) {
            case SETTING_PASSWORD:
                str = "密码设置成功";
                savePassword(password);
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
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
                str = "密码修改成功";
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
                savePassword(password);
                break;
            case VERIFY_PASSWORD:
                if (isNetVerify)
                    break;
                str = "密码正确";
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
                savePassword(password);
                break;
            case CLEAR_PASSWORD:
                str = "密码已经清除";
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
                break;
        }
        return str;
    }

    /**
     * 保存密码
     */
    private void savePassword(String password) {
        SpUtil.getsp().putString("GesPWD", password);   //GesPWD 手势密码
        SpUtil.getsp().putBoolean("hasGesPWD", true);   //hasGesPWD 是否有手势密码
        if (initGes) {
//            SpUtil.getspForBinding().putBoolean("initGesPWD", true);   //initGesPWD 初始化时设置手势
            SpUtil.getsp().putBoolean("initGesPWD", true);   //initGesPWD 初始化时设置手势
        }
    }


    /**
     * 设置解锁模式
     */
    private void setLockMode(LockMode mode) {
        String str = "";
        switch (mode) {
            case CLEAR_PASSWORD:
                str = "清除密码";
                setLockMode(LockMode.CLEAR_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case EDIT_PASSWORD:
                str = "输入原密码";
                setLockMode(LockMode.EDIT_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case SETTING_PASSWORD:
                str = "设置密码";
                setLockMode(LockMode.SETTING_PASSWORD, null, str);
                break;
            case VERIFY_PASSWORD:
                str = "验证密码";
                setLockMode(LockMode.VERIFY_PASSWORD, PasswordUtil.getPin(this), str);
                break;
        }
        tvHint.setText(str);
    }

}
