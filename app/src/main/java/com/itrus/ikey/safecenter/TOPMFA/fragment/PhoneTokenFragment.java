package com.itrus.ikey.safecenter.TOPMFA.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseFragment;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.FootDetailBean;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyCallback;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.GsonUtils;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.algorithm.TimeBasedOTP;
import com.itrus.ikey.safecenter.TOPMFA.widget.view.ImageNumber;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

import static com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig.getBaseurl;

/**
 * 手机令牌页面Frg
 */
public class PhoneTokenFragment extends BaseFragment implements Handler.Callback {

    @BindView(R.id.pv_linear_buffer)
    ProgressBar pb_process_opt;

    @BindView(R.id.in0)
    ImageNumber in0;

    @BindView(R.id.in1)
    ImageNumber in1;

    @BindView(R.id.in2)
    ImageNumber in2;

    @BindView(R.id.in3)
    ImageNumber in3;

    @BindView(R.id.in4)
    ImageNumber in4;

    @BindView(R.id.in5)
    ImageNumber in5;

    @BindView(R.id.tv_last_login_time)
    TextView tvLastLoginTime;


    private static boolean isConnect = false;



    private Handler handler;
    public static final int REQUEST_CODE = 35421;
    private String otpcode = "";
    private long time;
    private int processValue = 0;
    private TimeBasedOTP timeBasedOTP;
    // private HawkMobileClientAndroid client = HawkMobileClientAndroid.getInstance();


