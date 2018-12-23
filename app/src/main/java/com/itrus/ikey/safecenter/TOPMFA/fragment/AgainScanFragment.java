package com.itrus.ikey.safecenter.TOPMFA.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.BindingToolbarActivity;
import com.itrus.ikey.safecenter.TOPMFA.activity.GestureActivity;
import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseFragment;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.EventConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.LocalBroadcastUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.RemoveData;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.leo.gesturelibray.enums.LockMode;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 * 重新扫描页面frg
 */
public class AgainScanFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.iv_scan)
    ImageView ivScan;

    @BindView(R.id.tv_scan)
    TextView tvScan;

    @BindView(R.id.tv_jump)
    TextView tvJump;


    private String username = null;
    private String uuid = null;
    private String devicesInfo;
    private PopupWindow popupWindow;
    private boolean init;
    private long exitTime;
    public static final int AUTH_REQUEST_CODE = 10086;
    public static final int INIT_CERT_CODE = 10010;
    public static final int INIT_GES_CODE = 10011;
    public static final int REQUEST_CODE = 35421;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frg_again_scann, container, false);
        ButterKnife.bind(this, rootView);
        devicesInfo = StringsUtil.getDevicesInfo(ctx);
        ivScan.setOnClickListener(this);
        tvJump.setOnClickListener(this);
        return rootView;
    }



    private void onScanClick() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

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


    private void setGesture(LockMode mode) {
        Intent intent = new Intent(ctx, GestureActivity.class);
        intent.putExtra(Contants.INTENT_SECONDACTIVITY_KEY, mode);
        if (mode == LockMode.EDIT_PASSWORD)
            intent.putExtra("title", "修改手势密码");
        if (mode == LockMode.SETTING_PASSWORD) {
            intent.putExtra("initGes", true);
            intent.putExtra("title", "设置手势密码");
        }
        if (mode == LockMode.CLEAR_PASSWORD)
            intent.putExtra("title", "清除手势密码");
        startActivityForResult(intent, INIT_GES_CODE);
    }

    private void getOTPSecret() {
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

    private void getRadom() {
        DialogUtil.showProgress(ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "oauth/random")
                .addHeader("X-Hawk-Client", devicesInfo)
                .addParams("username", username)
                .addParams("type", "user")
                .build()
                .execute(new MyStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        DialogUtil.dismissProgress();
                        DialogUtil.showAlert(ctx, e.toString(), null);
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String repo, int id) {
                        try {
                            JSONObject response = new JSONObject(repo);
                            if (Contants.SUCCESS.equals(response.getString("status"))) {
                                uuid = response.getJSONObject("data").getJSONObject("random").getString("uuid");
                                String random = response.getJSONObject("data").getJSONObject("random").getString("random");
                                if (!Validator.isBlank(random) && !Validator.isBlank(uuid)) {
                                    loginByCert(uuid, random);
                                }
                            } else {
                                DialogUtil.dismissProgress();
                                String message = new JSONObject(repo).getString("message");
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void loginByCert(final String uuid, final String random) {
        String sign = AppUtil.sign(random, ctx);
        OkHttpUtils.post()
                .url(GlobalConfig.getBaseurl() + "oauth/signature")
                .addHeader("X-Hawk-Client", devicesInfo)
                .addParams("username", username)
                .addParams("signature", sign)
                .addParams("type", "user")
                .addParams("uuid", uuid)
                .build()
                .execute(new MyStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
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
                                String onlyusername = new JSONObject(response).getJSONObject("data").getJSONObject("user").getString("username");
                                InitConfig(onlyusername);
                                String token = new JSONObject(response).getJSONObject("data").getString("token");
                                String logintime = "";
                                if (new JSONObject(response).getJSONObject("data").toString().contains("loginTime")) {
                                    logintime = new JSONObject(response).getJSONObject("data").getString("loginTime");
                                }
                                Intent i = new Intent(ctx, MainActivity.class);
                                SpUtil.getsp().putString("token", token);
                                i.putExtra("token", token);
                                i.putExtra("loginTime", logintime);
                                i.putExtra("username", onlyusername);
                                startActivity(i);
                                getActivity().finish();
                            } else {
                                DialogUtil.showAlert(ctx, message, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void InitConfig(String onlyusername) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirst", false);
        editor.putString("username", username);
        editor.putString("onlyusername", onlyusername);
        editor.putInt("node", 0);
        editor.commit();
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
//
                                MyApplication.getInstance().setUSERNAME(username);

                                getOTPSecret();

                                Intent i = new Intent(ctx, BindingToolbarActivity.class);
                                i.putExtra("username", username);
                                i.putExtra("uuid", uuid);
                                i.putExtra("random", random);
                                startActivity(i);
                                getActivity().finish();
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
                String path = "/data/data/" + ctx.getPackageName();
                String filePath = "/data/data/" + ctx.getPackageName() + "/files/" + MainActivity.USERNAME;
                RemoveData.cleanApplicationData(ctx, path);
                RemoveData.cleanCustomCache(filePath);

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

//        if (requestCode == INIT_CERT_CODE) {
//
//
//        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
            case R.id.tv_scan:
                onScanClick();
                break;
            case R.id.tv_jump:
                LocalBroadcastUtil.sendLocalBroadcast(new Intent(EventConstants.JUMPHOME));
                break;
        }
    }

}
