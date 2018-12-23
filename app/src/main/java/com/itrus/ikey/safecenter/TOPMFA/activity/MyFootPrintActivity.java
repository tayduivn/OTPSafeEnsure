package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.widget.view.WaterDropListView;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.ChildStatusEntity;
import com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.LoginDevices;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyCallback;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyStringCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.DateUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.StringsUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.itrus.ikey.safecenter.TOPMFA.base.GlobalConfig.getBaseurl;

/**
 * Created by STAR on 2016/8/24.
 */
public class MyFootPrintActivity extends BaseActivity implements WaterDropListView.IWaterDropListViewListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tb_toolbar)
    Toolbar tbToolbar;
    @BindView(R.id.wdlv_login_devices)
    WaterDropListView wdlvLoginDevices;
    @BindView(R.id.lv_footprint_loginweek)
    WaterDropListView mLvFootprintLoginweek;
    @BindView(R.id.ll_footprint_logindevice)
    LinearLayout mLlFootprintLogindevice;

    private List<LoginDevices> devicesDatas = new ArrayList<>();
    private List<ChildStatusEntity> loginweekDatas = new ArrayList<>();
    private BaseAdapter loginDevicesAdapter = null;
    private BaseAdapter loginWeekAdapter = null;
    private String temp = "";
    private String devicesInfo;
    private String username;
    private String beginTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_footprint);
        ButterKnife.bind(this);
        initToolBarWithBack(tbToolbar);
        tvTitle.setText("我的足迹");
        devicesInfo = StringsUtil.getDevicesInfo(ctx);
        username = MainActivity.USERNAME;
        initDevicesList();
        initLoginWeekList();
        getLoginDevicesDatas();
        getLoginWeekDatas("10");
    }

    private void getLoginDevicesDatas() {
        String baseUrl = GlobalConfig.getBaseurl().replaceAll("/c/", "");
        OkHttpUtils.post()
                .tag("anxinyun")
                .url(baseUrl + "/onLineUser/queryOnLineUserForPhone")
                .addParams("username", username)
                .build()
                .execute(new MyStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONObject(jsonObject.getString("data")).getJSONArray(MainActivity.USERNAME);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String pos = jsonArray.getJSONObject(i).getString("host");
                                String loginTime = jsonArray.getJSONObject(i).getString("logonTime");
                                String sessionid = jsonArray.getJSONObject(i).getString("sessionId");
                                LoginDevices loginDevices = new LoginDevices();
                                loginDevices.setLoginPos(pos);
                                loginDevices.setLoginTime(loginTime);
                                loginDevices.setLoginDevice(sessionid);
                                if (!devicesDatas.contains(loginDevices))
                                    devicesDatas.add(loginDevices);
                            }
                            loginDevicesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (devicesDatas.size() == 0) {
                            mLlFootprintLogindevice.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initDevicesList() {
        loginDevicesAdapter = new CommonAdapter<LoginDevices>(ctx, R.layout.list_item_logindevice, devicesDatas) {
            @Override
            protected void convert(ViewHolder viewHolder, final LoginDevices item, int position) {
                String devicestype = "";
                viewHolder.setImageResource(R.id.iv_pic, R.drawable.computer);
                viewHolder.setText(R.id.tv_title, "网页端登录");
                viewHolder.setText(R.id.tv_desc, item.getLoginTime() + " 在" + item.getLoginPos() + "登录");
                viewHolder.getView(R.id.btn_uts_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.showAlertOkCancel(ctx, "是否剔除网页端的登录？", new MyCallback<Boolean>() {
                            @Override
                            public void onCallback(Boolean isRemove) {
                                if (isRemove) {
                                    mDatas.remove(item);
                                    loginDevicesAdapter.notifyDataSetChanged();
                                    if (mDatas.size() == 0) {
                                        mLlFootprintLogindevice.setVisibility(View.GONE);
                                    }
                                    //网页端执行
                                    String sessinId = item.getLoginDevice();
                                    quietFromBrower(sessinId);
                                }
                            }
                        });

                    }
                });
            }
        };

        wdlvLoginDevices.setAdapter(loginDevicesAdapter);
        wdlvLoginDevices.setPullLoadEnable(false);
        wdlvLoginDevices.setPullRefreshEnable(false);
    }

    private void getLoginWeekDatas(String count) {
        OkHttpUtils.post()
                .tag("anxinyun")
                .url(getBaseurl() + "user/myHistory")
                .addHeader("X-Hawk-Client", devicesInfo)
                .addParams("username", username)
                .addParams("max", count)
                .addParams("time", beginTime)
                .build()
                .execute(new MyStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLvFootprintLoginweek.stopLoadMore();
                        e.getMessage();
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String status = new JSONObject(response).getString("status");
                            if (!status.equals("0x0000")) {
                                mLvFootprintLoginweek.stopLoadMore();
                                return;
                            }

                            JSONArray jsonArray = new JSONObject(response).getJSONArray("recordList");
                            beginTime = new JSONObject(response).getString("beginTime");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String pos = jsonArray.getJSONObject(i).getString("host");
                                String time = jsonArray.getJSONObject(i).getString("time");
                                String type = "";
                                if (jsonArray.getJSONObject(i).toString().contains("todo"))
                                    type = jsonArray.getJSONObject(i).getString("todo");
                                String mode = "";
                                if (jsonArray.getJSONObject(i).toString().contains("mode"))
                                  mode = jsonArray.getJSONObject(i).getString("mode");
                                ChildStatusEntity childStatusEntity = new ChildStatusEntity();
                                childStatusEntity.setPos(pos);
                                childStatusEntity.setTime(time);
                                childStatusEntity.setType(type);
                                childStatusEntity.setMode(mode);
                                if (!loginweekDatas.contains(childStatusEntity))
                                    loginweekDatas.add(childStatusEntity);
                            }
                            if (loginweekDatas.size() > 3)
                                mLvFootprintLoginweek.setPullLoadEnable(true);
                            loginWeekAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            mLvFootprintLoginweek.stopLoadMore();
                        }
                    }

                });
    }

    private void initLoginWeekList() {
        loginWeekAdapter = new CommonAdapter<ChildStatusEntity>(ctx, R.layout.child_status_item, loginweekDatas) {
            @Override
            protected void convert(ViewHolder viewHolder, ChildStatusEntity item, int position) {
                Date date = DateUtil.str2Date(item.getTime());
                long time = date.getTime();
                String day = DateUtil.getDay(time);
                if (position == 0)
                    temp = "";
                else
                    temp = DateUtil.getDay(DateUtil.str2Date(loginweekDatas.get(position - 1).getTime()).getTime());
                if (day.equals(temp)) {
                    viewHolder.setVisible(R.id.ll_item_loginweek_date, false);
                } else {
//                    temp = day;
                    viewHolder.setVisible(R.id.ll_item_loginweek_date, true);
                    viewHolder.setText(R.id.tv_item_loginweek_date, day);
                }
                viewHolder.setText(R.id.tv_login_time, date.getHours() + ":" + date.getMinutes());
                viewHolder.setText(R.id.tv_login_type, item.getType().equals("") ? item.getMode() : item.getType());
                viewHolder.setText(R.id.tv_login_pos, item.getPos());
            }
        };

        mLvFootprintLoginweek.setAdapter(loginWeekAdapter);
        mLvFootprintLoginweek.setPullLoadEnable(false);
        mLvFootprintLoginweek.setPullRefreshEnable(false);
        mLvFootprintLoginweek.setWaterDropListViewListener(this);
    }

    private void quietFromBrower(String sessionId) {
        String baseUrl = GlobalConfig.getBaseurl().replaceAll("/c/", "");
        OkHttpUtils.post()
                .tag("anxinyun")
                .url(baseUrl + "/onLineUser/kickOffOnLineUser/" + sessionId)
                .build()
                .execute(new MyStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }
                });
    }

    @Override
    public void onRefresh() {
        getLoginWeekDatas("10");
        beginTime = "";
        loginweekDatas.clear();
    }

    @Override
    public void onLoadMore() {
        getLoginWeekDatas("10");
    }
}
