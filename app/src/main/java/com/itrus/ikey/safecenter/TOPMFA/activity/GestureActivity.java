package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.EventBusMessage;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppUtil;
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
 * Created by STAR on 2016/8/3.
 */
public class GestureActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tb_toolbar)
    Toolbar tb_toolbar;
    @BindView(R.id.lv_lock)
    CustomLockView lv_lock;
    @BindView(R.id.tvHint)
    TextView tvHint;
    @BindView(R.id.rv_back)
    RippleView rvBack;

    private boolean isNetVerify = false;
    private boolean loginVerify = false;
    private boolean initGes = false;
    private String FROM = "";

    public static final int LOGIN_AUTH_RESULT_CODE = 10088;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_old);
        ButterKnife.bind(this);
        initToolBarWithBack(tb_toolbar);
        String title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        initGes = getIntent().getBooleanExtra("initGes", false);
        initView();
        FROM = getIntent().getStringExtra("from");
        loginVerify = getIntent().getBooleanExtra("loginVerify", false);
    }

    public void initView() {
        //显示绘制方向
        lv_lock.setShow(true);
        //允许最大输入次数
        lv_lock.setErrorNumber(AppConstants.GESTURE_PWD_ERROR_COUNT);
        //密码最少位数
        lv_lock.setPasswordMinLength(4);
//        //编辑密码或设置密码时，是否将密码保存到本地，配合setSaveLockKey使用
//        lv_lock.setSavePin(true);
//        //保存密码Key
//        lv_lock.setSaveLockKey(Contants.PASS_KEY);
        lv_lock.setOnCompleteListener(onCompleteListener);
        rvBack.setOnRippleCompleteListener(this);
        //设置模式
        LockMode lockMode = (LockMode) getIntent().getSerializableExtra(Contants.INTENT_SECONDACTIVITY_KEY);
        setLockMode(lockMode);
    }


    /**
     * 密码输入模式
     */
    private void setLockMode(LockMode mode, String password, String msg) {
        lv_lock.setMode(mode);
        lv_lock.setErrorNumber(3);
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
                msg.setMessage(getIntent().getStringExtra("random"));
                EventBus.getDefault().post(msg);
            }
            if (loginVerify) {
                setResult(LOGIN_AUTH_RESULT_CODE);
            }
            finish();

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


    @Override
    public void onComplete(RippleView rippleView) {
        onBackPressed();
    }
}
