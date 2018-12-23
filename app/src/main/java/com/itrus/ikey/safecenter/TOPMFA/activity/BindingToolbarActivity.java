package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.SMGetTokenBean;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.GsonUtils;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 绑定页面
 */
public class BindingToolbarActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tb_toolbar)
    Toolbar tb_toolbar;
    @BindView(R.id.tv_binding_tel)
    TextView tvBindingTel;

    private String username = null;
    private String uuid = null;
    private String random = null;
    private String devicesInfo;
    public static final int REQUEST_CODE = 35421;
    private String shortMessage = null;


    public static void go2BindingToolbarActivity(Context activity, String USERNAME, String uuid, String random) {
        Intent intent = new Intent(activity, BindingToolbarActivity.class);
        intent.putExtra("username", MyApplication.getInstance().getUSERNAME());
        intent.putExtra("uuid", uuid);
        intent.putExtra("random", random);
        activity.startActivity(intent);
    }



    public static void go2BindingToolbarActivity(Context activity, String shortMessage) {

        Intent intent = new Intent(activity, BindingToolbarActivity.class);
        intent.putExtra("shortMessage",shortMessage);
        activity.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_toolbar);
        ButterKnife.bind(this);
        initToolBar(tb_toolbar);
        tvTitle.setText("扫码提示");
        //扫码部分
        devicesInfo = StringsUtil.getDevicesInfo(ctx);
        username = MyApplication.getInstance().getUSERNAME();
        uuid = getIntent().getStringExtra("uuid");
        random = getIntent().getStringExtra("random");
        //短信部分
        shortMessage = getIntent().getStringExtra("shortMessage");

        tvBindingTel.setText(username);
    }

    @OnClick({ R.id.btn_binding_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_binding_next:
                //这个时候就需要判断需要进行哪一种激活，如果短信验证码存在，短信激活，否则的话走扫码激活

                if (!TextUtils.isEmpty(shortMessage)){
                    shortMessageBingdingUser();
                }else{
                bingdingUser();
                }
                break;
        }
    }










    /**
     * 短信方式绑定设备
     */

    private void shortMessageBingdingUser() {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/saveUserDeviceInfoBySms")
                .addHeader("X-Hawk-Client", devicesInfo)
                .addParams("message", shortMessage)
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
                        //获取动态令牌服务
                        getOTPSecret();
                        //这里注意Token的这个类不知道干什么
                        SMGetTokenBean smGetTokenBean = GsonUtils.fromJson(response, SMGetTokenBean.class);

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isFirst", true);
                        editor.putString("username", username);
                        editor.putString("onlyusername", username);
                        editor.commit();
                        SpUtil.getsp().putString("devicesInfo", devicesInfo);
                        SpUtil.getsp().putBoolean("isBinding", true);
                        MainActivity.USERNAME = username;
                        Intent intent = new Intent(ctx, BindingOKActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();

                    }
                });
    }




    /**
     * 获取OTP令牌服务
     */
    private void getOTPSecret() {
        String username = MyApplication.getInstance().getUSERNAME();
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/getUserMobileOtpSecret")
                .addParams("device", devicesInfo)
                .addParams("username", username)
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







    private void bingdingUser() {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/saveUserDeviceInfoByQrCode")
                .addHeader("X-Hawk-Client", devicesInfo)
                .addParams("username", username)
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
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("isFirst", true);
                                editor.putString("username", username);
                                editor.putString("onlyusername", username);
                                editor.commit();
                                SpUtil.getsp().putString("devicesInfo", devicesInfo);
                                SpUtil.getsp().putBoolean("isBinding", true);
                                MainActivity.USERNAME = username;
                                Intent intent = new Intent(ctx, BindingOKActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
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



}
