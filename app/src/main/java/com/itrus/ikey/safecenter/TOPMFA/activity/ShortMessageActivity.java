package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.BySMUserDeatilBean;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.GsonUtils;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.URLEncodedUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * @Date 2018/8/30 下午2:16
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */
public class ShortMessageActivity extends BaseActivity {


    @BindView(R.id.et_short_message_show)
    EditText etShortMessageShow;

    @BindView(R.id.tv_actvication)
    TextView tvActvication;

    @BindView(R.id.tv_clera_data)
    TextView tvCleraData;

    @BindView(R.id.tv_left)
    TextView tvLeft;

    @BindView(R.id.tv_title)
    TextView tvTitle;


    private String shortMessage;
    private String devicesInfo;


    public static void goShortMessageActivity(Context activity) {
        Intent intent = new Intent(activity, ShortMessageActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_short_message);
        ButterKnife.bind(this);

        devicesInfo = StringsUtil.getDevicesInfo(ctx);

        initListen();
    }

    private void initListen() {

        tvTitle.setText("短信激活");
        //确认激活监听
        tvActvication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //获取到输入框里的值

                shortMessage = TextUtils.isEmpty(etShortMessageShow.getText().toString()) ? "" : etShortMessageShow.getText().toString().trim();

                //验证有效性准备请求接口
                verifySMsForActive(shortMessage);
            }
        });


        tvCleraData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etShortMessageShow.setText(null);
            }
        });


        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onResume() {

        super.onResume();
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

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            DialogUtil.dismissProgress();

                            BySMUserDeatilBean bySMUserDeatilBean = GsonUtils.fromJson(response, BySMUserDeatilBean.class);
                            //直接判断里面的data数据是否为空
                            if (null != bySMUserDeatilBean && null != bySMUserDeatilBean.getData()) {

                                BySMUserDeatilBean.DataBean.UserBean userData = bySMUserDeatilBean.getData().getUser();
                                if (!TextUtils.isEmpty(userData.getUsername())) {
                                    MyApplication.getInstance().setUSERNAME(userData.getUsername());
                                    MyApplication.getInstance().setDEVICESINFO(StringsUtil.getDevicesInfo(ctx));
                                }
                                //如果短信验证码正确，那么我就开始绑定设备

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


}
