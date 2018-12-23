package com.itrus.ikey.safecenter.TOPMFA.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.activity.gesture.GestureNomalActivity;
import com.itrus.ikey.safecenter.TOPMFA.activity.gesture.GestureSetPwdActivity;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseFragment;
import com.itrus.ikey.safecenter.TOPMFA.utils.AppConstants;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.leo.gesturelibray.enums.LockMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GestureOpenFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.ll_one)
    RelativeLayout llOne;

    @BindView(R.id.tv_modify)
    TextView tvModify;

    private boolean has_ges_pwd = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frg_open_gesture, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        has_ges_pwd = SpUtil.getsp().getBoolean(AppConstants.HAS_GES_PWD, false);
        tv_status.setSelected(has_ges_pwd);
        tvModify.setVisibility(has_ges_pwd ? View.VISIBLE : View.GONE);
        tv_status.setOnClickListener(this);
        tvModify.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modify:
                GestureSetPwdActivity.go2GestureSetPwdActivity(mContext);
                GestureSetPwdActivity.lockMode = LockMode.EDIT_PASSWORD;
                break;
            case R.id.tv_status:
                GestureSetPwdActivity.go2GestureSetPwdActivity(mContext);
                GestureSetPwdActivity.lockMode = LockMode.VERIFY_PASSWORD;
                break;
        }
    }

    @Override
    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
        super.wrapLocalBroadcastFilter(filter);
        filter.addAction(AppConstants.ACTION.FRAGMENT_VERIFY_GESTURE);
    }

    @Override
    protected void dealLocalBroadcast(Context context, Intent intent) {
        super.dealLocalBroadcast(context, intent);
        if (intent.getAction().equals(AppConstants.ACTION.FRAGMENT_VERIFY_GESTURE)) {
            has_ges_pwd = !has_ges_pwd;
            SpUtil.getsp().putBoolean(AppConstants.HAS_GES_PWD, has_ges_pwd);
            tv_status.setSelected(has_ges_pwd);
            tvModify.setVisibility(has_ges_pwd ? View.VISIBLE : View.GONE);
        }
    }
}
