package com.itrus.ikey.safecenter.TOPMFA.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.gesture.GestureNomalActivity;
import com.itrus.ikey.safecenter.TOPMFA.manager.SystemBarTintManager;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.EffectUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.LocalBroadcastUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.zhy.http.okhttp.OkHttpUtils;

/**
 * Created by STAR on 2016/8/3.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected MyApplication myapp;
    protected BaseActivity ctx;

    protected Fragment curFragment = null;
    private IntentFilter filter;
    private boolean isDestroyed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myapp = getMyapp();
        ctx = this;
        myapp.addActivity(ctx);
//        setStatusBarStyle(R.color.colorPrimary);
        filter = new IntentFilter();
        wrapLocalBroadcastFilter(filter);
        registLocalBroadCast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.isGesture) {
            AppConstants.isGesture = false;
            if (AppUtil.goGesturePasswordActivity()) {
                String userName = PreferenceManager.getDefaultSharedPreferences(ctx).getString(AppConstants.USER_NAME, "");
                GestureNomalActivity.go2GestureNomalActivity(this, false,false, userName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        myapp.removeActivity(ctx);
        OkHttpUtils.getInstance().cancelTag("anxinyun");
        isDestroyed = true;
        unregisterLocalBroadCast();
        super.onDestroy();
    }

    public MyApplication getMyapp() {
        return MyApplication.getInstance();
    }

    protected void initToolBar(Toolbar tb_toolbar) {
        if (tb_toolbar != null) {
            setSupportActionBar(tb_toolbar);
            setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
        }
    }

    protected void initToolBarWithBack(Toolbar tb_toolbar) {
        if (tb_toolbar != null) {
            setSupportActionBar(tb_toolbar);
            setTitle("");
            tb_toolbar.setNavigationIcon(R.drawable.ic_action_back);
        } else {
        }

    }


    ////////////////////////////////////////////////////////////////////////
    //导航条渐变
    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * 浸染状态栏底色背景，若使用布局的背景图浸染，设置resId为透明色即可
     * 其它情况设置resId为ToolBar(ActionBar)的背景色
     *
     * @param resId 颜色id
     */
    public void setStatusBarStyle(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(resId);//通知栏所需颜色
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkEditIsNull(String text, final EditText et, String msg) {
        if (Validator.isBlank(text)) {
            et.requestFocus();
            et.setError(msg);
            EffectUtil.showShake(this, et);
            return true;
        }
        return false;
    }

    public boolean checkTextIsMobile(String text, EditText et, String msg) {
        if (!Validator.isMobile(text)) {
            et.requestFocus();
            et.setError(msg);
            EffectUtil.showShake(this, et);
            return false;
        }
        return true;
    }

    public boolean checkPasswordIsRight(String text, EditText et, String msg) {
        if (!Validator.isNumAndChar8_20(text)) {
            et.requestFocus();
            et.setError(msg);
            EffectUtil.showShake(this, et);
            return false;
        }
        return true;
    }


    public void switchContent(int id, Fragment from, Fragment to, String tag) {
        if (from != to) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            /*if(null!=from){
                transaction.setCustomAnimations(
                        R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
            }*/
            if (!to.isAdded()) {    // 先判断是否被add过
                if (null == from) {
                    transaction.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment).add(id, to, tag).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                } else {
                    transaction.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment).hide(from).add(id, to, tag).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                }
            } else {
                if (null == from) {
                    transaction.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                } else {
                    transaction.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment).hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                }
            }
            curFragment = to;
        }
    }


    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
    }


    protected void dealLocalBroadcast(Context context, Intent intent) {
    }


    protected void registLocalBroadCast() {
        if (null != m_receiver && null != filter) {
            LocalBroadcastUtil.registerLocalBroadCast(m_receiver, filter);
        }
    }

    protected void unregisterLocalBroadCast() {
        if (null != m_receiver) {
            LocalBroadcastUtil.unregisterLocalBroadCast(m_receiver);
        }
    }

    private final BroadcastReceiver m_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isDestroy()) {
                return;
            }
            dealLocalBroadcast(context, intent);
        }
    };

    public boolean isDestroy() {
        return isDestroyed;
    }

    protected void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showMsg(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
