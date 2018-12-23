package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.fragment.AgainScanFragment;
import com.itrus.ikey.safecenter.TOPMFA.fragment.GestureOpenFragment;
import com.itrus.ikey.safecenter.TOPMFA.fragment.PhoneTokenFragment;
import com.itrus.ikey.safecenter.TOPMFA.receiver.HomeWatcherReceiver;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.EventConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.ExampleUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SharedPreferencesUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static String random;
    public static SharedPreferencesUtil sp;
    public static boolean isForeground = false;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView tv_title;
    private LinearLayout ll_home, ll_again, ll_password;

    private static final String TAG = "JPush";

    public static String USERNAME = null;
    public static Context CONTEXT = null;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    private long exitTime = 0;

    protected PhoneTokenFragment homeFragment;
    protected AgainScanFragment againScanFragment;
    protected GestureOpenFragment gestureOpenFragment;

    /****
     * 锁屏通知监听器
     */
    private HomeWatcherReceiver mHomeKeyReceiver = null;

    public static void go2MainActivity(Context activity, String userName) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(AppConstants.USER_NAME, userName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_new_main);
        USERNAME = getIntent().getStringExtra(AppConstants.USER_NAME);
        CONTEXT = ctx;
        initDatas();
       // getOTPSecret();
        initJpush();
        //注册锁屏通知
        registerHomeKeyReceiver(this);
    }


    private void getOTPSecret() {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/getUserMobileOtpSecret")
                .addParams("device", StringsUtil.getDevicesInfo(ctx))
                .addParams("username", MainActivity.USERNAME)
                .build()
                .execute(new MyStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        DialogUtil.dismissProgress();
                        DialogUtil.showAlert(ctx, e.toString(), null);
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DialogUtil.dismissProgress();
                        try {
                            String status = new JSONObject(response).getString("status");
                            String message = new JSONObject(response).getString("message");
                            if (Contants.SUCCESS.equals(status)) {
                                String secret = new JSONObject(response).getJSONObject("data").getString("secret");
                                SpUtil.getsp().putString("secret", secret);
                                SpUtil.getsp().putBoolean("initSecret", true);


                            } else {
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void initJpush() {
        //初始化极光推送
        JPushInterface.setDebugMode(true);      // 设置开启日志,发布时请关闭日志
        if (JPushInterface.isPushStopped(getApplicationContext()))
            JPushInterface.resumePush(getApplicationContext());
        else
            JPushInterface.init(getApplicationContext());              // 初始化 JPush
        isSetTagAndAlias();                             //设置别名
        setStyleBasic();                        // 设置通知样式.
    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
//        Toast.makeText(this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }

    private void isSetTagAndAlias() {
        SharedPreferences spdefault = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = spdefault.edit();

        if (!spdefault.getString("lastuser", "").equals(USERNAME)) {
            //设置别名和TAG
            String prefix = spdefault.getString("prefix", null);
            if (Validator.isBlank(prefix))
                prefix = "1";
            setAlias(prefix + "_" + USERNAME);                        // 设置别名
        }
        //更新最后一次登录的用户
        editor.putString("lastuser", MainActivity.USERNAME);
        editor.commit();
    }

    private void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
//            Toast.makeText(this, R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
//            Toast.makeText(this, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        isForeground = false;
        super.onPause();
    }


    private void initDatas() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        tv_title = (TextView) findViewById(R.id.tv_title);
        LinearLayout headerLayout = (LinearLayout) navigationView.getHeaderView(0);

        ll_home = (LinearLayout) headerLayout.findViewById(R.id.ll_home);
        ll_again = (LinearLayout) headerLayout.findViewById(R.id.ll_again);
//        ll_time = (LinearLayout) headerLayout.findViewById(R.id.ll_time);
        ll_password = (LinearLayout) headerLayout.findViewById(R.id.ll_password);

        toolbar.setNavigationIcon(R.drawable.menu);//修改图标
        toolbar.setTitle("");//不显示左上角的主标题
        drawer.setScrimColor(Color.TRANSPARENT);//取消阴影浮层
        setSupportActionBar(toolbar);

        gotoHomeFragment();
        setNavTabSelect(0, R.string.nav_home);
        initEvent();
    }

    private void initEvent() {
        ll_home.setOnClickListener(this);
        ll_again.setOnClickListener(this);
//        ll_time.setOnClickListener(this);
        ll_password.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 判断点击icon时是关闭还是打开drawerLayout
                 */
                boolean drawerOpen = drawer.isDrawerOpen(GravityCompat.START);
                if (drawerOpen) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 跳转首页
     */
    private void gotoHomeFragment() {
        setNavTabSelect(0, R.string.nav_home);

        String tag = PhoneTokenFragment.class.getName();
        homeFragment = (PhoneTokenFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (null == homeFragment) {
            homeFragment = new PhoneTokenFragment();
        }
        switchContent(R.id.FrameLayout, curFragment, homeFragment, tag);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                myapp.exit();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setNavTabSelect(int index, int titleResId) {
        tv_title.setText(titleResId);
        ll_home.setBackgroundColor(index == 0 ? Color.parseColor("#8a8a8a") : Color.TRANSPARENT);
        ll_again.setBackgroundColor(index == 1 ? Color.parseColor("#8a8a8a") : Color.TRANSPARENT);
//        ll_time.setBackgroundColor(index == 2 ? Color.parseColor("#8a8a8a") : Color.TRANSPARENT);
        ll_password.setBackgroundColor(index == 3 ? Color.parseColor("#8a8a8a") : Color.TRANSPARENT);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> set) {
                            String logs;
                            switch (code) {
                                case 0:
                                    logs = "Set tag and alias success";
                                    Log.i(TAG, logs);
                                    //记录成功状态
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("hasAliasAndTag", true);
                                    editor.commit();
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    Log.i(TAG, logs);
                                    if (ExampleUtil.isConnected(getApplicationContext())) {
                                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 15);
                                    } else {
                                        Log.i(TAG, "No network");
                                    }
                                    break;

                                default:
                                    logs = "Failed with errorCode = " + code;
//                                    Log.e(TAG, logs);
                            }

//                            ExampleUtil.showToast(logs, getApplicationContext());
                        }
                    });
                    break;

                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> tags) {
                            String logs;
                            switch (code) {
                                case 0:
                                    logs = "Set tag and alias success";
                                    Log.i(TAG, logs);
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    Log.i(TAG, logs);
                                    if (ExampleUtil.isConnected(getApplicationContext())) {
                                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 15);
                                    } else {
                                        Log.i(TAG, "No network");
                                    }
                                    break;

                                default:
                                    logs = "Failed with errorCode = " + code;
//                                    Log.e(TAG, logs);
                            }

//                            ExampleUtil.showToast(logs, getApplicationContext());
                        }
                    });
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    /**
     * 跳转 再次扫描界面
     */
    private void gotoAgainFragment() {
        setNavTabSelect(1, R.string.nav_again);
        String tag = AgainScanFragment.class.getName();
        againScanFragment = (AgainScanFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (null == againScanFragment) {
            againScanFragment = new AgainScanFragment();
        }
        switchContent(R.id.FrameLayout, curFragment, againScanFragment, tag);
    }

//    /**
//     * 跳转 时间校准界面
//     */
//    private void gotoTimeFragment() {
//          setNavTabSelect(2, R.string.nav_time);
//        String tag = TimeMendFragment.class.getName();
//        timeMendFragment = (TimeMendFragment) getSupportFragmentManager().findFragmentByTag(tag);
//        if (null == timeMendFragment) {
//            timeMendFragment = new TimeMendFragment();
//        }
//        switchContent(R.id.FrameLayout, curFragment, timeMendFragment, tag);
//    }

    /**
     * 跳转 密码界面
     */
    private void gotoPasswordFragment() {
        setNavTabSelect(3, R.string.nav_password);

        String tag = GestureCipherActivity.class.getName();
        gestureOpenFragment = (GestureOpenFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (null == gestureOpenFragment) {
            gestureOpenFragment = new GestureOpenFragment();
        }
        switchContent(R.id.FrameLayout, curFragment, gestureOpenFragment, tag);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                gotoHomeFragment();
                break;
            case R.id.ll_again:
                gotoAgainFragment();
                break;
//            case R.id.ll_time:
//                gotoTimeFragment();
//                break;
            case R.id.ll_password:
                gotoPasswordFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
        super.wrapLocalBroadcastFilter(filter);
        filter.addAction(EventConstants.JUMPHOME);
        filter.addAction(EventConstants.JUMPHOMETOO);
    }


    @Override
    protected void dealLocalBroadcast(Context context, Intent intent) {
        super.dealLocalBroadcast(context, intent);
        String action = intent.getAction();
        switch (action) {
            case EventConstants.JUMPHOME:
                gotoHomeFragment();
                break;
            case EventConstants.JUMPHOMETOO:
                gotoHomeFragment();
                break;
        }
    }

    /****
     * 注册锁屏监听器
     *
     * @param context
     */
    private void registerHomeKeyReceiver(Context context) {
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        homeFilter.setPriority(1000);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHomeKeyReceiver)
            unregisterReceiver(mHomeKeyReceiver);
        mHomeKeyReceiver = null;
    }
}
