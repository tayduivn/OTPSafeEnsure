package com.itrus.ikey.safecenter.TOPMFA.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 时间校准页面
 */
public class TimeMendFragment extends BaseFragment {


    @BindView(R.id.tv_phone_time)
    TextView tvPhoneTime;
    @BindView(R.id.tv_phone_YTD)
    TextView tvPhoneYTD;
    @BindView(R.id.rl_one)
    RelativeLayout rlOne;
    @BindView(R.id.tv_UTC_time)
    TextView tvUTCTime;
    @BindView(R.id.tv_UTC_YTD)
    TextView tvUTCYTD;
    @BindView(R.id.rl_two)
    RelativeLayout rlTwo;
    @BindView(R.id.tv_mend)
    TextView tvMend;
    @BindView(R.id.tv_ps)
    TextView tvPs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frg_time_mend, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
