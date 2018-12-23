package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.BySMUserDeatilBean;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.fragment.TokenCopyDialogFragment;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogClickCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.GsonUtils;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.URLEncodedUtil;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 *
 */
public class LoginByPasswordActivity extends BaseActivity {

    private String username = null;
    private String devicesInfo;
    private long exitTime;
    public static final int REQUEST_CODE = 35421;


    public static void go2LoginByPasswordActivity(Context activity) {
        Intent intent = new Intent(activity, LoginByPasswordActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
        super.wrapLocalBroadcastFilter(filter);
        filter.addAction(AppConstants.ACTION.TOKEN_SMS_CONFIRM);
    }

    @Override
    protected void dealLocalBroadcast(Context context, Intent intent) {
        super.dealLocalBroadcast(context, intent);
        if (intent.getAction().equals(AppConstants.ACTION.TOKEN_SMS_CONFIRM)) {
            String msg = intent.getStringExtra("msg");
            verifySMsForActive(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        devicesInfo = StringsUtil.getDevicesInfo(ctx);
        MyApplication.getInstance().setDEVICESINFO(devicesInfo);

        //短信激活按钮打开
        findViewById(R.id.btn_open_short_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortMessageActivity.goShortMessageActivity(ctx);
            }
        });
        //GangUpInvite(this);
    }


    @Override
    protected void onResume() {
        //判断是否需要剪切板监听
//        orNOtListenShortMessage();
//
        GangUpInvite(this);
        super.onResume();
    }

    /**
     * 剪切板监听
     *
     * @param context
     */
    public void GangUpInvite(final Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(null);
        //无数据时直接返回
        if (!clipboard.hasPrimaryClip()) {
            return;
        }
        //如果是文本信息
        if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData cdText = clipboard.getPrimaryClip();
            ClipData.Item item = cdText.getItemAt(0);
            //此处是TEXT文本信息
            if (item.getText() != null) {
                final String shortMessage = item.getText().toString();
                //如果含有安信令牌字段就要弹窗
                if (shortMessage.contains("安信令牌")) {
                    //如果SP里没有，则显示
                    showIsDeleteDialog(shortMessage);
                    clipboard.setPrimaryClip(null);
                }
            }
        }
    }


    /**
     * 展示弹窗
     *
     * @param msg
     */
    protected void showIsDeleteDialog(final String msg) {
        //显示了就要存储

        setSharedPreference("shortMessage", msg);
        TokenCopyDialogFragment tokenCopyDialogFragment = new TokenCopyDialogFragment(msg, new DialogClickCallback() {
            @Override
            public void confirm() {
                verifySMsForActive(msg);
            }

            @Override
            public void cancle() {

            }
        });
        tokenCopyDialogFragment.show(getSupportFragmentManager(), TokenCopyDialogFragment.class.getName());
    }


    /**
     * 验证有效性，准备请求接口
     *
     * @param shortMessage
     */
    private void verifySMsForActive(final String shortMessage) {

        if (!TextUtils.isEmpty(shortMessage)) {

            String message = URLEncodedUtil.toURLEncoded(shortMessage);

            OkHttpUtils.post()
                    .url(GlobalConfig.getBaseurl() + "user/verifySMsForActive")
                    .addParams("message", message)
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
                            BySMUserDeatilBean bySMUserDeatilBean = GsonUtils.fromJson(response, BySMUserDeatilBean.class);

                            if (null != bySMUserDeatilBean && null != bySMUserDeatilBean.getData()) {

                                BySMUserDeatilBean.DataBean.UserBean userData = bySMUserDeatilBean.getData().getUser();
                                if (!TextUtils.isEmpty(userData.getUsername())) {
                                    MyApplication.getInstance().setUSERNAME(userData.getUsername());
                                    MyApplication.getInstance().setDEVICESINFO(StringsUtil.getDevicesInfo(ctx));
                                }
                                //如果短信验证码正确，那么我就开始绑定设备
                                //获取动态令牌服务
                                getOTPSecret();
                                //跳转到这个页面BindingToolbarActivity
                                BindingToolbarActivity.go2BindingToolbarActivity(ctx, shortMessage);

                            } else {
                                DialogUtil.showAlert(ctx, bySMUserDeatilBean.getMessage(), null);

                            }

                        }
                    });

        } else {
            Toast.makeText(this, "您的短信激活码有误", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断是否需要剪切板监听
     */
    private void orNOtListenShortMessage() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String username = sp.getString(AppConstants.USER_NAME, AppConstants.EMPTY);

        if (TextUtils.isEmpty(username)) {//用户名为空需要监听剪切板事件
            GangUpInvite(this);
        } else {
            boolean isBinding = sp.getBoolean(AppConstants.IS_BINDING, false);
            if (isBinding) {//绑定则关掉剪切板事件

            } else {//未绑定需要释放剪切板事件
                GangUpInvite(this);
            }
        }
    }


    @OnClick({R.id.btn_login_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_login:
                onScanClick();
                break;
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

    private void onScanClick() {
        PermissionGen.with(ctx)
                .addRequestCode(100)
                .permissions(Manifest.permission.CAMERA)
                .request();
    }


    @PermissionSuccess(requestCode = 100)
    public void openContact() {
        Intent intent = new Intent(ctx, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @PermissionFail(requestCode = 100)
    public void failContact() {
        Toast.makeText(ctx, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    private void getOTPSecret() {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/getUserMobileOtpSecret")
                .addParams("device", devicesInfo)
                .addParams("username", MyApplication.getInstance().getUSERNAME())
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
//                                SpUtil.getspFor getRadom();
                            } else {
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void confirmBinding(final String uuid, final String random) {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/verifyQrCodeForActive")
                .addParams("uuid", uuid)
                .addParams("random", random)
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
                                username = new JSONObject(response).getJSONObject("data").getJSONObject("user").getString("username");

                                MyApplication.getInstance().setUSERNAME(username);
                                //获取OTP服务的秘钥
                                getOTPSecret();

                                //跳转到绑定页面
                                BindingToolbarActivity.go2BindingToolbarActivity(ctx, MyApplication.getInstance().getUSERNAME(), uuid, random);
                                finish();
                            } else {
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    try {
                        String uuid = new JSONObject(result).getString("uuid");
                        String random = new JSONObject(result).getString("random");
                        confirmBinding(uuid, random);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ctx, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    public String[] getSharedPreference(String key) {
        String regularEx = ",";
        String[] str = null;
        SharedPreferences sp = this.getSharedPreferences("shortMessageData", Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "");
        str = values.split(regularEx);

        return str;
    }

    public void setSharedPreference(String key, String values) {

        SharedPreferences sp = this.getSharedPreferences("shortMessageData", Context.MODE_PRIVATE);

            SharedPreferences.Editor et = sp.edit();
            et.putString(key, values);
            et.commit();
        }




}
