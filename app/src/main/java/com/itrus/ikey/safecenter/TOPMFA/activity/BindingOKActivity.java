package com.itrus.ikey.safecenter.TOPMFA.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.base.BaseActivity;
import com.itrus.ikey.safecenter.TOPMFA.utils.Contants;
import com.itrus.ikey.safecenter.TOPMFA.R;
import com.leo.gesturelibray.enums.LockMode;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindingOKActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tb_toolbar)
    Toolbar tb_toolbar;

    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_ok);
        ButterKnife.bind(this);
        initToolBar(tb_toolbar);
        tvTitle.setText("绑定成功");

        username = getIntent().getStringExtra("username");
    }


    @OnClick({R.id.btn_binding_next2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_binding_next2:
                setGesture(LockMode.SETTING_PASSWORD);
                break;
        }
    }

    private void setGesture(LockMode mode) {
        Intent intent = new Intent(ctx, InitGestureActivity.class);
        intent.putExtra(Contants.INTENT_SECONDACTIVITY_KEY, mode);
        intent.putExtra("title", "设置手势密码");
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

}