    private int a0 = 0, a1 = 0, a2 = 0, a3 = 0, a4 = 0, a5 = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frg_phone_token, container, false);
        ButterKnife.bind(this, rootView);
        handler = new Handler(this);
        pb_process_opt.setMax(600);

        //tvLastLoginTime.setText(MainActivity.LOGINTIME);

        //在一开始的时候就启动进度条,OTP服务启动

        initOTPServer();
        obtainServerTime();
        getLoginWeekDatas("1");
        return rootView;
    }






    private void initOTPServer() {
        try {
            timeBasedOTP = TimeBasedOTP.getInstance("EasySHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String key = SpUtil.getsp().getString("secret", "");
        timeBasedOTP.init(6, Hex.decode(key));

        long timeMillis = System.currentTimeMillis();
        long timeMillis2 = (timeMillis / 100) % 600;
        pb_process_opt.setProgress((int) timeMillis2);
        handler.sendEmptyMessageDelayed(1, 0);
        otpcode = timeBasedOTP.generate(timeMillis);
        //设置OPT 动态密码
        setOpt(otpcode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan_photo) {

            PermissionGen.with(PhoneTokenFragment.this)
                    .addRequestCode(100)
                    .permissions(Manifest.permission.CAMERA)
                    .request();
        }
        return super.onOptionsItemSelected(item);
    }

    @PermissionSuccess(requestCode = 100)
    public void openContact() {
        Intent intent = new Intent(ctx, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @PermissionFail(requestCode = 100)
    public void failContact() {
//        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    client.init(ctx, new ServerAddress("http", "124.205.224.180", "9075", "hawk"));
//                    client.logonByPass("13111115799", "user", "yz410816", new HawkMobileClient.HawkCallback() {
//                        @Override
//                        public void onSuccess(HawkMobileClient.HawkCallbackResult hawkCallbackResult) {
//                            client.handleQRCodeMsg(result, (FragmentActivity) ctx, new HawkMobileClient.HawkCallback() {
//                                @Override
//                                public void onSuccess(HawkMobileClient.HawkCallbackResult hawkCallbackResult) {
//                                    Toast.makeText(ctx, "登录成功", Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void onFailure(HawkMobileClientException e) {
//                                    Toast.makeText(ctx, "登录失败", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onFailure(HawkMobileClientException e) {
//
//                        }
//                    });
                    try {
                        String uuid = new JSONObject(result).getString("uuid");
                        scanQrCode(uuid);
                    } catch (JSONException e) {
                        Toast.makeText(ctx, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ctx, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        long timeMillis = System.currentTimeMillis();
        long timeMillis2 = (timeMillis / 100) % 600;
        switch (msg.what) {
            case 1:
                processValue = pb_process_opt.getProgress();
                pb_process_opt.setProgress((int) timeMillis2);
                if (pb_process_opt.getProgress() == 600 || pb_process_opt.getProgress() < processValue) {
                    handler.sendEmptyMessageDelayed(2, 0);
                } else {
                    handler.sendEmptyMessageDelayed(1, 100);
                }

                break;
            case 2:

                otpcode = timeBasedOTP.generate(timeMillis);
                //进度条到头后,开始滚动OTP
                setOpt(otpcode);
                pb_process_opt.setProgress((int) timeMillis2);
                handler.sendEmptyMessageDelayed(1, 100);
                break;
            case 3:
                break;
        }
        return true;
    }

    private void setOpt(final String random) {
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setChangeNumbers(random);
            }
        });
    }


    private void setChangeNumbers(String random) {
        try {
            Log.d("TEST", "random:" + random);
            int temp0 = Integer.valueOf(random.substring(0, 1));
            int temp1 = Integer.valueOf(random.substring(1, 2));
            int temp2 = Integer.valueOf(random.substring(2, 3));
            int temp3 = Integer.valueOf(random.substring(3, 4));
            int temp4 = Integer.valueOf(random.substring(4, 5));
            int temp5 = Integer.valueOf(random.substring(5, 6));

            in0.setChangeNumber(a0, temp0);
            in1.setChangeNumber(a1, temp1);
            in2.setChangeNumber(a2, temp2);
            in3.setChangeNumber(a3, temp3);
            in4.setChangeNumber(a4, temp4);
            in5.setChangeNumber(a5, temp5);

            a0 = temp0;
            a1 = temp1;
            a2 = temp2;
            a3 = temp3;
            a4 = temp4;
            a5 = temp5;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void scanQrCode(final String uuid) {
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/auth/scanQrCode")
                .addHeader("X-Hawk-Client", StringsUtil.getDevicesInfo(ctx))
                .addParams("username", MainActivity.USERNAME)
                .addParams("uuid", uuid)
                .build()
                .execute(new MyStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String status = new JSONObject(response).getString("status");
                            String message = new JSONObject(response).getString("message");
                            if ("0x0000".equals(status)) {
                                final String des = new JSONObject(response).getJSONObject("data").getJSONObject("transaction").getString("description");
                                final String uuuid = new JSONObject(response).getJSONObject("data").getJSONObject("transaction").getString("uuid");
                                DialogUtil.showAlertYesNo(ctx, des, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean ok) {
                                        if (ok) {
                                            handleQrCode(uuuid);
                                        } else {

                                        }
                                    }
                                });
                            } else {
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void handleQrCode(String uuid) {
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "user/auth/handleQrCode")
                .addHeader("X-Hawk-Client", StringsUtil.getDevicesInfo(ctx))
                .addParams("username", MainActivity.USERNAME)
                .addParams("uuid", uuid)
                .addParams("status", "2")
                .build()
                .execute(new MyStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String message = new JSONObject(response).getString("message");
                            DialogUtil.showAlert(ctx, message, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void obtainServerTime() {
    }

    /**
     * 获取我的足迹
     *
     * @param count
     */
    private void getLoginWeekDatas(String count) {
        OkHttpUtils.post()
                .tag("anxinyun")
                .url(getBaseurl() + "user/myHistory")
                .addHeader("X-Hawk-Client", StringsUtil.getDevicesInfo(ctx))
                .addParams("username", MainActivity.USERNAME)
                .addParams("max", count)
                .addParams("time", "")
                .build()
                .execute(new MyStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        e.getMessage();
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        FootDetailBean bean = GsonUtils.fromJson(response, FootDetailBean.class);
                        if (!TextUtils.isEmpty(bean.getRecordList().get(0).getTime())) {
                            tvLastLoginTime.setText(bean.getRecordList().get(0).getTime() + "    ");
                        }

                    }

                });
    }


}
